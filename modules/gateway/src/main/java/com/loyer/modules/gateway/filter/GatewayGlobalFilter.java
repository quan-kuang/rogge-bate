package com.loyer.modules.gateway.filter;

import com.alibaba.fastjson.JSON;
import com.loyer.common.dedicine.constant.SystemConst;
import com.loyer.common.dedicine.entity.ApiResult;
import com.loyer.common.dedicine.enums.HintEnum;
import com.loyer.common.dedicine.utils.DesUtil;
import com.loyer.common.dedicine.utils.Md5Util;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 网关全局过滤器
 *
 * @author kuangq
 * @date 2021-03-05 12:28
 */
@Component
public class GatewayGlobalFilter implements GlobalFilter, Ordered {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    /*私有放行接口*/
    private final List<String> privateReleasePorts = new ArrayList<String>() {{
        //开放微信配置校验接口
        add("/apis/system/wechat/link");
        //开放微信回调接口
        add("/apis/system/wechat/getAuthUserInfo");
    }};

    /*公共放行接口*/
    private final List<String> publicReleasePorts = new ArrayList<String>() {{
        //开放swagger资源
        add("/webjars");
        add("/doc.html");
        add("/v2/api-docs");
        add("/swagger-ui.html");
        add("/swagger-resources");
        //开放示例模块接口
        add("/demo");
        //开放websocket接口
        add("/websocket");
    }};

    /**
     * 截取请求地址
     *
     * @author kuangq
     * @date 2021-03-07 21:00
     */
    private String getPath(String path) {
        try {
            int index = StringUtils.ordinalIndexOf(path, "/", 2);
            return path.substring(index);
        } catch (Exception e) {
            return path;
        }
    }

    /**
     * 过滤规则
     *
     * @author kuangq
     * @date 2021-03-05 16:17
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest serverHttpRequest = exchange.getRequest();
        String path = serverHttpRequest.getPath().toString();
        //校验放行接口
        if (privateReleasePorts.stream().anyMatch(path::contains) || publicReleasePorts.stream().anyMatch(getPath(path)::contains)) {
            return chain.filter(exchange);
        }
        logger.info("【网关拦截器】{}", path);
        //获取header
        HttpHeaders httpHeaders = serverHttpRequest.getHeaders();
        //校验是否为测试接口，token设置固定值
        String token = httpHeaders.getFirst(SystemConst.TOEKN_KEY);
        if (SystemConst.SECRET_KEY.equals(token)) {
            return chain.filter(exchange);
        }
        //验签必传校验
        String noncestr = httpHeaders.getFirst("noncestr");
        String signature = httpHeaders.getFirst("signature");
        String timestamp = httpHeaders.getFirst("timestamp");
        if (StringUtils.isBlank(noncestr) || StringUtils.isBlank(signature) || StringUtils.isBlank(timestamp)) {
            return getResponseResult(exchange, HintEnum.HINT_1002);
        }
        //时间戳校验
        long currentTime = System.currentTimeMillis();
        long times = Long.parseLong(timestamp);
        if (Math.abs(currentTime - times) > SystemConst.SIGN_EXPIRE_TIME) {
            return getResponseResult(exchange, HintEnum.HINT_1003);
        }
        //签名校验
        String desEncryptStr = DesUtil.encrypt(noncestr + "&" + timestamp);
        String md5EncryptStr = Md5Util.encrypt(desEncryptStr);
        if (!signature.equals(md5EncryptStr)) {
            return getResponseResult(exchange, HintEnum.HINT_1004);
        }
        //认证通过
        return chain.filter(exchange);
    }

    /**
     * 值越小，优先级越大
     *
     * @author kuangq
     * @date 2021-03-05 16:17
     */
    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }

    /**
     * 获取响应结果
     *
     * @author kuangq
     * @date 2021-03-05 16:17
     */
    private Mono<Void> getResponseResult(ServerWebExchange serverWebExchange, HintEnum hintEnum) {
        ServerHttpResponse serverHttpResponse = serverWebExchange.getResponse();
        ApiResult apiResult = ApiResult.hintEnum(hintEnum, new HashMap<>(0));
        serverHttpResponse.setStatusCode(HttpStatus.OK);
        serverHttpResponse.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        return serverHttpResponse.writeWith(Mono.fromSupplier(() -> {
            DataBufferFactory bufferFactory = serverHttpResponse.bufferFactory();
            return bufferFactory.wrap(JSON.toJSONBytes(apiResult));
        }));
    }
}
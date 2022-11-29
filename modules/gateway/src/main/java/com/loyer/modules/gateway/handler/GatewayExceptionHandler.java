package com.loyer.modules.gateway.handler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.loyer.common.dedicine.entity.ApiResult;
import com.loyer.common.dedicine.utils.ExceptionUtil;
import com.loyer.common.dedicine.utils.StringUtil;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.io.InputStream;

/**
 * 网关异常处理
 *
 * @author kuangq
 * @date 2021-03-04 16:16
 */
@Order(-1)
@Configuration
public class GatewayExceptionHandler implements ErrorWebExceptionHandler {

    private static byte[] ERROR_PAGE = null;
    private final Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * 获取错误回跳页面
     *
     * @author kuangq
     * @date 2021-03-04 22:11
     */
    private byte[] getErrorPage() {
        try {
            if (ERROR_PAGE == null) {
                InputStream inputStream = this.getClass().getResourceAsStream("/static/error.html");
                ERROR_PAGE = IOUtils.toByteArray(inputStream);
            }
        } catch (IOException e) {
            ERROR_PAGE = StringUtil.decode("服务异常");
        }
        return ERROR_PAGE;
    }

    /**
     * 异常处理
     *
     * @author kuangq
     * @date 2021-03-04 22:19
     */
    @Override
    public Mono<Void> handle(ServerWebExchange serverWebExchange, Throwable throwable) {
        logger.error("【网关异常处理】请求路径：{}，异常信息：{}", serverWebExchange.getRequest().getPath(), throwable.getMessage());
        ServerHttpResponse serverHttpResponse = serverWebExchange.getResponse();
        if (serverHttpResponse.isCommitted()) {
            return Mono.error(throwable);
        }
        serverHttpResponse.setStatusCode(HttpStatus.OK);
        return getResponseResult(serverHttpResponse, throwable, true);
    }

    /**
     * 获取响应结果
     *
     * @author kuangq
     * @date 2021-03-06 23:54
     */
    private Mono<Void> getResponseResult(ServerHttpResponse serverHttpResponse, Throwable throwable, boolean isJson) {
        if (isJson) {
            //返回JSON
            ApiResult apiResult = ApiResult.failure(throwable.getMessage(), ExceptionUtil.getStackTrace(throwable));
            serverHttpResponse.setStatusCode(HttpStatus.OK);
            serverHttpResponse.getHeaders().setContentType(MediaType.APPLICATION_JSON);
            return serverHttpResponse.writeWith(Mono.fromSupplier(() -> {
                DataBufferFactory bufferFactory = serverHttpResponse.bufferFactory();
                return bufferFactory.wrap(JSON.toJSONBytes(apiResult, SerializerFeature.WriteMapNullValue));
            }));
        }
        //返回HTML
        serverHttpResponse.getHeaders().setContentType(MediaType.TEXT_HTML);
        return serverHttpResponse.writeWith(Mono.fromSupplier(() -> {
            DataBufferFactory bufferFactory = serverHttpResponse.bufferFactory();
            return bufferFactory.wrap(getErrorPage());
        }));
    }
}
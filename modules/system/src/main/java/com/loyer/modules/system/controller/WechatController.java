package com.loyer.modules.system.controller;

import com.loyer.common.apis.server.ToolsServer;
import com.loyer.common.core.annotation.OperateLogAnnotation;
import com.loyer.common.core.utils.common.ParamsUtil;
import com.loyer.common.dedicine.entity.ApiResult;
import com.loyer.modules.system.entity.TencentEntity;
import com.loyer.modules.system.utils.WeChatUtil;
import com.loyer.modules.system.utils.XmlUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;

/**
 * @author kuangq
 * @title WechatController
 * @description 微信开发Controller
 * @date 2020-06-20 13:40
 */
@Api(tags = "微信模块")
@RestController
@RequestMapping("wechat")
public class WechatController {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Resource
    private ToolsServer toolsServer;

    @SneakyThrows
    @OperateLogAnnotation
    @ApiOperation("微信公众号服务器配置校验")
    @GetMapping("link")
    public String getLink(HttpServletRequest httpServletRequest) {
        //防止乱码
        httpServletRequest.setCharacterEncoding(StandardCharsets.UTF_8.name());
        //微信加密签名
        String signature = httpServletRequest.getParameter("signature");
        //时间戳
        String timestamp = httpServletRequest.getParameter("timestamp");
        //随机数
        String nonce = httpServletRequest.getParameter("nonce");
        //随机字符串
        String echostr = httpServletRequest.getParameter("echostr");
        //校验成功返回echostr
        return WeChatUtil.checkSignature(signature, timestamp, nonce) ? echostr : null;
    }

    @OperateLogAnnotation
    @ApiOperation("获取微信JSSDK接口配置")
    @PostMapping("getConfig")
    public ApiResult getConfig(@RequestBody TencentEntity.JsSdk jsSdk) {
        return ApiResult.success(WeChatUtil.getConfig(jsSdk));
    }

    @OperateLogAnnotation
    @ApiOperation("下载微信服务器上的媒体文件")
    @GetMapping("downloadMedia/{mediaId}")
    public ApiResult downloadMedia(@PathVariable("mediaId") String mediaId) {
        return WeChatUtil.downloadMedia(mediaId, toolsServer);
    }

    @OperateLogAnnotation
    @ApiOperation("获取微信网页授权登录的跳转链接")
    @GetMapping("getAuthUserInfoUrl")
    public ApiResult getAuthUserInfoUrl(TencentEntity.Authorize authorize) {
        return WeChatUtil.getAuthUserInfoUrl(authorize);
    }

    @OperateLogAnnotation
    @ApiOperation("网页授权获取用户信息")
    @GetMapping("getAuthUserInfo")
    public ModelAndView getAuthUserInfo(Model model, String code, String state) {
        model.addAttribute("redirectUrl", WeChatUtil.getAuthUserInfo(code, state));
        return new ModelAndView("ftl/oauth");
    }

    @OperateLogAnnotation
    @ApiOperation("微信事件监听")
    @PostMapping("link")
    public ApiResult postLink(HttpServletRequest httpServletRequest) {
        //接收微信请求中的入参信息
        String xmlStr = ParamsUtil.getInArgs(httpServletRequest);
        logger.info("【微信事件监听】{}", xmlStr);
        //将XML信息转换成MAP
        return ApiResult.success(XmlUtil.xmlToMap(xmlStr));
    }
}
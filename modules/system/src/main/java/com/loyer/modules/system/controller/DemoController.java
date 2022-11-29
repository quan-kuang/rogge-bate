package com.loyer.modules.system.controller;

import com.loyer.common.core.annotation.OperateLogAnnotation;
import com.loyer.common.core.constant.PrefixConst;
import com.loyer.common.core.utils.request.CookieUtil;
import com.loyer.common.dedicine.constant.SystemConst;
import com.loyer.common.dedicine.entity.ApiResult;
import com.loyer.common.dedicine.enums.HintEnum;
import com.loyer.common.redis.utils.CacheUtil;
import com.loyer.common.security.annotation.ThrottlingAnnotation;
import com.loyer.common.security.enums.ThrottlingType;
import com.loyer.modules.system.entity.TencentEntity;
import com.loyer.modules.system.service.DemoService;
import com.loyer.modules.system.utils.AuthFaceUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import springfox.documentation.annotations.ApiIgnore;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * 示例Controller
 *
 * @author kuangq
 * @date 2019-07-16 15:26
 */
@Api(tags = "示例模块")
@RestController
@RequestMapping("demo")
public class DemoController {

    @Resource
    private DemoService demoService;

    @ApiIgnore
    @ThrottlingAnnotation(cycle = 10, throttlingType = ThrottlingType.IP)
    @OperateLogAnnotation
    @ApiOperation("查询版本号")
    @GetMapping("showVersion")
    public ApiResult showVersion() {
        return ApiResult.success(SystemConst.VERSION);
    }

    @OperateLogAnnotation
    @ApiOperation("查询数据库基本信息")
    @GetMapping("queryDataBase")
    public ApiResult queryDataBase() {
        return demoService.queryDataBase();
    }

    @OperateLogAnnotation
    @ApiOperation("获取Cookie")
    @GetMapping("getCookie")
    public ApiResult getCookie(@CookieValue(value = "name") String name) {
        System.out.println(CookieUtil.getCookieValue("age"));
        CookieUtil.setCookie("job", "Java开发", 600);
        return ApiResult.success(name);
    }

    @OperateLogAnnotation
    @ApiOperation("加载ftl文件")
    @GetMapping("freemarker")
    public ModelAndView freemarker(Model model) {
        Map<String, String> map = new HashMap<String, String>(2) {{
            put("name", "freemarker模板");
            put("type", "加载ftl文件");
        }};
        model.addAttribute("params", map);
        return new ModelAndView("ftl/index");
    }

    @OperateLogAnnotation
    @ApiOperation("加载html文件")
    @GetMapping("thymeleaf")
    public ModelAndView thymeleaf(Model model) {
        Map<String, String> map = new HashMap<String, String>(2) {{
            put("name", "thymeleaf模板");
            put("type", "加载html文件");
        }};
        model.addAttribute("params", map);
        return new ModelAndView("html/index");
    }

    @OperateLogAnnotation
    @ApiOperation("实名核身鉴权")
    @GetMapping("confirm")
    public ModelAndView confirm(Model model, @RequestParam String requestId, @RequestParam String redirectUrl, @RequestParam(defaultValue = "0") String ruleId) {
        ApiResult apiResult = AuthFaceUtil.detectAuth(ruleId, redirectUrl);
        if (apiResult.getFlag()) {
            TencentEntity.CloudEyeResponse.Response response = (TencentEntity.CloudEyeResponse.Response) apiResult.getData();
            //设置跳转地址
            apiResult.setData(response.getUrl());
            //缓存BizToken
            String key = PrefixConst.TENCENT_BIZ_TOKEN + requestId;
            CacheUtil.VALUE.set(key, response.getBizToken(), SystemConst.USER_EXPIRE_TIME);
        }
        model.addAttribute("apiResult", apiResult);
        return new ModelAndView("ftl/confirm");
    }

    @OperateLogAnnotation
    @ApiOperation("获取核身结果")
    @GetMapping("confirmBack")
    public ModelAndView confirmBack(Model model, @RequestParam String requestId, @RequestParam(defaultValue = "123") String infoType, @RequestParam(defaultValue = "0") String ruleId) {
        ApiResult apiResult;
        //判断缓存是否失效
        String key = PrefixConst.TENCENT_BIZ_TOKEN + requestId;
        if (!CacheUtil.KEY.has(key)) {
            apiResult = ApiResult.hintEnum(HintEnum.HINT_1024);
        } else {
            //获取bizToken
            String bizToken = CacheUtil.VALUE.get(key);
            CacheUtil.KEY.delete(key);
            //请求获取认证结果
            apiResult = AuthFaceUtil.getDetectInfo(ruleId, infoType, bizToken);
            //解析认证结果
            if (!apiResult.getFlag()) {
                apiResult = ApiResult.hintEnum(HintEnum.HINT_1025);
            }
        }
        model.addAttribute("apiResult", apiResult);
        return new ModelAndView("html/confirm");
    }
}
package com.loyer.modules.system.controller;

import com.loyer.common.core.annotation.OperateLogAnnotation;
import com.loyer.common.core.entity.Captcha;
import com.loyer.common.dedicine.entity.ApiResult;
import com.loyer.common.dedicine.enums.HintEnum;
import com.loyer.modules.system.entity.TableExplain;
import com.loyer.modules.system.enums.CaptchaType;
import com.loyer.modules.system.service.UtilService;
import com.loyer.modules.system.utils.CodeCaptchaUtil;
import com.loyer.modules.system.utils.PuzzleCaptchaUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

/**
 * 工具类Controller
 *
 * @author kuangq
 * @date 2019-12-12 23:29
 */
@Api(tags = "工具模块")
@RestController
@RequestMapping("util")
public class UtilController {

    @Resource
    private UtilService utilService;

    @Resource
    private CodeCaptchaUtil captchaUtil;

    @OperateLogAnnotation(isSaveOutArgs = false)
    @ApiOperation("获取验证码")
    @PostMapping("getCaptcha")
    public ApiResult getCaptcha(@RequestBody Captcha captcha) {
        String type = captcha.getType();
        if (CaptchaType.CODE.value().equals(type)) {
            return captchaUtil.getCaptcha();
        } else if (CaptchaType.PUZZLE.value().equals(type)) {
            return PuzzleCaptchaUtil.getCaptcha(captcha);
        }
        return ApiResult.hintEnum(HintEnum.HINT_1080, type);
    }

    @OperateLogAnnotation
    @ApiOperation("上传Vue项目")
    @GetMapping("putVue/{projectName}")
    public ApiResult putVue(@PathVariable("projectName") String projectName) {
        return utilService.putVue(projectName);
    }

    @OperateLogAnnotation
    @ApiOperation("发布Vue项目")
    @GetMapping("releaseVue/{projectName}")
    public ApiResult releaseVue(@PathVariable("projectName") String projectName) {
        return utilService.releaseVue(projectName);
    }

    @OperateLogAnnotation
    @ApiOperation("查询数据库模式")
    @GetMapping("selectSchemaName")
    public ApiResult selectSchemaName() {
        return utilService.selectSchemaName();
    }

    @OperateLogAnnotation
    @ApiOperation("查询表说明信息")
    @PostMapping("selectTableExplain")
    public ApiResult selectTableExplain(@RequestBody TableExplain tableExplain) {
        return utilService.selectTableExplain(tableExplain);
    }

    @OperateLogAnnotation
    @ApiOperation("代码生成")
    @GetMapping("generateCode")
    public void generateCode(HttpServletResponse httpServletResponse, @RequestParam int[] tableOids) {
        utilService.generateCode(httpServletResponse, tableOids);
    }
}
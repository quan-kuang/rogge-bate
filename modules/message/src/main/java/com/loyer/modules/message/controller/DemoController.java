package com.loyer.modules.message.controller;

import com.loyer.common.apis.server.MessageServer;
import com.loyer.common.core.annotation.OperateLogAnnotation;
import com.loyer.common.core.control.BaseControl;
import com.loyer.common.core.entity.ConsoleLog;
import com.loyer.common.dedicine.constant.SystemConst;
import com.loyer.common.dedicine.entity.ApiResult;
import com.loyer.common.dedicine.utils.GeneralUtil;
import com.loyer.common.security.annotation.ThrottlingAnnotation;
import com.loyer.common.security.enums.ThrottlingType;
import com.loyer.modules.message.service.NewsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import javax.annotation.Resource;
import java.util.Random;

/**
 * 示例Controller
 *
 * @author kuangq
 * @date 2019-07-16 15:26
 */
@Api(tags = "示例模块")
@RestController
@RequestMapping("demo")
public class DemoController extends BaseControl {

    @Resource
    private NewsService newsService;

    public DemoController(MessageServer messageServer) {
        super(messageServer);
    }

    @ApiIgnore
    @ThrottlingAnnotation(cycle = 10, throttlingType = ThrottlingType.IP)
    @OperateLogAnnotation
    @ApiOperation("查询版本号")
    @GetMapping("showVersion")
    public ApiResult showVersion() {
        return ApiResult.success(SystemConst.VERSION);
    }

    @OperateLogAnnotation
    @ApiOperation("控制台打印")
    @GetMapping("consolePrint")
    public ApiResult consolePrint() {
        return invoke(sessionId -> {
            Random random = new Random();
            for (int i = 0; i < 10; i++) {
                int millis = random.nextInt(150) % (150 - 1 + 1) + 1;
                GeneralUtil.sleep(millis);
                newsService.printConsoleLog(new ConsoleLog(sessionId, String.format("当前任务进度：%s%%", (i + 1) * 10)));
            }
            return ApiResult.success();
        });
    }
}
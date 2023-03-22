package com.loyer.modules.system.controller;


import com.loyer.common.core.annotation.OperateLogAnnotation;
import com.loyer.common.dedicine.constant.SystemConst;
import com.loyer.common.dedicine.entity.ApiResult;
import com.loyer.modules.system.entity.ValidListVo;
import com.loyer.modules.system.service.QianYuService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * 浅予
 *
 * @author kuangq
 * @date 2023-3-21 9:51
 */
@Api(tags = "浅予")
@RestController
@RequestMapping("qianYu")
public class QianYuController {

    @Resource
    private QianYuService qianYuService;

    @OperateLogAnnotation
    @ApiOperation("胎动打卡记录")
    @GetMapping("punchCard")
    public ApiResult punchCard(@RequestParam(defaultValue = SystemConst.BAO_OPEN_ID) String openId, @RequestParam(required = false) String currentDate) {
        return qianYuService.punchCard(openId, currentDate);
    }

    @OperateLogAnnotation
    @ApiOperation("查询胎动记录")
    @GetMapping("selectEcgInfo")
    public ApiResult selectEcgInfo(@RequestParam(defaultValue = SystemConst.BAO_OPEN_ID) String openId, @RequestParam(required = false) String currentDate, @RequestParam(defaultValue = "3") Integer num) {
        return qianYuService.selectEcgInfo(openId, currentDate, num);
    }

    @OperateLogAnnotation(isSaveInArgs = false)
    @PreAuthorize("@pu.hasAnyPermissions('qianYu:upload','qianYu:send')")
    @ApiOperation("发送图文消息")
    @PostMapping("sendImageMsg")
    public ApiResult sendImageMsg(@Validated @RequestBody ValidListVo validListVo) {
        return qianYuService.sendImageMsg(validListVo.getMediaInfoList());
    }
}
package com.loyer.modules.system.controller;


import com.loyer.common.core.annotation.OperateLogAnnotation;
import com.loyer.common.dedicine.entity.ApiResult;
import com.loyer.modules.system.service.QianYuService;
import com.loyer.modules.system.utils.WeChatUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
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

    @PostMapping("test")
    public String test(@RequestBody String xml) {
        return WeChatUtil.postLink(xml);
    }

    @OperateLogAnnotation
    @ApiOperation("查询胎动记录")
    @GetMapping("selectEcgInfo")
    public ApiResult selectEcgInfo(@RequestParam String openId, @RequestParam(required = false) String currentDate, @RequestParam(defaultValue = "3") Integer num) {
        return qianYuService.selectEcgInfo(openId, currentDate, num);
    }
}
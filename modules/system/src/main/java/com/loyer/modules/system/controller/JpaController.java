package com.loyer.modules.system.controller;

import com.loyer.common.dedicine.entity.ApiResult;
import com.loyer.modules.system.service.JpaService;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Map;

/**
 * Jpa测试接口
 *
 * @author kuangq
 * @date 2019-11-05 17:05
 */
@Api(tags = "Jpa测试模块")
@RestController
@RequestMapping("jpa")
public class JpaController {

    @Resource
    private JpaService jpaService;

    @GetMapping("decryptCard")
    public ApiResult decryptCard(@RequestParam Map<String, Object> params) {
        return jpaService.decryptCard(params);
    }

    @GetMapping("findByAcountId/{acountId}")
    public ApiResult findByAcountId(@PathVariable("acountId") String acountId) {
        return jpaService.findByAcountId(acountId);
    }

    @GetMapping("findByRealNameLike/{realName}")
    public ApiResult findByRealNameLike(@PathVariable("realName") String realName) {
        return jpaService.findByRealNameLike(realName);
    }
}
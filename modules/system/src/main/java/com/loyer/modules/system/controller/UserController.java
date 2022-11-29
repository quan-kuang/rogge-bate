package com.loyer.modules.system.controller;

import com.loyer.common.core.annotation.OperateLogAnnotation;
import com.loyer.common.core.entity.User;
import com.loyer.common.dedicine.entity.ApiResult;
import com.loyer.common.security.annotation.ThrottlingAnnotation;
import com.loyer.common.security.entity.LoginUser;
import com.loyer.modules.system.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * 用户Controller
 *
 * @author kuangq
 * @date 2020-05-13 9:58
 */
@Api(tags = "用户模块")
@RestController
@RequestMapping("user")
public class UserController {

    @Resource
    private UserService userService;

    @OperateLogAnnotation
    @ApiOperation("登录")
    @PostMapping("login")
    public ApiResult login(@RequestBody LoginUser loginUser) {
        return userService.login(loginUser);
    }

    @OperateLogAnnotation
    @ApiOperation("短信登录")
    @PostMapping("messageLogin")
    public ApiResult messageLogin(@RequestBody User user) {
        return userService.messageLogin(user);
    }

    @OperateLogAnnotation
    @PreAuthorize("@pu.hasAnyPermissions('user:insert','user:update')")
    @ApiOperation("保存用户信息")
    @PostMapping("saveUser")
    public ApiResult saveUser(@RequestBody User user) {
        return userService.saveUser(user);
    }

    @ThrottlingAnnotation(count = 100)
    @OperateLogAnnotation
    @PreAuthorize("@pu.hasAllPermissions('user:select')")
    @ApiOperation("查询用户信息")
    @PostMapping("selectUser")
    public ApiResult selectUser(@RequestBody User user) {
        return userService.selectUser(user);
    }

    @OperateLogAnnotation
    @PreAuthorize("@pu.hasAllPermissions('user:delete')")
    @ApiOperation("删除用户信息")
    @PostMapping("deleteUser")
    public ApiResult deleteUser(@RequestBody String[] uuids) {
        return userService.deleteUser(uuids);
    }

    @OperateLogAnnotation
    @PreAuthorize("@pu.hasAnyPermissions('user:update','user:reset')")
    @ApiOperation("修改用户信息")
    @PostMapping("updateUser")
    public ApiResult updateUser(@RequestBody User user) {
        return userService.updateUser(user);
    }

    @OperateLogAnnotation
    @ApiOperation("根据ID查询单条用户信息")
    @GetMapping("selectUserById/{uuid}")
    public ApiResult selectUserById(@PathVariable("uuid") String uuid) {
        return userService.selectUserById(uuid);
    }

    @OperateLogAnnotation
    @ApiOperation("精确查询单条用户信息")
    @PostMapping("selectUserBy")
    public ApiResult selectUserBy(@RequestBody User user) {
        return userService.selectUserBy(user);
    }

    @OperateLogAnnotation
    @PreAuthorize("@pu.hasAnyPermissions('user:insert','user:update')")
    @ApiOperation("校验用户是否存在")
    @GetMapping("checkUserExists/{account}")
    public ApiResult checkUserExists(@PathVariable("account") String account) {
        return userService.checkUserExists(account);
    }

    @OperateLogAnnotation
    @ApiOperation("实人认证")
    @PostMapping("auth")
    public ApiResult auth(@RequestBody User user) {
        return userService.auth(user);
    }
}
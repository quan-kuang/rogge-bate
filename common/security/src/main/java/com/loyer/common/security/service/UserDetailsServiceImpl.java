package com.loyer.common.security.service;

import com.alibaba.fastjson.JSON;
import com.loyer.common.apis.server.SystemServer;
import com.loyer.common.core.entity.User;
import com.loyer.common.dedicine.entity.ApiResult;
import com.loyer.common.dedicine.exception.BusinessException;
import com.loyer.common.security.entity.LoginUser;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 自定义UserDetailsService做登录验证
 *
 * @author kuangq
 * @date 2020-08-07 17:54
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Resource
    private SystemServer systemServer;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //跨服务调用
        ApiResult apiResult = systemServer.loadUserByUsername("account", username);
        //判断响应正常
        if (!apiResult.getFlag()) {
            throw new BusinessException(apiResult.getMsg(), apiResult.getData());
        }
        //获取用户信息
        User user = JSON.parseObject(JSON.toJSONString(apiResult.getData()), User.class);
        return new LoginUser(user);
    }
}
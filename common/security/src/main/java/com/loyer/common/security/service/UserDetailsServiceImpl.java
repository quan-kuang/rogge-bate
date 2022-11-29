package com.loyer.common.security.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.loyer.common.apis.server.SystemServer;
import com.loyer.common.core.entity.User;
import com.loyer.common.dedicine.entity.ApiResult;
import com.loyer.common.dedicine.enums.HintEnum;
import com.loyer.common.dedicine.exception.BusinessException;
import com.loyer.common.security.entity.LoginUser;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Optional;

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
    public UserDetails loadUserByUsername(String account) throws UsernameNotFoundException {
        return new LoginUser(selectUser("account", account));
    }

    /**
     * 查询用户信息
     *
     * @author kuangq
     * @date 2021-06-16 15:40
     */
    public User selectUser(String key, String value) {
        //组装入参
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(key, value);
        //跨服务调用
        ApiResult apiResult = systemServer.selectUser(jsonObject);
        //判断响应正常
        if (!apiResult.getFlag()) {
            throw new BusinessException(apiResult.getMsg(), apiResult.getData());
        }
        //获取用户信息，处理类型不一样，先转为JsonStr，再转换User对象
        Optional<User> userOptional = JSON.parseArray(JSON.toJSONString(apiResult.getData()), User.class).stream().findFirst();
        if (!userOptional.isPresent()) {
            throw new BusinessException(HintEnum.HINT_1041);
        }
        //判断用户禁用
        if (!userOptional.get().getStatus()) {
            throw new BusinessException(HintEnum.HINT_1042);
        }
        return userOptional.get();
    }
}
package com.loyer.common.security.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.loyer.common.core.entity.Captcha;
import com.loyer.common.core.entity.User;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

/**
 * @author kuangq
 * @title LoginUser
 * @description 登录用户对象
 * @date 2020-08-14 9:23
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class LoginUser implements UserDetails {

    //认证凭据
    private String token;

    //用户对象
    private User user;

    //验证码对象
    private Captcha captcha;

    /**
     * @param
     * @return java.util.Collection<? extends org.springframework.security.core.GrantedAuthority>
     * @author kuangq
     * @description 获取角色列表
     * @date 2020-08-14 9:39
     */
    @JsonIgnore
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    /**
     * @param
     * @return java.lang.String
     * @author kuangq
     * @description 登录账户
     * @date 2020-08-14 9:39
     */
    @JsonIgnore
    @Override
    public String getUsername() {
        return user.getAccount();
    }

    /**
     * @param
     * @return java.lang.String
     * @author kuangq
     * @description 登录密码
     * @date 2020-08-14 9:39
     */
    @JsonIgnore
    @Override
    public String getPassword() {
        return user.getPassword();
    }

    /**
     * @param
     * @return boolean
     * @author kuangq
     * @description 用户是否可用
     * @date 2020-08-09 9:51
     */
    @JsonIgnore
    @Override
    public boolean isEnabled() {
        return true;
    }

    /**
     * @param
     * @return boolean
     * @author kuangq
     * @description 账户是否未过期，过期无法验证
     * @date 2020-08-09 9:50
     */
    @JsonIgnore
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * @param
     * @return boolean
     * @author kuangq
     * @description 用户是否解锁，锁定无法登陆
     * @date 2020-08-09 9:50
     */
    @JsonIgnore
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * @param
     * @return boolean
     * @author kuangq
     * @description 登陆凭证是否过期，过期无法登陆
     * @date 2020-08-09 9:50
     */
    @JsonIgnore
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
}
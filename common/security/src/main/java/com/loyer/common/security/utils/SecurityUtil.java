package com.loyer.common.security.utils;

import com.loyer.common.core.constant.PrefixConst;
import com.loyer.common.core.entity.User;
import com.loyer.common.dedicine.constant.SystemConst;
import com.loyer.common.dedicine.enums.HintEnum;
import com.loyer.common.dedicine.exception.BusinessException;
import com.loyer.common.dedicine.utils.GeneralUtil;
import com.loyer.common.redis.utils.CacheUtil;
import com.loyer.common.security.entity.LoginUser;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Security工具类
 *
 * @author kuangq
 * @date 2020-08-13 17:00
 */
public class SecurityUtil {

    /**
     * 生成用户认证凭据，缓存登录用户信息
     *
     * @author kuangq
     * @date 2020-10-16 10:52
     */
    public static String createToken(LoginUser loginUser) {
        //创建登录用户的token
        String token = GeneralUtil.getUuid();
        loginUser.setToken(token);
        //将用户信息加入缓存
        String key = PrefixConst.LOGIN_USER + token;
        CacheUtil.STRING.set(key, loginUser, SystemConst.USER_EXPIRE_TIME);
        //返回前端的token进行加密
        Map<String, Object> claims = new HashMap<>(1);
        claims.put(PrefixConst.LOGIN_USER, token);
        String encryptToken = createToken(claims);
        loginUser.setToken(encryptToken);
        return token;
    }

    /**
     * 基于header中的token获取用户信息
     *
     * @author kuangq
     * @date 2020-08-14 9:45
     */
    public static LoginUser getLoginUser(String token) {
        if (StringUtils.isBlank(token)) {
            return null;
        }
        //设置loyer密匙直接放行，设置为管理员账户
        if (SystemConst.SECRET_KEY.equals(token)) {
            User user = new User();
            user.setUuid(SystemConst.LOYER);
            user.setName(SystemConst.LOYER);
            user.setDeptId(SystemConst.LOYER);
            user.setRoleIds(new ArrayList<String>() {{
                add(SystemConst.ADMIN);
            }});
            LoginUser loginUser = new LoginUser();
            loginUser.setToken(SystemConst.LOYER);
            loginUser.setUser(user);
            return loginUser;
        }
        //解密token获取loginUserKey从缓存中获取用户信息
        String loginUserKey = PrefixConst.LOGIN_USER + getDecodeToken(token);
        if (CacheUtil.KEY.has((loginUserKey))) {
            return CacheUtil.STRING.get(loginUserKey);
        }
        throw new BusinessException(HintEnum.HINT_1099);
    }

    /**
     * 刷新用户信息缓存时间
     *
     * @author kuangq
     * @date 2020-08-14 17:16
     */
    public static boolean refresh(String token) {
        CacheUtil.KEY.expire(PrefixConst.ONLINE_USER + token, SystemConst.USER_EXPIRE_TIME);
        return CacheUtil.KEY.expire(PrefixConst.LOGIN_USER + token, SystemConst.USER_EXPIRE_TIME);
    }

    /**
     * 刷新用户信息
     *
     * @author kuangq
     * @date 2020-08-16 15:04
     */
    public static void refresh(User user) {
        LoginUser loginUser = getLoginUser();
        loginUser.setUser(user);
        String key = PrefixConst.LOGIN_USER + loginUser.getToken();
        CacheUtil.STRING.set(key, loginUser, SystemConst.USER_EXPIRE_TIME);
    }

    /**
     * 获取解密后的token
     *
     * @author kuangq
     * @date 2020-08-14 16:04
     */
    public static String getDecodeToken(String token) {
        Claims claims = parseToken(token);
        return claims.get(PrefixConst.LOGIN_USER).toString();
    }

    /**
     * 从数据声明生成令牌
     *
     * @author kuangq
     * @date 2020-08-14 0:32
     */
    private static String createToken(Map<String, Object> claims) {
        return Jwts.builder().setClaims(claims).signWith(SignatureAlgorithm.HS512, SystemConst.SECRET_KEY).compact();
    }

    /**
     * 从令牌中获取数据声明
     *
     * @author kuangq
     * @date 2020-08-14 0:32
     */
    private static Claims parseToken(String token) {
        return Jwts.parser().setSigningKey(SystemConst.SECRET_KEY).parseClaimsJws(token).getBody();
    }

    /**
     * 生成加密字符串
     *
     * @author kuangq
     * @date 2020-08-13 17:26
     */
    public static String encrypt(String password) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return passwordEncoder.encode(password);
    }

    /**
     * 判断密码是否相同
     *
     * @author kuangq
     * @date 2020-08-13 17:26
     */
    public static boolean compare(String source, String target) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return passwordEncoder.matches(target, source);
    }

    /**
     * 获取Authentication
     *
     * @author kuangq
     * @date 2020-08-14 11:30
     */
    public static Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    /**
     * 从Security上下文中获取登录用户信息
     *
     * @author kuangq
     * @date 2020-08-14 11:43
     */
    public static LoginUser getLoginUser() {
        return (LoginUser) getAuthentication().getPrincipal();
    }
}
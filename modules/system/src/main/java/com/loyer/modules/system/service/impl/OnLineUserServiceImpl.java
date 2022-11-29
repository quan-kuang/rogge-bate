package com.loyer.modules.system.service.impl;

import com.loyer.common.apis.server.MessageServer;
import com.loyer.common.core.constant.SpecialCharsConst;
import com.loyer.common.core.entity.User;
import com.loyer.common.core.enums.DatePattern;
import com.loyer.common.core.utils.common.DateUtil;
import com.loyer.common.core.utils.common.IpUtil;
import com.loyer.common.datasource.utils.PageHelperUtil;
import com.loyer.common.dedicine.constant.SystemConst;
import com.loyer.common.dedicine.entity.ApiResult;
import com.loyer.common.redis.utils.CacheUtil;
import com.loyer.modules.system.constant.PrefixConst;
import com.loyer.modules.system.entity.Constant;
import com.loyer.modules.system.entity.OnLineUser;
import com.loyer.modules.system.mapper.postgresql.ConstantMapper;
import com.loyer.modules.system.service.OnLineUserService;
import eu.bitwalker.useragentutils.UserAgent;
import org.apache.commons.lang3.StringUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 在线用户ServiceImpl
 *
 * @author kuangq
 * @date 2020-12-15 17:46
 */
@Service
public class OnLineUserServiceImpl implements OnLineUserService {

    @Resource
    private MessageServer messageServer;

    @Resource
    private ConstantMapper constantMapper;

    /**
     * 缓存用户在线信息
     *
     * @author kuangq
     * @date 2020-12-15 17:23
     */
    @Async
    @Override
    public void asyncSaveOnLineUser(HttpServletRequest httpServletRequest, String token, User user) {
        OnLineUser onLineUser = new OnLineUser();
        //设置用户基本信息
        onLineUser.setUuid(token);
        onLineUser.setAccount(user.getAccount());
        onLineUser.setName(user.getName());
        onLineUser.setPhone(user.getPhone());
        onLineUser.setDeptName(user.getDeptName());
        onLineUser.setLoginTime(user.getLoginTime());
        //设置用户客户端请求信息
        UserAgent userAgent = UserAgent.parseUserAgentString(httpServletRequest.getHeader("User-Agent"));
        String ip = IpUtil.getRealIp(httpServletRequest);
        String position = IpUtil.getPosition(ip);
        String browser = userAgent.getBrowser().toString();
        String operateSystem = userAgent.getOperatingSystem().getName();
        onLineUser.setIp(ip);
        onLineUser.setPosition(position);
        onLineUser.setBrowser(browser);
        onLineUser.setOperateSystem(operateSystem);
        //发送邮件登录通知
        sendLoginInformMail(onLineUser);
        //加入缓存
        String key = PrefixConst.ONLINE_USER + token;
        CacheUtil.VALUE.set(key, onLineUser, SystemConst.USER_EXPIRE_TIME);
    }

    /**
     * 发送邮件登录通知
     *
     * @author kuangq
     * @date 2021-05-30 22:05
     */
    private void sendLoginInformMail(OnLineUser onLineUser) {
        Object value = CacheUtil.HASH.get(PrefixConst.CONSTANT, PrefixConst.SEND_LOGIN_INFORM_MAIL_KEY);
        if (value == null) {
            Constant constant = new Constant();
            constant.setKey(PrefixConst.SEND_LOGIN_INFORM_MAIL_KEY);
            List<Constant> constantList = constantMapper.selectConstant(constant);
            if (constantList.isEmpty()) {
                return;
            }
            value = constantList.get(0).getValue();
            CacheUtil.HASH.put(PrefixConst.CONSTANT, PrefixConst.SEND_LOGIN_INFORM_MAIL_KEY, value);
        }
        if (!SpecialCharsConst.TRUE.equals(value.toString())) {
            return;
        }
        List<String> messageList = new ArrayList<>();
        messageList.add(onLineUser.getAccount());
        messageList.add(onLineUser.getName());
        messageList.add(onLineUser.getIp());
        messageList.add(onLineUser.getPosition());
        messageList.add(DateUtil.getTimestamp(onLineUser.getLoginTime().getTime(), DatePattern.YMD_HMS_1));
        messageServer.sendLoginInformMail(messageList);
    }

    /**
     * 查询在线用户信息
     *
     * @author kuangq
     * @date 2020-12-15 22:59
     */
    @Override
    public ApiResult selectOnLineUser(OnLineUser onLineUser) {
        Set<String> keys = CacheUtil.KEY.getKeys(PrefixConst.ONLINE_USER);
        List<OnLineUser> onLineUserList = CacheUtil.VALUE.get(keys);
        //校验结果集是否为空
        if (onLineUserList != null && !onLineUserList.isEmpty()) {
            //根据登录时间倒序排序
            onLineUserList = onLineUserList.stream().sorted(Comparator.comparing(OnLineUser::getLoginTime).reversed()).collect(Collectors.toList());
            //校验是否传入过滤条件
            Map<String, Object> params = onLineUser.getParams();
            if (!params.isEmpty()) {
                //根据账户、名称模糊查询
                String userFilterKey = "userFilter";
                if (params.containsKey(userFilterKey)) {
                    String userFilter = params.get(userFilterKey).toString();
                    if (StringUtils.isNotBlank(userFilter)) {
                        onLineUserList = onLineUserList.stream().filter(item ->
                                item.getAccount().contains(userFilter) || item.getName().contains(userFilter)
                        ).collect(Collectors.toList());
                    }
                }
                //根据IP、位置模糊查询
                String loginFilterKey = "loginFilter";
                if (params.containsKey(loginFilterKey)) {
                    String loginFilter = params.get(loginFilterKey).toString();
                    if (StringUtils.isNotBlank(loginFilter)) {
                        onLineUserList = onLineUserList.stream().filter(item ->
                                item.getIp().contains(loginFilter) || item.getPosition().contains(loginFilter)
                        ).collect(Collectors.toList());
                    }
                }
            }
            //判断是否为分页查询
            if (PageHelperUtil.isPaging(onLineUser)) {
                int total = onLineUserList.size();
                int fromIndex = (onLineUser.getPageNum() - 1) * onLineUser.getPageSize();
                int toIndex = Math.min(total, fromIndex + onLineUser.getPageSize());
                //组装结果集
                Map<String, Object> result = new HashMap<>(2);
                result.put("total", total);
                result.put("list", onLineUserList.subList(fromIndex, toIndex));
                return ApiResult.success(result);
            }
        }
        return ApiResult.success(onLineUserList);
    }

    /**
     * 强退用户
     *
     * @author kuangq
     * @date 2020-12-15 22:58
     */
    @Override
    public ApiResult deleteOnLineUser(String... uuids) {
        //遍历删除用户的登录缓存信息
        for (String uuid : uuids) {
            CacheUtil.KEY.delete(PrefixConst.LOGIN_USER + uuid);
            CacheUtil.KEY.delete(PrefixConst.ONLINE_USER + uuid);
        }
        return ApiResult.success();
    }
}
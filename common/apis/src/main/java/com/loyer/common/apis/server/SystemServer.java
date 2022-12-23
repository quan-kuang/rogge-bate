package com.loyer.common.apis.server;

import com.alibaba.fastjson.JSONObject;
import com.loyer.common.apis.demote.SystemDemote;
import com.loyer.common.dedicine.entity.ApiResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * 系统服务
 *
 * @author kuangq
 * @date 2021-03-03 13:42
 */
@FeignClient(name = "SYSTEM", fallbackFactory = SystemDemote.class)
public interface SystemServer {

    /*查询用户信息*/
    @PostMapping("user/selectUser")
    ApiResult selectUser(@RequestBody JSONObject user);

    /*保存操作日志*/
    @PostMapping("log/insertOperateLog")
    ApiResult insertOperateLog(@RequestBody Object operateLog);

    /*保存定时任务日志*/
    @PostMapping("crontab/insertCrontabLog")
    ApiResult insertCrontabLog(@RequestBody Object crontabLog);

    /*发送微信告警*/
    @PostMapping("wechat/sendWeChatAlarm")
    ApiResult sendWeChatAlarm(@RequestBody Object weChatAlarm);
}
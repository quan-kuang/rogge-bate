package com.loyer.modules.system.service.impl;

import com.loyer.common.core.enums.DatePattern;
import com.loyer.common.core.utils.common.DateUtil;
import com.loyer.common.core.utils.reflect.ContextUtil;
import com.loyer.common.dedicine.constant.SystemConst;
import com.loyer.common.dedicine.entity.ApiResult;
import com.loyer.common.redis.utils.CacheUtil;
import com.loyer.modules.system.entity.MediaInfo;
import com.loyer.modules.system.entity.WeChatMessage;
import com.loyer.modules.system.service.QianYuService;
import com.loyer.modules.system.utils.WeChatUtil;
import com.loyer.modules.system.utils.XmlUtil;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * 浅予ServiceImpl
 *
 * @author kuangq
 * @date 2023-3-21 9:52
 */
@Service
public class QianYuServiceImpl implements QianYuService {

    /**
     * 胎动打卡
     *
     * @author kuangq
     * @date 2023-03-22 13:51
     */
    @Override
    public ApiResult punchCard(String openId, String currentDate) {
        WeChatMessage weChatMessage = new WeChatMessage();
        weChatMessage.setSender(openId);
        weChatMessage.setReceiver(SystemConst.WECHAT_ID);
        weChatMessage.setMsgType("event");
        weChatMessage.setEvent("CLICK");
        weChatMessage.setEventKey("1");
        weChatMessage.setCreateTime(DateUtil.getTimestamp(currentDate, DatePattern.YMD_HMS_1) / 1000);
        String xmlStr = XmlUtil.toXmlStr(weChatMessage, WeChatMessage.class);
        return ApiResult.success(WeChatUtil.postLink(xmlStr));
    }

    /**
     * 查询最近几日的打卡信息
     *
     * @author kuangq
     * @date 2023-03-21 13:33
     */
    @Override
    public ApiResult selectEcgInfo(String openId, String currentDate, int num) {
        List<String> recentDateList = DateUtil.getRecentDate(currentDate, num);
        List<int[]> dataList = new ArrayList<>();
        for (int i = 0; i < num; i++) {
            TreeMap<String, Long> ecgCountMap = getEcgCountByHour(openId, recentDateList.get(i));
            dataList.addAll(getEchartsData(i, ecgCountMap));
        }
        Map<String, Object> result = new HashMap<>(2);
        result.put("dateList", recentDateList);
        result.put("dataList", dataList);
        return ApiResult.success(result);
    }

    /**
     * 统计每小时的记录次数
     *
     * @author kuangq
     * @date 2023-03-21 13:33
     */
    private TreeMap<String, Long> getEcgCountByHour(String openId, String date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH");
        String key = String.format("%s%s:%s", com.loyer.modules.system.constant.PrefixConst.WE_CHAT_MESSAGE, date, openId);
        List<WeChatMessage> weChatMessageList = CacheUtil.LIST.range(key);
        return new TreeMap<>(weChatMessageList.parallelStream()
                .filter(item -> "event".equals(item.getMsgType()) && "CLICK".equals(item.getEvent()) && "1".equals(item.getEventKey()))
                .map(item -> simpleDateFormat.format(item.getCreateTime() * 1000))
                .collect(Collectors.groupingBy(val -> val, Collectors.counting())));
    }

    /**
     * 组装echarts散点图数据
     *
     * @author kuangq
     * @date 2023-03-21 13:32
     */
    private List<int[]> getEchartsData(int index, TreeMap<String, Long> ecgCount) {
        List<int[]> dataList = new ArrayList<>();
        AtomicInteger atomic = new AtomicInteger();
        IntStream.range(7, 24).forEach(item -> {
            int count = ecgCount.getOrDefault(String.format("%02d", item), 0L).intValue();
            int[] data = {atomic.getAndIncrement(), index, count};
            dataList.add(data);
        });
        return dataList;
    }

    /**
     * 群发图片信息
     *
     * @author kuangq
     * @date 2023-03-22 15:10
     */
    @Override
    public ApiResult sendImageMsg(List<MediaInfo> mediaInfoList) {
        List<String> openIdList = WeChatUtil.getFollowUserIdList();
        List<String> mediaIdList = mediaInfoList.parallelStream().map(WeChatUtil::upload).collect(Collectors.toList());
        List<String> envList = Arrays.asList(ContextUtil.getApplicationContext().getEnvironment().getActiveProfiles());
        if (envList.contains("uat") || envList.contains("dev")) {
            openIdList = new ArrayList<String>() {{
                add(SystemConst.MY_OPEN_ID);
                add(SystemConst.MY_OPEN_ID);
            }};
        }
        return WeChatUtil.sendMassMessage("浅予崽的活动统计", "image", mediaIdList, openIdList);
    }
}
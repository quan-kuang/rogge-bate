package com.loyer.modules.system.service.impl;

import com.loyer.common.core.utils.common.DateUtil;
import com.loyer.common.dedicine.entity.ApiResult;
import com.loyer.common.redis.utils.CacheUtil;
import com.loyer.modules.system.entity.WeChatMessage;
import com.loyer.modules.system.service.QianYuService;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;
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
        IntStream.range(0, 24).forEach(item -> {
            int count = ecgCount.getOrDefault(String.format("%02d", item), 0L).intValue();
            int[] data = {item, index, count};
            dataList.add(data);
        });
        return dataList;
    }
}
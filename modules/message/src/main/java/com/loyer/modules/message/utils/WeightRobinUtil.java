package com.loyer.modules.message.utils;

import com.loyer.modules.message.entity.BaiduCloudKeyPair;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 加权轮询
 *
 * @author kuangq
 * @date 2020-11-02 13:28
 */
public class WeightRobinUtil {

    //百度云申请的人脸识别应用ID
    public static final Map<BaiduCloudKeyPair, Integer> AUTH_KEY_PAIR_MAP = new HashMap<BaiduCloudKeyPair, Integer>() {{
        put(new BaiduCloudKeyPair("23679675", "ws29U4pDN5G7vqmqhTDsXfLn", "FDj3zgjn63LyYpiVTNRnVPgpOero6wNE"), 1);
    }};

    //百度云申请的OCR应用ID
    public static final Map<BaiduCloudKeyPair, Integer> OCR_KEY_PAIR_MAP = new HashMap<BaiduCloudKeyPair, Integer>() {{
        put(new BaiduCloudKeyPair("23696909", "54ZE0sN8gytgEUM5rOMHqPWF", "pGGMjfRtT8bcPZg7oUjcK52CY59vMDwY"), 1);
    }};

    //轮循点
    private static Integer breakpoint = 0;

    /**
     * 轮循获取应用密匙对
     *
     * @author kuangq
     * @date 2020-11-05 17:12
     */
    public static BaiduCloudKeyPair round(Map<BaiduCloudKeyPair, Integer> keyPairMap) {
        //重建一个ConcurrentHashMap，避免服务器的上下线导致的并发问题
        Map<BaiduCloudKeyPair, Integer> keyPairConcurrentHashMap = new ConcurrentHashMap<>(keyPairMap);
        //根据key取出所有的密匙对
        Set<BaiduCloudKeyPair> keyPairSet = keyPairConcurrentHashMap.keySet();
        Iterator<BaiduCloudKeyPair> keyPairIterator = keyPairSet.iterator();
        //定义一个list存放所有的密匙对
        List<BaiduCloudKeyPair> keyPairList = new ArrayList<>();
        //遍历set，根据set中的可以去得知map中的value，给list中添加对应数字的密匙对数量
        while (keyPairIterator.hasNext()) {
            BaiduCloudKeyPair keyPair = keyPairIterator.next();
            Integer weight = keyPairConcurrentHashMap.get(keyPair);
            for (int i = 0; i < weight; i++) {
                keyPairList.add(keyPair);
            }
        }
        //获取需要拿到的密匙对的下标
        if (breakpoint >= keyPairList.size()) {
            breakpoint = 0;
        }
        BaiduCloudKeyPair keyPair = keyPairList.get(breakpoint);
        //轮询+1
        breakpoint++;
        return keyPair;
    }
}
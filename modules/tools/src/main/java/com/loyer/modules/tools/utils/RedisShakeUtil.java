package com.loyer.modules.tools.utils;

import com.loyer.common.dedicine.exception.BusinessException;
import com.loyer.common.redis.utils.CacheUtil;
import com.loyer.modules.tools.entity.RedisDataSync;
import lombok.SneakyThrows;

import java.io.File;
import java.util.concurrent.ConcurrentHashMap;

/**
 * redis数据同步工具
 *
 * @author kuangq
 * @date 2022-01-26 15:51
 */
public class RedisShakeUtil {

    private static final String REDIS_KEY = "redisDataSyncTask";

    private static final ConcurrentHashMap<String, Process> PROCESS_MAP = new ConcurrentHashMap<>();

    private static String getTaskId(RedisDataSync redisDataSync) {
        return String.format("%s<>%s", redisDataSync.getSourceAddress(), redisDataSync.getTargetAddress());
    }

    @SneakyThrows
    public static void create(RedisDataSync redisDataSync) {
        String taskId = getTaskId(redisDataSync);
        boolean taskIsExist = PROCESS_MAP.containsKey(taskId) || (CacheUtil.KEY.has(REDIS_KEY) && CacheUtil.LIST.range(REDIS_KEY).contains(taskId));
        if (taskIsExist) {
            throw new BusinessException("任务已经存在");
        }
        File file = new File("C:/Intel/DevTools/Redis-Shake-v2.1.1");
        Process process = Runtime.getRuntime().exec("cmd /c redis-shake.windows -conf=redis-shake.conf -type=sync", null, file);
        PROCESS_MAP.put(taskId, process);
        CacheUtil.LIST.rPush(REDIS_KEY, taskId);
    }

    public static void delete(RedisDataSync redisDataSync) {
        String taskId = getTaskId(redisDataSync);
        boolean taskIsExist = !PROCESS_MAP.containsKey(taskId) || !CacheUtil.KEY.has(REDIS_KEY) || !CacheUtil.LIST.range(REDIS_KEY).contains(taskId);
        if (taskIsExist) {
            throw new BusinessException("任务不存在");
        }
        Process process = PROCESS_MAP.get(taskId);
        PROCESS_MAP.remove(taskId);
        CacheUtil.LIST.delete(REDIS_KEY, taskId);
        process.destroy();
    }
}
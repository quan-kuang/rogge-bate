package com.loyer.modules.tools.service.impl;

import com.loyer.common.core.constant.SpecialCharsConst;
import com.loyer.common.core.utils.common.DateUtil;
import com.loyer.common.dedicine.entity.ApiResult;
import com.loyer.common.dedicine.enums.HintEnum;
import com.loyer.common.redis.utils.CacheUtil;
import com.loyer.modules.tools.entity.CacheInfo;
import com.loyer.modules.tools.entity.CacheInfoDetails;
import com.loyer.modules.tools.service.CacheService;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.connection.DataType;
import org.springframework.data.redis.core.ConvertingCursor;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * TODO
 *
 * @author kuangq
 * @date 2022-12-01 11:15
 */
@SuppressWarnings("unchecked")
@Service
public class CacheServiceImpl implements CacheService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * 扫描匹配key
     *
     * @author kuangq
     * @date 2022-12-09 17:35
     */
    @SuppressWarnings("rawtypes")
    @SneakyThrows
    private Set<String> scanKeys(String key) {
        final int maxSize = 1000;
        long startTime = System.currentTimeMillis();
        String pattern = StringUtils.defaultIfBlank(key, SpecialCharsConst.ASTERISK);
        ScanOptions scanOptions = ScanOptions.scanOptions().count(100).match(pattern).build();
        RedisSerializer<String> redisSerializer = (RedisSerializer<String>) CacheUtil.CLIENT.getKeySerializer();
        Cursor<String> cursor = (Cursor) CacheUtil.CLIENT.executeWithStickyConnection(redisConnection -> new ConvertingCursor<>(redisConnection.scan(scanOptions), redisSerializer::deserialize));
        Set<String> keySet = new HashSet<>();
        Assert.notNull(cursor, "cursor cannot be null");
        while (cursor.hasNext() && keySet.size() < maxSize) {
            keySet.add(cursor.next());
        }
        cursor.close();
        logger.info("scan扫描共耗时：{}S，key数量：{}", DateUtil.getTdoa(startTime), keySet.size());
        return keySet;
    }

    /**
     * 查询缓存列表
     *
     * @author kuangq
     * @date 2022-12-01 14:47
     */
    @Override
    public ApiResult selectCacheInfo(CacheInfo cacheInfo) {
        Set<String> keySet = scanKeys(cacheInfo.getKey());
        Set<CacheInfo> cacheInfoSet = Collections.synchronizedSet(new HashSet<>());
        if (!cacheInfo.getFormatTree()) {
            keySet.parallelStream().forEach(key -> {
                CacheInfo item = new CacheInfo();
                item.setKey(key);
                //noinspection ConstantConditions
                item.setType(CacheUtil.CLIENT.type(key).name());
                cacheInfoSet.add(item);
            });
            return ApiResult.success(cacheInfoSet);
        }
        keySet.parallelStream().forEach(key -> {
            if (!key.contains(SpecialCharsConst.COLON)) {
                CacheInfo item = new CacheInfo();
                item.setKey(key);
                item.setName(key);
                item.setParent("root");
                cacheInfoSet.add(item);
                return;
            }
            String[] keyAry = key.split(SpecialCharsConst.COLON);
            for (int i = 0; i < keyAry.length; i++) {
                CacheInfo item = new CacheInfo();
                item.setName(keyAry[i]);
                item.setKey(getSign(keyAry, i));
                item.setParent(getSign(keyAry, i - 1));
                cacheInfoSet.add(item);
            }
        });
        return ApiResult.success(cacheInfoSet);
    }

    /**
     * 查询缓存详情信息
     *
     * @author kuangq
     * @date 2022-12-09 16:16
     */
    @Override
    public ApiResult selectCacheInfoDetails(String key) {
        DataType dataType = CacheUtil.CLIENT.type(key);
        if (dataType == DataType.NONE) {
            return ApiResult.success();
        }
        CacheInfoDetails cacheInfoDetails = new CacheInfoDetails();
        cacheInfoDetails.setKey(key);
        //noinspection ConstantConditions
        cacheInfoDetails.setType(dataType.name());
        cacheInfoDetails.setExpire(CacheUtil.STRING.getExpire(key));
        cacheInfoDetails.setExpireHum(DateUtil.getStr(cacheInfoDetails.getExpire()));
        if (dataType == DataType.STRING) {
            cacheInfoDetails.setValue(CacheUtil.STRING.get(key));
        } else if (dataType == DataType.SET) {
            cacheInfoDetails.setValue(CacheUtil.SET.members(key));
        } else if (dataType == DataType.LIST) {
            cacheInfoDetails.setValue(CacheUtil.LIST.range(key));
        } else if (dataType == DataType.HASH) {
            cacheInfoDetails.setValue(CacheUtil.HASH.getAll(key));
        } else {
            return ApiResult.hintEnum(HintEnum.HINT_1029);
        }
        return ApiResult.success(cacheInfoDetails);
    }

    /**
     * 截取key前缀
     *
     * @author kuangq
     * @date 2022-12-01 14:46
     */
    private String getSign(String[] keyAry, int i) {
        return i < 0 ? "root" : Arrays.stream(keyAry).limit(i + 1).collect(Collectors.joining(SpecialCharsConst.COLON));
    }

    /**
     * 删除缓存信息
     *
     * @author kuangq
     * @date 2022-12-09 16:16
     */
    @Override
    public ApiResult deleteCacheInfo(CacheInfoDetails cacheInfoDetails) {
        String key = cacheInfoDetails.getKey();
        if (StringUtils.isBlank(key)) {
            return ApiResult.failure("key can not be blank", null);
        }
        String type = cacheInfoDetails.getType();
        if (StringUtils.isBlank(type)) {
            Set<String> keySet = CacheUtil.CLIENT.keys(key);
            if (keySet == null) {
                return ApiResult.success();
            }
            keySet.parallelStream().forEach(item -> CacheUtil.KEY.delete(item));
            return ApiResult.success(keySet.size());
        }
        DataType dataType = DataType.valueOf(type);
        if (dataType == DataType.STRING) {
            return ApiResult.success(CacheUtil.KEY.delete(key));
        }
        if (dataType == DataType.HASH) {
            String field = cacheInfoDetails.getField();
            if (StringUtils.isBlank(field)) {
                return ApiResult.failure("field can not be blank", null);
            }
            return ApiResult.success(CacheUtil.HASH.delete(key, field));
        }
        Object value = cacheInfoDetails.getValue();
        if (value == null) {
            return ApiResult.failure("value can not be empty", null);
        }
        if (dataType == DataType.SET) {
            return ApiResult.success(CacheUtil.SET.delete(key, value));
        }
        if (dataType == DataType.LIST) {
            return ApiResult.success(CacheUtil.LIST.delete(key, value));
        }
        return ApiResult.hintEnum(HintEnum.HINT_1080);
    }

    /**
     * 保存缓存信息
     *
     * @author kuangq
     * @date 2022-12-13 15:02
     */
    @Override
    public ApiResult saveCacheInfo(CacheInfoDetails cacheInfoDetails) {
        String key = cacheInfoDetails.getKey();
        Object newValue = cacheInfoDetails.getNewValue();
        DataType dataType = DataType.valueOf(cacheInfoDetails.getType());
        if (dataType == DataType.STRING) {
            CacheUtil.STRING.set(key, newValue);
            return ApiResult.success();
        }
        if (dataType == DataType.HASH) {
            String field = cacheInfoDetails.getField();
            CacheUtil.HASH.put(key, field, newValue);
            return ApiResult.success();
        }
        Object oldValue = cacheInfoDetails.getValue();
        if (dataType == DataType.SET) {
            CacheUtil.SET.delete(key, oldValue);
            CacheUtil.SET.add(key, newValue);
            return ApiResult.success();
        }
        if (dataType == DataType.LIST) {
            CacheUtil.LIST.rPush(key, oldValue, newValue);
            CacheUtil.LIST.delete(key, oldValue);
            return ApiResult.success();
        }
        return ApiResult.hintEnum(HintEnum.HINT_1080);
    }
}
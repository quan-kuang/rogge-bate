package com.loyer.modules.tools.utils;

import com.loyer.common.core.constant.SpecialCharsConst;
import com.loyer.common.core.utils.common.DateUtil;
import com.loyer.common.core.utils.common.OracleUtil;
import com.loyer.common.dedicine.entity.ApiResult;
import com.loyer.common.dedicine.enums.HintEnum;
import com.loyer.common.dedicine.exception.BusinessException;
import com.loyer.common.dedicine.utils.TypeUtil;
import com.loyer.modules.tools.entity.ClusterNode;
import com.loyer.modules.tools.entity.JedisEntity;
import com.loyer.modules.tools.enums.RedisRoleType;
import com.loyer.modules.tools.enums.RedisType;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.*;
import redis.clients.jedis.util.JedisClusterCRC16;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.IntStream;

/**
 * jedis工具类
 *
 * @author kuangq
 * @date 2021-09-08 15:29
 */
public class JedisUtil {

    private static final Logger logger = LoggerFactory.getLogger(JedisUtil.class);

    /**
     * 获取jedis实例链接
     *
     * @author kuangq
     * @date 2021-09-08 22:57
     */
    public static Jedis getJedis(JedisEntity jedisEntity) {
        Jedis jedis = null;
        try {
            jedis = new Jedis(jedisEntity.getIp(), jedisEntity.getPort());
            if (StringUtils.isNotBlank(jedisEntity.getPassword())) {
                jedis.auth(jedisEntity.getPassword());
            }
            jedis.ping();
        } catch (Exception e) {
            if ("ERR unknown command 'AUTH'".equals(e.getMessage())) {
                return getJedis(new JedisEntity(jedisEntity.getIp(), jedisEntity.getPort()));
            }
            if (jedis != null) {
                jedis.close();
            }
            throw new BusinessException(String.format("【%s:%s】连接失败：%s", jedisEntity.getIp(), jedisEntity.getPort(), e.getMessage()));
        }
        return jedis;
    }

    /**
     * 获取jedis
     *
     * @author kuangq
     * @date 2021-09-08 22:57
     */
    public static ApiResult getJedis(JedisEntity jedisEntity, String type, String param) {
        try (Jedis jedis = getJedis(jedisEntity)) {
            switch (type) {
                case "info":
                    return ApiResult.success(StringUtils.isBlank(param) ? infoFormat(jedis.info()) : infoFormat(jedis.info(param)));
                case "config":
                    return ApiResult.success(StringUtils.isBlank(param) ? configFormat(jedis.configGet(SpecialCharsConst.ASTERISK)) : configFormat(jedis.configGet(param)));
                case "clientList":
                    return ApiResult.success(clientListFormat(jedis.clientList()));
                case "clusterInfo":
                    return ApiResult.success(infoFormat(jedis.clusterInfo()));
                case "clusterNodes":
                    return ApiResult.success(clusterNodesFormat(jedis.clusterNodes()));
                case "clusterSlots":
                    return ApiResult.success(clusterSlotsFormat(jedis.clusterSlots()));
                default:
                    return ApiResult.hintEnum(HintEnum.HINT_1080, type);
            }
        }
    }

    /**
     * 格式化info字符串信息
     *
     * @author kuangq
     * @date 2021-09-08 22:55
     */
    public static Map<String, String> infoFormat(String str) {
        Map<String, String> map = new HashMap<>(88);
        for (String itemStr : str.split(SpecialCharsConst.LINE_FEED)) {
            if (StringUtils.isBlank(itemStr.trim()) || StringUtils.startsWith(itemStr, SpecialCharsConst.POUND_SIGN)) {
                continue;
            }
            if (itemStr.contains(SpecialCharsConst.COLON)) {
                String[] itemStrAry = itemStr.split(SpecialCharsConst.COLON);
                String key = itemStrAry[0];
                if (itemStrAry.length == 2) {
                    map.put(key, itemStrAry[1].replace(SpecialCharsConst.ENTER, SpecialCharsConst.BLANK));
                } else {
                    map.put(key, StringUtils.substringAfterLast(itemStr, key).replace(SpecialCharsConst.ENTER, SpecialCharsConst.BLANK));
                }
            } else {
                logger.error("无法匹配的info数据：{}", itemStr);
            }
        }
        return map;
    }

    /**
     * 格式化configList信息
     *
     * @author kuangq
     * @date 2021-09-08 22:55
     */
    private static Map<String, String> configFormat(List<String> strList) {
        Map<String, String> map = new HashMap<>(66);
        for (int i = 0; i < strList.size() / 2; i++) {
            map.put(strList.get(2 * i), strList.get(2 * i + 1));
        }
        return map;
    }

    /**
     * 格式化clientList信息，个别字段key处理
     *
     * @author kuangq
     * @date 2021-09-08 22:56
     */
    private static List<Map<String, String>> clientListFormat(String str) {
        List<Map<String, String>> mapList = new ArrayList<>();
        for (String listStr : str.split(SpecialCharsConst.LINE_FEED)) {
            Map<String, String> map = new HashMap<>(22);
            for (String itemStr : listStr.split(SpecialCharsConst.SPACE)) {
                String[] itemAry = itemStr.split(SpecialCharsConst.EQUAL_SIGN);
                String key = Objects.requireNonNull(OracleUtil.decode(itemAry[0], "addr", "addressPort", "cmd", "lastCommand", itemAry[0])).toString();
                String value = itemAry.length > 1 ? itemAry[1] : SpecialCharsConst.BLANK;
                map.put(key, value);
            }
            mapList.add(map);
        }
        return mapList;
    }

    /**
     * 格式化slave字符串信息
     *
     * @author kuangq
     * @date 2022-01-11 17:51
     */
    protected static Map<String, String> slaveFormat(String str) {
        Map<String, String> map = new HashMap<>(8);
        for (String itemStr : str.split(SpecialCharsConst.COMMA)) {
            map.put(itemStr.split(SpecialCharsConst.EQUAL_SIGN)[0], itemStr.split(SpecialCharsConst.EQUAL_SIGN)[1]);
        }
        return map;
    }

    /**
     * 格式化clusterNodes信息
     *
     * @author kuangq
     * @date 2022-01-05 10:16
     */
    protected static List<ClusterNode> clusterNodesFormat(String str) {
        List<ClusterNode> clusterNodeList = new ArrayList<>();
        for (String info : str.split(SpecialCharsConst.LINE_FEED)) {
            ClusterNode clusterNode = new ClusterNode();
            String[] infoAry = info.split(SpecialCharsConst.SPACE);
            String[] hostAry = infoAry[1].split(SpecialCharsConst.COLON);
            clusterNode.setId(infoAry[0]);
            clusterNode.setIp(hostAry[0]);
            clusterNode.setPort(Integer.parseInt(hostAry[1].split(SpecialCharsConst.AIT)[0]));
            clusterNode.setStatus(infoAry[2]);
            clusterNode.setIsConnect(!clusterNode.getStatus().contains("fail") && !clusterNode.getStatus().contains("handshake"));
            clusterNodeList.add(clusterNode);
        }
        return clusterNodeList;
    }

    /**
     * 格式化槽位信息
     *
     * @author kuangq
     * @date 2022-01-13 17:56
     */
    public static TreeMap<Integer, String> clusterSlotsFormat(List<Object> objectList) {
        TreeMap<Integer, String> slotHostMap = new TreeMap<>();
        objectList.forEach(item -> {
            List<Object> slotObject = TypeUtil.convert(item);
            Integer startSlot = ((Long) slotObject.get(0)).intValue();
            Integer endSlot = ((Long) slotObject.get(1)).intValue();
            List<Object> hostMap = TypeUtil.convert(slotObject.get(2));
            String hostAndPort = String.format("%s:%s", new String((byte[]) hostMap.get(0)), hostMap.get(1));
            slotHostMap.put(startSlot, hostAndPort);
            slotHostMap.put(endSlot, hostAndPort);
        });
        return slotHostMap;
    }

    /**
     * 设置jedisConfig
     *
     * @author kuangq
     * @date 2021-09-08 22:54
     */
    public static ApiResult configSet(JedisEntity jedisEntity) {
        Map<String, String> config = jedisEntity.getConfig();
        if (config == null || config.isEmpty()) {
            return ApiResult.failure("入参不能为空");
        }
        try (Jedis jedis = getJedis(jedisEntity)) {
            Map<String, Object> result = new HashMap<>(36);
            for (Map.Entry<String, String> entry : config.entrySet()) {
                String key = entry.getKey();
                try {
                    jedis.configSet(key, entry.getValue());
                    result.put(key, true);
                } catch (Exception e) {
                    result.put(key, e.getMessage());
                    logger.error("jedis配置项{}设置异常：{}", key, e.getMessage());
                }
            }
            return ApiResult.success(result);
        }
    }

    /**
     * 批量写入数据
     *
     * @author kuangq
     * @date 2021-09-10 17:12
     */
    public static ApiResult batchWriteData(JedisEntity jedisEntity) {
        String tempType = jedisEntity.getType();
        if (StringUtils.isBlank(tempType)) {
            return ApiResult.failure("type：类型不能为空", null);
        }
        String type = tempType.trim().toUpperCase();
        RedisType[] redisTypeArray = RedisType.values();
        if (Arrays.stream(redisTypeArray).noneMatch(item -> item.name().equals(type))) {
            return ApiResult.failure("type：参数异常，不在指定范围内", redisTypeArray);
        }
        if (jedisEntity.getWriteData() == null) {
            String data = DateUtil.getTimestamp();
            jedisEntity.setWriteData(data);
        }
        if (RedisType.valueOf(type).equals(RedisType.S)) {
            return batchWriteDataBySingle(jedisEntity);
        } else if (RedisType.valueOf(type).equals(RedisType.M)) {
            return batchWriteDataByMasterSlave(jedisEntity);
        } else if (RedisType.valueOf(type).equals(RedisType.C)) {
            return batchWriteDataByCluster(jedisEntity);
        }
        return ApiResult.hintEnum(HintEnum.HINT_1029);
    }

    /**
     * 单机模式数据写入
     *
     * @author kuangq
     * @date 2021-10-15 18:10
     */
    private static ApiResult batchWriteDataBySingle(JedisEntity jedisEntity) {
        try (Jedis jedis = getJedis(jedisEntity)) {
            Pipeline pipeline = jedis.pipelined();
            long startTime = System.currentTimeMillis();
            IntStream.range(0, jedisEntity.getWriteCount()).forEach((item) -> {
                String key = String.format("batch:%s:%s", startTime, item);
                pipeline.set(key, jedisEntity.getWriteData().toString());
                pipeline.expire(key, jedisEntity.getExpireTime());
            });
            pipeline.syncAndReturnAll();
            pipeline.close();
            double elapsedTime = DateUtil.getTdoa(startTime);
            return ApiResult.success(String.format("共写入%s条数据，耗时%sS", jedisEntity.getWriteCount(), elapsedTime));
        }
    }


    /**
     * 主从模式数据写入
     *
     * @author kuangq
     * @date 2022-02-23 18:41
     */
    private static ApiResult batchWriteDataByMasterSlave(JedisEntity jedisEntity) {
        Map<String, String> infoMap;
        try (Jedis jedis = getJedis(jedisEntity)) {
            infoMap = infoFormat(jedis.info());
        }
        if (RedisRoleType.SLAVE.name().equalsIgnoreCase(infoMap.get("role"))) {
            jedisEntity.setIp(infoMap.get("master_host"));
            jedisEntity.setPort(Integer.valueOf(infoMap.get("master_port")));
        } else if (RedisRoleType.SENTINEL.name().equalsIgnoreCase(infoMap.get("redis_mode"))) {
            String masterInfo = infoMap.get("master0");
            if (StringUtils.isBlank(masterInfo)) {
                return ApiResult.failure("找不到主机节点");
            }
            String masterHost = slaveFormat(masterInfo).get("address");
            jedisEntity.setIp(masterHost.split(SpecialCharsConst.COLON)[0]);
            jedisEntity.setPort(Integer.valueOf(masterHost.split(SpecialCharsConst.COLON)[1]));
        }
        return batchWriteDataBySingle(jedisEntity);
    }

    /**
     * 集群模式数据写入
     *
     * @author kuangq
     * @date 2021-10-15 18:10
     */
    private static ApiResult batchWriteDataByCluster(JedisEntity jedisEntity) {
        //节点配置
        Set<HostAndPort> hostAndPortSet = new HashSet<>();
        hostAndPortSet.add(new HostAndPort(jedisEntity.getIp(), jedisEntity.getPort()));
        //以集群模式链接
        JedisCluster jedisCluster = new JedisCluster(hostAndPortSet, getJedisPoolConfig());
        //获取入参
        long startTime = System.currentTimeMillis();
        //剔除异常节点，获取槽位信息
        Map<String, JedisPool> clusterNodes = jedisCluster.getClusterNodes();
        String address = getAddress(clusterNodes);
        String ip = address.split(SpecialCharsConst.COLON)[0];
        int port = Integer.parseInt(address.split(SpecialCharsConst.COLON)[1]);
        TreeMap<Integer, String> slotHostMap;
        try (Jedis jedis = new Jedis(ip, port)) {
            slotHostMap = clusterSlotsFormat(jedis.clusterSlots());
        }
        //添加管道信息
        Map<String, Integer> result = new HashMap<>(jedisEntity.getWriteCount());
        Map<String, Pipeline> pipelineMap = new HashMap<>(jedisEntity.getWriteCount());
        IntStream.range(0, jedisEntity.getWriteCount()).forEach((item) -> {
            String key = String.format("batch:%s:%s", startTime, item);
            //根据key获取槽位获取redis节点
            int slot = JedisClusterCRC16.getSlot(key);
            int slotKey = slotHostMap.floorKey(slot);
            String host = slotHostMap.get(slotKey);
            //判断节点是否存在
            if (!clusterNodes.containsKey(host)) {
                return;
            }
            //添加管道信息
            if (!pipelineMap.containsKey(host)) {
                try (Jedis jedis = clusterNodes.get(host).getResource()) {
                    pipelineMap.put(host, jedis.pipelined());
                }
            }
            Pipeline pipeline = pipelineMap.get(host);
            pipeline.set(key, jedisEntity.getWriteData().toString());
            pipeline.expire(key, jedisEntity.getExpireTime());
            //记录每个节点的添加数量
            result.put(host, result.getOrDefault(host, 0) + 1);
        });
        //批量写入
        pipelineMap.values().parallelStream().forEach(pipeline -> {
            pipeline.syncAndReturnAll();
            pipeline.close();
        });
        jedisCluster.close();
        double elapsedTime = DateUtil.getTdoa(startTime);
        int writeCount = result.values().stream().mapToInt(i -> i).sum();
        return ApiResult.success(String.format("共写入%s条数据，耗时%sS", writeCount, elapsedTime), result);
    }

    /**
     * 获取连接池配置
     *
     * @author kuangq
     * @date 2021-10-20 11:25
     */
    private static JedisPoolConfig getJedisPoolConfig() {
        //连接池配置，默认配置GenericObjectPoolConfig
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        //最小空闲连接数, 默认0
        jedisPoolConfig.setMinIdle(0);
        //最大空闲连接数, 默认8
        jedisPoolConfig.setMaxIdle(10);
        //最大连接数, 默认8
        jedisPoolConfig.setMaxTotal(100);
        //获取连接时的最大等待毫秒数,  默认-1
        jedisPoolConfig.setMaxWaitMillis(1000);
        //对拿到的connection进行validateObject校验
        jedisPoolConfig.setTestOnBorrow(true);
        //链接实例在返回时校验，校验失败则废弃
        jedisPoolConfig.setTestOnReturn(true);
        return jedisPoolConfig;
    }

    /**
     * 剔除异常节点
     *
     * @author kuangq
     * @date 2021-10-20 11:42
     */
    private static String getAddress(Map<String, JedisPool> clusterNodes) {
        AtomicReference<String> address = new AtomicReference<>();
        Set<String> addressErrorSet = new TreeSet<>();
        clusterNodes.forEach((k, v) -> {
            try (Jedis ignored = v.getResource()) {
                address.set(k);
            } catch (Exception e) {
                addressErrorSet.add(k);
                logger.error("【节点异常】{}：{}", k, e.getMessage());
            }
        });
        addressErrorSet.forEach(clusterNodes::remove);
        return address.get();
    }

    /**
     * 获取节点槽位信息
     *
     * @author kuangq
     * @date 2021-10-15 16:14
     */
    @SuppressWarnings("unchecked")
    private static TreeMap<Long, String> getSlotHostMap(String host) {
        String ip = host.split(SpecialCharsConst.COLON)[0];
        int port = Integer.parseInt(host.split(SpecialCharsConst.COLON)[1]);
        TreeMap<Long, String> slotHostMap = new TreeMap<>();
        try (Jedis jedis = new Jedis(ip, port)) {
            jedis.clusterSlots().forEach(item -> {
                List<Object> slotObject = (List<Object>) item;
                Long startSlot = (Long) slotObject.get(0);
                Long endSlot = (Long) slotObject.get(1);
                List<Object> hostMap = (List<Object>) slotObject.get(2);
                String hostAndPort = String.format("%s:%s", new String((byte[]) hostMap.get(0)), hostMap.get(1));
                slotHostMap.put(startSlot, hostAndPort);
                slotHostMap.put(endSlot, hostAndPort);
            });
        }
        return slotHostMap;
    }
}
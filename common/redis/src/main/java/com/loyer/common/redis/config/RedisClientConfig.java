package com.loyer.common.redis.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.loyer.common.redis.constant.DefaultConst;
import com.loyer.common.redis.realize.FastJson2JsonRedisSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisClientConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import redis.clients.jedis.JedisPoolConfig;

import java.time.Duration;

/**
 * 定义redis客户端
 *
 * @author kuangq
 * @date 2022-08-18 22:08
 */
@SuppressWarnings({"rawtypes", "unchecked"})
@Order(Ordered.HIGHEST_PRECEDENCE)
@Configuration
public class RedisClientConfig {

    @Value("${spring.redis.host}")
    private String host;

    @Value("${spring.redis.port}")
    private int port;

    @Value("${spring.redis.password}")
    private String password;

    @Value("${spring.redis.timeout}")
    private int timeout;

    @Value("${spring.redis.database}")
    private int database;

    /**
     * Jedis连接池
     *
     * @author kuangq
     * @date 2022-08-19 17:00
     */
    @Bean
    public JedisPoolConfig jedisPoolConfig() {
        //创建连接池对象
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        //最大空闲数
        jedisPoolConfig.setMaxIdle(10);
        //连接池最大连接数
        jedisPoolConfig.setMaxTotal(3000);
        //连接最大等待时间
        jedisPoolConfig.setMaxWaitMillis(3000);
        //每次逐出的最大数量
        jedisPoolConfig.setNumTestsPerEvictionRun(300);
        //逐出最小空闲时间
        jedisPoolConfig.setMinEvictableIdleTimeMillis(3000);
        //逐出扫描时间的间隔
        jedisPoolConfig.setTimeBetweenEvictionRunsMillis(30000);
        //是否在从池中取出连接前进行检验，如果检验失败，则从池中去除连接并尝试取出另一个
        jedisPoolConfig.setTestOnBorrow(true);
        //空闲时检查有效性
        jedisPoolConfig.setTestWhileIdle(true);
        return jedisPoolConfig;
    }

    /**
     * 定义客户端连接
     *
     * @author kuangq
     * @date 2022-08-19 17:00
     */
    @Bean
    public RedisConnectionFactory redisConnectionFactory(JedisPoolConfig jedisPoolConfig) {
        JedisClientConfiguration.JedisClientConfigurationBuilder clientConfig = JedisClientConfiguration.builder();
        clientConfig.clientName(DefaultConst.CLIENT_NAME);
        clientConfig.connectTimeout(Duration.ofMillis(timeout));
        clientConfig.usePooling().poolConfig(jedisPoolConfig);
        RedisStandaloneConfiguration redisConfig = new RedisStandaloneConfiguration();
        redisConfig.setHostName(host);
        redisConfig.setPort(port);
        redisConfig.setPassword(RedisPassword.of(password));
        redisConfig.setDatabase(database);
        return new JedisConnectionFactory(redisConfig, clientConfig.build());
    }

    /**
     * 自定义序列化, 处理redisTemplate默认存储二进制字节码问题
     *
     * @author kuangq
     * @date 2019-08-27 22:13
     */
    @Bean
    public RedisTemplate redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        //定义FastJson2JsonRedisSerializer替换默认序列化Jackson2JsonRedisSerialize
        FastJson2JsonRedisSerializer<?> fastJson2JsonRedisSerializer = new FastJson2JsonRedisSerializer<>(Object.class);
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        objectMapper.activateDefaultTyping(LaissezFaireSubTypeValidator.instance, ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.WRAPPER_ARRAY);
        fastJson2JsonRedisSerializer.setObjectMapper(objectMapper);
        //设置key和value的序列化规则
        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
        RedisTemplate redisTemplate = new RedisTemplate();
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        redisTemplate.setKeySerializer(stringRedisSerializer);
        redisTemplate.setValueSerializer(fastJson2JsonRedisSerializer);
        redisTemplate.setHashKeySerializer(fastJson2JsonRedisSerializer);
        redisTemplate.setHashValueSerializer(fastJson2JsonRedisSerializer);
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }
}
package com.wyl.redis.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPoolConfig;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 *  @author: wuyunl
 *  @Description: redis配置类，集群
 *  @Date: 2020/1/2 14:46
 */
@Configuration
public class RedisClusterConfig {

    private final Logger LOG = LoggerFactory.getLogger(RedisClusterConfig.class);

    @Value("${spring.redis.host}")
    private String host;

    @Value("${spring.redis.port}")
    private int port;

    @Value("${spring.redis.timeout}")
    private int timeout;

    @Value("${spring.redis.jedis.pool.max-active}")
    private int maxActive;

    @Value("${spring.redis.jedis.pool.max-idle}")
    private int maxIdle;

    @Value("${spring.redis.jedis.pool.max-wait}")
    private int maxWaitMillis;

    @Value("${spring.redis.jedis.pool.min-idle}")
    private int minIdle;

    @Value("${spring.redis.password}")
    private String password;

    @Value("${spring.redis.cluster.nodes}")
    private String nodes;


    /**
     * 初始化jedis连接池
     */
    @Bean
    public JedisCluster redisCluster() {
        // 创建jedis池配置实例
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();

        // 设连接池最大连接数
        jedisPoolConfig.setMaxTotal(maxActive);

        // 连接池中的最大空闲连接
        jedisPoolConfig.setMaxIdle(maxIdle);

        // 连接池最大阻塞等待时间
        jedisPoolConfig.setMaxWaitMillis(maxWaitMillis);

        //连接池中的最小空闲连接
        jedisPoolConfig.setMinIdle(minIdle);

        // 是否启用pool的jmx管理功能, 默认true
        jedisPoolConfig.setJmxEnabled(true);

        // #jedis调用borrowObject方法时，是否进行有效检查
        jedisPoolConfig.setTestOnBorrow(true);

        // #jedis调用returnObject方法时，是否进行有效检查
//        jedisPoolConfig.setTestOnReturn(Boolean.valueOf(true));
        Set<HostAndPort> nodesList = new HashSet<>();
        String[] redisNodes = nodes.split(",");
        for (String node : redisNodes) {
            String[] parts = StringUtils.split(node, ":");
            Assert.state(parts.length == 2, "redis node shoule be defined as 'host:port', not '" + Arrays.toString(parts) + "'");
            nodesList.add(new HostAndPort(parts[0], Integer.valueOf(parts[1])));
        }
        //没有配置连接池使用默认的连接池
        //return new JedisCluster(nodes);

        //创建集群对象
        JedisCluster jedisCluster = null;
        if (!StringUtils.isEmpty(password)) {
            jedisCluster = new JedisCluster(nodesList, timeout, 1500, 3, password, jedisPoolConfig);
        } else {
            jedisCluster = new JedisCluster(nodesList,timeout,jedisPoolConfig);
        }
        return jedisCluster;
    }

    /**
     *  @Description: 使用下面的重载方法，需要类实现 InitializingBean
     *  @Date: 2019/12/31 16:23
     */
    /*@Override
    public void afterPropertiesSet() throws Exception {
        logger.info("****************正在启动Redis****************");
        logger.info("******Redis-Url:" + host + ":" + port + "******");
        logger.info("********************************************");
    }*/

    /**
     * redis模板配置
     * @param redisConnectionFactory
     * @return
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        //设置序列化,使用Jackson2JsonRedisSerializer来序列化和反序列化redis的value值（默认使用JDK的序列化方式）
        Jackson2JsonRedisSerializer jackson2JsonRedisSerializer =new Jackson2JsonRedisSerializer(Object.class);
        ObjectMapper objectMapper =new ObjectMapper();
        //指定要序列化的域，field,get和set,以及修饰符范围，ANY是都有包括private和public
        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        objectMapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        jackson2JsonRedisSerializer.setObjectMapper(objectMapper);

        redisTemplate.setValueSerializer(jackson2JsonRedisSerializer);
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        //redisTemplate.setValueSerializer(new StringRedisSerializer());
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashValueSerializer(new StringRedisSerializer());
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }
}

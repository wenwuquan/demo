package com.wyl.salaryincrease.study.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 *  @author: wuyunl
 *  @Description: redis配置类，单机版
 *  @Date: 2020/1/2 14:46
 */
@Configuration
public class RedisConfig {

    private final Logger LOG = LoggerFactory.getLogger(RedisConfig.class);

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


    /**
     * 初始化jedis连接池
     */
    @Bean
    public JedisPool jedisPoolConfig() {
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
        JedisPool jedisPool = null;
        try {
            // JedisPool默认的超时时间是2秒(单位毫秒)
            if (StringUtils.isEmpty(password)) {
                jedisPool = new JedisPool(jedisPoolConfig, host, port,timeout);
            } else {
                jedisPool = new JedisPool(jedisPoolConfig, host, port, timeout, password);
            }
        } catch (Exception e) {
            LOG.error("RedisUtil --> getPool happen error,", e);
        }
        return jedisPool;
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
}

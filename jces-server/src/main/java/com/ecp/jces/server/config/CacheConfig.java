package com.ecp.jces.server.config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisClientConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import redis.clients.jedis.JedisPoolConfig;

import java.time.Duration;


/**
 *
 * @date 2019/03/27
 */
@Configuration
public class CacheConfig extends CachingConfigurerSupport {

    @Value("${auth.redis.host}")
    private String host;

    @Value("${auth.redis.port}")
    private int port;

    @Value("${auth.redis.password}")
    private String password;

    @Value("${auth.redis.database}")
    private int database;

    @Value("${auth.redis.pool.max-active}")
    private int maxActive;

    @Value("${auth.redis.pool.min-idle}")
    private int minIdle;

    @Value("${auth.redis.pool.max-idle}")
    private int maxIdle;

    @Value("${auth.redis.pool.max-wait}")
    private int maxWait;

    @Bean
    public JedisConnectionFactory redisConnectionFactory() {
        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration ();
        redisStandaloneConfiguration.setHostName(host);
        redisStandaloneConfiguration.setPort(port);
        redisStandaloneConfiguration.setDatabase(database);

        if (password != null && password.length()!=0) {
            redisStandaloneConfiguration.setPassword(RedisPassword.of(password));
        }

        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxIdle(maxIdle);
        poolConfig.setMinIdle(minIdle);
        poolConfig.setMaxWaitMillis(maxWait);
        poolConfig.setMaxTotal(maxActive);

        JedisClientConfiguration jedisClientConfiguration =
                ((JedisClientConfiguration.JedisPoolingClientConfigurationBuilder) JedisClientConfiguration.builder()
                        .connectTimeout(Duration.ofMillis(3000)).readTimeout(Duration.ofMillis(3000)))
                        .poolConfig(poolConfig).build();

        return new JedisConnectionFactory(redisStandaloneConfiguration,
                jedisClientConfiguration);
    }


    @Bean
    public RedisTemplate<String, String> objectRedisTemplate() {
        RedisTemplate<String, String> template = new RedisTemplate();
        template.setConnectionFactory(redisConnectionFactory());

        // 设置value的序列化规则和 key的序列化规则
        StringRedisSerializer serializer = new StringRedisSerializer();
        template.setKeySerializer(serializer);
        template.setValueSerializer(serializer);
        template.setHashKeySerializer(serializer);
        template.setHashValueSerializer(serializer);
        template.afterPropertiesSet();
        return template;
    }
}

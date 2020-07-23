package com.sherlock.gmall.product.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * @auther Sherlock
 * @date 2020/7/17 21:23
 * @Description:
 */
@EnableCaching
@Configuration
@EnableConfigurationProperties(CacheProperties.class)
public class SpringCacheConfig {


//    @Autowired
//    private CacheProperties cacheProperties;

    /**
     * 参考 {@link org.springframework.boot.autoconfigure.cache.RedisCacheConfiguration#createConfiguration(org.springframework.boot.autoconfigure.cache.CacheProperties, java.lang.ClassLoader)}
     * 创建默认的 org.springframework.data.redis.cache.RedisCacheConfiguration
     *
     *
     * 在容器中加入 {@link RedisCacheConfiguration}之后导致application.properties中配置的相关属性不起作用解决办法
     *
     *  首先需要添加 @EnableConfigurationProperties(CacheProperties.class)将CacheProperties放入容器中
     *  在本类中注入CacheProperties，两种方法
     *
     *  ①  @Autowired
     *      private CacheProperties cacheProperties;
     *  或者
     *  ②  在public RedisCacheConfiguration redisCacheConfiguration()的方法参数中直接添加CacheProperties修改为
     *      public RedisCacheConfiguration redisCacheConfiguration(CacheProperties cacheProperties)
     *      参数中的CacheProperties cacheProperties会自动从spring容器中获取CacheProperties
     *
     * 然后参考 {@link org.springframework.boot.autoconfigure.cache.RedisCacheConfiguration#createConfiguration(org.springframework.boot.autoconfigure.cache.CacheProperties, java.lang.ClassLoader)}
     * 使用application.properties中的属性修改 RedisCacheConfiguration
     * @return
     */
    @Bean
    public RedisCacheConfiguration redisCacheConfiguration(CacheProperties cacheProperties) {

        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig();

        // 设置key的序列化为StringRedisSerializer
        config = config.serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()));
        // 设置value的序列化为GenericJackson2JsonRedisSerializer
        config = config.serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer()));

        CacheProperties.Redis redisProperties = cacheProperties.getRedis();

        if (redisProperties.getTimeToLive() != null) {
            config = config.entryTtl(redisProperties.getTimeToLive());
        }
        if (redisProperties.getKeyPrefix() != null) {
            config = config.prefixKeysWith(redisProperties.getKeyPrefix());
        }
        if (!redisProperties.isCacheNullValues()) {
            config = config.disableCachingNullValues();
        }
        if (!redisProperties.isUseKeyPrefix()) {
            config = config.disableKeyPrefix();
        }
        return config;
    }

}

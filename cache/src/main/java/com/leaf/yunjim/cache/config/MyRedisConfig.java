package com.leaf.yunjim.cache.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;

import java.net.UnknownHostException;

/**
 * @author: JimLin
 * @email: leafyunjim@gmail.com
 * @description: Redis 配置类：重写序列化规则，改为 json 序列化规则
 * @date: 2018-11-18
 * @time: 15:36
 */
@Configuration
public class MyRedisConfig {

    @Bean
    public RedisTemplate<Object, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) throws UnknownHostException {
        RedisTemplate<Object, Object> template = new RedisTemplate<Object, Object>();
        // 设置链接工厂
        template.setConnectionFactory(redisConnectionFactory);
        // 设置序列化机制：改为 Jackson
        Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<>(Object.class);
        // 设置 ObjectMapper（序列化和反序列化的核心组件）
        ObjectMapper om = new ObjectMapper();
        // 设置可见度:任何属性，就是指定要序列化的域，field,get 和 set,以及修饰符范围，ANY 是都有包括 private 和 public
        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        // 启动默认的类型：指定序列化输入的类型，类必须是非 final 修饰的，final 修饰的类，比如 String,Integer 等会跑出异常
        om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        // 序列化类，对象映射设置
        jackson2JsonRedisSerializer.setObjectMapper(om);
        template.setDefaultSerializer(jackson2JsonRedisSerializer);
        template.afterPropertiesSet();
        return template;
    }

    @Bean
    public RedisCacheManager cacheManager(RedisTemplate<Object, Object> redisTemplate) {
        RedisCacheManager cacheManager = new RedisCacheManager(redisTemplate);
        // 使用前缀，默认会将 CacheName 作为key的前缀
        cacheManager.setUsePrefix(true);
        //设置缓存过期时间(秒)
//        cacheManager.setDefaultExpiration(600);
        return cacheManager;

    }

}

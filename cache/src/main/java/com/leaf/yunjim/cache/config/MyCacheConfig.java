package com.leaf.yunjim.cache.config;

import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.lang.reflect.Method;
import java.util.Arrays;

@Configuration
public class MyCacheConfig {

    // 注入容器中， 默认组件的 id 叫 myKeyGenerator
    @Bean("myKeyGenerator")
    public KeyGenerator keyGenerator(){
        // KeyGenerator 是个接口，匿名内部类方式实现
        return new KeyGenerator(){

            /**
             *
             * @param o：目标对象
             * @param method：方法
             * @param args：参数
             * @return
             */
            @Override
            public Object generate(Object o, Method method, Object... args) {
                // 自定义key
                return method.getName() + "[" +Arrays.asList(args).toString() + "]";
            }
        };
    }
}

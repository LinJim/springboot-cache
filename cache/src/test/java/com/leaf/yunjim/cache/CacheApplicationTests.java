package com.leaf.yunjim.cache;

import com.leaf.yunjim.cache.bean.Employee;
import com.leaf.yunjim.cache.mapper.EmployeeMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CacheApplicationTests {

    @Autowired
    EmployeeMapper employeeMapper;


    // 这个操作 k-v 都是字符串的 redis 缓存
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    // 这个操作 k-v 都是对象的 redis 缓存
    @Autowired
    private RedisTemplate redisTemplate;

    @Test
    public void contextLoads() {
    }

    /**
     * 基本的 redis 数据类型：String List Set Hash（三列） ZSet（有序集合）
     * stringRedisTemplate.opsForValue();(String)
     * stringRedisTemplate.opsForHash();（Hash）
     * stringRedisTemplate.opsForSet(); (Set)
     * stringRedisTemplate.opsForZSet() (ZSet)
     */
    @Test
    public void test01() {
        stringRedisTemplate.opsForValue().append("hello", "panda");
        stringRedisTemplate.opsForValue().get("hello");
    }

    //测试保存对象
    @Test
    public void test02() {
        Employee emp = employeeMapper.getEmpById(1);
        // 如果保存的是对象，默认使用jdk序列化机制，将序列化数据保存到redis。
//        ArrayList<Employee> list = new ArrayList<Employee>();
//        list.add(emp);
//        list.add(emp);
//        list.add(emp);
//        list.add(emp);
        redisTemplate.opsForValue().set("emp-01", emp);
        // 自定义配置，改成json数据格式序列化
//        empRedisTemplate.opsForValue().set("emp-01", emp);
//        List<Employee> o = (ArrayList<Employee>)redisTemplate.opsForValue().get("emp-01");
//        System.out.println(o);
    }


}

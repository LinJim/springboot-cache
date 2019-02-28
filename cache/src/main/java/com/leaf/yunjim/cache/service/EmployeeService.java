package com.leaf.yunjim.cache.service;

import com.leaf.yunjim.cache.bean.Employee;
import com.leaf.yunjim.cache.mapper.EmployeeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.stereotype.Service;


/**
 * @author: JimLin
 * @email: leafyunjim@gmail.com
 * @description:
 * @date: 2018-11-17
 * @time: 12:45
 */
@Service
public class EmployeeService {

    @Autowired
    private EmployeeMapper employeeMapper;

    /**
     * 注解 @Cacheable ：将方法的运行结果进行缓存：以后再要相同的数据，直接从缓存中获取，不用调用方法,因此就不会再进入方法
     * 几个属性：
     * 1.cacheNames/value：指定缓存组件的名字,指定将方法的返回结果放到以此命名的缓存中，可以数组方式指定多个缓存。
     * 2.key：缓存数据时使用的key，可以用它来指定，默认是使用方法参数的值, 支持 SpEL 表达式：#id（参数id的值）, #a0 ,#p0, #root.args[0](#root 这个表示跟对象，可以得到所有参数名，方法名，甚至返回值)
     *        SpEL表达式：key="#root.methodName+'['+ #id + ']'" => key="getEmp[2]"
     * 3.keyGenerator：key 的生成器；可以自定义该生成器；key/keyGenerator 二选一,自定义 keyGenerator: @Cacheable(cacheNames = {"emp"}, keyGenerator = "myKeyGenerator")
     * 4.cacheManager（指定缓存管理器）和 cacheResolver（指定缓存解析器），功能类似
     * 5.condition：指定符合条件的情况下才缓存；支持SpEL表达式：condition = "#a0>1" 或者 condition = "#a0 eq 1"
     * 6.unless：否定缓存；当unless指定的条件为true，方法的返回值不会被缓存；可以获取到结果进行判断，如 unless="#result == null"
     * 7.sync：是否使用异步模式：默认同步模式，是指方法执行完同步将返回值保存到缓存中
     *
     * 原理：
     * 1、自动配置类 CacheAutoConfiguration
     * 2、自动引入的相关配置类
     * org.springframework.boot.autoconfigure.cache.GenericCacheConfiguration
     * org.springframework.boot.autoconfigure.cache.JCacheCacheConfiguration
     * org.springframework.boot.autoconfigure.cache.EhCacheCacheConfiguration
     * org.springframework.boot.autoconfigure.cache.HazelcastCacheConfiguration
     * org.springframework.boot.autoconfigure.cache.InfinispanCacheConfiguration
     * org.springframework.boot.autoconfigure.cache.CouchbaseCacheConfiguration
     * org.springframework.boot.autoconfigure.cache.RedisCacheConfiguration
     * org.springframework.boot.autoconfigure.cache.CaffeineCacheConfiguration
     * org.springframework.boot.autoconfigure.cache.GuavaCacheConfiguration
     * org.springframework.boot.autoconfigure.cache.SimpleCacheConfiguration【默认开启这个配置】
     * org.springframework.boot.autoconfigure.cache.NoOpCacheConfiguration
     *
     * 3、哪个配置类生效: 通过启动时自动配置类报告，得知SimpleCacheConfiguration生效
     * 4、SimpleCacheConfiguration 给容器中注册了缓存管理器：ConcurrentMapCacheManager ,将数据保存在 ConcurrentMap(ConcurrentHashMap) 中,其使用分段锁来保证线程安全
     *    可以获取和创建 ConcurrentMapCache 类型缓存组件 Cache ，将数据保存在 ConcurrentMap 中
     *
     *
     * 5、总的运行流程：@Cacheable 标注的方法执行之前先检查缓存中有无这个数据，默认按照参数值查询缓存，如果没有就将运行目标方法，并将目标方法的返回结果放入缓存；如果有直接使用缓存中的数据
     * 5-1、方法运行之前，先去查询 Cache（缓存组件），按照 cacheNames 指定的名字获取；CacheManager 先获取相应的缓存，第一次会自动创建不存在的 Cache
     * 5-2、去 Cache 中查找缓存的内容，使用默认的方法的参数作为 key 来查找，key是按照某种策略生成的：默认是使用 keyGenerator 生成的，这个又是默认使用 SimpleKeyGenerator
     *      SimpleKeyGenerator 的生成 key 策略：如果没有参数：key = new SimpleKey();如果有一个参数：key = 参数值；如果有多个参数：key = new SimpeKey(params)
     * 5-3、如果没有查找缓存内容就调用目标方法；并将方法的返回值放到 Cache 中，如果有直接使用缓存中的数据
     *
     * 6、核心
     *    6-1、使用 CacheManager【在没配置的情况下默认是：ConcurrentMapCacheManager】按照名字得到 Cache【ConcurrentMapCache】组件
     *    6-2、key使用 keyGenerator 生成的，默认是 SimpleKeyGenerator
     *
     * @param id
     * @return
     */
    @Cacheable(cacheNames = "emp", key = "#id",condition = "#id>0")
    public Employee getEmp(Integer id) {
        System.out.println("查询" + id + "号员工");
        Employee emp = employeeMapper.getEmpById(id);
        return emp;
    }


    /**
     * @CachePut：既调用方法，又更新缓存数据
     * 运行时机：
     * 1、先调用目标方法
     * 2、将目标方法的结果缓存起来。
     *  key="#employee.id"或者key="#result.id"，指定key是为更新的缓存和之前查询是同一缓存
     *
     * @param employee
     * @return
     *
     */
    @CachePut(value = {"emp"}, key="#employee.id")
    public Employee updateEmp(Employee employee){
        System.out.println("updateEmp={}" + employee);
        employeeMapper.updateEmp(employee);
        return employee;
    }

    /**
     *@CacheEvict：清除缓存
     *  key：指定要清除的数据
     *  allEntries = true：删掉emp中所有的key-value，缓存emp中所有数据被清空了。
     *  beforeInvocation = false：缓存的清除是否在方法之前执行
     *      默认false是在方法执行之后执行的；如果方法出现异常，缓存不会被清空。
     *      true是在方法执行之前执行；如果方法出现异常，缓存也会被清空。
     * @param id
     */
    @CacheEvict(value = "emp", key = "#id", allEntries = true, beforeInvocation = false)
    public void deleteEmp(Integer id){
        System.out.println("deleteEmp:" + id);
        employeeMapper.deleteEmpById(id);
    }

    /**
     * @Caching定义复杂的缓存规则
     * @param lastName
     * @return
     */
    @Caching(
            //在方法之后执行
            cacheable = {
                    @Cacheable(value = "emp", key="#lastName")
            },
            //在方法之后执行，分别利用返回结果的id和email存入缓存。
            put = {
                    @CachePut(value="emp", key="#result.id"),
                    @CachePut(value="emp", key="#result.email"),
            }
    )
    public Employee getEmpByLastName(String lastName){
        return employeeMapper.getEmpByLastName(lastName);
    }

}

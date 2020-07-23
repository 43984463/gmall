package com.sherlock.gmall.product.web;

import com.sherlock.gmall.product.entity.CategoryEntity;
import com.sherlock.gmall.product.service.CategoryService;
import com.sherlock.gmall.product.vo.Catelog2Vo;
import io.swagger.annotations.ApiOperation;
import org.redisson.api.RLock;
import org.redisson.api.RReadWriteLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @Author xueshuai
 * @Description: TODO
 * @Date 2020/6/19
 **/
@Controller
public class IndexController {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private RedissonClient redisson;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;


    @ApiOperation("转到首页")
    @GetMapping({"/", "/index.html"})
    public String indexPage(Model model) {
        List<CategoryEntity> list = categoryService.getLevel1Categorys();
        model.addAttribute("categorys", list);
        return "index";
    }

    @GetMapping("/index/catalog.json")
    @ResponseBody
    public Map<String, List<Catelog2Vo>> getCatelogJson() {
        Map<String, List<Catelog2Vo>> catelogJson = categoryService.getCatelogJson();
        return catelogJson;
    }

    @ResponseBody
    @GetMapping("/hello")
    public String hello() {
        // 1、获取一把分布式锁，只要锁的名称一样，那就是同一把锁
        RLock lock = redisson.getLock("lock");

        /**
         *
         * 2、加锁
         * 可重入锁
         *  redisson分布式锁特点
         * mylock.lock(); //阻塞式等待，默认加的锁都是30S过期。
         * 锁会自动续期，如果业务时间超长，运行期间自动给锁续期到30S，不用担心业务时间长锁被自动删掉
         * 当业务运行完成，就不会再自动续期，即使不手动解锁，锁也会在30S之后自动删除
         *
         * mylock.lock(20, TimeUnit.SECONDS);
         * 在获取锁之后会在20S之后自动解锁，所以要慎用，尽量不用
         * 假如业务时间超过20S，业务执行完之后再去解锁，解锁的就不是当前线程的分布式锁，会报错
         */

        lock.lock(); // 加锁 阻塞式等待
        // mylock.lock(20, TimeUnit.SECONDS);
        // mylock.tryLock();
        lock.lock();
        try {
            System.out.println(Thread.currentThread().getId() + "---> 加锁成功");
            Thread.sleep(30000);
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            // 3、手动解锁 即使解锁代码没有运行，redisson也不会出现死锁问题
            lock.unlock(); // 解锁
            System.out.println(Thread.currentThread().getId() + " ---> 释放锁");
        }

        return "hello";
    }

    /**
     * 读写锁
     *
     * 可以保证一定读到新数据，修改期间，写锁是一个排它锁（互斥锁）。 读锁是一个共享锁
     * 写锁没有释放的时候读就必须等待
     * 读 + 读  相当于无锁，并发读，只会在redis中记录好所有当前的读锁。他们会同时加锁成功
     * 写 + 读  需要等待写锁释放才能读
     * 读 + 写  需要等待读锁释放才能写（获取写锁，但是读时可以不用等待获取读锁：即 读 + 读）
     * 写 + 写  互斥锁，需要等待第一个释放之后才能获取锁
     *
     */

    @GetMapping("/write")
    @ResponseBody
    public String writeValue() {
        String s = "";

        RReadWriteLock readWriteLock = redisson.getReadWriteLock("rw-lock");
        RLock rLock = readWriteLock.writeLock();
        rLock.lock();
        try {
            System.out.println(Thread.currentThread().getId() + "---> 加锁成功");
            s = UUID.randomUUID().toString();
            Thread.sleep(30000);
            stringRedisTemplate.opsForValue().set("writeValue", s);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return s;
    }

    @GetMapping("/read")
    @ResponseBody
    public String readValue() {
        String s = "";

        RReadWriteLock readWriteLock = redisson.getReadWriteLock("rw-lock");
        RLock rLock = readWriteLock.readLock();
        rLock.lock();
        try {
            System.out.println(Thread.currentThread().getId() + "---> 加锁成功");
            Thread.sleep(30000);
            s = stringRedisTemplate.opsForValue().get("writeValue");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return s;
    }
}

package com.sherlock.gmall.product.web;

import com.sherlock.gmall.product.entity.CategoryEntity;
import com.sherlock.gmall.product.service.CategoryService;
import com.sherlock.gmall.product.vo.Catelog2Vo;
import io.swagger.annotations.ApiOperation;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

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
        RLock mylock = redisson.getLock("mylock");

        mylock.lock(); // 加锁 阻塞式等待
        try {
            System.out.println(Thread.currentThread().getId() + "---> 加锁成功");
            Thread.sleep(10000);
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            mylock.unlock(); // 解锁
            System.out.println(Thread.currentThread().getId() + " ---> 释放锁");
        }

        return "hello";
    }

}

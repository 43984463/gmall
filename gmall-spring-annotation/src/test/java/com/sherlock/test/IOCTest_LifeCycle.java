package com.sherlock.test;

import com.sherlock.gmall.config.MainConfigOfLifeCycle;
import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * @Author xueshuai
 * @Description: TODO
 * @Date 2020/10/23
 **/
public class IOCTest_LifeCycle {

    @Test
    public void test01(){
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(MainConfigOfLifeCycle.class);
        System.out.println("容器创建完成");
        System.out.println("容器准备关闭");
        applicationContext.close();
    }

}

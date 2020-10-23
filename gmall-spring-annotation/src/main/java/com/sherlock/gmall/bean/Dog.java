package com.sherlock.gmall.bean;

import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

/**
 * @Author xueshuai
 * @Description: TODO
 * @Date 2020/10/23
 **/
@Component
public class Dog {
    public Dog() {
        System.out.println("dog constructor...");
    }

    @PreDestroy
    public void destroy(){
        System.out.println("dog destroy method");
    }

    @PostConstruct
    public void init(){
        System.out.println("dog init method");
    }
}

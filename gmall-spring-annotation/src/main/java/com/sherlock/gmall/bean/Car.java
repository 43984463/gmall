package com.sherlock.gmall.bean;

import org.springframework.stereotype.Component;

/**
 * @Author xueshuai
 * @Description: TODO
 * @Date 2020/10/23
 **/
@Component
public class Car {
    public Car() {
        System.out.println("car constructor...");
    }

    public void init(){
        System.out.println("car init method");
    }

    public void destory(){
        System.out.println("car destory method");
    }
}

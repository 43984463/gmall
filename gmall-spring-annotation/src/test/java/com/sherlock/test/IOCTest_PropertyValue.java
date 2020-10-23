package com.sherlock.test;

import com.sherlock.gmall.bean.Person;
import com.sherlock.gmall.config.MainConfigOfPropertyValues;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * @Author xueshuai
 * @Description: TODO
 * @Date 2020/10/23
 **/
public class IOCTest_PropertyValue {

    private void printBeans(ApplicationContext applicationContext){
        System.out.println("===================================打印当前IOC容器中所有的bean===================================");
        System.out.println("===================================打印当前IOC容器中所有的bean的数量"+applicationContext.getBeanDefinitionCount()+"===================================");
        String[] beanDefinitionNames = applicationContext.getBeanDefinitionNames();
        for (String name : beanDefinitionNames) {
            System.out.println(name);
        }
    }

    @Test
    public void test01(){
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(MainConfigOfPropertyValues.class);
        printBeans(applicationContext);
        Person person = (Person)applicationContext.getBean("person");
        System.out.println(person);
        System.out.println("容器创建完成");
        System.out.println("容器准备关闭");
        applicationContext.close();
    }

}

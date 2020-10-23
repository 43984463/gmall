package com.sherlock.test;

import com.sherlock.gmall.bean.Blue;
import com.sherlock.gmall.bean.Color;
import com.sherlock.gmall.bean.ColorFactoryBean;
import com.sherlock.gmall.config.MainConfig;
import com.sherlock.gmall.config.MainConfig2;
import com.sherlock.gmall.bean.Person;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;

import java.util.Map;

/**
 * @auther Sherlock
 * @date 2020/10/22 23:35
 * @Description:
 */
public class IOCTest {

    private void  printBeans(ApplicationContext applicationContext){
        System.out.println("===================================打印当前IOC容器中所有的bean===================================");
        System.out.println("===================================打印当前IOC容器中所有的bean的数量"+applicationContext.getBeanDefinitionCount()+"===================================");
        String[] beanDefinitionNames = applicationContext.getBeanDefinitionNames();
        for (String name : beanDefinitionNames) {
            System.out.println(name);
        }
    }

    @Test
    public void testImportBean(){
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(MainConfig2.class);
        printBeans(applicationContext);
        Object colorBean = applicationContext.getBean("colorFactoryBean");
        Object colorFactoryBean = applicationContext.getBean("&colorFactoryBean");
        System.out.println("colorBean的类型" + colorBean.getClass());
        System.out.println("colorFactoryBean的类型" + colorFactoryBean.getClass());
    }

    @Test
    public void test03(){
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(MainConfig2.class);
        Map<String, Person> persons = applicationContext.getBeansOfType(Person.class);
        ConfigurableEnvironment environment = applicationContext.getEnvironment();
        // 动态获取环境变量的值；Windows 10
        String OSName = environment.getProperty("os.name");
        System.out.println("OSName ---->" + OSName);
        persons.forEach((key, value) -> {
            System.out.println(key);
            System.out.println(value);
        });
    }

    @Test
    public void test02(){
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(MainConfig2.class);
        System.out.println("IOC容器创建完成");
        printBeans(applicationContext);
        Person person1 = (Person) applicationContext.getBean("person");
        Person person2 = (Person) applicationContext.getBean("person");
        System.out.println(person1 == person2);
    }

    @Test
    public void test01(){
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(MainConfig.class);
        printBeans(applicationContext);
    }

}

package com.sherlock.gmall;

import com.sherlock.gmall.test.Person;
import com.sherlock.gmall.config.MainConfig;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * @auther Sherlock
 * @date 2020/10/22 22:50
 * @Description:
 */
public class MainTest {
    public static void main(String[] args) {
        /**
         * 读取xml配置方式的代码
         */
        /*ApplicationContext applicationContext = new ClassPathXmlApplicationContext("beans.xml");
        Person person = (Person) applicationContext.getBean("person");
        System.out.println(person);*/

        ApplicationContext applicationContext = new AnnotationConfigApplicationContext(MainConfig.class);
        String[] beanDefinitionNames = applicationContext.getBeanDefinitionNames();
        System.out.println(beanDefinitionNames);

        String[] beanNamesForType = applicationContext.getBeanNamesForType(Person.class);
        for (String name : beanNamesForType) {
            System.out.println(name);
        }
    }
}

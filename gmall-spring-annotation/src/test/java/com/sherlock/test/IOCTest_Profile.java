package com.sherlock.test;

import com.sherlock.gmall.config.MainConfigOfProfile;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class IOCTest_Profile {

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
		AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(MainConfigOfProfile.class);
		printBeans(applicationContext);
		applicationContext.close();
	}

}

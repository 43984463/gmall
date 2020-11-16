package com.sherlock.test;


import com.sherlock.gmall.aop.MathCalculator;
import com.sherlock.gmall.config.MainConfigOfAOP;
import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.math.BigDecimal;

public class IOCTest_AOP {

	@Test
	public void test02(){
		BigDecimal decimal = new BigDecimal(-0.090001);
		System.out.println("ROUND_UP:" + new BigDecimal(1.00000000).setScale(2, BigDecimal.ROUND_UP));
		System.out.println("ROUND_UP:" + new BigDecimal(1.00196789).setScale(2, BigDecimal.ROUND_UP));
		System.out.println("ROUND_UP:" + new BigDecimal(1.10196789).setScale(2, BigDecimal.ROUND_UP));
		System.out.println("ROUND_UP:" + new BigDecimal(1.90196789).setScale(2, BigDecimal.ROUND_UP));
		System.out.println("ROUND_UP:" + new BigDecimal(-1.00000000).setScale(2, BigDecimal.ROUND_UP));
		System.out.println("ROUND_UP:" + new BigDecimal(-1.00196789).setScale(2, BigDecimal.ROUND_UP));
		System.out.println("ROUND_UP:" + new BigDecimal(-1.10196789).setScale(2, BigDecimal.ROUND_UP));
		System.out.println("ROUND_UP:" + new BigDecimal(-1.90196789).setScale(2, BigDecimal.ROUND_UP));
		System.out.println("======================================================================================");
		System.out.println("ROUND_FLOOR:" + new BigDecimal(1.00000000).setScale(2, BigDecimal.ROUND_FLOOR));
		System.out.println("ROUND_FLOOR:" + new BigDecimal(1.00196789).setScale(2, BigDecimal.ROUND_FLOOR));
		System.out.println("ROUND_FLOOR:" + new BigDecimal(1.10196789).setScale(2, BigDecimal.ROUND_FLOOR));
		System.out.println("ROUND_FLOOR:" + new BigDecimal(1.90196789).setScale(2, BigDecimal.ROUND_FLOOR));
		System.out.println("ROUND_FLOOR:" + new BigDecimal(-1.00000000).setScale(2, BigDecimal.ROUND_FLOOR));
		System.out.println("ROUND_FLOOR:" + new BigDecimal(-1.00196789).setScale(2, BigDecimal.ROUND_FLOOR));
		System.out.println("ROUND_FLOOR:" + new BigDecimal(-1.10196789).setScale(2, BigDecimal.ROUND_FLOOR));
		System.out.println("ROUND_FLOOR:" + new BigDecimal(-1.90196789).setScale(2, BigDecimal.ROUND_FLOOR));
		System.out.println("======================================================================================");
		System.out.println("ROUND_HALF_UP:" + new BigDecimal(1.00000000).setScale(2, BigDecimal.ROUND_HALF_UP));
		System.out.println("ROUND_HALF_UP:" + new BigDecimal(1.00196789).setScale(2, BigDecimal.ROUND_HALF_UP));
		System.out.println("ROUND_HALF_UP:" + new BigDecimal(1.10196789).setScale(2, BigDecimal.ROUND_HALF_UP));
		System.out.println("ROUND_HALF_UP:" + new BigDecimal(1.90196789).setScale(2, BigDecimal.ROUND_HALF_UP));
		System.out.println("ROUND_HALF_UP:" + new BigDecimal(-1.00000000).setScale(2, BigDecimal.ROUND_HALF_UP));
		System.out.println("ROUND_HALF_UP:" + new BigDecimal(-1.00196789).setScale(2, BigDecimal.ROUND_HALF_UP));
		System.out.println("ROUND_HALF_UP:" + new BigDecimal(-1.10596789).setScale(2, BigDecimal.ROUND_HALF_UP));
		System.out.println("ROUND_HALF_UP:" + new BigDecimal(-1.90496789).setScale(2, BigDecimal.ROUND_HALF_UP));
		System.out.println("======================================================================================");
		System.out.println("ROUND_HALF_EVEN:" + new BigDecimal(-1.10596789).setScale(2, BigDecimal.ROUND_HALF_EVEN));
	}

	@Test
	public void test01(){
		AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(MainConfigOfAOP.class);

		//1、不要自己创建对象
//		MathCalculator mathCalculator = new MathCalculator();
//		mathCalculator.div(1, 1);
		MathCalculator mathCalculator = applicationContext.getBean(MathCalculator.class);

		mathCalculator.div(1, 1);

		applicationContext.close();
	}

}

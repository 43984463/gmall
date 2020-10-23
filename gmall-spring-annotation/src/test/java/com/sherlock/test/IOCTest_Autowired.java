package com.sherlock.test;
import com.sherlock.gmall.bean.Boss;
import com.sherlock.gmall.bean.Car;
import com.sherlock.gmall.config.MainConfigOfAutowired;
import com.sherlock.gmall.dao.BookDao;
import com.sherlock.gmall.service.BookService;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class IOCTest_Autowired {
	
	@Test
	public void test01(){
		AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(MainConfigOfAutowired.class);
		
		BookService bookService = applicationContext.getBean(BookService.class);
		System.out.println(bookService);

		System.out.println("=====================================");
//		bookService.print();
//		System.out.println(applicationContext);

//		BookDao bean = applicationContext.getBean(BookDao.class);
//		System.out.println(bean);
//
//		Boss boss = applicationContext.getBean(Boss.class);
//		System.out.println(boss);
//		Car car = applicationContext.getBean(Car.class);
//		System.out.println(car);

//		Color color = applicationContext.getBean(Color.class);
//		System.out.println(color);
//		System.out.println(applicationContext);
//		applicationContext.close();
	}

}

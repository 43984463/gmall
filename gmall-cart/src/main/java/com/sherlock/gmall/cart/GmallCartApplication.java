package com.sherlock.gmall.cart;

import com.sherlock.common.constants.GmallConstant;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @auther Sherlock
 * @date 2020/8/28 20:07
 * @Description:
 */
@EnableFeignClients(basePackages = GmallConstant.GMALL_CART_BASEPATH + GmallConstant.FEIGN)
@EnableDiscoveryClient
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
public class GmallCartApplication {
    public static void main(String[] args) {
        SpringApplication.run(GmallCartApplication.class, args);
    }
}

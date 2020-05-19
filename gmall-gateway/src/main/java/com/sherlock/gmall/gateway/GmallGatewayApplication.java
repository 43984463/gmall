package com.sherlock.gmall.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @auther Sherlock
 * @date 2020/5/19 22:58
 * @Description:
 */
@EnableDiscoveryClient
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
public class GmallGatewayApplication {
    public static void main(String[] args) {
        SpringApplication.run(GmallGatewayApplication.class, args);
    }
}

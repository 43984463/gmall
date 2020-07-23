package com.sherlock.gmall.search;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @auther Sherlock
 * @date 2020/6/15 23:33
 * @Description:
 */
@EnableDiscoveryClient
@SpringBootApplication
public class GmallSearchApplication {
    public static void main(String[] args) {
        SpringApplication.run(GmallSearchApplication.class, args);
    }
}


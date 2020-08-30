package com.sherlock.gmall.search;

import com.sherlock.common.constants.GmallConstant;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @auther Sherlock
 * @date 2020/6/15 23:33
 * @Description:
 */
@EnableFeignClients(basePackages = GmallConstant.GMALL_SEARCH_BASEPATH + GmallConstant.FEIGN)
@EnableDiscoveryClient
@SpringBootApplication
public class GmallSearchApplication {
    public static void main(String[] args) {
        SpringApplication.run(GmallSearchApplication.class, args);
    }
}


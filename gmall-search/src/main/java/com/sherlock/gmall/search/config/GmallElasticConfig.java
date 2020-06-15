package com.sherlock.gmall.search.config;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @auther Sherlock
 * @date 2020/6/15 23:29
 * @Description:
 */
@Configuration
public class GmallElasticConfig {

    @Bean
    public RestHighLevelClient esRestClient(){
        RestHighLevelClient client = new RestHighLevelClient(
                RestClient.builder(new HttpHost("120.76.60.77", 9100, "http"))
        );
        return client;
    }
}

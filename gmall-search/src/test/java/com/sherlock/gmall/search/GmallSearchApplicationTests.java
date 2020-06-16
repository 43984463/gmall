package com.sherlock.gmall.search;

import com.alibaba.fastjson.JSON;
import com.sherlock.gmall.search.config.GmallElasticConfig;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.io.IOException;

/**
 * @auther Sherlock
 * @date 2020/6/15 23:47
 * @Description:
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class GmallSearchApplicationTests {

    @Resource
    private RestHighLevelClient client;

    @Test
    public void indexData() throws IOException {
        IndexRequest indexRequest = new IndexRequest("users");
        User user = new User();
        indexRequest.id("1");
        user.setAge(18).setGender("男").setName("小王");
        String s = JSON.toJSONString(user);
        indexRequest.source(s, XContentType.JSON);
        IndexResponse index = client.index(indexRequest, GmallElasticConfig.COMMON_OPTIONS);
        System.out.println(index);
    }

    @Data
    @Accessors(chain=true)
    class User {
        private int age;
        private String name;
        private String gender;
    }

    @Test
    public void contextLoads() {
        System.out.println(client);
    }
}


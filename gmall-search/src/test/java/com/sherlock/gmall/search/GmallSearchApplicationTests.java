package com.sherlock.gmall.search;

import com.alibaba.fastjson.JSON;
import com.sherlock.gmall.search.config.GmallElasticConfig;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.Avg;
import org.elasticsearch.search.aggregations.metrics.AvgAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
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

    /**
     * 测试
     * @throws IOException
     */

    /**
     * 测试给es索引数据
     */
    @Test
    public void searchData() throws IOException {

        // 创建检索请求
        SearchRequest searchRequest = new SearchRequest();
        // 指定索引
        searchRequest.indices("bank");
        // 构造查询条件
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

//		searchSourceBuilder.query();
//		searchSourceBuilder.from();
//		searchSourceBuilder.size();
//		searchSourceBuilder.aggregation();

        searchSourceBuilder.query(QueryBuilders.matchQuery("address", "mill"));

        // 按照年龄进行聚合
        TermsAggregationBuilder ageAgg = AggregationBuilders.terms("ageAgg").field("age").size(10);
        searchSourceBuilder.aggregation(ageAgg);

        // 按照平均薪资进行聚合
        AvgAggregationBuilder avgAgg = AggregationBuilders.avg("avgAgg").field("balance");
        searchSourceBuilder.aggregation(avgAgg);

        System.out.println("检索条件：" +searchSourceBuilder.toString());

        searchRequest.source(searchSourceBuilder);

        // 执行检索
        SearchResponse searchResponse = client.search(searchRequest, GmallElasticConfig.COMMON_OPTIONS);

        // 分析结果
        System.out.println(searchResponse.toString());

//		Map map = JSON.parseObject(searchRequest.toString(), Map.class);

        SearchHits hits = searchResponse.getHits();

        SearchHit[] searchHits = hits.getHits();

        for (SearchHit searchHit : searchHits) {

            String asString = searchHit.getSourceAsString();
            Accout accout = JSON.parseObject(asString, Accout.class);
            System.out.println("当前检索到的数据信息：" + accout);
        }

        // 获取聚合的分析信息
        Aggregations aggregations = searchResponse.getAggregations();

        Terms terms = aggregations.get("ageAgg");

        for (Terms.Bucket bucket : terms.getBuckets()) {

            String keyAsString = bucket.getKeyAsString();
            System.out.println("年龄" + keyAsString);
        }

        Avg avg = aggregations.get("avgAgg");
        System.out.println("平均薪资" + avg.getValue());

//		for (Aggregation aggregation : aggregations.asList()) {
//			System.out.println("当前聚合的名字：" + aggregation.getName());
//
//		}

    }

    @Data
    @ToString
    class Accout {

        private int account_number;
        private int balance;
        private String firstname;
        private String lastname;
        private int age;
        private String gender;
        private String address;
        private String employer;
        private String email;
        private String city;
        private String state;

    }

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


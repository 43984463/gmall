package com.sherlock.gmall.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.sherlock.common.constants.GmallSearchConstant;
import com.sherlock.common.to.es.SkuEsModel;
import com.sherlock.gmall.search.config.GmallElasticConfig;
import com.sherlock.gmall.search.service.ProductSaveService;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @auther Sherlock
 * @date 2020/6/17 23:33
 * @Description:
 */
@Slf4j
@Service
public class ProductSaveServiceImpl implements ProductSaveService {

    @Resource
    private RestHighLevelClient highLevelClient;

    /**
     * 在ES中使用 GET product/_search进行数据查询
     * @param models
     * @return
     * @throws IOException
     */
    @Override
    public Boolean productStatusUp(List<SkuEsModel> models) throws IOException {

        // 保存到ES
        // 1、建立索引。 product 建立映射关系

        // 2、给es保存数据
        BulkRequest bulkRequest = new BulkRequest();
        models.forEach(model -> {
            // 指定要保存到哪个索引
            IndexRequest indexRequest = new IndexRequest(GmallSearchConstant.PRODUCT_INDEX);
            // 保存之后的数据Id
            indexRequest.id(model.getSkuId().toString());
            // 需要转换为json进行保存
            String json = JSON.toJSONString(model);
            indexRequest.source(json, XContentType.JSON);
            bulkRequest.add(indexRequest);
        });
        BulkResponse bulk = highLevelClient.bulk(bulkRequest, GmallElasticConfig.COMMON_OPTIONS);

        boolean hasFailures = bulk.hasFailures();

        List<String> collect = Arrays.stream(bulk.getItems()).map(item -> item.getId()).collect(Collectors.toList());
        log.info("ProductSaveServiceImpl.productStstusUp 上架成功商品：{} ,返回的数据：{}",collect,bulk.toString());

        return !hasFailures;
    }
}

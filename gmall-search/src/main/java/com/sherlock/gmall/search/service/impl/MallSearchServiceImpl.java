package com.sherlock.gmall.search.service.impl;

import com.sherlock.common.constants.GmallSearchConstant;
import com.sherlock.gmall.search.config.GmallElasticConfig;
import com.sherlock.gmall.search.service.MallSearchService;
import com.sherlock.gmall.search.vo.SearchParam;
import com.sherlock.gmall.search.vo.SearchResult;
import org.apache.commons.lang.StringUtils;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.NestedQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.IOException;


/**
 * @auther Sherlock
 * @date 2020/7/21 23:08
 * @Description:
 */
@Service
public class MallSearchServiceImpl implements MallSearchService {

    @Autowired
    private RestHighLevelClient client;

    /**
     * 去ES中检索
     * @param param
     * @return
     */
    @Override
    public SearchResult search(SearchParam param) {
        // 1、动态构建出查询需要的DSL语句
        SearchResult result = null;

        // 1、准备检索请求
        SearchRequest request = buildSearchRequest(param);
        try {
            // 2、执行检索请求
            SearchResponse response = client.search(request, GmallElasticConfig.COMMON_OPTIONS);

            // 3、分析响应数据封装成我们需要的格式
            result = buildSearchResult(response);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 准备检索请求
     * #模糊匹配，过滤(按照属性，分类，品牌，价格区间，库存)，排序，分页，高亮，聚合分析
     * 参考dsl.json
     * @return
     * @param param
     */
    private SearchRequest buildSearchRequest(SearchParam param) {
        SearchSourceBuilder builder = new SearchSourceBuilder(); //构建DSL语句

        /**
         * 查询:模糊匹配，过滤(按照属性，分类，品牌，价格区间，库存)
         */

        // 1、构建bool - query
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        // 1.1、must - 模糊匹配
        if (StringUtils.isNotEmpty(param.getKeyword())) {
            boolQueryBuilder.must(QueryBuilders.matchQuery("skuTitle", param.getKeyword()));
        }
        // 1.2 Start
        // 1.2.1、bool - filter - 按照三级分类ID查询
        if (param.getCatalog3Id() != null) {
            boolQueryBuilder.filter(QueryBuilders.termQuery("catalogId", param.getCatalog3Id()));
        }
        // 1.2.2、bool - filter - 按照品牌ID查询
        if (!CollectionUtils.isEmpty(param.getBrandId())) {
            boolQueryBuilder.filter(QueryBuilders.termsQuery("brandId", param.getBrandId()));
        }
        // 1.2.3、bool - filter - 按照所有指定属性进行查询
        // attrs=1_5寸:8寸&attrs=2_8G:16G
        if (!CollectionUtils.isEmpty(param.getAttrs())) {
            param.getAttrs().forEach(attr -> {
                BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
                String[] s = attr.split("_");
                // 检索的属性ID
                String attrId = s[0];
                // 检索的属性的所有值
                String[] attrValues = s[1].split(":");
                boolQuery.must(QueryBuilders.termQuery("attrs.attrId", attrId));
                boolQuery.must(QueryBuilders.termsQuery("attrs.attrValue", attrValues));
                NestedQueryBuilder nestedQuery = QueryBuilders.nestedQuery("attrs", boolQuery, ScoreMode.None);
                boolQueryBuilder.filter(nestedQuery);
            });
        }
        // 1.2.4、bool - filter - 按照是否有库存进行查询
        boolQueryBuilder.filter(QueryBuilders.termQuery("hasStock", param.getHasStock() == 1));
        // 1.2.5、bool - filter - 按照价格区间进行查询
        // skuPrice -> 1_500/_500/500_
        if (StringUtils.isNotEmpty(param.getSkuPrice())) {
            RangeQueryBuilder rangeQuery = QueryBuilders.rangeQuery("skuPrice");
            String[] s = param.getSkuPrice().split("_");
            if (s.length == 2) {
                rangeQuery = rangeQuery.gte(s[0]).lte(s[1]);
            } else if (s.length == 1){
                if(param.getSkuPrice().startsWith("_")){
                    rangeQuery = rangeQuery.lte(s[0]);
                }
                if(param.getSkuPrice().endsWith("_")){
                    rangeQuery = rangeQuery.gte(s[0]);
                }
            }
            boolQueryBuilder.filter(rangeQuery);
        }

        builder.query(boolQueryBuilder);

        /**
         * 排序，分页，高亮
         */

        /**
         * 聚合分析
         */

        SearchRequest searchRequest = new SearchRequest(new String[]{GmallSearchConstant.PRODUCT_INDEX}, builder);
        return searchRequest;
    }

    /**
     * 构建结果数据
     * @return
     * @param response
     */
    private SearchResult buildSearchResult(SearchResponse response) {
        return null;
    }
}

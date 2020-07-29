package com.sherlock.gmall.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.sherlock.common.constants.GmallSearchConstant;
import com.sherlock.common.to.es.SkuEsModel;
import com.sherlock.common.utils.R;
import com.sherlock.gmall.search.config.GmallElasticConfig;
import com.sherlock.gmall.search.feign.ProductFeignService;
import com.sherlock.gmall.search.service.MallSearchService;
import com.sherlock.gmall.search.vo.AttrResponseVo;
import com.sherlock.gmall.search.vo.SearchParam;
import com.sherlock.gmall.search.vo.SearchResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.NestedQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.nested.NestedAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.nested.ParsedNested;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedLongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


/**
 * @auther Sherlock
 * @date 2020/7/21 23:08
 * @Description:
 */
@Service
@Slf4j
public class MallSearchServiceImpl implements MallSearchService {

    @Autowired
    private RestHighLevelClient client;

    @Autowired
    private ProductFeignService productFeignService;

    /**
     * 去ES中检索
     *
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
            result = buildSearchResult(param, response);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 准备检索请求
     * #模糊匹配，过滤(按照属性，分类，品牌，价格区间，库存)，排序，分页，高亮，聚合分析
     * 参考dsl.json
     *
     * @param param
     * @return
     */
    private SearchRequest buildSearchRequest(SearchParam param) {
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder(); //构建DSL语句

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
        // attrs=1_5寸:8寸&attrs=2_8G:16G 属性前端传进来的模式
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
        if (param.getHasStock() != null) {
            boolQueryBuilder.filter(QueryBuilders.termQuery("hasStock", param.getHasStock() == 1));
        }
        // 1.2.5、bool - filter - 按照价格区间进行查询
        // skuPrice -> 1_500/_500/500_ 价格区间前端传进来的模式
        if (StringUtils.isNotEmpty(param.getSkuPrice())) {
            RangeQueryBuilder rangeQuery = QueryBuilders.rangeQuery("skuPrice");
            String[] s = param.getSkuPrice().split("_");
            if (s.length == 2) {
                rangeQuery = rangeQuery.gte(s[0]).lte(s[1]);
            } else if (s.length == 1) {
                if (param.getSkuPrice().startsWith("_")) {
                    rangeQuery = rangeQuery.lte(s[0]);
                }
                if (param.getSkuPrice().endsWith("_")) {
                    rangeQuery = rangeQuery.gte(s[0]);
                }
            }
            boolQueryBuilder.filter(rangeQuery);
        }

        sourceBuilder.query(boolQueryBuilder);

        /**
         * 排序，分页，高亮
         */
        // 2.1、排序
        if (StringUtils.isNotEmpty(param.getSort())) {
            // sort=hotScore_asc/desc 排序前端传进来的模式
            String sort = param.getSort();
            String[] split = sort.split("_");
            SortOrder order = split[1].equalsIgnoreCase("asc") ? SortOrder.ASC : SortOrder.DESC;
            sourceBuilder.sort(split[0], order);
        }
        // 2.2 分页 from = (pageNum-1)*PageSize
        sourceBuilder.from(((param.getPageNum() == null ? 1 : param.getPageNum()) - 1) * GmallSearchConstant.PRODUCT_PAGESIZE);
        sourceBuilder.size(GmallSearchConstant.PRODUCT_PAGESIZE);
        // 2.3 高亮
        if (StringUtils.isNotEmpty(param.getKeyword())) {
            HighlightBuilder highlightBuilder = new HighlightBuilder();
            highlightBuilder.field("skuTitle");
            highlightBuilder.preTags("<b style='color:red'>");
            highlightBuilder.postTags("</b>");
            sourceBuilder.highlighter(highlightBuilder);

        }
        /**
         * 聚合分析
         * Elasticsearch exception [type=illegal_argument_exception, reason=Fielddata is disabled on text fields by default. Set fielddata=true on [brandName] in order to load fielddata in memory by uninverting the inverted index. Note that this can however use significant memory. Alternatively use a keyword field instead
         * brand_agg.subAggregation(AggregationBuilders.terms("brand_name_agg").field("brandName").size(1));
         * 修改为brand_agg.subAggregation(AggregationBuilders.terms("brand_name_agg").field("brandName.keyword").size(1));
         * 其他类似
         *
         */
        // 3.1 品牌聚合
        // 聚合名称 聚合字段
        TermsAggregationBuilder brand_agg = AggregationBuilders.terms("brand_agg").field("brandId").size(50);
        // 3.1.1 品牌聚合的子聚合
        brand_agg.subAggregation(AggregationBuilders.terms("brand_name_agg").field("brandName.keyword").size(1));
        brand_agg.subAggregation(AggregationBuilders.terms("brand_img_agg").field("brandImg.keyword").size(1));

        sourceBuilder.aggregation(brand_agg);


        // 3.2 分类聚合
        TermsAggregationBuilder catalog_agg = AggregationBuilders.terms("catalog_agg").field("catalogId").size(20);
        // 3.2.1 分类聚合的子聚合
        catalog_agg.subAggregation(AggregationBuilders.terms("catalog_name_agg").field("catalogName.keyword").size(1));

        sourceBuilder.aggregation(catalog_agg);


        // 3.3 属性聚合
        /**
         * 本人在添加数据时没有添加属性信息
         * 暂时屏蔽这段代码
         */
        NestedAggregationBuilder attr_agg = AggregationBuilders.nested("attr_agg", "attrs");

        // 聚合出当前所有的attrId对应的名字
        TermsAggregationBuilder attr_id_agg = AggregationBuilders.terms("attr_id_agg").field("attrs.attrId");
        // 聚合分析出attr_id对应的名字的所有可能的属性值attrValue
        attr_id_agg.subAggregation(AggregationBuilders.terms("attr_name_agg").field("attrs.attrName").size(1));
        attr_id_agg.subAggregation(AggregationBuilders.terms("attr_value_agg").field("attrs.attrsValue").size(50));
        attr_agg.subAggregation(attr_id_agg);
        sourceBuilder.aggregation(attr_agg);

        System.out.println("构建的DSL语句" + sourceBuilder.toString());
        SearchRequest searchRequest = new SearchRequest(new String[]{GmallSearchConstant.PRODUCT_INDEX}, sourceBuilder);
        return searchRequest;
    }

    /**
     * 构建结果数据
     *
     * @param response
     * @return
     */
    private SearchResult buildSearchResult(SearchParam param, SearchResponse response) {
        SearchResult result = new SearchResult();
        // 1、返回所有查询到的商品
        SearchHits hits = response.getHits();
        SearchHit[] hitsHits = hits.getHits();
        List<SkuEsModel> skuEsModelList = new ArrayList<>();
        if (hitsHits != null && hitsHits.length > 0) {
            Arrays.stream(hitsHits).forEach(hit -> {
                String sourceAsString = hit.getSourceAsString();
                SkuEsModel skuEsModel = JSON.parseObject(sourceAsString, SkuEsModel.class);
                // 检索条件传入了才会高亮
                if (param.getKeyword() != null) {
                    // 获取高亮部分并替换原本的title
                    HighlightField skuTitle = hit.getHighlightFields().get("skuTitle");
                    String highlightTitle = skuTitle.getFragments()[0].string();
                    skuEsModel.setSkuTitle(highlightTitle);
                }
                skuEsModelList.add(skuEsModel);
            });
        }
        result.setProducts(skuEsModelList);


        // 2、当前所有商品设计到的所有属性信息
        /**
         * 本人在添加数据时没有添加属性信息
         * 暂时屏蔽这段代码
         */
        List<SearchResult.AttrVo> attrVoList = new ArrayList<>();
        ParsedNested attr_agg = response.getAggregations().get("attr_agg");
        // 确保有attr_agg分组
        if (attr_agg != null) {
            ParsedLongTerms attr_id_agg = attr_agg.getAggregations().get("attr_id_agg");
            if (attr_id_agg != null) {
                List<? extends Terms.Bucket> attrIdAggBuckets = attr_id_agg.getBuckets();
                attrIdAggBuckets.forEach(attr -> {
                    SearchResult.AttrVo attrVo = new SearchResult.AttrVo();
                    // 属性ID
                    attrVo.setAttrId(attr.getKeyAsNumber().longValue());
                    // 属性名称
                    ParsedStringTerms attr_name_agg = attr.getAggregations().get("attr_name_agg");
                    String attrName = attr_name_agg.getBuckets().get(0).getKeyAsString();
                    attrVo.setAttrName(attrName);
                    // 属性值
                    ParsedStringTerms attr_value_agg = attr.getAggregations().get("attr_value_agg");
                    List<String> attrValues = attr_value_agg.getBuckets().stream().map(attr_bucket -> ((Terms.Bucket) attr_bucket).getKeyAsString()).collect(Collectors.toList());
                    attrVo.setAttrValue(attrValues);
                    attrVoList.add(attrVo);
                });
            }
        }
        result.setAttrs(attrVoList);
        // 3、当前所有商品设计到的所有品牌信息
        List<SearchResult.BrandVo> brandVoList = new ArrayList<>();
        ParsedLongTerms brand_agg = response.getAggregations().get("brand_agg");
        // 确保有brand_agg分组
        if (brand_agg != null) {
            List<? extends Terms.Bucket> brandAggBuckets = brand_agg.getBuckets();
            brandAggBuckets.forEach(brand -> {
                SearchResult.BrandVo brandVo = new SearchResult.BrandVo();
                // 品牌ID
                // brandVo.setBrandId(Long.parseLong(brand.getKey().toString()));
                // 直接获取number类型的返回值
                brandVo.setBrandId(brand.getKeyAsNumber().longValue());
                // 品牌图片(地址)
                ParsedStringTerms brand_img_agg = brand.getAggregations().get("brand_img_agg");
                String brandImg = brand_img_agg.getBuckets().get(0).getKeyAsString();
                brandVo.setBrandImg(brandImg);
                // 品牌名称
                ParsedStringTerms brand_name_agg = brand.getAggregations().get("brand_name_agg");
                String brandName = brand_name_agg.getBuckets().get(0).getKeyAsString();
                brandVo.setBrandName(brandName);

                brandVoList.add(brandVo);
            });
        }
        result.setBrands(brandVoList);
        // 4、当前所有商品设计到的所分类信息
        List<SearchResult.CatalogVo> catalogVoList = new ArrayList<>();
        ParsedLongTerms catalog_agg = response.getAggregations().get("catalog_agg");
        // 确保有catalog_agg分组
        if (catalog_agg != null) {
            List<? extends Terms.Bucket> catalogAggBuckets = catalog_agg.getBuckets();
            catalogAggBuckets.forEach(bucket -> {
                SearchResult.CatalogVo catalogVo = new SearchResult.CatalogVo();
                // 分类ID
                catalogVo.setCatalogId(Long.parseLong(bucket.getKey().toString()));
                // 分类名称
                ParsedStringTerms aggregations = bucket.getAggregations().get("catalog_name_agg");
                String catalog_name = aggregations.getBuckets().get(0).getKeyAsString();
                catalogVo.setCatalogName(catalog_name);

                catalogVoList.add(catalogVo);
            });
        }
        result.setCatalogs(catalogVoList);
        // 5.1、分页信息 - 页码
        result.setPageNum(param.getPageNum());
        // 5.2、分页信息 - 总记录数
        long totalCount = hits.getTotalHits().value;
        result.setTotal(totalCount);
        // 5.3、分页信息 - 总页码
        int totalPages = (int) totalCount % GmallSearchConstant.PRODUCT_PAGESIZE == 0 ? (int) totalCount / GmallSearchConstant.PRODUCT_PAGESIZE : (int) totalCount / GmallSearchConstant.PRODUCT_PAGESIZE + 1;
        result.setTotalPages(totalPages);

        // 5.4、前一页和后一页按钮中间的页数
        List<Integer> pageNavs = new ArrayList<>();
        for (int i = 1; i <= totalPages; i++) {
            pageNavs.add(i);
        }

        // 6、面包屑导航
        if (param.getAttrs() != null && param.getAttrs().size() > 0) {

            List<SearchResult.NavVo> navVos = param.getAttrs().stream().map(attr -> {
                // 6.1、分析每个传过来的attr查询参数
                SearchResult.NavVo navVo = new SearchResult.NavVo();
                String[] s = attr.split("_");

                /**
                 * 远程调用参考 {@link com.sherlock.gmall.product.service.impl.SpuInfoServiceImpl#up(Long)}
                 */

                /*R<AttrResponseVo> r = productFeignService.info(Long.parseLong(s[0]));*/
                ResponseEntity<AttrResponseVo> r = productFeignService.info(Long.parseLong(s[0]));

                try {

                /*if (r.getCode() == 0) {
                    AttrResponseVo attrVo = r.getData("attr", new TypeReference<AttrResponseVo>(){});
                    navVo.setNavName(attrVo.getAttrName());
                } else {
                    navVo.setNavName(s[0]);
                }*/

                    if (r.getStatusCode() == HttpStatus.OK) {
                        AttrResponseVo attrVo = r.getBody();
                        navVo.setNavName(attrVo.getAttrName());
                    } else {
                        navVo.setNavName(s[0]);
                    }
                } catch (Exception e) {
                    log.error("属性名称查询异常");
                    e.printStackTrace();
                }
                // 6.2、取消了这个面包屑之后，我们要跳转到哪个地方，将请求地址url里面的当前置空
                // 拿到当前所有的查询条件，去掉当前。
                String encode = null;
                try {
                    encode = URLEncoder.encode(attr, "UTF-8");
                    // 将前台与java对空格的处理进行转换 前台的空格是 "20%"， java转换(encode之后)变成了"+";
                    encode = encode.replace("+", "20%");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                String replace = param.getQueryString().replace("&attr=" + encode, "");
                navVo.setLink("http://search.gmall.com/list.html?" + replace);

                navVo.setNavValue(s[1]);
                return navVo;
            }).collect(Collectors.toList());


            result.setNavs(navVos);
        }

        // 品牌和分类
        if (!CollectionUtils.isEmpty(param.getBrandId())) {
            List<SearchResult.NavVo> navs = result.getNavs();
            SearchResult.NavVo navVo = new SearchResult.NavVo();
            navVo.setNavName("品牌");
            // 远程查询品牌
            navVo.setNavName("品牌");
        }
        return result;
    }
}

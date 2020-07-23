package com.sherlock.gmall.search.vo;

import com.sherlock.common.to.es.SkuEsModel;
import lombok.Data;

import java.util.List;

/**
 * @auther Sherlock
 * @date 2020/7/21 23:31
 * @Description: 查询结果返回给页面的信息
 */
@Data
public class SearchResult {

    // 查询到的所有的商品信息
    private List<SkuEsModel> products;

    private List<BrandVo> brands;       //查询结果所有涉及到的品牌
    private List<AttrVo> attrs;         //查询结果所有涉及到的属性
    private List<CatalogVo> catalogs;   //查询结果所有涉及到的分类

    /**
     * 以下是分页信息
     */
    private Integer pageNum;    //当前页码
    private Long total;         //总记录数
    private Integer totalPages; //总页码

    @Data
    public static class BrandVo{
        private Long brandId;
        private String brandName;
        private String brandImg;
    }

    @Data
    public static class AttrVo{
        private Long attrId;
        private String attrName;
        private List<String> attrValue;
    }

    @Data
    public static class CatalogVo{
        private Long catalogId;
        private String catalogName;
    }
}

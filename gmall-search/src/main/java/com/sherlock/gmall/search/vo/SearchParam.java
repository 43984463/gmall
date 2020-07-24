package com.sherlock.gmall.search.vo;

import lombok.Data;

import java.util.List;

/**
 * @auther Sherlock
 * @date 2020/7/21 23:06
 * @Description: 封装首页所有的可能搜索带的参数
 */
@Data
public class SearchParam {

    private String keyword;  // 页面传递的全文匹配字段
    private Long catalog3Id; // 三级分类ID

    /**
     * sort=saleCount_asc_desc
     * sort=skuPrice_asc_desc
     * sort=hotScore_asc_desc
     *
     */
    private String sort; //排序条件

    /**
     *
     * 好多的过滤条件
     * hasStock(是否有货 0/1)
     * skuPrice=1_500(1到500)/_500(小于500)/500_(大于500)
     *
     */

    private Integer hasStock = 1;   // 是否有货 0无 1有 默认选择有货
    private String skuPrice;    // 价格区间查询
    private List<Long> BrandId; // 按照品牌进行查询，可以多选
    private List<String> attrs; // 按照属性进行筛选，可以多选

    private Integer pageNum = 1;    // 页码  //默认第一页
}

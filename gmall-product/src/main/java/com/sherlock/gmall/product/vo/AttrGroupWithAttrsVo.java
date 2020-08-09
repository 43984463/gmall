package com.sherlock.gmall.product.vo;

import com.sherlock.gmall.product.entity.AttrEntity;
import lombok.Data;

import java.util.List;

/**
 * @auther Sherlock
 * @date 2020/6/7 23:44
 * @Description:
 */
@Data
public class AttrGroupWithAttrsVo {

    private Long attrGroupId;
    /**
     * 组名
     */
    private String attrGroupName;
    /**
     * 排序
     */
    private Integer sort;
    /**
     * 描述
     */
    private String descript;
    /**
     * 组图标
     */
    private String icon;
    /**
     * 所属分类id
     */
    private Long catelogId;

    private List<AttrEntity> attrs;

}

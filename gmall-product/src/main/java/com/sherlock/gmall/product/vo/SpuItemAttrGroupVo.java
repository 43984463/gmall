package com.sherlock.gmall.product.vo;

import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 * @auther Sherlock
 * @date 2020/8/9 12:53
 * @Description:
 */
@Data
@ToString
public class SpuItemAttrGroupVo {

    private String groupName;

    private List<Attr> attrs;

}

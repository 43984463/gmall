package com.sherlock.gmall.product.vo;

import lombok.Data;

/**
 * @Author xueshuai
 * @Description: TODO
 * @Date 2020/6/3
 **/
@Data
public class AttrResVo extends AttrVo {

    private String catelogName;

    private String groupName;

    private Long [] catelogPath;
}

package com.sherlock.gmall.ware.vo;

import lombok.Data;

/**
 * @Author xueshuai
 * @Description: TODO
 * @Date 2020/6/11
 **/
@Data
public class PurchaseItemDoneVo {

    private Long ItemId;
    private Integer status;
    private String reason;
}

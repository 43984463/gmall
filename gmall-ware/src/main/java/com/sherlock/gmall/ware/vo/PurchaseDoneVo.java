package com.sherlock.gmall.ware.vo;

import lombok.Data;

import java.util.List;

/**
 * @Author xueshuai
 * @Description: TODO
 * @Date 2020/6/11
 **/
@Data
public class PurchaseDoneVo {

    private Long id;

    private List<PurchaseItemDoneVo> items;

}

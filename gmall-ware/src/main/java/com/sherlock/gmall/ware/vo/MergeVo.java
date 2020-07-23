package com.sherlock.gmall.ware.vo;

import lombok.Data;

import java.util.List;

/**
 * @auther Sherlock
 * @date 2020/6/10 22:48
 * @Description:
 */
@Data
public class MergeVo {

    private Long purchaseId;
    private List<Long> items;
}

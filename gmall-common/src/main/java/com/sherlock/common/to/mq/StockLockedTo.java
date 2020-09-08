package com.sherlock.common.to.mq;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @auther Sherlock
 * @date 2020/9/8 22:44
 * @Description:
 */
@Data
@Accessors(chain = true)
public class StockLockedTo {

    private Long id; //库存工作单ID   WareOrderTaskEntity
    private WareOrderStockLockedDetailTo detail; //详情  WareOrderTaskDetailEntity

}

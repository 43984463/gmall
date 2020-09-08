package com.sherlock.common.to.mq;

import lombok.Data;

/**
 * @auther Sherlock
 * @date 2020/9/8 22:55
 * @Description:
 */
@Data
public class WareOrderStockLockedDetailTo {

    private Long id;
    /**
     * sku_id
     */
    private Long skuId;
    /**
     * sku_name
     */
    private String skuName;
    /**
     * 购买个数
     */
    private Integer skuNum;
    /**
     * 工作单id
     */
    private Long taskId;

    /**
     * 仓库id
     */
    private Long wareId;

    /**
     * 锁定状态
     */
    private Integer lockStatus;

}

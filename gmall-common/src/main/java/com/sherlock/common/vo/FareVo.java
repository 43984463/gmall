package com.sherlock.common.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @auther Sherlock
 * @date 2020/9/4 0:51
 * @Description: 运费Vo
 *
 * @see com.sherlock.gmall.ware.controller.WareInfoController#getFare(java.lang.Long)
 */
@Data
public class FareVo {

    private MemberAddressVo address;
    private BigDecimal fare;

}

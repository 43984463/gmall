package com.sherlock.gmall.ware.vo;

import com.sherlock.common.vo.MemberAddressVo;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @auther Sherlock
 * @date 2020/9/4 0:51
 * @Description: 运费Vo
 */
@Data
public class FareVo {

    private MemberAddressVo address;
    private BigDecimal fare;

}

package com.sherlock.common.to;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @Author xueshuai
 * @Description: TODO
 * @Date 2020/6/8
 **/
@Data
public class SpuBoundTo {

    private Long spuId;
    private BigDecimal buyBounds;
    private BigDecimal growBounds;
}

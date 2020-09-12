package com.sherlock.gmall.seckill.to;

import com.sherlock.common.vo.SkuInfoVo;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @auther Sherlock
 * @date 2020/9/12 13:03
 * @Description:
 */
@Data
public class SecKillSkuRedisTo {

    private Long promotionId;
    /**
     * 活动场次id
     */
    private Long promotionSessionId;
    /**
     * 商品id
     */
    private Long skuId;
    /**
     * 商品秒杀的随机码
     */
    private String randomCode;
    /**
     * 秒杀价格
     */
    private BigDecimal seckillPrice;
    /**
     * 秒杀总量
     */
    private BigDecimal seckillCount;
    /**
     * 每人限购数量
     */
    private BigDecimal seckillLimit;
    /**
     * 排序
     */
    private Integer seckillSort;

    private SkuInfoVo skuInfo;

    /**
     * 当前商品秒杀的开始时间
     */
    private long StartTime;

    /**
     * 当前商品秒杀的结束时间
     */
    private long endTime;

}

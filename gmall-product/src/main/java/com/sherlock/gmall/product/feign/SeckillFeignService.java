package com.sherlock.gmall.product.feign;

import com.sherlock.common.constants.GmallConstant;
import com.sherlock.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @auther Sherlock
 * @date 2020/9/13 1:18
 * @Description:
 */
@FeignClient(GmallConstant.GMALL_SECKILL)
public interface SeckillFeignService {

    @GetMapping("/sku/seckill/{skuId}")
    R getSkuSeckillInfo(@PathVariable("skuId") Long skuId);

}

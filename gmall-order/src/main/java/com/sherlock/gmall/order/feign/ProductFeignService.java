package com.sherlock.gmall.order.feign;

import com.sherlock.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @auther Sherlock
 * @date 2020/9/5 0:29
 * @Description:
 */
@FeignClient("gmall-product")
public interface ProductFeignService {

    @GetMapping("/skuId/{id}")
    R getSpuInfoBySkuId(@PathVariable("id") Long skuId);

}

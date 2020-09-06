package com.sherlock.gmall.order.feign;

import com.sherlock.common.utils.R;
import com.sherlock.common.vo.SpuInfoVo;
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

    @GetMapping("/product/spuinfo/skuInfo/{skuId}")
    R<SpuInfoVo> getSpuInfoBySkuId(@PathVariable("skuId") Long skuId);

}

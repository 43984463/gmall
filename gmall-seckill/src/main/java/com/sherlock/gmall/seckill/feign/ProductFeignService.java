package com.sherlock.gmall.seckill.feign;

import com.sherlock.common.constants.GmallConstant;
import com.sherlock.common.utils.R;
import com.sherlock.common.vo.SkuInfoVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @auther Sherlock
 * @date 2020/9/12 13:14
 * @Description:
 */
@FeignClient(GmallConstant.GMALL_PRODUCT)
public interface ProductFeignService {

    @RequestMapping("/product/skuinfo/info/{skuId}")
    R<SkuInfoVo> getSkuInfo(@PathVariable("skuId") Long skuId);

}

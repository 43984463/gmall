package com.sherlock.gmall.ware.feign;

import com.sherlock.common.utils.R;
import com.sherlock.common.vo.SkuInfoVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @Author xueshuai
 * @Description: TODO
 * @Date 2020/6/11
 **/
@FeignClient("gmall-product")
public interface ProductFeignService {
    /**
     *      /product/skuinfo/info/{skuId}
     *
     *
     *   1)、让所有请求过网关；
     *          1、@FeignClient("gulimall-gateway")：给gulimall-gateway所在的机器发请求
     *          2、/api/product/skuinfo/info/{skuId}
     *   2）、直接让后台指定服务处理
     *          1、@FeignClient("gulimall-gateway")
     *          2、/product/skuinfo/info/{skuId}
     *
     * @return
     */
    @RequestMapping("/product/skuinfo/info/{skuId}")
    R<SkuInfoVo> getSkuInfo(@PathVariable("skuId") Long skuId);

}

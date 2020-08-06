package com.sherlock.gmall.product.feign;

import com.sherlock.common.to.SkuHasStockVo;
import com.sherlock.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * @auther Sherlock
 * @date 2020/6/17 21:20
 * @Description: 所有的方法头和方法签名需要和远程的保持一致
 */
@FeignClient("gmall-ware")
public interface WareFeignService {

    /*@PostMapping("/ware/waresku/hasstock")
    R<List<SkuHasStockVo>> getSkuHasStock(@RequestBody List<Long> skuIds);*/

    @PostMapping("/ware/waresku/hasstock")
    ResponseEntity<List<SkuHasStockVo>> getSkuHasStock(@RequestBody List<Long> skuIds);
}

package com.sherlock.gmall.order.feign;

import com.sherlock.common.to.SkuHasStockVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * @auther Sherlock
 * @date 2020/9/3 20:16
 * @Description:
 */
@FeignClient("gmall-ware")
public interface WmsFeignService {

    @PostMapping("/ware/waresku/hasstock")
    ResponseEntity<List<SkuHasStockVo>> getSkuHasStock(@RequestBody List<Long> skuIds);

}

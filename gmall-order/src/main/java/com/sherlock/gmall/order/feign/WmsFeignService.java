package com.sherlock.gmall.order.feign;

import com.sherlock.common.to.SkuHasStockVo;
import com.sherlock.common.utils.R;
import com.sherlock.common.vo.WareSkuLockVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

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

    @GetMapping("/ware/wareinfo/fare")
    R getFare(@RequestParam("addrId") Long addrId);

    @PostMapping("/ware/waresku/lock/order")
    R orderLockStock(@RequestBody WareSkuLockVo lockVo);
}

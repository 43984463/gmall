package com.sherlock.gmall.search.feign;

import com.sherlock.common.utils.R;
import com.sherlock.gmall.search.vo.AttrResponseVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @auther Sherlock
 * @date 2020/7/28 20:46
 * @Description:
 */
@FeignClient("gmall-product")
public interface ProductFeignService {

    /*@GetMapping("/product/attr/info/{attrId}")
    R<AttrResponseVo> info(@PathVariable("attrId") Long attrId);*/

    @GetMapping("/product/attr/info/{attrId}")
    ResponseEntity<AttrResponseVo> info(@PathVariable("attrId") Long attrId);
}

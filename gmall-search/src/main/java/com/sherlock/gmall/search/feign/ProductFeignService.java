package com.sherlock.gmall.search.feign;

import com.sherlock.common.utils.R;
import com.sherlock.gmall.search.vo.AttrResponseVo;
import com.sherlock.gmall.search.vo.BrandVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @auther Sherlock
 * @date 2020/7/28 20:46
 * @Description:
 */
@FeignClient("gmall-product")
public interface ProductFeignService {

    /*@GetMapping("/product/attr/info/{attrId}")
    R<AttrResponseVo> info(@PathVariable("attrId") Long attrId);*/

    /**
     * 获取属性信息
     * @param attrId
     * @return
     */
    @GetMapping("/product/attr/info/{attrId}")
    ResponseEntity<AttrResponseVo> info(@PathVariable("attrId") Long attrId);

    @GetMapping("/product/attr/infos")
    R<List<BrandVo>> brandsInfo(@RequestParam("brandIds") List<Long> brandIds);

   /**
     * 获取多个
     * @param brandIds
     * @return
     */
    /*@GetMapping("/product/attr/infos")
    ResponseEntity<List<BrandVo>> brandsInfo(@RequestParam("brandIds") List<Long> brandIds);*/
}

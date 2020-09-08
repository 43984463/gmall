package com.sherlock.gmall.product.feign;

import com.sherlock.common.constants.GmallConstant;
import com.sherlock.common.to.es.SkuEsModel;
import com.sherlock.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * @auther Sherlock
 * @date 2020/6/18 0:03
 * @Description:
 */
@FeignClient(GmallConstant.GMALL_SEARCH)
public interface SearchFeignService {

    @PostMapping("/search/save/product")
    R productStatusUp(@RequestBody List<SkuEsModel> models);

}

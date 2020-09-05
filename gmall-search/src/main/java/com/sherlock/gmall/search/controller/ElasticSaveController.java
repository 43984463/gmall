package com.sherlock.gmall.search.controller;

import com.sherlock.common.Annotation.GmallMapping;
import com.sherlock.common.exception.GmallBizCodeEnume;
import com.sherlock.common.to.es.SkuEsModel;
import com.sherlock.common.utils.R;
import com.sherlock.gmall.search.service.ProductSaveService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

/**
 * @auther Sherlock
 * @date 2020/6/17 23:27
 * @Description:
 */
@RestController
@GmallMapping("/search/save")
@Slf4j
public class ElasticSaveController {

    @Autowired
    private ProductSaveService productSaveService;

    @PostMapping("/product")
    public R productStatusUp(@RequestBody List<SkuEsModel> models) {
        Boolean b = false;
        try {
            b = productSaveService.productStatusUp(models);
        } catch (IOException e) {
            log.error("商品上架错误:{}", e);
            return R.error(GmallBizCodeEnume.PRODUCT_UP_EXCEPTION.getCode(), GmallBizCodeEnume.PRODUCT_UP_EXCEPTION.getMsg());
        }
        return b ? R.ok(): R.error(GmallBizCodeEnume.PRODUCT_UP_EXCEPTION.getCode(), GmallBizCodeEnume.PRODUCT_UP_EXCEPTION.getMsg());
    }

}

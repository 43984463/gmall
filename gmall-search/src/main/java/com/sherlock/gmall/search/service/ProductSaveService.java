package com.sherlock.gmall.search.service;

import com.sherlock.common.to.es.SkuEsModel;

import java.io.IOException;
import java.util.List;

/**
 * @auther Sherlock
 * @date 2020/6/17 23:31
 * @Description:
 */
public interface ProductSaveService {
    Boolean productStatusUp(List<SkuEsModel> models) throws IOException;
}

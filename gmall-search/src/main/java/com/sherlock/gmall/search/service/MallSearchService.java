package com.sherlock.gmall.search.service;

import com.sherlock.gmall.search.vo.SearchParam;
import com.sherlock.gmall.search.vo.SearchResult;

/**
 * @auther Sherlock
 * @date 2020/7/21 23:08
 * @Description:
 */
public interface MallSearchService {

    /**
     * 检索的所有参数
     * @param param
     * @return 返回检索的结果
     */
    SearchResult search(SearchParam param);
}

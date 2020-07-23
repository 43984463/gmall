package com.sherlock.gmall.search.controller;

import com.sherlock.gmall.search.service.MallSearchService;
import com.sherlock.gmall.search.vo.SearchParam;
import com.sherlock.gmall.search.vo.SearchResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @auther Sherlock
 * @date 2020/7/21 21:48
 * @Description:
 */
@Controller
public class SearchController {

    @Autowired
    private MallSearchService mallSearchService;

    /**
     * 自动将页面提交的所有请求查询封装成指定的对象
     * @param param
     * @return
     */
    @GetMapping({"/list.html","/"})
    public String listPage(SearchParam param, Model model){

        SearchResult result = mallSearchService.search(param);
        model.addAttribute("result", result);

        return "list";
    }

}

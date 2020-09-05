package com.sherlock.gmall.order.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @auther Sherlock
 * @date 2020/9/2 0:14
 * @Description:
 */
@Controller
public class HelloController {

    /**
     * /*@RequestMapping("/{page}.html")中的路径变量
     * 这样请求会导致swagger-ui访问出错
     *
     * @param page
     * @return
     */
    /*@RequestMapping("/{page}.html")
    public String listpage(@PathVariable("page") String page){
        return page;
    }*/

}

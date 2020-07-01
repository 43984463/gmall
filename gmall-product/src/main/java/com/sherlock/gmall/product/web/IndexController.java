package com.sherlock.gmall.product.web;

import com.sherlock.gmall.product.entity.CategoryEntity;
import com.sherlock.gmall.product.service.CategoryService;
import com.sherlock.gmall.product.vo.Catelog2Vo;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

/**
 * @Author xueshuai
 * @Description: TODO
 * @Date 2020/6/19
 **/
@Controller
public class IndexController {

    @Autowired
    private CategoryService categoryService;

    @ApiOperation("转到首页")
    @GetMapping({"/", "/index.html"})
    public String indexPage(Model model) {
        List<CategoryEntity> list = categoryService.getLevel1Categorys();
        model.addAttribute("categorys", list);
        return "index";
    }

    @GetMapping("/index/catalog.json")
    @ResponseBody
    public Map<String, List<Catelog2Vo>> getCatelogJson() {
        Map<String, List<Catelog2Vo>> catelogJson = categoryService.getCatelogJson();
        return catelogJson;
    }

}

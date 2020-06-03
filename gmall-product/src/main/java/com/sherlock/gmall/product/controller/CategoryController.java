package com.sherlock.gmall.product.controller;

import com.sherlock.common.Annotation.GmallMapping;
import com.sherlock.common.utils.R;
import com.sherlock.gmall.product.entity.CategoryEntity;
import com.sherlock.gmall.product.service.CategoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;


/**
 * 商品三级分类
 *
 * @author sherlockXue
 * @email xuesherlock@gmail.com
 * @date 2020-05-09 00:31:17
 */
@Api(tags = "CategoryController", description = "商品三层分类接口/商品模块")
@RestController
@GmallMapping(value = "product/category")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    /**
     * http://localhost:1301/product/category/list/tree
     * 列表
     */
    @RequestMapping("/list/tree")
    @ApiOperation(value = "显示所有列表")
    //@RequiresPermissions("product:category:list")
    public R list() {
        List<CategoryEntity> categoryEntities = categoryService.listWithTree();

        return R.ok().put("data", categoryEntities);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{catId}")
    @ApiOperation(value = "查询catId等于传入参数的信息")
    //@RequiresPermissions("product:category:info")
    public R info(@PathVariable("catId") Long catId) {
        CategoryEntity category = categoryService.getById(catId);
        return R.ok().put("data", category);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    @ApiOperation(value="保存商品")
    //@RequiresPermissions("product:category:save")
    public R save(@RequestBody CategoryEntity category) {
        categoryService.save(category);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    //@RequiresPermissions("product:category:update")
    public R update(@RequestBody CategoryEntity category) {
        categoryService.updateCascade(category);

        return R.ok();
    }


    @RequestMapping("/updateBatch")
    //@RequiresPermissions("product:category:update")
    public R updateBatch(@RequestBody CategoryEntity[] category) {
        categoryService.updateBatchById(Arrays.asList(category));
        return R.ok();
    }

    /**
     * localhost:88/api/product/category/delete
     * 删除
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("product:category:delete")
    public R delete(@RequestBody Long[] catIds) {
        int count = categoryService.deleteMenusByIds(Arrays.asList(catIds));
        return R.ok().put("count", count);
    }

}

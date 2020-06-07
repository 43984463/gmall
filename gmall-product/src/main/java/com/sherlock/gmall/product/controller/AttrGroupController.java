package com.sherlock.gmall.product.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.sherlock.common.Annotation.GmallMapping;
import com.sherlock.gmall.product.entity.AttrEntity;
import com.sherlock.gmall.product.service.AttrAttrgroupRelationService;
import com.sherlock.gmall.product.service.AttrService;
import com.sherlock.gmall.product.service.CategoryService;
import com.sherlock.gmall.product.vo.AttrGroupRelationVo;
import com.sherlock.gmall.product.vo.AttrGroupWithAttrsVo;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sherlock.gmall.product.entity.AttrGroupEntity;
import com.sherlock.gmall.product.service.AttrGroupService;
import com.sherlock.common.utils.PageUtils;
import com.sherlock.common.utils.R;


/**
 * 属性分组
 *
 * @author sherlockXue
 * @email xuesherlock@gmail.com
 * @date 2020-05-09 00:31:16
 */
@RestController
@GmallMapping("product/attrgroup")
public class AttrGroupController {
    @Autowired
    private AttrGroupService attrGroupService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private AttrService attrService;

    @Autowired
    private AttrAttrgroupRelationService relationService;

    @ApiOperation(value = "增加关联关系")
    @PostMapping("/attr/relation")
    //@RequiresPermissions("product:attrgroup:list")
    public R addRelation(@RequestBody List<AttrGroupRelationVo> vos) {
        /*PageUtils page = attrGroupService.queryPage(params);*/
        relationService.saveBatch(vos);
        return R.ok();
    }


    @RequestMapping("/{catelogId}/withattr")
    //@RequiresPermissions("product:attrgroup:list")
    public R getAttrGroupWithAttrs(@PathVariable("catelogId") Long catelogId) {
        /*PageUtils page = attrGroupService.queryPage(params);*/
        List<AttrGroupWithAttrsVo> vos= attrGroupService.getAttrGroupWithAttrsByCatelogId(catelogId);
        return R.ok().put("data", vos);
    }

    /**
     * 列表
     */
    @RequestMapping("/list/{catelogId}")
    //@RequiresPermissions("product:attrgroup:list")
    public R list(@RequestParam Map<String, Object> params, @PathVariable("catelogId") Long catelogId) {
        /*PageUtils page = attrGroupService.queryPage(params);*/
        PageUtils page = attrGroupService.queryPageByCatelogId(params,catelogId);
        return R.ok().put("page", page);
    }

    @GetMapping("/{attrgroupId}/attr/relation")
    public R attrRelation(@PathVariable("attrgroupId") Long attrgroupId) {
        List<AttrEntity> entities = attrService.getRelationAttr(attrgroupId);
        return R.ok().put("data", entities);
    }

    @GetMapping("/{attrgroupId}/noattr/relation")
    public R attrNoRelation(@PathVariable("attrgroupId") Long attrgroupId, @RequestParam Map<String, Object> params) {
        PageUtils page = attrService.getNoRelationAttr(attrgroupId, params);
        return R.ok().put("page", page);
    }

    /**
     *
     * 信息
     */
    @RequestMapping("/info/{attrGroupId}")
    //@RequiresPermissions("product:attrgroup:info")
    public R info(@PathVariable("attrGroupId") Long attrGroupId) {
        AttrGroupEntity attrGroup = attrGroupService.getById(attrGroupId);
        attrGroup.setCatelogPath(categoryService.getCategoryPath(attrGroup.getCatelogId()));
        return R.ok().put("attrGroup", attrGroup);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    //@RequiresPermissions("product:attrgroup:save")
    public R save(@RequestBody AttrGroupEntity attrGroup) {
        attrGroupService.save(attrGroup);
        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    //@RequiresPermissions("product:attrgroup:update")
    public R update(@RequestBody AttrGroupEntity attrGroup) {
        attrGroupService.updateAttrAndRelation(attrGroup);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("product:attrgroup:delete")
    public R delete(@RequestBody Long[] attrGroupIds) {
        attrGroupService.removeByIds(Arrays.asList(attrGroupIds));

        return R.ok();
    }


    @PostMapping("/attr/relation/delete")
    //@RequiresPermissions("product:attr:delete")
    public R deleteRelation(@RequestBody List<AttrGroupRelationVo> vos){
        attrService.deleteRelation(vos);
        return R.ok();
    }

}

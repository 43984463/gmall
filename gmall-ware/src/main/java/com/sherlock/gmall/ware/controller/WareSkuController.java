package com.sherlock.gmall.ware.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.sherlock.common.Annotation.GmallMapping;
import com.sherlock.common.exception.GmallBizCodeEnume;
import com.sherlock.common.to.SkuHasStockVo;
import com.sherlock.common.vo.WareSkuLockVo;
import com.sherlock.gmall.ware.exception.NoStockException;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sherlock.gmall.ware.entity.WareSkuEntity;
import com.sherlock.gmall.ware.service.WareSkuService;
import com.sherlock.common.utils.PageUtils;
import com.sherlock.common.utils.R;


/**
 * 商品库存
 *
 * @author sherlockXue
 * @email xuesherlock@gmail.com
 * @date 2020-05-10 01:18:07
 */
@RestController
@GmallMapping("ware/waresku")
public class WareSkuController {

    @Autowired
    private WareSkuService wareSkuService;

    /**
     * 使用R封装的类型返回
     */
    /*@PostMapping("/hasstock")
    public R<List<SkuHasStockVo>> getSkuHasStock(@RequestBody List<Long> skuIds) {
        List<SkuHasStockVo> vos = wareSkuService.getSkuHasStock(skuIds);
        R ok = R.ok();
        ok.setData(vos);
        return ok;
    }*/

    @ApiOperation("为某个订单锁定库存")
    @PostMapping("/lock/order")
    public R orderLockStock(@RequestBody WareSkuLockVo lockVo){
        try{
            Boolean locked = wareSkuService.orderLockStock(lockVo);
            return R.ok();
        } catch (NoStockException e){
            return R.error(GmallBizCodeEnume.NO_STOCK_EXCEPTION.getCode(), GmallBizCodeEnume.NO_STOCK_EXCEPTION.getMsg());
        }

    }

    /**
     * 使用ResponseEntity封装的类型返回
     */
    @PostMapping("/hasstock")
    public ResponseEntity<List<SkuHasStockVo>> getSkuHasStock(@RequestBody List<Long> skuIds) {
        List<SkuHasStockVo> vos = wareSkuService.getSkuHasStock(skuIds);
        return new ResponseEntity<>(vos, HttpStatus.OK);
    }

    /**
     * 列表
     */
    @RequestMapping("/list")
    //@RequiresPermissions("ware:waresku:list")
    public R list(@RequestParam Map<String, Object> params) {
        PageUtils page = wareSkuService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    //@RequiresPermissions("ware:waresku:info")
    public R info(@PathVariable("id") Long id) {
        WareSkuEntity wareSku = wareSkuService.getById(id);

        return R.ok().put("wareSku", wareSku);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    //@RequiresPermissions("ware:waresku:save")
    public R save(@RequestBody WareSkuEntity wareSku) {
        wareSkuService.save(wareSku);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    //@RequiresPermissions("ware:waresku:update")
    public R update(@RequestBody WareSkuEntity wareSku) {
        wareSkuService.updateById(wareSku);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("ware:waresku:delete")
    public R delete(@RequestBody Long[] ids) {
        wareSkuService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}

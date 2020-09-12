package com.sherlock.gmall.seckill.controller;

import com.sherlock.common.to.SecKillSkuRedisTo;
import com.sherlock.common.utils.R;
import com.sherlock.gmall.seckill.service.SeckillService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @auther Sherlock
 * @date 2020/9/12 22:08
 * @Description:
 */
@RestController
public class SeckillController {

    @Autowired
    private SeckillService seckillService;

    /**
     * 返回当前时间可以参与的秒杀商品的信息
     * @return
     */
    @GetMapping("/currentSeckillSkus")
    public R getCurrentSeckillSkus(){
        List<SecKillSkuRedisTo> vos = seckillService.getCurrentSeckillSkus();
        return R.ok().setData(vos);
    }

    @GetMapping("/uploadSeckillProduct")
    public R uploadSeckillProduct(){
        seckillService.uploadSeckillSkuLatest3Days();
        return R.ok();
    }

    @GetMapping("/sku/seckill/{skuId}")
    public R getSkuSeckillInfo(@PathVariable("skuId") Long skuId){
        SecKillSkuRedisTo to = seckillService.getSkuSeckillInfo(skuId);
        return R.ok().setData(to);
    }
}

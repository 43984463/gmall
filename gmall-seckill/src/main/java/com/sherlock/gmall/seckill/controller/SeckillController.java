package com.sherlock.gmall.seckill.controller;

import com.sherlock.common.to.SecKillSkuRedisTo;
import com.sherlock.common.utils.R;
import com.sherlock.gmall.seckill.service.SeckillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * @auther Sherlock
 * @date 2020/9/12 22:08
 * @Description:
 */
@Controller
public class SeckillController {

    @Autowired
    private SeckillService seckillService;

    /**
     * 返回当前时间可以参与的秒杀商品的信息
     * @return
     */
    @ResponseBody
    @GetMapping("/currentSeckillSkus")
    public R getCurrentSeckillSkus(){
        List<SecKillSkuRedisTo> vos = seckillService.getCurrentSeckillSkus();
        return R.ok().setData(vos);
    }

    @ResponseBody
    @GetMapping("/uploadSeckillProduct")
    public R uploadSeckillProduct(){
        seckillService.uploadSeckillSkuLatest3Days();
        return R.ok();
    }

    @ResponseBody
    @GetMapping("/sku/seckill/{skuId}")
    public R getSkuSeckillInfo(@PathVariable("skuId") Long skuId){
        SecKillSkuRedisTo to = seckillService.getSkuSeckillInfo(skuId);
        return R.ok().setData(to);
    }

    @GetMapping("/kill")
    public String secKill(@RequestParam(value = "killId") String killId,
                     @RequestParam(value = "key") String key,
                     @RequestParam(value = "num") Integer num,
                          Model model){
        String orderSn = seckillService.kill(killId, key, num);
        model.addAttribute("orderSn", orderSn);
        return "success";
    }
}

package com.sherlock.gmall.cart.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.sherlock.common.constants.GmallAuthConstant;
import com.sherlock.common.constants.GmallCartConstant;
import com.sherlock.common.utils.R;
import com.sherlock.common.vo.SkuInfoVo;
import com.sherlock.gmall.cart.feign.ProductFeignService;
import com.sherlock.gmall.cart.interceptor.CartInterceptor;
import com.sherlock.gmall.cart.service.CartService;
import com.sherlock.gmall.cart.vo.CartItem;
import com.sherlock.gmall.cart.vo.UserInfoTo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @auther Sherlock
 * @date 2020/8/28 21:39
 * @Description:
 */

@Slf4j
@Service("cartService")
public class CartServiceImpl implements CartService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private ProductFeignService productFeignService;

    @Autowired
    private ThreadPoolExecutor executor;

    @Override
    public CartItem addToCart(Long skuId, Integer num) throws ExecutionException, InterruptedException {
        BoundHashOperations<String, Object, Object> cartOps = getCartOps();

        CartItem cartItem = new CartItem();

        CompletableFuture<Void> getSkuInfo = CompletableFuture.runAsync(() -> {
            // 1、远程查询当前要添加的skuId对应的商品的信息
            R<SkuInfoVo> info = productFeignService.getSkuInfo(skuId);
            SkuInfoVo skuInfo = info.getData("skuInfo", new TypeReference<SkuInfoVo>() {
            });

            cartItem.setSkuId(skuId);
            cartItem.setCheck(true);
            cartItem.setCount(1);
            cartItem.setImage(skuInfo.getSkuDefaultImg());
            cartItem.setTitle(skuInfo.getSkuTitle());
            cartItem.setPrice(skuInfo.getPrice());
        }, executor);

        CompletableFuture<Void> getSkuSaleAttrValues = CompletableFuture.runAsync(() -> {
            // 3、远程查询sku的组合信息
            List<String> skuSaleAttrValues = productFeignService.getSkuSaleAttrValues(skuId);
            cartItem.setSkuAttr(skuSaleAttrValues);
        }, executor);

        // 等所有异步任务完成之后再进行保存，不然可能保存为null
        CompletableFuture.allOf(getSkuInfo, getSkuSaleAttrValues).get();

        String s = JSON.toJSONString(cartItem);
        cartOps.put(skuId.toString(), s);

        return cartItem;
    }

    /**
     * 获取到我们要操作的购物车
     *
     * @return
     */
    private BoundHashOperations<String, Object, Object> getCartOps() {
        UserInfoTo userInfoTo = CartInterceptor.threadLocal.get();

        String cartKey = "";
        if (userInfoTo.getUserId() != null) {
            cartKey = GmallCartConstant.GMALL_CART_PREFIX + userInfoTo.getUserId();
        } else {
            cartKey = GmallCartConstant.GMALL_CART_PREFIX + userInfoTo.getUserKey();
        }

        BoundHashOperations<String, Object, Object> operations = redisTemplate.boundHashOps(cartKey);
        return operations;
    }
}

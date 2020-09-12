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
import com.sherlock.gmall.cart.vo.Cart;
import com.sherlock.gmall.cart.vo.CartItem;
import com.sherlock.gmall.cart.vo.UserInfoTo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

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

        // 查看现在的购物车中是否有当前商品
        String result = (String) cartOps.get(skuId.toString());
        if (StringUtils.isNotEmpty(result)) {
            // 有当前商品
            CartItem cartItem = JSON.parseObject(result, CartItem.class);
            cartItem.setCount(cartItem.getCount() + num);

            String s = JSON.toJSONString(cartItem);
            cartOps.put(skuId.toString(), s);

            return cartItem;
        } else {
            // 没有当前商品
            CartItem cartItem = new CartItem();

            CompletableFuture<Void> getSkuInfo = CompletableFuture.runAsync(() -> {
                // 1、远程查询当前要添加的skuId对应的商品的信息
                R<SkuInfoVo> info = productFeignService.getSkuInfo(skuId);
                SkuInfoVo skuInfo = info.getData(new TypeReference<SkuInfoVo>() {});

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


    }

    @Override
    public CartItem getCartItem(Long skuId) {
        BoundHashOperations<String, Object, Object> cartOps = getCartOps();
        String result = (String) cartOps.get(skuId.toString());

        CartItem cartItem = JSON.parseObject(result, CartItem.class);
        return cartItem;
    }

    @Override
    public Cart getCart() throws ExecutionException, InterruptedException {
        Cart cart = new Cart();

        // 区分用户是否登录
        UserInfoTo userInfoTo = CartInterceptor.threadLocal.get();
        if (userInfoTo.getUserId() != null) {
            // 登录了
            // 如果临时购物车有数据需要合并并且清空临时购物车
            // 临时购物车
            String tempCartKey = GmallCartConstant.GMALL_CART_PREFIX + userInfoTo.getUserKey();
            List<CartItem> tempCartItems = getCartItems(tempCartKey);
            if (!CollectionUtils.isEmpty(tempCartItems)) {
                // 临时购物车有商品，需要合并
                // 直接把临时购物车的所有商品添加到(已登录的)购物车
                for (CartItem tempCartItem : tempCartItems) {
                    // TODO 感觉这种不好，如果购物车中的东西多，需要多次调用远程接口，待处理
                    addToCart(tempCartItem.getSkuId(), tempCartItem.getCount());
                }
                // 添加完成之后清空临时购物车
                clearCart(tempCartKey);
            }
            String cartKey = GmallCartConstant.GMALL_CART_PREFIX + userInfoTo.getUserId();
            List<CartItem> cartItems = getCartItems(cartKey);
            cart.setItems(cartItems);

        } else {
            // 没登录
            String cartKey = GmallCartConstant.GMALL_CART_PREFIX + userInfoTo.getUserKey();
            List<CartItem> cartItems = getCartItems(cartKey);
            cart.setItems(cartItems);
        }
        return cart;
    }

    @Override
    public void clearCart(String cartKey) {
        redisTemplate.delete(cartKey);
    }

    @Override
    public void checkCartItem(Long skuId, Integer check) {
        BoundHashOperations<String, Object, Object> cartOps = getCartOps();
        CartItem cartItem = getCartItem(skuId);
        cartItem.setCheck(check==1);
        String s = JSON.toJSONString(cartItem);
        cartOps.put(skuId.toString(), s);
    }

    @Override
    public void countCartItem(Long skuId, Integer num) {
        BoundHashOperations<String, Object, Object> cartOps = getCartOps();
        CartItem cartItem = getCartItem(skuId);
        cartItem.setCount(num);
        String s = JSON.toJSONString(cartItem);
        cartOps.put(skuId.toString(), s);
    }

    @Override
    public void deleteCartItem(Long skuId) {
        BoundHashOperations<String, Object, Object> cartOps = getCartOps();
        cartOps.delete(skuId.toString());
    }

    @Override
    public List<CartItem> getUserCartItems() {
        UserInfoTo userInfoTo = CartInterceptor.threadLocal.get();
        if (userInfoTo.getUserId() == null){
            return null;
        } else {
            List<CartItem> cartItems = getCartItems(GmallCartConstant.GMALL_CART_PREFIX + userInfoTo.getUserId());
            return cartItems.stream()
                    .filter(CartItem::isCheck)
                    //重新获取所有物品的最新价格
                    .peek(item -> item.setPrice(productFeignService.getPrice(item.getSkuId())))
                    .collect(Collectors.toList());
        }
    }

    /**
     * 获取购物车的所有购物项
     * @param cartKey
     * @return
     */
    private List<CartItem> getCartItems(String cartKey) {
        BoundHashOperations<String, Object, Object> hashOps = redisTemplate.boundHashOps(cartKey);
        List<Object> values = hashOps.values();
        if (!CollectionUtils.isEmpty(values)) {
            List<CartItem> collect = values.stream().map(value -> {
                String string = (String) value;
                CartItem cartItem = JSON.parseObject(string, CartItem.class);
                return cartItem;
            }).collect(Collectors.toList());
            return collect;
        }
        return null;
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

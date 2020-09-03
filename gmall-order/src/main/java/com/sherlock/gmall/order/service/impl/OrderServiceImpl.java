package com.sherlock.gmall.order.service.impl;

import com.sherlock.common.vo.MemberRespVo;
import com.sherlock.gmall.order.config.GmallFeignConfig;
import com.sherlock.gmall.order.feign.CartFeignService;
import com.sherlock.gmall.order.feign.MemberFeignService;
import com.sherlock.gmall.order.interceptor.LoginUserInterceptor;
import com.sherlock.gmall.order.vo.MemberAddressVo;
import com.sherlock.gmall.order.vo.OrderConfirmVo;
import com.sherlock.gmall.order.vo.OrderItemVo;
import feign.RequestInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sherlock.common.utils.PageUtils;
import com.sherlock.common.utils.Query;

import com.sherlock.gmall.order.dao.OrderDao;
import com.sherlock.gmall.order.entity.OrderEntity;
import com.sherlock.gmall.order.service.OrderService;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;


@Service("orderService")
public class OrderServiceImpl extends ServiceImpl<OrderDao, OrderEntity> implements OrderService {

    @Autowired
    private MemberFeignService memberFeignService;

    @Autowired
    private CartFeignService cartFeignService;

    @Autowired
    private ThreadPoolExecutor executor;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<OrderEntity> page = this.page(
                new Query<OrderEntity>().getPage(params),
                new QueryWrapper<OrderEntity>()
        );

        return new PageUtils(page);
    }

    /**
     *   异步线程下ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();获取到空的情况
     *    原因：
     *      本方法在同步调用时，
     *      @see GmallFeignConfig#requestInterceptor() 方法中的
     *      ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes() 代码获取的是同一个线程的数据，
     *      @see org.springframework.web.context.request.RequestContextHolder#getRequestAttributes() 是从 ThreadLocal 中获取数据，但是在异步情况下，运行的不是同一个线程，
     *      所以导致RequestContextHolder.getRequestAttributes()方法获取到null (就是开的另外的线程不能从之前线程获取到信息，所以报空指针)
     *    解决办法：
     *    本方法在Feign远程调用之前，把主线程RequestContextHolder的数据放到子线程
     *    RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
     *
     *    CompletableFuture<Void> getCurrentUserCartItems = CompletableFuture.runAsync(() -> {
     *             // 在Feign异步调用之前，把主线程的请求信息同步过来
     *             RequestContextHolder.setRequestAttributes(requestAttributes);
     *         }, executor);
     *
     * @return
     * @throws ExecutionException
     * @throws InterruptedException
     */
    @Override
    public OrderConfirmVo confirmOder() throws ExecutionException, InterruptedException {
        // 获取用户
        MemberRespVo memberRespVo = LoginUserInterceptor.loginUser.get();

        // 获取主线程的请求信息
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();

        OrderConfirmVo confirmVo = new OrderConfirmVo();
        // 1、远程查询所有的收货地址
        CompletableFuture<Void> getAddresses = CompletableFuture.runAsync(() -> {
            // 在Feign异步调用之前，把主线程的请求信息同步过来
            RequestContextHolder.setRequestAttributes(requestAttributes);
            List<MemberAddressVo> addresses = memberFeignService.getAddresses(memberRespVo.getId());
            confirmVo.setAddresses(addresses);
        }, executor);

        // 2、远程查询购物车所有选中的购物项

        CompletableFuture<Void> getCurrentUserCartItems = CompletableFuture.runAsync(() -> {
            // 在Feign异步调用之前，把主线程的请求信息同步过来
            RequestContextHolder.setRequestAttributes(requestAttributes);
            List<OrderItemVo> currentUserCartItems = cartFeignService.getCurrentUserCartItems();
            confirmVo.setItems(currentUserCartItems);
        }, executor);

        // 3、查询用户的积分信息
        confirmVo.setIntegration(memberRespVo.getIntegration());
        /**
         * Feign远程调用丢失请求头 (Header)
         *
         * Feign在远程调用之前需要构造请求，调用很多的拦截器
         * @see RequestInterceptor interceptor
         *
         * 解决办法
         * @see GmallFeignConfig#requestInterceptor()
         */

        // 4、其他数据自动计算

        // 5、订单防重令牌

        CompletableFuture.allOf(getAddresses, getCurrentUserCartItems).get();
        return confirmVo;
    }

}
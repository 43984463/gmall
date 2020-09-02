package com.sherlock.gmall.order.service.impl;

import com.sherlock.common.vo.MemberRespVo;
import com.sherlock.gmall.order.feign.CartFeignService;
import com.sherlock.gmall.order.feign.MemberFeignService;
import com.sherlock.gmall.order.interceptor.LoginUserInterceptor;
import com.sherlock.gmall.order.vo.MemberAddressVo;
import com.sherlock.gmall.order.vo.OrderConfirmVo;
import com.sherlock.gmall.order.vo.OrderItemVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sherlock.common.utils.PageUtils;
import com.sherlock.common.utils.Query;

import com.sherlock.gmall.order.dao.OrderDao;
import com.sherlock.gmall.order.entity.OrderEntity;
import com.sherlock.gmall.order.service.OrderService;


@Service("orderService")
public class OrderServiceImpl extends ServiceImpl<OrderDao, OrderEntity> implements OrderService {

    @Autowired
    private MemberFeignService memberFeignService;

    @Autowired
    private CartFeignService cartFeignService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<OrderEntity> page = this.page(
                new Query<OrderEntity>().getPage(params),
                new QueryWrapper<OrderEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public OrderConfirmVo confirmOder() {
        // 获取用户
        MemberRespVo memberRespVo = LoginUserInterceptor.loginUser.get();

        OrderConfirmVo confirmVo = new OrderConfirmVo();
        // 1、远程查询所有的收货地址
        List<MemberAddressVo> addresses = memberFeignService.getAddresses(memberRespVo.getId());
        confirmVo.setAddress(addresses);
        // 2、远程查询购物车所有选中的购物项
        List<OrderItemVo> currentUserCartItems = cartFeignService.getCurrentUserCartItems();
        confirmVo.setItems(currentUserCartItems);
        // 3、查询用户的积分信息
        confirmVo.setIntegration(memberRespVo.getIntegration());

        // 4、其他数据自动计算

        // 5、订单防重令牌


        return null;
    }

}
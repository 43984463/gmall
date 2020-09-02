package com.sherlock.gmall.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sherlock.common.utils.PageUtils;
import com.sherlock.gmall.order.entity.OrderEntity;
import com.sherlock.gmall.order.vo.OrderConfirmVo;

import java.util.Map;

/**
 * 订单
 *
 * @author sherlockXue
 * @email xuesherlock@gmail.com
 * @date 2020-05-10 01:13:42
 */
public interface OrderService extends IService<OrderEntity> {

    PageUtils queryPage(Map<String, Object> params);

    OrderConfirmVo confirmOder();

}


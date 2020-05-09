package com.sherlock.gmall.order.dao;

import com.sherlock.gmall.order.entity.OrderItemEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单项信息
 * 
 * @author sherlockXue
 * @email xuesherlock@gmail.com
 * @date 2020-05-10 01:13:42
 */
@Mapper
public interface OrderItemDao extends BaseMapper<OrderItemEntity> {
	
}

package com.sherlock.gmall.coupon.dao;

import com.sherlock.gmall.coupon.entity.CouponEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 优惠券信息
 * 
 * @author sherlockXue
 * @email xuesherlock@gmail.com
 * @date 2020-05-10 00:43:33
 */
@Mapper
public interface CouponDao extends BaseMapper<CouponEntity> {
	
}

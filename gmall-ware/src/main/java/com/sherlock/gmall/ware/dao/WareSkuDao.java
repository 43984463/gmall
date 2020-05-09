package com.sherlock.gmall.ware.dao;

import com.sherlock.gmall.ware.entity.WareSkuEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品库存
 * 
 * @author sherlockXue
 * @email xuesherlock@gmail.com
 * @date 2020-05-10 01:18:07
 */
@Mapper
public interface WareSkuDao extends BaseMapper<WareSkuEntity> {
	
}

package com.sherlock.gmall.product.dao;

import com.sherlock.gmall.product.entity.CategoryEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品三级分类
 * 
 * @author sherlockXue
 * @email xuesherlock@gmail.com
 * @date 2020-05-08 23:38:31
 */
@Mapper
public interface CategoryDao extends BaseMapper<CategoryEntity> {
	
}

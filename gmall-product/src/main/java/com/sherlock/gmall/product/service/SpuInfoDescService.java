package com.sherlock.gmall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sherlock.common.utils.PageUtils;
import com.sherlock.gmall.product.entity.SpuInfoDescEntity;

import java.util.Map;

/**
 * spu信息介绍
 *
 * @author sherlockXue
 * @email xuesherlock@gmail.com
 * @date 2020-05-08 23:38:30
 */
public interface SpuInfoDescService extends IService<SpuInfoDescEntity> {

    PageUtils queryPage(Map<String, Object> params);
}


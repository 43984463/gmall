package com.sherlock.gmall.product.service.impl;

import com.sherlock.gmall.product.service.AttrAttrgroupRelationService;
import com.sherlock.gmall.product.service.CategoryBrandRelationService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sherlock.common.utils.PageUtils;
import com.sherlock.common.utils.Query;

import com.sherlock.gmall.product.dao.BrandDao;
import com.sherlock.gmall.product.entity.BrandEntity;
import com.sherlock.gmall.product.service.BrandService;
import org.springframework.transaction.annotation.Transactional;


@Service("brandService")
public class BrandServiceImpl extends ServiceImpl<BrandDao, BrandEntity> implements BrandService {

    @Autowired
    private CategoryBrandRelationService categoryBrandRelationService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        QueryWrapper<BrandEntity> brandEntityQueryWrapper = new QueryWrapper<>();
        String key = (String)params.get("key");
        if (StringUtils.isNotEmpty(key)) {
            brandEntityQueryWrapper = brandEntityQueryWrapper.eq("brand_id", key).or().like("name", key).or().like("descript",key);
        }
        IPage<BrandEntity> page = this.page(new Query<BrandEntity>().getPage(params),brandEntityQueryWrapper);

        return new PageUtils(page);
    }

    @Transactional
    @Override
    public void updateCascade(BrandEntity brand) {
        updateById(brand);
        if (StringUtils.isNotEmpty(brand.getName())) {
            categoryBrandRelationService.UpdateBrand(brand.getBrandId(), brand.getName());
        }
    }

    @Transactional
    @Override
    public void removeCascade(List<Long> BrandIds) {
        BrandIds.forEach(brandId -> {
            removeById(brandId);
            Map columnMap = new HashMap();
            columnMap.put("brand_id", brandId);
            categoryBrandRelationService.removeByMap(columnMap);
        });
    }

}
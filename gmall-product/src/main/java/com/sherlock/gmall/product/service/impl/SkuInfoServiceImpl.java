package com.sherlock.gmall.product.service.impl;

import com.sherlock.gmall.product.entity.SkuImagesEntity;
import com.sherlock.gmall.product.entity.SpuInfoDescEntity;
import com.sherlock.gmall.product.service.AttrGroupService;
import com.sherlock.gmall.product.service.SkuImagesService;
import com.sherlock.gmall.product.service.SkuSaleAttrValueService;
import com.sherlock.gmall.product.service.SpuInfoDescService;
import com.sherlock.gmall.product.vo.SkuItemSaleAttrVo;
import com.sherlock.gmall.product.vo.SkuItemVo;
import com.sherlock.gmall.product.vo.SpuItemAttrGroupVo;
import jdk.internal.org.objectweb.asm.tree.TryCatchBlockNode;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sherlock.common.utils.PageUtils;
import com.sherlock.common.utils.Query;

import com.sherlock.gmall.product.dao.SkuInfoDao;
import com.sherlock.gmall.product.entity.SkuInfoEntity;
import com.sherlock.gmall.product.service.SkuInfoService;


@Service("skuInfoService")
public class SkuInfoServiceImpl extends ServiceImpl<SkuInfoDao, SkuInfoEntity> implements SkuInfoService {

    @Autowired
    private SkuImagesService skuImagesService;

    @Autowired
    private SpuInfoDescService spuInfoDescService;

    @Autowired
    private AttrGroupService attrGroupService;

    @Autowired
    private SkuSaleAttrValueService skuSaleAttrValueService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SkuInfoEntity> page = this.page(
                new Query<SkuInfoEntity>().getPage(params),
                new QueryWrapper<SkuInfoEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public PageUtils queryPageByCondition(Map<String, Object> params) {
        QueryWrapper<SkuInfoEntity> queryWrapper = new QueryWrapper<>();
        String key = (String) params.get("key");
        if (StringUtils.isNotEmpty(key)) {
            queryWrapper.and(wapper -> {
                wapper.eq("sku_id", key).or().like("sku_name", key);
            });
        }
        String catelogId = (String) params.get("catelogId");
        if (StringUtils.isNotEmpty(catelogId) && !("0").equals(catelogId)) {
            queryWrapper.eq("catalog_id", catelogId);
        }
        String brandId = (String) params.get("brandId");
        if (StringUtils.isNotEmpty(brandId) && !("0").equals(brandId)) {
            queryWrapper.eq("brand_id", brandId);
        }
        String min = (String) params.get("min");
        if (StringUtils.isNotEmpty(min)) {
            queryWrapper.ge("price", min);
        }
        String max = (String) params.get("max");
        if (StringUtils.isNotEmpty(max)) {
            try {
                BigDecimal maxValue = new BigDecimal(max);
                if (maxValue.compareTo(BigDecimal.ZERO) > 0){
                    queryWrapper.le("price", max);
                }
            }catch (Exception e){}
        }

        IPage<SkuInfoEntity> page = this.page(new Query<SkuInfoEntity>().getPage(params), queryWrapper);

        return new PageUtils(page);
    }

    @Override
    public List<SkuInfoEntity> getSkuBySpuId(Long spuId) {
        List<SkuInfoEntity> entities = list(new QueryWrapper<SkuInfoEntity>().eq("spu_id", spuId));
        return entities;
    }

    @Override
    public SkuItemVo item(Long skuId) {
        SkuItemVo skuItemVo = new SkuItemVo();
        // 1、sku基本信息  pms_sku_info
        SkuInfoEntity skuInfoEntity = getById(skuId);
        skuItemVo.setInfo(skuInfoEntity);

        Long catalogId = skuInfoEntity.getCatalogId();
        Long spuId = skuInfoEntity.getSpuId();

        // 2、sku的图片信息 pms_sku_images
        List<SkuImagesEntity> imagesEntities = skuImagesService.getImagesBySkuId(skuId);
        skuItemVo.setImages(imagesEntities);

        // 3、获取spu的销售属性组合
        List<SkuItemSaleAttrVo> saleAttrVos = skuSaleAttrValueService.getSaleAttrsBySpuId(spuId);
        skuItemVo.setSaleAttr(saleAttrVos);

        // 4、获取spu的介绍 psm_spu_info_desc
        SpuInfoDescEntity infoDescEntity = spuInfoDescService.getById(spuId);
        skuItemVo.setDesc(infoDescEntity);

        // 5、获取spu的规格参数信息
        List<SpuItemAttrGroupVo> attrGroupVos = attrGroupService.getAttrGroupWithAttrsBySpuId(spuId, catalogId);
        skuItemVo.setGroupAttrs(attrGroupVos);

        return skuItemVo;
    }

}
package com.sherlock.gmall.product.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sherlock.common.constants.GmallProductConstant;
import com.sherlock.common.to.SkuHasStockVo;
import com.sherlock.common.to.SkuReductionTo;
import com.sherlock.common.to.SpuBoundTo;
import com.sherlock.common.to.es.SkuEsModel;
import com.sherlock.common.utils.PageUtils;
import com.sherlock.common.utils.Query;
import com.sherlock.common.utils.R;
import com.sherlock.common.vo.SpuInfoVo;
import com.sherlock.gmall.product.dao.SpuInfoDao;
import com.sherlock.gmall.product.entity.AttrEntity;
import com.sherlock.gmall.product.entity.BrandEntity;
import com.sherlock.gmall.product.entity.CategoryEntity;
import com.sherlock.gmall.product.entity.ProductAttrValueEntity;
import com.sherlock.gmall.product.entity.SkuImagesEntity;
import com.sherlock.gmall.product.entity.SkuInfoEntity;
import com.sherlock.gmall.product.entity.SkuSaleAttrValueEntity;
import com.sherlock.gmall.product.entity.SpuInfoDescEntity;
import com.sherlock.gmall.product.entity.SpuInfoEntity;
import com.sherlock.gmall.product.feign.CouponFeignService;
import com.sherlock.gmall.product.feign.SearchFeignService;
import com.sherlock.gmall.product.feign.WareFeignService;
import com.sherlock.gmall.product.service.*;
import com.sherlock.gmall.product.vo.Attr;
import com.sherlock.gmall.product.vo.BaseAttrs;
import com.sherlock.gmall.product.vo.Bounds;
import com.sherlock.gmall.product.vo.Images;
import com.sherlock.gmall.product.vo.Skus;
import com.sherlock.gmall.product.vo.SpuSaveVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;


@Service("spuInfoService")
@Slf4j
public class SpuInfoServiceImpl extends ServiceImpl<SpuInfoDao, SpuInfoEntity> implements SpuInfoService {

    @Autowired
    private SpuInfoDescService spuInfoDescService;

    @Autowired
    private SpuImagesService spuImagesService;

    @Autowired
    private AttrService attrService;

    @Autowired
    private ProductAttrValueService productAttrValueService;

    @Autowired
    private SkuInfoService skuInfoService;

    @Autowired
    private SkuImagesService skuImagesService;

    @Autowired
    private SkuSaleAttrValueService skuSaleAttrValueService;

    @Autowired
    private BrandService brandService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private CouponFeignService couponFeignService;

    @Autowired
    private WareFeignService wareFeignService;

    @Autowired
    private SearchFeignService searchFeignService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SpuInfoEntity> page = this.page(
                new Query<SpuInfoEntity>().getPage(params),
                new QueryWrapper<SpuInfoEntity>()
        );

        return new PageUtils(page);
    }

    @Transactional
    @Override
    public void saveSpuInfo(SpuSaveVo spuInfoVo) {
        // 1、保存spu基本信息 pms_spu_info
        SpuInfoEntity spuInfoEntity = new SpuInfoEntity();
        BeanUtils.copyProperties(spuInfoVo, spuInfoEntity);
        spuInfoEntity.setCreateTime(new Date());
        spuInfoEntity.setUpdateTime(new Date());
        saveBaseSpuInfo(spuInfoEntity);

        // 2、保存Spu的描述图片 pms_spu_info_desc
        List<String> decript = spuInfoVo.getDecript();
        SpuInfoDescEntity descEntity = new SpuInfoDescEntity();
        descEntity.setSpuId(spuInfoEntity.getId());
        descEntity.setDecript(String.join(",", decript));
        spuInfoDescService.saveSpuInfoDesc(descEntity);

        // 3、保存spu的图片集 pms_spu_images
        List<String> images = spuInfoVo.getImages();
        spuImagesService.saveImages(spuInfoEntity.getId(), images);

        // 4、保存spu的规格参数;pms_product_attr_value
        List<BaseAttrs> baseAttrs = spuInfoVo.getBaseAttrs();
        List<ProductAttrValueEntity> valueEntities = baseAttrs.stream().map(attr -> {
            ProductAttrValueEntity valueEntity = new ProductAttrValueEntity();
            valueEntity.setAttrId(attr.getAttrId());
            AttrEntity id = attrService.getById(attr.getAttrId());
            valueEntity.setAttrName(id.getAttrName());
            valueEntity.setAttrValue(attr.getAttrValues());
            valueEntity.setQuickShow(attr.getShowDesc());
            valueEntity.setSpuId(spuInfoEntity.getId());

            return valueEntity;
        }).collect(Collectors.toList());
        productAttrValueService.saveProductAttr(valueEntities);


        // 5、保存spu的积分信息；gulimall_sms->sms_spu_bounds
        Bounds bounds = spuInfoVo.getBounds();
        SpuBoundTo spuBoundTo = new SpuBoundTo();
        BeanUtils.copyProperties(bounds, spuBoundTo);
        spuBoundTo.setSpuId(spuInfoEntity.getId());
        R r = couponFeignService.saveSpuBounds(spuBoundTo);
        if (r.getCode() != 0) {
            log.error("远程保存spu积分信息失败");
        }

        // 6、保存当前spu对应的所有sku信息；
        List<Skus> skus = spuInfoVo.getSkus();
        if (!CollectionUtils.isEmpty(skus)) {
            // 6.1）、sku的基本信息；pms_sku_info
            skus.forEach(sku -> {
                String defaultImageUrl = "";

                for (Images image : sku.getImages()) {
                    if (image.getDefaultImg() == 1) {
                        defaultImageUrl = image.getImgUrl();
                    }
                }

                SkuInfoEntity skuInfoEntity = new SkuInfoEntity();
                BeanUtils.copyProperties(sku, skuInfoEntity);
                skuInfoEntity.setBrandId(spuInfoEntity.getBrandId());
                skuInfoEntity.setCatalogId(spuInfoEntity.getCatalogId());
                skuInfoEntity.setSaleCount(0L);
                skuInfoEntity.setSpuId(spuInfoEntity.getId());
                skuInfoEntity.setSkuDefaultImg(defaultImageUrl);
                skuInfoService.save(skuInfoEntity);

                // 生成skuId
                Long skuId = skuInfoEntity.getSkuId();

                // 6.2）、sku的图片信息；pms_sku_image
                List<SkuImagesEntity> skuImagesEntities = sku.getImages().stream().map(image -> {
                    SkuImagesEntity skuImagesEntity = new SkuImagesEntity();
                    skuImagesEntity.setSkuId(skuId);
                    skuImagesEntity.setDefaultImg(image.getDefaultImg());
                    skuImagesEntity.setImgUrl(image.getImgUrl());
                    return skuImagesEntity;
                }).filter(skuImagesEntity -> StringUtils.isNotBlank(skuImagesEntity.getImgUrl()))
                        .collect(Collectors.toList());

                skuImagesService.saveBatch(skuImagesEntities);

                // 6.3）、sku的销售属性信息：pms_sku_sale_attr_value
                List<Attr> attrs = sku.getAttr();
                List<SkuSaleAttrValueEntity> saleAttrValueEntities = attrs.stream().map(attr -> {
                    SkuSaleAttrValueEntity attrValueEntity = new SkuSaleAttrValueEntity();
                    BeanUtils.copyProperties(attr, attrValueEntity);
                    attrValueEntity.setSkuId(skuId);
                    return attrValueEntity;
                }).collect(Collectors.toList());

                skuSaleAttrValueService.saveBatch(saleAttrValueEntities);

                // 6.4）、sku的优惠、满减等信息；gulimall_sms->sms_sku_ladder\sms_sku_full_reduction\sms_member_price
                SkuReductionTo skuReductionTo = new SkuReductionTo();
                BeanUtils.copyProperties(sku, skuReductionTo);
                skuReductionTo.setSkuId(skuId);
                if (skuReductionTo.getFullCount() > 0 || skuReductionTo.getFullPrice().compareTo(BigDecimal.ZERO) > 0) {
                    couponFeignService.saveSkuReduction(skuReductionTo);
                }

            });
        }

    }

    @Override
    public void saveBaseSpuInfo(SpuInfoEntity spuInfoEntity) {
        save(spuInfoEntity);
    }

    @Override
    public PageUtils queryPageByCondition(Map<String, Object> params) {

        QueryWrapper<SpuInfoEntity> queryWrapper = new QueryWrapper<>();
        String key = (String) params.get("key");
        if (StringUtils.isNotBlank(key)) {
            queryWrapper.and(wapper -> wapper.eq("id", key).or().like("spu_name", key));
        }
        String status = (String) params.get("status");
        if (StringUtils.isNotBlank(status)) {
            queryWrapper.eq("publish_status", status);
        }
        String brandId = (String) params.get("brandId");
        if (StringUtils.isNotBlank(brandId) && !("0").equals(brandId)) {
            queryWrapper.eq("brand_id", brandId);
        }
        String catelogId = (String) params.get("catelogId");
        if (StringUtils.isNotBlank(catelogId) && !("0").equals(brandId)) {
            queryWrapper.eq("catalog_id", catelogId);
        }

        IPage<SpuInfoEntity> page = this.page(new Query<SpuInfoEntity>().getPage(params), queryWrapper);

        return new PageUtils(page);
    }

    @Transactional
    @Override
    public void up(Long spuId) {

        // 查出当前spuId对应的所有sku信息
        List<SkuInfoEntity> entities = skuInfoService.getSkuBySpuId(spuId);
        List<Long> skuIds = entities.stream().map(SkuInfoEntity::getSkuId).collect(Collectors.toList());

        // 4、查询当前sku的所有可以被检索的规格属性
        // 先找出所有的属性
        List<ProductAttrValueEntity> baseAttrs = productAttrValueService.baseAttrListForSpu(spuId);
        List<Long> attrIds = baseAttrs.stream().map(ProductAttrValueEntity::getAttrId).collect(Collectors.toList());
        // 挑出可以用来检索的属性的Id集合
        List<Long> searchAttrIds = attrService.selectSearchAttrIds(attrIds);
        Set<Long> searchAttrIdSet = new HashSet<>(searchAttrIds);
        // 把baseAttrs里面可以用来检索的挑出来
        List<SkuEsModel.Attrs> attrsList = baseAttrs.stream()
                .filter(attr -> searchAttrIdSet.contains(attr.getAttrId()))
                .map(attr -> {
                    SkuEsModel.Attrs attrs = new SkuEsModel.Attrs();
                    BeanUtils.copyProperties(attr, attrs);
                    return attrs;
                }).collect(Collectors.toList());

        Map<Long, Boolean> hasStockMap = null;
        try {
            /**
             * 方式1 使用泛型R接收
             */
            /*R<List<SkuHasStockVo>> skuHasStock = wareFeignService.getSkuHasStock(skuIds);
            //转换为是否key为skuId，value为是否含有库存的boolean值
            TypeReference<List<SkuHasStockVo>> listTypeReference = new TypeReference<List<SkuHasStockVo>>() {};
            hasStockMap = skuHasStock.getData(listTypeReference).stream().collect(Collectors.toMap(SkuHasStockVo::getSkuId, item -> item.getHasStock()));*/

            /**
             * 使用带泛型的ResponseEntity接收
             */
            ResponseEntity<List<SkuHasStockVo>> skuHasStock = wareFeignService.getSkuHasStock(skuIds);
            //转换为是否key为skuId，value为是否含有库存的boolean值
            hasStockMap = skuHasStock.getBody().stream().collect(Collectors.toMap(SkuHasStockVo::getSkuId, item -> item.getHasStock()));
        } catch (Exception e) {
            log.error("库存查询异常");
            e.printStackTrace();
        }

        // 封装每个sku信息
        Map<Long, Boolean> finalHasStockMap = hasStockMap;
        List<SkuEsModel> skuEsModels = entities.stream().map(sku -> {
            // 组装需要的数据
            SkuEsModel model = new SkuEsModel();
            BeanUtils.copyProperties(sku, model);
            // 组装skuinfo里名称不匹配的和没有的字段
            model.setSkuPrice(sku.getPrice());
            model.setSkuImg(sku.getSkuDefaultImg());

            model.setHasStock(finalHasStockMap == null ? true : finalHasStockMap.get(sku.getSkuId()));
            // TODO 2、热度评分 (暂时默认0)
            model.setHotScore(0L);

            // 3、查询品牌和分类的名字
            BrandEntity brandEntity = brandService.getById(model.getBrandId());
            model.setBrandName(brandEntity.getName());
            model.setBrandImg(brandEntity.getLogo());

            CategoryEntity categoryEntity = categoryService.getById(model.getCatalogId());
            model.setCatalogName(categoryEntity.getName());

            model.setAttrs(attrsList);

            return model;
        }).collect(Collectors.toList());

        // 把sku信息发送给gmall-search进行保存到ES
        R booleanR = searchFeignService.productStatusUp(skuEsModels);
        if (booleanR.getCode() == 0) {
            // 远程调用成功， 修改上架状态
            log.info("远程调用成功");
            baseMapper.updateSpuStatus(spuId, GmallProductConstant.ProductStatusEnum.SPU_UP.getCode());
        } else {
            throw new RuntimeException();
            // TODO 远程调用失败，重新调用，保持幂等性， 重试机制
        }
    }

    @Override
    public SpuInfoVo getSpuInfoBySkuId(Long skuId) {
        SpuInfoVo spuInfoVo = new SpuInfoVo();
        SkuInfoEntity skuInfo = skuInfoService.getById(skuId);
        Long spuId = skuInfo.getSpuId();
        SpuInfoEntity spuInfoEntity = getById(spuId);

        BeanUtils.copyProperties(spuInfoEntity, spuInfoVo);
        BrandEntity brandEntity = brandService.getById(spuInfoEntity.getBrandId());

        spuInfoVo.setBrandName(brandEntity.getName());
        spuInfoVo.setSkuPrice(skuInfo.getPrice());
        return spuInfoVo;
    }

}
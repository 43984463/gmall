package com.sherlock.gmall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sherlock.common.constants.GmallConstant;
import com.sherlock.common.constants.GmallProductConstant;
import com.sherlock.common.utils.PageUtils;
import com.sherlock.common.utils.Query;
import com.sherlock.gmall.product.dao.AttrAttrgroupRelationDao;
import com.sherlock.gmall.product.dao.AttrDao;
import com.sherlock.gmall.product.entity.AttrAttrgroupRelationEntity;
import com.sherlock.gmall.product.entity.AttrEntity;
import com.sherlock.gmall.product.entity.AttrGroupEntity;
import com.sherlock.gmall.product.entity.CategoryEntity;
import com.sherlock.gmall.product.service.AttrAttrgroupRelationService;
import com.sherlock.gmall.product.service.AttrGroupService;
import com.sherlock.gmall.product.service.AttrService;
import com.sherlock.gmall.product.service.CategoryService;
import com.sherlock.gmall.product.vo.AttrGroupRelationVo;
import com.sherlock.gmall.product.vo.AttrResVo;
import com.sherlock.gmall.product.vo.AttrVo;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@Service("attrService")
public class AttrServiceImpl extends ServiceImpl<AttrDao, AttrEntity> implements AttrService {

    @Autowired
    private AttrAttrgroupRelationService attrAttrgroupRelationService;

    @Resource
    private AttrAttrgroupRelationDao relationDao;

    @Autowired
    private AttrGroupService attrGroupService;

    @Autowired
    private CategoryService categoryService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<AttrEntity> page = this.page(
                new Query<AttrEntity>().getPage(params),
                new QueryWrapper<AttrEntity>()
        );

        return new PageUtils(page);
    }

    @Transactional
    @Override
    public void saveAttr(AttrVo attr) {
        AttrEntity attrEntity = new AttrEntity();
        BeanUtils.copyProperties(attr, attrEntity);
        save(attrEntity);

        if (attr.getAttrType() == GmallProductConstant.Product_Attr_Enum.ATTR_TYPE_BASE.getCode() && attr.getAttrGroupId() != null) {
            AttrAttrgroupRelationEntity relationEntity = new AttrAttrgroupRelationEntity();
            relationEntity.setAttrGroupId(attr.getAttrGroupId());
            relationEntity.setAttrId(attrEntity.getAttrId());
            attrAttrgroupRelationService.save(relationEntity);
        }
    }

    @Override
    public PageUtils queryBaseAttrPage(Map<String, Object> params, Long catelogId, String type) {
        QueryWrapper<AttrEntity> queryWrapper = new QueryWrapper<AttrEntity>().eq("attr_type", "base".equalsIgnoreCase(type) ? GmallProductConstant.Product_Attr_Enum.ATTR_TYPE_BASE.getCode() : GmallProductConstant.Product_Attr_Enum.ATTR_TYPE_SALE.getCode());
        if (catelogId != 0) {
            queryWrapper.eq("catelog_id", catelogId);
        }
        String key = (String) params.get("key");
        if (StringUtils.isNotBlank(key)) {
            queryWrapper.and((obj) -> {
                obj.eq("attr_id", key).or().like("attr_name", key);
            });
        }

        IPage<AttrEntity> page = this.page(new Query<AttrEntity>().getPage(params), queryWrapper);
        PageUtils pageUtils = new PageUtils(page);
        List<AttrEntity> records = page.getRecords();
        List<AttrResVo> attrResVos = records.stream().map((attrEntity -> {
            AttrResVo attrResVo = new AttrResVo();
            BeanUtils.copyProperties(attrEntity, attrResVo);

            if ("base".equalsIgnoreCase(type)) {
                AttrAttrgroupRelationEntity relationEntity = attrAttrgroupRelationService.getOne(new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_id", attrEntity.getAttrId()));
                if (relationEntity != null && relationEntity.getAttrGroupId() != null) {
                    AttrGroupEntity attrGroupEntity = attrGroupService.getById(relationEntity.getAttrGroupId());
                    attrResVo.setGroupName(attrGroupEntity.getAttrGroupName());
                }
            }
            CategoryEntity categoryEntity = categoryService.getById(attrEntity.getCatelogId());
            if (categoryEntity != null) {
                attrResVo.setCatelogName(categoryEntity.getName());
            }

            return attrResVo;
        })).collect(Collectors.toList());
        pageUtils.setList(attrResVos);
        return pageUtils;
    }

    @Override
    public AttrResVo getAttrInfo(Long attrId) {
        AttrEntity attrEntity = getById(attrId);
        AttrResVo attrResVo = new AttrResVo();
        BeanUtils.copyProperties(attrEntity, attrResVo);

        if (attrEntity.getAttrType() == GmallProductConstant.Product_Attr_Enum.ATTR_TYPE_BASE.getCode()) {
            AttrAttrgroupRelationEntity relationEntity = attrAttrgroupRelationService.getOne(new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_id", attrEntity.getAttrId()));
            if (relationEntity != null) {
                AttrGroupEntity attrGroupEntity = attrGroupService.getById(relationEntity.getAttrGroupId());
                if (null != attrGroupEntity) {
                    attrResVo.setGroupName(attrGroupEntity.getAttrGroupName());
                    attrResVo.setAttrGroupId(attrGroupEntity.getAttrGroupId());
                }
            }
        }
        Long[] categoryPath = categoryService.getCategoryPath(attrEntity.getCatelogId());
        attrResVo.setCatelogPath(categoryPath);
        CategoryEntity categoryEntity = categoryService.getById(attrEntity.getCatelogId());
        if (categoryEntity != null) {
            attrResVo.setCatelogName(categoryEntity.getName());
        }
        return attrResVo;
    }


    @Transactional
    @Override
    public void updateAttr(AttrVo attr) {
        AttrEntity attrEntity = new AttrEntity();
        BeanUtils.copyProperties(attr, attrEntity);
        updateById(attrEntity);


        if (attrEntity.getAttrType() == GmallProductConstant.Product_Attr_Enum.ATTR_TYPE_BASE.getCode()) {
            AttrAttrgroupRelationEntity relationEntity = new AttrAttrgroupRelationEntity();
            relationEntity.setAttrGroupId(attr.getAttrGroupId());
            relationEntity.setAttrId(attr.getAttrId());


            List<AttrAttrgroupRelationEntity> relationEntities = attrAttrgroupRelationService.list(new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_id", attr.getAttrId()));
            if (attr.getAttrGroupId() != null) {
                if (!CollectionUtils.isEmpty(relationEntities)) {
                    relationEntity.setId(relationEntities.get(0).getId());
                    attrAttrgroupRelationService.updateById(relationEntity);
                } else {
                    attrAttrgroupRelationService.save(relationEntity);
                }
            } else {
                if (!CollectionUtils.isEmpty(relationEntities)) {
                    attrAttrgroupRelationService.removeById(relationEntities.get(0).getId());
                }
            }
        }
    }

    /**
     * 根据分组ID查找关联的所有基本属性
     * @param attrgroupId
     * @return
     */
    @Override
    public List<AttrEntity> getRelationAttr(Long attrgroupId) {
        List<AttrAttrgroupRelationEntity> entities = attrAttrgroupRelationService.list(new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_group_id", attrgroupId));
        List<Long> attrIds = entities.stream().map(attr -> attr.getAttrId()).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(attrIds)) {
            return null;
        }
        List<AttrEntity> attrEntities = listByIds(attrIds);
        return attrEntities;
    }

    @Override
    public void deleteRelation(List<AttrGroupRelationVo> vos) {
        List<AttrAttrgroupRelationEntity> relationEntities = vos.stream().map(vo -> {
            AttrAttrgroupRelationEntity entity = new AttrAttrgroupRelationEntity();
            BeanUtils.copyProperties(vo, entity);
            return entity;
        }).collect(Collectors.toList());
        relationDao.deleteBatchRelation(relationEntities);
    }

    @Override
    public PageUtils getNoRelationAttr(Long attrgroupId, Map<String, Object> params) {
        // 当前分组只能关联自己所属的分类里面的所有属性并且只能关联别的分组没有引用的属性
        List<Long> attrIds = new ArrayList<>();
        AttrGroupEntity attrGroupEntity = attrGroupService.getById(attrgroupId);
        List<AttrAttrgroupRelationEntity> relationEntities = attrAttrgroupRelationService.list(new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_group_id", attrgroupId));
        Long catelogId = attrGroupEntity.getCatelogId();
        // 当前分类的所有不等于传入的组ids
        List<AttrGroupEntity> attrGroupEntities = attrGroupService.list(new QueryWrapper<AttrGroupEntity>().eq("catelog_id", catelogId).ne("attr_group_id", attrgroupId));
        List<Long> attrGroupIds = attrGroupEntities.stream().map(attrGroup -> attrGroup.getAttrGroupId()).collect(Collectors.toList());
        // 当前分类组里面的所有已经关联的属性的ID集合
        QueryWrapper<AttrAttrgroupRelationEntity> attrAttrgroupRelationEntityQueryWrapper = new QueryWrapper<>();
        // 当前分类的其他分组
        if (!CollectionUtils.isEmpty(attrGroupIds)) {
            attrAttrgroupRelationEntityQueryWrapper.in("attr_group_id", attrGroupIds);
        }
        List<AttrAttrgroupRelationEntity> attrAttrgroupRelationEntities = attrAttrgroupRelationService.list(attrAttrgroupRelationEntityQueryWrapper);
        attrIds = attrAttrgroupRelationEntities.stream().map(attrAttrgroupRelationEntitie -> attrAttrgroupRelationEntitie.getAttrId()).collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(relationEntities)) {
            attrIds.addAll(relationEntities.stream().map(relation -> relation.getAttrId()).collect(Collectors.toList()));
        }
        // 找出所有等于当前组但是不在上面属性集合里面的属性(并且是规格参数而不是销售属性)
        QueryWrapper<AttrEntity> queryWrapper = new QueryWrapper<AttrEntity>().eq("catelog_id", catelogId).eq("attr_type", GmallProductConstant.Product_Attr_Enum.ATTR_TYPE_BASE.getCode());
        if (!CollectionUtils.isEmpty(attrIds)) {
            queryWrapper.notIn("attr_id", attrIds);
        }
        // 模糊查询
        String key = (String) params.get("key");
        if (StringUtils.isNotEmpty(key)) {
            queryWrapper.and(wapper -> {
                wapper.eq("attr_id",key).or().like("attr_name",key);
            });
        }

        IPage<AttrEntity> page = this.page(new Query<AttrEntity>().getPage(params), queryWrapper);
        return new PageUtils(page);
    }

}
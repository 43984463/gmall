package com.sherlock.gmall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sherlock.common.utils.PageUtils;
import com.sherlock.common.utils.Query;
import com.sherlock.gmall.product.dao.AttrDao;
import com.sherlock.gmall.product.entity.AttrAttrgroupRelationEntity;
import com.sherlock.gmall.product.entity.AttrEntity;
import com.sherlock.gmall.product.entity.AttrGroupEntity;
import com.sherlock.gmall.product.entity.CategoryEntity;
import com.sherlock.gmall.product.service.AttrAttrgroupRelationService;
import com.sherlock.gmall.product.service.AttrGroupService;
import com.sherlock.gmall.product.service.AttrService;
import com.sherlock.gmall.product.service.CategoryService;
import com.sherlock.gmall.product.vo.AttrResVo;
import com.sherlock.gmall.product.vo.AttrVo;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;


@Service("attrService")
public class AttrServiceImpl extends ServiceImpl<AttrDao, AttrEntity> implements AttrService {

    @Autowired
    private AttrAttrgroupRelationService attrAttrgroupRelationService;

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

        AttrAttrgroupRelationEntity relationEntity = new AttrAttrgroupRelationEntity();
        relationEntity.setAttrGroupId(attr.getAttrGroupId());
        relationEntity.setAttrId(attrEntity.getAttrId());
        attrAttrgroupRelationService.save(relationEntity);
    }

    @Override
    public PageUtils queryBaseAttrPage(Map<String, Object> params, Long catelogId) {
        QueryWrapper<AttrEntity> queryWrapper = new QueryWrapper<>();
        if (catelogId != 0) {
            queryWrapper.eq("catelog_id", catelogId);
        }
        String key = (String)params.get("key");
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

            AttrAttrgroupRelationEntity relationEntity = attrAttrgroupRelationService.getOne(new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_id", attrEntity.getAttrId()));
            if (relationEntity != null) {
                AttrGroupEntity attrGroupEntity = attrGroupService.getById(relationEntity.getAttrGroupId());
                attrResVo.setGroupName(attrGroupEntity.getAttrGroupName());
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
        BeanUtils.copyProperties(attrEntity,attrResVo);

        AttrAttrgroupRelationEntity relationEntity = attrAttrgroupRelationService.getOne(new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_id", attrEntity.getAttrId()));
        if (relationEntity != null) {
            AttrGroupEntity attrGroupEntity = attrGroupService.getById(relationEntity.getAttrGroupId());
            if (null != attrGroupEntity) {
                attrResVo.setGroupName(attrGroupEntity.getAttrGroupName());
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
    public void updateAttr(AttrResVo attr) {
        AttrEntity attrEntity = new AttrEntity();
        BeanUtils.copyProperties(attr, attrEntity);
        updateById(attrEntity);

        AttrAttrgroupRelationEntity relationEntity = new AttrAttrgroupRelationEntity();
        relationEntity.setAttrGroupId(attr.getAttrGroupId());
        relationEntity.setAttrId(attr.getAttrId());

        int count = attrAttrgroupRelationService.count(new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_id", attr.getAttrId()));
        if (count > 0){
            attrAttrgroupRelationService.update(relationEntity, new UpdateWrapper<AttrAttrgroupRelationEntity>().eq("attr_id",attr.getAttrId()));
        } else {

        }attrAttrgroupRelationService.save(relationEntity);
    }

}
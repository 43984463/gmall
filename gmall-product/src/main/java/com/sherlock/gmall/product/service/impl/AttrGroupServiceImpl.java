package com.sherlock.gmall.product.service.impl;

import com.sherlock.gmall.product.dao.AttrAttrgroupRelationDao;
import com.sherlock.gmall.product.entity.AttrAttrgroupRelationEntity;
import com.sherlock.gmall.product.entity.AttrEntity;
import com.sherlock.gmall.product.service.AttrAttrgroupRelationService;
import com.sherlock.gmall.product.service.AttrService;
import com.sherlock.gmall.product.vo.AttrGroupWithAttrsVo;
import com.sherlock.gmall.product.vo.SkuItemVo;
import com.sherlock.gmall.product.vo.SpuItemAttrGroupVo;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sherlock.common.utils.PageUtils;
import com.sherlock.common.utils.Query;

import com.sherlock.gmall.product.dao.AttrGroupDao;
import com.sherlock.gmall.product.entity.AttrGroupEntity;
import com.sherlock.gmall.product.service.AttrGroupService;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;


@Service("attrGroupService")
public class AttrGroupServiceImpl extends ServiceImpl<AttrGroupDao, AttrGroupEntity> implements AttrGroupService {

    @Autowired
    private AttrAttrgroupRelationService relationService;

    @Autowired
    private AttrService attrService;

    @Autowired
    private AttrGroupDao attrGroupDao;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<AttrGroupEntity> page = this.page(
                new Query<AttrGroupEntity>().getPage(params),
                new QueryWrapper<AttrGroupEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public PageUtils queryPageByCatelogId(Map<String, Object> params, Long catelogId) {
        QueryWrapper<AttrGroupEntity> attrGroupEntityQueryWrapper = new QueryWrapper<>();
        String param = (String)params.get("key");
        Optional.ofNullable(param).ifPresent(key -> {
            if (StringUtils.isNotBlank(key)) {
                attrGroupEntityQueryWrapper.and((obj) -> {
                    obj.eq("attr_group_id", key).or().like("attr_group_name", key);
                });
            }
        });
        if (catelogId == 0) {
            IPage<AttrGroupEntity> page = this.page(new Query<AttrGroupEntity>().getPage(params),attrGroupEntityQueryWrapper);
            return new PageUtils(page);
        } else {
            attrGroupEntityQueryWrapper.eq("catelog_id", catelogId);
            IPage<AttrGroupEntity> page = this.page(new Query<AttrGroupEntity>().getPage(params), attrGroupEntityQueryWrapper);
            return new PageUtils(page);
        }
    }

    @Transactional
    @Override
    public void updateAttrAndRelation(AttrGroupEntity attrGroup) {
        AttrGroupEntity groupEntityInDb = getById(attrGroup.getAttrGroupId());
        updateById(attrGroup);
        if (attrGroup.getCatelogId().longValue() != groupEntityInDb.getCatelogId().longValue()) {
            relationService.remove(new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_group_id", groupEntityInDb.getAttrGroupId()));
        }
    }

    @Override
    public List<AttrGroupWithAttrsVo> getAttrGroupWithAttrsByCatelogId(Long catelogId) {
        List<AttrGroupEntity> groupEntities = list(new QueryWrapper<AttrGroupEntity>().eq("catelog_id", catelogId));
        List<AttrGroupWithAttrsVo> attrGroupWithAttrsVos = groupEntities.stream().map(groupEntity -> {
            AttrGroupWithAttrsVo vo = new AttrGroupWithAttrsVo();
            BeanUtils.copyProperties(groupEntity, vo);
            List<AttrEntity> attrs = attrService.getRelationAttr(groupEntity.getAttrGroupId());
            vo.setAttrs(attrs);
            return vo;
        }).collect(Collectors.toList());
        return attrGroupWithAttrsVos;
    }

    @Override
    public List<SpuItemAttrGroupVo> getAttrGroupWithAttrsBySpuId(Long spuId, Long catelogId) {
        // 1、查出当前Spu对应的所有属性的分组信息以及当前分组下的所有属性对应的值
        List<SpuItemAttrGroupVo> vos = attrGroupDao.getAttrGroupWithAttrsBySpuId(spuId, catelogId);
        return vos;
    }

}
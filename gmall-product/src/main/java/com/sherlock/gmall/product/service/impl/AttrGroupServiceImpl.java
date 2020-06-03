package com.sherlock.gmall.product.service.impl;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import java.util.Map;
import java.util.Optional;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sherlock.common.utils.PageUtils;
import com.sherlock.common.utils.Query;

import com.sherlock.gmall.product.dao.AttrGroupDao;
import com.sherlock.gmall.product.entity.AttrGroupEntity;
import com.sherlock.gmall.product.service.AttrGroupService;


@Service("attrGroupService")
public class AttrGroupServiceImpl extends ServiceImpl<AttrGroupDao, AttrGroupEntity> implements AttrGroupService {

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

}
package com.sherlock.gmall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sherlock.common.utils.PageUtils;
import com.sherlock.common.utils.Query;
import com.sherlock.gmall.product.dao.CategoryDao;
import com.sherlock.gmall.product.entity.AttrGroupEntity;
import com.sherlock.gmall.product.entity.CategoryEntity;
import com.sherlock.gmall.product.service.CategoryService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryEntity> page = this.page(
                new Query<CategoryEntity>().getPage(params),
                new QueryWrapper<CategoryEntity>()
        );

        return new PageUtils(page);
    }

    /**
     * 获取菜单以及子菜单
     *
     * @return
     */
    @Override
    public List<CategoryEntity> listWithTree() {

        List<CategoryEntity> entities = baseMapper.selectList(null);

        List<CategoryEntity> Menus = entities.stream()
                .filter(categoryEntity -> categoryEntity.getParentCid() == 0)
                .peek(menu -> menu.setChildren(getChildren(menu, entities)))
                // 正序排列或者倒叙排列
                .sorted(Comparator.comparingInt(CategoryEntity::getSort))
                //.sorted(Comparator.comparingInt(CategoryEntity::getSort).reversed())
                //.sorted((menu1, menu2) -> (menu1.getSort() == null ? 0 : menu1.getSort()) - (menu2.getSort() == null ? 0 : menu2.getSort()))
                .collect(Collectors.toList());
        return Menus;
    }

    /**
     * 递归获取子菜单
     *
     * @param root
     * @param entities
     * @return
     */
    private List<CategoryEntity> getChildren(CategoryEntity root, List<CategoryEntity> entities) {
        List<CategoryEntity> childrenMenu = entities.stream()
                // 此处应该使用.equal方法而不是 ==
                .filter(categoryEntity -> categoryEntity.getParentCid().equals(root.getCatId()))
                .peek(menu -> menu.setChildren(getChildren(menu, entities)))
                .sorted(Comparator.comparingInt(CategoryEntity::getSort))
                //.sorted((menu1, menu2) -> (menu1.getSort() == null ? 0 : menu1.getSort()) - (menu2.getSort() == null ? 0 : menu2.getSort()))
                .collect(Collectors.toList());
        return childrenMenu;
    }

    /**
     * 逻辑删除菜单
     *
     * @param asList
     */
    @Override
    public int deleteMenusByIds(List<Long> asList) {
        // TODO 删除之前进行验证
        return baseMapper.deleteBatchIds(asList);
    }

    /**
     * 根据最底即找到完整路径
     * @param catelogId
     * @return
     */
    @Override
    public Long [] getCategoryPath(Long catelogId) {
        List<Long> path = new ArrayList<>();
        path = findParentPath(catelogId, path);
        Collections.reverse(path);
        return path.toArray(new Long[0]);
    }


    private List<Long> findParentPath(Long catelogId, List<Long> path) {
        path.add(catelogId);
        CategoryEntity categoryEntity = getById(catelogId);
        if(categoryEntity.getParentCid() != 0){
            findParentPath(categoryEntity.getParentCid(), path);
        }
        return path;
    }
}
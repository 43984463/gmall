package com.sherlock.gmall.product.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sherlock.common.utils.PageUtils;
import com.sherlock.common.utils.Query;
import com.sherlock.gmall.product.dao.CategoryDao;
import com.sherlock.gmall.product.entity.CategoryEntity;
import com.sherlock.gmall.product.service.CategoryBrandRelationService;
import com.sherlock.gmall.product.service.CategoryService;
import com.sherlock.gmall.product.vo.Catelog2Vo;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;


@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {

    @Autowired
    private CategoryBrandRelationService categoryBrandRelationService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

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

    @Transactional
    @Override
    public void updateCascade(CategoryEntity category) {
        updateById(category);
        categoryBrandRelationService.updateCategory(category.getCatId(), category.getName());
    }

    @Override
    public List<CategoryEntity> getLevel1Categorys() {
      return list(new QueryWrapper<CategoryEntity>().eq("parent_cid", 0));
    }

    /**
     * 请求经过缓存
     * @return
     *
     * TODO
     * 使用压力测试工具测试时会产生异常
     * //  产生了堆外存泄漏：OutDirceMemoryError
     *       1、springboot2.0以后默认使用lettuce作为操作redis的客户端。它使用netty进行网络通信
     *       2、lettuce的bug导致netty的堆内存溢出 （-Xmx300m  如果没有指定堆外存， 默认使用 -Xmx300）
     *            -Dio.netty.maxDirectMemory进行设置
     *       解决方案： 不能单独-Dio.netty.maxDirectMemory进行设置 调大堆外内存
     *                ① 升级lettuce客户端      ② 切换使用jedis
     *
     *         优化一：将数据库的多次查询变为一次
     *         优化二：将查到的数据放入redis缓存
     *
     *         优化：
     *              缓存穿透： 设置空结果缓存
     *
     *              缓存雪崩： 设置缓存随机的过期时间
     *
     *              缓存击穿： 加锁
     *
     */
    @Override
    public Map<String, List<Catelog2Vo>> getCatelogJson() {
        // 1.先从缓存中获取
        String catelogJson = stringRedisTemplate.opsForValue().get("catelogJson");
        // 2.缓存中没有就查询数据库并放入缓存
        if (StringUtils.isEmpty(catelogJson)) {
            // 查询数据库
            Map<String, List<Catelog2Vo>> catelogJsonFromDb = getCatelogJsonFromDb();

            String jsonString = JSON.toJSONString(catelogJsonFromDb);
            stringRedisTemplate.opsForValue().set("catelogJson", jsonString);
            return catelogJsonFromDb;
        }

        Map<String, List<Catelog2Vo>> stringListMap = JSON.parseObject(catelogJson, new TypeReference<Map<String, List<Catelog2Vo>>>() {
        });
        return stringListMap;
    }

    public Map<String, List<Catelog2Vo>> getCatelogJsonFromDb() {
        List<CategoryEntity> categoryEntityList = list();

        List<CategoryEntity> level1Categorys = getParent_cid(categoryEntityList, 0L);
        Map<String, List<Catelog2Vo>> resultMap = level1Categorys.stream().collect(Collectors.toMap(key -> key.getCatId().toString(), value -> {
            //1. 找到1及分类的所有2级分类
            List<CategoryEntity> level2Entities = getParent_cid(categoryEntityList, value.getCatId());
            List<Catelog2Vo> catelog2Vos = null;
            if (level2Entities != null) {
                catelog2Vos = level2Entities.stream().map(level2 -> {
                    List<Catelog2Vo.Catelog3Vo> catelog3Vos = null;
                    if (level2 != null) {
                        List<CategoryEntity> level3Entities = getParent_cid(categoryEntityList, level2.getCatId());
                        if (level3Entities != null) {
                            catelog3Vos = level3Entities.stream().map(level3 -> {
                                Catelog2Vo.Catelog3Vo catelog3Vo = new Catelog2Vo.Catelog3Vo(level2.getCatId().toString(),level3.getCatId().toString(),level3.getName());
                                return catelog3Vo;
                            }).collect(Collectors.toList());
                        }
                    }
                    Catelog2Vo catelog2Vo = new Catelog2Vo(value.getCatId().toString(), catelog3Vos, level2.getCatId().toString(), level2.getName());
                    return catelog2Vo;
                }).collect(Collectors.toList());
            }

            return catelog2Vos;
        }));
        return resultMap;
    }

    private List<CategoryEntity> getParent_cid(List<CategoryEntity> categoryEntityList, Long parent_id) {
        return categoryEntityList.stream().filter(categoryEntity -> categoryEntity.getParentCid() == parent_id).collect(Collectors.toList());
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
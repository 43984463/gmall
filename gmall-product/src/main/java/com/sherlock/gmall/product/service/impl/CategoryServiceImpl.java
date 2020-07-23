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
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.cache.CacheAutoConfiguration;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


@Service("categoryService")
@Slf4j
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {

    @Autowired
    private CategoryBrandRelationService categoryBrandRelationService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private RedissonClient redissonClient;

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

    /**
     *
     * 缓存失效模式
     *
     * value 与 {@link CategoryServiceImpl#getLevel1Categorys()}方法中的
     * @Cacheable(value = {"category"}, key = "#root.method.name")
     * 的value和缓存的key相同
     *
     * key为常量时需要用''包住
     *
     *
     * CacheEvict只能清除缓存中的1个值
     *
     * 缓存多个 方法①
     *@Caching(evict = {
     *             @CacheEvict(value = {"category"}, key = "'getCatelogJson'"),
     *             @CacheEvict(value = {"category"}, key = "'getLevel1Categorys'")
     *     })
     *  可以组合多个缓存操作(添加，删除，更新)
     *
     * 方法② @CacheEvict(value = {"category"}, allEntries = true)
     * 相同类型或者需要一起删除的数据可以放在同一个分区下，删除时allEntries = true一次删除全部
     *
     *
     * @CachePut 双写模式  方法的返回值需要在缓存中更新
     * @CacheEvict 失效模式
     *
     * @param category
     */
    //@CacheEvict(value = {"category"}, key = "'getCatelogJson'")
    //@CacheEvict(value = {"category"}, allEntries = true)
    @Caching(evict = {
            @CacheEvict(value = {"category"}, key = "'getCatelogJson'"),
            @CacheEvict(value = {"category"}, key = "'getLevel1Categorys'")
    })
    @Transactional
    @Override
    public void updateCascade(CategoryEntity category) {
        updateById(category);
        categoryBrandRelationService.updateCategory(category.getCatId(), category.getName());
    }

    /**
     *1、每一个需要缓存的数据我们都来指定放到哪个名字的缓存 【缓存的分区（按照业务类型分）】 @Cacheable的value属性 value为数组类型
     *2、@Cacheable({"category"}) // 代表当前方法的结果需要缓存，如果缓存中有，方法不用调用，直接从缓存中获取。如果缓存中没有，会调用方法，并将方法的结果放入缓存
     *3、默认行为
     *      1）、如果缓存中有，方法不用调用
     *      2）、key是默认生成的，格式为：   缓存的名字::SimpleKey[]   (category::SimpleKey [])
     *      3）、缓存的value的值。默认使用jdk序列化机制，将序列化后的数据放到redis
     *      4）、默认过期时间为-1 （即永不过期）
     *
     *    所以需要修改并自定义：
     *
     *       1）、指定key的格式
     *          @see Cacheable#key()  可以接收一个SpEL表达式
     *          key = "'level1Categorys'"  代表在缓存中的名称就是level1Categorys
     *          key = "#root.method.name"  代表在缓存中的名称就是方法的名字getLevel1Categorys
     *          SpEL 参考
     *          @see <a href="https://docs.spring.io/spring/docs/5.2.2.RELEASE/spring-framework-reference/integration.html#cache-annotations-cacheable-default-key">springCache文档8.2.1 Available Caching SpEL Evaluation Context</>
     *       2）、指定缓存数据的过期时间
     *       3）、将数据value保存为json格式
     *          @see CacheAutoConfiguration -> 通过方法
     *             @see CacheAutoConfiguration.CacheConfigurationImportSelector 调用导入 ->
     *               @see org.springframework.boot.autoconfigure.cache.CacheConfigurations#getConfigurationClass(org.springframework.boot.autoconfigure.cache.CacheType)  导入
     *                 @see org.springframework.boot.autoconfigure.cache.CacheConfigurations#MAPPINGS 加入
     *                  @see org.springframework.boot.autoconfigure.cache.RedisCacheConfiguration
     *                     @see org.springframework.boot.autoconfigure.cache.RedisCacheConfiguration#cacheManager(org.springframework.boot.autoconfigure.cache.CacheProperties, org.springframework.boot.autoconfigure.cache.CacheManagerCustomizers, org.springframework.beans.factory.ObjectProvider, org.springframework.beans.factory.ObjectProvider, org.springframework.data.redis.connection.RedisConnectionFactory, org.springframework.core.io.ResourceLoader)
     *                       @see org.springframework.boot.autoconfigure.cache.RedisCacheConfiguration#determineConfiguration(org.springframework.boot.autoconfigure.cache.CacheProperties, org.springframework.beans.factory.ObjectProvider, java.lang.ClassLoader)
     *
     *
     *
     *  @see org.springframework.data.redis.cache.RedisCacheConfiguration#defaultCacheConfig() 默认配置
     *  @see org.springframework.data.redis.cache.RedisCacheConfiguration#keySerializationPair  默认key的序列化
     *  @see org.springframework.data.redis.cache.RedisCacheConfiguration#valueSerializationPair  默认value的序列化
     *
     *
     *
     *  缓存失效
     * @see CategoryServiceImpl#updateCascade(CategoryEntity) 的注解
     * @CacheEvict(value = {"category"}, key = "'getLevel1Categorys'")
     *
     */
    //
    @Cacheable(value = {"category"}, key = "#root.method.name", sync = true)
    @Override
    public List<CategoryEntity> getLevel1Categorys() {
      return list(new QueryWrapper<CategoryEntity>().eq("parent_cid", 0));
    }


    @Cacheable(value = "category" , key = "#root.methodName")
    @Override
    public Map<String, List<Catelog2Vo>> getCatelogJson() {
        return getCatelogJsonFromDb();
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
    public Map<String, List<Catelog2Vo>> getCatelogJson2() {
        // 1.先从缓存中获取
        String catelogJson = stringRedisTemplate.opsForValue().get("catelogJson");
        // 2.缓存中没有就查询数据库并放入缓存
        if (StringUtils.isEmpty(catelogJson)) {
            // 加分布式锁的情况下查询数据库
            Map<String, List<Catelog2Vo>> catelogJsonFromDb = getCatalogJsonFromDBWithRedissonLockAndSetToRedis();

            // 我觉得这段也应该放在锁的环境下
            /*String jsonString = JSON.toJSONString(catelogJsonFromDb);
            // 设置自动失效时间，保证数据的最终一致性
            stringRedisTemplate.opsForValue().setIfAbsent("catelogJson", jsonString, 5, TimeUnit.MINUTES);*/
            return catelogJsonFromDb;
        }

        log.info("查询catelogJson命中缓存");
        Map<String, List<Catelog2Vo>> stringListMap = JSON.parseObject(catelogJson, new TypeReference<Map<String, List<Catelog2Vo>>>() {
        });
        return stringListMap;
    }

    /**
     * 查询前台需要显示的分类数据 - 本地锁(本地单机可以锁住)
     *
     * @return
     */
    public Map<String, List<Catelog2Vo>> getCatalogJsonFromDBWithLocalLock() {

        /**
         *
         * 本地锁 synchronized 进程锁 锁不住分布式的服务
         * 只要是同一把锁，就可以锁住需要这个锁的所有线程
         * 1、synchronized (this) springboot所有的组件在容器中都是单例的
         *
         */

        synchronized (this) {
            return getCatelogJsonFromDb();
        }


    }

    /**
     *
     * 查询前台需要显示的分类数据 - redisson分布式锁(分布式也可以锁住)
     * @see com.sherlock.gmall.product.service.impl.CategoryServiceImpl#updateCascade(com.sherlock.gmall.product.entity.CategoryEntity) 更新列表
     *
     *
     *
     * 缓存数据如何与数据库保持一致
     * 缓存数据的一致性
     *
     *  1）、双写模式
     *  2）、失效模式
     *
     *（1、可能产生脏数据，并发写加锁，而且给数据根据业务加过期时间()所以最终会数据库和缓存一致）分布式读写锁
     *（2、使用阿里的canal订阅binlog日志，根据日志最终更新缓存，类似于数据库的主从数据库）
     * @return
     */
    public Map<String, List<Catelog2Vo>> getCatalogJsonFromDBWithRedissonLockAndSetToRedis() {

        // 锁的名字相同代表是同一把锁，所以锁的名字影响锁的粒度
        // 具体商品加具体的锁
        RLock lock = redissonClient.getLock("CatelogJson-lock");
        lock.lock();

        Map<String, List<Catelog2Vo>> dataFromDb = null;
        try {
            dataFromDb = getCatelogJsonFromDb();

            String jsonString = JSON.toJSONString(dataFromDb);
            // 设置自动失效时间，保证数据的最终一致性
            stringRedisTemplate.opsForValue().setIfAbsent("catelogJson", jsonString, 5, TimeUnit.MINUTES);
        }finally {
            lock.unlock();
        }
        return dataFromDb;
    }

    /**
     * 从数据库中查询菜单
     * @return
     */
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
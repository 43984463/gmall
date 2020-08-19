## git小操作
```text
git pull origin master --allow-unrelated-histories
git拉取master分支的提交
```

## 设置项目启动时的虚拟机占用内存参数
-Xms256m -Xmx512m #可以直接粘入idea的Environment 的 VM options
* -Xms 为jvm启动时分配的内存，比如-Xms200m，表示分配200M
* -Xmx 为jvm运行过程中分配的最大内存，比如-Xms500m，表示jvm进程最多只能够占用500M内存
* -Xss 为jvm启动的每个线程分配的内存大小，默认JDK1.4中是256K，JDK1.5+中是1M


## 项目大,build的时候一直显示writing classes最终OOM
```text
① 在File -> setting... -> build,Execution,Deployment -> Complier -> build process heap size 中修改编译时的内存大小。
② 在Help -> Edit Customer VM Options... 增大-Xms和-Xmx的值
```

## 远程调用都是通过json格式传输对象，接收时需要@RequestBody

## 技术目录
```text
线程池 + CompletableFuture：com.sherlock.gmall.search.thread.ThreadTest 

redis缓存技术 + 分布式锁 + springCache + redission：
com.sherlock.gmall.product.GmallProductApplication
com.sherlock.gmall.product.service.impl.CategoryServiceImpl
com.sherlock.gmall.product.config.SpringCacheConfig
com.sherlock.gmall.product.web.IndexController

MyBatis-Plus:
com.sherlock.gmall.product.GmallProductApplication
com.sherlock.gmall.coupon.config.MybatisConfig

JSR303数据校验：
com.sherlock.gmall.product.GmallProductApplication
com.sherlock.gmall.product.entity.BrandEntity

阿里OSS存储技术：
com.sherlock.gmall.thirdparty.controller.OssController
阿里短信发送业务：
com.sherlock.gmall.thirdparty.controller.OssController

elasticsearch存储东西到服务器：
com.sherlock.gmall.search.service.impl.ProductSaveServiceImpl

elasticsearch从服务器搜索东西：
com.sherlock.gmall.search.service.impl.MallSearchServiceImpl

springCloud Feign远程调用: 
com.sherlock.gmall.ware.feign.ProductFeignService

mybatis XML写法
mapper/product/AttrGroupDao.xml

自定义线程池
com.sherlock.gmall.product.config.MyThreadConfig

使用线程池 + CompletableFuture异步编排进行异步任务执行
com.sherlock.gmall.product.service.impl.SkuInfoServiceImpl.item()

线程池状态监控
com.sherlock.common.utils.ThreadPoolMonitor

springMVC 请求直接映射视图(页面)
com.sherlock.gmall.auth.controller.LoginController
com.sherlock.gmall.auth.config.GmallWebConfig

spring的MD5盐值加密
com.sherlock.gmall.member.GmallMemberApplicationTests.contextLoads

网关gateway统一配置跨域
com.sherlock.gmall.gateway.config.GmallCorsConfig#corsWebFilter()

```
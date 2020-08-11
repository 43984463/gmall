# 采坑记录
## [https://docs.spring.io/spring/docs/] spring所有版本的文档地址
## [https://docs.spring.io/spring/docs/current/spring-framework-reference/] spring当前最新版本文档目录
## [https://www.cs.usfca.edu/~galles/visualization/Algorithms.html] 数据结构图形化演示地址
## [https://www.elastic.co/guide/en/elasticsearch/client/java-rest/current/java-rest-high-search.html] Elasticsearch与java API文档地址
## [https://www.elastic.co/guide/en/elasticsearch/reference/current/search.html] Elasticsearch在kibana中的操作API文档地址
## [https://github.com/medcl/elasticsearch-analysis-ik] Elasticsearch的中文文档github地址

## tips:
* 本地访问gmall-product时把/resources/templates/index2.html替换掉index.html
```properties
spring.thymeleaf.cache=false #暂时关闭thymeleaf的缓存
swagger.enable=false #暂时关闭swagger文档
```

## mybatisPlus配置文件采坑
```text
https://www.bilibili.com/video/BV1np4y1C7Yf?p=18 照着视频处理时在测试时发现会抛出
org.springframework.beans.factory.UnsatisfiedDependencyException: Error creating bean with name 'attrAttrgroupRelationController': Unsatisfied dependency expressed through field 'attrAttrgroupRelationService'; nested exception is org.springframework.beans.factory.UnsatisfiedDependencyException: Error creating bean with name 'attrAttrgroupRelationService': Unsatisfied dependency expressed through field 'baseMapper'; nested exception is org.springframework.beans.factory.UnsatisfiedDependencyException: Error creating bean with name 'attrAttrgroupRelationDao' defined in file [G:\Projects\gmall\gmall-product\target\classes\com\sherlock\gmall\product\dao\AttrAttrgroupRelationDao.class]: Unsatisfied dependency expressed through bean property 'sqlSessionFactory'; nested exception is org.springframework.beans.factory.BeanCreationException: Error creating bean with name 'sqlSessionFactory' defined in class path resource [com/baomidou/mybatisplus/autoconfigure/MybatisPlusAutoConfiguration.class]: Bean instantiation via factory method failed; nested exception is org.springframework.beans.BeanInstantiationException: Failed to instantiate [org.apache.ibatis.session.SqlSessionFactory]: Factory method 'sqlSessionFactory' threw exception; nested exception is java.io.FileNotFoundException: class path resource [mapper/**/*.xml] cannot be opened because it does not exist 异常
处理方法: 在application.yml中将mybatis-plus.config-location修改成为mybatis-plus.config-locations。

Durid配置参考Url：https://github.com/alibaba/druid/tree/master/druid-spring-boot-starter

alibaba的config配置中心结合@RefreshScope使用动态读取Nacos配置中的信息。
```

## Elasticsearch查询错误解决办法
```text
Elasticsearch exception [type=illegal_argument_exception, reason=Fielddata is disabled on text fields by default. Set fielddata=true on [brandName] in order to load fielddata in memory by uninverting the inverted index. Note that this can however use significant memory. Alternatively use a keyword field instead

解决办法： 将type为text的字段添加keyword并且查询时添加.keyword
          参考com.sherlock.gmall.search.service.impl.MallSearchServiceImpl.buildSearchRequest
```

## 配置文件
```properties
springcloud读取Nacos配置中心配置
spring.cloud.nacos.discovery.server-addr=120.76.60.77:9848  #Nacos服务器地址
spring.cloud.nacos.config.server-addr=120.76.60.77:9848    #Nacos配置文件地址
spring.cloud.nacos.config.extension-configs[0].data-id=data_source.yaml  #Nacos配置文件名称(数组形式)
spring.cloud.nacos.config.extension-configs[0].group=dev    #Nacos配置文件所属组
# Nacos配置文件是否动态刷新
spring.cloud.nacos.config.extension-configs[0].refresh=true
spring.cloud.nacos.config.extension-configs[1].data-id=mybatis_plus.yaml
spring.cloud.nacos.config.extension-configs[1].group=dev 
spring.cloud.nacos.config.extension-configs[1].refresh=true 

mybatis-plus.config-locations=classpath:/mapper/**/*.xml
#所有的数据库主键自增
mybatis-plus.global-config.db-config.id-type=auto
```

```yaml
spring:
  datasource:
    username: root
    password: sherlock
    url: jdbc:mysql://120.76.60.77:2061/gmall_sms?characterEncoding=utf-8&useSSL=false
    driver-class-name: com.mysql.cj.jdbc.Driver
    ###################以下为druid增加的配置###########################
    # 合并多个DruidDataSource的监控数据
    #spring.datasource.useGlobalDataSourceStat=true
    type: com.alibaba.druid.pool.DruidDataSource

    druid:
      # 配置监控统计拦截的servlet
      stat-view-servlet:
        login-username: admin
        login-password: admin
        url-pattern: /druid/*
        enabled: true # 是否启用StatViewServlet（监控页面）默认值为true（考虑到安全问题默认并未启动，如需启用建议设置密码或白名单以保障安全）
        # allow: 允许访问的白名单
        # deny:  拒绝访问的黑名单

      # 配置监控统计拦截的filters
      web-stat-filter:
        url-pattern: /*
        exclusions: "*.js,*.gif,*.jpg,*.bmp,*.png,*.css,*.ico,/druid/*"

      initialSize: 8 #初始化连接大小
      minIdle: 8  #最小连接池数量
      maxActive: 20 #最大连接池数量
      max-wait: 6000 #获取连接时最大等待时间，单位毫秒
      time-between-connect-error-millis: 28820 #配置间隔多久才进行一次检测，检测需要关闭的空闲连接，单位是毫秒
      ###[ERROR]-->minEvictableIdleTimeMillis should be greater than 30000###
      minEvictableIdleTimeMillis: 30000  #配置一个连接在池中最小生存的时间，单位是毫秒
      validationQuery: SELECT 1 FROM DUAL  #测试连接
      testWhileIdle: true   #申请连接的时候检测，建议配置为true，不影响性能，并且保证安全性
      testOnBorrow: false #获取连接时执行检测，建议关闭，影响性能
      testOnReturn: false #归还连接时执行检测，建议关闭，影响性能
      poolPreparedStatements: false #是否开启PSCache，PSCache对支持游标的数据库性能提升巨大，oracle建议开启，mysql下建议关闭
      maxPoolPreparedStatementPerConnectionSize: 20 #开启poolPreparedStatements后生效
      filters: stat #配置扩展插件，常用的插件有=>stat:监控统计  log4j:日志  wall:防御sql注入
      connectionProperties: 'druid.stat.mergeSql=true;druid.stat.slowSqlMillis=5000' #通过connectProperties属性来打开mergeSql功能;慢SQL记录
      query-timeout: 10000 #查询超时时间
      # default-transaction-isolation: #默认隔离级别
      
      
mybatis-plus:
#mybatis配置文件地址 按照提示书写(mybatis-plus.config-location可能报错)
  config-locations: classpath:/mapper/**/*.xml
  global-config:
    db-config:
      id-type: auto

```
## 在创建gateway时一直报错说servlet问题，修改方法为修改
<dependency>
    <groupId>javax.servlet</groupId>
    <artifactId>servlet-api</artifactId>
    <scope>provided</scope>  --->  <scope>compile</scope>
</dependency>

使用阿里云的OSS之后需要把在gmall-common中的
```xml
        <dependency>
            <groupId>com.alibaba.cloud</groupId>
            <artifactId>spring-cloud-starter-alicloud-oss</artifactId>
        </dependency>
```
依赖转移到gmall-third-party中,否则会在启动时报找不到endpoint错误。

# 解决远程调用时需要强制转换返回值类型
* 在设计公共返回对象R时应该加上泛型限制，可以在远程调用获取结果时不需要强制转换。
```java
public class R<T> extends HashMap<String, Object> {
    private T data;
    
    public T getData() {
        return data;
    }

    public R<T> setData(T data) {
        this.data = data;
        return this;
    }
}
```
* 返回时直接返回需要的对象而不是R类型，在远程调用接口也是使用需要的对象。
```java
@FeignClient("gmall-ware")
public interface WareFeignService {

    @PostMapping("/ware/waresku/hasstock")
    List<SkuHasStockVo> getSkuHasStock(@RequestBody List<Long> skuIds);
};


@RestController
@GmallMapping("ware/waresku")
public class WareSkuController {

    @Autowired
    private WareSkuService wareSkuService;

    @PostMapping("/hasstock")
    public List<SkuHasStockVo> getSkuHasStock(@RequestBody List<Long> skuIds) {
        return wareSkuService.getSkuHasStock(skuIds);
    }
}
```
* <b>或者直接返回org.springframework.http.ResponseEntity(这个对象封装了数据和状态码)<b>

## spring-Cache的不足
```text
1）、读模式：
     缓存穿透： 查询一个null数据。  解决：缓存空数据。  spring.cache.redis.cache-null-values: true
     缓存击穿： 大量并发进来同时查询一个正好过期的数据。 解决：加锁  spring-Cache默认未加锁
     org.springframework.cache.annotation.Cacheable.sync = true
     @Cacheable(value = {"category"}, key = "#root.method.name", sync = true)
     使用sync = true加锁(源码中为加的本地锁)
     缓存雪崩： 大量key同时过期。  解决：加随机时间的过期时间。 spring.cache.redis.time-to-live
```  
```java
    
    public class RedisCache extends AbstractValueAdaptingCache {
    
        /**
        * 源码中 springCache + redis
        * 使用单机锁从缓存中取值
        * @param key
        * @param valueLoader
        * @param <T>
        * @return 
        */
        @Override
        @SuppressWarnings("unchecked")
        public synchronized <T> T get(Object key, Callable<T> valueLoader) {
    
            ValueWrapper result = get(key);
    
            if (result != null) {
                return (T) result.get();
            }
    
            T value = valueFromLoader(key, valueLoader);
            put(key, value);
            return value;
        }
        
    }
```     
```text
  2）、写模式：（缓存与数据库一致）
     1）、读写加锁。(加读写锁)
     2）、引入阿里的canal，感知mysql的更新去更新数据库
     3）、读多写多，直接去数据库查（加锁更加耗时，加入缓存效果不大）   

  总结:
    常规数据(读多写少，即时性，一致性要求不高的数据); 完全可以使用Spring-Cache;  写模式（只要缓存的数据有过期时间就足够）
    特殊数据： 特殊处理
```
    
## spring-Cache的原理    
  CacheManager(RedisCacheManager) -> Cache(RedisCache) ->Cache进行操作
  
## nginx配置
### 请求的原理
```text
①. 本地请求gmall.com，由于在host文件中配置了转发地址到虚拟机。
②. 虚拟机中安装了nginx, nginx的gmall.conf默认监听端口80端口以及域名为gmall.com的请求。
③. gmall.conf中的location / 节点的proxy_pass属性 把符合gmall.com域名80端口的请求都转发给总配置nginx.conf的
proxy_pass http://gmall; 中的upstream节点。 proxy_pass http://gmall;中的gmall为nginx.conf的upstream节点的server属性。
④. 总配置nginx.conf的upstream节点中的server属性再次转发请求到具体地址(即本机上的88端口(本机上的网关。))
⑤. 网关将请求转到具体的html或者controller获得相应。
(gmall.com 这步最终匹配到 IndexController的indexPage方法，因为没有加@responseBody，则返回的是一个页面的名称
配合默认的spring.mvc.view.prefix 和 spring.mvc.view.prefix组成 index.html
)


nginx在转发时会丢掉信息
①. Host地址丢失 需要在gmall.conf的location / 节点配置 proxy_set_header Host $host;
这个host匹配的是网关中的
predicates:
      - Host=gmall.com
或者
predicates:
      - Host=search.gmall.com

网关gateway关于
        - id: gmall_host_route
          uri: lb://gmall-product #转到商品服务
          predicates:
            - Host=gmall.com
需要放在最后。不然首页可以访问，但是在访问API接口时，接口中包含gmall.com
上面的首先匹配到进行了转发，会导致找不到接口，所以需要放在最后
```
### gmall.conf
```text
server {
    listen       80;
    server_name  gmall.com *.gmall.com;    # 监听*.gmall.com和gmall.com的请求
    large_client_header_buffers 4 16k;     # 读取大型客户端请求头的缓冲区的最大数量和大小
    client_max_body_size 300m;             # 设置nginx能处理的最大请求主体大小。
    client_body_buffer_size 128k;          # 请求主体的缓冲区大小。 
    proxy_connect_timeout 600;
    proxy_read_timeout 600;
    proxy_send_timeout 600;
    proxy_buffer_size 64k;
    proxy_buffers   4 32k;
    proxy_busy_buffers_size 64k;
    proxy_temp_file_write_size 64k;

    #charset koi8-r;
    #access_log  /var/log/nginx/host.access.log  main;

    # 拦截所有路径包含 /static/ 的请求，并在 /usr/share/nginx/html 目录寻找资源。（root属性）
    location /static/ {
       root   /usr/share/nginx/html;
    }

    # 拦截所有请求，并转发请求（proxy_pass属性）
    # 配置请求要转到哪里和超时时间
    location / {
     uwsgi_send_timeout 600;                # 指定向uWSGI传送请求的超时时间，完成握手后向uWSGI传送请求的超时时间。
     uwsgi_connect_timeout 600;             # 指定连接到后端uWSGI的超时时间。
     uwsgi_read_timeout 600;                # 指定接收uWSGI应答的超时时间，完成握手后接收uWSGI应答的超时时间。
     proxy_set_header Host $host;           # nginx代理网关的时候会丢失请求的host信息，需要在此处重新设置。
     # proxy_pass http://192.168.1.3:1301;  # 在nginx.conf还没有配置upstream节点时使用，直接转到对应地址请求
     proxy_pass http://gmall;               # 在nginx.conf还配置upstream节点后使用，gmall为在nginx.conf中配置upstream节点的名称
    }

    #error_page  404              /404.html;

    # redirect server error pages to the static page /50x.html
    #
    error_page   500 502 503 504  /50x.html;
    location = /50x.html {
        root   /usr/share/nginx/html;
    }

    # proxy the PHP scripts to Apache listening on 127.0.0.1:80
    #
    #location ~ \.php$ {
    #    proxy_pass   http://127.0.0.1;
    #}

    # pass the PHP scripts to FastCGI server listening on 127.0.0.1:9000
    #
    #location ~ \.php$ {
    #    root           html;
    #    fastcgi_pass   127.0.0.1:9000;
    #    fastcgi_index  index.php;
    #    fastcgi_param  SCRIPT_FILENAME  /scripts$fastcgi_script_name;
    #    include        fastcgi_params;
    #}

    # deny access to .htaccess files, if Apache's document root
    # concurs with nginx's one
    #
    #location ~ /\.ht {
    #    deny  all;
    #}
 }
```
### nginx.conf
```text
user  nginx;
worker_processes  1;

error_log  /var/log/nginx/error.log warn;
pid        /var/run/nginx.pid;


events {
    worker_connections  1024;
}


http {
    include       /etc/nginx/mime.types;
    default_type  application/octet-stream;

    log_format  main  '$remote_addr - $remote_user [$time_local] "$request" '
                      '$status $body_bytes_sent "$http_referer" '
                      '"$http_user_agent" "$http_x_forwarded_for"';

    access_log  /var/log/nginx/access.log  main;

    sendfile        on;
    #tcp_nopush     on;

    keepalive_timeout  65;

    #gzip  on;
    
    # 配置名称为gmall的upstream，下游配置可以使用这个进行请求的转发
    upstream gmall{
        server 192.168.1.3:88;
        }

    include /etc/nginx/conf.d/*.conf;
}

```

## 多线程实现方法与特点

```text

1)、继承Thread
2)、实现Runable接口
3)、实现Callable接口 + FutureTask(可以拿到返回值，或者异常)
4)、使用线程池

区别：
  1、2不能拿到返回值。3可以获取返回值。
  1、2、3都不能控制资源。(不能资源重复利用，每个都需要开新的线程)。
  4可以控制资源。(性能稳定，不会因为创建线程导致系统资源耗尽)。

```
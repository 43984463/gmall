#采坑记录
##mybatisPlus配置文件采坑
https://www.bilibili.com/video/BV1np4y1C7Yf?p=18 照着视频处理时在测试时发现会抛出
org.springframework.beans.factory.UnsatisfiedDependencyException: Error creating bean with name 'attrAttrgroupRelationController': Unsatisfied dependency expressed through field 'attrAttrgroupRelationService'; nested exception is org.springframework.beans.factory.UnsatisfiedDependencyException: Error creating bean with name 'attrAttrgroupRelationService': Unsatisfied dependency expressed through field 'baseMapper'; nested exception is org.springframework.beans.factory.UnsatisfiedDependencyException: Error creating bean with name 'attrAttrgroupRelationDao' defined in file [G:\Projects\gmall\gmall-product\target\classes\com\sherlock\gmall\product\dao\AttrAttrgroupRelationDao.class]: Unsatisfied dependency expressed through bean property 'sqlSessionFactory'; nested exception is org.springframework.beans.factory.BeanCreationException: Error creating bean with name 'sqlSessionFactory' defined in class path resource [com/baomidou/mybatisplus/autoconfigure/MybatisPlusAutoConfiguration.class]: Bean instantiation via factory method failed; nested exception is org.springframework.beans.BeanInstantiationException: Failed to instantiate [org.apache.ibatis.session.SqlSessionFactory]: Factory method 'sqlSessionFactory' threw exception; nested exception is java.io.FileNotFoundException: class path resource [mapper/**/*.xml] cannot be opened because it does not exist 异常
处理方法: 在application.yml中将mybatis-plus.config-location修改成为mybatis-plus.config-locations。

Durid配置参考Url：https://github.com/alibaba/druid/tree/master/druid-spring-boot-starter

alibaba的config配置中心结合@RefreshScope使用动态读取Nacos配置中的信息。

```properties
springcloud读取Nacos配置中心配置
spring.cloud.nacos.config.server-addr=120.76.60.77:9848    #Nacos配置文件地址
spring.cloud.nacos.discovery.server-addr=120.76.60.77:9848  #Nacos服务器地址
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
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

## 远程调用都是通过json格式传输对象，接收时需要在参数位置添加@RequestBody

```java
org.thymeleaf.exceptions.TemplateInputException: Error resolving template [currentUserCartItems], template might not exist or might not be accessible by any of the configured Template Resolvers
	at org.thymeleaf.engine.TemplateManager.resolveTemplate(TemplateManager.java:869) ~[thymeleaf-3.0.11.RELEASE.jar:3.0.11.RELEASE]
	at org.thymeleaf.engine.TemplateManager.parseAndProcess(TemplateManager.java:607) ~[thymeleaf-3.0.11.RELEASE.jar:3.0.11.RELEASE]
	at org.thymeleaf.TemplateEngine.process(TemplateEngine.java:1098) [thymeleaf-3.0.11.RELEASE.jar:3.0.11.RELEASE]
	at org.thymeleaf.TemplateEngine.process(TemplateEngine.java:1072) [thymeleaf-3.0.11.RELEASE.jar:3.0.11.RELEASE]
	at org.thymeleaf.spring5.view.ThymeleafView.renderFragment(ThymeleafView.java:362) [thymeleaf-spring5-3.0.11.RELEASE.jar:3.0.11.RELEASE]
	at org.thymeleaf.spring5.view.ThymeleafView.render(ThymeleafView.java:189) [thymeleaf-spring5-3.0.11.RELEASE.jar:3.0.11.RELEASE]
	at org.springframework.web.servlet.DispatcherServlet.render(DispatcherServlet.java:1373) [spring-webmvc-5.2.2.RELEASE.jar:5.2.2.RELEASE]
	at org.springframework.web.servlet.DispatcherServlet.processDispatchResult(DispatcherServlet.java:1118) [spring-webmvc-5.2.2.RELEASE.jar:5.2.2.RELEASE]
	at org.springframework.web.servlet.DispatcherServlet.doDispatch(DispatcherServlet.java:1057) [spring-webmvc-5.2.2.RELEASE.jar:5.2.2.RELEASE]
	at org.springframework.web.servlet.DispatcherServlet.doService(DispatcherServlet.java:943) [spring-webmvc-5.2.2.RELEASE.jar:5.2.2.RELEASE]
	at org.springframework.web.servlet.FrameworkServlet.processRequest(FrameworkServlet.java:1006) [spring-webmvc-5.2.2.RELEASE.jar:5.2.2.RELEASE]
	at org.springframework.web.servlet.FrameworkServlet.doGet(FrameworkServlet.java:898) [spring-webmvc-5.2.2.RELEASE.jar:5.2.2.RELEASE]
	at javax.servlet.http.HttpServlet.service(HttpServlet.java:634) [tomcat-embed-core-9.0.29.jar:9.0.29]
	at org.springframework.web.servlet.FrameworkServlet.service(FrameworkServlet.java:883) [spring-webmvc-5.2.2.RELEASE.jar:5.2.2.RELEASE]
	at javax.servlet.http.HttpServlet.service(HttpServlet.java:741) [tomcat-embed-core-9.0.29.jar:9.0.29]
	at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:231) [tomcat-embed-core-9.0.29.jar:9.0.29]
	at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:166) [tomcat-embed-core-9.0.29.jar:9.0.29]
	at org.apache.tomcat.websocket.server.WsFilter.doFilter(WsFilter.java:53) [tomcat-embed-websocket-9.0.29.jar:9.0.29]
	at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:193) [tomcat-embed-core-9.0.29.jar:9.0.29]
	at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:166) [tomcat-embed-core-9.0.29.jar:9.0.29]
	at org.springframework.web.filter.RequestContextFilter.doFilterInternal(RequestContextFilter.java:100) [spring-web-5.2.2.RELEASE.jar:5.2.2.RELEASE]
	at org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:119) [spring-web-5.2.2.RELEASE.jar:5.2.2.RELEASE]
	at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:193) [tomcat-embed-core-9.0.29.jar:9.0.29]
	at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:166) [tomcat-embed-core-9.0.29.jar:9.0.29]
	at org.springframework.web.filter.FormContentFilter.doFilterInternal(FormContentFilter.java:93) [spring-web-5.2.2.RELEASE.jar:5.2.2.RELEASE]
	at org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:119) [spring-web-5.2.2.RELEASE.jar:5.2.2.RELEASE]
	at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:193) [tomcat-embed-core-9.0.29.jar:9.0.29]
	at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:166) [tomcat-embed-core-9.0.29.jar:9.0.29]
	at org.springframework.session.web.http.SessionRepositoryFilter.doFilterInternal(SessionRepositoryFilter.java:141) [spring-session-core-2.2.0.RELEASE.jar:2.2.0.RELEASE]
	at org.springframework.session.web.http.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:82) [spring-session-core-2.2.0.RELEASE.jar:2.2.0.RELEASE]
	at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:193) [tomcat-embed-core-9.0.29.jar:9.0.29]
	at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:166) [tomcat-embed-core-9.0.29.jar:9.0.29]
	at org.springframework.web.filter.CharacterEncodingFilter.doFilterInternal(CharacterEncodingFilter.java:201) [spring-web-5.2.2.RELEASE.jar:5.2.2.RELEASE]
	at org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:119) [spring-web-5.2.2.RELEASE.jar:5.2.2.RELEASE]
	at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:193) [tomcat-embed-core-9.0.29.jar:9.0.29]
	at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:166) [tomcat-embed-core-9.0.29.jar:9.0.29]
	at org.apache.catalina.core.StandardWrapperValve.invoke(StandardWrapperValve.java:202) [tomcat-embed-core-9.0.29.jar:9.0.29]
	at org.apache.catalina.core.StandardContextValve.invoke(StandardContextValve.java:96) [tomcat-embed-core-9.0.29.jar:9.0.29]
	at org.apache.catalina.authenticator.AuthenticatorBase.invoke(AuthenticatorBase.java:526) [tomcat-embed-core-9.0.29.jar:9.0.29]
	at org.apache.catalina.core.StandardHostValve.invoke(StandardHostValve.java:139) [tomcat-embed-core-9.0.29.jar:9.0.29]
	at org.apache.catalina.valves.ErrorReportValve.invoke(ErrorReportValve.java:92) [tomcat-embed-core-9.0.29.jar:9.0.29]
	at org.apache.catalina.core.StandardEngineValve.invoke(StandardEngineValve.java:74) [tomcat-embed-core-9.0.29.jar:9.0.29]
	at org.apache.catalina.connector.CoyoteAdapter.service(CoyoteAdapter.java:343) [tomcat-embed-core-9.0.29.jar:9.0.29]
	at org.apache.coyote.http11.Http11Processor.service(Http11Processor.java:367) [tomcat-embed-core-9.0.29.jar:9.0.29]
	at org.apache.coyote.AbstractProcessorLight.process(AbstractProcessorLight.java:65) [tomcat-embed-core-9.0.29.jar:9.0.29]
	at org.apache.coyote.AbstractProtocol$ConnectionHandler.process(AbstractProtocol.java:860) [tomcat-embed-core-9.0.29.jar:9.0.29]
	at org.apache.tomcat.util.net.NioEndpoint$SocketProcessor.doRun(NioEndpoint.java:1591) [tomcat-embed-core-9.0.29.jar:9.0.29]
	at org.apache.tomcat.util.net.SocketProcessorBase.run(SocketProcessorBase.java:49) [tomcat-embed-core-9.0.29.jar:9.0.29]
	at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1149) [na:1.8.0_171]
	at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:624) [na:1.8.0_171]
	at org.apache.tomcat.util.threads.TaskThread$WrappingRunnable.run(TaskThread.java:61) [tomcat-embed-core-9.0.29.jar:9.0.29]
	at java.lang.Thread.run(Thread.java:748) [na:1.8.0_171]
```
* 远程调用以json格式传递数据 *
```text
在确认购物车时发生错误，错误原因就是因为返回的不是json数据
com.sherlock.gmall.cart.controller.CartController#getCurrentUserCartItems在远程调用的时候没有标注@ResponseBody或者@RestController将json数据返回。
```


## 微博获取oauth2登录

![Image text](https://gmall-sherlock.oss-cn-beijing.aliyuncs.com/oauth2.png)

```text
第一步通过访问
https://api.weibo.com/oauth2/authorize?client_id=1509412573&response_type=code&redirect_uri=http://auth.gmall.com/oauth2.0/weibo/success
授权之后返回到成功页面+一个code(URL中)
http://auth.gmall.com/oauth2.0/weibo/success?code=85091825e0c37b6481f18b86fd30a8c2
然后访问获取access_token + uid
https://api.weibo.com/oauth2/access_token?client_id=1509412573&client_secret=c08bbf7a9174e5f218f8125f997ed2bd&grant_type=authorization_code&redirect_uri=http://auth.gmall.com/oauth2.0/weibo/success&code=85091825e0c37b6481f18b86fd30a8c2


{
    "access_token": "2.00suH9jFFK2JeB113648cb43Ld6PDD",
    "remind_in": "157679999",
    "expires_in": 157679999,
    "uid": "5252302898",
    "isRealName": "true"
}

获取到access_token + uid就可以参考https://open.weibo.com/apps/1509412573/privilege的接口去访问用户信息

获取微博用户信息：https://api.weibo.com/2/users/show.json?access_token=2.00suH9jFFK2JeB113648cb43Ld6PDD&uid=5252302898

返回结果

{
    "id": 5252302898,
    "idstr": "5252302898",
    "class": 1,
    "screen_name": "落叶10206",
    "name": "落叶10206",
    "province": "61",
    "city": "1",
    "location": "陕西 西安",
    "description": "",
    "url": "",
    "profile_image_url": "https://tvax2.sinaimg.cn/crop.0.0.1002.1002.50/005Js7USly8fn3hmnajurj30ru0ru0yg.jpg?KID=imgbed,tva&Expires=1597948589&ssig=kzF0kRf9ir",
    "cover_image_phone": "http://ww1.sinaimg.cn/crop.0.0.640.640.640/549d0121tw1egm1kjly3jj20hs0hsq4f.jpg",
    "profile_url": "u/5252302898",
    "domain": "",
    "weihao": "",
    "gender": "m",
    "followers_count": 5,
    "friends_count": 40,
    "pagefriends_count": 4,
    "statuses_count": 5,
    "video_status_count": 0,
    "video_play_count": 0,
    "favourites_count": 1,
    "created_at": "Wed Aug 13 15:09:58 +0800 2014",
    "following": false,
    "allow_all_act_msg": false,
    "geo_enabled": true,
    "verified": false,
    "verified_type": -1,
    "remark": "",
    "email": "",
    "insecurity": {
        "sexual_content": false
    },
    "status": {
        "visible": {
            "type": 0,
            "list_id": 0
        },
        "created_at": "Mon May 04 14:20:34 +0800 2020",
        "id": 4500864725373683,
        "idstr": "4500864725373683",
        "mid": "4500864725373683",
        "can_edit": false,
        "show_additional_indication": 0,
        "text": "转发微博",
        "source_allowclick": 1,
        "source_type": 1,
        "source": "<a href=\"http://app.weibo.com/t/feed/2ZhgIG\" rel=\"nofollow\">小米10 Pro</a>",
        "favorited": false,
        "truncated": false,
        "in_reply_to_status_id": "",
        "in_reply_to_user_id": "",
        "in_reply_to_screen_name": "",
        "pic_urls": [],
        "geo": null,
        "is_paid": false,
        "mblog_vip_type": 0,
        "annotations": [
            {
                "client_mblogid": "f91ea7a0-b2f5-4ef2-8578-b99c59c4f20a"
            },
            {
                "mapi_request": true
            }
        ],
        "reposts_count": 0,
        "comments_count": 0,
        "attitudes_count": 0,
        "pending_approval_count": 0,
        "isLongText": false,
        "reward_exhibition_type": 0,
        "hide_flag": 0,
        "mlevel": 0,
        "biz_feature": 0,
        "hasActionTypeCard": 0,
        "darwin_tags": [],
        "hot_weibo_tags": [],
        "text_tag_tips": [],
        "mblogtype": 0,
        "rid": "0",
        "userType": 0,
        "more_info_type": 0,
        "positive_recom_flag": 0,
        "content_auth": 0,
        "gif_ids": "",
        "is_show_bulletin": 2,
        "comment_manage_info": {
            "comment_permission_type": -1,
            "approval_comment_type": 0
        },
        "repost_type": 2,
        "pic_num": 0
    },
    "ptype": 0,
    "allow_all_comment": true,
    "avatar_large": "https://tvax2.sinaimg.cn/crop.0.0.1002.1002.180/005Js7USly8fn3hmnajurj30ru0ru0yg.jpg?KID=imgbed,tva&Expires=1597948589&ssig=VsLKRlr2tc",
    "avatar_hd": "https://tvax2.sinaimg.cn/crop.0.0.1002.1002.1024/005Js7USly8fn3hmnajurj30ru0ru0yg.jpg?KID=imgbed,tva&Expires=1597948589&ssig=HnhM2%2Bv0FW",
    "verified_reason": "",
    "verified_trade": "",
    "verified_reason_url": "",
    "verified_source": "",
    "verified_source_url": "",
    "follow_me": false,
    "like": false,
    "like_me": false,
    "online_status": 0,
    "bi_followers_count": 3,
    "lang": "zh-cn",
    "star": 0,
    "mbtype": 0,
    "mbrank": 0,
    "block_word": 0,
    "block_app": 0,
    "credit_score": 80,
    "user_ability": 35651584,
    "urank": 4,
    "story_read_state": -1,
    "vclub_member": 0,
    "is_teenager": 0,
    "is_guardian": 0,
    "is_teenager_list": 0,
    "pc_new": 0,
    "special_follow": false,
    "planet_video": 0,
    "video_mark": 0,
    "live_status": 0
}
```
返回字段说明
```text
返回字段说明
返回值字段	字段类型	字段说明
id	int64	用户UID
idstr	string	字符串型的用户UID
screen_name	string	用户昵称
name	string	友好显示名称
province	int	用户所在省级ID
city	int	用户所在城市ID
location	string	用户所在地
description	string	用户个人描述
url	string	用户博客地址
profile_image_url	string	用户头像地址（中图），50×50像素
profile_url	string	用户的微博统一URL地址
domain	string	用户的个性化域名
weihao	string	用户的微号
gender	string	性别，m：男、f：女、n：未知
followers_count	int	粉丝数
friends_count	int	关注数
statuses_count	int	微博数
favourites_count	int	收藏数
created_at	string	用户创建（注册）时间
following	boolean	暂未支持
allow_all_act_msg	boolean	是否允许所有人给我发私信，true：是，false：否
geo_enabled	boolean	是否允许标识用户的地理位置，true：是，false：否
verified	boolean	是否是微博认证用户，即加V用户，true：是，false：否
verified_type	int	暂未支持
remark	string	用户备注信息，只有在查询用户关系时才返回此字段
status	object	用户的最近一条微博信息字段 详细
allow_all_comment	boolean	是否允许所有人对我的微博进行评论，true：是，false：否
avatar_large	string	用户头像地址（大图），180×180像素
avatar_hd	string	用户头像地址（高清），高清头像原图
verified_reason	string	认证原因
follow_me	boolean	该用户是否关注当前登录用户，true：是，false：否
online_status	int	用户的在线状态，0：不在线、1：在线
bi_followers_count	int	用户的互粉数
lang	string	用户当前的语言版本，zh-cn：简体中文，zh-tw：繁体中文，en：英语
```

## 技术目录
```text
线程池 + CompletableFuture：com.sherlock.gmall.search.thread.ThreadTest 

redis缓存技术 + 分布式锁 + springCache + redission：
com.sherlock.gmall.product.GmallProductApplication
com.sherlock.gmall.product.service.impl.CategoryServiceImpl
com.sherlock.gmall.product.config.SpringCacheConfig
com.sherlock.gmall.product.web.IndexController

redis设置序列化机制
com.sherlock.gmall.auth.config.SpringSessionConfig#springSessionDefaultRedisSerializer()

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

springSession缓存session
com.sherlock.gmall.auth.config.SpringSessionConfig#cookieSerializer()

spring拦截器 + ThreadLocal对每个线程进行处理
com.sherlock.gmall.cart.interceptor.CartInterceptor

springBoot + rabbitMQ
com.sherlock.gmall.order.GmallOrderApplication
com.sherlock.gmall.order.GmallOrderApplicationTests
com.sherlock.gmall.order.component.RabbitReceive
com.sherlock.gmall.order.config.RabbitMQConfig
com.sherlock.gmall.order.controller.RabbitController

普通单线程远程Feign请求丢失请求头等信息解决办法
com.sherlock.gmall.order.config.GmallFeignConfig#requestInterceptor()
异步请求多线程情况下远程Feign请求丢失请求头等信息解决办法
com.sherlock.gmall.order.config.GmallFeignConfig#requestInterceptor()
com.sherlock.gmall.order.service.impl.OrderServiceImpl#confirmOder()
Feign 调用报错 java.io.IOException: too many bytes written
com.sherlock.gmall.order.config.GmallFeignConfig#requestInterceptor()

在spring框架中获取某个线程的请求
com.sherlock.gmall.order.config.GmallFeignConfig#requestInterceptor()
或者参考    RequestContextHolder.class
ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

使用ajax发送请求并且经过网关最后到达真正的请求
gmall/gmall-order/src/main/resources/templates/confirm.html  $(".addr-item p").click(){}

```

## windows查看占用端口的进程
```text
netstat -aon|findstr 80
```
查看占用端口的任务
```text
tasklist|findstr 19796
```
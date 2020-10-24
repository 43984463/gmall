package com.sherlock.gmall.order;

/**
 * @auther Sherlock
 * @date 2020/5/8 21:49
 * @Description: gmall-order
 * <p>
 * 使用rabbitmq
 * 1、引入amqp场景(导入依赖)
 *      <dependency>
 *           <groupId>org.springframework.boot</groupId>
 *           <artifactId>spring-boot-starter-amqp</artifactId>
 *      </dependency>
 *
 * @see org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration 就会自动生效， 给容器中自动配置了
 * @see org.springframework.amqp.rabbit.core.RabbitTemplate
 * @see org.springframework.amqp.core.AmqpAdmin
 * @see org.springframework.amqp.rabbit.connection.CachingConnectionFactory
 * @see org.springframework.amqp.rabbit.core.RabbitMessagingTemplate
 *
 * 2、@EnableRabbit
 * 3、{@link org.springframework.boot.autoconfigure.amqp.RabbitProperties} 有所有关于 rabbitmq 的配置信息
 *
 *
 *  AmqpAdmin 组件可以用来创建交换机，队列，绑定关系等
 *  RabbitTemplate 可以用来发送消息
 *  @RabbitListener 可以用来监听接收消息
 *
 *
 *  本地事务失效问题：
 *  同一个对象内事务方法互调默认失效，原因 绕过了代理对象，事务使用代理对象来控制的
 *
 *  解决： 使用代理对象来调用事务方法
 *  1）、引入
 *          <dependency>
 *             <groupId>org.springframework.boot</groupId>
 *             <artifactId>spring-boot-starter-aop</artifactId>
 *         </dependency>
*       这个包含了 aspectjweaver
 *  2）、@EnableAspectJAutoProxy(exposeProxy = true)  开启aspectj动态代理功能，以后所有的动态代理都是aspectj创建的(即使没有接口也可以创建动态代理)
 *  3）、 用调用对象本类互调
 *  Object o = AopContext.currentProxy(); 再强制转换为本类 然后进行调用
 *
 *
 *
 *
 *  seata控制分布式事务
 *  参考 http://seata.io/zh-cn/docs/user/quickstart.html
 *  1）、没个微服务先必须创建undo_log表，
 *
 *    drop table if exists undo_log;
 *
 *   CREATE TABLE `undo_log` (
 *       `id` bigint(20) NOT NULL AUTO_INCREMENT,
 *       `branch_id` bigint(20) NOT NULL,
 *       `xid` varchar(100) NOT NULL,
 *      `context` varchar(128) NOT NULL,
 *      `rollback_info` longblob NOT NULL,
 *      `log_status` int(11) NOT NULL,
 *      `log_created` datetime NOT NULL,
 *      `log_modified` datetime NOT NULL,
 *      `ext` varchar(100) DEFAULT NULL,
 *       PRIMARY KEY (`id`),
 *      UNIQUE KEY `ux_undo_log` (`xid`,`branch_id`)
 *   ) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;
 *
 *   alter table undo_log comment 'seata日志表';
 *
 *   2）、安装事务协调器
 *   从 https://github.com/seata/seata/releases ,下载服务器软件包，将其解压缩。  我这个是1.0版本 老师的是0.7.1版本
 *
 *   registry.conf :注册中心配置修改 registry type = "nacos"
 *   file.conf
 *
 *   3）、所有想要用到分布式事务的微服务使用seata DataSourceProxy代理自己的数据源
 *   @see com.sherlock.gmall.order.config.MySeataConfig#datasource(org.springframework.boot.autoconfigure.jdbc.DataSourceProperties)
 *
 *   在 org.springframework.cloud:spring-cloud-starter-alibaba-seata
 *   的 org.springframework.cloud.alibaba.seata.GlobalTransactionAutoConfiguration类中，
 *   默认会使用 ${spring.application.name}-fescar-service-group作为服务名注册到 Seata Server上，如果和file.conf中的配置不一致，会提示 no available server to connect错误
 *
 *   也可以通过配置 spring.cloud.alibaba.seata.tx-service-group修改后缀，但是必须和file.conf中的配置保持一致
 *
 *   4）、修改 file.conf 的 vgroup_mapping.my_test_tx_group = "default" 为 ${spring.application.name}-fescar-service-group = "default"
 *   5）、给分布式事务大事务的入口标注 @GlobalTransactional
 *   6）、给每个远程的小事务用spring的@Transactional即可
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *   SpringBoot初始化过程中的扩展点
 *   （一）容器初始化之前
 *
 *      接口方法：
 *      @see org.springframework.context.ApplicationContextInitializer#initialize(org.springframework.context.ConfigurableApplicationContext)
 *
 *      这是整个spring容器在刷新之前初始化ConfigurableApplicationContext的回调接口，
 *      简单来说，就是在容器刷新之前调用此类的initialize方法。这个点允许被用户自己扩展。用户可以在整个spring容器还没被初始化之前做一些事情。
 *      可以想到的场景可能为，在最开始激活一些配置，或者利用这时候class还没被类加载器加载的时机，进行动态字节码注入等操作。
 *
 *
 *      调用点：
 *      @see org.springframework.boot.SpringApplication#run(java.lang.String...) -->
 *           @see org.springframework.boot.SpringApplication#prepareContext(org.springframework.context.ConfigurableApplicationContext, org.springframework.core.env.ConfigurableEnvironment, org.springframework.boot.SpringApplicationRunListeners, org.springframework.boot.ApplicationArguments, org.springframework.boot.Banner) -->
 *               @see org.springframework.boot.SpringApplication#applyInitializers(org.springframework.context.ConfigurableApplicationContext)
 *               initializer.initialize(context);
 *
 *      因为这时候spring容器还没被初始化，所以想要自己的扩展的生效，有以下三种方式：
 *      公共部分： 重写 {@link org.springframework.context.ApplicationContextInitializer#initialize(org.springframework.context.ConfigurableApplicationContext)}
 *           public class TestApplicationContextInitializer implements ApplicationContextInitializer {
 *              @Override
 *              public void initialize(ConfigurableApplicationContext applicationContext) {
 *                  System.out.println("[ApplicationContextInitializer]");
 *              }
 *          }
 *     添加方式一：
 *          在启动类中用springApplication.addInitializers(new TestApplicationContextInitializer())语句加入
 *           @see org.springframework.boot.SpringApplication#addInitializers(org.springframework.context.ApplicationContextInitializer[])
 *
 *           例子：
 *
 *          public class GmallOrderApplication {
 *
 *               public static void main(String[] args) {
 *                   SpringApplication springApplication = new SpringApplication(GmallOrderApplication.class);
 *                   springApplication.addInitializers(new TestApplicationContextInitializer());
 *                   springApplication.run(args);
 *
 *                  // 分解 SpringApplication.run(GmallOrderApplication.class, args);
 *               }
 *
 *          }
 *
 *
 *      添加方式二：
 *        配置文件配置context.initializer.classes=com.example.demo.TestApplicationContextInitializer
 *      添加方式三：
 *        Spring SPI扩展，在spring.factories中加入
 *        org.springframework.context.ApplicationContextInitializer=com.example.demo.TestApplicationContextInitializer
 *
 *        @see org.springframework.core.io.support.SpringFactoriesLoader#FACTORIES_RESOURCE_LOCATION
 *        加载位置：从springboot主启动类开始
 *        @see org.springframework.boot.SpringApplication#SpringApplication(org.springframework.core.io.ResourceLoader, java.lang.Class[])  --> setInitializers((Collection) getSpringFactoriesInstances(ApplicationContextInitializer.class)); -->
 *              @see org.springframework.boot.SpringApplication#getSpringFactoriesInstances(java.lang.Class) -->
 *                   @see org.springframework.boot.SpringApplication#getSpringFactoriesInstances(java.lang.Class, java.lang.Class[], java.lang.Object...) --> Set<String> names = new LinkedHashSet<>(SpringFactoriesLoader.loadFactoryNames(type, classLoader)); -->
 *                       @see org.springframework.core.io.support.SpringFactoriesLoader#loadFactoryNames(java.lang.Class, java.lang.ClassLoader) -->
 *                           @see org.springframework.core.io.support.SpringFactoriesLoader#loadSpringFactories(java.lang.ClassLoader)
 *                           在此处加载 {@link org.springframework.core.io.support.SpringFactoriesLoader#FACTORIES_RESOURCE_LOCATION}文件中的所有类
 *
 *
 *
 *
 *
 *    （二）
 *         接口方法：
 *          @see org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor#postProcessBeanDefinitionRegistry(org.springframework.beans.factory.support.BeanDefinitionRegistry)
 *          容器初始化的时候添加bean定义信息beanDefinition
 *          这个接口在读取项目中的beanDefinition之后执行，提供一个补充的扩展点
 *          使用场景：你可以在这里动态注册自己的beanDefinition，可以加载classpath之外的bean
 *         调用点：
 *         @see org.springframework.boot.SpringApplication#run(java.lang.String...) -->
 *              @see org.springframework.boot.SpringApplication#refreshContext(org.springframework.context.ConfigurableApplicationContext)
 *                   @see org.springframework.boot.SpringApplication#refresh(org.springframework.context.ApplicationContext)
 *                        @see org.springframework.context.support.AbstractApplicationContext#refresh()  --> invokeBeanFactoryPostProcessors(beanFactory);
 *                              @see org.springframework.context.support.AbstractApplicationContext#invokeBeanFactoryPostProcessors(org.springframework.beans.factory.config.ConfigurableListableBeanFactory) -->
 *                                   @see org.springframework.context.support.PostProcessorRegistrationDelegate#invokeBeanFactoryPostProcessors(org.springframework.beans.factory.config.ConfigurableListableBeanFactory, java.util.List)
 *                                   2处使用
 *                                   ①
 *                                   registryProcessor.postProcessBeanDefinitionRegistry(registry);
 *                                   ②
 *                                   @see org.springframework.context.support.PostProcessorRegistrationDelegate#invokeBeanDefinitionRegistryPostProcessors(java.util.Collection, org.springframework.beans.factory.support.BeanDefinitionRegistry)
 *
 *          添加方式:
 *           @Component
 *          public class MyBeanDefinitionRegistryPostProcessor implements BeanDefinitionRegistryPostProcessor{
 *
 *          	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
 *          		System.out.println("MyBeanDefinitionRegistryPostProcessor...bean的数量："+beanFactory.getBeanDefinitionCount());
 *          	}
 *
 *          	//BeanDefinitionRegistry Bean定义信息的保存中心，以后BeanFactory就是按照BeanDefinitionRegistry里面保存的每一个bean定义信息创建bean实例；
 *          	public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
 *          		System.out.println("postProcessBeanDefinitionRegistry...bean的数量："+registry.getBeanDefinitionCount());
 *          		//RootBeanDefinition beanDefinition = new RootBeanDefinition(Blue.class);
 *          	    // 添加一个Blue到容器中作为组件
 *          		AbstractBeanDefinition beanDefinition = BeanDefinitionBuilder.rootBeanDefinition(Blue.class).getBeanDefinition();
 *          		registry.registerBeanDefinition("hello", beanDefinition);
 *          	}
 *
 *          }
 *      (三)、
 *           接口方法：
 *           @see org.springframework.beans.factory.config.BeanFactoryPostProcessor#postProcessBeanFactory(org.springframework.beans.factory.config.ConfigurableListableBeanFactory)
 *              容器初始化的时候在spring读取beanDefinition信息之后，实例化bean之前。
 *              这个接口是beanFactory的扩展接口，调用时机在spring在读取beanDefinition信息之后，实例化bean之前。
 *              在这个时机，用户可以通过实现这个扩展接口来自行处理一些东西，比如修改已经注册的beanDefinition的元信息。
 *           调用点：
 *           @see org.springframework.boot.SpringApplication#run(java.lang.String...) -->
 *                @see org.springframework.boot.SpringApplication#refreshContext(org.springframework.context.ConfigurableApplicationContext)
 *                     @see org.springframework.boot.SpringApplication#refresh(org.springframework.context.ApplicationContext)
 *                          @see org.springframework.context.support.AbstractApplicationContext#refresh()  --> invokeBeanFactoryPostProcessors(beanFactory);
 *                                @see org.springframework.context.support.AbstractApplicationContext#invokeBeanFactoryPostProcessors(org.springframework.beans.factory.config.ConfigurableListableBeanFactory) -->
 *                                     @see org.springframework.context.support.PostProcessorRegistrationDelegate#invokeBeanFactoryPostProcessors(org.springframework.beans.factory.config.ConfigurableListableBeanFactory, java.util.List)
 *                                          @see org.springframework.context.support.PostProcessorRegistrationDelegate#invokeBeanFactoryPostProcessors(java.util.Collection, org.springframework.beans.factory.config.ConfigurableListableBeanFactory)
 *
 *                                          postProcessor.postProcessBeanFactory(beanFactory);
 *
 *      (四)、
 *          接口方法：
 *          @see org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessor extends BeanPostProcessor
 *          该接口继承了 BeanPostProcessor 接口，区别如下:
 *             ** postProcessBeforeInstantiation：实例化bean之前，相当于new这个bean之前
 *             ** postProcessAfterInstantiation：实例化bean之后，相当于new这个bean之后
 *             ** postProcessPropertyValues：bean已经实例化完成，在属性注入时阶段触发，@Autowired,@Resource等注解原理基于此方法实现
 *             ** postProcessBeforeInitialization：初始化bean之前，相当于把bean注入spring上下文之前
 *             ** postProcessAfterInitialization：初始化bean之后，相当于把bean注入spring上下文之后
 *             @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet() bean初始化之后调用
 *
 *          使用场景：这个扩展点非常有用 ，无论是写中间件和业务中，都能利用这个特性。比如对实现了某一类接口的bean在各个生命期间进行收集，或者对某个类型的bean进行统一的设值等等。
 *
 *          1、实例化----实例化的过程是一个创建Bean的过程，即调用Bean的构造函数，单例的Bean放入单例池中   -> InstantiationAwareBeanPostProcessor的接口起作用  先于BeanPostProcessor执行
 *
 *          2、初始化----初始化的过程是一个赋值的过程，即调用Bean的setter，设置Bean的属性 -> BeanPostProcessor的接口起作用
 *
 *          postProcessBeforeInstantiation 一定执行, postProcessAfterInitialization 一定执行.
 *
 *
 *          @see org.springframework.beans.factory.BeanFactory#getBean(java.lang.String) 从容器中获取bean 如果获取不到就创建bean
 *              @see org.springframework.beans.factory.support.AbstractBeanFactory#getBean(java.lang.String)
 *                  @see org.springframework.beans.factory.support.AbstractBeanFactory#doGetBean(java.lang.String, java.lang.Class, java.lang.Object[], boolean)
 *
 *                      // Create bean instance.
 * 				        if (mbd.isSingleton()) {
 * 				        	sharedInstance = getSingleton(beanName, () -> {
 * 				        		try {
 * 				        			return createBean(beanName, mbd, args);
 *                              }
 * 				        		catch (BeansException ex) {
 * 				        			// Explicitly remove instance from singleton cache: It might have been put there
 * 				        			// eagerly by the creation process, to allow for circular reference resolution.
 * 				        			// Also remove any beans that received a temporary reference to the bean.
 * 				        			destroySingleton(beanName);
 * 				        			throw ex;
 *                                }
 *                          });
 * 				        	bean = getObjectForBeanInstance(sharedInstance, name, beanNamd);
 * 				        }
 *
 *                      @see org.springframework.beans.factory.support.AbstractBeanFactory#createBean(java.lang.String, org.springframework.beans.factory.support.RootBeanDefinition, java.lang.Object[])
 *                          @see org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#createBean(java.lang.String, org.springframework.beans.factory.support.RootBeanDefinition, java.lang.Object[])
 *                              @see org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#resolveBeforeInstantiation(java.lang.String, org.springframework.beans.factory.support.RootBeanDefinition)
 *
 *                      //  Give BeanPostProcessors a chance to return a proxy instead of the target bean instance.
 * 			                Object bean = resolveBeforeInstantiation(beanName, mbdToUse);
 *
 *                      @Nullable
 * 	                    protected Object resolveBeforeInstantiation(String beanName, RootBeanDefinition mbd) {
 * 	                    	Object bean = null;
 * 	                    	if (!Boolean.FALSE.equals(mbd.beforeInstantiationResolved)) {
 * 	                    		// Make sure bean class is actually resolved at this point.
 * 	                    		if (!mbd.isSynthetic() && hasInstantiationAwareBeanPostProcessors()) {
 * 	                    			Class<?> targetType = determineTargetType(beanName, mbd);
 * 	                    			if (targetType != null) {
 * 	                    	          @see org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessor#postProcessBeforeInstantiation(java.lang.Class, java.lang.String)
 * 	                    				bean = applyBeanPostProcessorsBeforeInstantiation(targetType, beanName);
 * 	                    	          如果生成对象(通过代理等方式)，则直接调用 BeanPostProcessor#postProcessAfterInitialization 对bean进行属性的填充
 * 	                    			  @see org.springframework.beans.factory.config.BeanPostProcessor#postProcessAfterInitialization(java.lang.Object, java.lang.String)
 * 	                    				if (bean != null) {
 * 	                    					bean = applyBeanPostProcessorsAfterInitialization(beanName);
 * 	                    				}
 * 	                    			}
 * 	                    		}
 * 	                    		mbd.beforeInstantiationResolved = (bean != null);
 * 	                    	}
 *                         return bean;
 * 	                    }
 *
 *
 *                      try {
 *
 *                           如果可以通过InstantiationAwareBeanPostProcessor生成对象， 即 bean ！= null 就直接返回这个对象，如果对象为空则继续执行代码创建bean
 *
 * 		                	// Give BeanPostProcessors a chance to return a proxy instead of the target bean instance.
 * 		                	Object bean = resolveBeforeInstantiation(beanName, mbdToUse);
 * 		                	if (bean != null) {
 * 		                		return bean;
 *                          }
 *                      }
 * 		                catch (Throwable ex) {
 * 		                	throw new BeanCreationException(mbdToUse.getResourceDescription(), beanName,
 * 		                			"BeanPostProcessor before instantiation of bean failed", ex);
 * 		                }
 *
 *                      创建bean对象实例
 *                     @see org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#doCreateBean(java.lang.String, org.springframework.beans.factory.support.RootBeanDefinition, java.lang.Object[])
 *                          bean实例创建之后进行，创建后的接口调用，对bean进行修改 InstantiationAwareBeanPostProcessor#postProcessAfterInstantiation
 *                          @see org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#populateBean(java.lang.String, org.springframework.beans.factory.support.RootBeanDefinition, org.springframework.beans.BeanWrapper)
 *
 *                      调用
 *                      @see org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessor#postProcessAfterInstantiation(java.lang.Object, java.lang.String)
 *                      // Give any InstantiationAwareBeanPostProcessors the opportunity to modify the
 * 	                	// state of the bean before properties are set. This can be used, for example,
 * 	                	// to support styles of field injection.
 * 	                	if (!mbd.isSynthetic() && hasInstantiationAwareBeanPostProcessors()) {
 * 	                		for (BeanPostProcessor bp : getBeanPostProcessors()) {
 * 	                			if (bp instanceof InstantiationAwareBeanPostProcessor) {
 * 	                				InstantiationAwareBeanPostProcessor ibp = (InstantiationAwareBeanPostProcessor) bp;
 * 	                				if (!ibp.postProcessAfterInstantiation(bw.getWrappedInstance(), beanName)) {
 * 	                					return;
 * 	                				}
 * 	                			}
 * 	                		}
 * 	                	}
 *
 *                      接下来调用
 *                          InstantiationAwareBeanPostProcessor ibp = (InstantiationAwareBeanPostProcessor) bp;
 * 					        PropertyValues pvsToUse = ibp.postProcessProperties(pvs, bw.getWrappedInstance(), beanName);
 * 					        获取所有需要注入的属性
 *                          @see org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessor#postProcessProperties(org.springframework.beans.PropertyValues, java.lang.Object, java.lang.String)
 *                              应用于bean的属性上
 *                              @see org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#applyPropertyValues(java.lang.String, org.springframework.beans.factory.config.BeanDefinition, org.springframework.beans.BeanWrapper, org.springframework.beans.PropertyValues)
 *
 *                      @see org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#doCreateBean(java.lang.String, org.springframework.beans.factory.support.RootBeanDefinition, java.lang.Object[])
 *                              exposedObject = initializeBean(beanName, exposedObject, mbd);
 *                              @see org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#initializeBean(java.lang.String, java.lang.Object, org.springframework.beans.factory.support.RootBeanDefinition)
 *
 *                              执行beanPostProcessor的初始化前置方法
 *                              wrappedBean = applyBeanPostProcessorsBeforeInitialization(wrappedBean, beanName);
 *                              @see org.springframework.beans.factory.config.BeanPostProcessor#postProcessBeforeInitialization(java.lang.Object, java.lang.String)
 *
 *                              执行bean的初始化方法
 *                              invokeInitMethods(beanName, wrappedBean, mbd);
 *                              @see org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#invokeInitMethods(java.lang.String, java.lang.Object, org.springframework.beans.factory.support.RootBeanDefinition)
 *                              在这个方法里面执行bean初始化之后的方法：
 *                                  通过实现 InitializingBean/DisposableBean 接口来定制初始化之后/销毁之前的操作方法；
 *                                  通过 <bean> 元素的 init-method/destroy-method属性指定初始化之后 /销毁之前调用的操作方法；
 *                                  在指定方法上加上@PostConstruct 或@PreDestroy注解来制定该方法是在初始化之后还是销毁之前调用。
 *                                  @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
 *
 *
 *                              执行beanPostProcessor的初始化后置方法
 *                              wrappedBean = applyBeanPostProcessorsAfterInitialization(wrappedBean, beanName);
 *                              @see org.springframework.beans.factory.config.BeanPostProcessor#postProcessAfterInitialization(java.lang.Object, java.lang.String)
 *
 *          (五)、
 *              接口方法：
 *              @see org.springframework.beans.factory.BeanFactoryAware#setBeanFactory(org.springframework.beans.factory.BeanFactory)
 *              @see org.springframework.context.ApplicationContextAware#setApplicationContext(org.springframework.context.ApplicationContext)
 *                  ----->
 *                  @see org.springframework.context.support.ApplicationContextAwareProcessor
 *              可以获取到 BeanFactory 或者 ApplicationContext， 在实现了接口的类中定义一个属性去接收框架传递的 BeanFactory 或者 ApplicationContext， 然后可以在需要的时候使用。
 *              使用场景为，你可以在bean实例化之后，但还未初始化之前，拿到 BeanFactory，在这个时候，可以对每个bean作特殊化的定制。也或者可以把BeanFactory拿到进行缓存，日后使用。
 *
 *          (六)、
 *              接口方法：
 *              @see org.springframework.beans.factory.BeanNameAware#setBeanName(java.lang.String)
 *              触发点在bean的初始化之前，也就是postProcessBeforeInitialization之前
 *              使用场景为：用户可以扩展这个点，在初始化bean之前拿到spring容器中注册的的beanName，来自行修改这个beanName的值。
 *
 *          (七)、
 *              接口方法：
 *              @see org.springframework.beans.factory.FactoryBean
 *              FactoryBean是一个工厂Bean，可以生成某一个类型Bean实例，它最大的一个作用是：可以让我们自定义Bean的创建过程。
 *              BeanFactory是Spring容器中的一个基本类也是很重要的一个类，在BeanFactory中可以创建和管理Spring容器中的Bean，它对于Bean的创建有一个统一的流程。
 *              主要作用：
 *              创建比较复杂的bean， 例如动态代理生成的bean。
 *              在获取的时候可以获取到该工厂bean，调用getBean方法， 需要添加前缀 {@link org.springframework.beans.factory.BeanFactory#FACTORY_BEAN_PREFIX}；
 *              这个工厂bean会创建复杂bean并加入容器。
 *          (八)、
 *              接口方法：
 *              @see org.springframework.beans.factory.SmartInitializingSingleton#afterSingletonsInstantiated()
 *              SmartInitializingSingleton中只有一个接口afterSingletonsInstantiated()，
 *              其作用是是 在spring容器管理的所有单例对象（非懒加载对象）初始化完成之后调用的回调接口
 *              调用点：
 *               @see org.springframework.boot.SpringApplication#run(java.lang.String...) -->
 *                    @see org.springframework.boot.SpringApplication#refreshContext(org.springframework.context.ConfigurableApplicationContext)
 *                         @see org.springframework.boot.SpringApplication#refresh(org.springframework.context.ApplicationContext)
 *                              @see org.springframework.context.support.AbstractApplicationContext#refresh()  --> finishBeanFactoryInitialization(beanFactory);
 *                                   @see org.springframework.context.support.AbstractApplicationContext#finishBeanFactoryInitialization(org.springframework.beans.factory.config.ConfigurableListableBeanFactory)
 *                                   // 实例化所有剩下的非懒加载的单例bean
 *                                   // Instantiate all remaining (non-lazy-init) singletons.
 * 		                             beanFactory.preInstantiateSingletons();
 *                                   @see org.springframework.beans.factory.support.DefaultListableBeanFactory#preInstantiateSingletons()
 *
 *                                   在spring容器管理的所有单例对象（非懒加载对象）初始化完成之后调用的回调接口
 *                                   @see org.springframework.beans.factory.SmartInitializingSingleton#afterSingletonsInstantiated()
 *                                  // Trigger post-initialization callback for all applicable beans...
 * 	                            	for (String beanName : beanNames) {
 * 	                            		Object singletonInstance = getSingleton(beanName);
 * 	                            		if (singletonInstance instanceof SmartInitializingSingleton) {
 * 	                            			final SmartInitializingSingleton smartSingleton = (SmartInitializingSingleton) singletonInstance;
 * 	                            			if (System.getSecurityManager() != null) {
 * 	                            				AccessController.doPrivileged((PrivilegedAction<Object>) () -> {
 * 	                            					smartSingleton.afterSingletonsInstantiated();
 * 	                            					return null;
 * 	                            				}, getAccessControlContext());
 * 	                            			}
 * 	                            			else {
 * 	                            				smartSingleton.afterSingletonsInstantiated();
 * 	                            			}
 * 	                            		}
 * 	                            	}
 *
 *          (九)、
 *              接口方法：
 *              @see org.springframework.boot.ApplicationRunner#run(org.springframework.boot.ApplicationArguments)
 *              @see org.springframework.boot.CommandLineRunner#run(java.lang.String...)
 *              调用点：
 *              @see org.springframework.boot.SpringApplication#run(java.lang.String...)
 *                  callRunners(context, applicationArguments);
 *                  @see org.springframework.boot.SpringApplication#callRunners(org.springframework.context.ApplicationContext, org.springframework.boot.ApplicationArguments)
 *
 *              触发时机为整个项目启动完毕后，自动执行。如果有多个CommandLineRunner 或者 ApplicationRunner，可以利用@Order来进行排序。
 *              可以在这时候执行一些sql文件或者定时任务什么的
 *
 *          (十)、
 *              接口方法：
 *              @see org.springframework.beans.factory.DisposableBean#destroy()
 *              调用bean的 {@link org.springframework.context.annotation.Bean#destroyMethod()}
 *
 *          (十一)、
 *              spring和springboot的事件发布
 *              接口方法：
 *              @see org.springframework.context.ApplicationListener#onApplicationEvent(org.springframework.context.ApplicationEvent)
 *              @see org.springframework.boot.SpringApplicationRunListener
 */

import com.sherlock.common.constants.GmallConstant;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@EnableAspectJAutoProxy(exposeProxy = true)
@EnableFeignClients(GmallConstant.GMALL_ORDER_BASEPATH + GmallConstant.FEIGN)
@EnableDiscoveryClient
@SpringBootApplication
public class GmallOrderApplication {

    public static void main(String[] args) {
        SpringApplication.run(GmallOrderApplication.class, args);
    }

}

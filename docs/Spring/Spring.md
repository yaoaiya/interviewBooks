# spring是什么，有什么好处？

spring其实是一个轻量级框架，我们常说的spring框架是指`spring framework`。它是很多模块的集合，使用这些模块，可以很方便的帮助我们进行开发，主要包括：

- Spring Core：核心类库，spring其他所有功能都依赖于该类库，主要提供IOC依赖注入功能；
- Spring AOP：提供了面向切面的编程实现；
- Spring Web：为创建Web应用程序提供支持；
- Spring ORM：用于支持Hibernate等ORM工具；

- Spring     Aspects ： 该模块为与AspectJ的集成提供支持；
- Spring JDBC : Java数据库连接；
- Spring JMS ：Java消息服务；
- Spring Test : 提供了对 JUnit 和 TestNG 测试的支持。

**spring的优点**

（1）spring属于低侵入式设计，代码的污染极低；

（2）spring的DI机制将对象之间的依赖关系交由框架处理，减低组件的耦合性；

（3）Spring提供了AOP技术，支持将一些通用任务，如安全、事务、日志、权限等进行集中式管理，从而提供更好的复用。

（4）spring对于主流的应用框架提供了集成支持。



# Bean的生命周期

我们在系统里通过**xml**或者**注解**，定义了一大堆的Bean。bean的生命周期是由容器进行管理的，包括`Bean的创建` -->`初始化`、-->`销毁`

<img src="C:\Users\yaoya\AppData\Roaming\Typora\typora-user-images\image-20220213121501556.png" alt="image-20220213121501556" style="zoom:40%;" />



> BeanFactory和ApplicationContext是Spring的两大核心接口，都可以当做Spring的容器。其中ApplicationContext是BeanFactory的子接口, 包含 BeanFactory 的所有特性,它的主要功能是支持大型的业务应用的创建。
>
>  不同点：
>
> （1）BeanFactory是Spring里面最底层的接口，是IoC的核心，定义了IoC的基本功能，包含了各种Bean的定义、加载、实例化，依赖注入和生命周期管理。ApplicationContext接口作为BeanFactory的子类，除了提供BeanFactory所具有的功能外，还提供了更完整的框架功能
>
> （2）BeanFactory是延迟加载,如果Bean的某一个属性没有注入，BeanFacotry加载后，直至第一次使用调用getBean方法才会抛出异常；而ApplicationContext则在初始化自身是检验，这样有利于检查所依赖属性是否注入；
>
> （3）BeanFactory和ApplicationContext都支持BeanPostProcessor、BeanFactoryPostProcessor的使用，但两者之间的区别是：BeanFactory需要手动注册，而ApplicationContext则是自动注册。
>
> （4）BeanFactory通常以编程的方式被创建，ApplicationContext还能以声明的方式创建，如使用ContextLoader。

**1）实例化Bean**

​	对于BeanFactory容器，当请求一个尚未初始化的bean时，或者初始化bean的时候需要注入另一个没有初始化的依赖时，容器就会调用createBean进行实例化；

​	对于ApplicationContext容器，当容器启动完毕后，会调用BeanDefinition对象中的信息，实例化所有的bean。

**2）设置对象属性（依赖注入）**

​	实例化后的Bean被封装在BeanWarpper对象中，紧接着，spring根据BeanDefinition中的信息，以及BeanWarpper中提供的设置属性，进行依赖注入。依赖注入的方式有：构造函数、setter、注解

**3）处理Aware接口**

接着，Spring 会检测该对象是否实现了XXAware 接口，并将相关的XXXAware 实例注入给 Bean:  

- 如果这个Bean已经实现了BeanNameAware 接口，会调用它实现的setBeanName (String  beanld )方法，此处传递的就是Spring 配置文件中Bean的id值； 
- 如果这个Bean已经实现了BeanFactoryAware 接口，会调用它实现的setBeanFactory(方 法，传递的是Spring 工厂自身。 
- 如果这个Bean已经实现了ApplicationContextAware 接口，会调用 setApplicationContext (Application Context )方法，传入Spring 上下文；

**4）BeanPostProcesser**

​	如果我们想要在bean实例构建好了之后，对bean进行自定义的处理，那么可以让Bean实现BeanPostProcesser后置处理器，那么将会调用postProcesserBeforInitalization（Object obj，String s）方法。

**5）InitializingBean和init-method**

如果Bean在spring配置文件配置了init-method属性，则会自动调用其配置进行初始化

**6）BeanPostProcesser**

在初始化结束之后，如果Bean实现了BeanPostProcesser接口，将会调用postProcesserAfterInitalization方法，由于这个方法是在初始化结束之后调用的，因此可以被应用于内存或者缓存技术

**7）DisPosableBean**

当Bean不在需要时，会进入清理阶段。如果Bean实现了DisposableBean接口，会调用其实现的destroy()方法；

**8）destroy-mothed**

最后如果这个bean的spring配置中配置了destroy-method属性，会自动调用其配置的销毁方法



# Bean的懒加载和即时加载原理

Spring 初始化入口 refresh（省略了部分根本次无关的代码，望理解，太长了影响阅读体验）

```java
[public void refresh() throws BeansException, IllegalStateException {
    synchronized (this.startupShutdownMonitor) {
        // Prepare this context for refreshing.
        prepareRefresh();

        // Prepare the bean factory for use in this context.
        prepareBeanFactory(beanFactory);

        try {
            // Allows post-processing of the bean factory in context subclasses.
            postProcessBeanFactory(beanFactory);

            // Invoke factory processors registered as beans in the context.
            invokeBeanFactoryPostProcessors(beanFactory);

            // Register bean processors that intercept bean creation.
            registerBeanPostProcessors(beanFactory);
            // Instantiate all remaining (non-lazy-init) singletons.
            // 初始化所有非 懒加载的bean！！！！
            finishBeanFactoryInitialization(beanFactory);

            // Last step: publish corresponding event.
            finishRefresh();
        }
 }](http://mp.weixin.qq.com/s?__biz=MzI3ODcxMzQzMw==&mid=2247492186&idx=3&sn=cbb8fda8f97f28a24939134e711b51a9&chksm=eb50676cdc27ee7afead02ae8c3c0fb6611681c119fa0b3f6dac238676aeb2660a9d77dde392&scene=21#wechat_redirect) 
```

第20行则是跟本次主题有关的，就是说在容器启动的时候只处理non-lazy-init bean，懒加载的bean在Spring启动阶段根本不做任何处理下面看下源码就明白了
点进去第20行的finishBeanFactoryInitialization(beanFactory)里头有个初始化non-lazy-init bean的函数 preInstantiateSingletons()
**具体逻辑如下**

##### 1.对beanNames 集合遍历获取每个BeanDefinition

##### 2.判断是否是懒加载的，如果不是则继续处理(non-lazy-init bean 不做处理)

##### 3.判断是否是factorybean 如果不是则进行实例化并依赖注入

```java
public void preInstantiateSingletons() throws BeansException {
   // 所有beanDefinition集合
   List<String> beanNames = new ArrayList<String>(this.beanDefinitionNames);
   // 触发所有非懒加载单例bean的初始化
   for (String beanName : beanNames) {
       // 获取bean 定义
      RootBeanDefinition bd = getMergedLocalBeanDefinition(beanName);
      // 判断是否是懒加载单例bean，如果是单例的并且不是懒加载的则在Spring 容器
      if (!bd.isAbstract() && bd.isSingleton() && !bd.isLazyInit()) {
          // 判断是否是FactoryBean
         if (isFactoryBean(beanName)) {
                final FactoryBean<?> factory = (FactoryBean<?>) getBean(FACTORY_BEAN_PREFIX + beanName);
                boolean isEagerInit;
                if (System.getSecurityManager() != null && factory instanceof SmartFactoryBean) {
                   isEagerInit = AccessController.doPrivileged(new PrivilegedAction<Boolean>() {
                      @Override
                      public Boolean run() {
                         return ((SmartFactoryBean<?>) factory).isEagerInit();
                      }
                   }, getAccessControlContext());
                }
         }else {
             // 如果是普通bean则进行初始化依赖注入，此 getBean(beanName)接下来触发的逻辑跟
             // context.getBean("beanName") 所触发的逻辑是一样的
            getBean(beanName);
         } }
   }
} 
```

getBean() 方法是实现bean 初始化以及依赖注入的函数

```java
@Override
public Object getBean(String name) throws BeansException {   
    return doGetBean(name, null, null, false);
} 
```

# spring事务的处理方式

Spring 为事务管理提供了丰富的功能支持。Spring 事务管理分为编码式和声明式的两种方式。

- 编程式事务指的是通过编码方式实现事务；
- 声明式事务基于 AOP,将具体业务逻辑与事务处理解耦。

声明式事务管理使业务代码逻辑不受污染, 因此在实际使用中声明式事务用的比较多。

##### 声明式事务有两种方式

- 一种是在配置文件（xml）中做相关的事务规则声明，
- 另一种是基于@Transactional 注解的方式。

注解配置是目前流行的使用方式。

在SpringBoot则非常简单，只需在业务层添加事务注解(@Transactional )即可快速开启事务（网上很多文章说需要在启动类上添加注解@EnableTransactionManagement 开启事务, 本人实际开发中并不需要添加，正确配置数据源后都是自动开启的）。虽然事务很简单，但对于数据方面是需要谨慎对待的。

##### @Transactional注解用于两种场景

- 标于类上：表示所有方法都进行事务处理
- 标于方法上：仅对该方法有效

##### @Transactional运行解读

　　在应用系统调用声明了 @Transactional 的目标方法时，Spring Framework 默认使用 AOP 代理，在代码运行时生成一个代理对象，根据 @Transactional 的属性配置信息，这个代理对象决定该声明 @Transactional 的目标方法是否由拦截器 TransactionInterceptor 来使用拦截，在 TransactionInterceptor 拦截时，会在目标方法开始执行之前创建并加入事务，并执行目标方法的逻辑, 最后根据执行情况是否出现异常，利用抽象事务管理器 AbstractPlatformTransactionManager 操作数据源 DataSource 提交或回滚事务。
　　Spring AOP 代理有 CglibAopProxy 和 JdkDynamicAopProxy 两种，以 CglibAopProxy 为例，对于 CglibAopProxy，需要调用其内部类的 DynamicAdvisedInterceptor 的 intercept 方法。对于 JdkDynamicAopProxy，需要调用其 invoke 方法。

##### 事务传播行为

@Transactional(propagation=Propagation.REQUIRED) ：如果有事务, 那么加入事务, 没有的话新建一个(默认情况下)
@Transactional(propagation=Propagation.NOT_SUPPORTED) ：容器不为这个方法开启事务
@Transactional(propagation=Propagation.REQUIRES_NEW) ：不管是否存在事务,都创建一个新的事务,原来的挂起,新的执行完毕,继续执行老的事务
@Transactional(propagation=Propagation.MANDATORY) ：必须在一个已有的事务中执行,否则抛出异常
@Transactional(propagation=Propagation.NEVER) ：必须在一个没有的事务中执行,否则抛出异常(与Propagation.MANDATORY相反)
@Transactional(propagation=Propagation.SUPPORTS) ：如果其他bean调用这个方法,在其他bean中声明事务,那就用事务.如果其他bean没有声明事务,那就不用事务.

##### 事务超时设置

@Transactional(timeout=30) //默认是30秒

##### 事务隔离级别

@Transactional(isolation = Isolation.READ_UNCOMMITTED)：读取未提交数据(会出现脏读, 不可重复读) 基本不使用
@Transactional(isolation = Isolation.READ_COMMITTED)：读取已提交数据(会出现不可重复读和幻读)
@Transactional(isolation = Isolation.REPEATABLE_READ)：可重复读(会出现幻读)
@Transactional(isolation = Isolation.SERIALIZABLE)：串行化

MYSQL: 默认为REPEATABLE_READ级别

SQLSERVER: 默认为READ_COMMITTED

ORACLE：默认为READ COMMITTED

脏读 : 一个事务读取到另一事务未提交的更新数据
不可重复读 : 在同一事务中, 多次读取同一数据返回的结果有所不同, 换句话说, 后续读取可以读到另一事务已提交的更新数据. 相反, "可重复读"在同一事务中多次读取数据时, 能够保证所读数据一样, 也就是后续读取不能读到另一事务已提交的更新数据
幻读 : 一个事务读到另一个事务已提交的insert数据
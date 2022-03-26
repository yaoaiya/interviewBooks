# Spring Boot 的自动配置是如何实现的?

对于Spring Boot 项目，我们只需要添加相关依赖，无需配置，通过启动main 方法即可。并且我们通过 Spring Boot 的全局配置文件 application.properties或application.yml即可对项目进行设置比如更换端口号，配置 JPA 属性等等。为什么 Spring Boot 使用起来这么酸爽呢？ 这得益于其自动装配。自动装配可以说是 Spring Boot 的核心，那究竟什么是自动装配呢？

### 什么是Spring Boot自动装配？

SpringBoot 定义了一套接口规范，这套规范规定：SpringBoot 在启动时会扫描外部引用 jar 包中的META-INF/spring.factories文件，将文件中配置的类型信息加载到 Spring 容器，并执行类中定义的各种操作。对于外部 jar 来说，只需要按照 SpringBoot 定义的标准，就能将自己的功能装置进 SpringBoot。

在我看来，自动装配可以简单理解为：通过注解或者一些简单的配置就能在 Spring Boot 的帮助下实现某块功能。

### Spring Boot如何实现自动装配？

Spring Boot的核心注解@SpringBootApplication，放置在Spring Boot的启动类上，表明该类是开启Spring Boot容器的入口，它是一个复合注解。大概可以把 @SpringBootApplication看作是以下三个注解的集合：

@EnableAutoConfiguration：启用 SpringBoot 的自动配置机制。

@Configuration：声明该类为配置类。

@ComponentScan： 组件扫描。扫描被@Component (@Service,@Controller)注解的 bean，注解默认会扫描启动类所在的包下所有的类 。

Spring Boot 通过@EnableAutoConfiguration开启自动装配，使用@Import注解向容器中注入AutoConfigurationImportSelector类，通过 SpringFactoriesLoader 最终加载META-INF/spring.factories中的自动配置类实现自动装配，自动配置类其实就是通过@Conditional按需加载的配置类，想要其生效必须引入spring-boot-starter-xxx包实现起步依赖

SpringBoot在启动的时候会调用run()方法，run()方法会刷新容器，刷新容器的时候，会扫描classpath下面的的包中META-INF/spring.factories文件，在这个文件中记录了好多的自动配置类，在刷新容器的时候会将这些自动配置类加载到容器中，然后在根据这些配置类中的条件注解，来判断是否将这些配置类在容器中进行实例化，这些条件主要是判断项目是否有相关jar包或是否引入了相关的bean。这样springboot就帮助我们完成了自动装配。


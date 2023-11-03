package com.mayee.spring.extension.lifecycle;

import com.mayee.bean.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

/**
 * 前面提到的FactoryBean是一种特殊的Bean的类型，@Import注解是往Spring容器中注册Bean。其实不论是@Import注解，还是@Component、@Bean等注解，又或是xml配置，甚至是demo中的register方法，其实主要都是做了一件事，那就是往Spring容器去注册Bean。
 * <p>
 * 为什么需要去注册Bean？
 * 当然是为了让Spring知道要为我们生成Bean，并且需要按照我的要求来生成Bean，比如说，我要@Autowired一个对象，那么你在创建Bean的过程中，就得给我@Autowired一个对象，这就是一个IOC的过程。所以这就涉及了Bean的创建，销毁的过程，也就是面试常问的Bean的生命周期。
 */
@Slf4j
public class LifeCycle implements InitializingBean, ApplicationContextAware, DisposableBean {

    @PostConstruct
    public void postConstruct() {
        log.info("@PostConstruct注解起作用，postConstruct方法被调用了");
    }

    @Autowired
    private User user;

    public LifeCycle() {
        log.info("LifeCycle对象被创建了");
    }

    /**
     * 实现 DisposableBean 注解
     */
    @Override
    public void destroy() throws Exception {
        log.info("DisposableBean接口起作用，destroy方法被调用了");
    }

    /**
     * 通过 {@link Bean#initMethod()}来指定
     *
     * @throws Exception
     */
    public void initMethod() throws Exception {
        log.info("@Bean#initMethod()起作用，initMethod方法被调用了");
    }

    /**
     * 通过 {@link Bean#destroyMethod()}来指定
     *
     * @throws Exception
     */
    public void destroyMethod() throws Exception {
        log.info("@Bean#destroyMethod()起作用，destroyMethod方法被调用了");
    }

    @PreDestroy
    public void preDestroy() throws Exception {
        log.info("@PreDestroy注解起作用，preDestroy方法被调用了");
    }

    /**
     * 实现 InitializingBean 接口
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        log.info("InitializingBean接口起作用，afterPropertiesSet方法被调用了");
    }

    /**
     * 实现的 ApplicationContextAware 回调接口
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        log.info("Aware接口起作用，setApplicationContext被调用了，此时user=" + user);
    }

    /**
     通过测试的结果可以看出，Bean在创建和销毁的过程当我们实现了某些接口或者加了某些注解，Spring就会回调我们实现的接口或者执行的方法。
     同时，在执行setApplicationContext的时候，能打印出User对象，说明User已经被注入了，说明注入发生在setApplicationContext之前。

     总结一下Bean创建和销毁过程中调用的顺序：
     构造方法 -> @Autowired -> xxxAware 接口 -> @PostConstruct -> InitializingBean#afterPropertiesSet -> Bean#initMethod // Bean 创建部分
     -> @PreDestroy -> DisposableBean#destroy -> Bean#destroyMethod // Bean 销毁部分吗，在容器关闭的时候


     Aware接口是指以Aware结尾的一些Spring提供的接口，当你的Bean实现了这些接口的话，在创建过程中会回调对应的set方法，并传入响应的对象
           接口	                                      作用
     ApplicationContextAware	              注入ApplicationContext
     ApplicationEventPublisherAware	      注入ApplicationEventPublisher事件发布器
     BeanFactoryAware	                        注入BeanFactory
     BeanNameAware	                             注入Bean的名称

     有了这些回调，比如说我的Bean想拿到ApplicationContext，不仅可以通过@Autowired注入，还可以通过实现ApplicationContextAware接口拿到。
    */
    static class Application {
        public static void main(String[] args) {
            AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();
            //将 LifeCycle 注册到容器中
            applicationContext.register(Application.class);
            applicationContext.refresh();

            // 关闭上下文，触发销毁操作
            applicationContext.close();
        }

        @Bean(initMethod = "initMethod", destroyMethod = "destroyMethod")
        public LifeCycle lifeCycle() {
            return new LifeCycle();
        }

        @Bean
        public User user() {
            return new User();
        }
    }
}

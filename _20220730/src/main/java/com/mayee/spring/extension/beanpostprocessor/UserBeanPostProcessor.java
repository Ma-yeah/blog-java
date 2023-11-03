package com.mayee.spring.extension.beanpostprocessor;

import com.mayee.bean.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 BeanPostProcessor，中文名 Bean的后置处理器，在Bean创建的过程中起作用。
 BeanPostProcessor在Spring Bean的扩展中扮演着重要的角色，是Spring Bean生命周期中很重要的一部分。正是因为有了BeanPostProcessor，就可以在Bean创建过程中的任意一个阶段扩展自己想要的东西。

 BeanPostProcessor是Bean在创建过程中一个非常重要的扩展点，因为每个Bean在创建的各个阶段，都会回调BeanPostProcessor及其子接口的方法，传入正在创建的Bean对象，这样如果想对Bean创建过程中某个阶段进行自定义扩展，那么就可以自定义BeanPostProcessor来完成。
 Bean创建的阶段比较多，然后BeanPostProcessor接口以及他的子接口InstantiationAwareBeanPostProcessor、DestructionAwareBeanPostProcessor就提供了很多方法，可以使得在不同的阶段都可以拿到正在创建的Bean进行扩展。
*/
@Slf4j
public class UserBeanPostProcessor implements BeanPostProcessor {

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof User) {
            //如果当前的Bean的类型是 User ，就把这个对象 username 的属性赋值为 三友的java日记
            ((User) bean).setName("UserBeanPostProcessor");
        }
        return bean;
    }

    /**
     从结果可以看出，每个生成的Bean在执行到某个阶段的时候，都会回调UserBeanPostProcessor

     Spring内置的BeanPostProcessor:
           BeanPostProcessor	                        作用
     AutowiredAnnotationBeanPostProcessor	    处理@Autowired、@Value注解
     CommonAnnotationBeanPostProcessor	        处理@Resource、@PostConstruct、@PreDestroy注解
     AnnotationAwareAspectJAutoProxyCreator	    处理一些注解或者是AOP切面的动态代理
     ApplicationContextAwareProcessor	        处理Aware接口注入的
     AsyncAnnotationBeanPostProcessor	        处理@Async注解
     ScheduledAnnotationBeanPostProcessor	    处理@Scheduled注解

     处理@Autowired、@PostConstruct、@PreDestroy注解其实就是通过BeanPostProcessor，在Bean的不同阶段来调用对应的方法起作用的。
    */
    static class Application{
        public static void main(String[] args) {
            AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();
            //将 UserBeanPostProcessor 和  User 注册到容器中
            applicationContext.register(UserBeanPostProcessor.class);
            applicationContext.register(User.class);
            applicationContext.refresh();

            User user = applicationContext.getBean(User.class);
            log.info("获取到的Bean为" + user + "，属性username值为：" + user.getName());
        }
    }
}

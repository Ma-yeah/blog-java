package com.mayee.spring.extension.importor;

import com.mayee.bean.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Import;

/**
 直接导入一个普通的类到容器中.

 其实不论是什么样的配置类，主要的作用就是往Spring容器中注册Bean，只不过注入的方式不同罢了。
 ImportSelector和ImportBeanDefinitionRegistrar的方法是有入参的，也就是注解的一些属性的封装，所以就可以根据注解的属性的配置，来决定应该返回样的配置类或者是应该往容器中注入什么样的类型的Bean，可以看一下 @EnableAsync 的实现，看看是如何根据@EnableAsync注解的属性来决定往容器中注入什么样的Bean。

 @Import的核心作用就是导入配置类，并且还可以根据配合（比如@EnableXXX）使用的注解的属性来决定应该往Spring中注入什么样的Bean。
*/
@Slf4j
@Import(User.class)
public class UserImport {
    public static void main(String[] args) {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();
        //将当前类注册到容器中
        applicationContext.register(UserImport.class);
        applicationContext.refresh();

        log.info("获取到的Bean为" + applicationContext.getBean(User.class));
    }
}

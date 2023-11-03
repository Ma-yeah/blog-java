package com.mayee.spring.extension.importor;

import com.mayee.bean.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

/**
 * 这个接口相比与 ImportSelector 接口的主要区别就是，ImportSelector接口是返回一个类，你不能对这个类进行任何操作，但是 ImportBeanDefinitionRegistrar 是可以自己注入 BeanDefinition，可以添加属性之类的。
 */
@Slf4j
public class UserImportBeanDefinitionRegistrar implements ImportBeanDefinitionRegistrar {

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry, BeanNameGenerator importBeanNameGenerator) {
        //构建一个 BeanDefinition , Bean的类型为 User
        AbstractBeanDefinition beanDefinition = BeanDefinitionBuilder.rootBeanDefinition(User.class)
                // 设置 User 这个Bean的属性username的值为三友的java日记
                .addPropertyValue("name", "UserImportBeanDefinitionRegistrar")
                .getBeanDefinition();

        log.info("往Spring容器中注入User");
        //把 User 这个Bean的定义注册到容器中
        registry.registerBeanDefinition("user", beanDefinition);
    }

    @Import(UserImportBeanDefinitionRegistrar.class)
    static class Application {
        public static void main(String[] args) {
            AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();
            //将 Application 注册到容器中
            applicationContext.register(Application.class);
            applicationContext.refresh();

            User user = applicationContext.getBean(User.class);
            log.info("获取到的Bean为" + user + "，属性username值为：" + user.getName());
        }
    }
}

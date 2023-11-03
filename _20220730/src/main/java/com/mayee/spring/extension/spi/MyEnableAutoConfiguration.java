package com.mayee.spring.extension.spi;

import com.mayee.bean.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.io.support.SpringFactoriesLoader;

import java.util.List;

/**
 Spring的SPI机制规定，配置文件必须在classpath路径下的META-INF文件夹内，文件名必须为spring.factories，文件内容为键值对，一个键可以有多个值，只需要用逗号分割就行，同时键值都需要是类的全限定名。但是键和值可以没有任何关系，当然想有也可以有。

 说到SpringBoot的扩展点，第一时间肯定想到的就是自动装配机制，面试贼喜欢问，但是其实就是一个很简单的东西。当项目启动的时候，会去从所有的spring.factories文件中读取@EnableAutoConfiguration键对应的值，拿到配置类，然后根据一些条件判断，决定哪些配置可以使用，哪些不能使用。

 自动装配机制是SpringBoot的一个很重要的扩展点，很多框架在整合SpringBoot的时候，也都通过自动装配来的，实现项目启动，框架就自动启动的。
*/
@Slf4j
@SpringBootApplication
public class MyEnableAutoConfiguration {

    public static void main(String[] args) {
        List<String> classNames = SpringFactoriesLoader.loadFactoryNames(MyEnableAutoConfiguration.class, MyEnableAutoConfiguration.class.getClassLoader());
        log.info("spi 中的配置: ");
        classNames.forEach(System.out::println);

        ConfigurableApplicationContext applicationContext = SpringApplication.run(UserAutoConfiguration.class);
        User user = applicationContext.getBean(User.class);
        log.info("获取到的Bean为" + user);
    }
}

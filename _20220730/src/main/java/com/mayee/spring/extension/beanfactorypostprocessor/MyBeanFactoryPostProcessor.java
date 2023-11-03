package com.mayee.spring.extension.beanfactorypostprocessor;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * BeanFactoryPostProcessor是可以对Spring容器做处理的，方法的入参就是Spring的容器，通过这个接口，就对容器进行为所欲为的操作。
 */
public class MyBeanFactoryPostProcessor implements BeanFactoryPostProcessor {
    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory configurableListableBeanFactory) throws BeansException {
        // 禁止循环依赖
        ((DefaultListableBeanFactory) configurableListableBeanFactory).setAllowCircularReferences(false);
    }

    static class Application {
        public static void main(String[] args) {
            AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();
            // 注入到Spring容器中就会生效
            applicationContext.register(MyBeanFactoryPostProcessor.class);
            applicationContext.refresh();
        }
    }
}

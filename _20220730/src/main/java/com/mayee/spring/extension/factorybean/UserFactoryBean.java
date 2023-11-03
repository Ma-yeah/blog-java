package com.mayee.spring.extension.factorybean;

import com.mayee.bean.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 BeanFactory是Bean的工厂，可以帮我们生成想要的Bean，而FactoryBean就是一种Bean的类型。当往容器中注入class类型为FactoryBean的类型的时候，最终生成的Bean是用过FactoryBean的getObject获取的。
 一般来说，FactoryBean 比较适合那种复杂Bean的构建，在其他框架整合Spring的时候用的比较多。
 */
@Slf4j
public class UserFactoryBean implements FactoryBean<User> {
    @Override
    public User getObject() throws Exception {
        User user = new User("1", "UserFactoryBean");
        System.out.println("调用 UserFactoryBean 的 getObject 方法生成 Bean:" + user);
        return user;
    }

    @Override
    public Class<?> getObjectType() {
        // 这个 FactoryBean 返回的Bean的类型
        return User.class;
    }

    static class Application{
        public static void main(String[] args) {
            AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
            //将 UserFactoryBean 注册到容器中
            context.register(UserFactoryBean.class);
            context.refresh();

            log.info("获取到的Bean为" + context.getBean(User.class));
        }
    }
}

package com.mayee.config;

import com.google.common.collect.Lists;
import com.mayee.bean.User;
import com.mayee.event.RefreshClientEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class RefreshClientListenerConfig {

    private  List<String> names = Lists.newArrayList("mayee","bobby","music");

    private final ApplicationContext context;

    @Bean
    public ApplicationListener<RefreshClientEvent> refreshClientListener(){
        return event -> {
            Integer index = event.getIndex();
            log.info("login close, 收到索引: {}, 刷新客户端...",index);
            //获取BeanFactory
            DefaultListableBeanFactory defaultListableBeanFactory = (DefaultListableBeanFactory) context.getAutowireCapableBeanFactory();
            //删除bean.
            defaultListableBeanFactory.destroySingleton("user");
            User user = new User();
            user.setName(index > names.size()-1 ? names.get(index%names.size()) : names.get(index));
            //动态注册bean.
            defaultListableBeanFactory.registerSingleton("user", user);
            RetryCounter.increment();
        };
    }
}

package com.mayee.config;

import com.google.common.collect.Lists;
import com.mayee.bean.ConnectInfo;
import com.mayee.bean.User;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class AppConfig {

    @Bean
    public User user() {
        return new User(
                "mayee",
                27,
                Lists.newArrayList(new ConnectInfo("广东省深圳市", "13344456789"))
        );
    }

}

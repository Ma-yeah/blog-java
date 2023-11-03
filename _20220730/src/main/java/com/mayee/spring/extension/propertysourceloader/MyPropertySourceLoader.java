package com.mayee.spring.extension.propertysourceloader;

import com.mayee.bean.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;

@Slf4j
@SpringBootApplication
public class MyPropertySourceLoader {

    public static void main(String[] args) {
        ConfigurableApplicationContext applicationContext = SpringApplication.run(MyPropertySourceLoader.class);

        User user = applicationContext.getBean(User.class);

        log.info("获取到的Bean为" + user + "，属性username值为：" + user.getName());
    }

    @Bean
    public User user() {
        return new User();
    }
}

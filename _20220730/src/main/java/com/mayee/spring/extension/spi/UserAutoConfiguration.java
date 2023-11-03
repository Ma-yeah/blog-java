package com.mayee.spring.extension.spi;

import com.mayee.spring.extension.factorybean.UserFactoryBean;
import org.springframework.context.annotation.Bean;

/**
 * UserAutoConfiguration 虽然没有加 @Configuration 注解，但由于在 spring.factories 中配置了值，@SpringBootApplication 中的 @EnableAutoConfiguration 就会中 spring.factories 中加载到配置项，然后注入到容器中
 */
public class UserAutoConfiguration {
    @Bean
    public UserFactoryBean userFactoryBean() {
        return new UserFactoryBean();
    }
}

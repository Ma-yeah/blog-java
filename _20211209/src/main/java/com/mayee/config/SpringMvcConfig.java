package com.mayee.config;

import com.mayee.resolver.ArrayHandlerMethodArgumentResolver;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * @program: blog-example
 * @description:
 * @author: Bobby.Ma
 * @create: 2021-12-16 18:49
 **/
@Configuration
public class SpringMvcConfig implements WebMvcConfigurer {
    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new ArrayHandlerMethodArgumentResolver());
    }
}

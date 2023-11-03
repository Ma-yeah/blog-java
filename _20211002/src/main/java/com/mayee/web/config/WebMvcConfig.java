package com.mayee.web.config;

import com.mayee.web.interceptor.ContentCacheInterceptor;
import com.mayee.web.interceptor.ContentCryptoInterceptor;
import com.mayee.web.resolver.FillArgsResolver;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new ContentCacheInterceptor()).addPathPatterns("/contentCache");
        registry.addInterceptor(new ContentCryptoInterceptor()).addPathPatterns("/contentCrypto");
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new FillArgsResolver());
    }
}

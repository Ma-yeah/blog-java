package com.mayee.web.filter;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Description: 定义多个 filter，order 越小。优先级越高
 * @Author: Bobby.Ma
 * @Date: 2021/10/7 2:11
 */
@Configuration
public class WebFilter {

    @Bean
    public FilterRegistrationBean<ContentCryptoFilter> contentCryptoFilter() {
        FilterRegistrationBean<ContentCryptoFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new ContentCryptoFilter());
        registration.addUrlPatterns("/contentCrypto");
        registration.setOrder(1);
        return registration;
    }

    @Bean
    public FilterRegistrationBean<ContentCacheFilter> contentCacheFilter() {
        FilterRegistrationBean<ContentCacheFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new ContentCacheFilter());
        registration.addUrlPatterns("/contentCache");
        registration.setOrder(1);
        return registration;
    }
}

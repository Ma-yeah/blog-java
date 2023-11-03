package com.mayee.validator.config;

import org.hibernate.validator.HibernateValidator;
import org.hibernate.validator.messageinterpolation.ResourceBundleMessageInterpolator;
import org.hibernate.validator.resourceloading.PlatformResourceBundleLocator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;

import javax.validation.Validation;
import javax.validation.Validator;
import java.util.Locale;

@Configuration
public class ValidatorConfig {

    @Bean
    public Validator validator() {
        // 设置校验不通过时的提示语种
        Locale.setDefault(Locale.US);
        return Validation.byProvider(HibernateValidator.class)// byDefaultProvider()
                .configure()
                // 设置validator模式为快速失败返回
                .failFast(true)
                .messageInterpolator(
                        new ResourceBundleMessageInterpolator(new PlatformResourceBundleLocator("MyMessages")))
                .buildValidatorFactory()
                .getValidator();
    }

    @Bean
    public MethodValidationPostProcessor methodValidationPostProcessor() {
        // 默认是普通模式，会返回所有的验证不通过信息集合
        MethodValidationPostProcessor processor = new MethodValidationPostProcessor();
        processor.setValidator(validator());
        return processor;
    }
}

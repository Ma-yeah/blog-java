package com.mayee.spring.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;

@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(Application.class, args);
        ConfigurableEnvironment env = context.getEnvironment();
        String name =  env.getProperty("developer.name");
        Integer age = env.getProperty("developer.age", Integer.class);
        System.out.println("name: " + name);
        System.out.println("age: " + age);
    }
}

package com.mayee;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

@Slf4j
@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(Application.class, args);
        ConfigurableEnvironment environment = context.getEnvironment();
        String buildTime = environment.getProperty("build.time");
        String startTime = Instant.ofEpochMilli(context.getStartupDate()).atOffset(ZoneOffset.UTC).toLocalDateTime().format(DateTimeFormatter.ofPattern("yyy-MM-dd HH:mm:ss"));
        log.info(">>>>>> maven build time(UTC): {}, startUp time: {} <<<<<<", buildTime, startTime);
    }
}

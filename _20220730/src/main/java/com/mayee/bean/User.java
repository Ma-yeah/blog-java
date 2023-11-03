package com.mayee.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

@Slf4j
@Data
@AllArgsConstructor
public class User {

    private String id;
    @Value("${user.source-name}") // 注入 application.json 配置文件的属性
    private String name;

    public User() {
        log.info("User 被创建了");
    }
}

package com.mayee;

import com.mayee.server.NettyServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class,args);

        // 启动 tcp 和 ws 服务
        new NettyServer().bind(9600);
    }
}
package com.mayee.service;

import java.util.concurrent.TimeUnit;

/**
 * @program: blog-example
 * @description:
 * @author: Bobby.Ma
 * @create: 2021-11-03 14:39
 **/
public interface IService {

    default void remote(){
        try {
            // 模拟 rpc 调用
            TimeUnit.MILLISECONDS.sleep(500L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

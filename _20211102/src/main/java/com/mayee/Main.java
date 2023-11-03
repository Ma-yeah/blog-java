package com.mayee;

import com.mayee.factory.UserFactory;
import com.mayee.model.User;

/**
 * @program: blog-example
 * @description: https://mp.weixin.qq.com/s/e-9hrjWK513VJqqyeGLxrQ
 * @author: Bobby.Ma
 * @create: 2021-11-02 15:12
 **/
public class Main {

    public static void main(String[] args) {
        // 假设为注入
        UserFactory userFactory = new UserFactory();

        User user = userFactory.build(1L);

        System.out.println(user.getDepartment());
        System.out.println(user.getSupervisor());
        System.out.println(user.getPermission());
        // 以上结果可以证明惰性求值和值缓存
    }
}

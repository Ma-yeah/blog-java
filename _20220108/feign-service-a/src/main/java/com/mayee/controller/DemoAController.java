package com.mayee.controller;

import com.alibaba.fastjson.JSON;
import com.mayee.bean.R;
import com.mayee.bean.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

@Slf4j
@RestController
@RequestMapping("/a")
public class DemoAController {

    /**
     * @param name
     * @Description: 简单的示例
     * @return: com.alibaba.fastjson.JSONObject
     * @Author: Bobby.Ma
     * @Date: 2022/1/8 16:06
     */
    @GetMapping("/hello")
    public R hello(String name) {
        log.info("hello: {}", name);
        return R.data(name);
    }

    /**
     * @param
     * @Description: 接收对象参数
     * @return: com.alibaba.fastjson.JSONObject
     * @Author: Bobby.Ma
     * @Date: 2022/1/8 16:06
     */
    @GetMapping("/query")
    public R query(User user) {
        if (Objects.isNull(user)) {
            user = new User();
        }
        log.info("query user: param: {}", JSON.toJSONString(user));
        user.setMsg("msg by provider");
        return R.data(user);
    }

    @PostMapping("/create")
    public R create(@RequestBody User user) {
        if (Objects.isNull(user)) {
            return R.fail("user is null");
        }
        log.info("create user: {}", JSON.toJSONString(user));
        return R.success();
    }

    @DeleteMapping("/{id}")
    public R delete(@PathVariable Integer id) {
        log.info("delete: {}", id);
        return R.success();
    }

    @GetMapping("/error")
    public R error() {
        throw new RuntimeException("Manual throw exception");
    }

    @GetMapping("/dynamic")
    public R dynamic(HttpServletRequest request) {
        return R.data("dynamic " + request.getRequestURI());
    }

    @GetMapping("/dynamic1")
    public R dynamic1(HttpServletRequest request) {
        return R.data("dynamic1 " + request.getRequestURI());
    }
}

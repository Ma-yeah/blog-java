package com.mayee.controller;

import com.mayee.bean.R;
import com.mayee.bean.User;
import com.mayee.feign.DynamicURLFeign;
import com.mayee.feign.FeignAService;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/b")
public class DemoBController {

    @Autowired
    private FeignAService feignAService;
    @Autowired
    private DynamicURLFeign dynamicURLFeign;

    @GetMapping("/hello")
    public R getHello() {
        return feignAService.hello("demo");
    }

    @GetMapping("/query")
    public R getQuery() {
        return feignAService.query(new User().setName("bobby").setAge(26));
    }

    @PostMapping("/create")
    public R create() {
        return feignAService.create(new User().setName("bobby").setAddress("宝安区"));
    }

    @DeleteMapping("/delete")
    public R delete() {
        return feignAService.delete(10);
    }

    @GetMapping("/error")
    public R error() {
        return feignAService.error();
    }

    @SneakyThrows
    @GetMapping("/dynamic")
    public R dynamic() {
        return dynamicURLFeign.dynamic(new URI("http://127.0.0.1:8081"));
    }

    @GetMapping("/dynamic1")
    public R dynamic1() {
        return dynamicURLFeign.dynamic1();
    }
}

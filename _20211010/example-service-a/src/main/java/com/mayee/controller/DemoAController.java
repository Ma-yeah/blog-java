package com.mayee.controller;

import com.mayee.model.R;
import lombok.extern.java.Log;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;

@Log
@RestController
@RequestMapping("/demoA")
public class DemoAController {

    @GetMapping("/get")
    public R get() {
        return R.data("demoA");
    }

    @GetMapping("/add/header")
    public R addHeader(HttpServletRequest request) {
        Enumeration<String> headerNames = request.getHeaderNames();
        log.info("====== add header ======");
        while (headerNames.hasMoreElements()) {
            String name = headerNames.nextElement();
            String value = request.getHeader(name);
            System.out.printf("%s:%s\n", name, value);
        }
        log.info("====== add header ======");
        return R.success();
    }

    @GetMapping("/remove/header")
    public R removeHeader(HttpServletRequest request) {
        Enumeration<String> headerNames = request.getHeaderNames();
        log.info("====== remove header ======");
        while (headerNames.hasMoreElements()) {
            String name = headerNames.nextElement();
            String value = request.getHeader(name);
            System.out.printf("%s:%s\n", name, value);
        }
        log.info("====== remove header ======");
        return R.success();
    }
}

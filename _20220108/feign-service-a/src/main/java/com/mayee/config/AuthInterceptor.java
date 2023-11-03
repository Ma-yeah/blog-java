package com.mayee.config;

import com.alibaba.fastjson.JSON;
import com.mayee.bean.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public class AuthInterceptor implements HandlerInterceptor {

    private final static List<String> PASS = Arrays.asList("feign-service-b");

    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Map<String, String> header = Collections.list(request.getHeaderNames()).stream().flatMap(name -> {
            Map<String, String> map = new HashMap<>();
            map.put(name, request.getHeader(name));
            return map.entrySet().stream();
        }).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        log.debug("header: {}", JSON.toJSONString(header));
        if (PASS.contains(request.getHeader("auth"))) {
            return true;
        }
        response.getWriter().write(JSON.toJSONString(R.fail("auth fail")));
        return false;
    }
}

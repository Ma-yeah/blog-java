package com.mayee.controller;

import com.alibaba.fastjson2.JSONObject;
import com.mayee.bean.User;
import com.mayee.config.RetryCounter;
import com.mayee.event.RefreshClientEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@RestController
@RequiredArgsConstructor
public class AppController {

    private final User user;
    private final ApplicationEventPublisher publisher;

    @GetMapping("/user/{index}")
    public JSONObject user(@PathVariable Integer index, HttpServletRequest request){
        log.info("请求路径：{}",request.getRequestURI());
        publisher.publishEvent(new RefreshClientEvent(this,index));
        JSONObject ret = new JSONObject();
        ret.put("data",user);
        ret.put("retry_country", RetryCounter.get());
        return ret;
    }
}

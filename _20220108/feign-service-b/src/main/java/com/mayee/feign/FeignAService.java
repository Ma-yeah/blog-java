package com.mayee.feign;

import com.mayee.bean.R;
import com.mayee.bean.User;
import com.mayee.config.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/a")
@FeignClient(value = "feign-service-a", configuration = FeignConfig.class)
public interface FeignAService {

    @GetMapping("/hello")
    R hello(@RequestParam String name);// 参数必须要 @RequestParam 注解，否则报错 405

    @GetMapping("/query")
    R query(@SpringQueryMap User user);// GET 请求传对象参数时，必须加 @SpringQueryMap 注解，否则报错 405

    @PostMapping("/create")
    R create(@RequestBody User user);

    @DeleteMapping("/{id}")
    R delete(@PathVariable Integer id);

    @GetMapping("/error")
    R error();
}

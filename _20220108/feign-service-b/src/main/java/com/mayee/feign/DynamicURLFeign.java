package com.mayee.feign;

import com.mayee.bean.R;
import com.mayee.config.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.net.URI;

/**
 * @Description: 动态指定 url
 * @Author: Bobby.Ma
 * @Date: 2022/2/16 0:47
 */
@RequestMapping("/a")
@FeignClient(name = "dynamicURLFeign", url = "http://127.0.0.1:8888", configuration = FeignConfig.class)
public interface DynamicURLFeign {

    /**
     * @param uri
     * @Description: 在参数中指定 url，请求时将访问参数 uri 的地址
     * @return: java.lang.String
     * @Author: Bobby.Ma
     * @Date: 2022/2/16 0:48
     */
    @GetMapping("/dynamic")
    R dynamic(URI uri);

    @GetMapping("/dynamic1")
    R dynamic1();
}

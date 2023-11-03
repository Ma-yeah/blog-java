package com.mayee.config;

import com.alibaba.fastjson.JSON;
import feign.*;
import feign.codec.DecodeException;
import feign.codec.Decoder;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.lang.reflect.Type;

@Slf4j
public class FeignConfig implements RequestInterceptor, Decoder, ErrorDecoder {

    /**
     * feign 拦截器，用于在发起请求前操作
     */
    @Override
    public void apply(RequestTemplate template) {
        template.header("auth", "feign-service-b");

        Target<?> target = template.feignTarget();

        if ("/a/dynamic1".equals(template.path())) {
            // 要实现动态 url，这一步是必须的
            template.target("http://127.0.0.1:8081");

            // 这一步不是必须的
            target = new Target.HardCodedTarget<>(target.type(), target.name(), target.url());
            template.feignTarget(target);
        }
    }

    /**
     * feign 解码器，用于在获取正常响应前操作
     */
    @Override
    public Object decode(Response response, Type type) throws IOException, DecodeException, FeignException {
        String url = response.request().url();
        String body = Util.toString(response.body().asReader(Util.UTF_8));
        log.debug("url: {}, response: {}", url, body);
        return JSON.parseObject(body, type);
    }

    /**
     * 当调用提供方发生异常，会在这里捕获
     */
    @Override
    public Exception decode(String methodKey, Response response) {// methodKey: FeignAService#error()
        return new RuntimeException("provider throw exception");
    }
}

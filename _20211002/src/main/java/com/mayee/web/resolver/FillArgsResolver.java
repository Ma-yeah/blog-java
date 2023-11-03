package com.mayee.web.resolver;

import com.alibaba.fastjson.JSONObject;
import com.mayee.annotation.FillArgs;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpServletRequest;

/**
    * @Description: 可以用来填充缺失的请求参数，或者直接填充整个对象，比如当前用户
    * @Author: Bobby.Ma
    * @Date: 2021/10/11 20:28
*/
@Slf4j
public class FillArgsResolver implements HandlerMethodArgumentResolver {
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        // 是否启用参数解析
        return parameter.hasParameterAnnotation(FillArgs.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        // 获取 request
        HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
        // do something
        JSONObject object = new JSONObject();
        object.put("id", 1);
        return object.toJavaObject(parameter.getParameterType());
    }
}

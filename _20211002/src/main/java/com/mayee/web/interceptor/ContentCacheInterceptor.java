package com.mayee.web.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
public class ContentCacheInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 此时，还未进入到 controller 中的方法中，因此还没有调用 request 的 getInputStream 方法，所以 getContentAsByteArray 方法没有值
        log.info("====== request uri: {} ======",request.getRequestURI());
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable ModelAndView modelAndView) throws Exception {
        if (request instanceof ContentCachingRequestWrapper) {
            log.info(">>>>>> print request args start  >>>>>>");
            // url 参数
            Map<String, String[]> urlArgsMap = request.getParameterMap();
            String urlArgs = urlArgsMap.entrySet().stream().map(entry -> {
                String v;
                String[] value = entry.getValue();
                if (Objects.isNull(value)) {
                    v = "";
                } else if (value.length == 1) {
                    v = value[0];
                } else {
                    v = Arrays.toString(value);
                }
                return String.format("%s=%s", entry.getKey(), v);
            }).collect(Collectors.joining(","));
            log.info("request url args: ({})", urlArgs);
            byte[] bytes = ((ContentCachingRequestWrapper) request).getContentAsByteArray();
            // 请求体参数
            String bodyArgs = new String(bytes, StandardCharsets.UTF_8);
            log.info("request body args: {}", StringUtils.trimAllWhitespace(bodyArgs));
            log.info(">>>>>> print request args end    >>>>>>");
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable Exception ex) throws Exception {
        if (response instanceof ContentCachingResponseWrapper) {
            log.info("<<<<<< print response args start <<<<<<");
            ContentCachingResponseWrapper responseWrapper = (ContentCachingResponseWrapper) response;
            byte[] bytes = responseWrapper.getContentAsByteArray();
            // 响应体参数
            String bodyArgs = new String(bytes, StandardCharsets.UTF_8);
            log.info("response body args: {}", bodyArgs);
            log.info("<<<<<< print response args end   <<<<<<");
            responseWrapper.copyBodyToResponse();
        }
    }
}

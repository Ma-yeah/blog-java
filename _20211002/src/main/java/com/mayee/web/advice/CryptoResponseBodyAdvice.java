package com.mayee.web.advice;

import com.mayee.annotation.CryptoAdvice;
import com.mayee.web.result.CipherText;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

@Slf4j
@RestControllerAdvice(annotations = CryptoAdvice.class)
public class CryptoResponseBodyAdvice implements ResponseBodyAdvice<Object> {
    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType, Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        log.info("response origin body: {}", body);
        return new CipherText("U2FsdGVkX18fFbYNhghNDR4o74uiS95ZbIs1dqGR50LVvmXavrreAInPfuRIZhVMT3mjzCcPeRa8");
    }
}

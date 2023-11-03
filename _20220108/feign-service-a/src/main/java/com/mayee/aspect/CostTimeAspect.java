package com.mayee.aspect;

import com.mayee.annotations.CostTime;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.reflect.Method;
import java.time.Duration;
import java.time.Instant;

@Slf4j
@Aspect
public class CostTimeAspect {

    @Pointcut("@annotation(com.mayee.annotations.CostTime)")
    public void pointCut() {
    }

    @Around("pointCut()")
    public Object around(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        Method method = ((MethodSignature) proceedingJoinPoint.getSignature()).getMethod();
        CostTime costTime = method.getAnnotation(CostTime.class);
        Instant start = Instant.now();
        Object proceed = proceedingJoinPoint.proceed();
        Instant end = Instant.now();
        long millis = Duration.between(start, end).toMillis();
        log.debug("method: {},cost: {}", method.getName(), millis);
        return proceed;
    }
}

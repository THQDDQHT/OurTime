package com.ourtime.aspect;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Arrays;

@Aspect
@Component
@Slf4j
public class LogAspect {

    @Pointcut("execution(* com.ourtime.controller..*.*(..))")
    public void controllerLog() {
    }

    @Around("controllerLog()")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes != null ? attributes.getRequest() : null;
        
        String url = request != null ? request.getRequestURL().toString() : "UNKNOWN";
        String method = request != null ? request.getMethod() : "UNKNOWN";
        String ip = request != null ? request.getRemoteAddr() : "UNKNOWN";
        String classMethod = joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();
        
        log.info("Request Start: URL=[{}], Method=[{}], IP=[{}], ClassMethod=[{}], Args={}", 
                url, method, ip, classMethod, Arrays.toString(args));
        
        Object result;
        try {
            result = joinPoint.proceed();
        } catch (Throwable e) {
            log.error("Request Error: ClassMethod=[{}], Exception={}", classMethod, e.getMessage());
            throw e;
        }
        
        long timeCost = System.currentTimeMillis() - startTime;
        log.info("Request End: ClassMethod=[{}], TimeCost=[{}ms], Result={}", classMethod, timeCost, result);
        
        return result;
    }
}


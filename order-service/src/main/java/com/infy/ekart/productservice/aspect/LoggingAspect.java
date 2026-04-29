package com.infy.ekart.productservice.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
public class LoggingAspect {

    private static final Logger log = LoggerFactory.getLogger(LoggingAspect.class);

    @Pointcut("execution(* com.infy.ekart.productservice.controller..*.*(..))")
    public void controllerMethods() {}

    @Pointcut("execution(* com.infy.ekart.productservice.service..*.*(..))")
    public void serviceMethods() {}

    @Pointcut("execution(* com.infy.ekart.productservice.repository..*.*(..))")
    public void repositoryMethods() {}

    @Around("controllerMethods() || serviceMethods() || repositoryMethods()")
    public Object logMethod(ProceedingJoinPoint joinPoint) throws Throwable {
        String className = joinPoint.getSignature().getDeclaringTypeName();
        String methodName = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();

        log.info("▶ Enter: {}.{}() - Args: {}", className, methodName, Arrays.toString(args));
        
        long start = System.currentTimeMillis();
        
        try {
            Object result = joinPoint.proceed();
            long timeMs = System.currentTimeMillis() - start;
            
            if (timeMs > 1000) {
                log.warn("⏱ SLOW: {}.{}() - {}ms", className, methodName, timeMs);
            } else {
                log.info("✓ Exit: {}.{}() - {}ms", className, methodName, timeMs);
            }
            
            return result;
        } catch (Exception e) {
            log.error("✗ ERROR: {}.{}() - {}", className, methodName, e.getMessage(), e);
            throw e;
        }
    }
}
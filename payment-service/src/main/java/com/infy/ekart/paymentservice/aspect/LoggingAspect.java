package com.infy.ekart.paymentservice.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {

    private static final Logger log = LoggerFactory.getLogger(LoggingAspect.class);

    @Pointcut("execution(* com.infy.ekart.paymentservice.service..*.*(..))")
    public void serviceMethods() {}

    @Pointcut("execution(* com.infy.ekart.paymentservice.controller..*.*(..))")
    public void controllerMethods() {}

    @Around("serviceMethods() || controllerMethods()")
    public Object logMethod(ProceedingJoinPoint joinPoint) throws Throwable {
        String className = joinPoint.getSignature().getDeclaringTypeName();
        String methodName = joinPoint.getSignature().getName();

        log.info("▶ Enter: {}.{}()", className, methodName);
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
            log.error("✗ ERROR: {}.{}() - {}", className, methodName, e.getMessage());
            throw e;
        }
    }
}
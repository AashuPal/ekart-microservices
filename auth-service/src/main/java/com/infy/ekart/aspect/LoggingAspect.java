package com.infy.ekart.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class LoggingAspect {

    @Around("execution(* com.infy.ekart.service..*(..))")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        log.info("Entering: {} with args {}", joinPoint.getSignature(), joinPoint.getArgs());
        Object result = joinPoint.proceed();
        log.info("Exiting: {} with result {}", joinPoint.getSignature(), result);
        return result;
    }
}
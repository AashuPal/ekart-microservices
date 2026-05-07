package com.infy.ekart.cartservice.aspect;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class PerformanceMonitoringAspect {

    private final MeterRegistry meterRegistry;

    // Fixed constructor - assigns the parameter to the field
    public PerformanceMonitoringAspect(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;  // This was missing!
    }

    @Around("@annotation(io.micrometer.core.annotation.Timed)")
    public Object measureExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        Timer.Sample sample = Timer.start(meterRegistry);
        
        String className = joinPoint.getSignature().getDeclaringTypeName();
        String methodName = joinPoint.getSignature().getName();
        
        try {
            return joinPoint.proceed();
        } finally {
            sample.stop(Timer.builder("method.execution.time")
                .description("Method execution time")
                .tag("class", className)
                .tag("method", methodName)
                .publishPercentileHistogram(true)
                .register(meterRegistry));
        }
    }
}
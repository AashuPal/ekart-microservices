package com.infy.ekart.cartservice.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Aspect
@Component
@Slf4j
public class TransactionAspect {
	private static final Logger log = LoggerFactory.getLogger(TransactionAspect.class);

    @Around("@annotation(org.springframework.transaction.annotation.Transactional)")
    public Object manageTransaction(ProceedingJoinPoint joinPoint) throws Throwable {
        boolean isTransactionActive = TransactionSynchronizationManager.isActualTransactionActive();
        
        log.debug("Transaction active: {} for method: {}", 
            isTransactionActive, joinPoint.getSignature().getName());
        
        try {
            Object result = joinPoint.proceed();
            log.debug("Transaction completed successfully for: {}", 
                joinPoint.getSignature().getName());
            return result;
        } catch (Exception e) {
            log.error("Transaction failed for: {} - {}", 
                joinPoint.getSignature().getName(), e.getMessage());
            throw e;
        }
    }
}
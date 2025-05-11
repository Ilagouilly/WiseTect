package com.wisetect.architectureservice.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Aspect for auditing actions performed in the ArchitectureService.
 * Logs method names and arguments for all methods in the service.
 */
@Aspect
@Component
public class ArchitectureActionAuditAspect {
    private static final Logger AUDIT_LOGGER = LoggerFactory.getLogger("AUDIT_LOGGER");

    /**
     * Logs the method name and arguments of any method executed in the
     * ArchitectureService.
     *
     * @param joinPoint provides reflective access to the method being executed.
     */
    @Before("execution(* com.wisetect.architectureservice.service.ArchitectureService.*(..))")
    public void logArchitectureAction(JoinPoint joinPoint) {
        String methodName = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();

        // Basic audit logging with method name and arguments
        AUDIT_LOGGER.info("Timestamp={}, Class={}, Method={}, Arguments={}",
                System.currentTimeMillis(),
                joinPoint.getTarget().getClass().getSimpleName(),
                methodName,
                args.length > 0 ? args[0] : "No arguments");
    }
}

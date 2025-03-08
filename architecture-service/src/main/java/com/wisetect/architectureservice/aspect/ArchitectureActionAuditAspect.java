package com.wisetect.architectureservice.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class ArchitectureActionAuditAspect {
    private static final Logger AUDIT_LOGGER = LoggerFactory.getLogger("AUDIT_LOGGER");

    @Before("execution(* com.wisetect.architectureservice.service.ArchitectureService.*(..))")
    public void logArchitectureAction(JoinPoint joinPoint) {
        String methodName = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();

        // Basic audit logging with method name and arguments
        AUDIT_LOGGER.info("Architecture Action: Method={}, Arguments={}", methodName,
                args.length > 0 ? args[0] : "No arguments");
    }
}

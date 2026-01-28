package ro.upb.acs.fleetman;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class ResponseLoggingAspect {

    private static final Logger logger = LoggerFactory.getLogger(ResponseLoggingAspect.class);

    @Around("@annotation(ro.upb.acs.fleetman.config.LogResponse)")
    public Object logResponse(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();

        long startTime = System.currentTimeMillis();

        try {
            Object response = joinPoint.proceed();
            long endTime = System.currentTimeMillis();
            long executionTime = endTime - startTime;

            logger.info("API Response - Class: {}, Method: {}, ExecutionTime: {}ms, Status: SUCCESS, response: {}",
                    className, methodName, executionTime, response);

            return response;
        } catch (Exception e) {
            long endTime = System.currentTimeMillis();
            long executionTime = endTime - startTime;

            logger.error("API Response - Class: {}, Method: {}, ExecutionTime: {}ms, Status: ERROR, Exception: {}",
                    className, methodName, executionTime, e.getMessage(), e);

            throw e;
        }
    }
}

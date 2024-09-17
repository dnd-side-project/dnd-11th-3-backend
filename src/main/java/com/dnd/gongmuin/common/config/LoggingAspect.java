package com.dnd.gongmuin.common.config;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Objects;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Aspect
@Slf4j
@Component
public class LoggingAspect {

	@Pointcut("execution(* com.dnd.gongmuin..controller..*(..))")
	public void controllerPointcut() {
	}

	@Pointcut("execution(* com.dnd.gongmuin..service..*(..))")
	public void servicePointcut() {
	}

	@Around("controllerPointcut() || servicePointcut()")
	public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
		long start = System.currentTimeMillis();
		log.info("START: {}", joinPoint.toString());
		try {
			return joinPoint.proceed();
		} finally {
			long end = System.currentTimeMillis();
			long timeMs = end - start;
			log.info("END: {} {}ms", joinPoint.toString(), timeMs);
		}
	}

	@Before("controllerPointcut()")
	public void logRequestArgs(JoinPoint joinPoint) throws Throwable {
		Method method = getMethod(joinPoint);
		String methodName = method.getName();
		log.info("===== {} Request Detail START =====", methodName);
		Arrays.stream(joinPoint.getArgs())
			.filter(Objects::nonNull)
			.forEach(arg -> {
				log.info("type: {},  value: {}", arg.getClass().getSimpleName(), arg);
			});
		log.info("===== {} Request Detail END =====", methodName);
	}

	@AfterReturning(value = "servicePointcut()", returning = "returnValue")
	public void logResponseDetails(JoinPoint joinPoint, Object returnValue) throws Throwable {
		Method method = getMethod(joinPoint);
		String methodName = method.getName();

		log.info("===== {} Response Detail START =====", methodName);
		if (returnValue == null) {
			log.info("null 값 반환 오류 발생!!!");
			return;
		}

		log.info("Detail: {}", returnValue.toString());
		log.info("===== {} Response Detail END =====", methodName);

	}

	private Method getMethod(JoinPoint joinPoint) {
		MethodSignature signature = (MethodSignature)joinPoint.getSignature();
		return signature.getMethod();
	}

}

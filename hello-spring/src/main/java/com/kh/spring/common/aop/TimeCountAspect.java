package com.kh.spring.common.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@Aspect
public class TimeCountAspect {
	@Pointcut("execution(* com.kh.spring.memo.controller..insertMemo(..))")
	public void insertMemoPointcut() {}
	
	
	@Around("insertMemoPointcut()")
	public Object logger(ProceedingJoinPoint joinPoint) throws Throwable {
		StopWatch stopWatch = new StopWatch("메세지 등록");
		
		stopWatch.start("메세지 등록");
		
		Object returnObj = joinPoint.proceed();
		
		stopWatch.stop();
		long milli = stopWatch.getTotalTimeMillis();
		
		log.debug("milli = {}", milli);
		log.debug("stopWatch = {}",stopWatch.toString());
		
		return returnObj;
	}
}

package com.kh.spring.log;

import org.apache.log4j.Logger;

/**
 * Logging
 * - 콘솔로그 : System.out.printxx 보다 효율적인 로그 관리가 가능하다
 * - 파일로그
 * 
 * Level(Priority) 우선순위
 * - fatal : 아주 심각한 에러 발생
 * - error : 요청 처리중 오류 발생
 * - warn : 경고성 메세지. 현재 실행에는 문제가 없지만, 향후 잠재적 오류가 될 가능성 있음
 * - info
 * - debug
 * - trace
 *
 */
public class Log4jTest {
	// org.apache.log4j.Logger
	private static final Logger log = Logger.getLogger(Log4jTest.class);
	
	public static void main(String[] args) {
		log.fatal("fatel");
		log.error("error");
		log.warn("warn");
		log.info("info");
		log.debug("debug");
		log.trace("trace");
	}

}

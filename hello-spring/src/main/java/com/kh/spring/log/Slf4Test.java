package com.kh.spring.log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.extern.slf4j.Slf4j;

/**
 * PSA (Portable Service Abstraction)
 *
 */
@Slf4j
public class Slf4Test {

	//private static final Logger log = LoggerFactory.getLogger(Slf4Test.class);
	
	public static void main(String[] args) {
		//log.fatal("fatal"); // slf4j에는 fatal레벨이 없다.
		log.error("error - {}", "메세지메세지");
		log.warn("warn");
		log.info("info");
		log.debug("debug");
		log.trace("trace");
		
	}

}

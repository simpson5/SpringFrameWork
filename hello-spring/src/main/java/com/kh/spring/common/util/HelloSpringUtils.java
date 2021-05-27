package com.kh.spring.common.util;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class HelloSpringUtils {

	/**
	 * test.jpg
	 * 
	 * @param originalFilename
	 * @return
	 */
	public static String getRenamedFilename(String originalFilename) {
		//확장자 추출
		int beginIndex = originalFilename.lastIndexOf("."); // 4
		String ext = originalFilename.substring(beginIndex);// .jpg
		
		//년월일_난수 format
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmssSSS_");
		DecimalFormat df = new DecimalFormat("000"); // 정수부 3자리
		
		return sdf.format(new Date()) + df.format(Math.random() * 1000) + ext;
	}

}

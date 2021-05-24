package com.kh.spring.common.interceptor;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.log;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.web.servlet.FlashMap;
import org.springframework.web.servlet.FlashMapManager;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import org.springframework.web.servlet.support.RequestContextUtils;

import com.kh.spring.member.model.vo.Member;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LoginInterceptor extends HandlerInterceptorAdapter {
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		//로그인 여부를 체크해서 로그인하지 않은 경우, return false
		HttpSession session = request.getSession();
		Member loginMember = (Member) session.getAttribute("loginMember");
		if(loginMember == null) {
			//FlashMap을 통해서 redirect후 사용자피드백 전달하기
			FlashMap flashMap = new FlashMap();
			flashMap.put("msg", "로그인 해라");
			FlashMapManager manager = RequestContextUtils.getFlashMapManager(request);
			manager.saveOutputFlashMap(flashMap, request, response);
			
			//로그인후 최종 이동할 url을 session 속성 next 저장
			String url = request.getRequestURL().toString();
			session.setAttribute("next", url); //http://localhost:9090/spring/member/mmeberDetail.do
			
			response.sendRedirect(request.getContextPath()+"/member/memberLogin.do");
			return false;
		}
		
		return super.preHandle(request, response, handler);
	}

}

package com.kh.spring.member.controller;

import java.beans.PropertyEditor;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.FlashMap;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.support.RequestContextUtils;
import org.springframework.web.servlet.view.RedirectView;

import com.kh.spring.member.model.service.MemberService;
import com.kh.spring.member.model.vo.Member;

import lombok.extern.slf4j.Slf4j;

/**
 * Model
 * MVC패턴의 Model이 아니다.
 * view단에 전달하기 위한 데이터를 저장하는 Map
 * 
 * 1. Model <<interface>>
 * 		- addAttribute(name, value)
 * 		- viewName : String을 리턴
 * 2. ModelMap
 * 		- Model <<interface>>의 구현체
 * 		- addAttribute(name, value)
 *		- viewName : String을 리턴
 * 3. ModelAndView
 * 		- addObject(name, value)
 * 		- setViewName를 이용해서 viewName설정
 * 		- ModelAndView객체를 리턴
 * 
 * -> 결국은 ModelAndView로 통합되서 spring container에 의해 관리된다.
 * 
 * @ModelAttribute | @SessionAttribute
 * - handler의 매개변수 앞에 사용
 * - 모델에 저장된 속성에 대한 getter
 * - name, required(기본값 : true) 지정이 가능하다.
 * 
 * @ModelAttribute
 * - method레벨에 작성
 * - 이 메소드에서 model속성을 setter
 * - 현재 controller클래스의 모든 handler에 앞서 실행되며, 모든 요청에서 해당 데이터 접근 가능
 *
 */
@Controller
@RequestMapping("/member")
@Slf4j
@SessionAttributes({"loginMember", "next"})
public class MemberController {
	//private static final Logger log = LoggerFactory.getLogger(MemberController.class);
	
	@Autowired
	private MemberService memberService;
	
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	
	/**
	 * view단에서는 ${common.adminEmail} 사용가능
	 * 
	 */
	@ModelAttribute("common")
	public Map<String, Object> common(){
		log.info("@ModelAttribute(\"common\")");
		Map<String, Object> map = new HashMap<>();
		map.put("adminEmail", "admin@kh.or.kr");
		map.put("adminPhone", "01099991111");
		
		return map;
	}
	
	@GetMapping("/memberEnroll.do")
	public void memberEnroll() {
		// /WEB-INF/views/member/memberEnroll.jsp 로 자동 포워딩됨.
		// DefaultRequestToViewNameTranslator빈이 요청주소에서 viewName을 유추함.
	}
	
	@PostMapping("/memberEnroll.do")
	public String memberEnroll(Member member, RedirectAttributes redirectAttr) {
		log.info("member = {}", member);

		try {
			// 0. 비밀번호 암호화 처리
			String rawPassword = member.getPassword();
			String encodedPassword = bCryptPasswordEncoder.encode(rawPassword);
			member.setPassword(encodedPassword);
			log.info("member(암호화처리이후) = {}", member);
			
			// 1. 업무로직
			int result = memberService.insertMember(member);
			
			// 2. 사용자 피드백
			redirectAttr.addFlashAttribute("msg", "회원가입성공");
		} catch(Exception e) {
			log.error("회원가입 오류!", e);
			throw e;
		}
		
		return "redirect:/";
	}
	
	/**
	 * java.sql.Date, java.util.Date 필드에 값대입시
	 * 사용자 입력값이 ""인 경우, null로 처리될 수 있도록 설정
	 * @param binder
	 */
	@InitBinder
	public void initBinder(WebDataBinder binder) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		//org.springframework.beans.propertyeditors.CustomDateEditor.CustomDateEditor(DateFormat dateFormat, boolean allowEmpty)
		PropertyEditor editor = new CustomDateEditor(format, true);
		binder.registerCustomEditor(Date.class, editor);
	}
	
	@GetMapping("/memberLogin.do")
	public void memberLogin(
			@SessionAttribute(required = false) String next,
				@RequestHeader(name = "Referer", required = false) String referer,
				Model model) {
		log.info("referer={}", referer);
		if(next != null && referer != null)
			model.addAttribute("next", referer); //sessionScope에 저장
	}
	
	@PostMapping("/memberLogin.do")
	public String memberLogin(
			@RequestParam String id,
			@RequestParam String password,
			@SessionAttribute(required = false) String next,
			RedirectAttributes redirectAttr,
			Model model
		) {
		//1. 업무로직
		Member member = memberService.selectOneMember(id);
		log.info("member = {}",member);
		//log.info("encryptedPassword = {}",bCryptPasswordEncoder.encode(password));
		
		//2. 로그인여부 분기처리
		//boolean org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder.matches(CharSequence rawPassword, String encodedPassword)
		if(member != null && bCryptPasswordEncoder.matches(password, member.getPassword())) {
			// 로그인 성공
			// loginMember 세션속성으로 저장하려면, class에 @SessionAttributes로 등록
			model.addAttribute("loginMember", member);
			//사용한 next값은 제거
			model.addAttribute("next", null);
		}
		else {
			// 로그인 실패
			redirectAttr.addFlashAttribute("msg", "아이디 또는 비밀번호가 틀립니다.");
			return "redirect:/member/memberLogin.do";
		}
		
		return "redirect:" + (next != null ? next : "/");
	}
	
	/**
	 * @SessionAttributes를 통해서 등록한 session속성은
	 * SessionStatus객체에 대해서 complete 처리해야한다.
	 * 
	 * @return
	 */
	@GetMapping("/memberLogout.do")
	public String memberLofgout(SessionStatus status) {
		if(!status.isComplete()) {
			status.setComplete();
		}
		return "redirect:/";
	}
	
	/**
	 * 로그인한 사용자 정보 열람하기
	 */
	@GetMapping("/memberDetail.do")
	public ModelAndView memberDetail(
			ModelAndView mav,
			@SessionAttribute(name = "loginMember"
		) Member loginMember) {
		log.info("loginMember = {}", loginMember);
		//속성 저장
		mav.addObject("time", System.currentTimeMillis());
		//viewName 설정
		mav.setViewName("member/memberDetail");
		return mav;
	}
	
	@PostMapping("/memberUpdate.do")
	public ModelAndView memberUpdate(
								@ModelAttribute Member member,
								@ModelAttribute("loginMember") Member loginMember,
								ModelAndView mav, 
								HttpServletRequest request
							) {
		log.debug("member = {}", member);
		log.debug("loginMember = {}", loginMember);
		
		try {
			//1. 업무로직
			int result = memberService.updateMember(member);
			
			//2. 사용자 피드백 & 리다이렉트
			//mav.setViewName("redirect:/member/memberDetail.do");
			
			//리다이렉트시 자동생성되는 queryString 방지
			RedirectView view = new RedirectView(request.getContextPath() + "/member/memberDetail.do");
			view.setExposeModelAttributes(false);
			mav.setView(view);
			
			//ModelAndView와 RedirectAttributes 충돌시 FlashMap을 직접 사용
			FlashMap flashMap = RequestContextUtils.getOutputFlashMap(request);
			flashMap.put("msg", "사용자 정보 수정 성공!!!!!!");
//			redirectAttr.addFlashAttribute("msg", "사용자 정보 수정 성공!");
			
		} catch (Exception e) {
			log.error("사용자 정보 수정 오류!", e);
			throw e;
		}
		return mav;
	}
	
	/**
	 * spring ajax(json)
	 * 1. gson - 응답메세지에 json문자열을 직접 출력
	 * 2. jsonView빈을 통해 처리하기 - model에 담긴 테이터를 json으로 변환, 응답에 출력
	 * 3. @ResponseBody - 리턴된 자바객체를 json으로 변환, 응답에 출력
	 * 4. ResponseEntity<Map<String, Object>>
	 * 
	 * jsonView방식
	 * 
	 * @param id
	 * @return
	 */
	@GetMapping("/checkIdDuplicate1.do")
	public String checkIdDuplicate1(@RequestParam String id, Model model) {
		//1. 업무로직
		Member member = memberService.selectOneMember(id);
		boolean available = member == null;
		//2. Model에 속성 저장
		model.addAttribute("available", available);
		model.addAttribute("id", id);
		
		return "jsonView";
	}
	
	@GetMapping("/checkIdDuplicate2.do")
	@ResponseBody
	public Map<String, Object> checkIdDuplicate2(@RequestParam String id) {
		//1. 업무로직
		Member member = memberService.selectOneMember(id);
		boolean available = member == null;
		//2. map에 요소 저장후 리턴
		Map<String, Object> map = new HashMap<>();
		map.put("available", available);
		map.put("id", id);

		
		return map;
	}
	
	@GetMapping("/checkIdDuplicate3.do")
	public ResponseEntity<Map<String, Object>> checkIdDuplicate3(@RequestParam String id) {
		//1. 업무로직
		Member member = memberService.selectOneMember(id);
		boolean available = (member == null);
		//2. map에 요소 저장후 리턴
		Map<String, Object> map = new HashMap<>();
		map.put("available", available);
		map.put("id", id);

		
		return ResponseEntity
				.ok()
				.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_UTF8_VALUE)
				.body(map);
	}
}

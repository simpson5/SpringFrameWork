package com.kh.spring.memo.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.kh.spring.memo.model.service.MemoService;
import com.kh.spring.memo.model.vo.Memo;

import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
@RequestMapping("/memo")
public class MemoController {
	
	@Autowired
	MemoService memoService;
	
	@GetMapping("/memo.do")
	public ModelAndView memoList(ModelAndView mav) {
		log.debug("memoController = {}", this.getClass());
		log.debug("memoService = {}", memoService.getClass());
		
		List<Memo> memoList = memoService.memoList();
		log.debug("memo = {}", memoList);
		mav.addObject("memoList", memoList);
		mav.setViewName("memo/memo");
		return mav;
	}
	
	@PostMapping("/insertMemo.do")
	public String insertMemo(
							@RequestParam String memo,
							RedirectAttributes redirectAttr
						) {
		log.debug("memo = {}", memo);
		try {
			int result = memoService.insertMemo(memo);
			redirectAttr.addAttribute("msg", "메모 등록 성공");
		} catch(Exception e) {
			log.error("메모 작성 오류!", e);
			throw e;
		}
		
		return "redirect:/memo/memo.do";
	}
}

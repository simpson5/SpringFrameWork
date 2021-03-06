package com.kh.spring.board.controller;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.kh.spring.board.model.service.BoardService;
import com.kh.spring.board.model.vo.Attachment;
import com.kh.spring.board.model.vo.Board;
import com.kh.spring.board.model.vo.BoardExt;
import com.kh.spring.common.util.HelloSpringUtils;
import com.kh.spring.common.util.pageUtils;
import com.kh.spring.member.model.vo.Member;

import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("/board")
@Slf4j
public class BoardController {
	@Autowired
	private ServletContext application;

	@Autowired
	private ResourceLoader resourceLoader;
	
	@Autowired
	private BoardService boardService;
	
	@GetMapping("/boardList.do")
	public String boardList(
					@RequestParam(required = true, defaultValue = "1") int cPage,
					Model model,
					HttpServletRequest request) {
		try {
			log.debug("cpage = {}", cPage);
			final int limit = 10;
			final int offset = (cPage - 1) * limit;
			Map<String, Object> param = new HashMap<>();
			param.put("limit", limit);
			param.put("offset", offset);
			
			List<Board> list = boardService.selectBoardExt(param);
			model.addAttribute("list", list);
			
			int totalContents = boardService.countBoardList();
			String url = request.getRequestURL().toString();
			
			String pageBar = pageUtils.getPageBar(
					cPage, limit, totalContents, url
					);
			model.addAttribute("pageBar", pageBar);
		} catch(Exception e) {
			log.error("????????? ?????? ??????", e);
			throw e;
		}
		
		return "board/boardList";
	}
	
	@GetMapping("/boardForm.do")
	public void boardForm() {
		
	}
	
	@PostMapping("/boardEnroll.do")
	public String boardEnroll(
					@ModelAttribute BoardExt board,
					@RequestParam(name = "upFile") MultipartFile[] upFiles,
					RedirectAttributes redirectAttr
				) throws Exception {
		try {
			log.debug("board = {}", board);
			//1. ?????? ?????? : ???????????? /resources/upload/board
			// pageContext:PageContext - request:HttpServletRequest - session:HttpSession - application:servletContext
			String saveDirectory = application.getRealPath("/resources/upload/board");
			
			//???????????? ??????
			File dir = new File(saveDirectory);
			if(!dir.exists()) dir.mkdirs(); // ???????????? ??????????????? ??????
			
			List<Attachment> attachList = new ArrayList<Attachment>();
			
			for(MultipartFile upFile : upFiles) {
				//input[name=upFile]????????? ???????????? upFile??? ????????????.
				if(upFile.isEmpty()) continue;
				
				String renamedFilename =
						HelloSpringUtils.getRenamedFilename(upFile.getOriginalFilename());
				
				//a. ?????????????????? ??????
				File dest = new File(saveDirectory, renamedFilename);
				upFile.transferTo(dest); // ????????????
				
				//b. ????????? ???????????? Attachment????????? ?????? ??? list??? ??????
				Attachment attach = new Attachment();
				attach.setOriginalFilename(upFile.getOriginalFilename());
				attach.setRenamedFilename(renamedFilename);
				attachList.add(attach);
			}
			
			log.debug("attachList = {}", attachList);
			//board????????? ??????
			board.setAttachList(attachList);
			
			//2. ???????????? : db?????? board, attachment
			int result = boardService.insertBoard(board);
			
			//3. ?????????????????? & ???????????????
			redirectAttr.addFlashAttribute("msg", "????????? ?????? ??????");
		} catch(Exception e) {
			log.error("????????? ?????? ??????",e);
			throw e;
		}
		return "redirect:/board/boardDetail.do?no" + board.getNo();
	}
	
	@GetMapping("/boardDetail.do")
	public void selectOneBoard(@RequestParam int no, Model model) {
		//1. ????????????
		//BoardExt board = boardService.selectOneBoard(no);
		//List<Attachment> attachList = boardService.selectAttachList(board.getNo());
		BoardExt board = boardService.selectOneBoardCollection(no);
		
		//2. jsp??? ??????
		model.addAttribute("board", board);
		//model.addAttribute("attachList", attachList);
		
	}
	
	/**
	 * ResponseEntity
	 * 1. status code ??????????????????
	 * 2. ?????? header ??????????????????
	 * 3. @ResponseBody ?????? ??????
	 * 
	 * @return
	 * @throws UnsupportedEncodingException 
	 */
	@GetMapping("/fileDownload.do")
	public ResponseEntity<Resource> fileDownoadWithResponseEntity(@RequestParam int no) throws UnsupportedEncodingException{
		ResponseEntity<Resource> responseEntity = null;
		try {
			//1. ????????????
			Attachment attach = boardService.selectOneAttachment(no);
			if(attach == null) {
				return ResponseEntity.notFound().build();
			}
			
			//2. Resource??????
			String saveDirectory = application.getRealPath("/resources/upload/board");
			File downFile = new File(saveDirectory, attach.getRenamedFilename());
			Resource resource = resourceLoader.getResource("file:"+downFile);
			String filename = new String(attach.getOriginalFilename().getBytes("utf-8"), "iso-8859-1");
			
			//3. ResponseEntity?????? ?????? ??? ??????
			//builder??????
			responseEntity =
					ResponseEntity
						.ok()
						.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE)
						.header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + filename)
						.body(resource);
		} catch (Exception e) {
			log.error("?????????????????? ??????", e);
			throw e;
		}
		
		return responseEntity;
	}
	
	/*
	 * @GetMapping( value = "/fileDownload.do", produces =
	 * MediaType.APPLICATION_OCTET_STREAM_VALUE )
	 */
	@ResponseBody // ?????????????????? return????????? ?????? ??????
	public Resource fileDownload(@RequestParam int no, HttpServletResponse response) throws UnsupportedEncodingException {
		//1. ???????????? : db?????? ???????????? ?????? ??????
		Attachment attach = boardService.selectOneAttachment(no);
		log.debug("attach = {}", attach);
		if(attach == null) {
			throw new IllegalArgumentException("?????? ??????????????? ???????????? ????????????. : " + no);
		}
		
		//2. Resource????????? ?????? : ????????????????????? ????????? spring-container??? ??????
		String originalFilename = attach.getOriginalFilename();
		//String originalFilename = "string.html";
		String renamedFilename = attach.getRenamedFilename();
		String saveDirectory = application.getRealPath("/resources/upload/board");
		File downFile = new File(saveDirectory, renamedFilename);
		//????????????, ???????????????????????? ?????? ????????? ?????? ???????????? ????????? layer
		String location = "file:" + downFile.toString();
		//String location = "https://docs.oracle.com/javase/8/docs/api/java/lang/String.html";
		log.debug("logaction = {}", location);
		Resource resource = resourceLoader.getResource(location);
		
		//3. ????????????
		//???????????? ????????????
		originalFilename = new String(originalFilename.getBytes("utf-8"), "iso-8859-1");
		//response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
		response.addHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + originalFilename);
		
		return resource;
		
	}
	
	@GetMapping("/boardSearch.do")
	public String boardSearch(@RequestParam String key, Model model) {
		//1. ????????????
		List<Board> boardList = boardService.boardSearch(key);
		//2. Model??? ?????? ??????
		model.addAttribute("boardList", boardList);
		
		return "jsonView";
	}
}

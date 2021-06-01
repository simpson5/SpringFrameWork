package com.kh.spring.menu.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kh.spring.menu.model.service.MenuService;
import com.kh.spring.menu.model.vo.Menu;
import com.kh.spring.menu.model.vo.MenuType;

import lombok.extern.slf4j.Slf4j;

/**
 * @RestController = @Controller + @RespnoseBody
 * 
 * 모든 handler는 @RespnoseBody로 처리된다. json으로 변환
 *
 */
@RestController
@Slf4j
public class MenuController {

	@Autowired
	private MenuService menuService;
	
	@GetMapping("/menus")
	public List<Menu> menus(){
		try {
			//1. 업무로직
			List<Menu> list = menuService.selectMenuList();
			log.debug("list = {}", list);
			//2. 리턴 @RespsonseBody에 의해서 json변환후, 응답출력 처리
			
			return list;
		} catch (Exception e) {
			log.error("/menus 오류!", e);
			throw e;
		}
	}
	
	/**
	 * @PathVariable 경로변수
	 * @return
	 */
	@GetMapping("/menus/{type}/{taste}")
	public List<Menu> menuByType(@PathVariable String type, @PathVariable String taste){
		try {
			log.debug("tyep = {}", type);
			Map<String, Object> param = new HashMap<>();
			param.put("type", type);
			param.put("taste", taste);
			
			List<Menu> list = menuService.selectMenuListByTypeAndTaste(param);
			log.debug("list = {}", list);
			return list;
		} catch (Exception e) {
			log.error("/menus 오류!", e);
			throw e;
		}
	}
	
	//@PostMapping("/menu")
	public int menuEnroll(@ModelAttribute Menu menu, @RequestParam String type_x) {
		log.debug("type_x = {}", type_x);
		menu.setType(MenuType.menuTypeValueOf(type_x));
		log.debug("menu = {}", menu);
		return menuService.menuEnroll(menu); 
	}
	
	/**
	 * @RequestBody 요청메세지의 body에 작성된 json문자열을 java객체로 변환
	 * 
	 * @param menu
	 * @return
	 */
	@PostMapping("/menu")
	public Map<String, Object> insertMenu(@RequestBody Menu menu) {
		try {
			log.debug("menu = {}", menu);
			int reulst = menuService.menuEnroll(menu);
			Map<String, Object> map = new HashMap<>();
			map.put("msg", "메뉴 등록 성공");
			return map;
		} catch (Exception e) {
			log.error("메뉴 등록 실패!", e);
			throw e;
		}
	}
	
	@GetMapping("/menus/{id}")
	public Menu menuId(@PathVariable String id){
		try {
			log.debug("id = {}", id);
			Menu menu = menuService.selectMenuId(id);
			log.debug("menu = {}", menu);
			return menu;
		} catch (Exception e) {
			log.error("/menus 오류!", e);
			throw e;
		}
	}
	
	@PutMapping("/menus/update")
	public Map<String, Object> updateMenu(@RequestBody Menu menu) {
		try {
			log.debug("menu = {}", menu);
			int reulst = menuService.menuUpdate(menu);
			Map<String, Object> map = new HashMap<>();
			map.put("msg", "메뉴 수정 성공");
			return map;
		} catch (Exception e) {
			log.error("메뉴 수정 실패!", e);
			throw e;
		}
	}
}
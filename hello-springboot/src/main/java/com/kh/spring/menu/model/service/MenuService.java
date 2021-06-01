package com.kh.spring.menu.model.service;

import java.util.List;
import java.util.Map;

import com.kh.spring.menu.model.vo.Menu;

public interface MenuService {

	List<Menu> selectMenuList();

	List<Menu> selectMenuListByTypeAndTaste(Map<String, Object> param);

	int menuEnroll(Menu menu);

	Menu selectMenuId(String id);

	int menuUpdate(Menu menu);

}

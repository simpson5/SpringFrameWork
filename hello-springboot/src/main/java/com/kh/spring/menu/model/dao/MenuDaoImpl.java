package com.kh.spring.menu.model.dao;

import java.util.List;
import java.util.Map;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.kh.spring.menu.model.vo.Menu;

@Repository
public class MenuDaoImpl implements MenuDao {

	@Autowired
	private SqlSessionTemplate session;

	@Override
	public List<Menu> selectMenuList() {
		return session.selectList("menu.selectMenuList");
	}

	@Override
	public List<Menu> selectMenuListByTypeAndTaste(Map<String, Object> param) {
		return session.selectList("menu.selectMenuListByTypeAndTaste", param);
	}

	@Override
	public int menuEnroll(Menu menu) {
		return session.insert("menu.menuEnroll", menu);
	}

	@Override
	public Menu selectMenuId(String id) {
		return session.selectOne("menu.selectMenuId", id);
	}

	@Override
	public int menuUpdate(Menu menu) {
		return session.update("menu.menuUpdate", menu);
	}
	
}

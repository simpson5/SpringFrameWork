package com.kh.spring.menu.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Menu {
	private int id;
	private String restaurant;
	private String name;
	private int price;
	private MenuType type; // kr, cj ,jp
	private String taste; // mild, hot
}

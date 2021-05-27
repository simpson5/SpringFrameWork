package com.kh.spring.common.util;

public class pageUtils {
	/**
	 *  1. cPage
	 *  2. numPerPage -> limit
	 *  3. totalContents 총 컨텐츠수
	 *  4. url 이동할 주소
	 *  --------------------------------------------
	 * 	5. totalPage 전체페이지수 - pageNo 넘침 방지
	 * 	6. pageBarSize 페이지바에 표시할 페이지 개수 지정 : 5
	 * 	7. pageStart ~ pageEnd pageNo의 범위
	 * 	8. pageNo 페이지넘버를 출력할 증감변수
	 */
	public static String getPageBar(int cPage, int numPerPage, int totalContents, String url) {
		// 메모리 상에서 좀더 이득
		StringBuilder pageBar = new StringBuilder();
		int totalPage = (int) Math.ceil((double) totalContents / numPerPage);
		int pageBarSize = 4;
		int pageStart = (cPage - 1) / pageBarSize * pageBarSize + 1;
		int pageEnd = pageStart + pageBarSize - 1;
		url += "?cPage=";
		int pageNo = pageStart;
		
		
		// 1. 이전영역
		if (pageNo == 1) {
			//이전버튼 비활성화
		} else {
			//이전버튼 활성화
			pageBar.append(
					"<li class=\"page-item\">" +
				    "<a class=\"page-link\" href='" + url + (pageNo - 1) + "' aria-label=\"Previous\">" +
				    "<span aria-hidden=\"true\">&laquo;</span>" + 
				    "<span class=\"sr-only\">Previous</span></a></li>"
			);
		}

		// 2. pageNo 영역
		while (pageNo <= pageEnd && pageNo <= totalPage) {
			if (pageNo == cPage) {
				//링크 비활성화 - 현제페이지
				pageBar.append("<li class=\"page-item active\"><a class=\"page-link\" href='#'>"+ pageNo +"</a></li>");
			} else {
				//링크 활성화
				pageBar.append("<li class=\"page-item\"><a class=\"page-link\" href=" + url + pageNo + ">"+ pageNo +"</a></li>");
			}
			pageNo++;
		}

		// 3. 다음영역
		if (pageNo > totalPage) {
			//다음버튼 비활성화
		} else {
			//다은버튼 활성화
			pageBar.append(
					"<li class=\"page-item\">" +
				    "<a class=\"page-link\" href='" + url + pageNo + "' aria-label=\"Next\">" +
				    "<span aria-hidden=\"true\">&raquo;</span>" + 
				    "<span class=\"sr-only\">Next</span></a></li>"
			);
		}

		return pageBar.toString();
	}

	public static String convertLinFeedToBr(String content) {
		return content.replaceAll("\\n", "<br/>");
	}

	public static String escapeHtml(String str) {
		return str.replaceAll("<", "&lt;").replaceAll(">", "&gt;");
	}
}

<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<jsp:include page="/WEB-INF/views/common/header.jsp">
	<jsp:param value="Dev 목록" name="title"/>
</jsp:include>
<table class="table w-75 mx-auto">
    <tr>
      <th scope="col">번호</th>
      <th scope="col">이름</th>
      <th scope="col">경력</th>
      <th scope="col">이메일</th>
      <th scope="col">성별</th>
      <th scope="col">개발가능언어</th>
      <th scope="col">수정 | 삭제</th>
    </tr>
   	<c:forEach items="${list}" var="dev">
   	<tr>
   		<th>${dev.no}</th>
   		<th>${dev.name}</th>
   		<th>${dev.career}년</th>
   		<th>${dev.email}</th>
   		<th>${dev.gender}</th>
   		<th>
   		<c:forEach items="${dev.lang}" var="lang" varStatus="vs">
   			${lang}${vs.last ? "" : ", "}
   		</c:forEach>
   		</th>
   		<td>
   			<button class="btn btn-outline-secondary" onclick="updateDev(this);" data-no="${dev.no}">수정</button>
   			<button class="btn btn-outline-danger" onclick="deleteDev(this);" data-no="${dev.no}">삭제</button>
   		</td>
   	</tr>
   	</c:forEach>
</table>
<script>
function updateDev(obj){
	var no = $(obj).data('no');
	//GET  /demo/updateDev.do?no=123 ---> devUpdateForm.jsp
	location.href="${pageContext.request.contextPath}/demo/updateDev.do?no=" + no;
	//POST /demo/updateDev.do ---> redirect:/demo/devList.do
}

function deleteDev(obj){
	var no = $(obj).data('no');
	location.href="${pageContext.request.contextPath}/demo/deleteDev.do?no=" + no;
}
</script>
<jsp:include page="/WEB-INF/views/common/footer.jsp"></jsp:include>

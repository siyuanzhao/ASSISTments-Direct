<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
    <%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>View Problems > ${requestScope.problem_set_name }"</title>
<link rel="stylesheet" 	href="../stylesheets/styles.css">
<script type="text/javascript" 	src="../js/jquery.min.js"></script>
<!-- Latest compiled and minified CSS -->
<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/css/bootstrap.min.css">
<!-- Optional theme -->
<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/css/bootstrap-theme.min.css">
<!-- Latest compiled and minified JavaScript -->
<script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/js/bootstrap.min.js"></script>
</head>
<body>
		<div class="container" style="text-align: left; color: black; padding: 30px 30px 30px 30px; background: white;">
			<c:if test="${sessionScope.is_skill_builder }">
				<h4>Five selected problems from "${requestScope.problem_set_name }"</h4>
			</c:if>
			<c:if test="${not sessionScope.is_skill_builder }">
				<h4>Problem Set "${requestScope.problem_set_name }"</h4>
			</c:if>
			<c:set var="sections" value="${sessionScope.head_section.children }"
				scope="request"></c:set>
			<jsp:include page="problems.jsp" />
			
			<div style="height: 65px; margin-top: 15px;">
				<span style="float:left; font-size: smaller;">Powered by </span><br>
				<img alt="ASSISTments Direct" src="../images/direct_logo.gif" height="35px" width="175px" style="float:left;">
			</div>
		</div>

</body>
</html>
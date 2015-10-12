<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
 <%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Not Found</title>
<link rel="stylesheet"  href="${initParam.path }stylesheets/styles.css">
</head>
<body>
	<div id="page-wrap">
		<c:import url="header.jsp" ></c:import>
		<h3 style="color:red;">404: Page not Found</h3>
		<h4>Sorry, but the page you are looking for has not been found. </h4>
		<h4>Try checking the URL for errors, then hit the refresh button on your browser.</h4>
		<img alt="Not Found" src="${initParam.path }images/404.jpg">
	</div>
</body>
</html>
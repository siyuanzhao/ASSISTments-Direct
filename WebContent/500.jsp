<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<link rel="stylesheet" href="${initParam.path }stylesheets/styles.css">
<title>Sorry...</title>
</head>
<body>
	<div id="page-wrap">
		<c:import url="header.jsp" ></c:import>
		<div class="content">
			<p style="font-size: 20px; font-weight: bold;">
				Sorry... <br> It's not you.<br> It's us. <br>
			</p>
			<p>&nbsp;</p>
			<p>
				We're experiencing an internal server problem.<br> Please try
				again later or contact <a href="mailto:assistments-help@wpi.edu">assistments-help@wpi.edu</a>
			</p>
		</div>
	</div>
</body>
</html>
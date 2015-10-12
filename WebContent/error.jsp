<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Sorry...</title>
<link rel="stylesheet" href="${initParam.path }stylesheets/styles.css">
<style type="text/css">
li {
	padding-bottom: 10px;
}
</style>
</head>
<body>
	<div id="page-wrap">
		<c:import url="header.jsp" ></c:import>
		<div
			style="width: 65%; margin: 0 auto; text-align: left; min-width: 450px;">
			<p style="margin: 20px 0 0 0;font-size: 20px; font-weight: bold;">There was a problem serving the
				requested page.</p>
			<p>&nbsp;</p>
			<p style="color: red">${requestScope.error_message }</p>
			<p>&nbsp;</p>
			<p style="font-size: 15px;font-weight: bold;">Now you're wondering, "what do i do now!?!", Well...</p>

			<ul>
				<li>${requestScope.instruction }</li>

				<li>If you continue to encounter this error, please contact <a href="mailto: assistments-help@wpi.edu">assistments-help@wpi.edu</a></li>
			</ul>
		</div>
	</div>
</body>
</html>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<link rel="stylesheet" 	href="stylesheets/styles.css">
<script type="text/javascript" 	src="js/jquery.min.js"></script>
<script type="text/javascript" 	src="js/jquery-ui.min.js"></script>
<script type="text/javascript">
	
</script>
<title>ASSISTments Direct</title>
</head>
<body>
	<div id="page-wrap">
		<div id="header" style="height:80px; background-color: #E1E6E6;"> 
			<span style="position: relative; top:50px;">The problem set was shared by ${sessionScope.distributer_name }</span>
		</div>
		<div style="width: 70%; margin: 0 auto; min-width: 550px;">
			<div style="margin-top: 50px;"></div>
			<h2>${sessionScope.problem_set_name }</h2>
			<hr>
			<div style="margin-top: 50px;"></div>
			<div style="border: solid 1px; text-align: left; padding: 30px ; margin: 30px 0 0 0;">
				Your assignment and report links have been sent to ${sessionScope.email}
			</div>
		</div>
		<div style="position:absolute; left: 0px; bottom: 30px;">
			<span style="float:left; padding-left: 10px;">Powered by </span><br>
			<img alt="ASSISTments Direct" src="images/direct_logo.gif" height="50px" width="250px">
		</div>
	</div>
</body>
</html>
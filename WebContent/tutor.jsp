<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="ISO-8859-1"%>
   <%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
	<meta charset="utf-8">
	<meta http-equiv="X-UA-Compatible" content="IE=edge">
	<meta name="viewport" content="width=device-width, initial-scale=1">
    <!-- The above 3 meta tags *must* come first in the head; any other head content must come *after* these tags -->
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
	<title>Tutor</title>
	<script src="https://ajax.googleapis.com/ajax/libs/jquery/2.1.4/jquery.min.js"></script>
	<!-- Latest compiled and minified CSS -->
	<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/css/bootstrap.min.css">
	<!-- Optional theme -->
	<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/css/bootstrap-theme.min.css">
	<!-- Latest compiled and minified JavaScript -->
	<script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/js/bootstrap.min.js"></script>
	<link rel="stylesheet" href="stylesheets/styles.css">
	<style type="text/css">
		body{
			background-color: white;
		}
		.holder {
			width: 100%;
			height: 100%;
			position: relative;
		}
		
		.frame {
			width: 100%;
			height: 100%;
		}
		
		.bar {
			position: absolute;
			top: 0;
			left: 0;
			width: 100%;
			height: 40px;
			color: white;
		}
		.banner {
    		background-color: #005192;
   		 	-webkit-box-shadow: inset 0 -2px 5px rgba(0,0,0,.1);
    		box-shadow: inset 0 -2px 5px rgba(0,0,0,.1);
    		height: 68px;
		}
</style>
<script type="text/javascript">
$(function() {
	//Here "addEventListener" is for standards-compliant web browsers and "attachEvent" is for IE Browsers.
	var eventMethod = window.addEventListener ? "addEventListener" : "attachEvent";
	var eventer = window[eventMethod];

	var messageEvent = eventMethod == "attachEvent" ? "onmessage" : "message";

	// Listen to message from child IFrame window
	eventer(messageEvent, function (e) {
	       location.reload();
	}, false); 
});
</script>
</head>
<body>

	<div class="holder">
		<iframe class="frame" src="${sessionScope.tutor_link }"></iframe>
		<div class="bar banner">
			<div class="container">
				<!-- 
				<img alt="ASSISTments" src="${initParam.path}images/direct_logo.gif" height="50px;" width="250px;" 
					style="position: relative; top: 5px; float: left;">
					 -->
				<span style="color: white; float: left; position: relative; top: 15px; font: small;">${sessionScope.notice_to_students }</span>
				<span style="color: white; float: right; position: relative; top: 15px; font: small;">${sessionScope.student_name }</span>
			</div>
		</div>
	</div>
</body>
</html>
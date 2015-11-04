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
			/* Permalink - use to edit and share this gradient: http://colorzilla.com/gradient-editor/#4f85bb+0,4f85bb+100;Blue+3D+%239 */
			background: #4f85bb; /* Old browsers */
			background: -moz-linear-gradient(top,  #4f85bb 0%, #4f85bb 100%); /* FF3.6+ */
			background: -webkit-gradient(linear, left top, left bottom, color-stop(0%,#4f85bb), color-stop(100%,#4f85bb)); /* Chrome,Safari4+ */
			background: -webkit-linear-gradient(top,  #4f85bb 0%,#4f85bb 100%); /* Chrome10+,Safari5.1+ */
			background: -o-linear-gradient(top,  #4f85bb 0%,#4f85bb 100%); /* Opera 11.10+ */
			background: -ms-linear-gradient(top,  #4f85bb 0%,#4f85bb 100%); /* IE10+ */
			background: linear-gradient(to bottom,  #4f85bb 0%,#4f85bb 100%); /* W3C */
			filter: progid:DXImageTransform.Microsoft.gradient( startColorstr='#4f85bb', endColorstr='#4f85bb',GradientType=0 ); /* IE6-9 */
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
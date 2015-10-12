<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
  <%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Reset Password</title>
<link rel="stylesheet" 	href="pure-release-0.6.0/pure-min.css">
<link rel="stylesheet" 	href="stylesheets/styles.css">
<link rel="stylesheet" 	href="stylesheets/teacher.css">

<script type="text/javascript"	src="js/jquery.min.js"></script>
<script type="text/javascript"	src="js/jquery-ui.min.js"></script>
<style type="text/css">
	.pure-form-aligned  .pure-control-group label{
		width: 12em;
	}
	#reset_password.pure-menu-link {
		color: #000000;
	}
	#logout.pure-menu-link {
		color: #000000;
	}
</style>
<script type="text/javascript">
	$(function() {
		$("#reset_password_form").submit(function(event) {
			$("#message").html("");
			$("#error").html("");
			var newPwd = $("#new_password").val();
			var confirmNewPwd = $("#confirm_new_password").val();
			console.log(newPwd + "  " + confirmNewPwd);
			if(newPwd != confirmNewPwd) {
				$("#message").html("Confirm New Password doesn't match New Password!");
				event.preventDefault();
			}
		});
	});
</script>
</head>
<body>
	<div id="page-wrap">
	<div id="header" style="height: 60px;">	
		<div class="home-menu pure-menu pure-menu-horizontal pure-menu-fixed" >
			<img alt="ASSISTments" src="${pageContext.request.contextPath}/images/direct_logo.gif" height="50px;" width="250px;" 
				style="position: absolute; top: 10px; left: 10%;">
			<ul class="pure-menu-list" style="position: relative; left: -10%;">
        		<li class="pure-menu-item">
        			<a href="${pageContext.request.contextPath}/teacher" class="pure-menu-link">Home</a>
        		</li>
        		<li class="pure-menu-item pure-menu-has-children pure-menu-allow-hover pure-menu-selected">
            		<a href="javascript:void(0);" id="menuLink1" class="pure-menu-link">Account</a>
            		<ul class="pure-menu-children">
                		<li class="pure-menu-item"><a  href="${pageContext.request.contextPath}/ResetPassword" 
                			class="pure-menu-link" id="reset_password">Reset Password</a></li>
                		<li class="pure-menu-item"><a  href="${pageContext.request.contextPath}/Logout" 
                			class="pure-menu-link" id="logout">Logout</a></li>
            		</ul>
        		</li>
        	</ul>
		</div>
    		
	</div>
	<form method="post" class="pure-form pure-form-aligned"
				style="margin: 5% 0 0 0;" id="reset_password_form" action="${pageContext.request.contextPath}/ResetPassword">
				<p class="validateTips">All form fields are required.</p>
		<fieldset>
			<div class="pure-control-group">
				<label for="email" class="pure-u-1">Email</label> 
				<input type="text" name="email" id="email"
					class="pure-input-1-4"  value="${sessionScope.loginInfo.email }" >
			</div>
			<div class="pure-control-group">
				<label for="current_password" class="pure-u-1">Current Password</label> 
				<input type="password" name="current_password" id="current_password"
					class="pure-input-1-4" required>
			</div>
			<div class="pure-control-group">
				<label for="new_password" class="pure-u-1">New Password </label> 
				<input type="password" name="new_password" id="new_password"
					class="pure-input-1-4" required>
			</div>
			<div class="pure-control-group">
				<label for="confirm_new_password" class="pure-u-1">Confirm New Password </label> 
				<input type="password" name="confirm_new_password" id="confirm_new_password"
					class="pure-input-1-4" required>
			</div>
			
			<div class="pure-controls">
				<input type="submit" value="Reset Password"
					class="pure-button pure-button-primary" name="submit" id="submit">
				<img 	src="${pageContext.request.contextPath}/images/indicator.gif"
					style="visibility: hidden;" id="indicator" height="25" width="25">
			</div>	
		</fieldset>
	</form>
	<div id="message" style='margin:15px 5px 10px 5px;' >${requestScope.message }</div>
	<div id="error" style='color:red;' >${requestScope.error }</div>
	</div>
	<!-- end of page wrap -->
	
</body>
</html>
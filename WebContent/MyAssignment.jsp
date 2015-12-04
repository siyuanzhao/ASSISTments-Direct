<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<meta name="google-signin-client_id"
	content="588893615069-3l8u6q8n9quf6ouaj1j9de1m4q24kb4k.apps.googleusercontent.com">
<title>Assignment</title>
<link rel="stylesheet"  href="../stylesheets/styles.css">
<script src="https://apis.google.com/js/platform.js"  async defer>
</script>
<script type="text/javascript"	src="../js/jquery.min.js"></script>
<script type="text/javascript" 	src="../js/script.js"></script>
<!-- Latest compiled and minified CSS -->
<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/css/bootstrap.min.css">
<!-- Optional theme -->
<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/css/bootstrap-theme.min.css">
<!-- Latest compiled and minified JavaScript -->
<script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/js/bootstrap.min.js"></script>
<link rel="stylesheet" href="../stylesheets/signin.css">
<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/font-awesome/4.4.0/css/font-awesome.min.css">
<script type="text/javascript">
	$(document).ready(function() {
		$("#language_div").hide();
		$("#google_login_indicator").css("visibility", "hidden");
		$("#facebook_login_indicator").css("visibility", "hidden");
		loadFacebook();
		
		$("#submit").click(function() {
			$("#submit").toggleClass("active");
		});
		$("#language").change(function() {
			var value = $("#language").val();
			$.ajax({
				url: '../change_language',
				type: 'POST',
				data: {value: value},
				async: false,
				success: function(data) {
					location.reload();
				},
				error: function(data) {
				}
			});
		});
	});
	function onSignIn(googleUser) {
		var assignment_ref = $("#assignment_ref").val();
		var id_token = googleUser.getAuthResponse().id_token;
		//signOut();
		$("#google_login_indicator").css("visibility", "visible");
		$.ajax({
			url: "../sign_in_with_google",
			type: "POST",
			data: {idtoken: id_token, assignment_ref: assignment_ref},
			success: function(data) {
				console.log(data);
				window.location.replace(data);
			},
			error: function(data) {
				console.log(data);
			}
		})
	}
	//Here we run a very simple test of the Graph API after login is
	// successful.  See statusChangeCallback() for when this call is made.
	function sign_in_with_facebook() {
		console.log('Welcome!  Fetching your information.... ');
		$("#facebook_login_indicator").css("visibility", "visible");
		FB.api('/me', function(response) {
			console.log(response);
			console.log('Successful login for: '
				+ response.name);
			//send user info to server to create an account
			var assignment_ref = $("#assignment_ref").val();
			$.ajax({
				url: "../sign_in_with_facebook",
				type: 'POST',
				data: {assignment_ref: assignment_ref, user_id: response.id, first_name: response.first_name, last_name: response.last_name},
				//dataType: "json",
				async: true,
				success: function(data) {
					console.log(data);
					window.location.replace(data);
				},
				error: function(data) {
					console.log(data);
				}
			});
		});
	}
	function fb_login() {
		FB.login(function(response) {
			if (response.authResponse) {
				sign_in_with_facebook();
			}

		}, {
			scope : 'public_profile,email'
		});
	}
	//sign out from google
	function signOut() {
	    var auth2 = gapi.auth2.getAuthInstance();
	    auth2.signOut().then(function () {
	      console.log('User signed out.');
	    });
	  }
</script>
</head>
<body>

	<fmt:setLocale value="${sessionScope.locale }" />
	<fmt:bundle basename="org.assistments.direct.Bundle">
		<fmt:message key="email" var="email_label"></fmt:message>
		<fmt:message key="password" var="pwd_label"></fmt:message>
		<fmt:message key="loginButton" var="login_button"></fmt:message>
		<fmt:message key="student.label1" var="label1"></fmt:message>
		<fmt:message key="student.first_name" var="first_name"></fmt:message>
		<fmt:message key="student.last_name" var="last_name"></fmt:message>
		<fmt:message key="student.go_to_assignment" var="go_to_assignment"></fmt:message>
	</fmt:bundle>
	
		<c:import url="header.jsp" ></c:import>
		<div style="float: right; margin: 20px 10px 0 0;"id="language_div">
			<select id="language">
				<option value="en_US" <c:if test="${sessionScope.locale eq 'en_US' }">selected</c:if> >English</option>
				<option value="zh_CN" <c:if test="${sessionScope.locale eq 'zh_CN' }">selected</c:if> >简体中文</option>
			</select>
		</div>
		<div style="clear: both;"></div>
		<div class ="container" style="background: white; height: 100%;">
		<h4 style="margin: 10px 0 0 0;">${label1 }</h4>
		<form action="/direct/beginAssignment" method="post"  class="form-signin">
			<fieldset>
				<input type="hidden" name="assignment_ref" id="assignment_ref"
					value=<%=request.getAttribute("assignment_ref").toString()%>>
				<br>
				<!-- 
				<div style="margin: 0 auto; min-width: 350px; width: 50%;">  -->
						<input type="text"
							name="first_name" placeholder="${first_name }" class="form-control" required>
						<input type="text"
							name="last_name" placeholder="${last_name }" class="form-control" required>
							<br>
						<button type="submit" 
							class="btn btn-primary btn-block btn-lg has-spinner" id="submit">
							<span class="spinner"><i class="fa fa-spin fa-refresh"></i></span>
							${go_to_assignment }
						</button>

				<!-- 
				</div>  -->
			</fieldset>
		</form>
		<h3>---- or ----</h3>
		<!-- sign in with google -->
		<div style="width: 250px; margin: auto; display: inline-block;">
			<div id="my-signin2" class="g-signin2" data-onsuccess="onSignIn" data-theme="dark" 
				data-width="250" data-height="50" data-longtitle="true"></div>
		</div>
		<img style="display: inline-block; position: relative; bottom:10px;"	src="${pageContext.request.contextPath}/images/indicator.gif" 
			id="google_login_indicator" height="25" width="25">
		<div style="margin-top: 5px;"></div>
		<div id="fb-root"></div>
		<!-- sign in with facebook -->
		<a href="javascript:void(0);" onclick="fb_login();" style="text-decoration: none;"> 
			<img src="${pageContext.request.contextPath}/images/sign_in_with_facebook.png"
			width="260px" height="60px" border="0" alt="Sign in with Facebook">
		</a>
		<img 	style="display: inline; bottom:10px; position: relative;" src="${pageContext.request.contextPath}/images/indicator.gif" 
			id="facebook_login_indicator" height="25" width="25">
			
	</div>
</body>
</html>
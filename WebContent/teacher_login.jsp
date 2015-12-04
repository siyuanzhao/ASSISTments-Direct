<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<meta name="google-signin-client_id"
	content="588893615069-3l8u6q8n9quf6ouaj1j9de1m4q24kb4k.apps.googleusercontent.com">
<meta name="google-site-verification" content="KmYDoW-s0aTEaWh0G2cBpbNkCVhs0JDUlwgZpHyat38" />
<title>Login</title>
<link rel="stylesheet"  href="/direct/stylesheets/signin.css">
<link href="stylesheets/flat-ui.min.css" rel="stylesheet">
<script type="text/javascript" 	src="/direct/js/jquery.min.js"></script>
<script type="text/javascript" src="https://apis.google.com/js/platform.js"  async defer></script>
<script type="text/javascript" 	src="/direct/js/script.js"></script>
<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/font-awesome/4.4.0/css/font-awesome.min.css">
<script type="text/javascript">
	$(function() {
		$("#language_div").hide();
		$("#teacher_login")
				.submit(
						function(event) {
							//$("#indicator").css("visibility", "visible");
							$("#submit").toggleClass("active");
							$("#google_message").hide();
							$("#facebook_message").hide();
							$("#message").html("");
						});
		//hide all indicators
		$("#google_login_indicator").css("visibility", "hidden");
		$("#facebook_login_indicator").css("visibility", "hidden");
		$("#google_message").hide();
		$("#facebook_message").hide();
		loadFacebook();
		$("#password").focus(function() {
			$("#message").html("");
		});
		$("#language").change(function() {
			var value = $("#language").val();
			$.ajax({
				url: 'change_language',
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
	//the end of document.ready
	
	function onSignIn(googleUser) {
		var id_token = googleUser.getAuthResponse().id_token;
		signOut();
		$("#google_login_indicator").css("visibility", "visible");
		$("#google_message").hide();
		$("#facebook_message").hide();
		$.ajax({
			url: "TeacherLogin",
			type: "POST",
			data: {idtoken: id_token, option: "google"},
			success: function(message, textStatus, xhr) {
				if(xhr.status == 203) {
					$("#google_login_indicator").css("visibility", "hidden");
					signOut();
					$("#google_message").html(message);
					
					$("#google_message").show("highlight");
				} else {
					window.location.replace(message);
				}
			},
			error: function(data) {
				$("#google_login_indicator").css("visibility", "hidden");
				signOut();
				$("#google_message").html("There is an error when you try to sign in with Google. Please try it again later!");
				
				$("#google_message").show();
				signOut();
			}
		});
	}
	
	//Here we run a very simple test of the Graph API after login is
	// successful.  See statusChangeCallback() for when this call is made.
	function sign_in_with_facebook() {
		console.log('Welcome!  Fetching your information.... ');
		$("#facebook_login_indicator").css("visibility", "visible");
		$("#google_message").hide();
		$("#facebook_message").hide();
		FB.api('/me', function(response) {
			console.log(response);
			console.log('Successful login for: '
				+ response.name);
			//send user info to server to create an account
			$.ajax({
				url: "TeacherLogin",
				type: 'POST',
				data: {user_id: response.id, option:"facebook"},
				//dataType: "json",
				async: true,
				success: function(message, textStattus, xhr) {
					if(xhr.status == 203) {
						$("#facebook_login_indicator").css("visibility", "hidden");
						$("#facebook_message").html(message);
						
						$("#facebook_message").show();
					} else {
						window.location.replace(message);	
					}
					console.log(message);
					
				},
				error: function(data) {
					$("#facebook_login_indicator").css("visibility", "hidden");
					$("#facebook_message").html("There is an error when you tried to sign up with Facebook. Please try it again later.");
					
					$("#facebook_message").show("highlight");
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
	<fmt:setLocale value="${sessionScope.locale }"/>
	<fmt:bundle basename="org.assistments.direct.Bundle">
		<fmt:message key="email" var="email_label"></fmt:message>
		<fmt:message key="password" var="pwd_label"></fmt:message>
		<fmt:message key="loginButton" var="login_button"></fmt:message>
	</fmt:bundle>
		<c:import url="header.jsp" ></c:import>
		<div style="float: right; margin: 20px 10px 0 0;" id="language_div">
			<select id="language">
				<option value="en_US" <c:if test="${sessionScope.locale eq 'en_US' }">selected</c:if> >English</option>
				<option value="zh_CN" <c:if test="${sessionScope.locale eq 'zh_CN' }">selected</c:if> >简体中文</option>
			</select>
		</div>
		<div style="clear: both;"></div>
		<div class="container" style="background: white; height:100%;">
		<br>
		<form action="TeacherLogin" method="post" class="form-signin" id="teacher_login">
			<fieldset>
					<input type="text"
						name="email" placeholder="${email_label }" value="${requestScope.email }" class="form-control" required>
					<input type="password"
						name="password" id="password" placeholder="${pwd_label }" class="form-control" required>
					<button type="submit" id="submit" name="submit"
						class="btn btn-primary btn-lg btn-block has-spinner">
						<span class="spinner"><i class="fa fa-spin fa-refresh"></i></span>
						${login_button }
					</button>
						<img 	src="images/indicator.gif"
						style="visibility: hidden;" id="indicator" height="25" width="25">	
			</fieldset>
		</form>
		<div style='margin:15px 5px 10px 5px; color:red' id="message">${requestScope.message }</div>
		<h3>---- or ----</h3>
		<!-- sign in with google -->
		<div style='margin:15px 5px 10px 5px; color:red' id="google_message"></div>
		<div style="width: 250px; margin: auto; display: inline-block;">
			<div id="my-signin2" class="g-signin2" data-onsuccess="onSignIn" data-theme="dark" 
				data-width="250" data-height="50" data-longtitle="true"></div>
		</div>
		<img style="display: inline-block; position: relative; bottom:5px;"	src="images/indicator.gif" id="google_login_indicator" height="25" width="25">
		<div style="margin-top: 5px;"></div>
		<div id="fb-root"></div>
		<!-- sign in with facebook -->
		<div style='margin:15px 5px 10px 5px; color:red' id="facebook_message"></div>
		<a href="javascript:void(0);" onclick="fb_login();" style="text-decoration: none;"> 
			<img src="images/sign_in_with_facebook.png"
			width="260px" height="60px" border="0" alt="Sign in with Facebook">
		</a>
		<img 	style="display: inline; bottom:10px; position: relative;" src="images/indicator.gif" id="facebook_login_indicator" height="25" width="25">
			
	</div>
</body>
</html>
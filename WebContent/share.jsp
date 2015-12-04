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
<title>Share Link >> ${sessionScope.problem_set_name }</title>
<link rel="stylesheet" href="../stylesheets/styles.css">
<link rel="stylesheet" href="../stylesheets/jquery-ui.css">
<script type="text/javascript"  src="https://apis.google.com/js/platform.js"  async defer></script>

<style type="text/css">
html {
	height: auto !important;
}

div.pure-control-group label.pure-u-1 {
	width: 250px;
}
.share_button {
	background-color: #8c9093;
	padding: 10px 14px 8px 12px;
}
.share_button_verb {
	padding-top: 15px;
	color: #eee;
	float: left;
}
a:hover {
 cursor:pointer;
}
</style>
<script type="text/javascript"	src="../js/jquery.min.js"></script>
<script src="https://apis.google.com/js/client.js"></script>
<script type="text/javascript"	src="../js/jquery-ui.min.js"></script>
<script type="text/javascript" 	src="../js/script.js"></script>
<script type="text/javascript" 	src="../js/bootstrap.min.js"></script>
<link rel="stylesheet" href="../stylesheets/bootstrap.min.css">
<link rel="stylesheet" href="../stylesheets/bootstrap-theme.min.css">
<script type="text/javascript" 
	src="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-select/1.7.5/js/bootstrap-select.min.js"></script>
<link href="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-select/1.7.5/css/bootstrap-select.min.css" rel="stylesheet">
<script type="text/javascript">
//window.___gcfg = {
//        parsetags: 'explicit'
//      };
var CLASSROOM_CLIENT_ID = "329183900516-6ejbv88ntbavjbqn7t8r610krglk2rsq.apps.googleusercontent.com";
var SCOPES = ["https://www.googleapis.com/auth/classroom.profile.emails",
              "https://www.googleapis.com/auth/classroom.rosters.readonly",
              "https://www.googleapis.com/auth/classroom.courses"];
var ownerId = "";
var firstName = "";
var lastName = "";
var emailAddr = "";
	$(function() {
		$('.selectpicker').selectpicker();
		var buttonPressed;
		$("#language_div").hide();
		$("#classroom_area").hide();
		$("#get_links_form").hide();
		$("#login_area").hide();
		$("#verify_panel").hide();
		$("#verfication_message").hide();
		//$("#password").prop('disabled', true);
		$("#log_in").prop('disabled', true);
		$("#create_account").prop('disabled', true);
		$("#google_login_indicator").css("visibility", "hidden");
		$("#facebook_login_indicator").css("visibility", "hidden");
		$("#message").hide();
		loadFacebook();
		var permittedEmails = [];
		//offer three options
		var checkedRadio = "";
		$('input[name=optionsRadios]').on("click", function() {
			$("#message").hide();
			$("#classroom_message").hide();
			$("#verification_message").hide();
			$("#error_message_not_share").hide();
			$("#error_message_unverified_teacher").hide();
			var option = $('input[name=optionsRadios]:checked').val();
			$("#login_area").hide(600);
			$("#get_links_form").hide(600);
			$("#classroom_area").hide(600);
			if(checkedRadio == $(this).val()) {
				$(this).attr("checked", false);
				checkedRadio = "";
			} else {
				checkedRadio = option;
				if( option == "option1") {
					$("#get_links_form").show(600);
				} else if(option == "option2") {
					$("#login_area").show(600);
				} else if(option == "option3") {
					$("#classroom_area").show(600);
				}
			}
		});
		
		$("#get_links_form #email").focus(function(){
			$("#message").hide();
			$("#verification_message").hide();
			$("#error_message_not_share").hide();
			$("#error_message_unverified_teacher").hide();
		});
		
		$("#login_area #email, #login_area #password").focus(function() {
			$("#message").hide();
			$("#verification_message").hide();
			$("#error_message_not_share").hide();
			$("#error_message_unverified_teacher").hide();
		});
		
		$("#login_area #email").focusout(function() {
			var email = $("#login_form  #email").val();	
			var readonly = $("#login_form  #email").attr("readonly");
			if(email == null || email == ""  || readonly) {
				return false;
			}
			$.ajax({
				url: '../isUserNameTaken',
				type: 'POST',
				data: {email: email},
				dataType: "json",
				async: true,
				success: function(data) {
					if(data.result == "true") {
						$("#log_in").prop("disabled", false);
						$("#create_account").prop("disabled", true);
					} else {
						$("#log_in").prop("disabled", true);
						$("#create_account").prop("disabled", false);
					}
				},
				error: function(data) {
					$("#message").html("You just encountered an error. Please try it again.");
					$("#message").effect("highlight");
					event.preventDefault();
				}
			});
		});
		
		$("#login_form").submit(function(event) {
			if($("#recipient").val() != "" && $("#recipient").val()!= "generic"){
				var email = $("#login_form #email").val();
				if(email != $("#recipient").val()){
					event.preventDefault();
					alert("this is a personalized link"); // this should be modified later
					return;
				}
			}
			var flag = false;
			if($("#assistments_verified").val()=='true'){
				var email = $("#login_form #email").val();
				$.ajax({
					url:'../ASTeacherVerification',
					type:'POST',
					data:{email:email},
					dataType:'text',
					async:false,
					success:function(data){
						flag = true;
						$("#error_message_not_share").hide();
						$("#error_message_unverified_teacher").hide();
						return;
					},
					error:function(data){
						flag = false;
						return;
					}
				});
			}
			if($("#url").val() != "" && !flag){
				if(buttonPressed == "log_in" || buttonPressed == "create_account"){
					if(buttonPressed == "log_in"){
						$("#login_indicator").css("visibility","visible");
					}else{
						$("#create_account_indicator").css("visibility","visible");
					}
					var email = $("#login_form  #email").val();
					var verifiedTeacher = verifyTeacherEmails(email);
					if(!verifiedTeacher){
						event.preventDefault();
						$("#login_indicator").css("visibility","hidden");
						$("#create_account_indicator").css("visibility","hidden");
						return;
					}
				}
			}
			if(!flag && $("#url").val() == "" && $("#assistments_verified").val() == 'true'){
				$("#get_links_indicator").css("visibility","hidden");
				$("#get_links_form #email").attr("readonly", false);
				$("#error_message_not_share").hide();
				$("#error_message_unverified_teacher").slideDown();
				e.preventDefault();
				return;
			}
			if(buttonPressed == "log_in"){
				//first check if password is correct!
				var email = $("#login_form  #email").val();	
				var password = $("#login_form  #password").val();	
				
				$.ajax({
					url: '../check_password',
					type: 'POST',
					data: {email: email, password: password},
					dataType: "json",
					async: false,
					success: function(data) {
						if(data.result == "true") {
							$("#login_indicator").css("visibility", "visible");
						} else if(data.result == "wrong") {
							$("#message").show();
							$("#message").html(data.message);
							$("#message").effect("highlight");
							$("#login_indicator").css("visibility", "hidden");
							event.preventDefault();
						}
					},
					error: function(data) {
						$("#message").html("You just encountered an error. Please try it again.");
						$("#message").effect("highlight");
						$("#login_indicator").css("visibility", "hidden");
						event.preventDefault();
					}
				});
			} else if (buttonPressed == "create_account") {
				$("#create_account_indicator").css("visibility", "visible");

				//check email
				event.preventDefault();
				var email = $("#login_form  #email").val();	
				var action = "GetVerifyCode";
				$.ajax({
					url: '../check_email',
					type: 'POST',
					data:{email: email, action: action},
					dataType: "json",
					async: false,
					success: function(data){
						if(data.result == "true"){
							$("#verifyCode").val("");
							$("#verify_panel").slideDown();
							$("#create_account").prop("disabled", true);
							$("#login_form #email").attr('readonly', true);
							$("#login_form #password").attr('readonly', true);
							$("#create_account_indicator").css("visibility", "hidden");
							$("#verifyCode").focus(function() {
								$("#verification_message").hide();
							});
							correctCode = data.correct_code;
						}else{
							$("#create_account_indicator").css("visibility", "hidden");
							$("#message").html("Please check your email. Make sure it's a valid email");
							$("#message").show();
							$("#message").effec("highlight");
						}
					},
					error: function(data){
						$("#create_account_indicator").css("visibility", "hidden");
						$("#message").html("You just encountered an error. Please try it again.");
						$("#message").effect("highlight");
					}
				});
			} else if(buttonPressed == "verify_code") {
				$("#verification_indicator").css("visibility", "visible");
				var code = $("#verifyCode").val();
				var action = "VerifyingCode";
				$.ajax({
					url: '../check_email',
					type: 'POST',
					data: {code: code, action: action, correct_code: correctCode},
					dataType: "json",
					async: false,
					success: function(data){
						if(data.result == "true") {
						} else if(data.result == "wrong") {
							$("#verfication_message").html("The verification code is wrong.");
							$("#verfication_message").effect("highlight");
							$("#verification_indicator").css("visibility", "hidden");
							event.preventDefault();
						}
					},
					error: function(data) {
						$("#verification_indicator").css("visibility", "hidden");
						$("#verfication_message").html("You just encountered an error. Please try it again.");
						$("#verfication_message").effect("highlight");
						event.preventDefault();
					}
				});
			}
		});
		$("input[name=submit]").click(function() {
			$("#message").hide();
			$("#verfication_message").hide();
			//$("#login_indicator").css("visibility", "visible");
			buttonPressed = $(this).attr("id");
		});
		$("#get_links_form").submit(function(e) {
			var flag = false;
			if($("#assistments_verified").val() == 'true'){
				var email = $("#get_links_form #email").val();
				$.ajax({
					url:'../ASTeacherVerification',
					type:'POST',
					data:{email:email},
					dataType:'text',
					async:false,
					success:function(data){
						console.log(data);
						$("#error_message_not_share").hide();
						$("#error_message_unverified_teacher").hide();
						flag = true;
					},
					error:function(data){
						console.log(data.responseText);
						flag = false;
					}
				});
			}
			if($("#url").val() != "" && !flag){
				$("#get_links_indicator").css("visibility", "visible");
				$("#get_links_form #email").attr("readonly", true);
				var email = $("#get_links_form #email").val();
				var verifiedTeacher = verifyTeacherEmails(email);
				if(!verifiedTeacher){
					$("#get_links_indicator").css("visibility","hidden");
					$("#get_links_form #email").attr("readonly", false);
					e.preventDefault();
					return;
				}
			}
			if(!flag && $("#url").val() == "" && $("#assistments_verified").val() == 'true'){
				$("#get_links_indicator").css("visibility","hidden");
				$("#get_links_form #email").attr("readonly", false);
				$("#error_message_not_share").hide();
				$("#error_message_unverified_teacher").slideDown();
				e.preventDefault();
				return;
			}
			//send the links to the teacher
		});
		
		$("#classroom_share_button").click(function() {
			gapi.auth.authorize({
				 'client_id': CLASSROOM_CLIENT_ID,
				 'scope': SCOPES,
				 'immediate': false}, handleAuthResult);
		});
		
		$("#course_btn").click(function(event) {
			var course_id = $("#courses_select").val(); 
			var course_name = $("#courses_select option:selected").text();
			var problem_set_name = "";
			var link_to_share = "";
			var problem_set_id = $("#problem_set_id").text();
			$.ajax({
				url: '../google_classroom/setup',
				type: 'POST',
				data: {course_id: course_id, course_name: course_name, email_address: emailAddr, first_name: firstName, 
					last_name: lastName, problem_set_id: problem_set_id, email: ownerId},
				async: false,
				success: function(data) {
					//$("#classroom_share_button_div").html("<g:sharetoclassroom url='"+data.classroom_link+"' size='48' " +
					//		"body='You will go to ASSISTments to do the assignment.' ></g:sharetoclassroom>");
					problem_set_name = $("#problem_set_name").text();
					problem_set_name = encodeURI(problem_set_name);
					link_to_share = encodeURI(data.classroom_link);
					//$("#classroom_share_button_div").show();
				},
				error: function(data) {
					console.log(data);
				}
			});
			var url = 'https://classroom.google.com/share?url='+link_to_share+'&title='+problem_set_name;
			window.open(url, '', 'width=650,height=450');
		});
		
		//i18n
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
		
		//question sign
		$("#question_sign").popover({
			content: "The app needs you to choose the class <b>before</b> it reaches out to Google."
				+"You will need to choose the same class again on the next screen.",
			html: true,
			trigger: 'focus',
			placement: 'right'
		});
	});
	
	function handleAuthResult(authResult) {
		if (authResult && !authResult.error) {
		    //get user profile
		    var request = gapi.client.request({
				root : 'https://classroom.googleapis.com',
				path : 'v1/userProfiles/me',
			});

			request.execute(function(resp) {
				if(resp.error != null) {
					$("#classroom_message").html("Sorry... Google Classroom is not available for your google account at this time.");
	    			$("#classroom_message").css("color", "blue");
		    		$("#classroom_message").show();
		    		return;
				}
				firstName = resp.name.givenName; 
				lastName = resp.name.familyName;
				emailAddr = resp.emailAddress
			});
		    var request = gapi.client.request({
				root : 'https://classroom.googleapis.com',
				path : 'v1/courses',
				params: {
					teacherId: 'me'
				}
			});
		    
		    request.execute(function(resp) {
		    	console.log(resp);
		    	//first check if the user has the permission to classroom
		    	if(resp.error != null) {
		    		if(resp.error.code == 403) {
		    			$("#classroom_message").html("Sorry... Google Classroom is not available for your google account at this time.");
		    			$("#classroom_message").css("color", "blue");
			    		$("#classroom_message").show();
			    		return;
		    		}
		    		$("#classroom_message").html("Sorry... Something goes wrong here with Google Classroom. But we have no idea what causes the problem.");
		    		$("#classroom_message").css("color", "red");
		    		$("#classroom_message").show();
		    		return;
		    	}
		    	var courses = resp.courses;
		    	$("#courses_select").html("");
		    	if(courses == null) {
		    		console.log("You don't have any course!");
		    		$("#classroom_message").html("Sorry... You don't have any course!<br> You should at least have a course in Google Classroom.");
		    		$("#classroom_message").css("color", "blue");
		    		$("#classroom_message").show();
		    	} else if ((courses.length > 0)) {
		    		var i = 0;
		    		for(i=0; i < courses.length; i++) {
		    			var course = courses[i];
		    			ownerId = course.ownerId;
		    			if(course.courseState == 'ACTIVE') {
		    				if(course.section != null) {
			    				$("#courses_select").append($("<option></option>")
			    					.attr("value", course.id)
			    					.html(course.name + " - " + course.section));
		    				} else {
		    					$("#courses_select").append($("<option></option>")
				    					.attr("value", course.id)
				    					.html(course.name));
		    				}
		    			}
		    		}
		    		$('.selectpicker').selectpicker('refresh');
		    		$("#courseModal").modal("show");
		    		//$("#classroom_share_button_div").hide();
		    	}
		    });
		}
		
	}
	
	function verifyTeacherEmails(email){
		var spreadsheetId = $("#spreadsheetId").val();
		var verifiedTeacher = false;
		$.ajax({
			url:"../CheckVerifyTeacherList",
			type:"POST",
			data:{email:email,spreadsheetId:spreadsheetId},
			dataType:'json',
			async:false,
			success: function(data){
				if(data.result == "true"){
					if(data.verified_email == "true"){
						verifiedTeacher = true;
						$("#error_message_not_share").hide();
						$("#error_message_unverified_teacher").hide();
					}else{
						$("#error_message_not_share").hide();
						$("#error_message_unverified_teacher").slideDown();
					}
				}else{
					//the spreadsheet is not shared to the domain user, check if it is published to the web
					var jsonUrl = $("#url").val();
					$.ajax({
						url: jsonUrl,
						type:"GET",
						dataType:'json',
						async: false,
						success: function(data){
							//the spreadsheet is published to the web, now verify the teacher email
							var entry = data.feed.entry;
							$(entry).each(function(){
								if(email == this.gs$cell.$t){
									//this is a verified teacher email
									verifiedTeacher = true;
									$("#error_message_not_share").hide();
									$("#error_message_unverified_teacher").hide();
									return false;
								}
							});
							if(!verifiedTeacher){
								$("#error_message_not_share").hide();
								$("#error_message_unverified_teacher").slideDown();
							}
						},
						error: function(data){
							// if gets here, it means the spreadsheet is not published to the web either
							$("#error_message_unverified_teacher").hide();
							$("#error_message_not_share").slideDown();
						}
					});
				}
			},
			error: function(data){
				alert("error1"+ verifiedTeacher);
			}
		});
		return verifiedTeacher;
	}
	
	function onSignIn(googleUser) {
		var assignment_ref = $("#assignment_ref").val();
		console.log(googleUser.getAuthResponse());
		var id_token = googleUser.getAuthResponse().id_token;
		$("#google_login_indicator").css("visibility", "visible");
		$.ajax({
			url: "../ShareSetup",
			type: "POST",
			data: {idtoken: id_token, assignment_ref: assignment_ref, option: "google"},
			success: function(data) {
				signOut();
				console.log(data);
				window.location.replace(data);
			},
			error: function(data) {
				signOut();
				console.log(data);
			}
		});
	}
	//Here we run a very simple test of the Graph API after login is
	// successful.  See statusChangeCallback() for when this call is made.
	function sign_in_with_facebook() {
		$("#facebook_login_indicator").css("visibility", "visible");
		console.log('Welcome!  Fetching your information.... ');
		FB.api('/me', function(response) {
			console.log(response);
			console.log('Successful login for: '
				+ response);
			//send user info to server to create an account
			var assignment_ref = $("#assignment_ref").val();
			$.ajax({
				url: "../ShareSetup",
				type: 'POST',
				data: {assignment_ref: assignment_ref, user_id: response.id, first_name: response.first_name, 
					last_name: response.last_name, option: "facebook"},
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

	<fmt:setLocale value="${sessionScope.locale }"/>
	<fmt:bundle basename="org.assistments.direct.Bundle">
		<fmt:message key="share.label1" var="label1"></fmt:message>
		<fmt:message key="share.label2" var="label2"></fmt:message>
		<fmt:message key="share.label3" var="label3"></fmt:message>
		<fmt:message key="share.label4" var="label4"></fmt:message>
		<fmt:message key="share.label5" var="label5"></fmt:message>
		<fmt:message key="share.banner" var="banner"></fmt:message>
		<fmt:message key="share.view_problems" var="view_problems"></fmt:message>
		<fmt:message key="share.powered_by" var="powered_by"></fmt:message>
		
		<fmt:message key="email" var="email_label"></fmt:message>
		<fmt:message key="password" var="pwd_label"></fmt:message>
		<fmt:message key="loginButton" var="login_button"></fmt:message>
		<fmt:message key="createAccountButton" var="create_account_button"></fmt:message>
		<fmt:message key="sendButton" var="send_button"></fmt:message>
	</fmt:bundle>
	<div id="page-wrap">
		<div id="header" style="height:80px; background-color: #E1E6E6;"> 
			<span style="position: relative; top:50px;">${banner} ${sessionScope.distributer_name }</span>
		</div>
		<div style="float: right; margin: 20px 10px 0 0;" id="language_div">
			<select id="language">
				<option value="en_US" <c:if test="${sessionScope.locale eq 'en_US' }">selected</c:if> >English</option>
				<option value="zh_CN" <c:if test="${sessionScope.locale eq 'zh_CN' }">selected</c:if> >简体中文</option>
			</select>
		</div>
		<div style="clear: both;"></div>
		
		<div style="width: 70%; margin: 40px auto 0 auto; min-width: 550px;">
			<h2 id="problem_set_name">${sessionScope.problem_set_name }</h2>
			<span id="problem_set_id" style="display: none;">${sessionScope.problem_set }</span>
			<p><a href="${sessionScope.view_problem_link }" target="_blank">${view_problems }</a></p>
			<hr>
			<h4><c:out value="${label1 }"></c:out></h4>
			<div style="margin-top: 30px;"></div>
			<!-- text-align: left -->
			<div style="text-align: left; margin-left: 25%;">
				<label for="option-two" class="pure-radio">
        			<input id="option-two" type="radio" name="optionsRadios" value="option1">
        			<c:out value="${label2 }"></c:out>
    			</label>
    			<div style="margin: 20px 0 0 0;"></div>
				<form method="post" class="form-inline pure-form" id="get_links_form" 
				name="login_form" action="../ShareSetup">
				<fieldset>
					<div class="form-group" style="width: 95%;"> 
						<!-- 
						<label for="email" style="margin: 5px 0 0 0;" class="col-sm-2">
							<c:out value="${email_label }"></c:out>
						</label>
						 -->
						<input type="email" id="email" name="email" class="form-control" style="width: 60%;" 
							placeholder="${email_label }" required pattern="^\S+@(([a-zA-Z0-9]([a-zA-Z0-9\-]{0,61}[a-zA-Z0-9])?\.)+[a-zA-Z]{2,6})$">	
							<input id="option" type="hidden"  name="option" value="get_links">	
						<input id="submit" type="submit" name="submit" value="${send_button }"
							class="btn btn-default" style="margin-left: 15px;">
							<input id="url" type="hidden" name="url" value="${sessionScope.url}">
							<input id="spreadsheetId" type="hidden" name="spreadsheetId" value="${sessionScope.spreadsheetId}">
							<input id="recipient" type = "hidden" name="recipient" value="${sessionScope.recipient}">
							<input id="assistments_verified" type="hidden" name = "assistments_verified" value="${sessionScope.assistments_verified }">
						<img 	src="../images/indicator.gif"
						style="visibility: hidden;" id="get_links_indicator" height="25" width="25" alt="" >	
						
					</div>
				</fieldset>
				<hr>
				</form>
				<div style="margin: 20px 0 0 0;"></div>
    			<label for="option-three" class="pure-radio">
        			<input id="option-three" type="radio" name="optionsRadios" value="option2">
        			<c:out value="${label3 }"></c:out>
    			</label>
			<div style="margin: 10px 0 0 0;"></div>
    		<i style="font-size: small;">
    			${label4 }
    		</i>
    		</div>
    		<!-- login or create account area -->
    		<div style="margin: 10px 0 0 0;" id="login_area" >
			<form method="post" class="form-horizontal pure-form pure-form-aligned" id="login_form" 
				name="login_form" action="../ShareSetup" style="margin-left: 24%; text-align: center;">
				<fieldset>
					<div class="form-group"> 
						<label for="email"  class="col-sm-2 control-label">${email_label }</label>
						<div class="col-sm-8">
						<input type="email"  id="email" name="email" class="form-control"
							placeholder="${email_label }" required pattern="^\S+@(([a-zA-Z0-9]([a-zA-Z0-9\-]{0,61}[a-zA-Z0-9])?\.)+[a-zA-Z]{2,6})$">
						</div>
					</div>
					<div class="form-group" id="password_div">
						<label for="password" class="col-sm-2 control-label">${pwd_label }</label>
						<div class="col-sm-8">
						<input type="password" id="password" name="password" class="form-control"
							placeholder="${pwd_label }" required>
						</div>
					</div>
					<input type="hidden"  name="option" value="form">
					<div style='margin:15px 5px 10px 5px; color:red' id="message"></div>
					<div class="pure-controls">
						<input type="submit" value="${login_button }"
							class="btn btn-default" name="submit" id="log_in" >
						<img 	src="../images/indicator.gif"
						style="visibility: hidden;" id="login_indicator" height="25" width="25">
						<input type="submit" value="${create_account_button }"
							class="btn btn-default" name="submit" id="create_account" >
						<img 	src="../images/indicator.gif"
						style="visibility: hidden;" id="create_account_indicator" height="25" width="25">
					</div>
					<div id="verify_panel" class="pure-control-group" style="margin: 30px 0 0 90px;">
						<p>Please enter the verify code you received in your email.</p>
						<input type="password"  id="verifyCode" name="verifyCode" class="pure-input-1-3" placeholder="Verification Code" >
						<input type= "submit" value ="Verify" class = "pure-button" name="submit" id="verify_code">
						<img 	src="../images/indicator.gif"
						style="visibility: hidden;" id="verification_indicator" height="25" width="25">
						<div style='margin:15px 5px 10px 5px; color:red' id="verfication_message"></div>
					</div>
				</fieldset>
			</form>
			<div style="margin-left: 24%;">
			<h5>---- or ----</h5>
			<!-- sign in with google -->
			<div style="width: 250px; margin: auto; display: inline-block;">
				<div id="my-signin2" class="g-signin2" data-onsuccess="onSignIn" data-theme="dark" 
				data-width="250" data-height="50" data-longtitle="true"></div>
			</div>
			<!-- end of sign in with google -->
			<img style="display: inline-block; position: relative; bottom:10px;"	src="../images/indicator.gif" id="google_login_indicator" height="25" width="25">
			<div style="margin-top: 10px;"></div>
			<div id="fb-root"></div>
			<!-- sign in with facebook -->
			<a href="javascript:void(0);" onclick="fb_login();" style="text-decoration: none;"> 
				<img src="../images/sign_in_with_facebook.png"
					width="260px" height="60px" border="0" alt="Sign in with Facebook">
			</a>
			<img 	style="display: inline; bottom:10px; position: relative;" src="../images/indicator.gif" id="facebook_login_indicator" height="25" width="25">
			<hr>
			</div>
			</div>
			<div style="text-align: left; margin-left: 25%; margin-top: 25px;">
				<label for="option-four" class="pure-radio">
	        		<input id="option-four" type="radio" name="optionsRadios" value="option3">
	        		<img src="//www.gstatic.com/classroom/logo_square_48.svg"  width="35px" height="35px" style="position: relative; top: 0px;">
	        		<c:out value="${label5 }"></c:out>
	    		</label>
    		</div>
			<div id="classroom_area" style="margin: 20px 0 0 0;">
				<div>
					<a class="share_button"  style="display: block; width: 45%; 
					margin-left: 30%; height: 70px; min-width: 300px;" id="classroom_share_button" >
						<span class="share_button_verb">Share to Google Classroom</span>
						<img src="//www.gstatic.com/classroom/logo_square_48.svg" style="float: right;">
					</a>
				</div>
				<div id="classroom_message" style="margin: 20px 0 0 0;"></div>
			</div>
			<div id="error_message_unverified_teacher" style="border:2px solid blue; background-color:#ccebf3; margin:15px 0 15px 0; display:none;">
				<p style="margin-left:10px; margin-right:10px;" text-align:left;>Distributor has set up this share link to only work for verified teachers. Your e-mail is not on the list. You can fill in <a href="${sessionScope.form}" target="_blank">this form</a> and wait to be verified.</p>
			</div>
			
			<div id ="error_message_not_share" style="border:2px solid blue; background-color:#ccebf3; margin:15px 0 15px 0; display:none;">
				<p>Sorry.. There is something wrong with this share link, and you cannot get your assignment and report links at this moment. Please leave your email with us(<a href="#" target="_blank">assistments-help@wpi.edu</a>), we will get back to you when it is fixed</p>
			</div>
			<div class="modal fade" id="courseModal" tabindex="-1" role="dialog" 
				aria-labelledby="courseModalLabel" style="text-align: left;">
				<div class="modal-dialog" role="document">
					<div class="modal-content">
						<div class="modal-header">
							<button type="button" class="close" data-dismiss="modal"
								aria-label="Close">
								<span aria-hidden="true">&times;</span>
							</button>
							<h4 class="modal-title">Choose class</h4>
						</div>
						<div class="modal-body">
								<div style="margin: 30px 0 50px 0;">
									<span class="glyphicon glyphicon-info-sign"></span>
									The app needs you to choose the class <b>before</b> it reaches out to Google. 
						You will need to choose the same class again on the next screen.
								</div>
								<select id="courses_select" class="selectpicker"  title='Choose class' data-width="70%">
								</select>
								<div style="margin: 30px 0 0 0;"></div>
						</div>
						<div class="modal-footer">
							<button type="button" class="btn btn-default"
								data-dismiss="modal">Close</button>
							<button type="button" class="btn btn-primary" id="course_btn">Go to Google Classroom</button>
						</div>
					</div>
					<!-- /.modal-content -->
				</div>
				<!-- /.modal-dialog -->
			</div>
			<!-- /.modal -->
		</div>
		<c:remove var="email" scope="session" />
		<c:remove var="message" scope="session" />
		<div style="margin-top: 60px; height: 140px;">
			<span style="float:left; padding-left: 10px; font-size: smaller;">${powered_by } </span><br>
			<img alt="ASSISTments Direct" src="../images/direct_logo.gif" height="35px" width="175px" style="float:left; padding-left: 10px;">
		</div>
	</div> <!-- end of page-wrap -->
</body>
</html>
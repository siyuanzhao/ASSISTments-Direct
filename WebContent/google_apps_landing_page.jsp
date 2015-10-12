<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>ASSISTments APP for Google Classroom</title>
<link rel="shortcut icon" href="images/shortcut_logo.png">
<link rel="stylesheet" href="stylesheets/jquery-ui.css">
<!-- Bootstrap core CSS -->
<link href="stylesheets/bootstrap.min.css" rel="stylesheet">
<!-- Optional theme -->
<link href="stylesheets/bootstrap-theme.min.css" rel="stylesheet">
<script
	src="https://ajax.googleapis.com/ajax/libs/jquery/2.1.3/jquery.min.js"></script>
<script type="text/javascript" src="js/bootstrap.min.js"></script>
<script type="text/javascript" src="js/jquery-ui.min.js"></script>
<script
	src="https://ajax.googleapis.com/ajax/libs/angularjs/1.4.3/angular.min.js"></script>
<script src="https://apis.google.com/js/platform.js" async defer></script>
<script src="https://apis.google.com/js/client.js"></script>
<style type="text/css">
body {
	padding-top: 50px;
	padding-bottom: 20px;
}
.apps_link:hover{
	cursor:pointer;
}
.container_customize{
	padding:0 60px 0 30px;
}
a:hover{
	cursor:pointer;
}
.nav_active{
	color:white !important;
}
.footer_section{
	position:fixed;
	bottom:10px;
	text-align:center;
}
</style>
<script>
var CLASSROOM_CLIENT_ID = "757036402283-8o3nu8pdve8snhj8ds11te8bnsrnmuu6.apps.googleusercontent.com";
var SCOPES = ["https://www.googleapis.com/auth/classroom.profile.emails",
              "https://www.googleapis.com/auth/classroom.rosters.readonly",
              "https://www.googleapis.com/auth/classroom.courses"];
var toolSelected = "";
	$(function(){
		$(".apps_link").click(function(){
			$("#alert_panel").hide();
			var id = $(this).attr("id");
			toolSelected = id;
			if(id == "skill_builder_link"){
				gapi.auth.authorize({
					 'client_id': CLASSROOM_CLIENT_ID,
					 'scope': SCOPES,
					 'immediate': false}, handleAuthResult);
			//	window.location.assign("/direct/SkillBuilderGoogleClassroom");
			}else if (id == "ap_statistics_link"){
				gapi.auth.authorize({
					 'client_id': CLASSROOM_CLIENT_ID,
					 'scope': SCOPES,
					 'immediate': false}, handleAuthResult);
			}else if (id == "chemistry_link"){
				gapi.auth.authorize({
					 'client_id': CLASSROOM_CLIENT_ID,
					 'scope': SCOPES,
					 'immediate': false}, handleAuthResult);
			}
			
		});
		
		$(".navbar_item").click(function(){
			$("#alert_panel").hide();
			var id = $(this).attr("id");
			if(id == "landing_page" || id == "logo"){
				toolSelected = "landing_page";
				window.location.assign("/direct/GoogleAppsLandingPage");
			}else if(id == "skill_builder_page"){
				toolSelected = "skill_builder_link";
				gapi.auth.authorize({
					 'client_id': CLASSROOM_CLIENT_ID,
					 'scope': SCOPES,
					 'immediate': false}, handleAuthResult);
				//window.location.assign("/direct/SkillBuilderGoogleClassroom");
			}else if (id == "ap_page"){
				toolSelected = "ap_statistics_link";
				gapi.auth.authorize({
					 'client_id': CLASSROOM_CLIENT_ID,
					 'scope': SCOPES,
					 'immediate': false}, handleAuthResult);
			}else if(id == "chemistry_page"){
				toolSelected = "chemistry_link";
				gapi.auth.authorize({
					 'client_id': CLASSROOM_CLIENT_ID,
					 'scope': SCOPES,
					 'immediate': false}, handleAuthResult);
			}
			
		});
		$(".apps_link").hover(function(){
			$("img",this).animate({
				width:"+=6"
			},100);
		},function(){
			$("img",this).animate({
				width:"-=6"
			},100);
		});
	});
	function handleAuthResult(authResult) {
		if (authResult && !authResult.error) {
			accessToken = authResult.access_token;
			var request = gapi.client.request({
				root : 'https://classroom.googleapis.com',
				path : 'v1/userProfiles/me',
			});
			request.execute(function(resp) {
				$.ajax({
					url:"AuthenticateAssistmentsAppsUser",
					type:"POST",
					data:{userId: resp.id, firstName: resp.name.givenName, lastName: resp.name.familyName, email: resp.emailAddress},
					async: true,
					success: function(data){
						if(data == "true" && toolSelected  == "skill_builder_link"){
							window.location.assign("/direct/SkillBuilderGoogleClassroom?folder_id=22&tool_type=skill_builder");
						}else if(data == "true" && toolSelected == "ap_statistics_link"){
							window.location.assign("/direct/SkillBuilderGoogleClassroom?folder_id=210644&tool_type=ap_statistics");
						}else if(data == "true" && toolSelected == "chemistry_link"){
							window.location.assign("/direct/SkillBuilderGoogleClassroom?folder_id=226695&tool_type=chemistry");
						}
					},
					error: function(data){
						$("#message").html("There is an error.<br> Error message is " + data.responseText);
					}
				});
			});
		}

	}
</script>
</head>
<body>
	<nav class="navbar navbar-inverse navbar-fixed-top">
	<div class="container_customize">
		<div class="navbar-header">
			<button type="button" class="navbar-toggle collapsed"
				data-toggle="collapse" data-target="#navbar" aria-expanded="false"
				aria-controls="navbar">
				<span class="sr-only">Toggle navigation</span> 
				<span class="icon-bar"></span> 
				<span class="icon-bar"></span> 
				<span class="icon-bar"></span>
			</button>
			<a class="navbar-brand navbar_item" id="logo"><img src="images/ASSISTments_APP_logo.png" style="width:30px; display:inline;margin-right:5px;"/>ASSISTments Apps</a>
		</div>
		<div id="navbar" class="navbar-collapse collapse">
			<ul class="nav navbar-nav">
				<li><a id="landing_page" class="navbar_item nav_active" >Home</a></li>
				<li><a id="skill_builder_page" class="navbar_item" >Math Skill Builders</a></li>
				<li><a id="ap_page" class="navbar_item" >AP Statistics</a></li>
				<li><a id="chemistry_page" class="navbar_item" >Chemistry</a></li>
			</ul>
		</div>
		<!--/.navbar-collapse -->
	</div>
	</nav>

	<!-- Main jumbotron for a primary marketing message or call to action -->
	<div class="jumbotron" style="background-color:white; height:600px;">
		<div class="container">
			<h1><center><small style="color:#333;">ASSISTments APP for Google Classroom</small></center></h1>
			<h2 style="color:#777;"><center>Select a tool to begin</center></h2>
			
			<div class="row" style="margin-top:100px; ">
			<div class="col-md-2 col-md-offset-1">
			<div id="skill_builder_link" class="apps_link">
				<h2><center><img src="images/assistments_app_math.png" style="width:90px;"></center></h2>
				<h3><center>Math Skill Builders</center></h3>
			</div>	
			</div>
			<div class="col-md-2 col-md-offset-2">
			<div id="ap_statistics_link" class="apps_link">
				<h2><center><img src="images/assistments_app_ap.png" style="width:90px;"></center></h2>
				<h3><center>AP Statistics</center></h3>
			</div>
			</div>
			<div class="col-md-2 col-md-offset-2">
			<div id="chemistry_link" class="apps_link">
				<h2><center><img src="images/assistments_app_chemistry.png" style="width:90px;"></center></h2>
				<h3><center>Chemistry</center></h3>
			</div>
			</div>
		</div>
		<div id="alert_panel" style="display:none; margin-top:30px;" class="alert alert-warning alert-dismissible fade in" role="alert">
		<!-- <button class='close' aria-label='Close' data-dismiss='alert' type='button'><span aria-hidden='true'>&times;</span></button> -->
			<p><center style="font-size:1.6em;">This feature is still under construction.</center></p>
		</div>
		</div>
	</div>

	<div class="container footer_section" >

		<hr>

		<footer>
		<p>&copy; ASSISTments 2015</p>
		</footer>
	</div>
	<!-- /container -->

</body>
</html>
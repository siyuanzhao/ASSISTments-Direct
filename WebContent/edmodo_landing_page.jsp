<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>ASSISTments App for Edmodo</title>
<link rel="shortcut icon" href="images/shortcut_logo.png">
<script
	src="https://ajax.googleapis.com/ajax/libs/jquery/2.1.3/jquery.min.js"></script>
<!-- Bootstrap core CSS -->
<link href="../stylesheets/bootstrap.min.css" rel="stylesheet">
<!-- Optional theme -->
<link href="../stylesheets/bootstrap-theme.min.css" rel="stylesheet">
<!-- Latest compiled and minified JavaScript -->
<script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/js/bootstrap.min.js"></script>
<script
	src="https://ajax.googleapis.com/ajax/libs/angularjs/1.4.3/angular.min.js"></script>
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
var toolSelected = "";
	$(function(){
		$(".apps_link").click(function(){
			$("#alert_panel").hide();
			var id = $(this).attr("id");
			toolSelected = id;
			if(toolSelected  == "skill_builder_link"){
				window.location.assign("/direct/SkillBuilderEdmodoClassroom?folder_id=22&tool_type=skill_builder");
			}else if(toolSelected == "ap_statistics_link"){
				window.location.assign("/direct/SkillBuilderGoogleClassroom?folder_id=210644&tool_type=ap_statistics");
			}else if(toolSelected == "chemistry_link"){
				window.location.assign("/direct/SkillBuilderGoogleClassroom?folder_id=226695&tool_type=chemistry");
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
				//window.location.assign("/direct/SkillBuilderGoogleClassroom");
			}else if (id == "ap_page"){
				toolSelected = "ap_statistics_link";
			}else if(id == "chemistry_page"){
				toolSelected = "chemistry_link";
			}
			if(toolSelected  == "skill_builder_link"){
				window.location.assign("/direct/SkillBuilderEdmodoClassroom?folder_id=22&tool_type=skill_builder");
			}else if(toolSelected == "ap_statistics_link"){
				window.location.assign("/direct/SkillBuilderGoogleClassroom?folder_id=210644&tool_type=ap_statistics");
			}else if(toolSelected == "chemistry_link"){
				window.location.assign("/direct/SkillBuilderGoogleClassroom?folder_id=226695&tool_type=chemistry");
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
			<a class="navbar-brand navbar_item" id="logo"><img src="../images/ASSISTments_APP_logo.png" style="width:30px; display:inline;margin-right:5px;"/>ASSISTments Apps</a>
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
			<h1><center><small style="color:#333;">ASSISTments App for Edmodo</small></center></h1>
			<h2 style="color:#777;"><center>Select a tool to begin</center></h2>
			
			<div class="row" style="margin-top:100px; ">
			<div class="col-md-2 col-md-offset-1">
			<div id="skill_builder_link" class="apps_link">
				<h2><center><img src="../images/assistments_app_math.png" style="width:90px;"></center></h2>
				<h3><center>Math Skill Builders</center></h3>
			</div>	
			</div>
			<div class="col-md-2 col-md-offset-2">
			<div id="ap_statistics_link" class="apps_link">
				<h2><center><img src="../images/assistments_app_ap.png" style="width:90px;"></center></h2>
				<h3><center>AP Statistics</center></h3>
			</div>
			</div>
			<div class="col-md-2 col-md-offset-2">
			<div id="chemistry_link" class="apps_link">
				<h2><center><img src="../images/assistments_app_chemistry.png" style="width:90px;"></center></h2>
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
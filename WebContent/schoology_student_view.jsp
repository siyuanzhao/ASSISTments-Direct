<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>ASSISTments APP for Schoology</title>
<link rel="shortcut icon" href="images/ASSISTments_APP_logo.png">
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
var toolSelected = "";
	$(function(){
		$(".apps_link").click(function(){
			$("#alert_panel").hide();
			var id = $(this).attr("id");
			toolSelected = id;
			if(id == "skill_builder_link"){
				window.location.assign("/direct/SkillBuilderGoogleClassroom?folder_id=22&tool_type=skill_builder");
			}else if (id == "ap_statistics_link"){
				window.location.assign("/direct/SkillBuilderGoogleClassroom?folder_id=210644&tool_type=ap_statistics");
			}else if (id == "chemistry_link"){
				window.location.assign("/direct/SkillBuilderGoogleClassroom?folder_id=226695&tool_type=chemistry");
			}
			
		});
		$(".navbar_item").click(function(){
			$("#alert_panel").hide();
			var id = $(this).attr("id");
			if(id == "landing_page" || id == "logo"){
				toolSelected = "landing_page";
				window.location.assign("/direct/schoology_student_view.jsp");
			}else if(id == "skill_builder_page"){
				toolSelected = "skill_builder_link";
				window.location.assign("/direct/SkillBuilderGoogleClassroom?folder_id=22&tool_type=skill_builder");
				//window.location.assign("/direct/SkillBuilderGoogleClassroom");
			}else if (id == "ap_page"){
				toolSelected = "ap_statistics_link";
				window.location.assign("/direct/SkillBuilderGoogleClassroom?folder_id=210644&tool_type=ap_statistics");
			}else if(id == "chemistry_page"){
				toolSelected = "chemistry_link";
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
			<a class="navbar-brand navbar_item" id="logo"><img src="images/ASSISTments_APP_logo.png" style="width:30px; display:inline;margin-right:5px;"/>ASSISTments Apps</a>
		</div>
		<!--/.navbar-collapse -->
	</div>
	</nav>

	<!-- Main jumbotron for a primary marketing message or call to action -->
	<div class="jumbotron" style="background-color: white; height: 600px;">
		<div class="container">


			<table class="table table-hover table-striped"
				style="font-size: 125%;">
				<thead>
					<tr>
						<th>Assignments</th>
						<th></th>
					</tr>
				</thead>
				<tbody>
					<tr>
						<td>Assignment 1</td>
						<td></td>
					</tr>
					<tr>
						<td>Assignment 2</td>
						<td></td>
					</tr>
					<tr>
						<td>Assignment 3</td>
						<td></td>
					</tr>
				</tbody>
			</table>
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
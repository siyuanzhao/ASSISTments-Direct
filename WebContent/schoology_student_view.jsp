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
		
		var rs = "";
		rs+="<div class=\"greeting\"><h2><center><small style=\"color:#333;\">Hello, <u>${sessionScope.studentName}</u>.<br> <br>If this is not ${sessionScope.studentName}, please log out and log back in as yourself. </small></center></h2><center><button type=\"button\" class=\"btn btn-primary\" id=\"continue_btn\">Continue</button></center> </div>";
		$("#problemSets").html(rs);
		$("#continue_btn").click(function(e){
			var listOfAssignments = null;
			var listOfReportLinks = null;
			$.ajax({
				url: 'schoology_classroom/get-list-of-assignments',
				type: 'GET',
				data: {},
				async: false,
				success: function(data) {
					listOfAssignments = data.list_assignments;
					listOfReportLinks = data.report_links;
				},
				error: function(data) {
					console.log(data);
					return;
				}
			});
			var rs = "";
			var newListAssignments = JSON.parse(listOfAssignments);
			var newListReports = JSON.parse(listOfReportLinks);
			if(newListAssignments!=null && newListAssignments.length != 0)
			{
				rs += "<ul class='searchable'>";
				rs += "<table class='table table-hover'><thead style='font-size:0.8em;'><tr class='row'><th class='col-md-1' style='text-align:center;'>Go to Assignment</th><th class='col-md-1' style='text-align:left;'>Comment</th><th class='col-md-8' style='text-align:center;'>Assignment Title</th><th class='col-md-20' style='text-align:center;'>Due Date</th></tr></thead><tbody style='font-size:1.5em;'>";
				for (var i = (newListAssignments.length)-1; i >=0 ; i--)
				{
					var assignment = newListAssignments[i];
					var reportLink = newListReports[i];
					var link = reportLink.link;
					var id = assignment.reference;
					var name = assignment.name;
					var duedate = assignment.duedate.substring(0,11);
					var d = new Date(duedate);
					rs += "<tr class='curriculum_item row'><td class='col-md-1' style='text-align:center;'><a class='view_problem_icon' target='_blank' href='"+link+"'><img src='images/question_mark.png' width='25px' height='25px'></a></td><td class='col-md-1' style='text-align:left;'><a href='javascript:void(0);' class='classroom_share_button' id='update'><input type='hidden' value='"+id+"'><input type='hidden' value='"+name+"'>"
					+ "<img src='images/comment.jpg' width='25px' height='25px' ></a></td><td class='col-md-8' style='text-align:center;'>" + name+ "</td>"+					
					"<td class='col-md-20' style='text-align:center;'>" + d.toLocaleDateString()+"</td>"+
					"</tr>";
				}
			
				rs += "</tbody></table>";
				rs += "</ul>";
				
			}
			else
			{
				rs= "<div><h2 style=\"color:#777;\"><center>No content assigned yet</center></h2></div>";
			}
			$("#problemSets").html(rs);
			$(".classroom_share_button").click(function(e) {
				problem_set_name = $(this).children().first().next().val();
				problem_set_id = $(this).children().first().val();
				$(".modal-header #myModalLabel").text( problem_set_name );
				$('#post_info').hide();
				$('#myModal').modal('show');
				$(".modal").on("hidden.bs.modal", function(){
	    			$("#description-text").val('');
	    			$("#share_to_classroom_btn").removeClass("disabled");
	    			$('#post_info').hide();
	    		});
			});
			$("#share_to_classroom_btn").click(function(e) {
				$(this).addClass("disabled");
				var comment = $("#description-text").val();
				$.ajax({
					url: 'schoology_classroom/student/assignment/comment',
					type: 'POST',
					data: {problem_set_id: problem_set_id, comment: comment},
					async: false,
					success: function(data) {
						console.log(problem_set_name);
						$("#post_info").html("Comment Posted Successfully!");
						$("#post_info").show(700);
					},
					error: function(data) {
						$("#post_info").html("Comment Could Not Be Created! Please Try Again!");
						$("#post_info").show(700);
						console.log(data);
						return;
					}
				});
			});
		});
		
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
			<h1><center><small style="color:#333;">ASSISTments APP for Schoology</small></center></h1>
			<div class="container-fluid">
				<div class="row">
					<div class="col-md-10 col-md-offset-1 main">
						<div id="problemSets">
						</div>
					</div>
				</div>
			</div>
			<!-- Modal -->
			<div class="modal fade" id="myModal" tabindex="-1" role="dialog"
				aria-labelledby="myModalLabel">
				<div class="modal-dialog" role="document">
					<div class="modal-content">
						<div class="modal-header">
							<button type="button" class="close" data-dismiss="modal"
								aria-label="Close">
								<span aria-hidden="true">&times;</span>
							</button>
							<h4 class="modal-title" id="myModalLabel">Share to Schoology Classroom</h4>
						</div>
						<div class="modal-body">
		        			<label for="description-text">Comment:</label>
				            <div>
							<textarea class="form-control" id="description-text"></textarea>	
				            </div>
						</div>
						<div class="alert alert-info" role="alert" id="post_info"></div>
						<div class="modal-footer">
							<button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
							<button type="button" class="btn btn-primary" id="share_to_classroom_btn">Post</button>
						</div>
					</div>
				</div>
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
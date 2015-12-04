<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>ASSISTments APP for Schoology</title>
<link rel="shortcut icon" href="images/ASSISTments_APP_logo.png">
<!-- Bootstrap core CSS -->
<link href="stylesheets/bootstrap.min.css" rel="stylesheet">
<link href="stylesheets/bootstrap-tour.min.css" rel="stylesheet">
<!-- Optional theme -->
<link href="stylesheets/bootstrap-theme.min.css" rel="stylesheet">
<script
	src="https://ajax.googleapis.com/ajax/libs/jquery/2.1.4/jquery.min.js"></script>
<script type="text/javascript" src="js/bootstrap.min.js"></script>
<script type="text/javascript" src="js/bootstrap-tour.min.js"></script>
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
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

var toolSelected = "";
	$(function(){
		$('#post_info').hide();
		$(".nav-tabs a").click(function(){
		       $(this).tab('show');
		   });
		$('.nav-tabs a').on('shown.bs.tab', function(event){
			var x = $(event.target).text();         // active tab
			if(x == "Content already Assigned")
			{
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
					rs += "<table class='table table-hover'><thead style='font-size:0.8em;'><tr class='row'><th class='col-md-1' style='text-align:left;'>View Report</th><th class='col-md-1' style='text-align:left;'>Announce</th><th class='col-md-8' style='text-align:center;'>Assignment Title</th><th class='col-md-20' style='text-align:center;'>Due Date</th></tr></thead><tbody style='font-size:1.5em;'>";
					for (var i = (newListAssignments.length)-1; i >=0 ; i--)
					{
						var assignment = newListAssignments[i];
						var reportLink = newListReports[i];
						var link = reportLink.link;
						var id = assignment.reference;
						var name = assignment.name;
						var duedate = assignment.duedate.substring(0,11);
						var d = new Date(duedate);
						rs += "<tr class='curriculum_item row'><td class='col-md-1' style='text-align:left;'><a class='view_problem_icon' target='_blank' href='"+link+"'><img src='images/view_problems_magnifier.png' style='width:30px'></a></td><td class='col-md-1' style='text-align:left;'><a href='javascript:void(0);' class='classroom_share_button' id='update'><input type='hidden' value='"+id+"'><input type='hidden' value='"+name+"'>"
						+ "<img src='images/announce.png' width='25px' height='25px' ></a></td><td class='col-md-8' style='text-align:center;'>" + name+ "</td>"+					
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
			}
			$(".classroom_share_button").click(function(e) {
				problem_set_name = $(this).children().first().next().val();
				$(".modal-header #myModalLabel").text( problem_set_name );
				$('#myModal').modal('show');
				$(".modal").on("hidden.bs.modal", function(){
	    			$("#description-text").val('');
	    			$("#share_to_classroom_btn").removeClass("disabled");
	    			$('#post_info').hide();
	    		});
			});
			$("#share_to_classroom_btn").click(function(e) {
				$(this).addClass("disabled");
				var update = $("#description-text").val();
				$.ajax({
					url: 'schoology_classroom/post-update',
					type: 'POST',
					data: {problem_set_name: problem_set_name, update: update},
					async: false,
					success: function(data) {
						console.log(problem_set_name);
						$("#post_info").html("Update Posted Successfully!");
						$("#post_info").show(700);
					},
					error: function(data) {
						$("#post_info").html("Update Could Not Be Created! Please Try Again!");
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
				window.location.assign("/direct/SkillBuilderSchoologyClassroom?folder_id=22&tool_type=skill_builder");
			}else if (id == "ap_statistics_link"){
				window.location.assign("/direct/SkillBuilderSchoologyClassroom?folder_id=210644&tool_type=ap_statistics");
			}else if (id == "chemistry_link"){
				window.location.assign("/direct/SkillBuilderSchoologyClassroom?folder_id=226695&tool_type=chemistry");
			}
			
		}); 
		$(".navbar_item").click(function(){
			$("#alert_panel").hide();
			var id = $(this).attr("id");
			if(id == "landing_page" || id == "logo"){
				toolSelected = "landing_page";
				window.location.assign("/direct/SchoologyAppsLandingPage");
			}else if(id == "skill_builder_page"){
				toolSelected = "skill_builder_link";
				window.location.assign("/direct/SkillBuilderSchoologyClassroom?folder_id=22&tool_type=skill_builder");
				//window.location.assign("/direct/SkillBuilderSchoologyClassroom");
			}else if (id == "ap_page"){
				toolSelected = "ap_statistics_link";
				window.location.assign("/direct/SkillBuilderSchoologyClassroom?folder_id=210644&tool_type=ap_statistics");
			}else if(id == "chemistry_page"){
				toolSelected = "chemistry_link";
				window.location.assign("/direct/SkillBuilderSchoologyClassroom?folder_id=226695&tool_type=chemistry");
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
	<div class="jumbotron" style="background-color:white; height:600px;">
		<div class="container">			
			<div>
			<h1><center><small style="color:#333;">ASSISTments APP for Schoology</small></center></h1>
				<!-- Nav tabs -->
				<ul class="nav nav-tabs" role="tablist">
					<li role="presentation" class="active"><a
						href="#content_to_assign" aria-controls="content_to_assign"
						role="tab" data-toggle="tab"> <span style="font-size: 125%;">Content to Assign</span></a></li>
					<li role="presentation"><a href="#assignments"
						aria-controls="assignments" role="tab" data-toggle="tab"><span style="font-size: 125%;">Content already Assigned</span></a></li>
				</ul>

				<!-- Tab panes -->
				<div class="tab-content">
					<div role="tabpanel" class="tab-pane fade in active" id="content_to_assign">
						<h2 style="color:#777;"><center>Select a tool to begin</center></h2>					
						<div class="row" style="margin-top: 100px;">
							<div class="col-md-2 col-md-offset-1">
								<div id="skill_builder_link" class="apps_link">
									<h3>
										<center>
											<img src="images/assistments_app_math.png"
												style="width: 90px;">
										</center>
									</h3>
									<h4>
										<center>Math Skill Builders</center>
									</h4>
								</div>
							</div>
							<div class="col-md-2 col-md-offset-2">
								<div id="ap_statistics_link" class="apps_link">
									<h3>
										<center>
											<img src="images/assistments_app_ap.png" style="width: 90px;">
										</center>
									</h3>
									<h4>
										<center>AP Statistics</center>
									</h4>
								</div>
							</div>
							<div class="col-md-2 col-md-offset-2">
								<div id="chemistry_link" class="apps_link">
									<h3>
										<center>
											<img src="images/assistments_app_chemistry.png"
												style="width: 90px;">
										</center>
									</h3>
									<h4>
										<center>Chemistry</center>
									</h4>
								</div>
							</div>
						</div>
					</div>
					<div role="tabpanel" class="tab-pane fade" id="assignments">
						<div class="container-fluid">
							<div class="row">
								<div class="col-md-10 col-md-offset-1 main">
					          		<!-- <div class="well" ><h4 id="instruction"></h4></div>  -->
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
					        			<label for="description-text">Update:</label>
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
			</div> 
		<div id="resp_message">${sessionScope.json }</div>
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
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<!-- 
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
-->
<!DOCTYPE html>
<html lang="en">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Home - ASSISTments Direct App</title>
<link rel="stylesheet" href="../stylesheets/bootstrap.min.css">
<link rel="stylesheet" href="../stylesheets/bootstrap-theme.min.css">
<script type="text/javascript" src="../js/jquery.min.js"></script>
<script type="text/javascript" src="../js/bootstrap.min.js"></script>
<script src="https://apis.google.com/js/client.js"></script>
<style>
.table-hover tbody tr:hover td, .table-hover tbody tr:hover th {
	background-color: #d6e9c6;
}
</style>
<script type="text/javascript">
var CLASSROOM_CLIENT_ID = "757036402283-8o3nu8pdve8snhj8ds11te8bnsrnmuu6.apps.googleusercontent.com";
var SCOPES = ["https://www.googleapis.com/auth/classroom.profile.emails",
              "https://www.googleapis.com/auth/classroom.rosters.readonly",
              "https://www.googleapis.com/auth/classroom.courses"];
var ownerId = "";
var problem_set_id = "";
var problem_set_name = "";
var link_to_share = "";
$(function() {	
	$(".classroom_share_button").click(function(e) {
		problem_set_id = $(this).children().first().val();
		problem_set_name = $(this).children().first().next().val();
		gapi.auth.authorize({
			 'client_id': CLASSROOM_CLIENT_ID,
			 'scope': SCOPES,
			 'immediate': false}, handleAuthResult);
	});
	
	$("#share_to_classroom_btn").click(function(e) {
		var course_id = $("#courses_select").val(); 
		var course_name = $("#courses_select option:selected").text();
		$.ajax({
			url: '../google_classroom/setup',
			type: 'POST',
			data: {course_id: course_id, course_name: course_name, email: ownerId, problem_set_id: problem_set_id},
			async: false,
			success: function(data) {
				console.log(problem_set_name);
				problem_set_name = encodeURI(problem_set_name);
				link_to_share = encodeURI(data.classroom_link);
			},
			error: function(data) {
				console.log(data);
				return;
			}
		});
		var url = 'https://classroom.google.com/share?url='+link_to_share+'&title='+problem_set_name;
		window.open(url, '', 'width=650,height=450');
	});
})

function handleAuthResult(authResult) {
		if (authResult && !authResult.error) {
			console.log(authResult);
		    //get user profile
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
		    				$("#courses_select").append($("<option></option>")
		    					.attr("value", course.id)
		    					.html(course.name + " - " + course.section));
		    			}
		    		}
		    		$('#myModal').modal('show')
		    	}
		    });
		}
		
	}
</script>
</head>
<body>
	<div style="margin: 30px auto 0 auto;" class="container">
		<table class="table table-striped table-hover">
			<c:forEach items="${sessionScope.shared_problem_sets }"
				var="problem_set" varStatus="loop">
				<tr>
					<td><a href="/direct/view_problems/${problem_set.id }" target="_blank">${problem_set.name }</a></td>
					<td>
						<a href="javascript: void(0);"  class="classroom_share_button">
							<input type="hidden" value="${problem_set.id }">
							<input type="hidden" value="${problem_set.name }">
							<img src="//www.gstatic.com/classroom/logo_square_48.svg" width="30px"
							height="30px" style="position: relative;">
						</a>
					</td>
				</tr>
			</c:forEach>
		</table>

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
					<h4 class="modal-title" id="myModalLabel">Choose class</h4>
				</div>
				<div class="modal-body">
					<form class="pure-form" id="courses_form">
						<div style="text-align: center; margin: 30px 0 0 0;">
							<div style="margin: 20px 0 0 0;"></div>
							<select id="courses_select" class="form-control">
							</select><br>
							<br>
							<div style="margin: 20px 0 0 0;" id="classroom_share_button_div">
							</div>
						</div>
					</form>
				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-primary" id="share_to_classroom_btn">Share to
						Google Classroom</button>
					<button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
				</div>
			</div>
		</div>
	</div>
</body>
</html>
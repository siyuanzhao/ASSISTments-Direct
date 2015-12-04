<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
   <%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta name="title" content="ASSISTments Direct" />
<link rel="image_src" href="http://www.assistments.org:8080/direct/images/logo_yellow_square.png" />
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<meta name="google-signin-client_id"
	content="757036402283-8o3nu8pdve8snhj8ds11te8bnsrnmuu6.apps.googleusercontent.com">
<title>${sessionScope.problem_set_name }</title>
<script type="text/javascript" 	src="../js/jquery.min.js"></script>
<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/css/bootstrap.min.css">
<!-- Optional theme -->
<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/css/bootstrap-theme.min.css">
<!-- Latest compiled and minified JavaScript -->
<script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/js/bootstrap.min.js"></script>
<script type="text/javascript">
var CLIENT_ID = "757036402283-8o3nu8pdve8snhj8ds11te8bnsrnmuu6.apps.googleusercontent.com";
var SCOPES = ["https://www.googleapis.com/auth/classroom.profile.emails",
              "https://www.googleapis.com/auth/classroom.rosters.readonly"];

/**
 * Check if current user has authorized this application.
 */
function checkAuth() {
  gapi.auth.authorize(
    {
      'client_id': CLIENT_ID,
      'scope': SCOPES,
      'immediate': true
    }, handleAuthResult);
}

/**
 * Handle response from authorization server.
 *
 * @param {Object} authResult Authorization result.
 */
function handleAuthResult(authResult) {
  var authorizeDiv = document.getElementById('authorize-div');
  if (authResult && !authResult.error) {
    // Hide auth UI, then load client library.
    authorizeDiv.style.display = 'none';
    //get user profile
    var request = gapi.client.request({
		root : 'https://classroom.googleapis.com',
		path : 'v1/userProfiles/me'
	});
    $("#message").html("Fetch your profile from Google...<br>");
    var id;
	var course_id = $("input#course_id").val();
	var assignment_ref = $("input#assignment_ref").val();
	var firstName;
	var lastName;
	var role = "teacher";
	request.execute(function(resp) {
		if(resp.error != null) {
			if(resp.error.message.indexOf("ClassroomApiDisabled") >= 0) {
				$("#message").html("Classroom API is not disabled on your account. Please contact your admin.<br>");	
			} else {
				$("#message").html("Sorry... Google Classroom is not available for your google account at this time.<br>");	
			}
			return;
		}
		id = resp.id;		
		firstName = resp.name.givenName;
		lastName = resp.name.familyName;
		$("#message").html("Verify you are a student or a teacher...<br>");
		if(resp.permissions != null) {
			//console.log(firstName);
			request = gapi.client.request({
				root : 'https://classroom.googleapis.com',
				path : 'v1/courses/'+course_id+"/teachers"
			});
			request.execute(function(resp) {
				role = "student";
				if(resp.error != null) {
					// access to api is denied
					console.log(resp.error);
					role = 'student';
				} else {
					var teachers = resp.teachers;
					if(teachers.length > 0) {
						var i = 0;
						for(i=0; i < teachers.length; i++) {
							var teacher = teachers[i];
							console.log(teacher.userId);
							if(teacher.userId == id) {
								role = "teacher";
							}
						}
					}
				}
				//console.log(role);
				//log in the user to ASSISTments 
				if(role == "student") {
					$("#message").html("Load your assignment...<br>");
				} else {
					$("#message").html("Build your report...<br>");
				}
				
				$.ajax({
					url: 'start',
					type: 'GET',
					data: {id: id, role: role, firstName: firstName, lastName: lastName, assignmentReference: assignment_ref},
					dataType: "json",
					async: true,
					success: function(resp) {
						window.location.replace(resp.location);
					},
					error: function(resp) {
						$("#message").html("There is an error.<br> Error message: " + resp.responseText);
					}
				});
			});
		} else {
			role = 'student';
			$("#message").html("Load your assignment...<br>");
			$.ajax({
				url: 'start',
				type: 'GET',
				data: {id: id, role: role, firstName: firstName, lastName: lastName, assignmentReference: assignment_ref},
				dataType: "json",
				async: true,
				success: function(resp) {
					window.location.replace(resp.location);
				},
				error: function(resp) {
					$("#message").html("There is an error.<br> Error message: " + resp.responseText);
				}
			});
		}
		
	});
	
  } else {
	  console.log(authResult);
    // Show auth UI, allowing the user to initiate authorization by
    // clicking authorize button.
    authorizeDiv.style.display = 'inline';
  }
}

/**
 * Initiate auth flow in response to user clicking authorize button.
 *
 * @param {Event} event Button click event.
 */
function handleAuthClick(event) {
  gapi.auth.authorize(
    {client_id: CLIENT_ID, scope: SCOPES, immediate: false},
    handleAuthResult);
  return false;
}
	/**
	 * Append a pre element to the body containing the given message
	 * as its text node.
	 *
	 * @param {string} message Text to be placed in pre element.
	 */
	function appendPre(message) {
		var pre = document.getElementById('output');
		var textContent = document.createTextNode(message + '\n');
		pre.appendChild(textContent);
	}
</script>
<script src="https://apis.google.com/js/client.js?onload=checkAuth"></script>
</head>
<body>
	<input type="text" id="course_id" value="${sessionScope.course_id }" style="display: none;">
	<input type="text" id="assignment_ref" value="${sessionScope.assignmentReference }" style="display: none;">
    <div id="authorize-div" style="display: none">
		<div class="alert alert-info" role="alert">
			This message shows up because either this is your first time running ASSISTments Tutor or you haven't signed into Google. <br>
			Click <button type="button" class="btn btn-primary" onClick='handleAuthClick(event);'>Authorize</button> to proceed.
		</div>
    </div>
    <div style="height: 700px;">
    <img alt="ASSISTments Logo" src="../images/logo_yellow_square.png"
    	style="position: relative; left: 200px;" >
    <div id="message" style="margin: 30px 0 0 0; color: blue;">
    	
    </div>
    </div>
</body>
</html>
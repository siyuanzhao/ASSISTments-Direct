<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<meta name="google-signin-client_id"
	content="329183900516-6ejbv88ntbavjbqn7t8r610krglk2rsq.apps.googleusercontent.com">
<title>Google Classroom Test</title>
<script type="text/javascript">
var CLIENT_ID = "329183900516-6ejbv88ntbavjbqn7t8r610krglk2rsq.apps.googleusercontent.com";
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
      'immediate': false
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
	
	request.execute(function(resp) {
		//send user id and user role back to server
		var id = resp.id;
		var role = "student";
		if(resp.permissions != null || resp.permissons.length > 0) {
			role = "teacher";
		}
		$.ajax({
			url: 'start',
			type: 'POST',
			data: {id: id, role: role},
			dataType: "json",
			async: true,
			success: function(resp) {
				window.location(data.location);
			},
			error: function(resp) {
			}
		});
	});
  } else {
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
	<div id="authorize-div" style="display: none">
      <span>Authorize access to Classroom API</span>
      <!--Button for the user to click to initiate auth sequence -->
      <button id="authorize-button" onclick="handleAuthClick(event)">
        Authorize
      </button>
    </div>
    <pre id="output"></pre>
</body>
</html>
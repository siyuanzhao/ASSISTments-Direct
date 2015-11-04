<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>ASSISTments with Google Sites</title>

<!-- Latest compiled and minified CSS -->
<link rel="stylesheet"
	href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/css/bootstrap.min.css">
<!-- Optional theme -->
<link rel="stylesheet"
	href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/css/bootstrap-theme.min.css">
<script
	src="https://ajax.googleapis.com/ajax/libs/jquery/2.1.4/jquery.min.js"></script>
<!-- Latest compiled and minified JavaScript -->
<script
	src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/js/bootstrap.min.js"></script>
<script src="https://apis.google.com/js/client.js"></script>
<script src="https://ajax.googleapis.com/ajax/libs/angularjs/1.4.3/angular.min.js"></script>
<style type="text/css">
body{

	font-family:"Times New Roman";
	font-size: 1.6em;
}
</style>
<script type="text/javascript">
	var CLASSROOM_CLIENT_ID = "757036402283-8o3nu8pdve8snhj8ds11te8bnsrnmuu6.apps.googleusercontent.com";
	var SCOPES = [ "https://sites.google.com/feeds/" ];
	var ownerId = "";
	var problem_set_id = "";
	var problem_set_name = "";
	var access_token = "";
	var siteName = "";
	var folderId = "";
	var linkType = "";
	var assistmentsVerified = false;
	var buttonClicked = "";
	$(function() {
		$("#alert").hide();
		$("#error").hide();
		$(".progress").hide();
		$("#verified_section").hide();
		$("#external_verified_list").hide();
		$("#error_message_invalid_url").hide();
		$("#error_message_not_share").hide();
		$("button").click(function(){
			buttonClicked = $(this).attr("id");
		});
		$("form").submit(function(e) {
			e.preventDefault();
			$("#alert").hide();
			$("#error").hide();
			$("#error_message_invalid_url").hide();
			$("#error_message_not_share").hide();
			//problem_set_id = $(this).children().first().val();
			//problem_set_name = $(this).children().first().next().val();
			ownerId = $("#user_id").val();
			folderId = $("#folder_id").val();
			siteName = $("#site_name").val();
			linkType = $(".link_type_radio:checked").val();
			assistmentsVerified = false;
			if(linkType == "verified" && $("#external_verified").is(':checked')){
				$(".progress").show();
				var url = $("#url").val();
				var form = $("#form").val();
				var urlflag = false;
				$.ajax({
					url:"../CheckSpreadsheetUrl",
					type:"POST",
					data:{url:url},
					dataType:'json',
					async:false,
					success: function(data){
						if(data.result == "true"){
							//the spreadsheet is shared to the domain user
							urlflag = true;
							$("#error_message_not_share").hide();
							$("#error_message_invalid_url").hide();
						}else{
							if(data.reason == "invalid_url"){
								$(".progress").hide();
								$("#error_message_not_share").hide();
								$("#error_message_invalid_url").slideDown();
							}else{
								//the spreadsheet is not shared to the domain user, check if it's published to the web
								var jsonUrl = data.jsonUrl;
								$.ajax({
									url:jsonUrl,
									type:"GET",
									dataType:'json',
									async:false,
									success: function(data){
										urlflag = true;
										$("#error_message_not_share").hide();
										$("#error_message_invalid_url").hide();
									},
									error: function(data){
										$(".progress").hide();
										$("#error_message_invalid_url").hide();
										$("#error_message_not_share").slideDown();
									}
								});
							}
						}
					},
					error: function(data){
						$(".progress").hide();
						alert("error!");
					}
				});
				if(!urlflag) return;
			}
			if(linkType == "verified" && $("#assistments_verified").is(':checked')){
				//record 
				assistmentsVerified = true;
			}
			gapi.auth.authorize({
				'client_id' : CLASSROOM_CLIENT_ID,
				'scope' : SCOPES,
				'immediate' : false
			}, handleAuthResult);
		});
		
		$("input[type='radio']").click(function(){
			if($(this).val() == 'generic'){
				$("#verified_section").hide();
			}else if ($(this).val() == 'verified'){
				$("#verified_section").show();
			}
		});
			
		$("#external_verified").click(function(){
			$("#external_verified_list").toggle();
		});

		function handleAuthResult(authResult) {
			if (authResult && !authResult.error) {
				console.log(authResult.access_token);
				accessToken = authResult.access_token;
				if(buttonClicked == "google_site_button"){
					createGoogleSites();
				}else if(buttonClicked == "google_site_update_button"){
					updateGoogleSites();
				}
				
			}

		}
		function createGoogleSites() {
			console.log(site_name);
			$(".progress").show();
			$("button").prop("disabled", true);
			var url = "";
			var form = "";
			if(linkType == "verified"){
				url = $("#url").val();
				form = $("#form").val();
			}
			$.ajax({
				url : "create_google_sites",
				timeout : 150000,
				type : "POST",
				data : {
					access_token : accessToken,
					site_name : siteName,
					owner_id : ownerId,
					folder_id : folderId,
					link_type : linkType,
					assistments_verified : assistmentsVerified,
					url : url,
					form : form
				},
				dataType : 'json',
				success : function(data) {
					$(".progress").hide();
					$("#alert").show();
					$("button").prop("disabled",false);
				},
				error : function(data) {
					console.log("Site Created Unsuccessfully!");
					$(".progress").hide();
					$("#error").show();
					$("button").prop("disabled", false);
				}
			});
		}
		function updateGoogleSites() {
			console.log(site_name);
			$(".progress").show();
			$("button").prop("disabled", true);
			var url = "";
			var form = "";
			if(linkType == "verified"){
				url = $("#url").val();
				form = $("#form").val();
			}
			$.ajax({
				url : "update_google_sites",
				timeout : 150000,
				type : "POST",
				data : {
					access_token : accessToken,
					site_url : siteName,
					owner_id : ownerId,
					folder_id : folderId,
					link_type : linkType,
					assistments_verified : assistmentsVerified,
					url : url,
					form : form
				},
				dataType : 'json',
				success : function(data) {
					$(".progress").hide();
					$("#alert").show();
					$("button").prop("disabled",false);
				},
				error : function(data) {
					console.log("Site Created Unsuccessfully!");
					$(".progress").hide();
					$("#error").show();
					$("button").prop("disabled", false);
				}
			});
		}

	});

	
</script>
</head>
<body>
	<div class="container">
		<div id="loginbox" style="margin-top: 100px;"
			class="mainbox col-md-6 col-md-offset-3 col-sm-8 col-sm-offset-2">
			<div class="panel panel-info">
				<div class="panel-heading">
					<div style="font-size: 22px">
						<img src="/direct/images/assistments.png" style="width: 30px;"
							class="img-circle">&nbsp;Import Folders into Google Sites
					</div>
				</div>
				<div style="padding-top: 10px" class="panel-body">
					<form ng-app="" class="form-horizontal" role="form" name="main_form" novalidate>
						<div class="form-group" ng-class="(userId == null) ? 'has-error' : 'has-success'" >
							<label for="user_id" class="col-sm-3 control-label">User
								Id</label>
							<div class="col-sm-9">
								<input type="text" class="form-control" id="user_id"
									placeholder="User Id" ng-model="userId" required>
							</div>
						</div>
						<div class="form-group" ng-class="(folderId == null) ? 'has-error' : 'has-success'">
							<label for="folder_id" class="col-sm-3 control-label">Folder
								Id</label>
							<div class="col-sm-9">
								<input type="text" class="form-control" id="folder_id"
									placeholder="Folder Id" ng-model="folderId" required>
							</div>
						</div>
						<div class="form-group" ng-class="(siteName == null) ? 'has-error' : 'has-success'">
							<label for="site_name" class="col-sm-3 control-label">Site
								Name</label>
							<div class="col-sm-9">
								<input type="text" class="form-control" id="site_name"
									placeholder="Your site name" ng-model="siteName" required>
							</div>
						</div>
						<div class="form-group">
							<div class="radio col-sm-9 col-sm-offset-3">
							  <label>
							    <input type="radio" class="link_type_radio" name="link_type_radios" id="generic" value="generic" checked>
							    Create generic link
							  </label>
							</div>
							<div class="radio col-sm-9 col-sm-offset-3">
							  <label>
							    <input type="radio" class="link_type_radio" name="link_type_radios" id="verified" value="verified">
							    Create verified teacher link
							  </label>
							</div>
						</div>
						<div id="verified_section">
							<div class="form-group">
								<div class="checkbox col-sm-9 col-sm-offset-3">
								  <label>
								    <input id="assistments_verified" type="checkbox" value="assistments_verified">
								    ASSISTments verified teacher list
								  </label>
								</div>
								<div class="checkbox col-sm-9 col-sm-offset-3">
								  <label>
								    <input id="external_verified" type="checkbox" value="external_verified">
								    External verified teacher list
								  </label>
								</div>
							</div>
							<div id= "external_verified_list">
								<div class="form-group" >
								<label for="url" class="col-sm-1 col-sm-offset-3 control-label">URL
									</label>
								<div class="col-sm-8">
									<input type="text" class="form-control" id="url"
										placeholder="spreadsheet url" ng-model="url">
								</div>
								</div>
								<div class="form-group" >
								<label for="form" class="col-sm-1 col-sm-offset-3 control-label">Form
									</label>
								<div class="col-sm-8">
									<input type="text" class="form-control" id="form"
										placeholder="google form url" ng-model="form">
								</div>
								</div>
							</div>
						</div>
						<div class="form-group">
							<div class="col-sm-offset-3 col-sm-3">
								<button ng-disabled="main_form.$invalid" type="submit" class="btn btn-primary" id="google_site_button">Create a website</button>
							</div>
							<div class="col-sm-3">
								<button ng-disabled="main_form.$invalid" type="submit" class="btn btn-primary" id="google_site_update_button">Update the website</button>
							</div>
						</div>
						<div class="alert alert-success" role="alert" id="alert">Site
							Created Successfully!</div>
						<div class="alert alert-danger" role="alert" id="error">Something
							must go wrong!</div>
						<div class="alert alert-danger" role="alert" id="error_message_invalid_url">Sorry.. URL doesn't point to a Google Spreadsheet. Please follow <a href="#">the instruction</a>
						 and type in a valid URL.</div>	
						<div class="alert alert-danger" role="alert" id="error_message_not_share">Sorry.. We cannot access to this Google Spreadsheet. You can either publish this sheet or share this sheet with us. Please follow <a href="#">the instruction</a>.</div>	
						<div class="progress">
							<div class="progress-bar progress-bar-striped active"
								id="progress_bar" role="progressbar" aria-valuenow="90"
								aria-valuemin="0" aria-valuemax="100" style="width: 90%">
							</div>
						</div>
					</form>
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
						<h4 class="modal-title" id="myModalLabel">Site Name</h4>
					</div>
					<div class="modal-body">
						<div class="form-group">
							<label class="sr-only" for="site_name">Site Name</label> <input
								type="text" class="form-control" id="site_name"
								placeholder="Site Name">
						</div>
						<div class="alert alert-success" role="alert" id="alert">Site
							Created Successfully!</div>
						<div class="alert alert-danger" role="alert" id="error">Something
							must go wrong!</div>
						<div class="progress">
							<div class="progress-bar progress-bar-striped active"
								id="progress_bar" role="progressbar" aria-valuenow="90"
								aria-valuemin="0" aria-valuemax="100" style="width: 90%">
							</div>
						</div>
					</div>
					<div class="modal-footer">
						<button type="button" class="btn btn-primary" id="submit_btn">Submit</button>
						<button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
					</div>
				</div>
			</div>
		</div>

	</div>
</body>
</html>
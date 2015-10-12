<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<script type="text/javascript" 	src="js/jquery.min.js"></script>
<script type="text/javascript" 	src="js/jquery-ui.min.js"></script>
<script type="text/javascript" 	src="js/tinymce/tinymce.min.js"></script>
<script type="text/javascript" 	src="js/tinymce/jquery.tinymce.min.js"></script>
<link rel="stylesheet" 	href="stylesheets/jquery-ui.css">
<link rel="stylesheet" href="pure-release-0.6.0/pure-min.css">
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Details Page</title>
<style type="text/css">

/*
p{
	font-family:"Times New Roman", Times, serif;
	font-size: 1.2em;
}
*/
</style>
<script type="text/javascript">
	$(function() {
		$("#tabs").tabs();
		$("#result").hide();
		$("#indicator").hide();
		$("#verify_teacher_section").hide();
		$("#external_teacher_list_section").hide();
		$("#personalized_section").hide();
		$("#generated_link").hide();
		$("#reshareable_option").hide();
		$("#edit_view_problem_section").hide();
		
		$("#num_of_problem").spinner({
			max: 5,
			min: 1
		});
		$("#num_of_problem").spinner("value", 3);
		
		$optional_message = $("#optional_message");
		$message_area = $("#optional_message_area");
		$optional_message.tinymce({
			theme : "modern",
			setup : function(editor) {
				editor.on('keyup', function(e) {
					$message_area.html(tinymce.get("optional_message").getContent());
				});
			},
			toolbar : false,
			menubar : false,
			statusbar : false,
			content_css : "stylesheets/tinymce.css"
		});	
		
		
		$("input[name=link_type]").click(function(){
			$(".link_type_section").hide();
			$("#"+$(this).val()+"_section").show();
			if($(this).val() != "generic"){
				$("#reshareable_option").show();
			}else{
				$("#reshareable_option").hide();
			}
		});
		$("#trust_external_teacher_list").click(function(){
			$("#external_teacher_list_section").toggle();
		});
		
		$("#create").click(function(event){
			event.preventDefault();
			var problem_set_id = $("#problem_set_id").val();
			var distributor_id = $("#distributor_id").val();
			var linkType = $("input[name=link_type]:checked").val();
			if(linkType == "generic"){
				$("#indicator").show();
				$.ajax({
					url:"CreateGenericLink",
					type:"POST",
					data:{problem_set_id:problem_set_id, distributor_id:distributor_id},
					dataType:'json',
					async:true,
					success: function(data){
						$("#indicator").hide();
						var link = data.generic_link;
						$("#generic_link").attr("href", link);
						$("#generic_link").html(link);
						$("#generated_link").show();
					},
					error: function(data){
						$("#indicator").hide();
						alert("error!");
					}
				});
			} else if(linkType == "verify_teacher"){
				$("#indicator").show();
				var url = "";
				var form = "";
				var assistmentsVerified = false;
				if($("#trust_external_teacher_list").is(":checked")){
					url = $("#url").val();
					form = $("#form").val();
					var urlflag = false;
					$.ajax({
						url:"CheckSpreadsheetUrl",
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
									$("#indicator").hide();
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
											$("#indicator").hide();
											$("#error_message_not_share").hide();
											$("#error_message_invalid_url").hide();
										},
										error: function(data){
											$("#indicator").hide();
											$("#error_message_invalid_url").hide();
											$("#error_message_not_share").slideDown();
										}
									});
								}
							}
						},
						error: function(data){
							$("#indicator").hide();
							alert("error!");
						}
					});
					if(urlflag == false){
						event.preventDefault();
						return;
					}
				}
				if($("#trust_as_teacher_list").is(":checked")){
					assistmentsVerified = true;
				}
				$.ajax({
					url:"CreateRestrictedSharedLink",
					type:"POST",
					data:{url:url, form:form, problem_set_id:problem_set_id, distributor_id:distributor_id, assistments_verified:assistmentsVerified},
					dataType:'json',
					async: true,
					success: function(data){
						$("#indicator").hide();
						var link = data.generic_link;
						$("#generic_link").attr("href", link);
						$("#generic_link").html(link);
						$("#generated_link").show();
					},
					error: function(data){
						$("#indicator").hide();
						alert("error!");
					}
				});
			} else if(linkType == "personalized"){
				$("#indicator").show();
				var emails = $("#emails").val();
				var message = $("#message_preview").html();
				var problem_set_id = $("#problem_set_id").val();
				var distributor_id = $("#distributor_id").val();
				$.ajax({
					url: "create_share_link",
					type: "POST",
					data: {emails : emails, message : message, problem_set_id : problem_set_id, distributor_id : distributor_id},
					success: function(data) {
						$("#indicator").hide();
						$("#generated_link").hide();
						$("#result").html("Direct links have been successfully sent to these emails");
						$("#result").show("highlight");
						$("#emails").val("");
						$("#optional_message_area").html("");
						$("#optional_message").html("");
					},
					error: function(data) {
						$("#indicator").hide();
						$("#result").html("There is an error in the server. Please try it again later!");
						$("#result").show("highlight");
					}
				});
			}
			
		});
		$("#edit_view_problem").click(function(){
			$("#edit_view_problem_section").toggle();
			$("#view_problems_link").toggle();
		});
		$("input[type=radio]").click(function(){
			$("#generated_link").hide();
		});
	});
</script>
</head>
<body>
<div id="tabs">
	<ul>
		<li>
			<a href="#tabs-1">Share Links</a>
		</li>
	</ul>
	<div id="tabs-1" style="text-align: left">
		<p>Teachers can use <b>share link</b> to create an assignment link and a report link for them to use. 
		You can use it to create your own links or give it to teachers to create their links
		 (by e-mail or on a website for example). </p>
		 
		 <form class="pure-form" style="margin-left:15px;">
		 	<fieldset>
		 		<!-- generic link -->
		 		<p>
		 		<label for="generic_link_radio_btn">
            		<input id="generic_link_radio_btn" type="radio" name="link_type" value="generic" checked>
            		Create a link that anyone can use
        		</label>
        		</p>
        		<!-- verified teacher link -->
		 		<p>
        		<label for="verify_teacher">
            		<input id="verify_teacher" type="radio" name="link_type" value="verify_teacher">
					Create a link for users on the verified teacher list
        		</label>
        		</p>
        		<div id="verify_teacher_section" class="link_type_section" style="margin-left:13px;">
		 			<p>
	        		<label for="trust_as_teacher_list">
	            	<input id="trust_as_teacher_list" type="checkbox"> ASSISTments verified teacher list
	        		</label>
	        		</p>
		 			<p>
	        		<label for="trust_external_teacher_list">
	            		<input id="trust_external_teacher_list" type="checkbox"> External verified teacher list
	        		</label>
	        		</p>
	        		<div style="margin-left:13px;" id="external_teacher_list_section">
				 		<p style="margin:5;padding:0;">
	        			<label style="text-align:center;" for="url" class="pure-u-1-24">URL</label>
	        			<input style="height:35px;" type="text" class="pure-u-1-6" id="url" name="URL" value="">
	        			</p>
		 				<p style="margin:5;padding:0;">
	        			<label style="text-align:center;" for="form" class="pure-u-1-24">Form</label>
	        			<input style="height:35px;" type="text" class="pure-u-1-6" id="form" name="form" value="">
	        			</p>
	        		</div>
	        		<div id="error_message_invalid_url" style="border:2px solid blue;width:90%; background-color:#ccebf3; display:none;">
	        		<p style="margin-left:30px;">Sorry.. URL doesn't point to a Google Spreadsheet. Please follow <a href="#">the instruction</a> and type in a valid URL.</p>
	        		</div>
	        		
	        		<div id="error_message_not_share" style="border:2px solid blue;width:90%; background-color:#ccebf3; display:none;">
	        		<p>Sorry.. We cannot access to this Google Spreadsheet. You can either publish this sheet or share this sheet with us. Please follow <a href="#">the instruction</a>.</p>
	        		</div>
	        		
        		</div>
        		<!-- personalized link -->
        		<p>
        		<label for="personalized_link">
            		<input id="personalized_link" type="radio" name="link_type" value="personalized">
					Create a link for specific users listed here
        		</label>
        		</p>
        		<div id="personalized_section" class="link_type_section">
        			<div class="pure-form">
        			<p></p>
						<input type="text" name="emails" id="emails" placeholder= "Email addresses.. (Comma separated)" class="pure-input-1-2">
						<p></p>
						<textarea class="pure-input-1-2"  id="optional_message" rows="8" ></textarea>
						<p>Email Preview</p>
						<p id="message_preview" style="font-size: small; padding: 10px 0 10px 20px; background: #e9e9e9;">
							<span id="optional_message_area"></span>
							You can use these links to give this assignment to your students and look at your reports. <br><br>
			 				These are for the problem set: ${sessionScope.problem_set_name }<br><br> 
			 				Assignment Link: Give this to your students.<br>
			 				<span class="replace">Assignment Link shows up here.</span><br><br>
			 
			 				Report Link: Go here to see how your students performed. <br>
			 				<span class="replace">Report Link shows up here.</span><br><br>
			 
			 				${sessionScope.distributor_name }
						</p>
						<input type="hidden"  value="${sessionScope.problem_set_id }" id="problem_set_id" >
						<input type="hidden"  value="${sessionScope.distributor_id }" id="distributor_id">
						
						<!-- 
						<button id="send" class="pure-button pure-button-primary">Send</button> 
						<img alt="indicator" src="images/indicator.gif" style="position: relative; top: 10px;" id="indicator">
						 -->
						 
					</div>
        		</div>
        		
        		<p id="reshareable_option">
        		<label for="reshareable">
        			<input id="reshareable" type="checkbox" name="reshareable" > 
        			I want the link to be re-shareable
        		</label>
        		</p>
        		
        		<p style="margin: 10px 0 0 10px;">
        			<label for="edit_view_problem">
        				<input id="edit_view_problem" type="checkbox" name="edit_view_problem">
        				I want to edit view problem
        			</label>
        		</p>
        		<p id="view_problems_link"><a href="../direct/view_problems/${sessionScope.problem_set_id }" target="_blank">View Problems</a></p>
        		<div id="edit_view_problem_section">
        		<p>
        			<textarea id="problem_view" class="pure-input-1-2" rows="5" readonly>
Problem One
Problem Two
Problem Three
        			</textarea>
        		</p>
        		<p>
        			<label for="not_show_problem">
        				<input id ="not_show_problem" type="checkbox" name="not_show_problem">
        				Do not show any problem
        			</label>
        		</p>
        		<p>
        			<label for="num_of_problem" class="pure-u-1-12">Pick another</label>
        			<input id="num_of_problem" class="ui-spinner-input pure-u-1-12" style="width:50px;height:30px;">
        			<button class="pure-button pure-button-active pure-u-1-12" style="width:115px;">Generate</button>
        		</p>
        		</div>
        		
        		<br/>
        		<button class="pure-button pure-button-active" id="create">Create</button>
				<img alt="indicator" src="images/indicator.gif" style="position: relative; top: 10px;" id="indicator">
		 	</fieldset>
		 </form>
		 
		 <div id="generated_link" style="border:1px solid black; margin:15px; height:50px;" class="pure-u-1-2">
		 	<div style="margin:12px;"><a id="generic_link" href="${sessionScope.generic_link }" target="_blank">${sessionScope.generic_link }</a></div>
		 </div>
		 
		<div id="result" style="margin: 20px 0 0 0;"></div>
	</div>
</div>
</body>
</html>
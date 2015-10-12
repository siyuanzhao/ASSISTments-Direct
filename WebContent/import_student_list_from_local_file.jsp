<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>ASSISTments Direct</title>
<link rel="stylesheet" 	href="pure-release-0.6.0/pure-min.css">
<link rel="stylesheet" 	href="stylesheets/styles.css">
<link rel="stylesheet" 	href="stylesheets/teacher.css">
<link rel="stylesheet" 	href="stylesheets/jquery-ui.css">
<script src="https://apis.google.com/js/platform.js" async defer></script>
	<script type="text/javascript" 	src="js/jquery.min.js"></script>
<script type="text/javascript" 	src="js/jquery-ui.min.js"></script>
<style type="text/css">
	.home-menu .pure-menu-selected a {
		color: white !important;
	}
	.google-selector{
		width:30%;
		color:#484848;
		background-color:#e0e0e0;
		border-radius:3px;
		height:32px;
	}
	select:hover, select:focus {
  		color: #484848;
  		background-color: #d3d3d3;
	}
	.form-block{
		margin:30px 0 30px 0;
	}
	
	.ui-tooltip, .arrow:after {
	    background: black;
	    border: 2px solid white;
	  }
	  .ui-tooltip {
	    padding: 10px 20px;
	    color: white;
	    border-radius: 20px;
	    font: bold 14px "Helvetica Neue", Sans-Serif;
	    text-transform: uppercase;
	    box-shadow: 0 0 7px black;
	  }
	  .arrow {
	    width: 70px;
	    height: 16px;
	    overflow: hidden;
	    position: absolute;
	    left: 50%;
	    margin-left: -35px;
	    bottom: -16px;
	  }
	  .arrow.top {
	    top: -16px;
	    bottom: auto;
	  }
	  .arrow.left {
	    left: 20%;
	  }
	  .arrow:after {
	    content: "";
	    position: absolute;
	    left: 20px;
	    top: -20px;
	    width: 25px;
	    height: 25px;
	    box-shadow: 6px 5px 9px -9px black;
	    -webkit-transform: rotate(45deg);
	    -ms-transform: rotate(45deg);
	    transform: rotate(45deg);
	  }
	  .arrow.top:after {
	    bottom: -20px;
	    top: auto;
	  }
	  
</style>
<script>
$(function() {
	$("#upload_student_list").click(function(){
			var firstSectionName = $("#group_name option:first-child").html();
			var firstSectionId = $("#group_name option:first-child").val();
			
			var student_list_file_name = $("#student_list_file").val();
			if(student_list_file_name == ""){
				$( "#dialog-message" ).dialog( "open" );
				return;
			}
			$("#upload_student_list").val("Importing..");
			$("#import_student_list_indicator").css("visibility","visible");
			var formData = new FormData($('#upload_form'));
			formData.append("teacher_id","1"); //need to modify this data later
			formData.append("student_list_file", $('input[type=file]')[0].files[0], student_list_file_name);
			$.ajax({
				url:'ImportStudentList',
				type:'POST',
				data:formData,
				dataType: "json",
				async:true,
				processData: false,  // tell jQuery not to process the data
	            contentType: false, 
				success:function(data){
					if (data.result=="true"){
						window.location.replace("http://hmd12-2.cs.wpi.edu:8080/direct/UploadStudentList?from=file&student_list_file_name="+student_list_file_name);
						
					}else{
						alert("something wrong")
					}
				},
				error:function(data){
					alert("error occurred during import");
				}
			});
			
		});
	$( ".dlg" ).dialog({
			autoOpen: false,
	     	modal: true,
	     	width:500,
	     	height:230,
	     	show:{
	     		effect:"scale",
	     		duration:300
	     	},
	      	buttons: {
	        OK: function() {
	          $( this ).dialog( "close" );
	        }
	      }
	    });
	$( document ).tooltip({
	      position: {
	        my: "center bottom-20",
	        at: "center top",
	        using: function( position, feedback ) {
	          $( this ).css( position );
	          $( "<div>" )
	            .addClass( "arrow" )
	            .addClass( feedback.vertical )
	            .addClass( feedback.horizontal )
	            .appendTo( this );
	        }
	      }
	    });
	
	$("#import_list_btn").click(function(){
		var worksheetId = $("#gworksheets").val();
		window.location.replace("http://hmd12-2.cs.wpi.edu:8080/direct/UploadStudentList?from=Google&worksheetId="+worksheetId);
	});
});
</script>
</head>
<body>
<div id="page-wrap">
	<div id="header" style="height: 60px;">	
		<div class="home-menu pure-menu pure-menu-horizontal pure-menu-fixed" >
			<img alt="ASSISTments" src="${pageContext.request.contextPath}/images/direct_logo.gif" height="50px;" width="250px;" 
				style="position: absolute; top: 10px; left: 10%;">
			<ul class="pure-menu-list" style="position: relative; left: -10%;">
        		<li class="pure-menu-item pure-menu-selected">
        			<a href="${pageContext.request.contextPath}/teacher" class="pure-menu-link">Home</a>
        		</li>
        		<c:choose>
        			<c:when test = "${ sessionScope.from eq 'form'}">
        			<li class="pure-menu-item pure-menu-has-children pure-menu-allow-hover">
            			<a href="javascript:void(0);" id="menuLink1" class="pure-menu-link">Hello, ${sessionScope.email}</a>
            			<ul class="pure-menu-children">
                		<li class="pure-menu-item"><a  href="${pageContext.request.contextPath}/ResetPassword" 
                			class="pure-menu-link" id="reset_password" >Reset Password</a></li>
                		<li class="pure-menu-item"><a  href="${pageContext.request.contextPath}/Logout" 
                			class="pure-menu-link" id="logout">Logout</a></li>
            			</ul>
        			</li>
        			</c:when>
        			<c:when test = "${ sessionScope.from eq 'google'}">
        			<li class="pure-menu-item">
            			<a  href="${pageContext.request.contextPath}/Logout" 
                			class="pure-menu-link" id="logout">Logout</a>
        			</li>
        			</c:when>
        			<c:when test = "${sessionScope.from eq 'facebook'}">
        			<li class="pure-menu-item">
            			<a  href="${pageContext.request.contextPath}/Logout" 
                			class="pure-menu-link" id="logout">Logout</a>
        			</li>
        			</c:when>
        		</c:choose>
    		</ul>
		</div>
    		
	</div>
	<div>
		<div id="disclaimer" style="width:90%; height:120px; margin:30px auto 30px auto; background-color:#ccebf3;">
			<div style="margin:auto;">
				<div style="width:5%; float:left;"><p><img src="images/exclaimation.PNG"/></p></div>
				<div style="width:90%; text-align:left; float:left;">
					<h4><strong>Important Disclaimer</strong></h4>
					<P>We'll <strong>automatically clean</strong> the duplicate instances of students from the list.</P>
				</div>
			</div>
		</div>
		
		<div style="width:90%; margin:auto; text-align:left;">
			<div>
			<h3><i>Import from CSV file</i></h3>
			<p>
			Upload a file
			</p>
			<form id="upload_form" enctype="multipart/form-data">
				<p><input id="student_list_file" type="file" name="student_list_file"></p>
				<input id="upload_student_list" type="button" class="pure-button button-secondary" value="Import">
				<img alt="" src="images/question_mark.png" style="position:relative;top:3px;" id="import_tooltip" height="15" width="15" title="Please upload the CSV file contains students' names">
				<img src="images/indicator.gif"
					style="visibility:hidden;"id="import_student_list_indicator" height="25" width="25" alt="" >
			</form>
			</div>
			
		</div>
	</div>

</div>
<div id="dialog-message" class="dlg" title="Upload File Error">
  <p>
    <span class="ui-icon ui-icon-closethick" style="float:left; margin:0 7px 50px 0;"></span>
    Please choose the file you want to upload
  </p>
</div>
</body>
</html>
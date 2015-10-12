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
</style>
<script>
$(function() {
	$("#gspreadsheets").change(function(){
		var spreadsheetId = $("#gspreadsheets").val();
		$.ajax({
			url:"LoadWorkSheets",
			type:"POST",
			data:{spreadsheetId:spreadsheetId},
			dataType:"json",
			async:true,
			success:function(data){
				var res = "";
				for (var i=0;i<data.length;i++){
					
					res += "<option value='"+data[i].id+"'>"+data[i].name+"</option>";
				}
				$("#gworksheets").html(res);
			},
			error:function(data){
				alert("error");
			}
		});
	});
	$("#gspreadsheets").change();
	$("#authorization_indicator").css("visibility", "hidden");
	
	$("#import_list_btn").click(function(){
		var worksheetId = $("#gworksheets").val();
		
		window.location.replace("http://hmd12-2.cs.wpi.edu:8080/direct/UploadStudentList?from=Google&worksheetId="+worksheetId);
	});
	
	$("#authorization_btn").click(function(){
		$("#authorization_indicator").css("visibility", "visible");
	});
	
	//dialog set up
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
			<h3><i>Import from Google Drive</i></h3>
			<p>
			Import lists you've created in <a href="http://google.com/drive" target="_blank">Google Drive </a>
			spreadsheets.
			</p>
			</div>
			<c:choose>
				<c:when test="${not empty sessionScope.spreadsheets }">
					<div class="form-block">
						<p><label for="gspreadsheets">spreadsheets in your Google Drive account</label></p>
						<p>
						<select id = "gspreadsheets" class="google-selector">
							<c:forEach items="${sessionScope.spreadsheets}" var = "spreadsheetEntry" varStatus="loop">
								<option value="${spreadsheetEntry.id}">${spreadsheetEntry.title.plainText }</option>
							</c:forEach>
						</select>
						</p>
					</div>
					<div class="form-block">
						<p><label for="gworksheets">choose a worksheet</label></p>
						<p>
						<select id ="gworksheets" class="google-selector">
							
						</select>
						</p>
					</div>
					<div class="form-block">
						<button id="import_list_btn" class="pure-button button-secondary" >Import List</button>
						<a href="${pageContext.request.contextPath}/teacher" style="margin-left:10px;">Cancel</a>
					</div>
				</c:when>
				<c:otherwise>
					<div>
						<p>
						<strong>Before we can import</strong>, we need to connect to your Google account. 
						<em>We'll bring you right back here afterwards to start your import.</em>
						</p>
						<a id="authorization_btn" class="pure-button button-secondary" href="${pageContext.request.contextPath}/SpreadSheetServelet">Authorize Connection</a>
						<img id="authorization_indicator" src="images/indicator.gif" style="visibility:hidden;" height="25" width="25" alt="" />
					</div>
				</c:otherwise>
			</c:choose>
		</div>
	</div>

</div>
											<!-- dialog reserved for later usage -->
<div id="format-err-message" class="dlg" title="File Format Error">
  <p>
    <span class="ui-icon ui-icon-closethick" style="float:left; margin:0 7px 50px 0;"></span>
    The Spreadsheet you selected is not in an appropriate format.
  </p>
</div>
											<!-- dialog end -->
</body>
</html>
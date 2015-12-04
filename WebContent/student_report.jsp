<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
<meta charset="UTF-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width, initial-scale=1">
<!-- The above 3 meta tags *must* come first in the head; any other head content must come *after* these tags -->
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>Student Report</title>
<script src="https://ajax.googleapis.com/ajax/libs/jquery/2.1.4/jquery.min.js"></script>
<!-- Latest compiled and minified CSS -->
<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/css/bootstrap.min.css">
<!-- Optional theme -->
<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/css/bootstrap-theme.min.css">
<!-- Latest compiled and minified JavaScript -->
<script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/js/bootstrap.min.js"></script>
<link rel="stylesheet" href="/direct/stylesheets/styles.css">
<!-- Latest compiled and minified CSS -->
<link rel="stylesheet" href="//cdnjs.cloudflare.com/ajax/libs/bootstrap-table/1.9.1/bootstrap-table.min.css">

<!-- Latest compiled and minified JavaScript -->
<script src="//cdnjs.cloudflare.com/ajax/libs/bootstrap-table/1.9.1/bootstrap-table.min.js"></script>
<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/font-awesome/4.4.0/css/font-awesome.min.css">
<style type="text/css">
	.bar {
			position: absolute;
			top: 0;
			left: 0;
			width: 100%;
			height: 40px;
			color: white;
		}
		.banner {
			/* Permalink - use to edit and share this gradient: http://colorzilla.com/gradient-editor/#4f85bb+0,4f85bb+100;Blue+3D+%239 */
			background: #4f85bb; /* Old browsers */
			background: -moz-linear-gradient(top,  #4f85bb 0%, #4f85bb 100%); /* FF3.6+ */
			background: -webkit-gradient(linear, left top, left bottom, color-stop(0%,#4f85bb), color-stop(100%,#4f85bb)); /* Chrome,Safari4+ */
			background: -webkit-linear-gradient(top,  #4f85bb 0%,#4f85bb 100%); /* Chrome10+,Safari5.1+ */
			background: -o-linear-gradient(top,  #4f85bb 0%,#4f85bb 100%); /* Opera 11.10+ */
			background: -ms-linear-gradient(top,  #4f85bb 0%,#4f85bb 100%); /* IE10+ */
			background: linear-gradient(to bottom,  #4f85bb 0%,#4f85bb 100%); /* W3C */
			filter: progid:DXImageTransform.Microsoft.gradient( startColorstr='#4f85bb', endColorstr='#4f85bb',GradientType=0 ); /* IE6-9 */
    		height: 68px;
		}
</style>
</head>
<body>
<div class="bar banner">
</div>
 <div class="container" style="text-align: left; color: black; padding: 30px 30px 30px 30px; background: white; margin-top: 68px;">
 <div class="alert alert-success" role="alert">
 	<h5>${sessionScope.notice_to_students }</h5>
 </div>
 <h3 style="color: #1363AC;">Assignment Report</h3>
 <h4>${sessionScope.problem_set_name }</h4>
 <table class="table table-striped table-hover" data-toggle="table">
 	
 	<thead>
 		<tr>
 			<th class="col-md-1" data-halign="center" data-align="center" data-valign="middle">Problem</th>
 			<th class="col-md-8" data-halign="center" data-align="center" data-valign="middle">My Answer</th>
 			<th class="col-md-2" data-halign="center" data-align="center" data-valign="middle">My Classmates' Average</th>
 			<th class="col-md-1" data-halign="center" data-align="center" data-valign="middle">Hint Usage</th>
 		</tr>
 	</thead>
 	<tbody>
 		<c:forEach items="${sessionScope.report_entries }" var="entry" varStatus="loop">
 			<tr>
 				<td>${loop.index + 1}</td>
 				<td>
 					<div style="display: inline-block; text-align: left; margin: 30px 0 0 0;">
 						${entry.myAnswer}
 					</div>
					<br>
 					<c:choose>
 						<c:when test="${entry.correct == 1.0 }">
 							<div><i class="fa fa-check" style="color: green;"></i>	</div>
 						</c:when>
 						<c:otherwise>
 							<div><i class="fa fa-times" style="color: red;"></i>	</div>
 						</c:otherwise>
 					</c:choose>
					<div class="label label-info">${entry.correctPercent }</div>
 					<div style="display: inline-block; text-align: left;">
 						<i>${entry.teacherComment}</i>
 					</div>
 				</td>
 				<td>${entry.classAverage}</td>
 				<td>${ entry.hintUsage}</td>
 			</tr>
 		</c:forEach>
 	</tbody>
 </table>
 	<div style="height: 65px; margin-top: 15px;">
		<span style="float:left; font-size: smaller;">Powered by </span><br>
		<img alt="ASSISTments Direct" src="/direct/images/direct_logo.gif" height="35px" width="175px" style="float:left;">
	</div>
</div>
</body>
</html>
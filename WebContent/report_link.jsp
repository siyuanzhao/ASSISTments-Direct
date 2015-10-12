<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
	<meta charset="utf-8">
	<meta http-equiv="X-UA-Compatible" content="IE=edge">
	<meta name="viewport" content="width=device-width, initial-scale=1">
    <!-- The above 3 meta tags *must* come first in the head; any other head content must come *after* these tags -->
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
	<title>Report > ${sessionScope.problem_set_name }</title>
	<script src="https://ajax.googleapis.com/ajax/libs/jquery/2.1.4/jquery.min.js"></script>
	<!-- Latest compiled and minified CSS -->
	<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/css/bootstrap.min.css">
	<!-- Optional theme -->
	<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/css/bootstrap-theme.min.css">
	<!-- Latest compiled and minified JavaScript -->
	<script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/js/bootstrap.min.js"></script>
	<!-- Latest compiled and minified CSS -->
	<link rel="stylesheet" href="//cdnjs.cloudflare.com/ajax/libs/bootstrap-table/1.8.1/bootstrap-table.min.css">
	<!-- Latest compiled and minified JavaScript -->
	<script src="//cdnjs.cloudflare.com/ajax/libs/bootstrap-table/1.8.1/bootstrap-table.min.js"></script>
	<link rel="stylesheet" href="/direct/stylesheets/styles.css">
	<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/font-awesome/4.4.0/css/font-awesome.min.css">
	<script type="text/javascript">
		$(function() {
			var assignmentId = $("#assignment_id").val();
			$("#not_started_apply").click(function(e) {
				$(this).toggleClass('active');
				var studentIds = [];
				var size = $("#not_started_table").bootstrapTable('getSelections').length;
				if(size == 0) {
					$(this).toggleClass('active');
					return;
				}
				var not_started_num = parseInt($("#not_started_num").html(), 10);
				var complete_num = parseInt($("#complete_num").html(), 10);
				$.each($("#not_started_table").bootstrapTable('getSelections'), function(index, value) {
					studentIds.push(value.student_id);
					//row += "student_name: \'" + value.student_name + "\', student_id: \'" + value.student_id + "\' ";
				});
				console.log(studentIds);
				$.ajax({
					url: '../s/complete_assignment_progress',
					type: 'POST',
					data: {assignment_id: assignmentId, student_ids: studentIds},
					success: function(data) {
						$("#not_started_apply").toggleClass('active');
						//add rows to new table
						$.each($("#not_started_table").bootstrapTable('getSelections'), function(index, value) {
							var student_name = $.trim(value.student_name);
							console.log(student_name);
							$("#complete_table").bootstrapTable("insertRow", {index: 1, row: {student_name: student_name, student_id: value.student_id } });
						});
						//delete rows from table
						$("#not_started_table").bootstrapTable('remove', {field: 'student_id', values: studentIds});
						//update the number
						$("#not_started_num").html(not_started_num-size);
						$("#complete_num").html(complete_num+size);
						console.log("It's working");
					}
				});
			});
			
			$("#in_progress_complete").click(function(e) {
				$(this).toggleClass('active');
				var studentIds = [];
				$.each($("#in_progress_table").bootstrapTable('getSelections'), function(index, value) {
					studentIds.push(value.student_id);
				});
				var size = $("#in_progress_table").bootstrapTable('getSelections').length;
				var not_started_num = parseInt($("#not_started_num").html(), 10);
				var complete_num = parseInt($("#complete_num").html(), 10);
				var in_progress_num = parseInt($("#in_progress_num").html(), 10)
				
				$.ajax({
					url: '../s/complete_assignment_progress',
					type: 'POST',
					data: {assignment_id: assignmentId, student_ids: studentIds},
					success: function(data) {
						//add rows to new table
						$.each($("#in_progress_table").bootstrapTable('getSelections'), function(index, value) {
							var student_name = $.trim(value.student_name);
							console.log(student_name);
							$("#complete_table").bootstrapTable("insertRow", {index: 0, row: {student_name: student_name, student_id: value.student_id } });
						});
						//delete rows from table
						$("#in_progress_table").bootstrapTable('remove', {field: 'student_id', values: studentIds});
						//update the number
						$("#in_progress_num").html(not_started_num-size);
						$("#complete_num").html(complete_num+size);
						
						$("#in_progress_complete").toggleClass('active');						
						console.log("It's working");
					}
				});
			});
			
			$("#in_progress_delete").click(function(e) {
				$(this).toggleClass('active');
				var studentIds = [];
				$.each($("#in_progress_table").bootstrapTable('getSelections'), function(index, value) {
					studentIds.push(value.student_id);
				});
				var size = $("#in_progress_table").bootstrapTable('getSelections').length;
				var not_started_num = parseInt($("#not_started_num").html(), 10);
				var complete_num = parseInt($("#complete_num").html(), 10);
				var in_progress_num = parseInt($("#in_progress_num").html(), 10)
				console.log(studentIds);
				$.ajax({
					url: '../s/delete_assignment_progress',
					type: 'POST',
					data: {assignment_id: assignmentId, student_ids: studentIds},
					success: function(data) {
						//add rows to new table
						$.each($("#in_progress_table").bootstrapTable('getSelections'), function(index, value) {
							var student_name = $.trim(value.student_name);
							console.log(student_name);
							$("#not_started_table").bootstrapTable("insertRow", {index: 0, row: {student_name: student_name, student_id: value.student_id } });
						});
						//delete rows from table
						$("#in_progress_table").bootstrapTable('remove', {field: 'student_id', values: studentIds});
						//update the number
						$("#in_progress_num").html(not_started_num-size);
						$("#not_started_num").html(complete_num+size);
						
						$("#in_progress_delete").toggleClass('active');
						console.log("It's working");
					}
				});
			});
			
			$("#complete_delete_progress").click(function(e) {
				$(this).toggleClass('active');
				var studentIds = [];
				var size = parseInt($("#complete_table").bootstrapTable('getSelections').length, 10);
				var not_started_num = parseInt($("#not_started_num").html(), 10);
				var complete_num = parseInt($("#complete_num").html(), 10);
				var in_progress_num = parseInt($("#in_progress_num").html(), 10)
				$.each($("#complete_table").bootstrapTable('getSelections'), function(index, value) {
					studentIds.push(value.student_id);
				});
				console.log(studentIds);
				$.ajax({
					url: '../s/delete_assignment_progress',
					type: 'POST',
					data: {assignment_id: assignmentId, student_ids: studentIds},
					success: function(data) {
						$("#complete_delete_progress").toggleClass('active');
						//add rows to new table
						$.each($("#complete_table").bootstrapTable('getSelections'), function(index, value) {
							var student_name = $.trim(value.student_name);
							console.log(student_name);
							$("#not_started_table").bootstrapTable("insertRow", {index: 0, row: {student_name: student_name, student_id: value.student_id } });
						});
						//delete rows from table
						$("#complete_table").bootstrapTable('remove', {field: 'student_id', values: studentIds});
						//update the number
						$("#not_started_num").html(not_started_num+size);
						$("#complete_num").html(complete_num-size);
						console.log("It's working");
					}
				});
			});
		});
	</script>
</head>
<body>
	<!-- 
	<div class="banner">
		<div class="container">
			<img alt="ASSISTments" src="${initParam.path}images/direct_logo.gif" height="50px;" width="250px;" 
				style="position: relative; top: 5px; float: left;">
		</div>
	</div>
	 -->
	<div class="container" style="text-align: left; background: white;">
		<div class="panel panel-info" style="margin: 40px 0 50px 0;">
			<div class="panel-heading">
				<span style="font-size: 135%;">${sessionScope.problem_set_name }</span>
			</div>
			<div class="panel-body">
				<input type="hidden" value="${sessionScope.assignment_id }" id="assignment_id">
				<div style="margin: 20px 0 0 0;"></div>
				<a href="${sessionScope.report_link }" target="_blank" class="btn btn-primary">Report</a>
				<a href="${sessionScope.view_problems_link }" target="_blank" class="btn btn-primary">View Problems</a>
				<hr>
				<div>
					Class progress: 
					<ul class="nav nav-pills" role="tablist">
						<li role="presentation">
							<a data-toggle="collapse" 
								href="#not_started" aria-expanded="false" aria-controls="not_started">Not started
								<span class="badge" id="not_started_num">${sessionScope.not_started_students.size()}</span>
							</a>
						</li>
						<li role="presentation">
							<a data-toggle="collapse" 
								href="#in_progress" aria-expanded="false" aria-controls="in_progress">In progress
								<span class="badge" id="in_progress_num">${sessionScope.in_progress_students.size()}</span>
							</a>
						</li>
						<li role="presentation">
							<a data-toggle="collapse" 
								href="#complete" aria-expanded="false" aria-controls="complete">Complete
								<span class="badge" id="complete_num">${sessionScope.complete_students.size()}</span>	
							</a>
						</li>
					</ul>		
				</div>

				<div class="collapse" id="not_started">
  					<div class="well">
  						<table data-toggle="table" data-click-to-select="true" id="not_started_table" 
  							data-sort-name="student_name" data-sort-order="asc">
  							<thead>
  							<tr>
  								<th data-checkbox="true" >Complete/Excuse</th>
  								<th data-field="student_name">Not Started Student</th>
  								<th data-field='student_id' data-visible='false'></th>
  							</tr>
  							</thead>
  							<tbody>	
    						<c:forEach items="${sessionScope.not_started_students}" var="student" varStatus="loop">
    							<tr>
    								<td>
    									<label>
    										<input type="checkbox" style="margin: 5px 0 0 20px;" 
    											class="not_started_check" value="${student.id }" >
    									</label>
    								</td>
    								<td>
    									<c:out value="${student.firstName }"></c:out> <c:out value="${student.lastName }"></c:out>
    								</td>
    								<td>${student.id }</td>
    							</tr> 
    						</c:forEach>
    						</tbody>
    					</table>
    					<div style="margin: 30px 0 0 0;">
    						<button class="btn btn-primary has-spinner" id="not_started_apply">
    							<span class="spinner"><i class="fa fa-spin fa-refresh"></i></span>
    							Complete/Excuse
    						</button>
    					</div>
  					</div>
				</div>
				<div class="collapse" id="in_progress">
  					<div class="well">
  						<table data-toggle="table" data-click-to-select="true" id="in_progress_table"
  							data-sort-name="student_name" data-sort-order="desc" >
  							<thead>
  							<tr>
  								<th data-checkbox="true" ></th>
  								<th data-field="student_name">In Progress Student</th>
  								<th data-field='student_id' data-visible='false'></th>
  							</tr>
  							</thead>
  							<tbody>	
    						<c:forEach items="${sessionScope.in_progress_students}" var="student" varStatus="loop">
    							<tr>
    								<td>
    									<label>
    										<input type="checkbox" style="margin: 5px 0 0 20px;" 
    											class="in_progress_check" value="${student.id }" >
    									</label>
    								</td>
    								<td>
    									<c:out value="${student.firstName }"></c:out> <c:out value="${student.lastName }"></c:out>
    								</td>
    								<td>${student.id }</td>
    							</tr> 
    						</c:forEach>
    						</tbody>
    					</table>
  						<div style="margin: 30px 0 0 0;">
							<button class="btn btn-primary" id="in_progress_complete">Complete/Excuse</button>
							<button class="btn btn-primary" id="in_progress_delete">Delete Progress</button>
						</div>
					</div>
				</div>
				<div class="collapse" id="complete">
  					<div class="well">
  						<table data-toggle="table" data-click-to-select="true" id="complete_table"
  							data-sort-name="student_name" data-sort-order="asc">
  							<thead>
  							<tr>
  								<th data-checkbox="true" data-title='Delete Progress'></th>
  								<th data-field="student_name">Complete Student</th>
  								<th data-field='student_id' data-visible='false'></th>
  							</tr>
  							</thead>
  							<tbody>	
    						<c:forEach items="${sessionScope.complete_students}" var="student" varStatus="loop">
    							<tr>
    								<td>
    									<label>
    										<input type="checkbox" style="margin: 5px 0 0 20px;" 
    											class="complete_check" value="${student.id }" >
    									</label>
    								</td>
    								<td>
    									<c:out value="${student.firstName }"></c:out> <c:out value="${student.lastName }"></c:out>
    								</td>
    								<td>${student.id }</td>
    							</tr> 
    						</c:forEach>
    						</tbody>
    					</table>
    					<div style="margin: 30px 0 0 0;">
							<button class="btn btn-primary has-spinner" id="complete_delete_progress">
								<span class="spinner"><i class="fa fa-spin fa-refresh"></i></span>
								Delete Progress
							</button>
						</div>
					</div>
				</div>
			</div>
		</div>
		<div style="height: 65px;">
			<span style="float:left; font-size: smaller;">Powered by </span><br>
			<img alt="ASSISTments Direct" src="/direct/images/direct_logo.gif" height="35px" width="175px" style="float:left;">
		</div>
	</div>
</body>
</html>
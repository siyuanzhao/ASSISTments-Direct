<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<meta name="google-site-verification" content="KmYDoW-s0aTEaWh0G2cBpbNkCVhs0JDUlwgZpHyat38" />
<title>ASSISTments Direct</title>
<link rel="stylesheet" 	href="pure-release-0.6.0/pure-min.css">
<link rel="stylesheet" 	href="stylesheets/styles.css">
<link rel="stylesheet" 	href="stylesheets/teacher.css">
<link rel="stylesheet" 	href="stylesheets/jquery-ui.css">
<script type="text/javascript" 	src="js/jquery.min.js"></script>
<script type="text/javascript" 	src="js/jquery-ui.min.js"></script>
<script src="https://apis.google.com/js/platform.js" async defer></script>
<style type="text/css">
	#accordion > h3 {
		margin-top: 10px;
  	}
  	.ui-state-active {
  		font-weight: bold !important;
  		background: #b4d8e7 !important;
  		color: #000000 !important;
  	}
	tbody.share_link tr td, tbody.direct_links tr td {
		border-top: 3px solid;
		border-bottom: 3px solid !important;
	}
	.datagrid table tbody:last-child tr td{ 
		border-bottom: none !important; 
	}
	.home-menu .pure-menu-selected a {
		color: white !important;
	}
	.button-xsmall {
    	font-size: 70% !important;
	}
	.button-small {
    	font-size: 85% !important;
	}
	.custom-restricted-width {
    	/* To limit the menu width to the content of the menu: */
    	display: inline-block;
    	/* Or set the width explicitly: */
    	/* width: 10em; */
	}
</style>
<script>
  	$(function() {
  		var headers = $('#accordion .accordion-header');
  		$("#language_div").hide();
  		$(".direct_links").hide();
  		$(".share_link").hide();
		$.each(headers, function(index, value) {
			var panel = $(this).next();
  		    var isOpen = panel.is(':visible');
  		    if(isOpen) {
  		    	$(this).addClass("ui-state-active");
  		    	$(this).removeClass("ui-state-default");
  		    } else {
  		    	$(this).removeClass("ui-state-active");
  		    	$(this).addClass("ui-state-default");
  		    }
		});
  		// add the accordion functionality
  		headers.click(function() {
  		    var panel = $(this).next();
  		    var isOpen = panel.is(':visible');
  		    if(!isOpen) {
  		    	$(this).addClass("ui-state-active");
  		    	$(this).removeClass("ui-state-default");
  		    } else {
  		    	$(this).removeClass("ui-state-active");
  		    	$(this).addClass("ui-state-default");
  		    }
  		    // open or close as necessary
  		    panel[isOpen? 'slideUp': 'slideDown']()
  		        // trigger the correct custom event
  		        .trigger(isOpen? 'hide': 'show');

  		    // stop the link from causing a pagescroll
  		    return false;
  		});
  		//show direct links
  		$(".direct_links_viewer").click(function() {
  			//$(".direct_links").hide("slow");
  			$(this).parent().parent().parent().next().next().toggle("slow");
  		});
  		$(".share_link_viewer").click(function() {
  			//$(".direct_links").hide("slow");
  			$(this).parent().parent().parent().next().toggle("slow");
  		});
  		$("#section_name_dialog").dialog({
  			autoOpen: false,
  			height: 300,
  			width: 500,
  			modal: true,
  			buttons: {
  				"Create Section": function() {
  					var sectionName = $("#new_section_name").val();
  					//first should check if the section name already exists
  					$.ajax({
  						url: 'create_new_section',
  						type: 'POST',
  						data: {section_name: sectionName},
  						dataType: "json",
  						async: true,
  						success: function(data) {  							
  						},
  						error: function(data) {
  							
  						}
  					});
  				},
  				Cancel: function() {
  					$("#section_name_dialog").dialog("close");
  				}  		
  			}
  		});
  		$("#new_section_button").click(function() {
  			$("#section_name_dialog").dialog("open");
  		});
  		
  		$("#language").change(function() {
			var value = $("#language").val();
			$.ajax({
				url: 'change_language',
				type: 'POST',
				data: {value: value},
				async: false,
				success: function(data) {
					location.reload();
				},
				error: function(data) {
				}
			});
		});
  	});
</script>
</head>
<body>
<fmt:setLocale value="${sessionScope.locale }"/>
	<fmt:bundle basename="org.assistments.direct.Bundle">
		<fmt:message key="email" var="email_label"></fmt:message>
		<fmt:message key="password" var="pwd_label"></fmt:message>
		<fmt:message key="loginButton" var="login_button"></fmt:message>
		<fmt:message key="teacher.home" var="home"></fmt:message>
		<fmt:message key="teacher.account" var="account"></fmt:message>
		<fmt:message key="reset_password" var="reset_password"></fmt:message>
		<fmt:message key="logout" var="logout"></fmt:message>
		<fmt:message key="teacher.label1" var="label1"></fmt:message>
		<fmt:message key="teacher.assignments" var="assignments"></fmt:message>
		<fmt:message key="teacher.problem_set_name" var="problem_set_name"></fmt:message>
		<fmt:message key="teacher.view" var="view"></fmt:message>
		<fmt:message key="teacher_report_link" var="teacher_report_link"></fmt:message>
		<fmt:message key="student_assignment_link" var="student_assignment_link"></fmt:message>
		<fmt:message key="teacher.my_students" var="my_students"></fmt:message>
		<fmt:message key="teacher.share_link" var="share_link"></fmt:message>
		<fmt:message key="teacher.direct_links" var="direct_links"></fmt:message>
	</fmt:bundle>

	<div id="page-wrap">
	<div id="header" style="height: 60px;">	
		<div class="home-menu pure-menu pure-menu-horizontal pure-menu-fixed" >
			<img alt="ASSISTments" src="${pageContext.request.contextPath}/images/direct_logo.gif" height="50px;" width="250px;" 
				style="position: absolute; top: 10px; left: 10%;">
			<ul class="pure-menu-list" style="position: relative; left: -10%;">
        		<li class="pure-menu-item pure-menu-selected">
        			<a href="${pageContext.request.contextPath}/teacher" class="pure-menu-link">${home }</a>
        		</li>
        		<c:choose>
        			<c:when test = "${ sessionScope.loginInfo.from.val eq 'form'}">
        			<li class="pure-menu-item pure-menu-has-children pure-menu-allow-hover">
            			<a href="javascript:void(0);" id="menuLink1" class="pure-menu-link">${account }</a>
            			<ul class="pure-menu-children">
                		<li class="pure-menu-item"><a  href="${pageContext.request.contextPath}/ResetPassword" 
                			class="pure-menu-link" id="reset_password" >${reset_password }</a></li>
                		<li class="pure-menu-item"><a  href="${pageContext.request.contextPath}/Logout" 
                			class="pure-menu-link" id="logout">${logout }</a></li>
            			</ul>
        			</li>
        			</c:when>
        			<c:when test = "${ sessionScope.loginInfo.from.val eq 'google'}">
        			<li class="pure-menu-item">
            			<a  href="${pageContext.request.contextPath}/Logout" 
                			class="pure-menu-link" id="logout">${logout }</a>
        			</li>
        			</c:when>
        			<c:when test = "${sessionScope.loginInfo.from.val eq 'facebook'}">
        			<li class="pure-menu-item">
            			<a  href="${pageContext.request.contextPath}/Logout" 
                			class="pure-menu-link" id="logout">${logout }</a>
        			</li>
        			</c:when>
        		</c:choose>
    		</ul>
		</div>
    		
	</div>
	<div style="float: right; margin: 20px 10px 0 0;" id="language_div">
			<select id="language">
				<option value="en_US" <c:if test="${sessionScope.locale eq 'en_US' }">selected</c:if> >English</option>
				<option value="zh_CN" <c:if test="${sessionScope.locale eq 'zh_CN' }">selected</c:if> >简体中文</option>
			</select>
		</div>
	<div style="clear: both;"></div>
	<div style="width: 90%; margin: 100px auto 0 auto; text-align: left; min-width: 550px;">

		<div id="accordion" class="ui-accordion ui-widget ui-helper-reset">
			<c:if test="${not empty sessionScope.student_link}">
			<h3 class="accordion-header ui-accordion-header ui-helper-reset ui-state-default ui-accordion-icons ui-corner-all">
				<span class="ui-accordion-header-icon ui-icon ui-icon-triangle-1-e"></span>
				${label1 }
			</h3>
				<div class="ui-accordion-content ui-helper-reset ui-widget-content ui-corner-bottom">
					<br><br>
					<div style="text-align: center; width: 75%;">
						The assignment <b style="font-size: 120%;">${sessionScope.problem_set_name }</b> has been created.
						<br><br>
						<b>Student Assignment Link</b><br>
						Give this link to your students. They will enter their name and do the assignment.<br>
   						<a href="${sessionScope.student_link }" target="_blank">${sessionScope.student_link }</a>
 						<br><br>
 						<b>Teacher Report Link</b><br>
 						Use this link to see a report on how they did on the assignment: <br>
 						<a href="${sessionScope.teacher_link }" target="_blank">${sessionScope.teacher_link }</a>
 						<br><br>
 					</div>
				</div>
			</c:if>
				
			<h3 class="accordion-header ui-accordion-header ui-helper-reset ui-state-default ui-accordion-icons ui-corner-all">
				<span class="ui-accordion-header-icon ui-icon ui-icon-triangle-1-e"></span>
				${assignments }
			</h3>
				<div id="assignments" class="ui-accordion-content ui-helper-reset ui-widget-content ui-corner-bottom">
					<div class="datagrid">
					<table width="100%">
						<thead>
						<tr>
							<th>${problem_set_name }</th>
							<th>${share_link }</th>
							<th>${direct_links }</th>
						</tr>
						</thead>
					<c:forEach items="${sessionScope.assignments }" var="assignment" varStatus="loop">
					<tbody>
						<tr ${loop.index % 2 eq 0 ? "class='alt'" : "" }>
							<td><c:out value="${assignment.problem_set_name }"></c:out></td>
							<td><a href="javascript:void(0);" class="share_link_viewer">${view }</a></td>
							<td><a href="javascript:void(0);"  class="direct_links_viewer">${view }</a></td>
						</tr>
					</tbody>
					<tbody class="share_link">
						<tr height="100px;">
							<td colspan="3">
							<span style="height: 20px;">${share_link }: 
								<a href="${assignment.share_link }" target="_blank">${assignment.share_link } </a></span><br><br>
							</td>
						</tr>
					</tbody>
					<tbody class="direct_links" >
						<tr height="150px;">
							<td colspan="3">
							<span style="height: 20px;">${teacher_report_link }: 
								<a href="${assignment.teacher_link }" target="_blank">${assignment.teacher_link } </a></span><br><br>
							<span style="height: 20px;">${student_assignment_link}: 
								<a href="${assignment.student_link }" target="_blank">${assignment.student_link }</a></span>
								<!-- 
								<g:sharetoclassroom url="${assignment.classroom_link }" size="25"  title="${assignment.problem_set_name }" 
								body="You will go to ASSISTments to do the assignment."></g:sharetoclassroom>
								 -->
							</td>
						</tr>
					</tbody>
					</c:forEach>
					</table>
					</div>
				</div>
				<!-- end of assignment list -->
		<!-- 
			<h3 class="accordion-header ui-accordion-header ui-helper-reset ui-state-default ui-accordion-icons ui-corner-all">
				<span class="ui-accordion-header-icon ui-icon ui-icon-triangle-1-e"></span>
				${my_students }
			</h3>
			<div id="students" class="ui-accordion-content ui-helper-reset ui-widget-content ui-corner-bottom">
				<button class="pure-button" id="new_section_button">Add new section</button>
				<div style="margin: 30px 0 0 0;"></div>
				<c:forEach items="${sessionScope.roster.sections }" var="section" varStatus="loop">
					<h3 class="accordion-header ui-accordion-header ui-helper-reset ui-state-default ui-accordion-icons ui-corner-all">
						<span class="ui-accordion-header-icon ui-icon ui-icon-triangle-1-e"></span>
						<c:out value="${section.sectionName}"></c:out>
					</h3>
					<div id="section" class="ui-accordion-content ui-helper-reset ui-widget-content ui-corner-bottom">
						<div class="pure-menu custom-restricted-width">
							<ul class="pure-menu-list">
							<c:forEach items="${section.students}" var="student" varStatus="loop">
									<li class="pure-menu-item pure-menu-has-children pure-menu-allow-hover">
										<a href="#" id="menuLink1" class="pure-menu-link">
											<c:out value="${student.displayName }"></c:out>
										</a>
										<ul class="pure-menu-children pure-menu-horizontal">
											<li class="pure-menu-item">
												<button class="pure-button button-small" style="margin: 5px 0 0 5px;" id="change_section_button">
													Change section</button>
												<button class="pure-button button-small" style="margin: 5px 0 0 5px;">Drop student</button>
											</li>
										</ul>
									</li>
						</c:forEach>
						</ul>
						</div>
					</div>
				</c:forEach>
			</div>
				-->
		</div>
	</div>
</div>

<div id="section_name_dialog" title="New Section">
	<div class="pure-form pure-form-aligned" style="margin-top: 50px;">
		<fieldset>
			<div class="pure-control-group">
				<label for="new_section_name">Section Name</label> <input type="text"
					name="new_section_name" id="new_section_name" placeholder="Section Name" required>
			</div>
		</fieldset>
	</div>
</div>

<c:remove var="problem_set_name" scope="session"/>
<c:remove var="student_link" scope="session"/>
<c:remove var="teacher_link" scope="session"/>
</body>
</html>
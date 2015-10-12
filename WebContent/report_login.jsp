<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Login</title>
<link rel="stylesheet"  href="${pageContext.request.contextPath}/pure-release-0.6.0/pure-min.css">
<link rel="stylesheet"  href="${pageContext.request.contextPath}/stylesheets/styles.css">
<script type="text/javascript" 	src="${pageContext.request.contextPath}/jquery-2.1.3.js"></script>

<script type="text/javascript">

	$(function() {
		$("#report_login")
				.submit(
						function(event) {
							$("#indicator").css("visibility", "visible");
						});

	});
</script>
</head>
<body>
	<div id="page-wrap">
		<div style="padding:20px 0 0 0; background-color: #E1E6E6;">
		<img alt="ASSISTments" src="${pageContext.request.contextPath}/images/AssistmentsTMBlack.png">
		</div>
		<form action="${pageContext.request.contextPath}/report/${sessionScope.report_ref}" method="post" class="pure-form pure-form-aligned"
			style="margin: 30px 0 0 0;" id="report_login">
			<fieldset>
				<legend>Please type in your email and password to see the report.</legend>
				<br>
				<div style="width: 50%; margin: 0 auto; text-align: left; min-width: 450px;">
				<div class="pure-control-group">
					<label for="email">Email</label> <input type="text"
						name="email" placeholder="Email" value="${requestScope.email }" required>
				</div>
				<div class="pure-control-group">
					<label for="password">Password</label> <input type="password"
						name="password" placeholder="Password" required>
				</div>
				<div class="pure-controls">
					<input type="submit" value="Login"
						class="pure-button pure-button-primary">
					<img 	src="${pageContext.request.contextPath}/images/indicator.gif"
						style="visibility: hidden;" id="indicator" height="25" width="25">
				</div>
				</div>
			</fieldset>
		</form>
		<div style='margin:15px 5px 10px 5px; color:red' id="message">${requestScope.message }</div>
	</div>
</body>
</html>
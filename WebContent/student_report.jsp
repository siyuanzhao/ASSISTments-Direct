<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
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
<link rel="stylesheet" href="stylesheets/styles.css">
<style type="text/css">
	body{
		background-color: white;
	}
	.holder {
		width: 100%;
		height: 100%;
		position: relative;
	}
		
	.frame {
		width: 100%;
		height: 100%;
	}
</style>
<script type="text/javascript">
	$(function() {
		if( window.localStorage ) {
			if( !localStorage.getItem( 'firstLoad' ) ) {
			      localStorage[ 'firstLoad' ] = true;
			      parent.postMessage("Assginment Done", "*");
			      parent.parent.postMessage("Assginment Done", "*");
			      parent.parent.parent.postMessage("Assginment Done", "*");
			}  else {
			      localStorage.removeItem( 'firstLoad' );
			}
		}
	});
</script>
</head>
<body>
	<div class="holder">
		<iframe class="frame" src="${sessionScope.student_report }"></iframe>
	</div>
</body>
</html>
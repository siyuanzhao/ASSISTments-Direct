<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>Share Links</title>
<link rel="stylesheet" href="stylesheets/jquery-ui.css">
<link rel="stylesheet" href="stylesheets/sharelinks.css">
<!-- Bootstrap core CSS -->
<link href="stylesheets/bootstrap.min.css" rel="stylesheet">

<!-- Optional theme -->
<link href="stylesheets/bootstrap-theme.min.css" rel="stylesheet">
<script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.3/jquery.min.js"></script>
<script type="text/javascript" src="js/bootstrap.min.js"></script>
<script type="text/javascript" src="js/jquery-ui.min.js"></script>
<script src="https://ajax.googleapis.com/ajax/libs/angularjs/1.4.3/angular.min.js"></script>
<script src="https://apis.google.com/js/platform.js" async defer></script>
<style type="text/css">
li {
	list-style-type: none;
}
a:hover{
	cursor: pointer;
}
</style>
<script>
	$(function() {
		$('.js-loading-bar').modal({
			  backdrop: 'static',
			  show: false
			});
		$(".nav-sidebar a").click(function(){
			var folderId = $(this).attr("id");
			$(".nav-sidebar li").removeClass("active");
			$(this).parent().addClass("active");
			$(".page-header").html($(this).html());
			
			//var progressbar = "<div class='progress'><div class='progress-bar progress-bar-striped active' role='progressbar' "
			//				+"aria-vavluenow='100' aria-valuemin='0' aria-valuemax='100' style='width:100%;'></div></div>";
			$("#problemSets").html("");
			$("#progressModal").modal("show");
			$("body").css("cursor", "progress");
			getShareLinks(folderId);
		});
		
		$(".nav-sidebar li:first-child a").click();
		(function ($) {
			
	        $('#filter').keyup(function () {
	
	            var rex = new RegExp($(this).val(), 'i');
	            $('.searchable .curriculum_item').hide();
	            $('.searchable .curriculum_item').filter(function () {
	                return rex.test($(this).text());
	            }).show();
	
	        });
	
	    }(jQuery));
	});
	function getShareLinks(folderId){
		$.getJSON(
				"GetSkillBuilderShareLinks?folder_id="
						+ folderId+"&from=share_links",
				function(data) {
					var rs = "";
					rs += "<ul class='searchable'>";
					for (var i = 0; i < data.length; i++) {
						if (data[i].type == "CurriculumItem") {
							rs += "<li class='curriculum_item'><h3><a target='_blank' href='"+data[i].share_link+"'>" + data[i].name
									+ "</a></h3></li>";
						} else {
							rs += "<li class='folder'><h2><span class='glyphicon glyphicon-folder-open' aria-hidden='true' style='font-size:0.8em;'></span>   <a>"
									+ data[i].name + "</a></h2></li>";
							rs += "<div><ul>";
							for (var j = 0; j < data[i].problem_sets.length; j++) {
								rs += "<li class='curriculum_item'><h3><a target='_blank' href='"+data[i].problem_sets[j].share_link+"'>"
										+ data[i].problem_sets[j].name
										+ "</a></h3></li>";
							}
							rs += "</ul></div>";
						}
					}
					rs += "</ul>";
					$("#problemSets").html(rs);
					$("h2 a").click(
							function() {
								if ($(this).prev("span").hasClass(
										"glyphicon-folder-open")) {
									$(this).parent().parent().next(
											"div").slideUp();
									$(this).prev("span").removeClass(
											"glyphicon-folder-open");
									$(this).prev("span").addClass(
											"glyphicon-folder-close");
								} else {
									$(this).parent().parent().next(
											"div").slideDown();
									$(this).prev("span").removeClass(
											"glyphicon-folder-close");
									$(this).prev("span").addClass(
											"glyphicon-folder-open");
								}
							});
					$("body").css("cursor", "initial");
					$("#progressModal").modal("hide");
				});
	}
</script>
</head>
<body>
	<nav class="navbar navbar-inverse navbar-fixed-top">
	<div class="container-fluid">
		<div class="navbar-header">
			<button type="button" class="navbar-toggle collapsed"
				data-toggle="collapse" data-target="#navbar" aria-expanded="false"
				aria-controls="navbar">
				<span class="sr-only">Toggle navigation</span> <span
					class="icon-bar"></span> <span class="icon-bar"></span> <span
					class="icon-bar"></span>
			</button>
			<a class="navbar-brand" href="#">Share Links</a>
		</div>
		<div id="navbar" class="navbar-collapse collapse">
			<ul class="nav navbar-nav navbar-right">
				<li><a href="#">Home</a></li>
				<li><a href="#">Settings</a></li>
				<li><a href="#">Profile</a></li>
				<li><a href="#">Help</a></li>
			</ul>
			<form class="navbar-form navbar-right">
				<input id="filter" type="text" class="form-control" placeholder="Search...">
			</form>
		</div>
	</div>
	</nav>
	<!-- Begin page content -->
	<div class="container-fluid">
		<div class="row">
			<div class="col-md-2 sidebar">
				<ul class="nav nav-sidebar">
				<c:forEach items="${sessionScope.folders}" var="folder">
					<li><a id="${folder.id}">${folder.name }</a></li>
				</c:forEach>
				</ul>
			</div>
			<div class="col-md-10 col-md-offset-2 main">
          		<h1 class="page-header"></h1>
				<div id="problemSets">
				</div>
			</div>
		</div>
	</div>
	
	
<div id="progressModal" class="modal js-loading-bar" >
 <div class="modal-dialog" style="width:90%; height:30px;">
   <div class="modal-content">
     <div class="modal-body">
       <div class='progress' style="margin-top:10px;">
	       <div class='progress-bar progress-bar-striped active' role='progressbar' aria-vavluenow='100' aria-valuemin='0' aria-valuemax='100' style='width:100%;'>
	       </div>
       </div>
     </div>
   </div>
 </div>
</div>
</body>
</html>
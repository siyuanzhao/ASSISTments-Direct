<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>AP Statistics</title>
<link rel="shortcut icon" href="images/shortcut_logo.png">
<link rel="stylesheet" href="stylesheets/sharelinks.css">
<!-- Bootstrap core CSS -->
<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-datepicker/1.5.0/css/bootstrap-datepicker.min.css">
<link href="stylesheets/bootstrap.min.css" rel="stylesheet">
<link href="stylesheets/bootstrap-tour.min.css" rel="stylesheet">
<!-- Optional theme -->
<link href="stylesheets/bootstrap-theme.min.css" rel="stylesheet">
<script src="https://ajax.googleapis.com/ajax/libs/jquery/2.1.4/jquery.min.js"></script>
<script type="text/javascript" src="js/bootstrap.min.js"></script>
<script type="text/javascript" src="js/bootstrap-tour.min.js"></script>
<script type="text/javascript" 
	src="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-datepicker/1.5.0/js/bootstrap-datepicker.min.js"></script>
<style type="text/css">
li {
	list-style-type: none;
}
a:hover{
	cursor: pointer;
}
h3 a:hover{
	color:#005192;
}
.container_customize{

	padding:0 60px 0 30px;
}
.nav_active{
	color:white !important;
}
.tour-step-backdrop>td{
	position:relative !important;
}
</style>
<script>

var ownerId = "";
var problem_set_id = "";
var problem_set_name = "";
var link_to_share = "";
var instruction = "Each assignment is a skill builder. Students work on one skill until they get three right in a row."
				+" Click on the name for a sample of the problems and the green icon to assign to your class.";
var toolSelected = "";
var tour;
	$(function() {
		$('#assignment_info').hide();
		$('.js-loading-bar').modal({
			  backdrop: 'static',
			  show: false
			});
		//set the nav bar active item
		var toolType = $("#tool_type").val();
		if(toolType == "skill_builder"){
			$("#skill_builder_page").addClass("nav_active");
		}else if (toolType == "ap_statistics"){
			$("#ap_page").addClass("nav_active");
		}else if(toolType == "chemistry"){
			$("#chemistry_page").addClass("nav_active");
		}
		
		$("#share_to_classroom_btn").click(function(e) {
//			var course_id = $("#courses_select").val(); 
	//		var course_name = $("#courses_select option:selected").text();
			$(this).addClass("disabled");
			var due_date = $("#datePicker").data('datepicker').getFormattedDate('yyyy-mm-dd');
			var description = $("#description-text").val();
			$.ajax({
				url: 'schoology_classroom/assign',
				type: 'POST',
				data: {problem_set_id: problem_set_id, due_date: due_date, description: description},
				async: false,
				success: function(data) {
					console.log(problem_set_name);
					$("#assignment_info").html("Assignment Created Successfully!");
					$("#assignment_info").show(700);
				},
				error: function(data) {
					$("#assignment_info").html("Assignment Cannot Be Created with No Enrollments In Your Class!");
					$("#assignment_info").show(700);
					console.log(data);
					return;
				}
			});
			//$("#share_to_classroom_btn").removeClass("disabled");
		});
		(function ($) {
			
	        $('#filter').keyup(function () {
	
	            var rex = new RegExp($(this).val(), 'i');
	            $('.searchable .curriculum_item').hide();
	            $('.searchable .curriculum_item').filter(function () {
	                return rex.test($(this).text());
	            }).show();
	
	        });
	
	    }(jQuery));
		$(".navbar_item").click(function(){
			$("#alert_panel").hide();
			var id = $(this).attr("id");
			if(id == "landing_page" || id == "logo"){
				toolSelected  = "landing_page";
				window.location.assign("/direct/SchoologyAppsLandingPage");
			}else if(id == "skill_builder_page"){
				toolSelected = "skill_builder_link";
				window.location.assign("/direct/SkillBuilderSchoologyClassroom?folder_id=22&tool_type=skill_builder");
			}else if (id == "ap_page"){
				toolSelected = "ap_statistics_link";
				window.location.assign("/direct/SkillBuilderSchoologyClassroom?folder_id=210644&tool_type=ap_statistics");
			}else if(id == "chemistry_page"){
				toolSelected = "chemistry_link";
				window.location.assign("/direct/SkillBuilderSchoologyClassroom?folder_id=226695&tool_type=chemistry");
			}
			
		});
		//$("#instruction").html(instruction);
		//var progressbar = "<div class='progress'><div class='progress-bar progress-bar-striped active' role='progressbar' "
		//				+"aria-vavluenow='100' aria-valuemin='0' aria-valuemax='100' style='width:100%;'></div></div>";
		$("#problemSets").html("");
		$("#progressModal").modal("show");
		$("body").css("cursor", "progress");
		getShareLinks($("#folder_id").val());
		$("#tour-btn").click(function(){
			tour.init();
			if(tour.ended()){
				tour.restart();
			}else{
				tour.start();	
			}
		});
	});
	function getShareLinks(folderId){
		var tourMarker = true;
		$.getJSON(
				"GetSkillBuilderShareLinks?folder_id="
						+ folderId+"&from=classroom",
				function(data) {
					var rs = "";
					rs += "<ul class='searchable'>";
					if(data[0].type == "CurriculumItem"){
						rs += "<table class='table table-hover'><thead style='font-size:0.8em;'><tr class='row'><th class='col-md-1' style='text-align:right;'>View</th><th class='col-md-1' style='text-align:left;'>Assign</th><th class='col-md-10'></th></tr></thead><tbody style='font-size:1.5em;'>";
						
					}
					for (var i = 0; i < data.length; i++) {
						if (data[i].type == "CurriculumItem") {
							rs += "<tr class='curriculum_item row'><td class='col-md-1' style='text-align:right;'><a class='view_problem_icon' target='_blank' href='view_problems/"+data[i].id+"'><img src='images/view_problems_magnifier.png' style='width:30px'></a></td><td class='col-md-1' style='text-align:left;'><a href='javascript:void(0);' class='classroom_share_button'><input type='hidden' value='"+data[i].id+"'><input type='hidden' value='"+data[i].name+"'>"
								+ "<img src='images/schoology_logo.jpg' width='25px' height='25px' ></a></td><td class='col-md-10'>" + data[i].name
								+ "</td></tr>";
						} else {
							rs += "<li class='folder'><h2><span class='glyphicon glyphicon-folder-open' aria-hidden='true' style='font-size:0.8em;'></span>   <a>"
									+ data[i].name + "</a></h2></li>";
							if(tourMarker){
								rs += "<div id='tour_skill_builders'>";
								tourMarker = false;
							}else{
								rs += "<div>";
							}
							rs += "<table class='table table-hover'><thead style='font-size:0.8em;'><tr class='row'><th class='col-md-1' style='text-align:right;'>View</th><th class='col-md-1' style='text-align:left;'>Assign</th><th class='col-md-10'></th></tr></thead><tbody style='font-size:1.5em;'>";
							for (var j = 0; j < data[i].problem_sets.length; j++) {
								rs += "<tr class='curriculum_item row'><td class='col-md-1' style='text-align:right;'><a class='view_problem_icon' target='_blank' href='view_problems/"+data[i].problem_sets[j].id+"'><img src='images/view_problems_magnifier.png' style='width:30px;'></a></td><td class='col-md-1' style='text-align:left;'><a href='javascript:void(0);' class='classroom_share_button'><input type='hidden' value='"+data[i].problem_sets[j].id+"'><input type='hidden' value='"+data[i].problem_sets[j].name+"'>"
								+ "<img src='images/schoology_logo.jpg' width='25px' height='25px'></a></td><td class='col-md-10'>"+ data[i].problem_sets[j].name
								+ "</td></tr>";
							}
							rs += "</tbody></table></div>";
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
					$(".classroom_share_button").click(function(e) {
						$("#classroom_message_alert").remove();
						var alert = "<div id='classroom_message_alert' style='display:none;' class='alert alert-warning alert-dismissible fade in' role='alert'>"
									+"<button class='close' aria-label='Close' data-dismiss='alert' type='button'><span aria-hidden='true'>&times;</span></button><p id='classroom_message'></p></div>";
						$(this).parent().parent().append(alert);
						
						problem_set_id = $(this).children().first().val();
						problem_set_name = $(this).children().first().next().val();
						//decode here to make sure the problem set name is correct
						problem_set_name = decodeURIComponent(problem_set_name);
						//problem_set_name = encodeURIComponent(problem_set_name);
						
			    		$('#datePicker').datepicker({
			    			autoclose: true,
			    			startDate: '+1d',
			    			format: {
				                /*
				                Say our UI should display a week ahead,
				                but textbox should store the actual date.
				                This is useful if we need UI to select local dates,
				                but store in UTC
				                */
				                toDisplay: function (date, format, language) {
				                    var d = new Date(date);
				                    d.setDate(d.getDate()+1);
				                    return d.toLocaleDateString();
				                },
				                toValue: function (date, format, language) {
				                    var d = new Date(date);
				                    d.setDate(d.getDate());
				                    return new Date(d);
				                }
				            }
				        });
			    		var today = new Date();
			    		$('#datePicker').datepicker("setDate", today.toLocaleDateString());
			    		$('#datePicker').datepicker('setStartDate', today.toLocaleDateString());
						$(".modal-header #myModalLabel").text( problem_set_name );
			    		$('#myModal').modal('show');
			    		$(".modal").on("hidden.bs.modal", function(){
			    			$('#datePicker').datepicker('setStartDate', today.toLocaleDateString());
			    			$("#description-text").val('');
			    			$("#share_to_classroom_btn").removeClass("disabled");
			    			$('#assignment_info').hide();
			    		});
					});
					$("body").css("cursor", "initial");
					$("#progressModal").modal("hide");
					$("h3 a").hover(function(){
						$(this).animate({
							"font-size": "+=3"
						},60);
					},function(){
						$(this).animate({
							"font-size":"-=3"
						},60);
					});
					var firstElm = "tour_skill_builders";
					if(tourMarker){
						firstElm = "problemSets"
					}
					tour = new Tour({
						steps:[{
							element:$("#"+firstElm+" tbody tr:first-child"),
							title: "Skill Builders",
							content: "Each assignment is a skill builder. Students work on one skill until they get three right in a row.",
							placement: "top"
						},
						{
							element: $("#"+firstElm+" tr:first-child .view_problem_icon img"),
							title: "View Problems",
							content: "Click on this icon to see a sample of the problems.",
							placement: "top"
						},
						{
							element: $("#"+firstElm+" tr:first-child .classroom_share_button img"),
							title: "Schoology Classroom Share Button",
							content:"Click on this icon to assign the skill builder to your class.",
							placement:"top"
						}
						       
						],
						storage:false,
						backdrop:true
					});
				});
	}
</script>
</head>
<body>
	<nav class="navbar navbar-inverse navbar-fixed-top">
	<div class="container_customize">
		<div class="navbar-header">
			<button type="button" class="navbar-toggle collapsed"
				data-toggle="collapse" data-target="#navbar" aria-expanded="false"
				aria-controls="navbar">
				<span class="sr-only">Toggle navigation</span> 
				<span class="icon-bar"></span> 
				<span class="icon-bar"></span> 
				<span class="icon-bar"></span>
			</button>
			<a class="navbar-brand navbar_item" id="logo"><img src="images/ASSISTments_APP_logo.png" style="width:30px; display:inline;margin-right:5px;"/>ASSISTments Apps</a>
		</div>
		<div id="navbar" class="navbar-collapse collapse">
			<ul class="nav navbar-nav">
				<li><a id="landing_page" class="navbar_item" >Home</a></li>
				<li><a id="skill_builder_page" class="navbar_item" >Math Skill Builders</a></li>
				<li><a id="ap_page" class="navbar_item" >AP Statistics</a></li>
				<li><a id="chemistry_page" class="navbar_item" >Chemistry</a></li>
			</ul>
			<input id="tool_type" type="hidden" value="${sessionScope.tool_type }">
			<form class="navbar-form navbar-right">
				<input id="filter" type="text" class="form-control" placeholder="Search...">
			</form>
		</div>
		<!--/.navbar-collapse -->
	</div>
	</nav>
	
	<!-- Begin page content -->
	<div class="container-fluid">
		<div class="row">
			<div class="col-md-10 col-md-offset-1 main">
				<input type="hidden" id="folder_id" value="${sessionScope.folder.id }"/>
          		<h1 class="page-header">${sessionScope.folder.name } <button id='tour-btn' class='btn btn-info' style='margin-left:20px;'>Start Tour</button></h1>
          		<!-- <div class="well" ><h4 id="instruction"></h4></div>  -->
				<div id="problemSets">
				</div>
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
					<h4 class="modal-title" id="myModalLabel">Share to Schoology Classroom</h4>
				</div>
				<div class="modal-body">
					<label for="input-group">Choose Due Date:</label>
					
					<div class="input-group input-append date" id="datePicker">
		                <input type="text" class="form-control" name="date" id="">
		                <span class="input-group-addon add-on">
		                	<span class="glyphicon glyphicon-calendar"></span>
		                </span>
		            </div>
		            <!-- 
		            <input class="datepicker" id="datePicker" data-provide="datepicker" data-date-format="mm/dd/yyyy">
		            -->
		            <br>
        			<label for="description-text">Description:</label>
		            <div>
					<textarea class="form-control" id="description-text"></textarea>	
		            </div>
				</div>
				<div class="alert alert-info" role="alert" id="assignment_info"></div>
				<div class="modal-footer">
					<button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
					<button type="button" class="btn btn-primary" id="share_to_classroom_btn">Share</button>
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
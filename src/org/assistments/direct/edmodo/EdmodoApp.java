package org.assistments.direct.edmodo;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.assistments.connector.domain.PartnerToAssistments;
import org.assistments.connector.domain.PartnerToAssistments.ColumnNames;
import org.assistments.connector.exception.ReferenceNotFoundException;
import org.assistments.connector.exception.TransferUserException;
import org.assistments.connector.service.AccountService;
import org.assistments.connector.service.AssignmentLogService;
import org.assistments.connector.service.AssignmentService;
import org.assistments.connector.service.ProblemSetService;
import org.assistments.connector.service.StudentClassService;
import org.assistments.connector.service.impl.AccountServiceImpl;
import org.assistments.connector.service.impl.AssignmentLogServiceImpl;
import org.assistments.connector.service.impl.AssignmentServiceImpl;
import org.assistments.connector.service.impl.ProblemSetServiceImpl;
import org.assistments.connector.service.impl.StudentClassServiceImpl;
import org.assistments.connector.utility.Constants;
import org.assistments.direct.LiteUtility;
import org.assistments.domain.Assignment;
import org.assistments.edmodo.controller.EdmodoAssignmentController;
import org.assistments.edmodo.controller.EdmodoGroupController;
import org.assistments.edmodo.controller.EdmodoLaunchController;
import org.assistments.edmodo.domain.EdmodoGroup;
import org.assistments.edmodo.domain.EdmodoLaunchRequest;
import org.assistments.edmodo.utility.ApplicationSettings;
import org.assistments.service.controller.ErrorLogController;
import org.assistments.service.domain.ProblemSet;
import org.assistments.service.domain.ReferenceTokenPair;
import org.assistments.service.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class EdmodoApp {
	
	public static String API_KEY = "9a49f0648957d5ad890b20056e52d7c2eb872760"; 
	@Autowired
	ServletContext context;
	
	@RequestMapping(value = "/edmodo_install", method = RequestMethod.POST)
	public ResponseEntity<Map<String, Object>> install() {
		Map<String, Object> map = new HashMap<>();
		map.put("status", "success");
		return new ResponseEntity<>(map, HttpStatus.OK);
	}

	@RequestMapping(value = "/edmodo_app", method = RequestMethod.POST)
	public String launch(HttpSession session, @RequestParam Map<String, Object> req, HttpServletResponse response) 
			throws IOException {
		String launchKey = (String)req.get("launch_key");

		EdmodoLaunchController launchController = new EdmodoLaunchController(API_KEY);
		EdmodoLaunchRequest launchReqBean = launchController.launchRequest(launchKey);
		String edmodoUserToken = launchReqBean.getUser_token();
		String edmodoAccessToken = launchReqBean.getAccess_token();
		String firstName = launchReqBean.getFirst_name();
		String lastName = launchReqBean.getLast_name();
		String displayName = firstName + " " + lastName;
		String userType = launchReqBean.getUser_type();
		AccountService accServ = new AccountServiceImpl(ApplicationSettings.partner_reference);
		if("TEACHER".equals(userType)) {
			User user = LiteUtility.populateTeacherInfo(firstName, lastName, displayName);
			String schoolRef = ApplicationSettings.edmodo_shchool_ref;
			ReferenceTokenPair pair = null;
			try {
				pair = accServ.transferUser(user, schoolRef, edmodoUserToken, edmodoAccessToken, "edmodo_app");
			} catch (TransferUserException e) {
				throw new RuntimeException(e);
			}
			String userRef = pair.getExternalRef();
			String accessToken = pair.getAccessToken();
			session.setAttribute("user_ref", userRef);
			session.setAttribute("assistments_access_token", accessToken);
		}
		session.setAttribute("user_token", edmodoUserToken);
		session.setAttribute("access_token", edmodoAccessToken);
		if(req.get("assignment_ref") != null) {
			String assignmentRef = (String)req.get("assignment_ref");
			PartnerToAssistments assignment = null;
			try {
				assignment = AssignmentServiceImpl.getFromAssignmentRef(ApplicationSettings.partner_reference, assignmentRef);
			} catch (ReferenceNotFoundException e1) {
			}
			String teacherToken = assignment.getAssistmentsAccessToken();
			// get the student class which belongs to this teacher
			StudentClassService scs = new StudentClassServiceImpl(ApplicationSettings.partner_reference, teacherToken);
			
			if("STUDENT".equals(userType)) {
				// create the student account
				String userName = firstName + "_" + lastName;
				User student = LiteUtility.populateStudentInfo(firstName, lastName, userName);
				String partnerExternalRef = edmodoUserToken;
				String studentRef = null;
				String studentToken = null;
				try {
					ReferenceTokenPair pair = accServ.transferUser(student, LiteUtility.Direct_SCHOOL_REF, 
							partnerExternalRef, "", "StudentAccount");
					studentRef = pair.getExternalRef();
					studentToken = pair.getAccessToken();
				} catch(org.assistments.connector.exception.TransferUserException e) {
					ErrorLogController.addNewError(e, LiteUtility.ERROR_SOURCE_TYPE);
				}
				
				String studentClassRef = assignment.getNote();
				//enroll student into the class
				scs.enrollStudent(studentRef, studentClassRef);
				
				//save url to student report
				Map<String, String> map = new HashMap<>();
				map.put("edmodoUserToken", edmodoUserToken);
				map.put("edmodoAccessToken", edmodoAccessToken);
				map.put("edmodoAssignmentId", assignment.getPartnerExternalReference());
				String studentReportId = LiteUtility.generateStudentReportId(studentRef, assignmentRef);
				context.setAttribute(studentReportId, map);
				String onExit = LiteUtility.generateStudentReportURL(studentRef, assignmentRef, "edmodo");
				//have to encode url twice
				onExit = URLEncoder.encode(onExit, "UTF-8");
				onExit = URLEncoder.encode(onExit, "UTF-8");
				AssignmentService as = new AssignmentServiceImpl(ApplicationSettings.partner_reference, studentToken);
				
				String tutorURL = as.getAssignment(assignmentRef, onExit);
				tutorURL = URLEncoder.encode(Constants.ASSISSTments_URL, "UTF-8") + tutorURL;
				String loginURL = Constants.LOGIN_URL;
				String tutorLink = String.format("%1$s?partner=%2$s&access=%3$s&on_success=%4$s&on_failure=%5$s", 
						loginURL, ApplicationSettings.partner_reference, studentToken, tutorURL, LiteUtility.LOGIN_FAILURE);
				session.setAttribute("tutor_link", tutorLink);
				session.removeAttribute("notice_to_students");
				tutorLink = URLEncoder.encode(tutorLink, "UTF-8");
				String fullName = firstName + " " + lastName;
				session.setAttribute("student_name", fullName);
				fullName = URLEncoder.encode(fullName, "UTF-8");
				return "redirect:/tutor?tutor_link="+tutorLink+"&student_name="+fullName;
			} else {
				return "redirect:teacher_report/" + assignmentRef;
			}
		} else {
			return "edmodo_landing_page";
			
		}
		
	}
	@RequestMapping(value = "/teacher_report/{assignmentRef}", method = RequestMethod.GET)
	public String teacherReport(HttpSession session, HttpServletRequest req, @PathVariable String assignmentRef) { 
		PartnerToAssistments externalAssignment = null;
		try {
			externalAssignment = AssignmentServiceImpl.getFromAssignmentRef(ApplicationSettings.partner_reference, assignmentRef);
		} catch (ReferenceNotFoundException e1) {
			throw new RuntimeException(e1);
		}
		//from assignment reference to get problem set
		ProblemSetService pss = new ProblemSetServiceImpl();
		String token = externalAssignment.getAssistmentsAccessToken();
		
		AssignmentService as = new AssignmentServiceImpl(ApplicationSettings.partner_reference, token);
		Assignment assignment = null;
		try {
			assignment = as.find(assignmentRef);
		} catch (ReferenceNotFoundException e1) {
			e1.printStackTrace();
			ErrorLogController.addNewError(e1, LiteUtility.ERROR_SOURCE_TYPE);
		}
		ProblemSet ps = pss.find((int)assignment.getProblemSetId());
		String problemSetName = ps.getName();
		String problemSetId = String.valueOf(ps.getDecodedID());
		long studentClassId = assignment.getClassId();
		
		long assignmentId = assignment.getId();
		
		AssignmentLogService als = new AssignmentLogServiceImpl();
		List<User> notStartedStudents = als.getNotStartedStudents(assignmentRef);
		List<User> inProgressStudents = als.getInProgressStudents(assignmentRef);
		List<User> completeStudents = als.getCompleteStudents(assignmentRef);
		
		String viewProblemsLink = LiteUtility.DIRECT_URL + "/view_problems/" + assignment.getProblemSetId();
		session.setAttribute("student_class_id", studentClassId);
		session.setAttribute("problem_set_name", problemSetName);
		session.setAttribute("problem_set_id", problemSetId);
		session.setAttribute("assignment_id", assignmentId);
		session.setAttribute("view_problems_link", viewProblemsLink);
		session.setAttribute("not_started_students", notStartedStudents);
		session.setAttribute("in_progress_students", inProgressStudents);
		session.setAttribute("complete_students", completeStudents);
		//TODO: Change URL to test1 or production
//		String onSuccess = Constants.ASSISSTments_URL + "external_teacher/student_class/assignment?ref=" + assignmentRef;
		String onSuccess = Constants.ASSISSTments_URL + "teacher/report/mastery_status/"+studentClassId+"?assignment_id="+assignmentId;
		String onFailure = "assistments.org";
//		String loginURL = "http://csta14-5.cs.wpi.edu:3000/api2_helper/user_login";
		String loginURL = Constants.LOGIN_URL;

		AccountService accService = new AccountServiceImpl(ApplicationSettings.partner_reference);
		PartnerToAssistments user = null;
		try {
			user = accService.find(ColumnNames.ASSISTMENTS_ACCESS_TOKEN, token).get(0);
		} catch (ReferenceNotFoundException e) {
			e.printStackTrace();
		}

		String reportLink = String.format("%1$s?partner=%2$s&access=%3$s&on_success=%4$s&on_failure=%5$s", 
				loginURL, ApplicationSettings.partner_reference, user.getAssistmentsAccessToken(), onSuccess, onFailure);
		session.setAttribute("report_link", reportLink);
//		resp.sendRedirect(resp.encodeRedirectURL(addressToGo));
		return "report_link";
	}
	
	@RequestMapping(value = "/get_groups_for_user", method = RequestMethod.GET)
	public ResponseEntity<Map<String, List<Map<String, String>>>> 
		getGroupsForUser(HttpSession session, @RequestParam Map<String, Object> req) {
		String userToken = (String)session.getAttribute("user_token");
		String accessToken = (String)session.getAttribute("access_token");
		EdmodoGroupController groupController = new EdmodoGroupController(API_KEY, accessToken);
		List<EdmodoGroup> groups = groupController.getGroupForUser(userToken); 
		
		List<Map<String, String>> groupList = new ArrayList<>();
		
		//check if this user owns these classes
		for(EdmodoGroup group : groups) {
			List<String> owners = group.getOwners();
			if(owners.contains(userToken)) {
				Map<String, String> map = new HashMap<>();
				
				map.put("group_id", String.valueOf(group.getGroup_id()));
				map.put("group_name", String.valueOf(group.getTitle()));
				groupList.add(map);
			}
		}
		Map<String, List<Map<String, String>>> body = new HashMap<>();
		body.put("groups", groupList);
		return new ResponseEntity<Map<String,List<Map<String, String>>>>(body, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/edmodo/setup", method = RequestMethod.POST)
	public ResponseEntity<Map<String, String>> 
		setup(HttpSession session, @RequestParam Map<String, Object> req) {
		
		String userRef = (String)session.getAttribute("user_ref");
		String assistmentsAccessToken = (String)session.getAttribute("assistments_access_token");
		
		String userToken = (String)session.getAttribute("user_token");
		String accessToken = (String)session.getAttribute("access_token");
		Map<String, String> map = new HashMap<>();
		String edmodoLink = new String();
		String courseId = (String)req.get("course_id");
		String courseName = (String)req.get("course_name");
		String problemSetId = (String)req.get("problem_set_id");
		String due_date = (String)req.get("due_date");
		StudentClassService scs = new StudentClassServiceImpl(ApplicationSettings.partner_reference, assistmentsAccessToken);
		String stuClassRef = scs.transferStudentClass(courseName, courseId, "", userRef);
		
		//create assignment for this teacher
		AssignmentService as = new AssignmentServiceImpl(ApplicationSettings.partner_reference, assistmentsAccessToken);
		String assignmentRef = as.createClassAssignment(problemSetId, stuClassRef, userToken, accessToken, stuClassRef);
		EdmodoAssignmentController eac = new EdmodoAssignmentController(API_KEY, userToken, accessToken);
//		String url = LiteUtility.DIRECT_URL + "/edmodo/" + assignmentRef;
		ProblemSetService pss = new ProblemSetServiceImpl();
		ProblemSet ps = pss.find(Long.valueOf(problemSetId));
		String title = ps.getName();
		String edmodoAssignmentId = eac.createGroupAssignment(Long.valueOf(courseId), title, assignmentRef, due_date);
		try {
			PartnerToAssistments pta = as.find(ColumnNames.ASSISTMENTS_EXTERNAL_REFERENCE, assignmentRef).get(0);
			pta.setPartnerExternalReference(edmodoAssignmentId);
			as.updateExternalAssignment(pta);
		} catch (ReferenceNotFoundException e) {
			// this should not ever happen, because I just creted this assignment
		}
		
		map.put("edmodo_link", edmodoLink);
		return new ResponseEntity<>(map, HttpStatus.OK);
	}
	@RequestMapping(value = "/edmodo_home", method = RequestMethod.GET)
	public String returnHome(HttpSession session, HttpServletRequest req){
		return "edmodo_landing_page";
	}
}

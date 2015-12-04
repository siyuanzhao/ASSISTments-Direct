package org.assistments.direct.google;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;

import org.apache.commons.codec.binary.Base32;
import org.assistments.connector.domain.PartnerToAssistments;
import org.assistments.connector.domain.PartnerToAssistments.ColumnNames;
import org.assistments.connector.exception.ReferenceNotFoundException;
import org.assistments.connector.exception.TransferUserException;
import org.assistments.connector.service.AccountService;
import org.assistments.connector.service.AssignmentService;
import org.assistments.connector.service.ProblemSetService;
import org.assistments.connector.service.StudentClassService;
import org.assistments.connector.service.impl.AccountServiceImpl;
import org.assistments.connector.service.impl.AssignmentServiceImpl;
import org.assistments.connector.service.impl.ProblemSetServiceImpl;
import org.assistments.connector.service.impl.StudentClassServiceImpl;
import org.assistments.connector.utility.Constants;
import org.assistments.direct.LiteUtility;
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
public class GoogleClassroom {
	
	@Autowired
	ServletContext context;
	
	@RequestMapping(method = RequestMethod.GET, value="/{assignmentReference}")
	public String parseLink(@PathVariable String assignmentReference, HttpSession session) {
		//TODO: first should check if this assignment reference already exists
		
		session.setAttribute("assignmentReference", assignmentReference);
		
		PartnerToAssistments assignment = null;
		try {
			assignment = AssignmentServiceImpl.getFromAssignmentRef(LiteUtility.PARTNER_REF, assignmentReference);
		} catch (ReferenceNotFoundException e1) {
			ErrorLogController.addNewError(e1, LiteUtility.ERROR_SOURCE_TYPE);
//			String errorMessage = "Sorry... There is something wrong with this link. The assignment doesn't exist!";
//			String instruction = "If you entered the URL in by hand, double check that it is correct";
//			LiteUtility.directToErrorPage(errorMessage, instruction, req, resp);
		}
		ProblemSetService pss = new ProblemSetServiceImpl();
		ProblemSet ps = pss.findByAssignment(assignmentReference);
		session.setAttribute("problem_set_name", ps.getName());
		
		String teacherToken = assignment.getAssistmentsAccessToken();
		String stuClassRef = assignment.getNote();
		// get the student class which belongs to this teacher
		StudentClassService scs = new StudentClassServiceImpl(LiteUtility.PARTNER_REF, teacherToken);
		PartnerToAssistments stuClass = null;
		try {
			stuClass = scs.find(ColumnNames.ASSISTMENTS_EXTERNAL_REFERENCE, stuClassRef).get(0);
		} catch (ReferenceNotFoundException e) {
			e.printStackTrace();
		}
//		String studentClassRef = stuClass.getAssistmentsExternalRefernce();
		String courseId = stuClass.getPartnerExternalReference();
		courseId = courseId.substring(17);
		session.setAttribute("course_id", courseId);
		
		//get problem set name
		
		
		return "google_classroom";
	}
	
	@RequestMapping(method = RequestMethod.POST, value="/setup")
	public ResponseEntity<Map<String, Object>> setup(@RequestParam Map<String, Object> req, HttpSession session) {
		String classroomLink = "";
		
		String email = (String)req.get("email");
		String partnerExternalRef = "google_" + email;
		String firstName = (String)req.get("first_name");
		String lastName = (String)req.get("last_name");
		String emailAddr = (String)req.get("email_address");
		
		String problemSetId = req.get("problem_set_id") == null ? 
				String.valueOf(session.getAttribute("problem_set")) : String.valueOf(req.get("problem_set_id"));
		String courseId = (String)req.get("course_id");
		String courseName = (String)req.get("course_name");
		String displayName = firstName + " " + lastName;
		String userRef = "";
		String accessToken = "";
		courseId = "google_classroom_" + courseId;
		User user = LiteUtility.populateTeacherInfo(firstName, lastName, displayName);
		AccountService as = new AccountServiceImpl(LiteUtility.PARTNER_REF);
		ReferenceTokenPair pair = null;
		try {
			pair = as.transferUser(user, LiteUtility.Direct_SCHOOL_REF, partnerExternalRef, "", emailAddr);
		} catch (TransferUserException e) {
			throw new RuntimeException(e);
		}
		
		userRef = pair.getExternalRef();
		accessToken = pair.getAccessToken();
		
		StudentClassService scs = new StudentClassServiceImpl(LiteUtility.PARTNER_REF, accessToken);
		String stuClassRef = scs.transferStudentClass(courseName, courseId, "", userRef);
		
		AssignmentService assignServ = new AssignmentServiceImpl(LiteUtility.PARTNER_REF, accessToken);
		String assignmentRef = assignServ.createClassAssignment(problemSetId, stuClassRef, partnerExternalRef, "", stuClassRef);
		
		classroomLink = LiteUtility.DIRECT_URL + "/google_classroom/" + assignmentRef;
		
		ProblemSetService pss = new ProblemSetServiceImpl();
		boolean b = pss.isSkillBuilder(Long.valueOf(problemSetId));
		
		
		Map<String, Object> resp = new HashMap<>();
		resp.put("classroom_link", classroomLink);
		resp.put("is_skill_builder", b);
		
		return new ResponseEntity<Map<String,Object>>(resp, HttpStatus.OK);
	}
	
	@RequestMapping(method = RequestMethod.GET, value="/start")
	public ResponseEntity<Map<String, Object>> start(@RequestParam Map<String, Object> req, HttpSession session) 
			throws UnsupportedEncodingException {
		String role = (String)req.get("role");
		String firstName = (String) req.get("firstName");
		String lastName = (String) req.get("lastName");
		String userId = (String) req.get("id");
		
		String location = "";
		
		String assignmentRef = req.get("assignmentReference").toString();
		PartnerToAssistments assignment = null;
		try {
			assignment = AssignmentServiceImpl.getFromAssignmentRef(LiteUtility.PARTNER_REF, assignmentRef);
		} catch (ReferenceNotFoundException e1) {
			ErrorLogController.addNewError(e1, LiteUtility.ERROR_SOURCE_TYPE);
//			String errorMessage = "Sorry... There is something wrong with this link. The assignment doesn't exist!";
//			String instruction = "If you entered the URL in by hand, double check that it is correct";
//			LiteUtility.directToErrorPage(errorMessage, instruction, req, resp);
		}
		String teacherToken = assignment.getAssistmentsAccessToken();
		// get the student class which belongs to this teacher
		StudentClassService scs = new StudentClassServiceImpl(LiteUtility.PARTNER_REF, teacherToken);
//		PartnerToAssistments stuClass = null;
//		try {
//			stuClass = scs.find(ColumnNames.ASSISTMENTS_ACCESS_TOKEN, teacherToken).get(0);
//		} catch (ReferenceNotFoundException e) {
//			e.printStackTrace();
//		}
//		String studentClassRef = stuClass.getAssistmentsExternalRefernce();

		String studentClassRef = assignment.getNote();
//		String courseId = stuClass.getPartnerExternalReference();
//		courseId.substring(17);
		if(("student").equals(role)) {
			// create the student account
			String userName = firstName + "_" + lastName;
			User student = LiteUtility.populateStudentInfo(firstName, lastName, userName);
			String partnerExternalRef = "google_student" + userId;
	//		List<String> studentRefAccessToken = null;
			String studentRef = null;
			String studentToken = null;
			AccountService accServ = new AccountServiceImpl(LiteUtility.PARTNER_REF);
			try {
				ReferenceTokenPair pair = accServ.transferUser(student, LiteUtility.Direct_SCHOOL_REF, 
						partnerExternalRef, "", "StudentAccount");
				studentRef = pair.getExternalRef();
				studentToken = pair.getAccessToken();
			} catch(org.assistments.connector.exception.TransferUserException e) {
				ErrorLogController.addNewError(e, 1, LiteUtility.ERROR_SOURCE_TYPE);
			}
			
			//enroll student into the class
			scs.enrollStudent(studentRef, studentClassRef);
			
			//save url to student report
			String studentReportURL = Constants.ASSISSTments_URL+"external_tutor/student_class/report?partner_id="+LiteUtility.PARTNER_ID
					+"&class_ref="+studentClassRef+"&assignment_ref="+assignmentRef;
	 
			String studentReportId = LiteUtility.generateStudentReportId(studentRef, assignmentRef);
			context.setAttribute(studentReportId, studentReportURL);
			String onExit = LiteUtility.generateStudentReportURL(studentRef, assignmentRef, "google_classroom");
	//		String onExit = "http://csta14-5.cs.wpi.edu:8080/connector/studentReport";
			//have to encode url twice
			onExit = URLEncoder.encode(onExit, "UTF-8");
			onExit = URLEncoder.encode(onExit, "UTF-8");
			AssignmentService as = new AssignmentServiceImpl(LiteUtility.PARTNER_REF, studentToken);

			String tutorURL = as.getAssignment(assignmentRef, onExit);
			tutorURL = URLEncoder.encode(Constants.ASSISSTments_URL, "UTF-8") + tutorURL;
			String loginURL = Constants.LOGIN_URL;
			String tutorLink = String.format("%1$s?partner=%2$s&access=%3$s&on_success=%4$s&on_failure=%5$s", 
					loginURL, LiteUtility.PARTNER_REF, studentToken, tutorURL, LiteUtility.LOGIN_FAILURE);
			session.setAttribute("tutor_link", tutorLink);
			tutorLink = URLEncoder.encode(tutorLink, "UTF-8");
			session.removeAttribute("notice_to_students");
			String fullName = firstName + " " + lastName;
			fullName = URLEncoder.encode(fullName, "UTF-8");
			location = LiteUtility.DIRECT_URL+"/tutor?tutor_link="+tutorLink+"&student_name="+fullName;
		} else {
			//teacher wants to see the report		
			//get report link
			Base32 base32 = new Base32();
			String reportRef = base32.encodeAsString(assignmentRef.getBytes());
			location = LiteUtility.DIRECT_URL + "/report/" + reportRef;
		}
		Map<String, Object> resp = new HashMap<>();
		resp.put("location", location);
		
		return new ResponseEntity<Map<String,Object>>(resp, HttpStatus.OK);
	}

	@RequestMapping(method = RequestMethod.GET, value="/tutor")
	public String tutor() {
		
		return "tutor";
	}
}

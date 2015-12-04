package org.assistments.direct.schoology;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
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
import org.assistments.edmodo.utility.ApplicationSettings;
import org.assistments.service.controller.ErrorLogController;
import org.assistments.service.domain.Assignment;
import org.assistments.service.domain.ProblemSet;
import org.assistments.service.domain.ReferenceTokenPair;
import org.assistments.service.domain.User;
import org.json.simple.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
@Controller
public class SchoologyClassroom {
	
	@Autowired
	ServletContext context;
	
	@RequestMapping(method=RequestMethod.GET, value="/get-list-of-assignments")
	public ResponseEntity<Map<String, Object>> getListOfAssignments(@RequestParam Map<String, Object> req, HttpSession session)
	{
		SchoologyAPI sapi = (SchoologyAPI)session.getAttribute("sapi");
		String userRef = "";
		String accessToken = "";
		ReferenceTokenPair pair = null;
		try {
			pair = sapi.findUser();
			userRef = pair.getExternalRef();
			accessToken = pair.getAccessToken();
		} catch (TransferUserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		StudentClassService scs = new StudentClassServiceImpl(LiteUtility.PARTNER_REF, accessToken);
		String stuClassRef = scs.transferStudentClass(sapi.getCourseName()+"-"+sapi.getSectionName(), sapi.getSectionID(), sapi.getPartnerAccessToken()+";"+sapi.getPartnerAccessSecret(), userRef);
		
		AssignmentService assignServ = new AssignmentServiceImpl(LiteUtility.PARTNER_REF, accessToken);
		List<Assignment> listAssignments = assignServ.getClassAssignments(stuClassRef);
		JsonArray assignmentArray = new JsonArray();
		JsonArray reportLinksArray = new JsonArray();
		for (Assignment assign: listAssignments){
			JsonObject assignment = new JsonObject();
			JsonObject reportLink = new JsonObject();
			assignment.addProperty("id", assign.getId());
			assignment.addProperty("psid", assign.getPs().getEncodedID());
			assignment.addProperty("name", assign.getPs().getName());
			assignment.addProperty("reference", assign.getExternalReference());
			String report = "";
			if(sapi.isTeacher())
				report = parseTeacherLink(assign.getExternalReference().toString(), String.valueOf(assign.getId()),String.valueOf(assign.getStudentClassId()), session);
			else if(!sapi.isTeacher())
				try {
					report = parseStudentLink(userRef,accessToken, assign.getExternalReference().toString(), String.valueOf(assign.getId()),String.valueOf(assign.getStudentClassId()), session);
				} catch (UnsupportedEncodingException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			String dueDate = "";
			try {
				dueDate = sapi.getAssignmentDueDate((String)session.getAttribute("partnerExternalReference"));
			} catch (OAuthMessageSignerException | OAuthExpectationFailedException | OAuthCommunicationException
					| IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			assignment.addProperty("duedate", dueDate);
			assignment.addProperty("classid", assign.getStudentClassId());
			reportLink.addProperty("link", report);
			
			assignmentArray.add(assignment);
			reportLinksArray.add(reportLink);
		}
		session.setAttribute("listAssignments", assignmentArray.toString());
		Map<String, Object> resp = new HashMap<>();
		resp.put("list_assignments", assignmentArray.toString());
		resp.put("report_links", reportLinksArray.toString());
		
		return new ResponseEntity<Map<String,Object>>(resp, HttpStatus.OK);
	}
	@RequestMapping(method = RequestMethod.GET, value="/teacher-report/{assignmentReference}")
	public String parseTeacherLink(@PathVariable String assignmentReference, @PathVariable String assignmentId, @PathVariable String studentClassId, HttpSession session) {
		//TODO: first should check if this assignment reference already exists
		String location = "";

		session.setAttribute("assignmentReference", assignmentReference);
		
		PartnerToAssistments assignment = null;
		try {
			assignment = AssignmentServiceImpl.getFromAssignmentRef(LiteUtility.PARTNER_REF, assignmentReference);
		} catch (ReferenceNotFoundException e1) {
			ErrorLogController.addNewError(e1, LiteUtility.ERROR_SOURCE_TYPE);
		}
		String schoologyAssignmentId = assignment.getPartnerExternalReference();
		ProblemSetService pss = new ProblemSetServiceImpl();
		ProblemSet ps = pss.findByAssignment(assignmentReference);
		session.setAttribute("problem_set_name", ps.getName());
		
		String teacherToken = assignment.getAssistmentsAccessToken();
		// get the student class which belongs to this teacher
		StudentClassService scs = new StudentClassServiceImpl(LiteUtility.PARTNER_REF, teacherToken);
		PartnerToAssistments stuClass = null;
		try {
			stuClass = scs.find(ColumnNames.ASSISTMENTS_ACCESS_TOKEN, teacherToken).get(0);
		} catch (ReferenceNotFoundException e) {
			e.printStackTrace();
		}
		
		Base32 base32 = new Base32();
		String reportRef = base32.encodeAsString(assignmentReference.getBytes());
		String reportLink = "/direct/report/" + reportRef;
		session.setAttribute("partnerExternalReference", schoologyAssignmentId);
		return reportLink;
	}
	
	@RequestMapping(method = RequestMethod.GET, value="/student/assignment/{assignmentReference}")
	public String parseStudentLink(@PathVariable String studentRef,@PathVariable String studentToken, @PathVariable String assignmentReference, @PathVariable String assignmentId, @PathVariable String studentClassId, HttpSession session) throws UnsupportedEncodingException {
		//TODO: first should check if this assignment reference already exists
		String location = "";
		SchoologyAPI sapi = (SchoologyAPI)session.getAttribute("sapi");

		session.setAttribute("assignmentReference", assignmentReference);
		
		PartnerToAssistments assignment = null;
		try {
			assignment = AssignmentServiceImpl.getFromAssignmentRef(LiteUtility.PARTNER_REF, assignmentReference);
		} catch (ReferenceNotFoundException e1) {
			ErrorLogController.addNewError(e1, LiteUtility.ERROR_SOURCE_TYPE);
		}
		String schoologyAssignmentId = assignment.getPartnerExternalReference();

		String teacherToken = assignment.getAssistmentsAccessToken();

		StudentClassService scs = new StudentClassServiceImpl(LiteUtility.PARTNER_REF, teacherToken);

		String studentClassRef = assignment.getNote();
		//enroll student into the class
		scs.enrollStudent(studentRef, studentClassRef);
		
		//save url to student report
		String onExit = LiteUtility.generateStudentReportURL(studentRef, assignmentReference, "schoology");
		
		//have to encode url twice
		onExit = URLEncoder.encode(onExit, "UTF-8");
		onExit = URLEncoder.encode(onExit, "UTF-8");
		
		AssignmentService as = new AssignmentServiceImpl(LiteUtility.PARTNER_REF, studentToken);
		
		String tutorURL = as.getAssignment(assignmentReference, onExit);
		
		tutorURL = URLEncoder.encode(Constants.ASSISSTments_URL, "UTF-8") + tutorURL;
		String loginURL = Constants.LOGIN_URL;
		String tutorLink = String.format("%1$s?partner=%2$s&access=%3$s&on_success=%4$s&on_failure=%5$s", 
				loginURL, LiteUtility.PARTNER_REF, studentToken, tutorURL, LiteUtility.LOGIN_FAILURE);
		tutorLink = URLEncoder.encode(tutorLink, "utf-8");
		session.setAttribute("partnerExternalReference", schoologyAssignmentId);
		String fullName = sapi.getUser().getUsername();
		fullName = URLEncoder.encode(fullName, "utf-8");
		return "/direct/tutor?tutor_link=" + tutorLink + "&student_name=" + fullName;
	}
	@RequestMapping(method = RequestMethod.POST, value="/student/assignment/comment")
	public ResponseEntity<Map<String,Object>> postAssignmentComment(@RequestParam Map<String, Object> req, HttpSession session) {
		//TODO: first should check if this assignment reference already exists
		String comment = (String)req.get("comment");
		String assignmentReference = req.get("problem_set_id") == null ? 
		String.valueOf(session.getAttribute("problem_set")) : String.valueOf(req.get("problem_set_id"));
		SchoologyAPI sapi = (SchoologyAPI)session.getAttribute("sapi");
		
		Map<String, Object> resp = new HashMap<>();
		HttpStatus status = HttpStatus.BAD_REQUEST;;
		
		String userRef = "";
		String accessToken = "";
		ReferenceTokenPair pair = null;
		try {
			pair = sapi.findUser();
			userRef = pair.getExternalRef();
			accessToken = pair.getAccessToken();
		} catch (TransferUserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		PartnerToAssistments assignment = null;
		try {
			assignment = AssignmentServiceImpl.getFromAssignmentRef(LiteUtility.PARTNER_REF, assignmentReference);
		} catch (ReferenceNotFoundException e1) {
			ErrorLogController.addNewError(e1, LiteUtility.ERROR_SOURCE_TYPE);
		}
		String assignmentId = assignment.getPartnerExternalReference();
		String schoologyCommentID="";
		try {
			schoologyCommentID = sapi.postAssignmentComment(assignmentId, comment);
			status = HttpStatus.OK;
		} catch (OAuthMessageSignerException | OAuthExpectationFailedException | OAuthCommunicationException
				| IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		resp.put("updateID", schoologyCommentID);
		return new ResponseEntity<Map<String,Object>>(resp, status);
		
	}
	@RequestMapping(method = RequestMethod.POST, value="/assign")
	public ResponseEntity<Map<String, Object>> setup(@RequestParam Map<String, Object> req, HttpSession session) {
		String problemSetId = req.get("problem_set_id") == null ? 
		String.valueOf(session.getAttribute("problem_set")) : String.valueOf(req.get("problem_set_id"));
		String dueDate = (String)req.get("due_date");
		String description = (String)req.get("description");
		String userRef = "";
		String accessToken = "";
		String schoologyAssignmentID = "";
		List<String> listOfAssignees = null;
		
		Map<String, Object> resp = new HashMap<>();
		HttpStatus status = HttpStatus.BAD_REQUEST;;
		
		SchoologyAPI sapi = (SchoologyAPI)session.getAttribute("sapi");
		
		ProblemSetService pss = new ProblemSetServiceImpl();
		boolean b = pss.isSkillBuilder(Long.valueOf(problemSetId));
		ProblemSet ps = pss.find(Long.valueOf(problemSetId));
		String title = ps.getName();
		
		try {
			listOfAssignees = sapi.getListOfEnrollments();
			if(listOfAssignees!=null)
			{
				String gradingGroupID = sapi.transferGradingGroup(listOfAssignees);
				schoologyAssignmentID = sapi.postAssignment(title, description, dueDate, listOfAssignees);
			}
		} catch (OAuthMessageSignerException | OAuthExpectationFailedException | OAuthCommunicationException
				| IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(listOfAssignees!=null)
		{
			ReferenceTokenPair pair = null;
			try {
				pair = sapi.findUser();
				userRef = pair.getExternalRef();
				accessToken = pair.getAccessToken();
			} catch (TransferUserException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			StudentClassService scs = new StudentClassServiceImpl(LiteUtility.PARTNER_REF, accessToken);
			String stuClassRef = scs.transferStudentClass(sapi.getCourseName()+"-"+sapi.getSectionName(), sapi.getSectionID(), sapi.getPartnerAccessToken()+";"+sapi.getPartnerAccessSecret(), userRef);
			
			AssignmentService assignServ = new AssignmentServiceImpl(LiteUtility.PARTNER_REF, accessToken);
			String assignmentRef = assignServ.createClassAssignment(problemSetId, stuClassRef, sapi.getUID(), sapi.getPartnerAccessToken()+";"+sapi.getPartnerAccessSecret(), stuClassRef);
					
			
			
			try {
				PartnerToAssistments pta = assignServ.find(ColumnNames.ASSISTMENTS_EXTERNAL_REFERENCE, assignmentRef).get(0);
				pta.setPartnerExternalReference(schoologyAssignmentID);
				assignServ.updateExternalAssignment(pta);
			} catch (ReferenceNotFoundException e) {
				// this should not ever happen, because I just creted this assignment
			}
			resp.put("is_skill_builder", b);
			status = HttpStatus.OK;
		}	
		else
		{
			status = HttpStatus.BAD_REQUEST;
		}
		
		
		return new ResponseEntity<Map<String,Object>>(resp, status);
	}
	
	@RequestMapping(method = RequestMethod.POST, value="/post-update")
	public ResponseEntity<Map<String, Object>> post(@RequestParam Map<String, Object> req, HttpSession session) {
		String problemSetName = String.valueOf(req.get("problem_set_name"));
		String update = (String)req.get("update");
		
		Map<String, Object> resp = new HashMap<>();
		HttpStatus status = HttpStatus.BAD_REQUEST;;
		
		SchoologyAPI sapi = (SchoologyAPI)session.getAttribute("sapi");
		String schoologyUpdateID="";
		try {
			schoologyUpdateID = sapi.postAnnouncement(update, problemSetName);
			status = HttpStatus.OK;
		} catch (OAuthMessageSignerException | OAuthExpectationFailedException | OAuthCommunicationException
				| IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		resp.put("updateID", schoologyUpdateID);
		return new ResponseEntity<Map<String,Object>>(resp, status);
	}
	@RequestMapping(method = RequestMethod.GET, value="/initialize")
	public ResponseEntity<Map<String, Object>> start(@RequestParam Map<String, Object> req, HttpSession session) 
			throws UnsupportedEncodingException {
		String role = (String)req.get("role");
		String firstName = (String) req.get("firstName");
		String lastName = (String) req.get("lastName");
		String userId = (String) req.get("id");
		
		String location = "";
		
		String assignmentRef = session.getAttribute("assignmentReference").toString();
		PartnerToAssistments assignment = null;
		try {
			assignment = AssignmentServiceImpl.getFromAssignmentRef(LiteUtility.PARTNER_REF, assignmentRef);
		} catch (ReferenceNotFoundException e1) {
			ErrorLogController.addNewError(e1, LiteUtility.ERROR_SOURCE_TYPE);
		}
		String teacherToken = assignment.getAssistmentsAccessToken();
		// get the student class which belongs to this teacher
		StudentClassService scs = new StudentClassServiceImpl(LiteUtility.PARTNER_REF, teacherToken);
		PartnerToAssistments stuClass = null;
		try {
			stuClass = scs.find(ColumnNames.ASSISTMENTS_ACCESS_TOKEN, teacherToken).get(0);
		} catch (ReferenceNotFoundException e) {
			e.printStackTrace();
		}
//		String studentClassRef = stuClass.getAssistmentsExternalRefernce();

		String studentClassRef = assignment.getNote();
		String courseId = stuClass.getPartnerExternalReference();
		courseId.substring(17);
		if(("student").equals(role)) {
			// create the student account
			String userName = firstName + "_" + lastName;
			User student = LiteUtility.populateStudentInfo(firstName, lastName, userName);
			String partnerExternalRef = userId;
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
			String onExit = LiteUtility.generateStudentReportURL(studentRef, assignmentRef, "schoology_classroom");
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
			session.removeAttribute("notice_to_students");
			String fullName = firstName + " " + lastName;
			session.setAttribute("student_name", fullName);
			location = LiteUtility.DIRECT_URL+"/tutor";
		} else {
			//teacher wants to see the report
			//first check if the teacher owns this assignment
		
			//get report link
			Base32 base32 = new Base32();
			String reportRef = base32.encodeAsString(assignmentRef.getBytes());
			location = LiteUtility.DIRECT_URL + "/report/" + reportRef;
		}
		Map<String, Object> resp = new HashMap<>();
		resp.put("location", location);
		
		return new ResponseEntity<Map<String,Object>>(resp, HttpStatus.OK);
	}

//	@RequestMapping(method = RequestMethod.GET, value="/tutor")
//	public String sendTutor() {
//		
//		return "tutor";
//	}
}
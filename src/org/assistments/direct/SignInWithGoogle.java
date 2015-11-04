package org.assistments.direct;

import java.io.IOException;
import java.net.URLEncoder;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.codec.binary.Base32;
import org.assistments.connector.controller.ExternalShareLinkDAO;
import org.assistments.connector.domain.ExternalShareLink;
import org.assistments.connector.domain.PartnerToAssistments;
import org.assistments.connector.domain.PartnerToAssistments.ColumnNames;
import org.assistments.connector.exception.ReferenceNotFoundException;
import org.assistments.connector.service.AccountService;
import org.assistments.connector.service.AssignmentService;
import org.assistments.connector.service.StudentClassService;
import org.assistments.connector.service.impl.AccountServiceImpl;
import org.assistments.connector.service.impl.AssignmentServiceImpl;
import org.assistments.connector.service.impl.StudentClassServiceImpl;
import org.assistments.connector.utility.Constants;
import org.assistments.connector.utility.LocalhostSettings;
import org.assistments.service.controller.ErrorLogController;
import org.assistments.service.domain.ReferenceTokenPair;
import org.assistments.service.domain.User;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;

@WebServlet({"/sign_in_with_google"})
public class SignInWithGoogle extends HttpServlet {
	private static final long serialVersionUID = -3881475316561553730L;
	static final String CLIENT_ID = "588893615069-3l8u6q8n9quf6ouaj1j9de1m4q24kb4k.apps.googleusercontent.com";
//	static final String APPS_DOMAIN_NAME = "http://csta14-5.cs.wpi.edu:8080";
	static final List<String> SCOPES = Arrays.asList("email", "profile");
	static final String CLIENT_SECRET = "Azr5EdlL3YuSXDMtHvt6sk8P";

	public SignInWithGoogle() {
		super();
	}
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doPost(req, resp);
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String idTokenString = req.getParameter("idtoken");
		
		HttpTransport transport = new NetHttpTransport();
		JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
		GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(transport, jsonFactory).
				setAudience(Arrays.asList(CLIENT_ID)).build();
		GoogleIdToken idToken = null;
		try {
			idToken = verifier.verify(idTokenString);
		} catch(GeneralSecurityException e) {
			e.printStackTrace();
		}
		if(idToken != null) {
			Payload payload = idToken.getPayload();
			String firstName = new String();
			if(payload.getUnknownKeys().get("given_name") != null) {
				firstName = payload.getUnknownKeys().get("given_name").toString();
			}
			String lastName = new String();
			if(payload.getUnknownKeys().get("family_name") != null) {
				lastName = payload.getUnknownKeys().get("family_name").toString();
			}
			String userId = payload.getSubject();
//			String email = payload.getEmail();
			HttpSession reqSession = req.getSession();
			AccountService accServ = new AccountServiceImpl(LiteUtility.PARTNER_REF);
			//if teacher signs in with google
			if(req.getParameter("teacher") != null) {
				String problemSet = (String)reqSession.getAttribute("problem_set");
				String shareLinkRef = (String)reqSession.getAttribute("share_link_ref");
//				String problemSetName = (String)reqSession.getAttribute("problem_set_name");
//				String problemSetStr = (String)reqSession.getAttribute("problem_set_str");
				
				String thirdPartyId = "google_" + userId;
				String studentClassPartnerRef = thirdPartyId;
				String displayName = firstName + " " + lastName;
				User teacher = LiteUtility.populateTeacherInfo(firstName, lastName, displayName);
//				List<String> teacherRefAccessToken = null;
				String teacherRef = null;
				String teacherToken = null;
				try {
					
					ReferenceTokenPair pair = accServ.transferUser(teacher, LiteUtility.Direct_SCHOOL_REF, 
							thirdPartyId, "", "");
					teacherRef = pair.getExternalRef();
					teacherToken = pair.getAccessToken();
				} catch(org.assistments.connector.exception.TransferUserException e) {
					String errorMessage = e.getMessage();
					String instruction = "The server seems to be unstable at this moment. Please take a break and try it again later.";
					LiteUtility.directToErrorPage(errorMessage, instruction, req, resp);
					return;
				}
				
				String studentClassName = "Class";
				// create a class for this teacher
				StudentClassService scs = new StudentClassServiceImpl(LiteUtility.PARTNER_REF, teacherToken);
				String studentClassRef = scs.createStudentClass(studentClassName, studentClassPartnerRef, "", teacherRef);
//				String studentClassRef = LiteUtility.createClass(studentClassName,
//						teacherToken, studentClassPartnerRef);
				// create class assignment
				AssignmentService as = new AssignmentServiceImpl(LiteUtility.PARTNER_REF, teacherToken);
				String assignmentRef = as.createClassAssignment(problemSet, studentClassRef, thirdPartyId, "", studentClassRef);
//				assignmentRef = LiteUtility.createAssignment(problemSet,
//						studentClassRef, teacherToken, thirdPartyId);
				Base32 base32 = new Base32();
				String reportRef = base32.encodeAsString(assignmentRef.getBytes());
				
				String teacherLink = LiteUtility.REPORT_LINK_PREFIX + "/" + reportRef;
				String studentLink = LiteUtility.ASSIGNMENT_LINK_PREFIX + "/" + assignmentRef;
				//store the association between share link and user
				PartnerToAssistments shareLink = new ExternalShareLink(LiteUtility.PARTNER_REF);
				shareLink.setAssistmentsExternalRefernce(shareLinkRef);
				shareLink.setAssistmentsAccessToken(teacherToken);
				shareLink.setPartnerExternalReference(thirdPartyId);
				shareLink.setNote(assignmentRef);
				ExternalShareLinkDAO shareLinkDAO = new ExternalShareLinkDAO(LiteUtility.PARTNER_REF);
				shareLinkDAO.add(shareLink);
				
				reqSession.setAttribute("student_link", studentLink);
				reqSession.setAttribute("teacher_link", teacherLink);
//				reqSession.setAttribute("problem_set_name", problemSetName);
				reqSession.setAttribute("user", teacherRef);
				reqSession.setAttribute("email", thirdPartyId);
				reqSession.setAttribute("from", "google");
				reqSession.setAttribute("submit", "Sign in with Google");
				resp.getWriter().print(req.getContextPath() + "/teacher");
				return;
			}
			
			//if a student signs in with google
			String assignmentRef = req.getParameter("assignment_ref");
			PartnerToAssistments assignment = null;
			try {
				assignment = AssignmentServiceImpl.getFromAssignmentRef(LiteUtility.PARTNER_REF, assignmentRef);
			} catch (ReferenceNotFoundException e1) {
				ErrorLogController.addNewError(e1, LiteUtility.ERROR_SOURCE_TYPE);
				String errorMessage = "Sorry... There is something wrong with this link. The assignment doesn't exist!";
				String instruction = "If you entered the URL in by hand, double check that it is correct";
				LiteUtility.directToErrorPage(errorMessage, instruction, req, resp);
			}
			
			// create the student account
			String userName = firstName + "_" + lastName;
			User student = LiteUtility.populateStudentInfo(firstName, lastName, userName);
			String partnerExternalRef = "google_student" + userId;
//			List<String> studentRefAccessToken = null;
			String studentRef = null;
			String studentToken = null;
			try {
				ReferenceTokenPair pair = accServ.transferUser(student, LiteUtility.Direct_SCHOOL_REF, 
						partnerExternalRef, "", "StudentAccount");
				studentRef = pair.getExternalRef();
				studentToken = pair.getAccessToken();
			} catch(org.assistments.connector.exception.TransferUserException e) {
				String errorMessage = e.getMessage();
				String instruction = "The server seems to be unstable at this moment. Please take a break and try it again later.";
				LiteUtility.directToErrorPage(errorMessage, instruction, req, resp);
				return;
			}
			String teacherToken = assignment.getAssistmentsAccessToken();
			// get the student class which belongs to this teacher
			StudentClassService scs = new StudentClassServiceImpl(LiteUtility.PARTNER_REF, teacherToken);
			PartnerToAssistments stuClass = null;
			try {
				stuClass = scs.find(ColumnNames.ASSISTMENTS_ACCESS_TOKEN, teacherToken).get(0);
			} catch (ReferenceNotFoundException e) {
				ErrorLogController.addNewError(e, 1, LiteUtility.ERROR_SOURCE_TYPE);
				String errorMessage = "Sorry... There is something wrong with this link. The student class doesn't exist!";
				String instruction = "If you entered the URL in by hand, double check that it is correct";
				LiteUtility.directToErrorPage(errorMessage, instruction, req, resp);
			}
			String studentClassRef = stuClass.getAssistmentsExternalRefernce();
			//enroll student into the class
			scs.enrollStudent(studentRef, studentClassRef);
			
			//save url to student report
			String studentReportURL = Constants.ASSISSTments_URL+"external_tutor/student_class/report?partner_id="+LiteUtility.PARTNER_ID
					+"&class_ref="+studentClassRef+"&assignment_ref="+assignmentRef;
			ServletContext context = getServletContext();
			String studentReportId = LiteUtility.generateStudentReportId(studentRef, assignmentRef);
			context.setAttribute(studentReportId, studentReportURL);
			String onExit = LiteUtility.generateStudentReportURL(studentRef, assignmentRef);
//			String onExit = "http://csta14-5.cs.wpi.edu:8080/connector/studentReport";
			//have to encode url twice
			onExit = URLEncoder.encode(onExit, "UTF-8");
			onExit = URLEncoder.encode(onExit, "UTF-8");
			AssignmentService as = new AssignmentServiceImpl(LiteUtility.PARTNER_REF, studentToken);
			String logoUrl = LiteUtility.DIRECT_URL + "/images/direct_logo.gif";
			logoUrl = URLEncoder.encode(logoUrl, "UTF-8");
			String whiteLabeled = "true";
			String accountName = student.getDisplayName();
			String tutorURL = as.getAssignment(assignmentRef, onExit);
			String loginURL = Constants.LOGIN_URL;
			String addressToGo = String.format("%1$s?partner=%2$s&access=%3$s&on_success=%4$s&on_failure=%5$s", 
					loginURL, LiteUtility.PARTNER_REF, studentToken, tutorURL, LiteUtility.LOGIN_FAILURE);
			resp.getWriter().print(addressToGo);
		} else {
			String message = "Sorry. You failed to sign in Google!";
			resp.getWriter().print(message);
			resp.setStatus(203);
			return;
		}

	}
}

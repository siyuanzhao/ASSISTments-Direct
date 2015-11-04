package org.assistments.direct;

import java.io.IOException;
import java.net.URLEncoder;
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
import org.assistments.connector.exception.TransferUserException;
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

import com.google.api.client.http.HttpStatusCodes;

@WebServlet({"/sign_in_with_facebook"})
public class SignInWithFacebook extends HttpServlet {
	private static final long serialVersionUID = -5014015865556824034L;
	
	public SignInWithFacebook() {
		super();
	}
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doPost(req, resp);
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException ,IOException {
		//first we should check where the request comes from
		String userId = req.getParameter("user_id");
//		String email = req.getParameter("email");
		String firstName = req.getParameter("first_name");
		String lastName = req.getParameter("last_name");
		
		AccountService accServ = new AccountServiceImpl(LiteUtility.PARTNER_REF);
		
		String userName = firstName + "_" + lastName;
		HttpSession reqSession = req.getSession();
		//if teacher signs in with google
		if(req.getParameter("teacher") != null) {
			String problemSet = (String)reqSession.getAttribute("problem_set");
			String shareLinkRef = (String)reqSession.getAttribute("share_link_ref");
//			String problemSetName = (String)reqSession.getAttribute("problem_set_name");
//			String problemSetStr = (String)reqSession.getAttribute("problem_set_str");
			
			String thirdPartyId = "facebook_" + userId;
			String studentClassPartnerRef = thirdPartyId;
			String assignmentPartnerRef = thirdPartyId;
			String displayName = firstName + " " + lastName;
			User teacher = LiteUtility.populateTeacherInfo(firstName, lastName, displayName);
			String teacherRef = new String();
			String teacherToken = new String();
			try {
				ReferenceTokenPair pair = accServ.transferUser(teacher,
						LiteUtility.Direct_SCHOOL_REF, thirdPartyId, "", "");
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
			String stuClassRef = scs.createStudentClass(studentClassName, studentClassPartnerRef, "", teacherRef);
			
			// create class assignment
			AssignmentService assignmentServ = new 
					AssignmentServiceImpl(LiteUtility.PARTNER_REF, teacherToken);
			String assignmentRef = assignmentServ.createClassAssignment(problemSet, 
					stuClassRef, assignmentPartnerRef, "", stuClassRef);
			
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
//			reqSession.setAttribute("problem_set_name", problemSetName);
			reqSession.setAttribute("user", teacherRef);
			reqSession.setAttribute("email", thirdPartyId);
			reqSession.setAttribute("from", "facebook");
			reqSession.setAttribute("submit", "Sign in with Facebook");
			resp.getWriter().print(req.getContextPath() + "/teacher");
			
			return;
		}
		//Students start to do assignment
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
		User student = LiteUtility.populateStudentInfo(firstName, lastName, userName);
		String partnerExternalRef = "facebook_student" + userId;
		String studentRef = new String();
		String studentToken = new String();
		try {
			ReferenceTokenPair pair = accServ.transferUser(student, LiteUtility.Direct_SCHOOL_REF, partnerExternalRef, "", "StudentAccount");
			studentRef = pair.getExternalRef();
			studentToken = pair.getAccessToken();
		} catch(TransferUserException e) {
			String errorMessage = e.getMessage();
			String instruction = "The server seems to be unstable at this moment. Please take a break and try it again later.";
			LiteUtility.directToErrorPage(errorMessage, instruction, req, resp);
			return;
		}
		String teacherToken = assignment.getAssistmentsAccessToken();
		
		StudentClassService scs = new StudentClassServiceImpl(LiteUtility.PARTNER_REF, teacherToken);
		PartnerToAssistments stuClass = null;
		try {
			stuClass = scs.find(ColumnNames.ASSISTMENTS_ACCESS_TOKEN, teacherToken).get(0);
		} catch (ReferenceNotFoundException e) {
			String errorMessage = e.getMessage();
			String instruction = "The server seems to be unstable at this moment. Please take a break and try it again later.";
			LiteUtility.directToErrorPage(errorMessage, instruction, req, resp);
			return;
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
		//have to encode url twice
		onExit = URLEncoder.encode(onExit, "UTF-8");
		onExit = URLEncoder.encode(onExit, "UTF-8");
		//get tutor url
		AssignmentService amServ = new AssignmentServiceImpl(LiteUtility.PARTNER_REF, studentToken);
		String tutorURL = amServ.getAssignment(assignmentRef, onExit);
		String loginURL = Constants.LOGIN_URL;
		String addressToGo = String.format("%1$s?partner=%2$s&access=%3$s&on_success=%4$s&on_failure=%5$s", 
				loginURL, LiteUtility.PARTNER_REF, studentToken, tutorURL, LiteUtility.LOGIN_FAILURE);
		resp.setStatus(HttpStatusCodes.STATUS_CODE_OK);
		resp.getWriter().print(addressToGo);
	}
	
}

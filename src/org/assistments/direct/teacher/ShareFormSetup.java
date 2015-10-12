package org.assistments.direct.teacher;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Arrays;

import javax.mail.MessagingException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.codec.binary.Base32;
import org.assistments.connector.service.AccountService;
import org.assistments.connector.service.AssignmentService;
import org.assistments.connector.service.ShareLinkService;
import org.assistments.connector.service.StudentClassService;
import org.assistments.connector.service.impl.AccountServiceImpl;
import org.assistments.connector.service.impl.AssignmentServiceImpl;
import org.assistments.connector.service.impl.ShareLinkServiceImpl;
import org.assistments.connector.service.impl.StudentClassServiceImpl;
import org.assistments.direct.LiteUtility;
import org.assistments.direct.LoginInfo;
import org.assistments.direct.LoginInfo.LoginFrom;
import org.assistments.service.controller.ErrorLogController;
import org.assistments.service.domain.ReferenceTokenPair;
import org.assistments.service.domain.User;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;

@WebServlet({ "/ShareSetup", "/share-setup" })
public class ShareFormSetup extends HttpServlet {
	private static final long serialVersionUID = 974128139273564080L;
	static final String CLIENT_ID = "588893615069-3l8u6q8n9quf6ouaj1j9de1m4q24kb4k.apps.googleusercontent.com";
	
	public static final String LOGIN = "Log in";
	public static final String SIGN_UP = "Create Account";
	public static final String GET_LINKS = "Send";

	public ShareFormSetup() {
		super();
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doGet(req, resp);
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String option = req.getParameter("option");
		String userPartnerRef = "";
		String userPartnerToken = "";
		String studentClassPartnerRef = "";
		String assignmentPartnerRef = "";
		String email = "";
		String note = "";
		String submit = "";
		User teacherInfo = null;
		//check where the user comes from: form, google, facebook
		LoginFrom loginFrom = null;
		if(option == null || option == "" ) {
			//parameter is missing, redirect use to error page
			String message = "There is something wired with your request to the server.";
			String instruction = "Please give it a try again later";
			LiteUtility.directToErrorPage(message, instruction, req, resp);
		} else if ("form".equals(option) || "get_links".equals(option)){
			loginFrom = LoginFrom.FORM;
			// get parameters from request
			email = req.getParameter("email").trim().toLowerCase();
			String pwd = req.getParameter("password");
			submit = req.getParameter("submit");
			if(submit == null) { //prevent duplicate submit
				req.getRequestDispatcher("/404.jsp").forward(req, resp);
				return;
			}
			if("form".equals(option)) {
				pwd = LiteUtility.getHash(pwd);
			} else if("get_links".equals(option)) {
				pwd = "";
			}
			
			userPartnerRef = email;
			userPartnerToken = pwd;
			studentClassPartnerRef = email;
			assignmentPartnerRef = email;
			teacherInfo = LiteUtility.populateTeacherInfo("ASSISTmentsDirect", "Teacher", "Teacher");
		} else if("google".equals(option)) {
			loginFrom = LoginFrom.GOOGLE;
			//verify user
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
				email = payload.getEmail();
				note = email;
				userPartnerRef = "google_" + userId;
				studentClassPartnerRef = userPartnerRef;
				assignmentPartnerRef = userPartnerRef;
				String displayName = firstName + " " + lastName;
				teacherInfo = LiteUtility.populateTeacherInfo(firstName, lastName, displayName);
			}
		} else if("facebook".equals(option)) {
			loginFrom = LoginFrom.FACEBOOK;
			String userId = req.getParameter("user_id");
			userPartnerRef = "facebook_" + userId;
			studentClassPartnerRef = assignmentPartnerRef = userPartnerRef;
			email = req.getParameter("email");
			note = email;
			String firstName = req.getParameter("first_name");
			String lastName = req.getParameter("last_name");
			String displayName = firstName + " " + lastName;
			teacherInfo = LiteUtility.populateTeacherInfo(firstName, lastName, displayName);
		} else {
			String message = "There is something wired with your request to the server.";
			String instruction = "Please give it a try again later";
			LiteUtility.directToErrorPage(message, instruction, req, resp);
		}

		HttpSession reqSession = req.getSession();
		String userRef = new String();
		String accessToken = new String();
		String problemSet = (String)reqSession.getAttribute("problem_set");
		String shareLinkRef = (String)reqSession.getAttribute("share_link_ref");
		String problemSetName = (String)reqSession.getAttribute("problem_set_name");

		AccountService as = new AccountServiceImpl(LiteUtility.PARTNER_REF);
		
		ReferenceTokenPair pair;
		try {
			pair = as.transferUser(teacherInfo, LiteUtility.Direct_SCHOOL_REF,
					userPartnerRef, userPartnerToken, note);
			userRef = pair.getExternalRef();
			accessToken = pair.getAccessToken();
		} catch (org.assistments.connector.exception.TransferUserException e1) {
			e1.printStackTrace();
			ErrorLogController.addNewError(e1, LiteUtility.ERROR_SOURCE_TYPE);
		}
		
		// create a class for this teacher
		String studentClassName = "Class";
		StudentClassService scs = new StudentClassServiceImpl(LiteUtility.PARTNER_REF, accessToken);
		String studentClassRef = scs.transferStudentClass(studentClassName, studentClassPartnerRef, "", userRef);
		
		// create class assignment
		AssignmentService assignmentServ = new AssignmentServiceImpl(LiteUtility.PARTNER_REF, accessToken);

		String assignmentRef = assignmentServ.createClassAssignment(problemSet, studentClassRef, 
				assignmentPartnerRef, "", studentClassRef);
		
		Base32 base32 = new Base32();
		String reportRef = base32.encodeAsString(assignmentRef.getBytes());
			
		String teacherLink = LiteUtility.REPORT_LINK_PREFIX + "/" + reportRef;
		String studentLink = LiteUtility.ASSIGNMENT_LINK_PREFIX + "/" + assignmentRef;
		//store the association between share link and user
		ShareLinkService sls = new ShareLinkServiceImpl(LiteUtility.PARTNER_REF);
		sls.addExternalShareLink(shareLinkRef, accessToken, userPartnerRef, "", assignmentRef);
		
		// send out the email
		String html = "<div style=\"text-align: center; width: 75%;\">The assignment \"<b style=\"font-size: 120%;\">"
				+ problemSetName 
				+ "</b>\" has been created.<br><br>"
				+ "<b>Student Assignment Link</b><br>"
				+ "Give this link to your students. They will enter their name and do the assignment.<br>"
				+ studentLink + "<br><br>"
				+ "<b>Teacher Report Link</b><br>"
				+ "Use this link to see a report on how they did on the assignment<br>"
				+ teacherLink
				+ "</div><br><br>" + "The ASSISTments Direct Team";
		String subject = "[ASSISTments Direct] An Assignment Has been Created for You";
		try {
//			LiteUtility.sendEmail(email, subject, text);
			LiteUtility.sendHtmlEmail(email, subject, html);
		} catch (MessagingException e) {
			//we fail to send out email
			ErrorLogController.addNewError(e, 1, LiteUtility.ERROR_SOURCE_TYPE);
		}
		reqSession.setAttribute("student_link", studentLink);
		reqSession.setAttribute("teacher_link", teacherLink);

		LoginInfo loginInfo = new LoginInfo(userRef, accessToken, email, userPartnerRef, loginFrom);
		if("get_links".equals(option)) {
			req.getSession().setAttribute("email", email);
			resp.sendRedirect(req.getContextPath() + "/instruction.jsp");
		} else if ("form".equals(option)){
			reqSession.setAttribute(LiteUtility.LOGIN_INFO_ATTRIBUTE, loginInfo);
			resp.sendRedirect(req.getContextPath() + "/teacher");
		} else {
			reqSession.setAttribute(LiteUtility.LOGIN_INFO_ATTRIBUTE, loginInfo);
			resp.getWriter().print(req.getContextPath() + "/teacher");
//			resp.sendRedirect(req.getContextPath() + "/teacher");
			
		}
	}
	
	public static void main(String[] args) {
		String email = "zhaosiyuankiddy@gmail.com";
		// send out the email
		String html = "<div style=\"text-align: center; width: 75%;\">The assignment \"<b style=\"font-size: 120%;\">"
				+ "Words to numeral 2.NBT.A.3" + "</b>\" has been created.<br><br>" + "<b>Student Assignment Link</b><br>"
				+ "Give this link to your students. They will enter their name and do the assignment.<br>" 
				+ "http://www.assistmentsdirect.org/assignment/adb7d11938fc0e31b207c061fca06657"
				+ "<br><br>" + "<b>Teacher Report Link</b><br>"
				+ "Use this link to see a report on how they did on the assignment<br>" 
				+ "http://www.assistmentsdirect.org/assignment/adb7d11938fc0e31b207c061fca06657" + "</div><br><br>"
				+ "The ASSISTments Direct Team";
		String subject = "[ASSISTments Direct] An Assignment Has been Created for You";
		try {
			// LiteUtility.sendEmail(email, subject, text);
			LiteUtility.sendHtmlEmail(email, subject, html);
		} catch (MessagingException e) {
			// we fail to send out email
			ErrorLogController.addNewError(e, LiteUtility.ERROR_SOURCE_TYPE);
		}
	}
}

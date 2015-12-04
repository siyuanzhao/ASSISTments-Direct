package org.assistments.direct.teacher;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.assistments.connector.domain.PartnerToAssistments;
import org.assistments.connector.domain.PartnerToAssistments.ColumnNames;
import org.assistments.connector.exception.ReferenceNotFoundException;
import org.assistments.connector.service.AccountService;
import org.assistments.connector.service.impl.AccountServiceImpl;
import org.assistments.direct.LiteUtility;
import org.assistments.direct.LoginInfo;
import org.assistments.direct.LoginInfo.LoginFrom;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;

@WebServlet({ "/TeacherLogin", "/teacher_login" })
public class TeacherLogin extends HttpServlet {
	
	static final String CLIENT_ID = "588893615069-3l8u6q8n9quf6ouaj1j9de1m4q24kb4k.apps.googleusercontent.com";

	private static final long serialVersionUID = 4524996561917493950L;

	public TeacherLogin() {
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
		//first detect which language is in use
		ResourceBundle message = LiteUtility.detectLanguageInUse(req);
		
		HttpSession session = req.getSession();
		AccountService as = new AccountServiceImpl(LiteUtility.PARTNER_REF);
		LoginFrom loginFrom = null;
		if (req.getParameter("option") == null) { //sign in via form
			loginFrom = LoginFrom.FORM;
			String submit = req.getParameter("submit");
			if (submit == null) {
				req.getRequestDispatcher("/teacher_login.jsp").forward(req,
						resp);
				return;
			}
			String email = req.getParameter("email").toLowerCase();
			String password = req.getParameter("password");
			String usrPartnerRef = email;
			
			List<PartnerToAssistments> list;
			try {
				list = as.find(ColumnNames.PARTNER_EXTERNAL_REFERENCE, email);
				PartnerToAssistments user = list.get(0);
				if (user.getPartnerAccessToken().equals(
						LiteUtility.getHash(password))) {
					LoginInfo loginInfo = new LoginInfo(user.getAssistmentsExternalRefernce(), 
							user.getAssistmentsAccessToken(), email, 
							usrPartnerRef, loginFrom);
					session.setAttribute(LiteUtility.LOGIN_INFO_ATTRIBUTE, loginInfo);
					resp.sendRedirect("/direct/teacher");
				} else { // Wrong password
					String msg = message.getString("teacher_login.incorrect_password");
					req.setAttribute("email", email);
					req.setAttribute("message", msg);
					req.getRequestDispatcher("/teacher_login.jsp").forward(req,
							resp);
					return;
				}
			} catch (ReferenceNotFoundException e) {
				//This should never happen 
			}
			
		} else { //this request is sent via ajax
			String thirdPartyId = new String();
			String email = "";
			String from = new String();
			if("facebook".equals(req.getParameter("option").toString())) {
				thirdPartyId = "facebook_" + req.getParameter("user_id");
				from = "Facebook";
				loginFrom = LoginFrom.FACEBOOK;
				email = req.getParameter("email");
			} else if("google".equals(req.getParameter("option").toString())) {
				loginFrom = LoginFrom.GOOGLE;
				String idTokenString = req.getParameter("idtoken");
				from = "Google";
				HttpTransport transport = new NetHttpTransport();
				JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
				GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(transport, jsonFactory).
					setAudience(Arrays.asList(CLIENT_ID)).build();
				GoogleIdToken idToken = null;
				try {
					idToken = verifier.verify(idTokenString);
				} catch(GeneralSecurityException e) {
					e.printStackTrace();
					String msg = message.getString("teacher_login.failure_on_google");
					resp.getWriter().print(msg);
					resp.setStatus(203);
					return;
				}
				if(idToken != null) {
					Payload payload = idToken.getPayload();
					String userId = payload.getSubject();
					email = payload.getEmail();
					thirdPartyId = "google_" + userId;
				} else {
					String msg = message.getString("teacher_login.failure_on_google");
					resp.getWriter().print(msg);
					resp.setStatus(203);
					return;
				}
			}
			List<PartnerToAssistments> list;
			try {
				list = as.find(ColumnNames.PARTNER_EXTERNAL_REFERENCE, thirdPartyId);
				PartnerToAssistments user = list.get(0);
				
				LoginInfo loginInfo = new LoginInfo(user.getAssistmentsExternalRefernce(), 
						user.getAssistmentsAccessToken(), email, thirdPartyId, loginFrom);
				session.setAttribute(LiteUtility.LOGIN_INFO_ATTRIBUTE, loginInfo);
				resp.getWriter().print(req.getContextPath() + "/teacher");
			} catch (ReferenceNotFoundException e) {
				String msg = String.format(message.getString("teacher_login.account_not_found"), from);
				resp.getWriter().print(msg);
				resp.setStatus(203);
				return;
			}
		}
	}
}

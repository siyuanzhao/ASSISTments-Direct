package org.assistments.direct;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.assistments.connector.service.AccountService;
import org.assistments.connector.service.impl.AccountServiceImpl;
import org.assistments.service.controller.ErrorLogController;
import org.assistments.service.domain.ReferenceTokenPair;
import org.assistments.service.domain.User;

/**
 * Servlet implementation class AuthenticateAssistmentsAppsUser
 */
@WebServlet("/AuthenticateAssistmentsAppsUser")
public class AuthenticateAssistmentsAppsUser extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public AuthenticateAssistmentsAppsUser() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	@Override
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		String userId = request.getParameter("userId");
		String firstName = request.getParameter("firstName");
		String lastName = request.getParameter("lastName");
		String email = request.getParameter("email");
		String userPartnerRef = "google_" + userId;
		String studentClassPartnerRef = userPartnerRef;
		String assignmentPartnerRef = userPartnerRef;
		String displayName = firstName + " " + lastName;
		User teacherInfo = LiteUtility.populateTeacherInfo(firstName, lastName,
				displayName);

		AccountService as = new AccountServiceImpl(LiteUtility.PARTNER_REF);

		ReferenceTokenPair pair;
		HttpSession session = request.getSession();
		try {
			pair = as.transferUser(teacherInfo, LiteUtility.Direct_SCHOOL_REF,
					userPartnerRef, "", email);
			session.setAttribute("external_ref", pair.getExternalRef());
		} catch (org.assistments.connector.exception.TransferUserException e1) {
			e1.printStackTrace();
			ErrorLogController
					.addNewError(e1, LiteUtility.ERROR_SOURCE_TYPE);
		}
		
		PrintWriter out = response.getWriter();
		out.write("true");
	}

}

package org.assistments.direct;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet({"/login_failure"})
public class LoginFailure extends HttpServlet {
	private static final long serialVersionUID = 3767939031976304521L;

	public LoginFailure() {
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
		String errorCode = req.getParameter("error_code");
		if(errorCode != null) {
			LiteUtility.directToErrorPage(errorCode, null, req, resp);
		} else {
			errorCode = "There was an error during the process of your login";
			String instrcution = "There must be something wrong with ASSISTments.org. Please give it a try again!";
			LiteUtility.directToErrorPage(errorCode, instrcution, req, resp);
		}
	}
}

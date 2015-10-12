package org.assistments.direct.student;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.assistments.direct.LiteUtility;

public class Assignment extends HttpServlet {

	private static final long serialVersionUID = -7769113299387297956L;

	public Assignment() {
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
		LiteUtility.detectLanguageInUse(req);
		String pathInfo = req.getPathInfo();
		String[] params = pathInfo.split("/");
		String accessCode = params[1];
		req.setAttribute("assignment_ref", accessCode);
		RequestDispatcher dispatcher = req.getRequestDispatcher("/MyAssignment.jsp");
		dispatcher.forward(req, resp);
	}	
	
}

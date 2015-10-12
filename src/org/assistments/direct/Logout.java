package org.assistments.direct;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet({ "/Logout", "/logout" })
public class Logout extends HttpServlet {
	private static final long serialVersionUID = -4557273898364994246L;

	public Logout() {
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
		HttpSession session = req.getSession();
		session.removeAttribute(LiteUtility.LOGIN_INFO_ATTRIBUTE);
		resp.sendRedirect(req.getContextPath() + "/teacher");
	}
}

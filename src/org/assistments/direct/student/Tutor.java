package org.assistments.direct.student;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet({ "/tutor" })
public class Tutor extends HttpServlet {
	private static final long serialVersionUID = -5652598130883121919L;
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doPost(req, resp);
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		HttpSession session = req.getSession();

//		String tutorLink = session.getAttribute("tutor_link").toString();
//		String stuName = session.getAttribute("student_name").toString();
		String tutorLink = req.getParameter("tutor_link").toString();
		String stuName = req.getParameter("student_name").toString();
		session.setAttribute("tutor_link", tutorLink);
		session.setAttribute("student_name", stuName);
		req.getRequestDispatcher("/tutor.jsp").forward(req, resp);
	}

}

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
		if(session.getAttribute("notice_to_students") == null) {
			//do nothing
		} else {
//			String studentReportUrl = session.getAttribute("student_report").toString();
			session.setAttribute("tutor_link", "student_report.jsp");
//			req.setAttribute("refresh_tag", true);
		}
		
		req.getRequestDispatcher("/tutor.jsp").forward(req, resp);
	}

}

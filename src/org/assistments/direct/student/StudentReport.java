package org.assistments.direct.student;

import java.io.IOException;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.assistments.connector.service.AssignmentService;
import org.assistments.connector.service.impl.AssignmentServiceImpl;
import org.assistments.connector.utility.Response;
import org.assistments.direct.LiteUtility;
import org.assistments.direct.edmodo.EdmodoApp;
import org.assistments.edmodo.controller.EdmodoAssignmentController;
import org.assistments.edmodo.utility.ApplicationSettings;
import org.assistments.service.controller.impl.AssignmentControllerWebImpl;

import com.google.gson.Gson;


@WebServlet({ "/studentReport", "/StudentReport" })
public class StudentReport extends HttpServlet {
	private static final long serialVersionUID = -3765332785755296446L;
	
	public StudentReport() {
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
		ServletContext context = getServletContext();
		String studentRef = req.getParameter("student_ref");
		String assignmentRef = req.getParameter("assignment_ref");
		String studentReportId = LiteUtility.generateStudentReportId(studentRef, assignmentRef);
		String studentReportURL = "";
		if(context.getAttribute(studentReportId) == null) {
			RequestDispatcher dispatcher = req.getRequestDispatcher("/assignment_finished.jsp");
			dispatcher.forward(req, resp);
			return;
		}
		
		HttpSession session = req.getSession();
		String from = req.getParameter("from");
		if(from != null) {
			if("google_classroom".equals(from)) {
				studentReportURL = context.getAttribute(studentReportId).toString();
				context.removeAttribute(studentReportId);
				String noticeToStudents = "You have completed this problem set. Return to Classroom, open the assignment and mark as done.";
				session.setAttribute("notice_to_students", noticeToStudents);
			} else if("edmodo".equalsIgnoreCase(from)) {
				String noticeToStudents = "You have completed this problem set. Please return to Edmodo.";
				session.setAttribute("notice_to_students", noticeToStudents);
				Map<String, String> map = (Map<String, String>)context.getAttribute(studentReportId);
				//submit student's grade and turn in assignment
				Response r = AssignmentControllerWebImpl.getGrade(studentRef, assignmentRef, ApplicationSettings.partner_id);
				Map jsonObject = new Gson().fromJson(r.getContent(), Map.class);
				String score = jsonObject.get("score").toString();
				String total = "100";
				String userToken = map.get("edmodoUserToken");
				String accessToken = map.get("edmodoAccessToken");
				studentReportURL = map.get("reportUrl");
				String assignmentId = map.get("edmodoAssignmentId");
				EdmodoAssignmentController eac = new EdmodoAssignmentController(EdmodoApp.API_KEY, 
						userToken, accessToken);
				eac.turnInAssignment(assignmentId);
				eac.setGrade(assignmentId, score, total);
			}
		} else {
			session.setAttribute("notice_to_students", "");
		}
		session.setAttribute("student_report", studentReportURL);
//		resp.sendRedirect(studentReportURL);
		String url = LiteUtility.DIRECT_URL + "/tutor";
		resp.sendRedirect(url);
	}
	
}

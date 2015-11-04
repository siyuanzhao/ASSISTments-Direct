package org.assistments.direct.student;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.assistments.connector.service.ProblemSetService;
import org.assistments.connector.service.ReportService;
import org.assistments.connector.service.impl.ProblemSetServiceImpl;
import org.assistments.connector.service.impl.ReportServiceImpl;
import org.assistments.connector.utility.Response;
import org.assistments.direct.LiteUtility;
import org.assistments.direct.edmodo.EdmodoApp;
import org.assistments.edmodo.controller.EdmodoAssignmentController;
import org.assistments.edmodo.utility.ApplicationSettings;
import org.assistments.service.controller.impl.AssignmentControllerWebImpl;
import org.assistments.service.domain.ProblemSet;
import org.assistments.service.domain.StudentReportEntry;

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
		ProblemSetService pss = new ProblemSetServiceImpl();
		ProblemSet ps = pss.findByAssignment(assignmentRef);
		ReportService rs = new ReportServiceImpl();
		List<StudentReportEntry> entryList = rs.generateStudentReport(studentRef, assignmentRef);
		HttpSession session = req.getSession();
		session.setAttribute("report_entries", entryList);
		session.setAttribute("problem_set_name", ps.getName());
		
		String studentReportId = LiteUtility.generateStudentReportId(studentRef, assignmentRef);
		String from = req.getParameter("from");
		if(from != null) {
			if("google_classroom".equals(from)) {
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
				String assignmentId = map.get("edmodoAssignmentId");
				EdmodoAssignmentController eac = new EdmodoAssignmentController(EdmodoApp.API_KEY, 
						userToken, accessToken);
				eac.turnInAssignment(assignmentId);
				eac.setGrade(assignmentId, score, total);
			}
		} else {
			session.setAttribute("notice_to_students", "");
		}
		
		req.getRequestDispatcher("student_report.jsp").forward(req, resp);
	}
	
}

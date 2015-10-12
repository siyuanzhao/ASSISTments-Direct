package org.assistments.direct.teacher;

import java.io.IOException;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.codec.binary.Base32;
import org.assistments.connector.domain.PartnerToAssistments;
import org.assistments.connector.domain.PartnerToAssistments.ColumnNames;
import org.assistments.connector.exception.ReferenceNotFoundException;
import org.assistments.connector.service.AccountService;
import org.assistments.connector.service.AssignmentLogService;
import org.assistments.connector.service.AssignmentService;
import org.assistments.connector.service.ProblemSetService;
import org.assistments.connector.service.impl.AccountServiceImpl;
import org.assistments.connector.service.impl.AssignmentLogServiceImpl;
import org.assistments.connector.service.impl.AssignmentServiceImpl;
import org.assistments.connector.service.impl.ProblemSetServiceImpl;
import org.assistments.connector.utility.Constants;
import org.assistments.direct.LiteUtility;
import org.assistments.domain.Assignment;
import org.assistments.service.controller.ErrorLogController;
import org.assistments.service.domain.ProblemSet;
import org.assistments.service.domain.User;

@WebServlet({ "/report/*", "/Report/*" })
public class Report extends HttpServlet {

	private static final long serialVersionUID = 828888353846628643L;

	public Report() {
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

		String pathInfo = req.getPathInfo();
		pathInfo = pathInfo.substring(1);
		String reportRef = pathInfo;
		
		if(reportRef == null) {
			String errorMessage = "It seems that the web page you just typed in doesn't exist.";
			String instruction = "If you entered the URL in by hand, double check that it is correct";
			RequestDispatcher dispatcher = req.getRequestDispatcher("/error.jsp");
			req.setAttribute("error_message", errorMessage);
			req.setAttribute("instruction", instruction);
			dispatcher.forward(req, resp);
		}
		
		Base32 base32 = new Base32();
		String assignmentRef = new String(base32.decode(reportRef));

		PartnerToAssistments externalAssignment = null;
		try {
			externalAssignment = AssignmentServiceImpl.getFromAssignmentRef(LiteUtility.PARTNER_REF, assignmentRef);
		} catch (ReferenceNotFoundException e1) {
			String errorMessage = "It seems that the web page you just typed in doesn't exist.";
			String instruction = "If you entered the URL in by hand, double check that it is correct";
			RequestDispatcher dispatcher = req.getRequestDispatcher("/error.jsp");
			req.setAttribute("error_message", errorMessage);
			req.setAttribute("instruction", instruction);
			dispatcher.forward(req, resp);
		}
		
		//from assignment reference to get problem set
		ProblemSetService pss = new ProblemSetServiceImpl();
//		ProblemSet ps = pss.findByAssignment(assignmentRef);
//		
//		String problemSetName = ps.getName();
//		String problemSetId = String.valueOf(ps.getDecodedID());
		String token = externalAssignment.getAssistmentsAccessToken();
		
		AssignmentService as = new AssignmentServiceImpl(LiteUtility.PARTNER_REF, token);
		Assignment assignment = null;
		try {
			assignment = as.find(assignmentRef);
		} catch (ReferenceNotFoundException e1) {
			e1.printStackTrace();
			ErrorLogController.addNewError(e1, LiteUtility.ERROR_SOURCE_TYPE);
		}
		ProblemSet ps = pss.find((int)assignment.getProblemSetId());
		String problemSetName = ps.getName();
		String problemSetId = String.valueOf(ps.getDecodedID());
		long studentClassId = assignment.getClassId();
		
		long assignmentId = assignment.getId();
		
		AssignmentLogService als = new AssignmentLogServiceImpl();
		List<User> notStartedStudents = als.getNotStartedStudents(assignmentRef);
		List<User> inProgressStudents = als.getInProgressStudents(assignmentRef);
		List<User> completeStudents = als.getCompleteStudents(assignmentRef);
		
		String viewProblemsLink = LiteUtility.DIRECT_URL + "/view_problems/" + assignment.getProblemSetId();
		HttpSession session = req.getSession();
		session.setAttribute("student_class_id", studentClassId);
		session.setAttribute("report_ref", reportRef);
		session.setAttribute("problem_set_name", problemSetName);
		session.setAttribute("problem_set_id", problemSetId);
		session.setAttribute("assignment_id", assignmentId);
		session.setAttribute("view_problems_link", viewProblemsLink);
		session.setAttribute("not_started_students", notStartedStudents);
		session.setAttribute("in_progress_students", inProgressStudents);
		session.setAttribute("complete_students", completeStudents);
		//TODO: Change URL to test1 or production
//		String onSuccess = Constants.ASSISSTments_URL + "external_teacher/student_class/assignment?ref=" + assignmentRef;
		String onSuccess = Constants.ASSISSTments_URL + "teacher/report/mastery_status/"+studentClassId+"?assignment_id="+assignmentId;
		String onFailure = "assistments.org";
//		String loginURL = "http://csta14-5.cs.wpi.edu:3000/api2_helper/user_login";
		String loginURL = Constants.LOGIN_URL;

		AccountService accService = new AccountServiceImpl(LiteUtility.PARTNER_REF);
		PartnerToAssistments user = null;
		try {
			user = accService.find(ColumnNames.ASSISTMENTS_ACCESS_TOKEN, token).get(0);
		} catch (ReferenceNotFoundException e) {
			e.printStackTrace();
		}

		String reportLink = String.format("%1$s?partner=%2$s&access=%3$s&on_success=%4$s&on_failure=%5$s", 
				loginURL, LiteUtility.PARTNER_REF, user.getAssistmentsAccessToken(), onSuccess, onFailure);
		session.setAttribute("report_link", reportLink);
//		resp.sendRedirect(resp.encodeRedirectURL(addressToGo));
		req.getRequestDispatcher("/report_link.jsp").forward(req, resp);

	}
}

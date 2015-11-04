package org.assistments.direct.student;

import java.io.IOException;
import java.net.URLEncoder;

import javax.servlet.ServletContext;
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
import org.assistments.connector.service.AssignmentService;
import org.assistments.connector.service.StudentClassService;
import org.assistments.connector.service.impl.AccountServiceImpl;
import org.assistments.connector.service.impl.AssignmentServiceImpl;
import org.assistments.connector.service.impl.StudentClassServiceImpl;
import org.assistments.connector.utility.Constants;
import org.assistments.connector.utility.LocalhostSettings;
import org.assistments.direct.LiteUtility;
import org.assistments.service.domain.ReferenceTokenPair;
import org.assistments.service.domain.User;

@WebServlet({ "/beginAssignment", "/BeginAssignment" })
public class BeginAssignment extends HttpServlet {

	private static final long serialVersionUID = -8487938278071103716L;

	public BeginAssignment() {
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
		String firstName = req.getParameter("first_name").toLowerCase();
		String lastName = req.getParameter("last_name").toLowerCase();
		String assignmentRef = req.getParameter("assignment_ref");
		
		PartnerToAssistments assignment = null;
		try {
			assignment = AssignmentServiceImpl.getFromAssignmentRef(LiteUtility.PARTNER_REF, assignmentRef);
		} catch (ReferenceNotFoundException e) {
			String errorMessage = "It seems that the web page you just typed in doesn't exist.";
			String instruction = "If you entered the URL in by hand, double check that it is correct";
			LiteUtility.directToErrorPage(errorMessage, instruction, req, resp);
		}
		String teacherRef = new String();
		String studentClassRef = new String();
		String token = assignment.getAssistmentsAccessToken();
		//get student class reference
		StudentClassService scs = new StudentClassServiceImpl(LiteUtility.PARTNER_REF, token);
		PartnerToAssistments studentClass = null;
		try {
			studentClass = scs.find(ColumnNames.ASSISTMENTS_ACCESS_TOKEN, token).get(0);
		} catch (ReferenceNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		studentClassRef = studentClass.getAssistmentsExternalRefernce();
		
		//find teacher account
		AccountService as = new AccountServiceImpl(LiteUtility.PARTNER_REF);
		PartnerToAssistments teacher = null;
		try {
			teacher = as.find(ColumnNames.ASSISTMENTS_ACCESS_TOKEN, token).get(0);
		} catch (ReferenceNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		teacherRef = teacher.getAssistmentsExternalRefernce();
		firstName = firstName.substring(0, 1).toUpperCase()+firstName.substring(1);
		lastName = lastName.substring(0, 1).toUpperCase() + lastName.substring(1);
		String fullName = firstName + " " + lastName;
		//create student account
		User student = LiteUtility.populateStudentInfo(firstName, lastName);
		String partnerExternalRef = student.getDisplayName() +"_" +teacherRef;
		ReferenceTokenPair pair = null;
		try {
			pair = as.transferUser(student, LiteUtility.Direct_SCHOOL_REF, partnerExternalRef, "", "Student Account");
		} catch (org.assistments.connector.exception.TransferUserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		String studentRef = pair.getExternalRef();
		String stuBehalf = pair.getAccessToken();
		//enroll student
		scs.enrollStudent(studentRef, studentClassRef);
		//double encoded
		String onExit = LiteUtility.generateStudentReportURL(studentRef, assignmentRef);
		onExit = URLEncoder.encode(onExit, "UTF-8");
		onExit = URLEncoder.encode(onExit, "UTF-8");
		//get url to invoke the tutor
		AssignmentService assignmentServ = new AssignmentServiceImpl(LiteUtility.PARTNER_REF, stuBehalf);
		String logoUrl = LiteUtility.DIRECT_URL + "/images/direct_logo.gif";
		logoUrl = URLEncoder.encode(logoUrl, "UTF-8");
//		String whiteLabeled = "true";
//		String accountName = student.getDisplayName();
		String tutorURL = assignmentServ.getAssignment(assignmentRef, onExit);
		tutorURL = URLEncoder.encode(Constants.ASSISSTments_URL, "UTF-8") + tutorURL;
		String loginURL = Constants.LOGIN_URL;
		String addressToGo = String.format("%1$s?partner=%2$s&access=%3$s&on_success=%4$s&on_failure=%5$s", 
				loginURL, LiteUtility.PARTNER_REF, stuBehalf, tutorURL, LiteUtility.LOGIN_FAILURE);
		HttpSession session = req.getSession();
		
		session.setAttribute("tutor_link", addressToGo);
		session.setAttribute("student_name", fullName);
		session.removeAttribute("notice_to_students");
		//save url to student report
		String studentReportURL = Constants.ASSISSTments_URL+"external_tutor/student_class/report?partner_id="+LiteUtility.PARTNER_ID
				+"&class_ref="+studentClassRef+"&assignment_ref="+assignmentRef;
 
		String studentReportId = LiteUtility.generateStudentReportId(studentRef, assignmentRef);
		ServletContext context = getServletContext();
		context.setAttribute(studentReportId, studentReportURL);
//		resp.sendRedirect(addressToGo);
//		req.getRequestDispatcher("tutor.jsp").forward(req, resp);
		resp.sendRedirect("tutor");
	}
}

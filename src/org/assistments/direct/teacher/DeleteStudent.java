package org.assistments.direct.teacher;

import java.io.IOException;

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
import org.assistments.connector.service.impl.AccountServiceImpl;
import org.assistments.direct.LiteUtility;

/**
 * Servlet implementation class DeleteStudent
 */
@WebServlet({ "/DeleteStudent", "/delete_student" })
public class DeleteStudent extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public DeleteStudent() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		int studentId = Integer.parseInt(request.getParameter("studentId"));
		int sectionId = Integer.parseInt(request.getParameter("sectionId"));
		
		String teacherRef = new String();
		HttpSession session = request.getSession();
		String email = new String();
		if(session.getAttribute("email") == null || session.getAttribute("user") == null) {
			request.getRequestDispatcher("/teacher_login.jsp").forward(request, response);
			return;
		} else {
			email = session.getAttribute("email").toString();
		}
		String teacherPartnerExternalRef = email;
		AccountService as = new AccountServiceImpl(LiteUtility.PARTNER_REF);
		PartnerToAssistments user;
		try {
			user = as.find(ColumnNames.PARTNER_EXTERNAL_REFERENCE, email).get(0);
			teacherRef = user.getAssistmentsExternalRefernce();
		} catch (ReferenceNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
//		the following codes need to be modified after the release of new API
//		String studentDisplayName = StudentClassController.getStudentDisplayName(studentId);
//		String partnerExternalRef = studentDisplayName+"_"+teacherRef;
//		StudentClassController.deleteStudent(studentId, sectionId, partnerExternalRef);
		
	}

}

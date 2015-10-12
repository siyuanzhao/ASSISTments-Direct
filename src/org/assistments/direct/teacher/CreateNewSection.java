package org.assistments.direct.teacher;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.assistments.connector.domain.PartnerToAssistments.ColumnNames;
import org.assistments.connector.exception.ReferenceNotFoundException;
import org.assistments.connector.service.StudentClassService;
import org.assistments.connector.service.impl.StudentClassServiceImpl;
import org.assistments.direct.LiteUtility;
import org.assistments.direct.LoginInfo;

@WebServlet({"/create_new_section"})
public class CreateNewSection extends HttpServlet {
	private static final long serialVersionUID = 2904053874798376770L;

	public CreateNewSection() {
		super();
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		//first check if the user already login
		HttpSession session = req.getSession();
		String userPartnerRef = new String();
		String userRef = new String();
		String accessToken = new String();
		LoginInfo loginInfo = null;
		if(session.getAttribute(LiteUtility.LOGIN_INFO_ATTRIBUTE) == null) {
			req.getRequestDispatcher("/teacher_login.jsp").forward(req, resp);
			return;
		} else {
			loginInfo = (LoginInfo) session.getAttribute(LiteUtility.LOGIN_INFO_ATTRIBUTE);
		}
		userPartnerRef = loginInfo.getPartnerExternalRef();
		accessToken = loginInfo.getAccessToken();
		userRef = loginInfo.getUserRef();
		
		String sectionName = req.getParameter("section_name");
		//check if section name already exists
		
		//create new section
		StudentClassService scs = new StudentClassServiceImpl(LiteUtility.PARTNER_REF, accessToken);
		String classRef = null;
		try {
			classRef = scs.find(ColumnNames.PARTNER_EXTERNAL_REFERENCE, userPartnerRef).get(0).getAssistmentsExternalRefernce();
		} catch (ReferenceNotFoundException e) {
			//this should not happen since each teacher should have a section at least
			new RuntimeException(e);
		}
		String sectionRef = scs.createNewClassSection(classRef, sectionName, userPartnerRef, "", userRef);
		
	}
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doPost(req, resp);
	}
}

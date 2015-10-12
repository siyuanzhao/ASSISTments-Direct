package org.assistments.direct.teacher;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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
import org.assistments.connector.service.ShareLinkService;
import org.assistments.connector.service.StudentClassService;
import org.assistments.connector.service.impl.AccountServiceImpl;
import org.assistments.connector.service.impl.ShareLinkServiceImpl;
import org.assistments.connector.service.impl.StudentClassServiceImpl;
import org.assistments.direct.LiteUtility;
import org.assistments.direct.LoginInfo;
import org.assistments.direct.teacher.Roster.SectionInfo;
import org.assistments.service.controller.impl.ShareLinkControllerDAOImpl;
import org.assistments.service.domain.ShareLink;
import org.assistments.service.domain.User;

@WebServlet({ "/Teacher", "/teacher" })
public class Teacher extends HttpServlet {
	private static final long serialVersionUID = -8603316572284984324L;

	public Teacher() {
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
		HttpSession session = req.getSession();
		String userPartnerRef = new String();
		LoginInfo loginInfo = null;
//		ShareLinkControllerDAOImpl.addCols();
		if(session.getAttribute(LiteUtility.LOGIN_INFO_ATTRIBUTE) == null) {
			req.getRequestDispatcher("/teacher_login.jsp").forward(req, resp);
			return;
		} else {
			loginInfo = (LoginInfo) session.getAttribute(LiteUtility.LOGIN_INFO_ATTRIBUTE);
		}
		userPartnerRef = loginInfo.getPartnerExternalRef();
		//get this teacher's assignments
		List<Map<String, String>> assignmentsInfo = new ArrayList<Map<String, String>>();
		ShareLinkService sls = new ShareLinkServiceImpl(LiteUtility.PARTNER_REF);
		List<PartnerToAssistments> shareLinks = null;
		try {
			shareLinks = sls.find(ColumnNames.PARTNER_EXTERNAL_REFERENCE, userPartnerRef);
		} catch (ReferenceNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Base32 base32 = new Base32();
		
		Collections.sort(shareLinks, Collections.reverseOrder());
		Iterator<PartnerToAssistments> ite = shareLinks.iterator();
		while (ite.hasNext()) {
			PartnerToAssistments externalLink = ite.next();
			ShareLink link = null;
			try {
				link = sls.find(externalLink.getAssistmentsExternalRefernce());
			} catch (ReferenceNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Map<String, String> info = new HashMap<String, String>();
			String encodedID = LiteUtility.encodeProblemSetId(link.getProblemSet().getDecodedID());
			String tmpStudentLink = LiteUtility.ASSIGNMENT_LINK_PREFIX + "/" + externalLink.getNote();
			String tmpTeacherLink = LiteUtility.REPORT_LINK_PREFIX + "/"
					+ base32.encodeAsString(externalLink.getNote().getBytes());
			String classroomLink = LiteUtility.DIRECT_URL + "/google_classroom/" + externalLink.getNote();
			info.put("problem_set_name", link.getProblemSet().getName());
			info.put("problem_set_id", encodedID);
			info.put("student_link", tmpStudentLink);
			info.put("teacher_link", tmpTeacherLink);
			info.put("classroom_link", classroomLink);
			String linkURL = LiteUtility.DIRECT_URL + "/share/" + externalLink.getAssistmentsExternalRefernce();
			info.put("share_link", linkURL);
			assignmentsInfo.add(info);
		}
		session.setAttribute("assignments", assignmentsInfo);
		/*
		AccountService accServ = new AccountServiceImpl(LiteUtility.PARTNER_REF);
		PartnerToAssistments teacher = null;
		try {
			teacher = accServ.find(ColumnNames.PARTNER_EXTERNAL_REFERENCE, userPartnerRef).get(0);
		} catch (ReferenceNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			//code should stop right here
			return;
		}
		
		String accessToken =  teacher.getAssistmentsAccessToken();
		//find all class sections which belong to the teacher
		StudentClassService scs = new StudentClassServiceImpl(LiteUtility.PARTNER_REF, accessToken);
		List<PartnerToAssistments> sectionRefs = new ArrayList<PartnerToAssistments>();
		try {
			sectionRefs = scs.find(ColumnNames.PARTNER_EXTERNAL_REFERENCE, userPartnerRef);
		} catch (ReferenceNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			//code should stop right here
			return;
		}
		//get all students in each section
		Iterator<PartnerToAssistments> iter = sectionRefs.iterator();
		Roster roster = new Roster();
		List<SectionInfo> sectionInfos = new ArrayList<SectionInfo>();
		while(iter.hasNext()) {
			SectionInfo info = roster.new SectionInfo();
			PartnerToAssistments section = iter.next();
			String sectionRef = section.getAssistmentsExternalRefernce();
			info.setSectionRef(sectionRef);
			String sectionName = scs.getSectionName(sectionRef);
			info.setSectionName(sectionName);
			
			List<String> studentRefs = scs.getMembeship(sectionRef);
			Iterator<String> studentIter = studentRefs.iterator();
			List<User> students = new ArrayList<User>();
			while(studentIter.hasNext()) {
				String ref = studentIter.next();
				User user = accServ.getUserProfile(ref);
				students.add(user);
			}
			info.setStudents(students);
			sectionInfos.add(info);
		}
		roster.setSections(sectionInfos);
		session.setAttribute("roster", roster);
		*/
		RequestDispatcher dispatcher = req.getRequestDispatcher("/teacher.jsp");
		
		dispatcher.forward(req, resp);
	}
}

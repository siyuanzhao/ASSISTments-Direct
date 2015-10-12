package org.assistments.direct.teacher;

import java.io.IOException;
import java.net.URL;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.assistments.connector.exception.ReferenceNotFoundException;
import org.assistments.connector.service.ShareLinkService;
import org.assistments.connector.service.impl.ShareLinkServiceImpl;
import org.assistments.direct.LiteUtility;
import org.assistments.service.domain.ProblemSet;
import org.assistments.service.domain.ShareLink;
import org.assistments.service.domain.User;

@WebServlet({ "/share/*", "/Share/*" })
public class ShareProblemSet extends HttpServlet {
	private static final long serialVersionUID = -678301132130219044L;
	

	public ShareProblemSet() {
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
		LiteUtility.detectLanguageInUse(req);
		String pathInfo = req.getPathInfo();
		pathInfo = pathInfo.substring(1);
		ShareLinkService sls = new ShareLinkServiceImpl(LiteUtility.PARTNER_REF);
		String shareLinkRef = pathInfo;
		ShareLink shareLink = null;
		try {
			shareLink = sls.find(shareLinkRef);
		} catch (ReferenceNotFoundException e) {
			String errorMgs = "Sorry... The share link doesn't exist!";
			req.setAttribute("error_message", errorMgs);
			String instruction = "If you entered the URL in by hand, double check that it is correct";
			RequestDispatcher dispatcher = req
					.getRequestDispatcher("/error.jsp");
			req.setAttribute("instruction", instruction);
			dispatcher.forward(req, resp);
			return;
		}
		ProblemSet problemSet = shareLink.getProblemSet();
		User distributor = shareLink.getDistributor();
			
		String encodedID = LiteUtility.encodeProblemSetId(problemSet.getDecodedID());
		HttpSession session = req.getSession();
//		String defaultLocale = "en_US";
//		if(session.getAttribute("locale") == null) {
//			session.setAttribute("locale", defaultLocale);
//		}
		String viewProblemLink = LiteUtility.DIRECT_URL + "/view_problems/" + problemSet.getDecodedID();
		session.setAttribute("problem_set", String.valueOf(problemSet.getDecodedID()));
		session.setAttribute("problem_set_str", encodedID);
		session.setAttribute("problem_set_name", problemSet.getName());
		session.setAttribute("distributer_email", distributor.getEmail());
		session.setAttribute("distributer_name", distributor.getDisplayName());
		session.setAttribute("share_link_ref", shareLinkRef);
		session.setAttribute("view_problem_link", viewProblemLink);
		
		session.removeAttribute("url");
		String spreadSheetUrl = shareLink.getUrl();
		String spreadSheetId = new String();
		if(!LiteUtility.isNullOrEmpty(spreadSheetUrl)){
			spreadSheetUrl = spreadSheetUrl.substring(0, spreadSheetUrl.lastIndexOf('/'));
			spreadSheetId = spreadSheetUrl.substring(spreadSheetUrl.lastIndexOf('/')+1);
			String newSpreadSheetUrl = "https://spreadsheets.google.com/feeds/cells/"+spreadSheetId+"/od6/public/values?alt=json";
			session.setAttribute("spreadsheetId", spreadSheetId);
			session.setAttribute("url", newSpreadSheetUrl);
		}
		
		session.setAttribute("form", shareLink.getForm());
		session.setAttribute("recipient", shareLink.getRecipient());
		session.setAttribute("assistments_verified", shareLink.isAssistmentsVerified());
		
		URL url = LiteUtility.getCustomizedImgURL();
		session.setAttribute("customizedImgURL", url.toString()); 
//		session.removeAttribute("email");
//		session.removeAttribute("message");
		RequestDispatcher dispatcher = req
				.getRequestDispatcher("/share.jsp");
		dispatcher.forward(req, resp);

	}
}

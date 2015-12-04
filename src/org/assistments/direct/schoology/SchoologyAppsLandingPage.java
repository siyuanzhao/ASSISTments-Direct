package org.assistments.direct.schoology;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.assistments.connector.exception.ReferenceNotFoundException;
import org.assistments.connector.exception.TransferUserException;
import org.assistments.service.controller.ErrorLogController;

import com.google.gson.JsonObject;
/**
 * Servlet implemeation class SchoologyAppsLandingPage
 */

@WebServlet("/SchoologyAppsLandingPage")
public class SchoologyAppsLandingPage extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     our_api_secret")
                .build();
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
//		String session_id = request.getParameter("realm_id");
//		String header = request.getHeader("referer");
//		URL url = new URL(header);
//		String domain = url.getHost();
//		String authUrl = null;
//		SchoologyAPI sapi = new SchoologyAPI("https://"+domain, LiteUtility.DIRECT_URL);
//		try {
//			authUrl = sapi.retrieveRequestToken();
//		} catch (OAuthMessageSignerException | OAuthNotAuthorizedException | OAuthExpectationFailedException
//				| OAuthCommunicationException e) {
//			System.out.println("OAuthRequestToken cannot be generated");
//			e.printStackTrace();
//		}
//		response.sendRedirect(authUrl);
//		OAuthConsumer consumer = new DefaultOAuthConsumer(SchoologyAPI.Consumer_Key, SchoologyAPI.Consumer_Secret);
		//System.setProperty("debug", "debug");
		HttpSession session = request.getSession();
		SchoologyAPI sapi = (SchoologyAPI)session.getAttribute("sapi");
		String session_id = request.getParameter("realm_id");

//		String uid = (String)session.getAttribute("uid");
//		String requestTokenEndpointUrl = SchoologyAPI.Api_Base +"/oauth/request_token?uid="+uId;
//		String accessTokenEndpointUrl = SchoologyAPI.Api_Base+"/oauth/access_token";
//		String authorizationWebsiteUrl = new String();
//		String json = new String();
//		JsonObject jObject = new JsonObject();
//		try {
//			jObject = sapi.getUserProfile(); 
//			JsonObject jObjectClasses = sapi.getListOfClasses(uid);
//			String sectionID = "373873319";
//	  		String courseID = "373873317";
//	  		String assignmentID = "380509057";
//	  		String enrollmentID = "1372768385";
//	  		String newAssignmentID = "404520243";
//	  		sapi.postGrade(sectionID, enrollmentID, newAssignmentID);
//	  		sapi.postAssignment(sectionID, courseID);
//	  		sapi.getListOfAssignments(sectionID);
//	  		sapi.getListOfEnrollments(sectionID);
//			System.out.println(json);
	  		
//		} catch (Exception e) {
//			ErrorLogController.addNewError(e, "Direct");
//		}
//		try {
//			sapi.connectAssistmentsUser( jObject);
//		} catch (TransferUserException | ReferenceNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		session.setAttribute("json", json);
		
		if(sapi.isTeacher())
		{
			session.setAttribute("teacherName", sapi.getUser().getFirstName()+" " +sapi.getUser().getLastName());
			request.getRequestDispatcher("schoology_apps_landing_page.jsp").forward(request, response);
		}
		else
		{
			session.setAttribute("studentName", sapi.getUser().getFirstName()+" " +sapi.getUser().getLastName());
			request.getRequestDispatcher("schoology_student_view.jsp").forward(request, response);
		}
	}
}

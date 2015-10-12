package org.assistments.direct.schoology;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.assistments.service.controller.ErrorLogController;
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
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
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
		System.setProperty("debug", "debug");
		HttpSession session = request.getSession();
		SchoologyAPI sapi = (SchoologyAPI)session.getAttribute("sapi");
		String uid = (String)session.getAttribute("uid");
//		String requestTokenEndpointUrl = SchoologyAPI.Api_Base +"/oauth/request_token?uid="+uId;
//		String accessTokenEndpointUrl = SchoologyAPI.Api_Base+"/oauth/access_token";
//		String authorizationWebsiteUrl = new String();
		String json = new String();
		try {
			sapi.retrieveAccessToken();
			sapi.getUserProfile(uid);
//			System.out.println(json);
		} catch (Exception e) {
			ErrorLogController.addNewError(e, "Direct");
		}
		
		session.setAttribute("json", json);
		request.getRequestDispatcher("schoology_apps_landing_page.jsp").forward(request, response);
		
	}
}

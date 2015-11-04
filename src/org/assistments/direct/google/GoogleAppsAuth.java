package org.assistments.direct.google;

import java.io.IOException;
import java.util.Arrays;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;
import com.google.api.client.extensions.servlet.auth.oauth2.AbstractAuthorizationCodeServlet;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;

/**
 * Servlet implementation class GoogleAppsLandingPage
 */
@WebServlet("/google_apps_auth")
public class GoogleAppsAuth extends AbstractAuthorizationCodeServlet implements Servlet{
	private static final long serialVersionUID = 1L;
	private final String CLIENT_ID = "757036402283-8o3nu8pdve8snhj8ds11te8bnsrnmuu6.apps.googleusercontent.com";
	private final String CLIENT_SECRET = "hxaYp2ZY3RN3a1Zmpb3kd-G0";
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GoogleAppsAuth() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//		doPost(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//request.getRequestDispatcher("google_apps_landing_page.jsp").forward(request, response);
	}

	@Override
	protected String getRedirectUri(HttpServletRequest request)
			throws ServletException, IOException {
		GenericUrl url = new GenericUrl(request.getRequestURL().toString());
	    url.setRawPath("/direct/GoogleAppsCallback");
	    return url.build();
	}

	@Override
	protected String getUserId(HttpServletRequest arg0)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected AuthorizationCodeFlow initializeFlow() throws ServletException,
			IOException {
		return new GoogleAuthorizationCodeFlow.Builder(
		        new NetHttpTransport(), JacksonFactory.getDefaultInstance(),
		        CLIENT_ID, CLIENT_SECRET,
		        Arrays.asList("https://www.googleapis.com/auth/classroom.rosters.readonly",
		        		"https://www.googleapis.com/auth/classroom.courses",
		        		"https://www.googleapis.com/auth/classroom.profile.emails")).build();
	}

}

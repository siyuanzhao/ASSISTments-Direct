package org.assistments.direct.google;

import java.io.IOException;
import java.util.Arrays;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.assistments.direct.LiteUtility;

import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.servlet.auth.oauth2.AbstractAuthorizationCodeCallbackServlet;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;

@WebServlet("/GoogleAppsCallback")
public class GoogleAppsAuthCallback extends AbstractAuthorizationCodeCallbackServlet {
	
	private final String CLIENT_ID = "757036402283-8o3nu8pdve8snhj8ds11te8bnsrnmuu6.apps.googleusercontent.com";
	private final String CLIENT_SECRET = "hxaYp2ZY3RN3a1Zmpb3kd-G0";

	private static final long serialVersionUID = 5425888646797912799L;

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
	protected void onSuccess(HttpServletRequest req, HttpServletResponse resp,
			Credential credential) throws ServletException, IOException {
		String url = LiteUtility.DIRECT_URL + "/GoogleAppsLandingPage";
		resp.sendRedirect(url);
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

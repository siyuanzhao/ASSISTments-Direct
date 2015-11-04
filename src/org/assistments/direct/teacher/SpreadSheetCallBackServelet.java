package org.assistments.direct.teacher;

import java.io.IOException;
import java.util.Collections;

import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;
import com.google.api.client.auth.oauth2.AuthorizationCodeResponseUrl;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.servlet.auth.oauth2.AbstractAuthorizationCodeCallbackServlet;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;

/**
 * Servlet implementation class SpreadSheetCallBackServelet
 */
@WebServlet("/SpreadSheetCallBackServelet")
public class SpreadSheetCallBackServelet extends AbstractAuthorizationCodeCallbackServlet {
	private static final long serialVersionUID = 1L;
	private final String CLIENT_ID = "868469417184-ava3g9j9t1c5q4ntif5qgr36v0m2pis4.apps.googleusercontent.com";
	private final String CLIENT_SECRET = "MZHG7lHHH36w3Fp4UXY70Xzt";
    /**
     * @see AbstractAuthorizationCodeCallbackServlet#AbstractAuthorizationCodeCallbackServlet()
     */
    public SpreadSheetCallBackServelet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see Servlet#init(ServletConfig)
	 */
	@Override
	public void init(ServletConfig config) throws ServletException {
		// TODO Auto-generated method stub
	}

	@Override
	protected void onError(HttpServletRequest req, HttpServletResponse resp,
			AuthorizationCodeResponseUrl errorResponse)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		System.out.println("cancel authorization");
		resp.sendRedirect("/direct/teacher");
	}

	@Override
	protected void onSuccess(HttpServletRequest req, HttpServletResponse resp,
			Credential credential) throws ServletException, IOException {
		// TODO Auto-generated method stub
		System.out.println("authorization accepted");
		HttpSession session = req.getSession();
		session.setAttribute("Google_access_token", credential.getAccessToken());
		resp.sendRedirect("/direct/LoadSpreadSheets");
	}

	@Override
	protected String getRedirectUri(HttpServletRequest req)
			throws ServletException, IOException {
		GenericUrl url = new GenericUrl(req.getRequestURL().toString());
	    url.setRawPath("/direct/SpreadSheetCallBackServelet");
	    return url.build();
	}

	@Override
	protected String getUserId(HttpServletRequest req)
			throws ServletException, IOException {
		HttpSession session = req.getSession();
		return session.getAttribute("email").toString();
	}

	@Override
	protected AuthorizationCodeFlow initializeFlow() throws ServletException,
			IOException {
		return new GoogleAuthorizationCodeFlow.Builder(
		        new NetHttpTransport(), JacksonFactory.getDefaultInstance(),
		        CLIENT_ID, CLIENT_SECRET,
		        Collections.singleton("https://spreadsheets.google.com/feeds")).build();
	}

}

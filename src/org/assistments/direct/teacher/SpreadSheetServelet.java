package org.assistments.direct.teacher;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;
import com.google.api.client.extensions.servlet.auth.oauth2.AbstractAuthorizationCodeServlet;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;

/**
 * Servlet implementation class SpreadSheetServelet
 */
@WebServlet("/SpreadSheetServelet")
public class SpreadSheetServelet extends AbstractAuthorizationCodeServlet implements Servlet {
	private static final long serialVersionUID = 1L;
	private final String CLIENT_ID = "868469417184-ava3g9j9t1c5q4ntif5qgr36v0m2pis4.apps.googleusercontent.com";
	private final String CLIENT_SECRET = "MZHG7lHHH36w3Fp4UXY70Xzt";
	Set<String> scopes = new HashSet<String>();
    /**
     * @see AbstractAuthorizationCodeServlet#AbstractAuthorizationCodeServlet()
     */
    public SpreadSheetServelet() {
        super();
        // TODO Auto-generated constructor stub
    }

	
	@Override
	public void init(ServletConfig config) throws ServletException {
		// TODO Auto-generated method stub
		scopes.add("https://spreadsheets.google.com/feeds");
		scopes.add("https://docs.google.com/feeds");
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		System.out.println("it gets here!");
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

	@Override
	protected String getRedirectUri(HttpServletRequest request)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		GenericUrl url = new GenericUrl(request.getRequestURL().toString());
	    url.setRawPath("/direct/SpreadSheetCallBackServelet");
	    return url.build();
	}

	@Override
	protected String getUserId(HttpServletRequest request)
			throws ServletException, IOException {
		HttpSession session = request.getSession();
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

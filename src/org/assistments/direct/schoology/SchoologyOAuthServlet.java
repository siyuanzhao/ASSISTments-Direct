package org.assistments.direct.schoology;

import java.io.IOException;
import java.net.URL;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.assistments.direct.LiteUtility;

import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;
import oauth.signpost.exception.OAuthNotAuthorizedException;
/**
 * Servlet implemeation class SchoologyAppsLandingPage
 */

@WebServlet("/SchoologyOAuthServlet")
public class SchoologyOAuthServlet extends HttpServlet {
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
		
		String session_id = request.getParameter("realm_id");
		String header = request.getHeader("referer");
		URL url = new URL(header);
		String domain = url.getHost();
		String authUrl = null;
		SchoologyAPI sapi = new SchoologyAPI("https://"+domain, LiteUtility.DIRECT_URL+"/SchoologyAppsLandingPage", session_id);
		try {
			authUrl = sapi.retrieveRequestToken();
		} catch (OAuthMessageSignerException | OAuthNotAuthorizedException | OAuthExpectationFailedException
				| OAuthCommunicationException e) {
			System.out.println("OAuthRequestToken cannot be generated");
			e.printStackTrace();
		}
		HttpSession session = request.getSession();
		session.setAttribute("sapi", sapi);
		session.setAttribute("uid", session_id);
		response.sendRedirect(authUrl);		
	}
}

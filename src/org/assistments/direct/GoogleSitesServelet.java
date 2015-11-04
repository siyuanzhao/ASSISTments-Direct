package org.assistments.direct;

import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;
import com.google.api.client.extensions.servlet.auth.oauth2.AbstractAuthorizationCodeServlet;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;

import java.io.IOException;
import java.util.Collections;

import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Servlet implementation class GoogleSitesServelet
 */
@WebServlet("/GoogleSitesServelet")
public class GoogleSitesServelet extends AbstractAuthorizationCodeServlet implements Servlet {
	private static final long serialVersionUID = 1L;
	private final String CLIENT_ID = "757036402283-8o3nu8pdve8snhj8ds11te8bnsrnmuu6.apps.googleusercontent.com";
	private final String CLIENT_SECRET = "hxaYp2ZY3RN3a1Zmpb3kd-G0";
    /**
     * @see AbstractAuthorizationCodeServlet#AbstractAuthorizationCodeServlet()
     */
    public GoogleSitesServelet() {
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

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

	@Override
	protected String getRedirectUri(HttpServletRequest request)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		GenericUrl url = new GenericUrl(request.getRequestURL().toString());
//		StringBuilder redirectParams = new StringBuilder();
//		redirectParams.append("?owner_id=").append(request.getAttribute("distributor_id"))
//					  .append("&folder_id=").append(request.getAttribute("folder_id"))
//					  .append("&site_name=").append(request.getAttribute("site_name"))
//					  .append("&link_type=").append(request.getAttribute("link_type"))
//					  .append("&assistments_verified=").append(request.getAttribute("assistments_verified"))
//					  .append("&form=").append(request.getAttribute("form"))
//					  .append("&url=").append(request.getAttribute("url"))
//					  .append("&from=").append(request.getAttribute("from"));
	    url.setRawPath("/direct/GoogleSitesCallBackServlet");
	    return url.build();
	}

	@Override
	protected String getUserId(HttpServletRequest request)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		HttpSession session = request.getSession();
		return "307910";
//		return session.getAttribute("distributor_id").toString();
	}

	@Override
	protected AuthorizationCodeFlow initializeFlow() throws ServletException,
			IOException {
		// TODO Auto-generated method stub
		return new GoogleAuthorizationCodeFlow.Builder(
		        new NetHttpTransport(), JacksonFactory.getDefaultInstance(),
		        CLIENT_ID, CLIENT_SECRET,
		        Collections.singleton("https://sites.google.com/feeds/")).build();
	}

}

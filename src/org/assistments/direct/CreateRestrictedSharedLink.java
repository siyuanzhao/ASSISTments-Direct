package org.assistments.direct;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.assistments.connector.service.ShareLinkService;
import org.assistments.connector.service.impl.ShareLinkServiceImpl;
import org.assistments.service.domain.ShareLink;

import com.google.gson.JsonObject;

/**
 * Servlet implementation class CreateRestrictedSharedLink
 */
@WebServlet("/CreateRestrictedSharedLink")
public class CreateRestrictedSharedLink extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public CreateRestrictedSharedLink() {
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
		String url = request.getParameter("url");
		String form = request.getParameter("form");
		System.out.println(url);
		String problemSetId = request.getParameter("problem_set_id");
		String distributorId = request.getParameter("distributor_id");
		String assistmentsVerified = request.getParameter("assistments_verified");
		boolean isAssistmentsVerified = new Boolean(assistmentsVerified);
//		Long problemSetIdLong = Long.valueOf(problemSetId);
//		Long distributorIdLong = Long.valueOf(distributorId);
		String restrictedGenericLink = new String();
		
		//create generic share link
		ShareLinkService sls  = new ShareLinkServiceImpl(LiteUtility.PARTNER_REF);
		String shareLinkRef = sls.create(distributorId, problemSetId, ShareLink.GENERIC, true, url, form, isAssistmentsVerified);
		
		restrictedGenericLink = LiteUtility.DIRECT_URL + "/share/" + shareLinkRef;
		PrintWriter out = response.getWriter();
		JsonObject json = new JsonObject();
		json.addProperty("generic_link", restrictedGenericLink);
		out.write(json.toString());
		out.flush();
		out.close();
	}

}

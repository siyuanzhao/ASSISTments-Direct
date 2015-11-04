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
 * Servlet implementation class CreateGenericLink
 */
@WebServlet("/CreateGenericLink")
public class CreateGenericLink extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public CreateGenericLink() {
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
		String problemSetId = request.getParameter("problem_set_id");
		String distributorId = request.getParameter("distributor_id");
		String genericLink = new String();
		
		ShareLinkService sls = new ShareLinkServiceImpl(LiteUtility.PARTNER_REF);
		String genericRef = sls.create(distributorId, problemSetId, ShareLink.GENERIC, true);
		genericLink = LiteUtility.DIRECT_URL + "/share/" + genericRef;
		
		PrintWriter out = response.getWriter();
		JsonObject json = new JsonObject();
		json.addProperty("generic_link", genericLink);
		out.write(json.toString());
		out.flush();
		out.close();
	}

}

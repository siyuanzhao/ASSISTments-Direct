package org.assistments.direct;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.assistments.connector.service.ProblemSetService;
import org.assistments.connector.service.ShareLinkService;
import org.assistments.connector.service.impl.ProblemSetServiceImpl;
import org.assistments.connector.service.impl.ShareLinkServiceImpl;
import org.assistments.service.domain.ShareLink;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

/**
 * Servlet implementation class GetSkillBuilderShareLinks
 */
@WebServlet("/GetSkillBuilderShareLinks")
public class GetSkillBuilderShareLinks extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GetSkillBuilderShareLinks() {
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
		PrintWriter out = response.getWriter();
		long folderId = Long.parseLong(request.getParameter("folder_id"));
		String from = request.getParameter("from");
		ProblemSetService pss = new ProblemSetServiceImpl();
		JsonArray problemSets = pss.getProblemSetsByFolder(folderId);
		ShareLinkService sls = new ShareLinkServiceImpl(LiteUtility.PARTNER_REF);
		if(from.equals("share_links")){
			
			for(int i=0;i<problemSets.size();i++){
				JsonObject folderItem = problemSets.get(i).getAsJsonObject();
				if(folderItem.get("type").getAsString().equals("CurriculumItem")){
					String problemSetId = folderItem.get("id").getAsString();
					String shareLinkRef = sls.create(LiteUtility.DISTRIBUTOR_ID, problemSetId, ShareLink.GENERIC, true);
					String shareLink = LiteUtility.DIRECT_URL + "/share/" + shareLinkRef;
					folderItem.addProperty("share_link", shareLink);
				}else{
					JsonArray curriculumItems = folderItem.get("problem_sets").getAsJsonArray();
					for (int j = 0; j < curriculumItems.size(); j++){
						JsonObject curriculumItem = curriculumItems.get(j).getAsJsonObject();
						String problemSetId = curriculumItem.get("id").getAsString();
						String shareLinkRef = sls.create(LiteUtility.DISTRIBUTOR_ID, problemSetId, ShareLink.GENERIC, true);
						String shareLink = LiteUtility.DIRECT_URL + "/share/" + shareLinkRef;
						curriculumItem.addProperty("share_link", shareLink);
					}
				}
			}
		}
		
//		request.setAttribute("problemSets", problemSets);
//		request.getRequestDispatcher("skill_builder_share_links.jsp").forward(request, response);
		out.write(problemSets.toString());
		out.flush();
		out.close();
	}

}

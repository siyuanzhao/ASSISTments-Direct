package org.assistments.direct;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.assistments.connector.service.ProblemSetService;
import org.assistments.connector.service.impl.ProblemSetServiceImpl;

/**
 * Servlet implementation class SkillBuilderGoogleClassroom
 */
@WebServlet("/SkillBuilderGoogleClassroom")
public class SkillBuilderGoogleClassroom extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SkillBuilderGoogleClassroom() {
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
		HttpSession session = request.getSession();
		String externalRef = (String)session.getAttribute("external_ref");
		if(externalRef == null){
			request.getRequestDispatcher("google_apps_landing_page.jsp").forward(request, response);
			return;
		}
		String toolType = request.getParameter("tool_type");
		session.setAttribute("tool_type", toolType);
		String folderId = request.getParameter("folder_id");
		ProblemSetService pss = new ProblemSetServiceImpl();
		List<Map<String, String>> folders = pss.getSubFoldersByFolderId(Integer.parseInt(folderId));
//		Integer[] folderIds = new Integer[]{186686, 177818, 177817, 177556, 177557, 177558, 177559, 177560, 177568, 198227, 198228};
//		List<Integer> folderIdList = Arrays.asList(folderIds);
		if(folders.size() != 0){
			//subfolder exists
			session.setAttribute("folders", folders);
			request.getRequestDispatcher("skill_builder_google_classroom.jsp").forward(request, response);
		}else{
//			String folderName = pss.getFolderNameById(Long.parseLong(folderId));
			List<Integer> folderIds = new ArrayList<Integer>();
			folderIds.add(Integer.parseInt(folderId));
			List<Map<String,String>> folderRoot = pss.getFoldersByIds(folderIds);
			Map<String,String> folder = folderRoot.get(0);
			session.setAttribute("folder", folder);
			request.getRequestDispatcher("ap_google_classroom.jsp").forward(request, response);
		}
		
	}

}

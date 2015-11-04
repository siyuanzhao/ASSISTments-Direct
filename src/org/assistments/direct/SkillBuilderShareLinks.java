package org.assistments.direct;

import java.io.IOException;
import java.util.Arrays;
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
 * Servlet implementation class SkillBuilderShareLinks
 */
@WebServlet("/SkillBuilderShareLinks")
public class SkillBuilderShareLinks extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SkillBuilderShareLinks() {
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
		Integer[] folderIds = new Integer[]{186686, 177818, 177817, 177556, 177557, 177558, 177559, 177560, 177568, 198227, 198228};
		List<Integer> folderIdList = Arrays.asList(folderIds);
		ProblemSetService pss = new ProblemSetServiceImpl();
		List<Map<String,String>> folders = pss.getFoldersByIds(folderIdList);
		HttpSession session = request.getSession();
		session.setAttribute("folders", folders);
		request.getRequestDispatcher("skill_builder_share_links.jsp").forward(request, response);
	}

}

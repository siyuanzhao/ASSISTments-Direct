package org.assistments.direct.teacher;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONValue;

/**
 * Servlet implementation class PlaceInGroup
 */
@WebServlet({ "/PlaceInGroup", "/place_in_group" })
public class PlaceInGroup extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public PlaceInGroup() {
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
		List<String> studentIds = (List<String>) JSONValue.parse(request.getParameter("studentIds"));
		List<String> oldStudentClassSectionIds = (List<String>) JSONValue.parse(request.getParameter("oldStudentClassSectionIds"));
		int newStudentClassSectionId = Integer.parseInt(request.getParameter("newStudentClassSectionId"));
		for (int i=0;i<studentIds.size();i++){
			int studentId = Integer.parseInt(studentIds.get(i));
			int oldStudentClassSectionId = Integer.parseInt(oldStudentClassSectionIds.get(i));
//			StudentClassController.placeInGroup(studentId, oldStudentClassSectionId, newStudentClassSectionId);
		}
	}

}

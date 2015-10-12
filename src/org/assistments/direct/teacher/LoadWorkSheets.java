package org.assistments.direct.teacher;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.gdata.data.spreadsheet.SpreadsheetEntry;
import com.google.gdata.data.spreadsheet.WorksheetEntry;
import com.google.gdata.util.ServiceException;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

/**
 * Servlet implementation class LoadWorkSheets
 */
@WebServlet("/LoadWorkSheets")
public class LoadWorkSheets extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public LoadWorkSheets() {
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
		String spreadsheetId = request.getParameter("spreadsheetId");
		HttpSession session = request.getSession();
		List<SpreadsheetEntry> spreadsheets = (List<SpreadsheetEntry>)session.getAttribute("spreadsheets");
		
		for(SpreadsheetEntry spreadsheet : spreadsheets){
			if (spreadsheet.getId().equals(spreadsheetId)){
				try {
					List<WorksheetEntry> worksheets = spreadsheet.getWorksheets();
					session.setAttribute("worksheets", worksheets);
					PrintWriter out = response.getWriter();
					JsonArray jsonSheets = new JsonArray();
					for (int i =0;i<worksheets.size();i++){
						JsonObject sheet = new JsonObject();
						sheet.addProperty("id", worksheets.get(i).getId());
						sheet.addProperty("name", worksheets.get(i).getTitle().getPlainText());
						jsonSheets.add(sheet);
					}
					out.write(jsonSheets.toString());
					out.flush();
					out.close();
				} catch (ServiceException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

}

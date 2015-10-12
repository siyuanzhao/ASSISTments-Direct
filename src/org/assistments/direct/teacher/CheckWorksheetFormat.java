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

import com.google.api.client.auth.oauth2.BearerToken;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.gdata.client.spreadsheet.SpreadsheetService;
import com.google.gdata.data.spreadsheet.WorksheetEntry;
import com.google.gson.JsonObject;

/**
 * Servlet implementation class CheckWorksheetFormat
 */
@WebServlet("/CheckWorksheetFormat")
public class CheckWorksheetFormat extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public CheckWorksheetFormat() {
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
		HttpSession session = request.getSession();
		SpreadsheetService service =new SpreadsheetService("ASSISTmentsDirect");
		String accessToken = session.getAttribute("Google_access_token").toString();
		service.setOAuth2Credentials(new Credential(BearerToken
	            .authorizationHeaderAccessMethod())
	            .setFromTokenResponse(new TokenResponse().setAccessToken(accessToken)));
		String worksheetId = request.getParameter("worksheetId");
		List<WorksheetEntry> worksheets = (List<WorksheetEntry>)session.getAttribute("worksheets");
		JsonObject jsonResult = new JsonObject();
		for (WorksheetEntry worksheet : worksheets){
			if(worksheetId.equals(worksheet.getId())){
				int colCount;
				
				if(worksheet.getColCount()==2){
					jsonResult.addProperty("result", "true");
				}else{
					jsonResult.addProperty("result", "false");
				}
				break;
			}
		}
		out.write(jsonResult.toString());
		out.flush();
		out.close();
	}

}

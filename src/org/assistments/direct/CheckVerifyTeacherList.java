package org.assistments.direct;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gdata.client.spreadsheet.SpreadsheetService;
import com.google.gdata.data.spreadsheet.CellEntry;
import com.google.gdata.data.spreadsheet.CellFeed;
import com.google.gdata.data.spreadsheet.SpreadsheetEntry;
import com.google.gdata.data.spreadsheet.SpreadsheetFeed;
import com.google.gdata.data.spreadsheet.WorksheetEntry;
import com.google.gdata.util.ServiceException;
import com.google.gson.JsonObject;

/**
 * Servlet implementation class CheckVerifyTeacherList
 */
@WebServlet("/CheckVerifyTeacherList")
public class CheckVerifyTeacherList extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public CheckVerifyTeacherList() {
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
		JsonObject json = new JsonObject();
		String email = request.getParameter("email");
		String spreadsheetId = request.getParameter("spreadsheetId");
		String path = getServletContext().getRealPath("");
		try {
			SpreadsheetService service = LiteUtility.getSpreadsheetService(path);
			URL feedUrl = new URL("https://spreadsheets.google.com/feeds/spreadsheets/private/full");
			SpreadsheetFeed feed = service.getFeed(feedUrl, SpreadsheetFeed.class);
			List<SpreadsheetEntry> spreadsheets = feed.getEntries();
			String spreadSheetIdUrl = "https://spreadsheets.google.com/feeds/spreadsheets/"+spreadsheetId;
			boolean flag = false;
			for(SpreadsheetEntry spreadsheet : spreadsheets){
				if(spreadsheet.getId().equals(spreadSheetIdUrl)){
					flag = true;
					json.addProperty("result", "true");
					
					List<WorksheetEntry> worksheets = spreadsheet.getWorksheets();
					//get the first worksheet by default, this should be modified later
					WorksheetEntry worksheet = worksheets.get(0);
					URL cellFeedUrl = worksheet.getCellFeedUrl();
					CellFeed cellFeed = service.getFeed(cellFeedUrl, CellFeed.class);
					boolean emailflag = false;
					for(CellEntry cell : cellFeed.getEntries()){
						if(email.equals(cell.getPlainTextContent().toString())){
							emailflag = true;
							json.addProperty("verified_email", "true");
							out.write(json.toString());
							out.flush();
							out.close();
							return;
						}
					}
					//handle the case that the spreadsheet is still shared to the domain user but the email is not listed in the spreadsheet
					if(!emailflag){
						json.addProperty("verified_email", "false");
						out.write(json.toString());
						out.flush();
						out.close();
						return;
					}
					
					break;
				}
			}
			//handle the case that the spreadsheet is no longer shared to the domain user
			if(!flag){
				json.addProperty("result", "false");
				out.write(json.toString());
				out.flush();
				out.close();
				return;
			}
			
		} catch (GeneralSecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return;
	}

}

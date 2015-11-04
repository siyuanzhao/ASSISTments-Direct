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
import com.google.gdata.data.spreadsheet.SpreadsheetEntry;
import com.google.gdata.data.spreadsheet.SpreadsheetFeed;
import com.google.gdata.util.ServiceException;
import com.google.gson.JsonObject;

/**
 * Servlet implementation class CheckSpreadsheetUrl
 */
@WebServlet("/CheckSpreadsheetUrl")
public class CheckSpreadsheetUrl extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final String SPREADSHEET_URL_PATTERN = "https://docs.google.com/spreadsheets/d/";
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public CheckSpreadsheetUrl() {
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
		PrintWriter out = response.getWriter();
		JsonObject json = new JsonObject();
		String url = request.getParameter("url");
		if(!url.startsWith(SPREADSHEET_URL_PATTERN)||!url.contains("edit#gid=")) {
			json.addProperty("result", "false");
			json.addProperty("reason", "invalid_url");
			out.write(json.toString());
			out.flush();
			out.close();
			return;
		}
		String spreadSheetUrl = url.substring(0, url.lastIndexOf('/'));
		String spreadSheetId = spreadSheetUrl.substring(spreadSheetUrl.lastIndexOf('/')+1);
		String spreadSheetIdUrl = "https://spreadsheets.google.com/feeds/spreadsheets/"+spreadSheetId;
		String path = getServletContext().getRealPath("");
		try {
			
			SpreadsheetService service = LiteUtility.getSpreadsheetService(path);
			URL feedUrl = new URL(
					"https://spreadsheets.google.com/feeds/spreadsheets/private/full");
			SpreadsheetFeed feed;
			feed = service.getFeed(feedUrl, SpreadsheetFeed.class);
			List<SpreadsheetEntry> spreadsheets = feed.getEntries();
			boolean flag = false;
			for(SpreadsheetEntry spreadsheet : spreadsheets){
				if(spreadsheet.getId().equals(spreadSheetIdUrl)){
					flag = true;
					break;
				}
			}
			if(!flag){
				String newSpreadSheetUrl = "https://spreadsheets.google.com/feeds/cells/"+spreadSheetId+"/od6/public/values?alt=json";
				json.addProperty("jsonUrl", newSpreadSheetUrl);
				json.addProperty("result", "false");
				json.addProperty("reason", "not_share");
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
		json.addProperty("result", "true");
		out.write(json.toString());
		out.flush();
		out.close();
	}

}

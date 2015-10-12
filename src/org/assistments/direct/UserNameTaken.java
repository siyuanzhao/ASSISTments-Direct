package org.assistments.direct;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.assistments.connector.domain.PartnerToAssistments;
import org.assistments.connector.domain.PartnerToAssistments.ColumnNames;
import org.assistments.connector.exception.ReferenceNotFoundException;
import org.assistments.connector.service.AccountService;
import org.assistments.connector.service.impl.AccountServiceImpl;

import com.google.gson.JsonObject;

@WebServlet({ "/isUserNameTaken" })
public class UserNameTaken extends HttpServlet {

	private static final long serialVersionUID = 2631728906468613401L;
	
	public UserNameTaken() {
		super();
	}
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doPost(req, resp);
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String email = req.getParameter("email");
//		ExternalUserDAO userDAO = new ExternalUserDAO(LiteUtility.PARTNER_REF);
		AccountService as = new AccountServiceImpl(LiteUtility.PARTNER_REF);
		
		PartnerToAssistments user;
//		JSONObject json = new JSONObject();
		JsonObject json = new JsonObject();
		try {
			user = as.find(ColumnNames.PARTNER_EXTERNAL_REFERENCE, email).get(0);
			
			if(user.getPartnerAccessToken() != null && !user.getPartnerAccessToken().equals("")) {
//				json.put("result", "true");
				json.addProperty("result", "true");
			} else {
				json.addProperty("result", "false");
			}
		} catch (ReferenceNotFoundException e) {
			json.addProperty("result", "false");
		}
		PrintWriter pw = resp.getWriter();
		pw.write(json.toString());
		pw.flush();
		pw.close(); 
	}

}

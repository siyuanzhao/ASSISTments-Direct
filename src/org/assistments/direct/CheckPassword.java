package org.assistments.direct;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ResourceBundle;

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

@WebServlet({ "/CheckPassword", "/check_password" })
public class CheckPassword extends HttpServlet {

	private static final long serialVersionUID = 4310974466946366147L;
	
	public CheckPassword() {
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
		ResourceBundle message = LiteUtility.detectLanguageInUse(req);
		
		String email = req.getParameter("email");
		String password = req.getParameter("password");
		resp.setCharacterEncoding("utf-8");
		PrintWriter pw = resp.getWriter();
		JsonObject json = new JsonObject();
		
		AccountService as = new AccountServiceImpl(LiteUtility.PARTNER_REF);
		
		PartnerToAssistments user;
		try {
			user = as.find(ColumnNames.PARTNER_EXTERNAL_REFERENCE, email).get(0);
			if(LiteUtility.getHash(password).equals(user.getPartnerAccessToken())) {
				json.addProperty("result", "true");
			} else {
				json.addProperty("result", "wrong");
				json.addProperty("message", message.getString("share.wrong_password"));
			}
		} catch (ReferenceNotFoundException e) {
			json.addProperty("result", "wrong");
			json.addProperty("message", message.getString("share.wrong_password"));
		}
		
		pw.write(json.toString());
		pw.flush();
		pw.close();
	}

}

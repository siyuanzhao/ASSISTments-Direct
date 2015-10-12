package org.assistments.direct;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.assistments.connector.domain.PartnerToAssistments;
import org.assistments.connector.domain.PartnerToAssistments.ColumnNames;
import org.assistments.connector.exception.ReferenceNotFoundException;
import org.assistments.connector.service.AccountService;
import org.assistments.connector.service.impl.AccountServiceImpl;

@WebServlet({ "/ResetPassword", "/reset_password" })
public class ResetPassword extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public ResetPassword() {
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
		HttpSession session = req.getSession();

		String submit = req.getParameter("submit");
		String email = req.getParameter("email");
		
		AccountService as = new AccountServiceImpl(LiteUtility.PARTNER_REF);

		if (submit == null) {	//user tries to access page
			if(session.getAttribute(LiteUtility.LOGIN_INFO_ATTRIBUTE) != null) {
				req.getRequestDispatcher("/reset_password.jsp").forward(req, resp);
			}
		} else { //user tries to reset password
			String password = req.getParameter("current_password");
			String newPassword = req.getParameter("new_password");
			PartnerToAssistments user;
			try {
				user = as.find(ColumnNames.PARTNER_EXTERNAL_REFERENCE, email).get(0);
				if (user.getPartnerAccessToken().equals(
						LiteUtility.getHash(password))) {
					// reset the password
					user.setPartnerAccessToken(LiteUtility.getHash(newPassword));
					as.update(user);
					String message = "Password reset successfully!";
					req.setAttribute("message", message);
					req.getRequestDispatcher("/reset_password.jsp").forward(req,
							resp);
				} else {
					String error = "Current password is incorrect!";
					req.setAttribute("error", error);
					req.getRequestDispatcher("/reset_password.jsp").forward(req,
							resp);
				}
			} catch (ReferenceNotFoundException e) {
				String error = "Email doesn't exist in the system!";
				req.setAttribute("error", error);
				req.getRequestDispatcher("/reset_password.jsp").forward(req,
						resp);
			}			
		}
	}
}

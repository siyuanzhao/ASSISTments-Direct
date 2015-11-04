package org.assistments.direct;

import java.io.IOException;
import java.io.PrintWriter;

import javax.mail.MessagingException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.JsonObject;

/**
 * Servlet implementation class CheckEmail
 */
@WebServlet({ "/CheckEmail", "/check_email" })
public class CheckEmail extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final String CODE_MAP = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public CheckEmail() {
		super();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	@Override
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		String action = request.getParameter("action");
		if (action.equals("GetVerifyCode")) {
			String email = request.getParameter("email");
			PrintWriter out = response.getWriter();
			JsonObject json = new JsonObject();
			StringBuilder code = new StringBuilder();
			for (int i = 0; i < 10; i++) {
				int rand = (int) (Math.random() * 62);
				code.append(CODE_MAP.charAt(rand));
			}

			String text = "Dear User,\n\n"
					+ "Thank you for singing up with Assistments Direct. Please validate your email address by entering the following code.\n\n"
					+ "Verification code: " + code + "\n\n"
					+ "~Assistments Direct Team\n";

			String subject = "[ASSISTments] Thank you for signing up. Please verify your email.";
			try {
				LiteUtility.sendEmail(email, subject, text);
				json.addProperty("result", "true");
				json.addProperty("correct_code", code.toString());
			} catch (MessagingException e) {
				json.addProperty("result", "wrong");
			}
			out.write(json.toString());
			out.flush();
			out.close();
		} else if (action.equals("VerifyingCode")) {
			String code = request.getParameter("code");
			String correctCode = request.getParameter("correct_code");
			PrintWriter out = response.getWriter();
			JsonObject json = new JsonObject();
			if (correctCode.equals("")) {
				json.addProperty("result", "wrong");
			}
			if (code.equals(correctCode)) {
				json.addProperty("result", "true");
			} else {
				json.addProperty("result", "wrong");
			}
			out.write(json.toString());
			out.flush();
			out.close();
		}

	}

}

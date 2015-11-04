package org.assistments.direct;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Timestamp;
import java.util.Calendar;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.assistments.service.controller.ErrorLogController;


@WebServlet({"/applicationExceptionHandler"})
public class ApplicationExceptionHandler extends HttpServlet {

	private static final long serialVersionUID = 238725213922734130L;

	public ApplicationExceptionHandler() {
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
//		Integer statusCode = (Integer)req.getAttribute("javax.servlet.error.status_code");  
		String servletName = (String)req.getAttribute("javax.servlet.error.servlet_name");  
//		String requestUri = (String)req.getAttribute("javax.servlet.error.request_uri");
		Throwable e = (Throwable)req.getAttribute("javax.servlet.error.exception");
		if(servletName == null) {
			servletName = "Unknown";
		}
		String errorType = e.getClass().getName();
		final StringWriter sw = new StringWriter();
		final PrintWriter pw = new PrintWriter(sw, true);
		e.printStackTrace(pw);
		int lineNum = e.getStackTrace()[1].getLineNumber();
		Calendar c = Calendar.getInstance();
		Timestamp now = new Timestamp(c.getTimeInMillis());
		String sourceType = "Direct";
		ErrorLogController.addNewError(servletName, sourceType, 
				errorType, sw.getBuffer().toString(), lineNum, e.getMessage(), now);
//		System.out.println(sw.getBuffer().toString());
		String errorMsg = "Sorry... We encountered an error!";
		String instruction = "The server seems to be unstable at this moment. Please take a break and try it again later.";
		LiteUtility.directToErrorPage(errorMsg, instruction, req, resp);
	}
}

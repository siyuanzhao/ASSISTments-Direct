package org.assistments.direct.google;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@WebServlet("/GoogleAppsLandingPage")
public class GoolgeAppsLandingPage extends HttpServlet {

	private static final long serialVersionUID = -3007538404485404284L;
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		req.getRequestDispatcher("google_apps_landing_page.jsp").forward(req, resp);
	}
}

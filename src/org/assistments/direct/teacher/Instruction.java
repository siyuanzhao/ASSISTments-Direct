package org.assistments.direct.teacher;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet({"/instruction"})
public class Instruction extends HttpServlet {
	
	private static final long serialVersionUID = -2537717246715913988L;

	public Instruction() {
		super();
	}
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doPost(req, resp);
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) 
			throws ServletException ,IOException {
		/*
		HttpSession session = req.getSession();
		if(session.getAttribute("submit") == null) {
			req.getRequestDispatcher("/404.jsp").forward(req, resp);
			return;
		}*/
		
		req.getRequestDispatcher("/instruction.jsp").forward(req, resp);
	}
	
}

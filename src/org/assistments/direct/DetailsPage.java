package org.assistments.direct;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.assistments.connector.service.AccountService;
import org.assistments.connector.service.ProblemSetService;
import org.assistments.connector.service.impl.AccountServiceImpl;
import org.assistments.connector.service.impl.ProblemSetServiceImpl;
import org.assistments.service.domain.ProblemSet;
import org.assistments.service.domain.User;

@WebServlet({"/details_page"})
public class DetailsPage extends HttpServlet {
	private static final long serialVersionUID = 4419340825676094665L;

	public DetailsPage() {
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
		String problemSetId = req.getParameter("problem_set_id");
		String distributorId = req.getParameter("distributor_id");
//		Long problemSetIdLong = Long.valueOf(problemSetId);
//		Long distributorIdLong = Long.valueOf(distributorId);
//		String genericLink = new String();
		
		User distributor = new User();
		ProblemSet ps = new ProblemSet();
		AccountService as = new AccountServiceImpl(LiteUtility.PARTNER_REF);
		ProblemSetService pss = new ProblemSetServiceImpl();

		distributor = as.getUserProfileById(Long.valueOf(distributorId));
		ps = pss.find(Integer.valueOf(problemSetId));
		
		HttpSession session = req.getSession();
		session.setAttribute("problem_set_id", ps.getDecodedID());
		session.setAttribute("problem_set_name", ps.getName());
		session.setAttribute("distributor_name", distributor.getDisplayName());
		session.setAttribute("distributor_id", distributorId);
		req.getRequestDispatcher("details_page.jsp").forward(req, resp);
	}
}

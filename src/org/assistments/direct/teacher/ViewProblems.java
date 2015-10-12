package org.assistments.direct.teacher;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.assistments.connector.service.ProblemSetService;
import org.assistments.connector.service.impl.ProblemSetServiceImpl;
import org.assistments.connector.utility.Constants;
import org.assistments.service.domain.Problem;
import org.assistments.service.domain.ProblemSection;
import org.assistments.service.domain.ProblemSection.Type;
import org.assistments.service.domain.ProblemSet;

@WebServlet({"/view_problems/*"})
public class ViewProblems extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doPost(req, resp);
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//		String problemSetId = req.getParameter("id");
		String pathInfo = req.getPathInfo();
		pathInfo = pathInfo.substring(1);
		
		ProblemSetService pss = new ProblemSetServiceImpl();
		long id = Long.valueOf(pathInfo);
		
		ProblemSet ps = pss.find(id);
	
		
//		ProblemSection headSection = pss.findBySectionId(ps.getHeadSectionId());
		
		ProblemSection headSection = new ProblemSection();
		
		//if it is a skill builder, we only randomly pick up 5 problems at most.
		List<Problem> list = new ArrayList<>();
		boolean isSkillBuilder = pss.isSkillBuilder(id);
		if(isSkillBuilder) {
			List<Problem> problems = pss.findAllProblems(id);
			int size = problems.size();
			if(size > 5) {
				Random random = new Random();
				for(int i=0; i < 5; i++) {
					int num = random.nextInt(size);
					Problem p = problems.get(num);
					list.add(p);
				}
			}
		} else if(pss.isPseudoSkillBuilder(id)) {
			isSkillBuilder = pss.isPseudoSkillBuilder(id);
			id = pss.getPseudoSkillBuilderId(id);
			List<Problem> problems = pss.findAllProblems(id);
			int size = problems.size();
			if(size > 5) {
				Random random = new Random();
				for(int i=0; i < 5; i++) {
					int num = random.nextInt(size);
					Problem p = problems.get(num);
					list.add(p);
				}
			}
		}else {
			List<Problem> problems = pss.findAllProblems(id);
			list = problems;
		}
		
		List<ProblemSection> children = new ArrayList<>();
		ProblemSection tmp = new ProblemSection();
		tmp.setType(Type.ProblemSection);
		tmp.setProblems(list);
		children.add(tmp);
		headSection.setChildren(children);
		headSection = sectionCheck(headSection);
		
		HttpSession session = req.getSession();
		
		
		
		req.setAttribute("problem_set_name", ps.getName());
		session.setAttribute("head_section", headSection);
		session.setAttribute("is_skill_builder", isSkillBuilder);
		
		req.getRequestDispatcher("/view_problems.jsp").forward(req, resp);
	}
	
	ProblemSection sectionCheck(ProblemSection section) {
		if(section.getType() == Type.ProblemSection) {
			for(Problem problem: section.getProblems()) {
				String body = imageCheck(problem.getBody());
				problem.setBody(body);
			}
		} else {
//			List<ProblemSection> tmpChildren = new ArrayList<>();
			for(ProblemSection childSection : section.getChildren()) {
				childSection = sectionCheck(childSection);
			}
			
		}
		return section;
	}
	
	//check every problem to see if it contains the image uploaded by user
	String imageCheck(String body) {
		String prefix = Constants.ASSISSTments_URL;
		if(body == null) {
			return "";
		}
		String s = "url"+Pattern.quote("(")+"\'.*images";
		Pattern p = Pattern.compile(s);
		String s1 = "url"+Pattern.quote("(")+".*images";
		Pattern p1 = Pattern.compile(s1);
		Matcher m = p.matcher(body);
		Matcher m1 = p1.matcher(body);
		if(body.contains("img src=\"/")) {
			body = body.replaceAll("img src=\"/", "img src=\""+prefix);
		} else if(m.find()) { 
			body = body.replaceAll(s, "url(\'"+prefix+"images");
		} else if(m1.find()) {
			body = body.replaceAll(s1, "url("+prefix+"images");
		} else {
		}
		return body;
	}
}

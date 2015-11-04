package org.assistments.direct.teacher;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class ImportStudentList
 */
@WebServlet({ "/ImportStudentList", "/import_student_list" })
public class ImportStudentList extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public ImportStudentList() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doPost(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	@Override
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		/*
		// TODO Auto-generated method stub
		String fileName = null;
		
		// process only if its multipart content
		if (ServletFileUpload.isMultipartContent(request)) {
			try {
				String name = null;
				List<FileItem> multiparts = new ServletFileUpload(
						new DiskFileItemFactory()).parseRequest();

				for (FileItem item : multiparts) {
					if (!item.isFormField()) {
						name = new File(item.getName()).getName();
						item.write(new File(LiteUtility.UPLOAD_DIRECTORY + File.separator
								+ name));
						fileName = item.getName();
					} else {
						String fieldName = item.getFieldName();
						
					}
				}

				// File uploaded successfully
				PrintWriter out = response.getWriter();
				JsonObject res = new JsonObject();
				res.addProperty("result", "true");
				out.write(res.toString());
				out.flush();
				out.close();
				
			} catch (Exception ex) {
				ex.printStackTrace();
			}

		}*/
	}

}

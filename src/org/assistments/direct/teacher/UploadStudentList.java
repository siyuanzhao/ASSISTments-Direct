package org.assistments.direct.teacher;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class UploadStudentList
 */
@WebServlet({"/UploadStudentList","/upload_student_list"})
public class UploadStudentList extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public UploadStudentList() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doPost(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		/*
		 
		PrintWriter out = response.getWriter();
		HttpSession session = request.getSession();
		
		String teacherPartnerExternalRef = (String) session.getAttribute("email");
		//upload from google drive
		if(request.getParameter("from").toString().equals("Google")){
			List<Map<String,String>> studentListFromGoogle = new ArrayList<Map<String, String>>();
			SpreadsheetService service =new SpreadsheetService("ASSISTmentsDirect");
			String accessToken = session.getAttribute("Google_access_token").toString();
			service.setOAuth2Credentials(new Credential(BearerToken
		            .authorizationHeaderAccessMethod())
		            .setFromTokenResponse(new TokenResponse().setAccessToken(accessToken)));
			String worksheetId = request.getParameter("worksheetId");
			List<WorksheetEntry> worksheets = (List<WorksheetEntry>)session.getAttribute("worksheets");
			for (WorksheetEntry worksheet : worksheets){
				if(worksheet.getId().equals(worksheetId)){
					URL listFeedUrl = worksheet.getListFeedUrl();
					try {
						ListFeed listFeed = service.getFeed(listFeedUrl, ListFeed.class);
						for (ListEntry row: listFeed.getEntries()){
							Map<String,String> studentMap = new HashMap<String,String>();
							
							for(String tag : row.getCustomElements().getTags()){
								if(tag.equalsIgnoreCase("firstname")||tag.equalsIgnoreCase("first_name")||tag.equalsIgnoreCase("first name")){
									studentMap.put("first_name", row.getCustomElements().getValue(tag));
								} else if (tag.equalsIgnoreCase("lastname")||tag.equalsIgnoreCase("last_name")||tag.equalsIgnoreCase("last name")){
									studentMap.put("last_name", row.getCustomElements().getValue(tag));
								}
							}
							//need to organize this block of codes later
							String firstName = studentMap.get("first_name");
							String lastName = studentMap.get("last_name");
							String teacherRef = new String();
							String studentClassRef = new String();
							
							ExternalStudentClassDAO classDAO = new ExternalStudentClassDAO(LiteUtility.PARTNER_REF);
							ExternalStudentClass esc = classDAO.findByPartnerExternalRef(teacherPartnerExternalRef);
							studentClassRef = esc.getAssistmentsExternalRefernce();

							ExternalUserDAO userDAO = new ExternalUserDAO(LiteUtility.PARTNER_REF);
							ExternalUser user = userDAO.findByPartnerExternalRef(teacherPartnerExternalRef);
							teacherRef = user.getAssistmentsExternalRefernce();
							
							User student = LiteUtility.populateStudentInfo(firstName.substring(0, 1).toUpperCase()+firstName.substring(1).toLowerCase(),
									lastName.substring(0, 1).toUpperCase() + lastName.substring(1).toLowerCase());
							List<String> studentRefAccessToken = null;
							try {
								String partnerExternalRef = student.getDisplayName() +"_" +teacherRef;
								studentRefAccessToken = LiteUtility.transferStudent(student, partnerExternalRef);
							} catch(TransferUserException e) {
								String errorMessage = e.getMessage();
								String instruction = "The server seems to be unstable at this moment. Please take a break and try it again later.";
								LiteUtility.directToErrorPage(errorMessage, instruction, request, response);
								return;
							}
							if(studentRefAccessToken != null) {
								String studentRef = studentRefAccessToken.get(0);
								String onBehalf = studentRefAccessToken.get(1);
								int type = Integer.parseInt(studentRefAccessToken.get(2));
								if(type == Constants.BRAND_NEW_USER){
									int studentId = StudentClassController.getStudentId(studentRef);
									studentMap.put("student_id", Integer.toString(studentId));
									studentListFromGoogle.add(studentMap);
									StudentClassController.enrollStudent(studentClassRef, studentRef, LiteUtility.PARTNER_REF, onBehalf);
								}
								
							}else{
								System.err.println("error occurred");
							}
						}
					} catch (ServiceException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					break;
				}
			}
			request.getRequestDispatcher("/Teacher").forward(request, response);
		} else if(request.getParameter("from").toString().equals("file")){
//			JsonArray studentList = new JsonArray();
			String studentListFileName = request.getParameter("student_list_file_name");
			String fileName = studentListFileName.substring(studentListFileName.lastIndexOf('\\')+1);
			
			String filePath = LiteUtility.UPLOAD_DIRECTORY + "/" + fileName;
			FileReader fileReader = new FileReader(filePath);
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			String line=null;
			while((line=bufferedReader.readLine())!=null){
				JsonObject jsonStudent = new JsonObject();
				jsonStudent.addProperty("first_name", line.substring(0,line.indexOf(',')));
				jsonStudent.addProperty("last_name", line.substring(line.indexOf(',')+2));
				
				String firstName = jsonStudent.get("first_name").getAsString();
				String lastName = jsonStudent.get("last_name").getAsString();
				String teacherRef = new String();
				String studentClassRef = new String();
				ExternalStudentClassDAO classDAO = new ExternalStudentClassDAO(LiteUtility.PARTNER_REF);
				ExternalStudentClass esc = classDAO.findByPartnerExternalRef(teacherPartnerExternalRef);
				studentClassRef = esc.getAssistmentsExternalRefernce();

				ExternalUserDAO userDAO = new ExternalUserDAO(LiteUtility.PARTNER_REF);
				ExternalUser user = userDAO.findByPartnerExternalRef(teacherPartnerExternalRef);
				teacherRef = user.getAssistmentsExternalRefernce();
				
				User student = LiteUtility.populateStudentInfo(firstName.substring(0, 1).toUpperCase()+firstName.substring(1).toLowerCase(),
						lastName.substring(0, 1).toUpperCase() + lastName.substring(1).toLowerCase());
				List<String> studentRefAccessToken = null;
				try {
					String partnerExternalRef = student.getDisplayName() +"_" +teacherRef;
					studentRefAccessToken = LiteUtility.transferStudent(student, partnerExternalRef);
				} catch(TransferUserException e) {
					String errorMessage = e.getMessage();
					String instruction = "The server seems to be unstable at this moment. Please take a break and try it again later.";
					LiteUtility.directToErrorPage(errorMessage, instruction, request, response);
					return;
				}
				if(studentRefAccessToken != null) {
					String studentRef = studentRefAccessToken.get(0);
					String onBehalf = studentRefAccessToken.get(1);
					int type = Integer.parseInt(studentRefAccessToken.get(2));
					if(type == Constants.BRAND_NEW_USER){
						int studentId = StudentClassController.getStudentId(studentRef);
//						jsonStudent.addProperty("student_id", studentId);
//						studentList.add(jsonStudent);
						StudentClassController.enrollStudent(studentClassRef, studentRef, LiteUtility.PARTNER_REF, onBehalf);
					}
					
				}else{
					System.err.println("error occurred");
				}
			}
			bufferedReader.close();
//			out.write(studentList.toString());
//			out.flush();
//			out.close();
			request.getRequestDispatcher("/Teacher").forward(request, response);
		} else if (request.getParameter("from").toString().equals("excel")){

			String studentListFileName = request.getParameter("student_list_file_name");
			String fileName = studentListFileName.substring(studentListFileName.lastIndexOf('\\')+1);
			
			String filePath = LiteUtility.UPLOAD_DIRECTORY + "/" + fileName;
			File file = new File(filePath);
			FileInputStream fis = new FileInputStream(file);
			POIFSFileSystem fs = new POIFSFileSystem(fis);
			HSSFWorkbook wb = new HSSFWorkbook(fs);
			HSSFSheet sheet = wb.getSheetAt(0);
			for(int i =0;i<sheet.getPhysicalNumberOfRows();i++){
				HSSFRow row = sheet.getRow(i);
				if(row != null){
					HSSFCell cell = row.getCell(0);
					String firstName = cell.getStringCellValue();
					cell = row.getCell(1);
					String lastName = cell.getStringCellValue();
					
					String teacherRef = new String();
					String studentClassRef = new String();
					ExternalStudentClassDAO classDAO = new ExternalStudentClassDAO(LiteUtility.PARTNER_REF);
					ExternalStudentClass esc = classDAO.findByPartnerExternalRef(teacherPartnerExternalRef);
					studentClassRef = esc.getAssistmentsExternalRefernce();

					ExternalUserDAO userDAO = new ExternalUserDAO(LiteUtility.PARTNER_REF);
					ExternalUser user = userDAO.findByPartnerExternalRef(teacherPartnerExternalRef);
					teacherRef = user.getAssistmentsExternalRefernce();
					
					User student = LiteUtility.populateStudentInfo(firstName.substring(0, 1).toUpperCase()+firstName.substring(1).toLowerCase(),
							lastName.substring(0, 1).toUpperCase() + lastName.substring(1).toLowerCase());
					List<String> studentRefAccessToken = null;
					try {
						String partnerExternalRef = student.getDisplayName() +"_" +teacherRef;
						studentRefAccessToken = LiteUtility.transferStudent(student, partnerExternalRef);
					} catch(TransferUserException e) {
						String errorMessage = e.getMessage();
						String instruction = "The server seems to be unstable at this moment. Please take a break and try it again later.";
						LiteUtility.directToErrorPage(errorMessage, instruction, request, response);
						return;
					}
					if(studentRefAccessToken != null) {
						String studentRef = studentRefAccessToken.get(0);
						String onBehalf = studentRefAccessToken.get(1);
						int type = Integer.parseInt(studentRefAccessToken.get(2));
						if(type == Constants.BRAND_NEW_USER){
							int studentId = StudentClassController.getStudentId(studentRef);
							StudentClassController.enrollStudent(studentClassRef, studentRef, LiteUtility.PARTNER_REF, onBehalf);
						}
						
					}else{
						System.err.println("error occurred");
					}
				}
			}
			request.getRequestDispatcher("/Teacher").forward(request, response);
		}
		 */
	}

}

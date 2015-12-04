package org.assistments.direct.schoology;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.assistments.connector.domain.PartnerToAssistments;
import org.assistments.connector.domain.PartnerToAssistments.ColumnNames;
import org.assistments.connector.exception.ReferenceNotFoundException;
import org.assistments.connector.exception.TransferUserException;
import org.assistments.connector.service.AccountService;
import org.assistments.connector.service.SchoolService;
import org.assistments.connector.service.impl.AccountServiceImpl;
import org.assistments.connector.service.impl.SchoolServiceImpl;
import org.assistments.direct.LiteUtility;
import org.assistments.service.domain.ReferenceTokenPair;
import org.assistments.service.domain.User;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import oauth.signpost.OAuth;
import oauth.signpost.OAuthConsumer;
import oauth.signpost.OAuthProvider;
import oauth.signpost.basic.DefaultOAuthConsumer;
import oauth.signpost.basic.HttpURLConnectionRequestAdapter;
import oauth.signpost.basic.HttpURLConnectionResponseAdapter;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;
import oauth.signpost.exception.OAuthNotAuthorizedException;
import oauth.signpost.http.HttpParameters;
import oauth.signpost.http.HttpRequest;
import oauth.signpost.http.HttpResponse;
import oauth.signpost.signature.AuthorizationHeaderSigningStrategy;

public class SchoologyAPI{
	
  private String domain;	
  public static final String Api_Base = "https://api.schoology.com/v1";
  private static final String apiSitebase = "https://www.schoology.com";
//  private static final String _saml_cert_path = "app.schoology.com.crt";
  public static final String Consumer_Key = "5fc3ced3275e6ecef3dfba7d5b486cfb055f8ae99";
  public static final String Consumer_Secret = "3e0b5cd179106f977ee3d042b6ec6761";
  
  private String authUrl;
  private OAuthConsumer consumer;
  private OAuthProvider provider;
  private String uID;
  private User user;
  private static java.util.Scanner scanner;
  private JsonObject userProfile;
  private boolean isTeacher;
  private boolean sendRedirect;
  private boolean isGradingCategoryExists;
  private AccountService as = new AccountServiceImpl(LiteUtility.PARTNER_REF);
  public static final String appsLandingPageURL = LiteUtility.DIRECT_URL+"/SchoologyAppsLandingPage";
  public static final String returnURL = LiteUtility.DIRECT_URL+"/SchoologySAMLServlet";
  public static final String assistmentsGradingGroupTitle = "AssistmentsGradingGroup";
  private String userName;
  private String courseName;
  private String sectionName;
  private String sectionID;
  private String courseID;
  private String gradingCategoryID;
  private String partnerAccessToken;
  private String partnerAccessSecret;
  private String assistmentsUserRef;
  private String assistmentsAccessToken;
  
  SchoologyAPI(String domain, String uID, User user, boolean isTeacher)
  {
	  this.domain = domain;
	  this.user = user;
	  this.uID = uID;
	  this.isTeacher = isTeacher;
	  this.isGradingCategoryExists = false;
//	  this.returnUrl = OAuth.percentEncode(returnUrl);
	  
	  this.consumer = new DefaultOAuthConsumer(Consumer_Key, Consumer_Secret);
	  //using header signing strategy
	  consumer.setSigningStrategy(new AuthorizationHeaderSigningStrategy());
	  HttpParameters params = new HttpParameters();
	  params.put("realm", "Schoology API");
	  consumer.setAdditionalParameters(params);
	  this.provider = new SchoologyOAuthProvider(getRequestTokenEndpoint(), getAccessTokenEndpoint(), getAuthorizationUrl());
	  if(!isUserExists(uID))
      {
      	// If user doesn't exist, get request token and set authorization url for getting approved
			try {
				this.retrieveRequestTokenAndSetAuthorizationUrl();
			    this.sendRedirect = true;
			} catch (OAuthMessageSignerException | OAuthNotAuthorizedException | OAuthExpectationFailedException
					| OAuthCommunicationException e) {
				System.out.println("OAuthRequestToken cannot be generated");
				e.printStackTrace();
			}
      }
	  else
		  this.sendRedirect = false;
  }
  public boolean gradingGroupExists()
  {
	  try {
		this.getListOfGradingCategories();
      } catch (OAuthMessageSignerException | OAuthExpectationFailedException | OAuthCommunicationException
				| IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
	  }
	  return this.isGradingCategoryExists;
  }
  
    /**
   * @return the user
   */
  public User getUser() {
	  return user;
  }
  /**
    * @param user the user to set
   */
  public void setUser(User user) {
	  this.user = user;
  }
	/**
   * @return the gradingGroupID
   */
  public String getGradingGroupID() {
  	return gradingCategoryID;
  }

  /**
   * @param gradingGroupID the gradingGroupID to set
   */
  public void setGradingGroupID(String gradingGroupID) {
	this.gradingCategoryID = gradingGroupID;
  }

  public String getUID()
  {
	  return uID;
  }
  public boolean isSendRedirect()
  {
	  return sendRedirect;
  }
  public boolean isTeacher()
  {
	  return isTeacher;
  }
  public OAuthConsumer getConsumer()
  {
	  return this.consumer;
  }
  public boolean isUserExists(String uid)
  {
	  if(as.isExternalUserExists(uid))
		  return true;
	  else 
		  return false;
  }
  	public void retrieveRequestTokenAndSetAuthorizationUrl() throws OAuthMessageSignerException, OAuthNotAuthorizedException, OAuthExpectationFailedException, OAuthCommunicationException
  	{
		System.out.println("Fetching request token... ");
 		this.authUrl = this.provider.retrieveRequestToken(consumer, returnURL);  	
 		this.authUrl = this.authUrl.replace("oauth_callback", "return_url");
 		System.out.println("Request token: " + consumer.getToken());
		System.out.println("Token secret: " + consumer.getTokenSecret());
		System.out.println(authUrl);
  	}
  	
  	public String getAuthUrl()
  	{
  		return authUrl;
  	}
  	
  	public void retrieveAccessTokenAndConnectAssistmentsUser(String sectionID) throws Exception {
  		if(!isUserExists(this.uID))
  		{
  			provider.retrieveAccessToken(consumer, null);
  			this.partnerAccessToken = consumer.getToken();
  			this.partnerAccessSecret = consumer.getTokenSecret();
  			this.retrieveUserProfile(); //gets user email address 
  			ReferenceTokenPair pair = as.createUser(user, this.uID, partnerAccessToken+";"+partnerAccessSecret, userProfile.get("primary_email").getAsString());
			this.assistmentsUserRef = pair.getExternalRef();
			this.assistmentsAccessToken = pair.getAccessToken();
			SchoolService ss = new SchoolServiceImpl(LiteUtility.PARTNER_REF, assistmentsAccessToken);
			ss.assignUserToSchool(assistmentsUserRef, LiteUtility.Direct_SCHOOL_REF);
  		}
  		else {
  			List<PartnerToAssistments> list = as.find(ColumnNames.PARTNER_EXTERNAL_REFERENCE, this.uID);
  			this.assistmentsUserRef = list.get(0).getAssistmentsExternalRefernce();
  			this.assistmentsAccessToken = list.get(0).getAssistmentsAccessToken();
  			String partnerAccessTokenPlusSecret = list.get(0).getPartnerAccessToken();
  			String[]tokens = partnerAccessTokenPlusSecret.split(";");
  			this.partnerAccessToken = tokens[0];
  			this.partnerAccessSecret = tokens[1];
  			consumer.setTokenWithSecret(partnerAccessToken, partnerAccessSecret);
  		}
  		this.setSectionID(sectionID);
		this.retrieveAndSetSectionName(); //get section name from ID
  	}
  	
  	/**
	 * @return the partnerAccessToken
	 */
	public String getPartnerAccessToken() {
		return partnerAccessToken;
	}
	/**
	 * @param partnerAccessToken the partnerAccessToken to set
	 */
	public void setPartnerAccessToken(String partnerAccessToken) {
		this.partnerAccessToken = partnerAccessToken;
	}
	/**
	 * @return the partnerAccessSecret
	 */
	public String getPartnerAccessSecret() {
		return partnerAccessSecret;
	}
	/**
	 * @param partnerAccessSecret the partnerAccessSecret to set
	 */
	public void setPartnerAccessSecret(String partnerAccessSecret) {
		this.partnerAccessSecret = partnerAccessSecret;
	}
	/**
	 * @return the assistmentsUserRef
	 */
	public String getAssistmentsUserRef() {
		return assistmentsUserRef;
	}
	/**
	 * @param assistmentsUserRef the assistmentsUserRef to set
	 */
	public void setAssistmentsUserRef(String assistmentsUserRef) {
		this.assistmentsUserRef = assistmentsUserRef;
	}
	/**
	 * @return the assistmentsAccessToken
	 */
	public String getAssistmentsAccessToken() {
		return assistmentsAccessToken;
	}
	/**
	 * @param assistmentsAccessToken the assistmentsAccessToken to set
	 */
	public void setAssistmentsAccessToken(String assistmentsAccessToken) {
		this.assistmentsAccessToken = assistmentsAccessToken;
	}
	public void setSectionID(String sectionID)
  	{
  		this.sectionID = sectionID;
  	}
  	public String getSectionID()
  	{
  		return sectionID;
  	}
  	public String getSectionName()
  	{
  		return sectionName;
  	}
  	public String getCourseName()
  	{
  		return courseName;
  	}
  	public ReferenceTokenPair findUser() throws TransferUserException
  	{
  		ReferenceTokenPair pair = as.transferUser(user, LiteUtility.Direct_SCHOOL_REF, uID, "", "");
		return pair;
  	}
  	public void postGrade(String sectionID, String enrollmentID, String assignmentID) throws IOException, OAuthMessageSignerException, OAuthExpectationFailedException, OAuthCommunicationException 
  	{
  		consumer.setSigningStrategy(new AuthorizationHeaderSigningStrategy());
  		
  		HttpParameters params = new HttpParameters();
  		params.put("realm", "Schoology API");
  		consumer.setAdditionalParameters(params);
  		URL url = new URL(Api_Base + "/sections/"+sectionID+"/grades"); 

  		HttpURLConnection connection = (HttpURLConnection) url	
				.openConnection();
		connection.setRequestMethod("PUT");
		
		String jsonReq = "{\"grades\":{ \"grade\": [ { \"type\": \"assignment\", \"assignment_id\":"+assignmentID+",\"enrollment_id\":"+enrollmentID+",\"grade\":95,\"comment\":\"grade submitted\"}]}}";
		
		HttpURLConnectionRequestAdapter httpReq = 
				new HttpURLConnectionRequestAdapter(connection);
		httpReq.setHeader("Accept", "application/json");
		httpReq.setHeader("Host", "api.schoology.com");
		httpReq.setHeader("Content-Type", "application/json");
		
		HttpRequest signedReq = consumer.sign(httpReq);
		System.out.println(httpReq.getHeader("Content-Type"));
		connection = (HttpURLConnection)signedReq.unwrap();
		connection.setDoOutput(true);
		byte[] outputInBytes = jsonReq.getBytes("UTF-8");
		OutputStream os = connection.getOutputStream();
		os.write( outputInBytes );    
		os.close();
		connection.connect();
		
		HttpResponse resp = new HttpURLConnectionResponseAdapter(connection);
		System.out.println(resp.getStatusCode());
//		HttpParameters responseParams = OAuth.decodeForm(resp.getContent());
//		for (String s : responseParams.keySet()) {
//			System.out.println(s + "  : " + responseParams.get(s));
//		}
		String json = convertStreamToString(resp.getContent());
		JsonElement jEelement = new JsonParser().parse(json);
		JsonObject jObject = jEelement.getAsJsonObject();
		String assignmentRef = jObject.get("course").getAsString();
		System.out.println(assignmentRef);
  		
  	}
  	public void retrieveUserProfile() 
  			throws IOException, OAuthMessageSignerException, OAuthExpectationFailedException, OAuthCommunicationException {
  		consumer.setSigningStrategy(new AuthorizationHeaderSigningStrategy());
  		HttpParameters params = new HttpParameters();
  		params.put("realm", "Schoology API");
  		consumer.setAdditionalParameters(params);
  		URL url = new URL(Api_Base + "/users/" + this.uID); 
//  		URL url = new URL(Api_Base + "/messages/inbox");
		HttpURLConnection connection = (HttpURLConnection) url
				.openConnection();
		connection.setRequestMethod("GET");
		
		HttpURLConnectionRequestAdapter httpReq = 
				new HttpURLConnectionRequestAdapter(connection);
		httpReq.setHeader("Accept", "application/json");
		httpReq.setHeader("Host", "api.schoology.com");
		httpReq.setHeader("Content-Type", "application/json");
		HttpRequest signedReq = consumer.sign(httpReq);
		System.out.println(httpReq.getHeader("Content-Type"));
		connection = (HttpURLConnection)signedReq.unwrap();
		connection.connect();
		
		HttpResponse resp = new HttpURLConnectionResponseAdapter(connection);
		System.out.println(resp.getStatusCode());
//		HttpParameters responseParams = OAuth.decodeForm(resp.getContent());
//		for (String s : responseParams.keySet()) {
//			System.out.println(s + "  : " + responseParams.get(s));
//		}
		String json = convertStreamToString(resp.getContent());
		JsonElement jEelement = new JsonParser().parse(json);
		this.userProfile = jEelement.getAsJsonObject();
//		String assignmentRef = jObject.get("primary_email").getAsString();
//		System.out.println(assignmentRef);

  	}
  	public List<String> getListOfEnrollments() 
  			throws IOException, OAuthMessageSignerException, OAuthExpectationFailedException, OAuthCommunicationException {
  		
  		List<String> assigneeIds =  new ArrayList<String>();
  		
  		consumer.setSigningStrategy(new AuthorizationHeaderSigningStrategy());
  		

  		HttpParameters params = new HttpParameters();
  		params.put("realm", "Schoology API");
  		consumer.setAdditionalParameters(params);
  		URL url = new URL(Api_Base + "/sections/"+sectionID+"/enrollments"); 
//  		URL url = new URL(Api_Base + "/messages/inbox");
		HttpURLConnection connection = (HttpURLConnection) url
				.openConnection();
		connection.setRequestMethod("GET");
		
		HttpURLConnectionRequestAdapter httpReq = 
				new HttpURLConnectionRequestAdapter(connection);
		httpReq.setHeader("Accept", "application/json");
		httpReq.setHeader("Host", "api.schoology.com");
		httpReq.setHeader("Content-Type", "application/json");
		HttpRequest signedReq = consumer.sign(httpReq);
		System.out.println(httpReq.getHeader("Content-Type"));
		connection = (HttpURLConnection)signedReq.unwrap();
		connection.connect();
		
		HttpResponse resp = new HttpURLConnectionResponseAdapter(connection);
		System.out.println(resp.getStatusCode());
//		HttpParameters responseParams = OAuth.decodeForm(resp.getContent());
//		for (String s : responseParams.keySet()) {
//			System.out.println(s + "  : " + responseParams.get(s));
//		}
		String json = convertStreamToString(resp.getContent());
		JsonElement jEelement = new JsonParser().parse(json);
		JsonObject jObject = jEelement.getAsJsonObject();
		JsonArray enrollments = jObject.getAsJsonArray("enrollment");
		for(int i = 0; i< enrollments.size(); i++)
		{
			String userId = enrollments.get(i).getAsJsonObject().get("uid").getAsString();
			if(!userId.equals(this.getUID()))
			{	String enrollmentId = enrollments.get(i).getAsJsonObject().get("id").getAsString();
				assigneeIds.add(enrollmentId);
			}
		}
		System.out.println(assigneeIds);
		return assigneeIds;	
  	}
  	public String getAssignmentDueDate(String assignmentID)throws IOException, OAuthMessageSignerException, OAuthExpectationFailedException, OAuthCommunicationException {
		
  		
  		consumer.setSigningStrategy(new AuthorizationHeaderSigningStrategy());
  		

  		HttpParameters params = new HttpParameters();
  		params.put("realm", "Schoology API");
  		consumer.setAdditionalParameters(params);
  		URL url = new URL(Api_Base + "/sections/"+sectionID+"/assignments/" + assignmentID); 
//  		URL url = new URL(Api_Base + "/messages/inbox");
		HttpURLConnection connection = (HttpURLConnection) url
				.openConnection();
		connection.setRequestMethod("GET");
		
		HttpURLConnectionRequestAdapter httpReq = 
				new HttpURLConnectionRequestAdapter(connection);
		httpReq.setHeader("Accept", "application/json");
		httpReq.setHeader("Host", "api.schoology.com");
		httpReq.setHeader("Content-Type", "application/json");
		HttpRequest signedReq = consumer.sign(httpReq);
		System.out.println(httpReq.getHeader("Content-Type"));
		connection = (HttpURLConnection)signedReq.unwrap();
		connection.connect();
		
		HttpResponse resp = new HttpURLConnectionResponseAdapter(connection);
		System.out.println(resp.getStatusCode());
//		HttpParameters responseParams = OAuth.decodeForm(resp.getContent());
//		for (String s : responseParams.keySet()) {
//			System.out.println(s + "  : " + responseParams.get(s));
//		}
		String json = convertStreamToString(resp.getContent());
		JsonElement jEelement = new JsonParser().parse(json);
		JsonObject jObject = jEelement.getAsJsonObject();
		String dueDate = jObject.get("due").getAsString();
		System.out.println(dueDate);
//		String assignmentRef = jObject.get("primary_email").getAsString();
//		System.out.println(assignmentRef);

  		return dueDate;

  	}
  	public JsonObject getListOfAssignments(String sectionID) 
  			throws IOException, OAuthMessageSignerException, OAuthExpectationFailedException, OAuthCommunicationException {
  		consumer.setSigningStrategy(new AuthorizationHeaderSigningStrategy());
  		

  		HttpParameters params = new HttpParameters();
  		params.put("realm", "Schoology API");
  		consumer.setAdditionalParameters(params);
  		URL url = new URL(Api_Base + "/sections/"+sectionID+"/grade_items"); 
//  		URL url = new URL(Api_Base + "/messages/inbox");
		HttpURLConnection connection = (HttpURLConnection) url
				.openConnection();
		connection.setRequestMethod("GET");
		
		HttpURLConnectionRequestAdapter httpReq = 
				new HttpURLConnectionRequestAdapter(connection);
		httpReq.setHeader("Accept", "application/json");
		httpReq.setHeader("Host", "api.schoology.com");
		httpReq.setHeader("Content-Type", "application/json");
		HttpRequest signedReq = consumer.sign(httpReq);
		System.out.println(httpReq.getHeader("Content-Type"));
		connection = (HttpURLConnection)signedReq.unwrap();
		connection.connect();
		
		HttpResponse resp = new HttpURLConnectionResponseAdapter(connection);
		System.out.println(resp.getStatusCode());
//		HttpParameters responseParams = OAuth.decodeForm(resp.getContent());
//		for (String s : responseParams.keySet()) {
//			System.out.println(s + "  : " + responseParams.get(s));
//		}
		String json = convertStreamToString(resp.getContent());
//		JsonElement jEelement = new JsonParser().parse(json);
//		JsonObject jObject = jEelement.getAsJsonObject();
//		String assignmentRef = jObject.get("course").getAsString();
//		System.out.println(assignmentRef);
		return null;
//		String assignmentRef = jObject.get("primary_email").getAsString();
//		System.out.println(assignmentRef);
		
  	}
  	public String postAnnouncement(String message, String problemName)throws IOException, OAuthMessageSignerException, OAuthExpectationFailedException, OAuthCommunicationException 
  	{
  		consumer.setSigningStrategy(new AuthorizationHeaderSigningStrategy());
  		
  		HttpParameters params = new HttpParameters();
  		params.put("realm", "Schoology API");
  		consumer.setAdditionalParameters(params);
  		URL url = new URL(Api_Base + "/sections/"+sectionID+"/updates"); 
//  		URL url = new URL(Api_Base + "/messages/inbox");
		HttpURLConnection connection = (HttpURLConnection) url	
				.openConnection();
		connection.setRequestMethod("POST");
		String instructionsToUseASSISTments = " Please go to the ASSISTments App to work on the assignment: "; 
		String jsonReq = "{\"body\": \""+message+ instructionsToUseASSISTments+ problemName+"\", \"attachments\":[] }";
		
		HttpURLConnectionRequestAdapter httpReq = 
				new HttpURLConnectionRequestAdapter(connection);
		httpReq.setHeader("Accept", "application/json");
		httpReq.setHeader("Host", "api.schoology.com");
		httpReq.setHeader("Content-Type", "application/json");
		
		HttpRequest signedReq = consumer.sign(httpReq);
		System.out.println(httpReq.getHeader("Content-Type"));
		connection = (HttpURLConnection)signedReq.unwrap();
		connection.setDoOutput(true);
		byte[] outputInBytes = jsonReq.getBytes("UTF-8");
		OutputStream os = connection.getOutputStream();
		os.write( outputInBytes );    
		os.close();
		connection.connect();
		
		HttpResponse resp = new HttpURLConnectionResponseAdapter(connection);
		System.out.println(resp.getStatusCode());
//		HttpParameters responseParams = OAuth.decodeForm(resp.getContent());
//		for (String s : responseParams.keySet()) {
//			System.out.println(s + "  : " + responseParams.get(s));
//		}
		String json = convertStreamToString(resp.getContent());
		JsonElement jEelement = new JsonParser().parse(json);
		JsonObject jObject = jEelement.getAsJsonObject();
		String assignmentRef = jObject.get("id").getAsString();
		System.out.println(assignmentRef);
		return assignmentRef;
  	}
  	public String postAssignmentComment(String assignmentId, String comment) throws IOException, OAuthMessageSignerException, OAuthExpectationFailedException, OAuthCommunicationException{
		
  		consumer.setSigningStrategy(new AuthorizationHeaderSigningStrategy());
  		
  		HttpParameters params = new HttpParameters();
  		params.put("realm", "Schoology API");
  		consumer.setAdditionalParameters(params);
  		URL url = new URL(Api_Base + "/sections/"+sectionID+"/assignments/" +assignmentId+"/comments"); 

  		HttpURLConnection connection = (HttpURLConnection) url	
				.openConnection();
		connection.setRequestMethod("POST");

		String jsonReq = "{\"uid\": \""+this.getUID()+"\", \"comment\": \""+ comment +"\" }";
		
		HttpURLConnectionRequestAdapter httpReq = 
				new HttpURLConnectionRequestAdapter(connection);
		httpReq.setHeader("Accept", "application/json");
		httpReq.setHeader("Host", "api.schoology.com");
		httpReq.setHeader("Content-Type", "application/json");
		
		HttpRequest signedReq = consumer.sign(httpReq);
		System.out.println(httpReq.getHeader("Content-Type"));
		connection = (HttpURLConnection)signedReq.unwrap();
		connection.setDoOutput(true);
		byte[] outputInBytes = jsonReq.getBytes("UTF-8");
		OutputStream os = connection.getOutputStream();
		os.write( outputInBytes );    
		os.close();
		connection.connect();
		
		HttpResponse resp = new HttpURLConnectionResponseAdapter(connection);
		System.out.println(resp.getStatusCode());

		String json = convertStreamToString(resp.getContent());
		JsonElement jEelement = new JsonParser().parse(json);
		JsonObject jObject = jEelement.getAsJsonObject();
		String assignmentCommentRef = jObject.get("id").getAsString();
		System.out.println(assignmentCommentRef);
		return assignmentCommentRef;
	}
  	public String postAssignment(String title, String description, String dueDate, List<String> assignees)throws IOException, OAuthMessageSignerException, OAuthExpectationFailedException, OAuthCommunicationException 
  	{
  		consumer.setSigningStrategy(new AuthorizationHeaderSigningStrategy());
  		
  		HttpParameters params = new HttpParameters();
  		params.put("realm", "Schoology API");
  		consumer.setAdditionalParameters(params);
  		URL url = new URL(Api_Base + "/sections/"+sectionID+"/assignments"); 
//  		URL url = new URL(Api_Base + "/messages/inbox");
		HttpURLConnection connection = (HttpURLConnection) url	
				.openConnection();
		connection.setRequestMethod("POST");
		String instructionsToUseASSISTments = " Please go to the ASSISTments App to work on the assignment"; 
		String jsonReq = "{\"title\": \""+title+"\", \"description\": \""+ description + instructionsToUseASSISTments+"\", \"due\": \""+dueDate+" 12:00:00\", \"type\": \"assignment\", \"assignees\":" +assignees.toString()+", \"grading_category\":"+this.gradingCategoryID+" }";
		
		HttpURLConnectionRequestAdapter httpReq = 
				new HttpURLConnectionRequestAdapter(connection);
		httpReq.setHeader("Accept", "application/json");
		httpReq.setHeader("Host", "api.schoology.com");
		httpReq.setHeader("Content-Type", "application/json");
		
		HttpRequest signedReq = consumer.sign(httpReq);
		System.out.println(httpReq.getHeader("Content-Type"));
		connection = (HttpURLConnection)signedReq.unwrap();
		connection.setDoOutput(true);
		byte[] outputInBytes = jsonReq.getBytes("UTF-8");
		OutputStream os = connection.getOutputStream();
		os.write( outputInBytes );    
		os.close();
		connection.connect();
		
		HttpResponse resp = new HttpURLConnectionResponseAdapter(connection);
		System.out.println(resp.getStatusCode());
//		HttpParameters responseParams = OAuth.decodeForm(resp.getContent());
//		for (String s : responseParams.keySet()) {
//			System.out.println(s + "  : " + responseParams.get(s));
//		}
		String json = convertStreamToString(resp.getContent());
		JsonElement jEelement = new JsonParser().parse(json);
		JsonObject jObject = jEelement.getAsJsonObject();
		String assignmentRef = jObject.get("id").getAsString();
		System.out.println(assignmentRef);
		return assignmentRef;
  	}
  	
  	public JsonObject getListOfClasses(String uid) 
  			throws IOException, OAuthMessageSignerException, OAuthExpectationFailedException, OAuthCommunicationException {
  		consumer.setSigningStrategy(new AuthorizationHeaderSigningStrategy());
  		

  		HttpParameters params = new HttpParameters();
  		params.put("realm", "Schoology API");
  		consumer.setAdditionalParameters(params);
  		URL url = new URL(Api_Base + "/users/"+uid+"/sections"); 
//  		URL url = new URL(Api_Base + "/messages/inbox");
		HttpURLConnection connection = (HttpURLConnection) url
				.openConnection();
		connection.setRequestMethod("GET");
		
		HttpURLConnectionRequestAdapter httpReq = 
				new HttpURLConnectionRequestAdapter(connection);
		httpReq.setHeader("Accept", "application/json");
		httpReq.setHeader("Host", "api.schoology.com");
		httpReq.setHeader("Content-Type", "application/json");
		HttpRequest signedReq = consumer.sign(httpReq);
		System.out.println(httpReq.getHeader("Content-Type"));
		connection = (HttpURLConnection)signedReq.unwrap();
		connection.connect();
		
		HttpResponse resp = new HttpURLConnectionResponseAdapter(connection);
		System.out.println(resp.getStatusCode());
//		HttpParameters responseParams = OAuth.decodeForm(resp.getContent());
//		for (String s : responseParams.keySet()) {
//			System.out.println(s + "  : " + responseParams.get(s));
//		}
		String json = convertStreamToString(resp.getContent());
//		JsonElement jEelement = new JsonParser().parse(json);
//		JsonObject jObject = jEelement.getAsJsonObject();
//		String assignmentRef = jObject.get("course").getAsString();
//		System.out.println(assignmentRef);
		return null;
//		String assignmentRef = jObject.get("primary_email").getAsString();
//		System.out.println(assignmentRef);
		
  	}
  	
  	public void retrieveAndSetSectionName() 
  			throws IOException, OAuthMessageSignerException, OAuthExpectationFailedException, OAuthCommunicationException {
  		consumer.setSigningStrategy(new AuthorizationHeaderSigningStrategy());
  		
  		HttpParameters params = new HttpParameters();
  		params.put("realm", "Schoology API");
  		consumer.setAdditionalParameters(params);
  		URL url = new URL(Api_Base + "/sections/"+sectionID); 
		HttpURLConnection connection = (HttpURLConnection) url
				.openConnection();
		connection.setRequestMethod("GET");
		
		HttpURLConnectionRequestAdapter httpReq = 
				new HttpURLConnectionRequestAdapter(connection);
		httpReq.setHeader("Accept", "application/json");
		httpReq.setHeader("Host", "api.schoology.com");
		httpReq.setHeader("Content-Type", "application/json");
		HttpRequest signedReq = consumer.sign(httpReq);
		System.out.println(httpReq.getHeader("Content-Type"));
		connection = (HttpURLConnection)signedReq.unwrap();
		connection.connect();
		
		HttpResponse resp = new HttpURLConnectionResponseAdapter(connection);
		System.out.println(resp.getStatusCode());
		String json = convertStreamToString(resp.getContent());
		JsonElement jEelement = new JsonParser().parse(json);
		JsonObject jObject = jEelement.getAsJsonObject();
		this.courseName = jObject.get("course_title").getAsString();
		this.courseID = jObject.get("course_id").getAsString(); 
		this.sectionName = jObject.get("section_title").getAsString();
		System.out.println(courseID+":"+courseName+sectionName);
  	}
  	
  	public void getListOfGradingCategories() throws IOException, OAuthMessageSignerException, OAuthExpectationFailedException, OAuthCommunicationException 
  	{
  		consumer.setSigningStrategy(new AuthorizationHeaderSigningStrategy());
  		
  		HttpParameters params = new HttpParameters();
  		params.put("realm", "Schoology API");
  		consumer.setAdditionalParameters(params);
  		URL url = new URL(Api_Base + "/sections/"+sectionID+"/grading_categories"); 
		HttpURLConnection connection = (HttpURLConnection) url
				.openConnection();
		connection.setRequestMethod("GET");
		
		HttpURLConnectionRequestAdapter httpReq = 
				new HttpURLConnectionRequestAdapter(connection);
		httpReq.setHeader("Accept", "application/json");
		httpReq.setHeader("Host", "api.schoology.com");
		httpReq.setHeader("Content-Type", "application/json");
		HttpRequest signedReq = consumer.sign(httpReq);
		System.out.println(httpReq.getHeader("Content-Type"));
		connection = (HttpURLConnection)signedReq.unwrap();
		connection.connect();
		
		HttpResponse resp = new HttpURLConnectionResponseAdapter(connection);
		System.out.println(resp.getStatusCode());
		
		String json = convertStreamToString(resp.getContent());
		JsonElement jEelement = new JsonParser().parse(json);
		JsonObject jObject = jEelement.getAsJsonObject();
		JsonArray gradingCategories = jObject.get("grading_category").getAsJsonArray();
		for(int i = 0; i< gradingCategories.size(); i++)
		{
			String title = gradingCategories.get(i).getAsJsonObject().get("title").getAsString();
			if(title.equals(this.assistmentsGradingGroupTitle))
			{	
				this.gradingCategoryID = gradingCategories.get(i).getAsJsonObject().get("id").getAsString();
				this.isGradingCategoryExists = true;
			}
		}
//		System.out.println(assignmentRef);
//		return assignmentRef;
  	}
  	public String transferGradingGroup(List<String> members) throws IOException, OAuthMessageSignerException, OAuthExpectationFailedException, OAuthCommunicationException {
  		
  		if(!this.gradingGroupExists())
  		{
	  		consumer.setSigningStrategy(new AuthorizationHeaderSigningStrategy());
	  		
	  		HttpParameters params = new HttpParameters();
	  		params.put("realm", "Schoology API");
	  		consumer.setAdditionalParameters(params);
	  		URL url = new URL(Api_Base + "/sections/"+sectionID+"/grading_categories"); 
			HttpURLConnection connection = (HttpURLConnection) url
					.openConnection();
			connection.setRequestMethod("POST");
			String jsonReq = "{\"grading_categories\": { \"grading_category\": [ {\"title\": \" "+ assistmentsGradingGroupTitle+"\", \"calculation_type\": 1 }]}}";
			
			HttpURLConnectionRequestAdapter httpReq = 
					new HttpURLConnectionRequestAdapter(connection);
			httpReq.setHeader("Accept", "application/json");
			httpReq.setHeader("Host", "api.schoology.com");
			httpReq.setHeader("Content-Type", "application/json");
			
			HttpRequest signedReq = consumer.sign(httpReq);
			System.out.println(httpReq.getHeader("Content-Type"));
			connection = (HttpURLConnection)signedReq.unwrap();
			connection.setDoOutput(true);
			byte[] outputInBytes = jsonReq.getBytes("UTF-8");
			OutputStream os = connection.getOutputStream();
			os.write( outputInBytes );    
			os.close();
			connection.connect();
			
			HttpResponse resp = new HttpURLConnectionResponseAdapter(connection);
			System.out.println(resp.getStatusCode());
	//		HttpParameters responseParams = OAuth.decodeForm(resp.getContent());
	//		for (String s : responseParams.keySet()) {
	//			System.out.println(s + "  : " + responseParams.get(s));
	//		}
			String json = convertStreamToString(resp.getContent());
			JsonElement jEelement = new JsonParser().parse(json);
			JsonObject jObject = jEelement.getAsJsonObject();
			JsonArray gradingCategories = jObject.getAsJsonArray("grading_category");
			for(int i = 0; i< gradingCategories.size(); i++)
			{
				this.gradingCategoryID = gradingCategories.get(i).getAsJsonObject().get("id").getAsString();
				this.isGradingCategoryExists = true;
			}
  		}	
  		
		return this.gradingCategoryID;
  		
  	}
  	private static String convertStreamToString(java.io.InputStream is) {
  	    scanner = new java.util.Scanner(is);
		java.util.Scanner s = scanner.useDelimiter("\\A");
  	    return s.hasNext() ? s.next() : "";
  	}


	public static String getAccessTokenEndpoint() {
		// TODO Auto-generated method stub
		return Api_Base+"/oauth/access_token";
	}
	
	public String getAuthorizationUrl() {
		return this.domain + "/oauth/authorize";		
	}

	public String getRequestTokenEndpoint() {
		// TODO Auto-generated method stub
		return Api_Base+"/oauth/request_token?uid="+this.uID;
	}
	
  
}

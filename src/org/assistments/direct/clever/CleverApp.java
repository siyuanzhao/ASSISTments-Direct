package org.assistments.direct.clever;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.fluent.Request;
import org.apache.http.entity.ContentType;
import org.assistments.connector.exception.TransferUserException;
import org.assistments.connector.service.AccountService;
import org.assistments.connector.service.impl.AccountServiceImpl;
import org.assistments.connector.utility.Constants;
import org.assistments.direct.LiteUtility;
import org.assistments.service.domain.ReferenceTokenPair;
import org.assistments.service.domain.User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

@Controller
public class CleverApp {

	public static final String CLIENT_ID = "fffc48f6db3f4b043777";
	public static final String CLIENT_SECRET = "efa2d1ac53d572fa80eeac2a133a6337be92873f";
	public static final String API_BASE = "https://api.clever.com/";
	public static final String PARTNER_REF = "Clever-Ref";
	public static final String PARTNER_ID = "8";
	
	@RequestMapping(value = "/clever_call_back", method = RequestMethod.GET) 
	public String callBack(@RequestParam Map<String, Object> req) {
		String code = req.get("code").toString();
		// get access token from clever
		String str = CLIENT_ID + ":" + CLIENT_SECRET;
		String basicAuthHeader = "Basic " + Base64.encodeBase64String(str.getBytes());
		
		// json post params
		Map<String, String> map = new HashMap<>();
		map.put("code", code);
		map.put("grant_type", "authorization_code");
		map.put("redirect_uri", "http://csta14-5.cs.wpi.edu/direct/s/clever_call_back");
		
		Gson gson = new Gson();
		String body = gson.toJson(map);
		String respJson = new String();
		try {
			respJson = Request.Post("https://clever.com/oauth/tokens")
							.addHeader("Authorization", basicAuthHeader)
							.addHeader("Content-Type", "application/json")
							.bodyString(body, ContentType.APPLICATION_JSON)
							.execute().returnContent().asString();
		} catch (ClientProtocolException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		Type type = new TypeToken<Map<String, String>>(){}.getType();
		map = gson.fromJson(respJson, type);
		
		String accessToken = map.get("access_token");
		
		// get user profile
		String oauthHeader = "Bearer " + accessToken;
		String url = API_BASE + "me";
		try {
			respJson = Request.Get(url)
							.addHeader("Authorization", oauthHeader)
							.execute().returnContent().asString();
		} catch (ClientProtocolException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		//parse json to get teacher id
		JsonElement jElement = new JsonParser().parse(respJson);
		JsonObject jObject = jElement.getAsJsonObject();
		jObject = jObject.getAsJsonObject("data");
		String userId = jObject.get("id").getAsString();
		String userType = jObject.get("type").getAsString();
		
		AccountService as = new AccountServiceImpl(PARTNER_REF);
		if("teacher".equals(userType)) {
			// get teacher profile
			url = API_BASE + "v1.1/teachers/" + userId;
			try {
				respJson = Request.Get(url)
													.addHeader("Authorization", oauthHeader)
													.execute().returnContent().asString();
			} catch (ClientProtocolException e) {
				throw new RuntimeException(e);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
			//ad-hoc to parse the data
			jElement = new JsonParser().parse(respJson);
			jObject = jElement.getAsJsonObject().get("data").getAsJsonObject();
			String email = jObject.get("email").getAsString();
			jObject = jObject.getAsJsonObject("name");
			String firstName = jObject.get("first").getAsString();
			String lastName = jObject.get("last").getAsString();

			// get district token
			/*
			url = "https://clever.com/oauth/tokens?owner_type=district";
			try {
				respJson = Request.Get(url)
						.addHeader("Authorization", basicAuthHeader)
						.execute().returnContent().asString();
			} catch (ClientProtocolException e1) {
				throw new RuntimeException(e1);
			} catch (IOException e1) {
				throw new RuntimeException(e1);
			}
			System.out.println(respJson);
			*/
			// create teacher account
			String displayName = firstName + " " + lastName;
			User teacherProfile = LiteUtility.populateTeacherInfo(firstName, lastName, displayName, email);
			
			ReferenceTokenPair pair = null;
			try {
				pair = as.transferUser(teacherProfile, LiteUtility.Direct_SCHOOL_REF, userId, accessToken, email);
			} catch (TransferUserException e) {
				throw new RuntimeException(e);
			}
//			String alsUserRef = pair.getExternalRef();
			String alsAccessToken = pair.getAccessToken();
			// transfer teacher's classes
			/*
			StudentClassService scs = new StudentClassServiceImpl(LiteUtility.PARTNER_REF, alsAccessToken);

			try {
				respJson = Request.Get(url)
						.addHeader("Authorization", oauthHeader)
						.execute().returnContent().asString();
			} catch (ClientProtocolException e) {
				throw new RuntimeException(e);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
			//parse classes info
			jElement = new JsonParser().parse(respJson);
			jObject = jElement.getAsJsonObject();
			JsonArray jArr = jObject.get("data").getAsJsonArray();
			for (JsonElement element : jArr) {
				jObject = element.getAsJsonObject().get("data").getAsJsonObject();
				String courseName = jObject.get("course_name").getAsString();
				String courseId = jObject.get("id").getAsString();
				scs.transferStudentClass(courseName, courseId, accessToken, alsUserRef);
			}*/
			String loginURL = Constants.LOGIN_URL;
			String redirectLink = String.format("%1$s?partner=%2$s&access=%3$s&on_success=%4$s&on_failure=%5$s", 
					loginURL, PARTNER_REF, alsAccessToken, Constants.ASSISSTments_URL + "teacher", LiteUtility.LOGIN_FAILURE);
			return "redirect:" + redirectLink;
		} else if("student".equals(userType)) {
			url = API_BASE + "v1.1/students/" + userId;
			try {
				respJson = Request.Get(url)
													.addHeader("Authorization", oauthHeader)
													.execute().returnContent().asString();
			} catch (ClientProtocolException e) {
				throw new RuntimeException(e);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
			//ad-hoc to parse the data
			jElement = new JsonParser().parse(respJson);
			jObject = jElement.getAsJsonObject().get("data").getAsJsonObject();
			String email = jObject.get("email").getAsString();
			jObject = jObject.getAsJsonObject("name");
			String firstName = jObject.get("first").getAsString();
			String lastName = jObject.get("last").getAsString();
			User studentInfo = LiteUtility.populateStudentInfo(firstName, lastName);
			ReferenceTokenPair pair = null;
			try {
				pair = as.transferUser(studentInfo, LiteUtility.Direct_SCHOOL_REF, userId, accessToken, email);
			} catch (TransferUserException e) {
				throw new RuntimeException();
			}
//			String alsUserRef = pair.getExternalRef();
			String alsAccessToken = pair.getAccessToken();
			String loginURL = Constants.LOGIN_URL;
			String redirectLink = String.format("%1$s?partner=%2$s&access=%3$s&on_success=%4$s&on_failure=%5$s", 
					loginURL, PARTNER_REF, alsAccessToken, Constants.ASSISSTments_URL + "tutor", LiteUtility.LOGIN_FAILURE);
			return "redirect:" + redirectLink;
		}
		System.out.println(respJson);
		return "redirect:https://www.assistments.org";
	}
}

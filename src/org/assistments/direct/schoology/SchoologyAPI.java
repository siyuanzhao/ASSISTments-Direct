package org.assistments.direct.schoology;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

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
  private String returnUrl;
  private String authUrl;
  private OAuthConsumer consumer;
  private OAuthProvider provider;
  private String uID;
  
  SchoologyAPI(String domain, String returnUrl, String uID)
  {
	  this.domain = domain;
	  this.uID = uID;
//	  this.returnUrl = OAuth.percentEncode(returnUrl);
	  this.returnUrl = returnUrl;
	  this.consumer = new DefaultOAuthConsumer(Consumer_Key, Consumer_Secret);
	  //using header signing strategy
	  consumer.setSigningStrategy(new AuthorizationHeaderSigningStrategy());
	  HttpParameters params = new HttpParameters();
	  params.put("realm", "Schoology API");
	  consumer.setAdditionalParameters(params);
	  this.provider = new SchoologyOAuthProvider(getRequestTokenEndpoint(), getAccessTokenEndpoint(), getAuthorizationUrl());
  }
  
  public OAuthConsumer getConsumer()
  {
	  return this.consumer;
  }
  	public String retrieveRequestToken() throws OAuthMessageSignerException, OAuthNotAuthorizedException, OAuthExpectationFailedException, OAuthCommunicationException
  	{
		System.out.println("Fetching request token... ");
 		this.authUrl = this.provider.retrieveRequestToken(consumer, returnUrl);  	
 		this.authUrl = this.authUrl.replace("oauth_callback", "return_url");
 		System.out.println("Request token: " + consumer.getToken());
		System.out.println("Token secret: " + consumer.getTokenSecret());
		System.out.println(authUrl);

 		return this.authUrl;
  	}
  	
  	public void retrieveAccessToken() 
  			throws OAuthMessageSignerException, OAuthNotAuthorizedException, OAuthExpectationFailedException, OAuthCommunicationException {
  		provider.retrieveAccessToken(consumer, null);
  	}
  	
  	public void getUserProfile(String uid) 
  			throws IOException, OAuthMessageSignerException, OAuthExpectationFailedException, OAuthCommunicationException {
  		consumer.setSigningStrategy(new AuthorizationHeaderSigningStrategy());
  		HttpParameters params = new HttpParameters();
  		params.put("realm", "Schoology API");
  		consumer.setAdditionalParameters(params);
  		URL url = new URL(Api_Base + "/users/" + uid); 
//  		URL url = new URL(Api_Base + "/messages/inbox");
		HttpURLConnection connection = (HttpURLConnection) url
				.openConnection();
		connection.setRequestMethod("GET");
		
		HttpURLConnectionRequestAdapter httpReq = 
				new HttpURLConnectionRequestAdapter(connection);
		httpReq.setHeader("Accept", "text/xml;q=1.0,application/json;q=0.0;application/php;q=0.0");
		httpReq.setHeader("Host", "api.schoology.com");
		httpReq.setHeader("Content-Type", "text/xml");
		HttpRequest signedReq = consumer.sign(httpReq);
		connection = (HttpURLConnection)signedReq.unwrap();
		connection.connect();
		
		HttpResponse resp = new HttpURLConnectionResponseAdapter(connection);
		System.out.println(resp.getStatusCode());
		HttpParameters responseParams = OAuth.decodeForm(resp.getContent());
		for (String s : responseParams.keySet()) {
			System.out.println(s + "  : " + responseParams.get(s));
		}
		
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

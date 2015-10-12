package org.assistments.direct;

public class LoginInfo {
	
	String userRef;	//external reference for the user from ASSISTments
	String accessToken; //access token for the user from ASSISTments
	String email;	//user's email
	String partnerExternalRef;	//partner external reference for the user from Direct
	LoginFrom from; //form, google, facebook
	
	public enum LoginFrom {
		FORM ("form"), GOOGLE ("google"), FACEBOOK ("facebook");
		
		String from;
		private LoginFrom(String from) {
			this.from = from;
		}
		
		public String getVal() {
			return from;
		}
	}
	
	public LoginInfo(String userRef, String accessToken, 
			String email, String partnerExternalRef, LoginFrom from) {
		this.userRef = userRef;
		this.accessToken = accessToken;
		this.email = email;
		this.partnerExternalRef = partnerExternalRef;
		this.from = from;
	}
	public String getUserRef() {
		return userRef;
	}
	public void setUserRef(String userRef) {
		this.userRef = userRef;
	}
	public String getAccessToken() {
		return accessToken;
	}
	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPartnerExternalRef() {
		return partnerExternalRef;
	}
	public void setPartnerExternalRef(String partnerExternalRef) {
		this.partnerExternalRef = partnerExternalRef;
	}
	public LoginFrom getFrom() {
		return from;
	}
	public void setFrom(LoginFrom from) {
		this.from = from;
	}

}

package org.assistments.direct.schoology;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import oauth.signpost.AbstractOAuthProvider;
import oauth.signpost.basic.HttpURLConnectionRequestAdapter;
import oauth.signpost.basic.HttpURLConnectionResponseAdapter;
import oauth.signpost.http.HttpRequest;
import oauth.signpost.http.HttpResponse;

public class SchoologyOAuthProvider extends AbstractOAuthProvider {

	private static final long serialVersionUID = 7273692363124989898L;

	public SchoologyOAuthProvider(String requestTokenEndpointUrl,
			String accessTokenEndpointUrl, String authorizationWebsiteUrl) {
		super(requestTokenEndpointUrl, accessTokenEndpointUrl, authorizationWebsiteUrl);
	}

	@Override
	protected HttpRequest createRequest(String endpointUrl) throws Exception {
		// TODO Auto-generated method stub
		HttpURLConnection connection = (HttpURLConnection) new URL(endpointUrl).openConnection();
        connection.setRequestMethod("GET");
        connection.setAllowUserInteraction(false);
        connection.setRequestProperty("Content-Length", "0");
        return new HttpURLConnectionRequestAdapter(connection);
	}

	@Override
	protected HttpResponse sendRequest(HttpRequest request) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) request.unwrap();
        connection.connect();
        return new HttpURLConnectionResponseAdapter(connection);
    }

    @Override
    protected void closeConnection(HttpRequest request, HttpResponse response) {
        HttpURLConnection connection = (HttpURLConnection) request.unwrap();
        if (connection != null) {
            connection.disconnect();
        }
    }
	
	
	
	

}

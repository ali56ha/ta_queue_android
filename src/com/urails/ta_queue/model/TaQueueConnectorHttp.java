package com.urails.ta_queue.model;

import org.apache.http.Header;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.HttpGet;

import org.apache.http.impl.auth.BasicScheme;

import android.util.Base64;

import com.loopj.android.http.*;

public class TaQueueConnectorHttp {

	private static final String BASE_URL = "http://nine.eng.utah.edu";
	//private static final String BASE_URL = "http://localhost:3001";
	
	private static AsyncHttpClient client = new AsyncHttpClient();
	
	public TaQueueConnectorHttp() {
		client.addHeader("Accept", "application/json");
	}
	
	public void setAuthget(String id, String token, String url, RequestParams params, AsyncHttpResponseHandler responseHandler)
	{
		UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(id, token);
		Header header = BasicScheme.authenticate(credentials, "UTF-8", false);
		Header[] headers = {header};
		client.get(null, getAbsoluteUrl(url), headers, params, responseHandler);
	}
	
	public void setAuthdelete(String id, String token, String url, RequestParams params, AsyncHttpResponseHandler responseHandler)
	{
		UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(id, token);
		Header header = BasicScheme.authenticate(credentials, "UTF-8", false);
		Header[] headers = {header};
		client.delete(null, getAbsoluteUrl(url), headers, responseHandler);
	}
	
	public void setAuth(String id, String token)
	{
		client.setBasicAuth(id, token);
	}
	
	public void addAuthHeader(String id, String token)
	{
		String basicAuth = id + ":" + token;
		//byte[] temp = authenticationString.getBytes();
		//String encodedString = Base64.encodeToString(temp, 0);   
		basicAuth = Base64.encodeToString(basicAuth.getBytes(), Base64.NO_WRAP);
		client.addHeader("Authorization", "Basic " + basicAuth);
	}
	
	public void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
	    client.get(getAbsoluteUrl(url), params, responseHandler);
	}
	
	public void put(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
		System.out.println("PUT CALL");
	    client.put(getAbsoluteUrl(url), params, responseHandler);
	}
	
	public void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
		System.out.println("POST CALL");
	    client.post(getAbsoluteUrl(url), params, responseHandler);
	}
    
	public void delete(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
		client.delete(getAbsoluteUrl(url), responseHandler);
	}
	
//	public void putAuth(String id, String token, String url, RequestParams params, AsyncHttpResponseHandler responseHandler,
//			String contentType, Context context) {
//		System.out.println("In the PUT Method call");
//		UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(id, token);
//		Header header = BasicScheme.authenticate(credentials, "UTF-8", false);
//		Header[] headers = {header};
//		final HttpContext httpContext = new SyncBasicHttpContext(new BasicHttpContext());
//		HttpEntityEnclosingRequestBase request = new HttpPut(getAbsoluteUrl(url));
//		final DefaultHttpClient httpclient = new DefaultHttpClient();
//        if(params != null) request.setEntity(paramsToEntity(params));
//        if(headers != null) request.setHeaders(headers);
//        sendRequest(httpclient, httpContext, request, contentType, responseHandler, context);
//		
////		client.put
////		client.put(getAbsoluteUrl(url), params, responseHandler);
//	}
//	
//	 private HttpEntity paramsToEntity(RequestParams params) {
//	        HttpEntity entity = null;
//
//	        if(params != null) {
//	            entity = params.getEntity();
//	        }
//	        return entity;
//	    }
	
	private String getAbsoluteUrl(String relativeUrl) {
	      return BASE_URL + relativeUrl;
	}

}

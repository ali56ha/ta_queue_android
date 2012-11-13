package com.urails.ta_queue.model;

import com.loopj.android.http.*;


public class TaQueueConnectorHttp {

	private static final String BASE_URL = "http://nine.eng.utah.edu";
	private static AsyncHttpClient client = new AsyncHttpClient();
	public TaQueueConnectorHttp() {
		client.addHeader("Accept", "application/json");
	}
	
	
	
	public static void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
	      client.get(getAbsoluteUrl(url), params, responseHandler);
	}

	public static void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
	      client.post(getAbsoluteUrl(url), params, responseHandler);
	}

	private static String getAbsoluteUrl(String relativeUrl) {
	      return BASE_URL + relativeUrl;
	}
	
	
	private void getSchools()
	{
		client.get("http://www.google.com", new AsyncHttpResponseHandler() {
		    @Override
		    public void onSuccess(String response) {
		        System.out.println(response);
		    }
		    
		    @Override
		    public void onStart() {
		    	// TODO Auto-generated method stub
		    	super.onStart();
		    }
		    
		    @Override
		    public void onFailure(Throwable arg0, String arg1) {
		    	// TODO Auto-generated method stub
		    	super.onFailure(arg0, arg1);
		    }
		    
		});
		
	}

	
}

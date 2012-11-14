package com.urails.ta_queue.model;

import com.loopj.android.http.*;


public class TaQueueConnectorHttp {

	private static final String BASE_URL = "http://nine.eng.utah.edu";
	private static AsyncHttpClient client = new AsyncHttpClient();
	public TaQueueConnectorHttp() {
		client.addHeader("Accept", "application/json");
	}
	
	
	
	public void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
	      client.get(getAbsoluteUrl(url), params, responseHandler);
	}

	public void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
	      client.post(getAbsoluteUrl(url), params, responseHandler);
	}

	private String getAbsoluteUrl(String relativeUrl) {
	      return BASE_URL + relativeUrl;
	}

	
}

package com.urails.ta_queue;

import java.util.ArrayList;

import org.json.*;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.*;
import com.urails.ta_queue.model.Instructor;
import com.urails.ta_queue.model.School;
import com.urails.ta_queue.model.TaQueueConnectorHttp;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class TAQueueMain extends Activity {
	private TaQueueConnectorHttp _connector;
	private ArrayList<School> _schools;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initial_set_up);
		_connector = new TaQueueConnectorHttp();
		
		try{
			getSchools();
		}catch(Exception e){
			System.out.println("ERROR: " + e);
		}
		
    }
    
    
    public void getSchools() throws JSONException {
        TaQueueConnectorHttp.get("/schools", null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(JSONArray information) {
            	try{
            		Gson gson = new Gson();
            		java.lang.reflect.Type listType = new TypeToken<ArrayList<School>>(){}.getType();
            		_schools = (ArrayList<School>) gson.fromJson(information.toString(), listType);
            		
//	                for (int schoolIndex = 0; schoolIndex < information.length(); schoolIndex++) {
//	                	JSONObject school = (JSONObject)information.get(schoolIndex);
//	                	System.out.println("School: " + school.get("abbreviation"));
//	                	JSONArray instructors = (JSONArray)school.get("instructors");
//	                	for (int instructorIndex = 0; instructorIndex < instructors.length(); instructorIndex++) {
//	                		JSONObject instructor = (JSONObject)instructors.get(instructorIndex);
//	                		System.out.println("  Instructor: " + instructor.get("username"));
//	                		JSONArray queues = (JSONArray)instructor.get("queues");
//	                		for (int queueIndex = 0; queueIndex < queues.length(); queueIndex++) {
//	                			JSONObject queue = (JSONObject)queues.get(queueIndex);
//	                			System.out.println("    Queue: " + queue.get("class_number"));
//	                		}
//	                	}
//	                }
            	}
            	catch(Exception e)
            	{
            		System.out.println("ERROR "+e);
            	}
            }
        });
    }
}

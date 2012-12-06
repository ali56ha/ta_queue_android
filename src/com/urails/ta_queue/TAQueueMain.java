package com.urails.ta_queue;

import java.util.ArrayList;

import org.json.*;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.*;
import com.urails.ta_queue.model.QueueItem;
import com.urails.ta_queue.model.School;
import com.urails.ta_queue.model.SchoolNames;
import com.urails.ta_queue.model.TaQueueConnectorHttp;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.database.DataSetObserver;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;

public class TAQueueMain extends Activity implements ListAdapter {
	private TaQueueConnectorHttp _connector;
	private ArrayList<School> _schools;
	private ArrayList<QueueItem> _queues;
	private ArrayList<SchoolNames> _schoolnames;
	private ListView _schoolList;
	//private Button _settings;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.schools);
		_connector = new TaQueueConnectorHttp();
		_schoolnames = new ArrayList<SchoolNames>();
		_schoolList = (ListView) findViewById(R.id.schools_listview);
		//_settings = (Button) findViewById(R.id.main_setting_button);
		try{
			getSchoolsInfo();
		}catch(Exception e){
			System.out.println("ERROR: " + e);
		}
		
		_schoolList.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long id) {
				processResult(position);
			}
		});
		
		
//		_settings.setOnClickListener(new OnClickListener() {
//		@Override
//		public void onClick(View v) {
//			//go to setting
//		}});
//	
    }
    
    public void getSchoolsInfo() throws JSONException {
    	_connector.get("/schools.json", null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(JSONArray information) {
            	try{
            		Gson gson = new Gson();
            		java.lang.reflect.Type listType = new TypeToken<ArrayList<School>>(){}.getType();
            		_schools = (ArrayList<School>) gson.fromJson(information.toString(), listType);
            		processSchools();
            	}
            	catch(Exception e)
            	{
            		System.out.println("ERROR "+e);
            	}
            }    
            @Override
            public void onSuccess(JSONObject information) {
            	try{
            		Gson gson = new Gson();
            		java.lang.reflect.Type listType = new TypeToken<ArrayList<School>>(){}.getType();
            		_schools = (ArrayList<School>) gson.fromJson(information.toString(), listType);
            		processSchools();
            	}
            	catch(Exception e)
            	{
            		System.out.println("ERROR "+e);
            	}
            }     
            @Override
            public void onFailure(Throwable arg0, JSONObject arg1) {
            	System.out.println("ERROR " + arg1.toString());
            	super.onFailure(arg0, arg1);
            }
        });
    }
    
    private void processSchools()
    {
    	SchoolNames schoolnameTemp;
    	for (int i = 0; i<_schools.size(); i++)
    	{
    		schoolnameTemp = new SchoolNames(_schools.get(i).name);
    		_schoolnames.add(schoolnameTemp);
    	}
    	_schoolList.setAdapter(this);
    }
    
    private void processResult(int i)
    {
    	_queues = new ArrayList<QueueItem>();
    	School tempSchool = new School();
    		tempSchool = _schools.get(i);
			for(int j = 0; j<tempSchool.instructors.length; j++)
				for(int k = 0; k<tempSchool.instructors[j].queues.length; k++)
				{
					String url = "/schools/"+tempSchool.abbreviation +"/"+
							tempSchool.instructors[j].username + "/" +
							tempSchool.instructors[j].queues[k].class_number;
					String title = tempSchool.instructors[j].queues[k].title;
					
					if(!tempSchool.instructors[j].queues[k].active)
						title += " [Inactive] ";
					if(tempSchool.instructors[j].queues[k].frozen)
						title += " [frozen] ";
					
					_queues.add(new QueueItem(title,
							tempSchool.instructors[j].name, url,
							tempSchool.instructors[j].queues[k].active,
							tempSchool.instructors[j].queues[k].frozen));
				}
			
			Intent intent = new Intent();
			intent.setClass(TAQueueMain.this, TAQueueChooseQueues.class);
			intent.putExtra(TAQueueChooseQueues.CHOOSE_QUEUE, _queues);
			startActivityForResult(intent, (int)2);
//			setResult((int)2, intent);
//			finish();
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	super.onActivityResult(requestCode, resultCode, data);
    	switch (resultCode) {
    		case 2:
    			_queues = new ArrayList<QueueItem>();
    			_schoolnames = new ArrayList<SchoolNames>();
    			_schools = null;
    			try{
    				getSchoolsInfo();
    			}catch(Exception e)
    			{}
    			break;
    		default:
    			break;	
    	}
    }
    //ListAdapter Methods
	@Override
	public int getCount() {
		return _schoolnames.size();
	}

	@Override
	public Object getItem(int position) {
		return _schoolnames.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public int getItemViewType(int position) {
		return 0xABC;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View layout = null;
		if(convertView != null)
			layout = convertView;
		else
			layout = getLayoutInflater().inflate(R.layout.single_item, null);
		SchoolNames schoolnamesTemp = _schoolnames.get(position);
		TextView sname = (TextView) layout.findViewById(R.id.single_item_text);
		sname.setText(schoolnamesTemp._schoolname);
		return layout;
	}

	@Override
	public int getViewTypeCount() {
		return 1;
	}

	@Override
	public boolean hasStableIds() {
		return false;
	}

	@Override
	public boolean isEmpty() {
		return _schoolnames.size() == 0;
	}

	@Override
	public void registerDataSetObserver(DataSetObserver observer) {		
	}

	@Override
	public void unregisterDataSetObserver(DataSetObserver observer) {		
	}

	@Override
	public boolean areAllItemsEnabled() {
		return true;
	}

	@Override
	public boolean isEnabled(int position) {
		return true;
	}
}

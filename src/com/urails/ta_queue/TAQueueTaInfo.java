package com.urails.ta_queue;

import java.util.ArrayList;

import org.json.JSONObject;

import com.google.gson.Gson;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.urails.ta_queue.model.QueueInfo;
import com.urails.ta_queue.model.QueueItem;
import com.urails.ta_queue.model.TA;
import com.urails.ta_queue.model.TaQueueConnectorHttp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class TAQueueTaInfo extends Activity implements ListAdapter{
	public static final String USER = "USER";
	public static final String QUEUE= "QUEUE";
	private TaQueueConnectorHttp _connector;
	private QueueInfo _queueinfo;
	private TA _ta;
	private QueueItem _queue;
	RequestParams _params;
	private int _duration;
	private TextView _taTitle;
	private TextView _studentTitle;
	private ListView _studentList;
	private boolean _hasStudent;
	private Button _buttonaccept;
	private Button _buttonputback;
	private Button _buttonforzen;
	private Button _buttonactive;
	private ArrayList<String> _students;
	private ArrayList<String> _studentIDs;
	private boolean _success;
	private String _studentName;
	private boolean _frozen;
	private boolean _active;
	private Context _context;
	private CharSequence _text;
	private int _studentIndex;
	private String _currentstudentid;
	private RelativeLayout _progress;
	private boolean _nostudent;
	private int _studentwithta;
	private boolean _foundstudentta;
	private boolean _setIndex;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ta_queue);
		_ta = (TA)getIntent().getExtras().get(USER);
		_queue = (QueueItem)getIntent().getExtras().get(QUEUE);
		_success = false;
		_frozen = false;
		_active = false;
		_nostudent = true;
		_foundstudentta = false;
		_studentIndex = 0;
		_setIndex = false;
		_studentwithta = 0;
		_currentstudentid = "";
		_connector = new TaQueueConnectorHttp();
		_taTitle = (TextView) findViewById(R.id.ta_queue_title);
		_studentTitle = (TextView) findViewById(R.id.ta_studnet_queue_title);
		_studentList = (ListView) findViewById(R.id.ta_student_queue_listview);
		_buttonaccept = (Button) findViewById(R.id.button_ta_queue_accept);
		_buttonputback = (Button) findViewById(R.id.button_ta_queue_putback);
		_buttonforzen = (Button) findViewById(R.id.button_ta_queue_frozen);
		_buttonactive = (Button) findViewById(R.id.button_ta_queue_active);
		_progress = (RelativeLayout)findViewById(R.id.taovedrlay);
		_buttonactive.setText("Deactiv");
		_buttonaccept.setText("Accept");
		_buttonforzen.setText("Freeze");
		_buttonputback.setText("PutBack");
		_buttonactive.setVisibility(8);
		_buttonforzen.setVisibility(8);
		_buttonputback.setVisibility(8);
		_context = getApplicationContext();
		_progress.setVisibility(8);
		_duration = Toast.LENGTH_SHORT;
		_hasStudent = false;
		_studentName ="";
		getQueueInfo();
		
		_buttonaccept.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				_buttonaccept.setClickable(false);
				if(_hasStudent)
					removeQueue();
				else if(!_nostudent && _students.size() > _studentwithta)
					acceptQueue();
				else
					getQueueInfo();
			}
		});
		_buttonputback.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				_buttonputback.setClickable(false);
				if(_hasStudent)
					putbackQueue();
			}
		});
		_buttonforzen.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				setFrozen();
			}
		});
		_buttonactive.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				setActive();
			}
		});
	}
	
	private void setFrozen(){
		_progress.setVisibility(0);
		_params = new RequestParams();
		String bool = "true";
		if(_frozen)
			bool = "false";
		_params.put("queue[frozen]", bool);
		System.out.println(_params.toString());
		_connector.setAuth(_ta.id, _ta.token);
		_connector.get("/queue", _params, new JsonHttpResponseHandler(){
			@Override
			public void onSuccess(JSONObject result) {
				super.onSuccess(result);
				System.out.println("Frozen QQQ " + result.toString());
				getQueueInfo();
			}
			@Override
	        public void onFailure(Throwable arg0, JSONObject arg1) {
	        	System.out.println("ERROR SetFrozen" + arg1.toString());
	           	super.onFailure(arg0, arg1);
	           	Intent intent = new Intent();
				intent.setClass(TAQueueTaInfo.this, TAQueueMain.class);
				setResult((int)2, intent);
				finish();
	        }
		});
	}
	
	private void setActive(){
		_progress.setVisibility(0);
		_params = new RequestParams();
		String active = "true";
		if(_active)
			active = "false";
		System.out.println("_ACTIVE IS :"+_active);
		_params.put("queue[active]", active);
		System.out.println(_params.toString());
		_connector.addAuthHeader(_ta.id, _ta.token);
		_connector.put("/queue", _params, new JsonHttpResponseHandler(){
			@Override
			public void onSuccess(JSONObject result) {
				super.onSuccess(result);
				System.out.println("Active QQQ " + result.toString());
				getQueueInfo();
			}
			
			@Override
	        public void onFailure(Throwable arg0, JSONObject arg1) {
	        	System.out.println("ERROR SetActive" + arg1.toString());
	           	super.onFailure(arg0, arg1);
	    		_progress.setVisibility(8);
	    		Intent intent = new Intent();
				intent.setClass(TAQueueTaInfo.this, TAQueueMain.class);
				setResult((int)2, intent);
				finish();
	        }
		});
	}
	
	private void acceptQueue(){
		_progress.setVisibility(0);
		_params = new RequestParams();
		String url = "/students/"+_studentIDs.get(_studentIndex)+"/ta_accept";
		System.out.println("ACCEPT: " + url);
		_connector.setAuthget(_ta.id, _ta.token, url, _params, new JsonHttpResponseHandler(){
			@Override
			public void onSuccess(JSONObject result) {
				super.onSuccess(result);
				System.out.println("Acept QQQ " + result.toString());
				getQueueInfo();
			}
			@Override
	        public void onFailure(Throwable arg0, JSONObject arg1) {
	        	System.out.println("ERROR " + arg1.toString());
	           	super.onFailure(arg0, arg1);
	           	getQueueInfo();
	    		_progress.setVisibility(8);
	           	_text = "ERROR Your login might be timeout. Please Login again";
				Toast.makeText(_context, _text, _duration).show();
				Intent intent = new Intent();
				intent.setClass(TAQueueTaInfo.this, TAQueueMain.class);
				setResult((int)2, intent);
				finish();
	        }
		});
	}
	
	private void removeQueue()
	{
		_progress.setVisibility(0);
		_params = new RequestParams();
		String url = "/students/"+_currentstudentid+"/ta_remove";
		_connector.setAuthget(_ta.id, _ta.token, url, _params, new JsonHttpResponseHandler(){
			@Override
			public void onSuccess(JSONObject result) {
				super.onSuccess(result);
				System.out.println("Remove QQQ " + result.toString());
				_hasStudent = false;
				getQueueInfo();
			}
			@Override
	        public void onFailure(Throwable arg0, JSONObject arg1) {
	        	System.out.println("ERROR " + arg1.toString());
	           	super.onFailure(arg0, arg1);
	    		getQueueInfo();
	    		_text = "ERROR Your login might be timeout. Please Login again";
	    		_progress.setVisibility(8);
				Toast.makeText(_context, _text, _duration).show();
				Intent intent = new Intent();
				intent.setClass(TAQueueTaInfo.this, TAQueueMain.class);
				setResult((int)2, intent);
				finish();
	        }
		});
	}
	
	private void putbackQueue(){
		_progress.setVisibility(0);
		_params = new RequestParams();
		String url = "/students/"+_currentstudentid+"/ta_putback";
		_connector.setAuthget(_ta.id, _ta.token, url, _params, new JsonHttpResponseHandler(){
			@Override
			public void onSuccess(JSONObject result) {
				super.onSuccess(result);
				System.out.println("PutBack QQQ " + result.toString());
				_hasStudent = false;
				getQueueInfo();
			}
			@Override
	        public void onFailure(Throwable arg0, JSONObject arg1) {
	        	System.out.println("ERROR " + arg1.toString());
	           	super.onFailure(arg0, arg1);
	           	_text = "ERROR Your login might be timeout. Please Login again";
				Toast.makeText(_context, _text, _duration).show();
				_progress.setVisibility(8);
				Intent intent = new Intent();
				intent.setClass(TAQueueTaInfo.this, TAQueueMain.class);
				setResult((int)2, intent);
				finish();
	        }
		});
	}
	
	
	private void getQueueInfo()
	{
		_progress.setVisibility(0);
		_frozen = false;
		_active = false;
		_success = false;
		_hasStudent = false;
		_setIndex = false;
		_foundstudentta = false;
		_nostudent = true;
		_studentwithta = 0;
		_buttonactive.setText("Deactiv");
		_buttonaccept.setText("Accept");
		_buttonforzen.setText("Freeze");
		_buttonputback.setText("PutBack");
		_studentIndex = 0;
		_currentstudentid = "";
		_students = new ArrayList<String>();
		_studentIDs = new ArrayList<String>();
		_params = new RequestParams();
		_connector.setAuthget(_ta.id, _ta.token,"/queue", _params, new JsonHttpResponseHandler(){
			@Override
			public void onSuccess(JSONObject result) {
				super.onSuccess(result);
				System.out.println("RESULT IS : "+ result.toString());
        		_success = true;
				try{
					Gson gson = new Gson();
            		_queueinfo = (QueueInfo) gson.fromJson(result.toString(), QueueInfo.class);
            		processQueue();
				}
				catch(Exception e)
				{
					System.out.println("ERROR Get Queue Info" + e.toString());
					_progress.setVisibility(8);
					_buttonputback.setClickable(true);
				}
			}
			@Override
	        public void onFailure(Throwable arg0, String arg1) {
	        	System.out.println("ERROR " + arg1);
	           	super.onFailure(arg0, arg1);
	           	_text = "ERROR Your login might be timeout. Please Login again";
				Toast.makeText(_context, _text, _duration).show();
				_progress.setVisibility(8);
				Intent intent = new Intent();
				intent.setClass(TAQueueTaInfo.this, TAQueueMain.class);
				setResult((int)2, intent);
				finish();
	        }
		});
	}
	
	private void processQueue()
	{
		if(!_success)
		{
			_text = "ERROR Your login might be timeout. Please Login again";
			Toast.makeText(_context, _text, _duration).show();
			_progress.setVisibility(8);
			Intent intent = new Intent();
			intent.setClass(TAQueueTaInfo.this, TAQueueMain.class);
			setResult((int)2, intent);
			finish();
		}
		
		int tacount = _queueinfo.tas.length;
		int scount = _queueinfo.students.length;
		
		String tamessage = "TA: ";
		String studentmessage ="Student: ";
		
		if(tacount==0)
			tamessage = "No TA";
		else
			for(int i=0; i<tacount; i++)
			{
				tamessage+=_queueinfo.tas[i].username;
				if(i < tacount-1)
					tamessage+=", ";
				System.out.println("CHECK STUDENT IN TA");
				if(_queueinfo.tas[i].id.equals(_ta.id))
					if(_queueinfo.tas[i].student==null)
						System.out.println("NULL");
					else
						_hasStudent = true;
			}
		
		if(scount==0)
		{
			_nostudent = true;
			_students.add("=== No Student ===");
		}
		else
		{
			for(int i = 0; i <scount; i++)
			{	
				if(_queueinfo.students[i].in_queue)
				{				
					_nostudent = false;
					if(_queueinfo.students[i].ta_id==null)
					{
						_students.add(_queueinfo.students[i].username + "@" + _queueinfo.students[i].location);
						_studentIDs.add(_queueinfo.students[i].id);
						if(_foundstudentta && i>0 && !_setIndex)
						{
							_setIndex = true;
							_studentIndex = _students.size()-1;
						}
					}
					else if(_queueinfo.students[i].ta_id.equals("null"))
					{
						_students.add(_queueinfo.students[i].username + "@" + _queueinfo.students[i].location);
						_studentIDs.add(_queueinfo.students[i].id);
						if(_foundstudentta && i>0 && !_setIndex)
						{
							_setIndex = true;
							_studentIndex = _students.size()-1;
						}
					}
					else
					{
						_students.add(_queueinfo.students[i].username + "@" + _queueinfo.students[i].location + "  with TA");
						_studentIDs.add(_queueinfo.students[i].id);
						_foundstudentta = true;
						_studentwithta++;
						if(_queueinfo.students[i].ta_id.equals(_ta.id))
						{
							_hasStudent = true;
							_studentName = _queueinfo.students[i].username;
							_currentstudentid = _queueinfo.students[i].id;
							studentmessage += _studentName;
							_buttonaccept.setText("Remove");
						}
					}
				}
			}
		}
	
		if(_students.size()==0)
			_students.add("=== No Student ===");
		if(_queueinfo.forzen)
		{
			tamessage += "[Frozen]";
			_frozen = true;
			_buttonforzen.setText("Unfreeze");
		}
		else
		{
			_frozen = false;
			_buttonforzen.setText("Freeze");
		}
		if(!_queueinfo.active)
		{
			_active = false;
			_buttonactive.setText("Active");
			tamessage += "[InActive]";
		}
		else
		{
			_buttonactive.setText("Deactivate");
			_active = true;
		}
		
		if(!_hasStudent)
		{
			_buttonaccept.setText("Accept");
			_buttonputback.setVisibility(8);
			_buttonputback.setClickable(false);
		}
		else
		{
			_buttonaccept.setText("Remove");
			_buttonputback.setVisibility(0);
			_buttonputback.setClickable(true);
		}
		_taTitle.setText(tamessage);
		_studentTitle.setText(studentmessage);
		_progress.setVisibility(8);
		_buttonaccept.setClickable(true);
		_studentList.setAdapter(this);
	}

	//ListAdapter Methods
	@Override
	public int getCount() {
		System.out.println("SIZE"+_students.size());
		return _students.size();
	}

	@Override
	public Object getItem(int position) {
		return _students.get(position);
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
		String temp = _students.get(position);
		TextView sname = (TextView) layout.findViewById(R.id.single_item_text);
		sname.setText(temp);
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
		return _students.size() == 0;
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




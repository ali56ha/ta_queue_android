package com.urails.ta_queue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.json.JSONObject;

import com.google.gson.Gson;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.urails.ta_queue.model.QueueInfo;
import com.urails.ta_queue.model.QueueItem;
import com.urails.ta_queue.model.SchoolNames;
import com.urails.ta_queue.model.Student;
import com.urails.ta_queue.model.TaQueueConnectorHttp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class TAQueueInfo extends Activity implements ListAdapter{
	public static final String USER = "USER";
	public static final String QUEUE= "QUEUE";
	private TaQueueConnectorHttp _connector;
	private QueueInfo _queueinfo;
	private Student _student;
	private QueueItem _queue;
	RequestParams _params;
	private int _duration;
	private TextView _taTitle;
	private TextView _studentTitle;
	private ListView _studentList;
	private boolean _inQ;
	private Button _enterButton;
	private boolean _active;
	private ArrayList<String> _students;
	private boolean _success;
	private boolean _enterQSuccess;
	private boolean _exitQSuccess;
	private Context _context;
	private CharSequence _text;
	private RelativeLayout _progress;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		System.out.println("Get QQQQQQQ INFO");

		setContentView(R.layout.student_queue);
		_student = (Student)getIntent().getExtras().get(USER);
		_queue = (QueueItem)getIntent().getExtras().get(QUEUE);
		_inQ = false;
		_enterQSuccess = false;
		_exitQSuccess = false;
		_success = false;
		_connector = new TaQueueConnectorHttp();
		_taTitle = (TextView) findViewById(R.id.studnet_ta_queue_title);
		_studentTitle = (TextView) findViewById(R.id.studnet_queue_title);
		_studentList = (ListView) findViewById(R.id.student_queue_listview);
		_enterButton = (Button) findViewById(R.id.button_studnet_queue);
		_progress = (RelativeLayout) findViewById(R.id.studentovedrlay);
		_studentTitle.setText("Students");
		_context = getApplicationContext();
		_duration = Toast.LENGTH_SHORT;
		getQueueInfo();
		
		_enterButton.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				_progress.setVisibility(0);
				if(_inQ)
					exitQueue();
				else if(_active && !_inQ)
					enterQueue();
				else
					getQueueInfo();
			}
		});
	}
	
	private void enterQueue(){
		_params = new RequestParams();
		_connector.setAuthget(_student.id, _student.token,"/queue/enter_queue?question=I+need+help", _params, new JsonHttpResponseHandler(){
			
			@Override
			public void onSuccess(JSONObject result) {
				super.onSuccess(result);
				System.out.println("Enter QQQ " + result.toString());
				_inQ = true;
				getQueueInfo();
			}
			@Override
	        public void onFailure(Throwable arg0, String arg1) {
	        	System.out.println("ERROR " + arg1);
	           	super.onFailure(arg0, arg1);
	        }
		});
		System.out.println("Enter Success : " + _enterQSuccess);
	}
	
	private void exitQueue(){
		_params = new RequestParams();
		_connector.setAuthget(_student.id, _student.token,"/queue/exit_queue", _params, new JsonHttpResponseHandler(){
			
			@Override
			public void onSuccess(JSONObject result) {
				super.onSuccess(result);
				System.out.println("Exit QQQ " + result.toString());
				_inQ = false;
				getQueueInfo();
			}
			@Override
	        public void onFailure(Throwable arg0, String arg1) {
	        	System.out.println("ERROR String " + arg1);
	           	super.onFailure(arg0, arg1);
	        }
		});
		System.out.println("Exit Success : " + _exitQSuccess);
	}
	
	
	private void getQueueInfo()
	{
		_students = new ArrayList<String>();
		_params = new RequestParams();
		_connector.setAuthget(_student.id, _student.token,"/queue", _params, new JsonHttpResponseHandler(){
			
			@Override
			public void onSuccess(JSONObject result) {
				super.onSuccess(result);
				System.out.println("JSONOBJECY" + result.toString());
        		_success = true;
				try{
					Gson gson = new Gson();
            		_queueinfo = (QueueInfo) gson.fromJson(result.toString(), QueueInfo.class);
            		processQueue();
				}
				catch(Exception e)
				{
					System.out.println("ERROR " + e);
				}
			}
			@Override
	        public void onFailure(Throwable arg0, String arg1) {
	        	System.out.println("ERROR " + arg1);
	           	super.onFailure(arg0, arg1);
	        }
		});
		System.out.println("Success : " + _success);
	}
	
	private void processQueue()
	{
		System.out.println("Success : " + _success);
		System.out.println("in the PROCESS");
		if(!_success)
		{
			_text = "ERROR Your login might be timeout. Please Login again";
			Toast.makeText(_context, _text, _duration).show();
			Intent intent = new Intent();
			intent.setClass(TAQueueInfo.this, TAQueueMain.class);
			setResult((int)2, intent);
			finish();
		}
		int tacount = _queueinfo.tas.length;
		int scount = _queueinfo.students.length;
		String tamessage = "TA: ";
		for(int i=0; i<tacount; i++)
		{
			tamessage+=_queueinfo.tas[i].username;
			if(i < tacount-1)
				tamessage+=", ";
		}
		
		if(tacount==0)
			tamessage = "No TA";
		for(int i = 0; i <scount; i++)
		{
			if(_queueinfo.students[i].in_queue)
			{
				if(_queueinfo.students[i].ta_id == null)
					_students.add(_queueinfo.students[i].username + "@" + _queueinfo.students[i].location);
				else
					_students.add(_queueinfo.students[i].username + "@" + _queueinfo.students[i].location + "  with TA");
				
				if(_queueinfo.students[i].username.equals(_student.username))
					_inQ = _queueinfo.students[i].in_queue;
				
				System.out.println("This is the Student"+_students.toString());
			}
		}
		
		if(_queueinfo.forzen)
		{
			tamessage += "[Frozen]";
			_active = false;
		}
		else if(_queueinfo.active)
			_active = true;
			
		if(!_queueinfo.active)
		{
			tamessage += "[InActive]";
			_active = false;
		}
		else if(!_queueinfo.forzen)
			_active = true;
		
		if(_active)
		{
			_enterButton.setVisibility(0);
			if(!_inQ)
				_enterButton.setText("Enter");
			else
				_enterButton.setText("Leave");
			_enterButton.setClickable(true);
		}
		else
		{
			if(!_inQ)
			{
				_enterButton.setVisibility(8);
				_enterButton.setClickable(false);
			}
			else
			{
				_enterButton.setVisibility(0);
				_enterButton.setText("Leave");
				_enterButton.setClickable(true);
			}
		}
		if(_students.size()==0)
			_students.add("=== No Student ===");
		_taTitle.setText(tamessage);
		_studentList.setAdapter(this);
		_progress.setVisibility(8);
		
	}

	//ListAdapter Methods
		@Override
		public int getCount() {
			System.out.println(_students.size());
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

package com.urails.ta_queue;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.urails.ta_queue.model.QueueItem;
import com.urails.ta_queue.model.Student;
import com.urails.ta_queue.model.TA;
import com.urails.ta_queue.model.TaQueueConnectorHttp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class TAQueueLogin extends Activity {
	public static final String LOGIN = "LOGIN";
	public static final String QUEUES= "QUEUES";
	private TaQueueConnectorHttp _connector;
	private ArrayList<QueueItem> _queues;
	private Student _student;
	private TA _ta;
	private QueueItem _queue;
	private Button _loginButton;
	private RadioButton _taRadio;
	private RadioButton _studentRadio;
	private EditText _usernameEdit;
	private EditText _locationEdit;
	private EditText _passwordEdit;
	private String _username;
	private String _location;
	private String _password;
	private Context _context;
	private CharSequence _text;
	RequestParams _params;
	private int _duration;
	private String _url;
	private RelativeLayout _progress;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		_queues = (ArrayList<QueueItem>)getIntent().getExtras().get(QUEUES);
		_queue = (QueueItem)getIntent().getExtras().get(LOGIN);
		_connector = new TaQueueConnectorHttp();
		_loginButton = (Button) findViewById(R.id.login_button);
		_taRadio = (RadioButton) findViewById(R.id.login_ta_radio);
		_studentRadio = (RadioButton) findViewById(R.id.login_student_radio);
		_usernameEdit = (EditText) findViewById(R.id.login_username);
		_locationEdit = (EditText) findViewById(R.id.login_location);
		_passwordEdit = (EditText) findViewById(R.id.login_password);
		_progress = (RelativeLayout) findViewById(R.id.loginovedrlay);
		_context = getApplicationContext();
		_duration = Toast.LENGTH_SHORT;
		_progress.setVisibility(8);

		_studentRadio.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				_passwordEdit.setVisibility(8);
				_locationEdit.setVisibility(0);
			}
		});
		
		_taRadio.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				_passwordEdit.setVisibility(0);
				_locationEdit.setVisibility(8);
			}
		});
		
		_loginButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				_username = _usernameEdit.getText().toString();
				_text = "username is empty";
				if(!_username.equals(""))
				{
					_loginButton.setClickable(false);
					if(_taRadio.isChecked())
						taLogin();
					else
						studentLogin();
				}
				else
					Toast.makeText(_context, _text, _duration).show();
			}
		});
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (resultCode) {
			case 1:
				Intent intent = new Intent();
    			intent.setClass(TAQueueLogin.this, TAQueueMain.class);
    			setResult((int)2, intent);
    			finish();
	        break;
			case 2:
				_queues = (ArrayList<QueueItem>)getIntent().getExtras().get(QUEUES);
				_queue = (QueueItem)getIntent().getExtras().get(LOGIN);
				_usernameEdit.setText("");
				_locationEdit.setText("");
				_passwordEdit.setText("");
				_username = _usernameEdit.getText().toString();
				_password = _passwordEdit.getText().toString();
				_location = _locationEdit.getText().toString();
    			_progress.setVisibility(8);
				break;
	      default:
	        break;	
		}
	}
	private void taLogin()
	{
		_password = _passwordEdit.getText().toString();
		_text = "Password is empty!";
		_url = _queue._loginurl+"/tas";
		_params = new RequestParams();
		if(!_password.equals(""))
		{
			_progress.setVisibility(0);
			_params.put("ta[username]", _username);
			_params.put("ta[password]", _password);
			_connector.post(_url, _params, new JsonHttpResponseHandler(){
				@Override
				public void onSuccess(JSONObject result) {
					super.onSuccess(result);
					try{
						Gson gson = new Gson();
	            		_ta = (TA) gson.fromJson(result.toString(), TA.class);
	            		_loginButton.setClickable(true);
		    			_progress.setVisibility(8);
	            		Intent intent = new Intent();
	        			intent.setClass(TAQueueLogin.this, TAQueueTaInfo.class);
	        			intent.putExtra(TAQueueTaInfo.USER, _ta);
	        			intent.putExtra(TAQueueTaInfo.QUEUE, _queue);		
	        			intent.putExtra(TAQueueTaInfo.QUEUES, _queues);		
	        			startActivityForResult(intent, (int)2);
					}
					catch(Exception e)
					{
						System.out.println("ERROR Exception TA LOGIN" + e.toString());
						Intent intent = new Intent();
						_loginButton.setClickable(true);
		    			_progress.setVisibility(8);
						intent.setClass(TAQueueLogin.this, TAQueueMain.class);
	        			setResult((int)2, intent);
	        			finish();
					}
				}
				
				@Override
				public void onFailure(Throwable arg0, String arg1) {
					System.out.println("ERROR Fail TA LOGIn" + arg1);
		           	super.onFailure(arg0, arg1);
		           	if(arg1.contains("authorized"))
		           	{
		           		_text = "Wrong Password";
		           		Toast.makeText(_context, _text, _duration).show();
		    			_loginButton.setClickable(true);
		    			_usernameEdit.setText("");
						_locationEdit.setText("");
						_passwordEdit.setText("");
						_username = _usernameEdit.getText().toString();
						_password = _passwordEdit.getText().toString();
						_location = _locationEdit.getText().toString();
						_loginButton.setClickable(true);
		    			_progress.setVisibility(8);
		           	}
		           	else
		           	{
		           		Intent intent = new Intent();
		           		intent.setClass(TAQueueLogin.this, TAQueueMain.class);
		           		setResult((int)2, intent);
		           		finish();
		           	}
				}

			});
		}
		else
		{
			Toast.makeText(_context, _text, _duration).show();
			_loginButton.setClickable(true);
			_progress.setVisibility(8);
		}
	}
	
	private void studentLogin()
	{
		_location = _locationEdit.getText().toString();
		_text = "Location is empty!";
		_url = _queue._loginurl+"/students";
		_params = new RequestParams();
		if(!_location.equals(""))
		{
			_progress.setVisibility(0);
			_params.put("student[username]", _username);
			_params.put("student[location]", _location);
			_connector.post(_url, _params, new JsonHttpResponseHandler(){
				@Override
				public void onSuccess(JSONObject result) {
					super.onSuccess(result);
					try{
						Gson gson = new Gson();
	            		_student = (Student) gson.fromJson(result.toString(), Student.class);
	            		_student.location = _location;
	            		_progress.setVisibility(8);
	        			Intent intent = new Intent();
	        			intent.setClass(TAQueueLogin.this, TAQueueInfo.class);
	        			intent.putExtra(TAQueueInfo.USER, _student);
	        			intent.putExtra(TAQueueInfo.QUEUE, _queue);	
	        			intent.putExtra(TAQueueInfo.QUEUES, _queues);		
	        			startActivityForResult(intent, (int)2);
					}
					catch(Exception e)
					{
						System.out.println("ERROR STUDENTLOGIN" + e.toString());
						Intent intent = new Intent();
	        			intent.setClass(TAQueueLogin.this, TAQueueMain.class);
	        			setResult((int)2, intent);
	        			finish();
					}
				}
				@Override
		        public void onFailure(Throwable arg0, JSONObject arg1) {
		        	System.out.println("ERROR FAIL Student LOGIN" + arg1.toString());
		        	Intent intent = new Intent();
		           	super.onFailure(arg0, arg1);
		           	intent.setClass(TAQueueLogin.this, TAQueueMain.class);
        			setResult((int)2, intent);
        			finish();
		        }
			});
			_loginButton.setClickable(true);
			_progress.setVisibility(8);
		}
		else
		{
			Toast.makeText(_context, _text, _duration).show();
			_loginButton.setClickable(true);
			_progress.setVisibility(8);
		}
	}
}

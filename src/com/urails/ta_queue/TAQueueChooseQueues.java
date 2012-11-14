package com.urails.ta_queue;

import java.util.ArrayList;
import com.urails.ta_queue.model.QueueItem;

import android.app.Activity;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class TAQueueChooseQueues extends Activity implements ListAdapter {
	public static final String CHOOSE_QUEUE = "CHOOSEQUEUE";
	private Button _settings;
	private ListView _mainList;
	private ArrayList<QueueItem> _queues;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		_mainList = (ListView) findViewById(R.id.main_listview);
		_settings = (Button) findViewById(R.id.main_setting_button);
		_queues = (ArrayList<QueueItem>)getIntent().getExtras().get(CHOOSE_QUEUE);
		
		_mainList.setAdapter(this);
		
//		_settings.setOnClickListener(new OnClickListener() {
//		@Override
//		public void onClick(View v) {
//			//go to setting
//		}});
//	
//	_mainList.setOnItemClickListener(new OnItemClickListener(){
//
//		@Override
//		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
//				long arg3) {
//			// TODO Auto-generated method stub
//			
//		}
//	});
	}

	
	//ListAdapter Methods
	@Override
	public int getCount() {
		return _queues.size();
	}


	@Override
	public Object getItem(int position) {
		return _queues.get(position);
	}


	@Override
	public long getItemId(int position) {
		return position;
	}


	@Override
	public int getItemViewType(int position) {
		return 1;
	}


	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View layout = null;
		if(convertView != null)
			layout = convertView;
		else
			layout = getLayoutInflater().inflate(R.layout.main_list, null);
		
		QueueItem queueTemp = _queues.get(position);
		TextView cname = (TextView) layout.findViewById(R.id.main_list_classname);
		TextView cinfo = (TextView) layout.findViewById(R.id.main_list_classinfo);
		cname.setText(queueTemp._classname);
		cinfo.setText(queueTemp._instructorname);
		return layout;
	}


	@Override
	public int getViewTypeCount() {
		return 1;
	}


	@Override
	public boolean hasStableIds() {
		return true;
	}


	@Override
	public boolean isEmpty() {
		return _queues.size()==0;
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

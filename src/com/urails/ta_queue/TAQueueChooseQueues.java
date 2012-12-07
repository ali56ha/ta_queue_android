package com.urails.ta_queue;

import java.util.ArrayList;
import com.urails.ta_queue.model.QueueItem;

import android.app.Activity;
import android.content.Intent;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class TAQueueChooseQueues extends Activity implements ListAdapter {
	public static final String CHOOSE_QUEUE = "CHOOSEQUEUE";
	private ListView _mainList;
	private ArrayList<QueueItem> _queues;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		_mainList = (ListView) findViewById(R.id.main_listview);
		_queues = (ArrayList<QueueItem>)getIntent().getExtras().get(CHOOSE_QUEUE);	
		_mainList.setAdapter(this);
		_mainList.setOnItemClickListener(new OnItemClickListener(){

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int position,
				long id) {
			QueueItem temp = _queues.get(position);
			Intent intent = new Intent();
			intent.setClass(TAQueueChooseQueues.this, TAQueueLogin.class);
			intent.putExtra(TAQueueLogin.LOGIN, temp);
			intent.putExtra(TAQueueLogin.QUEUES, _queues);
			startActivityForResult(intent, (int)2);
		}
	});
	}

	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	super.onActivityResult(requestCode, resultCode, data);
    	switch (resultCode) {
    		case 2:
    			_queues = null;
    			finish();
    			break;
    		default:
    			break;	
    	}
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

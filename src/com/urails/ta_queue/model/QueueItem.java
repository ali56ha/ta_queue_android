package com.urails.ta_queue.model;

import java.io.Serializable;

public class QueueItem implements Serializable{
	private static final long serialVersionUID = -3988824644282546936L;
	public String _classname;
	public String _instructorname;
	public String _loginurl;
	public boolean _active;
	public boolean _frozen;
	public QueueItem(String c, String i, String u, boolean a, boolean f) {
		_classname = c;
		_instructorname = i;
		_loginurl = u;
		_active = a;
		_frozen = f;
	}
}

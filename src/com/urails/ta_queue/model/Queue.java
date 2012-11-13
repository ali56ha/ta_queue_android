package com.urails.ta_queue.model;

public class Queue {

	public Queue() {}

	public boolean active;
	public boolean frozen;
	public String class_number;
	public String title;
	
	public boolean getActive()
	{
		return active;
	}
	
	public boolean getFrozen()
	{
		return frozen;
	}
}

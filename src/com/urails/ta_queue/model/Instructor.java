package com.urails.ta_queue.model;

public class Instructor {

	public Instructor() {}

	public String name;
	public String username;
	public Queue[] queues;
	
	public Queue[] getQueues()
	{
		return queues;
	}
	
	public String getInstructorName()
	{
		return name;
	}
	
	public String getInstructorUsername()
	{
		return username;
	}
}

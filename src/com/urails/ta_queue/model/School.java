package com.urails.ta_queue.model;

public class School {

	public School() {}
	
	public Instructor[] instructors;
	public String name;
	public String abbreviation;
	
	public Instructor[] getInstructors()
	{
		return instructors;
	}
}

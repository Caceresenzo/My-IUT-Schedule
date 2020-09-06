package caceresenzo.apps.iutschedule.models;

public class Student {
	
	/* Variables */
	private final long id;
	
	/* Constructor */
	public Student(long id) {
		this.id = id;
	}
	
	/** @return Student's card's id. */
	public long getId() {
		return id;
	}
	
}
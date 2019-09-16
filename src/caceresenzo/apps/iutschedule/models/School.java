package caceresenzo.apps.iutschedule.models;

public class School {
	
	/* Variables */
	private final String code, name;
	
	/* Constructor */
	public School(String code, String name) {
		this.code = code;
		this.name = name;
	}
	
	/** @return School's server code. */
	public String getCode() {
		return code;
	}
	
	/** @return School's name. */
	public String getName() {
		return name;
	}
	
}
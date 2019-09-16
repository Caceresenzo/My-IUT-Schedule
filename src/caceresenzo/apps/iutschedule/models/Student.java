package caceresenzo.apps.iutschedule.models;

import caceresenzo.libs.json.JsonAware;
import caceresenzo.libs.json.JsonObject;

public class Student implements JsonAware {
	
	/* Json Key */
	public static final String JSON_KEY_ID = "id";
	
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
	
	@Override
	public String toJsonString() {
		JsonObject jsonObject = new JsonObject();
		
		jsonObject.put(JSON_KEY_ID, id);
		
		return jsonObject.toJsonString();
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (id ^ (id >>> 32));
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Student other = (Student) obj;
		if (id != other.id)
			return false;
		return true;
	}
	
	/**
	 * Create a {@link Student student} instance from a {@link JsonObject JSON object}.
	 * 
	 * @param jsonObject
	 *            Source {@link JsonObject JSON object}.
	 * @return A {@link Student student} instance or <code>null</code> if the <code>id</code> or the <code>name</code> is not valid.
	 */
	public static Student fromJsonObject(JsonObject jsonObject) {
		long id = (long) jsonObject.getOrDefault(JSON_KEY_ID, -1);
		
		if (id == -1) {
			return null;
		}
		
		return new Student(id);
	}
	
}
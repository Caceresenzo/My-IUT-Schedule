package caceresenzo.apps.iutschedule.managers.implementations;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.util.Log;
import caceresenzo.apps.iutschedule.activities.IntroActivity;
import caceresenzo.apps.iutschedule.managers.AbstractManager;
import caceresenzo.apps.iutschedule.models.Student;
import caceresenzo.libs.filesystem.FileUtils;
import caceresenzo.libs.json.JsonArray;
import caceresenzo.libs.json.JsonObject;
import caceresenzo.libs.json.parser.JsonException;
import caceresenzo.libs.json.parser.JsonParser;
import caceresenzo.libs.string.StringUtils;

public class StudentManager extends AbstractManager {
	
	/* Json Key */
	public static final String JSON_KEY_STUDENTS = "students";
	public static final String JSON_KEY_SELECTED_STUDENT_ID = "selected_id";
	
	/* Tag */
	public static final String TAG = StudentManager.class.getSimpleName();
	
	/* Singleton */
	private static StudentManager INSTANCE;
	
	/* User */
	private final List<Student> students;
	private Student selectedStudent;
	
	/* Variables */
	private final File savingFile;
	
	/* Private Constructor */
	private StudentManager() {
		super();
		
		this.students = new ArrayList<>();
		this.savingFile = new File(getDataDirectory(), "students.json");
	}
	
	@Override
	public void initialize() {
		super.initialize();
		
		loadStudents();
		
		if (isStudentSetup()) {
			selectStudent(selectedStudent);
		}
	}
	
	@Override
	public void step() {
		saveStudents();
	}
	
	/**
	 * Validate the current config and do action to resolve problems.
	 * 
	 * @return Weather or not the setup has been fully validated without any problems (or minor that has been solved instantly).
	 */
	public boolean validateSetup() {
		if (!isStudentSetup()) {
			IntroActivity.start(false);
			
			return false;
		}
		
		return true;
	}
	
	/** Load previously saved students. */
	private void loadStudents() {
		students.clear();
		
		try {
			FileUtils.forceFileCreation(savingFile);
			
			String fileContent = StringUtils.fromFile(savingFile);
			
			if (StringUtils.validate(fileContent)) {
				JsonObject jsonObject = (JsonObject) new JsonParser().parse(fileContent);
				
				JsonArray studentsJsonArray = (JsonArray) jsonObject.get(JSON_KEY_STUDENTS);
				long selectedStudentId = (long) jsonObject.getOrDefault(JSON_KEY_SELECTED_STUDENT_ID, -1);
				
				for (Object item : studentsJsonArray) {
					Student student = Student.fromJsonObject((JsonObject) item);
					
					if (student != null) {
						students.add(student);
						
						if (selectedStudentId == -1 || selectedStudentId == student.getId()) {
							selectedStudent = student;
						}
					}
				}
			}
		} catch (Exception exception) {
			Log.w(TAG, "Failed to load students.", exception);
			
			if (exception instanceof JsonException) {
				savingFile.delete();
			}
		}
	}
	
	/** Save students to a JSON form. */
	private void saveStudents() {
		try {
			JsonObject jsonObject = new JsonObject();
			
			JsonArray studentJsonArray = new JsonArray();
			studentJsonArray.addAll(students);
			
			jsonObject.put(JSON_KEY_STUDENTS, studentJsonArray);
			jsonObject.put(JSON_KEY_SELECTED_STUDENT_ID, selectedStudent != null ? selectedStudent.getId() : -1);
			
			FileUtils.writeStringToFile(jsonObject.toJsonString(), savingFile);
		} catch (Exception exception) {
			Log.w(TAG, "Failed to save students.", exception);
		}
	}
	
	/**
	 * Change the selected user and so, the displayed calendar.<br>
	 * This will also {@link VirtualCalendarManager#invalidateCurrentCalendar() invalidate} the current calendar.
	 * 
	 * @param student
	 *            New {@link Student student}.
	 */
	public void selectStudent(Student student) {
		this.selectedStudent = student;
		
		if (!students.contains(student)) {
			students.add(student);
		}
		
		VirtualCalendarManager.get().invalidateCurrentCalendar();
		VirtualCalendarManager.get().refreshCalendar();
	}
	
	/** @return Selected {@link Student student} that will get his calendar notified. */
	public Student getSelectedStudent() {
		return selectedStudent;
	}
	
	/**
	 * @return Weather or not a {@link Student student} has aready been setup or not.<br>
	 *         If not, then the application should start the configuration sequence.
	 */
	public boolean isStudentSetup() {
		return selectedStudent != null;
	}
	
	/** @return UserManager's singleton instance. */
	public static final StudentManager get() {
		if (INSTANCE == null) {
			INSTANCE = new StudentManager();
		}
		
		return INSTANCE;
	}
	
}
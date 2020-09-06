package caceresenzo.apps.iutschedule.managers.implementations;

import caceresenzo.apps.iutschedule.activities.IntroActivity;
import caceresenzo.apps.iutschedule.application.ScheduleApplication;
import caceresenzo.apps.iutschedule.managers.AbstractManager;
import caceresenzo.apps.iutschedule.models.Student;

public class StudentManager extends AbstractManager {

	/* Shared Preferences Keys */
	public static final String SHARED_PREF_KEY_STUDENT_ID = "current_student_id";
	
	/* Tag */
	public static final String TAG = StudentManager.class.getSimpleName();
	
	/* Singleton */
	private static StudentManager INSTANCE;
	
	/* User */
	private Student student;
	
	/* Private Constructor */
	private StudentManager() {
		super();
	}
	
	@Override
	public void initialize() {
		super.initialize();
		
		loadStudent();
		
		if (isStudentSetup()) {
			selectStudent(student);
		}
	}
	
	@Override
	public void step() {
		saveStudent();
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
	private void loadStudent() {
		long studentId = ScheduleApplication.get().getSharedPreferences().getLong(SHARED_PREF_KEY_STUDENT_ID, -1);

		if (studentId != -1) {
			this.student = new Student(studentId);
		}
	}
	
	/** Save students to a JSON form. */
	private void saveStudent() {
		if (student != null) {
			ScheduleApplication.get().getSharedPreferences()
					.edit()
					.putLong(SHARED_PREF_KEY_STUDENT_ID, student.getId())
					.commit();
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
		this.student = student;
		
		VirtualCalendarManager.get().invalidateCurrentCalendar();
		VirtualCalendarManager.get().refreshCalendar();
	}
	
	/** @return Selected {@link Student student} that will get his calendar notified. */
	public Student getSelectedStudent() {
		return student;
	}
	
	/**
	 * @return Weather or not a {@link Student student} has already been setup or not.<br>
	 *         If not, then the application should start the configuration sequence.
	 */
	public boolean isStudentSetup() {
		return student != null;
	}
	
	/** @return UserManager's singleton instance. */
	public static final StudentManager get() {
		if (INSTANCE == null) {
			INSTANCE = new StudentManager();
		}
		
		return INSTANCE;
	}
	
}
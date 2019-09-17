package caceresenzo.apps.iutschedule.fragments.intro.sliders;

import agency.tango.materialintroscreen.SlideFragment;
import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import caceresenzo.android.libs.input.InputMethodUtils;
import caceresenzo.android.libs.internet.NetworkUtils;
import caceresenzo.apps.iutschedule.R;
import caceresenzo.apps.iutschedule.application.ScheduleApplication;
import caceresenzo.apps.iutschedule.managers.implementations.StudentManager;
import caceresenzo.apps.iutschedule.managers.implementations.VirtualCalendarManager;
import caceresenzo.apps.iutschedule.models.Student;
import caceresenzo.apps.iutschedule.utils.listeners.OnCalendarDownloadListener;
import caceresenzo.apps.iutschedule.utils.listeners.OnNewCalendarListener;
import caceresenzo.libs.parse.ParseUtils;

public class AccountConfigurationIntroSlide extends SlideFragment implements OnNewCalendarListener, OnCalendarDownloadListener {
	
	/* Singleton */
	private static AccountConfigurationIntroSlide INSTANCE;
	
	/* Views */
	private EditText studentIdEditText;
	private Button checkButton;
	private TextView currentStateTextView;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		final View view = inflater.inflate(R.layout.fragment_slide_account_configuration, container, false);
		
		studentIdEditText = view.findViewById(R.id.fragment_slide_account_configuration_edittext_student_id);
		checkButton = view.findViewById(R.id.fragment_slide_account_configuration_button_check);
		currentStateTextView = view.findViewById(R.id.fragment_slide_account_configuration_textview_current_state);
		
		studentIdEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView textView, int actionId, KeyEvent event) {
				doCheck();
				return true;
			}
		});
		
		checkButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				doCheck();
			}
		});
		
		if (StudentManager.get().isStudentSetup()) {
			publishCurrentCalendar();
		}
		
		return view;
	}
	
	@Override
	public void onAttach(Context context) {
		super.onAttach(context);
		
		INSTANCE = this;
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		
		if (INSTANCE == this) {
			INSTANCE = null;
		}
	}
	
	/**
	 * Publish a state to the current state {@link TextView text view}.
	 * 
	 * @param stringRessource
	 *            String ressource id of the state.
	 * @param reason
	 *            Reason of the state (often for errors).
	 * @deprecated Need to be replaced with something better.
	 */
	private void publishState(int stringRessource, String reason) {
		if (reason == null) {
			reason = "";
		}
		
		String text = String.format("%s: %s\n%s", getString(R.string.intro_account_state), getString(stringRessource), reason);
		
		currentStateTextView.setText(text);
	}
	
	/**
	 * Enable or disable both the student id {@link EditText edit text} and check {@link Button button}.<br>
	 * And will also hide the keyboard if the <code>enabled</code> state is set to <code>false</code>.
	 * 
	 * @param enabled
	 *            Enabled state.
	 * @see InputMethodUtils#hideKeyboard(android.app.Activity) Hide the keyboard
	 */
	private void enableInputs(boolean enabled) {
		studentIdEditText.setEnabled(enabled);
		checkButton.setEnabled(enabled);
		
		if (!enabled) {
			InputMethodUtils.hideKeyboard(getActivity());
		}
	}
	
	/**
	 * Do all the check work.<br>
	 * This mean checking if there are any internet connection, if the student's id can be parsed to an int and after that, a request to fetch the corresponding clendar will be done.
	 */
	private void doCheck() {
		if (!NetworkUtils.isConnected(ScheduleApplication.get())) {
			publishState(R.string.intro_account_state_error, "NO INTERNET");
			
			return;
		}
		
		int studentId = ParseUtils.parseInt(studentIdEditText.getText().toString(), -1);
		
		if (studentId == -1) {
			publishState(R.string.intro_account_state_error, "INVALID NUMBER");
		} else {
			StudentManager.get().selectStudent(new Student(studentId));
			VirtualCalendarManager.get().refreshCalendar();
		}
	}
	
	@Override
	public void onCalendarDownloadStarted() {
		enableInputs(false);
		
		publishState(R.string.intro_account_state_checking, null);
	}
	
	@Override
	public void onCalendarDownloadFailed() {
		enableInputs(true);
		
		publishState(R.string.intro_account_state_error, "FAILED");
	}
	
	@Override
	public void onNewCalendar() {
		enableInputs(true);
		
		publishCurrentCalendar();
	}
	
	/** Publish the valid state with the current calendar. */
	private void publishCurrentCalendar() {
		publishState(R.string.intro_account_state_valid, VirtualCalendarManager.get().getCurrentVirtualCalendar().getName());
	}
	
	@Override
	public int backgroundColor() {
		return R.color.colorPrimary;
	}
	
	@Override
	public int buttonsColor() {
		return R.color.colorAccent;
	}
	
	@Override
	public boolean canMoveFurther() {
		return VirtualCalendarManager.get().getCurrentVirtualCalendar() != null;
	}
	
	@Override
	public String cantMoveFurtherErrorMessage() {
		return getString(R.string.intro_account_error_invalid);
	}
	
	/** @return AccountConfigurationIntroSlide's singleton instance. */
	public static final AccountConfigurationIntroSlide get() {
		return INSTANCE;
	}
	
}
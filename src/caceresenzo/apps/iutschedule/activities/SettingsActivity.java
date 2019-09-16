package caceresenzo.apps.iutschedule.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import caceresenzo.apps.iutschedule.R;
import caceresenzo.apps.iutschedule.application.ScheduleApplication;
import caceresenzo.apps.iutschedule.fragments.other.PreferencesFragment;

public class SettingsActivity extends AppCompatActivity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);
		
		initializeViews();
	}
	
	/** Initialize android's views. */
	private void initializeViews() {
		Toolbar toolbar = findViewById(R.id.activity_settings_toolbar_bar);
		setSupportActionBar(toolbar);
		getSupportActionBar().setElevation(0);
		
		getSupportFragmentManager()
				.beginTransaction()
				.replace(R.id.activity_settings_framelayout_container_main, new PreferencesFragment())
				.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
				.commit();
	}
	
	/** Quickly start a {@link SettingsActivity}. */
	public static void start() {
		ScheduleApplication application = ScheduleApplication.get();
		
		Intent intent = new Intent(application, SettingsActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		
		application.startActivity(intent);
	}
	
}
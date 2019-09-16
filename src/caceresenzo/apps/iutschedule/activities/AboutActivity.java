package caceresenzo.apps.iutschedule.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import caceresenzo.apps.iutschedule.R;
import caceresenzo.apps.iutschedule.application.ScheduleApplication;
import caceresenzo.apps.iutschedule.fragments.other.AboutFragment;

public class AboutActivity extends AppCompatActivity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);
		
		initializeViews();
	}
	
	/** Initialize android's views. */
	private void initializeViews() {
		Toolbar toolbar = findViewById(R.id.activity_about_toolbar_bar);
		setSupportActionBar(toolbar);
		getSupportActionBar().setElevation(0);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setTitle(R.string.activity_about);
		
		getSupportFragmentManager()
				.beginTransaction()
				.replace(R.id.activity_about_framelayout_container, new AboutFragment())
				.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
				.commit();
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			super.onBackPressed();
			return true;
		}
		
		return super.onOptionsItemSelected(item);
	}
	
	/** Quickly start an {@link AboutActivity}. */
	public static void start() {
		ScheduleApplication application = ScheduleApplication.get();
		
		Intent intent = new Intent(application, AboutActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		
		application.startActivity(intent);
	}
	
}
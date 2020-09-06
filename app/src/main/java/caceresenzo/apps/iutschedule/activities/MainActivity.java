package caceresenzo.apps.iutschedule.activities;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentTransaction;

import caceresenzo.apps.iutschedule.R;
import caceresenzo.apps.iutschedule.fragments.schedule.ScheduleFragment;
import caceresenzo.apps.iutschedule.managers.implementations.StudentManager;

public class MainActivity extends AppCompatActivity {

	/* Tag */
	public static final String TAG = MainActivity.class.getSimpleName();

	/* Singleton */
	private static MainActivity INSTANCE;

	/* Constructor */
	public MainActivity() {
		super();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		INSTANCE = this;

		initializeViews();

		StudentManager.get().validateSetup();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		INSTANCE = null;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();

		switch (id) {
			case R.id.action_today:
			case R.id.action_refresh:
			case R.id.action_day_view:
			case R.id.action_three_day_view:
			case R.id.action_week_view: {
				return ScheduleFragment.get().onOptionsItemSelected(item);
			}

			case R.id.action_settings: {
				SettingsActivity.start();
				break;
			}

			case R.id.action_about: {
				AboutActivity.start();
				break;
			}

			default: {
				break;
			}
		}

		return super.onOptionsItemSelected(item);
	}

	/**
	 * Initialize android's views.
	 */
	private void initializeViews() {
		Toolbar toolbar = findViewById(R.id.activity_main_toolbar_bar);
		setSupportActionBar(toolbar);
		getSupportActionBar().setElevation(0);

		getSupportFragmentManager()
				.beginTransaction()
				.replace(R.id.activity_main_framelayout_container_main, new ScheduleFragment())
				.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
				.commit();
	}

	/**
	 * @return MainActivity's singleton instance.
	 */
	public static final MainActivity get() {
		return INSTANCE;
	}

}
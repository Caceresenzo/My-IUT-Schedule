package caceresenzo.apps.iutschedule.activities;

import java.lang.reflect.Field;

import agency.tango.materialintroscreen.MaterialIntroActivity;
import agency.tango.materialintroscreen.SlideFragmentBuilder;
import agency.tango.materialintroscreen.widgets.SwipeableViewPager;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import caceresenzo.apps.iutschedule.R;
import caceresenzo.apps.iutschedule.application.ScheduleApplication;
import caceresenzo.apps.iutschedule.fragments.intro.sliders.AccountConfigurationIntroSlide;
import caceresenzo.apps.iutschedule.fragments.intro.sliders.SchoolIntroSlide;
import caceresenzo.apps.iutschedule.managers.ScheduleManager;
import caceresenzo.apps.iutschedule.managers.implementations.StudentManager;;

public class IntroActivity extends MaterialIntroActivity {
	
	/* Tag */
	public static final String TAG = IntroActivity.class.getSimpleName();
	
	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		addSlide(new SlideFragmentBuilder()
				.backgroundColor(R.color.colorPrimary)
				.buttonsColor(R.color.colorAccent)
				.image(R.mipmap.icon_launcher_round)
				.title(getString(R.string.intro_welcome_title))
				.description(getString(R.string.intro_welcome_description))
				.build());
		
		addSlide(new SlideFragmentBuilder()
				.backgroundColor(R.color.colorPrimary)
				.buttonsColor(R.color.colorAccent)
				.image(R.drawable.icon_github_circle_white_24dp)
				.title(getString(R.string.intro_source_public_title))
				.description(getString(R.string.intro_source_public_description))
				.build());
		
		addSlide(new SchoolIntroSlide());
		
		addSlide(new AccountConfigurationIntroSlide());
		
		addSlide(new SlideFragmentBuilder()
				.backgroundColor(R.color.colorPrimary)
				.buttonsColor(R.color.colorAccent)
				.image(R.drawable.icon_check_bold_white_24dp)
				.title(getString(R.string.intro_finish_title))
				.description(getString(R.string.intro_finish_description))
				.build());
	}
	
	@Override
	public void onBackPressed() {
		if (!StudentManager.get().isStudentSetup()) {
			try {
				Field viewPagerField = getClass().getSuperclass().getDeclaredField("viewPager");
				viewPagerField.setAccessible(true);
				
				SwipeableViewPager viewPager = (SwipeableViewPager) viewPagerField.get(this);
				
				if (viewPager.getCurrentItem() == 0) {
					return;
				}
			} catch (Exception exception) {
				Log.i(TAG, "Failed to extract adapter field", exception);
			}
		}
		
		super.onBackPressed();
	}
	
	@Override
	public void onFinish() {
		super.onFinish();
		
		if (StudentManager.get().validateSetup()) {
			ScheduleManager.get().step();
		}
	}
	
	/** Quickly start an {@link IntroActivity}. */
	public static void start() {
		ScheduleApplication application = ScheduleApplication.get();
		
		Intent intent = new Intent(application, IntroActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		
		application.startActivity(intent);
	}
	
}
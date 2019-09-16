package caceresenzo.apps.iutschedule.managers.implementations;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import com.alamkanak.weekview.WeekViewEvent;

import android.os.AsyncTask;
import android.widget.Toast;
import caceresenzo.android.libs.internet.NetworkUtils;
import caceresenzo.apps.iutschedule.R;
import caceresenzo.apps.iutschedule.application.ScheduleApplication;
import caceresenzo.apps.iutschedule.fragments.intro.sliders.AccountConfigurationIntroSlide;
import caceresenzo.apps.iutschedule.fragments.schedule.ScheduleFragment;
import caceresenzo.apps.iutschedule.managers.AbstractManager;
import caceresenzo.apps.iutschedule.models.Student;
import caceresenzo.apps.iutschedule.services.ScheduleNotificationService;
import caceresenzo.apps.iutschedule.utils.AsyncTaskResult;
import caceresenzo.libs.iutschedule.calendar.VirtualCalendar;
import caceresenzo.libs.iutschedule.calendar.parser.VirtualCalendarRemoteParser;

public class VirtualCalendarManager extends AbstractManager {
	
	/* Singleton */
	private static VirtualCalendarManager INSTANCE;
	
	/* Variables */
	private final Map<Long, VirtualCalendar> calendars;
	private VirtualCalendar currentCalendar;
	private boolean downloading;
	
	/* Private Constructor */
	private VirtualCalendarManager() {
		super();
		
		this.calendars = new HashMap<>();
		this.downloading = false;
	}
	
	/** Invalidate the current {@link Calendar calendar}, usually to force a refresh. */
	public void invalidateCurrentCalendar() {
		this.currentCalendar = null;
	}
	
	/** Re-dowload the calendar but only if the network is connected. */
	public void refreshCalendar() {
		if (NetworkUtils.isConnected(application) && !downloading) {
			fetchCalendar(StudentManager.get().getSelectedStudent());
		}
	}
	
	public void fetchCalendar(final Student student) {
		new AsyncTask<Student, Void, AsyncTaskResult<VirtualCalendar>>() {
			@Override
			protected void onPreExecute() {
				downloading = true;
				
				ScheduleApplication.get().getHandler().post(new Runnable() {
					@Override
					public void run() {
						if (ScheduleFragment.get() != null) {
							ScheduleFragment.get().onCalendarDownloadStarted();
						}
						
						if (AccountConfigurationIntroSlide.get() != null) {
							AccountConfigurationIntroSlide.get().onCalendarDownloadStarted();
						}
					}
				});
			}
			
			@Override
			protected AsyncTaskResult<VirtualCalendar> doInBackground(Student... params) {
				try {
					return new AsyncTaskResult<>(new VirtualCalendarRemoteParser(student.getId()).parse());
				} catch (Exception exception) {
					return new AsyncTaskResult<>(exception);
				}
			}
			
			@Override
			protected void onPostExecute(AsyncTaskResult<VirtualCalendar> result) {
				VirtualCalendar virtualCalendar = result.getResult();
				
				downloading = false;
				
				if (virtualCalendar == null) {
					virtualCalendar = calendars.get(student.getId());
				}
				
				if (virtualCalendar == null) {
					String errorMessage = application.getString(R.string.error_failed_to_download_calendar, result.getException().getMessage());
					
					Toast.makeText(ScheduleApplication.get(), errorMessage, Toast.LENGTH_LONG).show();

					if (ScheduleFragment.get() != null) {
						ScheduleFragment.get().onCalendarDownloadFailed();
					}
					if (AccountConfigurationIntroSlide.get() != null) {
						AccountConfigurationIntroSlide.get().onCalendarDownloadFailed();
					}
				} else {
					currentCalendar = virtualCalendar;
					
					EventColorManager.get().onNewCalendar();
					
					if (ScheduleNotificationService.isRunning(application)) {
						ScheduleNotificationService.get().notifyNewCalendar();
					}
					if (ScheduleFragment.get() != null) {
						ScheduleFragment.get().onNewCalendar();
					}
					if (AccountConfigurationIntroSlide.get() != null) {
						AccountConfigurationIntroSlide.get().onNewCalendar();
					}
				}
			}
		}.execute(student);
	}
	
	/** @return Currently loaded {@link VirtualCalendar}. */
	public VirtualCalendar getCurrentVirtualCalendar() {
		return currentCalendar;
	}
	
	/** @return Weather the current calendar is being download at this instant or not. */
	public boolean isDownloading() {
		return downloading;
	}
	
	/**
	 * Checks if an event falls into a specific year and month.
	 * 
	 * @param event
	 *            The event to check for.
	 * @param year
	 *            The year.
	 * @param month
	 *            The month.
	 * @return True if the event matches the year and month.
	 */
	public static boolean eventMatches(WeekViewEvent event, int year, int month) {
		return (event.getStartTime().get(Calendar.YEAR) == year && event.getStartTime().get(Calendar.MONTH) == month - 1) || (event.getEndTime().get(Calendar.YEAR) == year && event.getEndTime().get(Calendar.MONTH) == month - 1);
	}
	
	/** @return VirtualCalendarManager's singleton instance. */
	public static final VirtualCalendarManager get() {
		if (INSTANCE == null) {
			INSTANCE = new VirtualCalendarManager();
		}
		
		return INSTANCE;
	}
	
}
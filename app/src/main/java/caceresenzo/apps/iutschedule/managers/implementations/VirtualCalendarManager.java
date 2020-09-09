package caceresenzo.apps.iutschedule.managers.implementations;

import android.os.AsyncTask;
import android.widget.Toast;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import caceresenzo.apps.iutschedule.R;
import caceresenzo.apps.iutschedule.application.ScheduleApplication;
import caceresenzo.apps.iutschedule.calendar.VirtualCalendar;
import caceresenzo.apps.iutschedule.calendar.VirtualCalendarEvent;
import caceresenzo.apps.iutschedule.calendar.parser.VirtualCalendarRemoteParser;
import caceresenzo.apps.iutschedule.fragments.intro.sliders.AccountConfigurationIntroSlide;
import caceresenzo.apps.iutschedule.fragments.schedule.ScheduleFragment;
import caceresenzo.apps.iutschedule.managers.AbstractManager;
import caceresenzo.apps.iutschedule.models.Student;
import caceresenzo.apps.iutschedule.services.ScheduleNotificationService;
import caceresenzo.apps.iutschedule.utils.AsyncTaskResult;
import caceresenzo.apps.iutschedule.utils.Utils;

public class VirtualCalendarManager extends AbstractManager {

	/* Singleton */
	private static VirtualCalendarManager INSTANCE;

	/* Variables */
	private VirtualCalendar currentCalendar;
	private boolean downloading;

	/* Private Constructor */
	private VirtualCalendarManager() {
		super();

		this.downloading = false;
	}

	/**
	 * Invalidate the current {@link Calendar calendar}, usually to force a refresh.
	 */
	public void invalidateCurrentCalendar() {
		this.currentCalendar = null;
	}

	/**
	 * Re-dowload the calendar but only if the network is connected.
	 */
	public void refreshCalendar() {
		if (Utils.hasInternetConnection(application) && !downloading) {
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

					if (isCalendarDownloadFailLoggingEnabled()) {
						Toast.makeText(ScheduleApplication.get(), application.getString(R.string.error_failed_to_download_calendar, result.getException().getMessage()), Toast.LENGTH_LONG).show();
					}

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

	/**
	 * @return Currently loaded {@link VirtualCalendar}.
	 */
	public VirtualCalendar getCurrentVirtualCalendar() {
		return currentCalendar;
	}

	/**
	 * @return Weather the current calendar is being download at this instant or not.
	 */
	public boolean isDownloading() {
		return downloading;
	}

	/**
	 * Checks if an event falls into a specific year and month.
	 *
	 * @param event The event to check for.
	 * @param year  The year.
	 * @param month The month.
	 * @return True if the event matches the year and month.
	 */
	public static boolean eventMatches(VirtualCalendarEvent event, int year, int month) {
		Calendar startTime = event.getStart().toCalendar();
		Calendar endTime = event.getEnd().toCalendar();

		return (startTime.get(Calendar.YEAR) == year && startTime.get(Calendar.MONTH) == month) || (endTime.get(Calendar.YEAR) == year && endTime.get(Calendar.MONTH) == month);
	}

	/**
	 * Get calendar download fail logging state from preferences.<br>
	 * The default value is <code>false</code> in case of an error.
	 *
	 * @return Whether or not the calendar fail should be toast.
	 */
	public static boolean isCalendarDownloadFailLoggingEnabled() {
		return Utils.fromConfig(R.string.pref_main_logging_calendar_download_fail_key, R.string.pref_main_logging_calendar_download_fail_default, Boolean::valueOf, false);
	}

	/**
	 * @return VirtualCalendarManager's singleton instance.
	 */
	public static final VirtualCalendarManager get() {
		if (INSTANCE == null) {
			INSTANCE = new VirtualCalendarManager();
		}

		return INSTANCE;
	}

}
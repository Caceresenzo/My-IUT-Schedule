package caceresenzo.apps.iutschedule.tasks;

import android.os.AsyncTask;
import android.widget.Toast;

import caceresenzo.apps.iutschedule.R;
import caceresenzo.apps.iutschedule.application.ScheduleApplication;
import caceresenzo.apps.iutschedule.calendar.VirtualCalendar;
import caceresenzo.apps.iutschedule.calendar.parser.VirtualCalendarRemoteParser;
import caceresenzo.apps.iutschedule.fragments.intro.sliders.AccountConfigurationIntroSlide;
import caceresenzo.apps.iutschedule.fragments.schedule.ScheduleFragment;
import caceresenzo.apps.iutschedule.managers.implementations.EventColorManager;
import caceresenzo.apps.iutschedule.managers.implementations.VirtualCalendarManager;
import caceresenzo.apps.iutschedule.models.Student;
import caceresenzo.apps.iutschedule.services.ScheduleNotificationService;
import caceresenzo.apps.iutschedule.utils.AsyncTaskResult;

public class CalendarDownloadAsyncTask extends AsyncTask<Student, Void, AsyncTaskResult<VirtualCalendar>> {

	/* Variables */
	private final long studentId;
	private final Callback callback;
	private boolean downloading;

	/* Constructor */
	public CalendarDownloadAsyncTask(long studentId, Callback callback) {
		super();

		this.studentId = studentId;
		this.callback = callback;
	}

	@Override
	protected void onPreExecute() {
		this.downloading = true;

		ScheduleApplication.get().getHandler().post(() -> {
			if (ScheduleFragment.get() != null) {
				ScheduleFragment.get().onCalendarDownloadStarted();
			}

			if (AccountConfigurationIntroSlide.get() != null) {
				AccountConfigurationIntroSlide.get().onCalendarDownloadStarted();
			}
		});
	}

	@Override
	protected AsyncTaskResult<VirtualCalendar> doInBackground(Student... students) {
		try {
			return new AsyncTaskResult<>(new VirtualCalendarRemoteParser(studentId).parse());
		} catch (Exception exception) {
			return new AsyncTaskResult<>(exception);
		}
	}

	@Override
	protected void onPostExecute(AsyncTaskResult<VirtualCalendar> result) {
		this.downloading = false;

		VirtualCalendar virtualCalendar = result.getResult();

		if (virtualCalendar == null) {
			if (VirtualCalendarManager.isCalendarDownloadFailLoggingEnabled()) {
				Toast.makeText(ScheduleApplication.get(), ScheduleApplication.get().getString(R.string.error_failed_to_download_calendar, result.getException().getMessage()), Toast.LENGTH_LONG).show();
			}

			if (ScheduleFragment.get() != null) {
				ScheduleFragment.get().onCalendarDownloadFailed(result.getException());
			}
			if (AccountConfigurationIntroSlide.get() != null) {
				AccountConfigurationIntroSlide.get().onCalendarDownloadFailed(result.getException());
			}
		} else {
			if (callback != null) {
				callback.accept(virtualCalendar);
			}

			EventColorManager.get().onNewCalendar(virtualCalendar);

			if (ScheduleNotificationService.isRunning(ScheduleApplication.get())) {
				System.out.println("SERVICE RUNNING");
				ScheduleNotificationService.get().notifyNewCalendar();
			} else {
				System.out.println("SERVICE NOT RUNNING");
			}

			if (ScheduleFragment.get() != null) {
				ScheduleFragment.get().onNewCalendar(virtualCalendar);
			}

			if (AccountConfigurationIntroSlide.get() != null) {
				AccountConfigurationIntroSlide.get().onNewCalendar(virtualCalendar);
			}
		}
	}

	public boolean isDownloading() {
		return downloading;
	}

	@FunctionalInterface
	public interface Callback {

		void accept(VirtualCalendar calendar);

	}

}
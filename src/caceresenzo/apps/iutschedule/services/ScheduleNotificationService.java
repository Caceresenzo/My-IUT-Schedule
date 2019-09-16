package caceresenzo.apps.iutschedule.services;

import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;
import caceresenzo.android.libs.service.ServiceUtils;
import caceresenzo.android.libs.toast.ToastUtils;
import caceresenzo.apps.iutschedule.R;
import caceresenzo.apps.iutschedule.activities.MainActivity;
import caceresenzo.apps.iutschedule.activities.ScheduleItemDetailActivity;
import caceresenzo.apps.iutschedule.application.Constants;
import caceresenzo.apps.iutschedule.application.ScheduleApplication;
import caceresenzo.apps.iutschedule.managers.ScheduleManager;
import caceresenzo.apps.iutschedule.managers.implementations.EventColorManager;
import caceresenzo.apps.iutschedule.managers.implementations.VirtualCalendarManager;
import caceresenzo.apps.iutschedule.utils.NotificationUtils;
import caceresenzo.libs.iutschedule.calendar.VirtualCalendar;
import caceresenzo.libs.iutschedule.calendar.VirtualCalendarEvent;

public class ScheduleNotificationService extends Service {
	
	/* Tag */
	public static final String TAG = ScheduleNotificationService.class.getSimpleName();
	
	/* Constants */
	public static final String ACTION_START_FOREGROUND_SERVICE = "ACTION_START_FOREGROUND_SERVICE";
	public static final String ACTION_STOP_FOREGROUND_SERVICE = "ACTION_STOP_FOREGROUND_SERVICE";
	
	public static final String ACTION_STOP = "ACTION_STOP";
	
	/* Singleton */
	private static ScheduleNotificationService INSTANCE;
	
	/* Timer */
	private Timer timer;
	private TimerTask timerTask;
	
	/* Variables */
	public int serviceIteration;
	
	/* Constructor */
	public ScheduleNotificationService() {
		super();
		
		this.timer = new Timer();
		this.timerTask = new TimerTask() {
			public void run() {
				Log.i(TAG, "Service Iteration: " + serviceIteration++);
				
				if (serviceIteration % 10 == 0) {
					VirtualCalendarManager.get().refreshCalendar();
				}
				
				updateNotification();
			}
		};
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		
		INSTANCE = this;
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if (intent != null) {
			String action = intent.getAction();
			
			switch (action) {
				case ACTION_START_FOREGROUND_SERVICE: {
					startForegroundService();
					
					Toast.makeText(getApplicationContext(), R.string.service_started, Toast.LENGTH_LONG).show();
					break;
				}
				
				case ACTION_STOP:
				case ACTION_STOP_FOREGROUND_SERVICE: {
					stopForegroundService();
					
					Toast.makeText(getApplicationContext(), getString(R.string.service_stopped), Toast.LENGTH_LONG).show();
					
					if (action.equals(ACTION_STOP)) {
						ScheduleApplication.get().getHandler().postDelayed(new Runnable() {
							@Override
							public void run() {
								ScheduleApplication.get().shutdown();
							}
						}, 500);
					}
					break;
				}
				
				default: {
					break;
				}
			}
		}
		
		return super.onStartCommand(intent, flags, startId);
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		
		destroy();
		
		INSTANCE = null;
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	/** Used to build and start foreground service. */
	private void startForegroundService() {
		Log.d(TAG, "Start foreground service.");
		
		startForeground(Constants.NOTIFICATION_ID.FOREGROUND_SERVICE, createNotification());
		
		execute();
	}
	
	/** Used to ask the service to stop as soon as possible. */
	private void stopForegroundService() {
		Log.d(TAG, "Stop foreground service.");
		
		if (ScheduleApplication.BUILD_DEBUG) {
			ToastUtils.makeLong(this, "Stopping foreground service.").show();
		}
		
		ScheduleManager.get().destroy();
		
		stopForeground(true);
		
		stopSelf();
	}
	
	/**
	 * Attach the final details to the {@link Notification}'s {@link NotificationCompat.Builder builder}.
	 * 
	 * @param builder
	 *            Base {@link NotificationCompat.Builder builder}.
	 * @return The same {@link NotificationCompat.Builder builder} but with the custom views set.
	 */
	private NotificationCompat.Builder attachNotificationRemoteViews(NotificationCompat.Builder builder) {
		VirtualCalendar virtualCalendar = VirtualCalendarManager.get().getCurrentVirtualCalendar();
		VirtualCalendarEvent nextEvent = null;
		int layoutId = R.layout.notification_schedule;
		
		if (virtualCalendar == null || VirtualCalendarManager.get().isDownloading()) {
			layoutId = R.layout.notification_schedule_no_icon;
		} else {
			nextEvent = virtualCalendar.getNextEvent(System.currentTimeMillis());
			
			if (nextEvent == null) {
				layoutId = R.layout.notification_schedule_no_icon;
			}
		}
		
		RemoteViews notificationLayout = new RemoteViews(getPackageName(), layoutId);
		RemoteViews notificationBigLayout = null;
		
		if (layoutId == R.layout.notification_schedule) {
			notificationBigLayout = new RemoteViews(getPackageName(), R.layout.notification_schedule_big);
		}
		
		List<RemoteViews> remoteViewss = Arrays.asList(notificationLayout, notificationBigLayout);
		String mainText = getString(R.string.error_no_calendar);
		
		if (VirtualCalendarManager.get().isDownloading()) {
			mainText = getString(R.string.downloading_calendar);
		} else {
			if (virtualCalendar != null) {
				if (nextEvent == null) {
					mainText = getString(R.string.error_no_next_event);
				} else {
					VirtualCalendarEvent event = virtualCalendar.getEventAtTime(System.currentTimeMillis());
					
					if (event == null || event.haveHalfPassed(System.currentTimeMillis())) {
						event = nextEvent;
					}
					
					/* Should not append */
					if (notificationBigLayout == null) {
						throw new IllegalStateException("Can't be null.");
					}
					
					Intent openEventIntent = ScheduleItemDetailActivity.createStartIntent(event, EventColorManager.get().getEventColor(event));
					PendingIntent openEventPendingIntent = PendingIntent.getActivity(this, 0, openEventIntent, 0);
					builder.addAction(new NotificationCompat.Action(R.drawable.icon_open_in_app_black_24dp, getString(R.string.service_action_open_event), openEventPendingIntent));
					
					mainText = event.getSummary();
					
					String location = event.getLocation();
					String description = event.getEscapedDescription();
					String timeRange = event.formatTimeRange();
					
					int howMuchDays = event.getStart().computeDaysBetween(System.currentTimeMillis());
					
					if (howMuchDays > 0) {
						timeRange += ", ";
						
						if (howMuchDays == 1) {
							timeRange += getString(R.string.time_range_tomorow);
						} else {
							timeRange += getString(R.string.time_range_in_days_format, howMuchDays);
						}
					}
					
					NotificationUtils.multipleSetTextViewText(R.id.notification_schedule_textview_location, location, remoteViewss);
					NotificationUtils.multipleSetTextViewText(R.id.notification_schedule_textview_time_range, timeRange, remoteViewss);
					
					notificationBigLayout.setTextViewText(R.id.notification_schedule_textview_description, description);
				}
			}
		}
		
		NotificationUtils.multipleSetTextViewText(R.id.notification_schedule_textview_main, mainText, remoteViewss);
		
		return builder
				.setContentTitle(mainText)
				.setCustomContentView(notificationLayout)
				.setCustomBigContentView(notificationBigLayout);
	}
	
	/** Create the {@link Notification notification} that will be displayed. */
	private Notification createNotification() {
		Intent cancelIntent = new Intent(this, ScheduleNotificationService.class);
		cancelIntent.setAction(ACTION_STOP);
		PendingIntent cancelPendingIntent = PendingIntent.getService(this, 0, cancelIntent, 0);
		NotificationCompat.Action cancelAction = new NotificationCompat.Action(R.drawable.icon_stop_black_24dp, getString(R.string.service_action_stop), cancelPendingIntent);
		
		return attachNotificationRemoteViews(new NotificationCompat.Builder(this, Constants.NOTIFICATION_CHANNEL.SCHEDULE)
				.setOnlyAlertOnce(true)
				.setWhen(System.currentTimeMillis())
				.setSmallIcon(R.mipmap.icon_launcher)
				.setPriority(NotificationCompat.PRIORITY_DEFAULT)
				.setStyle(new NotificationCompat.DecoratedCustomViewStyle())
				.setFullScreenIntent(PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class), 0), true)
				.addAction(cancelAction)).build();
	}
	
	/** Update the current {@link Notification notification}. */
	public void updateNotification() {
		Notification notification = createNotification();
		
		NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.notify(Constants.NOTIFICATION_ID.FOREGROUND_SERVICE, notification);
	}
	
	public void clearNotification() {
		NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.cancel(Constants.NOTIFICATION_ID.FOREGROUND_SERVICE);
	}
	
	/** Execute service's tasks. */
	private void execute() {
		timer.schedule(timerTask, 0, 1000 * 60);
		// timer.schedule(timerTask, 0, 1000);
	}
	
	/** End service's tasks. */
	private void destroy() {
		timer.cancel();
		clearNotification();
	}
	
	/** Notify the service of a new calendar. */
	public void notifyNewCalendar() {
		updateNotification();
	}
	
	/**
	 * Get an {@link Intent} ready to communicate with the {@link ScheduleNotificationService}.
	 * 
	 * @param context
	 *            Application's context.
	 * @return Created {@link Intent} instance.
	 */
	public static Intent getServiceIntent(Context context) {
		return new Intent(context, ScheduleNotificationService.class);
	}
	
	/**
	 * Tell if the {@link ScheduleNotificationService} is currently running.
	 * 
	 * @param context
	 *            Application's context.
	 * @return Weather it is running or not.
	 */
	public static boolean isRunning(Context context) {
		return ServiceUtils.isServiceRunning(context, ScheduleNotificationService.class);
	}
	
	/**
	 * Start the {@link ScheduleNotificationService} if it is not already running.
	 * 
	 * @param context
	 *            Application's context.
	 */
	public static void startIfNotAlready(Context context) {
		if (!isRunning(context)) {
			Intent intent = getServiceIntent(context);
			intent.setAction(ACTION_START_FOREGROUND_SERVICE);
			executeServiceIntent(context, intent);
		}
	}
	
	/**
	 * Stop the {@link ScheduleNotificationService} if it is running.
	 * 
	 * @param context
	 *            Application's context.
	 */
	public static void stopIfNotAlready(Context context) {
		if (isRunning(context)) {
			Intent intent = getServiceIntent(context);
			intent.setAction(ACTION_STOP_FOREGROUND_SERVICE);
			executeServiceIntent(context, intent);
		}
	}
	
	/**
	 * Send an {@link Intent} to the {@link ScheduleNotificationService}.
	 * 
	 * @param context
	 *            Application's context.
	 * @param intent
	 *            {@link Intent} to send.
	 */
	private static void executeServiceIntent(Context context, Intent intent) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			context.startForegroundService(intent);
		} else {
			context.startService(intent);
		}
	}
	
	/** @return ScheduleNotificationService's singleton instance. */
	public static final ScheduleNotificationService get() {
		return INSTANCE;
	}
	
}
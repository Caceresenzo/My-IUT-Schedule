package caceresenzo.apps.iutschedule.application;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Handler;
import android.preference.PreferenceManager;
import caceresenzo.android.libs.uncaughtexceptionhandler.AndroidUncaughtExceptionHandler;
import caceresenzo.apps.iutschedule.activities.MainActivity;
import caceresenzo.apps.iutschedule.managers.ScheduleManager;
import caceresenzo.apps.iutschedule.services.ScheduleNotificationService;

public class ScheduleApplication extends Application {
	
	/* Tag */
	public static final String TAG = ScheduleApplication.class.getSimpleName();
	
	/* Set Build as Debug */
	public static final boolean BUILD_DEBUG = false;
	
	/* Singleton */
	private static ScheduleApplication INSTANCE;
	
	/* Managers */
	private Handler handler;
	
	/* Preferences */
	private SharedPreferences sharedPreferences;
	
	@Override
	public void onCreate() {
		super.onCreate();
		INSTANCE = this;
		
		this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
		
		handler = new Handler();
		
		new AndroidUncaughtExceptionHandler.Builder(getApplicationContext()) //
				.setTrackActivitiesEnabled(true) //
				.setBackgroundModeEnabled(true) //
				.addCommaSeparatedEmailAddresses("caceresenzo1502@gmail.com") //
				.build();
		
		createNotificationChannels();
		
		ScheduleManager.get().initialize();
		
		ScheduleNotificationService.startIfNotAlready(this);
	}
	
	/** Create android's new {@link NotificationChannel notification channel}s. */
	private void createNotificationChannels() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			NotificationManager notificationManager = getSystemService(NotificationManager.class);
			
			if (BUILD_DEBUG) {
				for (String channel : new String[] { Constants.NOTIFICATION_CHANNEL.SCHEDULE }) {
					if (notificationManager.getNotificationChannel(channel) != null) {
						notificationManager.deleteNotificationChannel(channel);
					}
				}
			}
			
			if (notificationManager.getNotificationChannel(Constants.NOTIFICATION_CHANNEL.SCHEDULE) == null) {
				NotificationChannel mainChannel = new NotificationChannel(Constants.NOTIFICATION_CHANNEL.SCHEDULE, "xx_ Schedule Notification", NotificationManager.IMPORTANCE_DEFAULT);
				mainChannel.setDescription("xx_ Display current and next item on the schedule.");
				mainChannel.setImportance(NotificationManager.IMPORTANCE_LOW);
				
				notificationManager.createNotificationChannel(mainChannel);
			}
		}
	}
	
	public void shutdown() {
		if (MainActivity.get() != null) {
			MainActivity.get().finishAndRemoveTask();
		}
		
		System.exit(0);
	}
	
	/** @return Application's main {@link Handler handler}. */
	public Handler getHandler() {
		return handler;
	}
	
	/** @return Application's main {@link SharedPreferences preferences}. */
	public SharedPreferences getSharedPreferences() {
		return sharedPreferences;
	}
	
	/** @return ScheduleApplication's singleton instance. */
	public static final ScheduleApplication get() {
		return INSTANCE;
	}
	
}
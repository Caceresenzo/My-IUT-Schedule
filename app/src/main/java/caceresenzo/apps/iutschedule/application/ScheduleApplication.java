package caceresenzo.apps.iutschedule.application;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Handler;
import android.preference.PreferenceManager;

import com.google.android.gms.ads.MobileAds;

import caceresenzo.apps.iutschedule.R;
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
		
		/*new AndroidUncaughtExceptionHandler.Builder(getApplicationContext()) //
				.setTrackActivitiesEnabled(true) //
				.setBackgroundModeEnabled(true) //
				.addCommaSeparatedEmailAddresses("caceresenzo1502@gmail.com") //
				.build();*/
		
		createNotificationChannels();

		MobileAds.initialize(this);
		
		ScheduleManager.get().initialize();

		if (ScheduleNotificationService.isServiceEnabled()) {
			ScheduleNotificationService.startIfNotAlready(this);
		}
	}
	
	/** Create android's new {@link NotificationChannel notification channel}s. */
	private void createNotificationChannels() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			NotificationManager notificationManager = getSystemService(NotificationManager.class);
			
			if (BUILD_DEBUG) {
				for (String channel : new String[] { Constants.NOTIFICATION_CHANNEL.NEXT_EVENT }) {
					if (notificationManager.getNotificationChannel(channel) != null) {
						notificationManager.deleteNotificationChannel(channel);
					}
				}
			}
			
			if (notificationManager.getNotificationChannel(Constants.NOTIFICATION_CHANNEL.NEXT_EVENT) == null) {
				NotificationChannel mainChannel = new NotificationChannel(Constants.NOTIFICATION_CHANNEL.NEXT_EVENT, getString(R.string.notification_channel_next_event_title), NotificationManager.IMPORTANCE_DEFAULT);
				mainChannel.setDescription(getString(R.string.notification_channel_next_event_description));
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
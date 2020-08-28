package caceresenzo.apps.iutschedule.managers;

import android.os.Build;

import java.io.File;

import caceresenzo.apps.iutschedule.application.ScheduleApplication;

public abstract class AbstractManager {
	
	/* Shareds */
	protected ScheduleApplication application;
	
	/* Constructor */
	public AbstractManager() {
		this.application = ScheduleApplication.get();
	}
	
	/** Called when the {@link AbstractManager manager} should initialize. */
	public void initialize() {
		;
	}
	
	/** Called when the {@link AbstractManager manager} shoud do a regular task like saving data. */
	public void step() {
		;
	}
	
	/** Called when the {@link AbstractManager manager} will be destroy. */
	public void destroy() {
		;
	}
	
	protected File getDataDirectory() {
		if (Build.VERSION.SDK_INT <= 24) {
			return application.getFilesDir();
		}
		
		return application.getDataDir();
	}
	
}
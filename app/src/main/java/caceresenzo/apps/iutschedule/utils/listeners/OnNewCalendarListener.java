package caceresenzo.apps.iutschedule.utils.listeners;

import caceresenzo.apps.iutschedule.calendar.VirtualCalendar;

public interface OnNewCalendarListener {
	
	/** Called when a new {@link VirtualCalendar calendar} has been downloaded. */
	public void onNewCalendar();
	
}
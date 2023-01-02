package caceresenzo.apps.iutschedule.utils.listeners;

public interface OnCalendarDownloadListener {

	public void onCalendarDownloadStarted();

	public void onCalendarDownloadFailed(Exception exception);

}
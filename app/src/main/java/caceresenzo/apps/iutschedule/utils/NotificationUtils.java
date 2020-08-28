package caceresenzo.apps.iutschedule.utils;

import android.widget.RemoteViews;
import android.widget.TextView;

import java.util.List;

public class NotificationUtils {
	
	/**
	 * Call {@link RemoteViews#setTextViewText(int, CharSequence)} on a {@link List list} of {@link RemoteViews}.
	 * 
	 * @param viewId
	 *            {@link TextView}'s id.
	 * @param text
	 *            Text to set.
	 * @param remoteViewss
	 *            {@link List list} of {@link RemoteViews}.
	 */
	public static void multipleSetTextViewText(int viewId, CharSequence text, List<RemoteViews> remoteViewss) {
		for (RemoteViews remoteViews : remoteViewss) {
			if (remoteViews != null) {
				remoteViews.setTextViewText(viewId, text);
			}
		}
	}
	
}
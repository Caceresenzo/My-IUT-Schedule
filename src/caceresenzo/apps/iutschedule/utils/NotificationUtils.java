package caceresenzo.apps.iutschedule.utils;

import java.util.List;

import android.widget.RemoteViews;
import android.widget.TextView;

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
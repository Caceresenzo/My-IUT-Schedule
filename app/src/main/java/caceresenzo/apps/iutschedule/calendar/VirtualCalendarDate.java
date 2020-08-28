package caceresenzo.apps.iutschedule.calendar;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class VirtualCalendarDate extends Date {
	
	/* Constants */
	public static final SimpleDateFormat FORMAT = new SimpleDateFormat("yyyyMMdd'T'HHmmss");
	
	static {
		FORMAT.setTimeZone(TimeZone.getTimeZone("UTC"));
	}
	
	/* Constructor */
	private VirtualCalendarDate(long time) {
		super(time);
	}
	
	/**
	 * Compute how much days it has between this date and the time<code>time</code> parameter.
	 * 
	 * @param time
	 *            Time in milliseconds.
	 * @return How much (full, so no coma) days.
	 */
	public int computeDaysBetween(long time) {
		Calendar thisCalendar = toCalendar();
		Calendar timeCalendar = Calendar.getInstance();
		
		timeCalendar.setTimeInMillis(time);
		
		for (Calendar calendar : Arrays.asList(thisCalendar, timeCalendar)) {
			calendar.set(Calendar.HOUR_OF_DAY, 0);
			calendar.set(Calendar.MINUTE, 0);
			calendar.set(Calendar.SECOND, 0);
			calendar.set(Calendar.MILLISECOND, 0);
		}
		
		return (int) ((thisCalendar.getTimeInMillis() - timeCalendar.getTimeInMillis()) / (1000 * 60 * 60 * 24));
	}
	
	/** @return This date converted to a {@link Calendar calendar} instance. */
	public Calendar toCalendar() {
		Calendar calendar = Calendar.getInstance();
		
		calendar.setTime(this);
		
		return calendar;
	}
	
	/**
	 * Parse a date from a <code>string</code>.
	 * 
	 * @param string
	 *            Source string.
	 * @return {@link VirtualCalendarDate} object. (or <code>null</code> if failed to parse).
	 */
	public static VirtualCalendarDate fromString(String string) {
		try {
			return new VirtualCalendarDate(FORMAT.parse(string).getTime());
		} catch (ParseException exception) {
			throw new RuntimeException(exception);
		}
	}
	
}
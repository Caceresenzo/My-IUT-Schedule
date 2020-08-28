package caceresenzo.apps.iutschedule.calendar;

import com.alamkanak.weekview.WeekViewDisplayable;
import com.alamkanak.weekview.WeekViewEvent;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import java.util.TimeZone;
import java.util.regex.Pattern;

import caceresenzo.apps.iutschedule.managers.implementations.EventColorManager;
import caceresenzo.apps.iutschedule.utils.IBuilder;

public class VirtualCalendarEvent implements Serializable, WeekViewDisplayable<VirtualCalendarEvent> {
	
	/* Serializable */
	private static final long serialVersionUID = 4667175564259328353L;
	
	/* Constants */
	public static final String HOUR_MINUTE_STRING_FORMAT = "HH:mm";
	public static final String TIME_RANGE_FORMAT = "%s — %s";
	public static final SimpleDateFormat HOUR_MINUTE_FORMAT;
	public static final SimpleDateFormat HOUR_MINUTE_FORMAT_UTC;
	
	static {
		HOUR_MINUTE_FORMAT = new SimpleDateFormat(HOUR_MINUTE_STRING_FORMAT);
		HOUR_MINUTE_FORMAT_UTC = new SimpleDateFormat(HOUR_MINUTE_STRING_FORMAT);
		
		HOUR_MINUTE_FORMAT.setTimeZone(TimeZone.getDefault());
		HOUR_MINUTE_FORMAT_UTC.setTimeZone(TimeZone.getTimeZone("UTC"));
	}
	
	/* Variables */
	private final VirtualCalendarDate start, end;
	private final String summary, location, description, uid;
	
	/* Constructor: Serializable */
	public VirtualCalendarEvent() {
		this(null, null, null, null, null, null);
	}
	
	/* Constructor */
	protected VirtualCalendarEvent(VirtualCalendarDate start, VirtualCalendarDate end, String summary, String location, String description, String uid) {
		this.start = start;
		this.end = end;
		this.summary = summary;
		this.location = location;
		this.description = description;
		this.uid = uid;
	}
	
	/**
	 * Test if the <code>time</code> parameter is between the start and the end of this event.
	 * 
	 * @param time
	 *            Time relative.
	 * @return Weather or not this event is currently being appening (relative to the <code>time</code> parameter).
	 * @see #getStart()
	 * @see #getEnd()
	 */
	public boolean isCurrent(long time) {
		return time >= start.getTime() && time <= end.getTime();
	}
	
	/** @return <code>end - start</code>. */
	public long computeDuration() {
		return end.getTime() - start.getTime();
	}
	
	/** @return A formatted time range for this event. */
	public String formatTimeRange() {
		return String.format(TIME_RANGE_FORMAT, HOUR_MINUTE_FORMAT.format(start), HOUR_MINUTE_FORMAT.format(end));
	}
	
	/** @return A formatted duration for this event. */
	public String formatDuration() {
		return HOUR_MINUTE_FORMAT_UTC.format(new Date(computeDuration()));
	}
	
	/**
	 * Check if half the time has passed for this event (relative to the <code>time</code> parameter).
	 * 
	 * @param time
	 *            Relative time.
	 * @return Weather or not <code>time > (end - (duration / 2))</code>.
	 */
	public boolean haveHalfPassed(long time) {
		long duration = computeDuration();
		
		/* Never too much check */
		if (duration == 0) {
			return true;
		}
		
		long halfDuration = duration / 2;
		long halfLimit = end.getTime() - halfDuration;
		
		return time > halfLimit;
	}
	
	/** @return Event's start date and time. */
	public VirtualCalendarDate getStart() {
		return start;
	}
	
	/** @return Event's end date and time. */
	public VirtualCalendarDate getEnd() {
		return end;
	}
	
	/** @return Event's summary. */
	public String getSummary() {
		return summary;
	}
	
	/** @return Event's location. */
	public String getLocation() {
		return location;
	}
	
	/** @return Event's description. */
	public String getDescription() {
		return description;
	}
	
	/** @return Event's escaped description. (will be a null-safe value and "\n" are escaped) */
	public String getEscapedDescription() {
		return String.valueOf(description).replace("\\n", "\n");
	}
	
	/** @return Event's unique identifier. */
	public String getUid() {
		return uid;
	}
	
	@Override
	public WeekViewEvent<VirtualCalendarEvent> toWeekViewEvent() {		
		return new WeekViewEvent.Builder<VirtualCalendarEvent>(this)
				.setId(uid.hashCode())
				.setTitle(summary)
				.setLocation(location)
				.setStartTime(start.toCalendar())
				.setEndTime(end.toCalendar())
				.setStyle(new WeekViewEvent.Style.Builder()
						.setBackgroundColor(EventColorManager.get().getEventColor(this))
						.build())
				.build();
	}
	
	public static final class Builder implements IBuilder<VirtualCalendarEvent> {
		
		/* Constants */
		public static final Pattern PATTERN_PURIFY = Pattern.compile("(^(?:\\\\n)+|(?:\\\\n)+$)");
		
		/* Variables */
		private VirtualCalendarDate start, end;
		private String summary, location, description, uid;
		
		/* Constructor */
		public Builder() {
			;
		}
		
		/**
		 * Set a start date.
		 * 
		 * @param date
		 *            Start {@link VirtualCalendarDate date}.
		 * @return <code>this</code> for method chaining (fluent API).
		 */
		public Builder startAt(VirtualCalendarDate date) {
			this.start = date;
			
			return this;
		}
		
		/**
		 * Set a end date.
		 * 
		 * @param date
		 *            End {@link VirtualCalendarDate date}.
		 * @return <code>this</code> for method chaining (fluent API).
		 */
		public Builder endAt(VirtualCalendarDate date) {
			this.end = date;
			
			return this;
		}
		
		/**
		 * Set a summary for the event.
		 * 
		 * @param summary
		 *            Event's content.
		 * @return <code>this</code> for method chaining (fluent API).
		 */
		public Builder summary(String summary) {
			this.summary = summary;
			
			return this;
		}
		
		/**
		 * Set a location for the event.
		 * 
		 * @param location
		 *            Event's location (usually room where it will be taking place).
		 * @return <code>this</code> for method chaining (fluent API).
		 */
		public Builder atLocation(String location) {
			this.location = location;
			
			return this;
		}
		
		/**
		 * Set a description for the event.
		 * 
		 * @param description
		 *            Event's description (usually more information about the event like the teacher).
		 * @return <code>this</code> for method chaining (fluent API).
		 */
		public Builder describe(String description) {
			this.description = description;
			
			return this;
		}
		
		/**
		 * Purify the description.<br>
		 * This will remove all <code>\n</code> at the start and the line that are not necessary.
		 * 
		 * @return <code>this</code> for method chaining (fluent API).
		 * @throws NullPointerException
		 *             If the {@link #describe(String) description} is <code>null</code>.
		 * @see #describe(String) Add a description
		 */
		public Builder purifyDescription() {
			Objects.requireNonNull(this.description, "Cannot purity a null description.");
			this.description = PATTERN_PURIFY.matcher(this.description).replaceAll("");
			
			return this;
		}
		
		/**
		 * Set the unique identifier for the event.
		 * 
		 * @param uid
		 *            Event's unique identifier.
		 * @return <code>this</code> for method chaining (fluent API).
		 */
		public Builder identify(String uid) {
			this.uid = uid;
			
			return this;
		}
		
		@Override
		public VirtualCalendarEvent build() {
			return new VirtualCalendarEvent(start, end, summary, location, description, uid);
		}
		
	}
	
}
package caceresenzo.apps.iutschedule.calendar;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import caceresenzo.apps.iutschedule.utils.IBuilder;

public class VirtualCalendar {

	/* Variables */
	private final String name;
	private final List<VirtualCalendarEvent> events;

	/* Constructor */
	private VirtualCalendar(String name, List<VirtualCalendarEvent> events) {
		this.name = name;
		this.events = events;
	}

	/**
	 * Get an {@link VirtualCalendarEvent event} at a specific time.
	 *
	 * @param time Specific time.
	 * @return Found {@link VirtualCalendarEvent event} or <code>null</code> if not found.
	 */
	public VirtualCalendarEvent getEventAtTime(long time) {
		for (VirtualCalendarEvent event : events) {
			if (event.isCurrent(time)) {
				return event;
			}
		}

		return null;
	}

	/**
	 * Get the next event from now.
	 *
	 * @return Next {@link VirtualCalendarEvent event} or <code>null</code> if the end of the calendar has been reached.
	 * @see #getNextEvent(long) Get the next event with a specified start time.
	 */
	public VirtualCalendarEvent getNextEvent() {
		return getNextEvent(System.currentTimeMillis());
	}

	/**
	 * Get the next event from a specified time.
	 *
	 * @param start Number of milliseconds to start after.
	 * @return Next {@link VirtualCalendarEvent event} or <code>null</code> if the end of the calendar has been reached.
	 */
	public VirtualCalendarEvent getNextEvent(long start) {
		Date currentDate = new Date(start);
		VirtualCalendarEvent nextEvent = null;
		long delta = Long.MAX_VALUE;

		for (VirtualCalendarEvent event : events) {
			if (event.getStart().before(currentDate)) {
				continue;
			}

			long nextDelta = event.getStart().getTime() - start;

			if (delta > nextDelta) {
				nextEvent = event;
				delta = nextDelta;
			}
		}

		return nextEvent;
	}

	/**
	 * @return Calendar's name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return Calendar's events.
	 */
	public List<VirtualCalendarEvent> getEvents() {
		return events;
	}

	public static final class Builder implements IBuilder<VirtualCalendar> {

		/* Variables */
		private String name;
		private List<VirtualCalendarEvent> events;

		/* Constructor */
		public Builder() {
			;
		}

		/**
		 * Set the calendar name.
		 *
		 * @param name Calender name.
		 * @return <code>this</code> for method chaining (fluent API).
		 */
		public Builder name(String name) {
			this.name = name;

			return this;
		}

		/**
		 * Remove the year range in the name.
		 *
		 * @return <code>this</code> for method chaining (fluent API).
		 * @see VirtualCalendar#getSchoolYearRange()
		 */
		public Builder removeYearInName() {
			this.name = name.replace(" " + VirtualCalendar.getSchoolYearRange(), "");

			return this;
		}

		/**
		 * Remove the double-quotes (") in the name.
		 *
		 * @return <code>this</code> for method chaining (fluent API).
		 */
		public Builder escapeDoubleQuotesInName() {
			this.name = name.replace("\"", "");

			return this;
		}

		/**
		 * Set the calendar {@link VirtualCalendarEvent event} list.<br>
		 * This will override the previous list if {@link #addEvent(VirtualCalendarEvent) addEvent(event)} has been called previously.
		 *
		 * @param events Events list.
		 * @return <code>this</code> for method chaining (fluent API).
		 * @see #addEvent(VirtualCalendarEvent) addEvent(event)
		 */
		public Builder withEvents(List<VirtualCalendarEvent> events) {
			this.events = events;

			return this;
		}

		/**
		 * Add an {@link VirtualCalendarEvent event} to the current events list.<br>
		 * If no one has already been provided, a new one will be created.
		 *
		 * @param event {@link VirtualCalendarEvent Event} instance.
		 * @return <code>this</code> for method chaining (fluent API).
		 * @see #withEvents(List) withEvents(events)
		 */
		public Builder addEvent(VirtualCalendarEvent event) {
			if (events == null) {
				events = new ArrayList<>();
			}

			events.add(event);

			return this;
		}

		@Override
		public VirtualCalendar build() {
			return new VirtualCalendar(name, events);
		}

	}

	/**
	 * @return The current school year range.
	 */
	public static String getSchoolYearRange() {
		Calendar calendar = Calendar.getInstance();

		int month = calendar.get(Calendar.MONTH);
		int year = calendar.get(Calendar.YEAR);

		if (month <= Calendar.AUGUST) {
			year -= 1;
		}

		return String.format("%d-%d", year, year + 1);
	}

}
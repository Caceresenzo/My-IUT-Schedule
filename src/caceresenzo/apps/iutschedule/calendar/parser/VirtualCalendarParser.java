package caceresenzo.apps.iutschedule.calendar.parser;

import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;

import caceresenzo.apps.iutschedule.calendar.VirtualCalendar;
import caceresenzo.apps.iutschedule.calendar.VirtualCalendarDate;
import caceresenzo.apps.iutschedule.calendar.VirtualCalendarEvent;

public class VirtualCalendarParser {
	
	/* Constants */
	public static final String CORE_OBJECT_CALENDAR = "VCALENDAR";
	public static final String CORE_OBJECT_EVENT = "VEVENT";
	
	/* Variables */
	private final String content;
	
	/* Constructor */
	public VirtualCalendarParser(String content) {
		this.content = content;
	}
	
	/**
	 * Parse an {@link VirtualCalendarEvent event}.
	 * 
	 * @param listIterator
	 *            Current line iterator.
	 * @return Parsed {@link VirtualCalendarEvent event} or <code>null</code> if invalid.
	 */
	private VirtualCalendarEvent parseEvent(ListIterator<String> listIterator) {
		VirtualCalendarEvent.Builder builder = new VirtualCalendarEvent.Builder();
		
		while (listIterator.hasNext()) {
			String line = listIterator.next();
			String[] split = line.split(":", 2);
			
			if (line.isEmpty() || split.length != 2) {
				continue;
			}
			
			String command = split[0].trim().toUpperCase();
			String argument = split[1].trim();
			
			switch (command) {
				case "DTSTART": {
					builder.startAt(VirtualCalendarDate.fromString(argument));
					break;
				}
				
				case "DTEND": {
					builder.endAt(VirtualCalendarDate.fromString(argument));
					break;
				}
				
				case "SUMMARY": {
					builder.summary(argument);
					break;
				}
				
				case "LOCATION": {
					builder.atLocation(argument);
					break;
				}
				
				case "DESCRIPTION": {
					String description = argument;
					
					/* Handle description being on multiple line */
					while (listIterator.hasNext()) {
						String nextLine = listIterator.next();
						
						if (nextLine.startsWith(" ")) {
							description += nextLine.substring(1);
						} else {
							listIterator.previous();
							break;
						}
					}
					
					builder.describe(description).purifyDescription();
					break;
				}
				
				case "UID": {
					builder.identify(argument);
					break;
				}
				
				case "END": {
					return builder.build();
				}
				
				default: {
					break;
				}
			}
		}
		
		return null;
	}
	
	/** @return Parsed {@link VirtualCalendar calendar} or <code>null</code> if invalid. */
	public VirtualCalendar parse() {
		List<String> lines = Arrays.asList(content.split("\n"));
		ListIterator<String> listIterator = lines.listIterator();
		
		VirtualCalendar.Builder builder = new VirtualCalendar.Builder();
		
		while (listIterator.hasNext()) {
			String line = listIterator.next();
			String[] split = line.split(":", 2);
			
			if (line.isEmpty() || split.length != 2) {
				continue;
			}
			
			String command = split[0].trim().toUpperCase();
			String argument = split[1].trim();
			
			switch (command) {
				case "BEGIN": {
					if (argument.equalsIgnoreCase(CORE_OBJECT_EVENT)) {
						builder.addEvent(parseEvent(listIterator));
					}
					break;
				}
				
				case "END": {
					if (argument.equalsIgnoreCase(CORE_OBJECT_CALENDAR)) {
						return builder.build();
					}
					break;
				}
				
				case "CN": {
					builder.name(argument).removeYearInName().escapeDoubleQuotesInName();
					break;
				}
				
				default: {
					break;
				}
			}
		}
		
		return null;
	}
	
}

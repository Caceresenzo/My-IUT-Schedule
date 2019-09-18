package caceresenzo.apps.iutschedule.calendar.parser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import caceresenzo.apps.iutschedule.calendar.VirtualCalendar;
import caceresenzo.libs.http.client.webb.Webb;

public class VirtualCalendarRemoteParser {
	
	/* Constants */
	public static final String SERVER_URL = "http://www.univ-orleans.fr/EDTWeb/edt";
	public static final Pattern EXTRACTION_PATTERN = Pattern.compile("URL iCalendar &agrave; copier&nbsp;:[\\s\\n]*<input type=\"text\" value=\"(.*?)\".*?\\/>", Pattern.MULTILINE);
	public static final Pattern URL_REPLACE_AMP_PATTERN = Pattern.compile("&amp;");
	
	/* Variables */
	private final long studentId;
	
	/* Constructor */
	public VirtualCalendarRemoteParser(long studentId) {
		this.studentId = studentId;
	}
	
	/**
	 * This function will do all these thing:
	 * <ul>
	 * <li>extract the calendar link of the student</li>
	 * <li>download it</li>
	 * <li>and parse it</li>
	 * </ul>
	 * 
	 * @return Parsed {@link VirtualCalendar calendar} or <code>null</code> if invalid.
	 * @throws Exception
	 *             If an error append when network-related or regex-related exception are throws.
	 */
	public VirtualCalendar parse() throws Exception {
		String html = Webb.create()
				.post(SERVER_URL)
				.param("action", "displayWeeksPeople")
				.param("person", studentId)
				.chromeUserAgent()
				.ensureSuccess()
				.asString().getBody();
		
		Matcher matcher = EXTRACTION_PATTERN.matcher(html);
		if (!matcher.find()) {
			throw new IllegalStateException("Url not find.");
		}
		
		String rawUrl = matcher.group(1);
		String calendarUrl = URL_REPLACE_AMP_PATTERN.matcher(rawUrl).replaceAll("&");
		
		String calendarContent = Webb.create()
				.get(calendarUrl)
				.chromeUserAgent()
				.ensureSuccess()
				.asString()
				.getBody();
		
		return new VirtualCalendarParser(calendarContent).parse();
	}
	
}
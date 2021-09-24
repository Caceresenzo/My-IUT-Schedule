package caceresenzo.apps.iutschedule.calendar.parser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import caceresenzo.apps.iutschedule.calendar.VirtualCalendar;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

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
	 * @throws Exception If an error append when network-related or regex-related exception are throws.
	 */
	public VirtualCalendar parse() throws Exception {
		OkHttpClient client = new OkHttpClient();

		try (Response response = client.newCall(new Request.Builder()
				.url(SERVER_URL)
				.header("Content-Type", "application/x-www-form-urlencoded")
				.header("Accept", "*/*")
				.post(new FormBody.Builder()
						.add("action", "displayWeeksPeople")
						.add("person", String.valueOf(studentId))
						.build())
				.build()).execute()) {

			String html = response.body().string();

			Matcher matcher = EXTRACTION_PATTERN.matcher(html);
			if (!matcher.find()) {
				throw new IllegalStateException("Url not find.");
			}

			String rawUrl = matcher.group(1);
			String calendarUrl = URL_REPLACE_AMP_PATTERN.matcher(rawUrl).replaceAll("&");

			try (Response calendarResponse = client.newCall(new Request.Builder()
					.url(calendarUrl)
					.get()
					.build()).execute()) {
				return new VirtualCalendarParser(calendarResponse.body().string()).parse();
			}
		}
	}

}
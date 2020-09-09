package caceresenzo.apps.iutschedule.managers.implementations;

import android.graphics.Color;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import caceresenzo.apps.iutschedule.calendar.VirtualCalendar;
import caceresenzo.apps.iutschedule.calendar.VirtualCalendarEvent;
import caceresenzo.apps.iutschedule.managers.AbstractManager;
import caceresenzo.apps.iutschedule.utils.Utils;
import caceresenzo.apps.iutschedule.utils.listeners.OnNewCalendarListener;

public class EventColorManager extends AbstractManager implements OnNewCalendarListener {

	/* Singleton */
	private static EventColorManager INSTANCE;

	/* Variables */
	private final List<Integer> pool;
	private final Map<String, Integer> attributions;
	private String oldCalendarName;

	/* Private Constructor */
	private EventColorManager() {
		super();

		this.pool = new ArrayList<>();
		this.attributions = new HashMap<>();
	}

	@Override
	public void initialize() {
		super.initialize();

		float hsv[] = new float[]{0, 58, 96};

		for (int angle = 0; angle < 360; angle += 20) {
			hsv[0] = angle;

			/* Skip yellow or blue because it is too bright or too ugly */
			if (angle == 60 || angle == 240) {
				continue;
			}

			pool.add(Color.HSVToColor(hsv));
		}
	}

	@Override
	public void onNewCalendar(VirtualCalendar newCalendar) {
		String name = newCalendar.getName();

		if (!name.equals(oldCalendarName)) {
			oldCalendarName = name;

			doEventColorAttribution(newCalendar);
		}
	}

	/**
	 * Do the {@link VirtualCalendarEvent event} color attribution.<br>
	 * This will asign a random color from a color pool to all {@link VirtualCalendarEvent event}'s name.
	 *
	 * @param calendar Source {@link VirtualCalendar calendar} to get {@link VirtualCalendarEvent event} from.
	 */
	private void doEventColorAttribution(VirtualCalendar calendar) {
		List<Integer> localPool = new ArrayList<>();

		attributions.clear();

		for (VirtualCalendarEvent event : calendar.getEvents()) {
			String name = getEventName(event);

			if (localPool.isEmpty()) {
				localPool.addAll(pool);
			}

			if (!attributions.containsKey(name)) {
				int color = Utils.random(localPool);

				localPool.remove((Object) color);

				attributions.put(name, color);
			}
		}
	}

	/**
	 * Get color attribution corresponding to an {@link VirtualCalendarEvent event}.
	 *
	 * @param event Target {@link VirtualCalendarEvent event}.
	 * @return Attributed color (or {@link Color#BLACK} if no one has been attributed).
	 */
	public int getEventColor(VirtualCalendarEvent event) {
		String name = getEventName(event);

		if (attributions.containsKey(name)) {
			return attributions.get(getEventName(event));
		}

		return Color.BLACK;
	}

	/**
	 * Escape an {@link VirtualCalendarEvent event}'s name by replacing "_" by an empty space.<br>
	 * This will avoid little changes to be considered as a whole new {@link VirtualCalendarEvent event} "type".
	 *
	 * @param event Target {@link VirtualCalendarEvent event}.
	 * @return Escaped name.
	 */
	public String getEventName(VirtualCalendarEvent event) {
		return event.getSummary().replace("_", "");
	}

	/**
	 * @return EventColorManager's singleton instance.
	 */
	public static final EventColorManager get() {
		if (INSTANCE == null) {
			INSTANCE = new EventColorManager();
		}

		return INSTANCE;
	}

}
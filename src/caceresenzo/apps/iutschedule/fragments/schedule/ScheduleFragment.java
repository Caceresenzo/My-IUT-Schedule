package caceresenzo.apps.iutschedule.fragments.schedule;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import com.alamkanak.weekview.DateTimeInterpreter;
import com.alamkanak.weekview.MonthLoader;
import com.alamkanak.weekview.WeekView;
import com.alamkanak.weekview.WeekViewEvent;

import android.content.Context;
import android.graphics.RectF;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.TypedValue;
import android.view.MenuItem;
import android.widget.Toast;
import caceresenzo.apps.iutschedule.R;
import caceresenzo.apps.iutschedule.activities.MainActivity;
import caceresenzo.apps.iutschedule.activities.ScheduleItemDetailActivity;
import caceresenzo.apps.iutschedule.calendar.VirtualCalendar;
import caceresenzo.apps.iutschedule.calendar.VirtualCalendarEvent;
import caceresenzo.apps.iutschedule.fragments.BaseFragment;
import caceresenzo.apps.iutschedule.managers.implementations.EventColorManager;
import caceresenzo.apps.iutschedule.managers.implementations.VirtualCalendarManager;
import caceresenzo.apps.iutschedule.utils.listeners.OnCalendarDownloadListener;
import caceresenzo.apps.iutschedule.utils.listeners.OnNewCalendarListener;

public class ScheduleFragment extends BaseFragment implements OnNewCalendarListener, OnCalendarDownloadListener, WeekView.EventClickListener, MonthLoader.MonthChangeListener {
	
	/* Tag */
	public static final String TAG = ScheduleFragment.class.getSimpleName();
	
	/* WeekView Displaying Type */
	private static final int WEEK_VIEW_TYPE_DAY_VIEW = 1;
	private static final int WEEK_VIEW_TYPE_THREE_DAY_VIEW = 2;
	private static final int WEEK_VIEW_TYPE_WEEK_VIEW = 3;
	
	/* Singleton */
	private static ScheduleFragment INSTANCE;
	
	/* Views */
	private SwipeRefreshLayout swipeRefreshLayout;
	private WeekView weekView;
	
	/* Data */
	private final List<WeekViewEvent> events;
	private int weekViewType;
	
	/* Constructor */
	public ScheduleFragment() {
		super();
		
		this.events = new ArrayList<>();
	}
	
	@Override
	public void onAttach(Context context) {
		super.onAttach(context);
		
		INSTANCE = null;
	}
	
	@Override
	public void onUiReady(Bundle savedInstanceState) {
		INSTANCE = this;
		
		this.swipeRefreshLayout = getView().findViewById(R.id.fragment_schedule_swiperefreshlayout_main);
		this.weekView = getView().findViewById(R.id.weekView);
		
		this.weekView.setOnEventClickListener(this);
		this.weekView.setMonthChangeListener(this);
		this.weekView.goToToday();
		this.weekView.goToHour(7);
		
		changeWeekViewType(WEEK_VIEW_TYPE_WEEK_VIEW, null);
		setupDateTimeInterpreter(true);
		
		changeRefreshState(true);
		
		if (VirtualCalendarManager.get().getCurrentVirtualCalendar() != null) {
			onNewCalendar();
		}
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		
		switch (id) {
			case R.id.action_today: {
				this.weekView.goToToday();
				break;
			}
			
			case R.id.action_refresh: {
				VirtualCalendarManager.get().refreshCalendar();
				break;
			}
			
			case R.id.action_day_view: {
				changeWeekViewType(WEEK_VIEW_TYPE_DAY_VIEW, item);
				break;
			}
			
			case R.id.action_three_day_view: {
				changeWeekViewType(WEEK_VIEW_TYPE_THREE_DAY_VIEW, item);
				break;
			}
			
			case R.id.action_week_view: {
				changeWeekViewType(WEEK_VIEW_TYPE_WEEK_VIEW, item);
				break;
			}
			
			default: {
				break;
			}
		}
		
		return super.onOptionsItemSelected(item);
	}
	
	/**
	 * Display or hide the refresh indicator.
	 * 
	 * @param state
	 *            Visibility state.
	 */
	private void changeRefreshState(boolean state) {
		swipeRefreshLayout.setEnabled(state);
		swipeRefreshLayout.setRefreshing(state);
	}
	
	@Override
	public void onNewCalendar() {
		VirtualCalendar calendar = VirtualCalendarManager.get().getCurrentVirtualCalendar();
		
		this.events.clear();
		
		for (VirtualCalendarEvent event : calendar.getEvents()) {
			long id = event.getUid().hashCode();
			String name = event.getSummary();
			String location = event.getLocation();
			Calendar startTime = event.getStart().toCalendar();
			Calendar endTime = event.getEnd().toCalendar();
			
			WeekViewEvent weekViewEvent = new WeekViewEvent(id, name, location, startTime, endTime);
			weekViewEvent.setColor(EventColorManager.get().getEventColor(event));
			
			events.add(weekViewEvent);
		}
		
		changeRefreshState(false);
		weekView.notifyDatasetChanged();
		
		if (MainActivity.get() != null) {
			MainActivity.get().getSupportActionBar().setTitle(calendar.getName());
		}
	}
	
	@Override
	public void onCalendarDownloadStarted() {
		changeRefreshState(true);
	}
	
	@Override
	public void onCalendarDownloadFailed() {
		changeRefreshState(false);
	}
	
	/**
	 * Change the {@link WeekView week view}'s "type".<br>
	 * It mean how much day must be displayed. This function will also change columns gaps to better adapt the view.
	 * 
	 * @param type
	 *            New {@link WeekView week view}'s "type".
	 * @param item
	 *            {@link MenuItem Item} clicked for calling this function. If not <code>null</code>, the {@link MenuItem item} will be set as checked.
	 */
	private void changeWeekViewType(int type, MenuItem item) {
		if (weekViewType == type) {
			return;
		}
		
		weekViewType = type;
		
		if (item != null) {
			item.setChecked(!item.isChecked());
		}
		
		int numberOfVisibleDays, columnGap, textSize, eventTextSize;
		
		switch (type) {
			case WEEK_VIEW_TYPE_DAY_VIEW: {
				numberOfVisibleDays = 1;
				
				columnGap = 8;
				textSize = 12;
				eventTextSize = 12;
				break;
			}
			
			case WEEK_VIEW_TYPE_THREE_DAY_VIEW: {
				numberOfVisibleDays = 3;
				
				columnGap = 8;
				textSize = 12;
				eventTextSize = 12;
				break;
			}
			
			case WEEK_VIEW_TYPE_WEEK_VIEW: {
				numberOfVisibleDays = 7;
				
				columnGap = 2;
				textSize = 10;
				eventTextSize = 10;
				break;
			}
			
			default: {
				throw new IllegalStateException("Unknown week type: " + type);
			}
		}
		
		weekView.setNumberOfVisibleDays(numberOfVisibleDays);
		
		weekView.setColumnGap((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, columnGap, getResources().getDisplayMetrics()));
		weekView.setTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, textSize, getResources().getDisplayMetrics()));
		weekView.setEventTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, eventTextSize, getResources().getDisplayMetrics()));
	}
	
	/**
	 * Set up a date time interpreter which will show short date values when in week view and long date values otherwise.
	 * 
	 * @param shortDate
	 *            True if the date values should be short.
	 */
	private void setupDateTimeInterpreter(final boolean shortDate) {
		boolean use24HourFormat = android.text.format.DateFormat.is24HourFormat(application);
		
		final DateFormat weekDayNameFormat = new SimpleDateFormat("EEE", Locale.getDefault());
		final DateFormat weekDayFormat = new SimpleDateFormat(" " + getString(R.string.week_day_date_format));
		
		final DateFormat hourDateFormat = new SimpleDateFormat(use24HourFormat ? "HH:mm" : "KK a");
		hourDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
		
		this.weekView.setDateTimeInterpreter(new DateTimeInterpreter() {
			@Override
			public String interpretDate(Calendar date) {
				String weekday = weekDayNameFormat.format(date.getTime());
				
				if (shortDate)
					weekday = String.valueOf(weekday.charAt(0));
				return weekday.toUpperCase() + weekDayFormat.format(date.getTime());
			}
			
			@Override
			public String interpretTime(int hour) {
				return hourDateFormat.format(new Date(hour * 60 * 60 * 1000));
			}
		});
	}
	
	@Override
	public List<? extends WeekViewEvent> onMonthChange(int newYear, int newMonth) {
		List<WeekViewEvent> matchedEvents = new ArrayList<>();
		
		for (WeekViewEvent event : events) {
			if (VirtualCalendarManager.eventMatches(event, newYear, newMonth)) {
				matchedEvents.add(event);
			}
		}
		
		return matchedEvents;
	}
	
	@Override
	public void onEventClick(WeekViewEvent weekViewEvent, RectF eventRect) {
		VirtualCalendar calendar = VirtualCalendarManager.get().getCurrentVirtualCalendar();
		
		for (VirtualCalendarEvent event : calendar.getEvents()) {
			if (weekViewEvent.getId() == event.getUid().hashCode()) {
				ScheduleItemDetailActivity.start(event, weekViewEvent.getColor());
				
				return;
			}
		}
		
		Toast.makeText(application, "Error\nEvent with hash " + weekViewEvent.getId() + " not found.", Toast.LENGTH_SHORT).show();
	}
	
	@Override
	public int getLayoutId() {
		return R.layout.fragment_schedule;
	}
	
	/** @return ScheduleFragment's singleton instance. */
	public static final ScheduleFragment get() {
		return INSTANCE;
	}
	
}
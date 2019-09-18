package caceresenzo.apps.iutschedule.activities;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.marcoscg.easyabout.adapters.EasyAboutAdapter;
import com.marcoscg.easyabout.items.AboutItem;
import com.marcoscg.easyabout.items.HeaderAboutItem;
import com.marcoscg.easyabout.items.NormalAboutItem;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import caceresenzo.apps.iutschedule.R;
import caceresenzo.apps.iutschedule.application.ScheduleApplication;
import caceresenzo.apps.iutschedule.calendar.VirtualCalendarEvent;
import caceresenzo.libs.string.StringUtils;

public class ScheduleItemDetailActivity extends AppCompatActivity {
	
	/* Bundle Keys */
	public static final String BUNDLE_KEY_EVENT = "event";
	public static final String BUNDLE_KEY_COLOR = "color";
	
	/* Views */
	private LinearLayout listLinearLayout;
	
	/* Event */
	private VirtualCalendarEvent event;
	private int color;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_schedule_item_detail);
		
		event = (VirtualCalendarEvent) getIntent().getExtras().getSerializable(BUNDLE_KEY_EVENT);
		color = getIntent().getExtras().getInt(BUNDLE_KEY_COLOR);
		
		if (event == null) {
			finish();
			return;
		}
		
		initializeViews();
		displayEventInformation();
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			super.onBackPressed();
			return true;
		}
		
		return super.onOptionsItemSelected(item);
	}
	
	/** Initialize android's views. */
	private void initializeViews() {
		Toolbar toolbar = findViewById(R.id.activity_schedule_item_detail_toolbar_bar);
		setSupportActionBar(toolbar);
		getSupportActionBar().setElevation(0);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		
		listLinearLayout = findViewById(R.id.activity_schedule_item_detail_linearlayout_list);
	}
	
	/** Fill android's {@link View view}s with {@link VirtualCalendarEvent event}'s data. */
	private void displayEventInformation() {
		getSupportActionBar().setTitle(event.getSummary());
		
		@SuppressWarnings("deprecation")
		Drawable roundShapeDrawable = DrawableCompat.wrap(getResources().getDrawable(R.drawable.shape_circle));
		DrawableCompat.setTint(roundShapeDrawable, color);
		
		addCardItem(null, new HeaderAboutItem.Builder(this)
				.setIcon(roundShapeDrawable)
				.setTitle(event.getSummary())
				.setSubtitle(event.formatTimeRange())
				.build());
		
		addCardItem(null, new NormalAboutItem.Builder(this)
				.setIcon(R.drawable.icon_calendar_black_24dp)
				.setTitle(new SimpleDateFormat(getString(R.string.day_of_week_date_format), Locale.getDefault()).format(event.getStart()))
				.build());
		
		addCardItem(null, new NormalAboutItem.Builder(this)
				.setIcon(R.drawable.icon_map_marker_white_24dp)
				.setTitle(event.getLocation())
				.build());
		
		addCardItem(null, new NormalAboutItem.Builder(this)
				.setIcon(R.drawable.icon_text_white_24dp)
				.setTitle(event.getEscapedDescription())
				.build());
	}
	
	/**
	 * Add a section to the page.
	 * 
	 * @param title
	 *            Section title (can be <code>null</code>).
	 * @param items
	 *            {@link List} of item for the section. This list will be processed to only display one with {@link StringUtils#validate(String...) validated} content.
	 */
	private void addCardItem(String title, AboutItem... items) {
		List<AboutItem> aboutItems = new ArrayList<>();
		
		for (AboutItem item : items) {
			if (StringUtils.validate(item.getTitle())) {
				aboutItems.add(item);
			}
		}
		
		if (!aboutItems.isEmpty()) {
			View view = LayoutInflater.from(this).inflate(R.layout.ea_card_list, null);
			listLinearLayout.addView(view);
			
			TextView cardTitleTextView = view.findViewById(R.id.card_title);
			RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
			
			if (StringUtils.validate(title)) {
				cardTitleTextView.setText(title);
			} else {
				cardTitleTextView.setVisibility(View.GONE);
			}
			
			recyclerView.setLayoutManager(new LinearLayoutManager(this));
			recyclerView.setAdapter(new EasyAboutAdapter(this, aboutItems));
		}
	}
	
	/**
	 * 
	 * Create a start {@link Intent intent} for a {@link ScheduleItemDetailActivity}.
	 * 
	 * @param event
	 *            {@link VirtualCalendarEvent Event} to start with.
	 * @param color
	 *            {@link VirtualCalendarEvent Event}'s associated color.
	 * @return An {@link Intent intent} ready to start a {@link ScheduleItemDetailActivity}.
	 */
	public static Intent createStartIntent(VirtualCalendarEvent event, int color) {
		Intent intent = new Intent(ScheduleApplication.get(), ScheduleItemDetailActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.putExtra(BUNDLE_KEY_EVENT, event);
		intent.putExtra(BUNDLE_KEY_COLOR, color);
		
		return intent;
	}
	
	/**
	 * Quickly start a {@link ScheduleItemDetailActivity}.
	 * 
	 * @param event
	 *            {@link VirtualCalendarEvent Event} to start with.
	 * @param color
	 *            {@link VirtualCalendarEvent Event}'s associated color.
	 * @see #createStartIntent(VirtualCalendarEvent, int) Create starting {@link Intent intent}.
	 */
	public static void start(VirtualCalendarEvent event, int color) {
		ScheduleApplication.get().startActivity(createStartIntent(event, color));
	}
	
}
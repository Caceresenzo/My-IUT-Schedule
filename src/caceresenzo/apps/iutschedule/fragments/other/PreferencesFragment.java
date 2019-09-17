package caceresenzo.apps.iutschedule.fragments.other;

import android.support.v7.preference.Preference;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.PreferenceFragmentCompat;
import caceresenzo.apps.iutschedule.R;
import caceresenzo.apps.iutschedule.activities.IntroActivity;
import caceresenzo.apps.iutschedule.services.ScheduleNotificationService;

public class PreferencesFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {
	
	/* Variables */
	private boolean initialization;
	
	@Override
	public void onCreatePreferences(Bundle bundle, String rootKey) {
		setPreferencesFromResource(R.xml.preferences_main, rootKey);
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		int[] keysStringIds = {
				R.string.pref_main_time_iteration_relay_key,
				R.string.pref_main_time_refresh_relay_key,
		};
		
		initialization = true;
		
		for (int keyStringId : keysStringIds) {
			onSharedPreferenceChanged(getPreferenceScreen().getSharedPreferences(), getString(keyStringId));
		}
		
		findPreference(getString(R.string.pref_main_general_reset_key)).setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference preference) {
				IntroActivity.start(true);
				return false;
			}
		});
		
		initialization = false;
	}
	
	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		Preference preference = findPreference(key);
		
		if (preference == null) {
			return;
		}
		
		if (preference instanceof ListPreference) {
			ListPreference listPreference = (ListPreference) preference;
			
			if (key.equals(getString(R.string.pref_main_time_iteration_relay_key))) {
				int prefIndex = listPreference.findIndexOfValue(sharedPreferences.getString(key, getString(R.string.pref_main_time_refresh_relay_default)));
				ListPreference dependencyListFragment = (ListPreference) findPreference(getString(R.string.pref_main_time_refresh_relay_key));
				String summary;
				
				dependencyListFragment.setEnabled(prefIndex != 0);
				onSharedPreferenceChanged(sharedPreferences, dependencyListFragment.getKey());
				
				if (!dependencyListFragment.isEnabled()) {
					summary = getString(R.string.pref_main_time_iteration_relay_summary_disabled);
				} else {
					summary = getString(R.string.pref_main_time_iteration_relay_summary, listPreference.getEntries()[prefIndex]);
				}
				
				preference.setSummary(summary);
			} else if (key.equals(getString(R.string.pref_main_time_refresh_relay_key))) {
				int prefIndex = listPreference.findIndexOfValue(sharedPreferences.getString(key, getString(R.string.pref_main_time_refresh_relay_default)));
				String summary;
				
				if (prefIndex == 0 || !preference.isEnabled()) {
					summary = getString(R.string.pref_main_time_refresh_relay_summary_disabled);
				} else {
					summary = getString(R.string.pref_main_time_refresh_relay_summary, listPreference.getEntries()[prefIndex]);
				}
				
				if (!initialization) {
					/* Send update to service */
					if (ScheduleNotificationService.get() != null) {
						ScheduleNotificationService.get().restartTimer();
					} else {
						ScheduleNotificationService.startIfNotAlready(getContext());
					}
					
				}
				preference.setSummary(summary);
			}
		}
	}
	
	@Override
	public void onResume() {
		super.onResume();
		getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
	}
	
	@Override
	public void onPause() {
		super.onPause();
		getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
	}
	
}
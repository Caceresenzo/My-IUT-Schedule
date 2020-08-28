package caceresenzo.apps.iutschedule.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import caceresenzo.apps.iutschedule.application.ScheduleApplication;
import caceresenzo.apps.iutschedule.utils.Restorable;

public abstract class BaseFragment extends Fragment implements Restorable {
	
	/* Tag */
	public static final String TAG = BaseFragment.class.getSimpleName();
	
	/* Managers */
	protected ScheduleApplication application;
	
	/* Variables */
	protected boolean destroyed;
	protected Context context;
	
	/* Constructor */
	public BaseFragment() {
		this.application = ScheduleApplication.get();
		
		this.destroyed = false;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(getLayoutId(), container, false);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		onUiReady(savedInstanceState);
	}
	
	/** Called when an {@link Activity activity} has attached the views. */
	public abstract void onUiReady(Bundle savedInstanceState);
	
	@Override
	public void onAttach(Context context) {
		super.onAttach(context);
		
		this.context = context;
	}
	
	@Override
	public void saveInstanceState(Bundle outState) {
		;
	}
	
	@Override
	public void restoreInstanceState(Bundle savedInstanceState) {
		;
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		
		destroyed = true;
		context = null;
	}
	
	/** @return Weather or not the {@link Fragment}'s {@link Context context} is still valid. */
	public boolean isContextValid() {
		return !destroyed && context != null;
	}
	
	/** @return The layout's id for this {@link Fragment fragment}. */
	public abstract int getLayoutId();
	
}
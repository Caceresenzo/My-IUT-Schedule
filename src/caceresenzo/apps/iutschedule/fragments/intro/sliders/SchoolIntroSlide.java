package caceresenzo.apps.iutschedule.fragments.intro.sliders;

import java.util.ArrayList;
import java.util.List;

import agency.tango.materialintroscreen.SlideFragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;
import caceresenzo.apps.iutschedule.R;
import caceresenzo.apps.iutschedule.models.School;

public class SchoolIntroSlide extends SlideFragment {
	
	/* Views */
	private RecyclerView schoolRecyclerView;
	private RadioButton isPresentRadioButton, isNotPresentRadioButton;
	
	/* Variables */
	private final List<School> schools;
	private Boolean isPresent;
	
	/* Constructor */
	public SchoolIntroSlide() {
		super();
		
		this.schools = new ArrayList<>();
		
		loadSchools();
	}
	
	/** Load schools in memory. */
	private void loadSchools() {
		schools.add(new School("CDE", "CES DROIT CHATEAUROUX"));
		schools.add(new School("ALE", "CES LETTRES CHATEAUROUX"));
		schools.add(new School("SCT", "COLLEGIUM"));
		schools.add(new School("DEG", "DEG"));
		schools.add(new School("BDE", "DEG BOURGES"));
		schools.add(new School("100", "ESPE"));
		schools.add(new School("105", "ESPE BLOIS"));
		schools.add(new School("101", "ESPE BOURGES"));
		schools.add(new School("102", "ESPE CHARTRES"));
		schools.add(new School("103", "ESPE CHATEAUROUX"));
		schools.add(new School("106", "ESPE ORLEANS"));
		schools.add(new School("104", "ESPE TOURS"));
		schools.add(new School("IRK", "EUK"));
		schools.add(new School("IFO", "IFUO"));
		schools.add(new School("IBG", "IUT BOURGES"));
		schools.add(new School("ICS", "IUT CHARTRES"));
		schools.add(new School("ICX", "IUT CHATEAUROUX"));
		schools.add(new School("AIU", "IUT ISSOUDUN"));
		schools.add(new School("IOR", "IUT ORLEANS"));
		schools.add(new School("LET", "LLSH"));
		schools.add(new School("OSU", "OSUC"));
		schools.add(new School("EPL", "POLYTECH"));
		schools.add(new School("EPS", "POLYTECH SITE CHARTRES"));
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		final View view = inflater.inflate(R.layout.fragment_slide_schools, container, false);
		
		schoolRecyclerView = view.findViewById(R.id.fragment_slide_schools_recyclerview_list);
		isPresentRadioButton = view.findViewById(R.id.fragment_slide_schools_radiobutton_is_present);
		isNotPresentRadioButton = view.findViewById(R.id.fragment_slide_schools_radiobutton_is_not_present);
		
		schoolRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
		schoolRecyclerView.setHasFixedSize(true);
		schoolRecyclerView.setAdapter(new SchoolRecyclerViewAdapter(schools));
		
		if (isPresent != null) {
			(isPresent ? isPresentRadioButton : isNotPresentRadioButton).setChecked(true);
		}
		
		return view;
	}
	
	@Override
	public int backgroundColor() {
		return R.color.colorPrimary;
	}
	
	@Override
	public int buttonsColor() {
		return R.color.colorAccent;
	}
	
	@Override
	public boolean canMoveFurther() {
		return (isPresent = isPresentRadioButton.isChecked()) || isNotPresentRadioButton.isChecked();
	}
	
	@Override
	public String cantMoveFurtherErrorMessage() {
		return getString(R.string.intro_school_error_select_choice);
	}
	
	public static class SchoolRecyclerViewAdapter extends RecyclerView.Adapter<SchoolRecyclerViewAdapter.SchoolViewHolder> {
		
		/* Variables */
		private final List<School> schools;
		
		/* Constructor */
		public SchoolRecyclerViewAdapter(List<School> schools) {
			this.schools = schools;
		}
		
		@Override
		public SchoolRecyclerViewAdapter.SchoolViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
			return new SchoolViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_school, parent, false));
		}
		
		@Override
		public void onBindViewHolder(SchoolViewHolder holder, int position) {
			holder.bind(schools.get(position));
		}
		
		@Override
		public int getItemCount() {
			return schools.size();
		}
		
		public static class SchoolViewHolder extends RecyclerView.ViewHolder {
			
			/* Views */
			public TextView nameTextView;
			
			/* Constructor */
			public SchoolViewHolder(View itemView) {
				super(itemView);
				
				this.nameTextView = itemView.findViewById(R.id.item_school_name);
			}
			
			/**
			 * Bind this {@link RecyclerView.ViewHolder view holder} to some data.
			 * 
			 * @param school
			 *            {@link School} instance to bind with.
			 */
			public void bind(School school) {
				nameTextView.setText(school.getName());
			}
		}
		
	}
	
}
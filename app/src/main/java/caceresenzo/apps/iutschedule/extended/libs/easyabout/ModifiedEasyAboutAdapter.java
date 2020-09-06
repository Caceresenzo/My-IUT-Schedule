package caceresenzo.apps.iutschedule.extended.libs.easyabout;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

import com.marcoscg.easyabout.items.AboutItem;
import com.marcoscg.easyabout.items.HeaderAboutItem;
import com.marcoscg.easyabout.items.PersonAboutItem;
import com.marcoscg.easyabout.utils.ColorUtils;
import com.marcoscg.easyabout.views.IconView;

import java.util.List;

import caceresenzo.apps.iutschedule.R;

/**
 * Original code from https://github.com/marcoscgdev/EasyAbout/blob/master/easyabout/src/main/java/com/marcoscg/easyabout/adapters/EasyAboutAdapter.java .
 * Only necessary part has been modified.
 */
public final class ModifiedEasyAboutAdapter extends RecyclerView.Adapter<ModifiedEasyAboutAdapter.ModifiedMyViewHolder> {

	/* Tag */
	public static final String TAG = ModifiedEasyAboutAdapter.class.getSimpleName();

	private Context context;
	private List<AboutItem> aboutItemList;
	private final int ITEM_TYPE_HEADER = 0;
	private final int ITEM_TYPE_NORMAL = 1;
	private final int ITEM_TYPE_PERSON = 2;
	private final int ITEM_TYPE_CUSTOM = 99;

	public ModifiedEasyAboutAdapter(@NonNull Context context, List<AboutItem> aboutItemList) {
		this.context = context;
		this.aboutItemList = aboutItemList;
	}

	public void onBindViewHolder(ModifiedMyViewHolder holder, int position) {
		AboutItem aboutItem = (AboutItem) this.aboutItemList.get(holder.getAdapterPosition());

		int color = ColorUtils.getThemeAttrColor(this.context, "aboutCardBackground");

		if (color != 0) {
			int primaryColor = ColorUtils.isDark(color) ? this.getClr(com.marcoscg.easyabout.R.color.about_primary_text_color_dark) : this.getClr(com.marcoscg.easyabout.R.color.about_primary_text_color_light);
			int secondaryColor = ColorUtils.isDark(color) ? this.getClr(com.marcoscg.easyabout.R.color.about_secondary_text_color_dark) : this.getClr(com.marcoscg.easyabout.R.color.about_secondary_text_color_light);
			if (holder.icon instanceof IconView) {
				((IconView) holder.icon).setColor(secondaryColor);
			}

			if (!(aboutItem instanceof CustomAboutItem)) {
				holder.title.setTextColor(primaryColor);
				holder.subtitle.setTextColor(secondaryColor);
			}
		}

		if (!(aboutItem instanceof CustomAboutItem)) {
			if (aboutItem.getTitle() == null) {
				holder.title.setVisibility(View.GONE);
			} else {
				holder.title.setText(aboutItem.getTitle());
			}

			if (aboutItem.getSubtitle() == null) {
				holder.subtitle.setVisibility(View.GONE);
			} else {
				holder.subtitle.setText(aboutItem.getSubtitle());
			}
		} else {
			CustomAboutItem customAboutItem = (CustomAboutItem) aboutItem;

			View view = customAboutItem.getView();

			if (view != null) {
				boolean canAdd = true;

				if (view.getParent() instanceof ViewGroup) {
					((ViewGroup) view.getParent()).removeView(view);
				} else {
					if (view.getParent() != null) {
						Log.w(TAG, "Child seems to have a parent which is not a ViewGroup.");
						canAdd = false;
					}
				}

				if (canAdd) {
					holder.container.addView(customAboutItem.getView());
				}
			}
		}

		if (aboutItem.getIcon() == null) {
			holder.icon.setImageDrawable(new ColorDrawable(0));
		} else {
			holder.icon.setImageDrawable(aboutItem.getIcon());
		}

		if (aboutItem.getOnClickListener() != null) {
			holder.itemContainer.setOnClickListener(aboutItem.getOnClickListener());
		}

		if (aboutItem.getOnLongClickListener() != null) {
			holder.itemContainer.setOnLongClickListener(aboutItem.getOnLongClickListener());
		}

	}

	public ModifiedMyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		int layout;

		switch (viewType) {
			case ITEM_TYPE_HEADER: {
				layout = com.marcoscg.easyabout.R.layout.ea_header_about_item;
				break;
			}

			case ITEM_TYPE_PERSON: {
				layout = com.marcoscg.easyabout.R.layout.ea_person_about_item;
				break;
			}

			case ITEM_TYPE_CUSTOM: {
				layout = R.layout.ea_custom_about_item;
				break;
			}

			case ITEM_TYPE_NORMAL:
			default: {
				layout = R.layout.ea_normal_about_item;
				break;
			}
		}

		View v = LayoutInflater.from(parent.getContext()).inflate(layout, parent, false);
		return new ModifiedMyViewHolder(v);
	}

	public int getItemViewType(int position) {
		AboutItem aboutItem = (AboutItem) this.aboutItemList.get(position);

		if (aboutItem instanceof HeaderAboutItem) {
			return ITEM_TYPE_HEADER;
		} else if (aboutItem instanceof PersonAboutItem) {
			return ITEM_TYPE_PERSON;
		} else if (aboutItem instanceof CustomAboutItem) {
			return ITEM_TYPE_CUSTOM;
		} else {
			return ITEM_TYPE_NORMAL;
		}
	}

	public int getItemCount() {
		return this.aboutItemList.size();
	}

	private int getClr(int colorRes) {
		return this.context.getResources().getColor(colorRes);
	}

	public List<AboutItem> getItemList() {
		return aboutItemList;
	}

	class ModifiedMyViewHolder extends RecyclerView.ViewHolder {
		private LinearLayout itemContainer;
		private LinearLayout container;
		private TextView title;
		private TextView subtitle;
		private AppCompatImageView icon;

		ModifiedMyViewHolder(View view) {
			super(view);
			this.itemContainer = (LinearLayout) view.findViewById(com.marcoscg.easyabout.R.id.item_container);
			this.container = (LinearLayout) view.findViewById(R.id.container);
			this.title = (TextView) view.findViewById(com.marcoscg.easyabout.R.id.title);
			this.subtitle = (TextView) view.findViewById(com.marcoscg.easyabout.R.id.subtitle);
			this.icon = (AppCompatImageView) view.findViewById(com.marcoscg.easyabout.R.id.icon);
		}
	}
}
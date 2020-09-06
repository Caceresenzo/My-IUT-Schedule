//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package caceresenzo.apps.iutschedule.extended.libs.easyabout;

import android.content.Context;
import android.content.res.Resources.Theme;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;

import androidx.annotation.DrawableRes;
import androidx.core.content.res.ResourcesCompat;

import com.marcoscg.easyabout.items.AboutItem;

public final class CustomAboutItem extends AboutItem {

	private final View view;

	private CustomAboutItem(CustomAboutItem.Builder builder) {
		super(null, null, builder.icon, builder.onClickListener, builder.onLongClickListener);

		this.view = builder.view;
	}

	public View getView() {
		return view;
	}

	public static class Builder {
		private Context context;
		private Drawable icon;
		private OnClickListener onClickListener;
		private OnLongClickListener onLongClickListener;
		private View view;

		public Builder(Context context) {
			this.context = context;
		}

		public CustomAboutItem.Builder setIcon(Drawable icon) {
			this.icon = icon;
			return this;
		}

		public CustomAboutItem.Builder setIcon(@DrawableRes int icon) {
			this.icon = ResourcesCompat.getDrawable(this.context.getResources(), icon, (Theme) null);
			return this;
		}

		public CustomAboutItem.Builder setOnClickListener(OnClickListener onClickListener) {
			this.onClickListener = onClickListener;
			return this;
		}

		public CustomAboutItem.Builder setOnLongClickListener(OnLongClickListener onLongClickListener) {
			this.onLongClickListener = onLongClickListener;
			return this;
		}

		public CustomAboutItem.Builder setView(View view) {
			this.view = view;
			return this;
		}

		public CustomAboutItem build() {
			return new CustomAboutItem(this);
		}
	}
}

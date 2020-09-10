package caceresenzo.apps.iutschedule.fragments.other;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import com.marcoscg.easyabout.EasyAboutFragment;
import com.marcoscg.easyabout.helpers.AboutItemBuilder;
import com.marcoscg.easyabout.items.AboutCard;
import com.marcoscg.easyabout.items.NormalAboutItem;
import com.marcoscg.easyabout.items.PersonAboutItem;

import caceresenzo.apps.iutschedule.BuildConfig;
import caceresenzo.apps.iutschedule.R;

public class AboutFragment extends EasyAboutFragment {

	@Override
	protected void configureFragment(Context context, View rootView, Bundle savedInstanceState) {
		if (BuildConfig.DEBUG) {
			addCard(new AboutCard.Builder(context)
					.addItem(new NormalAboutItem.Builder(context)
							.setTitle("Debug Build")
							.build())
					.build());
		}

		addCard(new AboutCard.Builder(context)
				.addItem(AboutItemBuilder.generateAppTitleItem(context)
						.setSubtitle(R.string.about_application_author))
				.addItem(AboutItemBuilder.generateAppVersionItem(context, true)
						.setIcon(R.drawable.icon_info_white_24dp))
				.addItem(AboutItemBuilder.generateLinkItem(context, getString(R.string.application_github))
						.setTitle(R.string.about_application_github)
						.setIcon(R.drawable.icon_social_github_black_24dp))
				.build());

		addCard(new AboutCard.Builder(context)
				.setTitle(R.string.about_author)
				.addItem(new PersonAboutItem.Builder(context)
						.setTitle(R.string.about_author_author_detail)
						.setSubtitle(R.string.about_author_author_location)
						.setIcon(R.drawable.profile_caceresenzo)
						.build())
				.addItem(AboutItemBuilder.generateLinkItem(context, getString(R.string.author_github))
						.setTitle(R.string.about_author_github)
						.setIcon(R.drawable.icon_social_github_black_24dp))
				.addItem(AboutItemBuilder.generateEmailItem(context, getString(R.string.application_email))
						.setTitle(R.string.about_author_email)
						.setIcon(R.drawable.icon_mail_outline_white_24dp))
				.build());

		addCard(new AboutCard.Builder(context)
				.setTitle(R.string.about_description)
				.addItem(new NormalAboutItem.Builder(context)
						.setTitle(R.string.about_description_text)
						.build())
				.build());
	}

}
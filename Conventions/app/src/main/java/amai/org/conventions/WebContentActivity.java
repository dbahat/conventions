package amai.org.conventions;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Html;
import android.widget.TextView;

import amai.org.conventions.navigation.NavigationActivity;
import amai.org.conventions.utils.Views;
import androidx.annotation.StringRes;
import androidx.core.text.method.LinkMovementMethodCompat;
import fi.iki.kuitsi.listtest.ListTagHandler;
import sff.org.conventions.R;

public abstract class WebContentActivity extends NavigationActivity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentInContentContainer(R.layout.activity_web_content);
		setBackground(ThemeAttributes.getDrawable(this, R.attr.infoActivitiesBackground));
		setToolbarTitle(getString(getPageTitleResourceId()));

		// Handle edge to edge
		Views.registerApplyInsets(Views.InsetType.NONE, Views.InsetType.PADDING, Views.InsetType.PADDING, Views.InsetType.PADDING, findViewById(R.id.web_content_scroll));

		handleDeepLinks();

		TextView webContentContainer = findViewById(R.id.web_content);
		if (webContentContainer != null) {
			webContentContainer.setText(Html.fromHtml(getString(getWebContentResourceId()), null, new ListTagHandler()));
			webContentContainer.setMovementMethod(LinkMovementMethodCompat.getInstance());
		}
	}
	protected abstract @StringRes
	int getPageTitleResourceId();
	protected abstract @StringRes int getWebContentResourceId();

	private void handleDeepLinks() {
		Uri intentData = getIntent().getData();
		if (intentData != null && intentData.getPath() != null) {
			switch (intentData.getPath().intern()) {
				case "/open-accessibility": {
					Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
					startActivity(intent);
					finish(); // This activity was opened again due to the deep link so close the new instance
					break;
				}
			}
		}
	}

	public static class AccessibilityActivity extends WebContentActivity {

		@Override
		protected int getPageTitleResourceId() {
			return R.string.accessibility;
		}

		@Override
		protected int getWebContentResourceId() {
			return R.string.accessibility_content;
		}
	}

	public static class IconKidsActivity extends WebContentActivity {

		@Override
		protected int getPageTitleResourceId() {
			return R.string.icon_kids;
		}

		@Override
		protected int getWebContentResourceId() {
			return R.string.icon_kids_content;
		}
	}
}


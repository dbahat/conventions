package amai.org.conventions;

import android.os.Bundle;
import android.support.annotation.StringRes;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

import amai.org.conventions.navigation.NavigationActivity;
import fi.iki.kuitsi.listtest.ListTagHandler;
import sff.org.conventions.R;

public abstract class WebContentActivity extends NavigationActivity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentInContentContainer(R.layout.activity_web_content);
		setToolbarTitle(getString(getPageTitleResourceId()));

		TextView webContentContainer = findViewById(R.id.web_content);
		if (webContentContainer != null) {
			webContentContainer.setText(Html.fromHtml(getString(getWebContentResourceId()), null, new ListTagHandler()));
			webContentContainer.setMovementMethod(LinkMovementMethod.getInstance());
		}
	}
	protected abstract @StringRes int getPageTitleResourceId();
	protected abstract @StringRes int getWebContentResourceId();

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


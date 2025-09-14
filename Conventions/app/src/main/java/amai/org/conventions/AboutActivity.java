package amai.org.conventions;

import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

import amai.org.conventions.navigation.NavigationActivity;
import amai.org.conventions.utils.Views;
import androidx.core.text.method.LinkMovementMethodCompat;
import fi.iki.kuitsi.listtest.ListTagHandler;
import sff.org.conventions.R;


public class AboutActivity extends NavigationActivity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentInContentContainer(R.layout.activity_about);
		setBackground(ThemeAttributes.getDrawable(this, R.attr.infoActivitiesBackground));
		setToolbarTitle(getString(R.string.about));

		// Handle edge to edge
		Views.registerApplyInsets(Views.InsetType.NONE, Views.InsetType.NONE, Views.InsetType.PADDING, Views.InsetType.PADDING, findViewById(R.id.about_scroll));
		// The bottom padding must be applied to a view inside the scroll view, otherwise the scrolling doesn't include the padding when scrolled from the last
		// textview and it has a LinkMovementMethod
		Views.registerApplyInsets(Views.InsetType.NONE, Views.InsetType.PADDING, Views.InsetType.NONE, Views.InsetType.NONE, findViewById(R.id.scroll_view_bottom_padding));

		TextView aboutView = (TextView) findViewById(R.id.about_content);
		aboutView.setText(Html.fromHtml(getString(R.string.about_content), null, new ListTagHandler()));
		aboutView.setMovementMethod(LinkMovementMethodCompat.getInstance());

		TextView linksView = (TextView) findViewById(R.id.about_links);
		linksView.setText(Html.fromHtml(getString(R.string.about_links), null, new ListTagHandler()));
		linksView.setMovementMethod(LinkMovementMethodCompat.getInstance());

		TextView aboutAppView = (TextView) findViewById(R.id.about_app_content);
		aboutAppView.setText(Html.fromHtml(getString(R.string.about_app_content), null, new ListTagHandler()));
		aboutAppView.setMovementMethod(LinkMovementMethodCompat.getInstance());

		TextView aboutAppVersion = (TextView) findViewById(R.id.about_app_version);
		String versionName = ConventionsApplication.getVersionName();
		if (versionName == null || versionName.trim().isEmpty()) {
			aboutAppVersion.setVisibility(View.GONE);
		} else {
			aboutAppVersion.setText(getString(R.string.app_version, versionName));
		}
	}
}

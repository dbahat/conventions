package amai.org.conventions;

import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.TextView;

import amai.org.conventions.navigation.NavigationActivity;
import fi.iki.kuitsi.listtest.ListTagHandler;
import sff.org.conventions.R;


public class AboutActivity extends NavigationActivity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentInContentContainer(R.layout.activity_about);
		setToolbarTitle(getString(R.string.about));

		TextView aboutView = (TextView) findViewById(R.id.about_content);
		aboutView.setText(Html.fromHtml(getString(R.string.about_content), null, new ListTagHandler()));
		aboutView.setMovementMethod(LinkMovementMethod.getInstance());

		TextView linksView = (TextView) findViewById(R.id.about_links);
		linksView.setText(Html.fromHtml(getString(R.string.about_links), null, new ListTagHandler()));
		linksView.setMovementMethod(LinkMovementMethod.getInstance());

		TextView aboutAppView = (TextView) findViewById(R.id.about_app_content);
		aboutAppView.setText(Html.fromHtml(getString(R.string.about_app_content), null, new ListTagHandler()));
		aboutAppView.setMovementMethod(LinkMovementMethod.getInstance());

		TextView aboutAppVersion = (TextView) findViewById(R.id.about_app_version);
		String versionName = ConventionsApplication.getVersionName();
		if (versionName == null || versionName.trim().isEmpty()) {
			aboutAppVersion.setVisibility(View.GONE);
		} else {
			aboutAppVersion.setText(getString(R.string.app_version, versionName));
		}
	}
}

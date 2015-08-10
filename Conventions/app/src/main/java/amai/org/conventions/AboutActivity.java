package amai.org.conventions;

import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

import amai.org.conventions.navigation.NavigationActivity;


public class AboutActivity extends NavigationActivity {
	private final static String linksText = "<p><a href=\"http://www.amai.org.il/\">לאתר אמא\"י</a></p>" +
			"<p><a href=\"http://2015.cami.org.il/\">לאתר הכנס</a></p>" +
			"<p><a href=\"http://2015.cami.org.il/%D7%A7%D7%A8%D7%93%D7%99%D7%98%D7%99%D7%9D/\">קרדיטים לאומנים</a></p>";

	private final static String aboutAppText = "האפליקציה פותחה עבור כנס כאמ\"י 2015.<br/>בקשות והצעות ניתן לכתוב <a href=\"market://details?id=amai.org.conventions\">בדף האפליקציה בחנות</a>.";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentInContentContainer(R.layout.activity_about);
		setToolbarTitle(getString(R.string.about));

		TextView linksView = (TextView) findViewById(R.id.about_amai_links);
		linksView.setText(Html.fromHtml(linksText));
		linksView.setMovementMethod(LinkMovementMethod.getInstance());

		TextView aboutAppView = (TextView) findViewById(R.id.about_app_content);
		aboutAppView.setText(Html.fromHtml(aboutAppText));
		aboutAppView.setMovementMethod(LinkMovementMethod.getInstance());
	}
}

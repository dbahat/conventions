package amai.org.conventions;

import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

import amai.org.conventions.navigation.NavigationActivity;
import fi.iki.kuitsi.listtest.ListTagHandler;
import sff.org.conventions.R;


public class DiscountsActivity extends NavigationActivity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		int layout = ThemeAttributes.getResourceId(this, R.attr.discountsActivityLayout);
		setContentInContentContainer(layout);
		setToolbarTitle(getString(R.string.discounts));

		TextView discountsView = (TextView) findViewById(R.id.discounts_content);
		if (discountsView != null) {
			discountsView.setText(Html.fromHtml(getString(R.string.discounts_content), null, new ListTagHandler()));
			discountsView.setMovementMethod(LinkMovementMethod.getInstance());
		}
	}
}

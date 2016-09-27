package amai.org.conventions;

import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

import amai.org.conventions.navigation.NavigationActivity;
import sff.org.conventions.R;


public class DiscountsActivity extends NavigationActivity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentInContentContainer(R.layout.activity_discounts);
		setToolbarTitle(getString(R.string.discounts));

		TextView discountsView = (TextView) findViewById(R.id.discounts_content);
		discountsView.setText(Html.fromHtml(getString(R.string.discounts_content)));
		discountsView.setMovementMethod(LinkMovementMethod.getInstance());
	}
}

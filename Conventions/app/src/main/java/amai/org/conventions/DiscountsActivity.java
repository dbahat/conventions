package amai.org.conventions;

import android.os.Bundle;

import amai.org.conventions.navigation.NavigationActivity;
import sff.org.conventions.R;


public class DiscountsActivity extends NavigationActivity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		int layout = ThemeAttributes.getResourceId(this, R.attr.discountsActivityLayout);
		setContentInContentContainer(layout);
		setToolbarTitle(getString(R.string.discounts));
	}
}

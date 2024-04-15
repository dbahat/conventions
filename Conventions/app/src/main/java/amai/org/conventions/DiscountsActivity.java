package amai.org.conventions;

import android.os.Bundle;
import android.view.ViewGroup;

import amai.org.conventions.navigation.NavigationActivity;
import amai.org.conventions.utils.Views;
import sff.org.conventions.R;


public class DiscountsActivity extends NavigationActivity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		int layout = ThemeAttributes.getResourceId(this, R.attr.discountsActivityLayout);
		setContentInContentContainer(layout);
		setToolbarTitle(getString(R.string.discounts));

		handleLinks();
	}

	private void handleLinks() {
		ViewGroup contentContainer = findViewById(R.id.discounts_content_container);
		if (contentContainer == null) {
			return;
		}

		Views.enableLinkClicks(contentContainer);
	}
}

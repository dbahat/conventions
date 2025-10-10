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
		setContentInContentContainer(R.layout.activity_discounts);
		setToolbarTitle(getString(R.string.discounts));

		// Handle edge to edge
		Views.registerApplyInsets(Views.InsetType.NONE, Views.InsetType.NONE, Views.InsetType.PADDING, Views.InsetType.PADDING, false, findViewById(R.id.discounts_scroll));
		// The bottom padding must be applied to a view inside the scroll view, otherwise the scrolling doesn't include the padding when scrolled from the last
		// textview and it has a LinkMovementMethod
		Views.registerApplyInsets(Views.InsetType.NONE, Views.InsetType.PADDING, Views.InsetType.NONE, Views.InsetType.NONE, false, findViewById(R.id.scroll_view_bottom_padding));

		handleLinks();
	}

	private void handleLinks() {
		ViewGroup contentContainer = findViewById(R.id.discounts_content_container);
		if (contentContainer == null) {
			return;
		}

		Views.enableLinkClicks(contentContainer);

		// The texts may have links to events
		Views.runOnAllTextViews(contentContainer, this::interceptEventLinks);
	}
}

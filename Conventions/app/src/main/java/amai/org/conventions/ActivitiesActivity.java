package amai.org.conventions;

import android.graphics.Point;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ScrollView;

import amai.org.conventions.navigation.NavigationActivity;
import amai.org.conventions.utils.Views;
import sff.org.conventions.R;

public class ActivitiesActivity extends NavigationActivity {
	public static final String EXTRA_FOCUS_ON_VIEW = "ExtraFocusOnView";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentInContentContainer(R.layout.activity_activities);
		setToolbarTitle(getString(R.string.activities));

		// Handle edge to edge
		Views.registerApplyInsets(Views.InsetType.NONE, Views.InsetType.NONE, Views.InsetType.PADDING, Views.InsetType.PADDING, findViewById(R.id.activities_scroll));
		// The bottom padding must be applied to a view inside the scroll view, otherwise the scrolling doesn't include the padding when scrolled from the last
		// textview and it has a LinkMovementMethod
		Views.registerApplyInsets(Views.InsetType.NONE, Views.InsetType.PADDING, Views.InsetType.NONE, Views.InsetType.NONE, findViewById(R.id.scroll_view_bottom_padding));

		handleLinks();

		final int focusOnView = getIntent().getIntExtra(EXTRA_FOCUS_ON_VIEW, Views.NO_VIEW);

		if (focusOnView != Views.NO_VIEW) {
			View viewToFocus = findViewById(focusOnView);
			if (viewToFocus != null) {
				ScrollView scrollView = findViewById(R.id.activities_scroll);
				scrollView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
					public void onGlobalLayout() {
						// Unregister the listener to only call smoothScrollTo once
						scrollView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
						scrollView.post(new Runnable() {
							@Override
							public void run() {
								Point coordinates = Views.findCoordinates(scrollView, viewToFocus);
								scrollView.smoothScrollTo(coordinates.x, coordinates.y);
							}
						});
					}
				});
			}
		}
	}

	private void handleLinks() {
		ViewGroup contentContainer = findViewById(R.id.activities_content_container);
		if (contentContainer == null) {
			return;
		}

		Views.enableLinkClicks(contentContainer);

		// The texts may have links to events
		Views.runOnAllTextViews(contentContainer, this::interceptEventLinks);
	}
}
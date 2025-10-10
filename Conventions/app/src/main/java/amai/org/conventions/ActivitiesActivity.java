package amai.org.conventions;

import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ScrollView;

import java.util.List;

import amai.org.conventions.map.MapActivity;
import amai.org.conventions.model.ConventionMap;
import amai.org.conventions.model.MapLocation;
import amai.org.conventions.model.conventions.Convention;
import amai.org.conventions.navigation.NavigationActivity;
import amai.org.conventions.utils.CollectionUtils;
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
		Views.registerApplyInsets(Views.InsetType.NONE, Views.InsetType.NONE, Views.InsetType.PADDING, Views.InsetType.PADDING, false, findViewById(R.id.activities_scroll));
		// The bottom padding must be applied to a view inside the scroll view, otherwise the scrolling doesn't include the padding when scrolled from the last
		// textview and it has a LinkMovementMethod
		Views.registerApplyInsets(Views.InsetType.NONE, Views.InsetType.PADDING, Views.InsetType.NONE, Views.InsetType.NONE, false, findViewById(R.id.scroll_view_bottom_padding));

		handleDeepLinks();
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

	private void handleDeepLinks() {
		Uri intentData = getIntent().getData();
		if (intentData != null && intentData.getPath() != null) {
			switch (intentData.getPath().intern()) {
				case "/open-map": {
					//The URL looks like this: sff.org.conventions://activities/open-map?location=name
					String location = intentData.getQueryParameter("location");
					ConventionMap map = Convention.getInstance().getMap();
					List<MapLocation> locations = map.findLocationsByName(location);
					int[] locationIds = CollectionUtils.mapToInt(locations, MapLocation::getId);
					Bundle floorBundle = new Bundle();
					floorBundle.putIntArray(MapActivity.EXTRA_MAP_LOCATION_IDS, locationIds);
					navigateToActivity(MapActivity.class, false, floorBundle);
					break;
				}
			}
			finish(); // This activity was opened again due to the deep link so close the new instance
		}
	}
}
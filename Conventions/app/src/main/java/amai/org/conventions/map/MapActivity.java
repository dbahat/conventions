package amai.org.conventions.map;

import android.content.DialogInterface;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import amai.org.conventions.ThemeAttributes;
import amai.org.conventions.customviews.ConditionalSwipeVerticalViewPager;
import amai.org.conventions.model.ConventionMap;
import amai.org.conventions.model.DetailsActivityLocation;
import amai.org.conventions.model.Floor;
import amai.org.conventions.model.MapLocation;
import amai.org.conventions.model.Stand;
import amai.org.conventions.model.StandsArea;
import amai.org.conventions.model.conventions.Convention;
import amai.org.conventions.navigation.NavigationActivity;
import amai.org.conventions.utils.CollectionUtils;
import amai.org.conventions.utils.Objects;
import amai.org.conventions.utils.Views;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;
import sff.org.conventions.R;

public class MapActivity extends NavigationActivity implements MapFloorFragment.OnMapFloorEventListener {
	public static final String EXTRA_FLOOR_NUMBER = "ExtraFloorNumber";
	public static final String EXTRA_MAP_LOCATION_IDS = "ExtraMapLocationId";

	private static final String STATE_SEARCH_TERM = "StateMapSearchTerm";
	private static final String STATE_MAP_SEARCH_ONLY_HALLS = "StateMapSearchOnlyHalls";
	private static final String STATE_MAP_SEARCH_ONLY_STANDS_AREAS = "StateMapSearchOnlyStandsAreas";
	private static final String STATE_MAP_SEARCH_OPEN = "StateMapSearchOpen";

	private static final ConventionMap map = Convention.getInstance().getMap();
	private static final String TAG = MapActivity.class.getCanonicalName();

	private static boolean showAnimation = true;
	private ConditionalSwipeVerticalViewPager viewPager;
	private int currentFloorNumber = ConventionMap.FLOOR_NOT_FOUND;

	// Search
	private LinearLayout searchContainer;
	private TextView noResultsFound;
	private ListView searchResults;
	private CheckBox showOnlyHallsCheckbox;
	private CheckBox showOnlyStandsAreasCheckbox;
	private SearchView searchView;
	private MapLocationsAdapter locationsSearchResultsAdapter;
	private String searchTerm;
	private boolean showOnlyHalls;
	private boolean showOnlyStandsAreas;
	private boolean isSearchClosing;
	private Menu menu;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentInContentContainer(R.layout.activity_map, false, false);
		setToolbarBackground(ThemeAttributes.getDrawable(this, R.attr.mapToolbarColor));
		setBackground(ThemeAttributes.getDrawable(this, R.attr.mapBackground));

		if (map.getLocations().size() > 0) {
			setupActionButton(ThemeAttributes.getDrawable(this, R.attr.actionButtonIcon), new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					toggleSearch();
				}
			});
		}

		// Read and initialize parameters from bundle
		Bundle bundle = (savedInstanceState != null ? savedInstanceState : getIntent().getExtras());

		int[] initialLocationIds = (bundle == null ? null : bundle.getIntArray(EXTRA_MAP_LOCATION_IDS));
		List<MapLocation> initialLocations = new LinkedList<>();
		if (initialLocationIds != null) {
			for (int initialLocationId : initialLocationIds) {
				initialLocations.add(map.findLocationById(initialLocationId));
			}
		}

		int defaultFloorNumber = initialLocations.size() > 0 ? initialLocations.get(0).getFloor().getNumber() : getDefaultFloorNumber();
		int floorNumber = (bundle == null ? defaultFloorNumber : bundle.getInt(EXTRA_FLOOR_NUMBER, defaultFloorNumber));

		// Show animation only if we don't initially show a location
		// (we animate the initial location so it will look weird with the marker drops)
		if (initialLocations.size() > 0) {
			showAnimation = false;
		}
		initializeViewPager();
		setFloorInViewPager(floorNumber, initialLocations);

		initializeSearch(savedInstanceState);

		// Handle edge to edge
		Views.registerApplyInsets(Views.InsetType.NONE, Views.InsetType.PADDING, Views.InsetType.NONE, Views.InsetType.NONE, false, searchResults);
		Views.registerApplyInsets(Views.InsetType.NONE, Views.InsetType.NONE, Views.InsetType.PADDING, Views.InsetType.NONE, false, findViewById(R.id.map_search_pane));
	}

	@Override
	public boolean onCreateCustomOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_map, menu);
		this.menu = menu;
		updateZoomMenuItem();

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		return handleOptionsItem(item, Map.of(
			R.id.map_floor_zoom_to_fit, () -> {
				FirebaseAnalytics
					.getInstance(this)
					.logEvent("zoom_to_fit_clicked", null);

				closeSearch();
				getCurrentFloorFragment().toggleMapZoom();
			}
		));
	}

	private MapFloorFragment getCurrentFloorFragment() {
		return (MapFloorFragment) viewPager.getAdapter().instantiateItem(viewPager, viewPager.getCurrentItem());
	}

	private void setFloorInViewPager(int floorNumber, List<MapLocation> initialLocations) {
		int floorIndex = ConventionMap.FLOOR_NOT_FOUND;
		if (floorNumber != ConventionMap.FLOOR_NOT_FOUND) {
			floorIndex = map.floorNumberToFloorIndex(floorNumber);
		}
		// If no floor was sent or looked at or the floor was not found, view the first floor
		if (floorIndex == ConventionMap.FLOOR_NOT_FOUND) {
			floorIndex = 0;
		}

		viewPager.setCurrentItem(floorIndexToPagerPosition(floorIndex));
		if (initialLocations.size() > 0) {
			MapFloorFragment currentFragment = getCurrentFloorFragment();
			currentFragment.selectLocations(initialLocations);
		}

		Floor currentFloor = map.getFloors().get(floorIndex);
		updateCurrentFloor(currentFloor);
	}

	private int getDefaultFloorNumber() {
		int defaultFloorNumber = ConventionMap.FLOOR_NOT_FOUND;
		Floor defaultFloor = map.getLastLookedAtFloor();
		if (defaultFloor == null) {
			defaultFloor = map.getDefaultFloor();
		}
		if (defaultFloor != null) {
			defaultFloorNumber = defaultFloor.getNumber();
		}
		return defaultFloorNumber;
	}

	private void initializeViewPager() {
		viewPager = (ConditionalSwipeVerticalViewPager) findViewById(R.id.map_view_pager);

		// Configure the view pager
		viewPager.setAdapter(new MapFloorAdapter(getSupportFragmentManager(), showAnimation));
		viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
			@Override
			public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

			}

			@Override
			public void onPageSelected(int position) {
				Floor floor = pagerPositionToFloor(viewPager.getCurrentItem());
				updateCurrentFloor(floor);
			}

			@Override
			public void onPageScrollStateChanged(int state) {
			}
		});

		viewPager.setCondition(new ConditionalSwipeVerticalViewPager.Condition() {
			@Override
			public boolean shouldSwipe() {
				return getCurrentFloorFragment().canSwipeToChangeFloor();
			}
		});
		showAnimation = false; // Don't show animation next time this activity is created in this session
	}

	private void updateCurrentFloor(Floor floor) {
		setToolbarTitle(floor.getName());
		currentFloorNumber = floor.getNumber();
		map.setLastLookedAtFloor(floor);
		updateZoomMenuItem();

		// Update action button location after everything has been rendered
		viewPager.post(new Runnable() {
			@Override
			public void run() {
				updateActionButtonLocation();
			}
		});
	}

	@Override
	public void onUpArrowClicked() {
		viewPager.setCurrentItem(viewPager.getCurrentItem() - 1, true);
	}

	@Override
	public void onDownArrowClicked() {
		viewPager.setCurrentItem(viewPager.getCurrentItem() + 1, true);
	}

	private void updateZoomMenuItem() {
		if (menu == null) {
			return;
		}
		MenuItem zoomItem = menu.findItem(R.id.map_floor_zoom_to_fit);
		if (zoomItem == null) {
			return;
		}
		MapFloorFragment currentFloorFragment = getCurrentFloorFragment();
		if (currentFloorFragment == null) {
			return;
		}

		if (currentFloorFragment.isMapZoomedIn()) {
			zoomItem.setTitle(getString(R.string.shrink_map));
			zoomItem.setIcon(ThemeAttributes.getDrawable(this, R.attr.iconShrink));
		} else {
			zoomItem.setTitle(getString(R.string.enlarge_map));
			zoomItem.setIcon(ThemeAttributes.getDrawable(this, R.attr.iconEnlarge));
		}
	}

	@Override
	public void onZoomChanged() {
		updateZoomMenuItem();
	}

	private class MapFloorAdapter extends FragmentStatePagerAdapter {
		private boolean showAnimation;

		public MapFloorAdapter(FragmentManager fm, boolean showAnimation) {
			super(fm);
			this.showAnimation = showAnimation;
		}

		@Override
		public Fragment getItem(int position) {
			MapFloorFragment mapFloorFragment = MapFloorFragment.newInstance(pagerPositionToFloor(position).getNumber(), showAnimation);
			showAnimation = false; // The animation should only be displayed once
			return mapFloorFragment;
		}

		@Override
		public int getCount() {
			return map.getFloors().size();
		}
	}

	private int floorIndexToPagerPosition(int index) {
		// View pager positions are opposite of the floor numbers because the first
		// position is the top while floors start at the bottom
		return map.getFloors().size() - 1 - index;
	}

	private Floor pagerPositionToFloor(int position) {
		// View pager positions are opposite of the floor numbers because the first
		// position is the top while floors start at the bottom
		return map.getFloors().get(map.getFloors().size() - 1 - position);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt(EXTRA_FLOOR_NUMBER, currentFloorNumber);
		outState.putString(STATE_SEARCH_TERM, searchView.getQuery().toString());
		outState.putBoolean(STATE_MAP_SEARCH_ONLY_HALLS, showOnlyHallsCheckbox.isChecked());
		outState.putBoolean(STATE_MAP_SEARCH_ONLY_STANDS_AREAS, showOnlyStandsAreasCheckbox.isChecked());
		outState.putBoolean(STATE_MAP_SEARCH_OPEN, isSearchOpen());
	}

	private void initializeSearch(Bundle savedInstanceState) {
		searchContainer = (LinearLayout) findViewById(R.id.map_search);
		noResultsFound = (TextView) findViewById(R.id.map_search_no_results_found);
		searchResults = (ListView) findViewById(R.id.map_search_results);
		showOnlyHallsCheckbox = (CheckBox) findViewById(R.id.map_search_show_only_halls);
		showOnlyStandsAreasCheckbox = (CheckBox) findViewById(R.id.map_search_show_only_stands_areas);
		searchView = findViewById(R.id.map_search_text);

		isSearchClosing = false;

		// Restore state or use defaults
		searchTerm = (savedInstanceState != null ? savedInstanceState.getString(STATE_SEARCH_TERM) : null);
		showOnlyHalls = (savedInstanceState != null && savedInstanceState.getBoolean(STATE_MAP_SEARCH_ONLY_HALLS));
		showOnlyStandsAreas = (savedInstanceState != null && savedInstanceState.getBoolean(STATE_MAP_SEARCH_ONLY_STANDS_AREAS));
		boolean showSearch = (savedInstanceState != null && savedInstanceState.getBoolean(STATE_MAP_SEARCH_OPEN));
		searchContainer.setVisibility(showSearch ? View.VISIBLE : View.GONE);

		Views.hideKeyboardOnClickOutsideEditText(this, searchContainer);

		// Clicking anywhere outside the visible search pane should close it
		searchContainer.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				closeSearch();
			}
		});

		// Make sure clicks inside the pane don't close it
		findViewById(R.id.map_search_pane).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
			}
		});

		Drawable mapSearchTopBorderColor = ThemeAttributes.getDrawable(this, R.attr.mapSearchTopBorder);
		if (mapSearchTopBorderColor == null) {
			findViewById(R.id.map_search_top_border).setVisibility(View.GONE);
		}

		// Setup locations search results list
		locationsSearchResultsAdapter = new MapLocationsAdapter(Collections.<MapLocation>emptyList());

		if (showSearch) {
			locationsSearchResultsAdapter.setFloor(map.getLastLookedAtFloor());
		}
		searchResults.setAdapter(locationsSearchResultsAdapter);

		searchResults.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				closeSearch();

				MapLocation location = (MapLocation) locationsSearchResultsAdapter.getItem(position);
				// Go to the selected location's floor and reset its zoom/selection state
				if (!Objects.equals(getCurrentFloorFragment().getFloor(), location.getFloor())) {
					setCurrentFloor(location.getFloor());
					getCurrentFloorFragment().resetState();
				}
				// Set selected marker
				getCurrentFloorFragment().selectMarkersWithNameAndFloor(Collections.singletonList(location));
			}
		});

		// Setup search view
		searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
			@Override
			public boolean onQueryTextSubmit(String query) {
				searchTerm = query;
				// Only apply the filters if the user is currently searching
				// (otherwise it might happen when restoring the saved state)
				if (isSearchOpen()) {
					applySearchFiltersInBackground();
				}
				return false; // Close the search bar if full screen
			}

			@Override
			public boolean onQueryTextChange(String newText) {
				searchTerm = newText;
				// Only apply the filters if the user is currently searching
				// (otherwise it might happen when restoring the saved state)
				if (isSearchOpen()) {
					applySearchFiltersInBackground();
				}
				return true;
			}
		});
		if (searchTerm != null) {
			searchView.setQuery(searchTerm, true);
		}

		// Setup "show only halls" checkbox
		showOnlyHallsCheckbox.setChecked(showOnlyHalls);
		showOnlyHallsCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				showOnlyHalls = isChecked;
				// Only apply the filters if the user is currently searching
				// (otherwise it might happen when restoring the saved state)
				if (isSearchOpen()) {
					applySearchFiltersInBackground();
				}
			}
		});

		// Setup "show only stands areas" checkbox
		showOnlyStandsAreasCheckbox.setChecked(showOnlyStandsAreas);
		showOnlyStandsAreasCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				showOnlyStandsAreas = isChecked;
				// Only apply the filters if the user is currently searching
				// (otherwise it might happen when restoring the saved state)
				if (isSearchOpen()) {
					applySearchFiltersInBackground();
				}
			}
		});
	}

	private void selectStand(Stand stand, int delay) {
		StandsArea standsArea = stand.getStandsArea();
		List<MapLocation> locations = map.findLocationsByStandsAreaId(standsArea.getId());
		if (locations.size() >= 1) {
			MapLocation location = locations.get(0);
			// Go to selected stand's stand area floor and reset its zoom/selection state
			if (!Objects.equals(getCurrentFloorFragment().getFloor(), location.getFloor())) {
				setCurrentFloor(location.getFloor());
				getCurrentFloorFragment().resetState();
			}
			// Set selected marker
			getCurrentFloorFragment().selectStandByLocation(location, stand, delay);
		}
	}

	private void setCurrentFloor(Floor floor) {
		viewPager.setCurrentItem(floorIndexToPagerPosition(map.floorNumberToFloorIndex(floor.getNumber())));
	}

	@Override
	public void onShowFloorClicked(Floor floor) {
		setCurrentFloor(floor);
	}

	@Override
	public void onShowDetailsActivityClicked(DetailsActivityLocation detailsActivityLocation) {
		navigateToActivity(detailsActivityLocation.getActivityClass(), false, detailsActivityLocation.getBundle());
	}

	private void applySearchFiltersInBackground() {
		final String searchTerm = this.searchTerm;
		final boolean showOnlyHalls = this.showOnlyHalls;
		final boolean showOnlyStandsAreas = this.showOnlyStandsAreas;
		final Floor floor = getCurrentFloorFragment().getFloor();

		new AsyncTask<Void, Void, List<MapLocation>>() {
			@Override
			protected List<MapLocation> doInBackground(Void... params) {
				List<MapLocation> locations = map.getLocations();
				locations = CollectionUtils.filter(locations, item ->
					(searchTerm == null || searchTerm.isEmpty() || item.getName().toLowerCase().contains(searchTerm.toLowerCase())) &&
						((!showOnlyHalls) || item.areAnyPlacesHalls()) &&
						((!showOnlyStandsAreas) || item.areaAnyPlacesStandsAreas())
				);
				Collections.sort(locations, new Comparator<MapLocation>() {
					@Override
					public int compare(MapLocation lhs, MapLocation rhs) {
						// Sort order - floor (current floor is first), is hall (halls are first), is stands area, name
						if (!Objects.equals(lhs.getFloor(), rhs.getFloor())) {
							if (Objects.equals(lhs.getFloor(), floor)) {
								return -1;
							} else if (Objects.equals(rhs.getFloor(), floor)) {
								return 1;
							} else {
								return lhs.getFloor().getNumber() - rhs.getFloor().getNumber();
							}
						} else if (lhs.areAnyPlacesHalls() != rhs.areAnyPlacesHalls()) {
							if (lhs.areAnyPlacesHalls()) {
								return -1;
							} else {
								return 1;
							}
						} else if (lhs.areaAnyPlacesStandsAreas() != rhs.areaAnyPlacesStandsAreas()) {
							if (lhs.areaAnyPlacesStandsAreas()) {
								return -1;
							} else {
								return 1;
							}
						} else {
							return lhs.getName().compareTo(rhs.getName());
						}
					}
				});
				locations = CollectionUtils.unique(locations, new MapLocationSearchEquality());
				return locations;
			}

			@Override
			protected void onPostExecute(List<MapLocation> searchResult) {
				locationsSearchResultsAdapter.setMapLocations(searchResult);
				locationsSearchResultsAdapter.notifyDataSetChanged();

				// Show the "no results found" message if there are no results after applying the filters
				if (locationsSearchResultsAdapter.getCount() == 0) {
					noResultsFound.setVisibility(View.VISIBLE);
					searchResults.setVisibility(View.GONE);
				} else {
					noResultsFound.setVisibility(View.GONE);
					searchResults.setVisibility(View.VISIBLE);
				}

				// Select markers (only if a search term was entered or one of the checkboxes selected)
				getCurrentFloorFragment().selectMarkersWithNameAndFloor(
					(searchTerm == null || searchTerm.isEmpty()) && !showOnlyHalls && !showOnlyStandsAreas ?
						null : locationsSearchResultsAdapter.getMapLocations());
			}
		}.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	}

	private void toggleSearch() {
		if (isSearchOpen()) {
			closeSearch();
		} else {
			FirebaseAnalytics
					.getInstance(this)
					.logEvent("map_search_opened", null);

			openSearch();
		}
	}

	private boolean isSearchOpen() {
		return searchContainer.getVisibility() == View.VISIBLE;
	}

	private void openSearch() {
		getCurrentFloorFragment().resetState();
		isSearchClosing = false;
		locationsSearchResultsAdapter.setFloor(getCurrentFloorFragment().getFloor());
		searchContainer.setVisibility(View.VISIBLE);
		searchContainer.startAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_in_from_right));
		applySearchFiltersInBackground();
		hideActionButton(null);
	}

	private void closeSearch() {
		if (searchContainer.getVisibility() != View.VISIBLE || isSearchClosing) {
			return;
		}
		isSearchClosing = true;
		Animation animation = AnimationUtils.loadAnimation(this, R.anim.slide_out_to_right);
		searchContainer.startAnimation(animation);
		animation.setAnimationListener(new Animation.AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				searchContainer.setVisibility(View.GONE);
				isSearchClosing = false;
				showActionButton(null);
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}
		});
	}

	@Override
	public void onLocationDetailsTopChanged(int top, MapFloorFragment floorFragment) {
		if (floorFragment != null && floorFragment != getCurrentFloorFragment()) {
			return;
		}
		int parentHeight = viewPager.getMeasuredHeight();
		// The action button might be null if there are no locations
		FloatingActionButton actionButton = getActionButton();
		if (actionButton != null) {
			int actionButtonHeight = actionButton.getMeasuredHeight();
			if (top > parentHeight - actionButtonHeight) {
				top = parentHeight - actionButtonHeight;
			}
			// In case there are inset margins, both the FAB and the location details have the same
			// margin/padding from the bottom, so we need to remove it from the result
			if (top > 0 && actionButton.getTag(R.id.inset_margins) instanceof Rect) {
				top -= ((Rect) actionButton.getTag(R.id.inset_margins)).bottom;
				// The location details go below their padding, we don't want the FAB to follow too low
				if (top < 0) {
					top = 0;
				}
			}

			actionButton.setTranslationY(-top);
		}
	}

	private void updateActionButtonLocation() {
		MapFloorFragment currentFloorFragment = getCurrentFloorFragment();
		if (currentFloorFragment == null) {
			return;
		}
		int baseHeight = currentFloorFragment.getMapHiddenPortionHeight();
		onLocationDetailsTopChanged(baseHeight, null);
	}
}

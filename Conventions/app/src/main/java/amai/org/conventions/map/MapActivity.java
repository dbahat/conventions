package amai.org.conventions.map;

import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import amai.org.conventions.ConventionsApplication;
import sff.org.conventions.R;
import amai.org.conventions.ThemeAttributes;
import amai.org.conventions.customviews.ConditionalSwipeVerticalViewPager;
import amai.org.conventions.model.ConventionMap;
import amai.org.conventions.model.Floor;
import amai.org.conventions.model.MapLocation;
import amai.org.conventions.model.Stand;
import amai.org.conventions.model.StandsArea;
import amai.org.conventions.model.conventions.Convention;
import amai.org.conventions.navigation.NavigationActivity;
import amai.org.conventions.utils.CollectionUtils;
import amai.org.conventions.utils.Objects;
import amai.org.conventions.utils.Views;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

public class MapActivity extends NavigationActivity implements MapFloorFragment.OnMapFloorEventListener {
	public static final String EXTRA_FLOOR_NUMBER = "ExtraFloorNumber";
	public static final String EXTRA_MAP_LOCATION_IDS = "ExtraMapLocationId";

	private static final String STATE_SEARCH_TERM = "StateMapSearchTerm";
	private static final String STATE_MAP_SEARCH_ONLY_HALLS = "StateMapSearchOnlyHalls";
	private static final String STATE_MAP_SEARCH_OPEN = "StateMapSearchOpen";

	private static final ConventionMap map = Convention.getInstance().getMap();
	private static final String TAG = MapActivity.class.getCanonicalName();

	private static boolean showAnimation = true;
	private ConditionalSwipeVerticalViewPager viewPager;
	private int currentFloorNumber = ConventionMap.FLOOR_NOT_FOUND;

	// Search
	private LinearLayout searchContainer;
	private RadioButton searchTypeLocations;
	private TextView noResultsFound;
	private ListView searchResults;
	private CheckBox showOnlyHallsCheckbox;
	private EditText searchText;
	private MapLocationsAdapter locationsSearchResultsAdapter;
	private StandsAdapter standsSearchResultsAdapter;
	private String searchTerm;
	private boolean showOnlyHalls;
	private boolean isSearchClosing;
	private Menu menu;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentInContentContainer(R.layout.activity_map, false, false);
		setToolbarAndContentContainerBackground(ThemeAttributes.getDrawable(this, R.attr.mapBackground));
		setToolbarBackground(ThemeAttributes.getDrawable(this, R.attr.mapToolbarColor));
		if (map.getLocations().size() > 0) {
			setupActionButton(R.drawable.ic_action_search, new View.OnClickListener() {
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
		switch (item.getItemId()) {
			case R.id.map_floor_zoom_to_fit:
				ConventionsApplication.sendTrackingEvent(new HitBuilders.EventBuilder()
						.setCategory("Map")
						.setAction("ZoomToFitClicked")
						.build());
				closeSearch();
				getCurrentFloorFragment().toggleMapZoom();
				return true;
		}
		return super.onOptionsItemSelected(item);
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
		outState.putString(STATE_SEARCH_TERM, searchText.getText().toString());
		outState.putBoolean(STATE_MAP_SEARCH_ONLY_HALLS, showOnlyHallsCheckbox.isChecked());
		outState.putBoolean(STATE_MAP_SEARCH_OPEN, isSearchOpen());
	}

	private void initializeSearch(Bundle savedInstanceState) {
		RadioGroup searchType = (RadioGroup) findViewById(R.id.search_type);
		searchContainer = (LinearLayout) findViewById(R.id.map_search);
		searchTypeLocations = (RadioButton) findViewById(R.id.search_type_locations);
		noResultsFound = (TextView) findViewById(R.id.map_search_no_results_found);
		searchResults = (ListView) findViewById(R.id.map_search_results);
		showOnlyHallsCheckbox = (CheckBox) findViewById(R.id.map_search_show_only_halls);
		searchText = (EditText) findViewById(R.id.map_search_text);

		isSearchClosing = false;

		// Restore state or use defaults
		searchTerm = (savedInstanceState != null ? savedInstanceState.getString(STATE_SEARCH_TERM) : null);
		showOnlyHalls = (savedInstanceState != null && savedInstanceState.getBoolean(STATE_MAP_SEARCH_ONLY_HALLS));
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

		// Check if we can search for stands
		if (!Convention.getInstance().hasStands()) {
			searchType.setVisibility(View.GONE);
		}

		// Setup search type (radio button) change
		searchType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				getCurrentFloorFragment().resetState();
				if (checkedId == R.id.search_type_locations) {
					showOnlyHallsCheckbox.setVisibility(View.VISIBLE);
					searchResults.setAdapter(locationsSearchResultsAdapter);
				} else {
					showOnlyHallsCheckbox.setVisibility(View.GONE);
					searchResults.setAdapter(standsSearchResultsAdapter);
				}
				applySearchFiltersInBackground();
			}
		});

		// Setup locations and stands search results list
		locationsSearchResultsAdapter = new MapLocationsAdapter(Collections.<MapLocation>emptyList());
		if (showSearch) {
			locationsSearchResultsAdapter.setFloor(map.getLastLookedAtFloor());
		}

		standsSearchResultsAdapter = new StandsAdapter(Collections.<Stand>emptyList(), false, false, null);
		searchResults.setAdapter(isLocationsSearch() ? locationsSearchResultsAdapter : standsSearchResultsAdapter);

		searchResults.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				closeSearch();

				if (isLocationsSearch()) {
					MapLocation location = (MapLocation) locationsSearchResultsAdapter.getItem(position);
					// Go to the selected location's floor and reset its zoom/selection state
					if (!Objects.equals(getCurrentFloorFragment().getFloor(), location.getFloor())) {
						setCurrentFloor(location.getFloor());
						getCurrentFloorFragment().resetState();
					}
					// Set selected marker
					getCurrentFloorFragment().selectMarkersWithNameAndFloor(Collections.singletonList(location));
				} else {
					Stand stand = (Stand) standsSearchResultsAdapter.getItem(position);
					StandsArea standsArea = stand.getStandsArea();
					List<MapLocation> locations = map.findLocationsByStandsArea(standsArea);
					if (locations.size() >= 1) {
						MapLocation location = locations.get(0);
						// Go to selected stand's stand area floor and reset its zoom/selection state
						if (!Objects.equals(getCurrentFloorFragment().getFloor(), location.getFloor())) {
							setCurrentFloor(location.getFloor());
							getCurrentFloorFragment().resetState();
						}
						// Set selected marker
						getCurrentFloorFragment().selectStandByLocation(location, stand);
					}
				}
			}
		});

		// Setup search text filter
		if (searchTerm != null) {
			searchText.setText(searchTerm);
		}
		searchText.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				searchTerm = s.toString();
				// Only apply the filters if the user is currently searching
				// (otherwise it might happen when restoring the saved state)
				if (isSearchOpen()) {
					applySearchFiltersInBackground();
				}
			}
		});

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
	}

	private boolean isLocationsSearch() {
		return searchTypeLocations.isChecked();
	}

	private void setCurrentFloor(Floor floor) {
		viewPager.setCurrentItem(floorIndexToPagerPosition(map.floorNumberToFloorIndex(floor.getNumber())));
	}

	@Override
	public void onShowFloorClicked(Floor floor) {
		setCurrentFloor(floor);
	}

	private void applySearchFiltersInBackground() {
		final String searchTerm = this.searchTerm;
		final boolean showOnlyHalls = this.showOnlyHalls;
		final Floor floor = getCurrentFloorFragment().getFloor();

		new AsyncTask<Void, Void, List<?>>() {
			@Override
			protected List<?> doInBackground(Void... params) {
				if (isLocationsSearch()) {
					List<MapLocation> locations = map.getLocations();
					locations = CollectionUtils.filter(locations, new CollectionUtils.Predicate<MapLocation>() {
						@Override
						public boolean where(MapLocation item) {
							return (searchTerm == null || searchTerm.isEmpty() || item.getName().toLowerCase().contains(searchTerm.toLowerCase())) &&
									((!showOnlyHalls) || item.areAllPlacesHalls());
						}
					});
					Collections.sort(locations, new Comparator<MapLocation>() {
						@Override
						public int compare(MapLocation lhs, MapLocation rhs) {
							// Sort order - floor (current floor is first), is hall (halls are first), name
							if (!Objects.equals(lhs.getFloor(), rhs.getFloor())) {
								if (Objects.equals(lhs.getFloor(), floor)) {
									return -1;
								} else if (Objects.equals(rhs.getFloor(), floor)) {
									return 1;
								} else {
									return lhs.getFloor().getNumber() - rhs.getFloor().getNumber();
								}
							} else if (lhs.areAllPlacesHalls() != rhs.areAllPlacesHalls()) {
								if (lhs.areAllPlacesHalls()) {
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
				} else {
					List<Stand> stands = Convention.getInstance().getStands();
					stands = CollectionUtils.filter(stands, new CollectionUtils.Predicate<Stand>() {
						@Override
						public boolean where(Stand item) {
							return searchTerm == null || searchTerm.isEmpty() || item.getName().toLowerCase().contains(searchTerm.toLowerCase());
						}
					});
					Collections.sort(stands, new Comparator<Stand>() {
						@Override
						public int compare(Stand lhs, Stand rhs) {
							return lhs.getName().compareTo(rhs.getName());
						}
					});
					return stands;
				}
			}

			@Override
			protected void onPostExecute(List<?> searchResult) {
				boolean locationsSearch = isLocationsSearch();
				if (locationsSearch) {
					//noinspection unchecked
					locationsSearchResultsAdapter.setMapLocations((List<MapLocation>) searchResult);
					locationsSearchResultsAdapter.notifyDataSetChanged();
				} else {
					//noinspection unchecked
					standsSearchResultsAdapter.setStands((List<Stand>) searchResult);
					standsSearchResultsAdapter.notifyDataSetChanged();
				}

				// Show the "no results found" message if there are no results after applying the filters
				if ((locationsSearch && locationsSearchResultsAdapter.getCount() == 0) ||
						(!locationsSearch && standsSearchResultsAdapter.getCount() == 0)) {
					noResultsFound.setVisibility(View.VISIBLE);
					searchResults.setVisibility(View.GONE);
				} else {
					noResultsFound.setVisibility(View.GONE);
					searchResults.setVisibility(View.VISIBLE);
				}

				// Select markers (only if a search term was entered or the halls checkbox selected - and only for locations search)
				if (locationsSearch) {
					getCurrentFloorFragment().selectMarkersWithNameAndFloor(
							(searchTerm == null || searchTerm.isEmpty()) && !showOnlyHalls ?
									null : locationsSearchResultsAdapter.getMapLocations());
				}
			}
		}.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	}

	private void toggleSearch() {
		if (isSearchOpen()) {
			closeSearch();
		} else {
			ConventionsApplication.sendTrackingEvent(new HitBuilders.EventBuilder()
					.setCategory("Search")
					.setAction("MapSearchOpened")
					.build());
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

package amai.org.conventions.map;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
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
import android.widget.TextView;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import amai.org.conventions.R;
import amai.org.conventions.model.Convention;
import amai.org.conventions.model.ConventionMap;
import amai.org.conventions.model.Floor;
import amai.org.conventions.model.Hall;
import amai.org.conventions.model.MapLocation;
import amai.org.conventions.navigation.NavigationActivity;
import amai.org.conventions.utils.CollectionUtils;
import amai.org.conventions.utils.Objects;
import amai.org.conventions.utils.Views;
import fr.castorflex.android.verticalviewpager.VerticalViewPager;

public class MapActivity extends NavigationActivity implements MapFloorFragment.OnMapFloorEventListener {

    public static final String EXTRA_FLOOR_NUMBER = "ExtraFloorNumber";
	public static final String EXTRA_MAP_LOCATION_ID = "ExtraMapLocationId";

	private static final String STATE_SEARCH_TERM = "StateMapSearchTerm";
	private static final String STATE_MAP_SEARCH_ONLY_HALLS = "ExtraMapSearchOnlyHalls";

    private static final ConventionMap map = Convention.getInstance().getMap();

    private VerticalViewPager viewPager;
	private int currentFloorNumber;

	// Search
	private LinearLayout searchContainer;
	private TextView noResultsFound;
	private ListView searchResults;
	private CheckBox showOnlyHallsCheckbox;
	private EditText searchText;
	private MapLocationsAdapter searchResultsAdapter;
	private String searchTerm;
	private boolean showOnlyHalls;
	private boolean isSearchClosing;
	private Menu menu;

	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentInContentContainer(R.layout.activity_map, false);

	    // Read and initialize parameters from bundle
	    Bundle bundle = (savedInstanceState != null ? savedInstanceState : getIntent().getExtras());

	    int initialLocationId = (bundle == null ? -1 : bundle.getInt(EXTRA_MAP_LOCATION_ID, -1));
	    MapLocation initialLocation = null;
	    if (initialLocationId != -1) {
		    initialLocation = map.findLocationById(initialLocationId);
	    }

	    int defaultFloorNumber = initialLocation != null ? initialLocation.getFloor().getNumber() : getDefaultFloorNumber();
	    int floorNumber = (bundle == null ? defaultFloorNumber : bundle.getInt(EXTRA_FLOOR_NUMBER, defaultFloorNumber));

        initializeViewPager();
	    setFloorInViewPager(floorNumber, initialLocation);

		initializeSearch(savedInstanceState);
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.menu_map, menu);
		this.menu = menu;
		updateZoomMenuItem();

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.map_floor_zoom_to_fit:
				closeSearch();
				getCurrentFloorFragment().toggleMapZoom();
				return true;
			case R.id.map_floor_search:
				toggleSearch();
				return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public MapFloorFragment getCurrentFloorFragment() {
		return (MapFloorFragment) viewPager.getAdapter().instantiateItem(viewPager, viewPager.getCurrentItem());
	}

	private void setFloorInViewPager(int floorNumber, MapLocation initialLocation) {
		int floorIndex = ConventionMap.FLOOR_NOT_FOUND;
		if (floorNumber != ConventionMap.FLOOR_NOT_FOUND) {
		    floorIndex = map.floorNumberToFloorIndex(floorNumber);
		}
		// If no floor was sent or looked at or the floor was not found, view the first floor
		if (floorIndex == ConventionMap.FLOOR_NOT_FOUND) {
			floorIndex = 0;
		}

		viewPager.setCurrentItem(floorIndexToPagerPosition(floorIndex));
		if (initialLocation != null) {
			MapFloorFragment currentFragment = getCurrentFloorFragment();
			currentFragment.selectLocation(initialLocation);
		}

		Floor currentFloor = map.getFloors().get(floorIndex);
		updateCurrentFloor(currentFloor);
	}

	private int getDefaultFloorNumber() {
		int defaultFloor = ConventionMap.FLOOR_NOT_FOUND;
		Floor lastFloor = map.getLastLookedAtFloor();
		if (lastFloor != null) {
			defaultFloor = lastFloor.getNumber();
		}
		return defaultFloor;
	}

	private void initializeViewPager() {
        viewPager = (VerticalViewPager) findViewById(R.id.map_view_pager);

        // Configure the view pager
        viewPager.setAdapter(new MapFloorAdapter(getSupportFragmentManager()));
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

        // Hold all the fragments in memory for best transition performance
        viewPager.setOffscreenPageLimit(viewPager.getAdapter().getCount());
    }

	private void updateCurrentFloor(Floor floor) {
		setToolbarTitle(floor.getName());
		currentFloorNumber = floor.getNumber();
		map.setLastLookedAtFloor(floor);
		updateZoomMenuItem();
	}

	@Override
    public void onUpArrowClicked() {
	    viewPager.setCurrentItem(viewPager.getCurrentItem() - 1);
    }

	@Override
    public void onDownArrowClicked() {
		viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
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
			zoomItem.setIcon(R.drawable.shrink);
		} else {
			zoomItem.setTitle(getString(R.string.enlarge_map));
			zoomItem.setIcon(R.drawable.enlarge);
		}
	}

	@Override
	public void onZoomChanged() {
		updateZoomMenuItem();
	}

	private class MapFloorAdapter extends FragmentStatePagerAdapter {
        public MapFloorAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
	        return MapFloorFragment.newInstance(pagerPositionToFloor(position).getNumber());
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
	}

	private void initializeSearch(Bundle savedInstanceState) {
		searchContainer = (LinearLayout) findViewById(R.id.map_search);
		noResultsFound = (TextView) findViewById(R.id.map_search_no_results_found);
		searchResults = (ListView) findViewById(R.id.map_search_results);
		showOnlyHallsCheckbox = (CheckBox) findViewById(R.id.map_search_show_only_halls);
		searchText = (EditText) findViewById(R.id.map_search_text);

		isSearchClosing = false;

		// Restore state or use defaults
		searchTerm = (savedInstanceState != null ? savedInstanceState.getString(STATE_SEARCH_TERM) : null);
		showOnlyHalls = (savedInstanceState != null ? savedInstanceState.getBoolean(STATE_MAP_SEARCH_ONLY_HALLS) : false);

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

		// Setup locations search results list
		searchResultsAdapter = new MapLocationsAdapter(Collections.<MapLocation>emptyList());
		searchResults.setAdapter(searchResultsAdapter);

		searchResults.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				closeSearch();
				MapLocation location = (MapLocation) searchResultsAdapter.getItem(position);
				// Go to the selected location's floor and reset its zoom/selection state
				if (!Objects.equals(getCurrentFloorFragment().getFloor(), location.getFloor())) {
					setCurrentFloor(location.getFloor());
					getCurrentFloorFragment().resetState();
				}
				// Set selected marker
				getCurrentFloorFragment().selectMarkersWithNameAndFloor(Collections.singletonList(location));

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

	private void setCurrentFloor(Floor floor) {
		viewPager.setCurrentItem(floorIndexToPagerPosition(map.floorNumberToFloorIndex(floor.getNumber())));
	}

	private void applySearchFiltersInBackground() {
		final String searchTerm = this.searchTerm;
		final boolean showOnlyHalls = this.showOnlyHalls;
		final Floor floor = getCurrentFloorFragment().getFloor();

		new AsyncTask<Void, Void, Void>() {
			@Override
			protected Void doInBackground(Void... params) {
				List<MapLocation> locations = Convention.getInstance().getMap().getLocations();
				locations = CollectionUtils.filter(locations, new CollectionUtils.Predicate<MapLocation>() {
					@Override
					public boolean where(MapLocation item) {
						return (searchTerm == null || searchTerm.isEmpty() || item.getName().contains(searchTerm)) &&
								((!showOnlyHalls) || item.getPlace() instanceof Hall);
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
						} else if ((lhs.getPlace() instanceof Hall) != (rhs.getPlace() instanceof Hall)) {
							if (lhs.getPlace() instanceof Hall) {
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
				searchResultsAdapter.setMapLocations(locations);
				return null;
			}

			@Override
			protected void onPostExecute(Void aVoid) {
				searchResultsAdapter.notifyDataSetChanged();

				// Show the "no results found" message if there are no results after applying the filters
				if (searchResultsAdapter.getCount() == 0) {
					noResultsFound.setVisibility(View.VISIBLE);
					searchResults.setVisibility(View.GONE);
				} else {
					noResultsFound.setVisibility(View.GONE);
					searchResults.setVisibility(View.VISIBLE);
				}

				// Select markers (only if a search term was entered or the halls checkbox selected)
				getCurrentFloorFragment().selectMarkersWithNameAndFloor(
						(searchTerm == null || searchTerm.isEmpty()) && !showOnlyHalls ?
								null : searchResultsAdapter.getMapLocations());
			}
		}.execute();
	}

	public void toggleSearch() {
		if (isSearchOpen()) {
			closeSearch();
		} else {
			openSearch();
		}
	}

	private boolean isSearchOpen() {
		return searchContainer.getVisibility() == View.VISIBLE;
	}

	public void openSearch() {
		getCurrentFloorFragment().resetState();
		isSearchClosing = false;
		searchResultsAdapter.setFloor(getCurrentFloorFragment().getFloor());
		searchContainer.setVisibility(View.VISIBLE);
		searchContainer.startAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_in_from_right));
		applySearchFiltersInBackground();
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
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}
		});
	}
}

package amai.org.conventions.map;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;

import amai.org.conventions.R;
import amai.org.conventions.model.Convention;
import amai.org.conventions.model.ConventionMap;
import amai.org.conventions.model.Floor;
import amai.org.conventions.model.MapLocation;
import amai.org.conventions.navigation.NavigationActivity;
import fr.castorflex.android.verticalviewpager.VerticalViewPager;

public class MapActivity extends NavigationActivity implements MapFloorFragment.OnMapArrowClickedListener {

    public static final String EXTRA_FLOOR_NUMBER = "ExtraFloorNumber";
	public static final String EXTRA_MAP_LOCATION_ID = "ExtraMapLocationId";

    private static final ConventionMap map = Convention.getInstance().getMap();

    private VerticalViewPager viewPager;
	private int currentFloorNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentInContentContainer(R.layout.activity_map);

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
			MapFloorFragment currentFragment = (MapFloorFragment) viewPager.getAdapter().instantiateItem(viewPager, viewPager.getCurrentItem());
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
	}

	@Override
    public void onUpArrowClicked() {
	    viewPager.setCurrentItem(viewPager.getCurrentItem() - 1);
    }

	@Override
    public void onDownArrowClicked() {
		viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
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
	}
}

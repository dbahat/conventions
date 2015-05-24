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
import amai.org.conventions.navigation.NavigationActivity;
import fr.castorflex.android.verticalviewpager.VerticalViewPager;

public class MapActivity extends NavigationActivity implements MapFloorFragment.OnMapArrowClickedListener {

    public static final String EXTRA_FLOOR_NUMBER = "ExtraFloorNumber";

    private static final ConventionMap map = Convention.getInstance().getMap();
	private static final int NO_FLOOR = -1;

    private VerticalViewPager viewPager;
	private int currentFloorNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentInContentContainer(R.layout.activity_map);

        initializeViewPager();

	    Bundle bundle = (savedInstanceState != null ? savedInstanceState : getIntent().getExtras());
	    int defaultFloorNumber = getDefaultFloorNumber();
	    int floorNumber = (bundle == null ? defaultFloorNumber : bundle.getInt(EXTRA_FLOOR_NUMBER, defaultFloorNumber));
	    setFloorInViewPager(floorNumber);
    }

	private void setFloorInViewPager(int floorNumber) {
		int floorIndex = NO_FLOOR;
		if (floorNumber != NO_FLOOR) {
		    floorIndex = floorNumberToFloorIndex(floorNumber);
		}
		// If no floor was sent or looked at or the floor was not found, view the first floor
		if (floorIndex == NO_FLOOR) {
			floorIndex = 0;
		}

		viewPager.setCurrentItem(floorIndexToPagerPosition(floorIndex));
		Floor currentFloor = map.getFloors().get(floorIndex);
		updateCurrentFloor(currentFloor);
	}

	private int getDefaultFloorNumber() {
		int defaultFloor = NO_FLOOR;
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

	private int floorNumberToFloorIndex(int floorNumber) {
		boolean found = false;
		int index = 0;
		for (Floor curr : map.getFloors()) {
			if (curr.getNumber() == floorNumber) {
				found = true;
				break;
			}
			++index;
		}
		return found ? index : NO_FLOOR;
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

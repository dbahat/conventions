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

    private static final ConventionMap map = Convention.getInstance().getMap();
    private VerticalViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentInContentContainer(R.layout.activity_map);

        initializeViewPager();
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
		        setToolbarTitle(pagerPositionToFloor(viewPager.getCurrentItem()).getName());
	        }

	        @Override
	        public void onPageScrollStateChanged(int state) {

	        }
        });

        // Hold all the fragments in memory for best transition performance
        viewPager.setOffscreenPageLimit(viewPager.getAdapter().getCount());
        viewPager.setCurrentItem(floorIndexToPagerPosition(0));
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
}

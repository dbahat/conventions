package amai.org.conventions.map;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;

import amai.org.conventions.R;
import amai.org.conventions.navigation.NavigationActivity;
import fr.castorflex.android.verticalviewpager.VerticalViewPager;

public class MapActivity extends NavigationActivity implements MapFloorFragment.OnMapArrowClickedListener {

    private static final int NUMBER_OF_FLOORS = 3;
    private VerticalViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentInContentContainer(R.layout.activity_map);

        initializeViewPager();
    }

    private void initializeViewPager() {
        viewPager = (VerticalViewPager) findViewById(R.id.map_view_pager);

        // Configure the view pager.
        viewPager.setAdapter(new MapFloorAdapter(getSupportFragmentManager()));
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                setToolbarTitle(getResources().getString(R.string.map_floor) + " " + positionToFloorNumber(viewPager.getCurrentItem()));
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        // Hold all the fragments in memory for best transition performance.
        viewPager.setOffscreenPageLimit(viewPager.getAdapter().getCount());
        viewPager.setCurrentItem(viewPager.getAdapter().getCount());
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
            return MapFloorFragment.newInstance(positionToFloorNumber(position));
        }

        @Override
        public int getCount() {
            return NUMBER_OF_FLOORS;
        }
    }

    private int positionToFloorNumber(int position) {
        return NUMBER_OF_FLOORS - position;
    }
}

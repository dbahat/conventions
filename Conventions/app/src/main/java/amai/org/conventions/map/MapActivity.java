package amai.org.conventions.map;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;

import amai.org.conventions.R;
import amai.org.conventions.navigation.NavigationActivity;
import amai.org.conventions.navigation.NavigationToolbar;
import fr.castorflex.android.verticalviewpager.VerticalViewPager;

public class MapActivity extends NavigationActivity implements MapFloorFragment.OnMapArrowClickedListener {

    private VerticalViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        initializeViewPager();
        NavigationToolbar navigationToolbar = (NavigationToolbar) findViewById(R.id.map_toolbar);
        navigationToolbar.initialize();
        navigationToolbar.setNavigationPageSelectedListener(this);
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
                // TODO - Set the toolbar title here
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
            return MapFloorFragment.newInstance(3 - position - 1);
        }

        @Override
        public int getCount() {
            return 3;
        }
    }
}

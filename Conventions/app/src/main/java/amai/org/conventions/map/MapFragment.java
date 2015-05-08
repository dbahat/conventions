package amai.org.conventions.map;


import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import amai.org.conventions.R;
import amai.org.conventions.navigation.NavigationActivity;
import amai.org.conventions.navigation.NavigationAdapter;
import fr.castorflex.android.verticalviewpager.VerticalViewPager;


public class MapFragment extends Fragment {

    private VerticalViewPager viewPager;
    private MapFloorsNavigationPages navigationPages;

    public MapFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        navigationPages = new MapFloorsNavigationPages(activity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        initializeViewPager(view);

        return view;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (isVisibleToUser) {
            // Set the floor title based on the current viewPager position
            int currentPagePosition = viewPager != null ? viewPager.getCurrentItem() : 0;
            NavigationActivity navigationActivity = (NavigationActivity) getActivity();
            navigationActivity.setTitle(navigationPages.getPagesTitle()[currentPagePosition]);
        }
    }

    public void onUpArrowClicked() {
        // The viewPager pages are positioned backwards, meaning the "up" icon should bring us to the below page.
        viewPager.setCurrentItem(viewPager.getCurrentItem() - 1);
    }

    public void onDownArrowClicked() {
        // The viewPager pages are positioned backwards, meaning the "down" icon should bring us to the above page.
        viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
    }

    private void initializeViewPager(View view) {
        viewPager = (VerticalViewPager) view.findViewById(R.id.map_view_pager);

        // Configure the view pager.
        viewPager.setAdapter(new NavigationAdapter(getChildFragmentManager(), navigationPages));

        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                NavigationActivity navigationActivity = (NavigationActivity) getActivity();
                navigationActivity.setTitle(navigationPages.getPagesTitle()[position]);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        // Hold all the fragments in memory for best transition performance.
        viewPager.setOffscreenPageLimit(viewPager.getAdapter().getCount());
        viewPager.setCurrentItem(viewPager.getAdapter().getCount());
    }
}

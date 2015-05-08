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

        return view;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (isVisibleToUser) {
            int currentPagePosition = viewPager != null ? viewPager.getCurrentItem() : 0;
            NavigationActivity navigationActivity = (NavigationActivity) getActivity();
            navigationActivity.setTitle(navigationPages.getPagesTitle()[currentPagePosition]);
        }
    }
}

package amai.org.conventions;


import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import amai.org.conventions.map.MapFloorsNavigationPages;
import amai.org.conventions.navigation.NavigationActivity;
import amai.org.conventions.navigation.NavigationAdapter;


public class MapFragment extends Fragment {

    private ViewPager viewPager;
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
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (isVisibleToUser) {

            // Configure the view pager. Done using Handler.post to ensure this only happens after onCreateView was invoked, and the
            // pager UI was inflated.
            Handler handler = new Handler();
            handler.post(new Runnable() {
                @Override
                public void run() {
                    NavigationActivity navigationActivity = (NavigationActivity) getActivity();
                    viewPager.setAdapter(new NavigationAdapter(getChildFragmentManager(), navigationPages));
                    navigationActivity.configureMiddleSpinner(navigationPages, viewPager);
                }
            });
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        viewPager = (ViewPager) view.findViewById(R.id.map_view_pager);

        return view;
    }
}

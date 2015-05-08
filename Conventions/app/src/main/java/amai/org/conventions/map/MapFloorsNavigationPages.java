package amai.org.conventions.map;

import android.content.Context;
import android.support.v4.app.Fragment;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import amai.org.conventions.R;
import amai.org.conventions.navigation.NavigationPages;

/**
 * The navigation pages for the map floors
 */
public class MapFloorsNavigationPages extends NavigationPages {

    public MapFloorsNavigationPages(Context context) {
        super(context, createPageIdToFragmentMap());
    }

    private static LinkedHashMap<Integer, Fragment> createPageIdToFragmentMap() {
        LinkedHashMap<Integer, Fragment> pageIdToFragmentMapInOrder = new LinkedHashMap<>();

        // Note - the position of the floors is top to bottom to align with the VerticalViewPager scrolling animation (which default to swipe down => page up)
        pageIdToFragmentMapInOrder.put(R.string.map_floor_3, MapFloorFragment.newInstance(2));
        pageIdToFragmentMapInOrder.put(R.string.map_floor_2, MapFloorFragment.newInstance(1));
        pageIdToFragmentMapInOrder.put(R.string.map_floor_1, MapFloorFragment.newInstance(0));

        return pageIdToFragmentMapInOrder;
    }
}

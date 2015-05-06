package amai.org.conventions.navigation;

import android.content.Context;
import android.support.v4.app.Fragment;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import amai.org.conventions.HallFragment;
import amai.org.conventions.MapFragment;
import amai.org.conventions.MyEventsFragment;
import amai.org.conventions.ProgrammeFragment;
import amai.org.conventions.R;

/**
 * Represents the set of navigation pages selectable from the home screen.
 */
public class PrimaryNavigationPages extends NavigationPages {

    public PrimaryNavigationPages(Context context) {
        super(context, createPageIdToFragmentMap());
    }

    private static LinkedHashMap<Integer, Fragment> createPageIdToFragmentMap() {
        LinkedHashMap<Integer, Fragment> pageIdToFragmentMapInOrder = new LinkedHashMap<>();
        pageIdToFragmentMapInOrder.put(R.string.map, new MapFragment());
        pageIdToFragmentMapInOrder.put(R.string.programme, new ProgrammeFragment());
        pageIdToFragmentMapInOrder.put(R.string.updates, new MyEventsFragment());
        pageIdToFragmentMapInOrder.put(R.string.arrivalMethods, new HallFragment());

        return pageIdToFragmentMapInOrder;
    }
}

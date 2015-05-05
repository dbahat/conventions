package amai.org.conventions.navigation;

import android.content.Context;
import android.support.v4.app.Fragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

import amai.org.conventions.HallFragment;
import amai.org.conventions.MapFragment;
import amai.org.conventions.MyEventsFragment;
import amai.org.conventions.ProgrammeFragment;
import amai.org.conventions.R;

/**
 * Container class for accessing data regarding the main navigation pages.
 *
 * Usage - When adding a new navigation page, add it to pageIdToFragmentMapInOrder with it's associated fragment.
 */
public class NavigationPages {
    // Maps the page string resource id to it's associated fragment
    private LinkedHashMap<Integer, Fragment> pageIdToFragmentMapInOrder;

    // The page string resource ids in order
    private List<Integer> pageIdsInOrder;

    private Context context;

    public NavigationPages(Context context) {
        this.context = context;

        pageIdToFragmentMapInOrder = new LinkedHashMap<>();
        pageIdToFragmentMapInOrder.put(R.string.map, new MapFragment());
        pageIdToFragmentMapInOrder.put(R.string.programme, new ProgrammeFragment());
        pageIdToFragmentMapInOrder.put(R.string.updates, new MyEventsFragment());
        pageIdToFragmentMapInOrder.put(R.string.arrivalMethods, new HallFragment());

        pageIdsInOrder = new ArrayList<>(pageIdToFragmentMapInOrder.keySet());
    }

    public Fragment getFragment(int position) {
        if (position > pageIdToFragmentMapInOrder.size() || position < 0) {
            throw new AssertionError("No navigation page for position " + position);
        }

        int navigationPageId = pageIdsInOrder.get(position);
        return pageIdToFragmentMapInOrder.get(navigationPageId);
    }

    public String[] getPagesTitle() {
        List<String> pageTitles = new LinkedList<>();
        for (Integer pageId : pageIdsInOrder) {
            pageTitles.add(context.getResources().getString(pageId));
        }

        return pageTitles.toArray(new String[] {});
    }

    public int getPosition(String pageTitle) {
        List<String> pageTitles = Arrays.asList(getPagesTitle());
        if (!pageTitles.contains(pageTitle)) {
            throw new AssertionError("No navigation page for page title " + pageTitle);
        }
        return pageTitles.indexOf(pageTitle);
    }

    public int getCount() {
        return pageIdToFragmentMapInOrder.size();
    }
}

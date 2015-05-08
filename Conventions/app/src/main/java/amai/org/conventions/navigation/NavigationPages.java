package amai.org.conventions.navigation;

import android.content.Context;
import android.support.v4.app.Fragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * Container class for accessing data regarding the main navigation pages.
 * <p/>
 * Usage - Extend this class, and pass to its constructor the mapping between page string resorce ids and their relevant
 * fragment.
 */
public abstract class NavigationPages {
    private Context context;
    private LinkedHashMap<Integer, Fragment> pageIdToFragmentMapInOrder;
    private ArrayList<Integer> pageIdsInOrder;

    public NavigationPages(Context context, LinkedHashMap<Integer, Fragment> pageIdToFragmentMapInOrder) {
        this.context = context;
        this.pageIdToFragmentMapInOrder = pageIdToFragmentMapInOrder;
        this.pageIdsInOrder = new ArrayList<>(pageIdToFragmentMapInOrder.keySet());
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
            pageTitles.add(getString(pageId));
        }

        return pageTitles.toArray(new String[]{});
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

    protected String getString(int stringResourceId) {
        return context.getResources().getString(stringResourceId);
    }
}

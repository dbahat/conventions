package amai.org.conventions.navigation;

import android.app.Activity;
import android.content.Context;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

import amai.org.conventions.AboutActivity;
import amai.org.conventions.ArrivalMethodsActivity;
import amai.org.conventions.FeedbackActivity;
import amai.org.conventions.events.activities.ProgrammeActivity;
import amai.org.conventions.map.MapActivity;
import amai.org.conventions.settings.SettingsActivity;
import amai.org.conventions.updates.UpdatesActivity;
import sff.org.conventions.R;

/**
 * Container class for accessing data regarding the main navigation pages.
 */
public class NavigationPages {
    private Context context;
    private LinkedHashMap<Integer, Class<? extends Activity>> pageIdToActivityTypeMapInOrder;
    private ArrayList<Integer> pageIdsInOrder;

    public NavigationPages(Context context) {
        this.context = context;

        pageIdToActivityTypeMapInOrder = createPageIdToActivityTypeMap();
        pageIdsInOrder = new ArrayList<>(pageIdToActivityTypeMapInOrder.keySet());
    }

    public Class<? extends Activity> getActivityType(int position) {
        if (position > pageIdToActivityTypeMapInOrder.size() || position < 0) {
            throw new AssertionError("No navigation page for position " + position);
        }

        int navigationPageId = pageIdsInOrder.get(position);
        return pageIdToActivityTypeMapInOrder.get(navigationPageId);
    }

    public String[] getPagesTitle() {
        List<String> pageTitles = new LinkedList<>();
        for (Integer pageId : pageIdsInOrder) {
            pageTitles.add(getString(pageId));
        }

        return pageTitles.toArray(new String[pageTitles.size()]);
    }

    public int getPosition(String pageTitle) {
        List<String> pageTitles = Arrays.asList(getPagesTitle());
        if (!pageTitles.contains(pageTitle)) {
            throw new AssertionError("No navigation page for page title " + pageTitle);
        }
        return pageTitles.indexOf(pageTitle);
    }

    protected String getString(int stringResourceId) {
        return context.getResources().getString(stringResourceId);
    }

    private LinkedHashMap<Integer, Class<? extends Activity>> createPageIdToActivityTypeMap() {
        LinkedHashMap<Integer, Class<? extends Activity>> pageIdToFragmentMapInOrder = new LinkedHashMap<>();
        pageIdToFragmentMapInOrder.put(R.string.map, MapActivity.class);
        pageIdToFragmentMapInOrder.put(R.string.events, ProgrammeActivity.class);
        pageIdToFragmentMapInOrder.put(R.string.updates, UpdatesActivity.class);
        pageIdToFragmentMapInOrder.put(R.string.arrival_methods, ArrivalMethodsActivity.class);
	    pageIdToFragmentMapInOrder.put(R.string.feedback, FeedbackActivity.class);
	    pageIdToFragmentMapInOrder.put(R.string.settings, SettingsActivity.class);
	    pageIdToFragmentMapInOrder.put(R.string.about, AboutActivity.class);

        return pageIdToFragmentMapInOrder;
    }
}

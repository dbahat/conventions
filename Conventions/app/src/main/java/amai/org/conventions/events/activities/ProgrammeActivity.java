package amai.org.conventions.events.activities;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ViewTreeObserver;
import android.widget.AbsListView;
import android.widget.ListView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import amai.org.conventions.R;
import amai.org.conventions.events.activities.EventActivity;
import amai.org.conventions.events.adapters.EventsViewOrHourAdapter;
import amai.org.conventions.events.activities.MyEventsActivity;
import amai.org.conventions.model.Convention;
import amai.org.conventions.model.ConventionEvent;
import amai.org.conventions.model.ConventionEventComparator;
import amai.org.conventions.model.Dates;
import amai.org.conventions.navigation.NavigationActivity;
import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

public class ProgrammeActivity extends NavigationActivity {

    private EventsViewOrHourAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentInContentContainer(R.layout.activity_programme);
        setToolbarTitle(getResources().getString(R.string.programme_title));

        final StickyListHeadersListView listView = (StickyListHeadersListView) findViewById(R.id.programmeList);
        List<ConventionEvent> events = new ArrayList<>(Convention.getInstance().getEvents());
        Collections.sort(events, new Comparator<ConventionEvent>() {
            @Override
            public int compare(ConventionEvent lhs, ConventionEvent rhs) {
                return lhs.getStartTime().compareTo(rhs.getStartTime());
            }
        });
        adapter = new EventsViewOrHourAdapter(events);
	    listView.setAdapter(adapter);

	    final int position = findCurrentTimePosition(events);
	    if (position != -1) {
		    final ViewTreeObserver vto = listView.getViewTreeObserver();
		    vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
			    public void onGlobalLayout() {
				    listView.smoothScrollToPositionFromTop(position, 0, 500);

				    // There is a bug in smoothScrollToPositionFromTop that sometimes it doesn't scroll all the way.
				    // More info here : https://code.google.com/p/android/issues/detail?id=36062
				    // As a workaround, we listen to when it finished scrolling, and then scroll again to
				    // the same position.
				    listView.setOnScrollListener(new AbsListView.OnScrollListener() {

				    @Override
				    public void onScrollStateChanged(AbsListView view, int scrollState) {
					    if(scrollState== AbsListView.OnScrollListener.SCROLL_STATE_IDLE){
						    listView.setOnScrollListener(null);
						    listView.smoothScrollToPositionFromTop(position, 0, 500);
					    }
				    }

				    @Override
				    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
				    }
			    });

				    // Unregister the listener to only call scrollToPosition once
			    listView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
		    }
	    });
	    }
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        // Always redraw the list during onResume, since it's a fast operation, and this ensures the data is up to date in case the activity got paused.
        adapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.programme_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.programme_navigate_to_my_events:
                navigateToActivity(MyEventsActivity.class);

                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private static int toHour(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.HOUR_OF_DAY);
    }

    private int findCurrentTimePosition(List<ConventionEvent> events) {
        int currentHour = toHour(Dates.now());
        int i=0;
        for (ConventionEvent event : events) {
            if (toHour(event.getStartTime()) == currentHour) {
                return i;
            }
            i++;
        }

        return 0;
    }
}

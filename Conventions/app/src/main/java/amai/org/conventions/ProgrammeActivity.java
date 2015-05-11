package amai.org.conventions;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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

import amai.org.conventions.events.activities.EventActivity;
import amai.org.conventions.events.adapters.EventsViewOrHourAdapter;
import amai.org.conventions.events.activities.MyEventsActivity;
import amai.org.conventions.model.Convention;
import amai.org.conventions.model.ConventionEvent;
import amai.org.conventions.model.ConventionEventComparator;
import amai.org.conventions.navigation.NavigationActivity;

public class ProgrammeActivity extends NavigationActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentInContentContainer(R.layout.activity_programme);
        setToolbarTitle(getResources().getString(R.string.programme_title));
        getNavigationToolbar().setAsActionBar(this);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.programmeList);
        recyclerView.setAdapter(new EventsViewOrHourAdapter(getEventsAndStartTimes()));
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
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

    private List<Object> getEventsAndStartTimes() {
        // Gets the convention events
	    List<ConventionEvent> events = Convention.getInstance().getEvents();
	    List<Object> eventsAndDates = new ArrayList<Object>(events);

        // Gets the unique start times
        List<Date> dates = extractUniqueRoundedStartDates(events);

        eventsAndDates.addAll(dates);

        Collections.sort(eventsAndDates, new ConventionEventOrTimeComparator());
        return eventsAndDates;
    }

    private List<Date> extractUniqueRoundedStartDates(List<ConventionEvent> events) {
        HashSet<Date> dates = new HashSet<>();
        for (ConventionEvent event : events) {
            Date date = event.getStartTime();

            // Round the hour
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);

            dates.add(calendar.getTime());
        }

        return new ArrayList<>(dates);
    }

    private class ConventionEventOrTimeComparator implements Comparator<Object> {

        private ConventionEventComparator conventionEventComparator = new ConventionEventComparator();

        @Override
        public int compare(Object lhs, Object rhs) {
            if (lhs instanceof ConventionEvent && rhs instanceof ConventionEvent) {
                return conventionEventComparator.compare((ConventionEvent) lhs, (ConventionEvent) rhs);
            }

            if (lhs instanceof Date && rhs instanceof Date) {
                return ((Date) lhs).compareTo((Date) rhs);
            }

            int leftStartTime = getStartHour(lhs);
            int rightStartTime = getStartHour(rhs);

            // If the hours are equal, return the date type first
            if (leftStartTime == rightStartTime) {
                if (lhs instanceof Date) return -1;
                else return 1;
            }

            // Otherwise compare by the hours
            return ((Integer) leftStartTime).compareTo(rightStartTime);
        }

        private int getStartHour(Object eventOrDate) {
            if (eventOrDate instanceof ConventionEvent) {
                return toHour(((ConventionEvent) eventOrDate).getStartTime());
            } else {
                return toHour((Date) eventOrDate);
            }
        }

        private int toHour(Date date) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            return calendar.get(Calendar.HOUR_OF_DAY);
        }
    }
}

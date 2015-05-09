package amai.org.conventions;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

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

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.programmeList);
        recyclerView.setAdapter(new EventsViewOrHourAdapter(getEventsAndStartTimes()));
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private List<ConventionEvent> getEventsAndStartTimes() {
        // Gets the convention events
        List events = new ArrayList<Object>(Convention.getInstance().getEvents());

        // Gets the unique start times
        List dates = extractUniqueRoundedStartDates(events);

        events.addAll(dates);

        Collections.sort(events, new ConventionEventOrTimeComparator());
        return events;
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

    private class ConventionEventOrTimeComparator implements Comparator {

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

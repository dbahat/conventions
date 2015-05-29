package amai.org.conventions.events.activities;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.AbsListView;
import android.widget.DatePicker;
import android.widget.TimePicker;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import amai.org.conventions.R;
import amai.org.conventions.events.ProgrammeConventionEvent;
import amai.org.conventions.events.adapters.EventsViewOrHourAdapter;
import amai.org.conventions.events.holders.EventTimeViewHolder;
import amai.org.conventions.model.Convention;
import amai.org.conventions.model.ConventionEvent;
import amai.org.conventions.model.Dates;
import amai.org.conventions.navigation.NavigationActivity;
import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

public class ProgrammeActivity extends NavigationActivity {

    private EventsViewOrHourAdapter adapter;
    private StickyListHeadersListView listView;
    private List<ProgrammeConventionEvent> events;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentInContentContainer(R.layout.activity_programme);
        setToolbarTitle(getResources().getString(R.string.programme_title));

        this.listView = (StickyListHeadersListView) findViewById(R.id.programmeList);
        this.events = getEventList();
        adapter = new EventsViewOrHourAdapter(events);
        listView.setAdapter(adapter);

        final int position = findHourPosition(floorHour(Dates.now()));
        if (position != -1) {
            final ViewTreeObserver vto = listView.getViewTreeObserver();
            vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                public void onGlobalLayout() {
                    scrollToPosition(position);

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

    public void onTimeSectionClicked(View view) {
        EventTimeViewHolder eventTimeViewHolder = (EventTimeViewHolder) view.getTag();
        int selectedTimeSectionHour = eventTimeViewHolder.getCurrentHour();
        TimePickerDialog dialog = new TimePickerDialog(this, TimePickerDialog.THEME_HOLO_LIGHT, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                int position = findHourPosition(hourOfDay);
                if (position != -1) {
                    scrollToPosition(position);
                }
            }
        }, selectedTimeSectionHour, 0, true);
        dialog.show();
    }

    private void scrollToPosition(final int position) {
        listView.smoothScrollToPositionFromTop(position, 0, 500);

        // There is a bug in smoothScrollToPositionFromTop that sometimes it doesn't scroll all the way.
        // More info here : https://code.google.com/p/android/issues/detail?id=36062
        // As a workaround, we listen to when it finished scrolling, and then scroll again to
        // the same position.
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                    listView.setOnScrollListener(null);
                    listView.smoothScrollToPositionFromTop(position, 0, 500);
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            }
        });
    }

    private int findHourPosition(int hour) {
        int i = 0;
        for (ProgrammeConventionEvent event : events) {
            if (floorHour(event.getEvent().getStartTime()) == hour) {
                return i;
            }
            i++;
        }

        return -1;
    }

    private List<ProgrammeConventionEvent> getEventList() {
        List<ConventionEvent> events = new ArrayList<>(Convention.getInstance().getEvents());
        List<ProgrammeConventionEvent> programmeEvents = new LinkedList<>();

        for (ConventionEvent event : events) {

            // Convert the event start time to hourly time sections, and duplicate it if needed (e.g. if an event started at 13:30 and ended at 15:00, his
            // time sections are 13:00 and 14:00
            int eventDurationInHours = ceilHour(event.getEndTime()) - floorHour(event.getStartTime());
            for (int i = 0; i < eventDurationInHours; i++) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(event.getStartTime());
                calendar.add(Calendar.HOUR_OF_DAY, i);
                calendar.clear(Calendar.MINUTE);
                programmeEvents.add(new ProgrammeConventionEvent(event, calendar));
            }
        }

        Collections.sort(programmeEvents, new Comparator<ProgrammeConventionEvent>() {
            @Override
            public int compare(ProgrammeConventionEvent lhs, ProgrammeConventionEvent rhs) {
                // First compare by sections
                int result = lhs.getTimeSection().compareTo(rhs.getTimeSection());
                // In the same section, compare by hall order
                if (result == 0) {
                    result = lhs.getEvent().getHall().getOrder() - rhs.getEvent().getHall().getOrder();
                }
                // For 2 events in the same hall and section, compare by start time
                if (result == 0) {
                    result = lhs.getEvent().getStartTime().compareTo(rhs.getEvent().getStartTime());
                }

                return result;
            }
        });

        return programmeEvents;
    }

    private static int floorHour(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.HOUR_OF_DAY);
    }

    private int ceilHour(Date endTime) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(endTime);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        return minute >= 30 ? hour + 1 : hour;
    }
}

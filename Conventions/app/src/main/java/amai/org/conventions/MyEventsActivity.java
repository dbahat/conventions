package amai.org.conventions;

import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import amai.org.conventions.model.CollectionsFilter;
import amai.org.conventions.model.Convention;
import amai.org.conventions.model.ConventionEvent;
import amai.org.conventions.model.ConventionEventComparator;
import amai.org.conventions.model.Dates;
import amai.org.conventions.navigation.NavigationActivity;
import amai.org.conventions.navigation.NavigationToolbar;


public class MyEventsActivity extends NavigationActivity {

    // Handler for updating the next event start text
    private Handler nextEventStartTextRunner = new Handler();
    private Runnable updateNextEventStartTimeText;

    private TextView nextEventStart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_events);
        NavigationToolbar navigationToolbar = (NavigationToolbar) findViewById(R.id.my_events_toolbar);
        navigationToolbar.initialize();
        navigationToolbar.setNavigationPageSelectedListener(this);

        List<ConventionEvent> events = getMyEvents();
        Collections.sort(events, new ConventionEventComparator());

        // Set up text view for next event start
        nextEventStart = (TextView) findViewById(R.id.nextEventStart);
        setNextEventStartText(events);

        // Set up events list
        RecyclerView hallEventsList = (RecyclerView) findViewById(R.id.myEventsList);
        hallEventsList.setAdapter(new EventsViewAdapter(events, true, true));

        hallEventsList.setLayoutManager(new LinearLayoutManager(this));
    }

    private List<ConventionEvent> getMyEvents() {
        return CollectionsFilter.filter(
                Convention.getInstance().getEvents(),
                new CollectionsFilter.Predicate<ConventionEvent>() {
                    @Override
                    public boolean where(ConventionEvent event) {
                        return event.isAttending();
                    }
                },
                new ArrayList<ConventionEvent>()
        );
    }

    private void setNextEventStartText(final List<ConventionEvent> events) {
        ConventionEvent nextEvent = null;
        Date currTime = Dates.now();
        for (ConventionEvent curr : events) {
            if (curr.getStartTime().after(currTime)) {
                nextEvent = curr;
                break;
            }
        }

        // Only display it if it's on the same day
        boolean displayNextEventStart = false;
        if (nextEvent != null) {
            Calendar startTime = Calendar.getInstance();
            startTime.setTime(nextEvent.getStartTime());
            Calendar now = Calendar.getInstance();
            now.setTime(currTime);
            if (startTime.get(Calendar.DATE) == now.get(Calendar.DATE) &&
                    startTime.get(Calendar.MONTH) == now.get(Calendar.MONTH) &&
                    startTime.get(Calendar.YEAR) == now.get(Calendar.YEAR)) {
                displayNextEventStart = true;
            }
        }
        nextEventStart.setVisibility(displayNextEventStart ? View.VISIBLE : View.GONE);
        if (displayNextEventStart) {
            nextEventStart.setText("האירוע הבא מתחיל בעוד " +
                    Dates.toHumanReadableTimeDuration(nextEvent.getStartTime().getTime() - currTime.getTime()) +
                    " ב" + nextEvent.getHall().getName());

            if (updateNextEventStartTimeText == null) {
                updateNextEventStartTimeText = new Runnable() {
                    @Override
                    public void run() {
                        MyEventsActivity.this.setNextEventStartText(events);
                        nextEventStartTextRunner.postAtTime(this, System.currentTimeMillis() + 1000);
                    }
                };
            }
            nextEventStartTextRunner.postDelayed(updateNextEventStartTimeText, 1000);
        } else {
            nextEventStartTextRunner.removeCallbacks(updateNextEventStartTimeText);
        }
    }
}

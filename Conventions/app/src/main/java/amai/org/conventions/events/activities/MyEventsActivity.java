package amai.org.conventions.events.activities;

import android.os.Bundle;
import android.os.Handler;
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

import amai.org.conventions.R;
import amai.org.conventions.events.adapters.ConflictingEventsViewAdapter;
import amai.org.conventions.model.CollectionsFilter;
import amai.org.conventions.model.Convention;
import amai.org.conventions.model.ConventionEvent;
import amai.org.conventions.model.ConventionEventComparator;
import amai.org.conventions.model.Dates;
import amai.org.conventions.navigation.NavigationActivity;


public class MyEventsActivity extends NavigationActivity {

    // Handler for updating the next event start text
    private Handler nextEventStartTextRunner = new Handler();
    private Runnable updateNextEventStartTimeText;

    private TextView nextEventStart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentInContentContainer(R.layout.activity_my_events);
        setToolbarTitle(getResources().getString(R.string.my_events_title));

        List<ConventionEvent> events = getMyEvents();
        Collections.sort(events, new ConventionEventComparator());


        // Set up text view for next event start
        nextEventStart = (TextView) findViewById(R.id.nextEventStart);
        setNextEventStartText(events);

        // Set up events list
	    ArrayList<ArrayList<ConventionEvent>> nonConflictingGroups = getNonConflictingGroups(events);
	    RecyclerView eventsList = (RecyclerView) findViewById(R.id.myEventsList);
	    eventsList.setAdapter(new ConflictingEventsViewAdapter(nonConflictingGroups, true, true));

	    eventsList.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.my_events_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.my_event_navigate_to_programme:
                navigateToActivity(ProgrammeActivity.class);

                return true;
        }

        return super.onOptionsItemSelected(item);
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

	/**
	 * Split events to conflicting groups. A conflict between 2 events happens when one of events starts
	 * after the other event's start time and before its end time.
	 * @param events - list of events sorted by start time
	 * @return a list of event groups. Each event group is a list of events, with the same sort order as
	 * sent, where each event conflicts with at least one other event in the group. Events from different
	 * groups do not conflict with each other. The groups are ordered by the first event's start time.
	 */
	private ArrayList<ArrayList<ConventionEvent>> getNonConflictingGroups(List<ConventionEvent> events) {
		ArrayList<ArrayList<ConventionEvent>> nonConflictingEventGroups = new ArrayList<>();

		Date currGroupEndTime = null;
		ArrayList<ConventionEvent> currGroup = null;
		for (ConventionEvent event : events) {
			// Non-conflicting event - it's either the first event or it starts after
			// (or at the same time as) the current group ends.
			if (currGroup == null || !event.getStartTime().before(currGroupEndTime)) {
				// If we have a previous group, add it to the groups list
				if (currGroup != null) {
					nonConflictingEventGroups.add(currGroup);
				}
				currGroup = new ArrayList<>(1);
				currGroupEndTime = null;
			}
			currGroup.add(event);
			if (currGroupEndTime == null || event.getEndTime().after(currGroupEndTime)) {
				currGroupEndTime = event.getEndTime();
			}

		}
		// Add the last group
		nonConflictingEventGroups.add(currGroup);

		return nonConflictingEventGroups;
	}
}

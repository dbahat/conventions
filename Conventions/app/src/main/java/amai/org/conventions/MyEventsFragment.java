package amai.org.conventions;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

import amai.org.conventions.model.CollectionsFilter;
import amai.org.conventions.model.Convention;
import amai.org.conventions.model.ConventionEvent;
import amai.org.conventions.model.ConventionEventComparator;
import amai.org.conventions.model.Dates;
import amai.org.conventions.navigation.NavigationActivity;


/**
 * A simple {@link Fragment} subclass.
 */
public class MyEventsFragment extends Fragment {
	// Handler for updating the next event start text
	private Handler nextEventStartTextRunner = new Handler();
	private Runnable updateText;

	private TextView nextEventStart;

	public MyEventsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my_events, container, false);

        ArrayList<ConventionEvent> events = CollectionsFilter.filter(
                Convention.getInstance().getEvents(),
                new CollectionsFilter.Predicate<ConventionEvent>() {
                    @Override
                    public boolean where(ConventionEvent event) {
                        return event.isAttending();
                    }
                },
                new ArrayList<ConventionEvent>()
        );
        Collections.sort(events, new ConventionEventComparator());

	    // Set up text view for next event start
	    nextEventStart = (TextView) view.findViewById(R.id.nextEventStart);
	    setNextEventStartText(events);

	    // Set up events list
        RecyclerView hallEventsList = (RecyclerView) view.findViewById(R.id.myEventsList);
        hallEventsList.setAdapter(new EventsViewAdapter(events, true, true));

        hallEventsList.setLayoutManager(new LinearLayoutManager(getActivity()));

        return view;
    }

	private void setNextEventStartText(final ArrayList<ConventionEvent> events) {
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

			if (updateText == null) {
				updateText = new Runnable() {
					@Override
					public void run() {
						MyEventsFragment.this.setNextEventStartText(events);
						nextEventStartTextRunner.postAtTime(this, System.currentTimeMillis() + 1000);
					}
				};
			}
			nextEventStartTextRunner.postDelayed(updateText, 1000);
		} else {
			nextEventStartTextRunner.removeCallbacks(updateText);
		}
	}

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (isVisibleToUser) {
            NavigationActivity navigationActivity = (NavigationActivity) getActivity();
            navigationActivity.setTitle(getResources().getString(R.string.my_events_title));
        }
    }
}

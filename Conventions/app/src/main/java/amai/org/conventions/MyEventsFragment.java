package amai.org.conventions;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import java.util.LinkedList;
import java.util.List;

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
	    setNextEventStartText(events, nextEventStart);

	    // Set up events list
        RecyclerView hallEventsList = (RecyclerView) view.findViewById(R.id.myEventsList);
        hallEventsList.setAdapter(new EventsViewAdapter(events, true, true));

        hallEventsList.setLayoutManager(new LinearLayoutManager(getActivity()));

        return view;
    }

	private void setNextEventStartText(ArrayList<ConventionEvent> events, TextView nextEventStart) {
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
			nextEventStart.setText("האירוע הבא מתחיל בעוד " + toHumanReadableTimeDuration(nextEvent.getStartTime().getTime() - currTime.getTime()) +
					" ב" + nextEvent.getHall().getName());

		}
	}

	private String toHumanReadableTimeDuration(long milliseconds) {
		long x = milliseconds / 1000;
		int seconds = (int) (x % 60);
		x /= 60;
		int minutes = (int) (x % 60);
		x /= 60;
		int hours = (int) (x % 24);

		return toHumanReadableTimeDuration(hours, minutes, seconds);
	}

	private String toHumanReadableTimeDuration(int hours, int minutes, int seconds) {
		List<String> parts = new ArrayList<>(3);
		if (hours > 1) {
			parts.add(hours + " שעות");
		} else if (hours == 1) {
			parts.add("שעה");
		}

		if (minutes > 1) {
			parts.add(minutes + " דקות");
		} else if (minutes == 1) {
			parts.add("דקה");
		}

		if (seconds > 1) {
			parts.add(seconds + " שניות");
		} else if (seconds == 1) {
			parts.add("שנייה");
		}

		StringBuilder result = new StringBuilder();
		int size = parts.size();
		if (size == 1) {
			result.append(parts.get(0));
		} else {
			for (int i = 0; i < size; ++i) {
				result.append(parts.get(i));

				// Not last or before last
				if (i < size - 2) {
					result.append(", ");
				} else if (i == size - 2) { // before last
					result.append(" ו");
					if (Character.isDigit(parts.get(i + 1).charAt(0))) {
						result.append("-");
					}
				}
			}

		}

		return result.toString();
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

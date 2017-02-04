package amai.org.conventions.events.activities;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import amai.org.conventions.R;
import amai.org.conventions.events.adapters.EventGroupsAdapter;
import amai.org.conventions.model.ConventionEvent;
import amai.org.conventions.utils.CollectionUtils;
import amai.org.conventions.utils.Dates;

public class MyEventsDayFragment extends Fragment {
	private static final String ARGS_DATE = "ArgDate";
	private Calendar date;

	private RecyclerView eventsList;
	private View emptyView;
	private EventGroupsAdapter adapter;

	private EventsListener listener;


	public static MyEventsDayFragment newInstance(Calendar date) {
		MyEventsDayFragment fragment = new MyEventsDayFragment();
		Bundle args = new Bundle();
		args.putLong(ARGS_DATE, date.getTimeInMillis());
		fragment.setArguments(args);
		return fragment;
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_my_events_day, container, false);

		date = Calendar.getInstance();
		date.setTimeInMillis(getArguments().getLong(ARGS_DATE));

		emptyView = view.findViewById(R.id.my_events_empty);
		eventsList = (RecyclerView) view.findViewById(R.id.myEventsList);
		eventsList.setLayoutManager(new LinearLayoutManager(getActivity()));

		return view;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		if (activity instanceof EventsListener) {
			listener = (EventsListener) activity;
		}
	}

	@Override
	public void onDetach() {
		super.onDetach();
		listener = null;
	}

	@Override
	public void onResume() {
		super.onResume();
		updateDataset();
	}

	private void updateDataset() {
		final List<ConventionEvent> events = getMyEvents();

		// Set up events list
		ArrayList<EventsTimeSlot> nonConflictingGroups = getNonConflictingGroups(null, events, null);
		if (adapter == null) {
			adapter = new EventGroupsAdapter(nonConflictingGroups);
		} else {
			adapter.updateEventGroups(nonConflictingGroups);
		}
		eventsList.setAdapter(adapter);

		updateVisibility(nonConflictingGroups.size(), eventsList, emptyView);

		// Register for dataset update events, in case we need to return the empty layout view after all items were dismissed.
		adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
			@Override
			public void onItemRangeRemoved(int positionStart, int itemCount) {
				super.onItemRangeRemoved(positionStart, itemCount);
				updateVisibility(adapter.getItemCount(), eventsList, emptyView);
			}

			@Override
			public void onChanged() {
				super.onChanged();
				updateVisibility(adapter.getItemCount(), eventsList, emptyView);
			}
		});

		adapter.setOnEventRemovedAction(new Runnable() {
			@Override
			public void run() {
				if (listener != null) {
					listener.onEventRemoved();
				}
			}
		});
	}

	public List<ConventionEvent> getMyEvents() {
		List<ConventionEvent> events = MyEventsActivity.getMyEvents();
		events = CollectionUtils.filter(events, new CollectionUtils.Predicate<ConventionEvent>() {
			@Override
			public boolean where(ConventionEvent event) {
				Calendar startTime = Calendar.getInstance();
				startTime.setTime(event.getStartTime());
				Calendar endTime = Calendar.getInstance();
				endTime.setTime(event.getEndTime());

				// Filter out events not starting on this day
				return Dates.isSameDate(date, startTime);
			}
		});
		return events;
	}

	/**
	 * Split events to conflicting groups. A conflict between 2 events happens when one of events starts
	 * after the other event's start time and before its end time.
	 * @param events - list of events sorted by start time
	 * @return a list of event groups. Each event group is a list of events, with the same sort order as
	 * sent, where each event conflicts with at least one other event in the group. Events from different
	 * groups do not conflict with each other. The groups are ordered by the first event's start time.
	 */
	public static ArrayList<EventsTimeSlot> getNonConflictingGroups(EventsTimeSlot previous, List<ConventionEvent> events, EventsTimeSlot next) {
		ArrayList<EventsTimeSlot> nonConflictingTimeSlots = new ArrayList<>();

		Date currGroupEndTime = (previous != null ? previous.getEndTime() : null);
		EventsTimeSlot currSlot = previous;
		for (ConventionEvent event : events) {
			// Non-conflicting event - it's either the first event or it starts after
			// (or at the same time as) the current group ends.
			if (currSlot == null || !event.getStartTime().before(currGroupEndTime)) {
				// If we have a previous group, add it to the groups list
				if (currSlot != null) {
					if (currSlot != previous) {
						nonConflictingTimeSlots.add(currSlot);
					}

					// If there are at least 30 minutes between this group and the next, add a free time slot
					if (event.getStartTime().getTime() - currGroupEndTime.getTime() >= 30 * Dates.MILLISECONDS_IN_MINUTE) {
						EventsTimeSlot freeSlot = new EventsTimeSlot(currGroupEndTime, event.getStartTime());
						nonConflictingTimeSlots.add(freeSlot);
					}
				}
				currSlot = new EventsTimeSlot();
				currGroupEndTime = null;
			}
			currSlot.addEvent(event);
			if (currGroupEndTime == null || event.getEndTime().after(currGroupEndTime)) {
				currGroupEndTime = event.getEndTime();
			}
		}

		// Add the last group
		if (currSlot != null) {
			if (currSlot != previous) {
				nonConflictingTimeSlots.add(currSlot);
			}

			if (next != null && next.getStartTime().getTime() - currGroupEndTime.getTime() >= 30 * Dates.MILLISECONDS_IN_MINUTE) {
				EventsTimeSlot freeSlot = new EventsTimeSlot(currGroupEndTime, next.getStartTime());
				nonConflictingTimeSlots.add(freeSlot);
			}
		}

		return nonConflictingTimeSlots;
	}

	private void updateVisibility(int datasetSize, RecyclerView eventsList, View emptyView) {
		if (datasetSize > 0) {
			eventsList.setVisibility(View.VISIBLE);
			emptyView.setVisibility(View.GONE);
		} else {
			eventsList.setVisibility(View.GONE);
			emptyView.setVisibility(View.VISIBLE);
		}
	}

	public interface EventsListener {
		void onEventRemoved();
	}

}

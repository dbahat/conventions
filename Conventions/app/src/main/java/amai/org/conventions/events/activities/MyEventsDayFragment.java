package amai.org.conventions.events.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import amai.org.conventions.ThemeAttributes;
import amai.org.conventions.events.adapters.EventGroupsAdapter;
import amai.org.conventions.model.ConventionEvent;
import amai.org.conventions.model.conventions.Convention;
import amai.org.conventions.utils.CollectionUtils;
import amai.org.conventions.utils.Dates;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import sff.org.conventions.R;

public class MyEventsDayFragment extends Fragment {
	private static final String ARGS_DATE = "ArgDate";
	private Calendar date;

	private RecyclerView eventsList;
	private TextView emptyView;
	private EventGroupsAdapter adapter;

	private EventsListener listener;


	public static MyEventsDayFragment newInstance(Calendar date) {
		MyEventsDayFragment fragment = new MyEventsDayFragment();
		Bundle args = new Bundle();
		args.putLong(ARGS_DATE, date.getTimeInMillis());
		fragment.setArguments(args);
		return fragment;
	}

	/**
	 * Split events to conflicting groups. A conflict between 2 events happens when one of the events
	 * starts after the other event's start time and before its end time, and neither of the events are
	 * ongoing.
	 *
	 * @param events - list of events sorted by start time
	 * @return a list of event groups. Each event group is a list of events, with the same sort order as
	 * sent, where each event conflicts with at least one other event in the group. Events from different
	 * groups do not conflict with each other. The groups are ordered by the first event's start time.
	 */
	public static ArrayList<EventsTimeSlot> getNonConflictingGroups(EventsTimeSlot previous, List<ConventionEvent> events, EventsTimeSlot next) {
		ArrayList<EventsTimeSlot> nonConflictingTimeSlots = new ArrayList<>();
		List<ConventionEvent> currOngoingEvents = new LinkedList<>();

		Date currGroupEndTime = (previous != null ? previous.getEndTime() : null);
		EventsTimeSlot currSlot = previous;
		for (ConventionEvent event : events) {
			// Ongoing events are handled separately, when we add non-ongoing events
			if (Convention.getInstance().isEventOngoing(event)) {
				currOngoingEvents.add(event);
				continue;
			}

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

				// Add the current ongoing events in separate groups
				for (ConventionEvent ongoingEvent : currOngoingEvents) {
					EventsTimeSlot ongoingSlot = new EventsTimeSlot();
					ongoingSlot.addEvent(ongoingEvent);
					nonConflictingTimeSlots.add(ongoingSlot);
				}
				currOngoingEvents = new LinkedList<>();
			} else {
				// Add the current ongoing events to the current list, before the new event
				for (ConventionEvent ongoingEvent : currOngoingEvents) {
					currSlot.addEvent(ongoingEvent);
				}
				currOngoingEvents = new LinkedList<>();
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

		// Add the remaining ongoing events in separate groups
		for (ConventionEvent ongoingEvent : currOngoingEvents) {
			EventsTimeSlot ongoingSlot = new EventsTimeSlot();
			ongoingSlot.addEvent(ongoingEvent);
			nonConflictingTimeSlots.add(ongoingSlot);
		}

		return nonConflictingTimeSlots;
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_my_events_day, container, false);

		date = Calendar.getInstance();
		date.setTimeInMillis(getArguments().getLong(ARGS_DATE));

		emptyView = view.findViewById(R.id.my_events_empty);
		if (Convention.getInstance().canUserLogin()) {
			emptyView.setText(R.string.my_events_no_data);
		} else {
			emptyView.setText(R.string.my_events_no_data_no_login);
		}
		eventsList = (RecyclerView) view.findViewById(R.id.myEventsList);
		eventsList.setLayoutManager(new LinearLayoutManager(getActivity()));
		DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getActivity(),
				DividerItemDecoration.VERTICAL);
		dividerItemDecoration.setDrawable(ThemeAttributes.getDrawable(getActivity(), R.attr.eventListDivider));
		eventsList.addItemDecoration(dividerItemDecoration);

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

	public void updateDataset() {
		// Set up events list adapter
		if (adapter == null) {
			adapter = new EventGroupsAdapter(new EventGroupsAdapter.Callback<ArrayList<EventsTimeSlot>>() {
				@Override
				public ArrayList<EventsTimeSlot> call() {
					return getNonConflictingGroups(null, getMyEvents(), null);
				}
			});
		} else {
			adapter.updateEventGroups();
		}
		eventsList.setAdapter(adapter);

		updateVisibility(adapter.getItemCount(), eventsList, emptyView);

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

		adapter.setOnEventListChangedAction(new Runnable() {
			@Override
			public void run() {
				if (listener != null) {
					listener.onEventListChanged();
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

				// Filter out events not starting on this day
				return Dates.isSameDate(date, startTime);
			}
		});
		return events;
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
		void onEventListChanged();
	}

}

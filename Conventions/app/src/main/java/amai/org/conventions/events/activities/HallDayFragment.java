package amai.org.conventions.events.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import sff.org.conventions.R;
import amai.org.conventions.events.adapters.SwipeableEventsViewAdapter;
import amai.org.conventions.model.ConventionEvent;
import amai.org.conventions.model.ConventionEventComparator;
import amai.org.conventions.model.conventions.Convention;
import amai.org.conventions.utils.CollectionUtils;
import amai.org.conventions.utils.Dates;

public class HallDayFragment extends Fragment {
	private static final String ARGS_HALL_NAME = "ArgHallName";
	private static final String ARGS_DATE = "ArgDate";
	private SwipeableEventsViewAdapter adapter;

	public static HallDayFragment newInstance(String hallName, Calendar date) {
		HallDayFragment fragment = new HallDayFragment();
		Bundle args = new Bundle();
		args.putString(ARGS_HALL_NAME, hallName);
		args.putLong(ARGS_DATE, date.getTimeInMillis());
		fragment.setArguments(args);
		return fragment;
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_hall_day, container, false);

		final Calendar date = Calendar.getInstance();
		date.setTimeInMillis(getArguments().getLong(ARGS_DATE));
		String hallName = getArguments().getString(ARGS_HALL_NAME);

		RecyclerView hallEventsList = (RecyclerView) view.findViewById(R.id.hallEventsList);
		List<ConventionEvent> events = Convention.getInstance().findEventsByHall(hallName);
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

		Collections.sort(events, new ConventionEventComparator());

		adapter = new SwipeableEventsViewAdapter(events, hallEventsList);
		hallEventsList.setAdapter(adapter);
		hallEventsList.setLayoutManager(new LinearLayoutManager(getActivity()));

		return view;
	}

	@Override
	public void onResume() {
		super.onResume();

		// Always redraw the list during onResume, since it's a fast operation, and this ensures the data is up to date in case the activity got paused
		// (including going into an event, adding it to favorites and returning)
		adapter.notifyDataSetChanged();
	}
}

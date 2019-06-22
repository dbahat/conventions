package amai.org.conventions.events.activities;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import amai.org.conventions.R;
import amai.org.conventions.ThemeAttributes;
import amai.org.conventions.events.adapters.EventsViewAdapter;
import amai.org.conventions.model.ConventionEvent;
import amai.org.conventions.model.ConventionEventComparator;
import amai.org.conventions.model.conventions.Convention;
import amai.org.conventions.utils.CollectionUtils;
import amai.org.conventions.utils.Dates;

public class HallDayFragment extends Fragment {
	private static final String ARGS_HALL_NAME = "ArgHallName";
	private static final String ARGS_DATE = "ArgDate";
	private EventsViewAdapter adapter;

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
		View hallNoEvents = view.findViewById(R.id.hall_no_events_found);

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

		if (events.size() > 0) {
			hallEventsList.setVisibility(View.VISIBLE);
			hallNoEvents.setVisibility(View.GONE);
			Collections.sort(events, new ConventionEventComparator());

			adapter = new EventsViewAdapter(events, hallEventsList);
			hallEventsList.setAdapter(adapter);
			hallEventsList.setLayoutManager(new LinearLayoutManager(getActivity()));
			DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getActivity(),
					DividerItemDecoration.VERTICAL);
			dividerItemDecoration.setDrawable(ThemeAttributes.getDrawable(getActivity(), R.attr.eventListDivider));
			hallEventsList.addItemDecoration(dividerItemDecoration);
		} else {
			hallEventsList.setVisibility(View.GONE);
			hallNoEvents.setVisibility(View.VISIBLE);
		}


		return view;
	}

	@Override
	public void onResume() {
		super.onResume();

		// Always redraw the list during onResume, since it's a fast operation, and this ensures the data is up to date in case the activity got paused
		// (including going into an event, adding it to favorites and returning)
		if (adapter != null) {
			adapter.notifyDataSetChanged();
		}
	}
}

package amai.org.conventions.events.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AbsListView;
import android.widget.NumberPicker;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import amai.org.conventions.R;
import amai.org.conventions.ThemeAttributes;
import amai.org.conventions.events.DefaultEventFavoriteChangedListener;
import amai.org.conventions.events.ProgrammeConventionEvent;
import amai.org.conventions.events.ViewPagerAnimator;
import amai.org.conventions.events.adapters.EventsViewOrHourAdapter;
import amai.org.conventions.events.holders.TimeViewHolder;
import amai.org.conventions.model.ConventionEvent;
import amai.org.conventions.model.conventions.Convention;
import amai.org.conventions.utils.CollectionUtils;
import amai.org.conventions.utils.Dates;
import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

public class ProgrammeDayFragment extends Fragment implements StickyListHeadersListView.OnHeaderClickListener, SwipeRefreshLayout.OnRefreshListener {
	private static final String ARGS_DATE = "ArgDate";
	private static final String ARGS_DELAY_SCROLLING = "ArgDelayScrolling";
	private static final String STATE_PREVENT_SCROLLING = "StatePreventScrolling";

	private SwipeRefreshLayout swipeLayout;
	private EventsViewOrHourAdapter adapter;
	private StickyListHeadersListView listView;
	private List<ProgrammeConventionEvent> events;
	private EventsListener listener;

	private boolean cancelScroll;
	private Calendar date;


	public static ProgrammeDayFragment newInstance(Calendar date, int delayScrolling) {
		ProgrammeDayFragment fragment = new ProgrammeDayFragment();
		Bundle args = new Bundle();
		args.putLong(ARGS_DATE, date.getTimeInMillis());
		args.putInt(ARGS_DELAY_SCROLLING, delayScrolling);
		fragment.setArguments(args);

		return fragment;
	}

	private static int getHour(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		return calendar.get(Calendar.HOUR_OF_DAY);
	}

	private static int getEndHour(Date endTime) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(endTime);
		int hour = calendar.get(Calendar.HOUR_OF_DAY);
		int minute = calendar.get(Calendar.MINUTE);

		// The first minute of the next hour is considered this hour. For example, an event
		// ending at 12:00 is only considered to run during hour 11:00.
		return minute > 0 ? hour : hour - 1;
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_programme_day, container, false);

		date = Calendar.getInstance();
		date.setTimeInMillis(getArguments().getLong(ARGS_DATE));


		swipeLayout = (SwipeRefreshLayout) view.findViewById(R.id.programme_swipe_layout);
		swipeLayout.setOnRefreshListener(this);
		swipeLayout.setColorSchemeColors(ThemeAttributes.getColor(container.getContext(), R.attr.swipeToRefreshColor));

		listView = (StickyListHeadersListView) view.findViewById(R.id.programmeList);
		events = getEventsList();
		adapter = new EventsViewOrHourAdapter(events);
		listView.setAdapter(adapter);
		adapter.setOnEventFavoriteChangedListener(new DefaultEventFavoriteChangedListener(listView) {
			@Override
			public void onEventFavoriteChanged(ConventionEvent updatedEvent) {
				super.onEventFavoriteChanged(updatedEvent);
				if (listener != null) {
					listener.onEventFavoriteChanged(updatedEvent);
				}
			}
		});

		listView.setOnHeaderClickListener(this);

		if (savedInstanceState == null || !savedInstanceState.getBoolean(STATE_PREVENT_SCROLLING, false)) {
			final int position = findHourPosition(Dates.now());
			if (position != -1) {
				cancelScroll = false;

				listView.setOnTouchListener(new View.OnTouchListener() {
					@Override
					public boolean onTouch(View v, MotionEvent event) {
						// For some reason if the user touches the list view after scrolling ends (before bounce), there is no down event
						if (event.getActionMasked() == MotionEvent.ACTION_DOWN || event.getActionMasked() == MotionEvent.ACTION_MOVE) {
							cancelScroll = true;
							listView.setOnTouchListener(null);
						}
						return false;
					}
				});

				final ViewTreeObserver vto = listView.getViewTreeObserver();
				vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
					public void onGlobalLayout() {
						// Unregister the listener to only call scrollToPosition once
						listView.getViewTreeObserver().removeOnGlobalLayoutListener(this);

						int delay = getArguments().getInt(ARGS_DELAY_SCROLLING, 0);
						new Handler().postDelayed(new Runnable() {
							@Override
							public void run() {
								scrollToPosition(position, true);
							}
						}, delay);
					}
				});
			}
		}

		return view;
	}

	@Override
	public void onAttach(Context context) {
		super.onAttach(context);
		if (context instanceof EventsListener) {
			listener = (EventsListener) context;
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

		// Always redraw the list during onResume, since it's a fast operation, and this ensures the data is up to date in case the activity got paused.
		adapter.notifyDataSetChanged();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		outState.putBoolean(STATE_PREVENT_SCROLLING, true);
		super.onSaveInstanceState(outState);
	}

	@Override
	public void onRefresh() {
		if (listener != null) {
			listener.onRefresh();
		} else {
			setRefreshing(false);
		}
	}

	public void setRefreshing(boolean refreshing) {
		// This could be called before onCreateView
		if (swipeLayout != null) {
			swipeLayout.setRefreshing(refreshing);
		}
	}

	public void updateEvents() {
		new AsyncTask<Void, Void, List<ProgrammeConventionEvent>>() {
			@Override
			protected List<ProgrammeConventionEvent> doInBackground(Void... params) {
				return getEventsList();
			}

			@Override
			protected void onPostExecute(final List<ProgrammeConventionEvent> events) {
				// This must be done in postDelayed because if setItems runs during the checkbox
				// toggle animation it makes it not smooth, so we need to ensure it runs after it ends
				listView.postDelayed(new Runnable() {
					@Override
					public void run() {
						ProgrammeDayFragment.this.events = events;
						adapter.setItems(events);
					}
				}, 200);

			}
		}.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	}

	@Override
	public void onHeaderClick(StickyListHeadersListView stickyListHeadersListView, View view, int i, long l, boolean b) {
		TimeViewHolder eventTimeViewHolder = (TimeViewHolder) view.getTag();
		Calendar selectedTimeSectionTime = eventTimeViewHolder.getTime();

		// Setup number picker dialog
		final NumberPicker numberPicker = new NumberPicker(getActivity());

		final List<Calendar> timeSections = new LinkedList<>();

		int previousHour = -1;
		int selectedValue = 0;
		for (ProgrammeConventionEvent event : events) {
			int hour = event.getTimeSection().get(Calendar.HOUR_OF_DAY);
			if (hour != previousHour) {
				previousHour = hour;
				timeSections.add(event.getTimeSection());

				// Check if this is the time the user clicked on
				if (hour == selectedTimeSectionTime.get(Calendar.HOUR_OF_DAY) &&
						Dates.isSameDate(event.getTimeSection(), selectedTimeSectionTime)) {
					selectedValue = timeSections.size() - 1;
				}
			}
		}

		List<String> formattedSections = CollectionUtils.map(timeSections, new CollectionUtils.Mapper<Calendar, String>() {
			@Override
			public String map(Calendar item) {
				SimpleDateFormat sdf = new SimpleDateFormat("HH:00", Dates.getLocale());
				return sdf.format(item.getTime());
			}
		});

		numberPicker.setMinValue(0);
		numberPicker.setMaxValue(timeSections.size() - 1);
		numberPicker.setDisplayedValues(formattedSections.toArray(new String[formattedSections.size()]));
		numberPicker.setValue(selectedValue);
		numberPicker.setWrapSelectorWheel(false);

		numberPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);

		AlertDialog dialog = new AlertDialog.Builder(getActivity())
				.setView(numberPicker)
				.setPositiveButton(R.string.select_hour_ok,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int whichButton) {
								Calendar timeSection = timeSections.get(numberPicker.getValue());
								int position = findHourPosition(timeSection);
								if (position != -1) {
									cancelScroll = false;
									scrollToPosition(position, false);
								}
							}
						})
				.create();
		dialog.show();
	}

	private int findHourPosition(Calendar timeSection) {
		if (timeSection == null) {
			return -1;
		}

		int i = 0;
		for (ProgrammeConventionEvent event : events) {
			if (!event.getTimeSection().before(timeSection)) {
				return i;
			}
			i++;
		}
		return -1;
	}

	private int findHourPosition(Date time) {
		Calendar timeCalendar = Calendar.getInstance();
		timeCalendar.setTime(time);
		// Only keep up to the hour part
		timeCalendar.set(Calendar.MINUTE, 0);
		timeCalendar.set(Calendar.SECOND, 0);
		timeCalendar.set(Calendar.MILLISECOND, 0);
		int i = 0;

		for (ProgrammeConventionEvent event : events) {
			// This is called from the activity startup, we don't want to scroll if it's not the convention date
			if (!event.getTimeSection().before(timeCalendar)) {
				return i;
			}
			i++;
		}

		// If we got here it means the selected hour is after the last event ends so no scrolling is required
		return -1;
	}

	private List<ProgrammeConventionEvent> getEventsList() {
		List<ConventionEvent> events = new ArrayList<>(Convention.getInstance().getEvents());
		List<ProgrammeConventionEvent> programmeEvents = new LinkedList<>();

		for (ConventionEvent event : events) {
			Calendar startTime = Calendar.getInstance();
			startTime.setTime(event.getStartTime());
			// Only show events starting on this day
			if (!Dates.isSameDate(date, startTime)) {
				continue;
			}
			// Convert the event start time to hourly time sections, and duplicate it if needed (e.g. if an event started at 13:30 and ended at 15:00, its
			// time sections are 13:00 and 14:00)
			int eventDurationInHours = getEndHour(event.getEndTime()) - getHour(event.getStartTime()) + 1;
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

	private void scrollToPosition(final int position, final boolean shouldApplyFavoriteReminderAnimation) {
		if (!cancelScroll) {
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
						if (!cancelScroll) {
							listView.smoothScrollToPositionFromTop(position, 0, 500);

							// In some cases we'll want to show bounce animation to the scrolled view, to make it easier for users
							// to understand the views are swipeable.
							if (shouldApplyFavoriteReminderAnimation) {
								listView.postDelayed(new Runnable() {
									@Override
									public void run() {
										triggerBounceAnimationIfNeeded();
										listView.setOnTouchListener(null);
									}
								}, 1500);
							} else {
								listView.setOnTouchListener(null);
							}
						}
					}
				}

				@Override
				public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
				}
			});
		}
	}

	private void triggerBounceAnimationIfNeeded() {
		if (!Convention.getInstance().hasFavorites() && !cancelScroll) {
			if (listView.getListChildCount() > 1) {
				// Apply the animation on the second listView child, since the first will always be hidden by a sticky header
				View currentEvent = listView.getListChildAt(1);

				ViewPager currentEventViewPager = (ViewPager) currentEvent.findViewById(R.id.swipe_pager);
				ViewPagerAnimator.applyBounceAnimation(currentEventViewPager);
			}
		}
	}

	public interface EventsListener {
		void onEventFavoriteChanged(ConventionEvent updatedEvent);

		void onRefresh();
	}
}

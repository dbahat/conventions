package amai.org.conventions;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import amai.org.conventions.events.activities.MyEventsActivity;
import amai.org.conventions.events.activities.ProgrammeActivity;
import amai.org.conventions.model.ConventionEvent;
import amai.org.conventions.model.ConventionEventComparator;
import amai.org.conventions.model.ConventionEventEndTimeComparator;
import amai.org.conventions.model.conventions.Convention;
import amai.org.conventions.navigation.NavigationActivity;
import amai.org.conventions.updates.UpdatesActivity;
import amai.org.conventions.utils.CollectionUtils;
import amai.org.conventions.utils.Dates;
import amai.org.conventions.utils.Views;

import static amai.org.conventions.utils.CollectionUtils.filter;

public class HomeActivity extends NavigationActivity {
	// These are used for consistent navigation
	private ConventionEvent currentFavoriteEvent;
	private ConventionEvent upcomingFavoriteEvent;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setToolbarAndContentContainerBackground(ThemeAttributes.getDrawable(this, R.attr.homeBackground));
		setToolbarTitle(ThemeAttributes.getDrawable(this, R.attr.homeToolbarTitle));
	}

	@Override
	protected void onResume() {
		super.onResume();

		currentFavoriteEvent = getCurrentFavoriteEvent();

		// Creating the page layout during onResume (and not onCreate) since the layout is time-driven,
		// and we want it refreshed in case the activity was paused and got resumed.
		if (Dates.now().before(Convention.getInstance().getStartDate().getTime())) {
			setContentForBeforeConventionStarted();
		// We consider the convention as "ended" here if there is no current event the user is attending and
		// all events have already started (this could be during the convention date or after it)
		} else if (Convention.getInstance().haveAllEventsStarted() && currentFavoriteEvent == null) {
			setContentForAfterConventionEnded();
		} else {
			setContentForDuringConvention();
		}
	}

	private void setContentForDuringConvention() {
		currentFavoriteEvent = getCurrentFavoriteEvent();
		upcomingFavoriteEvent = getUpcomingFavoriteEvent();

		if (currentFavoriteEvent != null || upcomingFavoriteEvent != null) {
			setContentWithUpcomingFavorites(currentFavoriteEvent, upcomingFavoriteEvent);
		} else {
			setContentForNoUpcomingFavorites();
		}
	}

	private void setContentWithUpcomingFavorites(ConventionEvent currentEvent, ConventionEvent upcomingEvent) {
		setContentInContentContainer(R.layout.activity_home_during_convention, false, false);

		View currentEventContainer = findViewById(R.id.home_current_event_container);
		TextView currentEventTitle = (TextView)findViewById(R.id.home_current_event_title);
		TextView currentEventVoteText = (TextView)findViewById(R.id.home_current_event_vote);
		View upcomingEventContainer = findViewById(R.id.home_upcoming_event_container);
		TextView upcomingEventTime = (TextView)findViewById(R.id.home_upcoming_event_time);
		TextView upcomingEventTitle = (TextView)findViewById(R.id.home_upcoming_event_title);
		TextView upcomingEventHall = (TextView)findViewById(R.id.home_upcoming_event_hall);
		TextView upcomingEventVoteText = (TextView)findViewById(R.id.home_upcoming_event_vote);

		if (upcomingEvent != null) {
			// There's an upcoming event - show it
			upcomingEventTitle.setText(upcomingEvent.getTitle());
			upcomingEventHall.setText(upcomingEvent.getHall().getName());
			upcomingEventTime.setText(getString(
					R.string.home_upcoming_event_time,
					Dates.toHumanReadableTimeDuration(upcomingEvent.getStartTime().getTime() - Dates.now().getTime()))
			);

			// if there's a current event, show it as well
			if (currentEvent != null) {
				currentEventTitle.setText(getString(R.string.home_now_showing, currentEvent.getTitle()));
				if (currentEvent.getUserInput().getVoteSurvey() != null) {
					currentEventVoteText.setVisibility(View.VISIBLE);
				} else {
					currentEventVoteText.setVisibility(View.GONE);
				}
			} else {
				currentEventContainer.setVisibility(View.GONE);
			}
		} else {
			// no upcoming event to show, but there's an event currently showing - show the current event at the upcoming event layout
			currentEventTitle.setVisibility(View.GONE);
			upcomingEventTitle.setText(currentEvent.getTitle());
			upcomingEventTitle.setTextColor(ThemeAttributes.getColor(this, R.attr.homeCurrentEventText));
			upcomingEventTime.setText(getString(R.string.home_now_showing, ""));
			upcomingEventTime.setTextColor(ThemeAttributes.getColor(this, R.attr.homeCurrentEventText));
			upcomingEventHall.setText(currentEvent.getHall().getName());
			upcomingEventHall.setTextColor(ThemeAttributes.getColor(this, R.attr.homeCurrentEventText));
			upcomingEventContainer.setBackground(ThemeAttributes.getDrawable(this, R.attr.homeCurrentEventBackground));
			if (currentEvent.getUserInput().getVoteSurvey() != null) {
				upcomingEventVoteText.setVisibility(View.VISIBLE);
				upcomingEventVoteText.setTextColor(ThemeAttributes.getColor(this, R.attr.homeCurrentEventText));
			} else {
				upcomingEventVoteText.setVisibility(View.GONE);
			}

			// In this case, we want the 'go to my events' to go to the programme instead, since the user has no more favorite events.
			Button goToMyEventsButton = (Button)findViewById(R.id.home_go_to_my_events_button);
			goToMyEventsButton.setText(R.string.home_go_to_programme);
			goToMyEventsButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					onGoToProgrammeClicked(v);
				}
			});
		}
		Views.fixRadialGradient(findViewById(R.id.home_current_event_container_background));
		Views.fixRadialGradient(upcomingEventContainer);
	}

	private void setContentForNoUpcomingFavorites() {
		setContentInContentContainer(R.layout.activity_home_during_convention_no_favorites, false, false);

		TextView upcomingProgrammeEventsTitle = (TextView) findViewById(R.id.home_upcoming_programme_events_title);
		ListView upcomingEventsListView = (ListView) findViewById(R.id.home_upcoming_programme_events_list);

		final List<ConventionEvent> upcomingEvents = getUpcomingProgrammeEvents();

		Date upcomingEventsTime = upcomingEvents.size() > 0
				? upcomingEvents.get(0).getStartTime()
				: Dates.now(); // error case - if we don't have any upcoming events we shouldn't have reached here. Add some default non-crashing behavior.

		upcomingProgrammeEventsTitle.setText(getString(R.string.home_upcoming_programme_events, Dates.formatHoursAndMinutes(upcomingEventsTime)));
		final BaseAdapter adapter = new BaseAdapter() {
			@Override
			public int getCount() {
				return upcomingEvents.size();
			}

			@Override
			public Object getItem(int position) {
				return upcomingEvents.get(position);
			}

			@Override
			public long getItemId(int position) {
				return position;
			}

			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				ProgrammeEventTitleViewHolder viewHolder;
				if (convertView == null) {
					convertView = LayoutInflater.from(HomeActivity.this).inflate(R.layout.view_home_programme_event, parent, false);
					viewHolder = new ProgrammeEventTitleViewHolder(convertView);
					convertView.setTag(viewHolder);
				} else {
					viewHolder = (ProgrammeEventTitleViewHolder)convertView.getTag();
				}

				ConventionEvent upcomingEvent = upcomingEvents.get(position);
				viewHolder.bind(upcomingEvent.getTitle(), position < getCount() - 1);
				return convertView;
			}
		};
		upcomingEventsListView.setAdapter(adapter);
		upcomingEventsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				ConventionEvent event = (ConventionEvent) adapter.getItem(position);
				navigateToEvent(event.getId());
			}
		});

		Views.fixRadialGradient(upcomingEventsListView);
	}

	public void onCurrentEventClicked(View view) {
		if (currentFavoriteEvent != null) {
			navigateToEvent(currentFavoriteEvent.getId());
		}
	}

	public void onUpcomingEventClicked(View view) {
		if (upcomingFavoriteEvent != null) {
			navigateToEvent(upcomingFavoriteEvent.getId());
		} else {
			onCurrentEventClicked(view);
		}
	}

	private class ProgrammeEventTitleViewHolder extends RecyclerView.ViewHolder {

		private TextView title;
		private View divider;

		ProgrammeEventTitleViewHolder(View itemView) {
			super(itemView);

			title = (TextView)itemView.findViewById(R.id.home_programme_event_title);
			divider = itemView.findViewById(R.id.home_programme_event_divider);
		}

		void bind(String upcomingEventTitle, boolean shouldShowDivider) {
			title.setText(upcomingEventTitle);
			divider.setVisibility(shouldShowDivider ? View.VISIBLE : View.GONE);
		}
	}

	private List<ConventionEvent> getUpcomingProgrammeEvents() {
		List<ConventionEvent> allUpcomingEvents = CollectionUtils.filter(Convention.getInstance().getEvents(), new CollectionUtils.Predicate<ConventionEvent>() {
			@Override
			public boolean where(ConventionEvent event) {
				return !event.hasStarted();
			}
		});
		if (allUpcomingEvents.size() == 0) {
			return new ArrayList<>();
		}

		Collections.sort(allUpcomingEvents, new ConventionEventComparator());
		final Date nextEventsBatchStartTime = allUpcomingEvents.get(0).getStartTime();

		return CollectionUtils.filter(allUpcomingEvents, new CollectionUtils.Predicate<ConventionEvent>() {
			@Override
			public boolean where(ConventionEvent event) {
				return event.getStartTime().getTime() == nextEventsBatchStartTime.getTime();
			}
		});
	}

	@Nullable
	private ConventionEvent getCurrentFavoriteEvent() {
		List<ConventionEvent> currentFavoriteEvents = filter(Convention.getInstance().getEvents(), new CollectionUtils.Predicate<ConventionEvent>() {
			@Override
			public boolean where(ConventionEvent event) {
				return event.isAttending() && event.hasStarted() && !event.hasEnded();
			}
		});
		if (currentFavoriteEvents.size() == 0) {
			return null;
		}

		return currentFavoriteEvents.get(0);
	}

	@Nullable
	private ConventionEvent getUpcomingFavoriteEvent() {
		List<ConventionEvent> upcomingFavoriteEvents = filter(Convention.getInstance().getEvents(), new CollectionUtils.Predicate<ConventionEvent>() {
			@Override
			public boolean where(ConventionEvent event) {
				return event.isAttending() && !event.hasStarted();
			}
		});
		if (upcomingFavoriteEvents.size() == 0) {
			return null;
		}
		Collections.sort(upcomingFavoriteEvents, new ConventionEventEndTimeComparator());
		return upcomingFavoriteEvents.get(0);
	}

	private void setContentForBeforeConventionStarted() {
		setContentInContentContainer(R.layout.activity_home_before_convention, false, false);
		TextView titleView = (TextView)findViewById(R.id.home_content_title);
		TextView contentView = (TextView)findViewById(R.id.home_content);
		Views.fixRadialGradient(findViewById(R.id.home_content_container));

		// the convention didn't start yet. Show the user the number of days until it starts.
		int daysUntilConventionStarts = getDaysUntilConventionStart();
		if (daysUntilConventionStarts == 1) {
			contentView.setText("מחר!");
		} else if (daysUntilConventionStarts == 2) {
			contentView.setText("עוד יומיים!");
		} else {
			contentView.setText(getString(R.string.home_convention_start_time, daysUntilConventionStarts));
		}
		Calendar startDate = Convention.getInstance().getStartDate();
		SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy", Dates.getLocale());
		String dateTitle = sdf.format(startDate.getTime());
		Calendar endDate = Convention.getInstance().getEndDate();
		if (!Dates.isSameDate(startDate, endDate)) {
			dateTitle = getString(R.string.dash_with_values, dateTitle, sdf.format(endDate.getTime()));
			titleView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
		}
		titleView.setText(dateTitle);
	}

	private void setContentForAfterConventionEnded() {
		setContentInContentContainer(R.layout.activity_home_after_convention, false, false);
		FrameLayout contentViewContainer = (FrameLayout)findViewById(R.id.home_content_container);
		TextView titleView = (TextView)findViewById(R.id.home_content_title);
		TextView contentView = (TextView)findViewById(R.id.home_content);
		Views.fixRadialGradient(contentViewContainer);
		// All the events are already in-progress or finished. Show the user to the feedback screen.
		contentViewContainer.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				navigateToActivity(FeedbackActivity.class);
			}
		});

		if (Convention.getInstance().getFeedback().isSent() || Convention.getInstance().isFeedbackSendingTimeOver()) {
			// The feedback filling time is over or feedback was sent. Allow the user to see his feedback
			titleView.setText(R.string.home_convention_ended);
			contentView.setText(R.string.home_show_feedback);
		} else {
			// Ask the user to fill feedback
			titleView.setText(R.string.home_help_us_improve);
			contentView.setText(R.string.home_send_feedback);
		}
	}

	private int getDaysUntilConventionStart() {
		long timeUntilConvention = Convention.getInstance().getStartDate().getTimeInMillis() - Dates.now().getTime();
		Calendar calendarTimeUntilConvention = Dates.toCalendar(new Date(timeUntilConvention));
		return calendarTimeUntilConvention.get(Calendar.DAY_OF_YEAR);
	}

	public void onGoToProgrammeClicked(View view) {
		navigateToActivity(ProgrammeActivity.class);
	}

	public void onGoToUpdatesClicked(View view) {
		navigateToActivity(UpdatesActivity.class);
	}

	public void onGoToMyEventsClicked(View view) {
		navigateToActivity(MyEventsActivity.class);
	}
}

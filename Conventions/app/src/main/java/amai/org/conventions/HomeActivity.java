package amai.org.conventions;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

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

import static amai.org.conventions.utils.CollectionUtils.filter;

public class HomeActivity extends NavigationActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setToolbarAndContentContainerBackground(ContextCompat.getDrawable(this, R.drawable.harucon2017_home_background));
		setToolbarTitle(ContextCompat.getDrawable(this, R.drawable.harucon_2017_title_black));
	}

	@Override
	protected void onResume() {
		super.onResume();

		// Creating the page layout during onResume (and not onCreate) since the layout is time-driven, and we want it refreshed in case the activity was paused
		// and got resumed.
		if (!Convention.getInstance().hasStarted() || Convention.getInstance().haveAllEventsStarted()) {
			setContentForBeforeOrAfterConventionDate();
		} else {
			setContentForDuringConvention();
		}
	}

	private void setContentForDuringConvention() {
		ConventionEvent currentEvent = getCurrentFavoriteEvent();
		ConventionEvent upcomingEvent = getUpcomingFavoriteEvent();

		if (currentEvent != null || upcomingEvent != null) {
			setContentWithUpcomingFavorites(currentEvent, upcomingEvent);
		} else {
			setContentForNoUpcomingFavorites();
		}
	}

	private void setContentWithUpcomingFavorites(ConventionEvent currentEvent, ConventionEvent upcomingEvent) {
		setContentInContentContainer(R.layout.activity_home_during_convention, false, false);

		TextView currentEventTitle = (TextView)findViewById(R.id.home_current_event_title);
		TextView upcomingEventTime = (TextView)findViewById(R.id.home_upcoming_event_time);
		TextView upcomingEventTitle = (TextView)findViewById(R.id.home_upcoming_event_title);
		TextView upcomingEventHall = (TextView)findViewById(R.id.home_upcoming_event_hall);

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
			} else {
				currentEventTitle.setVisibility(View.GONE);
			}
		} else {
			// no upcoming event to show, but there's an event currently showing - show the current event at the upcoming event layout
			currentEventTitle.setVisibility(View.GONE);
			upcomingEventTitle.setText(currentEvent.getTitle());
			upcomingEventTime.setText(getString(R.string.home_now_showing, ""));
			upcomingEventHall.setText(currentEvent.getHall().getName());

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
					convertView = LayoutInflater.from(HomeActivity.this).inflate(R.layout.view_programme_event_title, parent, false);
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
	}

	public void onCurrentEventClicked(View view) {
		ConventionEvent currentEvent = getCurrentFavoriteEvent();
		if (currentEvent != null) {
			navigateToEvent(currentEvent.getId());
		}
	}

	public void onUpcomingEventClicked(View view) {
		ConventionEvent upcomingFavoriteEvent = getUpcomingFavoriteEvent();
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

			title = (TextView)itemView.findViewById(R.id.programme_event_title_text);
			divider = itemView.findViewById(R.id.programme_event_title_divider);
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

	private void setContentForBeforeOrAfterConventionDate() {
		setContentInContentContainer(R.layout.activity_home_not_during_convention, false, false);

		TextView titleView = (TextView)findViewById(R.id.home_content_title);
		TextView contentView = (TextView)findViewById(R.id.home_content);

		if (!Convention.getInstance().haveAllEventsStarted()) {
			// before the convention started, show the days until it starts.
			int daysUntilConventionStarts = getDaysUntilConventionStart();
			if (daysUntilConventionStarts == 1) {
				contentView.setText("מחר!");
			} else if (daysUntilConventionStarts == 2) {
				contentView.setText("עוד יומיים!");
			} else {
				contentView.setText(getString(R.string.home_convention_start_time, daysUntilConventionStarts));
			}
			titleView.setText(R.string.home_convention_date);
		} else {
			contentView.setForeground(ThemeAttributes.getDrawable(this, R.attr.selectableItemBackground));
			contentView.setClickable(true);
			contentView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					navigateToActivity(FeedbackActivity.class);
				}
			});
			findViewById(R.id.home_buttons_layout).setVisibility(View.GONE);

			if (Convention.getInstance().getFeedback().isSent() || Convention.getInstance().isFeedbackSendingTimeOver()) {
				// The feedback filling time is over or feedback was sent. Allow the user to see his feedback
				titleView.setText(R.string.home_convention_ended);
				contentView.setText(R.string.home_show_feedback);
				contentView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 50);
				ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) contentView.getLayoutParams();
				int margins = getResources().getDimensionPixelOffset(R.dimen.home_show_feedback_text_view_margins);
				layoutParams.setMarginStart(margins);
				layoutParams.setMarginEnd(margins);
				contentView.setLayoutParams(layoutParams);
			} else {
				// Ask the user to fill feedback
				titleView.setText(R.string.home_help_us_improve);
				contentView.setText(R.string.home_send_feedback);
			}
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

package amai.org.conventions;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import amai.org.conventions.events.activities.MyEventsActivity;
import amai.org.conventions.events.activities.ProgrammeActivity;
import amai.org.conventions.model.ConventionEvent;
import amai.org.conventions.model.ConventionEventEndTimeComparator;
import amai.org.conventions.model.conventions.Convention;
import amai.org.conventions.navigation.NavigationActivity;
import amai.org.conventions.updates.UpdatesActivity;
import amai.org.conventions.utils.CollectionUtils;
import amai.org.conventions.utils.Dates;

import static amai.org.conventions.R.id.home_upcoming_event_time;

public class HomeActivity extends NavigationActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (!Convention.getInstance().hasStarted() || Convention.getInstance().hasEnded()) {
			setContentForBeforeConventionDate();
		} else {
			ConventionEvent currentEvent = getCurrentFavoriteEvent();
			ConventionEvent upcomingEvent = getUpcomingFavoriteEvent();

			if (currentEvent != null || upcomingEvent != null) {
				setContentInContentContainer(R.layout.activity_home_during_convention, false, false);

				TextView currentEventTitle = (TextView)findViewById(R.id.home_current_event_title);
				TextView upcomingEventTime = (TextView)findViewById(home_upcoming_event_time);
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
				}
			} else {
				// TODO - add handling in case there's no current or upcoming event (show the programme)
				setContentInContentContainer(R.layout.activity_home_during_convention_no_favorites, false, false);
			}
		}

		setToolbarAndContentContainerBackground(ContextCompat.getDrawable(this, R.drawable.harucon2017_home_background));
		setToolbarTitle(ContextCompat.getDrawable(this, R.drawable.harucon_2017_title_black));
	}

	@Nullable
	private ConventionEvent getCurrentFavoriteEvent() {
		List<ConventionEvent> currentFavoriteEvents = CollectionUtils.filter(Convention.getInstance().getEvents(), new CollectionUtils.Predicate<ConventionEvent>() {
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
		List<ConventionEvent> upcomingFavoriteEvents = CollectionUtils.filter(Convention.getInstance().getEvents(), new CollectionUtils.Predicate<ConventionEvent>() {
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

	private void setContentForBeforeConventionDate() {
		setContentInContentContainer(R.layout.activity_home_not_during_convention, false, false);

		TextView titleView = (TextView)findViewById(R.id.home_content_title);
		TextView contentView = (TextView)findViewById(R.id.home_content);

		if (!Convention.getInstance().hasEnded()) {
			// before the convention started, show the days until it starts.
			// TODO: Add special handling for 1 and 2 days remaining
			contentView.setText(getString(R.string.home_convention_start_time, getDaysUntilConventionStart()));
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
				titleView.setText("");
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
		long timeUntilConvention = Convention.getInstance().getStartDate().getTimeInMillis() - Calendar.getInstance().getTimeInMillis();
		Calendar calendarTimeUntilConvention = Calendar.getInstance();
		calendarTimeUntilConvention.setTime(new Date(timeUntilConvention));
		return calendarTimeUntilConvention.get(Calendar.DAY_OF_YEAR);
	}

	public void onGoToProgrammeClicked(View view) {
		navigateToActivity(ProgrammeActivity.class);
	}

	public void onGoToUpdatesClicked(View view) {
		navigateToActivity(UpdatesActivity.class);
	}

	public void onGoToMyEventsClicked(View view) {
		startActivity(new Intent(this, MyEventsActivity.class));
	}
}

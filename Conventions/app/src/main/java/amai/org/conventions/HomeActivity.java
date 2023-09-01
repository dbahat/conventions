package amai.org.conventions;

import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
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
import amai.org.conventions.utils.StateList;
import amai.org.conventions.utils.Views;
import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import sff.org.conventions.R;

import static amai.org.conventions.utils.CollectionUtils.filter;

public class HomeActivity extends NavigationActivity {
	// These are used for consistent navigation
	private ConventionEvent currentFavoriteEvent;
	private ConventionEvent upcomingFavoriteEvent;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setToolbarBackground(ThemeAttributes.getDrawable(this, R.attr.homeToolbarBackground));
		setToolbarTitle(ThemeAttributes.getDrawable(this, R.attr.homeToolbarTitle));
		setBackground(ThemeAttributes.getDrawable(this, R.attr.homeBackground));
	}

	@Override
	protected void onResume() {
		super.onResume();

		currentFavoriteEvent = getCurrentFavoriteEvent();
		setContentInContentContainer(R.layout.activity_home, false, false);
		setBackground(ThemeAttributes.getDrawable(this, R.attr.homeBackground));

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

	private void setInfoBoxContent(@LayoutRes int infoBoxContent) {
		FrameLayout infoBox = findViewById(R.id.home_info_box);
		// Removing all previous views before attaching the newly inflated layout to support calling setInfoBoxContent() more then once
		infoBox.removeAllViews();
		getLayoutInflater().inflate(infoBoxContent, infoBox, true);
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

	private boolean setupVoteOrView(ConventionEvent currentEvent, ImageView imageView) {
		if (currentEvent.getUserInput().getVoteSurvey() != null || Convention.getInstance().getEventViewURL(currentEvent) != null) {
			if (Convention.getInstance().getEventViewURL(currentEvent) == null) {
				imageView.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.vote));
			} else if (currentEvent.getUserInput().getVoteSurvey() == null) {
				imageView.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.baseline_videocam_white_48));
			} else {
				imageView.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.videocam_vote_white_48));
			}
			imageView.setVisibility(View.VISIBLE);
			return true;
		} else {
			imageView.setVisibility(View.GONE);
			return false;
		}
	}

	private void setViewState(int viewID, StateList states) {
		states.setForView(findViewById(viewID));
	}

	private void setContentWithUpcomingFavorites(ConventionEvent currentEvent, ConventionEvent upcomingEvent) {
		boolean useSepTitlesLayout = ThemeAttributes.getBoolean(this, R.attr.homeShowCurrentUpcomingTitles);
		if (useSepTitlesLayout) {
			setInfoBoxContent(R.layout.home_box_during_convention_sep_titles);
		} else {
			setInfoBoxContent(R.layout.home_box_during_convention);
		}

		TextView currentEventTitle = findViewById(R.id.home_current_event_title);
		View currentEventTitleContainer = findViewById(R.id.home_current_event_title_container);
		View currentEventContainer = findViewById(R.id.home_current_event_container_title);
		TextView currentEventName = findViewById(R.id.home_current_event_name);
		TextView currentEventHall = findViewById(R.id.home_current_event_hall);
		ImageView currentEventVote = findViewById(R.id.home_current_event_vote);

		TextView upcomingEventTitle = findViewById(R.id.home_upcoming_event_title);
		View upcomingEventContainer = findViewById(R.id.home_upcoming_event_container);
		TextView upcomingEventTime = findViewById(R.id.home_upcoming_event_time);
		TextView upcomingEventName = findViewById(R.id.home_upcoming_event_name);
		TextView upcomingEventHall = findViewById(R.id.home_upcoming_event_hall);
		ImageView upcomingEventVote = findViewById(R.id.home_upcoming_event_vote);

		StateList baseStates = new StateList(R.attr.state_home_during_con, R.attr.state_home_has_favorites);
		if (currentEvent != null) {
			baseStates.add(R.attr.state_home_has_current);
		}
		if (upcomingEvent != null) {
			baseStates.add(R.attr.state_home_has_upcoming);
		}

		StateList currEventStates = baseStates.clone().add(R.attr.state_home_is_current);
		currEventStates.setForView(currentEventContainer);
		currEventStates.setForView(currentEventVote);
		currentEventName.setTextColor(currEventStates.getThemeColor(this, R.attr.homeContentText));

		StateList upcomingEventStates = baseStates.clone().add(R.attr.state_home_is_upcoming);
		upcomingEventStates.setForView(upcomingEventContainer);
		upcomingEventStates.setForView(upcomingEventVote);
		upcomingEventName.setTextColor(upcomingEventStates.getThemeColor(this, R.attr.homeContentText));
		upcomingEventTime.setTextColor(upcomingEventStates.getThemeColor(this, R.attr.homeEventTimeText));
		upcomingEventHall.setTextColor(upcomingEventStates.getThemeColor(this, R.attr.homeEventHallText));

		if (useSepTitlesLayout) {
			currEventStates.setForView(currentEventTitleContainer);
			currentEventTitle.setTextColor(currEventStates.getThemeColor(this, R.attr.homeTitleText));
			setViewState(R.id.home_upcoming_event_title_container, upcomingEventStates);
			upcomingEventTitle.setTextColor(upcomingEventStates.getThemeColor(this, R.attr.homeTitleText));
			currentEventHall.setTextColor(currEventStates.getThemeColor(this, R.attr.homeEventHallText));
		} else {
			setViewState(R.id.home_title_container, baseStates);
			TextView title = findViewById(R.id.home_content_title);
			title.setTextColor(baseStates.getThemeColor(this, R.attr.homeTitleText));
		}
		setViewState(R.id.home_logo, baseStates);
		setViewState(R.id.home_bottom_image, baseStates);

		if (upcomingEvent != null) {
			// There's an upcoming event - show it
			upcomingEventName.setText(upcomingEvent.getTitle());
			upcomingEventHall.setText(upcomingEvent.getHall().getName());
			upcomingEventTime.setText(getString(
					R.string.home_upcoming_event_time,
					Dates.toHumanReadableTimeDuration(upcomingEvent.getStartTime().getTime() - Dates.now().getTime()))
			);
			setupVoteOrView(upcomingEvent, upcomingEventVote);

			// if there's a current event, show it as well
			if (currentEvent != null) {
				// Only add the "currently showing:" prefix if it's not in the title
				if (useSepTitlesLayout) {
					currentEventName.setText(currentEvent.getTitle());
					currentEventHall.setText(currentEvent.getHall().getName());
				} else {
					currentEventName.setText(getString(R.string.home_now_showing, currentEvent.getTitle()));
				}
				setupVoteOrView(currentEvent, currentEventVote);
			} else {
				currentEventContainer.setVisibility(View.GONE);
				if (useSepTitlesLayout) {
					currentEventTitleContainer.setVisibility(View.GONE);
				}
			}
		} else {
			// no upcoming event to show, but there's an event currently showing - show the current event at the upcoming event layout
			currentEventContainer.setVisibility(View.GONE);
			currEventStates.setForView(upcomingEventContainer);
			if (useSepTitlesLayout) {
				currentEventTitleContainer.setVisibility(View.GONE);

				setViewState(R.id.home_upcoming_event_title_container, currEventStates);
				upcomingEventTitle.setText(R.string.home_now_showing_title);
				upcomingEventTitle.setTextColor(currEventStates.getThemeColor(this, R.attr.homeTitleText));
			}
			upcomingEventName.setText(currentEvent.getTitle());
			upcomingEventName.setTextColor(currEventStates.getThemeColor(this, R.attr.homeContentText));
			// Only set the "current" time if it's not in the title
			if (!useSepTitlesLayout) {
				upcomingEventTime.setText(R.string.home_now_showing_no_title);
			}
			upcomingEventTime.setTextColor(currEventStates.getThemeColor(this, R.attr.homeEventTimeText));
			upcomingEventHall.setText(currentEvent.getHall().getName());
			upcomingEventHall.setTextColor(currEventStates.getThemeColor(this, R.attr.homeEventHallText));
			setupVoteOrView(currentEvent, upcomingEventVote);
			currEventStates.setForView(upcomingEventVote);

			// In this case, we want the 'go to my events' to go to the programme instead, since the user has no more favorite events.
			Button goToMyEventsButton = findViewById(R.id.home_go_to_my_events_button);
			goToMyEventsButton.setText(R.string.home_go_to_programme);
			goToMyEventsButton.setOnClickListener(this::onGoToProgrammeClicked);
		}
		Views.fixRadialGradient(findViewById(R.id.home_current_event_container_title));
		Views.fixRadialGradient(upcomingEventContainer);

		// If one of the events is missing, add space to make it look like it's invisible instead of gone
		if (currentEvent == null || upcomingEvent == null) {
			View spaceView = findViewById(R.id.hidden_event_space);
			spaceView.setVisibility(View.VISIBLE);
			int height;

			// The spacing is different in each layout
			if (useSepTitlesLayout) {
				height = ThemeAttributes.getDimensionSize(this, R.attr.homeEventsMargin) +
					getResources().getDimensionPixelSize(R.dimen.home_during_event_title_height) +
					ThemeAttributes.getDimensionSize(this, R.attr.homeBoxTopMargin) +
					getResources().getDimensionPixelSize(R.dimen.home_during_event_content_height);
			} else {
				height = ThemeAttributes.getDimensionSize(this, R.attr.homeEventsMargin) +
					getResources().getDimensionPixelSize(R.dimen.home_during_event_curr_event_height);
			}
			ViewGroup.LayoutParams layoutParams = spaceView.getLayoutParams();
			layoutParams.height = height;
			spaceView.setLayoutParams(layoutParams);
		}

		boolean showButtons = ThemeAttributes.getBoolean(this, R.attr.homeShowButtons);
		if (!showButtons) {
			View goToMyEventsButton = findViewById(R.id.home_go_to_my_events_button);
			goToMyEventsButton.setVisibility(View.GONE);
		}
	}

	private void setContentForNoUpcomingFavorites() {
		setInfoBoxContent(R.layout.home_box_during_convention_no_favorites);

		boolean showButtons = ThemeAttributes.getBoolean(this, R.attr.homeShowButtons);
		if (!showButtons) {
			View button = findViewById(R.id.home_screen_bottom_button);
			button.setVisibility(View.GONE);
		}

		TextView upcomingProgrammeEventsTitle = findViewById(R.id.home_upcoming_programme_events_title);
		ListView upcomingEventsListView = findViewById(R.id.home_upcoming_programme_events_list);

		StateList baseStates = new StateList(R.attr.state_home_during_con);
		setViewState(R.id.home_title_container, baseStates);
		baseStates.setForView(upcomingEventsListView);
		upcomingProgrammeEventsTitle.setTextColor(baseStates.getThemeColor(this, R.attr.homeTitleText));
		setViewState(R.id.home_logo, baseStates);
		setViewState(R.id.home_bottom_image, baseStates);

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
				viewHolder.bind(upcomingEvent.getTitle(), Convention.getInstance().getEventViewURL(upcomingEvent) != null, baseStates);
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

	private static class ProgrammeEventTitleViewHolder extends RecyclerView.ViewHolder {
		private TextView title;
		private ImageView image;

		ProgrammeEventTitleViewHolder(View itemView) {
			super(itemView);
			title = itemView.findViewById(R.id.home_programme_event_title);
			image = itemView.findViewById(R.id.home_programme_event_image);
		}

		void bind(String upcomingEventTitle, boolean showImage, StateList states) {
			title.setText(upcomingEventTitle);
			title.setTextColor(states.getThemeColor(itemView.getContext(), R.attr.homeContentText));
			image.setVisibility(showImage ? View.VISIBLE : View.GONE);
			states.setForView(image);
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
		setInfoBoxContent(R.layout.home_box_before_convention);
		TextView titleView = findViewById(R.id.home_content_title);
		TextView contentView = findViewById(R.id.home_content);
		Views.fixRadialGradient(findViewById(R.id.home_content_container));

		StateList baseStates = new StateList(R.attr.state_home_before_con);
		setViewState(R.id.home_title_container, baseStates);
		setViewState(R.id.home_content_container, baseStates);
		contentView.setTextColor(baseStates.getThemeColor(this, R.attr.homeContentText));
		setViewState(R.id.home_logo, baseStates);
		setViewState(R.id.home_bottom_image, baseStates);

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
		titleView.setTextColor(baseStates.getThemeColor(this, R.attr.homeTitleText));

		boolean showButtons = ThemeAttributes.getBoolean(this, R.attr.homeShowButtons);
		if (!showButtons) {
			View buttonsLayout = findViewById(R.id.home_buttons_layout);
			buttonsLayout.setVisibility(View.GONE);
		}
	}

	private void setContentForAfterConventionEnded() {
		// All the events are already in-progress or finished. Show the user to the feedback screen.
		setInfoBoxContent(R.layout.home_box_after_convention);
		FrameLayout contentViewContainer = findViewById(R.id.home_content_container);
		TextView titleView = findViewById(R.id.home_content_title);
		TextView contentView = findViewById(R.id.home_content);
		Button goToFeedbackButton = findViewById(R.id.home_go_to_feedback_button);
		Views.fixRadialGradient(contentViewContainer);
		StateList baseStates = new StateList(R.attr.state_home_after_con);

		titleView.setText(getString(R.string.thanks_for_coming, Convention.getInstance().getDisplayName()));

		if (Convention.getInstance().getFeedback().isSent() || Convention.getInstance().isFeedbackSendingTimeOver()) {
			// The feedback filling time is over or feedback was sent. Allow the user to see his feedback
			goToFeedbackButton.setText(R.string.home_show_feedback);
			baseStates.add(R.attr.state_home_should_send_feedback);
		} else {
			// Ask the user to fill feedback
			goToFeedbackButton.setText(R.string.home_send_feedback);
		}

		setViewState(R.id.home_title_container, baseStates);
		baseStates.setForView(contentViewContainer);
		titleView.setTextColor(baseStates.getThemeColor(this, R.attr.homeTitleText));
		contentView.setTextColor(baseStates.getThemeColor(this, R.attr.homeContentText));
		setViewState(R.id.home_logo, baseStates);
		setViewState(R.id.home_bottom_image, baseStates);
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

	public void onGoToFeedbackClicked(View view) {
		navigateToActivity(FeedbackActivity.class);
	}
}

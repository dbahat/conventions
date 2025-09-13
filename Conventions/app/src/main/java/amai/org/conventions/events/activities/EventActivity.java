package amai.org.conventions.events.activities;

import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.net.URISyntaxException;
import java.net.URL;
import java.util.Calendar;
import java.util.List;

import amai.org.conventions.BuildConfig;
import amai.org.conventions.ConventionsApplication;
import amai.org.conventions.R;
import amai.org.conventions.ThemeAttributes;
import amai.org.conventions.customviews.FrameLayoutWithState;
import amai.org.conventions.events.CollapsibleFeedbackView;
import amai.org.conventions.events.ConfigureNotificationsFragment;
import amai.org.conventions.events.EventVoteSurveyFragment;
import amai.org.conventions.feedback.SurveySender;
import amai.org.conventions.map.MapActivity;
import amai.org.conventions.model.ConventionEvent;
import amai.org.conventions.model.ConventionMap;
import amai.org.conventions.model.FeedbackQuestion;
import amai.org.conventions.model.MapLocation;
import amai.org.conventions.model.Survey;
import amai.org.conventions.model.conventions.Convention;
import amai.org.conventions.navigation.NavigationActivity;
import amai.org.conventions.networking.SurveyDataRetriever;
import amai.org.conventions.notifications.PushNotificationTopicsSubscriber;
import amai.org.conventions.utils.BundleBuilder;
import amai.org.conventions.utils.CollectionUtils;
import amai.org.conventions.utils.Dates;
import amai.org.conventions.utils.Log;
import amai.org.conventions.utils.StateList;
import amai.org.conventions.utils.Views;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;

public class EventActivity extends NavigationActivity {

	public static final String EXTRA_EVENT_ID = "EventIdExtra";
	public static final String EXTRA_FOCUS_ON_FEEDBACK = "ExtraFocusOnFeedback";

	private static final String TAG = EventActivity.class.getCanonicalName();
	private static final String STATE_FEEDBACK_OPEN = "StateFeedbackOpen";

	private View mainLayout;
	private ConventionEvent conventionEvent;
	private LinearLayout imagesLayout;
	private FrameLayoutWithState feedbackContainer;
	private View voteSurveyOpenerContainer;
	private View voteSurveyOpenerLayout;
	private Button voteSurveyOpener;
	private ProgressBar voteSurveyOpenerProgress;
	private View viewEventLayout;
	private Button viewEventButton;
	private CollapsibleFeedbackView feedbackView;

	private Menu menu;

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentInContentContainer(R.layout.activity_event);

		mainLayout = findViewById(R.id.event_main_layout);
		feedbackContainer = findViewById(R.id.event_feedback_container);
		voteSurveyOpenerContainer = findViewById(R.id.event_vote_opener_container);
		voteSurveyOpenerLayout = findViewById(R.id.event_vote_opener_layout);
		voteSurveyOpener = (Button) findViewById(R.id.event_open_vote_survey_button);
		voteSurveyOpenerProgress = (ProgressBar) findViewById(R.id.event_open_vote_survey_progress_bar);
		viewEventLayout = findViewById(R.id.event_view_layout);
		viewEventButton = findViewById(R.id.event_view_button);
		feedbackView = (CollapsibleFeedbackView) findViewById(R.id.event_feedback_view);
		final View detailBoxes = findViewById(R.id.event_detail_boxes);
		final NestedScrollView scrollView = findViewById(R.id.event_details_scroll);

		// Handle edge to edge
		Views.registerApplyInsets(Views.InsetType.NONE, Views.InsetType.PADDING, Views.InsetType.PADDING, Views.InsetType.PADDING, scrollView);

		String eventId = getIntent().getStringExtra(EXTRA_EVENT_ID);
		conventionEvent = Convention.getInstance().findEventById(eventId);
		if (conventionEvent == null) {
			Log.e(TAG, "Could not find event with id " + eventId);
			Toast.makeText(this, getString(R.string.event_not_found), Toast.LENGTH_LONG).show();
			finish();
			return;
		}

		setToolbarTitle(conventionEvent.getType().getDescription());

		setToolbarBackground(ThemeAttributes.getDrawable(this, R.attr.eventToolbarColor));
		setBackground(ThemeAttributes.getDrawable(this, R.attr.eventDetailsDefaultBackground));

		// In this activity we have many items in the navigation bar (including overflow menu). They create 2 problems with a centered title design:
		// 1. The code for centering the title based on the number of action items assumes there's no overflow menu.
		// 2. The amount of action items doesn't leave enough room for the title text.
		// In order to avoid these issues, we align the title to the start in this activity only.
		setToolbarGravity(Gravity.START | Gravity.CENTER_VERTICAL);

		final boolean shouldFocusOnFeedback = getIntent().getBooleanExtra(EXTRA_FOCUS_ON_FEEDBACK, false);

		// Do the rest after the layout loads, since loading all the assets of this activity (which include images) can get long, we want the activity to first
		// start, and the content to fade in (instead of the UI hanging until the image loading is done).
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				setEvent(conventionEvent);

				// If the feedback view already had saved state, restore it
				if (savedInstanceState != null && savedInstanceState.containsKey(STATE_FEEDBACK_OPEN)) {
					if (savedInstanceState.getBoolean(STATE_FEEDBACK_OPEN)) {
						feedbackView.setState(CollapsibleFeedbackView.State.Expanded, false);
					} else {
						feedbackView.setState(CollapsibleFeedbackView.State.Collapsed, false);
					}
				} else if (shouldFocusOnFeedback) {
					// If we want to focus on the feedback view, it has to be expanded
					if (feedbackView.getState() == CollapsibleFeedbackView.State.Collapsed) {
						feedbackView.setState(CollapsibleFeedbackView.State.Expanded, false);
					}

					scrollView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
						public void onGlobalLayout() {
							// Unregister the listener to only call smoothScrollTo once
							scrollView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
							scrollView.post(new Runnable() {
								@Override
								public void run() {
									Point coordinates = Views.findCoordinates(scrollView, feedbackView);
									scrollView.smoothScrollTo(coordinates.x, coordinates.y);
								}
							});
						}
					});
				}

				mainLayout.setVisibility(View.VISIBLE);
			}
		}, 50);

	}

	@Override
	protected void onResume() {
		super.onResume();
		// This view is time-sensitive (it is visible or invisible according to the time)
		// so it should be updated more frequently
		setupVoteSurveyAndEventView(conventionEvent);
	}

	@Override
	public boolean onCreateCustomOptionsMenu(Menu menu) {
		this.menu = menu;
		getMenuInflater().inflate(R.menu.menu_event, menu);

		ConventionEvent.UserInput userInput = conventionEvent.getUserInput();
		if (userInput.isAttending()) {
			MenuItem favoritesButton = menu.findItem(R.id.event_change_favorite_state);
			changeFavoriteMenuIcon(true, favoritesButton);
		}

		setupAlarmsMenuItem(menu, userInput);

		hideNavigateToMapButtonIfNoLocationExists(menu);

		hideShareEventIfNoWebsiteUrl(menu);

		return true;
	}

	private void setupAlarmsMenuItem(Menu menu, ConventionEvent.UserInput userInput) {
		// Remove alarms button for ended events (unless they still have a feedback reminder)
		if (conventionEvent.hasEnded() &&
				!(conventionEvent.getEventFeedbackReminderNotificationTime() != null &&
				  conventionEvent.getEventFeedbackReminderNotificationTime().after(Dates.now()))) {
			menu.removeItem(R.id.event_configure_notifications);
		// Hide alarms in the overflow menu if the event is over or there are no alarms for this event
		// and it isn't in the favorites
		} else if (userInput.isAttending() ||
				userInput.getEventAboutToStartNotification().isEnabled() ||
				userInput.getEventFeedbackReminderNotification().isEnabled()) {
			MenuItem alarmsItem = menu.findItem(R.id.event_configure_notifications);
			// We might have removed the notifications item when entering the screen. In that case, don't display it.
			if (alarmsItem != null) {
				alarmsItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
			}
		} else {
			MenuItem alarmsItem = menu.findItem(R.id.event_configure_notifications);
			if (alarmsItem != null) {
				alarmsItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
			}
		}
	}

	@Override
	public boolean onOptionsItemSelected(final MenuItem item) {
		switch (item.getItemId()) {
			case R.id.event_change_favorite_state:
				ConventionEvent.UserInput userInput = conventionEvent.getUserInput();

				// In the user is adding this event and it conflicts with another favorite event, ask the user what to do
				if ((!userInput.isAttending()) && Convention.getInstance().conflictsWithOtherFavoriteEvent(conventionEvent)) {
					final List<ConventionEvent> conflictingEvents = Convention.getInstance().getFavoriteConflictingEvents(conventionEvent);
					String message;
					if (conflictingEvents.size() == 1) {
						message = getString(R.string.event_conflicts_with_one_question, conflictingEvents.get(0).getTitle());
					} else {
						message = getString(R.string.event_conflicts_with_several_question, conflictingEvents.size());
					}
					new AlertDialog.Builder(this)
							.setTitle(R.string.event_add_to_favorites)
							.setMessage(message)
							.setPositiveButton(R.string.add_anyway, new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									changeEventFavoriteState(item);
								}
							})
							.setNegativeButton(R.string.cancel, null)
							.show();
					return true;
				}

				changeEventFavoriteState(item);
				return true;
			case R.id.event_navigate_to_map:
				// Navigate to the map floor associated with this event
				Bundle floorBundle = new Bundle();
				ConventionMap map = Convention.getInstance().getMap();
				List<MapLocation> locations = map.findLocationsByName(conventionEvent.getHall().getName());
				int[] locationIds = CollectionUtils.mapToInt(locations, MapLocation::getId);
				floorBundle.putIntArray(MapActivity.EXTRA_MAP_LOCATION_IDS, locationIds);

				navigateToActivity(MapActivity.class, false, floorBundle);
				return true;
			case R.id.event_share_event:
				// Share a link to the event
				Intent sendIntent = new Intent();
				sendIntent.setAction(Intent.ACTION_SEND);
				sendIntent.putExtra(Intent.EXTRA_TEXT, conventionEvent.getWebsiteUrl());
				sendIntent.setType("text/plain");

				Intent shareIntent = Intent.createChooser(sendIntent, null);
				startActivity(shareIntent);
				return true;
			case R.id.event_navigate_to_hall:
				// Navigate to the hall associated with this event
				Bundle bundle = new Bundle();
				bundle.putString(HallActivity.EXTRA_HALL_NAME, conventionEvent.getHall().getName());

				navigateToActivity(HallActivity.class, false, bundle);
				return true;
			case R.id.event_configure_notifications:

				ConfigureNotificationsFragment configureNotificationsFragment = ConfigureNotificationsFragment.newInstance(conventionEvent.getId());
				configureNotificationsFragment.show(getSupportFragmentManager(), null);

				FirebaseAnalytics
						.getInstance(this)
						.logEvent("event_notification", new BundleBuilder()
								.putString("action", "EditClicked")
								.build()
						);

				return true;
		}

		return super.onOptionsItemSelected(item);
	}

	private void changeEventFavoriteState(MenuItem item) {
		ConventionEvent.UserInput userInput = conventionEvent.getUserInput();
		FirebaseAnalytics
				.getInstance(this)
				.logEvent("favorites_" + (!userInput.isAttending() ? "added" : "removed"), null);

		if (userInput.isAttending()) {
			userInput.setAttending(false);
			ConventionsApplication.alarmScheduler.cancelDefaultEventAlarms(conventionEvent);
			changeFavoriteMenuIcon(false, item);
			Snackbar.make(this.mainLayout, R.string.event_removed_from_favorites, Snackbar.LENGTH_SHORT).show();
		} else {
			userInput.setAttending(true);
			ConventionsApplication.alarmScheduler.scheduleDefaultEventAlarms(conventionEvent);
			changeFavoriteMenuIcon(true, item);
			Snackbar.make(this.mainLayout, R.string.event_added_to_favorites, Snackbar.LENGTH_SHORT).show();
		}
		setupAlarmsMenuItem(menu, userInput);
		saveUserInput();

		// Not that the event favorite state got updated, update the push notification subscription state
		if (conventionEvent.isAttending()) {
			PushNotificationTopicsSubscriber.subscribe(conventionEvent);
		} else {
			PushNotificationTopicsSubscriber.unsubscribe(conventionEvent);
		}
	}

	private void changeFavoriteMenuIcon(boolean isAttending, MenuItem favoriteItem) {
		if (isAttending) {
			Drawable icon = ContextCompat.getDrawable(this, R.drawable.ic_star_black_24dp);
			icon.mutate().setColorFilter(ThemeAttributes.getColor(this, R.attr.eventActivityFavoriteColor), PorterDuff.Mode.SRC_ATOP);
			favoriteItem.setIcon(icon);
			favoriteItem.setTitle(getResources().getString(R.string.event_remove_from_favorites));
		} else {
			favoriteItem.setIcon(ThemeAttributes.getDrawable(this, R.attr.iconAddToFavorites));
			favoriteItem.setTitle(getResources().getString(R.string.event_add_to_favorites));
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (feedbackView.isFeedbackChanged()) {
			saveUserInput();
		}
	}

	private void saveUserInput() {
		Convention.getInstance().getStorage().saveUserInput();
		conventionEvent.getUserInput().getFeedback().resetChangedAnswers();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		if (feedbackContainer.getVisibility() == View.VISIBLE) {
			outState.putBoolean(STATE_FEEDBACK_OPEN, feedbackView.getState() == CollapsibleFeedbackView.State.Expanded);
		}

		super.onSaveInstanceState(outState);
	}

	private void hideNavigateToMapButtonIfNoLocationExists(Menu menu) {
		ConventionMap map = Convention.getInstance().getMap();
		List<MapLocation> locations = map.findLocationsByName(conventionEvent.getHall().getName());
		if (locations.size() == 0) {
			menu.findItem(R.id.event_navigate_to_map).setVisible(false);
		}
	}

	private void hideShareEventIfNoWebsiteUrl(Menu menu) {
		if (conventionEvent.getWebsiteUrl() == null || conventionEvent.getWebsiteUrl().isEmpty()) {
			menu.findItem(R.id.event_share_event).setVisible(false);
		}
	}

	private void setEvent(ConventionEvent event) {
		boolean showLocationTypeSeparator = true;
		TextView type = (TextView) findViewById(R.id.event_type);
		if (event.getType() == null || TextUtils.isEmpty(event.getType().getDescription())) {
			showLocationTypeSeparator = false;
			type.setVisibility(View.GONE);
		} else {
			type.setText(event.getType().getDescription());
		}

		TextView locationType = findViewById(R.id.event_location_type);
		List<ConventionEvent.EventLocationType> eventLocationTypes = Convention.getInstance().getEventLocationTypes(event);
		if (eventLocationTypes != null && eventLocationTypes.size() > 0 && Convention.getInstance().getEventLocationTypes().size() > 1) {
			if (eventLocationTypes.size() > 1) {
				locationType.setText(R.string.hybrid_event);
			} else {
				locationType.setText(eventLocationTypes.get(0).getDescriptionStringId());
			}
		} else {
			showLocationTypeSeparator = false;
			locationType.setVisibility(View.GONE);
		}

		if (!showLocationTypeSeparator) {
			findViewById(R.id.event_type_and_location_type_separator).setVisibility(View.GONE);
		}

		TextView title = (TextView) findViewById(R.id.event_title);
		title.setText(event.getTitle());

		TextView subtitle = findViewById(R.id.event_subtitle);
		if (event.getSubTitle() == null || event.getSubTitle().isEmpty()) {
			subtitle.setVisibility(View.GONE);
		} else {
			subtitle.setText(event.getSubTitle());
		}

		TextView lecturerName = (TextView) findViewById(R.id.event_lecturer);
		String lecturer = event.getLecturer();
		if (lecturer == null || lecturer.length() == 0) {
			lecturerName.setVisibility(View.GONE);
		} else {
			lecturerName.setText(lecturer);
		}

		TextView hallAndTime = (TextView) findViewById(R.id.event_hall_and_time);

		// The unicode characters make sure the start time is printed before the end time
		// (U+2067 is RIGHT-TO-LEFT ISOLATE and U+2069 ends its scope). See https://unicode.org/reports/tr9/
		String formattedEventTime = String.format("%s%s\u2067–\u2069%s (%s)",
				// In case of single day convention, don't show the date
				Convention.getInstance().getLengthInDays() == 1 ? "" : Dates.formatDate("EEE dd.MM, ", event.getStartTime()),
				Dates.formatHoursAndMinutes(event.getStartTime()),
				Dates.formatHoursAndMinutes(event.getEndTime()),
				Dates.toHumanReadableTimeDuration(event.getEndTime().getTime() - event.getStartTime().getTime()));

		if (event.getHall() != null) {
			formattedEventTime = event.getHall().getName() + ", " + formattedEventTime;
		}

		hallAndTime.setText(formattedEventTime);

		TextView additionalInfo = (TextView) findViewById(R.id.event_additional_info);
		String eventAdditionalInfo = Convention.getInstance().getEventAdditionalInfo(event, this);
		if (eventAdditionalInfo == null || eventAdditionalInfo.isEmpty()) {
			additionalInfo.setVisibility(View.GONE);
		} else {
			additionalInfo.setText(eventAdditionalInfo);
		}

		TextView tags = (TextView) findViewById(R.id.event_tags);
		View tagsSeparator = findViewById(R.id.event_tags_separator);
		List<String> eventTags = event.getTags();
		if (eventTags == null || eventTags.size() == 0) {
			tags.setVisibility(View.GONE);
			tagsSeparator.setVisibility(View.GONE);
		} else {
			tags.setText(Html.fromHtml(getString(R.string.tags_formatted, event.getTagsAsString())));
		}

		setupFeedback(event);

		setupEventDescription(event);

		setupLogoImage(event);

	}

	private boolean shouldShowEventSurvey(ConventionEvent event) {
		final Survey voteSurvey = event.getUserInput().getVoteSurvey();
		if (voteSurvey == null) {
			return false;
		}

		Calendar eventSurveyEnd = Calendar.getInstance();
		eventSurveyEnd.setTime(event.getEndTime());

		// Allow until some time after the event ended to vote (in case there's a delay in the schedule)
		eventSurveyEnd.add(Calendar.HOUR, 2);

		return event.hasStarted() && (eventSurveyEnd.getTime().after(Dates.now()) || voteSurvey.isSent());
	}

	private boolean shouldShowViewEvent(ConventionEvent event) {
		URL eventViewURL = Convention.getInstance().getEventViewURL(event);
		if (eventViewURL == null) {
			return false;
		}
		try {
			eventViewURL.toURI();
		} catch (Exception e) {
			Log.e(TAG, "Event URL cannot be parsed to URI: " + eventViewURL, e);
		}
		// We only show the button while the event is on
		return event.hasStarted() && !event.hasEnded();
	}

	private void setupVoteSurveyAndEventView(ConventionEvent event) {
		boolean shouldShowSurvey = shouldShowEventSurvey(event);
		boolean shouldShowView = shouldShowViewEvent(event);

		if (!shouldShowSurvey && !shouldShowView) {
			voteSurveyOpenerContainer.setVisibility(View.GONE);
			return;
		} else {
			voteSurveyOpenerContainer.setVisibility(View.VISIBLE);
		}

		// Setup votes
		if (!shouldShowSurvey) {
			voteSurveyOpenerLayout.setVisibility(View.GONE);
		} else {
			final Survey voteSurvey = event.getUserInput().getVoteSurvey();
			voteSurveyOpenerLayout.setVisibility(View.VISIBLE);
			voteSurveyOpenerProgress.setVisibility(View.GONE);
			voteSurveyOpener.setVisibility(View.VISIBLE);
			voteSurveyOpener.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					voteSurveyOpener.setVisibility(View.GONE);
					voteSurveyOpenerProgress.setVisibility(View.VISIBLE);
					new AsyncTask<Void, Void, Exception>() {
						@Override
						protected Exception doInBackground(Void... params) {
							// No need to retrieve the answers if the user already sent the vote
							if (voteSurvey.isSent()) {
								return null;
							}
							try {
								for (FeedbackQuestion question : voteSurvey.getQuestions()) {
									SurveyDataRetriever.Answers retriever = Convention.getInstance().createSurveyAnswersRetriever(question);
									if (retriever != null) {
										List<String> answers = retriever.retrieveAnswers();
										if (answers == null || answers.size() == 0) {
											// No answers found - throw exception
											throw new RuntimeException("No answers found for question " + question.getQuestionId());
										}
										question.setPossibleMultipleAnswers(answers);
									}
								}
								// In case everything finished successfully, pass null to onPostExecute.
								return null;
							} catch (Exception e) {
								return e;
							}
						}

						@Override
						protected void onPostExecute(Exception exception) {
							voteSurveyOpenerProgress.setVisibility(View.GONE);
							voteSurveyOpener.setVisibility(View.VISIBLE);
							if (exception != null) {
								Toast.makeText(EventActivity.this, R.string.vote_survey_retrieve_answers_error, Toast.LENGTH_LONG).show();
								Log.e(TAG, "Error retrieving answers", exception);
								FirebaseAnalytics
										.getInstance(EventActivity.this)
										.logEvent("vote", new BundleBuilder()
												.putString("success", "false")
												.putString("error_message", exception.getMessage())
												.build()
										);
							} else {
								FirebaseAnalytics
										.getInstance(EventActivity.this)
										.logEvent("vote", new BundleBuilder()
												.putString("success", "true")
												.putString("event_title", conventionEvent.getTitle())
												.build()
										);
								if (!getSupportFragmentManager().isStateSaved()) {
									EventVoteSurveyFragment eventVoteSurveyFragment = EventVoteSurveyFragment.newInstance(conventionEvent.getId());
									eventVoteSurveyFragment.show(getSupportFragmentManager(), null);
								}
							}
						}
					}.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
				}
			});
		}

		// Setup event view
		if (!shouldShowView) {
			viewEventLayout.setVisibility(View.GONE);
		} else {
			URL eventViewURL = Convention.getInstance().getEventViewURL(event);
			viewEventLayout.setVisibility(View.VISIBLE);
			viewEventButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					try {
						Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(eventViewURL.toURI().toString()));
						try {
							EventActivity.this.startActivity(intent);
						} catch (ActivityNotFoundException e) {
							Toast.makeText(EventActivity.this, getString(R.string.view_event_error), Toast.LENGTH_LONG).show();
						}
					} catch (URISyntaxException e) {
						// We already checked this before
					}
				}
			});
		}
	}

	private void setupFeedback(ConventionEvent event) {
		if (event.canFillFeedback()) {
			feedbackContainer.setVisibility(View.VISIBLE);

			// This should be called before setModel for the color states to be correct
			if (shouldFeedbackBeClosed()) {
				feedbackView.setState(CollapsibleFeedbackView.State.Collapsed, false);
			} else {
				feedbackView.setState(CollapsibleFeedbackView.State.Expanded, false);
			}

			feedbackView.setAdditionalFeedbackURL(Convention.getInstance().getAdditionalEventFeedbackURL(event));
			feedbackView.setModel(event.getUserInput().getFeedback());

			feedbackView.setSendFeedbackClickListener(feedbackView.new CollapsibleFeedbackViewSendListener() {
				@Override
				protected void saveFeedback() {
					saveUserInput();
				}

				@Override
				protected SurveySender getSurveySender() {
					return Convention.getInstance().getEventFeedbackSender(conventionEvent);
				}

				@Override
				protected void onSuccess() {
					super.onSuccess();
					feedbackView.setState(CollapsibleFeedbackView.State.Collapsed, true);
					updateFeedbackState(event);
				}
			});

			feedbackView.setOnStateChangedListener(() -> {
				updateFeedbackState(event);
			});

			updateFeedbackState(event);
		} else {
			feedbackContainer.setVisibility(View.GONE);
		}
	}

	private void updateFeedbackState(ConventionEvent event) {
		StateList states = new StateList();
		Survey feedback = event.getUserInput().getFeedback();
		if (feedback.isSent()) {
			states.add(R.attr.state_event_feedback_sent);
		}
		if (!Convention.getInstance().isFeedbackSendingTimeOver()) {
			states.add(R.attr.state_event_feedback_can_send);
		}

		if (feedbackView.getState() == CollapsibleFeedbackView.State.Collapsed) {
			states.add(R.attr.state_feedback_collapsed);
		}
		states.setForView(feedbackContainer);
	}

	private boolean shouldFeedbackBeClosed() {
		// Feedback should start as closed in the following cases:
		// 1. The user shouldn't see the feedback (according to the logic in ConventionEvent)
		// 2. Feedback sending time is over and no questions were answered
		// Otherwise it should start as open.
		return !conventionEvent.shouldUserSeeFeedback() ||
				(Convention.getInstance().isFeedbackSendingTimeOver() &&
						!conventionEvent.getUserInput().getFeedback().hasAnsweredQuestions());
	}

	private void setupEventDescription(ConventionEvent event) {
		String eventDescription = event.getDescription();
		if (eventDescription == null || eventDescription.isEmpty() || event.getPlainTextDescription().trim().isEmpty()) {
			findViewById(R.id.event_description_box).setVisibility(View.GONE);
		} else {
			// Enable internal links from HTML <a> tags within the description textView.
			TextView description = (TextView) findViewById(R.id.event_description);
			description.setMovementMethod(LinkMovementMethod.getInstance());
			Spanned spanned = event.getSpannedDescription();
			description.setText(spanned);
		}
	}

	private void setupLogoImage(ConventionEvent event) {
		ImageView logoImageView = (ImageView) findViewById(R.id.event_logo);
		List<Integer> images = event.getLogoImageResources();
		if (images.size() == 0) {
			logoImageView.setVisibility(View.GONE);
		} else {
			logoImageView.setVisibility(View.VISIBLE);
			logoImageView.setImageResource(images.get(0));
			if (BuildConfig.DEBUG && images.size() > 1) {
				Log.e(TAG, images.size() + " logo images found in event " + event.getTitle() + "; using first image");
			}
		}
	}

	public void openFeedback(View view) {
		feedbackView.setState(CollapsibleFeedbackView.State.Expanded);
	}

	public void closeFeedback(View view) {
		feedbackView.setState(CollapsibleFeedbackView.State.Collapsed);
	}

}

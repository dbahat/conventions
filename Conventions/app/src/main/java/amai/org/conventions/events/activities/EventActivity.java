package amai.org.conventions.events.activities;

import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v7.graphics.Palette;
import android.text.Html;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;

import java.util.List;

import amai.org.conventions.ConventionsApplication;
import amai.org.conventions.R;
import amai.org.conventions.customviews.AspectRatioImageView;
import amai.org.conventions.events.CollapsibleFeedbackView;
import amai.org.conventions.events.ConfigureNotificationsFragment;
import amai.org.conventions.map.MapActivity;
import amai.org.conventions.model.Convention;
import amai.org.conventions.model.ConventionEvent;
import amai.org.conventions.model.ConventionMap;
import amai.org.conventions.model.Feedback;
import amai.org.conventions.model.MapLocation;
import amai.org.conventions.navigation.NavigationActivity;
import amai.org.conventions.utils.Dates;
import amai.org.conventions.utils.EventFeedbackMail;
import amai.org.conventions.utils.FeedbackMail;
import amai.org.conventions.utils.Log;
import amai.org.conventions.utils.Views;
import fi.iki.kuitsi.listtest.ListTagHandler;
import uk.co.chrisjenx.paralloid.views.ParallaxScrollView;


public class EventActivity extends NavigationActivity {

    public static final String EXTRA_EVENT_ID = "EventIdExtra";
	public static final String EXTRA_FOCUS_ON_FEEDBACK = "ExtraFocusOnFeedback";

    private static final String TAG = EventActivity.class.getCanonicalName();
    private static final String STATE_FEEDBACK_OPEN = "StateFeedbackOpen";

	private View mainLayout;
    private ConventionEvent conventionEvent;
    private LinearLayout imagesLayout;
	private LinearLayout feedbackContainer;
    private CollapsibleFeedbackView feedbackView;

	private AspectRatioImageView fadingImageView;
	private Menu menu;

	@Override
    protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentInContentContainer(R.layout.activity_event);

		mainLayout = findViewById(R.id.event_main_layout);
		imagesLayout = (LinearLayout) findViewById(R.id.images_layout);
		feedbackContainer = (LinearLayout) findViewById(R.id.event_feedback_container);
		feedbackView = (CollapsibleFeedbackView) findViewById(R.id.event_feedback_view);
		final View detailBoxes = findViewById(R.id.event_detail_boxes);
		final View backgroundView = imagesLayout;
		final ParallaxScrollView scrollView = (ParallaxScrollView) findViewById(R.id.parallax_scroll);

		String eventId = getIntent().getStringExtra(EXTRA_EVENT_ID);
		conventionEvent = Convention.getInstance().findEventById(eventId);
		setToolbarTitle(conventionEvent.getType().getDescription());

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

				mainLayout.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
					@Override
					public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
						// Set parallax
						int foregroundHeight = detailBoxes.getMeasuredHeight();
						int backgroundHeight = backgroundView.getMeasuredHeight();
						int screenHeight = mainLayout.getMeasuredHeight();
						float maxParallax = 1;

						// If background height is bigger than screen size, scrolling should be until background full height is reached.
						// If it's smaller, scrolling should be until background is scrolled out of the screen.
						int backgroundToScroll;
						if (backgroundHeight < screenHeight) {
							backgroundToScroll = backgroundHeight;
							maxParallax = 0.7f;
						} else {
							if (fadingImageView != null) {
								fadingImageView.setBottomFadingEdgeEnabled(false);
							}
							backgroundToScroll = backgroundHeight - screenHeight;

							// If foreground height is smaller than background height (and background should be scrolled),
							// increase foreground height to allow scrolling until the end of the background and see all the images.
							if (backgroundToScroll > 0 && foregroundHeight < backgroundHeight) {
								detailBoxes.setMinimumHeight(backgroundHeight);
								// Update height to calculate the parallax factor
								foregroundHeight = backgroundHeight;
							}
						}
						int foregroundToScroll = foregroundHeight - screenHeight;

						// Set parallax scrolling if both foreground and background should be scrolled
						if (backgroundToScroll > 0 && foregroundToScroll > 0) {
							float scrollFactor = backgroundToScroll / (float) foregroundToScroll;
							// If scroll factor is bigger than 1, set it to 1 so the background doesn't move too fast.
							// This could happen only in case the background is smaller than screen size so we can
							// still see all the images.
							scrollView.parallaxViewBy(backgroundView, Math.min(scrollFactor, maxParallax));
						}
					}
				});

				// Set images background color according to last image's color palette
				if (imagesLayout.getChildCount() > 0) {
					final View imagesBackground = findViewById(R.id.images_background);
					ImageView lastImage = (ImageView) imagesLayout.getChildAt(imagesLayout.getChildCount() - 1);
					if (lastImage.getDrawable() instanceof BitmapDrawable) {
						Bitmap bitmap = ((BitmapDrawable) lastImage.getDrawable()).getBitmap();

						Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
							@Override
							public void onGenerated(Palette palette) {
								Palette.Swatch swatch = palette.getMutedSwatch();
								if (swatch == null) {
									// Try vibrant swatch
									swatch = palette.getDarkVibrantSwatch();
								}
								if (swatch != null) {
									imagesBackground.setBackgroundColor(swatch.getRgb());
								}
							}
						});
					}
				}

				mainLayout.setVisibility(View.VISIBLE);
				mainLayout.startAnimation(AnimationUtils.loadAnimation(EventActivity.this, android.R.anim.fade_in));
			}
		}, 50);

	}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
	    this.menu = menu;
	    getMenuInflater().inflate(R.menu.menu_event, menu);

	    ConventionEvent.UserInput userInput = conventionEvent.getUserInput();
	    if (userInput.isAttending()) {
            MenuItem favoritesButton = menu.findItem(R.id.event_change_favorite_state);
            favoritesButton.setIcon(getResources().getDrawable(android.R.drawable.btn_star_big_on));
        }

	    setupAlarmsMenuItem(menu, userInput);

	    hideNavigateToMapButtonIfNoLocationExists(menu);

        return true;
    }

	private void setupAlarmsMenuItem(Menu menu, ConventionEvent.UserInput userInput) {
		// Remove alarms button for ended events (unless they still have a feedback reminder)
		if (conventionEvent.hasEnded() && !userInput.getEventFeedbackReminderNotification().isEnabled()) {
			menu.removeItem(R.id.event_configure_notifications);
		// Hide alarms in the overflow menu if the event is over or there are no alarms for this event
		// and it isn't in the favorites
		} else if (userInput.isAttending() ||
				userInput.getEventAboutToStartNotification().isEnabled() ||
				userInput.getEventFeedbackReminderNotification().isEnabled()) {
		    MenuItem alarmsItem = menu.findItem(R.id.event_configure_notifications);
			alarmsItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		} else {
			MenuItem alarmsItem = menu.findItem(R.id.event_configure_notifications);
			alarmsItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
		}
	}

	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.event_change_favorite_state:

				ConventionEvent.UserInput userInput = conventionEvent.getUserInput();

				ConventionsApplication.tracker.send(new HitBuilders.EventBuilder()
						.setCategory("Favorites")
						.setAction(!userInput.isAttending() ? "Add" : "Remove")
						.setLabel("EventActivity")
						.build());

	            if (userInput.isAttending()) {
                    userInput.setAttending(false);
					ConventionsApplication.alarmScheduler.cancelDefaultEventAlarms(conventionEvent);
                    item.setIcon(getResources().getDrawable(R.drawable.star_with_plus));
                    item.setTitle(getResources().getString(R.string.event_add_to_favorites));
                    Snackbar.make(this.mainLayout, R.string.event_removed_from_favorites, Snackbar.LENGTH_SHORT).show();
                } else {
                    userInput.setAttending(true);
					ConventionsApplication.alarmScheduler.scheduleDefaultEventAlarms(conventionEvent);
                    item.setIcon(getResources().getDrawable(android.R.drawable.btn_star_big_on));
                    item.setTitle(getResources().getString(R.string.event_remove_from_favorites));
                    Snackbar.make(this.mainLayout, R.string.event_added_to_favorites, Snackbar.LENGTH_SHORT).show();
                }
	            setupAlarmsMenuItem(menu, userInput);
                saveUserInput();
                return true;
            case R.id.event_navigate_to_map:
                // Navigate to the map floor associated with this event
                Bundle floorBundle = new Bundle();
                ConventionMap map = Convention.getInstance().getMap();
                List<MapLocation> locations = map.findLocationsByHall(conventionEvent.getHall());
                MapLocation location = map.findClosestLocation(locations);
                if (location != null) {
                    floorBundle.putInt(MapActivity.EXTRA_MAP_LOCATION_ID, location.getId());
                }

                navigateToActivity(MapActivity.class, false, floorBundle);
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

				ConventionsApplication.tracker.send(new HitBuilders.EventBuilder()
						.setCategory("Notifications")
						.setAction("EditClicked")
						.build());

				return true;
        }

        return super.onOptionsItemSelected(item);
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
        List<MapLocation> locations = map.findLocationsByHall(conventionEvent.getHall());
        MapLocation location = map.findClosestLocation(locations);
        if (location == null) {
            menu.findItem(R.id.event_navigate_to_map).setVisible(false);
        }
    }

    private void setEvent(ConventionEvent event) {
        TextView title = (TextView) findViewById(R.id.event_title);
        title.setText(event.getTitle());

        TextView lecturerName = (TextView) findViewById(R.id.event_lecturer);
        String lecturer = event.getLecturer();
        if (lecturer == null || lecturer.length() == 0) {
            lecturerName.setVisibility(View.GONE);
        } else {
            lecturerName.setText(lecturer);
        }

        TextView time = (TextView) findViewById(R.id.event_hall_and_time);

	    String formattedEventHall = "";
	    if (event.getHall() != null) {
		    formattedEventHall = String.format("%s, ", event.getHall().getName());
	    }

        String formattedEventTime = String.format("%s - %s (%s)",
                Dates.formatHoursAndMinutes(event.getStartTime()),
                Dates.formatHoursAndMinutes(event.getEndTime()),
                Dates.toHumanReadableTimeDuration(event.getEndTime().getTime() - event.getStartTime().getTime()));
        time.setText(formattedEventHall + formattedEventTime);

	    setupFeedback(event, false);

	    setupEventDescription(event);

        setupBackgroundImages(event);

    }

	private void setupFeedback(ConventionEvent event, boolean animate) {
		if (event.canFillFeedback()) {
			feedbackContainer.setVisibility(View.VISIBLE);
	        feedbackView.setModel(event.getUserInput().getFeedback());

			if (shouldFeedbackBeClosed()) {
				feedbackView.setState(CollapsibleFeedbackView.State.Collapsed, animate);
			} else {
				feedbackView.setState(CollapsibleFeedbackView.State.Expanded, animate);
			}

			feedbackView.setSendFeedbackClickListener(feedbackView.new CollapsibleFeedbackViewSendMailListener() {
				@Override
				protected void saveFeedback() {
					saveUserInput();
				}

				@Override
				protected FeedbackMail getFeedbackMail() {
					return new EventFeedbackMail(EventActivity.this, conventionEvent);
				}

				@Override
				protected void onFailure(Exception exception) {
					Log.w(TAG, "Failed to send feedback mail. Reason: " + exception.getMessage());
					Toast.makeText(EventActivity.this, R.string.feedback_send_mail_failed, Toast.LENGTH_LONG).show();
					sendUserSentFeedbackTelemetry(false);
				}

				@Override
				protected void onSuccess() {
					super.onSuccess();
					feedbackView.setState(CollapsibleFeedbackView.State.Collapsed, true);
					sendUserSentFeedbackTelemetry(true);
				}

				private void sendUserSentFeedbackTelemetry(boolean success) {
					ConventionsApplication.tracker.send(new HitBuilders.EventBuilder()
							.setCategory("Feedback")
							.setAction("SendAttempt")
							.setLabel(success ? "success" : "failure")
							.build());
				}
			});
		} else {
			feedbackContainer.setVisibility(View.GONE);
		}
	}


	private boolean shouldFeedbackBeClosed() {
		// Feedback should start as closed in the following cases:
		// 1. Feedback was sent
		// 2. Event was not attended an no questions were answered
		// Otherwise it should start as open.
		Feedback feedback = conventionEvent.getUserInput().getFeedback();
		return feedback.isSent() ||
				(!conventionEvent.isAttending() && !feedback.hasAnsweredQuestions());
	}


	private void setupEventDescription(ConventionEvent event) {
		String eventDescription = event.getDescription();
		if (eventDescription == null || eventDescription.isEmpty()) {
			findViewById(R.id.event_description_box).setVisibility(View.GONE);
		} else {
			// Enable internal links from HTML <a> tags within the description textView.
			TextView description = (TextView) findViewById(R.id.event_description);
			description.setMovementMethod(LinkMovementMethod.getInstance());

			Spanned spanned = Html.fromHtml(eventDescription, null, new ListTagHandler());
			description.setText(spanned);
		}
	}

	private void setupBackgroundImages(ConventionEvent event) {
        // Add images to the layout
        List<Integer> images = event.getImages();
        boolean first = true;
		// This will contain the last image view after the loop
		AspectRatioImageView imageView = null;
        for (int imageId : images) {
			imageView = new AspectRatioImageView(this);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            int topMargin = 0;
            if (first) {
                first = false;
            } else {
                topMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, getResources().getDisplayMetrics());
            }
            layoutParams.setMargins(0, topMargin, 0, 0);
            imageView.setLayoutParams(layoutParams);
            imageView.setImageResource(imageId);
            imagesLayout.addView(imageView);
        }

		if (imageView != null) {
			fadingImageView = imageView;
			fadingImageView.setBottomFadingEdgeEnabled(true);
			fadingImageView.setFadingEdgeLength((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 40, getResources().getDisplayMetrics()));
		}
    }

    public void openFeedback(View view) {
        feedbackView.setState(CollapsibleFeedbackView.State.Expanded);
    }

    public void closeFeedback(View view) {
        feedbackView.setState(CollapsibleFeedbackView.State.Collapsed);
    }

}

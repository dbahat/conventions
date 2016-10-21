package amai.org.conventions.events.activities;

import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.graphics.Palette;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.view.Display;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.analytics.HitBuilders;

import java.text.SimpleDateFormat;
import java.util.List;

import amai.org.conventions.ConventionsApplication;
import amai.org.conventions.ThemeAttributes;
import amai.org.conventions.customviews.AspectRatioImageView;
import amai.org.conventions.events.CollapsibleFeedbackView;
import amai.org.conventions.events.ConfigureNotificationsFragment;
import amai.org.conventions.map.MapActivity;
import amai.org.conventions.model.ConventionEvent;
import amai.org.conventions.model.ConventionMap;
import amai.org.conventions.model.MapLocation;
import amai.org.conventions.model.conventions.Convention;
import amai.org.conventions.navigation.NavigationActivity;
import amai.org.conventions.utils.Dates;
import amai.org.conventions.utils.EventFeedbackMail;
import amai.org.conventions.utils.FeedbackMail;
import amai.org.conventions.utils.Views;
import fi.iki.kuitsi.listtest.ListTagHandler;
import sff.org.conventions.R;
import uk.co.chrisjenx.paralloid.views.ParallaxScrollView;


public class EventActivity extends NavigationActivity {

    public static final String EXTRA_EVENT_ID = "EventIdExtra";
	public static final String EXTRA_FOCUS_ON_FEEDBACK = "ExtraFocusOnFeedback";

    private static final String STATE_FEEDBACK_OPEN = "StateFeedbackOpen";

	private View mainLayout;
    private ConventionEvent conventionEvent;
    private LinearLayout imagesLayout;
	private LinearLayout feedbackContainer;
    private CollapsibleFeedbackView feedbackView;

	private ImageView gradientImageView;
	private ImageView lastImageView;
	private Menu menu;
	private View imagesBackground;

	@Override
    protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentInContentContainer(R.layout.activity_event);
		setBackgroundColor(ThemeAttributes.getColor(this, R.attr.eventDetailsDefaultBackgroundColor));

		mainLayout = findViewById(R.id.event_main_layout);
		imagesBackground = findViewById(R.id.images_background);
		imagesLayout = (LinearLayout) findViewById(R.id.images_layout);
		feedbackContainer = (LinearLayout) findViewById(R.id.event_feedback_container);
		feedbackView = (CollapsibleFeedbackView) findViewById(R.id.event_feedback_view);
		final View detailBoxes = findViewById(R.id.event_detail_boxes);
		final ParallaxScrollView scrollView = (ParallaxScrollView) findViewById(R.id.parallax_scroll);

		String eventId = getIntent().getStringExtra(EXTRA_EVENT_ID);
		conventionEvent = Convention.getInstance().findEventById(eventId);
		if (conventionEvent == null) {
			throw new RuntimeException("Could not find event with id " + eventId);
		}

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
						int backgroundHeight = imagesLayout.getMeasuredHeight();
						int screenHeight = mainLayout.getMeasuredHeight();
						float maxParallax = 1;

						// If background height is bigger than screen size, scrolling should be until background full height is reached.
						// If it's smaller, scrolling should be until background is scrolled out of the screen.
						int backgroundToScroll;
						if (backgroundHeight < screenHeight) {
							backgroundToScroll = backgroundHeight;
							maxParallax = 0.7f;
							enableFadingEffect();
						} else {
							disableFadingEffect();
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
							scrollView.parallaxViewBy(imagesLayout, Math.min(scrollFactor, maxParallax));
						}
					}
				});

				// Set images background color according to last image's color palette
				if (lastImageView != null) {
					if (lastImageView.getDrawable() instanceof BitmapDrawable) {
						Bitmap bitmap = ((BitmapDrawable) lastImageView.getDrawable()).getBitmap();

						Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
							@Override
							public void onGenerated(Palette palette) {
								Palette.Swatch swatch = palette.getMutedSwatch();
								if (swatch == null) {
									// Try vibrant swatch
									swatch = palette.getDarkVibrantSwatch();
								}
								if (swatch != null) {
									updateBackgroundColor(swatch.getRgb());
								} else {
									// Set default background
									updateBackgroundColor(ThemeAttributes.getColor(EventActivity.this, android.R.attr.colorBackground));
								}

								// Fade in the images
								Animation fadeIn = AnimationUtils.loadAnimation(EventActivity.this, R.anim.fade_in);
								fadeIn.setAnimationListener(new Animation.AnimationListener() {
									@Override
									public void onAnimationStart(Animation animation) {
									}
									@Override
									public void onAnimationEnd(Animation animation) {
										// Replace default background with images layout
										imagesLayout.setVisibility(View.VISIBLE);
										removeBackground();
									}
									@Override
									public void onAnimationRepeat(Animation animation) {

									}
								});
								imagesLayout.startAnimation(fadeIn);
							}
						});
					}
				}

				mainLayout.setVisibility(View.VISIBLE);
				mainLayout.startAnimation(AnimationUtils.loadAnimation(EventActivity.this, android.R.anim.fade_in));
			}
		}, 50);

	}

	private int getBackgroundColor() {
		return ((ColorDrawable) imagesBackground.getBackground()).getColor();
	}

	private void updateBackgroundColor(int newColor) {
		imagesBackground.setBackgroundColor(newColor);
		if (gradientImageView != null) {
			((GradientDrawable) gradientImageView.getDrawable()).setColors(new int[]{Color.TRANSPARENT, newColor});
		}
	}

	private void enableFadingEffect() {
		if (gradientImageView != null) {
			((GradientDrawable) gradientImageView.getDrawable()).setColors(new int[]{Color.TRANSPARENT, getBackgroundColor()});
			gradientImageView.setVisibility(View.VISIBLE);
		}
	}

	private void disableFadingEffect() {
		if (gradientImageView != null) {
			gradientImageView.setVisibility(View.GONE);
		}
	}

	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
	    this.menu = menu;
	    getMenuInflater().inflate(R.menu.menu_event, menu);

	    ConventionEvent.UserInput userInput = conventionEvent.getUserInput();
	    if (userInput.isAttending()) {
            MenuItem favoritesButton = menu.findItem(R.id.event_change_favorite_state);
            favoritesButton.setIcon(ContextCompat.getDrawable(this, android.R.drawable.btn_star_big_on));
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
                List<MapLocation> locations = map.findLocationsByHall(conventionEvent.getHall());
	            int[] locationIds = new int[locations.size()];
	            int i = 0;
	            for (MapLocation location : locations) {
		            locationIds[i] = location.getId();
		            ++i;
	            }
                floorBundle.putIntArray(MapActivity.EXTRA_MAP_LOCATION_IDS, locationIds);

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

				ConventionsApplication.sendTrackingEvent(new HitBuilders.EventBuilder()
						.setCategory("Notifications")
						.setAction("EditClicked")
						.build());

				return true;
        }

        return super.onOptionsItemSelected(item);
    }

	private void changeEventFavoriteState(MenuItem item) {
		ConventionEvent.UserInput userInput = conventionEvent.getUserInput();
		ConventionsApplication.sendTrackingEvent(new HitBuilders.EventBuilder()
				.setCategory("Favorites")
				.setAction(!userInput.isAttending() ? "Add" : "Remove")
				.setLabel("EventActivity")
				.build());

		if (userInput.isAttending()) {
	        userInput.setAttending(false);
						ConventionsApplication.alarmScheduler.cancelDefaultEventAlarms(conventionEvent);
	        item.setIcon(ContextCompat.getDrawable(this, R.drawable.star_with_plus));
	        item.setTitle(getResources().getString(R.string.event_add_to_favorites));
	        Snackbar.make(this.mainLayout, R.string.event_removed_from_favorites, Snackbar.LENGTH_SHORT).show();
	    } else {
	        userInput.setAttending(true);
			ConventionsApplication.alarmScheduler.scheduleDefaultEventAlarms(conventionEvent);
	        item.setIcon(ContextCompat.getDrawable(this, android.R.drawable.btn_star_big_on));
	        item.setTitle(getResources().getString(R.string.event_remove_from_favorites));
	        Snackbar.make(this.mainLayout, R.string.event_added_to_favorites, Snackbar.LENGTH_SHORT).show();
	    }
		setupAlarmsMenuItem(menu, userInput);
		saveUserInput();
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
        if (locations.size() == 0) {
            menu.findItem(R.id.event_navigate_to_map).setVisible(false);
        }
    }

    private void setEvent(ConventionEvent event) {
	    boolean showSeparator = true;
	    TextView type = (TextView) findViewById(R.id.event_type);
	    if (event.getType() == null || TextUtils.isEmpty(event.getType().getDescription())) {
		    showSeparator = false;
		    type.setVisibility(View.GONE);
	    } else {
	        type.setText(event.getType().getDescription());
	    }
	    TextView category = (TextView) findViewById(R.id.event_category);
	    if (TextUtils.isEmpty(event.getCategory())) {
		    showSeparator = false;
		    category.setVisibility(View.GONE);
	    } else {
		    category.setText(event.getCategory());
	    }
	    if (!showSeparator) {
		    findViewById(R.id.event_type_and_category_separator).setVisibility(View.GONE);
	    }

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

	    SimpleDateFormat sdf = new SimpleDateFormat("EEE dd.MM", Dates.getLocale());
        String formattedEventTime = String.format("%s, %s - %s (%s)",
		        sdf.format(event.getStartTime()),
                Dates.formatHoursAndMinutes(event.getStartTime()),
                Dates.formatHoursAndMinutes(event.getEndTime()),
                Dates.toHumanReadableTimeDuration(event.getEndTime().getTime() - event.getStartTime().getTime()));
        time.setText(String.format("%s%s", formattedEventHall, formattedEventTime));

	    TextView prices = (TextView) findViewById(R.id.event_prices);
	    if (event.getPrice() == 0) {
		    prices.setText(getString(R.string.event_price_free));
	    } else {
		    prices.setText(getString(R.string.event_prices, event.getPrice(), event.getDiscountPrice()));
	    }

	    TextView tags = (TextView) findViewById(R.id.event_tags);
	    List<String> eventTags = event.getTags();
	    if (eventTags == null || eventTags.size() == 0) {
		    tags.setVisibility(View.GONE);
	    } else {
		    tags.setText(getString(R.string.tags, event.getTagsAsString()));
	    }

	    setupFeedback(event);

	    setupEventDescription(event);

        setupBackgroundImages(event);

    }

	private void setupFeedback(ConventionEvent event) {
		if (event.canFillFeedback()) {
			feedbackContainer.setVisibility(View.VISIBLE);
	        feedbackView.setModel(event.getUserInput().getFeedback());

			if (shouldFeedbackBeClosed()) {
				feedbackView.setState(CollapsibleFeedbackView.State.Collapsed, false);
			} else {
				feedbackView.setState(CollapsibleFeedbackView.State.Expanded, false);
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
				protected void onSuccess() {
					super.onSuccess();
					feedbackView.setState(CollapsibleFeedbackView.State.Collapsed, true);
				}
			});
		} else {
			feedbackContainer.setVisibility(View.GONE);
		}
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
		if (eventDescription == null || eventDescription.isEmpty()) {
			findViewById(R.id.event_description_box).setVisibility(View.GONE);
		} else {
			// Enable internal links from HTML <a> tags within the description textView.
			TextView description = (TextView) findViewById(R.id.event_description);
			description.setMovementMethod(LinkMovementMethod.getInstance());

			Spanned spanned = Html.fromHtml(eventDescription, null, new ListTagHandler());
			description.setText(spanned);

			// Intercept clicks on links to other events
			if (description.getText() instanceof SpannableString) {
				SpannableString spannable = (SpannableString) description.getText();
				URLSpan[] spans = spannable.getSpans(0, spanned.length(), URLSpan.class);
				for (URLSpan span : spans) {
					int spanStart = spannable.getSpanStart(span);
					int spanEnd = spannable.getSpanEnd(span);
					int spanFlags = spannable.getSpanFlags(span);
					spannable.removeSpan(span);
					spannable.setSpan(new EventURLSpan(span), spanStart, spanEnd, spanFlags);
				}
				description.setText(spannable);
			}
		}
	}

	private void setupBackgroundImages(ConventionEvent event) {
        // Add images to the layout
        List<Integer> images = event.getImageResources();
		boolean first = true;
		// This will contain the last image view after the loop
		FrameLayout lastImageLayout = null;
		int lastImageHeight = -1;
		int i = 0;
		// Screen size for calculating last image height
		Display display = getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		int widthSpec = View.MeasureSpec.makeMeasureSpec(size.x, View.MeasureSpec.EXACTLY);
        for (Integer imageResource : images) {
	        boolean last = (i == images.size() - 1);

			AspectRatioImageView imageView = new AspectRatioImageView(this);
	        View viewToAdd = imageView;
	        imageView.setImageResource(imageResource);

	        // Last image - make a frame layout with the image so we can put the gradient on top of it
	        if (last) {
		        FrameLayout frameLayout = new FrameLayout(this);
		        viewToAdd = frameLayout;
		        lastImageLayout = frameLayout;
		        lastImageView = imageView;

		        // Add the image view to the frame layout
		        FrameLayout.LayoutParams imageLayoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		        imageView.setLayoutParams(imageLayoutParams);
		        frameLayout.addView(imageView);

		        // Calculate the last image's height according the screen width
		        imageView.measure(widthSpec, View.MeasureSpec.UNSPECIFIED);
		        lastImageHeight = imageView.getMeasuredHeight();
	        }

	        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
	        int topMargin = 0;
            if (first) {
                first = false;
            } else {
                topMargin =  getResources().getDimensionPixelOffset(R.dimen.event_images_margin);
            }
	        layoutParams.setMargins(0, topMargin, 0, 0);
	        viewToAdd.setLayoutParams(layoutParams);
	        imagesLayout.addView(viewToAdd);

	        ++i;
        }

		if (lastImageLayout != null) {
			int gradientHeight =  getResources().getDimensionPixelSize(R.dimen.event_last_image_gradient_max_height);
			if (gradientHeight > lastImageHeight) {
				gradientHeight = lastImageHeight;
			}
			// Note: instead of gradient we could supposedly use fading edges and override getSolidColor of lastImageView
			// to return the background color. This will cause the same level of overdraw as this solution but will require
			// much less code and remove the need for a FrameLayout above this image (to make sure it works call
			// lastImageView.invalidate() in updateBackgroundColor).
			// However, for some reason the fade effect doesn't look as good - it doesn't start at the right place if the image is
			// small. So we stick with the gradient.
			gradientImageView = new ImageView(this);
			FrameLayout.LayoutParams gradientLayoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, gradientHeight);
			gradientImageView.setImageDrawable(createGradient());
			gradientLayoutParams.gravity = Gravity.BOTTOM;
			gradientImageView.setLayoutParams(gradientLayoutParams);
			lastImageLayout.addView(gradientImageView);

			enableFadingEffect();
		}
    }

	private Drawable createGradient() {
		return new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, new int[] {Color.TRANSPARENT, getBackgroundColor()});
	}

    public void openFeedback(View view) {
        feedbackView.setState(CollapsibleFeedbackView.State.Expanded);
    }

    public void closeFeedback(View view) {
        feedbackView.setState(CollapsibleFeedbackView.State.Collapsed);
    }

	private class EventURLSpan extends ClickableSpan {
		private final URLSpan urlSpan;

		public EventURLSpan(URLSpan span) {
			this.urlSpan = span;
		}

		@Override
		public void onClick(View view) {
			ConventionEvent event = Convention.getInstance().findEventByURL(urlSpan.getURL());
			// Go to the event in case of a link to an event. Otherwise go to the original URL.
			if (event != null) {
				navigateToEvent(event.getId());
			} else {
				urlSpan.onClick(view);
			}
		}
	}
}

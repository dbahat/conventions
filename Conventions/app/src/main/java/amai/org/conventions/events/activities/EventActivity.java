package amai.org.conventions.events.activities;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.graphics.Palette;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;
import java.util.Properties;

import amai.org.conventions.R;
import amai.org.conventions.customviews.AspectRatioImageView;
import amai.org.conventions.events.CollapsibleFeedbackView;
import amai.org.conventions.map.MapActivity;
import amai.org.conventions.model.Convention;
import amai.org.conventions.model.ConventionEvent;
import amai.org.conventions.model.ConventionMap;
import amai.org.conventions.model.Feedback;
import amai.org.conventions.model.FeedbackQuestion;
import amai.org.conventions.model.MapLocation;
import amai.org.conventions.navigation.NavigationActivity;
import amai.org.conventions.utils.Dates;
import amai.org.conventions.utils.GMailSender;
import uk.co.chrisjenx.paralloid.views.ParallaxScrollView;


public class EventActivity extends NavigationActivity {

    public static final String EXTRA_EVENT_ID = "EventIdExtra";

    private static final String TAG = EventActivity.class.getCanonicalName();
    private static final String STATE_FEEDBACK_OPEN = "StateFeedbackOpen";

    private ConventionEvent conventionEvent;
    private LinearLayout imagesLayout;
    private CollapsibleFeedbackView feedbackView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentInContentContainer(R.layout.activity_event);

        imagesLayout = (LinearLayout) findViewById(R.id.images_layout);
        feedbackView = (CollapsibleFeedbackView) findViewById(R.id.event_feedback_view);

        String eventId = getIntent().getStringExtra(EXTRA_EVENT_ID);
        conventionEvent = Convention.getInstance().findEventById(eventId);
        setEvent(conventionEvent, savedInstanceState);

        final View mainLayout = findViewById(R.id.event_main_layout);
        final ParallaxScrollView scrollView = (ParallaxScrollView) findViewById(R.id.parallax_scroll);
        final View backgroundView = imagesLayout;
        final View detailBoxes = findViewById(R.id.event_detail_boxes);

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
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_event, menu);

        if (conventionEvent.getUserInput().isAttending()) {
            MenuItem favoritesButton = menu.findItem(R.id.event_change_favorite_state);
            favoritesButton.setIcon(getResources().getDrawable(android.R.drawable.btn_star_big_on));
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.event_change_favorite_state:
                if (conventionEvent.getUserInput().isAttending()) {
                    conventionEvent.getUserInput().setAttending(false);
                    item.setIcon(getResources().getDrawable(R.drawable.star_with_plus));
                    item.setTitle(getResources().getString(R.string.event_add_to_favorites));
                    Toast.makeText(this, getString(R.string.event_removed_from_favorites), Toast.LENGTH_SHORT).show();
                } else {
                    conventionEvent.getUserInput().setAttending(true);
                    item.setIcon(getResources().getDrawable(android.R.drawable.btn_star_big_on));
                    item.setTitle(getResources().getString(R.string.event_remove_from_favorites));
                    Toast.makeText(this, getString(R.string.event_added_to_favorites), Toast.LENGTH_SHORT).show();
                }
                saveUserInput();
                return true;
            case R.id.event_navigate_to_map:
                // Navigate to the map floor associated with this event
                Bundle floorBundle = new Bundle();
                ConventionMap map = Convention.getInstance().getMap();
                List<MapLocation> locations = map.findLocationsByHall(conventionEvent.getHall());
                MapLocation location = map.findClosestLocation(locations);
                floorBundle.putInt(MapActivity.EXTRA_MAP_LOCATION_ID, location.getId());

                navigateToActivity(MapActivity.class, false, floorBundle);
                return true;
            case R.id.event_navigate_to_hall:
                // Navigate to the hall associated with this event
                Bundle bundle = new Bundle();
                bundle.putString(HallActivity.EXTRA_HALL_NAME, conventionEvent.getHall().getName());

                navigateToActivity(HallActivity.class, false, bundle);
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

		// Reset answer changed flag
		List<FeedbackQuestion> questions = this.conventionEvent.getUserInput().getFeedback().getQuestions();
		for (FeedbackQuestion question : questions) {
		    question.setAnswerChanged(false);
		}
	}

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(STATE_FEEDBACK_OPEN, feedbackView.getState() == CollapsibleFeedbackView.State.Expended);

        super.onSaveInstanceState(outState);
    }

    public void onSendFeedbackClicked(View view) {

        feedbackView.setProgressBarVisibility(true);

        new AsyncTask<Void, Void, String>() {

            @Override
            protected String doInBackground(Void... params) {
                // First save the user input before sending it
                saveUserInput();

                Properties properties = new Properties();
                try {
                    properties.load(getResources().openRawResource(R.raw.mail));
                } catch (IOException e) {
                    return e.getMessage();
                }

                String mail = properties.getProperty("mail");
                String password = properties.getProperty("password");
                if (mail == null || password == null) {
                    return "Failed to get the mail or password values from the mail.properties file.";
                }

                GMailSender sender = new GMailSender(mail, password);
                try {
                    sender.sendMail(
                            getString(R.string.feedback_mail_title) + ": " + conventionEvent.getTitle(),
                            formatFeedbackMailBody(),
                            mail,
                            Convention.getInstance().getFeedbackRecipient());
                } catch (Exception e) {
                    return e.getMessage();
                }

	            Feedback feedback = conventionEvent.getUserInput().getFeedback();
	            feedback.setIsSent(true);
	            feedback.removeUnansweredQuestions();
                saveUserInput();

                // In case everything finished successfully, pass null to onPostExecute.
                return null;
            }

            @Override
            protected void onPostExecute(String errorMessage) {
                feedbackView.setProgressBarVisibility(false);

                if (errorMessage != null) {
                    Log.w(TAG, "Failed to send feedback mail. Reason: " + errorMessage);
                    Toast.makeText(EventActivity.this, getString(R.string.feedback_send_mail_failed), Toast.LENGTH_LONG).show();
                } else {
                    // Re-setup the feedback UI so interactions will now be disabled in it, and the "feedback sent" indicator become visible.
                    feedbackView.setEvent(conventionEvent);
                }
            }

        }.execute();
    }

    private String formatFeedbackMailBody() {
        return String.format(Dates.getLocale(), "%s\n%s, %s\n\n%s\n\n\nDeviceId: %s",
                conventionEvent.getTitle(),
                Dates.formatHoursAndMinutes(conventionEvent.getStartTime()),
                conventionEvent.getHall().getName(),
                formatFeedbackQuestions(),
                getDeviceId()
        );
    }

    private String formatFeedbackQuestions() {
        StringBuilder stringBuilder = new StringBuilder();
        Feedback feedback = conventionEvent.getUserInput().getFeedback();
        for (FeedbackQuestion question : feedback.getQuestions()) {
            if (question.hasAnswer()) {
                stringBuilder.append(String.format(Dates.getLocale(), "%s %s\n",
                        question.getQuestionText(getResources(), feedback.isSent()),
                        question.getAnswer()));
            }
        }

        return stringBuilder.toString();
    }

    private String getDeviceId() {
        return Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    private void setEvent(ConventionEvent event, Bundle savedInstanceState) {
        setToolbarTitle(event.getType().getDescription());

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

        String formattedEventTime = String.format("%s, %s - %s (%s)",
                event.getHall().getName(),
                Dates.formatHoursAndMinutes(event.getStartTime()),
                Dates.formatHoursAndMinutes(event.getEndTime()),
                Dates.toHumanReadableTimeDuration(event.getEndTime().getTime() - event.getStartTime().getTime()));
        time.setText(formattedEventTime);

        feedbackView.setEvent(event);

        // If the feedback view already had saved state, restore it
        if (savedInstanceState != null && savedInstanceState.containsKey(STATE_FEEDBACK_OPEN)) {
            if (savedInstanceState.getBoolean(STATE_FEEDBACK_OPEN)) {
                feedbackView.setState(CollapsibleFeedbackView.State.Expended);
            } else {
                feedbackView.setState(CollapsibleFeedbackView.State.Collapsed);
            }
        }

        setupBackgroundImages(event);

        TextView description = (TextView) findViewById(R.id.event_description);

        // Enable internal links from HTML <a> tags within the description textView.
        description.setMovementMethod(LinkMovementMethod.getInstance());

        String eventDescription = event.getDescription()
                // Translate new lines into html <br> tags
                .replace("\n", "<BR/>")
                        // Replace images in the description text with some other non-visible tag (e.g. div)
                .replace("<img", "<div")
                .replace("/img>", "/div>");

        description.setText(Html.fromHtml(eventDescription));
    }

    private void setupBackgroundImages(ConventionEvent event) {
        // Add images to the layout
        List<Integer> images = event.getImages();
        boolean first = true;
        for (int imageId : images) {
            ImageView imageView = new AspectRatioImageView(this);
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
    }

    public void openFeedback(View view) {
        feedbackView.setState(CollapsibleFeedbackView.State.Expended);
    }

    public void closeFeedback(View view) {
        feedbackView.setState(CollapsibleFeedbackView.State.Collapsed);
    }
}

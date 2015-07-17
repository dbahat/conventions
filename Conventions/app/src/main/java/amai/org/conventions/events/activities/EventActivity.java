package amai.org.conventions.events.activities;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.graphics.Bitmap;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.graphics.Palette;
import android.text.Editable;
import android.text.Html;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import amai.org.conventions.R;
import amai.org.conventions.ThemeAttributes;
import amai.org.conventions.customviews.AspectRatioImageView;
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
    private ViewGroup feedbackCollapsed;
    private ViewGroup feedbackExpended;
    private ViewGroup feedbackContainer;
    private boolean shouldSaveFeedback;
    private ProgressBar progressBar;
    private TextView collapsedFeedbackTitle;
	private Button openFeedbackButton;
	private ImageView feedbackIcon;
	private View sendFeedbackButton;
	private View feedbackSentText;

    int expendedFeedbackLayoutHeight;
    int collapsedFeedbackLayoutHeight;

	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentInContentContainer(R.layout.activity_event);
        shouldSaveFeedback = false;

        imagesLayout = (LinearLayout) findViewById(R.id.images_layout);
        feedbackCollapsed = (ViewGroup) findViewById(R.id.feedback_collapsed);
        feedbackExpended = (ViewGroup) findViewById(R.id.feedback_expended);
        progressBar = (ProgressBar) findViewById(R.id.feedback_progress_bar);
        collapsedFeedbackTitle = (TextView) findViewById(R.id.collapsed_feedback_title);
		openFeedbackButton = (Button) findViewById(R.id.open_feedback_button);
		feedbackIcon = (ImageView) findViewById(R.id.feedback_icon);
        feedbackContainer = (ViewGroup) findViewById(R.id.feedback_container);
		sendFeedbackButton = findViewById(R.id.send_feedback_button);
		feedbackSentText = findViewById(R.id.feedback_sent_text);

        String eventId = getIntent().getStringExtra(EXTRA_EVENT_ID);
        conventionEvent = Convention.getInstance().findEventById(eventId);
        setEvent(conventionEvent, savedInstanceState);

        calculateCollapsedFeedbackHeight();
        calculateExpendedFeedbackHeight();

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
                Convention.getInstance().getStorage().saveUserInput();
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
        if (shouldSaveFeedback) {
            Convention.getInstance().getStorage().saveUserInput();

            // Reset answer changed flag
            List<FeedbackQuestion> questions = this.conventionEvent.getUserInput().getFeedback().getQuestions();
            for (FeedbackQuestion question : questions) {
                question.setAnswerChanged(false);
            }
        }
    }

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putBoolean(STATE_FEEDBACK_OPEN, feedbackExpended.getVisibility() == View.VISIBLE);

		super.onSaveInstanceState(outState);
	}

	public void onSendFeedbackClicked(final View view) {

        // Hide the send feedback button and show a spinny while sending is in progress
        view.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);

        new AsyncTask<Void, Void, String>() {

            @Override
            protected String doInBackground(Void... params) {
                // First save the user input before sending it
                Convention.getInstance().getStorage().saveUserInput();

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

                conventionEvent.getUserInput().getFeedback().setIsSent(true);
                Convention.getInstance().getStorage().saveUserInput();

                // In case everything finished successfully, pass null to onPostExecute.
                return null;
            }

            @Override
            protected void onPostExecute(String errorMessage) {
                progressBar.setVisibility(View.GONE);

                if (errorMessage != null) {
                    Log.w(TAG, "Failed to send feedback mail. Reason: " + errorMessage);
                    Toast.makeText(EventActivity.this, getString(R.string.feedback_send_mail_failed), Toast.LENGTH_LONG).show();
                    view.setVisibility(View.VISIBLE);
                } else {
                    // Re-setup the feedback UI so interactions will now be disabled in it, and the "feedback sent" indicator become visible.
                    setupFeedback(conventionEvent, null);
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

        setupFeedback(event, savedInstanceState);

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

    private void setupFeedback(ConventionEvent event, Bundle savedInstanceState) {
	    if (!event.canFillFeedback()) {
		    findViewById(R.id.feedback_container).setVisibility(View.GONE);
		    return;
	    }

	    Feedback feedback = event.getUserInput().getFeedback();

	    // If we got the saved state of the feedback (from rotation etc), use it. Instead calculate the
	    // initial state of the feedback.
	    boolean shouldFeedbackBeClosed;
	    if (savedInstanceState != null && savedInstanceState.containsKey(STATE_FEEDBACK_OPEN)) {
		    shouldFeedbackBeClosed = !savedInstanceState.getBoolean(STATE_FEEDBACK_OPEN);
	    } else {
		    // Feedback should start as closed in the following cases:
		    // 1. Feedback was sent
		    // 2. Event was not attended an no questions were answered
		    // Otherwise it should start as open.
		    shouldFeedbackBeClosed = feedback.isSent() ||
				    (!event.isAttending() && !feedback.hasAnsweredQuestions());
	    }

	    if (shouldFeedbackBeClosed) {
            closeFeedback();
	    } else {
		    openFeedback();
	    }

        if (feedback.isSent()) {
            collapsedFeedbackTitle.setText(getString(R.string.feedback_sent));
	        openFeedbackButton.setText(getString(R.string.display_feedback));
	        sendFeedbackButton.setVisibility(View.GONE);
	        feedbackSentText.setVisibility(View.VISIBLE);
        }
        setFeedbackIcon(event);

        LinearLayout questionsLayout = (LinearLayout) findViewById(R.id.questions_layout);
        questionsLayout.removeAllViews();
        for (FeedbackQuestion question : feedback.getQuestions()) {
            questionsLayout.addView(buildQuestionView(question, feedback.isSent()));
        }
    }

	private void setFeedbackIcon(ConventionEvent event) {
		Drawable icon;
		FeedbackQuestion.Smiley3PointAnswer weightedRating = event.getUserInput().getFeedback().getWeightedRating();
		int filterColor;
		if (weightedRating != null) {
			icon = getResources().getDrawable(weightedRating.getImageResourceId());
			filterColor = getResources().getColor(R.color.yellow);
		} else {
			icon = getResources().getDrawable(R.drawable.feedback);
			filterColor = ThemeAttributes.getColor(this, R.attr.eventFeedbackHighlightedButtonColor);
		}
		icon = icon.mutate();
		icon.setColorFilter(filterColor, PorterDuff.Mode.MULTIPLY);
		feedbackIcon.setImageDrawable(icon);
	}

    private View buildQuestionView(final FeedbackQuestion question, boolean isSent) {
        LinearLayout questionLayout = new LinearLayout(this);
        LinearLayout.LayoutParams questionLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        questionLayoutParams.setMargins(0, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20, getResources().getDisplayMetrics()), 0, 0);
        questionLayout.setLayoutParams(questionLayoutParams);

        TextView questionText = new TextView(this);
        questionText.setTextAppearance(this, R.style.TextAppearance_AppCompat_Body1);
        questionText.setText(question.getQuestionText(getResources(), isSent));

        Object answer = question.getAnswer();
        View answerView = null;
        int layoutOrientation = LinearLayout.VERTICAL;

        switch (question.getAnswerType()) {
            case TEXT: {
	            if (isSent) {
		            // Display in a text view
		            TextView textView = new TextView(this);
		            LinearLayout.LayoutParams textViewLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		            textView.setLayoutParams(textViewLayoutParams);
		            textView.setTextAppearance(this, R.style.TextAppearance_AppCompat_Body1);
		            if (answer != null) {
		                textView.setText(answer.toString());
		            }

		            answerView = textView;
	            } else {
		            // Display in an editable text
	                EditText editText = new EditText(this);
	                editText.setFreezesText(true);
	                editText.setInputType(
			                InputType.TYPE_CLASS_TEXT |
					                InputType.TYPE_TEXT_FLAG_AUTO_CORRECT |
					                InputType.TYPE_TEXT_VARIATION_LONG_MESSAGE |
					                InputType.TYPE_TEXT_FLAG_MULTI_LINE |
					                InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS
	                );
	                LinearLayout.LayoutParams editTextLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
	                editText.setLayoutParams(editTextLayoutParams);
	                editText.setTextAppearance(this, R.style.TextAppearance_AppCompat_Body1);
	                editText.addTextChangedListener(new TextWatcher() {
		                @Override
		                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
		                }

		                @Override
		                public void onTextChanged(CharSequence s, int start, int before, int count) {
		                }

		                @Override
		                public void afterTextChanged(Editable s) {
                            sendFeedbackButton.setEnabled(true);
			                question.setAnswer(s.toString());
			                shouldSaveFeedback |= question.isAnswerChanged();
		                }
	                });

	                if (answer != null) {
	                    editText.setText(answer.toString());
                    sendFeedbackButton.setEnabled(true);
	                }

		            answerView = editText;
	            }

                break;
            }
            case SMILEY_3_POINTS: {
                LinearLayout imagesLayout = new LinearLayout(this);
                imagesLayout.setOrientation(LinearLayout.HORIZONTAL);
                int margin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20, getResources().getDisplayMetrics());
                int size = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, getResources().getDisplayMetrics());

                ColorMatrix matrix = new ColorMatrix();
                matrix.setSaturation(0);
                final ColorMatrixColorFilter grayscale = new ColorMatrixColorFilter(matrix);

                final ImageView negativeRating = new ImageView(this);
                LinearLayout.LayoutParams negativeLayoutParams = new LinearLayout.LayoutParams(size, size);
                negativeLayoutParams.setMarginEnd(margin);
                negativeRating.setLayoutParams(negativeLayoutParams);
                negativeRating.setImageResource(FeedbackQuestion.Smiley3PointAnswer.NEGATIVE.getImageResourceId());
                negativeRating.setColorFilter(grayscale);
                imagesLayout.addView(negativeRating);

                final ImageView positiveRating = new AspectRatioImageView(this);
                LinearLayout.LayoutParams positiveLayoutParams = new LinearLayout.LayoutParams(size, size);
                positiveLayoutParams.setMarginEnd(margin);
                positiveRating.setLayoutParams(positiveLayoutParams);
                positiveRating.setImageResource(FeedbackQuestion.Smiley3PointAnswer.POSITIVE.getImageResourceId());
                positiveRating.setColorFilter(grayscale);
                imagesLayout.addView(positiveRating);

                final ImageView veryPositiveRating = new AspectRatioImageView(this);
                LinearLayout.LayoutParams veryPositiveLayoutParams = new LinearLayout.LayoutParams(size, size);
                veryPositiveLayoutParams.setMarginEnd(margin);
                veryPositiveRating.setLayoutParams(veryPositiveLayoutParams);
                veryPositiveRating.setImageResource(FeedbackQuestion.Smiley3PointAnswer.VERY_POSITIVE.getImageResourceId());
                veryPositiveRating.setColorFilter(grayscale);
                imagesLayout.addView(veryPositiveRating);

                View.OnClickListener listener = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sendFeedbackButton.setEnabled(true);
                        List<ImageView> otherImages = new ArrayList<>(Arrays.asList(negativeRating, positiveRating, veryPositiveRating));
                        for (ImageView otherImage : otherImages) {
                            otherImage.setColorFilter(grayscale);
                        }

                        ImageView selected = (ImageView) v;
                        selected.setColorFilter(getResources().getColor(R.color.yellow), PorterDuff.Mode.MULTIPLY);

                        Object answer = null;
                        if (selected == negativeRating) {
                            answer = FeedbackQuestion.Smiley3PointAnswer.NEGATIVE;
                        } else if (selected == positiveRating) {
                            answer = FeedbackQuestion.Smiley3PointAnswer.POSITIVE;
                        } else if (selected == veryPositiveRating) {
                            answer = FeedbackQuestion.Smiley3PointAnswer.VERY_POSITIVE;
                        }
                        question.setAnswer(answer);
                        shouldSaveFeedback |= question.isAnswerChanged();
                    }
                };

                if (!isSent) {
                    negativeRating.setOnClickListener(listener);
                    positiveRating.setOnClickListener(listener);
                    veryPositiveRating.setOnClickListener(listener);
                } else {
                    negativeRating.setOnClickListener(null);
                    positiveRating.setOnClickListener(null);
                    veryPositiveRating.setOnClickListener(null);
                }

                if (answer != null) {
                    sendFeedbackButton.setEnabled(true);
	                FeedbackQuestion.Smiley3PointAnswer smileyAnswer = FeedbackQuestion.Smiley3PointAnswer.getByAnswerText(answer.toString());
	                if (smileyAnswer != null) {
		                switch (smileyAnswer) {
			                case NEGATIVE:
		                        listener.onClick(negativeRating);
				                break;
			                case POSITIVE:
	                            listener.onClick(positiveRating);
				                break;
			                case VERY_POSITIVE:
	                            listener.onClick(veryPositiveRating);
				                break;
		                }
	                }
                }

                answerView = imagesLayout;
                break;
            }
            case YES_NO: {
                LinearLayout buttonsLayout = new LinearLayout(this);
                buttonsLayout.setOrientation(LinearLayout.HORIZONTAL);

                final TextView yesButton = new TextView(this);
                yesButton.setTextAppearance(this, R.style.EventFeedbackButton);
                yesButton.setText(R.string.yes);
                int padding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, getResources().getDisplayMetrics());
                yesButton.setPaddingRelative(0, padding, padding * 4, padding);
                buttonsLayout.addView(yesButton);

                final TextView noButton = new TextView(this);
                noButton.setTextAppearance(this, R.style.EventFeedbackButton);
                noButton.setText(R.string.no);
                noButton.setPaddingRelative(padding * 4, padding, padding * 4, padding);
                buttonsLayout.addView(noButton);

                View.OnClickListener listener = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sendFeedbackButton.setEnabled(true);
                        TextView selected = (TextView) v;
                        selected.setTextAppearance(EventActivity.this, R.style.EventAnswerButtonHighlighted);

                        TextView otherButton = (selected == yesButton ? noButton : yesButton);
                        otherButton.setTextAppearance(EventActivity.this, R.style.EventFeedbackButton);
                        question.setAnswer(selected.getText().toString());
                        shouldSaveFeedback |= question.isAnswerChanged();
                    }
                };

                if (!isSent) {
                    yesButton.setOnClickListener(listener);
                    noButton.setOnClickListener(listener);
                } else {
                    yesButton.setOnClickListener(null);
                    noButton.setOnClickListener(null);
                }

                if (answer != null) {
                    sendFeedbackButton.setEnabled(true);
                    if (answer.equals(yesButton.getText().toString())) {
                        listener.onClick(yesButton);
                    } else if (answer.equals(noButton.getText().toString())) {
                        listener.onClick(noButton);
                    }
                }

                answerView = buttonsLayout;
                break;
            }
        }

        questionLayout.setOrientation(layoutOrientation);
        questionLayout.addView(questionText);
        questionLayout.addView(answerView);

        return questionLayout;
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
        resizeFeedbackContainer(FeedbackLayoutResizeAction.Expand);
    }

    public void closeFeedback(View view) {
        resizeFeedbackContainer(FeedbackLayoutResizeAction.Collapse);
    }

    public void closeFeedback() {
        feedbackCollapsed.setVisibility(View.VISIBLE);
        feedbackExpended.setVisibility(View.GONE);
    }

    public void openFeedback() {
        feedbackCollapsed.setVisibility(View.GONE);
        feedbackExpended.setVisibility(View.VISIBLE);
    }

    private enum FeedbackLayoutResizeAction {
        Expand,
        Collapse
    }

    private void resizeFeedbackContainer(FeedbackLayoutResizeAction resizeAction) {
        int currentHeight = feedbackContainer.getHeight();
        int targetHeight = 0;
        ViewGroup layoutBeforeResize = null;
        ViewGroup layoutAfterResize = null;
        switch (resizeAction) {
            case Expand:
                targetHeight = expendedFeedbackLayoutHeight;
                layoutBeforeResize = feedbackCollapsed;
                layoutAfterResize = feedbackExpended;
                break;
            case Collapse:
                targetHeight = collapsedFeedbackLayoutHeight;
                layoutBeforeResize = feedbackExpended;
                layoutAfterResize = feedbackCollapsed;
                break;
        }

        ValueAnimator animation = slideAnimator(currentHeight, targetHeight, feedbackContainer);
        animation.setDuration(300);

        final ViewGroup finalLayoutBeforeResize = layoutBeforeResize;
        final ViewGroup finalLayoutAfterResize = layoutAfterResize;

        animation.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                finalLayoutBeforeResize.setVisibility(View.GONE);
                ViewGroup.LayoutParams layoutParams = feedbackContainer.getLayoutParams();
                layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                feedbackContainer.setLayoutParams(layoutParams);

                finalLayoutAfterResize.setVisibility(View.VISIBLE);
                finalLayoutAfterResize.startAnimation(AnimationUtils.loadAnimation(EventActivity.this, R.anim.fab_in));
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        animation.start();
    }

    private ValueAnimator slideAnimator(int start, int end, final View viewToResize) {

        ValueAnimator animator = ValueAnimator.ofInt(start, end);
	    animator.setInterpolator(new AccelerateDecelerateInterpolator());

        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                //Update Height
                int value = (Integer) valueAnimator.getAnimatedValue();

                ViewGroup.LayoutParams layoutParams = viewToResize.getLayoutParams();
                layoutParams.height = value;
                viewToResize.setLayoutParams(layoutParams);
            }
        });
        return animator;
    }

    private void calculateExpendedFeedbackHeight() {
        final ViewGroup expendedFeedbackLayout = (ViewGroup) getLayoutInflater().inflate(R.layout.feedback_layout_expanded, null);

        // Position the hidden view outside the bounds of the screen
        expendedFeedbackLayout.setX(getScreenWidth());

        // Add the view to the screen
        final ViewGroup root = (ViewGroup)findViewById(R.id.feedback_container);
        root.addView(expendedFeedbackLayout);

        // Add all the questions to the view
        LinearLayout questionsLayout = (LinearLayout) expendedFeedbackLayout.findViewById(R.id.questions_layout);
        questionsLayout.removeAllViews();
        for (FeedbackQuestion question : conventionEvent.getUserInput().getFeedback().getQuestions()) {
            questionsLayout.addView(buildQuestionView(question, conventionEvent.getUserInput().getFeedback().isSent()));
        }

        // Wait for the layout onMeasure to be called, get it's height, and then remove it from the screen
        expendedFeedbackLayout.post(new Runnable() {
            @Override
            public void run() {
                expendedFeedbackLayoutHeight = expendedFeedbackLayout.getMeasuredHeight();
                root.removeView(expendedFeedbackLayout);
            }
        });
    }

    public void calculateCollapsedFeedbackHeight() {
        final ViewGroup collapsedFeedbackLayout = (ViewGroup) getLayoutInflater().inflate(R.layout.feedback_layout_collapsed, null);

        // Position the hidden view outside the bounds of the screen
        collapsedFeedbackLayout.setX(getScreenWidth());

        // Add the view to the screen
        final ViewGroup root = (ViewGroup)findViewById(R.id.feedback_container);
        root.addView(collapsedFeedbackLayout);

        collapsedFeedbackLayout.bringToFront();

        // Wait for the layout onMeasure to be called, get its height, and then remove it from the screen
        collapsedFeedbackLayout.post(new Runnable() {
            @Override
            public void run() {
                collapsedFeedbackLayoutHeight = collapsedFeedbackLayout.getMeasuredHeight();
                root.removeView(collapsedFeedbackLayout);
            }
        });
    }

    private int getScreenWidth() {
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size.x;
    }
}

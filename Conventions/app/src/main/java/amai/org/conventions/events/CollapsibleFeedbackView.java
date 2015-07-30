package amai.org.conventions.events;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import amai.org.conventions.R;
import amai.org.conventions.ThemeAttributes;
import amai.org.conventions.customviews.AspectRatioImageView;
import amai.org.conventions.model.Feedback;
import amai.org.conventions.model.FeedbackQuestion;

public class CollapsibleFeedbackView extends FrameLayout {

    private State state;
    private TextView collapsedFeedbackTitle;
    private Button openFeedbackButton;
    private Button sendFeedbackButton;
    private ImageView feedbackIcon;
    private View feedbackSentText;
    private ViewGroup feedbackCollapsed;
    private ViewGroup feedbackExpended;
    private ViewGroup feedbackContainer;
    private ProgressBar progressBar;

    private int expendedFeedbackLayoutHeight;
    private int collapsedFeedbackLayoutHeight;

    private Feedback feedback;
    private boolean feedbackChanged;

    public CollapsibleFeedbackView(Context context, AttributeSet attrs) {
        super(context, attrs);

        LayoutInflater.from(this.getContext()).inflate(R.layout.view_collapsible_feedback_layout, this, true);

        state = State.Collapsed;
        feedbackChanged = false;

        collapsedFeedbackTitle = (TextView) findViewById(R.id.collapsed_feedback_title);
        openFeedbackButton = (Button) findViewById(R.id.open_feedback_button);
        feedbackIcon = (ImageView) findViewById(R.id.feedback_icon);
        feedbackContainer = (ViewGroup) findViewById(R.id.feedback_container);
        sendFeedbackButton = (Button) findViewById(R.id.send_feedback_button);
        feedbackSentText = findViewById(R.id.feedback_sent_text);
        feedbackCollapsed = (ViewGroup) findViewById(R.id.feedback_collapsed);
        feedbackExpended = (ViewGroup) findViewById(R.id.feedback_expended);
        progressBar = (ProgressBar) findViewById(R.id.feedback_progress_bar);
    }

	public void setState(State state, boolean animate) {
		 if (animate) {
			 setState(state);
		 } else {
			 setStateWithoutAnimation(state);
		 }
	}

    public void setState(State state) {
        this.state = state;

        resizeFeedbackContainer(state);
    }

    public State getState() {
        return state;
    }

    public void setProgressBarVisibility(boolean visible) {
        if (visible) {
            progressBar.setVisibility(VISIBLE);
            sendFeedbackButton.setVisibility(GONE);
        } else {
            progressBar.setVisibility(GONE);
            sendFeedbackButton.setVisibility(VISIBLE);
        }
    }

    public void setModel(Feedback feedback) {
        this.feedback = feedback;

        // Calculate the heights of the collapsed/expended states of the feedback view, since they are dynamic (based on the number of questions), and
        // we need the heights pre-calculated to be able to properly animate the transitions.
        calculateCollapsedFeedbackHeight();
        calculateExpendedFeedbackHeight();

        if (feedback.isSent()) {
            collapsedFeedbackTitle.setText(getContext().getString(R.string.feedback_sent));
            openFeedbackButton.setText(getContext().getString(R.string.display_feedback));
            sendFeedbackButton.setVisibility(View.GONE);
            feedbackSentText.setVisibility(View.VISIBLE);
        }
        setFeedbackIcon(feedback);

		LinearLayout questionsLayout = (LinearLayout) findViewById(R.id.questions_layout);
	    buildQuestionsLayout(questionsLayout, feedback);
    }

	private void buildQuestionsLayout(LinearLayout questionsLayout, Feedback feedback) {
		questionsLayout.removeAllViews();
		for (FeedbackQuestion question : feedback.getQuestions()) {
		    questionsLayout.addView(buildQuestionView(question, feedback));
		}
		sendFeedbackButton.setEnabled(feedback.hasAnsweredQuestions());
	}

	public boolean isFeedbackChanged() {
        return feedbackChanged;
    }

    private void setStateWithoutAnimation(State state) {
        this.state = state;

        switch (state) {
            case Collapsed:
                feedbackExpended.setVisibility(GONE);
                feedbackCollapsed.setVisibility(VISIBLE);
                break;
            case Expended:
                feedbackExpended.setVisibility(VISIBLE);
                feedbackCollapsed.setVisibility(GONE);
        }
    }

    private void setFeedbackIcon(Feedback feedback) {
        Drawable icon;
        FeedbackQuestion.Smiley3PointAnswer weightedRating = feedback.getWeightedRating();
        int filterColor;
        if (weightedRating != null) {
            icon = getResources().getDrawable(weightedRating.getImageResourceId());
            filterColor = getResources().getColor(R.color.yellow);
        } else {
            icon = getResources().getDrawable(R.drawable.feedback);
            filterColor = ThemeAttributes.getColor(getContext(), R.attr.eventFeedbackHighlightedButtonColor);
        }
        icon = icon.mutate();
        icon.setColorFilter(filterColor, PorterDuff.Mode.MULTIPLY);
        feedbackIcon.setImageDrawable(icon);
    }

    private View buildQuestionView(final FeedbackQuestion question, final Feedback feedback) {
        LinearLayout questionLayout = new LinearLayout(getContext());
	    questionLayout.setFocusableInTouchMode(true); // Prevent text edit from getting the focus
        LinearLayout.LayoutParams questionLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        questionLayoutParams.setMargins(0, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20, getResources().getDisplayMetrics()), 0, 0);
        questionLayout.setLayoutParams(questionLayoutParams);

        TextView questionText = new TextView(getContext());
        questionText.setTextAppearance(getContext(), R.style.TextAppearance_AppCompat_Body1);
	    boolean isSent = feedback.isSent();
	    questionText.setText(question.getQuestionText(getResources(), isSent));

        Object answer = question.getAnswer();
        View answerView = null;
        int layoutOrientation = LinearLayout.VERTICAL;

        switch (question.getAnswerType()) {
            case TEXT: {
                if (isSent) {
                    // Display in a text view
                    TextView textView = new TextView(getContext());
                    LinearLayout.LayoutParams textViewLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    textView.setLayoutParams(textViewLayoutParams);
                    textView.setTextAppearance(getContext(), R.style.TextAppearance_AppCompat_Body1);
                    if (answer != null) {
                        textView.setText(answer.toString());
                    }

                    answerView = textView;
                } else {
                    // Display in an editable text
                    EditText editText = new EditText(getContext());
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
                    editText.setTextAppearance(getContext(), R.style.TextAppearance_AppCompat_Body1);
                    editText.addTextChangedListener(new TextWatcher() {
	                    @Override
	                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
	                    }

	                    @Override
	                    public void onTextChanged(CharSequence s, int start, int before, int count) {
	                    }

	                    @Override
	                    public void afterTextChanged(Editable s) {
		                    question.setAnswer(s.toString());
		                    sendFeedbackButton.setEnabled(feedback.hasAnsweredQuestions());
		                    feedbackChanged |= question.isAnswerChanged();
	                    }
                    });

                    if (answer != null) {
	                    editText.setText(answer.toString());
                    }

                    answerView = editText;
                }

                break;
            }
            case SMILEY_3_POINTS: {
                LinearLayout imagesLayout = new LinearLayout(getContext());
                imagesLayout.setOrientation(LinearLayout.HORIZONTAL);
                int margin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20, getResources().getDisplayMetrics());
                int size = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, getResources().getDisplayMetrics());

                ColorMatrix matrix = new ColorMatrix();
                matrix.setSaturation(0);
                final ColorMatrixColorFilter grayscale = new ColorMatrixColorFilter(matrix);

                final ImageView negativeRating = new ImageView(getContext());
                LinearLayout.LayoutParams negativeLayoutParams = new LinearLayout.LayoutParams(size, size);
                negativeLayoutParams.setMarginEnd(margin);
                negativeRating.setLayoutParams(negativeLayoutParams);
                negativeRating.setImageResource(FeedbackQuestion.Smiley3PointAnswer.NEGATIVE.getImageResourceId());
                negativeRating.setColorFilter(grayscale);
                imagesLayout.addView(negativeRating);

                final ImageView positiveRating = new AspectRatioImageView(getContext());
                LinearLayout.LayoutParams positiveLayoutParams = new LinearLayout.LayoutParams(size, size);
                positiveLayoutParams.setMarginEnd(margin);
                positiveRating.setLayoutParams(positiveLayoutParams);
                positiveRating.setImageResource(FeedbackQuestion.Smiley3PointAnswer.POSITIVE.getImageResourceId());
                positiveRating.setColorFilter(grayscale);
                imagesLayout.addView(positiveRating);

                final ImageView veryPositiveRating = new AspectRatioImageView(getContext());
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
                        feedbackChanged |= question.isAnswerChanged();
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
                LinearLayout buttonsLayout = new LinearLayout(getContext());
                buttonsLayout.setOrientation(LinearLayout.HORIZONTAL);

                final TextView yesButton = new TextView(getContext());
                yesButton.setTextAppearance(getContext(), R.style.EventFeedbackButton);
                yesButton.setText(R.string.yes);
                int padding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, getResources().getDisplayMetrics());
                yesButton.setPaddingRelative(0, padding, padding * 4, padding);
                buttonsLayout.addView(yesButton);

                final TextView noButton = new TextView(getContext());
                noButton.setTextAppearance(getContext(), R.style.EventFeedbackButton);
                noButton.setText(R.string.no);
                noButton.setPaddingRelative(padding * 4, padding, padding * 4, padding);
                buttonsLayout.addView(noButton);

                View.OnClickListener listener = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sendFeedbackButton.setEnabled(true);
                        TextView selected = (TextView) v;
                        selected.setTextAppearance(getContext(), R.style.EventAnswerButtonHighlighted);

                        TextView otherButton = (selected == yesButton ? noButton : yesButton);
                        otherButton.setTextAppearance(getContext(), R.style.EventFeedbackButton);
                        question.setAnswer(selected.getText().toString());
                        feedbackChanged |= question.isAnswerChanged();
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

    private void calculateExpendedFeedbackHeight() {
        final ViewGroup expendedFeedbackLayout = (ViewGroup) LayoutInflater.from(this.getContext()).inflate(R.layout.feedback_layout_expanded, null);

        // Position the hidden view outside the bounds of the screen
        expendedFeedbackLayout.setX(getScreenWidth());

        // Add the view to the screen
        final ViewGroup root = (ViewGroup)findViewById(R.id.feedback_container);
        root.addView(expendedFeedbackLayout);

        // Add all the questions to the view
        LinearLayout questionsLayout = (LinearLayout) expendedFeedbackLayout.findViewById(R.id.questions_layout);
	    buildQuestionsLayout(questionsLayout, feedback);

        // Wait for the layout onMeasure to be called, get its height, and then remove it from the screen
        expendedFeedbackLayout.post(new Runnable() {
            @Override
            public void run() {
                expendedFeedbackLayoutHeight = expendedFeedbackLayout.getMeasuredHeight();
                root.removeView(expendedFeedbackLayout);
            }
        });
    }

    public void calculateCollapsedFeedbackHeight() {
        final ViewGroup collapsedFeedbackLayout = (ViewGroup) LayoutInflater.from(this.getContext()).inflate(R.layout.feedback_layout_collapsed, null);

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

    private void resizeFeedbackContainer(State state) {
        int currentHeight = feedbackContainer.getHeight();
        int targetHeight = 0;
        ViewGroup layoutBeforeResize = null;
        ViewGroup layoutAfterResize = null;
        switch (state) {
            case Expended:
                targetHeight = expendedFeedbackLayoutHeight;
                layoutBeforeResize = feedbackCollapsed;
                layoutAfterResize = feedbackExpended;
                break;
            case Collapsed:
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
                finalLayoutAfterResize.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.fab_in));
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

    private int getScreenWidth() {
        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size.x;
    }

    public enum State {
        Collapsed,
        Expended
    }
}

package amai.org.conventions.events;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.TextViewCompat;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.AttributeSet;
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
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import amai.org.conventions.R;
import amai.org.conventions.ThemeAttributes;
import amai.org.conventions.customviews.AspectRatioImageView;
import amai.org.conventions.model.Convention;
import amai.org.conventions.model.Feedback;
import amai.org.conventions.model.FeedbackQuestion;
import amai.org.conventions.utils.FeedbackMail;

public class CollapsibleFeedbackView extends FrameLayout {

    private State state;
    private TextView collapsedFeedbackTitle;
    private Button openFeedbackButton;
    private Button sendFeedbackButton;
    private ImageView feedbackIcon;
    private TextView feedbackSentText;
    private ViewGroup feedbackCollapsed;
    private ViewGroup feedbackExpended;
    private ViewGroup feedbackContainer;
    private ProgressBar progressBar;

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
        feedbackSentText = (TextView) findViewById(R.id.feedback_sent_text);
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

	public void refresh() {
		setModel(feedback);
	}

    public void setModel(Feedback feedback) {
        this.feedback = feedback;

        if (feedback.isSent()) {
            collapsedFeedbackTitle.setText(getContext().getString(R.string.feedback_sent));
            openFeedbackButton.setText(getContext().getString(R.string.display_feedback));
            sendFeedbackButton.setVisibility(View.GONE);
            feedbackSentText.setVisibility(View.VISIBLE);
        } else if (Convention.getInstance().isFeedbackSendingTimeOver()) {
	        collapsedFeedbackTitle.setText(getContext().getString(R.string.feedback_sending_time_over));
	        openFeedbackButton.setText(getContext().getString(R.string.display));
	        sendFeedbackButton.setVisibility(View.GONE);
	        feedbackSentText.setVisibility(View.VISIBLE);
	        feedbackSentText.setText(getContext().getString(R.string.feedback_sending_time_over));
        }

        setFeedbackIcon(feedback);

		LinearLayout questionsLayout = (LinearLayout) findViewById(R.id.questions_layout);
	    buildQuestionsLayout(questionsLayout, feedback);
    }

	public void setSendFeedbackClickListener(OnClickListener listener) {
		sendFeedbackButton.setOnClickListener(listener);
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
	        case ExpandedHeadless:
		        findViewById(R.id.feedback_expanded_title).setVisibility(GONE);
		        feedbackExpended.setVisibility(VISIBLE);
		        feedbackCollapsed.setVisibility(GONE);
		        break;
            case Expanded:
	            findViewById(R.id.feedback_expanded_title).setVisibility(VISIBLE);
                feedbackExpended.setVisibility(VISIBLE);
                feedbackCollapsed.setVisibility(GONE);
        }
    }

    private void setFeedbackIcon(Feedback feedback) {
        Drawable icon;
        FeedbackQuestion.Smiley3PointAnswer weightedRating = feedback.getWeightedRating();
        int filterColor;
        if (weightedRating != null) {
            icon = ContextCompat.getDrawable(getContext(), weightedRating.getImageResourceId());
            filterColor = ContextCompat.getColor(getContext(), R.color.yellow);
        } else {
            icon = ContextCompat.getDrawable(getContext(), R.drawable.feedback);
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
        questionLayoutParams.setMargins(0, getResources().getDimensionPixelOffset(R.dimen.feedback_question_top_margin), 0, 0);
        questionLayout.setLayoutParams(questionLayoutParams);

        TextView questionText = new TextView(getContext());
	    TextViewCompat.setTextAppearance(questionText, R.style.FeedbackQuestionTextAppearance);
	    boolean isSent = feedback.isSent();
	    questionText.setText(question.getQuestionText(getResources(), isSent));

        View answerView = null;
        int layoutOrientation = LinearLayout.VERTICAL;

        switch (question.getAnswerType()) {
            case TEXT: {
	            answerView = buildTextAnswerView(question, feedback);
	            break;
            }
            case SMILEY_3_POINTS: {
	            answerView = buildSmiley3PointsAnswerView(question, feedback);
                break;
            }
	        case MULTIPLE_ANSWERS: {
		        answerView = buildMultiAnswerView(question, feedback, question.getMultipleAnswers(), false);
		        break;
	        }
	        case MULTIPLE_ANSWERS_RADIO: {
		        answerView = buildMultiAnswerView(question, feedback, question.getMultipleAnswers(), true);
		        break;
	        }
        }

        questionLayout.setOrientation(layoutOrientation);
        questionLayout.addView(questionText);
        questionLayout.addView(answerView);

        return questionLayout;
    }

	private View buildSmiley3PointsAnswerView(final FeedbackQuestion question, final Feedback feedback) {
		LinearLayout imagesLayout = new LinearLayout(getContext());
		imagesLayout.setOrientation(LinearLayout.HORIZONTAL);
		int margin = getResources().getDimensionPixelOffset(R.dimen.feedback_smiley_answer_margin);
		int size = getResources().getDimensionPixelSize(R.dimen.feedback_smiley_icon_size);

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

		OnClickListener listener = new OnClickListener() {
		    @Override
		    public void onClick(View v) {
		        List<ImageView> allImages = new ArrayList<>(Arrays.asList(negativeRating, positiveRating, veryPositiveRating));
		        for (ImageView otherImage : allImages) {
		            otherImage.setColorFilter(grayscale);
		        }

		        ImageView selected = (ImageView) v;
		        Object selectedAnswer = null;
		        if (selected == negativeRating) {
		            selectedAnswer = FeedbackQuestion.Smiley3PointAnswer.NEGATIVE;
		        } else if (selected == positiveRating) {
		            selectedAnswer = FeedbackQuestion.Smiley3PointAnswer.POSITIVE;
		        } else if (selected == veryPositiveRating) {
		            selectedAnswer = FeedbackQuestion.Smiley3PointAnswer.VERY_POSITIVE;
		        }

			    // If the user clicked on the same answer, remove the answer
			    if (selectedAnswer == question.getAnswer()) {
				    selectedAnswer = null;
			    }

			    if (selectedAnswer != null) {
		            selected.setColorFilter(ContextCompat.getColor(getContext(), R.color.yellow), PorterDuff.Mode.MULTIPLY);
			    }

		        question.setAnswer(selectedAnswer);
		        feedbackChanged |= question.isAnswerChanged();
		        sendFeedbackButton.setEnabled(feedback.hasAnsweredQuestions());
		    }
		};

		if (!feedback.isSent()) {
		    negativeRating.setOnClickListener(listener);
		    positiveRating.setOnClickListener(listener);
		    veryPositiveRating.setOnClickListener(listener);
		} else {
		    negativeRating.setOnClickListener(null);
		    positiveRating.setOnClickListener(null);
		    veryPositiveRating.setOnClickListener(null);
		}

		Object answer = question.getAnswer();
		if (answer != null) {
		    FeedbackQuestion.Smiley3PointAnswer smileyAnswer = FeedbackQuestion.Smiley3PointAnswer.getByAnswerText(answer.toString());
		    if (smileyAnswer != null) {
			    question.setAnswer(null); // Needed so onClick won't cancel the answer selection
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
		return imagesLayout;
	}

	private View buildTextAnswerView(final FeedbackQuestion question, final Feedback feedback) {
		Object answer = question.getAnswer();
		View answerView;
		if (feedback.isSent()) {
		    // Display in a text view
		    TextView textView = new TextView(getContext());
		    LinearLayout.LayoutParams textViewLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		    textView.setLayoutParams(textViewLayoutParams);
			TextViewCompat.setTextAppearance(textView, R.style.FeedbackQuestionTextAppearance);
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
			TextViewCompat.setTextAppearance(editText, R.style.FeedbackQuestionTextAppearance);
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
		return answerView;
	}

	private View buildMultiAnswerView(final FeedbackQuestion question, final Feedback feedback, List<Integer> possibleAnswers, final boolean radio) {
		Object answer = question.getAnswer();
		LinearLayout buttonsLayout;
		final ViewGroup mainView;
		if (radio) {
			buttonsLayout = new RadioGroup(getContext());
			mainView = buttonsLayout;
			buttonsLayout.setOrientation(LinearLayout.VERTICAL);
		} else {
			buttonsLayout = new LinearLayout(getContext());
			buttonsLayout.setOrientation(LinearLayout.HORIZONTAL);
			mainView = new HorizontalScrollView(getContext());
			mainView.addView(buttonsLayout);
		}

		final List<TextView> answerViews = new ArrayList<>(possibleAnswers.size());

		OnClickListener listener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				TextView selected = (TextView) v;
				TextViewCompat.setTextAppearance(selected, R.style.EventAnswerButtonHighlighted);

				// If the user selected the same answer, deselect it
				String selectedAnswer = selected.getText().toString();
				if (selectedAnswer.equals(question.getAnswer())) {
					selectedAnswer = null;
				}
				// Ensure radio buttons are synchronized with user selection
				if (radio) {
					RadioGroup radioGroup = (RadioGroup) mainView;
					if (selectedAnswer == null) {
						radioGroup.clearCheck();
					} else {
						radioGroup.check(v.getId());
					}
				}

				for (TextView answerView : answerViews) {
					if (selectedAnswer == null || selected != answerView) {
						TextViewCompat.setTextAppearance(answerView, R.style.EventAnswerButton);
					}
				}

				question.setAnswer(selectedAnswer);
				feedbackChanged |= question.isAnswerChanged();
				sendFeedbackButton.setEnabled(feedback.hasAnsweredQuestions());
			}
		};

		int padding = getResources().getDimensionPixelOffset(R.dimen.feedback_multi_answer_padding);
		boolean first = true;
		for (int answerStringId : possibleAnswers) {
			final TextView answerButton;
			if (radio) {
				answerButton = new RadioButton(getContext());
			} else {
				answerButton = new TextView(getContext());
			}
			answerViews.add(answerButton);
			TextViewCompat.setTextAppearance(answerButton, R.style.EventAnswerButton);
			answerButton.setText(answerStringId);
			int endPadding = padding * 4;
			int startPadding = padding * 4;
			if ((!radio) && first) {
				startPadding = 0;
				first = false;
			}
			answerButton.setPaddingRelative(startPadding, padding, endPadding, padding);
			buttonsLayout.addView(answerButton);
			if (!feedback.isSent()) {
				answerButton.setOnClickListener(listener);
			} else {
				answerButton.setOnClickListener(null);
				// Don't allow selecting a radio button after the feedback is already sent
				answerButton.setEnabled(false);
			}
		}

		if (answer != null) {
			for (TextView answerView : answerViews) {
				if (answer.equals(answerView.getText().toString())) {
					question.setAnswer(null); // Needed so onClick won't cancel the answer selection
					listener.onClick(answerView);
					break;
				}
			}
		}
		return mainView;
	}

	private int calculateExpendedFeedbackHeight() {
        ViewGroup expendedFeedbackLayout = (ViewGroup) LayoutInflater.from(this.getContext()).inflate(R.layout.feedback_layout_expanded, feedbackContainer, false);

        // Add all the questions to the view
        LinearLayout questionsLayout = (LinearLayout) expendedFeedbackLayout.findViewById(R.id.questions_layout);
	    buildQuestionsLayout(questionsLayout, feedback);

        return calculateViewHeight(expendedFeedbackLayout);
    }

    private int calculateCollapsedFeedbackHeight() {
        return calculateViewHeight(LayoutInflater.from(this.getContext()).inflate(R.layout.feedback_layout_collapsed, feedbackContainer, false));
    }

    private int calculateViewHeight(View view) {
        Point screenSize = getScreenSize();

        view.measure(screenSize.x, screenSize.y);
        return view.getMeasuredHeight();
    }

    private void resizeFeedbackContainer(State state) {
        int currentHeight = feedbackContainer.getHeight();
        int targetHeight = 0;
        ViewGroup layoutBeforeResize = null;
        ViewGroup layoutAfterResize = null;

        // Calculate the heights of the collapsed/expended states of the feedback view, since they are dynamic (based on the number of questions), and
        // we need the heights pre-calculated before the animation to be able to properly animate the transitions and restructure of the layout.
        // Note -
        // The calculation is done by re-inflating the layout (which is not very efficient), since only one of the collapsed/expended layouts is visible
        // at any given time, and the other is in GONE state so the rest of the views will be positioned properly.
        switch (state) {
            case Expanded:
                targetHeight = calculateExpendedFeedbackHeight();
                layoutBeforeResize = feedbackCollapsed;
                layoutAfterResize = feedbackExpended;
                break;
            case Collapsed:
                targetHeight = calculateCollapsedFeedbackHeight();
                layoutBeforeResize = feedbackExpended;
                layoutAfterResize = feedbackCollapsed;
                break;
	        case ExpandedHeadless:
		        throw new RuntimeException("ExpandedHeadless state is not supported for animation");
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
                finalLayoutAfterResize.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.fade_in));
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

    private Point getScreenSize() {
        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size;
    }

    public enum State {
        Collapsed,
	    Expanded,
	    ExpandedHeadless
    }

	public abstract class CollapsibleFeedbackViewSendMailListener extends FeedbackMail.SendEventMailOnClickListener {
		protected abstract void saveFeedback();

		protected void onSuccess() {
			// Refresh the feedback UI so interactions will now be disabled in it
			refresh();
		}

		@Override
		protected void beforeStart() {
			setProgressBarVisibility(true);
		}

		@Override
		protected void beforeSend() {
			// First save the feedback before sending it
			saveFeedback();
		}

		@Override
		protected void afterSend() {
			saveFeedback();
		}

		@Override
		protected void afterEnd(Exception exception) {
			setProgressBarVisibility(false);
		}
	}
}

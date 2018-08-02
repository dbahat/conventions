package amai.org.conventions.events;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.annotation.StringRes;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.CompoundButtonCompat;
import android.support.v4.widget.TextViewCompat;
import android.support.v7.widget.AppCompatRadioButton;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import amai.org.conventions.ConventionsApplication;
import amai.org.conventions.R;
import amai.org.conventions.ThemeAttributes;
import amai.org.conventions.customviews.AspectRatioImageView;
import amai.org.conventions.feedback.SurveySender;
import amai.org.conventions.feedback.forms.SurveyDisabledException;
import amai.org.conventions.model.FeedbackQuestion;
import amai.org.conventions.model.Survey;
import amai.org.conventions.model.conventions.Convention;
import amai.org.conventions.utils.Log;
import amai.org.conventions.utils.Views;

public class CollapsibleFeedbackView extends FrameLayout {
	private static final String TAG = CollapsibleFeedbackView.class.getCanonicalName();

	private State state;
	private TextView feedbackLayoutTitle;
	private TextView collapsedFeedbackTitle;
	private Button openFeedbackButton;
	private Button sendFeedbackButton;
	private ImageView feedbackIcon;
	private TextView feedbackSentText;
	private ViewGroup feedbackCollapsed;
	private ViewGroup feedbackExpended;
	private ViewGroup feedbackContainer;
	private ProgressBar progressBar;
	private List<TextView> generatedQuestionTextViews = new LinkedList<>();

	private Survey feedback;
	private boolean feedbackChanged;
	private int textColor = Convention.NO_COLOR;
	private int feedbackSentTextResource = R.string.feedback_sent;
	private int feedbackSendErrorMessage = R.string.feedback_send_failed;

	public CollapsibleFeedbackView(Context context, AttributeSet attrs) {
		super(context, attrs);

		LayoutInflater.from(this.getContext()).inflate(R.layout.view_collapsible_feedback_layout, this, true);

		state = State.Collapsed;
		feedbackChanged = false;

		feedbackLayoutTitle = (TextView) findViewById(R.id.feedback_layout_title);
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

	public State getState() {
		return state;
	}

	public void setState(State state) {
		this.state = state;

		resizeFeedbackContainer(state);
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

	public void setModel(Survey feedback) {
		this.feedback = feedback;
		if (feedback == null) {
			return;
		}

		if (feedback.isSent()) {
			collapsedFeedbackTitle.setText(getContext().getString(R.string.feedback_sent));
			openFeedbackButton.setText(getContext().getString(R.string.display_feedback));
			sendFeedbackButton.setVisibility(View.GONE);
			feedbackSentText.setVisibility(View.VISIBLE);
			feedbackSentText.setText(feedbackSentTextResource);
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
		setTextColor(textColor);
	}

	public void setFeedbackSentText(@StringRes int stringResource) {
		feedbackSentTextResource = stringResource;
		refresh();
	}

	public void setFeedbackSendErrorMessage(@StringRes int stringResource) {
		feedbackSendErrorMessage = stringResource;
	}

	public void setTextColor(int color) {
		textColor = color;
		if (color != Convention.NO_COLOR) {
			collapsedFeedbackTitle.setTextColor(color);
			feedbackSentText.setTextColor(color);
			feedbackLayoutTitle.setTextColor(color);
			for (TextView textView : generatedQuestionTextViews) {
				textView.setTextColor(color);
			}
		}
	}

	public void setSendFeedbackClickListener(OnClickListener listener) {
		sendFeedbackButton.setOnClickListener(listener);
	}

	private void buildQuestionsLayout(LinearLayout questionsLayout, Survey feedback) {
		generatedQuestionTextViews.clear();
		questionsLayout.removeAllViews();
		for (FeedbackQuestion question : feedback.getQuestions()) {
			if (question.getAnswerType() == FeedbackQuestion.AnswerType.HIDDEN) {
				continue;
			}
			questionsLayout.addView(buildQuestionView(question, feedback));
		}
		updateSendButtonEnabledState(feedback);
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

	private void setFeedbackIcon(Survey feedback) {
		Drawable icon;
		FeedbackQuestion.Smiley3PointAnswer weightedRating = feedback.getWeightedRating();
		int filterColor;
		if (weightedRating != null) {
			icon = ContextCompat.getDrawable(getContext(), weightedRating.getImageResourceId());
			filterColor = ContextCompat.getColor(getContext(), R.color.yellow);
		} else {
			icon = ContextCompat.getDrawable(getContext(), R.drawable.feedback);
			filterColor = ThemeAttributes.getColor(getContext(), R.attr.feedbackButtonColor);
		}
		icon = icon.mutate();
		icon.setColorFilter(filterColor, PorterDuff.Mode.MULTIPLY);
		feedbackIcon.setImageDrawable(icon);
	}

	private View buildQuestionView(final FeedbackQuestion question, final Survey feedback) {
		LinearLayout questionLayout = new LinearLayout(getContext());
		questionLayout.setFocusableInTouchMode(true); // Prevent text edit from getting the focus
		LinearLayout.LayoutParams questionLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		questionLayoutParams.setMargins(0, getResources().getDimensionPixelOffset(R.dimen.feedback_question_top_margin), 0, 0);
		questionLayout.setLayoutParams(questionLayoutParams);

		TextView questionText = new TextView(getContext());
		generatedQuestionTextViews.add(questionText);
		TextViewCompat.setTextAppearance(questionText, R.style.FeedbackQuestionTextAppearance);
		boolean isSent = feedback.isSent();
		questionText.setText(question.getQuestionText(getResources(), isSent));

		View answerView = null;
		int layoutOrientation = LinearLayout.VERTICAL;

		switch (question.getAnswerType()) {
			case TEXT:
			case SINGLE_LINE_TEXT: {
				answerView = buildTextAnswerView(question, feedback, (question.getAnswerType() == FeedbackQuestion.AnswerType.TEXT));
				break;
			}
			case SMILEY_3_POINTS: {
				answerView = buildSmiley3PointsAnswerView(question, feedback);
				break;
			}
			case MULTIPLE_ANSWERS:
			case MULTIPLE_ANSWERS_RADIO: {
				answerView = buildMultiAnswerView(question, feedback, question.getPossibleMultipleAnswers(getResources()),
						(question.getAnswerType() == FeedbackQuestion.AnswerType.MULTIPLE_ANSWERS_RADIO));
				break;
			}
		}

		questionLayout.setOrientation(layoutOrientation);
		questionLayout.addView(questionText);
		questionLayout.addView(answerView);

		return questionLayout;
	}

	private View buildSmiley3PointsAnswerView(final FeedbackQuestion question, final Survey feedback) {
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
				updateSendButtonEnabledState(feedback);
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

	private View buildTextAnswerView(final FeedbackQuestion question, final Survey feedback, boolean multiline) {
		Object answer = question.getAnswer();
		View answerView;
		if (feedback.isSent()) {
			// Display in a text view
			TextView textView = new TextView(getContext());
			generatedQuestionTextViews.add(textView);
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
			generatedQuestionTextViews.add(editText);
			editText.setFreezesText(true);
			int type = InputType.TYPE_CLASS_TEXT |
					InputType.TYPE_TEXT_FLAG_AUTO_CORRECT |
					InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS;
			if (multiline) {
				type |= InputType.TYPE_TEXT_VARIATION_LONG_MESSAGE |
						InputType.TYPE_TEXT_FLAG_MULTI_LINE;
			} else {
				type |= InputType.TYPE_TEXT_VARIATION_NORMAL;
			}
			editText.setInputType(type);
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
					updateSendButtonEnabledState(feedback);
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

	private View buildMultiAnswerView(final FeedbackQuestion question, final Survey feedback, List<String> possibleAnswers, final boolean radio) {
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
				updateSendButtonEnabledState(feedback);
			}
		};

		int padding = getResources().getDimensionPixelOffset(R.dimen.feedback_multi_answer_padding);
		boolean first = true;
		for (String answerString : possibleAnswers) {
			final TextView answerButton;
			if (radio) {
				answerButton = new AppCompatRadioButton(getContext());
				CompoundButtonCompat.setButtonTintList((RadioButton) answerButton, new ColorStateList(
						new int[][]{
								new int[]{-android.R.attr.state_checked},
								new int[]{android.R.attr.state_checked}
						},
						new int[]{
								ThemeAttributes.getColor(getContext(), R.attr.feedbackAnswerButtonColor),
								ThemeAttributes.getColor(getContext(), R.attr.feedbackAnswerButtonSelectedColor)
						}
				));
			} else {
				answerButton = new TextView(getContext());
			}
			answerViews.add(answerButton);
			TextViewCompat.setTextAppearance(answerButton, R.style.EventAnswerButton);
			answerButton.setText(answerString);
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

	private void updateSendButtonEnabledState(Survey feedback) {
		sendFeedbackButton.setEnabled(feedback.hasAnsweredQuestions() && feedback.areAllRequiredQuestionsAnswered());
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
		Point screenSize = Views.getScreenSize(getContext());

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

	public enum State {
		Collapsed,
		Expanded,
		ExpandedHeadless
	}

	public abstract class CollapsibleFeedbackViewSendListener extends SurveySender.SendSurveyOnClickListener {
		protected abstract void saveFeedback();

		protected void onSuccess() {
			// Refresh the feedback UI so interactions will now be disabled in it
			refresh();
			sendUserSentFeedbackTelemetry(true, null);
		}

		@Override
		protected void onFailure(Exception exception) {
			if (exception instanceof SurveyDisabledException) {
				SurveyDisabledException surveyDisabledException = (SurveyDisabledException) exception;
				String toastMessage = TextUtils.isEmpty(surveyDisabledException.getDisabledErrorMessage())
						? getContext().getString(R.string.vote_send_error)
						: surveyDisabledException.getDisabledErrorMessage();
				Toast.makeText(getContext(), toastMessage, Toast.LENGTH_LONG).show();
				// Using success because the problem here is not in the application
				sendUserSentFeedbackTelemetry(true, exception);
				return;
			}

			Log.w(TAG, "Failed to send feedback. Reason: " + exception.getClass().getSimpleName() + ": " + exception.getMessage());
			Toast.makeText(getContext(), feedbackSendErrorMessage, Toast.LENGTH_LONG).show();
			sendUserSentFeedbackTelemetry(false, exception);
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

		private void sendUserSentFeedbackTelemetry(boolean success, Exception e) {
			String message = success ? "success" : "failure";
			if (e != null) {
				message += " - " + e.getClass().getSimpleName() + ": " + e.getMessage();
			}

			ConventionsApplication.sendTrackingEvent(new HitBuilders.EventBuilder()
					.setCategory("Feedback")
					.setAction("SendAttempt")
					.setLabel(message)
					.build());
		}
	}
}

package amai.org.conventions.events;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import amai.org.conventions.R;
import amai.org.conventions.ThemeAttributes;
import amai.org.conventions.customviews.AspectRatioImageView;
import amai.org.conventions.model.Convention;
import amai.org.conventions.model.Feedback;
import amai.org.conventions.model.FeedbackQuestion;
import amai.org.conventions.utils.Dates;
import amai.org.conventions.utils.GMailSender;

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
	        case MULTIPLE_ANSWERS: {
		        answerView = buildMultiAnswerQuestion(question, isSent, question.getMultipleAnswers());
		        break;
	        }
        }

        questionLayout.setOrientation(layoutOrientation);
        questionLayout.addView(questionText);
        questionLayout.addView(answerView);

        return questionLayout;
    }

	private LinearLayout buildMultiAnswerQuestion(final FeedbackQuestion question, boolean isSent, List<Integer> possibleAnswers) {
		Object answer = question.getAnswer();
		LinearLayout buttonsLayout = new LinearLayout(getContext());
		buttonsLayout.setOrientation(LinearLayout.HORIZONTAL);

		final List<TextView> answerViews = new ArrayList<>(possibleAnswers.size());

		OnClickListener listener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				sendFeedbackButton.setEnabled(true);
				TextView selected = (TextView) v;
				selected.setTextAppearance(getContext(), R.style.EventAnswerButtonHighlighted);

				for (TextView answerView : answerViews) {
					if (selected != answerView) {
						answerView.setTextAppearance(getContext(), R.style.EventFeedbackButton);
					}
				}

				question.setAnswer(selected.getText().toString());
				feedbackChanged |= question.isAnswerChanged();
			}
		};

		int padding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, getResources().getDisplayMetrics());
		boolean first = true;
		for (int answerStringId : possibleAnswers) {
			final TextView answerButton = new TextView(getContext());
			answerViews.add(answerButton);
			answerButton.setTextAppearance(getContext(), R.style.EventFeedbackButton);
			answerButton.setText(answerStringId);
			int startPadding = padding * 4;
			if (first) {
				startPadding = 0;
				first = false;
			}
			answerButton.setPaddingRelative(startPadding, padding, padding * 4, padding);
			buttonsLayout.addView(answerButton);
			if (!isSent) {
				answerButton.setOnClickListener(listener);
			} else {
				answerButton.setOnClickListener(null);
			}
		}

		if (answer != null) {
			for (TextView answerView : answerViews) {
				if (answer.equals(answerView.getText().toString())) {
					listener.onClick(answerView);
				}
			}
		}
		return buttonsLayout;
	}

	private int calculateExpendedFeedbackHeight() {
        ViewGroup expendedFeedbackLayout = (ViewGroup) LayoutInflater.from(this.getContext()).inflate(R.layout.feedback_layout_expanded, null);

        // Add all the questions to the view
        LinearLayout questionsLayout = (LinearLayout) expendedFeedbackLayout.findViewById(R.id.questions_layout);
	    buildQuestionsLayout(questionsLayout, feedback);

        return calculateViewHeight(expendedFeedbackLayout);
    }

    private int calculateCollapsedFeedbackHeight() {
        return calculateViewHeight(LayoutInflater.from(this.getContext()).inflate(R.layout.feedback_layout_collapsed, null));
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
		        throw new RuntimeException("ExpendadHeadless state is not supported for animation");
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

	public String getFormattedQuestions() {
		StringBuilder stringBuilder = new StringBuilder();
		for (FeedbackQuestion question : feedback.getQuestions()) {
			if (question.hasAnswer()) {
				stringBuilder.append(String.format(Dates.getLocale(), "%s\n%s\n\t\n\t\n",
						question.getQuestionText(getResources(), feedback.isSent()),
						question.getAnswer()));
			}
		}

		return stringBuilder.toString();
	}

	public abstract class SendMailOnClickListener implements OnClickListener {
		protected abstract void saveFeedback();

		protected abstract String getMailSubject();
		protected abstract String getMailBody();
		protected String getMailRecipient() {
			return Convention.getInstance().getFeedbackRecipient();
		}

		protected void onSuccess() {
			// Refresh the feedback UI so interactions will now be disabled in it
			refresh();
		}
		protected void onFailure(String errorMessage) {
		}

		@Override
		public void onClick(View v) {
			setProgressBarVisibility(true);

			new AsyncTask<Void, Void, String>() {

				@Override
				protected String doInBackground(Void... params) {
					// First save the feedback before sending it
					saveFeedback();

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
								getMailSubject(),
								getMailBody(),
								mail,
								getMailRecipient());
					} catch (Exception e) {
						return e.getMessage();
					}

					feedback.setIsSent(true);
					feedback.removeUnansweredQuestions();
					saveFeedback();

					// In case everything finished successfully, pass null to onPostExecute.
					return null;
				}

				@Override
				protected void onPostExecute(String errorMessage) {
					setProgressBarVisibility(false);

					if (errorMessage != null) {
						onFailure(errorMessage);
					} else {
						onSuccess();
					}
				}

			}.execute();

		}
	}
}

package amai.org.conventions;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;

import java.util.Collections;
import java.util.List;

import amai.org.conventions.events.CollapsibleFeedbackView;
import amai.org.conventions.events.activities.EventActivity;
import amai.org.conventions.events.activities.ProgrammeActivity;
import amai.org.conventions.events.adapters.EventsViewWithDateHeaderAdapter;
import amai.org.conventions.model.conventions.Convention;
import amai.org.conventions.model.ConventionEvent;
import amai.org.conventions.model.ConventionEventEndTimeComparator;
import amai.org.conventions.navigation.NavigationActivity;
import amai.org.conventions.utils.CollectionUtils;
import amai.org.conventions.utils.ConventionFeedbackMail;
import amai.org.conventions.utils.EventFeedbackMail;
import amai.org.conventions.utils.FeedbackMail;
import amai.org.conventions.utils.Log;


public class FeedbackActivity extends NavigationActivity {
	private final static String TAG = FeedbackActivity.class.getCanonicalName();

	private final static int ITEM_PROGRESS = 100;
	private static final String STATE_SENT_FEEDBACK_OPEN = "StateEventsWithSentFeedbackOpen";

	private CollapsibleFeedbackView feedbackView;
	private ListView eventsWithoutFeedbackList;
	private ViewGroup eventsWithoutFeedbackLayout;
	private ViewGroup eventsWithoutFeedbackListLayout;

	private TextView sendAllExplanation;
	private ViewGroup sendAllButtonLayout;
	private Button sendAllButton;
	private ProgressBar sendAllProgress;

	private ListView eventsWithSentFeedbackList;
	private ViewGroup eventsWithSentFeedbackLayout;
	private View noEventsText;

	private List<ConventionEvent> eventsWithoutFeedback;
	private List<ConventionEvent> eventsWithSentFeedback;
	private List<ConventionEvent> eventsWithUnsentFeedback;
	private Button showHideEventsWithSentFeedback;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentInContentContainer(R.layout.activity_feedback);
		setToolbarTitle(getString(R.string.feedback));

		eventsWithoutFeedbackLayout = (ViewGroup) findViewById(R.id.events_without_feedback);
		eventsWithoutFeedbackList = (ListView) findViewById(R.id.events_without_feedback_list);
		eventsWithoutFeedbackListLayout = (ViewGroup) findViewById(R.id.events_without_feedback_list_layout);

		sendAllExplanation = (TextView) findViewById(R.id.send_all_explanation);
		sendAllButtonLayout = (ViewGroup) findViewById(R.id.send_all_button_layout);
		sendAllButton = (Button) findViewById(R.id.send_all_button);
		sendAllProgress = (ProgressBar) findViewById(R.id.send_all_progress_bar);

		eventsWithSentFeedbackLayout = (ViewGroup) findViewById(R.id.events_with_sent_feedback);
		eventsWithSentFeedbackList = (ListView) findViewById(R.id.events_with_sent_feedback_list);

		noEventsText = findViewById(R.id.feedback_no_events_text);

		setupConventionFeedbackView();

		sendAllButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				setSendAllProgressBarVisibility(true);
				// This value is large for the progress animation
				sendAllProgress.setMax(eventsWithUnsentFeedback.size() * ITEM_PROGRESS);
				sendAllProgress.setProgress(0);

				new AsyncTask<Void, Void, Exception>() {
					private ObjectAnimator progressAnimation;

					@Override
					protected Exception doInBackground(Void... params) {
						publishProgress();
						Exception exception = null;
						for (ConventionEvent event : eventsWithUnsentFeedback) {
							try {

								FeedbackMail mail = new EventFeedbackMail(FeedbackActivity.this, event);
								mail.send();

								Convention.getInstance().getStorage().saveUserInput();
								event.getUserInput().getFeedback().resetChangedAnswers();
								sendUserSentFeedbackTelemetry(true, null);
							} catch (Exception e) {
								exception = e;
								sendUserSentFeedbackTelemetry(false, e);
							}
							publishProgress();
						}
						return exception;
					}

					@Override
					protected void onProgressUpdate(Void... values) {
						int progress = sendAllProgress.getProgress();
						if (progressAnimation != null && progressAnimation.isStarted()) {
							progressAnimation.cancel();
						}
						progressAnimation = ObjectAnimator.ofInt(sendAllProgress, "progress", progress, progress + ITEM_PROGRESS);
						// Assuming sending a mail takes less than 5 seconds on average, we set the animation to be long enough
						// that it wil run until the next update. Even if we update it again before it's finished it will appear
						// smooth because we always start from the previous value.
						progressAnimation.setDuration(5000);
						progressAnimation.setInterpolator(new DecelerateInterpolator());
						progressAnimation.start();
					}

					@Override
					protected void onPostExecute(Exception exception) {
						if (progressAnimation != null && progressAnimation.isStarted()) {
							progressAnimation.cancel();
						}
						setSendAllProgressBarVisibility(false);

						// Even if there was an error, some of the feedbacks might have been sent
						setupEventLists(true);

						if (exception != null) {
							Log.w(TAG, "Failed to send feedback mail. Reason: " + exception.getClass().getSimpleName() + ": " + exception.getMessage());
							Toast.makeText(FeedbackActivity.this, R.string.feedback_send_mail_failed, Toast.LENGTH_LONG).show();
						}
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

				}.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

			}
		});

		showHideEventsWithSentFeedback = (Button) findViewById(R.id.show_hide_events_with_sent_feedback);

		// Set focusable to false to prevent the list view from stealing the focus from the scrollview.
		// For some reason this only works in code and not in the xml.
		eventsWithoutFeedbackList.setFocusable(false);
		eventsWithSentFeedbackList.setFocusable(false);

		if (savedInstanceState != null) {
			boolean open = savedInstanceState.getBoolean(STATE_SENT_FEEDBACK_OPEN, true);
			eventsWithSentFeedbackList.setVisibility(open ? View.VISIBLE : View.GONE);
			showHideEventsWithSentFeedback.setText(open ? R.string.hide : R.string.display);
		}
	}

	private void setSendAllProgressBarVisibility(boolean visible) {
		if (visible) {
			sendAllProgress.setVisibility(View.VISIBLE);
			sendAllButton.setVisibility(View.INVISIBLE);
		} else {
			sendAllProgress.setVisibility(View.GONE);
			sendAllButton.setVisibility(View.VISIBLE);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		setupEventLists(false);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		outState.putBoolean(STATE_SENT_FEEDBACK_OPEN, eventsWithSentFeedbackList.getVisibility() == View.VISIBLE);
		super.onSaveInstanceState(outState);
	}

	private void setupEventsWithSentFeedbackLayout(boolean update) {
		if (eventsWithSentFeedback.size() == 0) {
			eventsWithSentFeedbackLayout.setVisibility(View.GONE);
		} else {
			eventsWithSentFeedbackLayout.setVisibility(View.VISIBLE);

			if (update && eventsWithSentFeedbackList.getAdapter() != null) {
				((EventsViewWithDateHeaderAdapter) eventsWithSentFeedbackList.getAdapter()).setEventsList(eventsWithSentFeedback);
			} else {
				eventsWithSentFeedbackList.setAdapter(new EventsViewWithDateHeaderAdapter(eventsWithSentFeedback));
			}
		}
	}

	private void setupEventsWithoutFeedbackLayout(boolean update) {
		if (eventsWithoutFeedback.size() == 0) {
			if (eventsWithSentFeedback.size() > 0) {
				// Only the sent feedback list is displayed
				eventsWithoutFeedbackLayout.setVisibility(View.GONE);
			} else {
				// Neither list has items, display explanatory text instead
				eventsWithoutFeedbackLayout.setVisibility(View.VISIBLE);
				eventsWithoutFeedbackListLayout.setVisibility(View.GONE);
				noEventsText.setVisibility(View.VISIBLE);
			}
		} else {
			// Display unsent feedback list
			eventsWithoutFeedbackLayout.setVisibility(View.VISIBLE);
			noEventsText.setVisibility(View.GONE);
			eventsWithoutFeedbackListLayout.setVisibility(View.VISIBLE);

			if (update && eventsWithoutFeedbackList.getAdapter() != null) {
				((EventsViewWithDateHeaderAdapter) eventsWithoutFeedbackList.getAdapter()).setEventsList(eventsWithoutFeedback);
			} else {
				eventsWithoutFeedbackList.setAdapter(new EventsViewWithDateHeaderAdapter(eventsWithoutFeedback));
			}

			if (Convention.getInstance().isFeedbackSendingTimeOver()) {
				sendAllExplanation.setVisibility(View.GONE);
				sendAllButtonLayout.setVisibility(View.GONE);
			} else {
				int unsentFeedbacks = eventsWithUnsentFeedback.size();
				if (unsentFeedbacks == 0) {
					sendAllExplanation.setText(getString(R.string.send_all_explanation_no_events));
					sendAllButton.setEnabled(false);
				} else {
					String explanation;
					if (unsentFeedbacks == 1) {
						explanation = getString(R.string.send_all_explanation_with_1_event);
					} else {
						explanation = getString(R.string.send_all_explanation_with_events, unsentFeedbacks);
					}
					sendAllExplanation.setText(explanation);
					sendAllButton.setEnabled(true);
				}
			}
		}
	}

	private void setupEventLists(boolean update) {
		eventsWithoutFeedback = CollectionUtils.filter(Convention.getInstance().getEvents(),
				new CollectionUtils.Predicate<ConventionEvent>() {
					@Override
					public boolean where(ConventionEvent item) {
						return item.shouldUserSeeFeedback();
					}
				});
		Collections.sort(eventsWithoutFeedback, new ConventionEventEndTimeComparator());

		eventsWithUnsentFeedback = CollectionUtils.filter(eventsWithoutFeedback,
				new CollectionUtils.Predicate<ConventionEvent>() {
					@Override
					public boolean where(ConventionEvent item) {
						return item.getUserInput().getFeedback().hasAnsweredQuestions();
					}
				});

		eventsWithSentFeedback = CollectionUtils.filter(Convention.getInstance().getEvents(),
				new CollectionUtils.Predicate<ConventionEvent>() {
					@Override
					public boolean where(ConventionEvent item) {
						return item.getUserInput().getFeedback().isSent();
					}
				});
		Collections.sort(eventsWithSentFeedback, new ConventionEventEndTimeComparator());

		setupEventsWithoutFeedbackLayout(update);
		setupEventsWithSentFeedbackLayout(update);
	}

	private void setupConventionFeedbackView() {
		feedbackView = (CollapsibleFeedbackView) findViewById(R.id.convention_feedback_view);
		feedbackView.setState(CollapsibleFeedbackView.State.ExpandedHeadless, false);
		feedbackView.setModel(Convention.getInstance().getFeedback());
		feedbackView.setSendFeedbackClickListener(feedbackView.new CollapsibleFeedbackViewSendMailListener() {
			@Override
			protected FeedbackMail getFeedbackMail() {
				return new ConventionFeedbackMail(FeedbackActivity.this);
			}

			@Override
			protected void saveFeedback() {
				saveConventionFeedback();
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_feedback, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.feedback_navigate_to_programme:
				navigateToActivity(ProgrammeActivity.class);
				return true;
		}

		return super.onOptionsItemSelected(item);
	}

	private void saveConventionFeedback() {
		Convention.getInstance().getStorage().saveConventionFeedback();
		Convention.getInstance().getFeedback().resetChangedAnswers();
	}

	@Override
	protected void addCustomEventActivityParameters(Bundle bundle) {
		bundle.putBoolean(EventActivity.EXTRA_FOCUS_ON_FEEDBACK, true);
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (feedbackView.isFeedbackChanged()) {
			saveConventionFeedback();
		}
	}

	public void toggleEventsWithSentFeedback(View view) {
		if (eventsWithSentFeedbackList.getVisibility() == View.VISIBLE) {
			// Hide the list
			Animator animator = createResizeAnimator(eventsWithSentFeedbackList.getMeasuredHeight(), 0,
					eventsWithSentFeedbackList, new Runnable() {
						@Override
						public void run() {
							showHideEventsWithSentFeedback.setText(R.string.display);
							eventsWithSentFeedbackList.setVisibility(View.GONE);
						}
					});
			animator.setDuration(300).start();
		} else {
			// Show the list
			eventsWithSentFeedbackList.setVisibility(View.VISIBLE);

			// Calculate the list's full height
			eventsWithSentFeedbackList.measure(
					View.MeasureSpec.makeMeasureSpec(eventsWithSentFeedbackLayout.getMeasuredWidth(), View.MeasureSpec.EXACTLY),
					View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
			int fullHeight = eventsWithSentFeedbackList.getMeasuredHeight();

			// Animate showing the list
			Animator animator = createResizeAnimator(0, fullHeight,
					eventsWithSentFeedbackList, new Runnable() {
						@Override
						public void run() {
							ViewGroup.LayoutParams layoutParams = eventsWithSentFeedbackList.getLayoutParams();
							layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
							eventsWithSentFeedbackList.setLayoutParams(layoutParams);
							showHideEventsWithSentFeedback.setText(R.string.hide);
						}
					});
			animator.setDuration(300).start();
		}
	}

	private Animator createResizeAnimator(int start, int end, final View viewToResize, final Runnable onAnimationEnd) {
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
		if (onAnimationEnd != null) {
			animator.addListener(new Animator.AnimatorListener() {
				@Override
				public void onAnimationStart(Animator animator) {
				}

				@Override
				public void onAnimationEnd(Animator animator) {
					onAnimationEnd.run();
				}

				@Override
				public void onAnimationCancel(Animator animator) {

				}

				@Override
				public void onAnimationRepeat(Animator animator) {

				}
			});
		}
		return animator;
	}
}

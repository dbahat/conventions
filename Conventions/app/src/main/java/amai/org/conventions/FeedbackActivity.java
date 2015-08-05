package amai.org.conventions;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;

import amai.org.conventions.events.CollapsibleFeedbackView;
import amai.org.conventions.events.activities.EventActivity;
import amai.org.conventions.events.activities.ProgrammeActivity;
import amai.org.conventions.events.adapters.EventsViewAdapter;
import amai.org.conventions.model.Convention;
import amai.org.conventions.model.ConventionEvent;
import amai.org.conventions.model.FeedbackQuestion;
import amai.org.conventions.navigation.NavigationActivity;
import amai.org.conventions.utils.CollectionUtils;
import amai.org.conventions.utils.Dates;
import amai.org.conventions.utils.Log;


public class FeedbackActivity extends NavigationActivity {
	private final static String TAG = FeedbackActivity.class.getCanonicalName();

	private CollapsibleFeedbackView feedbackView;
	private ListView eventsWithoutFeedbackList;
	private ViewGroup eventsWithoutFeedbackLayout;

	private ListView eventsWithSentFeedbackList;
	private ViewGroup eventsWithSentFeedbackLayout;
	private View noEventsText;

	private List<ConventionEvent> eventsWithoutFeedback;
	private List<ConventionEvent> eventsWithSentFeedback;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentInContentContainer(R.layout.activity_feedback);
		setToolbarTitle(getString(R.string.feedback));

		eventsWithoutFeedbackLayout = (ViewGroup) findViewById(R.id.events_without_feedback);
		eventsWithoutFeedbackList = (ListView) findViewById(R.id.events_without_feedback_list);

		eventsWithSentFeedbackLayout = (ViewGroup) findViewById(R.id.events_with_sent_feedback);
		eventsWithSentFeedbackList = (ListView) findViewById(R.id.events_with_sent_feedback_list);

		noEventsText = findViewById(R.id.feedback_no_events_text);

		setupConventionFeedbackView();
	}

	@Override
	protected void onResume() {
		super.onResume();
		setupEventLists();
	}

	private void setupEventsWithSentFeedbackLayout() {
		if (eventsWithSentFeedback.size() == 0) {
			eventsWithSentFeedbackLayout.setVisibility(View.GONE);
		} else {
			eventsWithSentFeedbackLayout.setVisibility(View.VISIBLE);
			eventsWithSentFeedbackList.setAdapter(new EventsViewAdapter(eventsWithSentFeedback));
		}
	}

	private void setupEventsWithoutFeedbackLayout() {
		if (eventsWithoutFeedback.size() == 0) {
			if (eventsWithSentFeedback.size() > 0) {
				// Only the sent feedback list is displayed
				eventsWithoutFeedbackLayout.setVisibility(View.GONE);
			} else {
				// Neither list has items, display explanatory text instead
				eventsWithoutFeedbackLayout.setVisibility(View.VISIBLE);
				eventsWithoutFeedbackList.setVisibility(View.GONE);
				noEventsText.setVisibility(View.VISIBLE);
			}
		} else {
			// Display unsent feedback list
			eventsWithoutFeedbackLayout.setVisibility(View.VISIBLE);
			noEventsText.setVisibility(View.GONE);
			eventsWithoutFeedbackList.setVisibility(View.VISIBLE);
			eventsWithoutFeedbackList.setAdapter(new EventsViewAdapter(eventsWithoutFeedback));
		}
	}

	private void setupEventLists() {
		eventsWithoutFeedback = CollectionUtils.filter(Convention.getInstance().getEvents(),
				new CollectionUtils.Predicate<ConventionEvent>() {
					@Override
					public boolean where(ConventionEvent item) {
						// Same logic as in EventActivity
						return item.canFillFeedback() &&
								(!item.getUserInput().getFeedback().isSent()) &&
								(item.getUserInput().isAttending() ||
										item.getUserInput().getFeedback().hasAnsweredQuestions());
					}
				});

		eventsWithSentFeedback = CollectionUtils.filter(Convention.getInstance().getEvents(),
				new CollectionUtils.Predicate<ConventionEvent>() {
					@Override
					public boolean where(ConventionEvent item) {
						return item.getUserInput().getFeedback().isSent();
					}
				});

		setupEventsWithoutFeedbackLayout();
		setupEventsWithSentFeedbackLayout();
	}

	private void setupConventionFeedbackView() {
		feedbackView = (CollapsibleFeedbackView) findViewById(R.id.convention_feedback_view);
		feedbackView.setState(CollapsibleFeedbackView.State.ExpandedHeadless, false);
		feedbackView.setModel(Convention.getInstance().getFeedback());
		feedbackView.setSendFeedbackClickListener(feedbackView.new SendMailOnClickListener() {
			@Override
			protected void saveFeedback() {
				saveConventionFeedback();
			}

			@Override
			protected String getMailSubject() {
				return getString(R.string.convention_feedback_mail_title);
			}

			@Override
			protected String getMailBody() {
				return String.format(Dates.getLocale(), "%s\n\t\n\t\n\t\nDeviceId:\n%s",
						feedbackView.getFormattedQuestions(),
						getDeviceId()
				);
			}

			@Override
			protected void onFailure(String errorMessage) {
				Log.w(TAG, "Failed to send feedback mail. Reason: " + errorMessage);
				Toast.makeText(FeedbackActivity.this, R.string.feedback_send_mail_failed, Toast.LENGTH_LONG).show();
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

		// Reset answer changed flag
		List<FeedbackQuestion> questions = Convention.getInstance().getFeedback().getQuestions();
		for (FeedbackQuestion question : questions) {
			question.setAnswerChanged(false);
		}

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
}

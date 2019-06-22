package amai.org.conventions.events;

import android.app.Dialog;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.analytics.HitBuilders;

import amai.org.conventions.ConventionsApplication;
import sff.org.conventions.R;
import amai.org.conventions.ThemeAttributes;
import amai.org.conventions.feedback.SurveySender;
import amai.org.conventions.model.ConventionEvent;
import amai.org.conventions.model.conventions.Convention;

public class EventVoteSurveyFragment extends DialogFragment {
	private static final String EventId = "EventId";
	private ConventionEvent event;

	public EventVoteSurveyFragment() {}

	public static EventVoteSurveyFragment newInstance(String eventId) {
		EventVoteSurveyFragment fragment = new EventVoteSurveyFragment();
		Bundle args = new Bundle();
		args.putString(EventId, eventId);
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (getArguments() != null) {
			String eventId = getArguments().getString(EventId);
			if (eventId != null) {
				event = Convention.getInstance().findEventById(eventId);
			}
		}
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);

		// Inflate the layout for this fragment
		View view = inflater.inflate(R.layout.fragment_event_vote_survey, container, false);

		TextView title = (TextView) view.findViewById(R.id.event_vote_survey_title);
		title.setText(getString(R.string.event_vote_survey_title, event.getTitle()));

		CollapsibleFeedbackView surveyView = (CollapsibleFeedbackView) view.findViewById(R.id.event_vote_survey_container);
		setupSurveyView(surveyView);

		Button dismissButton = (Button) view.findViewById(R.id.event_vote_survey_dismiss);
		dismissButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dismiss();
			}
		});

		return view;
	}

	@Override
	public void onStart() {
		super.onStart();

		Dialog dialog = getDialog();
		if (dialog != null && dialog.getWindow() != null) {
			dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		}
	}

	private void setupSurveyView(CollapsibleFeedbackView surveyView ) {
		surveyView.setState(CollapsibleFeedbackView.State.ExpandedHeadless, false);
		surveyView.setFeedbackSentText(R.string.event_vote_survey_sent);
		surveyView.setFeedbackSendErrorMessage(R.string.vote_send_error);
		surveyView.setModel(event.getUserInput().getVoteSurvey());
		surveyView.setTextColor(ThemeAttributes.getColor(surveyView.getContext(), R.attr.eventSurveyTextColor));
		surveyView.setSendFeedbackClickListener(surveyView.new CollapsibleFeedbackViewSendListener() {
			@Override
			protected void saveFeedback() {
				saveUserInput();
			}

			@Override
			protected SurveySender getSurveySender() {
				return Convention.getInstance().getEventVoteSender(event);
			}

			@Override
			protected void beforeSend() {
				super.beforeSend();
				ConventionsApplication.sendTrackingEvent(new HitBuilders.EventBuilder()
						.setCategory("EventVote")
						.setAction("send")
						.setLabel(event.getTitle())
						.build());
			}
		});
	}

	private void saveUserInput() {
		Convention.getInstance().getStorage().saveUserInput();
		event.getUserInput().getVoteSurvey().resetChangedAnswers();
	}
}

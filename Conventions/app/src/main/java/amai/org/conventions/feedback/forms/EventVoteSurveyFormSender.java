package amai.org.conventions.feedback.forms;

import java.net.HttpURLConnection;

import javax.net.ssl.HttpsURLConnection;

import amai.org.conventions.model.Survey;
import amai.org.conventions.networking.SurveyDataRetriever;

/**
 * Allows sending a survey to a google form, related to an event voting survey.
 */
public class EventVoteSurveyFormSender extends SurveyFormSender {

	private Survey survey;


	public EventVoteSurveyFormSender(SurveyForm form, Survey survey, SurveyDataRetriever.DisabledMessage disabledMessageRetriever) {
		super(form, disabledMessageRetriever);

		this.survey = survey;
	}

	@Override
	protected Survey getSurvey() {
		return survey;
	}
}

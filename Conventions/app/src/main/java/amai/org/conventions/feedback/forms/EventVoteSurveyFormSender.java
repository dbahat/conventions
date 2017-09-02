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
	private SurveyDataRetriever.DisabledMessage disabledMessageRetriever;

	public EventVoteSurveyFormSender(SurveyForm form, Survey survey, SurveyDataRetriever.DisabledMessage disabledMessageRetriever) {
		super(form);

		this.survey = survey;
		this.disabledMessageRetriever = disabledMessageRetriever;
	}

	@Override
	protected Survey getSurvey() {
		return survey;
	}

	@Override
	protected void verifyResponse(HttpURLConnection connection) throws Exception {

		if (connection.getResponseCode() == HttpsURLConnection.HTTP_MOVED_TEMP && connection.getHeaderField("Location").contains("closedform")) {
			// In the special case of survey votes, it's possible the request will fail since the survey was disabled.
			// In such a case, try to fetch the disable message and propagate it to the caller.
			throw new SurveyDisabledException(tryFetchDisabledMessage());

		} else {
			super.verifyResponse(connection);
		}
	}

	private String tryFetchDisabledMessage() {
		try {
			return disabledMessageRetriever.retrieveClosedMessage();
		} catch (Exception e) {
			return "";
		}
	}
}

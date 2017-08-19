package amai.org.conventions.feedback.forms;

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
}

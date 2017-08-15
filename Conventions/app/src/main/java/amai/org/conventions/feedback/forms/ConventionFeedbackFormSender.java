package amai.org.conventions.feedback.forms;

import android.content.Context;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import amai.org.conventions.BuildConfig;
import amai.org.conventions.model.Survey;
import amai.org.conventions.model.FeedbackQuestion;
import amai.org.conventions.model.conventions.Convention;

public class ConventionFeedbackFormSender extends SurveyFormSender {
	private final Convention convention;
	private final FeedbackForm form;

	public ConventionFeedbackFormSender(Context context, FeedbackForm form, Convention convention) {
		super(context, form);
		this.convention = convention;
		this.form = form;
	}

	@Override
	protected Survey getSurvey() {
		return convention.getFeedback();
	}

	@Override
	protected Map<String, String> getAnswers() {
		Map<String, String> answers = new HashMap<>();
		// General information
		// Convention name
		answers.put(form.getConventionNameEntry(), convention.getDisplayName());
		// Device id
		answers.put(form.getDeviceIdEntry(), getDeviceId());
		// Test feedback
		if (BuildConfig.DEBUG) {
			answers.put(form.getTestEntry(), "true");
		}

		// Questions
		List<FeedbackQuestion> questions = getSurvey().getQuestions();
		for (FeedbackQuestion question : questions) {
			if (question.hasAnswer()) {
				answers.put(form.getQuestionEntry(question.getQuestionId()), question.getAnswer().toString());
			}
		}
		return answers;
	}
}

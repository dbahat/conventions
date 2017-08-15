package amai.org.conventions.feedback.forms;

import android.content.Context;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import amai.org.conventions.BuildConfig;
import amai.org.conventions.model.Survey;
import amai.org.conventions.model.FeedbackQuestion;
import amai.org.conventions.model.conventions.Convention;

public class ConventionFeedbackFormSender extends FeedbackFormSender {
	private final Convention convention;

	public ConventionFeedbackFormSender(Context context, FeedbackForm form, Convention convention) {
		super(context, form);
		this.convention = convention;
	}

	@Override
	protected Survey getFeedback() {
		return convention.getFeedback();
	}

	@Override
	protected Map<String, String> getAnswers() {
		FeedbackForm form = getForm();

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
		List<FeedbackQuestion> questions = getFeedback().getQuestions();
		for (FeedbackQuestion question : questions) {
			if (question.hasAnswer()) {
				answers.put(form.getQuestionEntry(question.getQuestionId()), question.getAnswer().toString());
			}
		}
		return answers;
	}
}

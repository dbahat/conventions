package amai.org.conventions.feedback.forms;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import amai.org.conventions.model.FeedbackQuestion;
import amai.org.conventions.model.Survey;

public class SurveyForm {
	private URL sendUrl;
	private Map<Integer, String> questionIdToFormEntry = new HashMap<>();

	public URL getSendUrl() {
		return sendUrl;
	}

	public void setSendUrl(URL sendUrl) {
		this.sendUrl = sendUrl;
	}

	public SurveyForm withSendUrl(URL sendUrl) {
		setSendUrl(sendUrl);
		return this;
	}

	public SurveyForm withQuestionEntry(int questionId, String entry) {
		questionIdToFormEntry.put(questionId, entry);
		return this;
	}

	public String getQuestionEntry(int questionId) {
		return questionIdToFormEntry.get(questionId);
	}

	public boolean canFillFeedback(Survey feedback) {
		List<FeedbackQuestion> questions = feedback.getQuestions();
		for (FeedbackQuestion question : questions) {
			if (getQuestionEntry(question.getQuestionId()) == null) {
				return false;
			}
		}
		return true;
	}
}

package amai.org.conventions.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Feedback implements Serializable {
	Map<Integer, FeedbackQuestion> questions;
	boolean isSent;

	public Feedback() {
		questions = new LinkedHashMap<>();
	}

	public List<FeedbackQuestion> getQuestions() {
		return new ArrayList<>(questions.values());
	}

	public Feedback withQuestions(FeedbackQuestion... newQuestions) {
		questions.clear();
		for (FeedbackQuestion question : newQuestions) {
			questions.put(question.getQuestionId(), question);
		}
		return this;
	}

	public boolean isSent() {
		return isSent;
	}

	public void setIsSent(boolean isSent) {
		this.isSent = isSent;
	}

	public boolean hasAnsweredQuestions() {
		for (FeedbackQuestion question : questions.values()) {
			if (question.hasAnswer()) {
				return true;
			}
		}
		return false;
	}

}

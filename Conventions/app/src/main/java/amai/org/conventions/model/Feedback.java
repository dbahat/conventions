package amai.org.conventions.model;

import android.util.Log;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Feedback implements Serializable, Cloneable {
	private static final String TAG = Feedback.class.getCanonicalName();
	private Map<Integer, FeedbackQuestion> questions;
	boolean isSent;

	public Feedback() {
		questions = new LinkedHashMap<>();
	}

	@Override
	public Feedback clone() throws CloneNotSupportedException {
		Feedback newFeedback = (Feedback) super.clone();
		newFeedback.questions = new LinkedHashMap<>(questions);
		return newFeedback;
	}

	/**
	 * Update this instance from feedback loaded from file
	 * @param other the deserialized feedback
	 */
	public void updateFrom(Feedback other) {
		if (other == null) {
			return;
		}
		isSent = other.isSent;
		if (isSent) {
			questions = other.questions;
			for (FeedbackQuestion question : questions.values()) {
				convertQuestion(question);
			}
		} else {
			for (Map.Entry<Integer, FeedbackQuestion> entry : other.questions.entrySet()) {
				// If not sent yet, ignore non-existing questions
				if (questions.containsKey(entry.getKey())) {
					FeedbackQuestion newQuestion = entry.getValue();
					questions.put(entry.getKey(), newQuestion);
					convertQuestion(newQuestion);
				}
			}
		}
	}

	private void convertQuestion(FeedbackQuestion question) {
		// Handle enum answer type
		if (question.hasAnswer() && question.getAnswerType() == FeedbackQuestion.AnswerType.SMILEY_3_POINTS) {
			try {
				question.setAnswer(FeedbackQuestion.Smiley3PointAnswer.valueOf(question.getAnswer().toString()));
			} catch (IllegalArgumentException e) {
				Log.e(TAG, "Smiley answer with incorrect enum name: " + question.getAnswer().toString());
			}
		}
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

	public void removeUnansweredQuestions() {
		Iterator<Map.Entry<Integer, FeedbackQuestion>> iterator = questions.entrySet().iterator();
		while (iterator.hasNext()) {
			FeedbackQuestion question = iterator.next().getValue();
			if (!question.hasAnswer()) {
				iterator.remove();
			}
		}
	}

	public boolean isSent() {
		return isSent;
	}

	public void setIsSent(boolean isSent) {
		this.isSent = isSent;
	}

	public Feedback withSent(boolean isSent) {
		setIsSent(isSent);
		return this;
	}

	public boolean hasAnsweredQuestions() {
		for (FeedbackQuestion question : questions.values()) {
			if (question.hasAnswer()) {
				return true;
			}
		}
		return false;
	}

	public FeedbackQuestion.Smiley3PointAnswer getWeightedRating() {
		if (isSent()) {
			for (FeedbackQuestion question : questions.values()) {
				if (question.hasAnswer() && question.getAnswerType() == FeedbackQuestion.AnswerType.SMILEY_3_POINTS) {
					return (FeedbackQuestion.Smiley3PointAnswer) question.getAnswer();
				}
			}
		}
		return null;
	}

}

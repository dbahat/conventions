package amai.org.conventions.model;

import android.content.res.Resources;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import amai.org.conventions.R;
import amai.org.conventions.utils.Objects;

public class FeedbackQuestion implements Serializable {
	public static final int QUESTION_ID_ENJOYMENT = 1;
	public static final int QUESTION_ID_LECTURER_QUALITY = 2;
	public static final int QUESTION_ID_SIMILAR_EVENTS = 3;
	public static final int QUESTION_ID_ADDITIONAL_INFO = 4;

	private static Map<Integer, Integer> questions = initQuestions();

	private static Map<Integer, Integer> initQuestions() {
		Map<Integer, Integer> questions = new HashMap<>();
		questions.put(QUESTION_ID_ENJOYMENT, R.string.question_enjoyment);
		questions.put(QUESTION_ID_LECTURER_QUALITY, R.string.question_lecturer_quality);
		questions.put(QUESTION_ID_SIMILAR_EVENTS, R.string.question_similar_events);
		questions.put(QUESTION_ID_ADDITIONAL_INFO, R.string.question_additional_info);
		return questions;
	}

	private int questionId;
	private String text; // If feedback was already sent, this is the displayed text
	private AnswerType answerType;
	private Object answer;
	private transient boolean answerChanged = false; // No need to serialize this field

	public FeedbackQuestion(int questionId, AnswerType answerType) {
		this.questionId = questionId;
		this.answerType = answerType;
	}

	public int getQuestionId() {
		return questionId;
	}

	public String getQuestionText(Resources resources, boolean isSent) {
		// text will be null when we haven't saved and loaded the feedback yet and this is the first
		// time it's accessed
		if (text == null || !isSent) {
			Integer stringId = questions.get(questionId);
			if (stringId != null) {
				text = resources.getString(stringId);
			}
		}
		return text;
	}

	public AnswerType getAnswerType() {
		return answerType;
	}

	public void setAnswer(Object answer) {
		// Don't allow empty string an an answer
		if (answer instanceof String && ((String) answer).isEmpty()) {
			answer = null;
		}
		if (!Objects.equals(answer, this.answer)) {
			setAnswerChanged(true);
		}
		this.answer = answer;
	}

	public Object getAnswer() {
		return answer;
	}

	public boolean hasAnswer() {
		return answer != null;
	}

	public boolean isAnswerChanged() {
		return answerChanged;
	}

	public void setAnswerChanged(boolean answerChanged) {
		this.answerChanged = answerChanged;
	}

	// This enum must be backwards compatible - don't remove or rename any values from it
	public enum AnswerType {
		TEXT, YES_NO, SMILEY_3_POINTS
	}

	// This enum must be backwards compatible - don't remove or rename any values from it
	public enum Smiley3PointAnswer {
		NEGATIVE("|:", R.drawable.negative_rating),
		POSITIVE("(:", R.drawable.positive_rating),
		VERY_POSITIVE("D:", R.drawable.very_positive_rating);

		private String answerText;
		private int imageResourceId;

		Smiley3PointAnswer(String answerText, int imageResourceId) {
			this.answerText = answerText;
			this.imageResourceId = imageResourceId;
		}

		@Override
		public String toString() {
			return answerText;
		}

		public int getImageResourceId() {
			return imageResourceId;
		}

		public static Smiley3PointAnswer getByAnswerText(String textAnswer) {
			for (Smiley3PointAnswer answer : Smiley3PointAnswer.values()) {
				if (answer.answerText.equals(textAnswer)) {
					return answer;
				}
			}
			return null;
		}
	}
}


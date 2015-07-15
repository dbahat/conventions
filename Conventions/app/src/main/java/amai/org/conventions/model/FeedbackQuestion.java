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
	private boolean answerChanged;

	public FeedbackQuestion(int questionId, AnswerType answerType) {
		this.questionId = questionId;
		this.answerType = answerType;
		answerChanged = false;
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

	public enum AnswerType {
		TEXT, YES_NO, SMILEY_3_POINTS
	}

	public enum Smiley3PointAnswer {
		NEGATIVE(":|"), POSITIVE(":)"), VERY_POSITIVE(":D");

		private String answerText;
		Smiley3PointAnswer(String answerText) {
			this.answerText = answerText;
		}

		@Override
		public String toString() {
			return answerText;
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


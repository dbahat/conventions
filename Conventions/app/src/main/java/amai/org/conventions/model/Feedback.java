package amai.org.conventions.model;

import java.io.Serializable;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class Feedback implements Serializable {
	List<Question> questions;
	boolean isSent;

	public Feedback() {
		questions = new LinkedList<>();
	}

	public List<Question> getQuestions() {
		return questions;
	}

	public void setQuestions(List<Question> questions) {
		this.questions = questions;
	}

	public Feedback withQuestions(Question... questions) {
		setQuestions(Arrays.asList(questions));
		return this;
	}

	public boolean isSent() {
		return isSent;
	}

	public void setIsSent(boolean isSent) {
		this.isSent = isSent;
	}

	public static class Question implements Serializable {
		private int stringId;
		private AnswerType answerType;
		private Object answer;

		public Question(int stringId, AnswerType answerType) {
			this.stringId = stringId;
			this.answerType = answerType;
		}

		public int getStringId() {
			return stringId;
		}

		public AnswerType getAnswerType() {
			return answerType;
		}

		public void setAnswer(Object answer) {
			this.answer = answer;
		}

		public Object getAnswer() {
			return answer;
		}

		public boolean hasAnswer() {
			return answer != null;
		}

		public enum AnswerType {
			TEXT, YES_NO, SMILEY_3_POINTS
		}
	}
}

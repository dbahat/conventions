package amai.org.conventions.model;

import android.content.res.Resources;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import sff.org.conventions.R;
import amai.org.conventions.utils.CollectionUtils;
import amai.org.conventions.utils.Objects;

public class FeedbackQuestion {
	// The IDs are needed since questions get serialized, and we cannot serialize the question string resource Ids (as they change each build).
	public static final int QUESTION_ID_ENJOYMENT = 1;
	public static final int QUESTION_ID_LECTURER_QUALITY = 2;
	public static final int QUESTION_ID_SIMILAR_EVENTS = 3;
	public static final int QUESTION_ID_ADDITIONAL_INFO = 4;
	public static final int QUESTION_ID_AGE = 5;
	public static final int QUESTION_ID_IMPROVEMENT = 7;
	public static final int QUESTION_ID_LIKED = 8;
	public static final int QUESTION_ID_MAP_SIGNS = 9;
	public static final int QUESTION_ID_CONFLICTING_EVENTS = 10;

	public static final int QUESTION_ID_ENJOYMENT_5P = 11;
	public static final int QUESTION_ID_LECTURER_QUALITY_5P = 12;
	public static final int QUESTION_ID_SIMILAR_EVENTS_5P = 13;
	public static final int QUESTION_ID_LIKED_5P = 18;

	private static final Map<Integer, Integer> questions = initQuestions();
	private static final Map<Integer, List<Integer>> questionToMultipleAnswers = initMultipleAnswers();

	private static Map<Integer, Integer> initQuestions() {
		Map<Integer, Integer> questions = new HashMap<>();
		questions.put(QUESTION_ID_ENJOYMENT, R.string.question_enjoyment);
		questions.put(QUESTION_ID_ENJOYMENT_5P, R.string.question_enjoyment);
		questions.put(QUESTION_ID_LECTURER_QUALITY, R.string.question_lecturer_quality);
		questions.put(QUESTION_ID_LECTURER_QUALITY_5P, R.string.question_lecturer_quality);
		questions.put(QUESTION_ID_SIMILAR_EVENTS, R.string.question_similar_events);
		questions.put(QUESTION_ID_SIMILAR_EVENTS_5P, R.string.question_similar_events);
		questions.put(QUESTION_ID_ADDITIONAL_INFO, R.string.question_additional_info);
		questions.put(QUESTION_ID_AGE, R.string.question_age);
		questions.put(QUESTION_ID_LIKED, R.string.question_liked);
		questions.put(QUESTION_ID_LIKED_5P, R.string.question_liked);
		questions.put(QUESTION_ID_IMPROVEMENT, R.string.question_improvement);
		questions.put(QUESTION_ID_MAP_SIGNS, R.string.question_map_signs);
		questions.put(QUESTION_ID_CONFLICTING_EVENTS, R.string.question_conflicting_events);
		return questions;
	}

	private static Map<Integer, List<Integer>> initMultipleAnswers() {
		Map<Integer, List<Integer>> questionToMultipleAnswers = new HashMap<>();
		questionToMultipleAnswers.put(QUESTION_ID_AGE,
				Arrays.asList(R.string.age_less_than_12, R.string.age_12_to_17, R.string.age_18_to_25, R.string.age_more_than_25));
		questionToMultipleAnswers.put(QUESTION_ID_MAP_SIGNS,
				Arrays.asList(R.string.yes, R.string.no));
		questionToMultipleAnswers.put(QUESTION_ID_CONFLICTING_EVENTS,
				Arrays.asList(R.string.answer_event_conflicted, R.string.answer_no_room, R.string.answer_too_early_or_late, R.string.answer_other_reason));
		return questionToMultipleAnswers;
	}

	public static void addQuestion(int id, int stringResourceId) {
		if (questions.containsKey(id)) {
			throw new RuntimeException("Question ID already exists: " + id);
		}
		questions.put(id, stringResourceId);
	}

	private int questionId;

	// If feedback was already sent, this is the displayed text. Needed since it's possible the hardcoded questions will change per convention,
	// but a user will wish to see the original texts he previously answered.
	private String text;
	private boolean required = false;
	private AnswerType answerType;
	private Object answer;
	private List<String> possibleMultipleAnswers;
	private transient boolean answerChanged = false; // No need to serialize this field

	public FeedbackQuestion(int questionId, AnswerType answerType, boolean required) {
		this.questionId = questionId;
		this.answerType = answerType;
		this.required = required;
	}
	public FeedbackQuestion(int questionId, AnswerType answerType) {
		this(questionId, answerType, false);
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

	public boolean isRequired() {
		return required;
	}

	public AnswerType getAnswerType() {
		return answerType;
	}

	public void setAnswerType(AnswerType answerType) {
		this.answerType = answerType;
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

	public void setPossibleMultipleAnswers(List<String> possibleAnswers) {
		this.possibleMultipleAnswers = possibleAnswers;
	}

	public void setAnswerChanged(boolean answerChanged) {
		this.answerChanged = answerChanged;
	}

	public List<String> getPossibleMultipleAnswers(final Resources resources) {
		if (possibleMultipleAnswers != null) {
			return possibleMultipleAnswers;
		}
		List<Integer> answers = questionToMultipleAnswers.get(questionId);
		if (answers == null) {
			return Collections.emptyList();
		}
		return CollectionUtils.map(answers, new CollectionUtils.Mapper<Integer, String>() {
			@Override
			public String map(Integer item) {
				return resources.getString(item);
			}
		});
	}

	// This enum must be backwards compatible - don't remove or rename any values from it
	public enum AnswerType {
		TEXT, SINGLE_LINE_TEXT, SMILEY_3_POINTS, SMILEY_5_POINTS, MULTIPLE_ANSWERS, MULTIPLE_ANSWERS_RADIO, HIDDEN, FIVE_STARS
	}

	public interface DrawableAnswer {
		int getImageResourceId();
	}

	// This enum must be backwards compatible - don't remove or rename any values from it
	public enum Smiley3PointAnswer implements DrawableAnswer {
		NEGATIVE("|:", R.drawable.negative_rating),
		POSITIVE("(:", R.drawable.positive_rating),
		VERY_POSITIVE(":D", R.drawable.very_positive_rating);

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

	// This enum must be backwards compatible - don't remove or rename any values from it
	public enum Smiley5PointAnswer implements DrawableAnswer {
		VERY_NEGATIVE("\u2639\ufe0f", R.drawable.baseline_sentiment_very_dissatisfied_white_48),
		NEGATIVE("\uD83D\uDE41", R.drawable.baseline_sentiment_dissatisfied_white_48),
		NEUTRAL("\uD83D\uDE10", R.drawable.baseline_sentiment_neutral_white_48),
		POSITIVE("\uD83D\uDE42", R.drawable.baseline_sentiment_satisfied_white_48),
		VERY_POSITIVE("\uD83D\uDE03", R.drawable.baseline_sentiment_satisfied_alt_white_48);

		private String answerText;
		private int imageResourceId;

		Smiley5PointAnswer(String answerText, int imageResourceId) {
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

		public static Smiley5PointAnswer getByAnswerText(String textAnswer) {
			for (Smiley5PointAnswer answer : Smiley5PointAnswer.values()) {
				if (answer.answerText.equals(textAnswer)) {
					return answer;
				}
			}
			return null;
		}
	}
}


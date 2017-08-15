package amai.org.conventions.feedback.forms;

import android.content.Context;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import amai.org.conventions.BuildConfig;
import amai.org.conventions.model.ConventionEvent;
import amai.org.conventions.model.Survey;
import amai.org.conventions.model.FeedbackQuestion;
import amai.org.conventions.model.conventions.Convention;
import amai.org.conventions.utils.Dates;


public class EventFeedbackFormSender extends FeedbackFormSender {
	private final Convention convention;
	private ConventionEvent event;
    private EventFeedbackForm form;

    public EventFeedbackFormSender(Context context, EventFeedbackForm form, Convention convention, ConventionEvent event) {
        super(context, form);
		this.convention = convention;
		this.event = event;
        this.form = form;
    }

    @Override
    protected Map<String, String> getAnswers() {
        Map<String, String> answers = new HashMap<>();
        // General information
        // Convention name
        answers.put(form.getConventionNameEntry(), convention.getDisplayName());
        // Event name
        answers.put(form.getEventTitleEntry(), event.getTitle());
        // Event start time
        answers.put(form.getEventTimeEntry(), Dates.formatHoursAndMinutes(event.getStartTime()));
        // Device id
        answers.put(form.getDeviceIdEntry(), getDeviceId());
        // Event hall
        answers.put(form.getHallEntry(), event.getHall().getName());
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

    @Override
    protected Survey getFeedback() {
        return event.getUserInput().getFeedback();
    }
}

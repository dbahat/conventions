package amai.org.conventions.feedback.mail;

import android.content.Context;

import amai.org.conventions.R;
import amai.org.conventions.model.ConventionEvent;
import amai.org.conventions.model.Survey;
import amai.org.conventions.model.conventions.Convention;
import amai.org.conventions.utils.Dates;

public class EventFeedbackMail extends FeedbackMail {
	private ConventionEvent event;

	public EventFeedbackMail(Context context, ConventionEvent event) {
		super(context);
		this.event = event;
	}

	@Override
	protected Survey getFeedback() {
		return event.getUserInput().getFeedback();
	}

	@Override
	protected String getSubject() {
		return context.getString(R.string.event_feedback_mail_title, Convention.getInstance().getDisplayName()) + ": " + event.getTitle();
	}

	@Override
	protected String getBody(String formattedQuestions, String deviceId) {
		return String.format(Dates.getLocale(), "%s\n%s, %s\n\n%s\n\t\n\t\n\t\nDeviceId:\n%s",
				event.getTitle(),
				Dates.formatHoursAndMinutes(event.getStartTime()),
				event.getHall().getName(),
				formattedQuestions,
				deviceId
		);
	}
}

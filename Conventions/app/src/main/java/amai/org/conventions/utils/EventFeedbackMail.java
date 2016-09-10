package amai.org.conventions.utils;

import android.content.Context;

import sff.org.conventions.R;
import amai.org.conventions.model.conventions.Convention;
import amai.org.conventions.model.ConventionEvent;
import amai.org.conventions.model.Feedback;

public class EventFeedbackMail extends FeedbackMail {
	private ConventionEvent event;

	public EventFeedbackMail(Context context, ConventionEvent event) {
		super(context);
		this.event = event;
	}

	@Override
	protected Feedback getFeedback() {
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

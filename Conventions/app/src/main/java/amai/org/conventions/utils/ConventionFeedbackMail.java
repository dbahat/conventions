package amai.org.conventions.utils;

import android.content.Context;

import amai.org.conventions.R;
import amai.org.conventions.model.Convention;
import amai.org.conventions.model.Feedback;

public class ConventionFeedbackMail extends FeedbackMail {
	public ConventionFeedbackMail(Context context) {
		super(context);
	}

	@Override
	protected Feedback getFeedback() {
		return Convention.getInstance().getFeedback();
	}

	@Override
	protected String getSubject() {
		return context.getString(R.string.convention_feedback_mail_title);
	}

	@Override
	protected String getBody(String formattedQuestions, String deviceId) {
		return String.format(Dates.getLocale(), "%s\n\t\n\t\n\t\nDeviceId:\n%s", formattedQuestions, deviceId);
	}
}

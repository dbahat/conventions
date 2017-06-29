package amai.org.conventions.feedback.mail;

import android.content.Context;

import amai.org.conventions.R;
import amai.org.conventions.model.Feedback;
import amai.org.conventions.model.conventions.Convention;
import amai.org.conventions.utils.Dates;

public class ConventionFeedbackMail extends FeedbackMail {
	private Convention convention;

	public ConventionFeedbackMail(Context context, Convention convention) {
		super(context);
		this.convention = convention;
	}

	@Override
	protected Feedback getFeedback() {
		return convention.getFeedback();
	}

	@Override
	protected String getSubject() {
		return context.getString(R.string.convention_feedback_mail_title, Convention.getInstance().getDisplayName());
	}

	@Override
	protected String getBody(String formattedQuestions, String deviceId) {
		return String.format(Dates.getLocale(), "%s\n\t\n\t\n\t\nDeviceId:\n%s", formattedQuestions, deviceId);
	}
}

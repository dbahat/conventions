package amai.org.conventions.feedback.mail;

import android.content.Context;

import java.io.IOException;
import java.util.Properties;

import amai.org.conventions.R;
import amai.org.conventions.feedback.SurveySender;
import amai.org.conventions.model.Survey;
import amai.org.conventions.model.FeedbackQuestion;
import amai.org.conventions.model.conventions.Convention;
import amai.org.conventions.utils.Dates;

public abstract class FeedbackMail extends SurveySender {
	private static GMailSender sender;

	public FeedbackMail(Context context) {
		super(context);
	}

	private static GMailSender createGMailSender(Context context) {
		Properties properties = new Properties();
		try {
			properties.load(context.getResources().openRawResource(R.raw.mail));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		String mail = properties.getProperty("mail");
		String password = properties.getProperty("password");
		if (mail == null || password == null) {
			throw new RuntimeException("mail.properties file is missing the mail or password");
		}

		return new GMailSender(mail, password);
	}

	protected void sendSurvey(Survey feedback) throws Exception {
		getSender().sendMail(
				getSubject(),
				getBody(getFormattedQuestions(feedback), getDeviceId()),
				FeedbackMail.sender.getUser(),
				getMailRecipient());
	}

	private synchronized GMailSender getSender() {
		if (sender == null) {
			sender = createGMailSender(context);
		}
		return sender;
	}

	protected abstract String getSubject();

	protected abstract String getBody(String formattedQuestions, String deviceId);

	protected String getMailRecipient() {
		return Convention.getInstance().getFeedbackRecipient();
	}

	protected String getFormattedQuestions(Survey feedback) {
		StringBuilder stringBuilder = new StringBuilder();
		for (FeedbackQuestion question : feedback.getQuestions()) {
			if (question.hasAnswer()) {
				stringBuilder.append(String.format(Dates.getLocale(), "%s\n%s\n\t\n\t\n",
						question.getQuestionText(context.getResources(), feedback.isSent()),
						question.getAnswer()));
			}
		}

		return stringBuilder.toString();
	}

}

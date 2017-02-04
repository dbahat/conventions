package amai.org.conventions.utils;

import android.content.Context;
import android.os.AsyncTask;
import android.provider.Settings;
import android.view.View;

import java.io.IOException;
import java.util.Properties;

import amai.org.conventions.R;
import amai.org.conventions.model.Feedback;
import amai.org.conventions.model.FeedbackQuestion;
import amai.org.conventions.model.conventions.Convention;

public abstract class FeedbackMail {
	protected Context context;
	private static GMailSender sender;

	public FeedbackMail(Context context) {
		this.context = context;
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

	private synchronized GMailSender getSender() {
		if (sender == null) {
			sender = createGMailSender(context);
		}
		return sender;
	}

	public void send() throws Exception {
		Feedback feedback = getFeedback();
		getSender().sendMail(
				getSubject(),
				getBody(getFormattedQuestions(feedback), getDeviceId()),
				sender.getUser(),
				getMailRecipient());

		feedback.setIsSent(true);
		feedback.removeUnansweredQuestions();
	}

	protected abstract Feedback getFeedback();

	protected abstract String getSubject();

	protected abstract String getBody(String formattedQuestions, String deviceId);

	protected String getMailRecipient() {
		return Convention.getInstance().getFeedbackRecipient();
	}

	protected String getFormattedQuestions(Feedback feedback) {
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

	protected String getDeviceId() {
		return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
	}

	public static abstract class SendEventMailOnClickListener implements View.OnClickListener {

		/**
		 * Called on UI thread before starting the background task
		 */
		protected void beforeStart() {
		}

		/**
		 * Called before sending the mail, in the background
		 */
		protected void beforeSend() {
		}

		/**
		 * Called after sending the mail, in the background. The feedback state is updated before calling this method.
		 */
		protected void afterSend() {
		}

		/**
		 * Called on UI thread after the mail is sent, before onSuccess and onFailure calls.
		 */
		protected void afterEnd(Exception exception) {
		}

		protected void onFailure(Exception exception) {
		}

		protected void onSuccess() {
		}

		protected abstract FeedbackMail getFeedbackMail();

		@Override
		public void onClick(View v) {
			beforeStart();

			new AsyncTask<Void, Void, Exception>() {

				@Override
				protected Exception doInBackground(Void... params) {
					try {
						beforeSend();
						getFeedbackMail().send();
						afterSend();

						// In case everything finished successfully, pass null to onPostExecute.
						return null;
					} catch (Exception e) {
						return e;
					}
				}

				@Override
				protected void onPostExecute(Exception exception) {
					afterEnd(exception);

					if (exception != null) {
						onFailure(exception);
					} else {
						onSuccess();
					}
				}

			}.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

		}
	}
}

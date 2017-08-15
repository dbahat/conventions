package amai.org.conventions.feedback.forms;

import android.content.Context;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

import amai.org.conventions.feedback.FeedbackSender;
import amai.org.conventions.model.Survey;
import amai.org.conventions.utils.Log;

public abstract class FeedbackFormSender extends FeedbackSender {
	private final static String TAG = FeedbackFormSender.class.getCanonicalName();
	private static final int TIMEOUT = 10000;
	private FeedbackForm form;

	public FeedbackFormSender(Context context, FeedbackForm form) {
		super(context);
		this.form = form;
	}

	protected FeedbackForm getForm() {
		return form;
	}

	protected abstract Map<String, String> getAnswers();

	@Override
	protected void sendFeedback(Survey feedback) throws Exception {
		Map<String, String> questionsAndAnswers = getAnswers();
		StringBuilder postDataBuilder = new StringBuilder();
		boolean first = true;
		for (Map.Entry<String, String> questionAndAnswer : questionsAndAnswers.entrySet()) {
			if (!first) {
				postDataBuilder.append("&");
			}
			postDataBuilder.append(URLEncoder.encode(questionAndAnswer.getKey(), "UTF-8"))
					.append("=")
					.append(URLEncoder.encode(questionAndAnswer.getValue(), "UTF-8"));
			first = false;
		}

		URL url = form.getSendUrl();

		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		BufferedWriter writer = null;
		OutputStream outputStream = null;
		try {
			connection.setReadTimeout(TIMEOUT);
			connection.setConnectTimeout(TIMEOUT);
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			connection.setDoInput(true);
			connection.setDoOutput(true);

			outputStream = connection.getOutputStream();
			writer = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
			writer.write(postDataBuilder.toString());
			writer.flush();

			verifyResponse(connection);
		} finally {
			try {
				if (writer != null) {
					writer.close();
				}
				if (outputStream != null) {
					outputStream.close();
				}
			} catch (Exception e) {
				Log.e(TAG, "could not close output stream", e);
			}
		}

	}

	private void verifyResponse(HttpURLConnection connection) throws Exception {
		InputStream inputStream = null;
		BufferedReader reader = null;
		try {
			int responseCode = connection.getResponseCode();
			if (responseCode >= 200 && responseCode <= 299) {
				inputStream = connection.getInputStream();
			} else {
				inputStream = connection.getErrorStream();
			}

			reader = new BufferedReader(new InputStreamReader(inputStream));
			StringBuilder responseBuilder = new StringBuilder();
			String output;
			while ((output = reader.readLine()) != null) {
				responseBuilder.append(output);
			}
			String resopnseBody = responseBuilder.toString();

			// Check if the form was sent successfully
			// Unfortunately, even for unsuccessful send we get a 200 response, so we need to check the output.
			// There is no indication of error messages, but the "form was sent" message has class "freebirdFormviewerViewResponseConfirmationMessage"
			// so we check if it exists (the success message itself is localized so we can't check its text).
			// There is no error in case a field with a non-existing id was sent, but there is if a required field was not sent and if the form is
			// not accepting answers. In case a non-existing form ID is used the response code is 404.
			if (responseCode != HttpsURLConnection.HTTP_OK || !resopnseBody.contains("\"freebirdFormviewerViewResponseConfirmationMessage\"")) {
				throw new RuntimeException("Could not send feedback, response code: " + responseCode + ", response body:\n" + resopnseBody);
			}
		} finally {
			try {
				if (reader != null) {
					reader.close();
				}
				if (inputStream != null) {
					inputStream.close();
				}
			} catch (Exception e) {
				Log.e(TAG, "could not close input stream", e);
			}
		}
	}
}

package amai.org.conventions.feedback.forms;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

import amai.org.conventions.feedback.SurveySender;
import amai.org.conventions.model.FeedbackQuestion;
import amai.org.conventions.model.Survey;
import amai.org.conventions.utils.Log;

public abstract class SurveyFormSender extends SurveySender {
	private final static String TAG = SurveyFormSender.class.getCanonicalName();
	private static final int TIMEOUT = 10000;
	private SurveyForm form;

	public SurveyFormSender(SurveyForm form) {
		this.form = form;
	}

	protected Map<String, String> getAnswers() {
		Map<String, String> answers = new HashMap<>();
		// Questions
		List<FeedbackQuestion> questions = getSurvey().getQuestions();
		for (FeedbackQuestion question : questions) {
			if (question.hasAnswer()) {
				answers.put(form.getQuestionEntry(question.getQuestionId()), question.getAnswer().toString());
			}
		}
		return answers;
	}

	@Override
	protected void sendSurvey(Survey survey) throws Exception {
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
			String responseBody = responseBuilder.toString();

			// Check if the form was sent successfully
			// Unfortunately, even for unsuccessful send we get a 200 response, so we need to check the output.
			// There is no indication of error messages, but the "form was sent" message has class "freebirdFormviewerViewResponseConfirmationMessage"
			// in case of a new form and "ss-resp-message" in case of an old form so we check if one of them exists (the success message itself is
			// localized so we can't check its text).
			// There is no error in case a field with a non-existing id was sent, there is if the form is not accepting answers.
			// For new forms there is an error if a required field was not sent.
			// In case a non-existing form ID is used the response code is 404.
			if (responseCode != HttpsURLConnection.HTTP_OK ||
					(!responseBody.contains("\"freebirdFormviewerViewResponseConfirmationMessage\"") && !responseBody.contains("ss-resp-message"))) {
				throw new RuntimeException("Could not send feedback, response code: " + responseCode + ", response body:\n" + responseBody);
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

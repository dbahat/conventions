package amai.org.conventions.feedback.forms;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

import amai.org.conventions.feedback.SurveySender;
import amai.org.conventions.model.FeedbackQuestion;
import amai.org.conventions.model.Survey;
import amai.org.conventions.networking.SurveyDataRetriever;
import amai.org.conventions.utils.HttpConnectionCreator;
import amai.org.conventions.utils.Log;
import amai.org.conventions.utils.URLUtils;

public abstract class SurveyFormSender extends SurveySender {
	private final static String TAG = SurveyFormSender.class.getCanonicalName();
	private SurveyForm form;
	private SurveyDataRetriever.DisabledMessage disabledMessageRetriever;

	public SurveyFormSender(SurveyForm form, SurveyDataRetriever.DisabledMessage disabledMessageRetriever) {
		this.form = form;
		this.disabledMessageRetriever = disabledMessageRetriever;
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
			postDataBuilder.append(URLUtils.encodeURLParameterValue(questionAndAnswer.getKey()))
					.append("=")
					.append(URLUtils.encodeURLParameterValue(questionAndAnswer.getValue()));
			first = false;
		}

		URL url = form.getSendUrl();

		// TODO check if google forms API support creating responses (as for Match 2022 it only supports reading responses)
		// and use the API instead of the below code
		// https://developers.google.com/forms/api/reference/rest/v1/forms.responses
		HttpURLConnection connection = HttpConnectionCreator.createConnection(url);
		BufferedWriter writer = null;
		OutputStream outputStream = null;
		try {
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			connection.setDoInput(true);
			connection.setDoOutput(true);
			connection.setInstanceFollowRedirects(false);

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

	protected void verifyResponse(HttpURLConnection connection) throws Exception {
		InputStream inputStream = null;
		BufferedReader reader = null;
		try {
			int responseCode = connection.getResponseCode();

			// Check if the form is closed and throw the correct exception
			if (responseCode == HttpsURLConnection.HTTP_MOVED_TEMP && connection.getHeaderField("Location").contains("closedform")) {
				// In the special case of survey votes, it's possible the request will fail since the survey was disabled.
				// In such a case, try to fetch the disable message and propagate it to the caller.
				throw new SurveyDisabledException(tryFetchDisabledMessage());
			}

			// Check if the form was sent successfully.
			// In old forms, even for unsuccessful send we sometimes got a 200 response, so we had to check the output.
			// In new forms this has been fixed so we can rely on the response code.
			// We don't support old forms anymore because we can't know when the response is actually successful
			// (we used to be able to tell according to the success message css class but that's not possible anymore).
			if (responseCode != HttpsURLConnection.HTTP_OK) {
				String responseBody = "";
				if (responseCode >= 200 && responseCode <= 299) {
					inputStream = connection.getInputStream();
				} else {
					inputStream = connection.getErrorStream();
				}
				if (inputStream != null) {
					reader = new BufferedReader(new InputStreamReader(inputStream));
					StringBuilder responseBuilder = new StringBuilder();
					String output;
					while ((output = reader.readLine()) != null) {
						responseBuilder.append(output);
					}
					responseBody = responseBuilder.toString();
				}
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

	private String tryFetchDisabledMessage() {
		try {
			if (disabledMessageRetriever == null) {
				return "";
			}
			return disabledMessageRetriever.retrieveClosedMessage();
		} catch (Exception e) {
			return "";
		}
	}
}

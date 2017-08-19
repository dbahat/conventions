package amai.org.conventions.networking;

import android.support.annotation.Nullable;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.sheets.v4.Sheets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import amai.org.conventions.model.conventions.Convention;

/**
 * Container for components that retrieve data related to surveys
 */
public interface SurveyDataRetriever {

	interface Answers {
		/**
		 * @return the possible answers to a survey, or null if no answers were defined.
		 * @throws IOException in case there was an error retrieving the data.
		 */
		@Nullable
		List<String> retrieveAnswers() throws IOException;
	}

	/**
	 * @return the survey disabled message, or null if no such message was defined.
	 * @throws IOException in case there was an error retrieving the data.
	 */
	interface DisabledMessage {
		@Nullable
		String retrieveClosedMessage() throws IOException;
	}

	/**
	 * Retrieves the possible survey answers and custom closed message from a google spreadsheet.
	 *
	 * Assumptions:
	 * 1. The answers are located in a sheet named "Answers", in the first row of column A.
	 * 2. The survey closed message is located in a sheet named "ClosedMessage" in the first cell of the first row.
	 * 3. The sheet is configured to allow unauthenticated read access.
	 * 4. The relevant google form is configured to sync with the spreadsheet via google script.
	 *
	 * Fetching answers from a spreadsheet and not a google form since google forums don't have API to fetch values from.
	 */
	class GoogleSpreadSheet implements SurveyDataRetriever.Answers, DisabledMessage {

		// The range strings uses A1 notation. See https://developers.google.com/sheets/api/guides/concepts#a1_notation
		private static final String ANSWERS_RANGE = "Answers!A:A";
		private static final String CLOSED_MESSAGE_RANGE = "ClosedMessage!A1:A1";

		private String spreadsheetId;

		public GoogleSpreadSheet(String spreadsheetId) {
			this.spreadsheetId = spreadsheetId;
		}

		@Override
		public List<String> retrieveAnswers() throws IOException {
			List<List<Object>> values = retrieveValuesForRange(spreadsheetId, ANSWERS_RANGE);

			List<String> result = new ArrayList<>(values.size());
			for (List<Object> row : values) {
				if (row.size() > 0) {
					result.add(row.get(0).toString());
				}
			}

			return result;
		}

		@Override
		public String retrieveClosedMessage() throws IOException {
			List<List<Object>> values = retrieveValuesForRange(spreadsheetId, CLOSED_MESSAGE_RANGE);

			if (values.size() > 0 && values.get(0).size() > 0) {
				return values.get(0).get(0).toString();
			}

			return null;
		}

		private static List<List<Object>> retrieveValuesForRange(String spreadsheetId, String a1NotationRange) throws IOException {
			return createSheetsServiceClient()
					.spreadsheets()
					.values()
					.get(spreadsheetId, a1NotationRange)
					.setKey(Convention.getInstance().getGoogleSpreadsheetsApiKey())
					.execute()
					.getValues();
		}

		private static Sheets createSheetsServiceClient() {
			return new Sheets.Builder(AndroidHttp.newCompatibleTransport(), JacksonFactory.getDefaultInstance(), null)
					.build();
		}
	}
}

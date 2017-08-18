package amai.org.conventions.networking;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.ValueRange;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import amai.org.conventions.model.conventions.Convention;

/**
 * Retrieves the possible answers to a survey
 */
public interface SurveyAnswersRetriever {
	List<String> getAnswers() throws IOException;

	/**
	 * Retrieves the possible survey answers from a google spreadsheet.
	 *
	 * Assumptions:
	 * 1. The answers are located in a sheet named "Answers", in the first row of column A.
	 * 2. The sheet is configured to allow unauthenticated read access.
	 *
	 * Fetching answers from a spreadsheet and not a google forum since google forums don't have API to fetch values from.
	 */
	class GoogleSpreadSheet implements SurveyAnswersRetriever {

		private static final String RANGE = "Answers!A:A";
		private String spreadsheetId;

		public GoogleSpreadSheet(String spreadsheetId) {
			this.spreadsheetId = spreadsheetId;
		}

		@Override
		public List<String> getAnswers() throws IOException {

			Sheets sheets = new Sheets.Builder(AndroidHttp.newCompatibleTransport(), JacksonFactory.getDefaultInstance(), null)
					.build();

			ValueRange response = sheets.spreadsheets().values()
					.get(spreadsheetId, RANGE)
					.setKey(Convention.getInstance().getGoogleSpreadsheetsApiKey())
					.execute();

			List<List<Object>> values = response.getValues();
			List<String> result = new ArrayList<>(values.size());
			for (List<Object> row : values) {
				result.add(row.get(0).toString());
			}

			return result;
		}
	}
}

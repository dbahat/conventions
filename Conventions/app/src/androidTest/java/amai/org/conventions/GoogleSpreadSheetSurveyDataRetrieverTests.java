package amai.org.conventions;

import androidx.test.runner.AndroidJUnit4;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.List;

import amai.org.conventions.networking.SurveyDataRetriever;

@RunWith(AndroidJUnit4.class)
public class GoogleSpreadSheetSurveyDataRetrieverTests {

	/**
	 * Note - This test class assumes there's a test google spreadsheet document with the given ID and values, as well as internet connection.
	 */
	private static final String TEST_SPREADSHEET_ID = "1YadZlCD9TA66DK4r-9z-5Mbb_wdwrAVz5Ypo3tcYA1Q";

	@Test
	public void RetrieveAnswers_Returns_Possible_Answers_Of_A_Test_Google_Spreadsheet() throws Exception {
		List<String> result = new SurveyDataRetriever.GoogleSpreadSheet(TEST_SPREADSHEET_ID).retrieveAnswers();

		Assert.assertNotNull(result);
		Assert.assertEquals(3, result.size());
		Assert.assertEquals("FirstSelection2", result.get(0));
		Assert.assertEquals("SecondSelection2", result.get(1));
		Assert.assertEquals("ThirdSelection2", result.get(2));
	}

	@Test
	public void RetrieveClosedMessage_Returns_Custom_Closed_Message_Of_A_Test_Google_Spreadsheet() throws Exception {
		String result = new SurveyDataRetriever.GoogleSpreadSheet(TEST_SPREADSHEET_ID).retrieveClosedMessage();

		Assert.assertEquals("The sudoku is over!", result);
	}

	@Test(expected = IOException.class)
	public void RetrieveAnswers_Throws_When_Using_Non_Existing_SpreadsheetId() throws Exception {
		new SurveyDataRetriever.GoogleSpreadSheet("InvalidId").retrieveAnswers();
	}

	@Test(expected = IOException.class)
	public void RetrieveClosedMessage_Throws_When_Using_Non_Existing_SpreadsheetId() throws Exception {
		new SurveyDataRetriever.GoogleSpreadSheet("InvalidId").retrieveClosedMessage();
	}
}

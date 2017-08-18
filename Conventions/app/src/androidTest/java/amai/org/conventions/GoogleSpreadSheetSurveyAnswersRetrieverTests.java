package amai.org.conventions;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

import amai.org.conventions.networking.SurveyAnswersRetriever;

public class GoogleSpreadSheetSurveyAnswersRetrieverTests {

	/**
	 * Note - This test assumes there's a test google spreadsheet document with the given ID and values, as well as internet connection.
	 */
	@Test
	public void GetAnswers_Returns_Possible_Answers_Of_A_Test_Google_Spreadsheet() throws IOException {
		String testSpreadsheetId = "1YadZlCD9TA66DK4r-9z-5Mbb_wdwrAVz5Ypo3tcYA1Q";
		List<String> result = new SurveyAnswersRetriever.GoogleSpreadSheet(testSpreadsheetId).getAnswers();

		Assert.assertEquals("FirstSelection2", result.get(0));
		Assert.assertEquals("SecondSelection2", result.get(1));
		Assert.assertEquals("ThirdSelection2", result.get(2));
	}
}

package amai.org.conventions;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import amai.org.conventions.model.ConventionEvent;
import amai.org.conventions.networking.AmaiModelConverter;
import androidx.test.ext.junit.runners.AndroidJUnit4;

@RunWith(AndroidJUnit4.class)
public class ConventionEventTests {
	// These tests must run on with an Android sdk because they use android.text classes
	@Test
	public void getSpannedDescription_adds_linebreak_after_div_with_iframe() {
		String desc = "<div>\n" +
			"<iframe src=\"https://docs.google.com/forms/d/e/some-form-id/viewform?embedded=true\" loading=\"lazy\" style=\"width: 100%;height: 80vh;\" frameborder=\"0\" marginheight=\"0\" marginwidth=\"0\">Loading&#8230;</iframe>\n" +
			"</div>\n" +
			"<div class=\"wp-block-group amai-tab-content has-global-padding is-layout-constrained wp-block-group-is-layout-constrained\">\n" +
			"<iframe src=\"https://docs.google.com/forms/d/e/form-id-2/viewform?embedded=true\" loading=\"lazy\" style=\"width: 100%;height: 80vh;\" frameborder=\"0\" marginheight=\"0\" marginwidth=\"0\">Loading&#8230;</iframe>\n" +
			"</div>";

		ConventionEvent event = new ConventionEvent().withDescription(AmaiModelConverter.convertEventDescription(desc));

		String result = event.getSpannedDescription().toString();
		String linkText = ConventionEvent.FORM_LINK_TEXT;

		// Validate that the link text isn't split in the middle, and there is at least 1 linebreak
		// between the links (but not more than 2). The rest is added by the HTML parser and doesn't matter
		// if it changes (as long as it still looks ok).
		// This test was added due to a bug where the linebreak was added after the first character of the link.
		Assert.assertEquals(linkText + "\n \n" + linkText + "\n ", result);
	}
}

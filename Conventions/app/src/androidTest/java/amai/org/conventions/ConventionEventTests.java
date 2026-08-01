package amai.org.conventions;

import android.text.Spanned;
import android.text.style.URLSpan;

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
	public void getSpannedDescription_adds_linebreak_after_div_with_form_iframe() {
		String desc = "<div>\n" +
			"<iframe src=\"https://docs.google.com/forms/d/e/some-form-id/viewform?embedded=true\" loading=\"lazy\" style=\"width: 100%;height: 80vh;\" frameborder=\"0\" marginheight=\"0\" marginwidth=\"0\">Loading&#8230;</iframe>\n" +
			"</div>\n" +
			"<div class=\"wp-block-group amai-tab-content has-global-padding is-layout-constrained wp-block-group-is-layout-constrained\">\n" +
			"<iframe src=\"https://docs.google.com/forms/d/e/form-id-2/viewform?embedded=true\" loading=\"lazy\" style=\"width: 100%;height: 80vh;\" frameborder=\"0\" marginheight=\"0\" marginwidth=\"0\">Loading&#8230;</iframe>\n" +
			"</div>";

		ConventionEvent event = new ConventionEvent().withDescription(AmaiModelConverter.convertEventDescription(desc));

		Spanned spannedDescription = event.getSpannedDescription();
		String textDescription = spannedDescription.toString();
		String linkText = ConventionEvent.FORM_LINK_TEXT;

		// Validate that the link text isn't split in the middle, and there is at least 1 linebreak
		// between the links (but not more than 2). The rest is added by the HTML parser and doesn't matter
		// if it changes (as long as it still looks ok).
		// This test was added due to a bug where the linebreak was added after the first character of the link.
		Assert.assertEquals(linkText + "\n \n" + linkText + "\n ", textDescription);

		// Validate the links point to the forms
		URLSpan[] urlSpans = spannedDescription.getSpans(0, spannedDescription.length(), URLSpan.class);
		Assert.assertEquals(2, urlSpans.length);
		URLSpan firstURLSpan = urlSpans[0];
		Assert.assertEquals("https://docs.google.com/forms/d/e/some-form-id/viewform?embedded=true", firstURLSpan.getURL());
		Assert.assertEquals(0, spannedDescription.getSpanStart(firstURLSpan));
		Assert.assertEquals(linkText.length(), spannedDescription.getSpanEnd(firstURLSpan));
		URLSpan secondURLSpan = urlSpans[1];
		Assert.assertEquals("https://docs.google.com/forms/d/e/form-id-2/viewform?embedded=true", secondURLSpan.getURL());
		Assert.assertEquals((linkText + "\n \n").length() - 1, spannedDescription.getSpanStart(secondURLSpan)); // TODO the -1 should probably not be added
		Assert.assertEquals((linkText + "\n \n").length() + linkText.length(), spannedDescription.getSpanEnd(secondURLSpan));
	}

	@Test
	public void getSpannedDescription_adds_linebreak_after_div_with_form() {
		String desc = "<div class=\"ss-form-container\">\n" +
			"<form action=\"https://docs.google.com/forms/d/e/some-form-id/viewform?embedded=true\">Loading&#8230;</form>\n" +
			"</div>\n";

		ConventionEvent event = new ConventionEvent().withDescription(AmaiModelConverter.convertEventDescription(desc));

		Spanned spannedDescription = event.getSpannedDescription();
		String textDescription = spannedDescription.toString();
		String linkText = ConventionEvent.FORM_LINK_TEXT;

		// Validate that the link text is added
		Assert.assertEquals(linkText, textDescription);

		// Validate the links point to the form
		URLSpan[] urlSpans = spannedDescription.getSpans(0, spannedDescription.length(), URLSpan.class);
		Assert.assertEquals(1, urlSpans.length);
		URLSpan firstURLSpan = urlSpans[0];
		Assert.assertEquals("https://docs.google.com/forms/d/e/some-form-id/viewform?embedded=true", firstURLSpan.getURL());
		Assert.assertEquals(0, spannedDescription.getSpanStart(firstURLSpan));
		Assert.assertEquals(linkText.length(), spannedDescription.getSpanEnd(firstURLSpan));
	}

	@Test
	public void getSpannedDescription_keeps_form_urls_in_regular_links() {
		String desc = "<p>this is a regular " +
			"<a href=\"https://docs.google.com/forms/d/e/some-form-id/viewform?embedded=true\" target=\"_blank\" rel=\"noreferrer noopener nofollow\">" +
			"link to a form</a>.</p>";

		ConventionEvent event = new ConventionEvent().withDescription(AmaiModelConverter.convertEventDescription(desc));

		Spanned spannedDescription = event.getSpannedDescription();
		String textDescription = spannedDescription.toString();

		// Validate that the link text is added
		Assert.assertEquals("this is a regular link to a form.", textDescription);

		// Validate the links point to the form
		URLSpan[] urlSpans = spannedDescription.getSpans(0, spannedDescription.length(), URLSpan.class);
		Assert.assertEquals(1, urlSpans.length);
		URLSpan firstURLSpan = urlSpans[0];
		Assert.assertEquals("https://docs.google.com/forms/d/e/some-form-id/viewform?embedded=true", firstURLSpan.getURL());
		Assert.assertEquals(18, spannedDescription.getSpanStart(firstURLSpan));
		Assert.assertEquals(textDescription.length() - 1, spannedDescription.getSpanEnd(firstURLSpan));
	}

	@Test
	public void getSpannedDescription_removes_fragment_only_links() {
		String desc = "<p>this is a " +
			"<a href=\"#somewhere\">" +
			"fragment only link</a>.</p>";

		ConventionEvent event = new ConventionEvent().withDescription(AmaiModelConverter.convertEventDescription(desc));

		Spanned spannedDescription = event.getSpannedDescription();
		String textDescription = spannedDescription.toString();

		// Validate that the link text is added
		Assert.assertEquals("this is a fragment only link.", textDescription);

		// Validate the link was removed
		URLSpan[] urlSpans = spannedDescription.getSpans(0, spannedDescription.length(), URLSpan.class);
		Assert.assertEquals(0, urlSpans.length);
	}

	@Test
	public void getSpannedDescription_removes_hidden_content() {
		String desc = "<p>the rest of this is" +
			"<p class=\"has-text-align-right decimal-ol editorskit-no-mobile wp-block-paragraph\">" +
			" hidden</p>";

		ConventionEvent event = new ConventionEvent().withDescription(AmaiModelConverter.convertEventDescription(desc));

		Spanned spannedDescription = event.getSpannedDescription();
		String textDescription = spannedDescription.toString();

		// Validate that the hidden text was removed
		Assert.assertEquals("the rest of this is", textDescription);
	}

	@Test
	public void getSpannedDescription_removes_nested_hidden_content() {
		String desc = "<p>the rest of this is" +
			"<xdiv class=\"wp-block-column is-vertically-aligned-top editorskit-no-mobile is-layout-flow wp-block-column-is-layout-flow\" >" +
			"<p class=\"has-text-align-right decimal-ol editorskit-no-mobile wp-block-paragraph\">" +
			" hidden</p></xdiv>";

		ConventionEvent event = new ConventionEvent().withDescription(AmaiModelConverter.convertEventDescription(desc));

		Spanned spannedDescription = event.getSpannedDescription();
		String textDescription = spannedDescription.toString();

		// Validate that the hidden text was removed
		Assert.assertEquals("the rest of this is", textDescription);
	}
}

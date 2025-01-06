package amai.org.conventions.networking;

import android.text.Html;

import java.util.List;

public class ParseUtils {
	private static HTMLParser htmlParser;
	private static TextUtils textUtils;

	public static String parseHTML(String string) {
		if (htmlParser != null) {
			return htmlParser.parse(string);
		}

		if (string == null) {
			return null;
		}
		// Using deprecated fromHtml() overload, since fromHtml(string, int) is only supported from api level 17
		// noinspection deprecation
		return Html.fromHtml(string).toString();
	}

	public interface HTMLParser {
		String parse(String html);
	}

	public static void setHtmlParser(HTMLParser htmlParser) {
		ParseUtils.htmlParser = htmlParser;
	}

	public static String joinStrings(String delimiter, List<String> strings) {
		if (textUtils != null) {
			return textUtils.join(delimiter, strings);
		}
		return android.text.TextUtils.join(delimiter, strings);
	}

	public static boolean isEmpty(String string) {
		if (textUtils != null) {
			return textUtils.isEmpty(string);
		}
		return android.text.TextUtils.isEmpty(string);
	}

	public interface TextUtils {
		String join(String delimiter, List<String> strings);
		boolean isEmpty(String string);
	}

	public static void setTextUtils(TextUtils stringJoiner) {
		ParseUtils.textUtils = stringJoiner;
	}
}

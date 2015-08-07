package amai.org.conventions.utils;

/**
 * Created by dbahat on 8/7/2015.
 */
public class Strings {
    private static final int MAX_SNIPPIT_SIZE = 100;

    /**
     * Returns a short snippit of the input text, for the area around the input keyword.
     * For example: for the input text "This is a very long message with a lot of text" and keyword "of", the expected output will look like
     * "...with a lot of text".
     *
     * @param textToSnip The input text to snip.
     * @param keyword The keyword to snip the text around.
     * @return The text after snipping.
     */
    public static String snipTextNearKeyword(String textToSnip, String keyword) {
	    String lowerCaseText = textToSnip.toLowerCase();
        int startIndex = findIndexOfXWordBefore(lowerCaseText, keyword, 3);

        // Note - trimming the text to be at most MAX_SNIPPIT_SIZE, since textView seem to appear distorted when passing
        // a much larger text than the max allowed.
        if (startIndex <= 0) {
            return textToSnip.substring(0, Math.min(textToSnip.length(), MAX_SNIPPIT_SIZE));
        } else {
            return "..." + textToSnip.substring(startIndex, Math.min(textToSnip.length(), startIndex + MAX_SNIPPIT_SIZE));
        }
    }

    private static int findIndexOfXWordBefore(String searchString, String keyword, int numberOfWordsBefore) {
        int index = searchString.indexOf(keyword);
        while (index > 0 && numberOfWordsBefore > 0) {
            index = searchString.lastIndexOf(" ", index - 1);
            numberOfWordsBefore--;
        }

        return index;
    }
}

package amai.org.conventions.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class URLUtils {
	public static String encodeURLParameterValue(String keyOrValue) {
		try {
			return URLEncoder.encode(keyOrValue, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// UTF-8 should always be supported
			throw new RuntimeException(e);
		}
	}

	public static String encodeURLPath(String pathPart) {
		try {
			return URLEncoder.encode(pathPart, "UTF-8").replace("+", "%20");
		} catch (UnsupportedEncodingException e) {
			// UTF-8 should always be supported
			throw new RuntimeException(e);
		}
	}
}

package amai.org.conventions.utils;

import android.os.Build;

import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;

public class HttpConnectionCreator {
	private static final int REQUEST_TIMEOUT = 10000;

	public static HttpURLConnection createConnection(URL url) throws Exception {
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setConnectTimeout(REQUEST_TIMEOUT);
		connection.setReadTimeout(REQUEST_TIMEOUT);
		return connection;
	}
}

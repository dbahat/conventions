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
		// Android versions before L don't support TLSv1.2 by default, so we have to enable it explicitly
		if (Build.VERSION.SDK_INT < 21 && connection instanceof HttpsURLConnection) {
			// Enable TLSv1.2
			SSLContext sslContext = SSLContext.getInstance("TLSv1.2");
			sslContext.init(null, null, null);
			SSLSocketFactoryWrapper factory = new SSLSocketFactoryWrapper(sslContext.getSocketFactory());
			factory.setEnabledProtocols(new String[]{ "TLSv1.2" });
			((HttpsURLConnection) connection).setSSLSocketFactory(factory);
		}

		connection.setConnectTimeout(REQUEST_TIMEOUT);
		connection.setReadTimeout(REQUEST_TIMEOUT);
		return connection;
	}
}

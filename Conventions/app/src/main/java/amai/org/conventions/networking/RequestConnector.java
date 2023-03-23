package amai.org.conventions.networking;

import java.io.IOException;
import java.net.HttpURLConnection;

public class RequestConnector {
	private HttpURLConnection request;
	private boolean connected = false;

	public RequestConnector(HttpURLConnection request) {
		this.request = request;
	}

	public void connect() throws IOException {
		request.connect();
		connected = true;
	}

	public void disconnect() {
		if (connected) {
			request.disconnect();
		}
	}

	public HttpURLConnection getRequest() {
		return request;
	}
}

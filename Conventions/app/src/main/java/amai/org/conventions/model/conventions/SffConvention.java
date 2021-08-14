package amai.org.conventions.model.conventions;

import java.net.HttpURLConnection;
import java.net.URL;

import amai.org.conventions.networking.EventTicketsParser;
import amai.org.conventions.networking.ModelParser;
import amai.org.conventions.networking.SffEventTicketsParser;
import amai.org.conventions.networking.SffModelParser;
import amai.org.conventions.utils.HttpConnectionCreator;
import amai.org.conventions.utils.URLUtils;

public abstract class SffConvention extends Convention {
	@Override
	public ModelParser getModelParser() {
		return new SffModelParser();
	}

	@Override
	public EventTicketsParser getEventTicketsParser() {
		return new SffEventTicketsParser();
	}

	@Override
	public String getGoogleSpreadsheetsApiKey() {
		return null;
	}

	@Override
	public boolean canUserLogin() {
		return true;
	}

	@Override
	public HttpURLConnection getUserQRRequest(String user) throws Exception {
		URL url = new URL("https://api.sf-f.org.il/cons/qr/login/" + URLUtils.encodeURLPath(user));
		HttpURLConnection request = HttpConnectionCreator.createConnection(url);
		request.connect();
		return request;
	}
}

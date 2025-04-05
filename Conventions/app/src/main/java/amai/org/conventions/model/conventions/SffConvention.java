package amai.org.conventions.model.conventions;

import java.io.BufferedWriter;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
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
}

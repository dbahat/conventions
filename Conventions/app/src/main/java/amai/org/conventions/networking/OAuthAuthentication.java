package amai.org.conventions.networking;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import amai.org.conventions.model.conventions.Convention;
import amai.org.conventions.utils.HttpConnectionCreator;
import amai.org.conventions.utils.Log;
import amai.org.conventions.utils.URLUtils;
import sff.org.conventions.BuildConfig;

public class OAuthAuthentication {
	private final static String TAG = OAuthAuthentication.class.getCanonicalName();
	private static final OAuthAuthentication instance = new OAuthAuthentication();

	public static OAuthAuthentication getInstance() {
		return instance;
	}

	public String authenticate(String user, String password) throws Exception {
		URL oauthEndpoint = Convention.getInstance().getOAuthURL();
		HttpURLConnection connection = HttpConnectionCreator.createConnection(oauthEndpoint);
		BufferedWriter writer = null;
		OutputStream outputStream = null;
		try {
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			connection.setDoInput(true);
			connection.setDoOutput(true);
			connection.setInstanceFollowRedirects(false);

			outputStream = connection.getOutputStream();
			writer = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
			writer.write("client_id=con_apps&client_secret=d1f36c7e-83c1-4008-843c-8853f8e3c8ea&grant_type=password" +
					"&username=" + URLUtils.encodeURLParameterValue(user) + "&password=" + URLUtils.encodeURLParameterValue(password));
			writer.flush();

			int responseCode = connection.getResponseCode();

			if (responseCode != HttpURLConnection.HTTP_OK) {
				if (BuildConfig.DEBUG) {
					BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
					StringBuilder responseBuilder = new StringBuilder();
					String output;
					while ((output = bufferedReader.readLine()) != null) {
						responseBuilder.append(output);
					}
					String responseBody = responseBuilder.toString();
					Log.e(TAG, "Could not authenticate with oauth endpoint, response is: " + responseBody);
				}
				if (responseCode == HttpURLConnection.HTTP_UNAUTHORIZED) {
					throw new AuthenticationException();
				}
				throw new RuntimeException("Could not authenticate, error code: " + responseCode);
			}

			return parseResponse((InputStream) connection.getContent());
		} finally {
			try {
				if (writer != null) {
					writer.close();
				}
				if (outputStream != null) {
					outputStream.close();
				}
				connection.disconnect();
			} catch (Exception e) {
				Log.e(TAG, "could not close output stream", e);
			}
		}
	}

	private String parseResponse(InputStream response) {
		InputStreamReader reader = null;
		try {
			reader = new InputStreamReader(response);
			JsonParser jp = new JsonParser();
			JsonElement root = jp.parse(reader);
			JsonObject rootObject = root.getAsJsonObject();
			return rootObject.getAsJsonPrimitive("access_token").getAsString();
		} finally {
			try {
				if (reader != null) {
					reader.close();
				}
			} catch (Exception e) {
				Log.e(TAG, "could not close input stream");
			}
		}
	}

	public static class AuthenticationException extends RuntimeException {}
}

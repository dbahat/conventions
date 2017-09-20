package amai.org.conventions.networking;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.InputStreamReader;

public class SffEventTicketsParser implements EventTicketsParser {
	private static final String TAG = SffEventTicketsParser.class.getCanonicalName();

	@Override
	public int parse(InputStreamReader reader) {
		JsonParser jp = new JsonParser();
		JsonElement root = jp.parse(reader);
		JsonObject ticketsInfo = root.getAsJsonObject();
		JsonElement availableTicketsElement = ticketsInfo.get("available_tickets");
		int ticketsNumber;
		if (availableTicketsElement == null) {
			ticketsNumber = -1;
		} else {
			ticketsNumber = availableTicketsElement.getAsInt();
		}
		return ticketsNumber;
	}
}

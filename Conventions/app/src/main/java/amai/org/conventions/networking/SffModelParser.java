package amai.org.conventions.networking;

import android.support.annotation.NonNull;
import android.text.Html;
import android.text.TextUtils;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import amai.org.conventions.model.ConventionEvent;
import amai.org.conventions.model.EventType;
import amai.org.conventions.model.Hall;
import amai.org.conventions.model.conventions.Convention;
import amai.org.conventions.utils.Dates;
import amai.org.conventions.utils.Log;

public class SffModelParser implements ModelParser {
	private static final String TAG = SffModelParser.class.getCanonicalName();

	@Override
	public List<ConventionEvent> parse(Date modifiedDate, InputStreamReader reader) {
		JsonParser jp = new JsonParser();
		JsonElement root = jp.parse(reader);
		JsonArray events = root.getAsJsonArray();

		List<ConventionEvent> eventList = new LinkedList<>();

		for (JsonElement event : events) {
			JsonObject eventObj = event.getAsJsonObject();
			int eventId = eventObj.get("id").getAsInt();
			String eventType = decodeHtml(eventObj.get("track").getAsString());

			String title = decodeHtml(eventObj.get("title").getAsString());

			String eventDescription = parseEventDescription(eventObj.get("description").getAsString());

			JsonObject timeObject = eventObj.get("time").getAsJsonObject();
			Date startTime = parseEventTime(timeObject.get("start").getAsString());
			Date endTime = parseEventTime(timeObject.get("end").getAsString());
			JsonArray speakers = eventObj.get("speakers").getAsJsonArray();
			String allSpeakers = speakers.size() > 0 ? TextUtils.join(", ", getSpeakers(speakers)) : "";

			String hallName = eventObj.get("location").isJsonNull() ? "" : decodeHtml(eventObj.get("location").getAsString());
			if (TextUtils.isEmpty(hallName)) {
				// Some SF-F events came up corrupted without a location.
				// Ignore them - they don't appear in the programme in the site either.
				Log.w(TAG, "Skipping event with no hall: " + title + " (" + eventId + ")");
				continue;
			}

			Hall hall = Convention.getInstance().getHalls().findByName(hallName);
			if (hall == null) {
				// Add a new hall to the convention
				hall = Convention.getInstance().getHalls().add(hallName);
				Log.i(TAG, "Found and added new hall with name " + hallName);
			}

			// We convert the categories to a single string because there is only 1 category
			// except in the case of attractions/fandom that always come together
			JsonArray categories = eventObj.get("categories").getAsJsonArray();
			String category = categories.size() > 0 ? categories.get(0).getAsString() : "";

			// Tags are returned as an object: {1: "tag1", 2: "tag2"} or a string: "tag1"
			List<String> tags;
			JsonElement tagsElement = eventObj.get("tags");
			if (tagsElement == null) {
				tags = Collections.emptyList();
			} else if (tagsElement.isJsonObject()) {
				tags = convertValuesToStringList(tagsElement.getAsJsonObject());
			} else {
				tags = Collections.singletonList(decodeHtml(tagsElement.getAsString()));
			}

			int price;
			JsonElement priceElement = eventObj.get("price");
			if (priceElement.getAsString().equals("חינם")) {
				price = 0;
			} else {
				price = priceElement.getAsInt();
			}

			int availableTickets;
			JsonElement availableTicketsElement = eventObj.get("available_tickets");
			if (availableTicketsElement == null) {
				availableTickets = -1;
			} else {
				availableTickets = availableTicketsElement.getAsInt();
			}

			String websiteUrl = eventObj.get("url").getAsString();

			ConventionEvent conventionEvent = new ConventionEvent()
					.withServerId(eventId)
					.withTitle(title)
					.withLecturer(allSpeakers)
					.withType(new EventType(eventType))
					.withDescription(eventDescription)
					.withStartTime(startTime)
					.withEndTime(endTime)
					.withHall(hall)
					.withId(String.valueOf(eventId))
					.withCategory(category)
					.withTags(tags)
					.withPrice(price)
					.withAvailableTickets(availableTickets)
					.withTicketsLastModifiedDate(modifiedDate)
					.withWebsiteUrl(websiteUrl);

			eventList.add(conventionEvent);
		}

		return eventList;
	}

	private String decodeHtml(String string) {
		if (string == null) {
			return null;
		}
		// Using deprecated fromHtml() overload, since fromHtml(string, int) is only supported from api level 17
		// noinspection deprecation
		return Html.fromHtml(string).toString();
	}

	@NonNull
	private List<String> convertValuesToStringList(JsonObject jsonObject) {
		List<String> stringList = new LinkedList<>();
		for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
			JsonElement jsonStringElement = entry.getValue();
			if (jsonStringElement != null && jsonStringElement.isJsonPrimitive() && !TextUtils.isEmpty(jsonStringElement.getAsString())) {
				stringList.add(decodeHtml(jsonStringElement.getAsString()));
			}
		}
		return stringList;
	}

	public static Date parseEventTime(String sffFormat) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:SS", Dates.getLocale());
		try {
			return dateFormat.parse(sffFormat);
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
	}

	private List<String> getSpeakers(JsonArray speakers) {
		LinkedList<String> result = new LinkedList<>();
		for (JsonElement speaker : speakers) {
			result.add(decodeHtml(speaker.getAsString()));
		}
		return result;
	}

	private String parseEventDescription(String rawEventDescription) {
		if (rawEventDescription == null) {
			return "";
		}

		return rawEventDescription
				// Remove class, style, height and width attributes in tags since they make the element take
				// up more space than needed and are not supported anyway
				.replaceAll("class=\"[^\"]*\"", "")
				.replaceAll("style=\"[^\"]*\"", "")
				.replaceAll("width=\"[^\"]*\"", "")
				.replaceAll("height=\"[^\"]*\"", "")
						// Replace divs and images with some other unsupported (and therefore ignored)
				.replace("<div", "<xdiv")
				.replace("/div>", "/xdiv>")
						// Remove tabs because they are not treated as whitespace and mess up the formatting
				.replace("\t", "	");
	}
}

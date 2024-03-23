package amai.org.conventions.networking;

import android.text.Html;
import android.text.TextUtils;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import amai.org.conventions.model.ConventionEvent;
import amai.org.conventions.model.EventType;
import amai.org.conventions.model.Hall;
import amai.org.conventions.model.conventions.Convention;
import amai.org.conventions.utils.Dates;
import amai.org.conventions.utils.Log;
import androidx.annotation.NonNull;

public class SffModelParser implements ModelParser {
	private static final String TAG = SffModelParser.class.getCanonicalName();

	@Override
	public List<ConventionEvent> parse(Date modifiedDate, InputStreamReader reader) {
		JsonElement root = JsonParser.parseReader(reader);
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

			// Tags are returned as an array
			List<String> tags;
			JsonElement tagsElement = eventObj.get("tags");
			if (tagsElement.isJsonArray()) {
				tags = convertValuesToStringList(tagsElement.getAsJsonArray());
				tags = filterDuplicates(tags);
			} else {
				tags = Collections.emptyList();
				Log.w(TAG, "Tags list is not an array: " + tagsElement);
			}

			int price = getEventPrice(eventObj);

			int availableTickets;
			JsonElement availableTicketsElement = eventObj.get("available_tickets");
			if (availableTicketsElement == null || availableTicketsElement.isJsonNull()) {
				availableTickets = -1;
			} else {
				availableTickets = availableTicketsElement.getAsInt();
			}

			if (getBooleanValue(eventObj, "is_ticketless")) {
				availableTickets = -1;
			}

			String websiteUrl = eventObj.get("url").getAsString();

			List<ConventionEvent.EventLocationType> locationTypes = getLocationTypes(eventObj);

			String eventViewUrl = null;
			if (eventObj.has("virtual_url") && !eventObj.get("virtual_url").isJsonNull()) {
				eventViewUrl = eventObj.get("virtual_url").getAsString();
			}


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
					.withTags(tags)
					.withPrice(price)
					.withAvailableTickets(availableTickets)
					.withTicketsLimit(-1) // Tickets limit is not supported in the new COD API
					.withTicketsLastModifiedDate(modifiedDate)
					.withWebsiteUrl(websiteUrl)
					.withLocationTypes(locationTypes)
					.withEventViewUrl(eventViewUrl);

			eventList.add(conventionEvent);
		}

		return eventList;
	}

	private List<String> filterDuplicates(List<String> strings) {
		Set<String> existing = new HashSet<>();
		List<String> filtered = new LinkedList<>();
		for (String value: strings) {
			if (!existing.contains(value)) {
				existing.add(value);
				filtered.add(value);
			} else {
				Log.i(TAG, "found duplicate string: " + value);
			}
		}
		return filtered;
	}

	private boolean getBooleanValue(JsonObject object, String member) {
		return object.has(member) && !object.get(member).isJsonNull() && object.get(member).getAsBoolean();
	}

	private List<ConventionEvent.EventLocationType> getLocationTypes(JsonObject eventObj) {
		boolean hasVirtual = getBooleanValue(eventObj, "has_virtual");
		boolean hasPhysical = getBooleanValue(eventObj, "has_physical");

		if (hasPhysical && hasVirtual) {
			// Inhouse = streamed from the convention physical location
			boolean isInhouse = getBooleanValue(eventObj, "is_inhouse");
			if (isInhouse) {
				return Arrays.asList(ConventionEvent.EventLocationType.PHYSICAL, ConventionEvent.EventLocationType.VIRTUAL);
			} else {
				return Arrays.asList(ConventionEvent.EventLocationType.VIRTUAL, ConventionEvent.EventLocationType.PHYSICAL);
			}
		} else if (hasVirtual) {
			return Collections.singletonList(ConventionEvent.EventLocationType.VIRTUAL);
		} else {
			// This is also the default in case none of the properties is true
			return Collections.singletonList(ConventionEvent.EventLocationType.PHYSICAL);
		}
	}

	protected int getEventPrice(JsonObject eventObj) {
		int price;
		JsonElement priceElement = eventObj.get("price");
		if (priceElement.isJsonNull()) {
			price = -1;
		} else if (priceElement.getAsString().equals("חינם")) {
			price = 0;
		} else {
			price = priceElement.getAsInt();
		}
		return price;
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
	private List<String> convertValuesToStringList(JsonArray jsonArray) {
		List<String> stringList = new LinkedList<>();
		for (int i = 0; i < jsonArray.size(); ++i) {
			JsonElement jsonStringElement = jsonArray.get(i);
			if (jsonStringElement != null && jsonStringElement.isJsonPrimitive() && !TextUtils.isEmpty(jsonStringElement.getAsString())) {
				stringList.add(jsonStringElement.getAsString());
			}
		}
		return stringList;
	}

	public static Date parseEventTime(String sffFormat) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Dates.getLocale());
		try {
			return Dates.conventionToLocalTime(dateFormat.parse(sffFormat));
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

		// Handle events that have an image in base64 instead of description (bug in icon 2018)
		// This caused the event screen to freeze for a few minutes because of the size of the description
		// and the image was not displayed at the end.
		if (rawEventDescription.startsWith("data:image/png;base64,")) {
			return "";
		}

		return rawEventDescription
				// Remove class, style, height and width attributes in tags since they make the element take
				// up more space than needed and are not supported anyway
				.replaceAll("class=\"[^\"]*\"", "")
				.replaceAll("style=\"[^\"]*\"", "")
				.replaceAll("width=\"[^\"]*\"", "")
				.replaceAll("height=\"[^\"]*\"", "")
				.replaceAll("font\\s+color=\"[^\"]*\"", "font") // Remove font colors - they won't match the theme
				// Remove scripts (multi-line and lazy)
				.replaceAll("(?s)<script>.*?</script>", "")
				// Replace divs and images with some other unsupported (and therefore ignored)
				.replace("<div", "<xdiv")
				.replace("/div>", "/xdiv>")
				// Remove tabs because they are not treated as whitespace and mess up the formatting
				.replace("\t", "	");
	}
}

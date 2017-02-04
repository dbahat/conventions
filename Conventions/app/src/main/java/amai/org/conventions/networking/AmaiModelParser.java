package amai.org.conventions.networking;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.Html;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import amai.org.conventions.model.ConventionEvent;
import amai.org.conventions.model.EventType;
import amai.org.conventions.model.Hall;
import amai.org.conventions.model.conventions.Convention;
import amai.org.conventions.utils.Dates;
import amai.org.conventions.utils.Log;

public class AmaiModelParser implements ModelParser {
	public static final int NO_COLOR = Color.TRANSPARENT; // Assuming we will never get this from the server...

	private static final String TAG = AmaiModelParser.class.getCanonicalName();

	public List<ConventionEvent> parse(InputStreamReader reader) {
		Map<Integer, ParsedDescription> contentById = new HashMap<>();
		Map<ConventionEvent, Integer> eventsWithSpecialContent = new LinkedHashMap<>();

		JsonParser jp = new JsonParser();
		JsonElement root = jp.parse(reader);
		JsonArray events = root.getAsJsonArray();

		List<ConventionEvent> eventList = new LinkedList<>();

		for (JsonElement event : events) {
			JsonObject eventObj = event.getAsJsonObject();
			int eventId = eventObj.get("ID").getAsInt();
			String eventType = eventObj.get("categories-text").getAsJsonObject().get("name").getAsString();
			String title = eventObj.get("title").getAsString();

			// We don't use the description of events that are disabled or point to a different page
			boolean isSpecialEvent = eventObj.has("timetable-url-pid") && !"0".equals(eventObj.get("timetable-url-pid").getAsString());
			boolean ignoreEventDescription = isSpecialEvent || "1".equals(eventObj.get("timetable-disable-url").getAsString());
			ParsedDescription eventDescription = parseEventDescription(ignoreEventDescription ? null : eventObj.get("content").getAsString());

			// Check if this event points to a different page (special event)
			int specialEventId = 0;
			contentById.put(eventId, eventDescription);
			if (isSpecialEvent) {
				specialEventId = eventObj.get("timetable-url-pid").getAsInt();
			}

			int internalEventNumber = 1;

			for (JsonElement internalEvent : eventObj.get("timetable-info").getAsJsonArray()) {
				JsonObject internalEventObj = internalEvent.getAsJsonObject();

				// Ignore hidden events
				if ("hidden".equals(internalEventObj.get("tooltip").getAsString())) {
					continue;
				}

				// These 2 properties will only exist if there is timetable info, even though they aren't actually inside the timetable info
				int bgColor = parseColorFromServer(eventObj.get("timetable-bg").getAsString());
				int textColor = parseColorFromServer(eventObj.get("timetable-text-color").getAsString());

				Date startTime = parseEventTime(internalEventObj.get("start").getAsString());
				Date endTime = parseEventTime(internalEventObj.get("end").getAsString());
				String hallName = internalEventObj.get("room").getAsString();
				Hall hall = Convention.getInstance().findHallByName(hallName);

				if (hall == null) {
					// Add a new hall to the convention
					hall = Convention.getInstance().addHall(hallName);
					Log.i(TAG, "Found and added new hall with name " + hallName);
				}

				ConventionEvent conventionEvent = new ConventionEvent()
						.withServerId(eventId)
						.withBackgroundColor(bgColor)
						.withTextColor(textColor)
						.withTitle(title)
						.withLecturer(internalEventObj.get("before_hour_text").getAsString())
						.withDescription(eventDescription.getDescription())
						.withType(new EventType(bgColor, eventType))
						.withStartTime(startTime)
						.withEndTime(endTime)
						.withHall(hall)
						.withImages(eventDescription.getEventImageIds())
						.withId(String.format(Dates.getLocale(), "%d_%d", eventId, internalEventNumber));

				// Some events might have special pages and are not retrieved from the API
				// exposed by the server. For these spacial cases, add special handing of placing hardcoded texts/images.
				boolean handled = Convention.getInstance().handleSpecialEvent(conventionEvent);

				if (specialEventId != 0 && !handled) {
					eventsWithSpecialContent.put(conventionEvent, specialEventId);
				}

				eventList.add(conventionEvent);
				internalEventNumber++;
			}
		}

		// Handle special events
		for (Map.Entry<ConventionEvent, Integer> eventEntry : eventsWithSpecialContent.entrySet()) {
			ParsedDescription parsedDescription = contentById.get(eventEntry.getValue());
			if (parsedDescription != null) {
				ConventionEvent event = eventEntry.getKey();
				event.setDescription(parsedDescription.getDescription());
				event.setImages(parsedDescription.getEventImageIds());
			}
		}

		return eventList;
	}

	public static Date parseEventTime(String date) {
		try {
			Date hourAndMinute = new SimpleDateFormat("HH:mm:ss", Dates.getLocale()).parse(date);
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(hourAndMinute);
			setConventionDate(calendar);
			return calendar.getTime();
		} catch (ParseException e) {
			return new Date();
		}
	}

	private static void setConventionDate(Calendar calendar) {
		Calendar conventionDate = Convention.getInstance().getStartDate();
		calendar.set(conventionDate.get(Calendar.YEAR),
				conventionDate.get(Calendar.MONTH),
				conventionDate.get(Calendar.DAY_OF_MONTH));
	}

	private int parseColorFromServer(String serverColor) {

		int color = NO_COLOR;
		if (serverColor != null && !serverColor.isEmpty()) {
			try {
				if (!serverColor.startsWith("#")) {
					serverColor = "#" + serverColor;
				}
				color = Color.parseColor(serverColor);
			} catch (IllegalArgumentException e) {
				Log.e(TAG, "Color from server cannot be parsed: " + serverColor);
			}
		}
		return color;
	}

	private ParsedDescription parseEventDescription(String rawEventDescription) {
		if (rawEventDescription == null) {
			return new ParsedDescription();
		}

		String eventDescription = rawEventDescription
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
				.replace("\t", "    ");

		// Collect the images from the html. This must be done separately because we don't want to actually
		// include the images in the output.
		final List<String> eventIds = new LinkedList<>();
		Html.fromHtml(eventDescription, new Html.ImageGetter() {
			@Override
			public Drawable getDrawable(String source) {
				eventIds.add(source);
				return null;
			}
		}, null);

		// Replace img tags and remove src attribute for the reasons stated above
		String filteredDescription = eventDescription
				.replaceAll("src=\"[^\"]*\"", "")
				.replace("<img", "<ximg")
				.replace("/img>", "/ximg>");

		return new ParsedDescription()
				.withDescription(filteredDescription)
				.withEventImageIds(eventIds);
	}

	private class ParsedDescription {
		private String description = "";
		private List<String> eventImageIds = new ArrayList<>();

		public String getDescription() {
			return description;
		}

		public List<String> getEventImageIds() {
			return eventImageIds;
		}

		public void setDescription(String description) {
			this.description = description;
		}

		public void setEventImageIds(List<String> eventImageIds) {
			this.eventImageIds = eventImageIds;
		}

		public ParsedDescription withDescription(String description) {
			setDescription(description);
			return this;
		}

		public ParsedDescription withEventImageIds(List<String> eventIds) {
			setEventImageIds(eventIds);
			return this;
		}
	}
}

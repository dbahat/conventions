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
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import amai.org.conventions.model.ConventionEvent;
import amai.org.conventions.model.EventType;
import amai.org.conventions.model.Hall;
import amai.org.conventions.utils.Dates;

public class SffModelParser {

    public List<ConventionEvent> parse(InputStreamReader reader) {
        JsonParser jp = new JsonParser();
        JsonElement root = jp.parse(reader);
        JsonArray events = root.getAsJsonArray();

        List<ConventionEvent> eventList = new LinkedList<>();

        for (JsonElement event : events) {
            JsonObject eventObj = event.getAsJsonObject();
            int eventId = eventObj.get("id").getAsInt();
            String eventType = eventObj.get("track").getAsString();

            // Using deprecated fromHtml() overload, since fromHtml(string, int) is only supported from api level 17
            // noinspection deprecation
	        String title = Html.fromHtml(eventObj.get("title").getAsString()).toString();

            String eventDescription = parseEventDescription(eventObj.get("description").getAsString());

            JsonObject timeObject = eventObj.get("time").getAsJsonObject();
            Date startTime = parseEventTime(timeObject.get("start").getAsString());
            Date endTime = parseEventTime(timeObject.get("end").getAsString());
            JsonArray speakers = eventObj.get("speakers").getAsJsonArray();
            String allSpeakers = speakers.size() > 0 ? TextUtils.join(", ", getSpeakers(speakers)) : "";

            String hallName = eventObj.get("location").isJsonNull() ? "" : eventObj.get("location").getAsString();
            if (TextUtils.isEmpty(hallName)) {
                // Some SF-F events came up corrupted without hall name. Ignore them during parsing.
                continue;
            }
            // TODO - switch to using Convention.getInstance().findHallByName(hallName) if/when we add icon map support.
            Hall hall = new Hall().withName(hallName).withOrder(1);

            JsonArray categories = eventObj.get("categories").getAsJsonArray();
            String category = categories.size() > 0 ? TextUtils.join(", ", categories) : "";

            ConventionEvent conventionEvent = new ConventionEvent()
                    .withServerId(eventId)
                    .withTitle(title)
                    .withLecturer(allSpeakers)
                    .withType(new EventType(0, eventType))
                    .withDescription(eventDescription)
                    .withStartTime(startTime)
                    .withEndTime(endTime)
                    .withHall(hall)
                    .withId(String.valueOf(eventId))
                    .withCategory(category);

            eventList.add(conventionEvent);
        }

        return eventList;
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
            // Using deprecated fromHtml() overload, since fromHtml(string, int) is only supported from api level 17
            // noinspection deprecation
            result.add(Html.fromHtml(speaker.getAsString()).toString());
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
                .replace("\t", "    ");
    }
}

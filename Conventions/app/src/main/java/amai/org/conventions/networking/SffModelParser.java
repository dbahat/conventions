package amai.org.conventions.networking;

import android.text.Html;
import android.text.TextUtils;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.InputStreamReader;
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
            Date startTime = Dates.parseSffFormat(timeObject.get("start").getAsString());
            Date endTime = Dates.parseSffFormat(timeObject.get("end").getAsString());
            JsonArray speakers = eventObj.get("speakers").getAsJsonArray();
            String firstSpeaker = speakers.size() > 0 ? TextUtils.join(", ", getSpeakers(speakers)) : "";

            String hallName = eventObj.get("location").isJsonNull() ? "" : eventObj.get("location").getAsString();
            // TODO - switch to using Convention.getInstance().findHallByName(hallName) if/when we add icon map support.
            Hall hall = new Hall().withName(hallName).withOrder(1);

            ConventionEvent conventionEvent = new ConventionEvent()
                    .withServerId(eventId)
                    .withTitle(title)
                    .withLecturer(firstSpeaker)
                    .withType(new EventType(0, eventType))
                    .withDescription(eventDescription)
                    .withStartTime(startTime)
                    .withEndTime(endTime)
                    .withHall(hall)
                    .withId(String.valueOf(eventId));

            eventList.add(conventionEvent);
        }


        return eventList;
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

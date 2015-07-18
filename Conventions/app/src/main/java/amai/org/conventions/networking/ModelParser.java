package amai.org.conventions.networking;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.InputStreamReader;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import amai.org.conventions.model.Convention;
import amai.org.conventions.model.ConventionEvent;
import amai.org.conventions.utils.Dates;
import amai.org.conventions.model.EventToImageResourceIdMapper;
import amai.org.conventions.model.EventType;
import amai.org.conventions.model.Hall;

public class ModelParser {
    public List<ConventionEvent> parse(InputStreamReader reader) {
        JsonParser jp = new JsonParser();
        EventToImageResourceIdMapper mapper = new EventToImageResourceIdMapper();

        JsonElement root = jp.parse(reader);
        JsonArray events = root.getAsJsonArray();

        List<ConventionEvent> eventList = new LinkedList<>();

        for (JsonElement event : events) {
            JsonObject eventObj = event.getAsJsonObject();
            int eventId = eventObj.get("ID").getAsInt();
            int eventTypeId = eventObj.get("categories-text").getAsJsonObject().get("id").getAsInt();

            int internalEventNumber = 1;

            for (JsonElement internalEvent : eventObj.get("timetable-info").getAsJsonArray()) {
                JsonObject internalEventObj = internalEvent.getAsJsonObject();

                Date startTime = Dates.parseHourAndMinute(internalEventObj.get("start").getAsString());
                Date endTime = Dates.parseHourAndMinute(internalEventObj.get("end").getAsString());
                String hallName = internalEventObj.get("room").getAsString();
                Hall hall = Convention.getInstance().findHallByName(hallName);

                if (hall == null) {
                    throw new RuntimeException("Cannot find hall with name " + hallName);
                }

                ConventionEvent conventionEvent = new ConventionEvent()
                        .withServerId(eventId)
		                .withColorFromServer(eventObj.get("timetable-bg").getAsString())
                        .withTitle(eventObj.get("title").getAsString())
                        .withLecturer(eventObj.get("sub-title").getAsString())
                        .withDescription(eventObj.get("content").getAsString())
                        .withType(EventType.parse(eventTypeId))
                        .withStartTime(startTime)
                        .withEndTime(endTime)
                        .withHall(hall)
		                .withImages(mapper.getImageResourceIds(eventId))
		                .withId(String.format("%d_%d", eventId, internalEventNumber));

                eventList.add(conventionEvent);
                internalEventNumber++;
            }
        }

        return eventList;
    }
}

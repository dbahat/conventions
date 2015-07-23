package amai.org.conventions.networking;

import android.graphics.drawable.Drawable;
import android.text.Html;

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

                ParsedDescription eventDescription = parseEventDescription(eventObj.get("content").getAsString());

                ConventionEvent conventionEvent = new ConventionEvent()
                        .withServerId(eventId)
		                .withColorFromServer(eventObj.get("timetable-bg").getAsString())
                        .withTitle(eventObj.get("title").getAsString())
                        .withLecturer(internalEventObj.get("before_hour_text").getAsString())
                        .withDescription(eventDescription.getDescription())
                        .withType(EventType.parse(eventTypeId))
                        .withStartTime(startTime)
                        .withEndTime(endTime)
                        .withHall(hall)
		                .withImages(mapper.getImageResourceIds(eventDescription.getEventIds()))
		                .withId(String.format("%d_%d", eventId, internalEventNumber));

                eventList.add(conventionEvent);
                internalEventNumber++;
            }
        }

        return eventList;
    }

    private ParsedDescription parseEventDescription(String rawEventDescription) {
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
                .withEventIds(eventIds);
    }

    private class ParsedDescription {
        private String description;
        private List<String> eventIds;

        public String getDescription() {
            return description;
        }

        public List<String> getEventIds() {
            return eventIds;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public void setEventIds(List<String> eventIds) {
            this.eventIds = eventIds;
        }

        public ParsedDescription withDescription(String description) {
            setDescription(description);
            return this;
        }

        public ParsedDescription withEventIds(List<String> eventIds) {
            setEventIds(eventIds);
            return this;
        }
    }
}

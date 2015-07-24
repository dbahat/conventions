package amai.org.conventions.networking;

import android.graphics.drawable.Drawable;
import android.text.Html;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import amai.org.conventions.R;
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

                // Some events (like the guest of honor event and the games event) have special pages and are not retrieved from the API
                // exposed by the server. For these spacial cases, add special handing of placing hardcoded texts/images.
                if (conventionEvent.getType() == EventType.GuestOfHonor && conventionEvent.getDescription().length() == 0) {
                    conventionEvent = handleGuestOfHonorEvent(conventionEvent);
                }
                if (conventionEvent.getType() == EventType.Games && conventionEvent.getDescription().length() == 0) {
                    conventionEvent = handleGamesEvent(conventionEvent);
                }

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

    private final List<Integer> guestOfHonorImageResourceIds = Arrays.asList( R.drawable.event_reika1,  R.drawable.event_reika2,  R.drawable.event_reika3);
    private final String guestOfHonorDescription = "<p>השנה, אנחנו שמחים לארח אורחת מיוחדת – רייקה, קוספליירית ידועה שמגיעה אלינו מאוסאקה שביפן.</p>\n" +
            "<p>רייקה היא קוספליירית יפנית מוכרת, ויש לה כ300 אלף עוקבים <a href=\"https://www.facebook.com/profile.php?id=315573555144954&amp;fref=ts\" target=\"_blank\">בעמוד הפייסבוק שלה</a>.</p>\n" +
            "<p>היא ידועה בעיקר בזכות הקרוספליי (קוספליי שעושה אדם ממין אחד לדמות מהמין השני) שלה, התחפושות המפורטות ומלאות הפרטים ועבודת האיפור המדוייקת.</p>\n" +
            "<p>רייקה התחילה להכין קוספליי לראשונה לפני כ18 שנים, ומאז היא לא מפסיקה – ומוזמנת לאינספור כנסים ואירועים מסביב לעולם, ולראשונה גם אלינו לישראל.</p>\n" +
            "<p>רייקה תקח עמנו השנה חלק בכנס הקרוב במספר פעילויות שיוכרזו בהמשך.</p>";

    private ConventionEvent handleGuestOfHonorEvent(ConventionEvent event) {
        return event
                .withDescription(guestOfHonorDescription)
                .withImages(guestOfHonorImageResourceIds);
    }

    private final List<Integer> gamesImageResources = Arrays.asList( R.drawable.event_pokemon );
    private final String gamesEventDescription = "<p><b>טורניר TCG – משחק הקלפים של פוקימון</b></p>\n" +
            "<p>משחק הקלפים של פוקימון הוא משחק שולחני הידוע בשם TCG. הוא משלב בתוכו אספנות, החלפה ומשחק בקלפים בנושא פוקימון. יש למשחק הזה חוקים משלו, ואלה מבוססים על משחק הוידאו של פוקימון. המשחק הוא מרתק, מהנה ומלא אסטרטגיה. בעת משחק, כל שחקן אוחז ב-60 קלפים, המורכבים מקלפי פוקימון, קלפי מאמן וקלפי אנרגיה, ובאמצעותם עליו לנצח את היריב. דרך הנצחון נקבעת לפי אחת משלושת הדרכים הבאות:</p>\n" +
            "<ol>\n" +
            "<li>כאשר שחקן הצליח לאסוף את כל קלפי הפרס שלו.</li>\n" +
            "<li>כאשר לאחד מהשחקנים אין יותר פוקימונים פעילים בספסל שלו.</li>\n" +
            "<li>כאשר אחד מהשחקנים לא יכול לשלוף יותר קלפים מהחבילה שלו.</li>\n" +
            "</ol>\n" +
            "<p>הפופולריות של המשחק החלה עוד בשנת יציאתו לאור (1996) והיא חזקה כיום יותר מתמיד. לראייה, מומחים מעריכים שנמכרו עד היום כ-15 מיליארד קלפים בסך הכל.</p>\n" +
            "<p></p>\n" +
            "<p>טורניר הקלפים יערך לפי החוקים הבאים:</p>\n" +
            "<ul>\n" +
            "<li>חוקי נינטנדו המעודכנים.</li>\n" +
            "<li>לפי רשימת המודיפייד העדכנית.</li>\n" +
            "<li>טורניר בסגנון סוויס (כולם משחקים מספר שווה של קרבות).</li>\n" +
            "<li>על כל הקלפים להיות אותנטיים (יש איסור מוחלט להדפיס קלפים).</li>\n" +
            "<li>על השחקנים להביא את החפיסות מוכנות מהבית.</li>\n" +
            "</ul>\n" +
            "<p>&nbsp;</p>\n" +
            "<p><b>הטורניר בהנחיית יוסי אוחנה</b><b>:</b></p>\n" +
            "<p>יוסי, בן 31, ידוע ברשת בתור \"היטמן\". מנהל את אתר \"פוקימון: מפלצות כיס\". בוגר תואר ראשון בתקשורת ותרבות מטעם \"מכללת ספיר\", וסטודנט להוראה במכללת \"דוד ילין\". פועל על מנת לקדם את נושא פוקימון בארץ. לקח חלק פעיל בכנסי אמא\"י האחרונים והעביר הרצאות, כגון: \"נוסטלגיה בעולם הפוקימון\" ו\"פוקימון – האם הם באו מן החלל?\"</p>";

    private ConventionEvent handleGamesEvent(ConventionEvent event) {
        return event
                .withDescription(gamesEventDescription)
                .withImages(gamesImageResources);
    }
}

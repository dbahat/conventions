package amai.org.conventions.networking;

import android.graphics.drawable.Drawable;
import android.text.Html;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import amai.org.conventions.R;
import amai.org.conventions.model.Convention;
import amai.org.conventions.model.ConventionEvent;
import amai.org.conventions.model.EventToImageResourceIdMapper;
import amai.org.conventions.model.EventType;
import amai.org.conventions.model.Hall;
import amai.org.conventions.utils.Dates;
import amai.org.conventions.utils.Log;

public class ModelParser {
	private static final String TAG = ModelParser.class.getCanonicalName();

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

	            // Ignore hidden events
	            if ("hidden".equals(internalEventObj.get("tooltip").getAsString())) {
		            continue;
	            }

                Date startTime = Dates.parseHourAndMinute(internalEventObj.get("start").getAsString());
                Date endTime = Dates.parseHourAndMinute(internalEventObj.get("end").getAsString());
                String hallName = internalEventObj.get("room").getAsString();
                Hall hall = Convention.getInstance().findHallByName(hallName);

                if (hall == null) {
	                // Add a new hall to the convention
	                hall = Convention.getInstance().addHall(hallName);
                    Log.i(TAG, "Found and added new hall with name " + hallName);
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
	            if (conventionEvent.getDescription().length() == 0) {
	                if (conventionEvent.getType() == EventType.GuestOfHonor) {
	                    conventionEvent = handleGuestOfHonorEvent(conventionEvent);
	                }
	                if (conventionEvent.getType() == EventType.Games) {
	                    conventionEvent = handleGamesEvent(conventionEvent);
	                }
		            if (conventionEvent.getType() == EventType.Screening) {
			            // There's no other way to tell the difference between these events :(
			            if (conventionEvent.getServerId() == 1825) {
				            // Pandora
				            conventionEvent = handlePandoraEvent(conventionEvent);
			            } else if (conventionEvent.getServerId() == 1824) {
				            // Colorido movies
				            conventionEvent = handleStudioColoridMoviesEvent(conventionEvent);
			            }
		            }
	            }

                // In case some events came up without any images at all, add a generic image to them.
                if (conventionEvent.getImages().size() == 0) {
                    conventionEvent.setImages(Collections.singletonList(R.drawable.event_generic));
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
    private static final String guestOfHonorDescription = "<p>השנה, אנחנו שמחים לארח אורחת מיוחדת – רייקה, קוספליירית ידועה שמגיעה אלינו מאוסאקה שביפן.</p>\n" +
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
    private static final String gamesEventDescription = "<p><b>טורניר TCG – משחק הקלפים של פוקימון</b></p>\n" +
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

	private final List<Integer> coloridoMoviesImageResources = Arrays.asList(R.drawable.event_colorido_typhoon, R.drawable.event_colorido_sun);
	private final static String coloridoMoviesEventDescription = "<p><span style=\"color: #ffffff;\">סטודיו קולורידו הוא סטודיו קטן אשר ידוע בפירסומות האנימציה והסרטים הקצרים העליזים והצבעוניים אותו הפיק. ברצוננו להודות לסטודיו קולורידו (Studio Colorido) על אישור הקרנת שניים מסרטיו לקהל המבקרים של כאמ&quot;י 2015.</span></p>\n" +
			"<h3>טייפון נורודה &#8211; Taifuu no Noruda</h3>\n" +
			"<p><strong>שנה:</strong> 2015 <strong>אורך:</strong> 27 דקות <strong>במאי:</strong> יוג'ירו אראי <strong>אנימציה:</strong> סטודיו Colorido.</p>\n" +
			"<p>עלילת הסרט מתרחשת על אי מבודד כל שהוא, בחטיבת ביניים מסויימת, בערב לפני פסטיבל התרבות. נער פורש ממשחק הבייסבול אחרי ששיחק כל חייו ורב עם חברו הטוב ביותר כאשר לפתע הם פוגשים בבחורה מסתורית ואדומת עיניים בשם נורודה, וטייפון עוצמתי פוגע בחטיבת הביניים שלהם.</p>\n" +
			"<p><strong>אודות הבמאי:</strong><br />\n" +
			"ליוג'ירו אראי (Yojiro Arai), זוהי עבודתו הראשונה כבמאי. עד כה שימש כאנימטור בסטודיו ג'יבלי וכבמאי אנימציה בסטודיו קולורידו (בין עבודותיו בסטודיו גם הסרט &quot;ילד השמש וילדת הטל&quot;).</p>" +
			"<h3>ילד השמש וילדת הטל &#8211; Sunny Boy &amp; Dewdrop Girl</h3>\n" +
			"<p><strong>שנה:</strong> 2013 <strong>אורך:</strong> 18 דקות <strong>במאי:</strong> אישידה הירויאסו <strong>אנימציה:</strong> סטודיו Colorido.</p>\n" +
			"<p>הינאטה מתאהב בחברתו לספסל הלימודים, שיגארו. אבל בעוד שהינאטה ממש טוב בלצייר, הוא ממש לא טוב בלדבר עם אנשים, אז הוא שומר על רגשותיו כלפיה בציוריו ובדמיונותיו. כשמשפחתה של שיגארו מחליטים לעבור לעיר אחרת, הינאטה מחליט ביום המעבר שלהם שהוא מוכרח להתוודות על רגשותיו כלפיה ויוצא למרדף אחר הרכבת שלוקחת את שיגארו.</p>\n" +
			"<p><strong>אודות הבמאי:</strong></p>\n" +
			"<p>אישידה הירויאסו (Ishida Hiroyasu) הינו בוגר אוניבריסטת קיוטו סייקה. הירויאסו מפיק סרטים קצרים בצורה עצמאית ומפרסם אותם באינטרט תחת השם Tete. ב-2009 הסרט שלו &quot;ההתוודאות של פומיקו&quot; (Fumiko no Kokuhaku), שאורכו 2 וחצי דקות, זכה לתשומת לב בינלאומית ברחבי הרשת. עבור סרט זה זכה הירויאסו בפרס הוידאו של היוטיוב היפני. עבודותיו זכו לשבחים בין היתר על ידי הבמאי מאמורו הוסודה (Mamoru Hosuda) והן הוצגו במגזין &quot;Newtype&quot;.</p>\n" +
			"<p><a href=\"https://www.youtube.com/user/ishidahiroyasu/feed\" target=\"_blank\">לערוץ היוטוב שלו</a>.</p>";

	private ConventionEvent handleStudioColoridMoviesEvent(ConventionEvent event) {
		return event
				.withDescription(coloridoMoviesEventDescription)
				.withImages(coloridoMoviesImageResources);
	}

	private final List<Integer> pandoraImageResources = Arrays.asList(R.drawable.event_pandora);
	private final static String pandoraEventDescription = "<p><b>תקציר:</b></p>\n" +
			"<p>תארו לעצמכם עולם אנושי, קר ואכזרי, שכולו צבוע בשחור ולבן, בו חלים חוקים מחמירים ועונשים כבדים על כל מי שלא עומד בתקנות. יוקי טאנמי היא נערה רגילה בת 16 אשר לומדת בפנימיית ברקת, ועושה כל שביכולתה כדי לעמוד בציפיות ולשרוד במציאות האפרורית והמדכאת אך בסתר חולמת על מקום טוב יותר.</p>\n" +
			"<p>תארו לעצמכם ממלכה קסומה, צבעונית ומגוונת בה חיים יצורי קסם מופלאים, אשר נשלטת ביד קשה על ידיי מלך רודן. אניה דה לה פנתגרם השנייה היא הנסיכה בעולם זה, וחולמת על היום בו תהפוך למלכת פנדורה.</p>\n" +
			"<p>כל אחת חיה בעולם אחר אך שתיהן חולקות גורל משותף. שני העולמות מקבילים ומופרדים על ידי שער בצורת תיבה נעולה החוסמת את המעבר בין העולמות. אך מה יקרה כשהתיבה תפתח? אל תפספסו את ההזדמנות לראות את הדמויות מתאהבות, מתאכזבות ונלחמות על חייהן בפנדורה מיוזיקל!</p>" +
			"<h3>צוות הפקה:</h3>\n" +
			"<p><strong>במאית ויוצרת</strong> &#8211; נעה ירון.<br />\n" +
			"<strong>מפיקה ויוצרת</strong> &#8211; שקד שפירא.<br />\n" +
			"<strong>במאי שותף</strong> &#8211; רן בורשטיין.<br />\n" +
			"<strong>יועצת אומנותית</strong> &#8211; הירו גרייסון.<br />\n" +
			"<strong>אחראית קוספליי</strong> &#8211; ים בן דוד.<br />\n" +
			"<strong>צוות קוספליי</strong> &#8211; שחר אגרנט, עינב לוי, אופיר לוטן, ים בן דוד, דריה ברט, עומר רהב, אריאל באום.   <strong> אחראי מוסיקה</strong> &#8211; אביב ייני אשל.<br />\n" +
			"<strong>צוות מוסיקה</strong> &#8211; סלע ווינרוב, דניאל מרגלית, שי קדוש.<br />\n" +
			"<strong>צוות תפאורה</strong> &#8211; ענבר גוטליב, אלון צנג.<br />\n" +
			"<strong>אחראית אביזרים</strong> &#8211; אודיה ליבוביץ'.<br />\n" +
			"<strong>צילום הסרט</strong> &#8211; יניב גדי, אורן רוסטרט ויובל ירון.<br />\n" +
			"<strong>עריכת הסרט</strong> &#8211; נועה ירון.</p>" +
			"<h3>שחקנים:</h3>\n" +
			"<p><strong>יוקי</strong> &#8211; שיאל פלד זקס.<br />\n" +
			"<strong>ליאו </strong>&#8211; עומר אופיר.<br />\n" +
			"<strong>מקס </strong>&#8211; איתי רייכנטל.<br />\n" +
			"<strong>טנשי</strong> &#8211; עומר רהב בן יעקב.<br />\n" +
			"<strong>קארה</strong> &#8211; אריאל באום.<br />\n" +
			"<strong>האחיות</strong> &#8211; אנאל בר, שקד שפירא, שירה כסלו.<br />\n" +
			"<strong>קלוד</strong> &#8211; יותם שמיר.<br />\n" +
			"<strong>אניה</strong> &#8211; אופיר לוטן.<br />\n" +
			"<strong>נשות כפר</strong> &#8211; אסיא גרינברג, הדר שגיא, נעה ירון.<br />\n" +
			"<strong>רקדניות</strong> &#8211; נועם אריאלי, ליאור נגר, אילי מלכי, אביה מלכי, תמר גולדליסט, אסיא גרינברג, גילי רן, בוריה ארפורט.</p>";

	private ConventionEvent handlePandoraEvent(ConventionEvent event) {
		return event
				.withDescription(pandoraEventDescription)
				.withImages(pandoraImageResources);
	}

}

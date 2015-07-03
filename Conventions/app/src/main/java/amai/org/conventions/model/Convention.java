package amai.org.conventions.model;

import android.content.Context;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import amai.org.conventions.R;

public class Convention implements Serializable {
    private static final String EVENT_USER_INPUT_FILE_NAME = "convention_data_user_input";
    private static final String UPDATES_FILE_NAME = "convention_updates";
    private static SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm", Dates.getLocale());
    private static Convention convention = new Convention();
    private static Context context;

    private List<ConventionEvent> events;
    private List<Hall> halls;
    private List<Update> updates;

    private ConventionMap map;

    public static Convention getInstance() {
        return convention;
    }

    private Convention() {
        Hall auditorium = new Hall().withName("אודיטוריום אושיסקין").withOrder(1);
        Hall contentRoom = new Hall().withName("חדר אירועי תוכן").withOrder(2);
        Hall oranim1 = new Hall().withName("אורנים 1").withOrder(3);
        Hall oranim2 = new Hall().withName("אורנים 2").withOrder(4);
        Hall oranim3 = new Hall().withName("אורנים 3").withOrder(5);

        this.halls = Arrays.asList(auditorium, contentRoom, oranim1, oranim2, oranim3);

        this.events = CollectionUtils.flattenList(
                inHall(auditorium,

		                new ConventionEvent(1)
				                .withTitle("אירוע פתיחה")
				                .withStartTime(time("10:30"))
				                .withEndTime(time("12:00"))
				                .withType(EventType.Central)
				                .withImages()
				                .withAttending(false),

		                new ConventionEvent(2)
				                .withTitle("שתי טיפות של דם")
				                .withLecturer("R2")
				                .withStartTime(time("12:30"))
				                .withEndTime(time("14:30"))
				                .withType(EventType.Central)
				                .withImages(R.drawable.event_ntt)
				                .withAttending(false),

		                new ConventionEvent(3)
				                .withTitle("הכנה לאירוע קוספליי")
				                .withStartTime(time("15:00"))
				                .withEndTime(time("16:00"))
				                .withType(EventType.Central)
				                .withImages(R.drawable.event_cosplay1)
				                .withAttending(false),

		                new ConventionEvent(4)
				                .withTitle("אירוע הקוספליי")
				                .withStartTime(time("16:00"))
				                .withEndTime(time("19:00"))
				                .withType(EventType.Central)
				                .withImages(R.drawable.event_cosplay1, R.drawable.event_cosplay2, R.drawable.event_cosplay3)
				                .withAttending(false),

		                new ConventionEvent(5)
				                .withTitle("שתי טיפות של דם")
				                .withLecturer("R2")
				                .withStartTime(time("19:30"))
				                .withEndTime(time("21:00"))
				                .withType(EventType.Central)
				                .withImages(R.drawable.event_ntt)
				                .withAttending(false)
                ),

                inHall(contentRoom,

		                new ConventionEvent(6)
				                .withTitle("כיצד הקהילה עיצבה אותי?")
				                .withLecturer("אלה ברוך")
				                .withStartTime(time("11:00"))
				                .withEndTime(time("12:00"))
				                .withType(EventType.Panel)
				                .withAttending(false)
				                .withDescription(
						                "<p><strong>תקציר:</strong></p>\n" +
								                "<p>דיון פתוח העוסק בסוגיות חברתיות הקיימות בתוך קהילת האנימה והמנגה בישראל.</p>\n" +
								                "<p><b>הפאנל בהשתתפות:</b></p>\n" +
								                "<p>אלה ברוך, בת 25, בקהילה למעלה מ-10 שנים, בעברה השתייכה לקהילות פורומים שונות, כיום קוספליירית זה 5 שנים.</p>\n" +
								                "<p>דני פייגלמן, בן 25, נחשף לקהילה בעת שירותו הצבאי, מהכנס הראשון שלו הוא מתנדב פעיל ובעל מעמד אחראי.</p>\n" +
								                "<p>תמר קלינמן, בת 17, נחשפה לעולם האנימה דרך אחיה הגדולים, שייכת לקבוצת אוטאקואים בווטסאפ.</p>")
				                .withImages(R.drawable.event_reika3),

		                new ConventionEvent(7)
				                .withTitle("המדריך לקוספלייר המתחיל")
				                .withLecturer("ענן גיבסון")
				                .withStartTime(time("12:00"))
				                .withEndTime(time("13:00"))
				                .withType(EventType.Panel)
				                .withImages(R.drawable.event_reika2)
				                .withAttending(false),

		                new ConventionEvent(8)
				                .withTitle("כל מה שרציתם לדעת על אנימציה!")
				                .withLecturer("סם דניאל")
				                .withStartTime(time("13:00"))
				                .withEndTime(time("14:00"))
				                .withType(EventType.Panel)
				                .withImages(R.drawable.event_political_manga)
				                .withAttending(false),

		                new ConventionEvent(9)
				                .withTitle("לאב לייב: אז מה הסיפור")
				                .withLecturer("שרון טורנר")
				                .withStartTime(time("14:00"))
				                .withEndTime(time("15:00"))
				                .withType(EventType.Panel)
				                .withImages(R.drawable.event_idols)
				                .withAttending(false),

		                new ConventionEvent(10)
				                .withTitle("בועת מחשבה: מגזינים, תוכן וקהילה")
				                .withLecturer("יבגני קנטור")
				                .withStartTime(time("15:00"))
				                .withEndTime(time("16:00"))
				                .withType(EventType.Panel)
				                .withImages(R.drawable.event_yaoi)
				                .withAttending(false),

		                new ConventionEvent(11)
				                .withTitle("המדריך לכותב השונן המתחיל")
				                .withLecturer("אמנון לוי")
				                .withStartTime(time("16:00"))
				                .withEndTime(time("17:00"))
				                .withType(EventType.Lecture)
				                .withImages(R.drawable.event_school)
				                .withAttending(false),

		                new ConventionEvent(12)
				                .withTitle("שוגי: כשהצריח מתפתח לדרקון - ואין פה נסיכה")
				                .withLecturer("אופר עזתי")
				                .withStartTime(time("17:00"))
				                .withEndTime(time("18:00"))
				                .withType(EventType.Lecture)
				                .withImages()
				                .withAttending(false),

		                new ConventionEvent(13)
				                .withTitle("אל עולם המנגה המחתרתי")
				                .withLecturer("דני פייגלמן")
				                .withStartTime(time("19:00"))
				                .withEndTime(time("20:00"))
				                .withType(EventType.Lecture)
				                .withImages(R.drawable.event_yaoi)
				                .withAttending(false)
                ),

                inHall(oranim1,

		                new ConventionEvent(14)
				                .withTitle("קוספליי בחלוף הזמן")
				                .withLecturer("עומרי גולד, נמרוד גולד, יעל גלר")
				                .withStartTime(time("11:00"))
				                .withEndTime(time("12:00"))
				                .withType(EventType.Lecture)
				                .withImages(R.drawable.event_reika1, R.drawable.event_reika2, R.drawable.event_reika3)
				                .withAttending(false),

		                new ConventionEvent(15)
				                .withTitle("10 דברים שצריך לדעת לפני הטיול ליפן")
				                .withLecturer("מור אורן")
				                .withStartTime(time("12:00"))
				                .withEndTime(time("13:00"))
				                .withType(EventType.Lecture)
				                .withImages(R.drawable.event_food)
				                .withAttending(false),

		                new ConventionEvent(16)
				                .withTitle("בין ורדים לרובוטים: האנימה של איקוהארה, אנו ומיזאקי")
				                .withLecturer("לירון אפריאט")
				                .withStartTime(time("13:00"))
				                .withEndTime(time("14:00"))
				                .withType(EventType.Lecture)
				                .withImages(R.drawable.event_visual_novels)
				                .withAttending(false),

		                new ConventionEvent(17)
				                .withTitle("חומרים טרמופלסטיים 101")
				                .withLecturer("קרן לין")
				                .withStartTime(time("14:00"))
				                .withEndTime(time("15:00"))
				                .withType(EventType.Lecture)
				                .withImages(R.drawable.event_reika3)
				                .withAttending(false),

		                new ConventionEvent(18)
				                .withTitle("עיצוב דמויות בטוקוסאטסו")
				                .withLecturer("ליעד בר שלטון")
				                .withStartTime(time("15:00"))
				                .withEndTime(time("16:00"))
				                .withType(EventType.Lecture)
				                .withImages(R.drawable.event_reika2)
				                .withAttending(false),

		                new ConventionEvent(19)
				                .withTitle("מעוצמה קשה לרכה - מאימפריאליזם צבאי למשיכה תרבותית")
				                .withLecturer("שירן איבניצקי")
				                .withStartTime(time("16:00"))
				                .withEndTime(time("17:00"))
				                .withType(EventType.Lecture)
				                .withImages(R.drawable.event_history)
				                .withAttending(false),

		                new ConventionEvent(20)
				                .withTitle("כשרובוטים מסמיקים - אנדרואידים ומגדר במנגה ואנימה")
				                .withLecturer("עומר כהן")
				                .withStartTime(time("17:00"))
				                .withEndTime(time("18:00"))
				                .withType(EventType.Lecture)
				                .withImages(R.drawable.event_yaoi)
				                .withAttending(false),

		                new ConventionEvent(21)
				                .withTitle("פוקימון - האם הם באו מהחלל?")
				                .withLecturer("יוסי אוחנה")
				                .withStartTime(time("18:00"))
				                .withEndTime(time("19:00"))
				                .withType(EventType.Lecture)
				                .withImages(R.drawable.event_visual_novels)
				                .withAttending(false),

		                new ConventionEvent(22)
				                .withTitle("על Plamo ו-Gunpla")
				                .withLecturer("עומר אמסלם ועומרי חפר")
				                .withStartTime(time("19:00"))
				                .withEndTime(time("20:00"))
				                .withType(EventType.Lecture)
				                .withImages()
				                .withAttending(false)
                ),

                inHall(oranim2,

		                new ConventionEvent(23)
				                .withTitle("החתולים בתרבות היפנית")
				                .withLecturer("עדן קליימן")
				                .withStartTime(time("11:00"))
				                .withEndTime(time("12:00"))
				                .withType(EventType.Lecture)
				                .withImages(R.drawable.event_school)
				                .withAttending(false),

		                new ConventionEvent(24)
				                .withTitle("מהרעיון אל המסך - הקסם של עולם האנימציה")
				                .withLecturer("סם דניאל")
				                .withStartTime(time("12:00"))
				                .withEndTime(time("13:00"))
				                .withType(EventType.Lecture)
				                .withImages(R.drawable.event_school)
				                .withAttending(false),

		                new ConventionEvent(25)
				                .withTitle("סקיטים ושאר ירקות")
				                .withLecturer("ביאטריס ריטנבאנד")
				                .withStartTime(time("13:00"))
				                .withEndTime(time("14:00"))
				                .withType(EventType.Workshop)
				                .withImages(R.drawable.event_cosplay2)
				                .withAttending(false),

		                new ConventionEvent(26)
				                .withTitle("Transform Yourself - סדנת איפור קוספליי")
				                .withLecturer("יובל ריפקין")
				                .withStartTime(time("14:00"))
				                .withEndTime(time("15:00"))
				                .withType(EventType.Workshop)
				                .withImages(R.drawable.event_reika1)
				                .withAttending(false),

		                new ConventionEvent(27)
				                .withTitle("היכרות עם המטבח היפני")
				                .withLecturer("שרית אגב")
				                .withStartTime(time("15:00"))
				                .withEndTime(time("16:00"))
				                .withType(EventType.Lecture)
				                .withImages(R.drawable.event_bento)
				                .withAttending(false),

		                new ConventionEvent(28)
				                .withTitle("מאגדה לאנימה - חלק 2: יפן, הארץ בה כישוף מביא את האושר")
				                .withLecturer("עמית יזהר")
				                .withStartTime(time("16:00"))
				                .withEndTime(time("17:00"))
				                .withType(EventType.Lecture)
				                .withImages(R.drawable.event_history)
				                .withAttending(false),

		                new ConventionEvent(29)
				                .withTitle("המנגה של התלמוד")
				                .withLecturer("אביה אמיר")
				                .withStartTime(time("17:00"))
				                .withEndTime(time("18:00"))
				                .withType(EventType.Lecture)
				                .withImages(R.drawable.event_political_manga)
				                .withAttending(false),

		                new ConventionEvent(30)
				                .withTitle("סמוראי - אגדה או אמת?")
				                .withLecturer("דניאל דין ארן")
				                .withStartTime(time("18:00"))
				                .withEndTime(time("19:00"))
				                .withType(EventType.Lecture)
				                .withImages(R.drawable.event_history)
				                .withAttending(false),

		                new ConventionEvent(31)
				                .withTitle("סדנת קנדמה")
				                .withLecturer("אבירם לוגסי ודויד וידסלבסקי")
				                .withStartTime(time("19:00"))
				                .withEndTime(time("20:00"))
				                .withType(EventType.Workshop)
				                .withImages()
				                .withAttending(false)
                ),

                inHall(oranim3,

		                new ConventionEvent(32)
				                .withTitle("תחרות שירה")
				                .withStartTime(time("11:00"))
				                .withEndTime(time("12:00"))
				                .withType(EventType.Central)
				                .withImages(R.drawable.event_idols)
				                .withAttending(false),

		                new ConventionEvent(33)
				                .withTitle("טריוויה 2015")
				                .withStartTime(time("12:00"))
				                .withEndTime(time("13:00"))
				                .withType(EventType.Special)
				                .withImages()
				                .withAttending(false),

		                new ConventionEvent(34)
				                .withTitle("אנימה או לא?")
				                .withLecturer("אלה בן יעקב")
				                .withStartTime(time("13:00"))
				                .withEndTime(time("14:00"))
				                .withType(EventType.Special)
				                .withImages(R.drawable.event_visual_novels)
				                .withAttending(false),

		                new ConventionEvent(35)
				                .withTitle("המחזמר \"תודה שבחרת\" (NTT) - הסרט")
				                .withStartTime(time("14:00"))
				                .withEndTime(time("16:00"))
				                .withType(EventType.Screening)
				                .withImages(R.drawable.event_ntt)
				                .withAttending(false),

		                new ConventionEvent(36)
				                .withTitle("הרשמה לאירוע קראוקה")
				                .withStartTime(time("16:00"))
				                .withEndTime(time("16:30"))
				                .withType(EventType.Special)
				                .withImages(R.drawable.event_idols)
				                .withAttending(false),

		                new ConventionEvent(37)
				                .withTitle("אירוע קראוקה")
				                .withStartTime(time("16:30"))
				                .withEndTime(time("20:30"))
				                .withType(EventType.Central)
				                .withImages(R.drawable.event_idols)
				                .withAttending(false)
                )
        );

        Floor floor1 = new Floor(1).withName("מפלס תחתון - כניסה").withImageResource(R.raw.floor1).withMarkerWidth(11);
        Floor floor2 = new Floor(2).withName("מפלס עליון - אולם ראשי").withImageResource(R.raw.floor2).withMarkerWidth(13);

        this.map = new ConventionMap()
                .withFloors(Arrays.asList(floor1, floor2))
                .withLocations(
                        CollectionUtils.flattenList(
                                inFloor(floor1,
                                        new MapLocation()
                                                .withHall(oranim1)
                                                .withMarkerResource(R.raw.oranim1_marker)
                                                .withSelectedMarkerResource(R.raw.oranim1_marker_selected)
                                                .withX(49)
                                                .withY(79),
                                        new MapLocation()
                                                .withHall(oranim2)
                                                .withMarkerResource(R.raw.oranim2_marker)
                                                .withSelectedMarkerResource(R.raw.oranim2_marker_selected)
                                                .withX(59)
                                                .withY(74),
                                        new MapLocation()
                                                .withHall(oranim3)
                                                .withMarkerResource(R.raw.oranim3_marker)
                                                .withSelectedMarkerResource(R.raw.oranim3_marker_selected)
                                                .withX(69)
                                                .withY(68)),
                                inFloor(floor2,
                                        new MapLocation()
                                                .withHall(auditorium)
                                                .withMarkerResource(R.raw.main_hall_marker)
                                                .withSelectedMarkerResource(R.raw.main_hall_marker_selected)
                                                .withX(53)
                                                .withY(55),
                                        new MapLocation()
                                                .withHall(contentRoom)
                                                .withMarkerResource(R.raw.content_room_marker)
                                                .withSelectedMarkerResource(R.raw.content_room_marker_selected)
                                                .withX(86)
                                                .withY(74))
                        )
                );

        updates = new ArrayList<>();
    }

    public List<ConventionEvent> getEvents() {
        return events;
    }

    public List<Hall> getHalls() {
        return halls;
    }

    public ConventionMap getMap() {
        return map;
    }

    public ConventionEvent findEventById(int eventId) {
        for (ConventionEvent event : getEvents()) {
            if (eventId == event.getId()) {
                return event;
            }
        }

        return null;
    }

    public ArrayList<ConventionEvent> findEventsByHall(final String hallName) {
        return CollectionUtils.filter(
                getEvents(),
                new CollectionUtils.Predicate<ConventionEvent>() {
                    @Override
                    public boolean where(ConventionEvent event) {
                        return hallName.equals(event.getHall().getName());
                    }
                },
                new ArrayList<ConventionEvent>()
        );
    }

    public void setUpdates(List<Update> updates) {
        this.updates = updates;
    }

    public List<Update> getUpdates() {
        return updates;
    }

    private Date time(String timeAsString) {
        try {
            return dateFormat.parse("20.08.2015 " + timeAsString);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    private List<ConventionEvent> inHall(Hall hall, ConventionEvent... events) {
        for (ConventionEvent event : events) {
            event.setHall(hall);
        }
        return Arrays.asList(events);
    }

    private List<MapLocation> inFloor(Floor floor, MapLocation... locations) {
        for (MapLocation location : locations) {
            location.setFloor(floor);
        }
        return Arrays.asList(locations);
    }

    public static void initFromFile(Context context) {
        Convention.context = context;

        readUserInputFromFile();
        readUpdatesFromFile();
    }

    private static void readUserInputFromFile() {
        Object result = readFile(EVENT_USER_INPUT_FILE_NAME);
        if (result == null) {
            return;
        }

        @SuppressWarnings("unchecked")
        Map<Integer, ConventionEvent.UserInput> userInput = (Map<Integer, ConventionEvent.UserInput>) result;

        for (ConventionEvent event : convention.getEvents()) {
            ConventionEvent.UserInput currInput = userInput.get(event.getId());
            if (currInput != null) {
                event.setUserInput(currInput);
            }
        }
    }

    private static void readUpdatesFromFile() {
        Object result = readFile(UPDATES_FILE_NAME);
        if (result == null) {
            return;
        }

        @SuppressWarnings("unchecked")
        List<Update> updates = (List<Update>) result;
        convention.setUpdates(updates);
    }

    private static Object readFile(String fileName) {
        Object result = null;

        try {
            FileInputStream fileInputStream = context.openFileInput(fileName);
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);

            result = objectInputStream.readObject();
            objectInputStream.close();
            fileInputStream.close();

        } catch (FileNotFoundException f) {
            // Ignore - default user input will be created from hard-coded data
        } catch (Exception e) {
            // Nothing we can do about badly formatted file, don't crash the app
            e.printStackTrace();
        }

        return result;
    }

    public void save() {
        if (context == null) {
            return;
        }

        saveUserInput();
        saveUpdates();
    }

    private void saveUserInput() {
        // Gather all event user input in a list with the event id
        List<ConventionEvent> events = getEvents();
        Map<Integer, ConventionEvent.UserInput> userInput = new HashMap<>(events.size());
        for (ConventionEvent event : events) {
            userInput.put(event.getId(), event.getUserInput());
        }

        saveFile(userInput, EVENT_USER_INPUT_FILE_NAME);
    }

    private void saveUpdates() {
        saveFile(getUpdates(), UPDATES_FILE_NAME);
    }

    private void saveFile(Object objectToSave, String fileName) {
        try {
            FileOutputStream fos = context.openFileOutput(fileName, Context.MODE_PRIVATE);
            ObjectOutputStream os = new ObjectOutputStream(fos);
            os.writeObject(objectToSave);
            os.close();
            fos.close();

        } catch (IOException e) {
            // Nothing we can do... don't crash the app. Maybe show error message?
            e.printStackTrace();
        }
    }
}

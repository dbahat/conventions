package amai.org.conventions.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class Convention {
    private static Convention convention = new Convention();

    private List<ConventionEvent> events;

    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:ss");

    public static Convention getInstance() {
        return convention;
    }

    private Convention() {
        Hall auditorium = new Hall().withName("אודיטוריום אושיסקין").withOrder(1);
        Hall contentRoom = new Hall().withName("חדר אירועי תוכן").withOrder(2);
        Hall oranim1 = new Hall().withName("אורנים 1").withOrder(3);
        Hall oranim2 = new Hall().withName("אורנים 2").withOrder(4);
        Hall oranim3 = new Hall().withName("אורנים 3").withOrder(5);

        this.events = flattenList(
                inHall(auditorium,

                        new ConventionEvent()
                                .withTitle("אירוע פתיחה")
                                .withStartTime(time("10:30"))
                                .withEndTime(time("12:00"))
                                .withType(EventType.Stage)
                                .withAttending(true),

                        new ConventionEvent()
                                .withTitle("שתי טיפות של דם")
                                .withLecturer("R2")
                                .withStartTime(time("12:30"))
                                .withEndTime(time("14:30"))
                                .withType(EventType.Stage)
                                .withAttending(false),

                        new ConventionEvent()
                                .withTitle("הכנה לאירוע קוספליי")
                                .withStartTime(time("15:00"))
                                .withEndTime(time("16:00"))
                                .withType(EventType.Stage)
                                .withAttending(false),

                        new ConventionEvent()
                                .withTitle("אירוע הקוספליי")
                                .withStartTime(time("16:00"))
                                .withEndTime(time("19:00"))
                                .withType(EventType.Stage)
                                .withAttending(true),

                        new ConventionEvent()
                                .withTitle("שתי טיפות של דם")
                                .withLecturer("R2")
                                .withStartTime(time("19:30"))
                                .withEndTime(time("21:00"))
                                .withType(EventType.Stage)
                                .withAttending(true)
                ),

                inHall(contentRoom,

                        new ConventionEvent()
                                .withTitle("כיצד הקהילה עיצבה אותי?")
                                .withLecturer("אלה ברוך")
                                .withStartTime(time("11:00"))
                                .withEndTime(time("12:00"))
                                .withType(EventType.Panel)
                                .withAttending(false),

                        new ConventionEvent()
                                .withTitle("המדריך לקוספלייר המתחיל")
                                .withLecturer("ענן גיבסון")
                                .withStartTime(time("12:00"))
                                .withEndTime(time("13:00"))
                                .withType(EventType.Panel)
                                .withAttending(false),

                        new ConventionEvent()
                                .withTitle("כל מה שרציתם לדעת על אנימציה!")
                                .withLecturer("סם דניאל")
                                .withStartTime(time("13:00"))
                                .withEndTime(time("14:00"))
                                .withType(EventType.Panel)
                                .withAttending(false),

                        new ConventionEvent()
                                .withTitle("לאב לייב: אז מה הסיפור")
                                .withLecturer("שרון טורנר")
                                .withStartTime(time("14:00"))
                                .withEndTime(time("15:00"))
                                .withType(EventType.Panel)
                                .withAttending(false),

                        new ConventionEvent()
                                .withTitle("בועת מחשבה: מגזינים, תוכן וקהילה")
                                .withLecturer("יבגני קנטור")
                                .withStartTime(time("15:00"))
                                .withEndTime(time("16:00"))
                                .withType(EventType.Panel)
                                .withAttending(false),

                        new ConventionEvent()
                                .withTitle("המדריך לכותב השונן המתחיל")
                                .withLecturer("אמנון לוי")
                                .withStartTime(time("16:00"))
                                .withEndTime(time("17:00"))
                                .withType(EventType.Lecture)
                                .withAttending(false),

                        new ConventionEvent()
                                .withTitle("שוגי: כשהצריח מתפתח לדרקון - ואין פה נסיכה")
                                .withLecturer("אופר עזתי")
                                .withStartTime(time("17:00"))
                                .withEndTime(time("18:00"))
                                .withType(EventType.Lecture)
                                .withAttending(false),

                        new ConventionEvent()
                                .withTitle("אל עולם המנגה המחתרתי")
                                .withLecturer("דני פייגלמן")
                                .withStartTime(time("19:00"))
                                .withEndTime(time("20:00"))
                                .withType(EventType.Lecture)
                                .withAttending(false)
                ),

                inHall(oranim1,

                        new ConventionEvent()
                                .withTitle("קוספליי בחלוף הזמן")
                                .withLecturer("עומרי גולד, נמרוד גולד, יעל גלר")
                                .withStartTime(time("11:00"))
                                .withEndTime(time("12:00"))
                                .withType(EventType.Lecture)
                                .withAttending(false),

                        new ConventionEvent()
                                .withTitle("10 דברים שצריך לדעת לפני הטיול ליפן")
                                .withLecturer("מור אורן")
                                .withStartTime(time("12:00"))
                                .withEndTime(time("13:00"))
                                .withType(EventType.Lecture)
                                .withAttending(false),

                        new ConventionEvent()
                                .withTitle("בין ורדים לרובוטים: האנימה של איקוהארה, אני ומיזאקי")
                                .withLecturer("לירון אפריאט")
                                .withStartTime(time("13:00"))
                                .withEndTime(time("14:00"))
                                .withType(EventType.Lecture)
                                .withAttending(true),

                        new ConventionEvent()
                                .withTitle("חומרים טרמופלסטיים 101")
                                .withLecturer("קרן לין")
                                .withStartTime(time("14:00"))
                                .withEndTime(time("15:00"))
                                .withType(EventType.Lecture)
                                .withAttending(false),

                        new ConventionEvent()
                                .withTitle("עיצוב דמויות בטוקוסאטסו")
                                .withLecturer("ליעד בר שלטון")
                                .withStartTime(time("15:00"))
                                .withEndTime(time("16:00"))
                                .withType(EventType.Lecture)
                                .withAttending(false),

                        new ConventionEvent()
                                .withTitle("מעוצמה קשה לרכה - מאימפריאליזם צבאי למשיכה תרבותית")
                                .withLecturer("שירן איבניצקי")
                                .withStartTime(time("16:00"))
                                .withEndTime(time("17:00"))
                                .withType(EventType.Lecture)
                                .withAttending(false),

                        new ConventionEvent()
                                .withTitle("כשרובוטים מסמיקים - אנדרואידים ומגדר במנגה ואנימה")
                                .withLecturer("עומר כהן")
                                .withStartTime(time("17:00"))
                                .withEndTime(time("18:00"))
                                .withType(EventType.Lecture)
                                .withAttending(false),

                        new ConventionEvent()
                                .withTitle("פוקימון - האם הם באו מהחלל?")
                                .withLecturer("יוסי אוחנה")
                                .withStartTime(time("18:00"))
                                .withEndTime(time("19:00"))
                                .withType(EventType.Lecture)
                                .withAttending(false),

                        new ConventionEvent()
                                .withTitle("על Plamo ו-Gunpla")
                                .withLecturer("עומר אמסלם ועומרי חפר")
                                .withStartTime(time("19:00"))
                                .withEndTime(time("20:00"))
                                .withType(EventType.Lecture)
                                .withAttending(false)
                ),

                inHall(oranim2,

                        new ConventionEvent()
                                .withTitle("החתולים בתרבות היפנית")
                                .withLecturer("עדן קליימן")
                                .withStartTime(time("11:00"))
                                .withEndTime(time("12:00"))
                                .withType(EventType.Lecture)
                                .withAttending(false),

                        new ConventionEvent()
                                .withTitle("מהרעיון אל המסך - הקסם של עולם האנימציה")
                                .withLecturer("סם דניאל")
                                .withStartTime(time("12:00"))
                                .withEndTime(time("13:00"))
                                .withType(EventType.Lecture)
                                .withAttending(true),

                        new ConventionEvent()
                                .withTitle("סקיטים ושאר ירקות")
                                .withLecturer("ביאטריס ריטנבאנד")
                                .withStartTime(time("13:00"))
                                .withEndTime(time("14:00"))
                                .withType(EventType.Workshop)
                                .withAttending(false),

                        new ConventionEvent()
                                .withTitle("Transform Yourself - סדנת איפור קוספליי")
                                .withLecturer("יובל ריפקין")
                                .withStartTime(time("14:00"))
                                .withEndTime(time("15:00"))
                                .withType(EventType.Workshop)
                                .withAttending(false),

                        new ConventionEvent()
                                .withTitle("היכרות עם המטבח היפני")
                                .withLecturer("שרית אגב")
                                .withStartTime(time("15:00"))
                                .withEndTime(time("16:00"))
                                .withType(EventType.Lecture)
                                .withAttending(false),

                        new ConventionEvent()
                                .withTitle("מאגדה לאנימה - חלק 2: יפן, הארץ בה כישוף מביא את האושר")
                                .withLecturer("עמית יזהר")
                                .withStartTime(time("16:00"))
                                .withEndTime(time("17:00"))
                                .withType(EventType.Lecture)
                                .withAttending(false),

                        new ConventionEvent()
                                .withTitle("המנגה של התלמוד")
                                .withLecturer("אביה אמיר")
                                .withStartTime(time("17:00"))
                                .withEndTime(time("18:00"))
                                .withType(EventType.Lecture)
                                .withAttending(false),

                        new ConventionEvent()
                                .withTitle("סמוראי - אגדה או אמת?")
                                .withLecturer("דניאל דין ארן")
                                .withStartTime(time("18:00"))
                                .withEndTime(time("19:00"))
                                .withType(EventType.Lecture)
                                .withAttending(false),

                        new ConventionEvent()
                                .withTitle("סדנת קנדמה")
                                .withLecturer("אבירם לוגסי ודויד וידסלבסקי")
                                .withStartTime(time("19:00"))
                                .withEndTime(time("20:00"))
                                .withType(EventType.Workshop)
                                .withAttending(false)
                ),

                inHall(oranim3,

                        new ConventionEvent()
                                .withTitle("תחרות שירה")
                                .withStartTime(time("11:00"))
                                .withEndTime(time("12:00"))
                                .withType(EventType.Special)
                                .withAttending(true),

                        new ConventionEvent()
                                .withTitle("טריוויה 2015")
                                .withStartTime(time("12:00"))
                                .withEndTime(time("13:00"))
                                .withType(EventType.Special)
                                .withAttending(false),

                        new ConventionEvent()
                                .withTitle("אנימה או לא?")
                                .withLecturer("אלה בן יעקב")
                                .withStartTime(time("13:00"))
                                .withEndTime(time("14:00"))
                                .withType(EventType.Special)
                                .withAttending(false),

                        new ConventionEvent()
                                .withTitle("המחזמר \"תודה שבחרת\" )NTT( - הסרט")
                                .withStartTime(time("14:00"))
                                .withEndTime(time("16:00"))
                                .withType(EventType.Screening)
                                .withAttending(false),

                        new ConventionEvent()
                                .withTitle("הרשמה לאירוע קראוקה")
                                .withStartTime(time("16:00"))
                                .withEndTime(time("16:30"))
                                .withType(EventType.Special)
                                .withAttending(false),

                        new ConventionEvent()
                                .withTitle("אירוע קראוקה")
                                .withStartTime(time("16:30"))
                                .withEndTime(time("20:30"))
                                .withType(EventType.Special)
                                .withAttending(false)
                )
        );
    }

    public List<ConventionEvent> getEvents() {
        return events;
    }

    private Date time(String timeAsString) {
        try {
            return dateFormat.parse("05.03.2015 " + timeAsString);
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

    @SafeVarargs
    public final <T> ArrayList<T> flattenList(List<T>... instancesList) {
        int size = 0;
        for (List<T> list : instancesList) {
            size += list.size();
        }

        ArrayList<T> flattened = new ArrayList<>(size);
        for (List<T> list : instancesList) {
            flattened.addAll(list);
        }

        return flattened;
    }
}

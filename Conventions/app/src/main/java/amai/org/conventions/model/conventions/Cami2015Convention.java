package amai.org.conventions.model.conventions;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import amai.org.conventions.R;
import amai.org.conventions.model.Convention;
import amai.org.conventions.model.ConventionEvent;
import amai.org.conventions.model.ConventionMap;
import amai.org.conventions.model.EventToImageResourceIdMapper;
import amai.org.conventions.model.Floor;
import amai.org.conventions.model.Hall;
import amai.org.conventions.model.MapLocation;
import amai.org.conventions.model.Place;
import amai.org.conventions.utils.CollectionUtils;
import amai.org.conventions.utils.ConventionStorage;

public class Cami2015Convention extends Convention {
	public static class ImageIds {
		public static final String EVENT_REIKA1 = "event_reika1";
		public static final String EVENT_REIKA2 = "event_reika2";
		public static final String EVENT_REIKA3 = "event_reika3";
		public static final String EVENT_POKEMON = "event_pokemon";
		public static final String EVENT_COLORIDO_TYPHOON = "event_colorido_typhoon";
		public static final String EVENT_COLORIDO_SUN = "event_colorido_sun";
		public static final String EVENT_PANDORA = "event_pandora";
	}

	private static final String GUEST_OF_HONOR = "אורחת כבוד";
	private static final String GAMES = "משחקים";
	private static final String SCREENINGS = "הקרנות";

	private static final String MAIN_HALL_NAME = "אולם ראשי";
	private static final String AUDITORIUM_NAME = "אודיטוריום שוורץ";
	private static final String ESHKOL1_NAME = "אשכול 1";
	private static final String ESHKOL2_NAME = "אשכול 2";
	private static final String GAMES_HALL_NAME = "משחקייה";
	private static final String SPECIAL_EVENTS_HALL_NAME = "אירועים מיוחדים";

	@Override
	protected ConventionStorage initStorage() {
		return new ConventionStorage(this);
	}

	@Override
	protected EventToImageResourceIdMapper initImageMapper() {
		EventToImageResourceIdMapper imageMapper = new EventToImageResourceIdMapper();

		return imageMapper;
	}

	@Override
	protected Calendar initDate() {
		Calendar date = Calendar.getInstance();
		date.clear();
		date.set(2015, Calendar.AUGUST, 20);
		return date;
	}

	@Override
	protected String initID() {
		return "Cami2016";
	}

	@Override
	protected String initDisplayName() {
		return "כאמ\"י 2016";
	}

	@Override
	protected String initFeedbackRecipient() {
		return "content@cami.org.il";
	}

	@Override
	protected URL initModelURL() {
		try {
			return new URL("http://2015.cami.org.il/wp-admin/admin-ajax.php?action=get_event_list");
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	protected String initFacebookFeedPath() {
		return "/cami.org.il/posts";
	}

	@Override
	protected double initLongitude() {
		return 35.202425;
	}

	@Override
	protected double initLatitude() {
		return 31.786372;
	}

	@Override
	protected List<Hall> initHalls() {
		Hall mainHall = new Hall().withName(MAIN_HALL_NAME).withOrder(1);
		Hall auditorium = new Hall().withName(AUDITORIUM_NAME).withOrder(2);
		Hall eshkol1 = new Hall().withName(ESHKOL1_NAME).withOrder(3);
		Hall eshkol2 = new Hall().withName(ESHKOL2_NAME).withOrder(4);
		Hall games = new Hall().withName(GAMES_HALL_NAME).withOrder(5);
		Hall specialEvents = new Hall().withName(SPECIAL_EVENTS_HALL_NAME).withOrder(6);

		return Arrays.asList(mainHall, auditorium, eshkol1, eshkol2, games, specialEvents);
	}

	protected ConventionMap initMap() {
		Hall mainHall = findHallByName(MAIN_HALL_NAME);
		Hall auditorium = findHallByName(AUDITORIUM_NAME);
		Hall eshkol1 = findHallByName(ESHKOL1_NAME);
		Hall eshkol2 = findHallByName(ESHKOL2_NAME);
		Hall games = findHallByName(GAMES_HALL_NAME);

		Floor floor1 = new Floor(1).withName("מפלס תחתון וקומת ביניים").withImageResource(R.raw.cami_floor1).withMarkerHeight(13);
		Floor floor2 = new Floor(2).withName("מפלס עליון").withImageResource(R.raw.cami_floor2).withMarkerHeight(14);

		return new ConventionMap()
				.withFloors(Arrays.asList(floor1, floor2))
				.withLocations(
						CollectionUtils.flattenList(
								inFloor(floor1,
										new MapLocation()
												.withPlace(eshkol2)
												.withMarkerResource(R.raw.eshkol2_marker)
												.withSelectedMarkerResource(R.raw.eshkol2_marker_selected)
												.withX(26)
												.withY(73),
										new MapLocation()
												.withPlace(new Place().withName("החתמות"))
												.withMarkerResource(R.raw.signatures_marker)
												.withSelectedMarkerResource(R.raw.signatures_marker_selected)
												.withX(81)
												.withY(58),
										new MapLocation()
												.withPlace(new Place().withName("מודיעין"))
												.withMarkerResource(R.raw.information_marker)
												.withSelectedMarkerResource(R.raw.information_marker_selected)
												.withX(41)
												.withY(37),
										new MapLocation()
												.withPlace(new Place().withName("קופות"))
												.withMarkerResource(R.raw.cachiers_marker)
												.withSelectedMarkerResource(R.raw.cachiers_marker_selected)
												.withX(36)
												.withY(19),
										new MapLocation()
												.withPlace(new Place().withName("שירותים"))
												.withMarkerResource(R.raw.toilet_marker)
												.withSelectedMarkerResource(R.raw.toilet_marker_selected)
												.withX(49)
												.withY(7),
										new MapLocation()
												.withPlace(eshkol1)
												.withMarkerResource(R.raw.eshkol1_marker)
												.withSelectedMarkerResource(R.raw.eshkol1_marker_selected)
												.withX(26)
												.withY(54),
										new MapLocation()
												.withPlace(auditorium)
												.withMarkerResource(R.raw.schwartz_marker)
												.withSelectedMarkerResource(R.raw.schwartz_marker_selected)
												.withX(30)
												.withY(49),
										new MapLocation()
												.withPlace(new Place().withName("שירותים"))
												.withMarkerResource(R.raw.toilet_marker)
												.withSelectedMarkerResource(R.raw.toilet_marker_selected)
												.withX(12)
												.withY(46)),
								inFloor(floor2,
										// Keep this location before storage because otherwise when
										// storage is selected, it's displayed behind this location
										new MapLocation()
												.withPlace(new Place().withName("שיפוט קוספליי"))
												.withMarkerResource(R.raw.cosplay_judgement_marker)
												.withSelectedMarkerResource(R.raw.cosplay_judgement_marker_selected)
												.withX(83)
												.withY(75),
										new MapLocation()
												.withPlace(games)
												.withMarkerResource(R.raw.games_marker)
												.withSelectedMarkerResource(R.raw.games_marker_selected)
												.withX(48)
												.withY(79),
										new MapLocation()
												.withPlace(mainHall)
												.withMarkerResource(R.raw.main_hall_marker)
												.withSelectedMarkerResource(R.raw.main_hall_marker_selected)
												.withX(58)
												.withY(57),
										new MapLocation()
												.withPlace(new Place().withName("כניסה פלוס"))
												.withMarkerResource(R.raw.entrance_plus_marker)
												.withSelectedMarkerResource(R.raw.entrance_plus_marker_selected)
												.withX(65)
												.withY(37),
										new MapLocation()
												.withPlace(new Place().withName("פינת צילום"))
												.withMarkerResource(R.raw.photoshoot_corner_marker)
												.withSelectedMarkerResource(R.raw.photoshoot_corner_marker_selected)
												.withX(55)
												.withY(35),
										new MapLocation()
												.withPlace(new Place().withName("תיקון קוספליי"))
												.withMarkerResource(R.raw.cosplay_fixes_marker)
												.withSelectedMarkerResource(R.raw.cosplay_fixes_marker_selected)
												.withX(58)
												.withY(22),
										new MapLocation()
												.withPlace(new Place().withName("שירותים"))
												.withMarkerResource(R.raw.toilet_marker)
												.withSelectedMarkerResource(R.raw.toilet_marker_selected)
												.withX(58)
												.withY(11),
										new MapLocation()
												.withPlace(new Place().withName("שמירת חפצים"))
												.withMarkerResource(R.raw.storage_marker)
												.withSelectedMarkerResource(R.raw.storage_marker_selected)
												.withX(22)
												.withY(65),
										new MapLocation()
												.withPlace(new Place().withName("ווידוא ווקאון"))
												.withMarkerResource(R.raw.walkon_marker)
												.withSelectedMarkerResource(R.raw.walkon_marker_selected)
												.withX(30)
												.withY(58),
										new MapLocation()
												.withPlace(new Place().withName("שירותים"))
												.withMarkerResource(R.raw.toilet_marker)
												.withSelectedMarkerResource(R.raw.toilet_marker_selected)
												.withX(7)
												.withY(61))
						)
				);
	}

	@Override
	public ConventionEvent handleSpecialEvent(ConventionEvent conventionEvent) {
		if (conventionEvent.getDescription().isEmpty()) {
			if (conventionEvent.getType().getDescription().equals(GUEST_OF_HONOR)) {
				conventionEvent = handleGuestOfHonorEvent(conventionEvent);
			}
			if (conventionEvent.getType().getDescription().equals(GAMES)) {
				conventionEvent = handleGamesEvent(conventionEvent);
			}
			if (conventionEvent.getType().getDescription().equals(SCREENINGS)) {
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
		return super.handleSpecialEvent(conventionEvent);
	}

	private final List<String> guestOfHonorImageResourceIds = Arrays.asList(ImageIds.EVENT_REIKA1, ImageIds.EVENT_REIKA2, ImageIds.EVENT_REIKA3);
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

	private final List<String> gamesImageResources = Arrays.asList(ImageIds.EVENT_POKEMON);
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

	private final List<String> coloridoMoviesImageResources = Arrays.asList(ImageIds.EVENT_COLORIDO_TYPHOON, ImageIds.EVENT_COLORIDO_SUN);
	private final static String coloridoMoviesEventDescription = "<p><span style=\"color: #ffffff;\">סטודיו קולורידו הוא סטודיו קטן אשר ידוע בפירסומות האנימציה והסרטים הקצרים העליזים והצבעוניים אותו הפיק. ברצוננו להודות לסטודיו קולורידו (Studio Colorido) על אישור הקרנת שניים מסרטיו לקהל המבקרים של כאמ&quot;י 2015.</span></p>\n" +
			"<h3>טייפון נורודה &#8211; Taifuu no Noruda</h3>\n" +
			"<p><strong>שנה:</strong> 2015 <strong>אורך:</strong> 27 דקות <strong>במאי:</strong> יוג'ירו אראי <strong>אנימציה:</strong> סטודיו Colorido.</p>\n" +
			"<p>עלילת הסרט מתרחשת על אי מבודד כל שהוא, בחטיבת ביניים מסויימת, בערב לפני פסטיבל התרבות. נער פורש ממשחק הבייסבול אחרי ששיחק כל חייו ורב עם חברו הטוב ביותר כאשר לפתע הם פוגשים בבחורה מסתורית ואדומת עיניים בשם נורודה, וטייפון עוצמתי פוגע בחטיבת הביניים שלהם.</p>\n" +
			"<p><strong>אודות הבמאי:</strong><br />\n" +
			"ליוג'ירו אראי (Yojiro Arai), זוהי עבודתו הראשונה כבמאי. עד כה שימש כאנימטור בסטודיו ג'יבלי וכבמאי אנימציה בסטודיו קולורידו (בין עבודותיו בסטודיו גם הסרט &quot;ילד השמש וילדת הטל&quot;).</p>" +
			"<p>תמונה עליונה: <br/>Yujiro Arai / Studio Colorido ©</p>" +
			"<h3>ילד השמש וילדת הטל &#8211; Sunny Boy &amp; Dewdrop Girl</h3>\n" +
			"<p><strong>שנה:</strong> 2013 <strong>אורך:</strong> 18 דקות <strong>במאי:</strong> אישידה הירויאסו <strong>אנימציה:</strong> סטודיו Colorido.</p>\n" +
			"<p>הינאטה מתאהב בחברתו לספסל הלימודים, שיגארו. אבל בעוד שהינאטה ממש טוב בלצייר, הוא ממש לא טוב בלדבר עם אנשים, אז הוא שומר על רגשותיו כלפיה בציוריו ובדמיונותיו. כשמשפחתה של שיגארו מחליטים לעבור לעיר אחרת, הינאטה מחליט ביום המעבר שלהם שהוא מוכרח להתוודות על רגשותיו כלפיה ויוצא למרדף אחר הרכבת שלוקחת את שיגארו.</p>\n" +
			"<p><strong>אודות הבמאי:</strong></p>\n" +
			"<p>אישידה הירויאסו (Ishida Hiroyasu) הינו בוגר אוניבריסטת קיוטו סייקה. הירויאסו מפיק סרטים קצרים בצורה עצמאית ומפרסם אותם באינטרט תחת השם Tete. ב-2009 הסרט שלו &quot;ההתוודאות של פומיקו&quot; (Fumiko no Kokuhaku), שאורכו 2 וחצי דקות, זכה לתשומת לב בינלאומית ברחבי הרשת. עבור סרט זה זכה הירויאסו בפרס הוידאו של היוטיוב היפני. עבודותיו זכו לשבחים בין היתר על ידי הבמאי מאמורו הוסודה (Mamoru Hosuda) והן הוצגו במגזין &quot;Newtype&quot;.</p>\n" +
			"<p><a href=\"https://www.youtube.com/user/ishidahiroyasu/feed\" target=\"_blank\">לערוץ היוטוב שלו</a>.</p>" +
			"<p>תמונה תחתונה: <br/>Ishida Hiroyasu / Studio Colorido ©</p>";

	private ConventionEvent handleStudioColoridMoviesEvent(ConventionEvent event) {
		return event
				.withDescription(coloridoMoviesEventDescription)
				.withImages(coloridoMoviesImageResources);
	}

	private final List<String> pandoraImageResources = Arrays.asList(ImageIds.EVENT_PANDORA);
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

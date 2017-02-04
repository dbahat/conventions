package amai.org.conventions.model.conventions;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import amai.org.conventions.R;
import amai.org.conventions.model.ConventionMap;
import amai.org.conventions.model.EventToImageResourceIdMapper;
import amai.org.conventions.model.Floor;
import amai.org.conventions.model.Hall;
import amai.org.conventions.model.MapLocation;
import amai.org.conventions.model.Place;
import amai.org.conventions.model.Stand;
import amai.org.conventions.model.StandsArea;
import amai.org.conventions.utils.CollectionUtils;
import amai.org.conventions.utils.ConventionStorage;

public class Cami2016Convention extends AmaiConvention {
	// Hall names
	private static final String MAIN_HALL_NAME = "אולם ראשי";
	private static final String SCHWARTZ_NAME = "אודיטוריום שוורץ";
	private static final String ESHKOL1_NAME = "אשכול 1";
	private static final String ESHKOL2_NAME = "אשכול 2";
	private static final String ESHKOL3_NAME = "אשכול 3";

	@Override
	protected ConventionStorage initStorage() {
		return new ConventionStorage(this, R.raw.cami2016_convention_events, 1);
	}

	@Override
	protected Calendar initDate() {
		Calendar date = Calendar.getInstance();
		date.clear();
		date.set(2016, Calendar.AUGUST, 25);
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
	protected String initFeedbackRecipient() {
		return "content@cami.org.il";
	}

	@Override
	protected URL initModelURL() {
		try {
			return new URL("http://2016.cami.org.il/wp-admin/admin-ajax.php?action=get_event_list");
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	protected EventToImageResourceIdMapper initImageMapper() {
		EventToImageResourceIdMapper imageMapper = new EventToImageResourceIdMapper();

		imageMapper.addMapping("http://2016.cami.org.il/wp-content/uploads/sites/9/2016/07/Youtube-banner-1.jpg", R.drawable.event_2d_musical);
		imageMapper.addMapping("http://2016.cami.org.il/wp-content/uploads/sites/9/2016/07/פייר_אמבלם-225x300.png", R.drawable.event_fire_emblem);
		imageMapper.addMapping("http://2016.cami.org.il/wp-content/uploads/sites/9/2016/07/קוספליי-שאלות-ותשובות-768x1024.jpg", R.drawable.event_cosplay_il);
		imageMapper.addMapping("http://2016.cami.org.il/wp-content/uploads/sites/9/2016/07/תרבות-האוטקו-ממבט-אישי.jpg", R.drawable.event_otaku_culture);
		imageMapper.addMapping("http://2016.cami.org.il/wp-content/uploads/sites/9/2016/07/13558855_1560989217537372_9143726053987064666_o.jpg", R.drawable.event_taiko);
		imageMapper.addMapping("http://2016.cami.org.il/wp-content/uploads/sites/9/2016/07/המדריך-לאספן-המתחיל_-פיגרים.jpg", R.drawable.event_figures);
		imageMapper.addMapping("http://2016.cami.org.il/wp-content/uploads/sites/9/2016/07/ארונות-קוספליי-ואלטעזאכן.jpeg", R.drawable.event_closets);
		imageMapper.addMapping("http://2016.cami.org.il/wp-content/uploads/sites/9/2016/07/כרומוזום-X-956x1024.jpg", R.drawable.event_chromosome_x);
		imageMapper.addMapping("http://2016.cami.org.il/wp-content/uploads/sites/9/2016/07/טריוויה-754x1024.png", R.drawable.event_committee);
		imageMapper.addMapping("http://2016.cami.org.il/wp-content/uploads/sites/9/2016/07/kawaii-or-ready-to-die-101.jpg", R.drawable.event_kawaii);
		imageMapper.addMapping("http://2016.cami.org.il/wp-content/uploads/sites/9/2016/07/משחקי-אוטומה-237x300.jpg", R.drawable.event_otome_games);
		imageMapper.addMapping("http://2016.cami.org.il/wp-content/uploads/sites/9/2016/07/בין-הנאה-לשחרור-225x300.jpg", R.drawable.event_yaoi);
		imageMapper.addMapping("http://2016.cami.org.il/wp-content/uploads/sites/9/2016/07/Aimee.jpg", R.drawable.event_aimee);
		imageMapper.addMapping("http://2016.cami.org.il/wp-content/uploads/sites/9/2016/07/משל-השועל-300x225.png", R.drawable.event_fox);
		imageMapper.addMapping("http://2016.cami.org.il/wp-content/uploads/sites/9/2016/07/חומרים-קסומים-225x300.jpg", R.drawable.event_materials);
		imageMapper.addMapping("http://2016.cami.org.il/wp-content/uploads/sites/9/2016/07/גיישה-וסמוראי-300x241.jpg", R.drawable.event_geisha_samurai);
		imageMapper.addMapping("http://2016.cami.org.il/wp-content/uploads/sites/9/2016/07/ציור_דמויות_למתחילים.jpg", R.drawable.event_drawing);
		imageMapper.addMapping("http://2016.cami.org.il/wp-content/uploads/sites/9/2016/07/הגיבורה-בהיעדרה-דמויות-נשיות-בנובלות-ויזואליות-300x225.jpg", R.drawable.event_vn_girls);
		imageMapper.addMapping("http://2016.cami.org.il/wp-content/uploads/sites/9/2016/07/בנטו-300x300.jpg", R.drawable.event_obento);
		imageMapper.addMapping("http://2016.cami.org.il/wp-content/uploads/sites/9/2016/06/cosplay2.jpg", R.drawable.event_cosplay);
		imageMapper.addMapping("http://2016.cami.org.il/wp-content/uploads/sites/9/2016/07/איסקאי-300x225.png", R.drawable.event_isekai);
		imageMapper.addMapping("http://2016.cami.org.il/wp-content/uploads/sites/9/2016/07/סוף_העולם-300x225.jpg", R.drawable.event_world_end);
		imageMapper.addMapping("http://2016.cami.org.il/wp-content/uploads/sites/9/2016/07/הנטאי-225x300.jpg", R.drawable.event_hentai);
		imageMapper.addMapping("http://2016.cami.org.il/wp-content/uploads/sites/9/2016/07/מאדוקה.png", R.drawable.event_madoka);
		imageMapper.addMapping("http://2016.cami.org.il/wp-content/uploads/sites/9/2016/07/נשים-ביפן.jpg", R.drawable.event_mememe);

		// Non-URL IDs
		imageMapper.addMapping(EventToImageResourceIdMapper.EVENT_GENERIC, R.drawable.cami2016_events_default_cover);

		return imageMapper;
	}

	@Override
	protected List<Hall> initHalls() {
		Hall mainHall = new Hall().withName(MAIN_HALL_NAME).withOrder(1);
		Hall auditorium = new Hall().withName(SCHWARTZ_NAME).withOrder(2);
		Hall eshkol1 = new Hall().withName(ESHKOL1_NAME).withOrder(3);
		Hall eshkol2 = new Hall().withName(ESHKOL2_NAME).withOrder(4);
		Hall eshkol3 = new Hall().withName(ESHKOL3_NAME).withOrder(5);

		return Arrays.asList(mainHall, auditorium, eshkol1, eshkol2, eshkol3);
	}

	@Override
	protected ConventionMap initMap() {
		Hall mainHall = findHallByName(MAIN_HALL_NAME);
		Hall schwatrz = findHallByName(SCHWARTZ_NAME);
		Hall eshkol1 = findHallByName(ESHKOL1_NAME);
		Hall eshkol2 = findHallByName(ESHKOL2_NAME);
		Hall eshkol3 = findHallByName(ESHKOL3_NAME);

		Floor floor1 = new Floor(1)
				.withName("מפלס תחתון")
				.withImageResource(R.raw.cami2016_floor1, true)
				.withImageWidth(552.20001f)
				.withImageHeight(324.60001f);
		Floor floor2 = new Floor(2)
				.withName("מפלס עליון")
				.withImageResource(R.raw.cami2016_floor2, true)
				.withImageWidth(848.39502f)
				.withImageHeight(502.86401f);

		StandsArea agam = new StandsArea().withName("טרקלין אגם").withStands(getAgamStands()).withImageResource(R.drawable.agam);
		StandsArea pinkus = new StandsArea().withName("אולם פינקוס").withStands(getPinkusStands()).withImageResource(R.drawable.pinkus);
		StandsArea nesher = new StandsArea().withName("אולם כניסה").withStands(getNesherStands()).withImageResource(R.drawable.nesher);
		return new ConventionMap()
				.withFloors(Arrays.asList(floor1, floor2))
				.withLocations(
						CollectionUtils.flattenList(
								inFloor(floor1,
										new MapLocation()
												.withPlace(new Place().withName("שירותים"))
												.withMarkerResource(R.raw.cami2016_marker_toilet, true)
												.withSelectedMarkerResource(R.raw.cami2016_marker_toilet_floor1_selected, true)
												.withMarkerHeight(25.066f)
												.withX(507.46f)
												.withY(184.49f),
										new MapLocation() // This is before the guest sign post so it will be selected as the stands area
												.withName("מודיעין ודוכן אמא\"י")
												.withPlace(nesher)
												.withMarkerResource(R.raw.cami2016_marker_information, true)
												.withSelectedMarkerResource(R.raw.cami2016_marker_information_selected, true)
												.withMarkerHeight(46.766f)
												.withX(347.67f)
												.withY(113.62f),
										new MapLocation()
												.withPlace(nesher)
												.withName("החתמת אורחת")
												.withMarkerResource(R.raw.cami2016_marker_guest, true)
												.withSelectedMarkerResource(R.raw.cami2016_marker_guest_selected, true)
												.withMarkerHeight(38.066f)
												.withX(410.17f)
												.withY(155.19f),
										new MapLocation()
												.withName("מתחם דוכנים")
												.withPlace(pinkus)
												.withMarkerResource(R.raw.cami2016_marker_stalls_pink, true)
												.withSelectedMarkerResource(R.raw.cami2016_marker_stalls_pink_selected, true)
												.withMarkerHeight(26.366f)
												.withX(352.06f)
												.withY(194.29f),
										new MapLocation()
												.withPlace(new Place().withName("שמירת חפצים"))
												.withMarkerResource(R.raw.cami2016_marker_storage, true)
												.withSelectedMarkerResource(R.raw.cami2016_marker_storage_selected, true)
												.withMarkerHeight(39.667f)
												.withX(314.06f)
												.withY(73.50f),
										new MapLocation()
												.withPlace(new Place().withName("שירותים"))
												.withMarkerResource(R.raw.cami2016_marker_toilet, true)
												.withSelectedMarkerResource(R.raw.cami2016_marker_toilet_floor1_selected, true)
												.withMarkerHeight(25.066f)
												.withX(291.36f)
												.withY(26.89f),
										new MapLocation()
												.withPlace(eshkol1)
												.withMarkerResource(R.raw.cami2016_marker_eshkol1, true)
												.withSelectedMarkerResource(R.raw.cami2016_marker_eshkol1_selected, true)
												.withMarkerHeight(39.466f)
												.withX(148.36f)
												.withY(177.50f),
										new MapLocation()
												.withPlace(schwatrz)
												.withMarkerResource(R.raw.cami2016_marker_schwartz, true)
												.withSelectedMarkerResource(R.raw.cami2016_marker_schwartz_selected, true)
												.withMarkerHeight(38.566f)
												.withX(195.86f)
												.withY(162.39f),
										new MapLocation()
												.withPlace(eshkol3)
												.withMarkerResource(R.raw.cami2016_marker_eshkol3, true)
												.withSelectedMarkerResource(R.raw.cami2016_marker_eshkol3_selected, true)
												.withMarkerHeight(39.766f)
												.withX(103.36f)
												.withY(239.70f),
										new MapLocation()
												.withPlace(eshkol2)
												.withMarkerResource(R.raw.cami2016_marker_eshkol2, true)
												.withSelectedMarkerResource(R.raw.cami2016_marker_eshkol2_selected, true)
												.withMarkerHeight(39.766f)
												.withX(159.16f)
												.withY(239.69f),
										new MapLocation()
												.withPlace(new Place().withName("שירותים"))
												.withMarkerResource(R.raw.cami2016_marker_toilet, true)
												.withSelectedMarkerResource(R.raw.cami2016_marker_toilet_floor1_selected, true)
												.withMarkerHeight(25.066f)
												.withX(81.36f)
												.withY(147.39f)),
								inFloor(floor2,
										new MapLocation()
												.withPlace(new Place().withName("שיפוט קוספליי"))
												.withMarkerResource(R.raw.cami2016_marker_cosplay_judgement, true)
												.withSelectedMarkerResource(R.raw.cami2016_marker_cosplay_judgement_selected, true)
												.withMarkerHeight(59.226f)
												.withX(729.53f)
												.withY(345.00f),
										new MapLocation()
												.withPlace(new Place().withName("כניסה פלוס"))
												.withMarkerResource(R.raw.cami2016_marker_entrance_plus, true)
												.withSelectedMarkerResource(R.raw.cami2016_marker_entrance_plus_selected, true)
												.withMarkerHeight(59.074f)
												.withX(572.36f)
												.withY(185.70f),
										new MapLocation()
												.withPlace(mainHall)
												.withMarkerResource(R.raw.cami2016_marker_main_hall, true)
												.withSelectedMarkerResource(R.raw.cami2016_marker_main_hall_selected, true)
												.withMarkerHeight(91.121f)
												.withX(527.28f)
												.withY(268.68f),
										new MapLocation()
												.withPlace(new Place().withName("שירותים"))
												.withMarkerResource(R.raw.cami2016_marker_toilet, true)
												.withSelectedMarkerResource(R.raw.cami2016_marker_toilet_floor2_selected, true)
												.withMarkerHeight(38.046f)
												.withX(506.39f)
												.withY(46.52f),
										new MapLocation()
												.withPlace(new Place().withName("מתחם קוספליי"))
												.withMarkerResource(R.raw.cami2016_marker_cosplay_area, true)
												.withSelectedMarkerResource(R.raw.cami2016_marker_cosplay_area_selected, true)
												.withMarkerHeight(59.074f)
												.withX(433.93f)
												.withY(371.32f),
										new MapLocation()
												.withPlace(new Place().withName("משחקייה וקונסולות"))
												.withMarkerResource(R.raw.cami2016_marker_games, true)
												.withSelectedMarkerResource(R.raw.cami2016_marker_games_selected, true)
												.withMarkerHeight(59.074f)
												.withX(306.85f)
												.withY(332.30f),
										new MapLocation()
												.withName("מתחם דוכנים")
												.withPlace(agam)
												.withMarkerResource(R.raw.cami2016_marker_stalls_red, true)
												.withSelectedMarkerResource(R.raw.cami2016_marker_stalls_red_selected, true)
												.withMarkerHeight(39.860f)
												.withX(306.85f)
												.withY(181.00f),
										new MapLocation()
												.withPlace(new Place().withName("שירותים"))
												.withMarkerResource(R.raw.cami2016_marker_toilet, true)
												.withSelectedMarkerResource(R.raw.cami2016_marker_toilet_floor2_selected, true)
												.withMarkerHeight(38.044f)
												.withX(75.99f)
												.withY(277.38f))
						)
				);
	}

	private List<Stand> getPinkusStands() {
		return Arrays.asList(
				new Stand().withName("אוטאקו שופ").withType(Stand.StandType.COMMERCIAL).withLocationName("c-01-04"),
				new Stand().withName("RETRO GAME CENTER").withType(Stand.StandType.COMMERCIAL).withLocationName("c-05-08"),
				new Stand().withName("נקסוס").withType(Stand.StandType.COMMERCIAL).withLocationName("c-09-12"),
				new Stand().withName("EDHD☆shirt").withType(Stand.StandType.INDEPENDENT).withLocationName("c-13"),
				new Stand().withName("קריספי סאב\\Crispy-Sub!").withType(Stand.StandType.DONATION).withLocationName("c-14"),
				new Stand().withName("Candy Lenses").withType(Stand.StandType.COMMERCIAL).withLocationName("c-15-20"),
				new Stand().withName("Gamer").withType(Stand.StandType.COMMERCIAL).withLocationName("c-21"),
				new Stand().withName("SVAG").withType(Stand.StandType.COMMERCIAL).withLocationName("c-23-24"),
				new Stand().withName("PowerfulMerch").withType(Stand.StandType.COMMERCIAL).withLocationName("c-25-26"),
				new Stand().withName("הדוכן של אור").withType(Stand.StandType.INDEPENDENT).withLocationName("c-27-28"),
				new Stand().withName("Cosplay Senpai").withType(Stand.StandType.COMMERCIAL).withLocationName("c-29-30"),
				new Stand().withName("אנימה סטור").withType(Stand.StandType.COMMERCIAL).withLocationName("c-31-36"),
				new Stand().withName("הקובייה משחקים בע\"מ").withType(Stand.StandType.COMMERCIAL).withLocationName("c-37-38"),
				new Stand().withName("Uranophobia ccg").withType(Stand.StandType.COMMERCIAL).withLocationName("c-39-41"),
				new Stand().withName("Worbla").withType(Stand.StandType.COMMERCIAL).withLocationName("c-42"),
				new Stand().withName("Gaming Land").withType(Stand.StandType.COMMERCIAL).withLocationName("c-43-48"),
				new Stand().withName("המרכז ללימודי יפנית").withType(Stand.StandType.OTHER).withLocationName("c-49-50"),
				new Stand().withName("waterdew").withType(Stand.StandType.INDEPENDENT).withLocationName("d-01-02"),
				new Stand().withName("הגלריה של איתי").withType(Stand.StandType.INDEPENDENT).withLocationName("d-03"),
				new Stand().withName("Beadesign").withType(Stand.StandType.INDEPENDENT).withLocationName("d-04"),
				new Stand().withName("מושיק גולסט").withType(Stand.StandType.INDEPENDENT).withLocationName("d-05-06"),
				new Stand().withName("Shironeko Pony").withType(Stand.StandType.INDEPENDENT).withLocationName("d-07-08"),
				new Stand().withName("DraMagic").withType(Stand.StandType.DONATION).withLocationName("d-10"),
				new Stand().withName("Dor's Designs").withType(Stand.StandType.INDEPENDENT).withLocationName("d-11-12"),
				new Stand().withName("הדוכן המדהים של שלישית הקסם").withType(Stand.StandType.COMMERCIAL).withLocationName("d-13-14"),
				new Stand().withName("Roza's Fluffy Stuff").withType(Stand.StandType.INDEPENDENT).withLocationName("d-15-16"),
				new Stand().withName("Dark vibes").withType(Stand.StandType.INDEPENDENT).withLocationName("d-17-18"),
				new Stand().withName("Amelia hats").withType(Stand.StandType.INDEPENDENT).withLocationName("d-19"),
				new Stand().withName("כריות פנדומים").withType(Stand.StandType.INDEPENDENT).withLocationName("d-20-21"),
				new Stand().withName("Japaneasy").withType(Stand.StandType.INDEPENDENT).withLocationName("d-22"),
				new Stand().withName("velvet octopus").withType(Stand.StandType.INDEPENDENT).withLocationName("d-23-24"),
				new Stand().withName("Rivendell").withType(Stand.StandType.INDEPENDENT).withLocationName("e-01"),
				new Stand().withName("Compoco").withType(Stand.StandType.INDEPENDENT).withLocationName("e-02-03"),
				new Stand().withName("לא אנושיט - Low Eno Shit").withType(Stand.StandType.INDEPENDENT).withLocationName("e-05"),
				new Stand().withName("נרדגזם/גיק בסטה").withType(Stand.StandType.COMMERCIAL).withLocationName("e-06"),
				new Stand().withName("Crow's Treasure").withType(Stand.StandType.INDEPENDENT).withLocationName("e-07-08"),
				new Stand().withName("סרוגי").withType(Stand.StandType.INDEPENDENT).withLocationName("e-09-10"),
				new Stand().withName("KawaiiStickers").withType(Stand.StandType.INDEPENDENT).withLocationName("e-11-12"),
				new Stand().withName("בועת מחשבה - מגזין אנימה, מנגה ותרבות האוטקו").withType(Stand.StandType.INDEPENDENT).withLocationName("e-13"),
				new Stand().withName("הדוכן של לי").withType(Stand.StandType.COMMERCIAL).withLocationName("e-14"),
				new Stand().withName("Hatz, lolita & more").withType(Stand.StandType.INDEPENDENT).withLocationName("e-15"),
				new Stand().withName("בתוך הקופסה").withType(Stand.StandType.COMMERCIAL).withLocationName("e-16-17"),
				new Stand().withName("לימור שטרן תכשיטים").withType(Stand.StandType.COMMERCIAL).withLocationName("e-18-20"),
				new Stand().withName("אמיגורמי").withType(Stand.StandType.INDEPENDENT).withLocationName("e-21"),
				new Stand().withName("כובעי עינב").withType(Stand.StandType.INDEPENDENT).withLocationName("e-22-23")
		);
	}

	private List<Stand> getAgamStands() {
		return Arrays.asList(
				new Stand().withName("נגה- ציורים ואומנות").withType(Stand.StandType.INDEPENDENT).withLocationName("b-20-21"),
				new Stand().withName("הדוכן של פיצה").withType(Stand.StandType.INDEPENDENT).withLocationName("b-22-23"),
				new Stand().withName("הדוכן של בר").withType(Stand.StandType.INDEPENDENT).withLocationName("b-24"),
				new Stand().withName("dor20 studios").withType(Stand.StandType.INDEPENDENT).withLocationName("b-25-26"),
				new Stand().withName("היקום המקביל").withType(Stand.StandType.INDEPENDENT).withLocationName("b-27"),
				new Stand().withName("Aniart4u").withType(Stand.StandType.INDEPENDENT).withLocationName("b-28"),
				new Stand().withName("קבוצת יצירת קומיקס").withType(Stand.StandType.INDEPENDENT).withLocationName("b-29-30"),
				new Stand().withName("פוגי קומיקס").withType(Stand.StandType.INDEPENDENT).withLocationName("b-01"),
				new Stand().withName("Anime Fanarts").withType(Stand.StandType.INDEPENDENT).withLocationName("b-11"),
				new Stand().withName("ציורים ופיצ'פקס").withType(Stand.StandType.INDEPENDENT).withLocationName("b-12"),
				new Stand().withName("Ella's Art").withType(Stand.StandType.INDEPENDENT).withLocationName("b-13"),
				new Stand().withName("Rinska's Booth").withType(Stand.StandType.INDEPENDENT).withLocationName("b-14-15"),
				new Stand().withName("fishiebug").withType(Stand.StandType.INDEPENDENT).withLocationName("b-16-17"),
				new Stand().withName("Grisim & Mirrorshards").withType(Stand.StandType.INDEPENDENT).withLocationName("b-18-19"),
				new Stand().withName("הגלקסיה").withType(Stand.StandType.COMMERCIAL).withLocationName("a-21-24"),
				new Stand().withName("קומיקאזה 2.0").withType(Stand.StandType.COMMERCIAL).withLocationName("a-27-30"),
				new Stand().withName("Panda shop").withType(Stand.StandType.COMMERCIAL).withLocationName("a-31-33"),
				new Stand().withName("אנימה ווייב").withType(Stand.StandType.COMMERCIAL).withLocationName("a-33-38"),
				new Stand().withName("Halo's Art").withType(Stand.StandType.COMMERCIAL).withLocationName("a-39-40"),
				new Stand().withName("Anime Chudoku").withType(Stand.StandType.COMMERCIAL).withLocationName("a-41-42"),
				new Stand().withName("מאי ארט").withType(Stand.StandType.COMMERCIAL).withLocationName("a-01-04"),
				new Stand().withName("אנימו ומנגו").withType(Stand.StandType.COMMERCIAL).withLocationName("a-05-06"),
				new Stand().withName("GoZgi").withType(Stand.StandType.COMMERCIAL).withLocationName("a-07-08"),
				new Stand().withName("הממלכה").withType(Stand.StandType.COMMERCIAL).withLocationName("a-09"),
				new Stand().withName("קומיקום").withType(Stand.StandType.COMMERCIAL).withLocationName("a-10"),
				new Stand().withName("קומיקס וירקות").withType(Stand.StandType.COMMERCIAL).withLocationName("a-11-14"),
				new Stand().withName("שיפודן ישראל").withType(Stand.StandType.COMMERCIAL).withLocationName("a-15-20")
		);
	}

	private List<Stand> getNesherStands() {
		return Arrays.asList(
				new Stand().withName("מודיעין ודוכן אמא\"י").withType(Stand.StandType.OTHER),
				new Stand().withName("דוכן אורחת הכנס - איימי בלאקשלגר").withType(Stand.StandType.OTHER).withLocationName("F-11-12"),
				new Stand().withName("שגרירות יפן").withType(Stand.StandType.OTHER).withLocationName("F-07-08"),
				new Stand().withName("קוספליי למען מטרה").withType(Stand.StandType.DONATION).withLocationName("F-05"),
				new Stand().withName("תא ניפון באוניברסיטה העברית").withType(Stand.StandType.OTHER).withLocationName("F-04"),
				new Stand().withName("האגודה לידידות יפן").withType(Stand.StandType.OTHER).withLocationName("F-02-03"),
				new Stand().withName("BrAND Musical - דוכן תרומות").withType(Stand.StandType.DONATION).withLocationName("F-09-10"),
				new Stand().withName("החוג ללימודי אסיה באוניברסיטת חיפה").withType(Stand.StandType.OTHER).withLocationName("F-01")
		);
	}
}

package amai.org.conventions.model.conventions;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import amai.org.conventions.R;
import amai.org.conventions.model.ConventionMap;
import amai.org.conventions.model.ImageIdToImageResourceMapper;
import amai.org.conventions.model.Floor;
import amai.org.conventions.model.Hall;
import amai.org.conventions.model.MapLocation;
import amai.org.conventions.model.Place;
import amai.org.conventions.model.Stand;
import amai.org.conventions.model.StandsArea;
import amai.org.conventions.utils.CollectionUtils;
import amai.org.conventions.utils.ConventionStorage;

public class Harucon2017Convention extends AmaiConvention {
	// Hall names
	private static final String MAIN_HALL_NAME = "אולם ראשי";
	private static final String SCHWARTZ_NAME = "אודיטוריום שוורץ";
	private static final String ESHKOL1_NAME = "אשכול 1";
	private static final String ESHKOL2_NAME = "אשכול 2";
	private static final String ESHKOL3_NAME = "אשכול 3";
	private static final String GAMES_NAME = "משחקיה";
	private static final String SIGNING_NAME = "אזור החתמות";

	@Override
	protected ConventionStorage initStorage() {
		return new ConventionStorage(this, R.raw.harucon2017_convention_events, 0);
	}

	@Override
	protected Calendar initDate() {
		Calendar date = Calendar.getInstance();
		date.clear();
		date.set(2017, Calendar.MARCH, 12);
		return date;
	}

	@Override
	protected String initID() {
		return "Harucon2017";
	}

	@Override
	protected String initDisplayName() {
		return "הארוקון 2017";
	}

	@Override
	protected String initFacebookFeedPath() {
		return "/harucon.org.il/posts";
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
		return "content@harucon.org.il";
	}

	@Override
	protected URL initModelURL() {
		try {
			return new URL("http://2017.harucon.org.il/wp-admin/admin-ajax.php?action=get_event_list");
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	protected ImageIdToImageResourceMapper initImageMapper() {
		ImageIdToImageResourceMapper imageMapper = new ImageIdToImageResourceMapper();

		imageMapper.addMapping("http://2017.harucon.org.il/wp-content/uploads/sites/11/2017/02/מנגה-קפה-חברתי.jpg", R.drawable.event_manga_cafe);
		imageMapper.addMapping("http://2017.harucon.org.il/wp-content/uploads/sites/11/2016/06/אומנויות-לחימה.jpg", R.drawable.event_martial_arts);
		imageMapper.addMapping("http://2017.harucon.org.il/wp-content/uploads/sites/11/2016/06/קריסטינה.jpg", R.drawable.event_small_details);
		imageMapper.addMapping("http://2017.harucon.org.il/wp-content/uploads/sites/11/2016/06/אובנטו-יפני-טעים-קאוואי-וקל-להכנה-הרצאה-מעשית-לאוטאקו-הרעב.jpg", R.drawable.event_obento);
		imageMapper.addMapping("http://2017.harucon.org.il/wp-content/uploads/sites/11/2016/06/kuroshitsuji.jpg", R.drawable.event_kuroshitsuji);
		imageMapper.addMapping("http://2017.harucon.org.il/wp-content/uploads/sites/11/2016/06/סטודיו-טריגר.jpg", R.drawable.event_studio_trigger);
		imageMapper.addMapping("http://2017.harucon.org.il/wp-content/uploads/sites/11/2016/06/WCS.png", R.drawable.event_wcs);
		imageMapper.addMapping("http://2017.harucon.org.il/wp-content/uploads/sites/11/2016/06/דוגינשי.jpg", R.drawable.event_doujinshi);
		imageMapper.addMapping("http://2017.harucon.org.il/wp-content/uploads/sites/11/2016/06/Revolutionary-Girl-Utnea.png", R.drawable.event_utena);
		imageMapper.addMapping("http://2017.harucon.org.il/wp-content/uploads/sites/11/2016/06/16651726_10206778877070918_1169167867_n.png", R.drawable.event_harucomedy_central);
		imageMapper.addMapping("http://2017.harucon.org.il/wp-content/uploads/sites/11/2016/06/פיינל-פנטזי.jpg", R.drawable.event_final_fantasy);
		imageMapper.addMapping("http://2017.harucon.org.il/wp-content/uploads/sites/11/2016/06/מה-אתם-הייתם-עושים-שחף-עדן.jpg", R.drawable.event_what_would_you_do);
		imageMapper.addMapping("http://2017.harucon.org.il/wp-content/uploads/sites/11/2016/06/Cover_Image_GITS_Manga.png", R.drawable.event_cyberpunk);
		imageMapper.addMapping("http://2017.harucon.org.il/wp-content/uploads/sites/11/2016/06/מאגדה-לאנימה.png", R.drawable.event_legends);
		imageMapper.addMapping("http://2017.harucon.org.il/wp-content/uploads/sites/11/2016/06/בין-הראש-לדף.png", R.drawable.event_head_paper);
		imageMapper.addMapping("http://2017.harucon.org.il/wp-content/uploads/sites/11/2016/06/יוני-ההרפתקה-המבעיתה-של-גוגו.png", R.drawable.event_jojo);
		imageMapper.addMapping("http://2017.harucon.org.il/wp-content/uploads/sites/11/2016/06/טכניקות-קומיקס.jpg", R.drawable.event_comics);
		imageMapper.addMapping("http://2017.harucon.org.il/wp-content/uploads/sites/11/2016/06/מסע-בזמן.png", R.drawable.event_time_travel);
		imageMapper.addMapping("http://2017.harucon.org.il/wp-content/uploads/sites/11/2016/06/2D-הסרט.jpg", R.drawable.event_2d_movie);
		imageMapper.addMapping("http://2017.harucon.org.il/wp-content/uploads/sites/11/2016/06/14195417_524560747755018_332506590212759241_o.jpg", R.drawable.event_taiko_life);

		// Non-URL IDs
		imageMapper.addMapping(ImageIdToImageResourceMapper.EVENT_GENERIC, R.drawable.harucon2017_event_default_image);

		// Excluded IDs - mostly for debug purposes (don't show messages about these when entering an event that has them)
		// Not displayed due to bad quality
		imageMapper.addExcludedId("http://2017.harucon.org.il/wp-content/uploads/sites/11/2016/06/11219643_10207384743566314_4164409570104231506_n.jpg");
		// It's a logo, it doesn't fit in the background
		imageMapper.addExcludedId("http://2017.harucon.org.il/wp-content/uploads/sites/11/2017/02/EOJ_Logo_LONG.png");
		// Cosplay event judges
		imageMapper.addExcludedId("http://2017.harucon.org.il/wp-content/uploads/sites/11/2016/06/DafnaShaulson-150x150.jpg");
		imageMapper.addExcludedId("http://2017.harucon.org.il/wp-content/uploads/sites/11/2016/06/ofirKertes-150x150.jpg");
		imageMapper.addExcludedId("http://2017.harucon.org.il/wp-content/uploads/sites/11/2016/06/oded-150x150.jpg");
		imageMapper.addExcludedId("http://2017.harucon.org.il/wp-content/uploads/sites/11/2016/06/netta-150x150.jpg");
		imageMapper.addExcludedId("http://2017.harucon.org.il/wp-content/uploads/sites/11/2016/06/shachar-1-150x150.jpg");
		imageMapper.addExcludedId("http://2017.harucon.org.il/wp-content/uploads/sites/11/2016/07/yaelgeller-150x150.jpg");
		imageMapper.addExcludedId("http://2017.harucon.org.il/wp-content/uploads/sites/11/2016/07/nimrodgold-150x150.jpg");

		return imageMapper;
	}

	@Override
	protected List<Hall> initHalls() {
		Hall mainHall = new Hall().withName(MAIN_HALL_NAME).withOrder(1);
		Hall auditorium = new Hall().withName(SCHWARTZ_NAME).withOrder(2);
		Hall eshkol1 = new Hall().withName(ESHKOL1_NAME).withOrder(3);
		Hall eshkol2 = new Hall().withName(ESHKOL2_NAME).withOrder(4);
		Hall eshkol3 = new Hall().withName(ESHKOL3_NAME).withOrder(5);
		Hall games = new Hall().withName(GAMES_NAME).withOrder(6);
		Hall signing = new Hall().withName(SIGNING_NAME).withOrder(7);

		return Arrays.asList(mainHall, auditorium, eshkol1, eshkol2, eshkol3, games, signing);
	}

	@Override
	protected ConventionMap initMap() {
		Hall mainHall = findHallByName(MAIN_HALL_NAME);
		Hall schwatrz = findHallByName(SCHWARTZ_NAME);
		Hall eshkol1 = findHallByName(ESHKOL1_NAME);
		Hall eshkol2 = findHallByName(ESHKOL2_NAME);
		Hall eshkol3 = findHallByName(ESHKOL3_NAME);
		Hall games = findHallByName(GAMES_NAME);
		Hall signing = findHallByName(SIGNING_NAME);

		Floor floor1 = new Floor(1)
				.withName("מפלס תחתון")
				.withImageResource(R.raw.harucon2017_floor1, true)
				.withImageWidth(1224.31995f)
				.withImageHeight(626.5155f);
		Floor floor2 = new Floor(2)
				.withName("מפלס עליון")
				.withImageResource(R.raw.harucon2017_floor2, true)
				.withImageWidth(1382.21216f)
				.withImageHeight(780.93359f);

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
												.withMarkerResource(R.raw.harucon2017_marker_toilet, true)
												.withSelectedMarkerResource(R.raw.harucon2017_marker_toilet_selected, true)
												.withMarkerHeight(73.2f)
												.withX(1132.71f)
												.withY(405.1f),
										new MapLocation() // This is before the guest sign post so it will be selected as the stands area
												.withName("מודיעין ודוכן אמא\"י")
												.withPlace(nesher)
												.withMarkerResource(R.raw.harucon2017_marker_information, true)
												.withSelectedMarkerResource(R.raw.harucon2017_marker_information_selected, true)
												.withMarkerHeight(108.6f)
												.withX(552.12f)
												.withY(241.39f),
										new MapLocation()
												.withPlace(signing)
												.withName("אזור החתמות")
												.withMarkerResource(R.raw.harucon2017_marker_signing, true)
												.withSelectedMarkerResource(R.raw.harucon2017_marker_signing_selected, true)
												.withMarkerHeight(84.9f)
												.withX(909.42f)
												.withY(339.51f),
										new MapLocation()
												.withName("מתחם דוכנים")
												.withPlace(pinkus)
												.withMarkerResource(R.raw.harucon2017_marker_stands_pink, true)
												.withSelectedMarkerResource(R.raw.harucon2017_marker_stands_pink_selected, true)
												.withMarkerHeight(71.1f)
												.withX(779.92f)
												.withY(426.69f),
										new MapLocation()
												.withPlace(new Place().withName("שמירת חפצים"))
												.withMarkerResource(R.raw.harucon2017_marker_storage, true)
												.withSelectedMarkerResource(R.raw.harucon2017_marker_storage_selected, true)
												.withMarkerHeight(88.4f)
												.withX(695.22f)
												.withY(157.41f),
										new MapLocation()
												.withPlace(new Place().withName("שירותים"))
												.withMarkerResource(R.raw.harucon2017_marker_toilet, true)
												.withSelectedMarkerResource(R.raw.harucon2017_marker_toilet_selected, true)
												.withMarkerHeight(73.2f)
												.withX(667.23f)
												.withY(79.01f),
										new MapLocation()
												.withPlace(eshkol1)
												.withMarkerResource(R.raw.harucon2017_marker_eshkol1, true)
												.withSelectedMarkerResource(R.raw.harucon2017_marker_eshkol1_selected, true)
												.withMarkerHeight(88f)
												.withX(317.81f)
												.withY(389.2f),
										new MapLocation()
												.withPlace(schwatrz)
												.withMarkerResource(R.raw.harucon2017_marker_schwartz, true)
												.withSelectedMarkerResource(R.raw.harucon2017_marker_schwartz_selected, true)
												.withMarkerHeight(85.9f)
												.withX(431.62f)
												.withY(355.62f),
										new MapLocation()
												.withPlace(eshkol3)
												.withMarkerResource(R.raw.harucon2017_marker_eshkol3, true)
												.withSelectedMarkerResource(R.raw.harucon2017_marker_eshkol3_selected, true)
												.withMarkerHeight(88.6f)
												.withX(225.52f)
												.withY(527.91f),
										new MapLocation()
												.withPlace(eshkol2)
												.withMarkerResource(R.raw.harucon2017_marker_eshkol2, true)
												.withSelectedMarkerResource(R.raw.harucon2017_marker_eshkol2_selected, true)
												.withMarkerHeight(88.6f)
												.withX(349.92f)
												.withY(527.91f),
										new MapLocation()
												.withPlace(new Place().withName("שירותים"))
												.withMarkerResource(R.raw.harucon2017_marker_toilet, true)
												.withSelectedMarkerResource(R.raw.harucon2017_marker_toilet_selected, true)
												.withMarkerHeight(73.1f)
												.withX(176.72f)
												.withY(322.41f)),
								inFloor(floor2,
										new MapLocation()
												.withPlace(new Place().withName("שיפוט קוספליי"))
												.withMarkerResource(R.raw.harucon2017_marker_cosplay_judgement, true)
												.withSelectedMarkerResource(R.raw.harucon2017_marker_cosplay_judgement_selected, true)
												.withMarkerHeight(93.6f)
												.withX(1070.19f)
												.withY(599.92f),
										new MapLocation()
												.withPlace(mainHall)
												.withMarkerResource(R.raw.harucon2017_marker_main_hall, true)
												.withSelectedMarkerResource(R.raw.harucon2017_marker_main_hall_selected, true)
												.withMarkerHeight(142.303f)
												.withX(866.19f)
												.withY(429.06f),
										new MapLocation()
												.withPlace(new Place().withName("שירותים"))
												.withMarkerResource(R.raw.harucon2017_marker_toilet, true)
												.withSelectedMarkerResource(R.raw.harucon2017_marker_toilet_selected, true)
												.withMarkerHeight(73.2f)
												.withX(875.59f)
												.withY(125.93f),
										new MapLocation()
												.withPlace(new Place().withName("מתחם קוספליי"))
												.withMarkerResource(R.raw.harucon2017_marker_cosplay_area, true)
												.withSelectedMarkerResource(R.raw.harucon2017_marker_cosplay_area_selected, true)
												.withMarkerHeight(180.5f)
												.withX(718.07f)
												.withY(590.39f),
										new MapLocation()
												.withPlace(games)
												.withName("משחקייה")
												.withMarkerResource(R.raw.harucon2017_marker_games, true)
												.withSelectedMarkerResource(R.raw.harucon2017_marker_games_selected, true)
												.withMarkerHeight(93.7f)
												.withX(516.5f)
												.withY(528.43f),
										new MapLocation()
												.withName("סמטת האמנים")
												.withPlace(agam)
												.withMarkerResource(R.raw.harucon2017_marker_artist_alley, true)
												.withSelectedMarkerResource(R.raw.harucon2017_marker_artist_alley_selected, true)
												.withMarkerHeight(79.3f)
												.withX(516.6f)
												.withY(288.43f),
										new MapLocation()
												.withPlace(new Place().withName("שירותים"))
												.withMarkerResource(R.raw.harucon2017_marker_toilet, true)
												.withSelectedMarkerResource(R.raw.harucon2017_marker_toilet_selected, true)
												.withMarkerHeight(73.2f)
												.withX(181.77f)
												.withY(472.02f))
						)
				);
	}

	private List<Stand> getPinkusStands() {
		return Arrays.asList(
//				new Stand().withName("אוטאקו שופ").withType(Stand.StandType.COMMERCIAL).withLocationName("c-01-04"),
//				new Stand().withName("RETRO GAME CENTER").withType(Stand.StandType.COMMERCIAL).withLocationName("c-05-08"),
//				new Stand().withName("נקסוס").withType(Stand.StandType.COMMERCIAL).withLocationName("c-09-12"),
//				new Stand().withName("EDHD☆shirt").withType(Stand.StandType.INDEPENDENT).withLocationName("c-13"),
//				new Stand().withName("קריספי סאב\\Crispy-Sub!").withType(Stand.StandType.DONATION).withLocationName("c-14"),
//				new Stand().withName("Candy Lenses").withType(Stand.StandType.COMMERCIAL).withLocationName("c-15-20"),
//				new Stand().withName("Gamer").withType(Stand.StandType.COMMERCIAL).withLocationName("c-21"),
//				new Stand().withName("SVAG").withType(Stand.StandType.COMMERCIAL).withLocationName("c-23-24"),
//				new Stand().withName("PowerfulMerch").withType(Stand.StandType.COMMERCIAL).withLocationName("c-25-26"),
//				new Stand().withName("הדוכן של אור").withType(Stand.StandType.INDEPENDENT).withLocationName("c-27-28"),
//				new Stand().withName("Cosplay Senpai").withType(Stand.StandType.COMMERCIAL).withLocationName("c-29-30"),
//				new Stand().withName("אנימה סטור").withType(Stand.StandType.COMMERCIAL).withLocationName("c-31-36"),
//				new Stand().withName("הקובייה משחקים בע\"מ").withType(Stand.StandType.COMMERCIAL).withLocationName("c-37-38"),
//				new Stand().withName("Uranophobia ccg").withType(Stand.StandType.COMMERCIAL).withLocationName("c-39-41"),
//				new Stand().withName("Worbla").withType(Stand.StandType.COMMERCIAL).withLocationName("c-42"),
//				new Stand().withName("Gaming Land").withType(Stand.StandType.COMMERCIAL).withLocationName("c-43-48"),
//				new Stand().withName("המרכז ללימודי יפנית").withType(Stand.StandType.OTHER).withLocationName("c-49-50"),
//				new Stand().withName("waterdew").withType(Stand.StandType.INDEPENDENT).withLocationName("d-01-02"),
//				new Stand().withName("הגלריה של איתי").withType(Stand.StandType.INDEPENDENT).withLocationName("d-03"),
//				new Stand().withName("Beadesign").withType(Stand.StandType.INDEPENDENT).withLocationName("d-04"),
//				new Stand().withName("מושיק גולסט").withType(Stand.StandType.INDEPENDENT).withLocationName("d-05-06"),
//				new Stand().withName("Shironeko Pony").withType(Stand.StandType.INDEPENDENT).withLocationName("d-07-08"),
//				new Stand().withName("DraMagic").withType(Stand.StandType.DONATION).withLocationName("d-10"),
//				new Stand().withName("Dor's Designs").withType(Stand.StandType.INDEPENDENT).withLocationName("d-11-12"),
//				new Stand().withName("הדוכן המדהים של שלישית הקסם").withType(Stand.StandType.COMMERCIAL).withLocationName("d-13-14"),
//				new Stand().withName("Roza's Fluffy Stuff").withType(Stand.StandType.INDEPENDENT).withLocationName("d-15-16"),
//				new Stand().withName("Dark vibes").withType(Stand.StandType.INDEPENDENT).withLocationName("d-17-18"),
//				new Stand().withName("Amelia hats").withType(Stand.StandType.INDEPENDENT).withLocationName("d-19"),
//				new Stand().withName("כריות פנדומים").withType(Stand.StandType.INDEPENDENT).withLocationName("d-20-21"),
//				new Stand().withName("Japaneasy").withType(Stand.StandType.INDEPENDENT).withLocationName("d-22"),
//				new Stand().withName("velvet octopus").withType(Stand.StandType.INDEPENDENT).withLocationName("d-23-24"),
//				new Stand().withName("Rivendell").withType(Stand.StandType.INDEPENDENT).withLocationName("e-01"),
//				new Stand().withName("Compoco").withType(Stand.StandType.INDEPENDENT).withLocationName("e-02-03"),
//				new Stand().withName("לא אנושיט - Low Eno Shit").withType(Stand.StandType.INDEPENDENT).withLocationName("e-05"),
//				new Stand().withName("נרדגזם/גיק בסטה").withType(Stand.StandType.COMMERCIAL).withLocationName("e-06"),
//				new Stand().withName("Crow's Treasure").withType(Stand.StandType.INDEPENDENT).withLocationName("e-07-08"),
//				new Stand().withName("סרוגי").withType(Stand.StandType.INDEPENDENT).withLocationName("e-09-10"),
//				new Stand().withName("KawaiiStickers").withType(Stand.StandType.INDEPENDENT).withLocationName("e-11-12"),
//				new Stand().withName("בועת מחשבה - מגזין אנימה, מנגה ותרבות האוטקו").withType(Stand.StandType.INDEPENDENT).withLocationName("e-13"),
//				new Stand().withName("הדוכן של לי").withType(Stand.StandType.COMMERCIAL).withLocationName("e-14"),
//				new Stand().withName("Hatz, lolita & more").withType(Stand.StandType.INDEPENDENT).withLocationName("e-15"),
//				new Stand().withName("בתוך הקופסה").withType(Stand.StandType.COMMERCIAL).withLocationName("e-16-17"),
//				new Stand().withName("לימור שטרן תכשיטים").withType(Stand.StandType.COMMERCIAL).withLocationName("e-18-20"),
//				new Stand().withName("אמיגורמי").withType(Stand.StandType.INDEPENDENT).withLocationName("e-21"),
//				new Stand().withName("כובעי עינב").withType(Stand.StandType.INDEPENDENT).withLocationName("e-22-23")
		);
	}

	private List<Stand> getAgamStands() {
		return Arrays.asList(
//				new Stand().withName("נגה- ציורים ואומנות").withType(Stand.StandType.INDEPENDENT).withLocationName("b-20-21"),
//				new Stand().withName("הדוכן של פיצה").withType(Stand.StandType.INDEPENDENT).withLocationName("b-22-23"),
//				new Stand().withName("הדוכן של בר").withType(Stand.StandType.INDEPENDENT).withLocationName("b-24"),
//				new Stand().withName("dor20 studios").withType(Stand.StandType.INDEPENDENT).withLocationName("b-25-26"),
//				new Stand().withName("היקום המקביל").withType(Stand.StandType.INDEPENDENT).withLocationName("b-27"),
//				new Stand().withName("Aniart4u").withType(Stand.StandType.INDEPENDENT).withLocationName("b-28"),
//				new Stand().withName("קבוצת יצירת קומיקס").withType(Stand.StandType.INDEPENDENT).withLocationName("b-29-30"),
//				new Stand().withName("פוגי קומיקס").withType(Stand.StandType.INDEPENDENT).withLocationName("b-01"),
//				new Stand().withName("Anime Fanarts").withType(Stand.StandType.INDEPENDENT).withLocationName("b-11"),
//				new Stand().withName("ציורים ופיצ'פקס").withType(Stand.StandType.INDEPENDENT).withLocationName("b-12"),
//				new Stand().withName("Ella's Art").withType(Stand.StandType.INDEPENDENT).withLocationName("b-13"),
//				new Stand().withName("Rinska's Booth").withType(Stand.StandType.INDEPENDENT).withLocationName("b-14-15"),
//				new Stand().withName("fishiebug").withType(Stand.StandType.INDEPENDENT).withLocationName("b-16-17"),
//				new Stand().withName("Grisim & Mirrorshards").withType(Stand.StandType.INDEPENDENT).withLocationName("b-18-19"),
//				new Stand().withName("הגלקסיה").withType(Stand.StandType.COMMERCIAL).withLocationName("a-21-24"),
//				new Stand().withName("קומיקאזה 2.0").withType(Stand.StandType.COMMERCIAL).withLocationName("a-27-30"),
//				new Stand().withName("Panda shop").withType(Stand.StandType.COMMERCIAL).withLocationName("a-31-33"),
//				new Stand().withName("אנימה ווייב").withType(Stand.StandType.COMMERCIAL).withLocationName("a-33-38"),
//				new Stand().withName("Halo's Art").withType(Stand.StandType.COMMERCIAL).withLocationName("a-39-40"),
//				new Stand().withName("Anime Chudoku").withType(Stand.StandType.COMMERCIAL).withLocationName("a-41-42"),
//				new Stand().withName("מאי ארט").withType(Stand.StandType.COMMERCIAL).withLocationName("a-01-04"),
//				new Stand().withName("אנימו ומנגו").withType(Stand.StandType.COMMERCIAL).withLocationName("a-05-06"),
//				new Stand().withName("GoZgi").withType(Stand.StandType.COMMERCIAL).withLocationName("a-07-08"),
//				new Stand().withName("הממלכה").withType(Stand.StandType.COMMERCIAL).withLocationName("a-09"),
//				new Stand().withName("קומיקום").withType(Stand.StandType.COMMERCIAL).withLocationName("a-10"),
//				new Stand().withName("קומיקס וירקות").withType(Stand.StandType.COMMERCIAL).withLocationName("a-11-14"),
//				new Stand().withName("שיפודן ישראל").withType(Stand.StandType.COMMERCIAL).withLocationName("a-15-20")
		);
	}

	private List<Stand> getNesherStands() {
		return Arrays.asList(
//				new Stand().withName("מודיעין ודוכן אמא\"י").withType(Stand.StandType.OTHER),
//				new Stand().withName("דוכן אורחת הכנס - איימי בלאקשלגר").withType(Stand.StandType.OTHER).withLocationName("F-11-12"),
//				new Stand().withName("שגרירות יפן").withType(Stand.StandType.OTHER).withLocationName("F-07-08"),
//				new Stand().withName("קוספליי למען מטרה").withType(Stand.StandType.DONATION).withLocationName("F-05"),
//				new Stand().withName("תא ניפון באוניברסיטה העברית").withType(Stand.StandType.OTHER).withLocationName("F-04"),
//				new Stand().withName("האגודה לידידות יפן").withType(Stand.StandType.OTHER).withLocationName("F-02-03"),
//				new Stand().withName("BrAND Musical - דוכן תרומות").withType(Stand.StandType.DONATION).withLocationName("F-09-10"),
//				new Stand().withName("החוג ללימודי אסיה באוניברסיטת חיפה").withType(Stand.StandType.OTHER).withLocationName("F-01")
		);
	}
}

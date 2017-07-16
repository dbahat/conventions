package amai.org.conventions.model.conventions;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import amai.org.conventions.R;
import amai.org.conventions.feedback.forms.EventFeedbackForm;
import amai.org.conventions.feedback.forms.FeedbackForm;
import amai.org.conventions.model.ConventionMap;
import amai.org.conventions.model.FeedbackQuestion;
import amai.org.conventions.model.Floor;
import amai.org.conventions.model.Hall;
import amai.org.conventions.model.Halls;
import amai.org.conventions.model.ImageIdToImageResourceMapper;
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

	protected Harucon2017Convention() {
		super(new Halls(Arrays.asList(
				new Hall().withName(MAIN_HALL_NAME).withOrder(1),
				new Hall().withName(SCHWARTZ_NAME).withOrder(2),
				new Hall().withName(ESHKOL1_NAME).withOrder(3),
				new Hall().withName(ESHKOL2_NAME).withOrder(4),
				new Hall().withName(ESHKOL3_NAME).withOrder(5),
				new Hall().withName(GAMES_NAME).withOrder(6),
				new Hall().withName(SIGNING_NAME).withOrder(7)
		)));
	}

	@Override
	protected ConventionStorage initStorage() {
		return new ConventionStorage(this, R.raw.harucon2017_convention_events, 1);
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
		return null;
	}

	@Override
	protected EventFeedbackForm initEventFeedbackForm() {
		EventFeedbackForm eventFeedbackForm = null;
		try {
			eventFeedbackForm = (EventFeedbackForm) new EventFeedbackForm()
                    .withEventTitleEntry("entry.1847107867")
                    .withEventTimeEntry("entry.1648362575")
                    .withHallEntry("entry.1510105148")
                    .withConventionNameEntry("entry.1882876736")
                    .withDeviceIdEntry("entry.312890800")
                    .withTestEntry("entry.791883029")
					.withQuestionEntry(FeedbackQuestion.QUESTION_ID_ENJOYMENT, "entry.415572741")
					.withQuestionEntry(FeedbackQuestion.QUESTION_ID_LECTURER_QUALITY, "entry.1327236956")
					.withQuestionEntry(FeedbackQuestion.QUESTION_ID_SIMILAR_EVENTS, "entry.1416969956")
					.withQuestionEntry(FeedbackQuestion.QUESTION_ID_ADDITIONAL_INFO, "entry.1582215667")
                    .withSendUrl(new URL("https://docs.google.com/forms/d/e/1FAIpQLSf0qSN1DreR4h93k1QJWf_flL2LFrLCOvnp6HTZOvK_iLZHGA/formResponse"));
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
		return eventFeedbackForm;
	}

	@Override
	protected FeedbackForm initConventionFeedbackForm() {
		FeedbackForm feedbackForm = null;
		try {
			feedbackForm = new FeedbackForm()
					.withConventionNameEntry("entry.1882876736")
					.withDeviceIdEntry("entry.312890800")
					.withTestEntry("entry.791883029")
					.withQuestionEntry(FeedbackQuestion.QUESTION_ID_AGE, "entry.415572741")
					.withQuestionEntry(FeedbackQuestion.QUESTION_ID_LIKED, "entry.1327236956")
					.withQuestionEntry(FeedbackQuestion.QUESTION_ID_MAP_SIGNS, "entry.1416969956")
					.withQuestionEntry(FeedbackQuestion.QUESTION_ID_CONFLICTING_EVENTS, "entry.1582215667")
					.withQuestionEntry(FeedbackQuestion.QUESTION_ID_IMPROVEMENT, "entry.993320932")
					.withSendUrl(new URL("https://docs.google.com/forms/d/e/1FAIpQLSdadoDGXMriFVgW1Lki22OcrOGQoJVIlW8cU29DfkJRvAPWUQ/formResponse"));
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
		return feedbackForm;
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
	protected ConventionMap initMap() {
		Hall mainHall = getHalls().findByName(MAIN_HALL_NAME);
		Hall schwatrz = getHalls().findByName(SCHWARTZ_NAME);
		Hall eshkol1 = getHalls().findByName(ESHKOL1_NAME);
		Hall eshkol2 = getHalls().findByName(ESHKOL2_NAME);
		Hall eshkol3 = getHalls().findByName(ESHKOL3_NAME);
		Hall games = getHalls().findByName(GAMES_NAME);
		Hall signing = getHalls().findByName(SIGNING_NAME);

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

		StandsArea agam = new StandsArea().withName("טרקלין אגם").withStands(getAgamStands()).withImageResource(R.drawable.stands_agam).withImageWidth(2700).withImageHeight(1504);
		StandsArea pinkus = new StandsArea().withName("אולם פינקוס").withStands(getPinkusStands()).withImageResource(R.drawable.stands_pinkus).withImageWidth(2700).withImageHeight(1708);
		StandsArea nesher = new StandsArea().withName("רחבת הכניסה").withStands(getNesherStands()).withImageResource(R.drawable.stands_nesher).withImageWidth(2588).withImageHeight(1588);
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
				new Stand().withName("otaku shop").withType(Stand.StandType.REGULAR_STAND).withLocationName("c01-c04").withImageX(825).withImageY(535),
				new Stand().withName("retro game center").withType(Stand.StandType.REGULAR_STAND).withLocationName("c05-c08").withImageX(1016).withImageY(535),
				new Stand().withName("נקסוס").withType(Stand.StandType.REGULAR_STAND).withLocationName("c09-c11").withImageX(1306).withImageY(535),
				new Stand().withName("אוטאקו פרוג'קטו").withType(Stand.StandType.REGULAR_STAND).withLocationName("c12").withImageX(1401).withImageY(535),
				new Stand().withName("שרז עיצובים").withType(Stand.StandType.REGULAR_STAND).withLocationName("c13-c14").withImageX(1563).withImageY(535),
				new Stand().withName("קנדי לנסס").withType(Stand.StandType.REGULAR_STAND).withLocationName("c15-c20").withImageX(1752).withImageY(535),
				new Stand().withName("Animestuff").withType(Stand.StandType.REGULAR_STAND).withLocationName("c23").withImageX(2025).withImageY(708),
				new Stand().withName("קוספליי סנפאיי").withType(Stand.StandType.REGULAR_STAND).withLocationName("c24-c25").withImageX(2025).withImageY(803),
				new Stand().withName("סוואג").withType(Stand.StandType.REGULAR_STAND).withLocationName("c28-c31").withImageX(2025).withImageY(1107),
				new Stand().withName("BLUP").withType(Stand.StandType.REGULAR_STAND).withLocationName("c32").withImageX(2068).withImageY(1303),
				new Stand().withName("אנימה סטור").withType(Stand.StandType.REGULAR_STAND).withLocationName("c33-c38").withImageX(1959).withImageY(1428),
				new Stand().withName("הקוביה").withType(Stand.StandType.REGULAR_STAND).withLocationName("c39-c40").withImageX(1680).withImageY(1416),
				new Stand().withName("עדשות COLORVUE ועוד").withType(Stand.StandType.REGULAR_STAND).withLocationName("c41-c42").withImageX(1570).withImageY(1416),
				new Stand().withName("וורבלה").withType(Stand.StandType.REGULAR_STAND).withLocationName("c43-c44").withImageX(1470).withImageY(1416),
				new Stand().withName("gaming land").withType(Stand.StandType.REGULAR_STAND).withLocationName("c45-c50").withImageX(1087).withImageY(1428),
				new Stand().withName("BrandMusical").withType(Stand.StandType.REGULAR_STAND).withLocationName("c51-c52").withImageX(933).withImageY(1500),
				new Stand().withName("Geek n' Otaku").withType(Stand.StandType.REGULAR_STAND).withLocationName("d01-d02").withImageX(1427).withImageY(810),
				new Stand().withName("מאי שירי design&art").withType(Stand.StandType.REGULAR_STAND).withLocationName("d03").withImageX(1505).withImageY(810),
				new Stand().withName("דוכן תרומות עבור הפקת Nmusical").withType(Stand.StandType.REGULAR_STAND).withLocationName("d04").withImageX(1559).withImageY(810),
				new Stand().withName("Lynnja's").withType(Stand.StandType.REGULAR_STAND).withLocationName("d06").withImageX(1727).withImageY(810),
				new Stand().withName("D&M Armory").withType(Stand.StandType.REGULAR_STAND).withLocationName("d09").withImageX(1755).withImageY(979),
				new Stand().withName("ODA").withType(Stand.StandType.REGULAR_STAND).withLocationName("d11").withImageX(1756).withImageY(1099),
				new Stand().withName("מריונטה").withType(Stand.StandType.REGULAR_STAND).withLocationName("d15-d16").withImageX(1596).withImageY(1211),
				new Stand().withName("ויולה ויל").withType(Stand.StandType.REGULAR_STAND).withLocationName("d17-d18").withImageX(1489).withImageY(1211),
				new Stand().withName("המרכז ללימוד יפנית").withType(Stand.StandType.REGULAR_STAND).withLocationName("d19-d20").withImageX(1369).withImageY(1134),
				new Stand().withName("Velvet Octopus").withType(Stand.StandType.REGULAR_STAND).withLocationName("d23-d24").withImageX(1369).withImageY(885),
				new Stand().withName("rivendell").withType(Stand.StandType.REGULAR_STAND).withLocationName("e01").withImageX(829).withImageY(803),
				new Stand().withName("roza's art").withType(Stand.StandType.REGULAR_STAND).withLocationName("e02").withImageX(883).withImageY(803),
				new Stand().withName("Lee's Stand").withType(Stand.StandType.REGULAR_STAND).withLocationName("e03-e04").withImageX(963).withImageY(803),
				new Stand().withName("סרוגי").withType(Stand.StandType.REGULAR_STAND).withLocationName("e05").withImageX(1105).withImageY(812),
				new Stand().withName("dafna's nail art").withType(Stand.StandType.REGULAR_STAND).withLocationName("e06").withImageX(1158).withImageY(812),
				new Stand().withName("גיל והחציל המעופף").withType(Stand.StandType.REGULAR_STAND).withLocationName("e07").withImageX(1187).withImageY(858),
				new Stand().withName("Aurore22").withType(Stand.StandType.REGULAR_STAND).withLocationName("e08").withImageX(1187).withImageY(918),
				new Stand().withName("Compoco").withType(Stand.StandType.REGULAR_STAND).withLocationName("e09-e10").withImageX(1187).withImageY(1012),
				new Stand().withName("Crow's Treasure").withType(Stand.StandType.REGULAR_STAND).withLocationName("e11").withImageX(1187).withImageY(1104),
				new Stand().withName("Low.Eno.Shit לא אנושיט").withType(Stand.StandType.REGULAR_STAND).withLocationName("e12").withImageX(1187).withImageY(1169),
				new Stand().withName("בועת מחשבה").withType(Stand.StandType.REGULAR_STAND).withLocationName("e13").withImageX(1158).withImageY(1213),
				new Stand().withName("Hatz lolita & more").withType(Stand.StandType.REGULAR_STAND).withLocationName("e14").withImageX(1106).withImageY(1213),
				new Stand().withName("בתוך הקופסה").withType(Stand.StandType.REGULAR_STAND).withLocationName("e15-e16").withImageX(958).withImageY(1204),
				new Stand().withName("גיק בסטה").withType(Stand.StandType.REGULAR_STAND).withLocationName("e17").withImageX(884).withImageY(1204),
				new Stand().withName("Kawaii Stickers").withType(Stand.StandType.REGULAR_STAND).withLocationName("e18").withImageX(829).withImageY(1204),
				new Stand().withName("Harajuku Jewlery").withType(Stand.StandType.REGULAR_STAND).withLocationName("e19").withImageX(800).withImageY(1158),
				new Stand().withName("גיקפליז").withType(Stand.StandType.REGULAR_STAND).withLocationName("e20").withImageX(800).withImageY(1096),
				new Stand().withName("לימור שטרן").withType(Stand.StandType.REGULAR_STAND).withLocationName("e21-e24").withImageX(800).withImageY(941)
		);
	}

	private List<Stand> getAgamStands() {
		return Arrays.asList(
				new Stand().withName("Besandilove - להתלבש בתשוקה").withType(Stand.StandType.REGULAR_STAND).withLocationName("a01").withImageX(396).withImageY(441),
				new Stand().withName("גברת וודו").withType(Stand.StandType.REGULAR_STAND).withLocationName("a02").withImageX(445).withImageY(441),
				new Stand().withName("מאי ארט").withType(Stand.StandType.REGULAR_STAND).withLocationName("a05-a08").withImageX(669).withImageY(441),
				new Stand().withName("פנדה שופ").withType(Stand.StandType.REGULAR_STAND).withLocationName("a09-a10").withImageX(837).withImageY(441),
				new Stand().withName("קומיקס וירקות").withType(Stand.StandType.REGULAR_STAND).withLocationName("a11-a14").withImageX(986).withImageY(441),
				new Stand().withName("שיפודן ישראל").withType(Stand.StandType.REGULAR_STAND).withLocationName("a15-a20").withImageX(1258).withImageY(441),
				new Stand().withName("הגלקסיה").withType(Stand.StandType.REGULAR_STAND).withLocationName("a21-a24").withImageX(1530).withImageY(441),
				new Stand().withName("go-japan").withType(Stand.StandType.REGULAR_STAND).withLocationName("a25-a26").withImageX(1678).withImageY(441),
				new Stand().withName("קומיקאזה").withType(Stand.StandType.REGULAR_STAND).withLocationName("a27-a31").withImageX(1874).withImageY(441),
				new Stand().withName("קבוצת יצירת קומיקס").withType(Stand.StandType.REGULAR_STAND).withLocationName("a32").withImageX(2024).withImageY(441),
				new Stand().withName("animewave").withType(Stand.StandType.REGULAR_STAND).withLocationName("a33-a38").withImageX(2224).withImageY(441),
				new Stand().withName("אנימה צ'ודוקו").withType(Stand.StandType.REGULAR_STAND).withLocationName("a39-a42").withImageX(459).withImageY(837),
				new Stand().withName("Mirrorshards").withType(Stand.StandType.ARTIST_STAND).withLocationName("b01").withImageX(1013).withImageY(604),
				new Stand().withName("Grisim").withType(Stand.StandType.ARTIST_STAND).withLocationName("b02").withImageX(1073).withImageY(604),
				new Stand().withName("BL Palace").withType(Stand.StandType.ARTIST_STAND).withLocationName("b03").withImageX(1123).withImageY(604),
				new Stand().withName("דוכן ציור").withType(Stand.StandType.ARTIST_STAND).withLocationName("b04-b05").withImageX(1208).withImageY(604),
				new Stand().withName("Landes").withType(Stand.StandType.ARTIST_STAND).withLocationName("b10").withImageX(1564).withImageY(604),
				new Stand().withName("אביב ציפין קומיקס").withType(Stand.StandType.ARTIST_STAND).withLocationName("b11").withImageX(1619).withImageY(604),
				new Stand().withName("מושיק גולסט").withType(Stand.StandType.ARTIST_STAND).withLocationName("b12-b13").withImageX(1650).withImageY(685),
				new Stand().withName("Dor20 Studio").withType(Stand.StandType.ARTIST_STAND).withLocationName("b15").withImageX(1656).withImageY(877),
				new Stand().withName("adelistic").withType(Stand.StandType.ARTIST_STAND).withLocationName("b16").withImageX(1621).withImageY(925),
				new Stand().withName("YUEvander").withType(Stand.StandType.ARTIST_STAND).withLocationName("b18").withImageX(1512).withImageY(925),
				new Stand().withName("מרטין ציורים").withType(Stand.StandType.ARTIST_STAND).withLocationName("b19").withImageX(1453).withImageY(925),
				new Stand().withName("Yael's Colors").withType(Stand.StandType.ARTIST_STAND).withLocationName("b20").withImageX(1400).withImageY(925),
				new Stand().withName("היקום המקביל").withType(Stand.StandType.ARTIST_STAND).withLocationName("b22").withImageX(1238).withImageY(925),
				new Stand().withName("AniArt 4U").withType(Stand.StandType.ARTIST_STAND).withLocationName("b23").withImageX(1181).withImageY(925),
				new Stand().withName("Rinska's Booth").withType(Stand.StandType.ARTIST_STAND).withLocationName("b25-b26").withImageX(1043).withImageY(925),
				new Stand().withName("Fishibug").withType(Stand.StandType.ARTIST_STAND).withLocationName("b27-b28").withImageX(987).withImageY(843),
				new Stand().withName("הדוכן של פיצה").withType(Stand.StandType.ARTIST_STAND).withLocationName("b29").withImageX(987).withImageY(718),
				new Stand().withName("Tair Art").withType(Stand.StandType.ARTIST_STAND).withLocationName("b30").withImageX(987).withImageY(655)
		);
	}

	private List<Stand> getNesherStands() {
		return Arrays.asList(
				new Stand().withName("מודיעין ודוכן אמא\"י").withType(Stand.StandType.REGULAR_STAND).withImageX(1276).withImageY(948),
				new Stand().withName("שגרירות יפן").withType(Stand.StandType.REGULAR_STAND).withImageX(1112).withImageY(1000),
				new Stand().withName("ידידות יפן").withType(Stand.StandType.REGULAR_STAND).withImageX(878).withImageY(1000)
		);
	}
}

package amai.org.conventions.model.conventions;

import android.support.annotation.Nullable;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import amai.org.conventions.R;
import amai.org.conventions.feedback.SurveySender;
import amai.org.conventions.feedback.forms.EventFeedbackForm;
import amai.org.conventions.feedback.forms.EventVoteSurveyFormSender;
import amai.org.conventions.feedback.forms.FeedbackForm;
import amai.org.conventions.feedback.forms.SurveyForm;
import amai.org.conventions.model.ConventionEvent;
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
import amai.org.conventions.model.Survey;
import amai.org.conventions.networking.SurveyDataRetriever;
import amai.org.conventions.utils.CollectionUtils;
import amai.org.conventions.utils.ConventionStorage;

public class Harucon2018Convention extends AmaiConvention {
	// Hall names
	private static final String MAIN_HALL_NAME = "אולם ראשי";
	private static final String SCHWARTZ_NAME = "אודיטוריום שוורץ";
	private static final String ESHKOL1_NAME = "אשכול 1";
	private static final String ESHKOL2_NAME = "אשכול 2";
	private static final String ESHKOL3_NAME = "אשכול 3";
	private static final String WORKSHOPS_NAME = "חדר סדנאות";
	private static final String GAMES_NAME = "משחקיה";
	private static final String SIGNING_AREA_NAME = "אזור החתמות";

	// Vote questions - these values are serialized, don't change them!
	private static final int QUESTION_ID_COSPLAY_VOTE = 1002;
	private static final int QUESTION_ID_SHOWCASE_VOTE = 1004;

	// Special events server id
	private static final int EVENT_ID_SHOWCASE = 4612;
	private static final int EVENT_ID_COSPLAY = 4610;

	// Ids of google spreadsheets associated with the special events
	private static final String SHOWCASE_SPREADSHEET_ID = "1QRRw453cyzIDPgnFXah735J2BDd2GebThjCIlI6o5UM";
	private static final String COSPLAY_SPREADSHEET_ID = "1su0vTI4rvaN_B7cAgzME-5mfKKyHEyzCICPLHbNsGUM";

	static {
		FeedbackQuestion.addQuestion(QUESTION_ID_COSPLAY_VOTE, R.string.cosplay_vote_question);
		FeedbackQuestion.addQuestion(QUESTION_ID_SHOWCASE_VOTE, R.string.showcase_vote_question);
	}


	@Override
	protected ConventionStorage initStorage() {
		return new ConventionStorage(this, R.raw.harucon2018_convention_events, 1);
	}

	@Override
	protected Calendar initDate() {
		Calendar date = Calendar.getInstance();
		date.clear();
		date.set(2018, Calendar.MARCH, 1);
		return date;
	}

	@Override
	protected String initID() {
		return "Harucon2018";
	}

	@Override
	protected String initDisplayName() {
		return "הארוקון 2018";
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
                    .withSendUrl(new URL("https://docs.google.com/forms/d/e/1FAIpQLSev7UVt5A635xh9t_DnQ5tSIiCmbEwAPydz05xbvZn-7hGWwA/formResponse"));
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
		return eventFeedbackForm;
	}

	@Override
	protected FeedbackForm initConventionFeedbackForm() {
		FeedbackForm feedbackForm = null;
		try {
			feedbackForm = (FeedbackForm) new FeedbackForm()
					.withConventionNameEntry("entry.1882876736")
					.withDeviceIdEntry("entry.312890800")
					.withTestEntry("entry.791883029")
					.withQuestionEntry(FeedbackQuestion.QUESTION_ID_AGE, "entry.415572741")
					.withQuestionEntry(FeedbackQuestion.QUESTION_ID_LIKED, "entry.1327236956")
					.withQuestionEntry(FeedbackQuestion.QUESTION_ID_MAP_SIGNS, "entry.1416969956")
					.withQuestionEntry(FeedbackQuestion.QUESTION_ID_CONFLICTING_EVENTS, "entry.1582215667")
					.withQuestionEntry(FeedbackQuestion.QUESTION_ID_IMPROVEMENT, "entry.993320932")
					.withSendUrl(new URL("https://docs.google.com/forms/d/e/1FAIpQLSeWy8GieIo8YPKKz6MF3H63UMIhlN-XVc6myGN4Xk7leiVD2Q/formResponse"));
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
		return feedbackForm;
	}

	@Override
	protected URL initModelURL() {
		try {
			return new URL("http://2018.harucon.org.il/wp-admin/admin-ajax.php?action=get_event_list");
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	protected ImageIdToImageResourceMapper initImageMapper() {
		ImageIdToImageResourceMapper imageMapper = new ImageIdToImageResourceMapper();

		imageMapper.addMapping("http://2018.harucon.org.il/wp-content/uploads/sites/14/2018/01/משחקי-קצב-101-מDDR-עד-פופ_נ-מיוזיק.png", R.drawable.event_ddr);
		imageMapper.addMapping("http://2018.harucon.org.il/wp-content/uploads/sites/14/2018/01/אמנזיה-וזהות-זכרונות-בנובלות-ויזואליות-ומדיה-יפנית.jpg", R.drawable.event_visual_noval);
		imageMapper.addMapping("http://2018.harucon.org.il/wp-content/uploads/sites/14/2018/01/המדריך-לאספן-המנגה.jpg", R.drawable.event_manga_collector);
		imageMapper.addMapping("http://2018.harucon.org.il/wp-content/uploads/sites/14/2018/01/ניסיון-וויפוז.jpg", R.drawable.event_gatcha_games);
		imageMapper.addMapping("http://2018.harucon.org.il/wp-content/uploads/sites/14/2018/01/פיינל-פנטזי-15-החלקים-החסרים.jpg", R.drawable.event_ff15);
		imageMapper.addMapping("http://2018.harucon.org.il/wp-content/uploads/sites/14/2018/01/הסוכן-החשאי-של-יפן.jpg", R.drawable.event_detective);
		imageMapper.addMapping("http://2018.harucon.org.il/wp-content/uploads/sites/14/2018/01/קוואי-פמיניסטי.jpg", R.drawable.event_kawai);
		imageMapper.addMapping("http://2018.harucon.org.il/wp-content/uploads/sites/14/2018/01/היסטוריית-הגו-מאלף-ה-2-לפנה_ס-ועד-אלפא-גו.png", R.drawable.event_go_history);
		imageMapper.addMapping("http://2018.harucon.org.il/wp-content/uploads/sites/14/2018/01/גיבורי-על-כסף.jpg", R.drawable.event_money_heros);
		imageMapper.addMapping("http://2018.harucon.org.il/wp-content/uploads/sites/14/2018/01/אנימה-בזכוכית-מגדלת.jpg", R.drawable.event_anime_glass);
		imageMapper.addMapping("http://2018.harucon.org.il/wp-content/uploads/sites/14/2018/01/אנטומיה-במנגה-וקומיקס.jpg", R.drawable.event_manga_and_comics);
		imageMapper.addMapping("http://2018.harucon.org.il/wp-content/uploads/sites/14/2018/01/קוספליי-למתחילים-.jpeg", R.drawable.event_beginner_cosplay);
		imageMapper.addMapping("http://2018.harucon.org.il/wp-content/uploads/sites/14/2018/01/Southpark.jpg", R.drawable.event_southpark);
		imageMapper.addMapping("http://2018.harucon.org.il/wp-content/uploads/sites/14/2018/01/MATSURI-אמיתי-–-פסטיבלים-ביפן.jpg", R.drawable.event_matsuri);
		imageMapper.addMapping("http://2018.harucon.org.il/wp-content/uploads/sites/14/2018/01/איך-להתחיל-ולהישאר-בחיים-הדלת-אל-עולם-האנימה.jpg", R.drawable.event_anime_door);
		imageMapper.addMapping("http://2018.harucon.org.il/wp-content/uploads/sites/14/2018/01/kyotoAnimation.jpg", R.drawable.event_kyoto_animation);
		imageMapper.addMapping("http://2018.harucon.org.il/wp-content/uploads/sites/14/2018/01/מסע-האנטי-גיבור.png", R.drawable.event_anti_hero);
		imageMapper.addMapping("http://2018.harucon.org.il/wp-content/uploads/sites/14/2018/01/טורניר-פוקימון-לאפליקציה.jpg", R.drawable.event_pokemon);
		imageMapper.addMapping("http://2018.harucon.org.il/wp-content/uploads/sites/14/2018/01/מנגה-קפה-לאתר.jpg", R.drawable.event_manga_cafe);
		imageMapper.addMapping("http://2018.harucon.org.il/wp-content/uploads/sites/14/2018/02/סקיטים-לאתר-אפשרות-2.jpg", R.drawable.event_skits);
		imageMapper.addMapping("http://2018.harucon.org.il/wp-content/uploads/sites/14/2018/02/Death-in-anime-combined.png", R.drawable.event_death_in_anime);
		imageMapper.addMapping("http://2018.harucon.org.il/wp-content/uploads/sites/14/2018/01/מפאנסאב-אנלוגי-ועד-אנימה-בכבלים-סיפורה-של-ADV-ואנימה-במערב.png", R.drawable.event_fansub);
		imageMapper.addMapping("http://2018.harucon.org.il/wp-content/uploads/sites/14/2018/01/הצללות-בקוספליי-ואיך-לעשותן.jpg", R.drawable.event_cosplay_shadow);
		imageMapper.addMapping("http://2018.harucon.org.il/wp-content/uploads/sites/14/2018/01/אקאשי-מורקאמי-מקנייה-ועד-וורסאי.jpg", R.drawable.event_murakami);
		imageMapper.addMapping("http://2018.harucon.org.il/wp-content/uploads/sites/14/2018/01/אנימה-על-הספקטרום.jpg", R.drawable.event_anime_spectrum);
		imageMapper.addMapping("http://2018.harucon.org.il/wp-content/uploads/sites/14/2018/02/יפן-בראי-האנימה-לאתר-הכנס.jpg", R.drawable.event_anime_mirror_app);
		imageMapper.addMapping("http://2018.harucon.org.il/wp-content/uploads/sites/14/2018/01/שיעור-יפנית.jpg", R.drawable.event_japanease);
		imageMapper.addMapping("http://2018.harucon.org.il/wp-content/uploads/sites/14/2018/02/הדגמת-קנדאמה-Kendama.jpg", R.drawable.event_kendama);
		imageMapper.addMapping("http://2018.harucon.org.il/wp-content/uploads/sites/14/2018/02/יוקאי-לאתר.jpg", R.drawable.event_yokai);
		imageMapper.addMapping("http://2018.harucon.org.il/wp-content/uploads/sites/14/2018/02/מחזמר-תמונה-לאתר.jpg", R.drawable.event_musical);
		imageMapper.addMapping("http://2018.harucon.org.il/wp-content/uploads/sites/14/2018/02/At-Site-Code-Geass02.jpg", R.drawable.event_geass);
		imageMapper.addMapping("http://2018.harucon.org.il/wp-content/uploads/sites/14/2018/01/קוף-הדרך-המסע-למערב-בין-האנימה-לסין.jpg", R.drawable.event_goku);
		imageMapper.addMapping("http://2018.harucon.org.il/wp-content/uploads/sites/14/2018/01/Whose-Manga-is-it-ANYWAY.png", R.drawable.event_manga_anyway);
		imageMapper.addMapping("http://2018.harucon.org.il/wp-content/uploads/sites/14/2018/02/presontation-Yoshimi-Katahira-1.jpg", R.drawable.event_yoshimi_katahira);
		imageMapper.addMapping("http://2018.harucon.org.il/wp-content/uploads/sites/14/2018/02/16583_271.jpg", R.drawable.event_naginata);
		// Non-URL IDs
		imageMapper.addMapping(ImageIdToImageResourceMapper.EVENT_GENERIC, R.drawable.harucon2018_home_background);

		// Excluded IDs - mostly for debug purposes (don't show messages about these when entering an event that has them)
		// Foreground text is not readable

		imageMapper.addExcludedIds(
				// Cosplay / showcase judges
				"http://2018.harucon.org.il/wp-content/uploads/sites/14/2018/01/יעל-גלר.jpg",
				"http://2018.harucon.org.il/wp-content/uploads/sites/14/2018/01/אלי-פודוליאק.jpg",
				"http://2018.harucon.org.il/wp-content/uploads/sites/14/2018/02/IMG_9671.jpg",
				"http://2018.harucon.org.il/wp-content/uploads/sites/14/2018/02/MG_8954.jpg",
				"http://2018.harucon.org.il/wp-content/uploads/sites/14/2018/01/סער-שמר.jpg",
				"http://2018.harucon.org.il/wp-content/uploads/sites/14/2018/01/סיון-מגן.jpg",
				"http://2018.harucon.org.il/wp-content/uploads/sites/14/2018/01/נסטיה-סימנובסקי.png",
				"http://2018.harucon.org.il/wp-content/uploads/sites/14/2018/01/אופיר-לוטן.jpg",
				"http://2018.harucon.org.il/wp-content/uploads/sites/14/2018/01/נטע-וייס.png"
		);

		return imageMapper;
	}

	@Override
	protected Halls initHalls() {
		Hall mainHall = new Hall().withName(MAIN_HALL_NAME).withOrder(1);
		Hall auditorium = new Hall().withName(SCHWARTZ_NAME).withOrder(2);
		Hall eshkol1 = new Hall().withName(ESHKOL1_NAME).withOrder(3);
		Hall eshkol2 = new Hall().withName(ESHKOL2_NAME).withOrder(4);
		Hall eshkol3 = new Hall().withName(ESHKOL3_NAME).withOrder(5);
		Hall workshops = new Hall().withName(WORKSHOPS_NAME).withOrder(6);
		Hall games = new Hall().withName(GAMES_NAME).withOrder(7);
		Hall singingArea = new Hall().withName(SIGNING_AREA_NAME).withOrder(8);
		return new Halls(Arrays.asList(mainHall, auditorium, eshkol1, eshkol2, eshkol3, workshops, games, singingArea));
	}

	@Override
	protected ConventionMap initMap() {
		Hall mainHall = this.getHalls().findByName(MAIN_HALL_NAME);
		Hall schwatrz = this.getHalls().findByName(SCHWARTZ_NAME);
		Hall eshkol1 = this.getHalls().findByName(ESHKOL1_NAME);
		Hall eshkol2 = this.getHalls().findByName(ESHKOL2_NAME);
		Hall eshkol3 = this.getHalls().findByName(ESHKOL3_NAME);
		Hall workshops = this.getHalls().findByName(WORKSHOPS_NAME);
		Hall games = this.getHalls().findByName(GAMES_NAME);
		Hall singingArea = this.getHalls().findByName(SIGNING_AREA_NAME);

		Floor floor1 = new Floor(1)
				.withName("קומה 1")
				.withImageResource(R.raw.harucon2018_floor1, true)
				.withImageWidth(2230.06241f)
				.withImageHeight(1164.26335f);
		Floor floor2 = new Floor(2)
				.withName("קומה 2")
				.withImageResource(R.raw.harucon2018_floor2, true)
				.withImageWidth(2224.61499f)
				.withImageHeight(1478.37012f);

		StandsArea agam = new StandsArea().withName("טרקלין אגם").withStands(getAgamStands())/*.withImageResource(R.drawable.stands_agam).withImageWidth(2700).withImageHeight(1504)*/;
		StandsArea pinkus = new StandsArea().withName("אולם פינקוס").withStands(getPinkusStands())/*.withImageResource(R.drawable.stands_pinkus).withImageWidth(2700).withImageHeight(1708)*/;
		StandsArea nesher = new StandsArea().withName("רחבת הכניסה").withStands(getNesherStands())/*.withImageResource(R.drawable.stands_nesher).withImageWidth(2588).withImageHeight(1588)*/;
		return new ConventionMap()
				.withFloors(Arrays.asList(floor1, floor2))
				.withLocations(
						CollectionUtils.flattenList(
								inFloor(floor1,
										new MapLocation()
												.withPlace(new Place().withName("שירותים"))
												.withMarkerResource(R.raw.harucon2018_marker_toilet, true)
												.withSelectedMarkerResource(R.raw.harucon2018_marker_toilet_selected, true)
												.withMarkerHeight(133.4f)
												.withX(2065.696f)
												.withY(743.43f),
										new MapLocation() // This is before the guest sign post so it will be selected as the stands area
												.withName("מודיעין ודוכן אמא\"י")
												.withPlace(nesher)
												.withMarkerResource(R.raw.harucon2018_marker_information, true)
												.withSelectedMarkerResource(R.raw.harucon2018_marker_information_selected, true)
												.withMarkerHeight(197.7f)
												.withX(1008.296f)
												.withY(445.73f),
										new MapLocation()
												.withName("איזור החתמות")
												.withPlace(singingArea)
												.withMarkerResource(R.raw.harucon2018_marker_signing, true)
												.withSelectedMarkerResource(R.raw.harucon2018_marker_signing_selected, true)
												.withMarkerHeight(154.5f)
												.withX(1659.196f)
												.withY(624.43f),
										new MapLocation()
												.withName("מתחם דוכנים")
												.withPlace(pinkus)
												.withMarkerResource(R.raw.harucon2018_marker_stands, true)
												.withSelectedMarkerResource(R.raw.harucon2018_marker_stands_selected, true)
												.withMarkerHeight(129.6f)
												.withX(1423.196f)
												.withY(783.13f),
										new MapLocation()
												.withPlace(new Place().withName("שמירת חפצים"))
												.withMarkerResource(R.raw.harucon2018_marker_storage, true)
												.withSelectedMarkerResource(R.raw.harucon2018_marker_storage_selected, true)
												.withMarkerHeight(161f)
												.withX(1268.996f)
												.withY(292.73f),
										new MapLocation()
												.withPlace(new Place().withName("שירותים"))
												.withMarkerResource(R.raw.harucon2018_marker_toilet, true)
												.withSelectedMarkerResource(R.raw.harucon2018_marker_toilet_selected, true)
												.withMarkerHeight(133.4f)
												.withX(1217.996f)
												.withY(149.83f),
										new MapLocation()
												.withPlace(eshkol1)
												.withMarkerResource(R.raw.harucon2018_marker_eshkol1, true)
												.withSelectedMarkerResource(R.raw.harucon2018_marker_eshkol1_selected, true)
												.withMarkerHeight(160.1f)
												.withX(596.196f)
												.withY(715.03f),
										new MapLocation()
												.withPlace(schwatrz)
												.withMarkerResource(R.raw.harucon2018_marker_schwartz, true)
												.withSelectedMarkerResource(R.raw.harucon2018_marker_schwartz_selected, true)
												.withMarkerHeight(156.5f)
												.withX(789.096f)
												.withY(653.73f),
										new MapLocation()
												.withPlace(eshkol3)
												.withMarkerResource(R.raw.harucon2018_marker_eshkol3, true)
												.withSelectedMarkerResource(R.raw.harucon2018_marker_eshkol3_selected, true)
												.withMarkerHeight(161.3f)
												.withX(413.596f)
												.withY(967.53f),
										new MapLocation()
												.withPlace(eshkol2)
												.withMarkerResource(R.raw.harucon2018_marker_eshkol2, true)
												.withSelectedMarkerResource(R.raw.harucon2018_marker_eshkol2_selected, true)
												.withMarkerHeight(161.3f)
												.withX(640.146f)
												.withY(967.53f),
										new MapLocation()
												.withPlace(new Place().withName("שירותים"))
												.withMarkerResource(R.raw.harucon2018_marker_toilet, true)
												.withSelectedMarkerResource(R.raw.harucon2018_marker_toilet_selected, true)
												.withMarkerHeight(133.4f)
												.withX(324.796f)
												.withY(593.03f)),
								inFloor(floor2,
										new MapLocation()
												.withPlace(workshops)
												.withMarkerResource(R.raw.harucon2018_marker_workshops, true)
												.withSelectedMarkerResource(R.raw.harucon2018_marker_workshops_selected, true)
												.withMarkerHeight(162.3f)
												.withX(1846.75f)
												.withY(1306.07f),
										new MapLocation()
												.withPlace(new Place().withName("חדר בריחה"))
												.withMarkerResource(R.raw.harucon2018_marker_escape_room, true)
												.withSelectedMarkerResource(R.raw.harucon2018_marker_escape_room_selected, true)
												.withMarkerHeight(155f)
												.withX(1628.25f)
												.withY(1195.77f),
										new MapLocation()
												.withPlace(new Place().withName("שיפוט קוספליי"))
												.withMarkerResource(R.raw.harucon2018_marker_cosplay_judgement, true)
												.withSelectedMarkerResource(R.raw.harucon2018_marker_cosplay_judgement_selected, true)
												.withMarkerHeight(160.3f)
												.withX(1732.75f)
												.withY(993.17f),
										new MapLocation()
												.withPlace(mainHall)
												.withMarkerResource(R.raw.harucon2018_marker_main_hall, true)
												.withSelectedMarkerResource(R.raw.harucon2018_marker_main_hall_selected, true)
												.withMarkerHeight(243.707f)
												.withX(1383.65f)
												.withY(700.57f),
										new MapLocation()
												.withPlace(new Place().withName("שירותים"))
												.withMarkerResource(R.raw.harucon2018_marker_toilet, true)
												.withSelectedMarkerResource(R.raw.harucon2018_marker_toilet_selected, true)
												.withMarkerHeight(125.4f)
												.withX(1399.65f)
												.withY(181.57f),
										new MapLocation()
												.withPlace(new Place().withName("מתחם קוספליי"))
												.withMarkerResource(R.raw.harucon2018_marker_cosplay_area, true)
												.withSelectedMarkerResource(R.raw.harucon2018_marker_cosplay_area_selected, true)
												.withMarkerHeight(309.2f)
												.withX(1129.85f)
												.withY(976.87f),
										new MapLocation()
												.withPlace(games)
												.withName("משחקייה")
												.withMarkerResource(R.raw.harucon2018_marker_gaming, true)
												.withSelectedMarkerResource(R.raw.harucon2018_marker_gaming_selected, true)
												.withMarkerHeight(160.4f)
												.withX(784.65f)
												.withY(870.97f),
										new MapLocation()
												.withName("סמטת האמנים")
												.withPlace(agam)
												.withMarkerResource(R.raw.harucon2018_marker_artist_alley, true)
												.withSelectedMarkerResource(R.raw.harucon2018_marker_artist_alley_selected, true)
												.withMarkerHeight(135.7f)
												.withX(784.75f)
												.withY(459.97f),
										new MapLocation()
												.withPlace(new Place().withName("שירותים"))
												.withMarkerResource(R.raw.harucon2018_marker_toilet, true)
												.withSelectedMarkerResource(R.raw.harucon2018_marker_toilet_selected, true)
												.withMarkerHeight(125.4f)
												.withX(211.65f)
												.withY(774.27f))
						)
				);
	}

	private List<Stand> getPinkusStands() {
		// TODO update stands for harucon 2018
		return Arrays.asList(
//				new Stand().withName("otaku shop").withType(Stand.StandType.REGULAR_STAND).withLocationName("c01-c04").withImageX(825).withImageY(535),
//				new Stand().withName("retro game center").withType(Stand.StandType.REGULAR_STAND).withLocationName("c05-c08").withImageX(1016).withImageY(535),
//				new Stand().withName("נקסוס").withType(Stand.StandType.REGULAR_STAND).withLocationName("c09-c11").withImageX(1306).withImageY(535),
//				new Stand().withName("אוטאקו פרוג'קטו").withType(Stand.StandType.REGULAR_STAND).withLocationName("c12").withImageX(1401).withImageY(535),
//				new Stand().withName("שרז עיצובים").withType(Stand.StandType.REGULAR_STAND).withLocationName("c13-c14").withImageX(1563).withImageY(535),
//				new Stand().withName("קנדי לנסס").withType(Stand.StandType.REGULAR_STAND).withLocationName("c15-c20").withImageX(1752).withImageY(535),
//				new Stand().withName("Animestuff").withType(Stand.StandType.REGULAR_STAND).withLocationName("c23").withImageX(2025).withImageY(708),
//				new Stand().withName("קוספליי סנפאיי").withType(Stand.StandType.REGULAR_STAND).withLocationName("c24-c25").withImageX(2025).withImageY(803),
//				new Stand().withName("סוואג").withType(Stand.StandType.REGULAR_STAND).withLocationName("c28-c31").withImageX(2025).withImageY(1107),
//				new Stand().withName("BLUP").withType(Stand.StandType.REGULAR_STAND).withLocationName("c32").withImageX(2068).withImageY(1303),
//				new Stand().withName("אנימה סטור").withType(Stand.StandType.REGULAR_STAND).withLocationName("c33-c38").withImageX(1959).withImageY(1428),
//				new Stand().withName("הקוביה").withType(Stand.StandType.REGULAR_STAND).withLocationName("c39-c40").withImageX(1680).withImageY(1416),
//				new Stand().withName("עדשות COLORVUE ועוד").withType(Stand.StandType.REGULAR_STAND).withLocationName("c41-c42").withImageX(1570).withImageY(1416),
//				new Stand().withName("וורבלה").withType(Stand.StandType.REGULAR_STAND).withLocationName("c43-c44").withImageX(1470).withImageY(1416),
//				new Stand().withName("gaming land").withType(Stand.StandType.REGULAR_STAND).withLocationName("c45-c50").withImageX(1087).withImageY(1428),
//				new Stand().withName("BrandMusical").withType(Stand.StandType.REGULAR_STAND).withLocationName("c51-c52").withImageX(933).withImageY(1500),
//				new Stand().withName("Geek n' Otaku").withType(Stand.StandType.REGULAR_STAND).withLocationName("d01-d02").withImageX(1427).withImageY(810),
//				new Stand().withName("מאי שירי design&art").withType(Stand.StandType.REGULAR_STAND).withLocationName("d03").withImageX(1505).withImageY(810),
//				new Stand().withName("דוכן תרומות עבור הפקת Nmusical").withType(Stand.StandType.REGULAR_STAND).withLocationName("d04").withImageX(1559).withImageY(810),
//				new Stand().withName("Lynnja's").withType(Stand.StandType.REGULAR_STAND).withLocationName("d06").withImageX(1727).withImageY(810),
//				new Stand().withName("D&M Armory").withType(Stand.StandType.REGULAR_STAND).withLocationName("d09").withImageX(1755).withImageY(979),
//				new Stand().withName("ODA").withType(Stand.StandType.REGULAR_STAND).withLocationName("d11").withImageX(1756).withImageY(1099),
//				new Stand().withName("מריונטה").withType(Stand.StandType.REGULAR_STAND).withLocationName("d15-d16").withImageX(1596).withImageY(1211),
//				new Stand().withName("ויולה ויל").withType(Stand.StandType.REGULAR_STAND).withLocationName("d17-d18").withImageX(1489).withImageY(1211),
//				new Stand().withName("המרכז ללימוד יפנית").withType(Stand.StandType.REGULAR_STAND).withLocationName("d19-d20").withImageX(1369).withImageY(1134),
//				new Stand().withName("Velvet Octopus").withType(Stand.StandType.REGULAR_STAND).withLocationName("d23-d24").withImageX(1369).withImageY(885),
//				new Stand().withName("rivendell").withType(Stand.StandType.REGULAR_STAND).withLocationName("e01").withImageX(829).withImageY(803),
//				new Stand().withName("roza's art").withType(Stand.StandType.REGULAR_STAND).withLocationName("e02").withImageX(883).withImageY(803),
//				new Stand().withName("Lee's Stand").withType(Stand.StandType.REGULAR_STAND).withLocationName("e03-e04").withImageX(963).withImageY(803),
//				new Stand().withName("סרוגי").withType(Stand.StandType.REGULAR_STAND).withLocationName("e05").withImageX(1105).withImageY(812),
//				new Stand().withName("dafna's nail art").withType(Stand.StandType.REGULAR_STAND).withLocationName("e06").withImageX(1158).withImageY(812),
//				new Stand().withName("גיל והחציל המעופף").withType(Stand.StandType.REGULAR_STAND).withLocationName("e07").withImageX(1187).withImageY(858),
//				new Stand().withName("Aurore22").withType(Stand.StandType.REGULAR_STAND).withLocationName("e08").withImageX(1187).withImageY(918),
//				new Stand().withName("Compoco").withType(Stand.StandType.REGULAR_STAND).withLocationName("e09-e10").withImageX(1187).withImageY(1012),
//				new Stand().withName("Crow's Treasure").withType(Stand.StandType.REGULAR_STAND).withLocationName("e11").withImageX(1187).withImageY(1104),
//				new Stand().withName("Low.Eno.Shit לא אנושיט").withType(Stand.StandType.REGULAR_STAND).withLocationName("e12").withImageX(1187).withImageY(1169),
//				new Stand().withName("בועת מחשבה").withType(Stand.StandType.REGULAR_STAND).withLocationName("e13").withImageX(1158).withImageY(1213),
//				new Stand().withName("Hatz lolita & more").withType(Stand.StandType.REGULAR_STAND).withLocationName("e14").withImageX(1106).withImageY(1213),
//				new Stand().withName("בתוך הקופסה").withType(Stand.StandType.REGULAR_STAND).withLocationName("e15-e16").withImageX(958).withImageY(1204),
//				new Stand().withName("גיק בסטה").withType(Stand.StandType.REGULAR_STAND).withLocationName("e17").withImageX(884).withImageY(1204),
//				new Stand().withName("Kawaii Stickers").withType(Stand.StandType.REGULAR_STAND).withLocationName("e18").withImageX(829).withImageY(1204),
//				new Stand().withName("Harajuku Jewlery").withType(Stand.StandType.REGULAR_STAND).withLocationName("e19").withImageX(800).withImageY(1158),
//				new Stand().withName("גיקפליז").withType(Stand.StandType.REGULAR_STAND).withLocationName("e20").withImageX(800).withImageY(1096),
//				new Stand().withName("לימור שטרן").withType(Stand.StandType.REGULAR_STAND).withLocationName("e21-e24").withImageX(800).withImageY(941)
		);
	}

	private List<Stand> getAgamStands() {
		// TODO update stands for harucon 2018
		return Arrays.asList(
//				new Stand().withName("Besandilove - להתלבש בתשוקה").withType(Stand.StandType.REGULAR_STAND).withLocationName("a01").withImageX(396).withImageY(441),
//				new Stand().withName("גברת וודו").withType(Stand.StandType.REGULAR_STAND).withLocationName("a02").withImageX(445).withImageY(441),
//				new Stand().withName("מאי ארט").withType(Stand.StandType.REGULAR_STAND).withLocationName("a05-a08").withImageX(669).withImageY(441),
//				new Stand().withName("פנדה שופ").withType(Stand.StandType.REGULAR_STAND).withLocationName("a09-a10").withImageX(837).withImageY(441),
//				new Stand().withName("קומיקס וירקות").withType(Stand.StandType.REGULAR_STAND).withLocationName("a11-a14").withImageX(986).withImageY(441),
//				new Stand().withName("שיפודן ישראל").withType(Stand.StandType.REGULAR_STAND).withLocationName("a15-a20").withImageX(1258).withImageY(441),
//				new Stand().withName("הגלקסיה").withType(Stand.StandType.REGULAR_STAND).withLocationName("a21-a24").withImageX(1530).withImageY(441),
//				new Stand().withName("go-japan").withType(Stand.StandType.REGULAR_STAND).withLocationName("a25-a26").withImageX(1678).withImageY(441),
//				new Stand().withName("קומיקאזה").withType(Stand.StandType.REGULAR_STAND).withLocationName("a27-a31").withImageX(1874).withImageY(441),
//				new Stand().withName("קבוצת יצירת קומיקס").withType(Stand.StandType.REGULAR_STAND).withLocationName("a32").withImageX(2024).withImageY(441),
//				new Stand().withName("animewave").withType(Stand.StandType.REGULAR_STAND).withLocationName("a33-a38").withImageX(2224).withImageY(441),
//				new Stand().withName("אנימה צ'ודוקו").withType(Stand.StandType.REGULAR_STAND).withLocationName("a39-a42").withImageX(459).withImageY(837),
//				new Stand().withName("Mirrorshards").withType(Stand.StandType.ARTIST_STAND).withLocationName("b01").withImageX(1013).withImageY(604),
//				new Stand().withName("Grisim").withType(Stand.StandType.ARTIST_STAND).withLocationName("b02").withImageX(1073).withImageY(604),
//				new Stand().withName("BL Palace").withType(Stand.StandType.ARTIST_STAND).withLocationName("b03").withImageX(1123).withImageY(604),
//				new Stand().withName("דוכן ציור").withType(Stand.StandType.ARTIST_STAND).withLocationName("b04-b05").withImageX(1208).withImageY(604),
//				new Stand().withName("Landes").withType(Stand.StandType.ARTIST_STAND).withLocationName("b10").withImageX(1564).withImageY(604),
//				new Stand().withName("אביב ציפין קומיקס").withType(Stand.StandType.ARTIST_STAND).withLocationName("b11").withImageX(1619).withImageY(604),
//				new Stand().withName("מושיק גולסט").withType(Stand.StandType.ARTIST_STAND).withLocationName("b12-b13").withImageX(1650).withImageY(685),
//				new Stand().withName("Dor20 Studio").withType(Stand.StandType.ARTIST_STAND).withLocationName("b15").withImageX(1656).withImageY(877),
//				new Stand().withName("adelistic").withType(Stand.StandType.ARTIST_STAND).withLocationName("b16").withImageX(1621).withImageY(925),
//				new Stand().withName("YUEvander").withType(Stand.StandType.ARTIST_STAND).withLocationName("b18").withImageX(1512).withImageY(925),
//				new Stand().withName("מרטין ציורים").withType(Stand.StandType.ARTIST_STAND).withLocationName("b19").withImageX(1453).withImageY(925),
//				new Stand().withName("Yael's Colors").withType(Stand.StandType.ARTIST_STAND).withLocationName("b20").withImageX(1400).withImageY(925),
//				new Stand().withName("היקום המקביל").withType(Stand.StandType.ARTIST_STAND).withLocationName("b22").withImageX(1238).withImageY(925),
//				new Stand().withName("AniArt 4U").withType(Stand.StandType.ARTIST_STAND).withLocationName("b23").withImageX(1181).withImageY(925),
//				new Stand().withName("Rinska's Booth").withType(Stand.StandType.ARTIST_STAND).withLocationName("b25-b26").withImageX(1043).withImageY(925),
//				new Stand().withName("Fishibug").withType(Stand.StandType.ARTIST_STAND).withLocationName("b27-b28").withImageX(987).withImageY(843),
//				new Stand().withName("הדוכן של פיצה").withType(Stand.StandType.ARTIST_STAND).withLocationName("b29").withImageX(987).withImageY(718),
//				new Stand().withName("Tair Art").withType(Stand.StandType.ARTIST_STAND).withLocationName("b30").withImageX(987).withImageY(655)
		);
	}

	private List<Stand> getNesherStands() {
		// TODO update stands for harucon 2018
		return Arrays.asList(
//				new Stand().withName("מודיעין ודוכן אמא\"י").withType(Stand.StandType.REGULAR_STAND).withImageX(1276).withImageY(948),
//				new Stand().withName("שגרירות יפן").withType(Stand.StandType.REGULAR_STAND).withImageX(1112).withImageY(1000),
//				new Stand().withName("ידידות יפן").withType(Stand.StandType.REGULAR_STAND).withImageX(878).withImageY(1000)
		);
	}

	@Override
	public SurveySender getEventVoteSender(final ConventionEvent event) {
		if (event.getUserInput().getVoteSurvey() == null) {
			return null;
		}
		try {
			if (event.getServerId() == EVENT_ID_SHOWCASE) {
				SurveyForm form = new SurveyForm()
						.withQuestionEntry(QUESTION_ID_SHOWCASE_VOTE, "entry.774724773")
						.withSendUrl(new URL("https://docs.google.com/forms/d/e/1FAIpQLSf8YA74ASQ3MXcd5Fjl2iWS2epA-RLTTS5iSI7FSedw-jjT3w/formResponse"));

				SurveyDataRetriever.DisabledMessage disabledMessageRetriever = new SurveyDataRetriever.GoogleSpreadSheet(SHOWCASE_SPREADSHEET_ID);

				return new EventVoteSurveyFormSender(form, event.getUserInput().getVoteSurvey(), disabledMessageRetriever);

			} else if (event.getServerId() == EVENT_ID_COSPLAY) {
				SurveyForm form = new SurveyForm()
						.withQuestionEntry(QUESTION_ID_COSPLAY_VOTE, "entry.751291262")
						.withSendUrl(new URL("https://docs.google.com/forms/d/e/1FAIpQLSeT-yAg5y3CUXC36THZxrpH7jeM9ozQ8JeQE79PuHabw64gIA/formResponse"));

				SurveyDataRetriever.DisabledMessage disabledMessageRetriever = new SurveyDataRetriever.GoogleSpreadSheet(COSPLAY_SPREADSHEET_ID);

				return new EventVoteSurveyFormSender(form, event.getUserInput().getVoteSurvey(), disabledMessageRetriever);
			}
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
		return super.getEventVoteSender(event);
	}

	@Override
	@Nullable
	public SurveyDataRetriever.Answers createSurveyAnswersRetriever(FeedbackQuestion question) {
		switch (question.getQuestionId()) {
			case QUESTION_ID_SHOWCASE_VOTE: {
				return new SurveyDataRetriever.GoogleSpreadSheet(SHOWCASE_SPREADSHEET_ID);
			}
			case QUESTION_ID_COSPLAY_VOTE: {
				return new SurveyDataRetriever.GoogleSpreadSheet(COSPLAY_SPREADSHEET_ID);
			}
		}

		return null;
	}

	@Override
	protected ConventionEvent.UserInput createUserInputForEvent(ConventionEvent event) {
		ConventionEvent.UserInput userInput = super.createUserInputForEvent(event);
		convertUserInputForEvent(userInput, event);
		return userInput;
	}

	@Override
	public void convertUserInputForEvent(ConventionEvent.UserInput userInput, ConventionEvent event) {
		super.convertUserInputForEvent(userInput, event);
		if (userInput.getVoteSurvey() == null && event != null) {
			if (event.getServerId() == EVENT_ID_SHOWCASE) {
				userInput.setVoteSurvey(new Survey().withQuestions(
						new FeedbackQuestion(QUESTION_ID_SHOWCASE_VOTE, FeedbackQuestion.AnswerType.MULTIPLE_ANSWERS_RADIO, true)
				));
			} else if (event.getServerId() == EVENT_ID_COSPLAY) {
				userInput.setVoteSurvey(new Survey().withQuestions(
						new FeedbackQuestion(QUESTION_ID_COSPLAY_VOTE, FeedbackQuestion.AnswerType.MULTIPLE_ANSWERS_RADIO, true)
				));
			}
		}
	}
}

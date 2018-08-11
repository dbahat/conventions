package amai.org.conventions.model.conventions;

import android.support.annotation.Nullable;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
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
import amai.org.conventions.model.SpecialEventsProcessor;
import amai.org.conventions.model.Stand;
import amai.org.conventions.model.StandsArea;
import amai.org.conventions.model.Survey;
import amai.org.conventions.networking.SurveyDataRetriever;
import amai.org.conventions.utils.CollectionUtils;
import amai.org.conventions.utils.ConventionStorage;

public class Cami2018Convention extends AmaiConvention {
	// Hall names
	private static final String MAIN_HALL_NAME = "אולם ראשי";
	private static final String SCHWARTZ_NAME = "אודיטוריום שוורץ";
	private static final String ESHKOL1_NAME = "אשכול 1";
	private static final String ESHKOL2_NAME = "אשכול 2";
	private static final String ESHKOL3_NAME = "אשכול 3";
	private static final String WORKSHOPS_NAME = "חדר סדנאות";
	private static final String GAMES_NAME = "משחקייה";
	private static final String SIGNING_AREA_NAME = "אזור ההחתמות";

	// Vote questions - these values are serialized, don't change them!
	private static final int QUESTION_ID_AMAIDOL_NAME = 1000;
	private static final int QUESTION_ID_AMAIDOL_VOTE = 1001;
	// Special events server id
	private static final int EVENT_ID_AMAIDOL = 5579;
	private static final int EVENT_ID_MIYAZAKI_MOVIE = 5729;

	// Ids of google spreadsheets associated with the special events
	private static final String AMAIDOL_SPREADSHEET_ID = "12ckMtCm-OVPmMgDQfLWHJuzyoKP34wu9TmgeHTnwVkc";

	// Special image IDs
	private static final String MIYAZAKI_MOVIE_IMAGE = "MIYAZAKI_MOVIE_IMAGE";

	static {
		FeedbackQuestion.addQuestion(QUESTION_ID_AMAIDOL_NAME, R.string.amaidol_name_question);
		FeedbackQuestion.addQuestion(QUESTION_ID_AMAIDOL_VOTE, R.string.amaidol_vote_question);
	}


	@Override
	protected ConventionStorage initStorage() {
		return new ConventionStorage(this, R.raw.cami2018_convention_events, 2);
	}

	@Override
	protected Calendar initDate() {
		Calendar date = Calendar.getInstance();
		date.clear();
		date.set(2018, Calendar.AUGUST, 23);
		return date;
	}

	@Override
	protected String initID() {
		return "Cami2018";
	}

	@Override
	protected String initDisplayName() {
		return "כאמ\" 2018";
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
                    .withSendUrl(new URL("https://docs.google.com/forms/d/e/1FAIpQLScI09zCW8w-fUazSoOfZlpJHsEdt3DLPsGMo0TXsJmqx2ksvQ/formResponse"));
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
					.withSendUrl(new URL("https://docs.google.com/forms/d/e/1FAIpQLScvbyNMc4iUD8BsfZFoJZ_u5toUt_fFGlnecdYtmaYOnCgRhQ/formResponse"));
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
		return feedbackForm;
	}

	@Override
	protected URL initModelURL() {
		try {
			return new URL("http://2018.cami.org.il/wp-admin/admin-ajax.php?action=get_event_list");
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	protected ImageIdToImageResourceMapper initImageMapper() {
		ImageIdToImageResourceMapper imageMapper = new ImageIdToImageResourceMapper();

		imageMapper
				.addMapping("http://2018.cami.org.il/wp-content/uploads/sites/15/2018/07/romanlampert.png", R.drawable.event_roman)
				.addMapping("http://2018.cami.org.il/wp-content/uploads/sites/15/2018/07/kerenhaim.png", R.drawable.event_keren)
				.addMapping("http://2018.cami.org.il/wp-content/uploads/sites/15/2018/07/inbareitan.png", R.drawable.event_inbar)
				.addMapping("http://2018.cami.org.il/wp-content/uploads/sites/15/2018/07/omercohen.png", R.drawable.event_omerc)
				.addMapping("http://2018.cami.org.il/wp-content/uploads/sites/15/2018/07/sharonelbaz.png", R.drawable.event_sharonelbaz)
				.addMapping("http://2018.cami.org.il/wp-content/uploads/sites/15/2018/07/shaharagranat.png", R.drawable.event_shahar)
				.addMapping("http://2018.cami.org.il/wp-content/uploads/sites/15/2018/07/matankahal.png", R.drawable.event_matan)
				.addMapping("http://2018.cami.org.il/wp-content/uploads/sites/15/2018/07/doritbentov.png", R.drawable.event_dorit)
				.addMapping("http://2018.cami.org.il/wp-content/uploads/sites/15/2018/07/ophirlotan.png", R.drawable.event_ofirloten)
				.addMapping("http://2018.cami.org.il/wp-content/uploads/sites/15/2018/07/omeramsalem.jpg", R.drawable.event_omeramsalem)
				.addMapping("http://2018.cami.org.il/wp-content/uploads/sites/15/2018/07/naginata.jpg", R.drawable.event_naginata)
				.addMapping("http://2018.cami.org.il/wp-content/uploads/sites/15/2018/07/adarzaks.jpg", R.drawable.event_adarzaks)
				.addMapping("http://2018.cami.org.il/wp-content/uploads/sites/15/2018/07/netapeled.png", R.drawable.event_neta)
				.addMapping("http://2018.cami.org.il/wp-content/uploads/sites/15/2018/07/liadandkeren.png", R.drawable.event_liadandkeren)
				.addMapping("http://2018.cami.org.il/wp-content/uploads/sites/15/2018/07/liadbarshilton.png", R.drawable.event_liadbarshilton)
				.addMapping("http://2018.cami.org.il/wp-content/uploads/sites/15/2018/07/leaotchitel.jpg", R.drawable.event_leaotchitel)
				.addMapping("http://2018.cami.org.il/wp-content/uploads/sites/15/2018/07/guytamir.png", R.drawable.event_guy)
				.addMapping("http://2018.cami.org.il/wp-content/uploads/sites/15/2018/07/omerrozencwieg.jpg", R.drawable.event_omerrozencwieg)
				.addMapping("http://2018.cami.org.il/wp-content/uploads/sites/15/2018/07/guymaraton.png", R.drawable.event_guymaraton)
				.addMapping("http://2018.cami.org.il/wp-content/uploads/sites/15/2018/07/inbarchen.jpg", R.drawable.event_inbarchen)
				.addMapping("http://2018.cami.org.il/wp-content/uploads/sites/15/2018/07/michalefrati.jpg", R.drawable.event_michal)
				.addMapping("http://2018.cami.org.il/wp-content/uploads/sites/15/2018/07/amaidolsite.png", R.drawable.event_amaidol)
				.addMapping("http://2018.cami.org.il/wp-content/uploads/sites/15/2018/07/socialmanga.jpg", R.drawable.event_manga_cafe)
				.addMapping("http://2018.cami.org.il/wp-content/uploads/sites/15/2018/07/leonsolomon.png", R.drawable.event_leon)
				.addMapping("http://2018.cami.org.il/wp-content/uploads/sites/15/2018/07/avivtamir.png", R.drawable.event_aviv)
				.addMapping("http://2018.cami.org.il/wp-content/uploads/sites/15/2018/07/saritegev.jpg", R.drawable.event_sarit)
				.addMapping("http://2018.cami.org.il/wp-content/uploads/sites/15/2018/07/sharonturner.png", R.drawable.event_sharont)
				.addMapping("http://2018.cami.org.il/wp-content/uploads/sites/15/2018/07/trivia.jpg", R.drawable.event_trivia)
				.addMapping("http://2018.cami.org.il/wp-content/uploads/sites/15/2018/07/davidbahat.png", R.drawable.event_davidbahat)
				.addMapping("http://2018.cami.org.il/wp-content/uploads/sites/15/2018/07/MV5BNTFmMDZmMDAtZGE3Yi00MzVmLWEwZmQtZjFhN2U2ZjdiODlmL2ltYWdlL2ltYWdlXkEyXkFqcGdeQXVyNTAyODkwOQ@@._V1_.jpg", R.drawable.event_shlomit)
				.addMapping(MIYAZAKI_MOVIE_IMAGE, R.drawable.event_miyazaki);

		imageMapper.addMapping(ImageIdToImageResourceMapper.EVENT_GENERIC, R.drawable.cami2018_home_activity_background);

		// Excluded IDs - mostly for debug purposes (don't show messages about these when entering an event that has them)
		// Foreground text is not readable

		imageMapper.addExcludedIds(
				// Cosplay / showcase judges
				"http://2018.cami.org.il/wp-content/uploads/sites/15/2018/06/ענבל-מילר.jpg",
				"http://2018.cami.org.il/wp-content/uploads/sites/15/2018/06/דניאל-אביטבול.jpg",
				"http://2018.cami.org.il/wp-content/uploads/sites/15/2018/07/IMG_9671-300x200.jpg",
				"http://2018.cami.org.il/wp-content/uploads/sites/15/2018/07/MG_8954-300x200.jpg",
				"http://2018.cami.org.il/wp-content/uploads/sites/15/2017/11/עודד-איינבינדר.jpg",
				"http://2018.cami.org.il/wp-content/uploads/sites/15/2018/06/סיוון-מגן.jpg",
				"http://2018.cami.org.il/wp-content/uploads/sites/15/2018/06/סבטה-רוד.jpg",
				"http://2018.cami.org.il/wp-content/uploads/sites/15/2018/06/שני-פאיבישנקו.jpg",
				"http://2018.cami.org.il/wp-content/uploads/sites/15/2017/11/אופיר-אבן-צור.jpeg"
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
		Hall signingArea = new Hall().withName(SIGNING_AREA_NAME).withOrder(8);
		return new Halls(Arrays.asList(mainHall, auditorium, eshkol1, eshkol2, eshkol3, workshops, games, signingArea));
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
		Hall signingArea = this.getHalls().findByName(SIGNING_AREA_NAME);

		Floor entrance = new Floor(1)
				.withName("מתחם כניסה")
				.withImageResource(R.drawable.cami2018_entrance_map, false)
				.withImageHeight(523.20001f)
				.withImageWidth(796.45038f);

		Floor floor1 = new Floor(2)
				.withName("קומה 1")
				.withImageResource(R.raw.cami2018_floor1, true)
				.withImageWidth(908.495f)
				.withImageHeight(469.49725f);
		Floor floor2 = new Floor(3)
				.withName("קומה 2")
				.withImageResource(R.raw.cami2018_floor2, true)
				.withImageWidth(986.86462f)
				.withImageHeight(585.27039f);

		StandsArea agam = new StandsArea().withName("טרקלין אגם").withStands(getAgamStands())/*.withImageResource(R.drawable.stands_agam).withImageWidth(2700).withImageHeight(1504)*/;
		StandsArea pinkus = new StandsArea().withName("אולם פינקוס").withStands(getPinkusStands())/*.withImageResource(R.drawable.stands_pinkus).withImageWidth(2700).withImageHeight(1708)*/;
		StandsArea nesher = new StandsArea().withName("רחבת הכניסה").withStands(getNesherStands())/*.withImageResource(R.drawable.stands_nesher).withImageWidth(2588).withImageHeight(1588)*/;
		return new ConventionMap()
				.withFloors(Arrays.asList(entrance, floor1, floor2))
				.withDefaultFloor(floor1)
				.withLocations(
						CollectionUtils.flattenList(
								inFloor(entrance,
										new MapLocation()
												.withPlace(new Place().withName("מתחם הזמנה מראש"))
												.withMarkerResource(R.raw.cami2018_marker_preorder, true)
												.withSelectedMarkerResource(R.raw.cami2018_marker_preorder_selected, true)
												.withMarkerHeight(72.6f)
												.withX(667.4f)
												.withY(288.9f),
										new MapLocation()
												.withPlace(new Place().withName("מתחם קנייה במקום"))
												.withMarkerResource(R.raw.cami2018_marker_cashiers, true)
												.withSelectedMarkerResource(R.raw.cami2018_marker_cashiers_selected, true)
												.withMarkerHeight(72.6f)
												.withX(99.05f)
												.withY(374.9f)
								),
								inFloor(floor1,
										new MapLocation()
												.withPlace(new Place().withName("שירותים"))
												.withMarkerResource(R.raw.cami2018_marker_toilet, true)
												.withSelectedMarkerResource(R.raw.cami2018_marker_toilet_selected, true)
												.withMarkerHeight(54.9f)
												.withX(845.362f)
												.withY(301.397f),
										new MapLocation() // This is before the guest sign post so it will be selected as the stands area
												.withName("מודיעין ודוכן אמא\"י")
												.withPlace(nesher)
												.withMarkerResource(R.raw.cami2018_marker_information, true)
												.withSelectedMarkerResource(R.raw.cami2018_marker_information_selected, true)
												.withMarkerHeight(81.4f)
												.withX(410.362f)
												.withY(178.897f),
										new MapLocation()
												.withName("איזור החתמות")
												.withPlace(signingArea)
												.withMarkerResource(R.raw.cami2018_marker_signings, true)
												.withSelectedMarkerResource(R.raw.cami2018_marker_signings_selected, true)
												.withMarkerHeight(64.7f)
												.withX(678.062f)
												.withY(252.497f),
										new MapLocation()
												.withName("מתחם דוכנים")
												.withPlace(pinkus)
												.withMarkerResource(R.raw.cami2018_marker_stands, true)
												.withSelectedMarkerResource(R.raw.cami2018_marker_stands_selected, true)
												.withMarkerHeight(53.2f)
												.withX(581.062f)
												.withY(317.797f),
										new MapLocation()
												.withPlace(new Place().withName("שמירת חפצים"))
												.withMarkerResource(R.raw.cami2018_marker_storage, true)
												.withSelectedMarkerResource(R.raw.cami2018_marker_storage_selected, true)
												.withMarkerHeight(65.4f)
												.withX(517.562f)
												.withY(115.997f),
										new MapLocation()
												.withPlace(new Place().withName("שירותים"))
												.withMarkerResource(R.raw.cami2018_marker_toilet, true)
												.withSelectedMarkerResource(R.raw.cami2018_marker_toilet_selected, true)
												.withMarkerHeight(54.9f)
												.withX(496.562f)
												.withY(57.197f),
										new MapLocation()
												.withPlace(eshkol1)
												.withMarkerResource(R.raw.cami2018_marker_eshkol1, true)
												.withSelectedMarkerResource(R.raw.cami2018_marker_eshkol1_selected, true)
												.withMarkerHeight(65.5f)
												.withX(240.062f)
												.withY(295.597f),
										new MapLocation()
												.withPlace(schwatrz)
												.withMarkerResource(R.raw.cami2018_marker_schwartz, true)
												.withSelectedMarkerResource(R.raw.cami2018_marker_schwartz_selected, true)
												.withMarkerHeight(64.6f)
												.withX(332.162f)
												.withY(259.997f),
										new MapLocation()
												.withPlace(eshkol3)
												.withMarkerResource(R.raw.cami2018_marker_eshkol3, true)
												.withSelectedMarkerResource(R.raw.cami2018_marker_eshkol3_selected, true)
												.withMarkerHeight(65.9f)
												.withX(165.712f)
												.withY(393.597f),
										new MapLocation()
												.withPlace(eshkol2)
												.withMarkerResource(R.raw.cami2018_marker_eshkol2, true)
												.withSelectedMarkerResource(R.raw.cami2018_marker_eshkol2_selected, true)
												.withMarkerHeight(65.9f)
												.withX(258.812f)
												.withY(393.597f),
										new MapLocation()
												.withPlace(new Place().withName("שירותים"))
												.withMarkerResource(R.raw.cami2018_marker_toilet, true)
												.withSelectedMarkerResource(R.raw.cami2018_marker_toilet_selected, true)
												.withMarkerHeight(55.4f)
												.withX(129.162f)
												.withY(239.597f)),
								inFloor(floor2,
										new MapLocation()
												.withPlace(workshops)
												.withMarkerResource(R.raw.cami2018_marker_workshops, true)
												.withSelectedMarkerResource(R.raw.cami2018_marker_workshops_selected, true)
												.withMarkerHeight(79.1f)
												.withX(825.9f)
												.withY(391.57f),
										new MapLocation()
												.withPlace(new Place().withName("שיפוט קוספליי"))
												.withMarkerResource(R.raw.cami2018_marker_cosplay_judgement, true)
												.withSelectedMarkerResource(R.raw.cami2018_marker_cosplay_judgement_selected, true)
												.withMarkerHeight(66.4f)
												.withX(866.7f)
												.withY(368.57f),
										new MapLocation()
												.withPlace(mainHall)
												.withMarkerResource(R.raw.cami2018_marker_main_hall, true)
												.withSelectedMarkerResource(R.raw.cami2018_marker_main_hall_selected, true)
												.withMarkerHeight(100.302f)
												.withX(634.9f)
												.withY(294.37f),
										new MapLocation()
												.withPlace(new Place().withName("שירותים"))
												.withMarkerResource(R.raw.cami2018_marker_toilet, true)
												.withSelectedMarkerResource(R.raw.cami2018_marker_toilet_selected, true)
												.withMarkerHeight(58.9f)
												.withX(615f)
												.withY(63.87f),
										new MapLocation()
												.withPlace(new Place().withName("מתחם קוספליי"))
												.withMarkerResource(R.raw.cami2018_marker_cosplay_corner, true)
												.withSelectedMarkerResource(R.raw.cami2018_marker_cosplay_corner_selected, true)
												.withMarkerHeight(167.2f)
												.withX(530.5f)
												.withY(408.07f),
										new MapLocation()
												.withPlace(games)
												.withName("משחקייה")
												.withMarkerResource(R.raw.cami2018_marker_games, true)
												.withSelectedMarkerResource(R.raw.cami2018_marker_games_selected, true)
												.withMarkerHeight(166f)
												.withX(388.5f)
												.withY(364.47f),
										new MapLocation()
												.withName("סמטת האמנים")
												.withPlace(agam)
												.withMarkerResource(R.raw.cami2018_marker_artists, true)
												.withSelectedMarkerResource(R.raw.cami2018_marker_artists_selected, true)
												.withMarkerHeight(55.8f)
												.withX(388.5f)
												.withY(195.47f),
										new MapLocation()
												.withPlace(new Place().withName("שירותים"))
												.withMarkerResource(R.raw.cami2018_marker_toilet, true)
												.withSelectedMarkerResource(R.raw.cami2018_marker_toilet_selected, true)
												.withMarkerHeight(53.3f)
												.withX(161.5f)
												.withY(316.87f))
						)
				);
	}

	private List<Stand> getPinkusStands() {
		// TODO update for cami 2018
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
		// TODO update for cami 2018
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
		// TODO update for cami 2018
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
			if (event.getServerId() == EVENT_ID_AMAIDOL) {
				SurveyForm form = new SurveyForm()
						.withQuestionEntry(QUESTION_ID_AMAIDOL_NAME, "entry.109802680")
						.withQuestionEntry(QUESTION_ID_AMAIDOL_VOTE, "entry.1600353678")
						.withSendUrl(new URL("https://docs.google.com/forms/d/e/1FAIpQLScaw6VMWyggn4hE3iK9KANCGcl0bpW9wgeCSVJP9aXnJajiMw/formResponse"));

				SurveyDataRetriever.DisabledMessage disabledMessageRetriever = new SurveyDataRetriever.GoogleSpreadSheet(AMAIDOL_SPREADSHEET_ID);

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
			case QUESTION_ID_AMAIDOL_NAME:
			case QUESTION_ID_AMAIDOL_VOTE: {
				return new SurveyDataRetriever.GoogleSpreadSheet(AMAIDOL_SPREADSHEET_ID);
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
			if (event.getServerId() == EVENT_ID_AMAIDOL) {
				userInput.setVoteSurvey(new Survey().withQuestions(
						new FeedbackQuestion(QUESTION_ID_AMAIDOL_NAME, FeedbackQuestion.AnswerType.SINGLE_LINE_TEXT, true),
						new FeedbackQuestion(QUESTION_ID_AMAIDOL_VOTE, FeedbackQuestion.AnswerType.MULTIPLE_ANSWERS_RADIO, true)
				));
			}
		}
	}

	@Override
	public SpecialEventsProcessor getSpecialEventsProcessor() {
		return new SpecialEventsProcessor() {
			@Override
			public boolean processSpecialEvent(ConventionEvent event) {
				// The image isn't returned from the server for this event
				if (event.getServerId() == EVENT_ID_MIYAZAKI_MOVIE) {
					event.setImages(Collections.singletonList(MIYAZAKI_MOVIE_IMAGE));
				}
				return false;
			}
		};
	}
}

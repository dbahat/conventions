package amai.org.conventions.model.conventions;

import androidx.annotation.Nullable;

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
import amai.org.conventions.model.SpecialEventsProcessor;
import amai.org.conventions.model.Stand;
import amai.org.conventions.model.StandsArea;
import amai.org.conventions.model.Survey;
import amai.org.conventions.networking.SurveyDataRetriever;
import amai.org.conventions.utils.CollectionUtils;
import amai.org.conventions.utils.ConventionStorage;

public class Harucon2022Convention extends AmaiConvention {
	// Hall names
	private static final String MAIN_HALL_NAME = "אולם ראשי";
	private static final String ORANIM_NAME = "אודיטוריום אורנים";
	private static final String ESHKOL1_NAME = "אשכול 1";
	private static final String ESHKOL2_NAME = "אשכול 2";
	private static final String ESHKOL3_NAME = "אשכול 3";
	private static final String WORKSHOPS_NAME = "חדר סדנאות";
	private static final String GAMES_NAME = "משחקייה";
	private static final String COSPLAY_AREA_NAME = "מתחם הקוספליי";
	private static final String SCREENINGS_NAME = "חדר הקרנות";
	// Location names
	public static final String PARENTS_ROOM_NAME = "חדר הורים";

	// Vote questions - these values are serialized, don't change them!
	private static final int QUESTION_ID_AMAIDOL_VOTE = 1000;
	private static final int QUESTION_ID_AMAIDOL_NAME = 1001;

	// Special events server id
	private static final int EVENT_ID_AMAIDOL = 12431;

	// Ids of google spreadsheets associated with the special events
	private static final String AMAIDOL_SPREADSHEET_ID = "1u9xu3FNq2gA25oZoVHVguTzJA5HheXWPf2wnUj-iipE";

	static {
		FeedbackQuestion.addQuestion(QUESTION_ID_AMAIDOL_NAME, R.string.amaidol_name_question);
		FeedbackQuestion.addQuestion(QUESTION_ID_AMAIDOL_VOTE, R.string.amaidol_vote_question);
	}

	// Stand types
	private enum StandType implements Stand.StandType {
		CLOTHES(R.string.clothes_stand, R.drawable.shirt),
		MERCH(R.string.merch_stand, R.drawable.ic_shopping_basket),
		MANGA(R.string.manga_stand, R.drawable.book),
		VIDEO_GAMES(R.string.video_games_stand, R.drawable.videogame_black),
		TABLETOP_GAMES(R.string.tabletop_games_stand, R.drawable.chess),
		ARTIST(R.string.artist_stand, R.drawable.ic_color_lens),
		OTHER(R.string.other, R.drawable.ic_shopping_basket);

		private int title;
		private int image;

		StandType(int title, int image) {
			this.title = title;
			this.image = image;
		}

		public int getTitle() {
			return title;
		}

		public int getImage() {
			return image;
		}

		@Override
		public int compareTo(Stand.StandType standType) {
			if (!(standType instanceof StandType)) {
				throw new ClassCastException();
			}
			return this.compareTo((StandType) standType);
		}
	}

	@Override
	protected ConventionStorage initStorage() {
		return new ConventionStorage(this, R.raw.harucon2022_convention_events, 0);
	}

	@Override
	protected Calendar initDate() {
		Calendar date = Calendar.getInstance();
		date.clear();
		date.set(2022, Calendar.MARCH, 17);
		return date;
	}

	@Override
	protected String initID() {
		return "Harucon2022";
	}

	@Override
	protected String initDisplayName() {
		return "הארוקון 2022";
	}

	@Override
	protected URL initUpdatesURL() {
		try {
			return new URL("https://harucon.org.il/2022/wp-content/plugins/get-harucon-feed.php");
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
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
					.withSendUrl(new URL("https://docs.google.com/forms/d/e/1FAIpQLSdYbpAdyjPiwDYWY3GrJKTvf4uwkUSZ97YEhkyQdUPOlF3gKA/formResponse"));
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
					.withSendUrl(new URL("https://docs.google.com/forms/d/e/1FAIpQLSde5_3LNnhJhRDlhU-pyVUQR3ENYCQpCA-PzVitLcKZ_MgR_A/formResponse"));
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
		return feedbackForm;
	}

	@Override
	protected URL initModelURL() {
		try {
			return new URL("https://harucon.org.il/2022/wp-admin/admin-ajax.php?action=get_event_list");
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	protected ImageIdToImageResourceMapper initImageMapper() {

		// In case the convention has custom images per event, map them like this
//		imageMapper
//				.addMapping("https://harucon.org.il/2020/wp-content/uploads/sites/21/2020/01/GuyTamir.jpg", R.drawable.event_guytamir)
//		;


		// In case the convention has Excluded IDs images per event, map them like this
//		imageMapper.addExcludedIds(
//				// Games room
//				"https://harucon.org.il/2020/wp-content/uploads/sites/21/2019/07/IMG_3812.png"
//		);

		return new ImageIdToImageResourceMapper()
				.addMapping(ImageIdToImageResourceMapper.EVENT_GENERIC, R.drawable.harucon2022_default_background);
	}

	@Override
	protected Halls initHalls() {
		Hall mainHall = new Hall().withName(MAIN_HALL_NAME).withOrder(1);
		Hall auditorium = new Hall().withName(ORANIM_NAME).withOrder(2);
		Hall eshkol1 = new Hall().withName(ESHKOL1_NAME).withOrder(3);
		Hall eshkol2 = new Hall().withName(ESHKOL2_NAME).withOrder(4);
		Hall eshkol3 = new Hall().withName(ESHKOL3_NAME).withOrder(5);
		Hall workshops = new Hall().withName(WORKSHOPS_NAME).withOrder(6);
		Hall cosplayArea = new Hall().withName(COSPLAY_AREA_NAME).withOrder(7);
		Hall games = new Hall().withName(GAMES_NAME).withOrder(8);
		Hall screenings = new Hall().withName(SCREENINGS_NAME).withOrder(9);
		return new Halls(Arrays.asList(mainHall, auditorium, eshkol1, eshkol2, eshkol3, workshops, cosplayArea, games, screenings));
	}

	@Override
	protected ConventionMap initMap() {
		return createMap();
	}

	private ConventionMap createMap() {
		Hall mainHall = this.getHalls().findByName(MAIN_HALL_NAME);
		Hall auditorium = this.getHalls().findByName(ORANIM_NAME);
		Hall eshkol1 = this.getHalls().findByName(ESHKOL1_NAME);
		Hall eshkol2 = this.getHalls().findByName(ESHKOL2_NAME);
		Hall eshkol3 = this.getHalls().findByName(ESHKOL3_NAME);
		Hall workshops = this.getHalls().findByName(WORKSHOPS_NAME);
		Hall games = this.getHalls().findByName(GAMES_NAME);
		Hall cosplayArea = this.getHalls().findByName(COSPLAY_AREA_NAME);
		Hall screenings = this.getHalls().findByName(SCREENINGS_NAME);

		Floor entrance = new Floor(1)
				.withName("מתחם כניסה")
				.withImageResource(R.raw.harucon2022_floor_entrance, true)
				.withImageWidth(1297.96997f)
				.withImageHeight(804.69f);
		Floor floor1 = new Floor(2)
				.withName("קומה 1")
				.withImageResource(R.raw.harucon2022_floor1, true)
				.withImageWidth(1637f)
				.withImageHeight(834f);
		Floor floor2 = new Floor(3)
				.withName("קומה 2")
				.withImageResource(R.raw.harucon2022_floor2, true)
				.withImageWidth(1805.90002f)
				.withImageHeight(1081.19995f);

		StandsArea agam = new StandsArea().withName("טרקלין אגם").withStands(getAgamStands()).withImageResource(R.drawable.harucon2022_stands_map_agam).withImageWidth(2700).withImageHeight(919);
		StandsArea pinkus = new StandsArea().withName("אולם פינקוס").withStands(getPinkusStands()).withImageResource(R.drawable.harucon2022_stands_map_pinkus).withImageWidth(2700).withImageHeight(2003);
		return new ConventionMap()
				.withFloors(Arrays.asList(entrance, floor1, floor2))
				.withDefaultFloor(floor1)
				.withLocations(
						CollectionUtils.flattenList(
								inFloor(entrance,
										new MapLocation()
												.withPlace(new Place().withName("עמדת מודיעין"))
												.withMarkerResource(R.raw.harucon2022_marker_information, true)
												.withSelectedMarkerResource(R.raw.harucon2022_marker_information_selected, true)
												.withMarkerHeight(136.51f)
												.withX(1047.39f)
												.withY(658.18f),
										new MapLocation()
												.withPlace(new Place().withName("עמדות צימוד"))
												.withMarkerResource(R.raw.harucon2022_marker_bracelets, true)
												.withSelectedMarkerResource(R.raw.harucon2022_marker_bracelets_selected, true)
												.withMarkerHeight(104.76f)
												.withX(661.73f)
												.withY(674.37f),
										new MapLocation()
												.withPlace(new Place().withName("קופות"))
												.withMarkerResource(R.raw.harucon2022_marker_cashiers, true)
												.withSelectedMarkerResource(R.raw.harucon2022_marker_cashiers_selected, true)
												.withMarkerHeight(104.76f)
												.withX(498.6f)
												.withY(634.04f),
										new MapLocation()
												.withPlace(new Place().withName("קופה נגישה"))
												.withMarkerResource(R.raw.harucon2022_marker_accessible_cashier, true)
												.withSelectedMarkerResource(R.raw.harucon2022_marker_accessible_cashier_selected, true)
												.withMarkerHeight(104.76f)
												.withX(670.84f)
												.withY(521.85f),
										new MapLocation()
												.withPlace(new Place().withName("מתחם הזמנה מראש"))
												.withMarkerResource(R.raw.harucon2022_marker_preorders, true)
												.withSelectedMarkerResource(R.raw.harucon2022_marker_preorders_selected, true)
												.withMarkerHeight(104.75f)
												.withX(722.45f)
												.withY(237.4f),
										new MapLocation()
												.withPlace(new Place().withName("מתחם קנייה במקום"))
												.withMarkerResource(R.raw.harucon2022_marker_tickets_area, true)
												.withSelectedMarkerResource(R.raw.harucon2022_marker_tickets_area_selected, true)
												.withMarkerHeight(104.76f)
												.withX(220.69f)
												.withY(369.84f),
										new MapLocation()
												.withPlace(new Place().withName("מעבר נגיש"))
												.withMarkerResource(R.raw.harucon2022_marker_accessible_passage, true)
												.withSelectedMarkerResource(R.raw.harucon2022_marker_accessible_passage_selected, true)
												.withMarkerHeight(104.76f)
												.withX(195.73f)
												.withY(258.36f)
								),
								inFloor(floor1,
										new MapLocation()
												.withPlace(new Place().withName("שירותים"))
												.withMarkerResource(R.raw.harucon2022_marker_toilet, true)
												.withSelectedMarkerResource(R.raw.harucon2022_marker_toilet_selected, true)
												.withMarkerHeight(97.3f)
												.withX(1533.1f)
												.withY(526.5f),
										new MapLocation()
												.withPlace(screenings)
												.withMarkerResource(R.raw.harucon2022_marker_screenings, true)
												.withSelectedMarkerResource(R.raw.harucon2022_marker_screenings_selected, true)
												.withMarkerHeight(70.8f)
												.withX(1390.3f)
												.withY(635.2f),
										new MapLocation()
												.withPlace(auditorium)
												.withMarkerResource(R.raw.harucon2022_marker_oranim, true)
												.withSelectedMarkerResource(R.raw.harucon2022_marker_oranim_selected, true)
												.withMarkerHeight(104.8f)
												.withX(1312.4f)
												.withY(668f),
										new MapLocation()
												.withPlace(new Place().withName("שמירת חפצים"))
												.withMarkerResource(R.raw.harucon2022_marker_storage, true)
												.withSelectedMarkerResource(R.raw.harucon2022_marker_storage_selected, true)
												.withMarkerHeight(121.1f)
												.withX(1216.9f)
												.withY(702.9f),
										new MapLocation()
												.withName("מתחם דוכנים")
												.withPlace(pinkus)
												.withMarkerResource(R.raw.harucon2022_marker_stands, true)
												.withSelectedMarkerResource(R.raw.harucon2022_marker_stands_selected, true)
												.withMarkerHeight(94.4f)
												.withX(1064.4f)
												.withY(555.4f),
										new MapLocation()
												.withPlace(new Place().withName("שירותים"))
												.withMarkerResource(R.raw.harucon2022_marker_toilet, true)
												.withSelectedMarkerResource(R.raw.harucon2022_marker_toilet_selected, true)
												.withMarkerHeight(97.3f)
												.withX(914.6f)
												.withY(93.5f),
										new MapLocation()
												.withPlace(new Place().withName("מעלית"))
												.withMarkerResource(R.raw.harucon2022_marker_elevator, true)
												.withSelectedMarkerResource(R.raw.harucon2022_marker_elevator_selected, true)
												.withMarkerHeight(94.502f)
												.withX(776.1f)
												.withY(139.9f),
										new MapLocation()
												.withPlace(new Place().withName("מודיעין ודוכן אמא\"י"))
												.withMarkerResource(R.raw.harucon2022_marker_information_amai, true)
												.withSelectedMarkerResource(R.raw.harucon2022_marker_information_amai_selected, true)
												.withMarkerHeight(144.2f)
												.withX(761.5f)
												.withY(309.3f),
										new MapLocation()
												.withPlace(eshkol1)
												.withMarkerResource(R.raw.harucon2022_marker_eshkol1, true)
												.withSelectedMarkerResource(R.raw.harucon2022_marker_eshkol1_selected, true)
												.withMarkerHeight(116.8f)
												.withX(450.5f)
												.withY(507.5f),
										new MapLocation()
												.withPlace(eshkol3)
												.withMarkerResource(R.raw.harucon2022_marker_eshkol3, true)
												.withSelectedMarkerResource(R.raw.harucon2022_marker_eshkol3_selected, true)
												.withMarkerHeight(116.8f)
												.withX(327.6f)
												.withY(690.1f),
										new MapLocation()
												.withPlace(eshkol2)
												.withMarkerResource(R.raw.harucon2022_marker_eshkol2, true)
												.withSelectedMarkerResource(R.raw.harucon2022_marker_eshkol2_selected, true)
												.withMarkerHeight(116.8f)
												.withX(492.9f)
												.withY(690.1f),
										new MapLocation()
												.withPlace(new Place().withName("שירותים"))
												.withMarkerResource(R.raw.harucon2022_marker_toilet, true)
												.withSelectedMarkerResource(R.raw.harucon2022_marker_toilet_selected, true)
												.withMarkerHeight(98.3f)
												.withX(262.8f)
												.withY(416.8f),
										new MapLocation()
												.withPlace(new Place().withName("מעלית"))
												.withMarkerResource(R.raw.harucon2022_marker_elevator, true)
												.withSelectedMarkerResource(R.raw.harucon2022_marker_elevator_selected, true)
												.withMarkerHeight(94.502f)
												.withX(89.2f)
												.withY(504.2f)
								),
								inFloor(floor2,
										new MapLocation()
												.withPlace(workshops)
												.withMarkerResource(R.raw.harucon2022_marker_workshops, true)
												.withSelectedMarkerResource(R.raw.harucon2022_marker_workshops_selected, true)
												.withMarkerHeight(133.6f)
												.withX(1493.6f)
												.withY(937.59995f),
										new MapLocation()
												.withPlace(new Place().withName("שיפוט קוספליי"))
												.withMarkerResource(R.raw.harucon2022_marker_cosplay_judgement, true)
												.withSelectedMarkerResource(R.raw.harucon2022_marker_cosplay_judgement_selected, true)
												.withMarkerHeight(116f)
												.withX(1355.5f)
												.withY(885.59995f),
										new MapLocation()
												.withPlace(new Place().withName(PARENTS_ROOM_NAME))
												.withMarkerResource(R.raw.harucon2022_marker_parents, true)
												.withSelectedMarkerResource(R.raw.harucon2022_marker_parents_selected, true)
												.withMarkerHeight(94.6f)
												.withX(1476.6f)
												.withY(714.09995f),
										new MapLocation()
												.withPlace(new Place().withName("שירותים"))
												.withMarkerResource(R.raw.harucon2022_marker_toilet, true)
												.withSelectedMarkerResource(R.raw.harucon2022_marker_toilet_selected, true)
												.withMarkerHeight(98.8f)
												.withX(1705.8f)
												.withY(587.49995f),
										new MapLocation()
												.withPlace(mainHall)
												.withMarkerResource(R.raw.harucon2022_marker_main_hall, true)
												.withSelectedMarkerResource(R.raw.harucon2022_marker_main_hall_selected, true)
												.withMarkerHeight(177.805f)
												.withX(1189.8f)
												.withY(517.19995f),
										new MapLocation()
												.withPlace(new Place().withName("שירותים"))
												.withMarkerResource(R.raw.harucon2022_marker_toilet, true)
												.withSelectedMarkerResource(R.raw.harucon2022_marker_toilet_selected, true)
												.withMarkerHeight(101.1f)
												.withX(1154.6f)
												.withY(108.19995f),
										new MapLocation()
												.withPlace(new Place().withName("מעלית"))
												.withMarkerResource(R.raw.harucon2022_marker_elevator, true)
												.withSelectedMarkerResource(R.raw.harucon2022_marker_elevator_selected, true)
												.withMarkerHeight(94.502f)
												.withX(1025.7f)
												.withY(126.89995f),
										new MapLocation()
												.withPlace(cosplayArea)
												.withMarkerResource(R.raw.harucon2022_marker_cosplay, true)
												.withSelectedMarkerResource(R.raw.harucon2022_marker_cosplay_selected, true)
												.withMarkerHeight(261.6f)
												.withX(1004.7f)
												.withY(718.79995f),
										new MapLocation()
												.withPlace(games)
												.withName("משחקייה")
												.withMarkerResource(R.raw.harucon2022_marker_games, true)
												.withSelectedMarkerResource(R.raw.harucon2022_marker_games_selected, true)
												.withMarkerHeight(256.9f)
												.withX(752.8f)
												.withY(641.49995f),
										new MapLocation()
												.withName("שדרת ציירים ומתחם דוכנים")
												.withPlace(agam)
												.withMarkerResource(R.raw.harucon2022_marker_artists_alley, true)
												.withSelectedMarkerResource(R.raw.harucon2022_marker_artists_alley_selected, true)
												.withMarkerHeight(139.3f)
												.withX(752.8f)
												.withY(341.59995f),
										new MapLocation()
												.withPlace(new Place().withName("שירותים"))
												.withMarkerResource(R.raw.harucon2022_marker_toilet, true)
												.withSelectedMarkerResource(R.raw.harucon2022_marker_toilet_selected, true)
												.withMarkerHeight(94.6f)
												.withX(343.6f)
												.withY(471.79995f),
										new MapLocation()
												.withPlace(new Place().withName("מעלית"))
												.withMarkerResource(R.raw.harucon2022_marker_elevator, true)
												.withSelectedMarkerResource(R.raw.harucon2022_marker_elevator_selected, true)
												.withMarkerHeight(94.502f)
												.withX(251.4f)
												.withY(549.59995f)
								)
						)
				);
	}

	private List<Stand> getPinkusStands() {
		return Arrays.asList(
				new Stand().withName("נקסוס").withType(StandType.TABLETOP_GAMES).withLocationName("c1-c6").withSort("c01-c06").withImageX(348).withImageY(125),
				new Stand().withName("sampai designs").withType(StandType.CLOTHES).withLocationName("c7-c8").withSort("c07-c08").withImageX(696).withImageY(125),
				new Stand().withName("U.kaiju").withType(StandType.CLOTHES).withLocationName("c9-c10").withSort("c09-c10").withImageX(1082).withImageY(125),
				new Stand().withName("Fantasy House").withType(StandType.MERCH).withLocationName("c11-c12").withSort("c11-c12").withImageX(1260).withImageY(125),
				new Stand().withName("Sirolynia").withType(StandType.TABLETOP_GAMES).withLocationName("c13-c16").withSort("c13-c16").withImageX(1626).withImageY(125),
				new Stand().withName("Ascendant Fiction").withType(StandType.OTHER).withLocationName("c19").withSort("c19").withImageX(2023).withImageY(125),
				new Stand().withName("Toys of America").withType(StandType.OTHER).withLocationName("c20-c22").withSort("c20-c22").withImageX(2196).withImageY(125),
				new Stand().withName("מיסקייסיס").withType(StandType.OTHER).withLocationName("c23-c26").withSort("c23-c26").withImageX(2417).withImageY(632),
				new Stand().withName("BEADUS").withType(StandType.MERCH).withLocationName("c27-c28").withSort("c27-c28").withImageX(2417).withImageY(898),
				new Stand().withName("Funko V.I.P").withType(StandType.OTHER).withLocationName("c29-c30").withSort("c29-c30").withImageX(2417).withImageY(1069),
				new Stand().withName("אנימה סטור").withType(StandType.MERCH).withLocationName("c31-c36").withSort("c31-c36").withImageX(2355).withImageY(1687),
				new Stand().withName("Gaming land גיימינג לנד").withType(StandType.VIDEO_GAMES).withLocationName("c37-c40").withSort("c37-c40").withImageX(1805).withImageY(1687),
				new Stand().withName("Respberry").withType(StandType.CLOTHES).withLocationName("c41").withSort("c41").withImageX(1585).withImageY(1687),
				new Stand().withName("SweetHeartYun").withType(StandType.CLOTHES).withLocationName("c42").withSort("c42").withImageX(1501).withImageY(1687),
				new Stand().withName("גיקפליז").withType(StandType.OTHER).withLocationName("c43-c44").withSort("c43-c44").withImageX(1250).withImageY(1687),
				new Stand().withName("ארט סנטר").withType(StandType.OTHER).withLocationName("c45-c48").withSort("c45-c48").withImageX(984).withImageY(1687),
				new Stand().withName("HUJI sensei").withType(StandType.OTHER).withLocationName("c49").withSort("c49").withImageX(771).withImageY(1782),
				new Stand().withName("איגוד מקצועות האנימציה").withType(StandType.OTHER).withLocationName("c50").withSort("c50").withImageX(771).withImageY(1871),
				new Stand().withName("גברת וודו").withType(StandType.CLOTHES).withLocationName("d1-d2").withSort("d01-d02").withImageX(1892).withImageY(589),
				new Stand().withName("501st Legion").withType(StandType.OTHER).withLocationName("d3").withSort("d03").withImageX(2031).withImageY(682),
				new Stand().withName("Noyanny").withType(StandType.CLOTHES).withLocationName("d4").withSort("d04").withImageX(2031).withImageY(766),
				new Stand().withName("Angry customs").withType(StandType.MERCH).withLocationName("d5-d6").withSort("d05-d06").withImageX(2031).withImageY(897),
				new Stand().withName("המרכז ללימודי יפנית").withType(StandType.OTHER).withLocationName("d7-d8").withSort("d07-d08").withImageX(2031).withImageY(1072),
				new Stand().withName("Jill._.Creations").withType(StandType.OTHER).withLocationName("d9-d10").withSort("d09-d10").withImageX(1895).withImageY(1203),
				new Stand().withName("blueberry_crown").withType(StandType.CLOTHES).withLocationName("d11").withSort("d11").withImageX(1762).withImageY(1203),
				new Stand().withName("Natoki").withType(StandType.CLOTHES).withLocationName("d12").withSort("d12").withImageX(1677).withImageY(1203),
				new Stand().withName("NoamWool").withType(StandType.CLOTHES).withLocationName("d13").withSort("d13").withImageX(1586).withImageY(1203),
				new Stand().withName("Resinbar").withType(StandType.OTHER).withLocationName("d14").withSort("d14").withImageX(1503).withImageY(1203),
				new Stand().withName("Velvet Octopus").withType(StandType.CLOTHES).withLocationName("d15-d16").withSort("d15-d16").withImageX(1312).withImageY(1072),
				new Stand().withName("מאי שירי design & art").withType(StandType.CLOTHES).withLocationName("d17-d18").withSort("d17-d18").withImageX(1312).withImageY(897),
				new Stand().withName("AKINAPAZ").withType(StandType.MERCH).withLocationName("d19-d20").withSort("d19-d20").withImageX(1312).withImageY(723),
				new Stand().withName("Kawaii land shop").withType(StandType.CLOTHES).withLocationName("d21").withSort("d21").withImageX(1400).withImageY(593),
				new Stand().withName("Pop-Storm").withType(StandType.MERCH).withLocationName("d23-d24").withSort("d23-d24").withImageX(1616).withImageY(593),
				new Stand().withName("TOKYO POSTERS XTR").withType(StandType.MERCH).withLocationName("e1-e2").withSort("e01-e02").withImageX(810).withImageY(593),
				new Stand().withName("ComiXunity").withType(StandType.MANGA).withLocationName("e5-e6").withSort("e05-e06").withImageX(948).withImageY(897),
				new Stand().withName("Aquamaren").withType(StandType.CLOTHES).withLocationName("e7-e8").withSort("e07-e08").withImageX(948).withImageY(1071),
				new Stand().withName("Frozen flawers").withType(StandType.CLOTHES).withLocationName("e9-e10").withSort("e09-e10").withImageX(811).withImageY(1203),
				new Stand().withName("Cathatart").withType(StandType.OTHER).withLocationName("e11").withSort("e11").withImageX(576).withImageY(1203),
				new Stand().withName("Haruugami").withType(StandType.MERCH).withLocationName("e12").withSort("e12").withImageX(490).withImageY(1203),
				new Stand().withName("אנימאג -Animug").withType(StandType.MERCH).withLocationName("e13").withSort("e13").withImageX(405).withImageY(1203),
				new Stand().withName("Japaneasy").withType(StandType.OTHER).withLocationName("e14").withSort("e14").withImageX(322).withImageY(1203),
				new Stand().withName("TVfox").withType(StandType.OTHER).withLocationName("e15-e16").withSort("e15-e16").withImageX(231).withImageY(1071),
				new Stand().withName("blup").withType(StandType.MANGA).withLocationName("e17-e20").withSort("e17-e20").withImageX(231).withImageY(809),
				new Stand().withName("Candy Lenses").withType(StandType.MERCH).withLocationName("e21-e24").withSort("e21-e24").withImageX(446).withImageY(592)
		);
	}

	private List<Stand> getAgamStands() {
		return Arrays.asList(
				new Stand().withName("מאי ארט").withType(StandType.OTHER).withLocationName("a1-a4").withSort("a01-a04").withImageX(248).withImageY(186),
				new Stand().withName("החתול הסגול").withType(StandType.CLOTHES).withLocationName("a5-a8").withSort("a05-a08").withImageX(484).withImageY(186),
				new Stand().withName("Mini Tokio").withType(StandType.MERCH).withLocationName("a9-a10").withSort("a09-a10").withImageX(717).withImageY(186),
				new Stand().withName("קומיקאזה").withType(StandType.MANGA).withLocationName("a11-a13").withSort("a11-a13").withImageX(865).withImageY(186),
				new Stand().withName("N FIG").withType(StandType.MERCH).withLocationName("a14").withSort("a14").withImageX(987).withImageY(186),
				new Stand().withName("דוכן שיפודן").withType(StandType.MERCH).withLocationName("a15-a20").withSort("a15-a20").withImageX(1250).withImageY(186),
				new Stand().withName("קומיקס וירקות").withType(StandType.MANGA).withLocationName("a21-a24").withSort("a21-a24").withImageX(1604).withImageY(186),
				new Stand().withName("הממלכה").withType(StandType.TABLETOP_GAMES).withLocationName("a25-a26").withSort("a25-a26").withImageX(1785).withImageY(186),
				new Stand().withName("אנימה וויב").withType(StandType.MERCH).withLocationName("a27-a30").withSort("a27-a30").withImageX(2004).withImageY(186),
				new Stand().withName("T&T").withType(StandType.CLOTHES).withLocationName("a31-a32").withSort("a31-a32").withImageX(2183).withImageY(186),
				new Stand().withName("אנימה שופ").withType(StandType.MERCH).withLocationName("a33-a38").withSort("a33-a38").withImageX(2463).withImageY(186),
				new Stand().withName("Anime Gifts").withType(StandType.MERCH).withLocationName("a39-a42").withSort("a39-a42").withImageX(85).withImageY(730),
				new Stand().withName("Gran D. Line").withType(StandType.MERCH).withLocationName("a43-a44").withSort("a43-a44").withImageX(2609).withImageY(604),
				new Stand().withName("Vered Roze Art").withType(StandType.ARTIST).withLocationName("b1-b2").withSort("b01-b02").withImageX(936).withImageY(474),
				new Stand().withName("דניהלמן ארט").withType(StandType.ARTIST).withLocationName("b3").withSort("b03").withImageX(936).withImageY(561),
				new Stand().withName("Ked_creation").withType(StandType.ARTIST).withLocationName("b4").withSort("b04").withImageX(936).withImageY(623),
				new Stand().withName("גריסים").withType(StandType.ARTIST).withLocationName("b5-b6").withSort("b05-b06").withImageX(858).withImageY(828),
				new Stand().withName("תיקים מגניבים של Otaku").withType(StandType.ARTIST).withLocationName("b7").withSort("b07").withImageX(776).withImageY(828),
				new Stand().withName("Shorterthan").withType(StandType.ARTIST).withLocationName("b8-b9").withSort("b08-b09").withImageX(677).withImageY(828),
				new Stand().withName("TOTAL LOST").withType(StandType.ARTIST).withLocationName("b10-b11").withSort("b10-b11").withImageX(546).withImageY(711),
				new Stand().withName("הוט גירל פורים").withType(StandType.ARTIST).withLocationName("b12").withSort("b12").withImageX(546).withImageY(627),
				new Stand().withName("דוכן האמן של ליאן").withType(StandType.ARTIST).withLocationName("b13").withSort("b13").withImageX(546).withImageY(566),
				new Stand().withName("Hikikomoring - Art by Sem Daniel").withType(StandType.ARTIST).withLocationName("b14").withSort("b14").withImageX(546).withImageY(552),
				new Stand().withName("Shokolyr").withType(StandType.ARTIST).withLocationName("b15").withSort("b15").withImageX(546).withImageY(549),
				new Stand().withName("Mariliya").withType(StandType.ARTIST).withLocationName("b16-b17").withSort("b16-b17").withImageX(651).withImageY(360),
				new Stand().withName("טחינה").withType(StandType.ARTIST).withLocationName("b18").withSort("b18").withImageX(740).withImageY(360),
				new Stand().withName("הדוחן").withType(StandType.ARTIST).withLocationName("b19").withSort("b19").withImageX(806).withImageY(360),
				new Stand().withName("קומיקום").withType(StandType.ARTIST).withLocationName("b20").withSort("b20").withImageX(1475).withImageY(530),
				new Stand().withName("Kip, Eve & Co.").withType(StandType.ARTIST).withLocationName("b21").withSort("b21").withImageX(1475).withImageY(591),
				new Stand().withName("Strawberry flavors").withType(StandType.ARTIST).withLocationName("b22").withSort("b22").withImageX(1475).withImageY(651),
				new Stand().withName("Geek Aesthetics").withType(StandType.ARTIST).withLocationName("b23").withSort("b23").withImageX(1475).withImageY(710),
				new Stand().withName("Saigar Art").withType(StandType.ARTIST).withLocationName("b24").withSort("b24").withImageX(1475).withImageY(769),
				new Stand().withName("Kartzi's").withType(StandType.ARTIST).withLocationName("b25-b26").withSort("b25-b26").withImageX(1386).withImageY(830),
				new Stand().withName("OdeChan Art").withType(StandType.ARTIST).withLocationName("b27").withSort("b27").withImageX(1292).withImageY(830),
				new Stand().withName("צפרדע שוקולד").withType(StandType.ARTIST).withLocationName("b28").withSort("b28").withImageX(1232).withImageY(830),
				new Stand().withName("בועת מחשבה").withType(StandType.OTHER).withLocationName("b29").withSort("b29").withImageX(1145).withImageY(671),
				new Stand().withName("Sloth with a hat").withType(StandType.ARTIST).withLocationName("b30").withSort("b30").withImageX(1145).withImageY(617),
				new Stand().withName("cryptic arts").withType(StandType.ARTIST).withLocationName("b31").withSort("b31").withImageX(1145).withImageY(554),
				new Stand().withName("מאיירת מציאות").withType(StandType.ARTIST).withLocationName("b32").withSort("b32").withImageX(1145).withImageY(495),
				new Stand().withName("Foxerish").withType(StandType.ARTIST).withLocationName("b33").withSort("b33").withImageX(1145).withImageY(434),
				new Stand().withName("עולם החיות של בת-חן צרפתי").withType(StandType.ARTIST).withLocationName("b34-b35").withSort("b34-b35").withImageX(1231).withImageY(373),
				new Stand().withName("ROTEMZ ART").withType(StandType.ARTIST).withLocationName("b36").withSort("b36").withImageX(1325).withImageY(373),
				new Stand().withName("Martin Draws").withType(StandType.ARTIST).withLocationName("b37").withSort("b37").withImageX(1382).withImageY(373),
				new Stand().withName("Sweetie's Stand").withType(StandType.ARTIST).withLocationName("b38").withSort("b38").withImageX(1891).withImageY(373),
				new Stand().withName("האומנות של ריי").withType(StandType.ARTIST).withLocationName("b39").withSort("b39").withImageX(1955).withImageY(373),
				new Stand().withName("Haruhi Chili").withType(StandType.ARTIST).withLocationName("b40").withSort("b40").withImageX(2006).withImageY(373),
				new Stand().withName("Tomato Eater").withType(StandType.ARTIST).withLocationName("b41").withSort("b41").withImageX(2068).withImageY(373),
				new Stand().withName("SHIR K").withType(StandType.ARTIST).withLocationName("b42-b43").withSort("b42-b43").withImageX(2153).withImageY(478),
				new Stand().withName("Orchibald art").withType(StandType.ARTIST).withLocationName("b44-b45").withSort("b44-b45").withImageX(2153).withImageY(596),
				new Stand().withName("Ozart").withType(StandType.ARTIST).withLocationName("b46-b47").withSort("b46-b47").withImageX(2153).withImageY(716),
				new Stand().withName("fishiebug").withType(StandType.ARTIST).withLocationName("b48").withSort("b48").withImageX(2061).withImageY(832),
				new Stand().withName("Meiiior").withType(StandType.ARTIST).withLocationName("b49").withSort("b49").withImageX(2000).withImageY(832),
				new Stand().withName("קבוצת יצירת קומיקס").withType(StandType.MANGA).withLocationName("b50-b51").withSort("b50-b51").withImageX(1911).withImageY(832),
				new Stand().withName("kukeshii").withType(StandType.ARTIST).withLocationName("b52").withSort("b52").withImageX(1821).withImageY(832),
				new Stand().withName("koruhiko").withType(StandType.ARTIST).withLocationName("b53-b54").withSort("b53-b54").withImageX(1760).withImageY(645),
				new Stand().withName("AniPug").withType(StandType.ARTIST).withLocationName("b55").withSort("b55").withImageX(1760).withImageY(557),
				new Stand().withName("Gabisweb").withType(StandType.ARTIST).withLocationName("b56").withSort("b56").withImageX(1760).withImageY(492)
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
                        .withSendUrl(new URL("https://docs.google.com/forms/d/e/1FAIpQLSe3sJ2LYHFkg2e0bQePIMI1K3nV1GCNyYwhHDLRcGIx-Twl4Q/formResponse"));

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
				return false;
			}
		};
	}
}

package amai.org.conventions.model.conventions;

import amai.org.conventions.model.StandLocation;
import amai.org.conventions.model.StandLocations;
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

		StandsArea agam = new StandsArea()
				.withName("טרקלין אגם")
				.withStandLocations(getAgamStandLocations()) // This must be initialized before the stands
				.withStands(getAgamStands())
				.withImageResource(R.drawable.harucon2022_stands_map_agam)
				.withImageWidth(2700)
				.withImageHeight(919);
		StandsArea pinkus = new StandsArea()
				.withName("אולם פינקוס")
				.withStandLocations(getPinkusStandLocations()) // This must be initialized before the stands
				.withStands(getPinkusStands())
				.withImageResource(R.drawable.harucon2022_stands_map_pinkus)
				.withImageWidth(2700)
				.withImageHeight(2003);

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
				new Stand().withName("נקסוס").withType(StandType.TABLETOP_GAMES).withLocationIds("c1", "c2", "c3", "c4", "c5", "c6"),
				new Stand().withName("sampai designs").withType(StandType.CLOTHES).withLocationIds("c7", "c8"),
				new Stand().withName("U.kaiju").withType(StandType.CLOTHES).withLocationIds("c9", "c10"),
				new Stand().withName("Fantasy House").withType(StandType.MERCH).withLocationIds("c11", "c12"),
				new Stand().withName("Sirolynia").withType(StandType.TABLETOP_GAMES).withLocationIds("c13", "c14", "c15", "c16"),
				new Stand().withName("Ascendant Fiction").withType(StandType.OTHER).withLocationIds("c19"),
				new Stand().withName("Toys of America").withType(StandType.OTHER).withLocationIds("c20", "c21", "c22"),
				new Stand().withName("מיסקייסיס").withType(StandType.OTHER).withLocationIds("c23", "c24", "c25", "c26"),
				new Stand().withName("BEADUS").withType(StandType.MERCH).withLocationIds("c27", "c28"),
				new Stand().withName("Funko V.I.P").withType(StandType.OTHER).withLocationIds("c29", "c30"),
				new Stand().withName("אנימה סטור").withType(StandType.MERCH).withLocationIds("c31", "c32", "c33", "c34", "c35", "c36"),
				new Stand().withName("Gaming land גיימינג לנד").withType(StandType.VIDEO_GAMES).withLocationIds("c37", "c38", "c39", "c40"),
				new Stand().withName("Respberry").withType(StandType.CLOTHES).withLocationIds("c41"),
				new Stand().withName("SweetHeartYun").withType(StandType.CLOTHES).withLocationIds("c42"),
				new Stand().withName("גיקפליז").withType(StandType.OTHER).withLocationIds("c43", "c44"),
				new Stand().withName("ארט סנטר").withType(StandType.OTHER).withLocationIds("c45", "c46", "c47", "c48"),
				new Stand().withName("HUJI sensei").withType(StandType.OTHER).withLocationIds("c49"),
				new Stand().withName("איגוד מקצועות האנימציה").withType(StandType.OTHER).withLocationIds("c50"),
				new Stand().withName("גברת וודו").withType(StandType.CLOTHES).withLocationIds("d1", "d2"),
				new Stand().withName("501st Legion").withType(StandType.OTHER).withLocationIds("d3"),
				new Stand().withName("Noyanny").withType(StandType.CLOTHES).withLocationIds("d4"),
				new Stand().withName("Angry customs").withType(StandType.MERCH).withLocationIds("d5", "d6"),
				new Stand().withName("המרכז ללימודי יפנית").withType(StandType.OTHER).withLocationIds("d7", "d8"),
				new Stand().withName("Jill._.Creations").withType(StandType.OTHER).withLocationIds("d9", "d10"),
				new Stand().withName("blueberry_crown").withType(StandType.CLOTHES).withLocationIds("d11"),
				new Stand().withName("Natoki").withType(StandType.CLOTHES).withLocationIds("d12"),
				new Stand().withName("NoamWool").withType(StandType.CLOTHES).withLocationIds("d13"),
				new Stand().withName("Resinbar").withType(StandType.OTHER).withLocationIds("d14"),
				new Stand().withName("Velvet Octopus").withType(StandType.CLOTHES).withLocationIds("d15", "d16"),
				new Stand().withName("מאי שירי design & art").withType(StandType.CLOTHES).withLocationIds("d17", "d18"),
				new Stand().withName("AKINAPAZ").withType(StandType.MERCH).withLocationIds("d19", "d20"),
				new Stand().withName("Kawaii land shop").withType(StandType.CLOTHES).withLocationIds("d21"),
				new Stand().withName("Pop-Storm").withType(StandType.MERCH).withLocationIds("d23", "d24"),
				new Stand().withName("TOKYO POSTERS XTR").withType(StandType.MERCH).withLocationIds("e1", "e2"),
				new Stand().withName("ComiXunity").withType(StandType.MANGA).withLocationIds("e5", "e6"),
				new Stand().withName("Aquamaren").withType(StandType.CLOTHES).withLocationIds("e7", "e8"),
				new Stand().withName("Frozen flawers").withType(StandType.CLOTHES).withLocationIds("e9", "e10"),
				new Stand().withName("Cathatart").withType(StandType.OTHER).withLocationIds("e11"),
				new Stand().withName("Haruugami").withType(StandType.MERCH).withLocationIds("e12"),
				new Stand().withName("אנימאג -Animug").withType(StandType.MERCH).withLocationIds("e13"),
				new Stand().withName("Japaneasy").withType(StandType.OTHER).withLocationIds("e14"),
				new Stand().withName("TVfox").withType(StandType.OTHER).withLocationIds("e15", "e16"),
				new Stand().withName("blup").withType(StandType.MANGA).withLocationIds("e17", "e18", "e19", "e20"),
				new Stand().withName("Candy Lenses").withType(StandType.MERCH).withLocationIds("e21", "e22", "e23", "e24")
		);
	}

	private List<Stand> getAgamStands() {
		return Arrays.asList(
				new Stand().withName("מאי ארט").withType(StandType.OTHER).withLocationIds("a1", "a2", "a3", "a4"),
				new Stand().withName("החתול הסגול").withType(StandType.CLOTHES).withLocationIds("a5", "a6", "a7", "a8"),
				new Stand().withName("Mini Tokio").withType(StandType.MERCH).withLocationIds("a9", "a10"),
				new Stand().withName("קומיקאזה").withType(StandType.MANGA).withLocationIds("a11", "a12", "a13"),
				new Stand().withName("N FIG").withType(StandType.MERCH).withLocationIds("a14"),
				new Stand().withName("דוכן שיפודן").withType(StandType.MERCH).withLocationIds("a15", "a16", "a17", "a18", "a19", "a20"),
				new Stand().withName("קומיקס וירקות").withType(StandType.MANGA).withLocationIds("a21", "a22", "a23", "a24"),
				new Stand().withName("הממלכה").withType(StandType.TABLETOP_GAMES).withLocationIds("a25", "a26"),
				new Stand().withName("אנימה וויב").withType(StandType.MERCH).withLocationIds("a27", "a28", "a29", "a30"),
				new Stand().withName("T&T").withType(StandType.CLOTHES).withLocationIds("a31", "a32"),
				new Stand().withName("אנימה שופ").withType(StandType.MERCH).withLocationIds("a33", "a34", "a35", "a36", "a37", "a38"),
				new Stand().withName("Anime Gifts").withType(StandType.MERCH).withLocationIds("a39", "a40", "a41", "a42"),
				new Stand().withName("Gran D. Line").withType(StandType.MERCH).withLocationIds("a43", "a44"),
				new Stand().withName("Vered Roze Art").withType(StandType.ARTIST).withLocationIds("b1", "b2"),
				new Stand().withName("דניהלמן ארט").withType(StandType.ARTIST).withLocationIds("b3"),
				new Stand().withName("Ked_creation").withType(StandType.ARTIST).withLocationIds("b4"),
				new Stand().withName("גריסים").withType(StandType.ARTIST).withLocationIds("b5", "b6"),
				new Stand().withName("תיקים מגניבים של Otaku").withType(StandType.ARTIST).withLocationIds("b7"),
				new Stand().withName("Shorterthan").withType(StandType.ARTIST).withLocationIds("b8", "b9"),
				new Stand().withName("TOTAL LOST").withType(StandType.ARTIST).withLocationIds("b10", "b11"),
				new Stand().withName("הוט גירל פורים").withType(StandType.ARTIST).withLocationIds("b12"),
				new Stand().withName("דוכן האמן של ליאן").withType(StandType.ARTIST).withLocationIds("b13"),
				new Stand().withName("Hikikomoring - Art by Sem Daniel").withType(StandType.ARTIST).withLocationIds("b14"),
				new Stand().withName("Shokolyr").withType(StandType.ARTIST).withLocationIds("b15"),
				new Stand().withName("Mariliya").withType(StandType.ARTIST).withLocationIds("b16", "b17"),
				new Stand().withName("טחינה").withType(StandType.ARTIST).withLocationIds("b18"),
				new Stand().withName("הדוחן").withType(StandType.ARTIST).withLocationIds("b19"),
				new Stand().withName("קומיקום").withType(StandType.ARTIST).withLocationIds("b20"),
				new Stand().withName("Kip, Eve & Co.").withType(StandType.ARTIST).withLocationIds("b21"),
				new Stand().withName("Strawberry flavors").withType(StandType.ARTIST).withLocationIds("b22"),
				new Stand().withName("Geek Aesthetics").withType(StandType.ARTIST).withLocationIds("b23"),
				new Stand().withName("Saigar Art").withType(StandType.ARTIST).withLocationIds("b24"),
				new Stand().withName("Kartzi's").withType(StandType.ARTIST).withLocationIds("b25", "b26"),
				new Stand().withName("OdeChan Art").withType(StandType.ARTIST).withLocationIds("b27"),
				new Stand().withName("צפרדע שוקולד").withType(StandType.ARTIST).withLocationIds("b28"),
				new Stand().withName("בועת מחשבה").withType(StandType.OTHER).withLocationIds("b29"),
				new Stand().withName("Sloth with a hat").withType(StandType.ARTIST).withLocationIds("b30"),
				new Stand().withName("cryptic arts").withType(StandType.ARTIST).withLocationIds("b31"),
				new Stand().withName("מאיירת מציאות").withType(StandType.ARTIST).withLocationIds("b32"),
				new Stand().withName("Foxerish").withType(StandType.ARTIST).withLocationIds("b33"),
				new Stand().withName("עולם החיות של בת-חן צרפתי").withType(StandType.ARTIST).withLocationIds("b34", "b35"),
				new Stand().withName("ROTEMZ ART").withType(StandType.ARTIST).withLocationIds("b36"),
				new Stand().withName("Martin Draws").withType(StandType.ARTIST).withLocationIds("b37"),
				new Stand().withName("Sweetie's Stand").withType(StandType.ARTIST).withLocationIds("b38"),
				new Stand().withName("האומנות של ריי").withType(StandType.ARTIST).withLocationIds("b39"),
				new Stand().withName("Haruhi Chili").withType(StandType.ARTIST).withLocationIds("b40"),
				new Stand().withName("Tomato Eater").withType(StandType.ARTIST).withLocationIds("b41"),
				new Stand().withName("SHIR K").withType(StandType.ARTIST).withLocationIds("b42", "b43"),
				new Stand().withName("Orchibald art").withType(StandType.ARTIST).withLocationIds("b44", "b45"),
				new Stand().withName("Ozart").withType(StandType.ARTIST).withLocationIds("b46", "b47"),
				new Stand().withName("fishiebug").withType(StandType.ARTIST).withLocationIds("b48"),
				new Stand().withName("Meiiior").withType(StandType.ARTIST).withLocationIds("b49"),
				new Stand().withName("קבוצת יצירת קומיקס").withType(StandType.MANGA).withLocationIds("b50", "b51"),
				new Stand().withName("kukeshii").withType(StandType.ARTIST).withLocationIds("b52"),
				new Stand().withName("koruhiko").withType(StandType.ARTIST).withLocationIds("b53", "b54"),
				new Stand().withName("AniPug").withType(StandType.ARTIST).withLocationIds("b55"),
				new Stand().withName("Gabisweb").withType(StandType.ARTIST).withLocationIds("b56")
		);
	}

	private StandLocations getAgamStandLocations() {
		float defaultWidth = 60;
		float defaultHeight = 60;

		// Top row
		float a1Top = 57;
		float a1Left = 126;
		float a9Top = a1Top;
		float a9Left = 655;
		float a15Top = a1Top;
		float a15Left = 1069;
		float a21Top = a1Top;
		float a21Left = 1484;
		float a27Top = a1Top;
		float a27Left = 1884;
		float a33Top = a1Top;
		float a33Left = 2282;

		// Bottom left column
		float a39Top = 616;
		float a39Left = 57;

		// Bottom right column
		float a43Top = 561;
		float a43Left = 2582;

		// Left square
		float b1Top = 413;
		float b1Left = 904;
		float b9Top = 799;
		float b9Left = 620;
		float b15Top = b1Top;
		float b15Left = 516;
		float b16Top = 330;
		float b16Left = 590;

		// Middle square
		float b20Top = 502;
		float b20Left = 1445;
		float b28Top = 801;
		float b28Left = 1205;
		float b33Top = 405;
		float b33Left = 1113;
		float b34Top = 345;
		float b34Left = 1173;

		// Right square
		float b38Top = b34Top;
		float b38Left = 1859;
		float b42Top = 417;
		float b42Left = 2123;
		float b52Top = b28Top;
		float b52Left = 1792;
		float b57Top = 404;
		float b57Left = 1728;

		return new StandLocations(
				// As
				StandLocation.fromWidths("a1", "a01", "a2", a1Left, defaultWidth, a1Top, defaultHeight),
				StandLocation.fromWidths("a2", "a02", "a3", a1Left + defaultWidth, defaultWidth, a1Top, defaultHeight),
				StandLocation.fromWidths("a3", "a03", "a4", a1Left + 2*defaultWidth, defaultWidth, a1Top, defaultHeight),
				StandLocation.fromWidths("a4", "a04", "a5", a1Left + 3*defaultWidth, defaultWidth, a1Top, defaultHeight),
				StandLocation.fromWidths("a5", "a05", "a6", a1Left + 4*defaultWidth, defaultWidth, a1Top, defaultHeight),
				StandLocation.fromWidths("a6", "a06", "a7", a1Left + 5*defaultWidth, defaultWidth, a1Top, defaultHeight),
				StandLocation.fromWidths("a7", "a07", "a8", a1Left + 6*defaultWidth, defaultWidth, a1Top, defaultHeight),
				StandLocation.fromWidths("a8", "a08", "a9", a1Left + 7*defaultWidth, defaultWidth, a1Top, defaultHeight),

				StandLocation.fromWidths("a9", "a09", "a10", a9Left, defaultWidth, a9Top, defaultHeight),
				StandLocation.fromWidths("a10", "a10", "a11", a9Left + defaultWidth, defaultWidth, a9Top, defaultHeight),
				StandLocation.fromWidths("a11", "a11", "a12", a9Left + 2*defaultWidth, defaultWidth, a9Top, defaultHeight),
				StandLocation.fromWidths("a12", "a12", "a13", a9Left + 3*defaultWidth, defaultWidth, a9Top, defaultHeight),
				StandLocation.fromWidths("a13", "a13", "a14", a9Left + 4*defaultWidth, defaultWidth, a9Top, defaultHeight),
				StandLocation.fromWidths("a14", "a14", "a15", a9Left + 5*defaultWidth, defaultWidth, a9Top, defaultHeight),

				StandLocation.fromWidths("a15", "a15", "a16", a15Left, defaultWidth, a15Top, defaultHeight),
				StandLocation.fromWidths("a16", "a16", "a17", a15Left + defaultWidth, defaultWidth, a15Top, defaultHeight),
				StandLocation.fromWidths("a17", "a17", "a18", a15Left + 2*defaultWidth, defaultWidth, a15Top, defaultHeight),
				StandLocation.fromWidths("a18", "a18", "a19", a15Left + 3*defaultWidth, defaultWidth, a15Top, defaultHeight),
				StandLocation.fromWidths("a19", "a19", "a20", a15Left + 4*defaultWidth, defaultWidth, a15Top, defaultHeight),
				StandLocation.fromWidths("a20", "a20", "a21", a15Left + 5*defaultWidth, defaultWidth, a15Top, defaultHeight),

				StandLocation.fromWidths("a21", "a21", "a22", a21Left, defaultWidth, a21Top, defaultHeight),
				StandLocation.fromWidths("a22", "a22", "a23", a21Left + defaultWidth, defaultWidth, a21Top, defaultHeight),
				StandLocation.fromWidths("a23", "a23", "a24", a21Left + 2*defaultWidth, defaultWidth, a21Top, defaultHeight),
				StandLocation.fromWidths("a24", "a24", "a25", a21Left + 3*defaultWidth, defaultWidth, a21Top, defaultHeight),
				StandLocation.fromWidths("a25", "a25", "a26", a21Left + 4*defaultWidth, defaultWidth, a21Top, defaultHeight),
				StandLocation.fromWidths("a26", "a26", "a27", a21Left + 5*defaultWidth, defaultWidth, a21Top, defaultHeight),

				StandLocation.fromWidths("a27", "a27", "a28", a27Left, defaultWidth, a27Top, defaultHeight),
				StandLocation.fromWidths("a28", "a28", "a29", a27Left + defaultWidth, defaultWidth, a27Top, defaultHeight),
				StandLocation.fromWidths("a29", "a29", "a30", a27Left + 2*defaultWidth, defaultWidth, a27Top, defaultHeight),
				StandLocation.fromWidths("a30", "a30", "a31", a27Left + 3*defaultWidth, defaultWidth, a27Top, defaultHeight),
				StandLocation.fromWidths("a31", "a31", "a32", a27Left + 4*defaultWidth, defaultWidth, a27Top, defaultHeight),
				StandLocation.fromWidths("a32", "a32", "a33", a27Left + 5*defaultWidth, defaultWidth, a27Top, defaultHeight),

				StandLocation.fromWidths("a33", "a33", "a34", a33Left, defaultWidth, a33Top, defaultHeight),
				StandLocation.fromWidths("a34", "a34", "a35", a33Left + defaultWidth, defaultWidth, a33Top, defaultHeight),
				StandLocation.fromWidths("a35", "a35", "a36", a33Left + 2*defaultWidth, defaultWidth, a33Top, defaultHeight),
				StandLocation.fromWidths("a36", "a36", "a37", a33Left + 3*defaultWidth, defaultWidth, a33Top, defaultHeight),
				StandLocation.fromWidths("a37", "a37", "a38", a33Left + 4*defaultWidth, defaultWidth, a33Top, defaultHeight),
				StandLocation.fromWidths("a38", "a38", null, a33Left + 5*defaultWidth, defaultWidth, a33Top, defaultHeight),

				StandLocation.fromWidths("a39", "a39", "a40", a39Left, defaultWidth, a39Top, defaultHeight),
				StandLocation.fromWidths("a40", "a40", "a41", a39Left, defaultWidth, a39Top + defaultHeight, defaultHeight),
				StandLocation.fromWidths("a41", "a41", "a42", a39Left, defaultWidth, a39Top + 2*defaultHeight, defaultHeight),
				StandLocation.fromWidths("a42", "a42", null, a39Left, defaultWidth, a39Top + 3*defaultHeight, defaultHeight),

				StandLocation.fromWidths("a43", "a43", "a44", a43Left, defaultWidth, a43Top, defaultHeight),
				StandLocation.fromWidths("a44", "a44", "a45", a43Left, defaultWidth, a43Top + defaultHeight, defaultHeight),
				StandLocation.fromWidths("a45", "a45", "a46", a43Left, defaultWidth, a43Top + 2*defaultHeight, defaultHeight),
				StandLocation.fromWidths("a46", "a46", null, a43Left, defaultWidth, a43Top + 3*defaultHeight, defaultHeight),

				// Bs
				StandLocation.fromWidths("b1", "b01", "b2", b1Left, defaultWidth, b1Top, defaultHeight),
				StandLocation.fromWidths("b2", "b02", "b3", b1Left, defaultWidth, b1Top + defaultHeight, defaultHeight),
				StandLocation.fromWidths("b3", "b03", "b4", b1Left, defaultWidth, b1Top + 2*defaultHeight, defaultHeight),
				StandLocation.fromWidths("b4", "b04", "b5", b1Left, defaultWidth, b1Top + 3*defaultHeight, defaultHeight),

				StandLocation.fromWidths("b5", "b05", "b6", b9Left + 4*defaultWidth, defaultWidth, b9Top, defaultHeight),
				StandLocation.fromWidths("b6", "b06", "b7", b9Left + 3*defaultWidth, defaultWidth, b9Top, defaultHeight),
				StandLocation.fromWidths("b7", "b07", "b8", b9Left + 2*defaultWidth, defaultWidth, b9Top, defaultHeight),
				StandLocation.fromWidths("b8", "b08", "b9", b9Left + defaultWidth, defaultWidth, b9Top, defaultHeight),
				StandLocation.fromWidths("b9", "b09", "b10", b9Left, defaultWidth, b9Top, defaultHeight),

				StandLocation.fromWidths("b10", "b10", "b11", b15Left, defaultWidth, b15Top + 5*defaultHeight, defaultHeight),
				StandLocation.fromWidths("b11", "b11", "b12", b15Left, defaultWidth, b15Top + 4*defaultHeight, defaultHeight),
				StandLocation.fromWidths("b12", "b12", "b13", b15Left, defaultWidth, b15Top + 3*defaultHeight, defaultHeight),
				StandLocation.fromWidths("b13", "b13", "b14", b15Left, defaultWidth, b15Top + 2*defaultHeight, defaultHeight),
				StandLocation.fromWidths("b14", "b14", "b15", b15Left, defaultWidth, b15Top + defaultHeight, defaultHeight),
				StandLocation.fromWidths("b15", "b15", "b16", b15Left, defaultWidth, b15Top, defaultHeight),

				StandLocation.fromWidths("b16", "b16", "b17", b16Left, defaultWidth, b16Top, defaultHeight),
				StandLocation.fromWidths("b17", "b17", "b18", b16Left + defaultWidth, defaultWidth, b16Top, defaultHeight),
				StandLocation.fromWidths("b18", "b18", "b19", b16Left + 2*defaultWidth, defaultWidth, b16Top, defaultHeight),
				StandLocation.fromWidths("b19", "b19", "b1", b16Left + 3*defaultWidth, defaultWidth, b16Top, defaultHeight),

				StandLocation.fromWidths("b20", "b20", "b21", b20Left, defaultWidth, b20Top, defaultHeight),
				StandLocation.fromWidths("b21", "b21", "b22", b20Left, defaultWidth, b20Top + defaultHeight, defaultHeight),
				StandLocation.fromWidths("b22", "b22", "b23", b20Left, defaultWidth, b20Top + 2*defaultHeight, defaultHeight),
				StandLocation.fromWidths("b23", "b23", "b24", b20Left, defaultWidth, b20Top + 3*defaultHeight, defaultHeight),
				StandLocation.fromWidths("b24", "b24", "b25", b20Left, defaultWidth, b20Top + 4*defaultHeight, defaultHeight),

				StandLocation.fromWidths("b25", "b25", "b26", b28Left + 3*defaultWidth, defaultWidth, b28Top, defaultHeight),
				StandLocation.fromWidths("b26", "b26", "b27", b28Left + 2*defaultWidth, defaultWidth, b28Top, defaultHeight),
				StandLocation.fromWidths("b27", "b27", "b28", b28Left + defaultWidth, defaultWidth, b28Top, defaultHeight),
				StandLocation.fromWidths("b28", "b28", "b29", b28Left, defaultWidth, b28Top, defaultHeight),

				StandLocation.fromWidths("b29", "b29", "b30", b33Left, defaultWidth, b33Top + 4*defaultHeight, defaultHeight),
				StandLocation.fromWidths("b30", "b30", "b31", b33Left, defaultWidth, b33Top + 3*defaultHeight, defaultHeight),
				StandLocation.fromWidths("b31", "b31", "b32", b33Left, defaultWidth, b33Top + 2*defaultHeight, defaultHeight),
				StandLocation.fromWidths("b32", "b32", "b33", b33Left, defaultWidth, b33Top + defaultHeight, defaultHeight),
				StandLocation.fromWidths("b33", "b33", "b34", b33Left, defaultWidth, b33Top, defaultHeight),

				StandLocation.fromWidths("b34", "b34", "b35", b34Left, defaultWidth, b34Top, defaultHeight),
				StandLocation.fromWidths("b35", "b35", "b36", b34Left + defaultWidth, defaultWidth, b34Top, defaultHeight),
				StandLocation.fromWidths("b36", "b36", "b37", b34Left + 2*defaultWidth, defaultWidth, b34Top, defaultHeight),
				StandLocation.fromWidths("b37", "b37", "b20", b34Left + 3*defaultWidth, defaultWidth, b34Top, defaultHeight),

				StandLocation.fromWidths("b38", "b38", "b39", b38Left, defaultWidth, b38Top, defaultHeight),
				StandLocation.fromWidths("b39", "b39", "b40", b38Left + defaultWidth, defaultWidth, b38Top, defaultHeight),
				StandLocation.fromWidths("b40", "b40", "b41", b38Left + 2*defaultWidth, defaultWidth, b38Top, defaultHeight),
				StandLocation.fromWidths("b41", "b41", "b42", b38Left + 3*defaultWidth, defaultWidth, b38Top, defaultHeight),

				StandLocation.fromWidths("b42", "b42", "b43", b42Left, defaultWidth, b42Top, defaultHeight),
				StandLocation.fromWidths("b43", "b43", "b44", b42Left, defaultWidth, b42Top + defaultHeight, defaultHeight),
				StandLocation.fromWidths("b44", "b44", "b45", b42Left, defaultWidth, b42Top + 2*defaultHeight, defaultHeight),
				StandLocation.fromWidths("b45", "b45", "b46", b42Left, defaultWidth, b42Top + 3*defaultHeight, defaultHeight),
				StandLocation.fromWidths("b46", "b46", "b47", b42Left, defaultWidth, b42Top + 4*defaultHeight, defaultHeight),
				StandLocation.fromWidths("b47", "b47", "b48", b42Left, defaultWidth, b42Top + 5*defaultHeight, defaultHeight),

				StandLocation.fromWidths("b48", "b48", "b49", b52Left + 4*defaultWidth, defaultWidth, b52Top, defaultHeight),
				StandLocation.fromWidths("b49", "b49", "b50", b52Left + 3*defaultWidth, defaultWidth, b52Top, defaultHeight),
				StandLocation.fromWidths("b50", "b50", "b51", b52Left + 2*defaultWidth, defaultWidth, b52Top, defaultHeight),
				StandLocation.fromWidths("b51", "b51", "b52", b52Left + defaultWidth, defaultWidth, b52Top, defaultHeight),
				StandLocation.fromWidths("b52", "b52", "b53", b52Left, defaultWidth, b52Top, defaultHeight),

				StandLocation.fromWidths("b53", "b53", "b54", b57Left, defaultWidth, b57Top + 4*defaultHeight, defaultHeight),
				StandLocation.fromWidths("b54", "b54", "b55", b57Left, defaultWidth, b57Top + 3*defaultHeight, defaultHeight),
				StandLocation.fromWidths("b55", "b55", "b56", b57Left, defaultWidth, b57Top + 2*defaultHeight, defaultHeight),
				StandLocation.fromWidths("b56", "b56", "b57", b57Left, defaultWidth, b57Top + defaultHeight, defaultHeight),
				StandLocation.fromWidths("b57", "b57", "b38", b57Left, defaultWidth, b57Top, defaultHeight)
		);
	}

	private StandLocations getPinkusStandLocations() {
		float defaultWidth = 88;
		float defaultHeight = 88;

		// Top row
		float c1Top = 83;
		float c1Left = 83;
		float c9Top = c1Top;
		float c9Left = 996;
		float c13Top = c1Top;
		float c13Left = 1451;

		// Right column
		float c23Top = 461;
		float c23Left = 2369;

		// Bottom row
		float c36Top = 1647;
		float c36Left = 2092;
		float c42Top = c36Top;
		float c42Left = 1457;
		float c48Top = c36Top;
		float c48Left = 812;

		// Bottom left column
		float c49Top = 1744;
		float c49Left = 718;

		// Middle square
		float d1Top = 549;
		float d1Left = 1806;
		float d3Top = 636;
		float d3Left = 1982;
		float d14Top = 1159;
		float d14Left = 1459;
		float d20Top = d3Top;
		float d20Left = 1268;
		float d21Top = d1Top;
		float d21Left = 1355;

		// Left square
		float e1Top = d1Top;
		float e1Left = 723;
		float e3Top = d3Top;
		float e3Left = 900;
		float e10Top = d14Top;
		float e10Left = 725;
		float e14Top = e10Top;
		float e14Left = 273;
		float e20Top = e3Top;
		float e20Left = 185;
		float e21Top = e1Top;
		float e21Left = e14Left;


		return new StandLocations(
				// Cs
				StandLocation.fromWidths("c1", "c01", "c2", c1Left, defaultWidth, c1Top, defaultHeight),
				StandLocation.fromWidths("c2", "c02", "c3", c1Left + defaultWidth, defaultWidth, c1Top, defaultHeight),
				StandLocation.fromWidths("c3", "c03", "c4", c1Left + 2*defaultWidth, defaultWidth, c1Top, defaultHeight),
				StandLocation.fromWidths("c4", "c04", "c5", c1Left + 3*defaultWidth, defaultWidth, c1Top, defaultHeight),
				StandLocation.fromWidths("c5", "c05", "c6", c1Left + 4*defaultWidth, defaultWidth, c1Top, defaultHeight),
				StandLocation.fromWidths("c6", "c06", "c7", c1Left + 5*defaultWidth, defaultWidth, c1Top, defaultHeight),
				StandLocation.fromWidths("c7", "c07", "c8", c1Left + 6*defaultWidth, defaultWidth, c1Top, defaultHeight),
				StandLocation.fromWidths("c8", "c08", "c9", c1Left + 7*defaultWidth, defaultWidth, c1Top, defaultHeight),

				StandLocation.fromWidths("c9", "c09", "c10", c9Left, defaultWidth, c9Top, defaultHeight),
				StandLocation.fromWidths("c10", "c10", "c11", c9Left + defaultWidth, defaultWidth, c9Top, defaultHeight),
				StandLocation.fromWidths("c11", "c11", "c12", c9Left + 2*defaultWidth, defaultWidth, c9Top, defaultHeight),
				StandLocation.fromWidths("c12", "c12", "c13", c9Left + 3*defaultWidth, defaultWidth, c9Top, defaultHeight),

				StandLocation.fromWidths("c13", "c13", "c14", c13Left, defaultWidth, c13Top, defaultHeight),
				StandLocation.fromWidths("c14", "c14", "c15", c13Left + defaultWidth, defaultWidth, c13Top, defaultHeight),
				StandLocation.fromWidths("c15", "c15", "c16", c13Left + 2*defaultWidth, defaultWidth, c13Top, defaultHeight),
				StandLocation.fromWidths("c16", "c16", "c17", c13Left + 3*defaultWidth, defaultWidth, c13Top, defaultHeight),
				StandLocation.fromWidths("c17", "c17", "c18", c13Left + 4*defaultWidth, defaultWidth, c13Top, defaultHeight),
				StandLocation.fromWidths("c18", "c18", "c19", c13Left + 5*defaultWidth, defaultWidth, c13Top, defaultHeight),
				StandLocation.fromWidths("c19", "c19", "c20", c13Left + 6*defaultWidth, defaultWidth, c13Top, defaultHeight),
				StandLocation.fromWidths("c20", "c20", "c21", c13Left + 7*defaultWidth, defaultWidth, c13Top, defaultHeight),
				StandLocation.fromWidths("c21", "c21", "c22", c13Left + 8*defaultWidth, defaultWidth, c13Top, defaultHeight),
				StandLocation.fromWidths("c22", "c22", null, c13Left + 9*defaultWidth, defaultWidth, c13Top, defaultHeight),

				StandLocation.fromWidths("c23", "c23", "c24", c23Left, defaultWidth, c23Top, defaultHeight),
				StandLocation.fromWidths("c24", "c24", "c25", c23Left, defaultWidth, c23Top + defaultHeight, defaultHeight),
				StandLocation.fromWidths("c25", "c25", "c26", c23Left, defaultWidth, c23Top + 2*defaultHeight, defaultHeight),
				StandLocation.fromWidths("c26", "c26", "c27", c23Left, defaultWidth, c23Top + 3*defaultHeight, defaultHeight),
				StandLocation.fromWidths("c27", "c27", "c28", c23Left, defaultWidth, c23Top + 4*defaultHeight, defaultHeight),
				StandLocation.fromWidths("c28", "c28", "c29", c23Left, defaultWidth, c23Top + 5*defaultHeight, defaultHeight),
				StandLocation.fromWidths("c29", "c29", "c30", c23Left, defaultWidth, c23Top + 6*defaultHeight, defaultHeight),
				StandLocation.fromWidths("c30", "c30", null, c23Left, defaultWidth, c23Top + 7*defaultHeight, defaultHeight),

				StandLocation.fromWidths("c31", "c31", "c32", c36Left + 5*defaultWidth, defaultWidth, c36Top, defaultHeight),
				StandLocation.fromWidths("c32", "c32", "c33", c36Left + 4*defaultWidth, defaultWidth, c36Top, defaultHeight),
				StandLocation.fromWidths("c33", "c33", "c34", c36Left + 3*defaultWidth, defaultWidth, c36Top, defaultHeight),
				StandLocation.fromWidths("c34", "c34", "c35", c36Left + 2*defaultWidth, defaultWidth, c36Top, defaultHeight),
				StandLocation.fromWidths("c35", "c35", "c36", c36Left + defaultWidth, defaultWidth, c36Top, defaultHeight),
				StandLocation.fromWidths("c36", "c36", "c37", c36Left, defaultWidth, c36Top, defaultHeight),

				StandLocation.fromWidths("c37", "c37", "c38", c42Left + 5*defaultWidth, defaultWidth, c42Top, defaultHeight),
				StandLocation.fromWidths("c38", "c38", "c39", c42Left + 4*defaultWidth, defaultWidth, c42Top, defaultHeight),
				StandLocation.fromWidths("c39", "c39", "c40", c42Left + 3*defaultWidth, defaultWidth, c42Top, defaultHeight),
				StandLocation.fromWidths("c40", "c40", "c41", c42Left + 2*defaultWidth, defaultWidth, c42Top, defaultHeight),
				StandLocation.fromWidths("c41", "c41", "c42", c42Left + defaultWidth, defaultWidth, c42Top, defaultHeight),
				StandLocation.fromWidths("c42", "c42", "c43", c42Left, defaultWidth, c42Top, defaultHeight),

				StandLocation.fromWidths("c43", "c43", "c44", c48Left + 5*defaultWidth, defaultWidth, c48Top, defaultHeight),
				StandLocation.fromWidths("c44", "c44", "c45", c48Left + 4*defaultWidth, defaultWidth, c48Top, defaultHeight),
				StandLocation.fromWidths("c45", "c45", "c46", c48Left + 3*defaultWidth, defaultWidth, c48Top, defaultHeight),
				StandLocation.fromWidths("c46", "c46", "c47", c48Left + 2*defaultWidth, defaultWidth, c48Top, defaultHeight),
				StandLocation.fromWidths("c47", "c47", "c48", c48Left + defaultWidth, defaultWidth, c48Top, defaultHeight),
				StandLocation.fromWidths("c48", "c48", "c49", c48Left, defaultWidth, c48Top, defaultHeight),

				StandLocation.fromWidths("c49", "c49", "c50", c49Left, defaultWidth, c49Top, defaultHeight),
				StandLocation.fromWidths("c50", "c50", null, c49Left, defaultWidth, c49Top + defaultHeight, defaultHeight),

				// Ds
				StandLocation.fromWidths("d1", "d01", "d2", d1Left, defaultWidth, d1Top, defaultHeight),
				StandLocation.fromWidths("d2", "d02", "d3", d1Left + defaultWidth, defaultWidth, d1Top, defaultHeight),

				StandLocation.fromWidths("d3", "d03", "d4", d3Left, defaultWidth, d3Top, defaultHeight),
				StandLocation.fromWidths("d4", "d04", "d5", d3Left, defaultWidth, d3Top + defaultHeight, defaultHeight),
				StandLocation.fromWidths("d5", "d05", "d6", d3Left, defaultWidth, d3Top + 2*defaultHeight, defaultHeight),
				StandLocation.fromWidths("d6", "d06", "d7", d3Left, defaultWidth, d3Top + 3*defaultHeight, defaultHeight),
				StandLocation.fromWidths("d7", "d07", "d8", d3Left, defaultWidth, d3Top + 4*defaultHeight, defaultHeight),
				StandLocation.fromWidths("d8", "d08", "d9", d3Left, defaultWidth, d3Top + 5*defaultHeight, defaultHeight),

				StandLocation.fromWidths("d9", "d09", "d10", d14Left + 5*defaultWidth, defaultWidth, d14Top, defaultHeight),
				StandLocation.fromWidths("d10", "d10", "d11", d14Left + 4*defaultWidth, defaultWidth, d14Top, defaultHeight),
				StandLocation.fromWidths("d11", "d11", "d12", d14Left + 3*defaultWidth, defaultWidth, d14Top, defaultHeight),
				StandLocation.fromWidths("d12", "d12", "d13", d14Left + 2*defaultWidth, defaultWidth, d14Top, defaultHeight),
				StandLocation.fromWidths("d13", "d13", "d14", d14Left + defaultWidth, defaultWidth, d14Top, defaultHeight),
				StandLocation.fromWidths("d14", "d14", "d15", d14Left, defaultWidth, d14Top, defaultHeight),

				StandLocation.fromWidths("d15", "d15", "d16", d20Left, defaultWidth, d20Top + 5*defaultHeight, defaultHeight),
				StandLocation.fromWidths("d16", "d16", "d17", d20Left, defaultWidth, d20Top + 4*defaultHeight, defaultHeight),
				StandLocation.fromWidths("d17", "d17", "d18", d20Left, defaultWidth, d20Top + 3*defaultHeight, defaultHeight),
				StandLocation.fromWidths("d18", "d18", "d19", d20Left, defaultWidth, d20Top + 2*defaultHeight, defaultHeight),
				StandLocation.fromWidths("d19", "d19", "d20", d20Left, defaultWidth, d20Top + defaultHeight, defaultHeight),
				StandLocation.fromWidths("d20", "d20", "d21", d20Left, defaultWidth, d20Top, defaultHeight),

				StandLocation.fromWidths("d21", "d21", "d22", d21Left, defaultWidth, d21Top, defaultHeight),
				StandLocation.fromWidths("d22", "d22", "d23", d21Left + defaultWidth, defaultWidth, d21Top, defaultHeight),
				StandLocation.fromWidths("d23", "d23", "d24", d21Left + 2*defaultWidth, defaultWidth, d21Top, defaultHeight),
				StandLocation.fromWidths("d24", "d24", "d1", d21Left + 3*defaultWidth, defaultWidth, d21Top, defaultHeight),

				// Es
				StandLocation.fromWidths("e1", "e01", "e2", e1Left, defaultWidth, e1Top, defaultHeight),
				StandLocation.fromWidths("e2", "e02", "e3", e1Left + defaultWidth, defaultWidth, e1Top, defaultHeight),

				StandLocation.fromWidths("e3", "e03", "e4", e3Left, defaultWidth, e3Top, defaultHeight),
				StandLocation.fromWidths("e4", "e04", "e5", e3Left, defaultWidth, e3Top + defaultHeight, defaultHeight),
				StandLocation.fromWidths("e5", "e05", "e6", e3Left, defaultWidth, e3Top + 2*defaultHeight, defaultHeight),
				StandLocation.fromWidths("e6", "e06", "e7", e3Left, defaultWidth, e3Top + 3*defaultHeight, defaultHeight),
				StandLocation.fromWidths("e7", "e07", "e8", e3Left, defaultWidth, e3Top + 4*defaultHeight, defaultHeight),
				StandLocation.fromWidths("e8", "e08", "e9", e3Left, defaultWidth, e3Top + 5*defaultHeight, defaultHeight),

				StandLocation.fromWidths("e9", "e09", "e10", e10Left + defaultWidth, defaultWidth, e10Top, defaultHeight),
				StandLocation.fromWidths("e10", "e10", "e11", e10Left, defaultWidth, e10Top, defaultHeight),

				StandLocation.fromWidths("e11", "e11", "e12", e14Left + 3*defaultWidth, defaultWidth, e14Top, defaultHeight),
				StandLocation.fromWidths("e12", "e12", "e13", e14Left + 2*defaultWidth, defaultWidth, e14Top, defaultHeight),
				StandLocation.fromWidths("e13", "e13", "e14", e14Left + defaultWidth, defaultWidth, e14Top, defaultHeight),
				StandLocation.fromWidths("e14", "e14", "e15", e14Left, defaultWidth, e14Top, defaultHeight),

				StandLocation.fromWidths("e15", "e15", "e16", e20Left, defaultWidth, e20Top + 5*defaultHeight, defaultHeight),
				StandLocation.fromWidths("e16", "e16", "e17", e20Left, defaultWidth, e20Top + 4*defaultHeight, defaultHeight),
				StandLocation.fromWidths("e17", "e17", "e18", e20Left, defaultWidth, e20Top + 3*defaultHeight, defaultHeight),
				StandLocation.fromWidths("e18", "e18", "e19", e20Left, defaultWidth, e20Top + 2*defaultHeight, defaultHeight),
				StandLocation.fromWidths("e19", "e19", "e20", e20Left, defaultWidth, e20Top + defaultHeight, defaultHeight),
				StandLocation.fromWidths("e20", "e20", "e21", e20Left, defaultWidth, e20Top, defaultHeight),

				StandLocation.fromWidths("e21", "e21", "e22", e21Left, defaultWidth, e21Top, defaultHeight),
				StandLocation.fromWidths("e22", "e22", "e23", e21Left + defaultWidth, defaultWidth, e21Top, defaultHeight),
				StandLocation.fromWidths("e23", "e23", "e24", e21Left + 2*defaultWidth, defaultWidth, e21Top, defaultHeight),
				StandLocation.fromWidths("e24", "e24", "e1", e21Left + 3*defaultWidth, defaultWidth, e21Top, defaultHeight)
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

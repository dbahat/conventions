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
		return new Halls(Arrays.asList(mainHall, auditorium, eshkol1, eshkol2, eshkol3, workshops, cosplayArea, games));
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

		// Disabling the stands image resources until we have the maps
		StandsArea agam = new StandsArea().withName("טרקלין אגם").withStands(getAgamStands());//.withImageResource(R.drawable.harucon2020_stands_map_agam).withImageWidth(2700).withImageHeight(1069);
		StandsArea pinkus = new StandsArea().withName("אולם פינקוס").withStands(getPinkusStands());//.withImageResource(R.drawable.harucon2020_stands_map_pinkus).withImageWidth(2700).withImageHeight(1596);
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
												.withPlace(new Place().withName("חדר הקרנות"))
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
				new Stand().withName("נקסוס").withLocationName("C1-C06").withType(StandType.TABLETOP_GAMES)
				,new Stand().withName("sampai designs").withLocationName("C7-C8").withType(StandType.CLOTHES)
				,new Stand().withName("U.kaiju").withLocationName("C9-C10").withType(StandType.CLOTHES)
				,new Stand().withName("Fantasy House").withLocationName("C11-C12").withType(StandType.MERCH)
				,new Stand().withName("Sirolynia").withLocationName("C13-C16").withType(StandType.TABLETOP_GAMES)
				,new Stand().withName("Ascendant Fiction").withLocationName("C19").withType(StandType.OTHER)
				,new Stand().withName("Toys of America").withLocationName("C20-C22").withType(StandType.OTHER)
				,new Stand().withName("מיסקייסיס").withLocationName("C23-C26").withType(StandType.OTHER)
				,new Stand().withName("BEADUS").withLocationName("C27-C28").withType(StandType.MERCH)
				,new Stand().withName("Funko V.I.P").withLocationName("C29-C30").withType(StandType.OTHER)
				,new Stand().withName("אנימה סטור").withLocationName("C31-C36").withType(StandType.MERCH)
				,new Stand().withName("Gaming land גיימינג לנד").withLocationName("C37-C40").withType(StandType.VIDEO_GAMES)
				,new Stand().withName("Respberry").withLocationName("C41").withType(StandType.CLOTHES)
				,new Stand().withName("SweetHeartYun").withLocationName("C42").withType(StandType.CLOTHES)
				,new Stand().withName("גיקפליז").withLocationName("C43-C44").withType(StandType.OTHER)
				,new Stand().withName("ארט סנטר").withLocationName("C45-C48").withType(StandType.OTHER)
				,new Stand().withName("HUJI sensei").withLocationName("C49").withType(StandType.OTHER)
				,new Stand().withName("איגוד מקצועות האנימציה").withLocationName("C50").withType(StandType.OTHER)
				,new Stand().withName("גברת וודו").withLocationName("D1-D2").withType(StandType.CLOTHES)
				,new Stand().withName("501st Legion").withLocationName("D3").withType(StandType.OTHER)
				,new Stand().withName("Noyanny").withLocationName("D4").withType(StandType.CLOTHES)
				,new Stand().withName("Angry customs").withLocationName("D5-D6").withType(StandType.MERCH)
				,new Stand().withName("המרכז ללימודי יפנית").withLocationName("D7-D8").withType(StandType.OTHER)
				,new Stand().withName("Jill._.Creations").withLocationName("D9-D10").withType(StandType.OTHER)
				,new Stand().withName("blueberry_crown").withLocationName("D11").withType(StandType.CLOTHES)
				,new Stand().withName("Natoki").withLocationName("D12").withType(StandType.CLOTHES)
				,new Stand().withName("NoamWool").withLocationName("D13").withType(StandType.CLOTHES)
				,new Stand().withName("Resinbar").withLocationName("D14").withType(StandType.OTHER)
				,new Stand().withName("Velvet Octopus").withLocationName("D15-D16").withType(StandType.CLOTHES)
				,new Stand().withName("מאי שירי  design & art").withLocationName("D17-D18").withType(StandType.CLOTHES)
				,new Stand().withName("AKINAPAZ").withLocationName("D19-D20").withType(StandType.MERCH)
				,new Stand().withName("Kawaii land shop").withLocationName("D21").withType(StandType.CLOTHES)
				,new Stand().withName("Pop-Storm").withLocationName("D23-D24").withType(StandType.MERCH)
				,new Stand().withName("TOKYO POSTERS XTR").withLocationName("E1-E2").withType(StandType.MERCH)
				,new Stand().withName("ComiXunity").withLocationName("E5-D6").withType(StandType.MANGA)
				,new Stand().withName("Aquamaren").withLocationName("E7-E8").withType(StandType.CLOTHES)
				,new Stand().withName("Frozen flawers").withLocationName("E9-E10").withType(StandType.CLOTHES)
				,new Stand().withName("Cathatart").withLocationName("E11").withType(StandType.OTHER)
				,new Stand().withName("Haruugami").withLocationName("E12").withType(StandType.MERCH)
				,new Stand().withName("אנימאג -Animug").withLocationName("E13").withType(StandType.MERCH)
				,new Stand().withName("Japaneasy").withLocationName("E14").withType(StandType.OTHER)
				,new Stand().withName("TVfox").withLocationName("E15-E16").withType(StandType.OTHER)
				,new Stand().withName("blup").withLocationName("E17-E20").withType(StandType.MANGA)
				,new Stand().withName("Candy Lenses").withLocationName("E21-E24").withType(StandType.MERCH)
		);
	}

	private List<Stand> getAgamStands() {
		return Arrays.asList(
				new Stand().withName("מאי ארט").withLocationName("A1-A4").withType(StandType.OTHER)
				,new Stand().withName("החתול הסגול").withLocationName("A5-A8").withType(StandType.CLOTHES)
				,new Stand().withName("Mini Tokio").withLocationName("A9-A10").withType(StandType.MERCH)
				,new Stand().withName("קומיקאזה").withLocationName("A11-A13").withType(StandType.MANGA)
				,new Stand().withName("N FIG").withLocationName("A14").withType(StandType.MERCH)
				,new Stand().withName("דוכן שיפודן").withLocationName("A15-A20").withType(StandType.MERCH)
				,new Stand().withName("קומיקס וירקות").withLocationName("A21-A24").withType(StandType.MANGA)
				,new Stand().withName("הממלכה").withLocationName("A25-A26").withType(StandType.TABLETOP_GAMES)
				,new Stand().withName("אנימה וויב").withLocationName("A27-A30").withType(StandType.MERCH)
				,new Stand().withName("T&T").withLocationName("A31-A32").withType(StandType.CLOTHES)
				,new Stand().withName("אנימה שופ").withLocationName("A33-A38").withType(StandType.MERCH)
				,new Stand().withName("Anime Gifts").withLocationName("A39-A42").withType(StandType.MERCH)
				,new Stand().withName("Gran D. Line").withLocationName("A43-A44").withType(StandType.MERCH)
				,new Stand().withName("Vered Roze Art").withLocationName("B1-B2").withType(StandType.ARTIST)
				,new Stand().withName("דניהלמן ארט").withLocationName("B3").withType(StandType.ARTIST)
				,new Stand().withName("Ked_creation").withLocationName("B04").withType(StandType.ARTIST)
				,new Stand().withName("גריסים").withLocationName("B05-B6").withType(StandType.ARTIST)
				,new Stand().withName("Otaku תיקים מגניבים של").withLocationName("B07").withType(StandType.ARTIST)
				,new Stand().withName("Shorterthan").withLocationName("B8-B9").withType(StandType.ARTIST)
				,new Stand().withName("TOTAL LOST").withLocationName("B10-B11").withType(StandType.ARTIST)
				,new Stand().withName("הוט גירל פורים").withLocationName("B12").withType(StandType.ARTIST)
				,new Stand().withName("דוכן האמן של ליאן").withLocationName("B13").withType(StandType.ARTIST)
				,new Stand().withName("Hikikomoring - Art by Sem Daniel").withLocationName("B14").withType(StandType.ARTIST)
				,new Stand().withName("Shokolyr ").withLocationName("B15").withType(StandType.ARTIST)
				,new Stand().withName("Mariliya").withLocationName("B16-B17").withType(StandType.ARTIST)
				,new Stand().withName("טחינה").withLocationName("B18").withType(StandType.ARTIST)
				,new Stand().withName("הדוחן").withLocationName("B19").withType(StandType.ARTIST)
				,new Stand().withName("קומיקום").withLocationName("B20").withType(StandType.ARTIST)
				,new Stand().withName("Kip, Eve & Co.").withLocationName("B21").withType(StandType.ARTIST)
				,new Stand().withName("Strawberry flavors").withLocationName("B22").withType(StandType.ARTIST)
				,new Stand().withName("Geek Aesthetics").withLocationName("B23").withType(StandType.ARTIST)
				,new Stand().withName("Saigar Art").withLocationName("B24").withType(StandType.ARTIST)
				,new Stand().withName("Kartzi's").withLocationName("B25-B26").withType(StandType.ARTIST)
				,new Stand().withName("OdeChan Art").withLocationName("B27").withType(StandType.ARTIST)
				,new Stand().withName("צפרדע שוקולד").withLocationName("B28").withType(StandType.ARTIST)
				,new Stand().withName("בועת מחשבה").withLocationName("B29").withType(StandType.OTHER)
				,new Stand().withName("Sloth with a hat").withLocationName("B30").withType(StandType.ARTIST)
				,new Stand().withName("cryptic arts").withLocationName("B31").withType(StandType.ARTIST)
				,new Stand().withName("מאיירת מציאות").withLocationName("B32").withType(StandType.ARTIST)
				,new Stand().withName("Foxerish").withLocationName("B33").withType(StandType.ARTIST)
				,new Stand().withName("עולם החיות של בת-חן צרפתי").withLocationName("B34-B35").withType(StandType.ARTIST)
				,new Stand().withName("ROTEMZ ART").withLocationName("B36").withType(StandType.ARTIST)
				,new Stand().withName("Martin Draws").withLocationName("B37").withType(StandType.ARTIST)
				,new Stand().withName("Sweetie's Stand").withLocationName("B38").withType(StandType.ARTIST)
				,new Stand().withName("האומנות של ריי").withLocationName("B39").withType(StandType.ARTIST)
				,new Stand().withName("Haruhi Chili").withLocationName("B40").withType(StandType.ARTIST)
				,new Stand().withName("Tomato Eater").withLocationName("B41").withType(StandType.ARTIST)
				,new Stand().withName("SHIR K").withLocationName("B42-B43").withType(StandType.ARTIST)
				,new Stand().withName("Orchibald art").withLocationName("B44-B45").withType(StandType.ARTIST)
				,new Stand().withName("Ozart").withLocationName("B46-B47").withType(StandType.ARTIST)
				,new Stand().withName("fishiebug").withLocationName("B48").withType(StandType.ARTIST)
				,new Stand().withName("Meiiior").withLocationName("B49").withType(StandType.ARTIST)
				,new Stand().withName("קבוצת יצירת קומיקס").withLocationName("B50-B51").withType(StandType.MANGA)
				,new Stand().withName("kukeshii").withLocationName("B52").withType(StandType.ARTIST)
				,new Stand().withName("koruhiko").withLocationName("B53-B54").withType(StandType.ARTIST)
				,new Stand().withName("AniPug").withLocationName("B55").withType(StandType.ARTIST)
				,new Stand().withName("Gabisweb").withLocationName("B56").withType(StandType.ARTIST)
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

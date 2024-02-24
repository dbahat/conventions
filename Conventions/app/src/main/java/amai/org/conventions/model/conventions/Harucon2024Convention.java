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

public class Harucon2024Convention extends AmaiConvention {
	// Hall names
	private static final String MAIN_HALL_NAME = "אולם ראשי";
	private static final String ESHKOL1_NAME = "אשכול 1";
	private static final String ESHKOL2_NAME = "אשכול 2";
	private static final String ESHKOL3_NAME = "אשכול 3";
	private static final String CONTENT4_NAME = "חדר תוכן 4";
	private static final String GAMES_NAME = "משחקייה";
	private static final String COSPLAY_AREA_NAME = "מתחם קוספליי";
	// Location names
	public static final String CHILDREN_ROOM_NAME = "חדר פעוטות";

	// Vote questions - these values are serialized, don't change them!
	private static final int QUESTION_ID_AMAIDOL_VOTE = 1000;
	private static final int QUESTION_ID_AMAIDOL_NAME = 1001;

	// Special events server id
	private static final int EVENT_ID_AMAIDOL = 1775;

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
		return new ConventionStorage(this, R.raw.harucon2024_convention_events, 0);
	}

	@Override
	protected Calendar initDate() {
		Calendar date = Calendar.getInstance();
		date.clear();
		date.set(2024, Calendar.MARCH, 23);
		return date;
	}

	@Override
	protected String initID() {
		return "Harucon2024";
	}

	@Override
	protected String initDisplayName() {
		return "הארוקון 2024";
	}

	@Override
	protected URL initUpdatesURL() {
		try {
			return new URL("https://harucon.org.il/2023/wp-content/plugins/get-harucon-feed.php");
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
			return new URL("https://harucon.org.il/2024/wp-admin/admin-ajax.php?action=get_event_list");
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
				.addMapping(ImageIdToImageResourceMapper.EVENT_GENERIC, R.drawable.harucon2023_event_activity_background);
	}

	@Override
	protected Halls initHalls() {
		Hall mainHall = new Hall().withName(MAIN_HALL_NAME).withOrder(1);
		Hall eshkol1 = new Hall().withName(ESHKOL1_NAME).withOrder(2);
		Hall eshkol2 = new Hall().withName(ESHKOL2_NAME).withOrder(3);
		Hall eshkol3 = new Hall().withName(ESHKOL3_NAME).withOrder(4);
		Hall content4 = new Hall().withName(CONTENT4_NAME).withOrder(5);
		Hall cosplayArea = new Hall().withName(COSPLAY_AREA_NAME).withOrder(7);
		Hall games = new Hall().withName(GAMES_NAME).withOrder(8);
		return new Halls(Arrays.asList(mainHall, eshkol1, eshkol2, eshkol3, content4, cosplayArea, games));
	}

	@Override
	protected ConventionMap initMap() {
		return createMap();
	}

	private ConventionMap createMap() {
		Hall mainHall = this.getHalls().findByName(MAIN_HALL_NAME);
		Hall eshkol1 = this.getHalls().findByName(ESHKOL1_NAME);
		Hall eshkol2 = this.getHalls().findByName(ESHKOL2_NAME);
		Hall eshkol3 = this.getHalls().findByName(ESHKOL3_NAME);
		Hall content4 = this.getHalls().findByName(CONTENT4_NAME);
		Hall games = this.getHalls().findByName(GAMES_NAME);
		Hall cosplayArea = this.getHalls().findByName(COSPLAY_AREA_NAME);

		Floor entrance = new Floor(1)
				.withName("מתחם כניסה")
				.withImageResource(R.raw.harucon2024_floor_entrance, true)
				.withImageWidth(1297.96997f)
				.withImageHeight(804.69f);
		Floor floor1 = new Floor(2)
				.withName("קומה 1")
				.withImageResource(R.raw.harucon2024_floor1, true)
				.withImageWidth(1657f)
				.withImageHeight(836.90002f);
		Floor floor2 = new Floor(3)
				.withName("קומה 2")
				.withImageResource(R.raw.harucon2024_floor2, true)
				.withImageWidth(1825.90002f)
				.withImageHeight(1101.19995f);

		StandsArea agam = new StandsArea()
				.withName("טרקלין אגם")
				.withStandLocations(getAgamStandLocations()) // This must be initialized before the stands
				.withStands(getAgamStands())
				.withImageResource(R.drawable.harucon2023_stands_map_agam)
				.withImageWidth(1545)
				.withImageHeight(662);
		StandsArea pinkus = new StandsArea()
				.withName("אולם פינקוס")
				.withStandLocations(getPinkusStandLocations()) // This must be initialized before the stands
				.withStands(getPinkusStands())
				.withImageResource(R.drawable.harucon2023_stands_map_pinkus)
				.withImageWidth(934)
				.withImageHeight(564);

		return new ConventionMap()
				.withFloors(Arrays.asList(entrance, floor1, floor2))
				.withDefaultFloor(floor1)
				.withLocations(
						CollectionUtils.flattenList(
								inFloor(entrance,
										new MapLocation()
												.withPlace(new Place().withName("עמדת מודיעין"))
												.withMarkerResource(R.raw.harucon2024_marker_information, true)
												.withSelectedMarkerResource(R.raw.harucon2024_marker_information_selected, true)
												.withMarkerHeight(136.51f)
												.withX(1047.39f)
												.withY(658.18f),
										new MapLocation()
												.withPlace(new Place().withName("עמדות צימוד"))
												.withMarkerResource(R.raw.harucon2024_marker_bracelets, true)
												.withSelectedMarkerResource(R.raw.harucon2024_marker_bracelets_selected, true)
												.withMarkerHeight(104.76f)
												.withX(661.73f)
												.withY(674.37f),
										new MapLocation()
												.withPlace(new Place().withName("קופות"))
												.withMarkerResource(R.raw.harucon2024_marker_cashiers, true)
												.withSelectedMarkerResource(R.raw.harucon2024_marker_cashiers_selected, true)
												.withMarkerHeight(104.76f)
												.withX(498.6f)
												.withY(634.04f),
										new MapLocation()
												.withPlace(new Place().withName("קופה נגישה"))
												.withMarkerResource(R.raw.harucon2024_marker_accessible_cashier, true)
												.withSelectedMarkerResource(R.raw.harucon2024_marker_accessible_cashier_selected, true)
												.withMarkerHeight(104.76f)
												.withX(670.84f)
												.withY(521.85f),
										new MapLocation()
												.withPlace(new Place().withName("מתחם הזמנה מראש"))
												.withMarkerResource(R.raw.harucon2024_marker_preorders, true)
												.withSelectedMarkerResource(R.raw.harucon2024_marker_preorders_selected, true)
												.withMarkerHeight(104.75f)
												.withX(722.45f)
												.withY(237.4f),
										new MapLocation()
												.withPlace(new Place().withName("מתחם קנייה במקום"))
												.withMarkerResource(R.raw.harucon2024_marker_tickets_area, true)
												.withSelectedMarkerResource(R.raw.harucon2024_marker_tickets_area_selected, true)
												.withMarkerHeight(104.76f)
												.withX(220.69f)
												.withY(369.84f),
										new MapLocation()
												.withPlace(new Place().withName("מעבר נגיש"))
												.withMarkerResource(R.raw.harucon2024_marker_accessible_passage, true)
												.withSelectedMarkerResource(R.raw.harucon2024_marker_accessible_passage_selected, true)
												.withMarkerHeight(104.76f)
												.withX(195.73f)
												.withY(258.36f)
								),
								inFloor(floor1,
										new MapLocation()
												.withPlace(new Place().withName("שירותים"))
												.withMarkerResource(R.raw.harucon2024_marker_toilet, true)
												.withSelectedMarkerResource(R.raw.harucon2024_marker_toilet_selected, true)
												.withMarkerHeight(97.3f)
												.withX(1543.1f)
												.withY(536.50002f),
										new MapLocation()
												.withPlace(new Place().withName("שמירת חפצים"))
												.withMarkerResource(R.raw.harucon2024_marker_storage, true)
												.withSelectedMarkerResource(R.raw.harucon2024_marker_storage_selected, true)
												.withMarkerHeight(134.7f)
												.withX(1400.3f)
												.withY(645.20002f),
										new MapLocation()
												.withPlace(new Place().withName("מרחב מוגן"))
												.withMarkerResource(R.raw.harucon2024_marker_safe, true)
												.withSelectedMarkerResource(R.raw.harucon2024_marker_safe_selected, true)
												.withMarkerHeight(105.5f)
												.withX(1320.9f)
												.withY(671.00002f),
										new MapLocation()
												.withName("מתחם דוכנים")
												.withPlace(pinkus)
												.withMarkerResource(R.raw.harucon2024_marker_stands, true)
												.withSelectedMarkerResource(R.raw.harucon2024_marker_stands_selected, true)
												.withMarkerHeight(94.4f)
												.withX(1074.4f)
												.withY(565.40002f),
										new MapLocation()
												.withPlace(new Place().withName("שירותים"))
												.withMarkerResource(R.raw.harucon2024_marker_toilet, true)
												.withSelectedMarkerResource(R.raw.harucon2024_marker_toilet_selected, true)
												.withMarkerHeight(97.3f)
												.withX(924.6f)
												.withY(103.50002f),
										new MapLocation()
												.withPlace(new Place().withName("מעלית"))
												.withMarkerResource(R.raw.harucon2024_marker_elevator, true)
												.withSelectedMarkerResource(R.raw.harucon2024_marker_elevator_selected, true)
												.withMarkerHeight(94.502f)
												.withX(786.1f)
												.withY(149.90002f),
										new MapLocation()
												.withPlace(new Place().withName("מודיעין ודוכן אמא\"י"))
												.withMarkerResource(R.raw.harucon2024_marker_info, true)
												.withSelectedMarkerResource(R.raw.harucon2024_marker_info_selected, true)
												.withMarkerHeight(144.2f)
												.withX(771.5f)
												.withY(319.30002f),
										new MapLocation()
												.withPlace(eshkol1)
												.withMarkerResource(R.raw.harucon2024_marker_eshkol1, true)
												.withSelectedMarkerResource(R.raw.harucon2024_marker_eshkol1_selected, true)
												.withMarkerHeight(116.8f)
												.withX(460.5f)
												.withY(517.50002f),
										new MapLocation()
												.withPlace(eshkol3)
												.withMarkerResource(R.raw.harucon2024_marker_eshkol3, true)
												.withSelectedMarkerResource(R.raw.harucon2024_marker_eshkol3_selected, true)
												.withMarkerHeight(116.8f)
												.withX(337.6f)
												.withY(700.10002f),
										new MapLocation()
												.withPlace(eshkol2)
												.withMarkerResource(R.raw.harucon2024_marker_eshkol2, true)
												.withSelectedMarkerResource(R.raw.harucon2024_marker_eshkol2_selected, true)
												.withMarkerHeight(116.8f)
												.withX(502.9f)
												.withY(700.10002f),
										new MapLocation()
												.withPlace(new Place().withName("שירותים"))
												.withMarkerResource(R.raw.harucon2024_marker_toilet, true)
												.withSelectedMarkerResource(R.raw.harucon2024_marker_toilet_selected, true)
												.withMarkerHeight(98.3f)
												.withX(272.8f)
												.withY(426.80002f),
										new MapLocation()
												.withPlace(new Place().withName("מעלית"))
												.withMarkerResource(R.raw.harucon2024_marker_elevator, true)
												.withSelectedMarkerResource(R.raw.harucon2024_marker_elevator_selected, true)
												.withMarkerHeight(94.502f)
												.withX(99.2f)
												.withY(514.20002f)
								),
								inFloor(floor2,
										new MapLocation()
												.withPlace(content4)
												.withMarkerResource(R.raw.harucon2024_marker_content4, true)
												.withSelectedMarkerResource(R.raw.harucon2024_marker_content4_selected, true)
												.withMarkerHeight(133.6f)
												.withX(1503.6f)
												.withY(947.59995f),
										new MapLocation()
												.withPlace(new Place().withName("שיפוט קוספליי"))
												.withMarkerResource(R.raw.harucon2024_marker_cosplay_judgement, true)
												.withSelectedMarkerResource(R.raw.harucon2024_marker_cosplay_judgement_selected, true)
												.withMarkerHeight(116f)
												.withX(1365.5f)
												.withY(895.59995f),
										new MapLocation()
												.withPlace(new Place().withName(CHILDREN_ROOM_NAME))
												.withMarkerResource(R.raw.harucon2024_marker_children_room, true)
												.withSelectedMarkerResource(R.raw.harucon2024_marker_children_room_selected, true)
												.withMarkerHeight(94.6f)
												.withX(1486.6f)
												.withY(724.09995f),
										new MapLocation()
												.withPlace(new Place().withName("שירותים"))
												.withMarkerResource(R.raw.harucon2024_marker_toilet, true)
												.withSelectedMarkerResource(R.raw.harucon2024_marker_toilet_selected, true)
												.withMarkerHeight(98.8f)
												.withX(1715.8f)
												.withY(597.49995f),
										new MapLocation()
											.withPlace(mainHall)
											.withName("כניסה נגישה")
											.withMarkerResource(R.raw.harucon2024_marker_accessible_entrance, true)
											.withSelectedMarkerResource(R.raw.harucon2024_marker_accessible_entrance_selected, true)
											.withMarkerHeight(98.8f)
											.withX(1370.61f)
											.withY(450.39895f),
										new MapLocation()
												.withPlace(mainHall)
												.withMarkerResource(R.raw.harucon2024_marker_main_hall, true)
												.withSelectedMarkerResource(R.raw.harucon2024_marker_main_hall_selected, true)
												.withMarkerHeight(177.805f)
												.withX(1199.8f)
												.withY(527.19995f),
										new MapLocation()
												.withPlace(new Place().withName("שירותים"))
												.withMarkerResource(R.raw.harucon2024_marker_toilet, true)
												.withSelectedMarkerResource(R.raw.harucon2024_marker_toilet_selected, true)
												.withMarkerHeight(101.1f)
												.withX(1164.6f)
												.withY(118.19995f),
										new MapLocation()
												.withPlace(new Place().withName("מעלית"))
												.withMarkerResource(R.raw.harucon2024_marker_elevator, true)
												.withSelectedMarkerResource(R.raw.harucon2024_marker_elevator_selected, true)
												.withMarkerHeight(94.502f)
												.withX(1035.7f)
												.withY(136.89995f),
										new MapLocation()
												.withPlace(cosplayArea)
												.withMarkerResource(R.raw.harucon2024_marker_cosplay_area, true)
												.withSelectedMarkerResource(R.raw.harucon2024_marker_cosplay_area_selected, true)
												.withMarkerHeight(261.6f)
												.withX(1014.7f)
												.withY(728.79995f),
										new MapLocation()
												.withPlace(games)
												.withName("משחקייה")
												.withMarkerResource(R.raw.harucon2024_marker_games, true)
												.withSelectedMarkerResource(R.raw.harucon2024_marker_games_selected, true)
												.withMarkerHeight(256.9f)
												.withX(762.8f)
												.withY(651.49995f),
										new MapLocation()
												.withName("שדרת ציירים ומתחם דוכנים")
												.withPlace(agam)
												.withMarkerResource(R.raw.harucon2024_marker_artists_alley, true)
												.withSelectedMarkerResource(R.raw.harucon2024_marker_artists_alley_selected, true)
												.withMarkerHeight(139.3f)
												.withX(762.8f)
												.withY(351.59995f),
										new MapLocation()
												.withPlace(new Place().withName("שירותים"))
												.withMarkerResource(R.raw.harucon2024_marker_toilet, true)
												.withSelectedMarkerResource(R.raw.harucon2024_marker_toilet_selected, true)
												.withMarkerHeight(94.6f)
												.withX(353.6f)
												.withY(481.79995f),
										new MapLocation()
												.withPlace(new Place().withName("מעלית"))
												.withMarkerResource(R.raw.harucon2024_marker_elevator, true)
												.withSelectedMarkerResource(R.raw.harucon2024_marker_elevator_selected, true)
												.withMarkerHeight(94.502f)
												.withX(261.4f)
												.withY(559.59995f)
								)
						)
				);
	}

	private List<Stand> getPinkusStands() {
		return Arrays.asList(
			new Stand().withName("נקסוס").withType(StandType.TABLETOP_GAMES).withLocationIds("c1", "c2", "c3", "c4"),
			new Stand().withName("Mini Tokio").withType(StandType.MERCH).withLocationIds("c11", "c12", "c13", "c14"),
			new Stand().withName("PopStorm").withType(StandType.MERCH).withLocationIds("c15", "c16", "c17"),
			new Stand().withName("Anime Storm").withType(StandType.CLOTHES).withLocationIds("c18", "c19", "c20"),
			new Stand().withName("Coolstuff").withType(StandType.MERCH).withLocationIds("c21", "c22", "c23", "c24"),
			new Stand().withName("מיסקייסיס").withType(StandType.OTHER).withLocationIds("c25", "c26", "c27", "c28", "c29", "c30"),
			new Stand().withName("Shop-pin").withType(StandType.MERCH).withLocationIds("c31", "c32"),
			new Stand().withName("אנימה סטור").withType(StandType.MERCH).withLocationIds("c33", "c34", "c35", "c36", "c37", "c38"),
			new Stand().withName("גיימינג לנד").withType(StandType.VIDEO_GAMES).withLocationIds("c39", "c40", "c41", "c42"),
			new Stand().withName("סטימצקי").withType(StandType.OTHER).withLocationIds("c43", "c44", "c45", "c46", "c47", "c48", "c49", "c50", "c51", "c52"),
			new Stand().withName("Blup").withType(StandType.OTHER).withLocationIds("c5", "c6", "c7", "c8", "c9", "c10"),
			new Stand().withName("Anime store").withType(StandType.MERCH).withLocationIds("d1", "d2"),
			new Stand().withName("הדוכן של פאניק").withType(StandType.CLOTHES).withLocationIds("d11", "d12"),
			new Stand().withName("Kawaii land shop").withType(StandType.CLOTHES).withLocationIds("d13", "d14"),
			new Stand().withName("המרכז ללימודי יפנית").withType(StandType.OTHER).withLocationIds("d15", "d16"),
			new Stand().withName("Velvet Octopus").withType(StandType.CLOTHES).withLocationIds("d17", "d18"),
			new Stand().withName("may shiri Design & Art").withType(StandType.CLOTHES).withLocationIds("d19", "d20"),
			new Stand().withName("האוניברסיטה העברית").withType(StandType.OTHER).withLocationIds("d21", "d22"),
			new Stand().withName("Sampai designs").withType(StandType.OTHER).withLocationIds("d23", "d24"),
			new Stand().withName("SweetheartYun").withType(StandType.CLOTHES).withLocationIds("d3"),
			new Stand().withName("Raspberry").withType(StandType.CLOTHES).withLocationIds("d4"),
			new Stand().withName("Jill._.Creations").withType(StandType.OTHER).withLocationIds("d5"),
			new Stand().withName("U.kaiju").withType(StandType.CLOTHES).withLocationIds("d7", "d8"),
			new Stand().withName("Aquamaren").withType(StandType.CLOTHES).withLocationIds("d9", "d10"),
			new Stand().withName("Otaku and Fujoshi").withType(StandType.MERCH).withLocationIds("e1", "e2"),
			new Stand().withName("אנימאג- Aninug").withType(StandType.OTHER).withLocationIds("e10"),
			new Stand().withName("catthatart").withType(StandType.OTHER).withLocationIds("e11"),
			new Stand().withName("Candy Lenses").withType(StandType.CLOTHES).withLocationIds("e12", "e13", "e14"),
			new Stand().withName("TVfox").withType(StandType.OTHER).withLocationIds("e15", "e16"),
			new Stand().withName("N FIG").withType(StandType.MERCH).withLocationIds("e17", "e18", "e19"),
			new Stand().withName("פיגר בחינם?").withType(StandType.OTHER).withLocationIds("e20"),
			new Stand().withName("Fantasy House").withType(StandType.MERCH).withLocationIds("e21", "e22", "e23", "e24"),
			new Stand().withName("Fairy Cabinet").withType(StandType.CLOTHES).withLocationIds("e24"),
			new Stand().withName("Fairy Art").withType(StandType.CLOTHES).withLocationIds("e3", "e4"),
			new Stand().withName("Treasure planet").withType(StandType.MANGA).withLocationIds("e5", "e6", "e7", "e8"),
			new Stand().withName("Noyanny").withType(StandType.CLOTHES).withLocationIds("e9")
		);
	}

	private List<Stand> getAgamStands() {
		return Arrays.asList(
			new Stand().withName("Creative a tea").withType(StandType.OTHER).withLocationIds("a1"),
			new Stand().withName("קבוצת יצירת קומיקס").withType(StandType.MERCH).withLocationIds("a11", "a12"),
			new Stand().withName("A-ANIME").withType(StandType.MERCH).withLocationIds("a13", "a14"),
			new Stand().withName("קומיקאזה").withType(StandType.MANGA).withLocationIds("a15", "a16", "a17", "a18"),
			new Stand().withName("דוכן שיפודן").withType(StandType.MERCH).withLocationIds("a19", "a20", "a21", "a22", "a23", "a24"),
			new Stand().withName("Moolish").withType(StandType.MERCH).withLocationIds("a2"),
			new Stand().withName("קומיקס וירקות").withType(StandType.MANGA).withLocationIds("a25", "a26", "a27", "a28"),
			new Stand().withName("הממלכה").withType(StandType.TABLETOP_GAMES).withLocationIds("a29", "a30"),
			new Stand().withName("אוריג'ין סטורי").withType(StandType.OTHER).withLocationIds("a3", "a4"),
			new Stand().withName("SVAG").withType(StandType.CLOTHES).withLocationIds("a31", "a32", "a33", "a34", "a35", "a36"),
			new Stand().withName("הסיכות של גברת וודו").withType(StandType.CLOTHES).withLocationIds("a37", "a38"),
			new Stand().withName("Anime Wave").withType(StandType.MERCH).withLocationIds("a39", "a40", "a41", "a42"),
			new Stand().withName("Batel and Erano").withType(StandType.OTHER).withLocationIds("a43", "a44"),
			new Stand().withName("Aliza Bass Art").withType(StandType.OTHER).withLocationIds("a45"),
			new Stand().withName("בועת מחשבה - פאנזין אנימה ישראלי").withType(StandType.ARTIST).withLocationIds("a46"),
			new Stand().withName("Sirolynia").withType(StandType.TABLETOP_GAMES).withLocationIds("a5", "a6", "a7", "a8", "a9", "a10"),
			new Stand().withName("mirrorshards").withType(StandType.ARTIST).withLocationIds("b1", "b2"),
			new Stand().withName("האומנות של ריי").withType(StandType.ARTIST).withLocationIds("b11"),
			new Stand().withName("Awesome Possum").withType(StandType.ARTIST).withLocationIds("b12"),
			new Stand().withName("הדוחן").withType(StandType.ARTIST).withLocationIds("b13"),
			new Stand().withName("דניהלמן ארט").withType(StandType.ARTIST).withLocationIds("b14"),
			new Stand().withName("Hikikomoring - Art by Sem Daniel").withType(StandType.ARTIST).withLocationIds("b15", "b16"),
			new Stand().withName("Inimi draws! - Art by Maayan Elbaz").withType(StandType.ARTIST).withLocationIds("b17"),
			new Stand().withName("Grisim").withType(StandType.ARTIST).withLocationIds("b18", "b19"),
			new Stand().withName("Martin Draws").withType(StandType.ARTIST).withLocationIds("b20"),
			new Stand().withName("Blueberry_crown").withType(StandType.ARTIST).withLocationIds("b21"),
			new Stand().withName("מאיירת מציאות").withType(StandType.ARTIST).withLocationIds("b22"),
			new Stand().withName("Tomatoes Trash").withType(StandType.ARTIST).withLocationIds("b23"),
			new Stand().withName("TOTAL LOST").withType(StandType.ARTIST).withLocationIds("b24"),
			new Stand().withName("SHIR K").withType(StandType.ARTIST).withLocationIds("b25", "b26"),
			new Stand().withName("Orchibald Art").withType(StandType.ARTIST).withLocationIds("b27", "b28"),
			new Stand().withName("Ameriix_").withType(StandType.ARTIST).withLocationIds("b29"),
			new Stand().withName("דוכן האמן של ליאן").withType(StandType.ARTIST).withLocationIds("b3", "b4"),
			new Stand().withName("Nighto").withType(StandType.ARTIST).withLocationIds("b30"),
			new Stand().withName("הדוכן השמור של דוד צור 2").withType(StandType.ARTIST).withLocationIds("b31"),
			new Stand().withName("Pompoms").withType(StandType.ARTIST).withLocationIds("b32"),
			new Stand().withName("Meiiior").withType(StandType.ARTIST).withLocationIds("b33"),
			new Stand().withName("kartzi's").withType(StandType.ARTIST).withLocationIds("b34", "b35"),
			new Stand().withName("עולם החיות של בת-חן צרפתי").withType(StandType.ARTIST).withLocationIds("b36"),
			new Stand().withName("Kukeshii").withType(StandType.ARTIST).withLocationIds("b37"),
			new Stand().withName("Koruhiko").withType(StandType.ARTIST).withLocationIds("b38", "b39"),
			new Stand().withName("מריליה").withType(StandType.ARTIST).withLocationIds("b40", "b41"),
			new Stand().withName("Ecliby").withType(StandType.ARTIST).withLocationIds("b42"),
			new Stand().withName("Haruhi Chili").withType(StandType.ARTIST).withLocationIds("b43"),
			new Stand().withName("YUEvander & Livinkart").withType(StandType.ARTIST).withLocationIds("b44", "b45"),
			new Stand().withName("Gal's art").withType(StandType.ARTIST).withLocationIds("b46"),
			new Stand().withName("Sophia Volovik").withType(StandType.ARTIST).withLocationIds("b47"),
			new Stand().withName("adelistic").withType(StandType.ARTIST).withLocationIds("b48"),
			new Stand().withName("RedNLeaf").withType(StandType.ARTIST).withLocationIds("b49"),
			new Stand().withName("Anime_Glass").withType(StandType.ARTIST).withLocationIds("b5"),
			new Stand().withName("Shandrwa").withType(StandType.ARTIST).withLocationIds("b50"),
			new Stand().withName("Eli Zeroix").withType(StandType.ARTIST).withLocationIds("b51"),
			new Stand().withName("crimson soda").withType(StandType.ARTIST).withLocationIds("b52"),
			new Stand().withName("התוכייה").withType(StandType.ARTIST).withLocationIds("b53"),
			new Stand().withName("kaboodel").withType(StandType.ARTIST).withLocationIds("b54"),
			new Stand().withName("Ozart").withType(StandType.ARTIST).withLocationIds("b55", "b56"),
			new Stand().withName("AB.art").withType(StandType.ARTIST).withLocationIds("b6"),
			new Stand().withName("Cryptic arts").withType(StandType.ARTIST).withLocationIds("b7", "b8"),
			new Stand().withName("gabisweb").withType(StandType.ARTIST).withLocationIds("b9", "b10")
		);
	}

	private StandLocations getAgamStandLocations() {
		float defaultWidth = 24;
		float defaultSpaceHorizontal = 4;
		float defaultHeight = 24;
		float defaultSpaceVertical = 4;

		// Top row
		float a5Left = 292.000f;
		float a5Top = 88.000f;
		float a13Left = 532.000f;
		float a13Top = 88.000f;
		float a19Left = 716.000f;
		float a19Top = 88.000f;
		float a25Left = 900.000f;
		float a25Top = 88.000f;
		float a31Left = 1084.000f;
		float a31Top = 88.000f;
		float a37Left = 1268.000f;
		float a37Top = 88.000f;

		// Bottom left column
		float a43Left = 1385.000f;
		float a43Top = 290.500f;

		// Bottom right column
		float a1Left = 209.500f;
		float a1Top = 292.500f;

		// Left square
		float b6Left = 426.000f;
		float b6Top = 213.000f;
		float b7Left = 459.000f;
		float b7Top = 185.000f;
		float b11Left = 585.000f;
		float b11Top = 213.000f;
		float b19Left = 452.000f;
		float b19Top = 384.000f;

		// Middle square
		float b24Left = 698.000f;
		float b24Top = 213.000f;
		float b25Left = 728.500f;
		float b25Top = 185.000f;
		float b29Left = 852.000f;
		float b29Top = 248.000f;
		float b37Left = 741.000f;
		float b37Top = 384.000f;

		// Right square
		float b41Left = 968.500f;
		float b41Top = 220.500f;
		float b42Left = 1003.000f;
		float b42Top = 185.000f;
		float b47Left = 1149.000f;
		float b47Top = 207.000f;
		float b56Left = 1004.500f;
		float b56Top = 384.000f;

		return new StandLocations(
				// As
				StandLocation.fromWidths("a1", "a01", "a2", a1Left, defaultWidth, a1Top, defaultHeight),
				StandLocation.fromWidths("a2", "a02", "a3", a1Left, defaultWidth, a1Top + (defaultHeight + defaultSpaceVertical), defaultHeight),
				StandLocation.fromWidths("a3", "a03", "a4", a1Left, defaultWidth, a1Top + 2*(defaultHeight + defaultSpaceVertical), defaultHeight),
				StandLocation.fromWidths("a4", "a04", "a5", a1Left, defaultWidth, a1Top + 3*(defaultHeight + defaultSpaceVertical), defaultHeight),

				StandLocation.fromWidths("a5", "a05", "a6", a5Left, defaultWidth, a5Top, defaultHeight),
				StandLocation.fromWidths("a6", "a06", "a7", a5Left + (defaultWidth + defaultSpaceHorizontal), defaultWidth, a5Top, defaultHeight),
				StandLocation.fromWidths("a7", "a07", "a8", a5Left + 2*(defaultWidth + defaultSpaceHorizontal), defaultWidth, a5Top, defaultHeight),
				StandLocation.fromWidths("a8", "a08", "a9", a5Left + 3*(defaultWidth + defaultSpaceHorizontal), defaultWidth, a5Top, defaultHeight),
				StandLocation.fromWidths("a9", "a09", "a10", a5Left + 4*(defaultWidth + defaultSpaceHorizontal), defaultWidth, a5Top, defaultHeight),
				StandLocation.fromWidths("a10", "a10", "a11", a5Left + 5*(defaultWidth + defaultSpaceHorizontal), defaultWidth, a5Top, defaultHeight),
				StandLocation.fromWidths("a11", "a11", "a12", a5Left + 6*(defaultWidth + defaultSpaceHorizontal), defaultWidth, a5Top, defaultHeight),
				StandLocation.fromWidths("a12", "a12", "a13", a5Left + 7*(defaultWidth + defaultSpaceHorizontal), defaultWidth, a5Top, defaultHeight),

				StandLocation.fromWidths("a13", "a13", "a14", a13Left, defaultWidth, a13Top, defaultHeight),
				StandLocation.fromWidths("a14", "a14", "a15", a13Left + (defaultWidth + defaultSpaceHorizontal), defaultWidth, a13Top, defaultHeight),
				StandLocation.fromWidths("a15", "a15", "a16", a13Left + 2*(defaultWidth + defaultSpaceHorizontal), defaultWidth, a13Top, defaultHeight),
				StandLocation.fromWidths("a16", "a16", "a17", a13Left + 3*(defaultWidth + defaultSpaceHorizontal), defaultWidth, a13Top, defaultHeight),
				StandLocation.fromWidths("a17", "a17", "a18", a13Left + 4*(defaultWidth + defaultSpaceHorizontal), defaultWidth, a13Top, defaultHeight),
				StandLocation.fromWidths("a18", "a18", "a19", a13Left + 5*(defaultWidth + defaultSpaceHorizontal), defaultWidth, a13Top, defaultHeight),

				StandLocation.fromWidths("a19", "a19", "a20", a19Left, defaultWidth, a19Top, defaultHeight),
				StandLocation.fromWidths("a20", "a20", "a21", a19Left + (defaultWidth + defaultSpaceHorizontal), defaultWidth, a19Top, defaultHeight),
				StandLocation.fromWidths("a21", "a21", "a22", a19Left + 2*(defaultWidth + defaultSpaceHorizontal), defaultWidth, a19Top, defaultHeight),
				StandLocation.fromWidths("a22", "a22", "a23", a19Left + 3*(defaultWidth + defaultSpaceHorizontal), defaultWidth, a19Top, defaultHeight),
				StandLocation.fromWidths("a23", "a23", "a24", a19Left + 4*(defaultWidth + defaultSpaceHorizontal), defaultWidth, a19Top, defaultHeight),
				StandLocation.fromWidths("a24", "a24", "a25", a19Left + 5*(defaultWidth + defaultSpaceHorizontal), defaultWidth, a19Top, defaultHeight),

				StandLocation.fromWidths("a25", "a25", "a26", a25Left, defaultWidth, a25Top, defaultHeight),
				StandLocation.fromWidths("a26", "a26", "a27", a25Left + (defaultWidth + defaultSpaceHorizontal), defaultWidth, a25Top, defaultHeight),
				StandLocation.fromWidths("a27", "a27", "a28", a25Left + 2*(defaultWidth + defaultSpaceHorizontal), defaultWidth, a25Top, defaultHeight),
				StandLocation.fromWidths("a28", "a28", "a29", a25Left + 3*(defaultWidth + defaultSpaceHorizontal), defaultWidth, a25Top, defaultHeight),
				StandLocation.fromWidths("a29", "a29", "a30", a25Left + 4*(defaultWidth + defaultSpaceHorizontal), defaultWidth, a25Top, defaultHeight),
				StandLocation.fromWidths("a30", "a30", "a31", a25Left + 5*(defaultWidth + defaultSpaceHorizontal), defaultWidth, a25Top, defaultHeight),

				StandLocation.fromWidths("a31", "a31", "a32", a31Left, defaultWidth, a31Top, defaultHeight),
				StandLocation.fromWidths("a32", "a32", "a33", a31Left + (defaultWidth + defaultSpaceHorizontal), defaultWidth, a31Top, defaultHeight),
				StandLocation.fromWidths("a33", "a33", "a34", a31Left + 2*(defaultWidth + defaultSpaceHorizontal), defaultWidth, a31Top, defaultHeight),
				StandLocation.fromWidths("a34", "a34", "a35", a31Left + 3*(defaultWidth + defaultSpaceHorizontal), defaultWidth, a31Top, defaultHeight),
				StandLocation.fromWidths("a35", "a35", "a36", a31Left + 4*(defaultWidth + defaultSpaceHorizontal), defaultWidth, a31Top, defaultHeight),
				StandLocation.fromWidths("a36", "a36", "a37", a31Left + 5*(defaultWidth + defaultSpaceHorizontal), defaultWidth, a31Top, defaultHeight),

				StandLocation.fromWidths("a37", "a37", "a38", a37Left, defaultWidth, a37Top, defaultHeight),
				StandLocation.fromWidths("a38", "a38", null, a37Left + (defaultWidth + defaultSpaceHorizontal), defaultWidth, a37Top, defaultHeight),
				StandLocation.fromWidths("a39", "a39", "a40", a37Left + 2*(defaultWidth + defaultSpaceHorizontal), defaultWidth, a37Top, defaultHeight),
				StandLocation.fromWidths("a40", "a40", "a41", a37Left + 3*(defaultWidth + defaultSpaceHorizontal), defaultWidth, a37Top, defaultHeight),
				StandLocation.fromWidths("a41", "a41", "a42", a37Left + 4*(defaultWidth + defaultSpaceHorizontal), defaultWidth, a37Top, defaultHeight),
				StandLocation.fromWidths("a42", "a42", null, a37Left + 5*(defaultWidth + defaultSpaceHorizontal), defaultWidth, a37Top, defaultHeight),

				StandLocation.fromWidths("a43", "a43", "a44", a43Left, defaultWidth, a43Top, defaultHeight),
				StandLocation.fromWidths("a44", "a44", "a45", a43Left, defaultWidth, a43Top + (defaultHeight + defaultSpaceVertical), defaultHeight),
				StandLocation.fromWidths("a45", "a45", "a46", a43Left, defaultWidth, a43Top + 2*(defaultHeight + defaultSpaceVertical), defaultHeight),
				StandLocation.fromWidths("a46", "a46", null, a43Left, defaultWidth, a43Top + 3*(defaultHeight + defaultSpaceVertical), defaultHeight),

				// Bs
				StandLocation.fromWidths("b1", "b01", "b2", b6Left, defaultWidth, b6Top + 5*(defaultHeight + defaultSpaceVertical), defaultHeight),
				StandLocation.fromWidths("b2", "b02", "b3", b6Left, defaultWidth, b6Top + 4*(defaultHeight + defaultSpaceVertical), defaultHeight),
				StandLocation.fromWidths("b3", "b03", "b4", b6Left, defaultWidth, b6Top + 3*(defaultHeight + defaultSpaceVertical), defaultHeight),
				StandLocation.fromWidths("b4", "b04", "b5", b6Left, defaultWidth, b6Top + 2*(defaultHeight + defaultSpaceVertical), defaultHeight),
				StandLocation.fromWidths("b5", "b05", "b6", b6Left, defaultWidth, b6Top + (defaultHeight + defaultSpaceVertical), defaultHeight),
				StandLocation.fromWidths("b6", "b06", "b7", b6Left, defaultWidth, b6Top, defaultHeight),

				StandLocation.fromWidths("b7", "b07", "b8", b7Left, defaultWidth, b7Top, defaultHeight),
				StandLocation.fromWidths("b8", "b08", "b9", b7Left + (defaultWidth + defaultSpaceHorizontal), defaultWidth, b7Top, defaultHeight),
				StandLocation.fromWidths("b9", "b09", "b10", b7Left + 2*(defaultWidth + defaultSpaceHorizontal), defaultWidth, b7Top, defaultHeight),
				StandLocation.fromWidths("b10", "b10", "b11", b7Left + 3*(defaultWidth + defaultSpaceHorizontal), defaultWidth, b7Top, defaultHeight),

				StandLocation.fromWidths("b11", "b11", "b12", b11Left, defaultWidth, b11Top, defaultHeight),
				StandLocation.fromWidths("b12", "b12", "b13", b11Left, defaultWidth, b11Top + (defaultHeight + defaultSpaceVertical), defaultHeight),
				StandLocation.fromWidths("b13", "b13", "b14", b11Left, defaultWidth, b11Top + 2*(defaultHeight + defaultSpaceVertical), defaultHeight),
				StandLocation.fromWidths("b14", "b14", "b15", b11Left, defaultWidth, b11Top + 3*(defaultHeight + defaultSpaceVertical), defaultHeight),

				StandLocation.fromWidths("b15", "b15", "b16", b19Left + 4*(defaultWidth + defaultSpaceHorizontal), defaultWidth, b19Top, defaultHeight),
				StandLocation.fromWidths("b16", "b16", "b17", b19Left + 3*(defaultWidth + defaultSpaceHorizontal), defaultWidth, b19Top, defaultHeight),
				StandLocation.fromWidths("b17", "b17", "b18", b19Left + 2*(defaultWidth + defaultSpaceHorizontal), defaultWidth, b19Top, defaultHeight),
				StandLocation.fromWidths("b18", "b18", "b19", b19Left + (defaultWidth + defaultSpaceHorizontal), defaultWidth, b19Top, defaultHeight),
				StandLocation.fromWidths("b19", "b19", "b1", b19Left, defaultWidth, b19Top, defaultHeight),

				StandLocation.fromWidths("b20", "b20", "b21", b24Left, defaultWidth, b24Top + 4*(defaultHeight + defaultSpaceVertical), defaultHeight),
				StandLocation.fromWidths("b21", "b21", "b22", b24Left, defaultWidth, b24Top + 3*(defaultHeight + defaultSpaceVertical), defaultHeight),
				StandLocation.fromWidths("b22", "b22", "b23", b24Left, defaultWidth, b24Top + 2*(defaultHeight + defaultSpaceVertical), defaultHeight),
				StandLocation.fromWidths("b23", "b23", "b24", b24Left, defaultWidth, b24Top + (defaultHeight + defaultSpaceVertical), defaultHeight),
				StandLocation.fromWidths("b24", "b24", "b25", b24Left, defaultWidth, b24Top, defaultHeight),

				StandLocation.fromWidths("b25", "b25", "b26", b25Left, defaultWidth, b25Top, defaultHeight),
				StandLocation.fromWidths("b26", "b26", "b27", b25Left + (defaultWidth + defaultSpaceHorizontal), defaultWidth, b25Top, defaultHeight),
				StandLocation.fromWidths("b27", "b27", "b28", b25Left + 2*(defaultWidth + defaultSpaceHorizontal), defaultWidth, b25Top, defaultHeight),
				StandLocation.fromWidths("b28", "b28", "b29", b25Left + 3*(defaultWidth + defaultSpaceHorizontal), defaultWidth, b25Top, defaultHeight),

				StandLocation.fromWidths("b29", "b29", "b30", b29Left, defaultWidth, b29Top, defaultHeight),
				StandLocation.fromWidths("b30", "b30", "b31", b29Left, defaultWidth, b29Top + (defaultHeight + defaultSpaceVertical), defaultHeight),
				StandLocation.fromWidths("b31", "b31", "b32", b29Left, defaultWidth, b29Top + 2*(defaultHeight + defaultSpaceVertical), defaultHeight),
				StandLocation.fromWidths("b32", "b32", "b33", b29Left, defaultWidth, b29Top + 3*(defaultHeight + defaultSpaceVertical), defaultHeight),
				StandLocation.fromWidths("b33", "b33", "b34", b29Left, defaultWidth, b29Top + 4*(defaultHeight + defaultSpaceVertical), defaultHeight),

				StandLocation.fromWidths("b34", "b34", "b35", b37Left + 3*(defaultWidth + defaultSpaceHorizontal), defaultWidth, b37Top, defaultHeight),
				StandLocation.fromWidths("b35", "b35", "b36", b37Left + 2*(defaultWidth + defaultSpaceHorizontal), defaultWidth, b37Top, defaultHeight),
				StandLocation.fromWidths("b36", "b36", "b37", b37Left + (defaultWidth + defaultSpaceHorizontal), defaultWidth, b37Top, defaultHeight),
				StandLocation.fromWidths("b37", "b37", "b20", b37Left, defaultWidth, b37Top, defaultHeight),

				StandLocation.fromWidths("b38", "b38", "b39", b41Left, defaultWidth, b41Top + 3*(defaultHeight + defaultSpaceVertical), defaultHeight),
				StandLocation.fromWidths("b39", "b39", "b40", b41Left, defaultWidth, b41Top + 2*(defaultHeight + defaultSpaceVertical), defaultHeight),
				StandLocation.fromWidths("b40", "b40", "b41", b41Left, defaultWidth, b41Top + (defaultHeight + defaultSpaceVertical), defaultHeight),
				StandLocation.fromWidths("b41", "b41", "b42", b41Left, defaultWidth, b41Top, defaultHeight),

				StandLocation.fromWidths("b42", "b42", "b43", b42Left, defaultWidth, b42Top, defaultHeight),
				StandLocation.fromWidths("b43", "b43", "b44", b42Left + (defaultWidth + defaultSpaceHorizontal), defaultWidth, b42Top, defaultHeight),
				StandLocation.fromWidths("b44", "b44", "b45", b42Left + 2*(defaultWidth + defaultSpaceHorizontal), defaultWidth, b42Top, defaultHeight),
				StandLocation.fromWidths("b45", "b45", "b46", b42Left + 3*(defaultWidth + defaultSpaceHorizontal), defaultWidth, b42Top, defaultHeight),
				StandLocation.fromWidths("b46", "b46", "b47", b42Left + 4*(defaultWidth + defaultSpaceHorizontal), defaultWidth, b42Top, defaultHeight),

				StandLocation.fromWidths("b47", "b47", "b48", b47Left, defaultWidth, b47Top, defaultHeight),
				StandLocation.fromWidths("b48", "b48", "b49", b47Left, defaultWidth, b47Top + (defaultHeight + defaultSpaceVertical), defaultHeight),
				StandLocation.fromWidths("b49", "b49", "b50", b47Left, defaultWidth, b47Top + 2*(defaultHeight + defaultSpaceVertical), defaultHeight),
				StandLocation.fromWidths("b50", "b50", "b51", b47Left, defaultWidth, b47Top + 3*(defaultHeight + defaultSpaceVertical), defaultHeight),
				StandLocation.fromWidths("b51", "b51", "b52", b47Left, defaultWidth, b47Top + 4*(defaultHeight + defaultSpaceVertical), defaultHeight),

				StandLocation.fromWidths("b52", "b52", "b53", b56Left + 4*(defaultWidth + defaultSpaceHorizontal), defaultWidth, b56Top, defaultHeight),
				StandLocation.fromWidths("b53", "b53", "b54", b56Left + 3*(defaultWidth + defaultSpaceHorizontal), defaultWidth, b56Top, defaultHeight),
				StandLocation.fromWidths("b54", "b54", "b55", b56Left + 2*(defaultWidth + defaultSpaceHorizontal), defaultWidth, b56Top, defaultHeight),
				StandLocation.fromWidths("b55", "b55", "b56", b56Left + (defaultWidth + defaultSpaceHorizontal), defaultWidth, b56Top, defaultHeight),
				StandLocation.fromWidths("b56", "b56", "b38", b56Left, defaultWidth, b56Top, defaultHeight)
		);
	}

	private StandLocations getPinkusStandLocations() {
		float defaultWidth = 24;
		float defaultSpaceHorizontal = 2;
		float defaultHeight = 24;
		float defaultSpaceVertical = 2;

		// Top row
		float c1Left = 190.000f;
		float c1Top = 105.000f;
		float c11Left = 472.000f;
		float c11Top = 105.000f;
		float c15Left = 598.000f;
		float c15Top = 105.000f;

		// Right column
		float c25Left = 832.000f;
		float c25Top = 187.000f;

		// Bottom row
		float c38Left = 702.000f;
		float c38Top = 461.000f;
		float c42Left = 576.000f;
		float c42Top = 461.000f;
		float c50Left = 346.000f;
		float c50Top = 461.000f;

		// Bottom left column
		float c51Left = 320.000f;
		float c51Top = 485.000f;

		// Middle square
		float d1Left = 669.000f;
		float d1Top = 211.000f;
		float d3Left = 720.000f;
		float d3Top = 234.000f;
		float d14Left = 565.000f;
		float d14Top = 391.000f;
		float d20Left = 525.000f;
		float d20Top = 235.000f;
		float d21Left = 549.000f;
		float d21Top = 211.000f;

		// Left square
		float e1Left = 360.000f;
		float e1Top = 211.000f;
		float e3Left = 412.000f;
		float e3Top = 236.000f;
		float e10Left = 360.000f;
		float e10Top = 391.000f;
		float e14Left = 240.000f;
		float e14Top = 391.000f;
		float e20Left = 216.000f;
		float e20Top = 235.000f;
		float e21Left = 240.000f;
		float e21Top = 211.000f;


		return new StandLocations(
				// Cs
				StandLocation.fromWidths("c1", "c01", "c2", c1Left, defaultWidth, c1Top, defaultHeight),
				StandLocation.fromWidths("c2", "c02", "c3", c1Left + (defaultWidth + defaultSpaceHorizontal), defaultWidth, c1Top, defaultHeight),
				StandLocation.fromWidths("c3", "c03", "c4", c1Left + 2*(defaultWidth + defaultSpaceHorizontal), defaultWidth, c1Top, defaultHeight),
				StandLocation.fromWidths("c4", "c04", "c5", c1Left + 3*(defaultWidth + defaultSpaceHorizontal), defaultWidth, c1Top, defaultHeight),
				StandLocation.fromWidths("c5", "c05", "c6", c1Left + 4*(defaultWidth + defaultSpaceHorizontal), defaultWidth, c1Top, defaultHeight),
				StandLocation.fromWidths("c6", "c06", "c7", c1Left + 5*(defaultWidth + defaultSpaceHorizontal), defaultWidth, c1Top, defaultHeight),
				StandLocation.fromWidths("c7", "c07", "c8", c1Left + 6*(defaultWidth + defaultSpaceHorizontal), defaultWidth, c1Top, defaultHeight),
				StandLocation.fromWidths("c8", "c08", "c9", c1Left + 7*(defaultWidth + defaultSpaceHorizontal), defaultWidth, c1Top, defaultHeight),
				StandLocation.fromWidths("c9", "c09", "c10", c1Left + 8*(defaultWidth + defaultSpaceHorizontal), defaultWidth, c1Top, defaultHeight),
				StandLocation.fromWidths("c10", "c10", "c11", c1Left + 9*(defaultWidth + defaultSpaceHorizontal), defaultWidth, c1Top, defaultHeight),

				StandLocation.fromWidths("c11", "c11", "c12", c11Left, defaultWidth, c11Top, defaultHeight),
				StandLocation.fromWidths("c12", "c12", "c13", c11Left + (defaultWidth + defaultSpaceHorizontal), defaultWidth, c11Top, defaultHeight),
				StandLocation.fromWidths("c13", "c13", "c14", c11Left + 2*(defaultWidth + defaultSpaceHorizontal), defaultWidth, c11Top, defaultHeight),
				StandLocation.fromWidths("c14", "c14", "c15", c11Left + 3*(defaultWidth + defaultSpaceHorizontal), defaultWidth, c11Top, defaultHeight),

				StandLocation.fromWidths("c15", "c15", "c16", c15Left, defaultWidth, c15Top, defaultHeight),
				StandLocation.fromWidths("c16", "c16", "c17", c15Left + (defaultWidth + defaultSpaceHorizontal), defaultWidth, c15Top, defaultHeight),
				StandLocation.fromWidths("c17", "c17", "c18", c15Left + 2*(defaultWidth + defaultSpaceHorizontal), defaultWidth, c15Top, defaultHeight),
				StandLocation.fromWidths("c18", "c18", "c19", c15Left + 3*(defaultWidth + defaultSpaceHorizontal), defaultWidth, c15Top, defaultHeight),
				StandLocation.fromWidths("c19", "c19", "c20", c15Left + 4*(defaultWidth + defaultSpaceHorizontal), defaultWidth, c15Top, defaultHeight),
				StandLocation.fromWidths("c20", "c20", "c21", c15Left + 5*(defaultWidth + defaultSpaceHorizontal), defaultWidth, c15Top, defaultHeight),
				StandLocation.fromWidths("c21", "c21", "c22", c15Left + 6*(defaultWidth + defaultSpaceHorizontal), defaultWidth, c15Top, defaultHeight),
				StandLocation.fromWidths("c22", "c22", "c23", c15Left + 7*(defaultWidth + defaultSpaceHorizontal), defaultWidth, c15Top, defaultHeight),
				StandLocation.fromWidths("c23", "c23", "c24", c15Left + 8*(defaultWidth + defaultSpaceHorizontal), defaultWidth, c15Top, defaultHeight),
				StandLocation.fromWidths("c24", "c24", "c25", c15Left + 9*(defaultWidth + defaultSpaceHorizontal), defaultWidth, c15Top, defaultHeight),

				StandLocation.fromWidths("c25", "c25", "c26", c25Left, defaultWidth, c25Top, defaultHeight),
				StandLocation.fromWidths("c26", "c26", "c27", c25Left, defaultWidth, c25Top + (defaultHeight + defaultSpaceVertical), defaultHeight),
				StandLocation.fromWidths("c27", "c27", "c28", c25Left, defaultWidth, c25Top + 2*(defaultHeight + defaultSpaceVertical), defaultHeight),
				StandLocation.fromWidths("c28", "c28", "c29", c25Left, defaultWidth, c25Top + 3*(defaultHeight + defaultSpaceVertical), defaultHeight),
				StandLocation.fromWidths("c29", "c29", "c30", c25Left, defaultWidth, c25Top + 4*(defaultHeight + defaultSpaceVertical), defaultHeight),
				StandLocation.fromWidths("c30", "c30", "c31", c25Left, defaultWidth, c25Top + 5*(defaultHeight + defaultSpaceVertical), defaultHeight),
				StandLocation.fromWidths("c31", "c31", "c32", c25Left, defaultWidth, c25Top + 6*(defaultHeight + defaultSpaceVertical), defaultHeight),
				StandLocation.fromWidths("c32", "c32", "c33", c25Left, defaultWidth, c25Top + 7*(defaultHeight + defaultSpaceVertical), defaultHeight),

				StandLocation.fromWidths("c33", "c33", "c34", c38Left + 5*(defaultWidth + defaultSpaceHorizontal), defaultWidth, c38Top, defaultHeight),
				StandLocation.fromWidths("c34", "c34", "c35", c38Left + 4*(defaultWidth + defaultSpaceHorizontal), defaultWidth, c38Top, defaultHeight),
				StandLocation.fromWidths("c35", "c35", "c36", c38Left + 3*(defaultWidth + defaultSpaceHorizontal), defaultWidth, c38Top, defaultHeight),
				StandLocation.fromWidths("c36", "c36", "c37", c38Left + 2*(defaultWidth + defaultSpaceHorizontal), defaultWidth, c38Top, defaultHeight),
				StandLocation.fromWidths("c37", "c37", "c38", c38Left + (defaultWidth + defaultSpaceHorizontal), defaultWidth, c38Top, defaultHeight),
				StandLocation.fromWidths("c38", "c38", "c39", c38Left, defaultWidth, c38Top, defaultHeight),

				StandLocation.fromWidths("c39", "c39", "c40", c42Left + 3*(defaultWidth + defaultSpaceHorizontal), defaultWidth, c42Top, defaultHeight),
				StandLocation.fromWidths("c40", "c40", "c41", c42Left + 2*(defaultWidth + defaultSpaceHorizontal), defaultWidth, c42Top, defaultHeight),
				StandLocation.fromWidths("c41", "c41", "c42", c42Left + (defaultWidth + defaultSpaceHorizontal), defaultWidth, c42Top, defaultHeight),
				StandLocation.fromWidths("c42", "c42", "c43", c42Left, defaultWidth, c42Top, defaultHeight),

				StandLocation.fromWidths("c43", "c43", "c44", c50Left + 7*(defaultWidth + defaultSpaceHorizontal), defaultWidth, c50Top, defaultHeight),
				StandLocation.fromWidths("c44", "c44", "c45", c50Left + 6*(defaultWidth + defaultSpaceHorizontal), defaultWidth, c50Top, defaultHeight),
				StandLocation.fromWidths("c45", "c45", "c46", c50Left + 5*(defaultWidth + defaultSpaceHorizontal), defaultWidth, c50Top, defaultHeight),
				StandLocation.fromWidths("c46", "c46", "c47", c50Left + 4*(defaultWidth + defaultSpaceHorizontal), defaultWidth, c50Top, defaultHeight),
				StandLocation.fromWidths("c47", "c47", "c48", c50Left + 3*(defaultWidth + defaultSpaceHorizontal), defaultWidth, c50Top, defaultHeight),
				StandLocation.fromWidths("c48", "c48", "c49", c50Left + 2*(defaultWidth + defaultSpaceHorizontal), defaultWidth, c50Top, defaultHeight),
				StandLocation.fromWidths("c49", "c49", "c50", c50Left + (defaultWidth + defaultSpaceHorizontal), defaultWidth, c50Top, defaultHeight),
				StandLocation.fromWidths("c50", "c50", "c51", c50Left, defaultWidth, c50Top, defaultHeight),

				StandLocation.fromWidths("c51", "c51", "c52", c51Left, defaultWidth, c51Top, defaultHeight),
				StandLocation.fromWidths("c52", "c52", null, c51Left, defaultWidth, c51Top + (defaultHeight + defaultSpaceVertical), defaultHeight),


				// Ds
				StandLocation.fromWidths("d1", "d01", "d2", d1Left, defaultWidth, d1Top, defaultHeight),
				StandLocation.fromWidths("d2", "d02", "d3", d1Left + (defaultWidth + defaultSpaceHorizontal), defaultWidth, d1Top, defaultHeight),

				StandLocation.fromWidths("d3", "d03", "d4", d3Left, defaultWidth, d3Top, defaultHeight),
				StandLocation.fromWidths("d4", "d04", "d5", d3Left, defaultWidth, d3Top + (defaultHeight + defaultSpaceVertical), defaultHeight),
				StandLocation.fromWidths("d5", "d05", "d6", d3Left, defaultWidth, d3Top + 2*(defaultHeight + defaultSpaceVertical), defaultHeight),
				StandLocation.fromWidths("d6", "d06", "d7", d3Left, defaultWidth, d3Top + 3*(defaultHeight + defaultSpaceVertical), defaultHeight),
				StandLocation.fromWidths("d7", "d07", "d8", d3Left, defaultWidth, d3Top + 4*(defaultHeight + defaultSpaceVertical), defaultHeight),
				StandLocation.fromWidths("d8", "d08", "d9", d3Left, defaultWidth, d3Top + 5*(defaultHeight + defaultSpaceVertical), defaultHeight),

				StandLocation.fromWidths("d9", "d09", "d10", d14Left + 5*(defaultWidth + defaultSpaceHorizontal), defaultWidth, d14Top, defaultHeight),
				StandLocation.fromWidths("d10", "d10", "d11", d14Left + 4*(defaultWidth + defaultSpaceHorizontal), defaultWidth, d14Top, defaultHeight),
				StandLocation.fromWidths("d11", "d11", "d12", d14Left + 3*(defaultWidth + defaultSpaceHorizontal), defaultWidth, d14Top, defaultHeight),
				StandLocation.fromWidths("d12", "d12", "d13", d14Left + 2*(defaultWidth + defaultSpaceHorizontal), defaultWidth, d14Top, defaultHeight),
				StandLocation.fromWidths("d13", "d13", "d14", d14Left + (defaultWidth + defaultSpaceHorizontal), defaultWidth, d14Top, defaultHeight),
				StandLocation.fromWidths("d14", "d14", "d15", d14Left, defaultWidth, d14Top, defaultHeight),

				StandLocation.fromWidths("d15", "d15", "d16", d20Left, defaultWidth, d20Top + 5*(defaultHeight + defaultSpaceVertical), defaultHeight),
				StandLocation.fromWidths("d16", "d16", "d17", d20Left, defaultWidth, d20Top + 4*(defaultHeight + defaultSpaceVertical), defaultHeight),
				StandLocation.fromWidths("d17", "d17", "d18", d20Left, defaultWidth, d20Top + 3*(defaultHeight + defaultSpaceVertical), defaultHeight),
				StandLocation.fromWidths("d18", "d18", "d19", d20Left, defaultWidth, d20Top + 2*(defaultHeight + defaultSpaceVertical), defaultHeight),
				StandLocation.fromWidths("d19", "d19", "d20", d20Left, defaultWidth, d20Top + (defaultHeight + defaultSpaceVertical), defaultHeight),
				StandLocation.fromWidths("d20", "d20", "d21", d20Left, defaultWidth, d20Top, defaultHeight),

				StandLocation.fromWidths("d21", "d21", "d22", d21Left, defaultWidth, d21Top, defaultHeight),
				StandLocation.fromWidths("d22", "d22", "d23", d21Left + (defaultWidth + defaultSpaceHorizontal), defaultWidth, d21Top, defaultHeight),
				StandLocation.fromWidths("d23", "d23", "d24", d21Left + 2*(defaultWidth + defaultSpaceHorizontal), defaultWidth, d21Top, defaultHeight),
				StandLocation.fromWidths("d24", "d24", "d1", d21Left + 3*(defaultWidth + defaultSpaceHorizontal), defaultWidth, d21Top, defaultHeight),

				// Es
				StandLocation.fromWidths("e1", "e01", "e2", e1Left, defaultWidth, e1Top, defaultHeight),
				StandLocation.fromWidths("e2", "e02", "e3", e1Left + (defaultWidth + defaultSpaceHorizontal), defaultWidth, e1Top, defaultHeight),

				StandLocation.fromWidths("e3", "e03", "e4", e3Left, defaultWidth, e3Top, defaultHeight),
				StandLocation.fromWidths("e4", "e04", "e5", e3Left, defaultWidth, e3Top + (defaultHeight + defaultSpaceVertical), defaultHeight),
				StandLocation.fromWidths("e5", "e05", "e6", e3Left, defaultWidth, e3Top + 2*(defaultHeight + defaultSpaceVertical), defaultHeight),
				StandLocation.fromWidths("e6", "e06", "e7", e3Left, defaultWidth, e3Top + 3*(defaultHeight + defaultSpaceVertical), defaultHeight),
				StandLocation.fromWidths("e7", "e07", "e8", e3Left, defaultWidth, e3Top + 4*(defaultHeight + defaultSpaceVertical), defaultHeight),
				StandLocation.fromWidths("e8", "e08", "e9", e3Left, defaultWidth, e3Top + 5*(defaultHeight + defaultSpaceVertical), defaultHeight),

				StandLocation.fromWidths("e9", "e09", "e10", e10Left + (defaultWidth + defaultSpaceHorizontal), defaultWidth, e10Top, defaultHeight),
				StandLocation.fromWidths("e10", "e10", "e11", e10Left, defaultWidth, e10Top, defaultHeight),

				StandLocation.fromWidths("e11", "e11", "e12", e14Left + 3*(defaultWidth + defaultSpaceHorizontal), defaultWidth, e14Top, defaultHeight),
				StandLocation.fromWidths("e12", "e12", "e13", e14Left + 2*(defaultWidth + defaultSpaceHorizontal), defaultWidth, e14Top, defaultHeight),
				StandLocation.fromWidths("e13", "e13", "e14", e14Left + (defaultWidth + defaultSpaceHorizontal), defaultWidth, e14Top, defaultHeight),
				StandLocation.fromWidths("e14", "e14", "e15", e14Left, defaultWidth, e14Top, defaultHeight),

				StandLocation.fromWidths("e15", "e15", "e16", e20Left, defaultWidth, e20Top + 5*(defaultHeight + defaultSpaceVertical), defaultHeight),
				StandLocation.fromWidths("e16", "e16", "e17", e20Left, defaultWidth, e20Top + 4*(defaultHeight + defaultSpaceVertical), defaultHeight),
				StandLocation.fromWidths("e17", "e17", "e18", e20Left, defaultWidth, e20Top + 3*(defaultHeight + defaultSpaceVertical), defaultHeight),
				StandLocation.fromWidths("e18", "e18", "e19", e20Left, defaultWidth, e20Top + 2*(defaultHeight + defaultSpaceVertical), defaultHeight),
				StandLocation.fromWidths("e19", "e19", "e20", e20Left, defaultWidth, e20Top + (defaultHeight + defaultSpaceVertical), defaultHeight),
				StandLocation.fromWidths("e20", "e20", "e21", e20Left, defaultWidth, e20Top, defaultHeight),

				StandLocation.fromWidths("e21", "e21", "e22", e21Left, defaultWidth, e21Top, defaultHeight),
				StandLocation.fromWidths("e22", "e22", "e23", e21Left + (defaultWidth + defaultSpaceHorizontal), defaultWidth, e21Top, defaultHeight),
				StandLocation.fromWidths("e23", "e23", "e24", e21Left + 2*(defaultWidth + defaultSpaceHorizontal), defaultWidth, e21Top, defaultHeight),
				StandLocation.fromWidths("e24", "e24", "e1", e21Left + 3*(defaultWidth + defaultSpaceHorizontal), defaultWidth, e21Top, defaultHeight)
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
				return true;
			}
		};
	}

	@Override
	public String convertEventDescriptionURL(String url) {
		// URLs in event descriptions that don't have a domain should point to the website URL
		if (url != null && url.startsWith("/")) {
			return "https://harucon.org.il" + url;
		}
		return super.convertEventDescriptionURL(url);
	}
}

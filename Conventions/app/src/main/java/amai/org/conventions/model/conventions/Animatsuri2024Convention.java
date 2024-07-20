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

public class Animatsuri2024Convention extends AmaiConvention {
	// Hall names
	private static final String MAIN_HALL_NAME = "אולם ראשי";
	private static final String ESHKOL1_NAME = "אשכול 1";
	private static final String ESHKOL2_NAME = "אשכול 2";
	private static final String ESHKOL3_NAME = "אשכול 3";
//	private static final String CONTENT4_NAME = "חדר תוכן 4";
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
		OTHER(R.string.other_stand, R.drawable.ic_shopping_basket);

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
		return new ConventionStorage(this, R.raw.animatsuri2024_convention_events, 1);
	}

	@Override
	protected Calendar initDate() {
		Calendar date = Calendar.getInstance();
		date.clear();
		date.set(2024, Calendar.AUGUST, 8);
		return date;
	}

	@Override
	protected String initID() {
		return "Animatsuri2024";
	}

	@Override
	protected String initDisplayName() {
		return "אנימאטסורי 2024";
	}

	@Override
	protected URL initUpdatesURL() {
		try {
			return new URL("https://us-central1-starlit-brand-95018.cloudfunctions.net/get-feed");
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
			return new URL("https://animatsuri.org.il/2024/wp-admin/admin-ajax.php?action=get_event_list");
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

		// In case the convention has a generic image which should keep aspect ratio and the fade effect, map it like this
//		imageMapper.addMapping(ImageIdToImageResourceMapper.EVENT_GENERIC, R.drawable.harucon_gen_event_activity_background);

		return new ImageIdToImageResourceMapper();
	}

	@Override
	protected Halls initHalls() {
		Hall mainHall = new Hall().withName(MAIN_HALL_NAME).withOrder(1);
		Hall eshkol1 = new Hall().withName(ESHKOL1_NAME).withOrder(2);
		Hall eshkol2 = new Hall().withName(ESHKOL2_NAME).withOrder(3);
		Hall eshkol3 = new Hall().withName(ESHKOL3_NAME).withOrder(4);
		Hall cosplayArea = new Hall().withName(COSPLAY_AREA_NAME).withOrder(5);
		Hall games = new Hall().withName(GAMES_NAME).withOrder(6);
		return new Halls(Arrays.asList(mainHall, eshkol1, eshkol2, eshkol3, cosplayArea, games));
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
		Hall games = this.getHalls().findByName(GAMES_NAME);
		Hall cosplayArea = this.getHalls().findByName(COSPLAY_AREA_NAME);

		Floor entrance = new Floor(1)
				.withName("מתחם כניסה")
				.withImageResource(R.raw.animatsuri2024_floor_entrance, true)
				.withImageWidth(1297.96997f)
				.withImageHeight(804.69f);
		Floor floor1 = new Floor(2)
				.withName("קומה 1")
				.withImageResource(R.raw.animatsuri2024_floor1, true)
				.withImageWidth(1608.79663f)
				.withImageHeight(821.56598f);
		Floor floor2 = new Floor(3)
				.withName("קומה 2")
				.withImageResource(R.raw.animatsuri2024_floor2, true)
				.withImageWidth(1686f)
				.withImageHeight(971.51898f);

		StandsArea agam = new StandsArea()
				.withName("טרקלין אגם")
				.withStandLocations(getAgamStandLocations()) // This must be initialized before the stands
				.withStands(getAgamStands())
				.withImageResource(R.drawable.animatsuri2024_stands_map_agam)
				.withImageWidth(4320)
				.withImageHeight(2430);
		StandsArea pinkus = new StandsArea()
				.withName("אולם פינקוס")
				.withStandLocations(getPinkusStandLocations()) // This must be initialized before the stands
				.withStands(getPinkusStands())
				.withImageResource(R.drawable.animatsuri2024_stands_map_pinkus)
				.withImageWidth(4320)
				.withImageHeight(2430);

		return new ConventionMap()
				.withFloors(Arrays.asList(entrance, floor1, floor2))
				.withDefaultFloor(floor1)
				.withLocations(
						CollectionUtils.flattenList(
								inFloor(entrance,
										new MapLocation()
												.withPlace(new Place().withName("עמדת מודיעין"))
												.withMarkerResource(R.raw.animatsuri2024_marker_information_entrance, true)
												.withSelectedMarkerResource(R.raw.animatsuri2024_marker_information_entrance_selected, true)
												.withMarkerHeight(136.51f)
												.withX(1047.39f)
												.withY(658.18f),
										new MapLocation()
												.withPlace(new Place().withName("עמדות צימוד"))
												.withMarkerResource(R.raw.animatsuri2024_marker_bracelets, true)
												.withSelectedMarkerResource(R.raw.animatsuri2024_marker_bracelets_selected, true)
												.withMarkerHeight(104.76f)
												.withX(661.73f)
												.withY(674.37f),
										new MapLocation()
												.withPlace(new Place().withName("קופות"))
												.withMarkerResource(R.raw.animatsuri2024_marker_cashiers, true)
												.withSelectedMarkerResource(R.raw.animatsuri2024_marker_cashiers_selected, true)
												.withMarkerHeight(104.76f)
												.withX(498.6f)
												.withY(634.04f),
										new MapLocation()
												.withPlace(new Place().withName("קופה נגישה"))
												.withMarkerResource(R.raw.animatsuri2024_marker_accessible_cashier, true)
												.withSelectedMarkerResource(R.raw.animatsuri2024_marker_accessible_cashier_selected, true)
												.withMarkerHeight(104.76f)
												.withX(670.84f)
												.withY(521.85f),
										new MapLocation()
												.withPlace(new Place().withName("מתחם הזמנה מראש"))
												.withMarkerResource(R.raw.animatsuri2024_marker_preorders, true)
												.withSelectedMarkerResource(R.raw.animatsuri2024_marker_preorders_selected, true)
												.withMarkerHeight(104.75f)
												.withX(722.45f)
												.withY(237.4f),
										new MapLocation()
												.withPlace(new Place().withName("קופה נגישה"))
												.withMarkerResource(R.raw.animatsuri2024_marker_accessible_cashier, true)
												.withSelectedMarkerResource(R.raw.animatsuri2024_marker_accessible_cashier_selected, true)
												.withMarkerHeight(104.76f)
												.withX(697.8965f)
												.withY(110.342f),
										new MapLocation()
												.withPlace(new Place().withName("מתחם קנייה במקום"))
												.withMarkerResource(R.raw.animatsuri2024_marker_tickets_area, true)
												.withSelectedMarkerResource(R.raw.animatsuri2024_marker_tickets_area_selected, true)
												.withMarkerHeight(104.76f)
												.withX(330.69f)
												.withY(389.84f),
										new MapLocation()
												.withPlace(new Place().withName("מעבר נגיש"))
												.withMarkerResource(R.raw.animatsuri2024_marker_accessible_passage, true)
												.withSelectedMarkerResource(R.raw.animatsuri2024_marker_accessible_passage_selected, true)
												.withMarkerHeight(104.76f)
												.withX(195.73f)
												.withY(258.36f)
								),
								inFloor(floor1,
										new MapLocation()
												.withPlace(new Place().withName("שירותים"))
												.withMarkerResource(R.raw.animatsuri2024_marker_toilet, true)
												.withSelectedMarkerResource(R.raw.animatsuri2024_marker_toilet_selected, true)
												.withMarkerHeight(102.438f)
												.withX(1511.577f)
												.withY(523.45498f),
										new MapLocation()
												.withPlace(new Place().withName("שמירת חפצים"))
												.withMarkerResource(R.raw.animatsuri2024_marker_storage, true)
												.withSelectedMarkerResource(R.raw.animatsuri2024_marker_storage_selected, true)
												.withMarkerHeight(126.947f)
												.withX(1374.322f)
												.withY(630.56198f),
										new MapLocation()
												.withPlace(new Place().withName("מרחב מוגן"))
												.withMarkerResource(R.raw.animatsuri2024_marker_safe, true)
												.withSelectedMarkerResource(R.raw.animatsuri2024_marker_safe_selected, true)
												.withMarkerHeight(131.35f)
												.withX(1283.822f)
												.withY(673.16498f),
										new MapLocation()
												.withName("מתחם דוכנים")
												.withPlace(pinkus)
												.withMarkerResource(R.raw.animatsuri2024_marker_stands, true)
												.withSelectedMarkerResource(R.raw.animatsuri2024_marker_stands_selected, true)
												.withMarkerHeight(98.445f)
												.withX(1053.502f)
												.withY(572.10298f),
										new MapLocation()
												.withPlace(new Place().withName("שירותים"))
												.withMarkerResource(R.raw.animatsuri2024_marker_toilet, true)
												.withSelectedMarkerResource(R.raw.animatsuri2024_marker_toilet_selected, true)
												.withMarkerHeight(102.437f)
												.withX(891.0995f)
												.withY(72.19898f),
										new MapLocation()
												.withPlace(new Place().withName("מעלית"))
												.withMarkerResource(R.raw.animatsuri2024_marker_elevator, true)
												.withSelectedMarkerResource(R.raw.animatsuri2024_marker_elevator_selected, true)
												.withMarkerHeight(85.813f)
												.withX(750.969f)
												.withY(142.84798f),
										new MapLocation()
												.withPlace(new Place().withName("מודיעין ודוכן אמא\"י"))
												.withMarkerResource(R.raw.animatsuri2024_marker_information, true)
												.withSelectedMarkerResource(R.raw.animatsuri2024_marker_information_selected, true)
												.withMarkerHeight(168.231f)
												.withX(735.953f)
												.withY(307.24298f),
										new MapLocation()
												.withPlace(eshkol1)
												.withMarkerResource(R.raw.animatsuri2024_marker_eshkol1, true)
												.withSelectedMarkerResource(R.raw.animatsuri2024_marker_eshkol1_selected, true)
												.withMarkerHeight(123.669f)
												.withX(421.7735f)
												.withY(504.57398f),
										new MapLocation()
												.withPlace(eshkol3)
												.withMarkerResource(R.raw.animatsuri2024_marker_eshkol3, true)
												.withSelectedMarkerResource(R.raw.animatsuri2024_marker_eshkol3_selected, true)
												.withMarkerHeight(123.67f)
												.withX(299.5585f)
												.withY(687.89598f),
										new MapLocation()
												.withPlace(eshkol2)
												.withMarkerResource(R.raw.animatsuri2024_marker_eshkol2, true)
												.withSelectedMarkerResource(R.raw.animatsuri2024_marker_eshkol2_selected, true)
												.withMarkerHeight(123.67f)
												.withX(464.0785f)
												.withY(687.89598f),
										new MapLocation()
												.withPlace(new Place().withName("שירותים"))
												.withMarkerResource(R.raw.animatsuri2024_marker_toilet, true)
												.withSelectedMarkerResource(R.raw.animatsuri2024_marker_toilet_selected, true)
												.withMarkerHeight(102.436f)
												.withX(233.0185f)
												.withY(415.34298f),
										new MapLocation()
												.withPlace(new Place().withName("מעלית"))
												.withMarkerResource(R.raw.animatsuri2024_marker_elevator, true)
												.withSelectedMarkerResource(R.raw.animatsuri2024_marker_elevator_selected, true)
												.withMarkerHeight(85.813f)
												.withX(59.983f)
												.withY(509.49298f)
								),
								inFloor(floor2,
										new MapLocation()
												.withPlace(new Place().withName("שיפוט קוספליי"))
												.withMarkerResource(R.raw.animatsuri2024_marker_cosplay_judgement, true)
												.withSelectedMarkerResource(R.raw.animatsuri2024_marker_cosplay_judgement_selected, true)
												.withMarkerHeight(113.011f)
												.withX(1591.49f)
												.withY(848.50798f),
										new MapLocation()
												.withPlace(new Place().withName(CHILDREN_ROOM_NAME))
												.withMarkerResource(R.raw.animatsuri2024_marker_children, true)
												.withSelectedMarkerResource(R.raw.animatsuri2024_marker_children_selected, true)
												.withMarkerHeight(98.353f)
												.withX(1337.895f)
												.withY(647.50498f),
										new MapLocation()
												.withPlace(new Place().withName("שירותים"))
												.withMarkerResource(R.raw.animatsuri2024_marker_toilet, true)
												.withSelectedMarkerResource(R.raw.animatsuri2024_marker_toilet_selected, true)
												.withMarkerHeight(102.14f)
												.withX(1544.72f)
												.withY(531.23198f),
										new MapLocation()
												.withPlace(mainHall)
												.withMarkerResource(R.raw.animatsuri2024_marker_main_hall, true)
												.withSelectedMarkerResource(R.raw.animatsuri2024_marker_main_hall_selected, true)
												.withMarkerHeight(160.982f)
												.withX(1078.2195f)
												.withY(469.23298f),
										new MapLocation()
												.withPlace(new Place().withName("שירותים"))
												.withMarkerResource(R.raw.animatsuri2024_marker_toilet, true)
												.withSelectedMarkerResource(R.raw.animatsuri2024_marker_toilet_selected, true)
												.withMarkerHeight(90.416f)
												.withX(1047.9f)
												.withY(100.03398f),
										new MapLocation()
												.withPlace(new Place().withName("מעלית"))
												.withMarkerResource(R.raw.animatsuri2024_marker_elevator, true)
												.withSelectedMarkerResource(R.raw.animatsuri2024_marker_elevator_selected, true)
												.withMarkerHeight(85.564f)
												.withX(931.613f)
												.withY(114.23298f),
										new MapLocation()
												.withPlace(cosplayArea)
												.withMarkerResource(R.raw.animatsuri2024_marker_cosplay, true)
												.withSelectedMarkerResource(R.raw.animatsuri2024_marker_cosplay_selected, true)
												.withMarkerHeight(206.585f)
												.withX(910.627f)
												.withY(651.76098f),
										new MapLocation()
												.withPlace(games)
												.withName("משחקייה")
												.withMarkerResource(R.raw.animatsuri2024_marker_games, true)
												.withSelectedMarkerResource(R.raw.animatsuri2024_marker_games_selected, true)
												.withMarkerHeight(206.269f)
												.withX(682.55f)
												.withY(581.77198f),
										new MapLocation()
												.withName("שדרת ציירים ומתחם דוכנים")
												.withPlace(agam)
												.withMarkerResource(R.raw.animatsuri2024_marker_artists, true)
												.withSelectedMarkerResource(R.raw.animatsuri2024_marker_artists_selected, true)
												.withMarkerHeight(133.313f)
												.withX(682.5455f)
												.withY(310.23698f),
										new MapLocation()
												.withPlace(new Place().withName("שירותים"))
												.withMarkerResource(R.raw.animatsuri2024_marker_toilet, true)
												.withSelectedMarkerResource(R.raw.animatsuri2024_marker_toilet_selected, true)
												.withMarkerHeight(85.657f)
												.withX(312.05f)
												.withY(428.11898f),
										new MapLocation()
												.withPlace(new Place().withName("מעלית"))
												.withMarkerResource(R.raw.animatsuri2024_marker_elevator, true)
												.withSelectedMarkerResource(R.raw.animatsuri2024_marker_elevator_selected, true)
												.withMarkerHeight(85.565f)
												.withX(228.57f)
												.withY(498.56098f)
								)
						)
				);
	}

	private List<Stand> getPinkusStands() {
		return Arrays.asList(
			new Stand().withName("נקסוס").withType(StandType.TABLETOP_GAMES).withLocationIds("c1", "c2", "c3", "c4"),
			new Stand().withName("Candy Lenses").withType(StandType.MERCH).withLocationIds("c13", "c14", "c15", "c16"),
			new Stand().withName("סירולניה").withType(StandType.TABLETOP_GAMES).withLocationIds("c17", "c18", "c19", "c20", "c21", "c22"),
			new Stand().withName("Topdeck").withType(StandType.TABLETOP_GAMES).withLocationIds("c23", "c24", "c25", "c26", "c27", "c28"),
			new Stand().withName("may design x art").withType(StandType.CLOTHES).withLocationIds("c29", "c30"),
			new Stand().withName("אנימה סטור").withType(StandType.MERCH).withLocationIds("c31", "c32", "c33", "c34", "c35", "c36"),
			new Stand().withName("מיסקייסיס").withType(StandType.MERCH).withLocationIds("c37", "c38", "c39", "c40"),
			new Stand().withName("סטימצקי").withType(StandType.MANGA).withLocationIds("c41", "c42", "c43", "c44", "c45", "c46", "c47", "c48", "c49", "c50"),
			new Stand().withName("גיימינג לנד").withType(StandType.VIDEO_GAMES).withLocationIds("c5", "c6", "c7", "c8"),
			new Stand().withName("Fantasy House").withType(StandType.MERCH).withLocationIds("c9", "c10", "c11", "c12"),
			new Stand().withName("Kuzco").withType(StandType.MERCH).withLocationIds("d1", "d2"),
			new Stand().withName("שני לימונים").withType(StandType.CLOTHES).withLocationIds("d10"),
			new Stand().withName("Art of JVN & U.G. Gutman").withType(StandType.OTHER).withLocationIds("d11"),
			new Stand().withName("אפריל").withType(StandType.OTHER).withLocationIds("d12"),
			new Stand().withName("Happy Hoopoe").withType(StandType.OTHER).withLocationIds("d13"),
			new Stand().withName("Sio magen").withType(StandType.CLOTHES).withLocationIds("d14"),
			new Stand().withName("דוכן שיפודן").withType(StandType.MERCH).withLocationIds("d15", "d16", "d17", "d18", "d19", "d20"),
			new Stand().withName("Animanga").withType(StandType.TABLETOP_GAMES).withLocationIds("d21", "d22", "d23", "d24"),
			new Stand().withName("יוצרים עם דוד").withType(StandType.MERCH).withLocationIds("d3", "d4"),
			new Stand().withName("N fig").withType(StandType.MERCH).withLocationIds("d5", "d6", "d7", "d8"),
			new Stand().withName("Otaku and Fujoshi").withType(StandType.MERCH).withLocationIds("e1", "e2"),
			new Stand().withName("Soni Anime Socks").withType(StandType.MERCH).withLocationIds("e15"),
			new Stand().withName("Ascendant Fiction").withType(StandType.MANGA).withLocationIds("e16"),
			new Stand().withName("קימבי").withType(StandType.OTHER).withLocationIds("e17"),
			new Stand().withName("YK crochet").withType(StandType.OTHER).withLocationIds("e18"),
			new Stand().withName("Cosplay Boutique").withType(StandType.OTHER).withLocationIds("e19"),
			new Stand().withName("Laser-iCon").withType(StandType.MERCH).withLocationIds("e21", "e22"),
			new Stand().withName("frozen flowers").withType(StandType.CLOTHES).withLocationIds("e23", "e24"),
			new Stand().withName("Sheers By Shir").withType(StandType.CLOTHES).withLocationIds("e3", "e4"),
			new Stand().withName("Fusion Frame Studio").withType(StandType.MERCH).withLocationIds("e5", "e6"),
			new Stand().withName("קאוואי לנד שופ").withType(StandType.CLOTHES).withLocationIds("e7", "e8"),
			new Stand().withName("Anime Storm").withType(StandType.CLOTHES).withLocationIds("e9", "e10", "e11", "e12", "e13", "e14")
		);
	}

	private List<Stand> getAgamStands() {
		return Arrays.asList(
			new Stand().withName("דייס מיוזיקל").withType(StandType.OTHER).withLocationIds("a1", "a2"),
			new Stand().withName("Geek Aesthetics").withType(StandType.OTHER).withLocationIds("a10"),
			new Stand().withName("Anime Market").withType(StandType.MERCH).withLocationIds("a11", "a12", "a13", "a14", "a15", "a16"),
			new Stand().withName("A silly frog").withType(StandType.OTHER).withLocationIds("a17", "a18"),
			new Stand().withName("בתאל וערן").withType(StandType.OTHER).withLocationIds("a19", "a20"),
			new Stand().withName("קומיקאזה").withType(StandType.MANGA).withLocationIds("a21", "a22", "a23", "a24"),
			new Stand().withName("SHIR K").withType(StandType.OTHER).withLocationIds("a25", "a26"),
			new Stand().withName("Raspberry").withType(StandType.CLOTHES).withLocationIds("a27", "a28"),
			new Stand().withName("Sweetheartyun").withType(StandType.CLOTHES).withLocationIds("a29", "a30"),
			new Stand().withName("Yaelas art").withType(StandType.OTHER).withLocationIds("a3", "a4"),
			new Stand().withName("Fentahon_carpet").withType(StandType.OTHER).withLocationIds("a31", "a32"),
			new Stand().withName("Animode").withType(StandType.CLOTHES).withLocationIds("a33", "a34", "a35", "a36"),
			new Stand().withName("Almogolan Art").withType(StandType.OTHER).withLocationIds("a37", "a38"),
			new Stand().withName("Anime Wave").withType(StandType.MERCH).withLocationIds("a39", "a40", "a41", "a42"),
			new Stand().withName("קי\"ק").withType(StandType.MERCH).withLocationIds("a43", "a44"),
			new Stand().withName("Anime Life").withType(StandType.MERCH).withLocationIds("a45", "a46"),
			new Stand().withName("Mini Tokio").withType(StandType.MERCH).withLocationIds("a5"),
			new Stand().withName("כאוספליי").withType(StandType.OTHER).withLocationIds("a6"),
			new Stand().withName("Natoki").withType(StandType.CLOTHES).withLocationIds("a7"),
			new Stand().withName("Art_studio_handmade").withType(StandType.OTHER).withLocationIds("a8"),
			new Stand().withName("Orion jewelry").withType(StandType.CLOTHES).withLocationIds("a9"),
			new Stand().withName("The Chip Club").withType(StandType.ARTIST).withLocationIds("b1"),
			new Stand().withName("Tomatoes Trash").withType(StandType.ARTIST).withLocationIds("b11"),
			new Stand().withName("הדוכן המדליק של אריאל וניק").withType(StandType.ARTIST).withLocationIds("b12"),
			new Stand().withName("kimichux").withType(StandType.ARTIST).withLocationIds("b14"),
			new Stand().withName("Anipug").withType(StandType.ARTIST).withLocationIds("b15"),
			new Stand().withName("crimson soda").withType(StandType.ARTIST).withLocationIds("b16"),
			new Stand().withName("Eli Zeroix").withType(StandType.ARTIST).withLocationIds("b17"),
			new Stand().withName("The Vampair Series").withType(StandType.ARTIST).withLocationIds("b18"),
			new Stand().withName("אוריג׳ין סטורי").withType(StandType.ARTIST).withLocationIds("b19", "b20"),
			new Stand().withName("cryptic arts").withType(StandType.ARTIST).withLocationIds("b2", "b3"),
			new Stand().withName("Donrex").withType(StandType.ARTIST).withLocationIds("b21"),
			new Stand().withName("Sakurahav art").withType(StandType.ARTIST).withLocationIds("b22"),
			new Stand().withName("Stawbee's Art").withType(StandType.ARTIST).withLocationIds("b23"),
			new Stand().withName("מריליה").withType(StandType.ARTIST).withLocationIds("b24", "b25"),
			new Stand().withName("B4rmn").withType(StandType.ARTIST).withLocationIds("b26", "b27"),
			new Stand().withName("Hikikomoring - Art by Sem").withType(StandType.ARTIST).withLocationIds("b28", "b29"),
			new Stand().withName("Inimi Draws! Art by Maayan Elbaz").withType(StandType.ARTIST).withLocationIds("b30", "b31"),
			new Stand().withName("kukeshii").withType(StandType.ARTIST).withLocationIds("b32", "b33"),
			new Stand().withName("Perotts").withType(StandType.ARTIST).withLocationIds("b34"),
			new Stand().withName("הדוכן השמור של דוד צור").withType(StandType.ARTIST).withLocationIds("b35"),
			new Stand().withName("Grisim").withType(StandType.ARTIST).withLocationIds("b36", "b37"),
			new Stand().withName("Shourterthan").withType(StandType.ARTIST).withLocationIds("b38", "b39"),
			new Stand().withName("Meiiior").withType(StandType.ARTIST).withLocationIds("b4"),
			new Stand().withName("Sharki00").withType(StandType.ARTIST).withLocationIds("b40", "b41"),
			new Stand().withName("פרעושים ופשפשים").withType(StandType.ARTIST).withLocationIds("b42"),
			new Stand().withName("orchibald art").withType(StandType.ARTIST).withLocationIds("b43", "b44"),
			new Stand().withName("Sweetie's Stand").withType(StandType.ARTIST).withLocationIds("b45", "b46"),
			new Stand().withName("ShaLini").withType(StandType.ARTIST).withLocationIds("b47"),
			new Stand().withName("גולצ'י וטליטייטור").withType(StandType.ARTIST).withLocationIds("b48"),
			new Stand().withName("NatArt").withType(StandType.ARTIST).withLocationIds("b49", "b50"),
			new Stand().withName("Dinchies").withType(StandType.ARTIST).withLocationIds("b5"),
			new Stand().withName("koruhiko").withType(StandType.ARTIST).withLocationIds("b51", "b52"),
			new Stand().withName("Ozart").withType(StandType.ARTIST).withLocationIds("b53", "b54"),
			new Stand().withName("Burucheri").withType(StandType.ARTIST).withLocationIds("b55"),
			new Stand().withName("eladb_art").withType(StandType.ARTIST).withLocationIds("b56"),
			new Stand().withName("Elmiellart").withType(StandType.ARTIST).withLocationIds("b57"),
			new Stand().withName("Bloomka").withType(StandType.ARTIST).withLocationIds("b58"),
			new Stand().withName("Meitlavi95").withType(StandType.ARTIST).withLocationIds("b59", "b60"),
			new Stand().withName("Smatan").withType(StandType.ARTIST).withLocationIds("b6"),
			new Stand().withName("rob_artsy").withType(StandType.ARTIST).withLocationIds("b61"),
			new Stand().withName("Martin Draws").withType(StandType.ARTIST).withLocationIds("b62"),
			new Stand().withName("Nighto").withType(StandType.ARTIST).withLocationIds("b63"),
			new Stand().withName("Happy Little Accidents").withType(StandType.ARTIST).withLocationIds("b64"),
			new Stand().withName("nod3ret").withType(StandType.ARTIST).withLocationIds("b65"),
			new Stand().withName("Meo").withType(StandType.ARTIST).withLocationIds("b66"),
			new Stand().withName("mekarer").withType(StandType.ARTIST).withLocationIds("b67", "b68"),
			new Stand().withName("FluffyKittenk").withType(StandType.ARTIST).withLocationIds("b69"),
			new Stand().withName("adelistic").withType(StandType.ARTIST).withLocationIds("b7"),
			new Stand().withName("eszart").withType(StandType.ARTIST).withLocationIds("b8"),
			new Stand().withName("Kartzi's").withType(StandType.ARTIST).withLocationIds("b9", "b10")
		);
	}

	private StandLocations getAgamStandLocations() {
		float defaultWidth = 72;
		float defaultSpaceHorizontal = 9;
		float defaultHeight = 72;
		float defaultSpaceVertical = 9;

		// Top row
		float a5Left = 768.500f;
		float a5Top = 512.000f;
		float a11Left = 1290.500f;
		float a11Top = 512.000f;
		float a19Left = 1974.500f;
		float a19Top = 512.000f;
		float a25Left = 2496.500f;
		float a25Top = 512.000f;
		float a31Left = 3018.500f;
		float a31Top = 512.000f;
		float a37Left = 3540.500f;
		float a37Top = 512.000f;

		// Bottom left column
		float a43Left = 3459.500f;
		float a43Top = 993.500f;

		// Bottom right column
		float a4Left = 1016.000f;
		float a4Top = 998.000f;

		// Middle columns from left to right

		float b7Left = 1317.500f;
		float b7Top = 821.375f;

		float b14Left = 1484.000f;
		float b14Top = 821.375f;

		float b20Left = 1724.750f;
		float b20Top = 834.875f;
		float b16Left = 1724.750f;
		float b16Top = 1230.880f;

		float b27Left = 1895.750f;
		float b27Top = 834.875f;

		float b34Left = 2163.500f;
		float b34Top = 830.375f;

		float b41Left = 2330.000f;
		float b41Top = 830.375f;

		float b48Left = 2579.750f;
		float b48Top = 830.375f;

		float b55Left = 2746.250f;
		float b55Top = 830.375f;

		float b62Left = 3018.500f;
		float b62Top = 830.375f;

		float b69Left = 3185.000f;
		float b69Top = 830.375f;

		return new StandLocations(
				// As
				StandLocation.fromWidths("a1", "a01", "a2", a4Left, defaultWidth, a4Top + 3*(defaultHeight + defaultSpaceVertical), defaultHeight),
				StandLocation.fromWidths("a2", "a02", "a3", a4Left, defaultWidth, a4Top + 2*(defaultHeight + defaultSpaceVertical), defaultHeight),
				StandLocation.fromWidths("a3", "a03", "a4", a4Left, defaultWidth, a4Top + (defaultHeight + defaultSpaceVertical), defaultHeight),
				StandLocation.fromWidths("a4", "a04", "a5", a4Left, defaultWidth, a4Top, defaultHeight),

				StandLocation.fromWidths("a5", "a05", "a6", a5Left, defaultWidth, a5Top, defaultHeight),
				StandLocation.fromWidths("a6", "a06", "a7", a5Left + (defaultWidth + defaultSpaceHorizontal), defaultWidth, a5Top, defaultHeight),
				StandLocation.fromWidths("a7", "a07", "a8", a5Left + 2*(defaultWidth + defaultSpaceHorizontal), defaultWidth, a5Top, defaultHeight),
				StandLocation.fromWidths("a8", "a08", "a9", a5Left + 3*(defaultWidth + defaultSpaceHorizontal), defaultWidth, a5Top, defaultHeight),
				StandLocation.fromWidths("a9", "a09", "a10", a5Left + 4*(defaultWidth + defaultSpaceHorizontal), defaultWidth, a5Top, defaultHeight),
				StandLocation.fromWidths("a10", "a10", "a11", a5Left + 5*(defaultWidth + defaultSpaceHorizontal), defaultWidth, a5Top, defaultHeight),

				StandLocation.fromWidths("a11", "a11", "a12", a11Left, defaultWidth, a11Top, defaultHeight),
				StandLocation.fromWidths("a12", "a12", "a13", a11Left + (defaultWidth + defaultSpaceHorizontal), defaultWidth, a11Top, defaultHeight),
				StandLocation.fromWidths("a13", "a13", "a14", a11Left + 2*(defaultWidth + defaultSpaceHorizontal), defaultWidth, a11Top, defaultHeight),
				StandLocation.fromWidths("a14", "a14", "a15", a11Left + 3*(defaultWidth + defaultSpaceHorizontal), defaultWidth, a11Top, defaultHeight),
				StandLocation.fromWidths("a15", "a15", "a16", a11Left + 4*(defaultWidth + defaultSpaceHorizontal), defaultWidth, a11Top, defaultHeight),
				StandLocation.fromWidths("a16", "a16", "a17", a11Left + 5*(defaultWidth + defaultSpaceHorizontal), defaultWidth, a11Top, defaultHeight),
				StandLocation.fromWidths("a17", "a17", "a18", a11Left + 6*(defaultWidth + defaultSpaceHorizontal), defaultWidth, a11Top, defaultHeight),
				StandLocation.fromWidths("a18", "a18", "a19", a11Left + 7*(defaultWidth + defaultSpaceHorizontal), defaultWidth, a11Top, defaultHeight),

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
				StandLocation.fromWidths("a38", "a38", "a39", a37Left + (defaultWidth + defaultSpaceHorizontal), defaultWidth, a37Top, defaultHeight),
				StandLocation.fromWidths("a39", "a39", "a40", a37Left + 2*(defaultWidth + defaultSpaceHorizontal), defaultWidth, a37Top, defaultHeight),
				StandLocation.fromWidths("a40", "a40", "a41", a37Left + 3*(defaultWidth + defaultSpaceHorizontal), defaultWidth, a37Top, defaultHeight),
				StandLocation.fromWidths("a41", "a41", "a42", a37Left + 4*(defaultWidth + defaultSpaceHorizontal), defaultWidth, a37Top, defaultHeight),
				StandLocation.fromWidths("a42", "a42", "a43", a37Left + 5*(defaultWidth + defaultSpaceHorizontal), defaultWidth, a37Top, defaultHeight),

				StandLocation.fromWidths("a43", "a43", "a44", a43Left, defaultWidth, a43Top, defaultHeight),
				StandLocation.fromWidths("a44", "a44", "a45", a43Left, defaultWidth, a43Top + (defaultHeight + defaultSpaceVertical), defaultHeight),
				StandLocation.fromWidths("a45", "a45", "a46", a43Left, defaultWidth, a43Top + 2*(defaultHeight + defaultSpaceVertical), defaultHeight),
				StandLocation.fromWidths("a46", "a46", null, a43Left, defaultWidth, a43Top + 3*(defaultHeight + defaultSpaceVertical), defaultHeight),

				// Bs
				StandLocation.fromWidths("b1", "b01", "b2", b7Left, defaultWidth, b7Top + 6*(defaultHeight + defaultSpaceVertical), defaultHeight),
				StandLocation.fromWidths("b2", "b02", "b3", b7Left, defaultWidth, b7Top + 5*(defaultHeight + defaultSpaceVertical), defaultHeight),
				StandLocation.fromWidths("b3", "b03", "b4", b7Left, defaultWidth, b7Top + 4*(defaultHeight + defaultSpaceVertical), defaultHeight),
				StandLocation.fromWidths("b4", "b04", "b5", b7Left, defaultWidth, b7Top + 3*(defaultHeight + defaultSpaceVertical), defaultHeight),
				StandLocation.fromWidths("b5", "b05", "b6", b7Left, defaultWidth, b7Top + 2*(defaultHeight + defaultSpaceVertical), defaultHeight),
				StandLocation.fromWidths("b6", "b06", "b7", b7Left, defaultWidth, b7Top + (defaultHeight + defaultSpaceVertical), defaultHeight),
				StandLocation.fromWidths("b7", "b07", "b8", b7Left, defaultWidth, b7Top, defaultHeight),

				StandLocation.fromWidths("b8", "b08", "b9", b14Left, defaultWidth, b14Top + 6*(defaultHeight + defaultSpaceVertical), defaultHeight),
				StandLocation.fromWidths("b9", "b09", "b10", b14Left, defaultWidth, b14Top + 5*(defaultHeight + defaultSpaceVertical), defaultHeight),
				StandLocation.fromWidths("b10", "b10", "b11", b14Left, defaultWidth, b14Top + 4*(defaultHeight + defaultSpaceVertical), defaultHeight),
				StandLocation.fromWidths("b11", "b11", "b12", b14Left, defaultWidth, b14Top + 3*(defaultHeight + defaultSpaceVertical), defaultHeight),
				StandLocation.fromWidths("b12", "b12", "b13", b14Left, defaultWidth, b14Top + 2*(defaultHeight + defaultSpaceVertical), defaultHeight),
				StandLocation.fromWidths("b13", "b13", "b14", b14Left, defaultWidth, b14Top + (defaultHeight + defaultSpaceVertical), defaultHeight),
				StandLocation.fromWidths("b14", "b14", "b15", b14Left, defaultWidth, b14Top, defaultHeight),

				StandLocation.fromWidths("b15", "b15", "b16", b16Left, defaultWidth, b16Top + (defaultHeight + defaultSpaceVertical), defaultHeight),
				StandLocation.fromWidths("b16", "b16", "b17", b16Left, defaultWidth, b16Top, defaultHeight),
				StandLocation.fromWidths("b17", "b17", "b18", b20Left, defaultWidth, b20Top + 3*(defaultHeight + defaultSpaceVertical), defaultHeight),
				StandLocation.fromWidths("b18", "b18", "b19", b20Left, defaultWidth, b20Top + 2*(defaultHeight + defaultSpaceVertical), defaultHeight),
				StandLocation.fromWidths("b19", "b19", "b20", b20Left, defaultWidth, b20Top + (defaultHeight + defaultSpaceVertical), defaultHeight),
				StandLocation.fromWidths("b20", "b20", "b21", b20Left, defaultWidth, b20Top, defaultHeight),

				StandLocation.fromWidths("b21", "b21", "b22", b27Left, defaultWidth, b27Top + 6*(defaultHeight + defaultSpaceVertical), defaultHeight),
				StandLocation.fromWidths("b22", "b22", "b23", b27Left, defaultWidth, b27Top + 5*(defaultHeight + defaultSpaceVertical), defaultHeight),
				StandLocation.fromWidths("b23", "b23", "b24", b27Left, defaultWidth, b27Top + 4*(defaultHeight + defaultSpaceVertical), defaultHeight),
				StandLocation.fromWidths("b24", "b24", "b25", b27Left, defaultWidth, b27Top + 3*(defaultHeight + defaultSpaceVertical), defaultHeight),
				StandLocation.fromWidths("b25", "b25", "b26", b27Left, defaultWidth, b27Top + 2*(defaultHeight + defaultSpaceVertical), defaultHeight),
				StandLocation.fromWidths("b26", "b26", "b27", b27Left, defaultWidth, b27Top + (defaultHeight + defaultSpaceVertical), defaultHeight),
				StandLocation.fromWidths("b27", "b27", "b28", b27Left, defaultWidth, b27Top, defaultHeight),

				StandLocation.fromWidths("b28", "b28", "b29", b34Left, defaultWidth, b34Top + 6*(defaultHeight + defaultSpaceVertical), defaultHeight),
				StandLocation.fromWidths("b29", "b29", "b30", b34Left, defaultWidth, b34Top + 5*(defaultHeight + defaultSpaceVertical), defaultHeight),
				StandLocation.fromWidths("b30", "b30", "b31", b34Left, defaultWidth, b34Top + 4*(defaultHeight + defaultSpaceVertical), defaultHeight),
				StandLocation.fromWidths("b31", "b31", "b32", b34Left, defaultWidth, b34Top + 3*(defaultHeight + defaultSpaceVertical), defaultHeight),
				StandLocation.fromWidths("b32", "b32", "b33", b34Left, defaultWidth, b34Top + 2*(defaultHeight + defaultSpaceVertical), defaultHeight),
				StandLocation.fromWidths("b33", "b33", "b34", b34Left, defaultWidth, b34Top + (defaultHeight + defaultSpaceVertical), defaultHeight),
				StandLocation.fromWidths("b34", "b34", "b35", b34Left, defaultWidth, b34Top, defaultHeight),

				StandLocation.fromWidths("b35", "b35", "b36", b41Left, defaultWidth, b41Top + 6*(defaultHeight + defaultSpaceVertical), defaultHeight),
				StandLocation.fromWidths("b36", "b36", "b37", b41Left, defaultWidth, b41Top + 5*(defaultHeight + defaultSpaceVertical), defaultHeight),
				StandLocation.fromWidths("b37", "b37", "b38", b41Left, defaultWidth, b41Top + 4*(defaultHeight + defaultSpaceVertical), defaultHeight),
				StandLocation.fromWidths("b38", "b38", "b39", b41Left, defaultWidth, b41Top + 3*(defaultHeight + defaultSpaceVertical), defaultHeight),
				StandLocation.fromWidths("b39", "b39", "b40", b41Left, defaultWidth, b41Top + 2*(defaultHeight + defaultSpaceVertical), defaultHeight),
				StandLocation.fromWidths("b40", "b40", "b41", b41Left, defaultWidth, b41Top + (defaultHeight + defaultSpaceVertical), defaultHeight),
				StandLocation.fromWidths("b41", "b41", "b42", b41Left, defaultWidth, b41Top, defaultHeight),

				StandLocation.fromWidths("b42", "b42", "b43", b48Left, defaultWidth, b48Top + 6*(defaultHeight + defaultSpaceVertical), defaultHeight),
				StandLocation.fromWidths("b43", "b43", "b44", b48Left, defaultWidth, b48Top + 5*(defaultHeight + defaultSpaceVertical), defaultHeight),
				StandLocation.fromWidths("b44", "b44", "b45", b48Left, defaultWidth, b48Top + 4*(defaultHeight + defaultSpaceVertical), defaultHeight),
				StandLocation.fromWidths("b45", "b45", "b46", b48Left, defaultWidth, b48Top + 3*(defaultHeight + defaultSpaceVertical), defaultHeight),
				StandLocation.fromWidths("b46", "b46", "b47", b48Left, defaultWidth, b48Top + 2*(defaultHeight + defaultSpaceVertical), defaultHeight),
				StandLocation.fromWidths("b47", "b47", "b48", b48Left, defaultWidth, b48Top + (defaultHeight + defaultSpaceVertical), defaultHeight),
				StandLocation.fromWidths("b48", "b48", "b49", b48Left, defaultWidth, b48Top, defaultHeight),

				StandLocation.fromWidths("b49", "b49", "b50", b55Left, defaultWidth, b55Top + 6*(defaultHeight + defaultSpaceVertical), defaultHeight),
				StandLocation.fromWidths("b50", "b50", "b51", b55Left, defaultWidth, b55Top + 5*(defaultHeight + defaultSpaceVertical), defaultHeight),
				StandLocation.fromWidths("b51", "b51", "b52", b55Left, defaultWidth, b55Top + 4*(defaultHeight + defaultSpaceVertical), defaultHeight),
				StandLocation.fromWidths("b52", "b52", "b53", b55Left, defaultWidth, b55Top + 3*(defaultHeight + defaultSpaceVertical), defaultHeight),
				StandLocation.fromWidths("b53", "b53", "b54", b55Left, defaultWidth, b55Top + 2*(defaultHeight + defaultSpaceVertical), defaultHeight),
				StandLocation.fromWidths("b54", "b54", "b55", b55Left, defaultWidth, b55Top + (defaultHeight + defaultSpaceVertical), defaultHeight),
				StandLocation.fromWidths("b55", "b55", "b56", b55Left, defaultWidth, b55Top, defaultHeight),

				StandLocation.fromWidths("b56", "b56", "b57", b62Left, defaultWidth, b62Top + 6*(defaultHeight + defaultSpaceVertical), defaultHeight),
				StandLocation.fromWidths("b57", "b57", "b58", b62Left, defaultWidth, b62Top + 5*(defaultHeight + defaultSpaceVertical), defaultHeight),
				StandLocation.fromWidths("b58", "b58", "b59", b62Left, defaultWidth, b62Top + 4*(defaultHeight + defaultSpaceVertical), defaultHeight),
				StandLocation.fromWidths("b59", "b59", "b60", b62Left, defaultWidth, b62Top + 3*(defaultHeight + defaultSpaceVertical), defaultHeight),
				StandLocation.fromWidths("b60", "b60", "b61", b62Left, defaultWidth, b62Top + 2*(defaultHeight + defaultSpaceVertical), defaultHeight),
				StandLocation.fromWidths("b61", "b61", "b62", b62Left, defaultWidth, b62Top + (defaultHeight + defaultSpaceVertical), defaultHeight),
				StandLocation.fromWidths("b62", "b62", "b63", b62Left, defaultWidth, b62Top, defaultHeight),

				StandLocation.fromWidths("b63", "b63", "b64", b69Left, defaultWidth, b69Top + 6*(defaultHeight + defaultSpaceVertical), defaultHeight),
				StandLocation.fromWidths("b64", "b64", "b65", b69Left, defaultWidth, b69Top + 5*(defaultHeight + defaultSpaceVertical), defaultHeight),
				StandLocation.fromWidths("b65", "b65", "b66", b69Left, defaultWidth, b69Top + 4*(defaultHeight + defaultSpaceVertical), defaultHeight),
				StandLocation.fromWidths("b66", "b66", "b67", b69Left, defaultWidth, b69Top + 3*(defaultHeight + defaultSpaceVertical), defaultHeight),
				StandLocation.fromWidths("b67", "b67", "b68", b69Left, defaultWidth, b69Top + 2*(defaultHeight + defaultSpaceVertical), defaultHeight),
				StandLocation.fromWidths("b68", "b68", "b69", b69Left, defaultWidth, b69Top + (defaultHeight + defaultSpaceVertical), defaultHeight),
				StandLocation.fromWidths("b69", "b69", null, b69Left, defaultWidth, b69Top, defaultHeight)
		);
	}

	private StandLocations getPinkusStandLocations() {
		float defaultWidth = 72;
		float defaultSpaceHorizontal = 4.5f;
		float defaultHeight = 72;
		float defaultSpaceVertical = 4.5f;

		// Top row
		float c1Left = 1386.380f;
		float c1Top = 573.000f;
		float c9Left = 2137.880f;
		float c9Top = 573.000f;
		float c13Left = 2511.380f;
		float c13Top = 573.000f;

		// Right column
		float c23Left = 3422.620f;
		float c23Top = 924.000f;

		// Bottom row
		float c36Left = 3040.120f;
		float c36Top = 1826.250f;
		float c40Left = 2684.620f;
		float c40Top = 1826.250f;
		float c48Left = 2023.120f;
		float c48Top = 1826.250f;

		// Bottom left column
		float c49Left = 1942.120f;
		float c49Top = 1898.250f;

		// Middle square
		float d1Left = 2941.120f;
		float d1Top = 1002.750f;
		float d3Left = 3091.880f;
		float d3Top = 1077.000f;
		float d14Left = 2626.120f;
		float d14Top = 1536.000f;
		float d20Left = 2515.880f;
		float d20Top = 1077.000f;
		float d21Left = 2596.880f;
		float d21Top = 1002.750f;

		// Left square
		float e1Left = 1964.620f;
		float e1Top = 1000.500f;
		float e3Left = 2115.380f;
		float e3Top = 1077.000f;
		float e14Left = 1651.880f;
		float e14Top = 1536.000f;
		float e20Left = 1516.880f;
		float e20Top = 1077.000f;
		float e21Left = 1588.880f;
		float e21Top = 1002.750f;


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

				StandLocation.fromWidths("c9", "c09", "c10", c9Left, defaultWidth, c9Top, defaultHeight),
				StandLocation.fromWidths("c10", "c10", "c11", c9Left + (defaultWidth + defaultSpaceHorizontal), defaultWidth, c9Top, defaultHeight),
				StandLocation.fromWidths("c11", "c11", "c12", c9Left + 2*(defaultWidth + defaultSpaceHorizontal), defaultWidth, c9Top, defaultHeight),
				StandLocation.fromWidths("c12", "c12", "c13", c9Left + 3*(defaultWidth + defaultSpaceHorizontal), defaultWidth, c9Top, defaultHeight),

				StandLocation.fromWidths("c13", "c13", "c14", c13Left, defaultWidth, c13Top, defaultHeight),
				StandLocation.fromWidths("c14", "c14", "c15", c13Left + (defaultWidth + defaultSpaceHorizontal), defaultWidth, c13Top, defaultHeight),
				StandLocation.fromWidths("c15", "c15", "c16", c13Left + 2*(defaultWidth + defaultSpaceHorizontal), defaultWidth, c13Top, defaultHeight),
				StandLocation.fromWidths("c16", "c16", "c17", c13Left + 3*(defaultWidth + defaultSpaceHorizontal), defaultWidth, c13Top, defaultHeight),
				StandLocation.fromWidths("c17", "c17", "c18", c13Left + 4*(defaultWidth + defaultSpaceHorizontal), defaultWidth, c13Top, defaultHeight),
				StandLocation.fromWidths("c18", "c18", "c19", c13Left + 5*(defaultWidth + defaultSpaceHorizontal), defaultWidth, c13Top, defaultHeight),
				StandLocation.fromWidths("c19", "c19", "c20", c13Left + 6*(defaultWidth + defaultSpaceHorizontal), defaultWidth, c13Top, defaultHeight),
				StandLocation.fromWidths("c20", "c20", "c21", c13Left + 7*(defaultWidth + defaultSpaceHorizontal), defaultWidth, c13Top, defaultHeight),
				StandLocation.fromWidths("c21", "c21", "c22", c13Left + 8*(defaultWidth + defaultSpaceHorizontal), defaultWidth, c13Top, defaultHeight),
				StandLocation.fromWidths("c22", "c22", "c23", c13Left + 9*(defaultWidth + defaultSpaceHorizontal), defaultWidth, c13Top, defaultHeight),

				StandLocation.fromWidths("c23", "c23", "c24", c23Left, defaultWidth, c23Top, defaultHeight),
				StandLocation.fromWidths("c24", "c24", "c25", c23Left, defaultWidth, c23Top + (defaultHeight + defaultSpaceVertical), defaultHeight),
				StandLocation.fromWidths("c25", "c25", "c26", c23Left, defaultWidth, c23Top + 2*(defaultHeight + defaultSpaceVertical), defaultHeight),
				StandLocation.fromWidths("c26", "c26", "c27", c23Left, defaultWidth, c23Top + 3*(defaultHeight + defaultSpaceVertical), defaultHeight),
				StandLocation.fromWidths("c27", "c27", "c28", c23Left, defaultWidth, c23Top + 4*(defaultHeight + defaultSpaceVertical), defaultHeight),
				StandLocation.fromWidths("c28", "c28", "c29", c23Left, defaultWidth, c23Top + 5*(defaultHeight + defaultSpaceVertical), defaultHeight),
				StandLocation.fromWidths("c29", "c29", "c30", c23Left, defaultWidth, c23Top + 6*(defaultHeight + defaultSpaceVertical), defaultHeight),
				StandLocation.fromWidths("c30", "c30", "c31", c23Left, defaultWidth, c23Top + 7*(defaultHeight + defaultSpaceVertical), defaultHeight),

				StandLocation.fromWidths("c31", "c31", "c32", c36Left + 5*(defaultWidth + defaultSpaceHorizontal), defaultWidth, c36Top, defaultHeight),
				StandLocation.fromWidths("c32", "c32", "c33", c36Left + 4*(defaultWidth + defaultSpaceHorizontal), defaultWidth, c36Top, defaultHeight),
				StandLocation.fromWidths("c33", "c33", "c34", c36Left + 3*(defaultWidth + defaultSpaceHorizontal), defaultWidth, c36Top, defaultHeight),
				StandLocation.fromWidths("c34", "c34", "c35", c36Left + 2*(defaultWidth + defaultSpaceHorizontal), defaultWidth, c36Top, defaultHeight),
				StandLocation.fromWidths("c35", "c35", "c36", c36Left + (defaultWidth + defaultSpaceHorizontal), defaultWidth, c36Top, defaultHeight),
				StandLocation.fromWidths("c36", "c36", "c37", c36Left, defaultWidth, c36Top, defaultHeight),

				StandLocation.fromWidths("c37", "c37", "c38", c40Left + 3*(defaultWidth + defaultSpaceHorizontal), defaultWidth, c40Top, defaultHeight),
				StandLocation.fromWidths("c38", "c38", "c39", c40Left + 2*(defaultWidth + defaultSpaceHorizontal), defaultWidth, c40Top, defaultHeight),
				StandLocation.fromWidths("c39", "c39", "c40", c40Left + (defaultWidth + defaultSpaceHorizontal), defaultWidth, c40Top, defaultHeight),
				StandLocation.fromWidths("c40", "c40", "c41", c40Left, defaultWidth, c40Top, defaultHeight),

				StandLocation.fromWidths("c41", "c41", "c42", c48Left + 7*(defaultWidth + defaultSpaceHorizontal), defaultWidth, c48Top, defaultHeight),
				StandLocation.fromWidths("c42", "c42", "c43", c48Left + 6*(defaultWidth + defaultSpaceHorizontal), defaultWidth, c48Top, defaultHeight),
				StandLocation.fromWidths("c43", "c43", "c44", c48Left + 5*(defaultWidth + defaultSpaceHorizontal), defaultWidth, c48Top, defaultHeight),
				StandLocation.fromWidths("c44", "c44", "c45", c48Left + 4*(defaultWidth + defaultSpaceHorizontal), defaultWidth, c48Top, defaultHeight),
				StandLocation.fromWidths("c45", "c45", "c46", c48Left + 3*(defaultWidth + defaultSpaceHorizontal), defaultWidth, c48Top, defaultHeight),
				StandLocation.fromWidths("c46", "c46", "c47", c48Left + 2*(defaultWidth + defaultSpaceHorizontal), defaultWidth, c48Top, defaultHeight),
				StandLocation.fromWidths("c47", "c47", "c48", c48Left + (defaultWidth + defaultSpaceHorizontal), defaultWidth, c48Top, defaultHeight),
				StandLocation.fromWidths("c48", "c48", "c49", c48Left, defaultWidth, c48Top, defaultHeight),

				StandLocation.fromWidths("c49", "c49", "c50", c49Left, defaultWidth, c49Top + (defaultHeight + defaultSpaceVertical), defaultHeight),
				StandLocation.fromWidths("c50", "c50", null, c49Left, defaultWidth, c49Top, defaultHeight),


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

				StandLocation.fromWidths("e9", "e09", "e10", e14Left + 5*(defaultWidth + defaultSpaceHorizontal), defaultWidth, e14Top, defaultHeight),
				StandLocation.fromWidths("e10", "e10", "e11", e14Left + 4*(defaultWidth + defaultSpaceHorizontal), defaultWidth, e14Top, defaultHeight),
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
				return false;
			}
		};
	}

	@Override
	public String convertEventDescriptionURL(String url) {
		// URLs in event descriptions that don't have a domain should point to the website URL
		if (url != null && url.startsWith("/")) {
			return "https://animatsuri.org.il" + url;
		}
		return super.convertEventDescriptionURL(url);
	}
}

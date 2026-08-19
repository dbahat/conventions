package amai.org.conventions.model.conventions;

import android.graphics.BlendMode;
import android.os.Build;

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
import amai.org.conventions.model.FloorLocation;
import amai.org.conventions.model.Hall;
import amai.org.conventions.model.Halls;
import amai.org.conventions.model.ImageIdToImageResourceMapper;
import amai.org.conventions.model.MapLocation;
import amai.org.conventions.model.Place;
import amai.org.conventions.model.Stand;
import amai.org.conventions.model.StandLocations;
import amai.org.conventions.model.StandLocationsBuilder;
import amai.org.conventions.model.StandsArea;
import amai.org.conventions.model.Survey;
import amai.org.conventions.networking.SurveyDataRetriever;
import amai.org.conventions.utils.CollectionUtils;
import amai.org.conventions.utils.ConventionStorage;
import androidx.annotation.Nullable;

public class Animatsuri2026Convention extends AmaiConvention {
	// Hall names
	private static final String MAIN_HALL_NAME = "אולם ראשי";
	private static final String ORANIM_NAME = "אודיטוריום אורנים";
	private static final String ESHKOL1_NAME = "אשכול 1";
	private static final String ESHKOL2_NAME = "אשכול 2";
	private static final String ESHKOL3_NAME = "אשכול 3";
//	private static final String WORKSHOPS_NAME = "חדר סדנאות";
//	private static final String ORANIM2_NAME = "אורנים 2";
	private static final String GAMES_NAME = "משחקייה";
	private static final String COSPLAY_AREA_NAME = "מתחם קוספליי";
	// Location names
	public static final String CHILDREN_ROOM_NAME = "חדר פעוטות";
	public static final String ACCESSIBLE_CASHIERS_NAME = "קופה נגישה";

	// Vote questions - these values are serialized, don't change them!
	private static final int QUESTION_ID_AMAIDOL_VOTE = 1000;
	private static final int QUESTION_ID_AMAIDOL_NAME = 1001;
	private static final int QUESTION_ID_IDOLFEST_VOTE = 1002;

	// Special events server id
	/**
	 * To disable, set the event ID to EVENT_ID_NO_EVENT.
	 * To add a new event, update methods:
	 * {@link #convertUserInputForEvent(ConventionEvent.UserInput, ConventionEvent)}
	 * {@link #getEventVoteSender(ConventionEvent)}
	 */
	private static final int EVENT_ID_NO_EVENT = -1;
	private static final int EVENT_ID_AMAIDOL = 3712;
	private static final int EVENT_ID_IDOLFEST = 3713;

	// Ids of google spreadsheets associated with the special events
	private static final String AMAIDOL_SPREADSHEET_ID = "1u9xu3FNq2gA25oZoVHVguTzJA5HheXWPf2wnUj-iipE";
	private static final String IDOLFEST_SPREADSHEET_ID = "1tTqrnVOzDnu_wesOMKmbtW5nk6bFpFsk9DhkfcYG_n0";

	static {
		FeedbackQuestion.addQuestion(QUESTION_ID_AMAIDOL_NAME, R.string.amaidol_name_question);
		FeedbackQuestion.addQuestion(QUESTION_ID_AMAIDOL_VOTE, R.string.amaidol_vote_question);
		FeedbackQuestion.addQuestion(QUESTION_ID_IDOLFEST_VOTE, R.string.idolfest_vote_question);
	}

	// Stand types
	private enum StandType implements Stand.StandType {
		JEWELRY(R.string.jewelry_stand, R.drawable.diamond_24px),
		CLOTHES(R.string.clothes_stand, R.drawable.shirt),
		HAND_MADE(R.string.hand_made_stand, R.drawable.content_cut_24px),
		CREATORS(R.string.creators_stand, R.drawable.diamond_24px),
		MERCH(R.string.merch_stand, R.drawable.ic_shopping_basket),
		OTHER(R.string.other_stand, R.drawable.icon_animatsuri),
		TABLETOP_GAMES(R.string.tabletop_games_stand, R.drawable.casino_24px),
		ROLE_PLAY_GAMES(R.string.role_play_games_stand, R.drawable.swords_24px),
		VIDEO_GAMES(R.string.video_games_stand, R.drawable.videogame_black),
		MANGA(R.string.manga_stand, R.drawable.book),
		BOOKS(R.string.books_stand, R.drawable.book),
		FIGURES(R.string.figures_stand, R.drawable.face_2_24px),
		DOLLS(R.string.dolls_stand, R.drawable.face_2_24px),
		ARTIST(R.string.artist_stand, R.drawable.ic_color_lens),
		GENERAL(R.string.general_stand, R.drawable.ic_shopping_basket);

		private final int title;
		private final int image;

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
		return new ConventionStorage(this, R.raw.animatsuri2026_convention_events, 4);
	}

	@Override
	protected Calendar initDate() {
		Calendar date = Calendar.getInstance();
		date.clear();
		date.set(2026, Calendar.AUGUST, 27);
		return date;
	}

	@Override
	protected String initID() {
		return "Animatsuri2026";
	}

	@Override
	protected String initDisplayName() {
		return "אנימאטסורי 2026";
	}

	@Override
	protected URL initUpdatesURL() {
		try {
			return new URL("https://us-central1-starlit-brand-95018.cloudfunctions.net/getFeed?page=animatsuri.org.il");
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
		EventFeedbackForm eventFeedbackForm;
		try {
			eventFeedbackForm = (EventFeedbackForm) new EventFeedbackForm()
					.withEventTitleEntry("entry.1847107867")
					.withEventTimeEntry("entry.1648362575")
					.withHallEntry("entry.1510105148")
					.withConventionNameEntry("entry.1882876736")
					.withDeviceIdEntry("entry.312890800")
					.withTestEntry("entry.791883029")
					.withOsEntry("entry.1551173705")
					.withVersionEntry("entry.1280601324")
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
		FeedbackForm feedbackForm;
		try {
			feedbackForm = (FeedbackForm) new FeedbackForm()
					.withConventionNameEntry("entry.1882876736")
					.withDeviceIdEntry("entry.312890800")
					.withTestEntry("entry.791883029")
					.withOsEntry("entry.608096033")
					.withVersionEntry("entry.1190523439")
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
			return new URL("https://animatsuri.org.il/wp-admin/admin-ajax.php?action=get_event_list");
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
//		imageMapper.addMapping(ImageIdToImageResourceMapper.EVENT_GENERIC, R.drawable.event_activity_background);

		return new ImageIdToImageResourceMapper();
	}

	@Override
	protected Halls initHalls() {
		List<Hall> halls = Arrays.asList(
			new Hall().withName(MAIN_HALL_NAME).withShelter(true),
			new Hall().withName(ORANIM_NAME).withShelter(true),
			new Hall().withName(ESHKOL1_NAME).withShelter(true),
			new Hall().withName(ESHKOL2_NAME).withShelter(true),
			new Hall().withName(ESHKOL3_NAME).withShelter(true),
			new Hall().withName(GAMES_NAME).withShelter(true),
			new Hall().withName(COSPLAY_AREA_NAME)
		);
		int i = 1;
		for (Hall hall : halls) {
			hall.setOrder(i);
			++i;
		}
		return new Halls(halls);
	}

	@Override
	protected ConventionMap initMap() {
		return createMap();
	}

	private ConventionMap createMap() {
		Hall mainHall = this.getHalls().findByName(MAIN_HALL_NAME);
		Hall oranim = this.getHalls().findByName(ORANIM_NAME);
		Hall eshkol1 = this.getHalls().findByName(ESHKOL1_NAME);
		Hall eshkol2 = this.getHalls().findByName(ESHKOL2_NAME);
		Hall eshkol3 = this.getHalls().findByName(ESHKOL3_NAME);
		Hall games = this.getHalls().findByName(GAMES_NAME);
		Hall cosplayArea = this.getHalls().findByName(COSPLAY_AREA_NAME);

		Floor entrance = new Floor(1)
			.withName("מתחם כניסה")
			.withImageResource(R.drawable.animatsuri2026_entrance, false)
			.withImageWidth(1724.04f)
			.withImageHeight(1106.58f);
		Floor floor1 = new Floor(2)
			.withName("קומה 1")
			.withImageResource(R.drawable.animatsuri2026_floor1, false)
			.withImageWidth(1848.48f)
			.withImageHeight(1301f);
		Floor floor2 = new Floor(3)
			.withName("קומה 2")
			.withImageResource(R.drawable.animatsuri2026_floor2, false)
			.withImageWidth(1862.16f)
			.withImageHeight(981.37f);

		StandsArea tedi = new StandsArea()
			.withName("אולם טדי")
			.withStandLocations(getTediStandLocations()) // This must be initialized before the stands
			.withStands(getTediStands())
			.withImageResource(R.drawable.animatsuri2026_stands_tedi)
			.withImageWidth(3677.000f)
			.withImageHeight(4208.000f);
		StandsArea agam = new StandsArea()
			.withName("אולם אגם")
			.withStandLocations(getAgamStandLocations()) // This must be initialized before the stands
			.withStands(getAgamStands())
			.withImageResource(R.drawable.animatsuri2026_stands_agam)
			.withImageWidth(4641.000f)
			.withImageHeight(1826.000f);

		return new ConventionMap()
			.withFloors(Arrays.asList(entrance, floor1, floor2))
			.withDefaultFloor(floor1)
			.withLocations(
				CollectionUtils.flattenList(
					inFloor(entrance,
						new MapLocation()
							.withPlace(new Place().withName("עמדת מודיעין ודוכן אמא\"י"))
							.withMarkerResource(R.drawable.animatsuri2026_marker_info_entrance, false)
							.withSelectedMarkerResource(R.drawable.animatsuri2026_selected_marker_info_entrance, false)
							.withMarkerHeight(155.91f)
							.withX(1132.73f)
							.withY(776.17f),
						new MapLocation()
							.withPlace(new Place().withName("עמדת צימוד"))
							.withMarkerResource(R.drawable.animatsuri2026_marker_bracelets, false)
							.withSelectedMarkerResource(R.drawable.animatsuri2026_selected_marker_bracelets, false)
							.withMarkerHeight(78.83f)
							.withX(823.48f)
							.withY(793.85f),
						new MapLocation()
							.withPlace(new Place().withName("קופות"))
							.withMarkerResource(R.drawable.animatsuri2026_marker_cashiers, false)
							.withSelectedMarkerResource(R.drawable.animatsuri2026_selected_marker_cashiers, false)
							.withMarkerHeight(78.83f)
							.withX(623.91f)
							.withY(762.3f),
						new MapLocation()
							.withPlace(new Place().withName(ACCESSIBLE_CASHIERS_NAME))
							.withMarkerResource(R.drawable.animatsuri2026_marker_accessible_cashier, false)
							.withSelectedMarkerResource(R.drawable.animatsuri2026_selected_marker_accessible_cashier, false)
							.withMarkerHeight(69.12f)
							.withX(811.54f)
							.withY(633.52f),
						new MapLocation()
							.withPlace(new Place().withName("עמדת פלוס"))
							.withMarkerResource(R.drawable.animatsuri2026_marker_plus_cashier, false)
							.withSelectedMarkerResource(R.drawable.animatsuri2026_selected_marker_plus_cashier, false)
							.withMarkerHeight(71.9f)
							.withX(1065f)
							.withY(498.16f),
						new MapLocation()
							.withPlace(new Place().withName("מתחם קנייה במקום"))
							.withMarkerResource(R.drawable.animatsuri2026_marker_purchase, false)
							.withSelectedMarkerResource(R.drawable.animatsuri2026_selected_marker_purchase, false)
							.withMarkerHeight(78.83f)
							.withX(439.27f)
							.withY(608.78f),
						new MapLocation()
							.withPlace(new Place().withName("מתחם הזמנה מראש"))
							.withMarkerResource(R.drawable.animatsuri2026_marker_preorders, false)
							.withSelectedMarkerResource(R.drawable.animatsuri2026_selected_marker_preorders, false)
							.withMarkerHeight(78.83f)
							.withX(844.26f)
							.withY(385.18f),
						new MapLocation()
							.withPlace(new Place().withName("מעבר נגיש"))
							.withMarkerResource(R.drawable.animatsuri2026_marker_accessible_passage, false)
							.withSelectedMarkerResource(R.drawable.animatsuri2026_selected_marker_accessible_passage, false)
							.withMarkerHeight(78.83f)
							.withX(304.94f)
							.withY(380.22f),
						new MapLocation()
							.withPlace(new Place().withName(ACCESSIBLE_CASHIERS_NAME))
							.withMarkerResource(R.drawable.animatsuri2026_marker_accessible_cashier, false)
							.withSelectedMarkerResource(R.drawable.animatsuri2026_selected_marker_accessible_cashier, false)
							.withMarkerHeight(69.12f)
							.withX(707.38f)
							.withY(254.79f)
					),
					inFloor(floor1,
						new MapLocation()
							.withPlace(new Place().withName("שירותים"))
							.withMarkerResource(R.drawable.animatsuri2026_marker_toilet, false)
							.withSelectedMarkerResource(R.drawable.animatsuri2026_selected_marker_toilet, false)
							.withMarkerHeight(101.95f)
							.withX(1563.345f)
							.withY(712.84f),
						new MapLocation()
							.withPlace(oranim)
							.withMarkerResource(R.drawable.animatsuri2026_marker_oranim, false)
							.withSelectedMarkerResource(R.drawable.animatsuri2026_selected_marker_oranim, false)
							.withMarkerHeight(161f)
							.withX(1415.98f)
							.withY(775f),
						new MapLocation()
							.withName("מתחם דוכנים")
							.withDescription("החלק הפנימי של מתחם זה הינו מרחב מוגן.")
							.withPlace(tedi.withShelter(true))
							.withMarkerResource(R.drawable.animatsuri2026_marker_stands, false)
							.withSelectedMarkerResource(R.drawable.animatsuri2026_selected_marker_stands, false)
							.withMarkerHeight(147.16f)
							.withX(894.505f)
							.withY(999.19f),
						new MapLocation()
							.withPlace(new Place().withName("שירותים"))
							.withMarkerResource(R.drawable.animatsuri2026_marker_toilet, false)
							.withSelectedMarkerResource(R.drawable.animatsuri2026_selected_marker_toilet, false)
							.withMarkerHeight(102.44f)
							.withX(547.345f)
							.withY(944.35f),
						new MapLocation()
							.withPlace(new Place().withName("מתחם אוכל"))
							.withMarkerResource(R.drawable.animatsuri2026_marker_food_court, false)
							.withSelectedMarkerResource(R.drawable.animatsuri2026_selected_marker_food_court, false)
							.withMarkerHeight(98.44f)
							.withX(1141.735f)
							.withY(662.56f),
						new MapLocation()
							.withPlace(games)
							.withName("מתחם המשחקייה")
							.withMarkerResource(R.drawable.animatsuri2026_marker_games, false)
							.withSelectedMarkerResource(R.drawable.animatsuri2026_selected_marker_games, false)
							.withMarkerHeight(129.83f)
							.withX(1298.98f)
							.withY(375.17f),
						new MapLocation()
							.withPlace(new Place().withName("שמירת חפצים").withShelter(true))
							.withMarkerResource(R.drawable.animatsuri2026_marker_storage, false)
							.withSelectedMarkerResource(R.drawable.animatsuri2026_selected_marker_storage, false)
							.withMarkerHeight(128.04f)
							.withX(1122.235f)
							.withY(237.96f),
						new MapLocation()
							.withPlace(new Place().withName("דוכן מרצ'"))
							.withMarkerResource(R.drawable.animatsuri2026_marker_merch, false)
							.withSelectedMarkerResource(R.drawable.animatsuri2026_selected_marker_merch, false)
							.withMarkerHeight(106.23f)
							.withX(997.495f)
							.withY(483.79f),
						new MapLocation()
							.withPlace(new Place().withName("מודיעין ודוכן אמא\"י"))
							.withMarkerResource(R.drawable.animatsuri2026_marker_info, false)
							.withSelectedMarkerResource(R.drawable.animatsuri2026_selected_marker_info, false)
							.withMarkerHeight(193.42f)
							.withX(825.06f)
							.withY(409.4f),
						new MapLocation()
							.withPlace(new Place().withName("שירותים"))
							.withMarkerResource(R.drawable.animatsuri2026_marker_toilet, false)
							.withSelectedMarkerResource(R.drawable.animatsuri2026_selected_marker_toilet, false)
							.withMarkerHeight(102.44f)
							.withX(933.6f)
							.withY(157.56f),
						new MapLocation()
							.withPlace(new Place().withName("מעלית"))
							.withMarkerResource(R.drawable.animatsuri2026_marker_elevator, false)
							.withSelectedMarkerResource(R.drawable.animatsuri2026_selected_marker_elevator, false)
							.withMarkerHeight(85.812f)
							.withX(827.15f)
							.withY(233.28f),
						new MapLocation()
							.withPlace(eshkol3)
							.withMarkerResource(R.drawable.animatsuri2026_marker_eshkol3, false)
							.withSelectedMarkerResource(R.drawable.animatsuri2026_selected_marker_eshkol3, false)
							.withMarkerHeight(123.67f)
							.withX(376.405f)
							.withY(778.33f),
						new MapLocation()
							.withPlace(eshkol2)
							.withMarkerResource(R.drawable.animatsuri2026_marker_eshkol2, false)
							.withSelectedMarkerResource(R.drawable.animatsuri2026_selected_marker_eshkol2, false)
							.withMarkerHeight(123.67f)
							.withX(540.405f)
							.withY(778.33f),
						new MapLocation()
							.withPlace(eshkol1)
							.withMarkerResource(R.drawable.animatsuri2026_marker_eshkol1, false)
							.withSelectedMarkerResource(R.drawable.animatsuri2026_selected_marker_eshkol1, false)
							.withMarkerHeight(123.68f)
							.withX(500.405f)
							.withY(595f),
						new MapLocation()
							.withPlace(new FloorLocation().withFloor(floor2).withName("מעבר לקומה 2"))
							.withMarkerResource(R.drawable.animatsuri2026_marker_goto_floor2, false)
							.withSelectedMarkerResource(R.drawable.animatsuri2026_selected_marker_goto_floor2, false)
							.withMarkerHeight(71.4f)
							.withX(219.195f)
							.withY(734.24f),
						new MapLocation()
							.withPlace(new Place().withName("שירותים"))
							.withMarkerResource(R.drawable.animatsuri2026_marker_toilet, false)
							.withSelectedMarkerResource(R.drawable.animatsuri2026_selected_marker_toilet, false)
							.withMarkerHeight(102.44f)
							.withX(309.2f)
							.withY(505.77f),
						new MapLocation()
							.withPlace(new Place().withName("מעלית"))
							.withMarkerResource(R.drawable.animatsuri2026_marker_elevator, false)
							.withSelectedMarkerResource(R.drawable.animatsuri2026_selected_marker_elevator, false)
							.withMarkerHeight(85.812f)
							.withX(136.165f)
							.withY(599.93f)
					),
					inFloor(floor2,
						new MapLocation()
							.withPlace(new Place().withName("שירותים"))
							.withMarkerResource(R.drawable.animatsuri2026_marker_toilet, false)
							.withSelectedMarkerResource(R.drawable.animatsuri2026_selected_marker_toilet, false)
							.withMarkerHeight(102.44f)
							.withX(1683.5f)
							.withY(640.82f),
						new MapLocation()
							.withPlace(new Place().withName(CHILDREN_ROOM_NAME))
							.withMarkerResource(R.drawable.animatsuri2026_marker_children, false)
							.withSelectedMarkerResource(R.drawable.animatsuri2026_selected_marker_children, false)
							.withMarkerHeight(98.35f)
							.withX(1485.335f)
							.withY(743.16f),
						new MapLocation()
							.withPlace(new Place().withName("שיפוט קוספליי"))
							.withMarkerResource(R.drawable.animatsuri2026_marker_cosplay_judgement, false)
							.withSelectedMarkerResource(R.drawable.animatsuri2026_selected_marker_cosplay_judgement, false)
							.withMarkerHeight(113.29f)
							.withX(1072.065f)
							.withY(759.75f),
						new MapLocation()
							.withPlace(cosplayArea)
							.withMarkerResource(R.drawable.animatsuri2026_marker_cosplay_area, false)
							.withSelectedMarkerResource(R.drawable.animatsuri2026_selected_marker_cosplay_area, false)
							.withMarkerHeight(195.84f)
							.withX(826.905f)
							.withY(685.53f),
						new MapLocation()
							.withPlace(mainHall)
							.withMarkerResource(R.drawable.animatsuri2026_marker_main_hall, false)
							.withSelectedMarkerResource(R.drawable.animatsuri2026_selected_marker_main_hall, false)
							.withMarkerHeight(156f)
							.withX(1348.295f)
							.withY(633.97f),
						new MapLocation()
							.withPlace(new Place().withName("כניסה נגישה לאולם ראשי"))
							.withMarkerResource(R.drawable.animatsuri2026_marker_accessible_entrance, false)
							.withSelectedMarkerResource(R.drawable.animatsuri2026_selected_marker_accessible_entrance, false)
							.withMarkerHeight(98.35f)
							.withX(1144.915f)
							.withY(637.24f),
						new MapLocation()
							.withPlace(new Place().withName("כניסה ראשית לאולם ראשי"))
							.withMarkerResource(R.drawable.animatsuri2026_marker_main_entrance, false)
							.withSelectedMarkerResource(R.drawable.animatsuri2026_selected_marker_main_entrance, false)
							.withMarkerHeight(93.72f)
							.withX(1000.015f)
							.withY(591.24f),
						new MapLocation()
							.withPlace(new Place().withName("כניסת פלוס לאולם ראשי"))
							.withMarkerResource(R.drawable.animatsuri2026_marker_plus_entrance, false)
							.withSelectedMarkerResource(R.drawable.animatsuri2026_selected_marker_plus_entrance, false)
							.withMarkerHeight(93.08f)
							.withX(1345.835f)
							.withY(497.93f),
						new MapLocation()
							.withName("שדרת הציירים")
							.withPlace(agam)
							.withMarkerResource(R.drawable.animatsuri2026_marker_artist_alley, false)
							.withSelectedMarkerResource(R.drawable.animatsuri2026_selected_marker_artist_alley, false)
							.withMarkerHeight(91.99f)
							.withX(831.835f)
							.withY(396.98f),
						new MapLocation()
							.withPlace(new Place().withName("שירותים"))
							.withMarkerResource(R.drawable.animatsuri2026_marker_toilet, false)
							.withSelectedMarkerResource(R.drawable.animatsuri2026_selected_marker_toilet, false)
							.withMarkerHeight(100.23f)
							.withX(1186.575f)
							.withY(190.03f),
						new MapLocation()
							.withPlace(new Place().withName("מעלית"))
							.withMarkerResource(R.drawable.animatsuri2026_marker_elevator, false)
							.withSelectedMarkerResource(R.drawable.animatsuri2026_selected_marker_elevator, false)
							.withMarkerHeight(85.572f)
							.withX(1071.615f)
							.withY(204.23f),
						new MapLocation()
							.withPlace(new FloorLocation().withFloor(floor1).withName("מעבר לאשכולות"))
							.withMarkerResource(R.drawable.animatsuri2026_marker_goto_floor1, false)
							.withSelectedMarkerResource(R.drawable.animatsuri2026_selected_marker_goto_floor1, false)
							.withMarkerHeight(102.77f)
							.withX(296.59f)
							.withY(559.86f),
						new MapLocation()
							.withPlace(new Place().withName("שירותים"))
							.withMarkerResource(R.drawable.animatsuri2026_marker_toilet, false)
							.withSelectedMarkerResource(R.drawable.animatsuri2026_selected_marker_toilet, false)
							.withMarkerHeight(85.66f)
							.withX(452.05f)
							.withY(518.12f),
						new MapLocation()
							.withPlace(new Place().withName("מעלית"))
							.withMarkerResource(R.drawable.animatsuri2026_marker_elevator, false)
							.withSelectedMarkerResource(R.drawable.animatsuri2026_selected_marker_elevator, false)
							.withMarkerHeight(85.562f)
							.withX(368.57f)
							.withY(588.57f)
					)
				)
			);
	}

	private List<Stand> getAgamStands() {
		return Arrays.asList(
			new Stand().withName("The Slavic Witches - המכשפות הסלביות").withType(StandType.ARTIST).withLocationIds("a1", "a2"),
			new Stand().withName("HoshiGo").withType(StandType.ARTIST).withLocationIds("a10"),
			new Stand().withName("Nod3ret").withType(StandType.ARTIST).withLocationIds("a11", "a12"),
			new Stand().withName("מריליה").withType(StandType.ARTIST).withLocationIds("a13", "a14"),
			new Stand().withName("lil puppy").withType(StandType.ARTIST).withLocationIds("a15"),
			new Stand().withName("Techelet Art").withType(StandType.ARTIST).withLocationIds("a16"),
			new Stand().withName("Raybae").withType(StandType.ARTIST).withLocationIds("a17"),
			new Stand().withName("JustAWetTowel").withType(StandType.ARTIST).withLocationIds("a18"),
			new Stand().withName("Sel & Aiko’s Booth").withType(StandType.ARTIST).withLocationIds("a19"),
			new Stand().withName("HillelArt").withType(StandType.ARTIST).withLocationIds("a20"),
			new Stand().withName("rotemz and oranotoren").withType(StandType.ARTIST).withLocationIds("a21", "a22"),
			new Stand().withName("Lianrycal").withType(StandType.ARTIST).withLocationIds("a23"),
			new Stand().withName("Mete-Art").withType(StandType.ARTIST).withLocationIds("a24"),
			new Stand().withName("Lapinvert.e").withType(StandType.ARTIST).withLocationIds("a25", "a26"),
			new Stand().withName("rubraboa").withType(StandType.ARTIST).withLocationIds("a27", "a28"),
			new Stand().withName("Orezpan Art").withType(StandType.ARTIST).withLocationIds("a29"),
			new Stand().withName("TomatoesTrash").withType(StandType.ARTIST).withLocationIds("a3"),
			new Stand().withName("Pepprex Arts").withType(StandType.ARTIST).withLocationIds("a30"),
			new Stand().withName("Shinomi Art").withType(StandType.ARTIST).withLocationIds("a31"),
			new Stand().withName("Learoosh").withType(StandType.ARTIST).withLocationIds("a32", "a33"),
			new Stand().withName("Unlovablycozy").withType(StandType.ARTIST).withLocationIds("a34"),
			new Stand().withName("שי קאגאמינה").withType(StandType.ARTIST).withLocationIds("a35", "a36"),
			new Stand().withName("Sharkioo").withType(StandType.ARTIST).withLocationIds("a37", "a38"),
			new Stand().withName("הדוכן של דני וגיל").withType(StandType.ARTIST).withLocationIds("a39"),
			new Stand().withName("strawberry flavors").withType(StandType.ARTIST).withLocationIds("a4"),
			new Stand().withName("fleshvore").withType(StandType.ARTIST).withLocationIds("a40"),
			new Stand().withName("הדוכן של אריאל המדליק").withType(StandType.ARTIST).withLocationIds("a41", "a42"),
			new Stand().withName("Meitlavi95").withType(StandType.ARTIST).withLocationIds("a43", "a44"),
			new Stand().withName("Donrex").withType(StandType.ARTIST).withLocationIds("a45"),
			new Stand().withName("Angelofthyrsday").withType(StandType.ARTIST).withLocationIds("a46"),
			new Stand().withName("Plazma").withType(StandType.ARTIST).withLocationIds("a5"),
			new Stand().withName("The Chip Club").withType(StandType.ARTIST).withLocationIds("a6", "a7"),
			new Stand().withName("Gasi_CL").withType(StandType.ARTIST).withLocationIds("a8"),
			new Stand().withName("Elmiellart").withType(StandType.ARTIST).withLocationIds("a9"),
			new Stand().withName("Fluffykittenka").withType(StandType.ARTIST).withLocationIds("b1"),
			new Stand().withName("Rin_isintheshower").withType(StandType.ARTIST).withLocationIds("b10"),
			new Stand().withName("nallybus").withType(StandType.ARTIST).withLocationIds("b11", "b12"),
			new Stand().withName("Burucheri x Lettuce").withType(StandType.ARTIST).withLocationIds("b13", "b14"),
			new Stand().withName("FlyingFox Art").withType(StandType.ARTIST).withLocationIds("b15", "b16"),
			new Stand().withName("livinkart").withType(StandType.ARTIST).withLocationIds("b17"),
			new Stand().withName("YUEvander").withType(StandType.ARTIST).withLocationIds("b18", "b19"),
			new Stand().withName("Captain Ayay").withType(StandType.ARTIST).withLocationIds("b2", "b3"),
			new Stand().withName("Vivi Fox").withType(StandType.ARTIST).withLocationIds("b20"),
			new Stand().withName("Norpamidor").withType(StandType.ARTIST).withLocationIds("b21"),
			new Stand().withName("Moonmor & Foxyohay").withType(StandType.ARTIST).withLocationIds("b22"),
			new Stand().withName("Ray slay").withType(StandType.ARTIST).withLocationIds("b23"),
			new Stand().withName("Purple bunny").withType(StandType.ARTIST).withLocationIds("b24"),
			new Stand().withName("Ro._.chan").withType(StandType.ARTIST).withLocationIds("b25", "b26"),
			new Stand().withName("CHRONIIKA").withType(StandType.ARTIST).withLocationIds("b27", "b28"),
			new Stand().withName("kartzi's").withType(StandType.ARTIST).withLocationIds("b29", "b30"),
			new Stand().withName("shorterthan").withType(StandType.ARTIST).withLocationIds("b31", "b32"),
			new Stand().withName("adelistic").withType(StandType.ARTIST).withLocationIds("b33", "b34"),
			new Stand().withName("Kaegomi").withType(StandType.ARTIST).withLocationIds("b35"),
			new Stand().withName("Bogouki").withType(StandType.ARTIST).withLocationIds("b36"),
			new Stand().withName("אוריג'ין סטורי").withType(StandType.ARTIST).withLocationIds("b37", "b38"),
			new Stand().withName("הפועל מיו מיו").withType(StandType.ARTIST).withLocationIds("b39"),
			new Stand().withName("fish★teeth").withType(StandType.ARTIST).withLocationIds("b4"),
			new Stand().withName("B4RMN").withType(StandType.ARTIST).withLocationIds("b40", "b41"),
			new Stand().withName("Tokisesa").withType(StandType.ARTIST).withLocationIds("b42"),
			new Stand().withName("Smatan Gold").withType(StandType.ARTIST).withLocationIds("b43", "b44"),
			new Stand().withName("Inimi Draws! Art by Maayan Elbaz").withType(StandType.ARTIST).withLocationIds("b45", "b46"),
			new Stand().withName("Hikikomoring - Art by Sem Daniel").withType(StandType.ARTIST).withLocationIds("b47", "b48"),
			new Stand().withName("Naamoola").withType(StandType.ARTIST).withLocationIds("b49"),
			new Stand().withName("sonderein").withType(StandType.ARTIST).withLocationIds("b5"),
			new Stand().withName("Shandrwa").withType(StandType.ARTIST).withLocationIds("b50"),
			new Stand().withName("eladb_art").withType(StandType.ARTIST).withLocationIds("b51"),
			new Stand().withName("Multipotent").withType(StandType.ARTIST).withLocationIds("b52"),
			new Stand().withName("Kimichu.x").withType(StandType.ARTIST).withLocationIds("b53", "b54"),
			new Stand().withName("Koruhiko").withType(StandType.ARTIST).withLocationIds("b55", "b56"),
			new Stand().withName("רותם רקיר").withType(StandType.ARTIST).withLocationIds("b57", "b58"),
			new Stand().withName("Selenita").withType(StandType.ARTIST).withLocationIds("b59", "b60"),
			new Stand().withName("הדוכן המגניב של גופבול").withType(StandType.ARTIST).withLocationIds("b6"),
			new Stand().withName("Awii.ner").withType(StandType.ARTIST).withLocationIds("b61"),
			new Stand().withName("Puffermish artz").withType(StandType.ARTIST).withLocationIds("b62"),
			new Stand().withName("הדוכן של קאספר").withType(StandType.ARTIST).withLocationIds("b63"),
			new Stand().withName("Dinchies").withType(StandType.ARTIST).withLocationIds("b64"),
			new Stand().withName("Nighto").withType(StandType.ARTIST).withLocationIds("b65", "b66"),
			new Stand().withName("VSRSTUFF").withType(StandType.ARTIST).withLocationIds("b67", "b68"),
			new Stand().withName("NatArt").withType(StandType.ARTIST).withLocationIds("b69", "b70"),
			new Stand().withName("Jupiilol").withType(StandType.ARTIST).withLocationIds("b7"),
			new Stand().withName("Cryptic Arts").withType(StandType.ARTIST).withLocationIds("b8", "b9")
		);
	}

	private List<Stand> getTediStands() {
		return Arrays.asList(
			new Stand().withName("סטימצקי").withType(StandType.GENERAL).withLocationIds("d1", "d2", "d3", "d4", "d5"),
			new Stand().withName("Paludu's").withType(StandType.HAND_MADE).withLocationIds("e1", "e2"),
			new Stand().withName("Kuzco").withType(StandType.MERCH).withLocationIds("e11", "e12"),
			new Stand().withName("Sugoii ! Anime streetwear").withType(StandType.CLOTHES).withLocationIds("e13"),
			new Stand().withName("אפריל").withType(StandType.GENERAL).withLocationIds("e14"),
			new Stand().withName("Bored reys art").withType(StandType.GENERAL).withLocationIds("e15"),
			new Stand().withName("Yael's Colors").withType(StandType.MERCH).withLocationIds("e16"),
			new Stand().withName("מ.ש. אלבוים").withType(StandType.MANGA).withLocationIds("e17"),
			new Stand().withName("Seal mochi kawaii shop").withType(StandType.JEWELRY).withLocationIds("e18"),
			new Stand().withName("SoniAnimeSocks").withType(StandType.CLOTHES).withLocationIds("e19"),
			new Stand().withName("Velvet Octopus").withType(StandType.CLOTHES).withLocationIds("e20"),
			new Stand().withName("קאמיקוני").withType(StandType.MERCH).withLocationIds("e21"),
			new Stand().withName("Pilmeny lashes").withType(StandType.OTHER).withLocationIds("e22"),
			new Stand().withName("Fujoshis favourite place").withType(StandType.GENERAL).withLocationIds("e23"),
			new Stand().withName("Colourete + shashux").withType(StandType.CLOTHES).withLocationIds("e24"),
			new Stand().withName("AkinaPaz").withType(StandType.MERCH).withLocationIds("e25"),
			new Stand().withName("Fantasy house").withType(StandType.MERCH).withLocationIds("e26", "e27"),
			new Stand().withName("Raspberry").withType(StandType.CLOTHES).withLocationIds("e28"),
			new Stand().withName("SweetheartYun").withType(StandType.CLOTHES).withLocationIds("e29"),
			new Stand().withName("GachAnime").withType(StandType.TABLETOP_GAMES).withLocationIds("e3"),
			new Stand().withName("N FIG").withType(StandType.FIGURES).withLocationIds("e30", "e31"),
			new Stand().withName("פריק").withType(StandType.TABLETOP_GAMES).withLocationIds("e32"),
			new Stand().withName("Ultimate.Collect.IL").withType(StandType.GENERAL).withLocationIds("e33"),
			new Stand().withName("ישראקומיקס").withType(StandType.MERCH).withLocationIds("e34", "e35"),
			new Stand().withName("יוצרים עם דוד").withType(StandType.HAND_MADE).withLocationIds("e36"),
			new Stand().withName("Mini Tokio").withType(StandType.MERCH).withLocationIds("e37"),
			new Stand().withName("Otaku and Fujoshi").withType(StandType.MERCH).withLocationIds("e4"),
			new Stand().withName("Art studio handmade").withType(StandType.HAND_MADE).withLocationIds("e5"),
			new Stand().withName("Your Friend 's Goodies").withType(StandType.MANGA).withLocationIds("e6"),
			new Stand().withName("Gear fifth figures").withType(StandType.MERCH).withLocationIds("e7"),
			new Stand().withName("Molly's").withType(StandType.HAND_MADE).withLocationIds("e8"),
			new Stand().withName("קימבי").withType(StandType.MERCH).withLocationIds("e9", "e10"),
			new Stand().withName("Craftella").withType(StandType.CREATORS).withLocationIds("f1"),
			new Stand().withName("crimson soda").withType(StandType.CREATORS).withLocationIds("f11"),
			new Stand().withName("LoLy’s Booth").withType(StandType.CREATORS).withLocationIds("f12"),
			new Stand().withName("pointlessfield").withType(StandType.CREATORS).withLocationIds("f13"),
			new Stand().withName("Art by Ayala").withType(StandType.CREATORS).withLocationIds("f14"),
			new Stand().withName("YBOO!").withType(StandType.CREATORS).withLocationIds("f15"),
			new Stand().withName("Jill._.creations").withType(StandType.CREATORS).withLocationIds("f16"),
			new Stand().withName("רפאים").withType(StandType.CREATORS).withLocationIds("f17", "f18"),
			new Stand().withName("דיגידאן טירו-טאן").withType(StandType.CREATORS).withLocationIds("f19"),
			new Stand().withName("Cosmo's Trinket Shop").withType(StandType.CREATORS).withLocationIds("f2"),
			new Stand().withName("Gabisweb").withType(StandType.CREATORS).withLocationIds("f20"),
			new Stand().withName("Pixel Kid").withType(StandType.CREATORS).withLocationIds("f21"),
			new Stand().withName("XX סטודיו").withType(StandType.CREATORS).withLocationIds("f22"),
			new Stand().withName("orazashy").withType(StandType.CREATORS).withLocationIds("f23"),
			new Stand().withName("candytoast").withType(StandType.CREATORS).withLocationIds("f24"),
			new Stand().withName("Kawaii Land Shop - קאוואי לנד שופ").withType(StandType.CREATORS).withLocationIds("f25"),
			new Stand().withName("Satanic Panic Shop").withType(StandType.CREATORS).withLocationIds("f26"),
			new Stand().withName("Air Nomads Crafts").withType(StandType.CREATORS).withLocationIds("f27"),
			new Stand().withName("חלומות").withType(StandType.CREATORS).withLocationIds("f28"),
			new Stand().withName("Shoshi's").withType(StandType.CREATORS).withLocationIds("f29"),
			new Stand().withName("dreamydoll x Mei accessories").withType(StandType.CREATORS).withLocationIds("f3"),
			new Stand().withName("yaelas art").withType(StandType.CREATORS).withLocationIds("f30"),
			new Stand().withName("Orchi & Shelly").withType(StandType.CREATORS).withLocationIds("f31", "f32"),
			new Stand().withName("Ms_crochettt").withType(StandType.CREATORS).withLocationIds("f33"),
			new Stand().withName("sampai designs").withType(StandType.CREATORS).withLocationIds("f34"),
			new Stand().withName("Art By Eli").withType(StandType.CREATORS).withLocationIds("f35"),
			new Stand().withName("Tal's Fantasy Creations").withType(StandType.CREATORS).withLocationIds("f36"),
			new Stand().withName("SpoonKit").withType(StandType.CREATORS).withLocationIds("f37"),
			new Stand().withName("Curly Craft").withType(StandType.CREATORS).withLocationIds("f38"),
			new Stand().withName("הפרוותית").withType(StandType.CREATORS).withLocationIds("f39"),
			new Stand().withName("Paws and Palette").withType(StandType.CREATORS).withLocationIds("f4"),
			new Stand().withName("Natoki").withType(StandType.CREATORS).withLocationIds("f40"),
			new Stand().withName("Sivs Crochet & Avironpie").withType(StandType.CREATORS).withLocationIds("f41"),
			new Stand().withName("Merc Drop").withType(StandType.CREATORS).withLocationIds("f42"),
			new Stand().withName("shir k").withType(StandType.CREATORS).withLocationIds("f43", "f44"),
			new Stand().withName("איגוד מקצועות האנימציה").withType(StandType.GENERAL).withLocationIds("f45"),
			new Stand().withName("J.C MAKES ART!").withType(StandType.CREATORS).withLocationIds("f46"),
			new Stand().withName("iDollsCollection").withType(StandType.CREATORS).withLocationIds("f47"),
			new Stand().withName("Stawbee's Art").withType(StandType.CREATORS).withLocationIds("f48"),
			new Stand().withName("מרים יעל- קומיק ומרצ׳").withType(StandType.CREATORS).withLocationIds("f49"),
			new Stand().withName("Nekkuresu").withType(StandType.CREATORS).withLocationIds("f5"),
			new Stand().withName("Scylla art").withType(StandType.CREATORS).withLocationIds("f50"),
			new Stand().withName("Stuffer").withType(StandType.CREATORS).withLocationIds("f51"),
			new Stand().withName("Redkon Art").withType(StandType.CREATORS).withLocationIds("f52"),
			new Stand().withName("Baaahd Girl").withType(StandType.CREATORS).withLocationIds("f53", "f54"),
			new Stand().withName("דניהלמן ארט").withType(StandType.CREATORS).withLocationIds("f55"),
			new Stand().withName("Tslil jewelry").withType(StandType.CREATORS).withLocationIds("f56"),
			new Stand().withName("Cherry Staff🍒✨").withType(StandType.CREATORS).withLocationIds("f57"),
			new Stand().withName("AFlair").withType(StandType.CREATORS).withLocationIds("f58"),
			new Stand().withName("Anime Glass").withType(StandType.CREATORS).withLocationIds("f59"),
			new Stand().withName("Orion Geek Jewelry").withType(StandType.CREATORS).withLocationIds("f6"),
			new Stand().withName("Red Panda Art").withType(StandType.CREATORS).withLocationIds("f60"),
			new Stand().withName("Espirito Art").withType(StandType.CREATORS).withLocationIds("f61"),
			new Stand().withName("Teacup Craft").withType(StandType.CREATORS).withLocationIds("f62"),
			new Stand().withName("השפיריות").withType(StandType.CREATORS).withLocationIds("f63", "f64"),
			new Stand().withName("Almogolan Art").withType(StandType.CREATORS).withLocationIds("f64", "f65"),
			new Stand().withName("join_paranoia").withType(StandType.CREATORS).withLocationIds("f66"),
			new Stand().withName("Lala Fshasha").withType(StandType.CREATORS).withLocationIds("f67"),
			new Stand().withName("A Silly Frog").withType(StandType.CREATORS).withLocationIds("f68"),
			new Stand().withName("Creative a tea").withType(StandType.CREATORS).withLocationIds("f69", "f70"),
			new Stand().withName("Cheesecake shop").withType(StandType.CREATORS).withLocationIds("f7"),
			new Stand().withName("ozart").withType(StandType.CREATORS).withLocationIds("f71", "f72"),
			new Stand().withName("Kira Kira fashion").withType(StandType.CREATORS).withLocationIds("f8"),
			new Stand().withName("SUNSH").withType(StandType.CREATORS).withLocationIds("f9", "f10"),
			new Stand().withName("mayo san design X art").withType(StandType.CLOTHES).withLocationIds("g1"),
			new Stand().withName("אבי מאיר המדבב של סון גוקו").withType(StandType.OTHER).withLocationIds("g11"),
			new Stand().withName("Niku - graphic designer").withType(StandType.GENERAL).withLocationIds("g12"),
			new Stand().withName("סירולניה").withType(StandType.TABLETOP_GAMES).withLocationIds("g13", "g14", "g15", "g16"),
			new Stand().withName("Stormy").withType(StandType.CLOTHES).withLocationIds("g17", "g18", "g19"),
			new Stand().withName("אנימה סטיישן").withType(StandType.MERCH).withLocationIds("g2"),
			new Stand().withName("Pop house").withType(StandType.DOLLS).withLocationIds("g20"),
			new Stand().withName("דוכן שיפודן").withType(StandType.FIGURES).withLocationIds("g21", "g22", "g23"),
			new Stand().withName("שני לימונים").withType(StandType.CLOTHES).withLocationIds("g24"),
			new Stand().withName("אנימה סטור").withType(StandType.MERCH).withLocationIds("g25", "g26", "g27"),
			new Stand().withName("His Majesty's TCG").withType(StandType.OTHER).withLocationIds("g28", "g29"),
			new Stand().withName("קומיקאזה").withType(StandType.MANGA).withLocationIds("g3", "g4"),
			new Stand().withName("אנימנגה - מכללת בלינק").withType(StandType.MANGA).withLocationIds("g30"),
			new Stand().withName("LaserIcon").withType(StandType.HAND_MADE).withLocationIds("g31", "g32"),
			new Stand().withName("מיסקייסיס").withType(StandType.DOLLS).withLocationIds("g33", "g34", "g35", "g36"),
			new Stand().withName("Topdeck").withType(StandType.MERCH).withLocationIds("g37", "g38"),
			new Stand().withName("בר אומנית ציפורניים").withType(StandType.HAND_MADE).withLocationIds("g39"),
			new Stand().withName("גלנה - מוצרי אנימה ומנגה יד שנייה").withType(StandType.MERCH).withLocationIds("g40"),
			new Stand().withName("Candy Lenses").withType(StandType.MERCH).withLocationIds("g41", "g42"),
			new Stand().withName("Dec's IY").withType(StandType.HAND_MADE).withLocationIds("g43"),
			new Stand().withName("Custom Pop Israel").withType(StandType.FIGURES).withLocationIds("g44"),
			new Stand().withName("Anime Life").withType(StandType.FIGURES).withLocationIds("g45"),
			new Stand().withName("נרות דוכיפת").withType(StandType.OTHER).withLocationIds("g46"),
			new Stand().withName("קי\"ק").withType(StandType.BOOKS).withLocationIds("g47"),
			new Stand().withName("Toysland.il").withType(StandType.HAND_MADE).withLocationIds("g48"),
			new Stand().withName("גיימינג לנד gaming land").withType(StandType.VIDEO_GAMES).withLocationIds("g5", "g6"),
			new Stand().withName("Geekish").withType(StandType.HAND_MADE).withLocationIds("g7"),
			new Stand().withName("Anime Wave").withType(StandType.MERCH).withLocationIds("g8", "g9", "g10")
		);
	}

	private StandLocations getTediStandLocations() {
		float defaultWidth = 75.819f;
		float defaultSpaceHorizontal = 8;
		float defaultHeight = 75.819f;
		float defaultSpaceVertical = 8;

		float defaultRotatedWidth = defaultWidth;
		float defaultRotatedSpaceHorizontal = 72.955f; // Between left corners of 2 locations
		float defaultRotatedHeight = defaultHeight;
		float defaultRotatedSpaceVertical = 42.12f; // Between top corners of 2 locations
		int defaultRotationFromTopLeft = 30; // Available in xml editor, under transform
		int defaultRotationFromBottomLeft = -30; // Available in xml editor, under transform

		// Adjust left and top for rotation - add half the difference between full width (or height) and non-rotated width (or height)
		float horizontalRotationFactor = (103.570f - defaultRotatedWidth) / 2;
		float verticalRotationFactor = (103.570f - defaultRotatedHeight) / 2;

		int defaultHighlightColor = R.color.animatsuri2026_dark_pink;

		BlendMode highlightBlendMode = null;
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
			highlightBlendMode = BlendMode.COLOR;
		}

		return new StandLocationsBuilder()
			.setDefaults(defaultWidth, defaultHeight, 0, defaultSpaceHorizontal, defaultSpaceVertical, defaultHighlightColor, highlightBlendMode)
			.single(1593.000f, 824.000f, "d", 1, "d2")
			.leftToRight(1676.820f, 908.000f, "d", 2, 4, "d5")
			.single(1928.270f, 824.000f, "d", 5, null)

			.setDefaults(defaultRotatedWidth, defaultRotatedHeight, defaultRotationFromTopLeft, defaultRotatedSpaceHorizontal, defaultRotatedSpaceVertical, defaultHighlightColor, highlightBlendMode)
			.diagonalFromTopLeft(422.425f + horizontalRotationFactor, 3158.510f + verticalRotationFactor, "e", 12, 1, null)

			.setDefaults(defaultWidth, defaultHeight, 0, defaultSpaceHorizontal, defaultSpaceVertical, defaultHighlightColor, highlightBlendMode)
			.leftToRight(373.985f, 2434.010f, "e", 13, 14, "e15")
			.topToBottom(534.048f, 1684.250f, "e", 23, 15, "e24")
			.leftToRight(373.985f, 1608.430f, "e", 25, 24, null)

			.setDefaults(defaultRotatedWidth, defaultRotatedHeight, defaultRotationFromBottomLeft, defaultRotatedSpaceHorizontal, defaultRotatedSpaceVertical, defaultHighlightColor, highlightBlendMode)
			.diagonalFromBottomLeft(396.100f + horizontalRotationFactor, 827.085f + verticalRotationFactor, "e", 26, 37, null)

			.setDefaults(defaultWidth, defaultHeight, 0, defaultSpaceHorizontal, defaultSpaceVertical, defaultHighlightColor, highlightBlendMode)
			.leftToRight(1512.260f, 2605.180f, "f", 1, 8, "f9")
			.leftToRight(1507.980f, 2445.000f, "f", 16, 9, null)
			.leftToRight(1255.990f, 2092.260f, "f", 17, 30, "f31")
			.leftToRight(1255.990f, 1932.200f, "f", 44, 31, null)
			.leftToRight(1256.000f, 1572.060f, "f", 45, 58, "f59")
			.leftToRight(1256.000f, 1412.000f, "f", 72, 59, null)

			.setDefaults(defaultRotatedWidth, defaultRotatedHeight, defaultRotationFromBottomLeft, defaultRotatedSpaceHorizontal, defaultRotatedSpaceVertical, defaultHighlightColor, highlightBlendMode)
			.diagonalFromBottomLeft(2392.660f + horizontalRotationFactor, 3638.691f + verticalRotationFactor, "g", 1, 10, "g11")

			.setDefaults(defaultWidth, defaultHeight, 0, defaultSpaceHorizontal, defaultSpaceVertical, defaultHighlightColor, highlightBlendMode)
			.topToBottom(3227.710f, 2081.140f, "g", 24, 11, "g25")
			.topToBottom(3227.710f, 868.149f, "g", 38, 25, "g39")

			.setDefaults(defaultRotatedWidth, defaultRotatedHeight, defaultRotationFromTopLeft, defaultRotatedSpaceHorizontal, defaultRotatedSpaceVertical, defaultHighlightColor, highlightBlendMode)
			.diagonalFromTopLeft(2392.661f + horizontalRotationFactor, 367.958f + verticalRotationFactor, "g", 48, 39, null)

			.build();
	}

	private StandLocations getAgamStandLocations() {
		float defaultWidth = 72;
		float defaultSpaceHorizontal = 8;
		float defaultSpaceVertical = 8;
		float defaultHeight = 72;

		int defaultHighlightColor = R.color.animatsuri2026_dark_pink;
		BlendMode highlightBlendMode = null;
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
			highlightBlendMode = BlendMode.COLOR;
		}

		return new StandLocationsBuilder()
			.setSortFormat("%s%02.0f") // width includes the decimal digit
			.setDefaults(defaultWidth, defaultHeight, 0, defaultSpaceHorizontal, defaultSpaceVertical, defaultHighlightColor, highlightBlendMode)
			.topToBottom(412.000f, 753.000f, "a", 4, 1, "a5")
			.leftToRight(524.000f, 454.000f, "a", 5, 10, "a11")
			.leftToRight(1116.000f, 454.000f, "a", 11, 18, "a19")
			.leftToRight(1868.000f, 454.000f, "a", 19, 24, "a25")
			.leftToRight(2460.000f, 454.000f, "a", 25, 30, "a31")
			.leftToRight(3052.000f, 454.000f, "a", 31, 36, "a37")
			.leftToRight(3644.000f, 454.000f, "a", 37, 42, "a43")
			.topToBottom(4157.000f, 761.000f, "a", 43, 46, null)

			.topToBottom(984.000f, 673.000f, "b", 7, 1, "b8")
			.topToBottom(1176.000f, 673.000f, "b", 8, 14, null)
			.topToBottom(1576.000f, 673.000f, "b", 21, 15, "b22")
			.topToBottom(1768.000f, 673.000f, "b", 22, 28, null)
			.topToBottom(2168.000f, 673.000f, "b", 35, 29, "b36")
			.topToBottom(2360.000f, 673.000f, "b", 36, 42, null)
			.topToBottom(2760.000f, 673.000f, "b", 49, 43, "b50")
			.topToBottom(2952.000f, 673.000f, "b", 50, 56, null)
			.topToBottom(3352.000f, 673.000f, "b", 63, 57, "b64")
			.topToBottom(3544.000f, 673.000f, "b", 64, 70, null)

			.build();
	}

	@Override
	public SurveySender getEventVoteSender(final ConventionEvent event) {
		if (event.getUserInput().getVoteSurvey() == null) {
			return null;
		}
        try {
            if (event.getServerId() == EVENT_ID_AMAIDOL && isEventIDRelevantForEventVote(EVENT_ID_AMAIDOL)) {
                SurveyForm form = new SurveyForm()
                        .withQuestionEntry(QUESTION_ID_AMAIDOL_NAME, "entry.109802680")
                        .withQuestionEntry(QUESTION_ID_AMAIDOL_VOTE, "entry.1600353678")
                        .withSendUrl(new URL("https://docs.google.com/forms/d/e/1FAIpQLSe3sJ2LYHFkg2e0bQePIMI1K3nV1GCNyYwhHDLRcGIx-Twl4Q/formResponse"));

                SurveyDataRetriever.DisabledMessage disabledMessageRetriever = new SurveyDataRetriever.GoogleSpreadSheet(AMAIDOL_SPREADSHEET_ID);

                return new EventVoteSurveyFormSender(form, event.getUserInput().getVoteSurvey(), disabledMessageRetriever);

            }
			if (event.getServerId() == EVENT_ID_IDOLFEST && isEventIDRelevantForEventVote(EVENT_ID_IDOLFEST)) {
				SurveyForm form = new SurveyForm()
						.withQuestionEntry(QUESTION_ID_IDOLFEST_VOTE, "entry.1250645599")
						.withSendUrl(new URL("https://docs.google.com/forms/d/e/1FAIpQLSc-X3LyAKiKRXiBwq3q8KEQwsYGwn7pZAqK2g1273sVroPWvw/formResponse"));

				SurveyDataRetriever.DisabledMessage disabledMessageRetriever = new SurveyDataRetriever.GoogleSpreadSheet(IDOLFEST_SPREADSHEET_ID);

				return new EventVoteSurveyFormSender(form, event.getUserInput().getVoteSurvey(), disabledMessageRetriever);
			}
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
		return super.getEventVoteSender(event);
	}

	private boolean isEventIDRelevantForEventVote(int id) {
		return id != EVENT_ID_NO_EVENT;
	}

	@Override
	@Nullable
	public SurveyDataRetriever.Answers createSurveyAnswersRetriever(FeedbackQuestion question) {
        switch (question.getQuestionId()) {
            case QUESTION_ID_AMAIDOL_VOTE: {
                return new SurveyDataRetriever.GoogleSpreadSheet(AMAIDOL_SPREADSHEET_ID);
            }
			case QUESTION_ID_IDOLFEST_VOTE: {
				return new SurveyDataRetriever.GoogleSpreadSheet(IDOLFEST_SPREADSHEET_ID);
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
            if (event.getServerId() == EVENT_ID_AMAIDOL && isEventIDRelevantForEventVote(EVENT_ID_AMAIDOL)) {
                userInput.setVoteSurvey(new Survey().withQuestions(
                        new FeedbackQuestion(QUESTION_ID_AMAIDOL_NAME, FeedbackQuestion.AnswerType.SINGLE_LINE_TEXT, true),
                        new FeedbackQuestion(QUESTION_ID_AMAIDOL_VOTE, FeedbackQuestion.AnswerType.MULTIPLE_ANSWERS_RADIO, true)
                ));
            }
            if (event.getServerId() == EVENT_ID_IDOLFEST && isEventIDRelevantForEventVote(EVENT_ID_IDOLFEST)) {
                userInput.setVoteSurvey(new Survey().withQuestions(
                        new FeedbackQuestion(QUESTION_ID_IDOLFEST_VOTE, FeedbackQuestion.AnswerType.MULTIPLE_ANSWERS_RADIO, true)
                ));
            }
        }
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

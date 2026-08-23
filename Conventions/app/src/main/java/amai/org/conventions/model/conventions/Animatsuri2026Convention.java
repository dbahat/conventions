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
import amai.org.conventions.model.StandType;
import amai.org.conventions.model.StandTypes;
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
	// Stand area names
	public static final String AGAM_STAND_AREA = "אולם אגם";
	public static final String TEDI_STAND_AREA = "אולם טדי";

	// General stand type name
	private static final String GENERAL_STAND_TYPE = "כלליים";

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

	@Override
	protected ConventionStorage initStorage() {
		return new ConventionStorage(this)
			.withInitialEventsFile(R.raw.animatsuri2026_convention_events, 4)
			.withInitialStandsFile(R.raw.animatsuri2026_stands);
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
	protected StandTypes initStandTypes() {
		List<StandType> standTypes = Arrays.asList(
			new StandType().withName("תכשיטים ואופנה").withImage(R.drawable.diamond_24px),
			new StandType().withName("ביגוד ואקססוריז").withImage(R.drawable.shirt),
			new StandType().withName("עבודות יד").withImage(R.drawable.content_cut_24px),
			new StandType().withName("יוצרים").withImage(R.drawable.diamond_24px),
			new StandType().withName("מרצ'נדייז").withImage(R.drawable.ic_shopping_basket),
			new StandType().withName("שונות").withImage(R.drawable.icon_animatsuri),
			new StandType().withName("משחקי קופסה ומשחקי קלפים").withImage(R.drawable.casino_24px),
			new StandType().withName("משחקי תפקידים").withImage(R.drawable.swords_24px),
			new StandType().withName("משחקי וידאו").withImage(R.drawable.videogame_black),
			new StandType().withName("מנגה וקומיקס").withImage(R.drawable.book),
			new StandType().withName("ספרים").withImage(R.drawable.book),
			new StandType().withName("פיגרים").withImage(R.drawable.face_2_24px),
			new StandType().withName("בובות").withImage(R.drawable.face_2_24px),
			new StandType().withName("ציירים").withImage(R.drawable.ic_color_lens),
			new StandType().withName("כלליים").withImage(R.drawable.ic_shopping_basket)
		);
		int i = 1;
		for (StandType standType : standTypes) {
			standType.setOrder(i);
			++i;
		}
		return new StandTypes(standTypes);
	}

	protected String getGeneralStandType() {
		return GENERAL_STAND_TYPE;
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
			.withName(TEDI_STAND_AREA)
			.withStandLocations(getTediStandLocations())
			.withImageResource(R.drawable.animatsuri2026_stands_tedi)
			.withImageWidth(3677.000f)
			.withImageHeight(4208.000f);
		StandsArea agam = new StandsArea()
			.withName(AGAM_STAND_AREA)
			.withStandLocations(getAgamStandLocations())
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

	public StandsArea convertStandsArea(String locationIds) {
		if (locationIds == null || locationIds.trim().isEmpty()) {
			return null;
		}

		// A+B are in Agam, D+E+F+G are in Tedi
		switch (locationIds.charAt(0)) {
			case 'A':
				// Fallthrough
			case 'B':
				return this.findStandsAreaByName(AGAM_STAND_AREA);
			default:
				return this.findStandsAreaByName(TEDI_STAND_AREA);
		}
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

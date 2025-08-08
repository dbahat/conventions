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

public class Animatsuri2025Convention extends AmaiConvention {
	// Hall names
	private static final String MAIN_HALL_NAME = "אולם ראשי";
	private static final String ORANIM_NAME = "אודיטוריום אורנים";
	private static final String ESHKOL1_NAME = "אשכול 1";
	private static final String ESHKOL2_NAME = "אשכול 2";
	private static final String ESHKOL3_NAME = "אשכול 3";
	private static final String GAMES_NAME = "משחקייה";
	private static final String COSPLAY_AREA_NAME = "תיקון קוספליי";
	private static final String COSPLAY_PHOTOGRAPHY_NAME = "עמדת צילום";
	// Location names
	public static final String CHILDREN_ROOM_NAME = "חדר פעוטות";

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
	private static final int EVENT_ID_AMAIDOL = EVENT_ID_NO_EVENT;
	private static final int EVENT_ID_IDOLFEST = 884;

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
		CLOTHES(R.string.clothes_stand, R.drawable.shirt),
		MERCH(R.string.merch_stand, R.drawable.ic_shopping_basket),
		MANGA(R.string.manga_stand, R.drawable.book),
		VIDEO_GAMES(R.string.video_games_stand, R.drawable.videogame_black),
		TABLETOP_GAMES(R.string.tabletop_games_stand, R.drawable.casino_24px),
		ARTIST(R.string.artist_stand, R.drawable.ic_color_lens),
		JEWELRY(R.string.jewelry_stand, R.drawable.diamond_24px),
		GENERAL(R.string.general_stand, R.drawable.ic_shopping_basket),
		OTHER(R.string.other_stand, R.drawable.icon_harucon);

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
		return new ConventionStorage(this, R.raw.animatsuri2025_convention_events, 0);
	}

	@Override
	protected Calendar initDate() {
		Calendar date = Calendar.getInstance();
		date.clear();
		date.set(2025, Calendar.AUGUST, 28);
		return date;
	}

	@Override
	protected String initID() {
		return "Animatsuri2025";
	}

	@Override
	protected String initDisplayName() {
		return "אנימאטסורי 2025";
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
			return new URL("https://animatsuri.org.il/2025/wp-admin/admin-ajax.php?action=get_event_list");
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
			new Hall().withName(GAMES_NAME),
			new Hall().withName(COSPLAY_AREA_NAME),
			new Hall().withName(COSPLAY_PHOTOGRAPHY_NAME)
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
		Hall cosplayPhotography = this.getHalls().findByName(COSPLAY_PHOTOGRAPHY_NAME);

		Floor entrance = new Floor(1)
				.withName("מתחם כניסה")
				.withImageResource(R.raw.animatsuri2025_floor_entrance, true)
				.withImageWidth(1985.652f)
				.withImageHeight(1261.589f);
		Floor floor1 = new Floor(2)
				.withName("קומה 1")
				.withImageResource(R.raw.animatsuri2025_floor1, true)
				.withImageWidth(1774.979f)
				.withImageHeight(1107.5f);
		Floor floor2 = new Floor(3)
				.withName("קומה 2")
				.withImageResource(R.raw.animatsuri2025_floor2, true)
				.withImageWidth(1817f)
				.withImageHeight(1122.078f);

		Place tetsugot = new Place().withName("תצוגות");
//		StandsArea tetsugot = new StandsArea()
//				.withName("תצוגות")
//				.withStandLocations(getTetsugotStandLocations()) // This must be initialized before the stands
//				.withStands(getTetsugotStands())
//				.withImageResource(R.drawable.harucon2025_stands_map_tetsugot)
//				.withImageWidth(4320.03564f)
//				.withImageHeight(2430.00049f);
		Place agam = new Place().withName("אגם");
//		StandsArea agam = new StandsArea()
//				.withName("אולם אגם")
//				.withStandLocations(getAgamStandLocations()) // This must be initialized before the stands
//				.withStands(getAgamStands())
//				.withImageResource(R.drawable.harucon2025_stands_map_pinkus)
//				.withImageWidth(4320)
//				.withImageHeight(2430);

		return new ConventionMap()
				.withFloors(Arrays.asList(entrance, floor1, floor2))
				.withDefaultFloor(floor1)
				.withLocations(
						CollectionUtils.flattenList(
								inFloor(entrance,
										new MapLocation()
												.withPlace(new Place().withName("עמדת מודיעין ודוכן אמא\"י"))
												.withMarkerResource(R.raw.animatsuri2025_marker_entrance_info, true)
												.withSelectedMarkerResource(R.raw.animatsuri2025_selected_marker_entrance_info, true)
												.withMarkerHeight(154.89f)
												.withX(1315.847f)
												.withY(887.54f),
										new MapLocation()
												.withPlace(new Place().withName("עמדת צימוד"))
												.withMarkerResource(R.raw.animatsuri2025_marker_bracelets, true)
												.withSelectedMarkerResource(R.raw.animatsuri2025_selected_marker_bracelets, true)
												.withMarkerHeight(92.439f)
												.withX(947.8445f)
												.withY(912.991f),
										new MapLocation()
												.withPlace(new Place().withName("קופות"))
												.withMarkerResource(R.raw.animatsuri2025_marker_cashier, true)
												.withSelectedMarkerResource(R.raw.animatsuri2025_selected_marker_cashier, true)
												.withMarkerHeight(92.439f)
												.withX(713.8445f)
												.withY(875.991f),
										new MapLocation()
												.withPlace(new Place().withName("קופה נגישה"))
												.withMarkerResource(R.raw.animatsuri2025_marker_accessible_cashier, true)
												.withSelectedMarkerResource(R.raw.animatsuri2025_selected_marker_accessible_cashier, true)
												.withMarkerHeight(92.439f)
												.withX(933.8445f)
												.withY(724.991f),
										new MapLocation()
												.withPlace(new Place().withName("מתחם הזמנה מראש"))
												.withMarkerResource(R.raw.animatsuri2025_marker_preorders, true)
												.withSelectedMarkerResource(R.raw.animatsuri2025_selected_marker_preorders, true)
												.withMarkerHeight(92.439f)
												.withX(970.8445f)
												.withY(417.991f),
										new MapLocation()
												.withPlace(new Place().withName("מתחם קנייה במקום"))
												.withMarkerResource(R.raw.animatsuri2025_marker_tickets_area, true)
												.withSelectedMarkerResource(R.raw.animatsuri2025_selected_marker_tickets_area, true)
												.withMarkerHeight(92.439f)
												.withX(559.3445f)
												.withY(646.991f),
										new MapLocation()
												.withPlace(new Place().withName("קופה נגישה"))
												.withMarkerResource(R.raw.animatsuri2025_marker_accessible_cashier, true)
												.withSelectedMarkerResource(R.raw.animatsuri2025_selected_marker_accessible_cashier, true)
												.withMarkerHeight(92.439f)
												.withX(823.3445f)
												.withY(271.991f),
										new MapLocation()
												.withPlace(new Place().withName("מעבר נגיש לקופות"))
												.withMarkerResource(R.raw.animatsuri2025_marker_accessible_passage, true)
												.withSelectedMarkerResource(R.raw.animatsuri2025_selected_marker_accessible_passage, true)
												.withMarkerHeight(92.439f)
												.withX(369.8445f)
												.withY(416.991f)
								),
								inFloor(floor1,
										new MapLocation()
												.withPlace(new Place().withName("שירותים"))
												.withMarkerResource(R.raw.animatsuri2025_marker_toilet, true)
												.withSelectedMarkerResource(R.raw.animatsuri2025_selected_marker_toilet, true)
												.withMarkerHeight(102.438f)
												.withX(1587.76f)
												.withY(613.889f),
										new MapLocation()
												.withPlace(oranim)
												.withMarkerResource(R.raw.animatsuri2025_marker_oranim, true)
												.withSelectedMarkerResource(R.raw.animatsuri2025_selected_marker_oranim, true)
												.withMarkerHeight(125.037f)
												.withX(1448.234f)
												.withY(729.963f),
										new MapLocation()
												.withPlace(new Place().withName("שמירת חפצים"))
												.withMarkerResource(R.raw.animatsuri2025_marker_storage, true)
												.withSelectedMarkerResource(R.raw.animatsuri2025_selected_marker_storage, true)
												.withMarkerHeight(128.037f)
												.withX(1315.234f)
												.withY(783.963f),
										new MapLocation()
												.withPlace(cosplayPhotography)
												.withMarkerResource(R.raw.animatsuri2025_marker_photography, true)
												.withSelectedMarkerResource(R.raw.animatsuri2025_selected_marker_photography, true)
												.withMarkerHeight(178.085f)
												.withX(1205.449f)
												.withY(829.415f),
										new MapLocation()
												.withPlace(new Place().withName("פינת אוכל"))
												.withMarkerResource(R.raw.animatsuri2025_marker_food_court, true)
												.withSelectedMarkerResource(R.raw.animatsuri2025_selected_marker_food_court, true)
												.withMarkerHeight(98.446f)
												.withX(1170.355f)
												.withY(651.554f),
										new MapLocation()
												.withPlace(new Place().withName("כניסה נגישה לדוכנים מסחריים"))
												.withMarkerResource(R.raw.animatsuri2025_marker_accessible_entrance, true)
												.withSelectedMarkerResource(R.raw.animatsuri2025_selected_marker_accessible_entrance, true)
												.withMarkerHeight(95.439f)
												.withX(1221.785f)
												.withY(438.561f),
										new MapLocation()
												.withName("דוכנים מסחריים")
												.withPlace(tetsugot)
												.withMarkerResource(R.raw.animatsuri2025_marker_commercial_stands, true)
												.withSelectedMarkerResource(R.raw.animatsuri2025_selected_marker_commercial_stands, true)
												.withMarkerHeight(94.947f)
												.withX(1237.335f)
												.withY(296.053f),
										new MapLocation()
												.withPlace(new Place().withName("עמדת מודיעין ודוכן אמא\"י"))
												.withMarkerResource(R.raw.animatsuri2025_marker_info, true)
												.withSelectedMarkerResource(R.raw.animatsuri2025_selected_marker_info, true)
												.withMarkerHeight(168.231f)
												.withX(812.135f)
												.withY(397.677f),
										new MapLocation()
												.withPlace(new Place().withName("שירותים"))
												.withMarkerResource(R.raw.animatsuri2025_marker_toilet, true)
												.withSelectedMarkerResource(R.raw.animatsuri2025_selected_marker_toilet, true)
												.withMarkerHeight(102.437f)
												.withX(935.4765f)
												.withY(157.563f),
										new MapLocation()
												.withPlace(new Place().withName("מעלית"))
												.withMarkerResource(R.raw.animatsuri2025_marker_elevator, true)
												.withSelectedMarkerResource(R.raw.animatsuri2025_selected_marker_elevator, true)
												.withMarkerHeight(85.813f)
												.withX(827.152f)
												.withY(233.282f),
										new MapLocation()
												.withPlace(eshkol3)
												.withMarkerResource(R.raw.animatsuri2025_marker_eshkol3, true)
												.withSelectedMarkerResource(R.raw.animatsuri2025_selected_marker_eshkol3, true)
												.withMarkerHeight(123.67f)
												.withX(375.7405f)
												.withY(778.33f),
										new MapLocation()
												.withPlace(eshkol2)
												.withMarkerResource(R.raw.animatsuri2025_marker_eshkol2, true)
												.withSelectedMarkerResource(R.raw.animatsuri2025_selected_marker_eshkol2, true)
												.withMarkerHeight(123.67f)
												.withX(540.2615f)
												.withY(778.33f),
										new MapLocation()
												.withPlace(eshkol1)
												.withMarkerResource(R.raw.animatsuri2025_marker_eshkol1, true)
												.withSelectedMarkerResource(R.raw.animatsuri2025_selected_marker_eshkol1, true)
												.withMarkerHeight(123.669f)
												.withX(497.9555f)
												.withY(595.008f),
										new MapLocation()
												.withPlace(new Place().withName("שירותים"))
												.withMarkerResource(R.raw.animatsuri2025_marker_toilet, true)
												.withSelectedMarkerResource(R.raw.animatsuri2025_selected_marker_toilet, true)
												.withMarkerHeight(102.436f)
												.withX(309.2015f)
												.withY(505.777f),
										new MapLocation()
												.withPlace(new Place().withName("מעלית"))
												.withMarkerResource(R.raw.animatsuri2025_marker_elevator, true)
												.withSelectedMarkerResource(R.raw.animatsuri2025_selected_marker_elevator, true)
												.withMarkerHeight(85.813f)
												.withX(136.167f)
												.withY(599.927f)
								),
								inFloor(floor2,
										new MapLocation()
												.withPlace(new Place().withName("שיפוט קוספליי"))
												.withMarkerResource(R.raw.animatsuri2025_marker_cosplay_judgement, true)
												.withSelectedMarkerResource(R.raw.animatsuri2025_selected_marker_cosplay_judgement, true)
												.withMarkerHeight(113.011f)
												.withX(1305.49f)
												.withY(874.509f),
										new MapLocation()
												.withPlace(new Place().withName(CHILDREN_ROOM_NAME))
												.withMarkerResource(R.raw.animatsuri2025_marker_children, true)
												.withSelectedMarkerResource(R.raw.animatsuri2025_selected_marker_children, true)
												.withMarkerHeight(98.353f)
												.withX(1427.895f)
												.withY(737.506f),
										new MapLocation()
												.withPlace(new Place().withName("שירותים"))
												.withMarkerResource(R.raw.animatsuri2025_marker_toilet, true)
												.withSelectedMarkerResource(R.raw.animatsuri2025_selected_marker_toilet, true)
												.withMarkerHeight(102.437f)
												.withX(1633.5f)
												.withY(640.823f),
										new MapLocation()
												.withPlace(mainHall)
												.withMarkerResource(R.raw.animatsuri2025_marker_main_hall, true)
												.withSelectedMarkerResource(R.raw.animatsuri2025_selected_marker_main_hall, true)
												.withMarkerHeight(156f)
												.withX(1316.975f)
												.withY(604.26f),
										new MapLocation()
												.withPlace(new Place().withName("כניסה נגישה לאולם ראשי"))
												.withMarkerResource(R.raw.animatsuri2025_marker_main_hall_accessible_entrance, true)
												.withSelectedMarkerResource(R.raw.animatsuri2025_selected_marker_main_hall_accessible_entrance, true)
												.withMarkerHeight(98.353f)
												.withX(1155.105f)
												.withY(692.907f),
										new MapLocation()
												.withPlace(new Place().withName("כניסת פלוס לאולם ראשי"))
												.withMarkerResource(R.raw.animatsuri2025_marker_main_hall_plus_entrance, true)
												.withSelectedMarkerResource(R.raw.animatsuri2025_selected_marker_main_hall_plus_entrance, true)
												.withMarkerHeight(98.282f)
												.withX(1311.545f)
												.withY(469.978f),
										new MapLocation()
												.withPlace(new Place().withName("כניסה ראשית לאולם ראשי"))
												.withMarkerResource(R.raw.animatsuri2025_marker_main_hall_entrance, true)
												.withSelectedMarkerResource(R.raw.animatsuri2025_selected_marker_main_hall_entrance, true)
												.withMarkerHeight(98.353f)
												.withX(1002.1025f)
												.withY(640.907f),
										new MapLocation()
												.withPlace(cosplayArea)
												.withMarkerResource(R.raw.animatsuri2025_marker_cosplay_area, true)
												.withSelectedMarkerResource(R.raw.animatsuri2025_selected_marker_cosplay_area, true)
												.withMarkerHeight(113.011f)
												.withX(1009.4905f)
												.withY(760.249f),
										new MapLocation()
												.withPlace(games)
												.withMarkerResource(R.raw.animatsuri2025_marker_games, true)
												.withSelectedMarkerResource(R.raw.animatsuri2025_selected_marker_games, true)
												.withMarkerHeight(217.828f)
												.withX(754.187f)
												.withY(670.432f),
										new MapLocation()
												.withPlace(new Place().withName("שירותים"))
												.withMarkerResource(R.raw.animatsuri2025_marker_toilet, true)
												.withSelectedMarkerResource(R.raw.animatsuri2025_selected_marker_toilet, true)
												.withMarkerHeight(100.226f)
												.withX(1136.575f)
												.withY(190.034f),
										new MapLocation()
												.withPlace(new Place().withName("מעלית"))
												.withMarkerResource(R.raw.animatsuri2025_marker_elevator, true)
												.withSelectedMarkerResource(R.raw.animatsuri2025_selected_marker_elevator, true)
												.withMarkerHeight(85.564f)
												.withX(1021.613f)
												.withY(204.234f),
										new MapLocation()
												.withName("שדרת ציירים")
												.withPlace(agam)
												.withMarkerResource(R.raw.animatsuri2025_marker_artists_alley, true)
												.withSelectedMarkerResource(R.raw.animatsuri2025_selected_marker_artists_alley, true)
												.withMarkerHeight(98.282f)
												.withX(781.045f)
												.withY(396.978f),
										new MapLocation()
												.withPlace(new FloorLocation().withFloor(floor1).withName("מעבר לאשכולות"))
												.withMarkerResource(R.raw.animatsuri2025_marker_goto_floor1, true)
												.withSelectedMarkerResource(R.raw.animatsuri2025_selected_marker_goto_floor1, true)
												.withMarkerHeight(102.767f)
												.withX(246.59f)
												.withY(559.862f),
										new MapLocation()
												.withPlace(new Place().withName("שירותים"))
												.withMarkerResource(R.raw.animatsuri2025_marker_toilet, true)
												.withSelectedMarkerResource(R.raw.animatsuri2025_selected_marker_toilet, true)
												.withMarkerHeight(85.658f)
												.withX(402.05f)
												.withY(518.119f),
										new MapLocation()
												.withPlace(new Place().withName("מעלית"))
												.withMarkerResource(R.raw.animatsuri2025_marker_elevator, true)
												.withSelectedMarkerResource(R.raw.animatsuri2025_selected_marker_elevator, true)
												.withMarkerHeight(85.564f)
												.withX(318.57f)
												.withY(588.562f)
								)
						)
				);
	}

	private List<Stand> getAgamStands() {
		return Arrays.asList(
			new Stand().withName("NatArt").withType(StandType.ARTIST).withLocationIds("a1", "a2"),
			new Stand().withName("מריליה").withType(StandType.ARTIST).withLocationIds("a10", "a11"),
			new Stand().withName("רפאים").withType(StandType.ARTIST).withLocationIds("a12", "a13"),
			new Stand().withName("B4RMN").withType(StandType.ARTIST).withLocationIds("a14", "a15"),
			new Stand().withName("Meitlavi95").withType(StandType.ARTIST).withLocationIds("a16", "a17"),
			new Stand().withName("adelistic").withType(StandType.ARTIST).withLocationIds("a18"),
			new Stand().withName("Cryptic arts").withType(StandType.ARTIST).withLocationIds("a19", "a20"),
			new Stand().withName("Meiiior").withType(StandType.ARTIST).withLocationIds("a21"),
			new Stand().withName("TVfox").withType(StandType.JEWELRY).withLocationIds("a22", "a23"),
			new Stand().withName("FluffyKittenKa").withType(StandType.ARTIST).withLocationIds("a24"),
			new Stand().withName("Gasi_CL").withType(StandType.ARTIST).withLocationIds("a25"),
			new Stand().withName("מארץ' אנימה ווקולוייד").withType(StandType.ARTIST).withLocationIds("a26"),
			new Stand().withName("Captain Ayay").withType(StandType.ARTIST).withLocationIds("a27"),
			new Stand().withName("איילושיות").withType(StandType.ARTIST).withLocationIds("a28"),
			new Stand().withName("Unlovablycozy").withType(StandType.ARTIST).withLocationIds("a29"),
			new Stand().withName("Rob Artsy").withType(StandType.ARTIST).withLocationIds("a3"),
			new Stand().withName("shay kagamine").withType(StandType.ARTIST).withLocationIds("a30"),
			new Stand().withName("טליטייטור וגולצ'י").withType(StandType.ARTIST).withLocationIds("a31"),
			new Stand().withName("Scylla art").withType(StandType.ARTIST).withLocationIds("a32", "a33"),
			new Stand().withName("כאוספליי").withType(StandType.GENERAL).withLocationIds("a34", "a35"),
			new Stand().withName("Rin_isintheshower").withType(StandType.ARTIST).withLocationIds("a4"),
			new Stand().withName("✦teeth.fish & shuuchu_✦").withType(StandType.ARTIST).withLocationIds("a5"),
			new Stand().withName("Yodarem0n").withType(StandType.ARTIST).withLocationIds("a6"),
			new Stand().withName("LapinVert.e").withType(StandType.ARTIST).withLocationIds("a7"),
			new Stand().withName("פרעושים ופשפשים").withType(StandType.ARTIST).withLocationIds("a8", "a9"),
			new Stand().withName("dinchies").withType(StandType.ARTIST).withLocationIds("b1"),
			new Stand().withName("nod3ret").withType(StandType.ARTIST).withLocationIds("b10"),
			new Stand().withName("ro._.chan").withType(StandType.ARTIST).withLocationIds("b12", "b13"),
			new Stand().withName("Ameriix_").withType(StandType.ARTIST).withLocationIds("b14"),
			new Stand().withName("Shoshi's").withType(StandType.ARTIST).withLocationIds("b15", "b16"),
			new Stand().withName("crimson soda").withType(StandType.ARTIST).withLocationIds("b17"),
			new Stand().withName("eladb_art").withType(StandType.ARTIST).withLocationIds("b18"),
			new Stand().withName("eszart").withType(StandType.ARTIST).withLocationIds("b19"),
			new Stand().withName("Tomatoes Trash").withType(StandType.ARTIST).withLocationIds("b2"),
			new Stand().withName("Nighto").withType(StandType.ARTIST).withLocationIds("b20", "b21"),
			new Stand().withName("Clove Apple Art").withType(StandType.ARTIST).withLocationIds("b22"),
			new Stand().withName("The Chip Club").withType(StandType.ARTIST).withLocationIds("b23", "b24"),
			new Stand().withName("Hikikomoring - Art by Sem Daniel").withType(StandType.ARTIST).withLocationIds("b25", "b26"),
			new Stand().withName("Inimi Draws! Art by Maayan Elbaz").withType(StandType.ARTIST).withLocationIds("b27", "b28"),
			new Stand().withName("Elmiellart").withType(StandType.ARTIST).withLocationIds("b29"),
			new Stand().withName("Martin Draws").withType(StandType.ARTIST).withLocationIds("b3"),
			new Stand().withName("kartzi's").withType(StandType.ARTIST).withLocationIds("b30", "b31"),
			new Stand().withName("Bogouki").withType(StandType.ARTIST).withLocationIds("b32"),
			new Stand().withName("Raimu").withType(StandType.ARTIST).withLocationIds("b4"),
			new Stand().withName("Perotss").withType(StandType.ARTIST).withLocationIds("b5"),
			new Stand().withName("Shourterthan").withType(StandType.ARTIST).withLocationIds("b6", "b7"),
			new Stand().withName("Grisim").withType(StandType.ARTIST).withLocationIds("b8"),
			new Stand().withName("Naamoola").withType(StandType.ARTIST).withLocationIds("b9"),
			new Stand().withName("Amy E. Jones").withType(StandType.ARTIST).withLocationIds("c1"),
			new Stand().withName("kimichu.x").withType(StandType.ARTIST).withLocationIds("c10", "c11"),
			new Stand().withName("שרבטים | Sharbetim").withType(StandType.ARTIST).withLocationIds("c12"),
			new Stand().withName("Clawny Art").withType(StandType.ARTIST).withLocationIds("c13"),
			new Stand().withName("Pepprex art").withType(StandType.ARTIST).withLocationIds("c14"),
			new Stand().withName("דניהלמן ארט").withType(StandType.ARTIST).withLocationIds("c15", "c16"),
			new Stand().withName("vsrstuff").withType(StandType.ARTIST).withLocationIds("c17"),
			new Stand().withName("ozart").withType(StandType.ARTIST).withLocationIds("c18", "c19"),
			new Stand().withName("אוריג'ין סטורי").withType(StandType.ARTIST).withLocationIds("c2", "c3"),
			new Stand().withName("Koruhiko").withType(StandType.ARTIST).withLocationIds("c20", "c21"),
			new Stand().withName("Eli Zeroix").withType(StandType.ARTIST).withLocationIds("c22"),
			new Stand().withName("CHRONIIKA").withType(StandType.ARTIST).withLocationIds("c23", "c24"),
			new Stand().withName("פלזמה").withType(StandType.ARTIST).withLocationIds("c25"),
			new Stand().withName("Jupiilol").withType(StandType.ARTIST).withLocationIds("c26"),
			new Stand().withName("Burucheri").withType(StandType.ARTIST).withLocationIds("c27"),
			new Stand().withName("Sharkioo").withType(StandType.ARTIST).withLocationIds("c28", "c29"),
			new Stand().withName("Cider").withType(StandType.ARTIST).withLocationIds("c30"),
			new Stand().withName("מסע בצבע").withType(StandType.ARTIST).withLocationIds("c31"),
			new Stand().withName("Gabisweb").withType(StandType.ARTIST).withLocationIds("c32"),
			new Stand().withName("Donrex").withType(StandType.ARTIST).withLocationIds("c4"),
			new Stand().withName("Orchi art").withType(StandType.ARTIST).withLocationIds("c5", "c6"),
			new Stand().withName("דוכן של אריאל המדליק").withType(StandType.ARTIST).withLocationIds("c7", "c8"),
			new Stand().withName("Smatan").withType(StandType.ARTIST).withLocationIds("c9")
		);
	}

	private List<Stand> getGamesStands() {
		return Arrays.asList(
			new Stand().withName("נקסוס").withType(StandType.TABLETOP_GAMES),
			new Stand().withName("הממלכה").withType(StandType.TABLETOP_GAMES),
			new Stand().withName("TopDeck").withType(StandType.MERCH)
		);
	}

	private List<Stand> getTetsugotStands() {
		return Arrays.asList(
			new Stand().withName("סטימצקי").withType(StandType.MANGA).withLocationIds("d1", "d2", "d3", "d4", "d5", "d6", "d7", "d8", "d9", "d10", "d11", "d12"),
			new Stand().withName("דוכן שיפודן - מוצרי אנימה ומנגה").withType(StandType.MERCH).withLocationIds("d13", "d14", "d15", "d16", "d17", "d18"),
			new Stand().withName("Gaming land גיימינג לנד").withType(StandType.TABLETOP_GAMES).withLocationIds("d19", "d20", "d21", "d22"),
			new Stand().withName("Tikargos - תיקרגוס").withType(StandType.CLOTHES).withLocationIds("d23", "d24", "d25", "d26"),
			new Stand().withName("מיסקייסיס").withType(StandType.OTHER).withLocationIds("d27", "d28", "d29", "d30", "d31", "d32", "d33", "d34"),
			new Stand().withName("Anime Storm").withType(StandType.MERCH).withLocationIds("d35", "d36", "d37", "d38", "d39", "d40"),
			new Stand().withName("SHIR K").withType(StandType.MERCH).withLocationIds("d41", "d42", "d43"),
			new Stand().withName("סירולניה").withType(StandType.TABLETOP_GAMES).withLocationIds("d44", "d45", "d46", "d47", "d48", "d49"),
			new Stand().withName("Paludu's").withType(StandType.MERCH).withLocationIds("d50", "d51", "d52", "d53"),
			new Stand().withName("Anime Wave").withType(StandType.MERCH).withLocationIds("d54", "d55", "d56", "d57", "d58", "d59"),
			new Stand().withName("Yaelas_Art").withType(StandType.JEWELRY).withLocationIds("d60", "d61"),
			new Stand().withName("Kuzco").withType(StandType.MERCH).withLocationIds("d68", "d69", "d70", "d71"),
			new Stand().withName("Fantasy House").withType(StandType.CLOTHES).withLocationIds("d72", "d73", "d74", "d75"),
			new Stand().withName("A Silly Frog").withType(StandType.JEWELRY).withLocationIds("e1", "e2"),
			new Stand().withName("Dec's IY").withType(StandType.JEWELRY).withLocationIds("e10"),
			new Stand().withName("Ms_crochettt").withType(StandType.JEWELRY).withLocationIds("e11"),
			new Stand().withName("אפריל").withType(StandType.OTHER).withLocationIds("e12"),
			new Stand().withName("יוצרים עם דוד").withType(StandType.CLOTHES).withLocationIds("e13", "e14"),
			new Stand().withName("Hoseki jewelry").withType(StandType.JEWELRY).withLocationIds("e3"),
			new Stand().withName("Ascendant Fiction").withType(StandType.MANGA).withLocationIds("e4"),
			new Stand().withName("ira's amigurumi").withType(StandType.JEWELRY).withLocationIds("e5"),
			new Stand().withName("שני לימונים").withType(StandType.CLOTHES).withLocationIds("e6", "e7"),
			new Stand().withName("iDollsCollection").withType(StandType.MERCH).withLocationIds("e8", "e9"),
			new Stand().withName("CustomPop Israel").withType(StandType.MERCH).withLocationIds("f1"),
			new Stand().withName("Creative a tea").withType(StandType.MERCH).withLocationIds("f10", "f11"),
			new Stand().withName("קימבי").withType(StandType.MERCH).withLocationIds("f12", "f13"),
			new Stand().withName("Soni Anime Socks").withType(StandType.CLOTHES).withLocationIds("f14"),
			new Stand().withName("orazashy").withType(StandType.CLOTHES).withLocationIds("f15", "f16"),
			new Stand().withName("chocolatexxkandixx").withType(StandType.JEWELRY).withLocationIds("f2"),
			new Stand().withName("N FIG").withType(StandType.MERCH).withLocationIds("f3", "f4", "f5", "f6"),
			new Stand().withName("YK Crochet").withType(StandType.JEWELRY).withLocationIds("f7", "f8"),
			new Stand().withName("מלגיקימון").withType(StandType.TABLETOP_GAMES).withLocationIds("f9"),
			new Stand().withName("המרכז ללימודי יפנית").withType(StandType.OTHER).withLocationIds("g1", "g2"),
			new Stand().withName("קי\"ק").withType(StandType.MANGA).withLocationIds("g10", "g11"),
			new Stand().withName("Natoki").withType(StandType.JEWELRY).withLocationIds("g12"),
			new Stand().withName("לייזר אייקון").withType(StandType.MERCH).withLocationIds("g13", "g14"),
			new Stand().withName("Otaku and Fujoshi").withType(StandType.MERCH).withLocationIds("g15", "g16"),
			new Stand().withName("mayo sun design & art").withType(StandType.CLOTHES).withLocationIds("g17", "g18"),
			new Stand().withName("happy hoopu").withType(StandType.MERCH).withLocationIds("g3"),
			new Stand().withName("אין מקום בבית").withType(StandType.MERCH).withLocationIds("g4", "g5"),
			new Stand().withName("Seal mochi kawaii shop").withType(StandType.JEWELRY).withLocationIds("g6", "g7"),
			new Stand().withName("Fusion Frame Studio").withType(StandType.MERCH).withLocationIds("g8", "g9"),
			new Stand().withName("קומיקאזה").withType(StandType.MANGA).withLocationIds("h1", "h2", "h3", "h4"),
			new Stand().withName("Candy Lenses").withType(StandType.JEWELRY).withLocationIds("h10", "h11", "h12", "h13"),
			new Stand().withName("Sweetheartyun").withType(StandType.JEWELRY).withLocationIds("h14", "h15"),
			new Stand().withName("Raspberry").withType(StandType.JEWELRY).withLocationIds("h16", "h17"),
			new Stand().withName("Toysland.il").withType(StandType.JEWELRY).withLocationIds("h18", "h19"),
			new Stand().withName("ANIME_GLASS").withType(StandType.MERCH).withLocationIds("h5"),
			new Stand().withName("stuffer").withType(StandType.JEWELRY).withLocationIds("h6", "h7"),
			new Stand().withName("Mini Tokio").withType(StandType.JEWELRY).withLocationIds("h8", "h9"),
			new Stand().withName("Art_studio_handmade").withType(StandType.JEWELRY).withLocationIds("i1", "i2"),
			new Stand().withName("Aria Manga and Fashion").withType(StandType.OTHER).withLocationIds("i10", "i11"),
			new Stand().withName("Sampai Designs").withType(StandType.JEWELRY).withLocationIds("i12", "i13"),
			new Stand().withName("Velvet Octopus").withType(StandType.JEWELRY).withLocationIds("i14"),
			new Stand().withName("טוקינוטן").withType(StandType.MERCH).withLocationIds("i15", "i16", "i17", "i18"),
			new Stand().withName("Almogolan Art").withType(StandType.JEWELRY).withLocationIds("i3", "i4"),
			new Stand().withName("Orion Geek Jewelry").withType(StandType.JEWELRY).withLocationIds("i5"),
			new Stand().withName("Myst").withType(StandType.MERCH).withLocationIds("i6", "i7", "i8", "i9")
		);
	}

	private StandLocations getTetsugotStandLocations() {
		float defaultWidth = 72;
		float defaultSpaceHorizontal = 12;
		float defaultHeight = 72;
		float defaultSpaceVertical = 12;

		// Top row
		float d75Left = 3373.001f;
		float d75Top = 438.000f;

		// Middle left row
		float d1Left = 103.251f;
		float d1Top = 783.000f;

		// Middle columns from left to right
		float d3Left = 261.001f;
		float d3Top = 873.000f;

		float e1Left = 585.001f;
		float e1Top = 789.000f;
		float e6Left = 585.001f;
		float e6Top = 1293.000f;

		float e8Left = 923.001f;
		float e8Top = 789.000f;
		float e12Left = 923.001f;
		float e12Top = 1209.000f;

		float f1Left = 1257.001f;
		float f1Top = 789.000f;

		float f9Left = 1601.001f;
		float f9Top = 787.000f;

		float g1Left = 1939.001f;
		float g1Top = 710.000f;

		float g10Left = 2273.001f;
		float g10Top = 710.000f;

		float h1Left = 2691.001f;
		float h1Top = 711.000f;
		float h6Left = 2691.001f;
		float h6Top = 1215.000f;

		float h10Left = 3029.001f;
		float h10Top = 711.000f;

		float i1Left = 3205.001f;
		float i1Top = 711.000f;

		float i10Left = 3541.001f;
		float i10Top = 711.000f;

		float d67Left = 4040.001f;
		float d67Top = 537.000f;

		// Bottom row
		float d13Left = 333.001f;
		float d13Top = 1779.000f;

		float d27Left = 1601.001f;
		float d27Top = 1779.000f;

		float d44Left = 3205.001f;
		float d44Top = 1779.000f;

		BlendMode highlightBlendMode = null;
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
			highlightBlendMode = BlendMode.MULTIPLY;
		}

		return new StandLocationsBuilder()
			.setDefaults(defaultWidth, defaultHeight, defaultSpaceHorizontal, defaultSpaceVertical, R.color.animatsuri2025_pink200, highlightBlendMode)
			.leftToRight(d1Left, d1Top, "d", 1, 2, "d3")
			.topToBottom(d3Left, d3Top, "d", 3, 12, "d13")
			.leftToRight(d13Left, d13Top, "d", 13, 26, "d27")
			.leftToRight(d27Left, d27Top, "d", 27, 43, "d44")
			.leftToRight(d44Left, d44Top, "d", 44, 53, "d54")
			.topToBottom(d67Left, d67Top, "d", 67, 54, "d68")
			.leftToRight(d75Left, d75Top, "d", 75, 68, null)
			.topToBottom(e1Left, e1Top, "e", 1, 5, "e6")
			.topToBottom(e6Left, e6Top, "e", 6, 7, null)
			.topToBottom(e8Left, e8Top, "e", 8, 11, "e12")
			.topToBottom(e12Left, e12Top, "e", 12, 14, null)
			.topToBottom(f1Left, f1Top, "f", 1, 8, null)
			.topToBottom(f9Left, f9Top, "f", 9, 16, null)
			.topToBottom(g1Left, g1Top, "g", 1, 9, null)
			.topToBottom(g10Left, g10Top, "g", 10, 18, null)
			.topToBottom(h1Left, h1Top, "h", 1, 5, "h6")
			.topToBottom(h6Left, h6Top, "h", 6, 9, null)
			.topToBottom(h10Left, h10Top, "h", 10, 19, null)
			.topToBottom(i1Left, i1Top, "i", 1, 9, null)
			.topToBottom(i10Left, i10Top, "i", 10, 18, null)
			.build();
	}

	private StandLocations getAgamStandLocations() {
		float defaultWidth = 72;
		float defaultSpaceHorizontal = 12;
		float defaultHeight = 72;

		// Rows from top to bottom

		float a1Left = 1124.000f;
		float a1Top = 577.000f;
		float a7Left = 1710.000f;
		float a7Top = 577.000f;
		float a12Left = 2408.000f;
		float a12Top = 577.000f;

		float b1Left = 1292.000f;
		float b1Top = 829.000f;
		float c1Left = 2408.000f;
		float c1Top = 829.000f;

		float b9Left = 1292.000f;
		float b9Top = 1070.670f;
		float c9Left = 2408.000f;
		float c9Top = 1070.670f;

		float b17Left = 1292.000f;
		float b17Top = 1312.330f;
		float c17Left = 2408.000f;
		float c17Top = 1312.330f;

		float b25Left = 1292.000f;
		float b25Top = 1554.000f;
		float c25Left = 2408.000f;
		float c25Top = 1554.000f;

		float a35Left = 1710.000f;
		float a35Top = 1806.000f;
		float a27Left = 2662.000f;
		float a27Top = 1806.000f;

		BlendMode highlightBlendMode = null;
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
			highlightBlendMode = BlendMode.MULTIPLY;
		}

		return new StandLocationsBuilder()
			.setDefaults(defaultWidth, defaultHeight, defaultSpaceHorizontal, defaultSpaceHorizontal, R.color.animatsuri2025_pink200, highlightBlendMode)
			.leftToRight(a1Left, a1Top, "a", 1, 6, "a7")
			.leftToRight(a7Left, a7Top, "a", 7, 11, "a12")
			.leftToRight(a12Left, a12Top, "a", 12, 18, null)
			.leftToRight(b1Left, b1Top, "b", 1, 8, "c1")
			.leftToRight(c1Left, c1Top, "c", 1, 8, null)
			.leftToRight(b9Left, b9Top, "b", 9, 16, "c9")
			.leftToRight(c9Left, c9Top, "c", 9, 16, null)
			.leftToRight(b17Left, b17Top, "b", 17, 24, "c17")
			.leftToRight(c17Left, c17Top, "c", 17, 24, null)
			.leftToRight(b25Left, b25Top, "b", 25, 32, "c25")
			.leftToRight(c25Left, c25Top, "c", 25, 32, null)
			.leftToRight(a27Left, a27Top, "a", 27, 19, "a28")
			.leftToRight(a35Left, a35Top, "a", 35, 28, null)
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
			return "https://harucon.org.il" + url;
		}
		return super.convertEventDescriptionURL(url);
	}
}

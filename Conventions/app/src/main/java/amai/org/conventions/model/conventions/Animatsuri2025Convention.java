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
		OTHER(R.string.other_stand, R.drawable.icon_animatsuri);

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
		return new ConventionStorage(this, R.raw.animatsuri2025_convention_events, 1);
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

		StandsArea tetsugot = new StandsArea()
				.withName("תצוגות")
				.withStandLocations(getTetsugotStandLocations()) // This must be initialized before the stands
				.withStands(getTetsugotStands())
				.withImageResource(R.drawable.animatsuri2025_stands_map_tetzugot)
				.withImageWidth(1335.000f)
				.withImageHeight(3157.000f);
		StandsArea agam = new StandsArea()
				.withName("אולם אגם")
				.withStandLocations(getAgamStandLocations()) // This must be initialized before the stands
				.withStands(getAgamStands())
				.withImageResource(R.drawable.animatsuri2025_stands_map_agam)
				.withImageWidth(1244.160f)
				.withImageHeight(3152.000f);

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
			new Stand().withName("Candy Lenses").withType(StandType.CLOTHES).withLocationIds("a1", "a2", "a3", "a4"),
			new Stand().withName("Velvet Octopus").withType(StandType.JEWELRY).withLocationIds("a11"),
			new Stand().withName("grilled little lamb").withType(StandType.ARTIST).withLocationIds("a12"),
			new Stand().withName("vsrstuff").withType(StandType.ARTIST).withLocationIds("a13"),
			new Stand().withName("Tokisesa").withType(StandType.ARTIST).withLocationIds("a14", "a15"),
			new Stand().withName("Stawbee's Art").withType(StandType.ARTIST).withLocationIds("a16"),
			new Stand().withName("Scylla art").withType(StandType.ARTIST).withLocationIds("a17", "a18"),
			new Stand().withName("Orit sapir/ o.s").withType(StandType.ARTIST).withLocationIds("a19"),
			new Stand().withName("Inkspire").withType(StandType.ARTIST).withLocationIds("a20"),
			new Stand().withName("Pepprex arts").withType(StandType.ARTIST).withLocationIds("a21"),
			new Stand().withName("Gasi_cl").withType(StandType.ARTIST).withLocationIds("a22"),
			new Stand().withName("ISHA RAKA").withType(StandType.ARTIST).withLocationIds("a23"),
			new Stand().withName("Donrex").withType(StandType.ARTIST).withLocationIds("a24"),
			new Stand().withName("Learoosh").withType(StandType.ARTIST).withLocationIds("a25"),
			new Stand().withName("דניהלמן ארט").withType(StandType.ARTIST).withLocationIds("a26"),
			new Stand().withName("Chiyayu").withType(StandType.ARTIST).withLocationIds("a27"),
			new Stand().withName("Burucheri").withType(StandType.ARTIST).withLocationIds("a28"),
			new Stand().withName("Lapin Verte").withType(StandType.ARTIST).withLocationIds("a29"),
			new Stand().withName("LoLy's Booth").withType(StandType.ARTIST).withLocationIds("a30"),
			new Stand().withName("מרים יעל").withType(StandType.ARTIST).withLocationIds("a31"),
			new Stand().withName("Jupiilol").withType(StandType.ARTIST).withLocationIds("a32"),
			new Stand().withName("Delulu artist").withType(StandType.ARTIST).withLocationIds("a33"),
			new Stand().withName("הדוכן המגניב בטירוף של רועיריידרז").withType(StandType.ARTIST).withLocationIds("a34"),
			new Stand().withName("MoonMor & Foxyohay").withType(StandType.ARTIST).withLocationIds("a35"),
			new Stand().withName("Red Panda Art").withType(StandType.ARTIST).withLocationIds("a36"),
			new Stand().withName("Fish's Planet").withType(StandType.ARTIST).withLocationIds("a38"),
			new Stand().withName("Art Café").withType(StandType.ARTIST).withLocationIds("a39"),
			new Stand().withName("Yodaremon_art").withType(StandType.ARTIST).withLocationIds("a40"),
			new Stand().withName("אוריג'ין סטורי").withType(StandType.ARTIST).withLocationIds("a41", "a42"),
			new Stand().withName("Casper&Osher").withType(StandType.ARTIST).withLocationIds("a43"),
			new Stand().withName("Puffermish").withType(StandType.ARTIST).withLocationIds("a44"),
			new Stand().withName("Foreverangel.art").withType(StandType.ARTIST).withLocationIds("a45"),
			new Stand().withName("Shyly.et").withType(StandType.ARTIST).withLocationIds("a46"),
			new Stand().withName("Yaelas art").withType(StandType.GENERAL).withLocationIds("a5", "a6"),
			new Stand().withName("Toysland.il").withType(StandType.JEWELRY).withLocationIds("a7", "a8"),
			new Stand().withName("Pixel Kid").withType(StandType.JEWELRY).withLocationIds("a9", "a10"),
			new Stand().withName("Clawny art").withType(StandType.ARTIST).withLocationIds("b1"),
			new Stand().withName("TVfox").withType(StandType.ARTIST).withLocationIds("b11", "b12"),
			new Stand().withName("Sharkioo").withType(StandType.ARTIST).withLocationIds("b13", "b14"),
			new Stand().withName("kimichu.x").withType(StandType.ARTIST).withLocationIds("b15", "b16"),
			new Stand().withName("CHRONIIKA").withType(StandType.ARTIST).withLocationIds("b17", "b18"),
			new Stand().withName("Eli Zeroix").withType(StandType.ARTIST).withLocationIds("b19", "b20"),
			new Stand().withName("Fluffykittenka").withType(StandType.ARTIST).withLocationIds("b2"),
			new Stand().withName("Dinchies").withType(StandType.ARTIST).withLocationIds("b21"),
			new Stand().withName("shourterthan").withType(StandType.ARTIST).withLocationIds("b22", "b23"),
			new Stand().withName("B4RMN").withType(StandType.ARTIST).withLocationIds("b24", "b25"),
			new Stand().withName("Mariliya").withType(StandType.ARTIST).withLocationIds("b26", "b27"),
			new Stand().withName("Hikikomoring - Art by Sem Daniel").withType(StandType.ARTIST).withLocationIds("b28", "b29"),
			new Stand().withName("eladb_art").withType(StandType.ARTIST).withLocationIds("b3"),
			new Stand().withName("Inimi Draws! Art by Maayan Elbaz").withType(StandType.ARTIST).withLocationIds("b30", "b31"),
			new Stand().withName("adelistic").withType(StandType.ARTIST).withLocationIds("b32"),
			new Stand().withName("Cryptic arts").withType(StandType.ARTIST).withLocationIds("b33", "b34"),
			new Stand().withName("kartzi's").withType(StandType.ARTIST).withLocationIds("b35", "b36"),
			new Stand().withName("eszart").withType(StandType.ARTIST).withLocationIds("b37", "b38"),
			new Stand().withName("Captain Ayay").withType(StandType.ARTIST).withLocationIds("b39", "b40"),
			new Stand().withName("Meitlavi95").withType(StandType.ARTIST).withLocationIds("b4", "b5"),
			new Stand().withName("Elmiellart").withType(StandType.ARTIST).withLocationIds("b41"),
			new Stand().withName("Shoshi's").withType(StandType.ARTIST).withLocationIds("b42", "b43"),
			new Stand().withName("שי קאגאמינה").withType(StandType.ARTIST).withLocationIds("b44", "b45"),
			new Stand().withName("strawberry flavors").withType(StandType.ARTIST).withLocationIds("b46"),
			new Stand().withName("watery skyes").withType(StandType.ARTIST).withLocationIds("b47"),
			new Stand().withName("Gefi & Maya").withType(StandType.ARTIST).withLocationIds("b48"),
			new Stand().withName("NatArt").withType(StandType.ARTIST).withLocationIds("b49", "b50"),
			new Stand().withName("Rob Artsy").withType(StandType.ARTIST).withLocationIds("b51"),
			new Stand().withName("koruhiko").withType(StandType.ARTIST).withLocationIds("b52", "b53"),
			new Stand().withName("ozart").withType(StandType.ARTIST).withLocationIds("b54", "b55"),
			new Stand().withName("nallybus").withType(StandType.ARTIST).withLocationIds("b56"),
			new Stand().withName("OrannotOren").withType(StandType.ARTIST).withLocationIds("b57"),
			new Stand().withName("רפאים").withType(StandType.ARTIST).withLocationIds("b58", "b59"),
			new Stand().withName("ro._.chan").withType(StandType.ARTIST).withLocationIds("b6", "b7"),
			new Stand().withName("Tomatoes Trash").withType(StandType.ARTIST).withLocationIds("b60"),
			new Stand().withName("Orchi art").withType(StandType.ARTIST).withLocationIds("b61", "b62"),
			new Stand().withName("רותם רקיר").withType(StandType.ARTIST).withLocationIds("b63"),
			new Stand().withName("The Slavic Witches - המכשפות הסלביות").withType(StandType.ARTIST).withLocationIds("b64"),
			new Stand().withName("Nighto").withType(StandType.ARTIST).withLocationIds("b65"),
			new Stand().withName("BogoUki").withType(StandType.ARTIST).withLocationIds("b66"),
			new Stand().withName("Techelet").withType(StandType.ARTIST).withLocationIds("b67"),
			new Stand().withName("Nod3ret").withType(StandType.ARTIST).withLocationIds("b68"),
			new Stand().withName("pointlessfield").withType(StandType.ARTIST).withLocationIds("b69"),
			new Stand().withName("Rin_isintheshower").withType(StandType.ARTIST).withLocationIds("b8"),
			new Stand().withName("דוכן של אריאל המדליק").withType(StandType.ARTIST).withLocationIds("b9", "b10")
		);
	}

	private List<Stand> getTetsugotStands() {
		return Arrays.asList(
			new Stand().withName("סטימצקי").withType(StandType.MANGA).withLocationIds("d1", "d2", "d3", "d4", "d5", "d6", "d7", "d8", "d9", "d10"),
			new Stand().withName("דוכן שיפודן").withType(StandType.MERCH).withLocationIds("d13", "d14", "d15", "d16", "d17", "d18"),
			new Stand().withName("גיימינג לנד").withType(StandType.VIDEO_GAMES).withLocationIds("d19", "d20", "d21", "d22"),
			new Stand().withName("מיסקייסיס").withType(StandType.MERCH).withLocationIds("d24", "d25", "d26", "d27", "d28", "d29"),
			new Stand().withName("אנימה סטור").withType(StandType.MERCH).withLocationIds("d30", "d31", "d32", "d33", "d34", "d35"),
			new Stand().withName("N FIG").withType(StandType.MERCH).withLocationIds("d37", "d38", "d39", "d40"),
			new Stand().withName("קומיקאזה").withType(StandType.MANGA).withLocationIds("d41", "d42", "d43", "d44"),
			new Stand().withName("סירולניה").withType(StandType.TABLETOP_GAMES).withLocationIds("d45", "d46", "d47", "d48", "d49", "d50"),
			new Stand().withName("Kuzco").withType(StandType.MERCH).withLocationIds("d51", "d52", "d53", "d54"),
			new Stand().withName("Anime wave").withType(StandType.MERCH).withLocationIds("d55", "d56", "d57", "d58", "d59", "d60"),
			new Stand().withName("Anime Storm").withType(StandType.MERCH).withLocationIds("d62", "d63", "d64", "d65", "d66", "d67"),
			new Stand().withName("SHIR k").withType(StandType.GENERAL).withLocationIds("d68", "d69", "d70", "d71"),
			new Stand().withName("Satanic Panic Shop").withType(StandType.JEWELRY).withLocationIds("d73", "d74"),
			new Stand().withName("Topdeck").withType(StandType.MERCH).withLocationIds("d75", "d76", "d77", "d78"),
			new Stand().withName("Anime Glass").withType(StandType.JEWELRY).withLocationIds("e1", "e2"),
			new Stand().withName("Dec's IY").withType(StandType.OTHER).withLocationIds("e11", "e12"),
			new Stand().withName("Haruugami").withType(StandType.JEWELRY).withLocationIds("e13", "e14"),
			new Stand().withName("Kawaii land shop").withType(StandType.JEWELRY).withLocationIds("e15"),
			new Stand().withName("סאקורה - SAKURA").withType(StandType.OTHER).withLocationIds("e16", "e17", "e18", "e19"),
			new Stand().withName("Geekish").withType(StandType.OTHER).withLocationIds("e3", "e4"),
			new Stand().withName("ms_crochettt").withType(StandType.JEWELRY).withLocationIds("e5"),
			new Stand().withName("Almogolan Art").withType(StandType.GENERAL).withLocationIds("e7", "e8"),
			new Stand().withName("יוצרים עם דוד").withType(StandType.MERCH).withLocationIds("e9", "e10"),
			new Stand().withName("QUEEN'S CARDS").withType(StandType.TABLETOP_GAMES).withLocationIds("f1", "f2"),
			new Stand().withName("Creative a tea").withType(StandType.MERCH).withLocationIds("f11", "f12"),
			new Stand().withName("נקסוס").withType(StandType.TABLETOP_GAMES).withLocationIds("f13", "f14", "f15"),
			new Stand().withName("Seal mochi kawaii shop").withType(StandType.JEWELRY).withLocationIds("f17", "f18"),
			new Stand().withName("SUNSH").withType(StandType.CLOTHES).withLocationIds("f19", "f20"),
			new Stand().withName("OTAKU AND FUJOSHI").withType(StandType.MERCH).withLocationIds("f3", "f4"),
			new Stand().withName("Chocolatexxkandixx").withType(StandType.JEWELRY).withLocationIds("f5"),
			new Stand().withName("Custom Pop Israel").withType(StandType.JEWELRY).withLocationIds("f6", "f7"),
			new Stand().withName("קימבי").withType(StandType.OTHER).withLocationIds("f8", "f9"),
			new Stand().withName("Pop house/ בית הפופים").withType(StandType.MERCH).withLocationIds("g1", "g2"),
			new Stand().withName("may design X art").withType(StandType.CLOTHES).withLocationIds("g11", "g12"),
			new Stand().withName("Mini Tokio מיני טוקיו").withType(StandType.MERCH).withLocationIds("g13", "g14"),
			new Stand().withName("נטע מקרמה").withType(StandType.JEWELRY).withLocationIds("g16"),
			new Stand().withName("Art_studio_handmade").withType(StandType.JEWELRY).withLocationIds("g17", "g18"),
			new Stand().withName("YK Crochet").withType(StandType.JEWELRY).withLocationIds("g19", "g20"),
			new Stand().withName("Laser-i-Con").withType(StandType.MERCH).withLocationIds("g3", "g4"),
			new Stand().withName("Cherry Staff").withType(StandType.JEWELRY).withLocationIds("g5"),
			new Stand().withName("fantasy house").withType(StandType.MERCH).withLocationIds("g7", "g8", "g9", "g10"),
			new Stand().withName("Frame by Frame").withType(StandType.OTHER).withLocationIds("h1"),
			new Stand().withName("anime station").withType(StandType.MERCH).withLocationIds("h10", "h11"),
			new Stand().withName("הממלכה").withType(StandType.TABLETOP_GAMES).withLocationIds("h12", "h13"),
			new Stand().withName("Fusion frame").withType(StandType.MERCH).withLocationIds("h14", "h15"),
			new Stand().withName("רין ארט אנד פנטאזי - RIN ART & FANTASY").withType(StandType.MERCH).withLocationIds("h17"),
			new Stand().withName("דיגי-דאן טירו-טאן").withType(StandType.JEWELRY).withLocationIds("h2", "h3"),
			new Stand().withName("גלנה - אנימה, מנגה ומוצרי יד שניה").withType(StandType.MERCH).withLocationIds("h4", "h5"),
			new Stand().withName("iDollsCollection").withType(StandType.JEWELRY).withLocationIds("h6", "h7"),
			new Stand().withName("קי\"ק").withType(StandType.MANGA).withLocationIds("h8", "h9"),
			new Stand().withName("ישראקומיקס").withType(StandType.MERCH).withLocationIds("i15", "i16"),
			new Stand().withName("מנגה ישראל").withType(StandType.MANGA).withLocationIds("i17", "i18", "i19", "i20"),
			new Stand().withName("Jill creation").withType(StandType.OTHER).withLocationIds("i2"),
			new Stand().withName("Raspberry").withType(StandType.JEWELRY).withLocationIds("i3", "i4"),
			new Stand().withName("SweetheartYun").withType(StandType.JEWELRY).withLocationIds("i5", "i6"),
			new Stand().withName("MYST").withType(StandType.MERCH).withLocationIds("i7", "i8", "i9", "i10")
		);
	}

	private StandLocations getTetsugotStandLocations() {
		float defaultWidth = 40;
		float defaultSpaceHorizontal = 16;
		float defaultHeight = 40;
		float defaultSpaceVertical = 16;

		// Wrapping line

		float d1Left = 827.000f;
		float d1Top = 159.000f;

		float d12Left = 265.000f;
		float d12Top = 267.000f;

		float d13Left = 168.000f;
		float d13Top = 324.997f;

		float d24Left = 168.000f;
		float d24Top = 949.000f;

		float d37Left = 172.000f;
		float d37Top = 1797.000f;

		float d45Left = 172.000f;
		float d45Top = 2400.000f;

		float d55Left = 209.000f;
		float d55Top = 2960.000f;

		float d62Left = 601.000f;
		float d62Top = 2960.000f;

		float d78Left = 1158.000f;
		float d78Top = 2456.000f;

		// Rows from top to bottom

		float e10Left = 377.000f;
		float e10Top = 493.000f;

		float e5Left = 657.000f;
		float e5Top = 493.000f;

		float e19Left = 377.000f;
		float e19Top = 717.000f;

		float e15Left = 657.000f;
		float e15Top = 717.000f;

		float f9Left = 433.000f;
		float f9Top = 949.000f;

		float f20Left = 377.000f;
		float f20Top = 1173.000f;

		float f15Left = 657.000f;
		float f15Top = 1173.000f;

		float g10Left = 377.000f;
		float g10Top = 1397.000f;

		float g5Left = 657.000f;
		float g5Top = 1397.000f;

		float g20Left = 377.000f;
		float g20Top = 1627.000f;

		float g14Left = 713.000f;
		float g14Top = 1627.000f;

		float h9Left = 377.000f;
		float h9Top = 1965.000f;

		float h5Left = 657.000f;
		float h5Top = 1965.000f;

		float h17Left = 489.000f;
		float h17Top = 2189.000f;

		float i10Left = 377.000f;
		float i10Top = 2400.000f;

		float i20Left = 377.000f;
		float i20Top = 2624.000f;

		float i12Left = 825.000f;
		float i12Top = 2624.000f;


		BlendMode highlightBlendMode = null;
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
			highlightBlendMode = BlendMode.MULTIPLY;
		}

		return new StandLocationsBuilder()
			.setDefaults(defaultWidth, defaultHeight, defaultSpaceHorizontal, defaultSpaceVertical, R.color.animatsuri2025_pink200, highlightBlendMode)
			.topToBottom(d1Left, d1Top, "d", 1, 2, "d3")
			.leftToRight(d12Left, d12Top, "d", 12, 3, "d13")
			.topToBottom(d13Left, d13Top, "d", 13, 22, "d24")
			.topToBottom(d24Left, d24Top, "d", 24, 35, "d37")
			.topToBottom(d37Left, d37Top, "d", 37, 44, "d45")
			.topToBottom(d45Left, d45Top, "d", 45, 54, "d55")
			.leftToRight(d55Left, d55Top, "d", 55, 60, "d62")
			.leftToRight(d62Left, d62Top, "d", 62, 70, "d71")
			.topToBottom(d78Left, d78Top, "d", 78, 71, null)

			.leftToRight(e5Left, e5Top, "e", 5, 1, "e7")
			.leftToRight(e10Left, e10Top, "e", 10, 7, null)
			.leftToRight(e15Left, e15Top, "e", 15, 11, "e16")
			.leftToRight(e19Left, e19Top, "e", 19, 16, null)
			.leftToRight(f9Left, f9Top, "f", 9, 1, null)
			.leftToRight(f15Left, f15Top, "f", 15, 11, "e17")
			.leftToRight(f20Left, f20Top, "f", 20, 17, null)
			.leftToRight(g5Left, g5Top, "g", 5, 1, "g7")
			.leftToRight(g10Left, g10Top, "g", 10, 7, null)
			.leftToRight(g14Left, g14Top, "g", 14, 11, "g16")
			.leftToRight(g20Left, g20Top, "g", 20, 16, null)
			.leftToRight(h5Left, h5Top, "h", 5, 1, "h6")
			.leftToRight(h9Left, h9Top, "h", 9, 6, null)
			.leftToRight(h17Left, h17Top, "h", 17, 10, null)
			.leftToRight(i10Left, i10Top, "i", 10, 2, null)
			.leftToRight(i12Left, i12Top, "i", 12, 11, "i14")
			.leftToRight(i20Left, i20Top, "i", 20, 14, null)
			.build();
	}

	private StandLocations getAgamStandLocations() {
		float defaultWidth = 40;
		float defaultSpaceHorizontal = 16;
		float defaultSpaceVertical = 16;
		float defaultHeight = 40;

		// Rows from top to bottom

		float a1Left = 589.000f;
		float a1Top = 386.500f;

		float b1Left = 505.000f;
		float b1Top = 733.500f;

		float b8Left = 505.000f;
		float b8Top = 917.500f;

		float b15Left = 505.000f;
		float b15Top = 1141.500f;

		float b17Left = 673.000f;
		float b17Top = 1141.500f;

		float b21Left = 505.000f;
		float b21Top = 1326.500f;

		float b28Left = 505.000f;
		float b28Top = 1549.500f;

		float b35Left = 505.000f;
		float b35Top = 1729.500f;

		float b42Left = 505.000f;
		float b42Top = 1953.500f;

		float b49Left = 505.000f;
		float b49Top = 2131.500f;

		float b56Left = 505.000f;
		float b56Top = 2357.500f;

		float b63Left = 505.000f;
		float b63Top = 2593.500f;

		float a46Left = 589.000f;
		float a46Top = 2883.500f;


		// Right column

		float a5Left = 1016.000f;
		float a5Top = 453.500f;

		float a11Left = 1016.000f;
		float a11Top = 861.500f;

		float a19Left = 1016.000f;
		float a19Top = 1381.500f;

		float a25Left = 1016.000f;
		float a25Top = 1785.500f;

		float a31Left = 1016.000f;
		float a31Top = 2189.500f;

		float a37Left = 1016.000f;
		float a37Top = 2593.500f;

		BlendMode highlightBlendMode = null;
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
			highlightBlendMode = BlendMode.MULTIPLY;
		}

		return new StandLocationsBuilder()
			.setDefaults(defaultWidth, defaultHeight, defaultSpaceHorizontal, defaultSpaceVertical, R.color.animatsuri2025_pink200, highlightBlendMode)
			.leftToRight(a1Left, a1Top, "a", 1, 4, null)
			.leftToRight(b1Left, b1Top, "b", 1, 7, null)
			.leftToRight(b8Left, b8Top, "b", 8, 14, null)
			.leftToRight(b15Left, b15Top, "b", 15, 16, "b17")
			.leftToRight(b17Left, b17Top, "b", 17, 20, null)
			.leftToRight(b21Left, b21Top, "b", 21, 27, null)
			.leftToRight(b28Left, b28Top, "b", 28, 34, null)
			.leftToRight(b35Left, b35Top, "b", 35, 41, null)
			.leftToRight(b42Left, b42Top, "b", 42, 48, null)
			.leftToRight(b49Left, b49Top, "b", 49, 55, null)
			.leftToRight(b56Left, b56Top, "b", 56, 62, null)
			.leftToRight(b63Left, b63Top, "b", 63, 69, null)
			.leftToRight(a46Left, a46Top, "a", 46, 43, null)

			.topToBottom(a5Left, a5Top, "a", 5, 10, "a11")
			.topToBottom(a11Left, a11Top, "a",11, 18, "a19")
			.topToBottom(a19Left, a19Top, "a", 19, 24, "a25")
			.topToBottom(a25Left, a25Top, "a", 25, 30, "a31")
			.topToBottom(a31Left, a31Top, "a", 31, 36, "a37")
			.topToBottom(a37Left, a37Top, "a", 37, 42, null)
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

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

public class Harucon2026Convention extends AmaiConvention {
	// Hall names
	private static final String MAIN_HALL_NAME = "אולם ראשי";
	private static final String ORANIM_NAME = "אודיטוריום אורנים";
	private static final String ESHKOL1_NAME = "אשכול 1";
	private static final String ESHKOL2_NAME = "אשכול 2";
	private static final String ESHKOL3_NAME = "אשכול 3";
	private static final String WORKSHOPS_NAME = "חדר סדנאות";
	private static final String ORANIM2_NAME = "אורנים 2";
	private static final String GAMES_NAME = "משחקייה";
	private static final String COSPLAY_AREA_NAME = "מתחם קוספליי";
	// Location names
	public static final String CHILDREN_ROOM_NAME = "חדר פעוטות";
	public static final String ACCESSIBLE_CASHIERS_NAME = "עמדות נגישות";

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
	private static final int EVENT_ID_AMAIDOL = 1439;
	private static final int EVENT_ID_IDOLFEST = EVENT_ID_NO_EVENT;

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
		return new ConventionStorage(this, R.raw.harucon2026_convention_events, 1);
	}

	@Override
	protected Calendar initDate() {
		Calendar date = Calendar.getInstance();
		date.clear();
		date.set(2026, Calendar.MARCH, 3);
		return date;
	}

	@Override
	protected String initID() {
		return "Harucon2026";
	}

	@Override
	protected String initDisplayName() {
		return "הארוקון 2026";
	}

	@Override
	protected URL initUpdatesURL() {
		try {
			return new URL("https://us-central1-starlit-brand-95018.cloudfunctions.net/getFeed?page=harucon.org.il");
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
			return new URL("https://harucon.org.il/wp-admin/admin-ajax.php?action=get_event_list");
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
			new Hall().withName(ESHKOL1_NAME).withShelter(true),
			new Hall().withName(ESHKOL2_NAME).withShelter(true),
			new Hall().withName(ESHKOL3_NAME).withShelter(true),
			new Hall().withName(WORKSHOPS_NAME),
			new Hall().withName(ORANIM_NAME).withShelter(true),
			new Hall().withName(GAMES_NAME),
			new Hall().withName(ORANIM2_NAME),
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
		return new ConventionMap();
//		return createMap();
	}

	private ConventionMap createMap() {
		Hall mainHall = this.getHalls().findByName(MAIN_HALL_NAME);
		Hall oranim = this.getHalls().findByName(ORANIM_NAME);
		Hall eshkol1 = this.getHalls().findByName(ESHKOL1_NAME);
		Hall eshkol2 = this.getHalls().findByName(ESHKOL2_NAME);
		Hall eshkol3 = this.getHalls().findByName(ESHKOL3_NAME);
		Hall workshops = this.getHalls().findByName(WORKSHOPS_NAME);
		Hall oranim2 = this.getHalls().findByName(ORANIM2_NAME);
		Hall games = this.getHalls().findByName(GAMES_NAME);
		Hall cosplayArea = this.getHalls().findByName(COSPLAY_AREA_NAME);

		Floor entrance = new Floor(1)
				.withName("מתחם כניסה")
				.withImageResource(R.raw.harucon2026_floor_entrance, true)
				.withImageWidth(1985.652f)
				.withImageHeight(1261.588f);
		Floor floor1 = new Floor(2)
				.withName("קומה 1")
				.withImageResource(R.raw.harucon2026_floor1, true)
				.withImageWidth(1848.479f)
				.withImageHeight(1301f);
		Floor floor2 = new Floor(3)
				.withName("קומה 2")
				.withImageResource(R.raw.harucon2026_floor2, true)
				.withImageWidth(1817f)
				.withImageHeight(1155.26f);

		StandsArea tedi = new StandsArea()
				.withName("אולם טדי")
				.withStandLocations(getTediStandLocations()) // This must be initialized before the stands
				.withStands(getTetsugotStands())
				.withImageResource(R.drawable.harucon2026_stands_map_tedi)
				.withImageWidth(4948.000f)
				.withImageHeight(5036.000f);
		StandsArea agam = new StandsArea()
				.withName("אולם אגם")
				.withStandLocations(getAgamStandLocations()) // This must be initialized before the stands
				.withStands(getAgamStands())
				.withImageResource(R.drawable.harucon2026_stands_map_agam)
				.withImageWidth(4425.000f)
				.withImageHeight(1728.000f);

		return new ConventionMap()
				.withFloors(Arrays.asList(entrance, floor1, floor2))
				.withDefaultFloor(floor1)
				.withLocations(
						CollectionUtils.flattenList(
								inFloor(entrance,
										new MapLocation()
												.withPlace(new Place().withName("עמדת מודיעין ודוכן אמא\"י"))
												.withMarkerResource(R.raw.harucon2026_marker_info_entrance, true)
												.withSelectedMarkerResource(R.raw.harucon2026_selected_marker_info_entrance, true)
												.withMarkerHeight(202.165f)
												.withX(1310.462f)
												.withY(892.265f),
										new MapLocation()
												.withPlace(new Place().withName("עמדת צימוד"))
												.withMarkerResource(R.raw.harucon2026_marker_bracelets, true)
												.withSelectedMarkerResource(R.raw.harucon2026_selected_marker_bracelets, true)
												.withMarkerHeight(92.439f)
												.withX(947.8445f)
												.withY(912.991f),
										new MapLocation()
												.withPlace(new Place().withName("קופות"))
												.withMarkerResource(R.raw.harucon2026_marker_cashier, true)
												.withSelectedMarkerResource(R.raw.harucon2026_selected_marker_cashier, true)
												.withMarkerHeight(92.439f)
												.withX(713.8445f)
												.withY(875.991f),
										new MapLocation()
												.withPlace(new Place().withName(ACCESSIBLE_CASHIERS_NAME))
												.withMarkerResource(R.raw.harucon2026_marker_accessible_cashier, true)
												.withSelectedMarkerResource(R.raw.harucon2026_selected_marker_accessible_cashier, true)
												.withMarkerHeight(92.439f)
												.withX(933.8445f)
												.withY(724.991f),
										new MapLocation()
												.withPlace(new Place().withName("מתחם הזמנה מראש"))
												.withMarkerResource(R.raw.harucon2026_marker_preorders, true)
												.withSelectedMarkerResource(R.raw.harucon2026_selected_marker_preorders, true)
												.withMarkerHeight(92.439f)
												.withX(972.3445f)
												.withY(433.991f),
										new MapLocation()
												.withPlace(new Place().withName("מתחם קנייה במקום"))
												.withMarkerResource(R.raw.harucon2026_marker_purchase, true)
												.withSelectedMarkerResource(R.raw.harucon2026_selected_marker_purchase, true)
												.withMarkerHeight(92.439f)
												.withX(497.3445f)
												.withY(695.991f),
										new MapLocation()
												.withPlace(new Place().withName("מעבר נגיש"))
												.withMarkerResource(R.raw.harucon2026_marker_accessible_passage, true)
												.withSelectedMarkerResource(R.raw.harucon2026_selected_marker_accessible_passage, true)
												.withMarkerHeight(92.439f)
												.withX(339.8445f)
												.withY(427.991f)
								),
								inFloor(floor1,
										new MapLocation()
												.withPlace(new Place().withName("שירותים"))
												.withMarkerResource(R.raw.harucon2026_marker_toilet, true)
												.withSelectedMarkerResource(R.raw.harucon2026_selected_marker_toilet, true)
												.withMarkerHeight(101.949f)
												.withX(1562.6f)
												.withY(693.051f),
										new MapLocation()
												.withPlace(oranim)
												.withMarkerResource(R.raw.harucon2026_marker_oranim, true)
												.withSelectedMarkerResource(R.raw.harucon2026_selected_marker_oranim, true)
												.withMarkerHeight(161f)
												.withX(1415.979f)
												.withY(775f),
										new MapLocation()
												.withPlace(oranim2)
												.withName("אורנים 2 - סדנת קנדו")
												.withMarkerResource(R.raw.harucon2026_marker_oranim2, true)
												.withSelectedMarkerResource(R.raw.harucon2026_selected_marker_oranim2, true)
												.withMarkerHeight(152.67f)
												.withX(1266.404f)
												.withY(859.33f),
										new MapLocation()
												.withName("מתחם דוכנים")
												.withPlace(tedi)
												.withMarkerResource(R.raw.harucon2026_marker_stands, true)
												.withSelectedMarkerResource(R.raw.harucon2026_selected_marker_stands, true)
												.withMarkerHeight(94.947f)
												.withX(819.331f)
												.withY(1024.053f),
										new MapLocation()
												.withPlace(new Place().withName("שירותים"))
												.withMarkerResource(R.raw.harucon2026_marker_toilet, true)
												.withSelectedMarkerResource(R.raw.harucon2026_selected_marker_toilet, true)
												.withMarkerHeight(102.436f)
												.withX(546.5975f)
												.withY(924.564f),
										new MapLocation()
												.withPlace(new Place().withName("מתחם אוכל"))
												.withMarkerResource(R.raw.harucon2026_marker_food_court, true)
												.withSelectedMarkerResource(R.raw.harucon2026_selected_marker_food_court, true)
												.withMarkerHeight(98.446f)
												.withX(1141.734f)
												.withY(662.554f),
										new MapLocation()
												.withPlace(games)
												.withName("מתחם המשחקייה")
												.withMarkerResource(R.raw.harucon2026_marker_games, true)
												.withSelectedMarkerResource(R.raw.harucon2026_selected_marker_games, true)
												.withMarkerHeight(129.828f)
												.withX(1298.979f)
												.withY(364.086f),
										new MapLocation()
												.withPlace(new Place().withName("מתחם נינטנדו"))
												.withMarkerResource(R.raw.harucon2026_marker_nintendo, true)
												.withSelectedMarkerResource(R.raw.harucon2026_selected_marker_nintendo, true)
												.withMarkerHeight(114.037f)
												.withX(1294.234f)
												.withY(247.963f),
										new MapLocation()
												.withPlace(new Place().withName("שמירת חפצים"))
												.withMarkerResource(R.raw.harucon2026_marker_storage, true)
												.withSelectedMarkerResource(R.raw.harucon2026_selected_marker_storage, true)
												.withMarkerHeight(128.037f)
												.withX(1122.234f)
												.withY(237.963f),
										new MapLocation()
												.withPlace(new Place().withName("מודיעין ודוכן אמא\"י"))
												.withMarkerResource(R.raw.harucon2026_marker_info, true)
												.withSelectedMarkerResource(R.raw.harucon2026_selected_marker_info, true)
												.withMarkerHeight(185.165f)
												.withX(818.895f)
												.withY(416.835f),
										new MapLocation()
												.withPlace(new Place().withName("שירותים"))
												.withMarkerResource(R.raw.harucon2026_marker_toilet, true)
												.withSelectedMarkerResource(R.raw.harucon2026_selected_marker_toilet, true)
												.withMarkerHeight(102.44f)
												.withX(933.5975f)
												.withY(157.56f),
										new MapLocation()
												.withPlace(new Place().withName("מעלית"))
												.withMarkerResource(R.raw.harucon2026_marker_elevator, true)
												.withSelectedMarkerResource(R.raw.harucon2026_selected_marker_elevator, true)
												.withMarkerHeight(85.813f)
												.withX(827.152f)
												.withY(233.282f),
										new MapLocation()
												.withPlace(eshkol3)
												.withMarkerResource(R.raw.harucon2026_marker_eshkol3, true)
												.withSelectedMarkerResource(R.raw.harucon2026_selected_marker_eshkol3, true)
												.withMarkerHeight(123.67f)
												.withX(376.4045f)
												.withY(778.33f),
										new MapLocation()
												.withPlace(eshkol2)
												.withMarkerResource(R.raw.harucon2026_marker_eshkol2, true)
												.withSelectedMarkerResource(R.raw.harucon2026_selected_marker_eshkol2, true)
												.withMarkerHeight(123.67f)
												.withX(540.4055f)
												.withY(778.33f),
										new MapLocation()
												.withPlace(eshkol1)
												.withMarkerResource(R.raw.harucon2026_marker_eshkol1, true)
												.withSelectedMarkerResource(R.raw.harucon2026_selected_marker_eshkol1, true)
												.withMarkerHeight(123.669f)
												.withX(500.4055f)
												.withY(595.008f),
										new MapLocation()
												.withPlace(new FloorLocation().withFloor(floor2).withName("מעבר לקומה 2"))
												.withMarkerResource(R.raw.harucon2026_marker_goto_floor2, true)
												.withSelectedMarkerResource(R.raw.harucon2026_selected_marker_goto_floor2, true)
												.withMarkerHeight(71.401f)
												.withX(219.1935f)
												.withY(734.237f),
										new MapLocation()
												.withPlace(new Place().withName("שירותים"))
												.withMarkerResource(R.raw.harucon2026_marker_toilet, true)
												.withSelectedMarkerResource(R.raw.harucon2026_selected_marker_toilet, true)
												.withMarkerHeight(102.436f)
												.withX(309.2025f)
												.withY(505.777f),
										new MapLocation()
												.withPlace(new Place().withName("מעלית"))
												.withMarkerResource(R.raw.harucon2026_marker_elevator, true)
												.withSelectedMarkerResource(R.raw.harucon2026_selected_marker_elevator, true)
												.withMarkerHeight(85.813f)
												.withX(136.166f)
												.withY(599.927f)
								),
								inFloor(floor2,
										new MapLocation()
												.withPlace(new Place().withName("שיפוט קוספליי"))
												.withMarkerResource(R.raw.harucon2026_marker_cosplay_judgement, true)
												.withSelectedMarkerResource(R.raw.harucon2026_selected_marker_cosplay_judgement, true)
												.withMarkerHeight(113.011f)
												.withX(1305.49f)
												.withY(874.509f),
										new MapLocation()
												.withPlace(workshops)
												.withMarkerResource(R.raw.harucon2026_marker_workshops, true)
												.withSelectedMarkerResource(R.raw.harucon2026_selected_marker_workshops, true)
												.withMarkerHeight(111.82f)
												.withX(1454.525f)
												.withY(943.44f),
										new MapLocation()
												.withPlace(new Place().withName(CHILDREN_ROOM_NAME))
												.withMarkerResource(R.raw.harucon2026_marker_parents_room, true)
												.withSelectedMarkerResource(R.raw.harucon2026_selected_marker_parents_room, true)
												.withMarkerHeight(98.353f)
												.withX(1427.895f)
												.withY(737.506f),
										new MapLocation()
												.withPlace(new Place().withName("שירותים"))
												.withMarkerResource(R.raw.harucon2026_marker_toilet, true)
												.withSelectedMarkerResource(R.raw.harucon2026_selected_marker_toilet, true)
												.withMarkerHeight(102.437f)
												.withX(1633.5f)
												.withY(640.823f),
										new MapLocation()
												.withPlace(mainHall)
												.withMarkerResource(R.raw.harucon2026_marker_main_hall, true)
												.withSelectedMarkerResource(R.raw.harucon2026_selected_marker_main_hall, true)
												.withMarkerHeight(156f)
												.withX(1183.975f)
												.withY(592.26f),
										new MapLocation()
												.withPlace(new Place().withName("כניסה נגישה לאולם ראשי"))
												.withMarkerResource(R.raw.harucon2026_marker_main_hall_accessible_entrance, true)
												.withSelectedMarkerResource(R.raw.harucon2026_selected_marker_main_hall_accessible_entrance, true)
												.withMarkerHeight(98.353f)
												.withX(1312.105f)
												.withY(480.907f),
										new MapLocation()
												.withPlace(cosplayArea)
												.withMarkerResource(R.raw.harucon2026_marker_cosplay_area, true)
												.withSelectedMarkerResource(R.raw.harucon2026_selected_marker_cosplay_area, true)
												.withMarkerHeight(205.011f)
												.withX(874.987f)
												.withY(710.249f),
										new MapLocation()
												.withName("שדרת ציירים")
												.withPlace(agam)
												.withMarkerResource(R.raw.harucon2026_marker_artist_alley, true)
												.withSelectedMarkerResource(R.raw.harucon2026_selected_marker_artist_alley, true)
												.withMarkerHeight(98.282f)
												.withX(781.045f)
												.withY(396.978f),
										new MapLocation()
												.withPlace(new Place().withName("שירותים"))
												.withMarkerResource(R.raw.harucon2026_marker_toilet, true)
												.withSelectedMarkerResource(R.raw.harucon2026_selected_marker_toilet, true)
												.withMarkerHeight(100.226f)
												.withX(1136.575f)
												.withY(190.034f),
										new MapLocation()
												.withPlace(new Place().withName("מעלית"))
												.withMarkerResource(R.raw.harucon2026_marker_elevator, true)
												.withSelectedMarkerResource(R.raw.harucon2026_selected_marker_elevator, true)
												.withMarkerHeight(85.564f)
												.withX(1021.613f)
												.withY(204.234f),
										new MapLocation()
												.withPlace(new FloorLocation().withFloor(floor1).withName("מעבר לאשכולות"))
												.withMarkerResource(R.raw.harucon2026_marker_goto_floor1, true)
												.withSelectedMarkerResource(R.raw.harucon2026_selected_marker_goto_floor1, true)
												.withMarkerHeight(102.767f)
												.withX(246.59f)
												.withY(559.862f),
										new MapLocation()
												.withPlace(new Place().withName("שירותים"))
												.withMarkerResource(R.raw.harucon2026_marker_toilet, true)
												.withSelectedMarkerResource(R.raw.harucon2026_selected_marker_toilet, true)
												.withMarkerHeight(85.658f)
												.withX(402.05f)
												.withY(518.119f),
										new MapLocation()
												.withPlace(new Place().withName("מעלית"))
												.withMarkerResource(R.raw.harucon2026_marker_elevator, true)
												.withSelectedMarkerResource(R.raw.harucon2026_selected_marker_elevator, true)
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

	private StandLocations getTediStandLocations() {
		float defaultWidth = 72;
		float defaultSpaceHorizontal = 8;
		float defaultHeight = 72;
		float defaultSpaceVertical = 8;

		float defaultRotatedWidth = 72;
		float defaultRotatedSpaceHorizontal = 69.28f;
		float defaultRotatedHeight = 72;
		float defaultRotatedSpaceVertical = 40;
		int defaultRotationFromTopLeft = 30;
		int defaultRotationFromBottomLeft = -30;

		// Adjust left and top for rotation - add half the difference between full width (or height) and non-rotated width (or height)
		float horizontalRotationFactor = (98.354f - defaultRotatedWidth) / 2;
		float verticalRotationFactor = (98.354f - defaultRotatedHeight) / 2;

		int defaultHighlightColor = R.color.harucon2026_pink1;

		BlendMode highlightBlendMode = null;
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
			highlightBlendMode = BlendMode.COLOR;
		}

		return new StandLocationsBuilder()
			.setDefaults(defaultRotatedWidth, defaultRotatedHeight, defaultRotationFromTopLeft, defaultRotatedSpaceHorizontal, defaultRotatedSpaceVertical, defaultHighlightColor, highlightBlendMode)
			.diagonalFromTopLeft(2085.960f + horizontalRotationFactor, 4087.200f + verticalRotationFactor, "d", 8, 1, "d9")
			.diagonalFromTopLeft(1469.000f + horizontalRotationFactor, 3731.000f + verticalRotationFactor, "d", 16, 9, "d17")

			.setDefaults(defaultWidth, defaultHeight, 0, defaultSpaceHorizontal, defaultSpaceVertical, defaultHighlightColor, highlightBlendMode)
			.leftToRight(559.000f, 3797.000f, "d", 24, 17, "d25")
			.topToBottom(385.000f, 2875.000f, "d", 34, 25, "d35")
			.topToBottom(385.000f, 2141.000f, "d", 40, 35, "d41")
			.leftToRight(534.000f, 1845.000f, "d", 41, 48, "d49")
			.topToBottom(1191.000f, 1650.000f, "d", 50, 49, "d51")

			.setDefaults(defaultRotatedWidth, defaultRotatedHeight, defaultRotationFromBottomLeft, defaultRotatedSpaceHorizontal, defaultRotatedSpaceVertical, defaultHighlightColor, highlightBlendMode)
			.diagonalFromBottomLeft(1388.000f + horizontalRotationFactor, 984.000f + verticalRotationFactor, "d", 51, 56, "d57")
			.diagonalFromBottomLeft(1866.050f + horizontalRotationFactor, 708.000f + verticalRotationFactor, "d", 57, 62, "d63")

			.setDefaults(defaultWidth, defaultHeight, 0, defaultSpaceHorizontal, defaultSpaceVertical, defaultHighlightColor, highlightBlendMode)
			.leftToRight(2421.000f, 406.000f, "d", 63, 68, "d69")
			.leftToRight(3015.000f, 406.000f, "d", 69, 74, null)

			.setDefaults(defaultRotatedWidth, defaultRotatedHeight, defaultRotationFromTopLeft, defaultRotatedSpaceHorizontal, defaultRotatedSpaceVertical, defaultHighlightColor, highlightBlendMode)
			.diagonalFromTopLeft(1788.000f + horizontalRotationFactor, 3602.000f + verticalRotationFactor, "e", 12, 1, "e13")

			.setDefaults(defaultWidth, defaultHeight, 0, defaultSpaceHorizontal, defaultSpaceVertical, defaultHighlightColor, highlightBlendMode)
			.leftToRight(1742.000f, 2955.000f, "e", 13, 14, "e15")
			.topToBottom(1894.000f, 2163.000f, "e", 24, 15, "e25")
			.leftToRight(1742.000f, 2091.000f, "e", 26, 25, "e27")

			.setDefaults(defaultRotatedWidth, defaultRotatedHeight, defaultRotationFromBottomLeft, defaultRotatedSpaceHorizontal, defaultRotatedSpaceVertical, defaultHighlightColor, highlightBlendMode)
			.diagonalFromBottomLeft(1763.000f + horizontalRotationFactor, 1388.000f + verticalRotationFactor, "e", 27, 38, null)

			.setDefaults(defaultWidth, defaultHeight, 0, defaultSpaceHorizontal, defaultSpaceVertical, defaultHighlightColor, highlightBlendMode)
			.leftToRight(2776.000f, 3587.000f, "f", 1, 10, null)
			.leftToRight(2776.000f, 3435.000f, "f", 11, 20, null)
			.leftToRight(2400.000f, 3093.000f, "f", 21, 40, null)
			.leftToRight(2400.000f, 2941.000f, "f", 41, 60, null)
			.leftToRight(2400.000f, 2599.000f, "f", 61, 80, null)
			.leftToRight(2400.000f, 2447.000f, "f", 81, 100, null)
			.leftToRight(2400.000f, 2105.000f, "f", 101, 120, null)
			.leftToRight(2400.000f, 1953.000f, "f", 121, 140, null)
			.leftToRight(2776.000f, 1611.000f, "f", 141, 150, null)
			.leftToRight(2776.000f, 1459.000f, "f", 151, 160, null)

			.setDefaults(defaultRotatedWidth, defaultRotatedHeight, defaultRotationFromBottomLeft, defaultRotatedSpaceHorizontal, defaultRotatedSpaceVertical, defaultHighlightColor, highlightBlendMode)
			.diagonalFromBottomLeft(3659.000f + horizontalRotationFactor, 4058.000f + verticalRotationFactor, "g", 1, 10, "g11")

			.setDefaults(defaultWidth, defaultHeight, 0, defaultSpaceHorizontal, defaultSpaceVertical, defaultHighlightColor, highlightBlendMode)
			.topToBottom(4452.000f, 2578.900f, "g", 24, 17, "g25")
			.topToBottom(4452.000f, 1427.000f, "g", 38, 25, "g39")

			.setDefaults(defaultRotatedWidth, defaultRotatedHeight, defaultRotationFromTopLeft, defaultRotatedSpaceHorizontal, defaultRotatedSpaceVertical, defaultHighlightColor, highlightBlendMode)
			.diagonalFromTopLeft(3659.000f + horizontalRotationFactor, 952.000f + verticalRotationFactor, "g", 48, 39, null)

			.build();
	}

	private StandLocations getAgamStandLocations() {
		float defaultWidth = 72;
		float defaultSpaceHorizontal = 8;
		float defaultSpaceVertical = 8;
		float defaultHeight = 72;

		int defaultHighlightColor = R.color.harucon2026_pink1;
		BlendMode highlightBlendMode = null;
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
			highlightBlendMode = BlendMode.COLOR;
		}

		return new StandLocationsBuilder()
			.setSortFormat("%s%04.1f") // width includes the decimal digit
			.setDefaults(defaultWidth, defaultHeight, 0, defaultSpaceHorizontal, defaultSpaceVertical, defaultHighlightColor, highlightBlendMode)
			.topToBottom(304.000f, 672.000f, "a", 4, 1, "a5")
			.leftToRight(416.000f, 373.000f, "a", 5, 10, "a11")
			.leftToRight(1008.000f, 373.000f, "a", 11, 18, "a19")
			.leftToRight(1760.000f, 373.000f, "a", 19, 22, "a23")
			.leftToRight(2192.000f, 373.000f, "a", 23, 30, "a31")
			.leftToRight(2944.000f, 373.000f, "a", 31, 36, "a37")
			.leftToRight(3536.000f, 373.000f, "a", 37, 42, "a43")
			.topToBottom(4049.000f, 680.000f, "a", 43, 46, null)

			.topToBottom(876.000f, 592.000f, "b", 1, 7, "b7.5")
			.single(876.000f, 1152.000f, "b", 7.5f, null)
			.topToBottom(1068.000f, 592.000f, "b", 8, 14, null)
			.topToBottom(1468.000f, 592.000f, "b", 15, 18, "b19")
			.topToBottom(1468.000f, 992.000f, "b", 19, 20, null)
			.topToBottom(1660.000f, 592.000f, "b", 21, 27, null)
			.topToBottom(2060.000f, 592.000f, "b", 28, 34, null)
			.topToBottom(2252.000f, 592.000f, "b", 35, 41, null)
			.topToBottom(2652.000f, 592.000f, "b", 42, 48, null)
			.topToBottom(2844.000f, 592.000f, "b", 49, 55, null)
			.topToBottom(3244.000f, 592.000f, "b", 56, 62, null)
			.topToBottom(3436.000f, 592.000f, "b", 63, 69, null)

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

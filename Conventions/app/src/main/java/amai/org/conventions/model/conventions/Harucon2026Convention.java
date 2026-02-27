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
		JEWELRY(R.string.jewelry_stand, R.drawable.diamond_24px),
		CLOTHES(R.string.clothes_stand, R.drawable.shirt),
		HAND_MADE(R.string.hand_made_stand, R.drawable.content_cut_24px),
		MERCH(R.string.merch_stand, R.drawable.ic_shopping_basket),
		OTHER(R.string.other_stand, R.drawable.icon_harucon),
		TABLETOP_GAMES(R.string.tabletop_games_stand, R.drawable.casino_24px),
		ROLE_PLAY_GAMES(R.string.role_play_games_stand, R.drawable.swords_24px),
		VIDEO_GAMES(R.string.video_games_stand, R.drawable.videogame_black),
		MANGA(R.string.manga_stand, R.drawable.book),
		FIGURES(R.string.figures_stand, R.drawable.face_2_24px),
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
		return new ConventionStorage(this, R.raw.harucon2026_convention_events, 2);
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
			new Hall().withName(GAMES_NAME).withShelter(true),
			new Hall().withName(ORANIM2_NAME).withShelter(true),
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
				.withStands(getTediStands())
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
												.withDescription("החלק הפנימי של מתחם זה הינו מרחב מוגן.")
												.withPlace(tedi.withShelter(true))
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
												.withPlace(new Place().withName("מתחם נינטנדו").withShelter(true))
												.withMarkerResource(R.raw.harucon2026_marker_nintendo, true)
												.withSelectedMarkerResource(R.raw.harucon2026_selected_marker_nintendo, true)
												.withMarkerHeight(114.037f)
												.withX(1294.234f)
												.withY(247.963f),
										new MapLocation()
												.withPlace(new Place().withName("שמירת חפצים").withShelter(true))
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
			new Stand().withName("שושיז").withType(StandType.ARTIST).withLocationIds("a1", "a2"),
			new Stand().withName("פלזמה").withType(StandType.ARTIST).withLocationIds("a11"),
			new Stand().withName("הדוכן של גיל ודני").withType(StandType.ARTIST).withLocationIds("a12"),
			new Stand().withName("DONREX").withType(StandType.ARTIST).withLocationIds("a13"),
			new Stand().withName("הדוכן המגניב של גופבול").withType(StandType.ARTIST).withLocationIds("a14"),
			new Stand().withName("דניהלמן ארט").withType(StandType.ARTIST).withLocationIds("a15"),
			new Stand().withName("Tokisesa").withType(StandType.ARTIST).withLocationIds("a16"),
			new Stand().withName("Orchi Art").withType(StandType.ARTIST).withLocationIds("a17", "a18"),
			new Stand().withName("Shandrwa").withType(StandType.ARTIST).withLocationIds("a19"),
			new Stand().withName("Pepprex art").withType(StandType.ARTIST).withLocationIds("a20"),
			new Stand().withName("Raybae").withType(StandType.ARTIST).withLocationIds("a21"),
			new Stand().withName("שי קאגאמינה").withType(StandType.ARTIST).withLocationIds("a22", "a23"),
			new Stand().withName("shustar").withType(StandType.ARTIST).withLocationIds("a24"),
			new Stand().withName("Sharkioo").withType(StandType.ARTIST).withLocationIds("a25", "a26"),
			new Stand().withName("Shyly.et").withType(StandType.ARTIST).withLocationIds("a27", "a28"),
			new Stand().withName("Smatan Gold").withType(StandType.ARTIST).withLocationIds("a29", "a30"),
			new Stand().withName("רותם רקיר").withType(StandType.ARTIST).withLocationIds("a3"),
			new Stand().withName("Techelet Art").withType(StandType.ARTIST).withLocationIds("a31"),
			new Stand().withName("נומי").withType(StandType.ARTIST).withLocationIds("a32"),
			new Stand().withName("Tomatoes Trash").withType(StandType.ARTIST).withLocationIds("a33"),
			new Stand().withName("Vivi Fox").withType(StandType.ARTIST).withLocationIds("a34"),
			new Stand().withName("The Chip Club").withType(StandType.ARTIST).withLocationIds("a35", "a36"),
			new Stand().withName("YUEvander").withType(StandType.ARTIST).withLocationIds("a37"),
			new Stand().withName("Livinkart").withType(StandType.ARTIST).withLocationIds("a38"),
			new Stand().withName("vsrstuff").withType(StandType.ARTIST).withLocationIds("a39", "a40"),
			new Stand().withName("קומיקס ומנגה של טומר").withType(StandType.ARTIST).withLocationIds("a4"),
			new Stand().withName("Yodarem0n_Art").withType(StandType.ARTIST).withLocationIds("a41"),
			new Stand().withName("Martin Draws").withType(StandType.ARTIST).withLocationIds("a42"),
			new Stand().withName("adelistic").withType(StandType.ARTIST).withLocationIds("a43", "a44"),
			new Stand().withName("The ultimate fish🐟🐟").withType(StandType.ARTIST).withLocationIds("a45", "a46"),
			new Stand().withName("Captain Ayay").withType(StandType.ARTIST).withLocationIds("a5", "a6"),
			new Stand().withName("Orezpan Art").withType(StandType.ARTIST).withLocationIds("a7"),
			new Stand().withName("Awii.ner").withType(StandType.ARTIST).withLocationIds("a8"),
			new Stand().withName("meilavi95").withType(StandType.ARTIST).withLocationIds("a9", "a10"),
			new Stand().withName("מריליה").withType(StandType.ARTIST).withLocationIds("b1", "b2"),
			new Stand().withName("Ro._.chan").withType(StandType.ARTIST).withLocationIds("b10", "b11"),
			new Stand().withName("crimson soda").withType(StandType.ARTIST).withLocationIds("b12"),
			new Stand().withName("fluffykittenka").withType(StandType.ARTIST).withLocationIds("b13"),
			new Stand().withName("Gabisweb").withType(StandType.ARTIST).withLocationIds("b14"),
			new Stand().withName("DiscoveryArts (Aiko & Sel)").withType(StandType.ARTIST).withLocationIds("b15"),
			new Stand().withName("HillelArt").withType(StandType.ARTIST).withLocationIds("b16"),
			new Stand().withName("Bogouki").withType(StandType.ARTIST).withLocationIds("b17"),
			new Stand().withName("eladb_art").withType(StandType.ARTIST).withLocationIds("b18"),
			new Stand().withName("kartzi").withType(StandType.ARTIST).withLocationIds("b19", "b20"),
			new Stand().withName("Gasi_CL").withType(StandType.ARTIST).withLocationIds("b21"),
			new Stand().withName("Kaegomi").withType(StandType.ARTIST).withLocationIds("b22"),
			new Stand().withName("CHRONIIKA").withType(StandType.ARTIST).withLocationIds("b23", "b24"),
			new Stand().withName("Eli Zeroix").withType(StandType.ARTIST).withLocationIds("b25", "b26"),
			new Stand().withName("Jupiilol").withType(StandType.ARTIST).withLocationIds("b27"),
			new Stand().withName("XX סטודיו").withType(StandType.ARTIST).withLocationIds("b28"),
			new Stand().withName("Haruempathy").withType(StandType.ARTIST).withLocationIds("b29", "b30"),
			new Stand().withName("ashyitoons").withType(StandType.ARTIST).withLocationIds("b3"),
			new Stand().withName("Inimi Draws! Art by Maayan Elbaz").withType(StandType.ARTIST).withLocationIds("b31", "b32"),
			new Stand().withName("Hikikomoring - Art by Sem").withType(StandType.ARTIST).withLocationIds("b33", "b34"),
			new Stand().withName("Cryptic arts").withType(StandType.ARTIST).withLocationIds("b35", "b36"),
			new Stand().withName("הדוכן של אריאל").withType(StandType.ARTIST).withLocationIds("b37", "b38"),
			new Stand().withName("fleshvore").withType(StandType.ARTIST).withLocationIds("b39"),
			new Stand().withName("Dinchies").withType(StandType.ARTIST).withLocationIds("b4"),
			new Stand().withName("Elmiellart").withType(StandType.ARTIST).withLocationIds("b40"),
			new Stand().withName("Mete Art").withType(StandType.ARTIST).withLocationIds("b41"),
			new Stand().withName("Momo’s heart").withType(StandType.ARTIST).withLocationIds("b42"),
			new Stand().withName("Scylla art").withType(StandType.ARTIST).withLocationIds("b43", "b44"),
			new Stand().withName("Kimichu.x").withType(StandType.ARTIST).withLocationIds("b45", "b46"),
			new Stand().withName("FlyingFox Art").withType(StandType.ARTIST).withLocationIds("b47", "b48"),
			new Stand().withName("Naamoola").withType(StandType.ARTIST).withLocationIds("b49"),
			new Stand().withName("B4RMN").withType(StandType.ARTIST).withLocationIds("b5", "b6"),
			new Stand().withName("nallybus").withType(StandType.ARTIST).withLocationIds("b50", "b51"),
			new Stand().withName("ROTEMZ AND ORANnotOREN").withType(StandType.ARTIST).withLocationIds("b52", "b53"),
			new Stand().withName("Learoosh & Shinomi").withType(StandType.ARTIST).withLocationIds("b54", "b55"),
			new Stand().withName("R&G").withType(StandType.ARTIST).withLocationIds("b56"),
			new Stand().withName("Norpamidor").withType(StandType.ARTIST).withLocationIds("b57"),
			new Stand().withName("Nod3ret").withType(StandType.ARTIST).withLocationIds("b58"),
			new Stand().withName("Nighto").withType(StandType.ARTIST).withLocationIds("b59", "b60"),
			new Stand().withName("NatArt").withType(StandType.ARTIST).withLocationIds("b61", "b62"),
			new Stand().withName("Ray slay").withType(StandType.ARTIST).withLocationIds("b63"),
			new Stand().withName("Purple bunny").withType(StandType.ARTIST).withLocationIds("b64"),
			new Stand().withName("PUFFERMISH ARTZ").withType(StandType.ARTIST).withLocationIds("b65"),
			new Stand().withName("rubraboa").withType(StandType.ARTIST).withLocationIds("b66", "b67"),
			new Stand().withName("Lapinvert.e").withType(StandType.ARTIST).withLocationIds("b68", "b69"),
			new Stand().withName("Shourterthan").withType(StandType.ARTIST).withLocationIds("b7", "b7.5"),
			new Stand().withName("BURUBUROKORI's").withType(StandType.ARTIST).withLocationIds("b8"),
			new Stand().withName("Burucheri").withType(StandType.ARTIST).withLocationIds("b9")
		);
	}

	private List<Stand> getTediStands() {
		return Arrays.asList(
			new Stand().withName("החנות של גולדן שואו").withType(StandType.OTHER).withLocationIds("d1", "d2"),
			new Stand().withName("סטודיו THE HIVE").withType(StandType.MANGA).withLocationIds("d10"),
			new Stand().withName("Mini Tokio").withType(StandType.CLOTHES).withLocationIds("d11", "d12"),
			new Stand().withName("Satanic Panic Shop").withType(StandType.JEWELRY).withLocationIds("d13", "d14"),
			new Stand().withName("Yael's Colors").withType(StandType.JEWELRY).withLocationIds("d15"),
			new Stand().withName("Moonmor & Foxyohay").withType(StandType.ARTIST).withLocationIds("d16"),
			new Stand().withName("Gal Zippor Art").withType(StandType.ARTIST).withLocationIds("d17", "d18"),
			new Stand().withName("Noyanny").withType(StandType.JEWELRY).withLocationIds("d19"),
			new Stand().withName("grilled little lamb").withType(StandType.ARTIST).withLocationIds("d20"),
			new Stand().withName("anicomgeek").withType(StandType.MERCH).withLocationIds("d21", "d22"),
			new Stand().withName("עמדת צילום").withType(StandType.OTHER).withLocationIds("d23", "d24"),
			new Stand().withName("Purple Beard").withType(StandType.CLOTHES).withLocationIds("d25", "d26", "d27", "d28", ""),
			new Stand().withName("איגוד מקצועות האנימציה").withType(StandType.OTHER).withLocationIds("d29", "d30"),
			new Stand().withName("SweetheartYun").withType(StandType.JEWELRY).withLocationIds("d3", " d4"),
			new Stand().withName("fantasy house").withType(StandType.MERCH).withLocationIds("d31", "d32", "d33", "d34"),
			new Stand().withName("הפרוותית").withType(StandType.OTHER).withLocationIds("d35"),
			new Stand().withName("Y-boo!").withType(StandType.ARTIST).withLocationIds("d36"),
			new Stand().withName("GachAnima").withType(StandType.MERCH).withLocationIds("d37", "d38"),
			new Stand().withName("Orchu_beads").withType(StandType.JEWELRY).withLocationIds("d39"),
			new Stand().withName("Spoonkit").withType(StandType.JEWELRY).withLocationIds("d41"),
			new Stand().withName("JustAWetTowel").withType(StandType.ARTIST).withLocationIds("d42"),
			new Stand().withName("רפאים").withType(StandType.ARTIST).withLocationIds("d43", "d44"),
			new Stand().withName("AFlair").withType(StandType.HAND_MADE).withLocationIds("d46"),
			new Stand().withName("Raspberry").withType(StandType.JEWELRY).withLocationIds("d5", "d6"),
			new Stand().withName("מכללת בלינק - מסלול אנינמגה").withType(StandType.MANGA).withLocationIds("d51"),
			new Stand().withName("המרכז ללימודי יפנית").withType(StandType.OTHER).withLocationIds("d7", "d8"),
			new Stand().withName("Mzarssssss").withType(StandType.CLOTHES).withLocationIds("d9"),
			new Stand().withName("קאי קולקטורז").withType(StandType.TABLETOP_GAMES).withLocationIds("e13", "e14"),
			new Stand().withName("חלומות").withType(StandType.HAND_MADE).withLocationIds("e15"),
			new Stand().withName("MYST").withType(StandType.MERCH).withLocationIds("e16", "e17", "e18"),
			new Stand().withName("Babyzkpop").withType(StandType.JEWELRY).withLocationIds("e21", "e22"),
			new Stand().withName("ghost's cemetery").withType(StandType.MERCH).withLocationIds("e22"),
			new Stand().withName("AURORA").withType(StandType.HAND_MADE).withLocationIds("e23", "e24", "e25", "e26"),
			new Stand().withName("ravioli").withType(StandType.OTHER).withLocationIds("e27"),
			new Stand().withName("The Pop Labratory").withType(StandType.MERCH).withLocationIds("e28"),
			new Stand().withName("stuffer").withType(StandType.MERCH).withLocationIds("e29", "e30"),
			new Stand().withName("אנימה מרקט").withType(StandType.MERCH).withLocationIds("e31", "e32", "e33", "e34", "e35", "e36"),
			new Stand().withName("דוכן שיפודן").withType(StandType.CLOTHES).withLocationIds("f1", "f2", "f3", "f4", "f5", "f6", "f7", "f8"),
			new Stand().withName("אנימה סטור").withType(StandType.MERCH).withLocationIds("f101", "f102", "f103", "f104", "f105", "f106", "f107", "f108"),
			new Stand().withName("פופ האוס").withType(StandType.MERCH).withLocationIds("f109", "f110"),
			new Stand().withName("Creative a tea").withType(StandType.MERCH).withLocationIds("f11"),
			new Stand().withName("art studio handmade").withType(StandType.HAND_MADE).withLocationIds("f111"),
			new Stand().withName("N FIG").withType(StandType.MERCH).withLocationIds("f113", "f114", "f115", "f116"),
			new Stand().withName("Custom Pop Israel").withType(StandType.HAND_MADE).withLocationIds("f117", "f118"),
			new Stand().withName("בר אומנית ציפורניים").withType(StandType.OTHER).withLocationIds("f119"),
			new Stand().withName("ozart").withType(StandType.MERCH).withLocationIds("f12", "f13", "f14", "f15"),
			new Stand().withName("Aurora Charm").withType(StandType.MERCH).withLocationIds("f120"),
			new Stand().withName("ישראקומיקס").withType(StandType.MERCH).withLocationIds("f121", "f122", "f123", "f124"),
			new Stand().withName("Stormy").withType(StandType.CLOTHES).withLocationIds("f125", "f126", "f127", "f128", "f129", "f130"),
			new Stand().withName("A Silly Frog").withType(StandType.JEWELRY).withLocationIds("f131", "f132"),
			new Stand().withName("Laser iCon").withType(StandType.MERCH).withLocationIds("f133", "f134", "f135", "f136"),
			new Stand().withName("טרופי").withType(StandType.VIDEO_GAMES).withLocationIds("f137", "f138"),
			new Stand().withName("Chio crochet").withType(StandType.OTHER).withLocationIds("f139"),
			new Stand().withName("Fujoshi.il").withType(StandType.MANGA).withLocationIds("f140"),
			new Stand().withName("ריוונדל Rivendell").withType(StandType.HAND_MADE).withLocationIds("f141", "f142"),
			new Stand().withName("Candy Lenses").withType(StandType.MERCH).withLocationIds("f143", "f144", "f145", "f146"),
			new Stand().withName("Crafty Witches").withType(StandType.VIDEO_GAMES).withLocationIds("f147", "f148"),
			new Stand().withName("קאוואי לנד שופ").withType(StandType.JEWELRY).withLocationIds("f149", "f150"),
			new Stand().withName("Topdeck").withType(StandType.MERCH).withLocationIds("f151", "f152", "f153", "f154", "f155", "f156", "f157", "f158"),
			new Stand().withName("Anime_Glass").withType(StandType.MERCH).withLocationIds("f159", "f160"),
			new Stand().withName("יוצרים עם דוד").withType(StandType.MERCH).withLocationIds("f16", "f17"),
			new Stand().withName("Geekish").withType(StandType.MERCH).withLocationIds("f18", "f19"),
			new Stand().withName("AkinaPaz").withType(StandType.CLOTHES).withLocationIds("f20"),
			new Stand().withName("Almogolan art").withType(StandType.MERCH).withLocationIds("f21", "f22"),
			new Stand().withName("Sampai designs").withType(StandType.JEWELRY).withLocationIds("f23", "f24"),
			new Stand().withName("Oz magical gifts").withType(StandType.JEWELRY).withLocationIds("f25", "f26"),
			new Stand().withName("מיסקייסיס").withType(StandType.MERCH).withLocationIds("f27", "f28", "f29", "f30", "f31", "f32", "f33", "f34"),
			new Stand().withName("Pixel Kid").withType(StandType.JEWELRY).withLocationIds("f35", "f36"),
			new Stand().withName("לוליפופ").withType(StandType.CLOTHES).withLocationIds("f37", "f38"),
			new Stand().withName("Eclipsic TCG").withType(StandType.TABLETOP_GAMES).withLocationIds("f39", "f40"),
			new Stand().withName("גיימינג לנד gaming land").withType(StandType.VIDEO_GAMES).withLocationIds("f41", "f42", "f43", "f44"),
			new Stand().withName("Fusion Frame").withType(StandType.HAND_MADE).withLocationIds("f45", "f46"),
			new Stand().withName("המחלקה לקסם").withType(StandType.JEWELRY).withLocationIds("f47"),
			new Stand().withName("Curly Craft").withType(StandType.JEWELRY).withLocationIds("f48"),
			new Stand().withName("Akiva’s little shop").withType(StandType.OTHER).withLocationIds("f49"),
			new Stand().withName("Ms_crochettt").withType(StandType.HAND_MADE).withLocationIds("f50"),
			new Stand().withName("Jc.makes_art").withType(StandType.ARTIST).withLocationIds("f51"),
			new Stand().withName("אפריל").withType(StandType.OTHER).withLocationIds("f52"),
			new Stand().withName("Jill creations").withType(StandType.OTHER).withLocationIds("f53"),
			new Stand().withName("Velvet Octopus").withType(StandType.CLOTHES).withLocationIds("f54"),
			new Stand().withName("קאמיקוני").withType(StandType.MERCH).withLocationIds("f55"),
			new Stand().withName("ultimate.collect.il").withType(StandType.TABLETOP_GAMES).withLocationIds("f56"),
			new Stand().withName("SoniAnimeSocks").withType(StandType.MERCH).withLocationIds("f57"),
			new Stand().withName("Natoki").withType(StandType.JEWELRY).withLocationIds("f58"),
			new Stand().withName("אורלי קסם").withType(StandType.CLOTHES).withLocationIds("f59"),
			new Stand().withName("Seal mochi kawaii shop").withType(StandType.HAND_MADE).withLocationIds("f60"),
			new Stand().withName("Otaku and Fujoshi").withType(StandType.MERCH).withLocationIds("f61", "f62"),
			new Stand().withName("His Majesty's TCG").withType(StandType.OTHER).withLocationIds("f63", "f64", "f65", "f66"),
			new Stand().withName("Sidequest").withType(StandType.MERCH).withLocationIds("f67", "f68", "f69", "f70"),
			new Stand().withName("Anime Life").withType(StandType.CLOTHES).withLocationIds("f71", "f72"),
			new Stand().withName("אין מקום בבית").withType(StandType.MERCH).withLocationIds("f73", "f74"),
			new Stand().withName("iDollsCollectiom").withType(StandType.MERCH).withLocationIds("f75", "f76", "f77", "f78"),
			new Stand().withName("SUNSH").withType(StandType.MERCH).withLocationIds("f79", "f80"),
			new Stand().withName("Kuzco").withType(StandType.CLOTHES).withLocationIds("f81", "f82", "f83", "f84"),
			new Stand().withName("Charmless").withType(StandType.MERCH).withLocationIds("f85", "f86"),
			new Stand().withName("קימבי").withType(StandType.MERCH).withLocationIds("f87", "f88"),
			new Stand().withName("orazashy").withType(StandType.JEWELRY).withLocationIds("f89", "f90"),
			new Stand().withName("שני לימונים").withType(StandType.MERCH).withLocationIds("f9", "f10"),
			new Stand().withName("קי\"ק - קבוצת יצירת קומיקס").withType(StandType.MANGA).withLocationIds("f91", "f92"),
			new Stand().withName("גיק שלטר").withType(StandType.TABLETOP_GAMES).withLocationIds("f93", "f94"),
			new Stand().withName("Mollys").withType(StandType.JEWELRY).withLocationIds("f95", "f96"),
			new Stand().withName("YK Crochet").withType(StandType.HAND_MADE).withLocationIds("f97", "f98"),
			new Stand().withName("mayo design X art").withType(StandType.CLOTHES).withLocationIds("f99", "f100"),
			new Stand().withName("דיגי-דאן טירו טאן").withType(StandType.JEWELRY).withLocationIds("g1", "g2"),
			new Stand().withName("סירולניה").withType(StandType.TABLETOP_GAMES).withLocationIds("g11", "g12", "g13", "g14", "g15", "g16", "g17", "g18"),
			new Stand().withName("AB.art").withType(StandType.MERCH).withLocationIds("g19", "g20"),
			new Stand().withName("Japaneasy").withType(StandType.OTHER).withLocationIds("g21"),
			new Stand().withName("Shir K").withType(StandType.JEWELRY).withLocationIds("g21", "g22", "g23", "g24"),
			new Stand().withName("Toysland.il").withType(StandType.JEWELRY).withLocationIds("g25", "g26"),
			new Stand().withName("Dec's IY").withType(StandType.JEWELRY).withLocationIds("g27", "g28"),
			new Stand().withName("Gear fifth figures").withType(StandType.MERCH).withLocationIds("g29", "g30"),
			new Stand().withName("קאיטו - אסיה זה הבית").withType(StandType.MERCH).withLocationIds("g29", "g30", "g31", "g32"),
			new Stand().withName("דוכן מוצרי אנימה ומנגה רשמיים יד שנייה").withType(StandType.MERCH).withLocationIds("g3", "g4"),
			new Stand().withName("Luminite’s Studio").withType(StandType.MERCH).withLocationIds("g31", "g32"),
			new Stand().withName("yaelas art").withType(StandType.JEWELRY).withLocationIds("g33", "g34"),
			new Stand().withName("anime station").withType(StandType.MERCH).withLocationIds("g35", "g36"),
			new Stand().withName("Litboxfandom").withType(StandType.MERCH).withLocationIds("g37", "g38"),
			new Stand().withName("בתאל וערן").withType(StandType.CLOTHES).withLocationIds("g39", "g40"),
			new Stand().withName("Anime Wave").withType(StandType.MERCH).withLocationIds("g41", "g42", "g43", "g44", "g45", "g46", "g47", "g48"),
			new Stand().withName("קומיקאזה").withType(StandType.MERCH).withLocationIds("g5", "g6", "g7", "g8"),
			new Stand().withName("הממלכה / פריק").withType(StandType.TABLETOP_GAMES).withLocationIds("g9", "g10")
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
			.leftToRight(1760.000f, 373.000f, "a", 19, 24, "a25")
			.leftToRight(2352.000f, 373.000f, "a", 25, 30, "a31")
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

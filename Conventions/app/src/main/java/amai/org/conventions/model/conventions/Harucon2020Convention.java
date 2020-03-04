package amai.org.conventions.model.conventions;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import amai.org.conventions.R;
import amai.org.conventions.feedback.SurveySender;
import amai.org.conventions.feedback.forms.EventFeedbackForm;
import amai.org.conventions.feedback.forms.FeedbackForm;
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
import amai.org.conventions.networking.SurveyDataRetriever;
import amai.org.conventions.utils.CollectionUtils;
import amai.org.conventions.utils.ConventionStorage;
import androidx.annotation.Nullable;

public class Harucon2020Convention extends AmaiConvention {
	// Hall names
	private static final String MAIN_HALL_NAME = "אולם ראשי";
	private static final String SCHWARTZ_NAME = "אודיטוריום שוורץ";
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
	private static final int EVENT_ID_AMAIDOL = 8651;

	// Ids of google spreadsheets associated with the special events
	private static final String AMAIDOL_SPREADSHEET_ID = "1u9xu3FNq2gA25oZoVHVguTzJA5HheXWPf2wnUj-iipE";

	static {
		FeedbackQuestion.addQuestion(QUESTION_ID_AMAIDOL_NAME, R.string.amaidol_name_question);
		FeedbackQuestion.addQuestion(QUESTION_ID_AMAIDOL_VOTE, R.string.amaidol_vote_question);
	}

	// Stand types
	private enum StandType implements Stand.StandType {
		GENERAL(R.string.general_stand, R.drawable.ic_shopping_basket),
		ARTIST(R.string.artist_stand, R.drawable.ic_color_lens),
		DONATION(R.string.donation_stand, R.drawable.ic_monetization);

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
		return new ConventionStorage(this, R.raw.harucon2020_convention_events, 1);
	}

	@Override
	protected Calendar initDate() {
		Calendar date = Calendar.getInstance();
		date.clear();
		date.set(2020, Calendar.MARCH, 10);
		return date;
	}

	@Override
	protected String initID() {
		return "Harucon2020";
	}

	@Override
	protected String initDisplayName() {
		return "הארוקון 2020";
	}

	@Override
	protected URL initUpdatesURL() {
		try {
			return new URL("https://amai.org.il/wp-content/plugins/GetHaruconFeed.php");
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
					.withSendUrl(new URL("https://docs.google.com/forms/d/e/1FAIpQLSdNt0smJ77qdnLdZuX53m6YqBXxW7fKxzn2n-3EnW7zQIbTZg/formResponse"));
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
					.withSendUrl(new URL("https://docs.google.com/forms/d/e/1FAIpQLSfWhoCDzTz83jA87HGEudl1nnBlQoURifdvllPxwZBX6uA-Pw/formResponse"));
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
		return feedbackForm;
	}

	@Override
	protected URL initModelURL() {
		try {
			return new URL("https://harucon.org.il/2020/wp-admin/admin-ajax.php?action=get_event_list");
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	protected ImageIdToImageResourceMapper initImageMapper() {
		ImageIdToImageResourceMapper imageMapper = new ImageIdToImageResourceMapper();

		imageMapper
				.addMapping("https://harucon.org.il/2020/wp-content/uploads/sites/21/2020/01/54398045_2304783192875510_4248499860140982272_o-e1579101907870.jpg", R.drawable.event_cosplay)
				.addMapping("https://harucon.org.il/2020/wp-content/uploads/sites/21/2020/01/GuyTamir.jpg", R.drawable.event_guytamir)
				.addMapping("https://harucon.org.il/2020/wp-content/uploads/sites/21/2020/01/nami.jpg", R.drawable.event_nami)
				.addMapping("https://harucon.org.il/2020/wp-content/uploads/sites/21/2020/01/Amaidol.jpg", R.drawable.event_amaidol)
				.addMapping("https://harucon.org.il/2020/wp-content/uploads/sites/21/2020/01/Calligraphy.jpg", R.drawable.event_calligraphy)
				.addMapping("https://harucon.org.il/2020/wp-content/uploads/sites/21/2020/01/DalitBloch.jpg", R.drawable.event_dalitbloch)
				.addMapping("https://harucon.org.il/2020/wp-content/uploads/sites/21/2020/01/DannyOrbach.jpg", R.drawable.event_dannyorbach)
				.addMapping("https://harucon.org.il/2020/wp-content/uploads/sites/21/2020/01/EfratPahima.jpg", R.drawable.event_efratpahima)
				.addMapping("https://harucon.org.il/2020/wp-content/uploads/sites/21/2020/01/EranBenAsher.jpg", R.drawable.event_eranbenasher)
				.addMapping("https://harucon.org.il/2020/wp-content/uploads/sites/21/2020/01/GuyLenman1.jpg", R.drawable.event_guylenman1)
				.addMapping("https://harucon.org.il/2020/wp-content/uploads/sites/21/2020/01/HapoelTokyo.jpg", R.drawable.event_hapoeltokyo)
				.addMapping("https://harucon.org.il/2020/wp-content/uploads/sites/21/2020/01/Hikari.jpg", R.drawable.event_hikarigreen)
				.addMapping("https://harucon.org.il/2020/wp-content/uploads/sites/21/2020/01/InbarEitan.jpg", R.drawable.event_inbareitan)
				.addMapping("https://harucon.org.il/2020/wp-content/uploads/sites/21/2020/01/Japaneasy.png", R.drawable.event_japaneasy)
				.addMapping("https://harucon.org.il/2020/wp-content/uploads/sites/21/2020/01/KerenHaim.jpg", R.drawable.event_kerenhaim)
				.addMapping("https://harucon.org.il/2020/wp-content/uploads/sites/21/2020/01/KristinaMartynenko.jpg", R.drawable.event_kristinamartynenko)
				.addMapping("https://harucon.org.il/2020/wp-content/uploads/sites/21/2020/01/LeahUchitel.jpg", R.drawable.event_leahuchitel)
				.addMapping("https://harucon.org.il/2020/wp-content/uploads/sites/21/2020/01/LironAfriat.jpg", R.drawable.event_lironafriat)
				.addMapping("https://harucon.org.il/2020/wp-content/uploads/sites/21/2020/01/Manga.png", R.drawable.event_manga)
				.addMapping("https://harucon.org.il/2020/wp-content/uploads/sites/21/2020/01/MatanKachel-scaled.jpg", R.drawable.event_matankachel)
				.addMapping("https://harucon.org.il/2020/wp-content/uploads/sites/21/2020/01/MichalLevin.jpg", R.drawable.event_michallevin)
				.addMapping("https://harucon.org.il/2020/wp-content/uploads/sites/21/2020/01/NirGerber.jpg", R.drawable.event_nirgerber)
				.addMapping("https://harucon.org.il/2020/wp-content/uploads/sites/21/2020/01/OdedCohen.jpg", R.drawable.event_odedcohen)
				.addMapping("https://harucon.org.il/2020/wp-content/uploads/sites/21/2020/01/OmerCohen.jpg", R.drawable.event_omercohen)
				.addMapping("https://harucon.org.il/2020/wp-content/uploads/sites/21/2020/01/OmerKedem.jpg", R.drawable.event_omerkedem)
				.addMapping("https://harucon.org.il/2020/wp-content/uploads/sites/21/2020/01/OmerMessinger-scaled.jpg", R.drawable.event_omermessinger)
				.addMapping("https://harucon.org.il/2020/wp-content/uploads/sites/21/2020/01/OmerRozencwieg.jpg", R.drawable.event_omerrozencwieg)
				.addMapping("https://harucon.org.il/2020/wp-content/uploads/sites/21/2020/01/OraRakov.jpg", R.drawable.event_orarakov)
				.addMapping("https://harucon.org.il/2020/wp-content/uploads/sites/21/2020/01/PazGlebotzki.jpg", R.drawable.event_pazglebotzki)
				.addMapping("https://harucon.org.il/2020/wp-content/uploads/sites/21/2020/01/PieceOfMind.jpg", R.drawable.event_pieceofmind)
				.addMapping("https://harucon.org.il/2020/wp-content/uploads/sites/21/2020/01/RanWolf.jpg", R.drawable.event_ranwolf)
				.addMapping("https://harucon.org.il/2020/wp-content/uploads/sites/21/2020/01/RazGreenberg.jpg", R.drawable.event_razgreenberg)
				.addMapping("https://harucon.org.il/2020/wp-content/uploads/sites/21/2020/01/KimiNami.jpg", R.drawable.event_rideyourwave)
				.addMapping("https://harucon.org.il/2020/wp-content/uploads/sites/21/2020/01/RomanLempert.jpg", R.drawable.event_romanlempert)
				.addMapping("https://harucon.org.il/2020/wp-content/uploads/sites/21/2020/01/SheliVikniansky.jpg", R.drawable.event_shelivikniansky)
				.addMapping("https://harucon.org.il/2020/wp-content/uploads/sites/21/2020/01/TalHazan.jpg", R.drawable.event_talhazan)
				.addMapping("https://harucon.org.il/2020/wp-content/uploads/sites/21/2020/01/YaelStein.jpg", R.drawable.event_yaelstein)
				.addMapping("https://harucon.org.il/2020/wp-content/uploads/sites/21/2020/01/YoutubePanel.jpg", R.drawable.event_youtubepanel)
				.addMapping("https://harucon.org.il/2020/wp-content/uploads/sites/21/2020/01/69382648_611083972753199_4254163118118141952_o.jpg", R.drawable.event_runway)
				.addMapping("https://harucon.org.il/2020/wp-content/uploads/sites/21/2019/12/GameMangaCafe.jpg", R.drawable.event_manga_cafe)
				.addMapping("https://harucon.org.il/2020/wp-content/uploads/sites/21/2020/01/LeonSalomon.jpeg", R.drawable.event_leonsalomon)
				.addMapping("https://harucon.org.il/2020/wp-content/uploads/sites/21/2020/02/SemLecture.jpg", R.drawable.event_semlectureapp)

		;

		imageMapper.addMapping(ImageIdToImageResourceMapper.EVENT_GENERIC, R.drawable.harucon2020_event_default_background);

		// Excluded IDs - mostly for debug purposes (don't show messages about these when entering an event that has them)
		imageMapper.addExcludedIds(
				// Games room
				"https://harucon.org.il/2020/wp-content/uploads/sites/21/2019/07/IMG_3812.png",
				"https://harucon.org.il/2020/wp-content/uploads/sites/21/2019/12/GameGunpla.jpg",
				"https://harucon.org.il/2020/wp-content/uploads/sites/21/2019/12/GameBoard.jpg",
				"https://harucon.org.il/2020/wp-content/uploads/sites/21/2019/12/GameGo.jpg",
				"https://harucon.org.il/2020/wp-content/uploads/sites/21/2019/12/GameNinjaWar.jpg",
				// Ride your wave unused pictures
				"https://harucon.org.il/2020/wp-content/uploads/sites/21/2020/01/Mr.MasaakiYuasa_photo.jpg",
				"https://harucon.org.il/2020/wp-content/uploads/sites/21/2020/01/Kiminami_0161718.jpg",
				"https://harucon.org.il/2020/wp-content/uploads/sites/21/2020/01/Nami_c295.jpg",
				"https://harucon.org.il/2020/wp-content/uploads/sites/21/2020/01/Nami_c142_01.jpg",
				"https://harucon.org.il/2020/wp-content/uploads/sites/21/2020/01/Nami_c313_01.jpg",
				// Amaidol unused pictures
				"https://harucon.org.il/2020/wp-content/uploads/sites/21/2020/01/Arielle.png",
				"https://harucon.org.il/2020/wp-content/uploads/sites/21/2020/01/Aviv.png",
				"https://harucon.org.il/2020/wp-content/uploads/sites/21/2020/01/43462148_2050555078367760_2980978848436322304_o.jpg",
				"https://harucon.org.il/2020/wp-content/uploads/sites/21/2020/01/photo_2019-12-21_21-24-40.jpg",
				// Runway unused pictures
				"https://harucon.org.il/2020/wp-content/uploads/sites/21/2020/01/03-ירדן-לבני.png",
				// Cosplay unused pictures
				"https://harucon.org.il/2020/wp-content/uploads/sites/21/2020/01/01-בוריס-רודמן.png",
				"https://harucon.org.il/2020/wp-content/uploads/sites/21/2020/01/02-מעיין-אלבז.png",
				"https://harucon.org.il/2020/wp-content/uploads/sites/21/2020/01/57504481_2350322358321593_8123707843250487296_o.jpg"
		);

		return imageMapper;
	}

	@Override
	protected Halls initHalls() {
		Hall mainHall = new Hall().withName(MAIN_HALL_NAME).withOrder(1);
		Hall auditorium = new Hall().withName(SCHWARTZ_NAME).withOrder(2);
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
		Hall schwatrz = this.getHalls().findByName(SCHWARTZ_NAME);
		Hall eshkol1 = this.getHalls().findByName(ESHKOL1_NAME);
		Hall eshkol2 = this.getHalls().findByName(ESHKOL2_NAME);
		Hall eshkol3 = this.getHalls().findByName(ESHKOL3_NAME);
		Hall workshops = this.getHalls().findByName(WORKSHOPS_NAME);
		Hall games = this.getHalls().findByName(GAMES_NAME);
		Hall cosplayArea = this.getHalls().findByName(COSPLAY_AREA_NAME);

		Floor entrance = new Floor(1)
				.withName("מתחם כניסה")
				.withImageResource(R.raw.harucon2020_floor_entrance, true)
				.withImageWidth(1297.96997f)
				.withImageHeight(804.69f);
		Floor floor1 = new Floor(2)
				.withName("קומה 1")
				.withImageResource(R.raw.harucon2020_floor1, true)
				.withImageWidth(1886.83997f)
				.withImageHeight(1024.06604f);
		Floor floor2 = new Floor(3)
				.withName("קומה 2")
				.withImageResource(R.raw.harucon2020_floor2, true)
				.withImageWidth(2121.07007f)
				.withImageHeight(1268.43005f);

		StandsArea agam = new StandsArea().withName("טרקלין אגם").withStands(getAgamStands()).withImageResource(R.drawable.harucon2020_stands_map_agam).withImageWidth(2700).withImageHeight(1069);
		StandsArea pinkus = new StandsArea().withName("אולם פינקוס").withStands(getPinkusStands()).withImageResource(R.drawable.harucon2020_stands_map_pinkus).withImageWidth(2700).withImageHeight(1596);
		return new ConventionMap()
				.withFloors(Arrays.asList(entrance, floor1, floor2))
				.withDefaultFloor(floor1)
				.withLocations(
						CollectionUtils.flattenList(
								inFloor(entrance,
										new MapLocation()
												.withPlace(new Place().withName("עמדת מודיעין ומרצ'נדייז כנסי"))
												.withMarkerResource(R.raw.harucon2020_marker_information, true)
												.withSelectedMarkerResource(R.raw.harucon2020_marker_information_selected, true)
												.withMarkerHeight(136.51f)
												.withX(1047.39f)
												.withY(658.18f),
										new MapLocation()
												.withPlace(new Place().withName("עמדות צימוד"))
												.withMarkerResource(R.raw.harucon2020_marker_bracelets, true)
												.withSelectedMarkerResource(R.raw.harucon2020_marker_bracelets_selected, true)
												.withMarkerHeight(104.76f)
												.withX(661.73f)
												.withY(674.37f),
										new MapLocation()
												.withPlace(new Place().withName("קופות"))
												.withMarkerResource(R.raw.harucon2020_marker_cashiers, true)
												.withSelectedMarkerResource(R.raw.harucon2020_marker_cashiers_selected, true)
												.withMarkerHeight(104.76f)
												.withX(498.6f)
												.withY(634.04f),
										new MapLocation()
												.withPlace(new Place().withName("קופה נגישה"))
												.withMarkerResource(R.raw.harucon2020_marker_accessible_cashier, true)
												.withSelectedMarkerResource(R.raw.harucon2020_marker_accessible_cashier_selected, true)
												.withMarkerHeight(104.76f)
												.withX(670.84f)
												.withY(521.85f),
										new MapLocation()
												.withPlace(new Place().withName("מתחם הזמנה מראש"))
												.withMarkerResource(R.raw.harucon2020_marker_preorders, true)
												.withSelectedMarkerResource(R.raw.harucon2020_marker_preorders_selected, true)
												.withMarkerHeight(104.75f)
												.withX(722.45f)
												.withY(237.4f),
										new MapLocation()
												.withPlace(new Place().withName("מתחם קנייה במקום"))
												.withMarkerResource(R.raw.harucon2020_marker_tickets_area, true)
												.withSelectedMarkerResource(R.raw.harucon2020_marker_tickets_area_selected, true)
												.withMarkerHeight(104.76f)
												.withX(220.69f)
												.withY(369.84f),
										new MapLocation()
												.withPlace(new Place().withName("מעבר נגיש"))
												.withMarkerResource(R.raw.harucon2020_marker_accessible_passage, true)
												.withSelectedMarkerResource(R.raw.harucon2020_marker_accessible_passage_selected, true)
												.withMarkerHeight(104.76f)
												.withX(195.73f)
												.withY(258.36f)
								),
								inFloor(floor1,
										new MapLocation()
												.withPlace(new Place().withName("שירותים"))
												.withMarkerResource(R.raw.harucon2020_marker_toilet, true)
												.withSelectedMarkerResource(R.raw.harucon2020_marker_toilet_selected, true)
												.withMarkerHeight(114.51f)
												.withX(1766.375f)
												.withY(617.61f),
//										new MapLocation()
//												.withPlace(new Place().withName("אורנים 4"))
//												.withMarkerResource(R.raw.harucon2020_marker_oranim4, true)
//												.withSelectedMarkerResource(R.raw.harucon2020_marker_oranim4_selected, true)
//												.withMarkerHeight(137.35f)
//												.withX(1515.21f)
//												.withY(773.53f),
//										new MapLocation()
//												.withPlace(new Place().withName("אורנים 3 - מתחם קונסולות - נינטנדו"))
//												.withMarkerResource(R.raw.harucon2020_marker_oranim3, true)
//												.withSelectedMarkerResource(R.raw.harucon2020_marker_oranim3_selected, true)
//												.withMarkerHeight(188.926f)
//												.withX(1394.149f)
//												.withY(825.14f),
										new MapLocation()
												.withName("מתחם דוכנים")
												.withPlace(pinkus)
												.withMarkerResource(R.raw.harucon2020_marker_stands, true)
												.withSelectedMarkerResource(R.raw.harucon2020_marker_stands_selected, true)
												.withMarkerHeight(111f)
												.withX(1214.895f)
												.withY(651.69f),
										new MapLocation()
												.withPlace(new Place().withName("שמירת חפצים"))
												.withMarkerResource(R.raw.harucon2020_marker_storage, true)
												.withSelectedMarkerResource(R.raw.harucon2020_marker_storage_selected, true)
												.withMarkerHeight(136.31f)
												.withX(1082.46f)
												.withY(230.81f),
										new MapLocation()
												.withPlace(new Place().withName("שירותים"))
												.withMarkerResource(R.raw.harucon2020_marker_toilet, true)
												.withSelectedMarkerResource(R.raw.harucon2020_marker_toilet_selected, true)
												.withMarkerHeight(114.5f)
												.withX(1038.645f)
												.withY(108.11f),
										new MapLocation()
												.withPlace(new Place().withName("מעלית"))
												.withMarkerResource(R.raw.harucon2020_marker_elevator, true)
												.withSelectedMarkerResource(R.raw.harucon2020_marker_elevator_selected, true)
												.withMarkerHeight(111.203f)
												.withX(875.795f)
												.withY(162.73f),
										new MapLocation()
												.withPlace(new Place().withName("מודיעין ודוכן אמא\"י"))
												.withMarkerResource(R.raw.harucon2020_marker_information_amai, true)
												.withSelectedMarkerResource(R.raw.harucon2020_marker_information_amai_selected, true)
												.withMarkerHeight(169.65f)
												.withX(858.685f)
												.withY(362.12f),
										new MapLocation()
												.withPlace(eshkol1)
												.withMarkerResource(R.raw.harucon2020_marker_eshkol1, true)
												.withSelectedMarkerResource(R.raw.harucon2020_marker_eshkol1_selected, true)
												.withMarkerHeight(137.35f)
												.withX(492.835f)
												.withY(595.31f),
										new MapLocation()
												.withPlace(schwatrz)
												.withMarkerResource(R.raw.harucon2020_marker_schwartz, true)
												.withSelectedMarkerResource(R.raw.harucon2020_marker_schwartz_selected, true)
												.withMarkerHeight(136.61f)
												.withX(658.125f)
												.withY(524.45f),
										new MapLocation()
												.withPlace(eshkol3)
												.withMarkerResource(R.raw.harucon2020_marker_eshkol3, true)
												.withSelectedMarkerResource(R.raw.harucon2020_marker_eshkol3_selected, true)
												.withMarkerHeight(137.51f)
												.withX(348.18f)
												.withY(810.02f),
										new MapLocation()
												.withPlace(eshkol2)
												.withMarkerResource(R.raw.harucon2020_marker_eshkol2, true)
												.withSelectedMarkerResource(R.raw.harucon2020_marker_eshkol2_selected, true)
												.withMarkerHeight(137.51f)
												.withX(542.615f)
												.withY(810.02f),
										new MapLocation()
												.withPlace(new Place().withName("שירותים"))
												.withMarkerResource(R.raw.harucon2020_marker_toilet, true)
												.withSelectedMarkerResource(R.raw.harucon2020_marker_toilet_selected, true)
												.withMarkerHeight(115.61f)
												.withX(271.885f)
												.withY(488.56f),
										new MapLocation()
												.withPlace(new Place().withName("מעלית"))
												.withMarkerResource(R.raw.harucon2020_marker_elevator, true)
												.withSelectedMarkerResource(R.raw.harucon2020_marker_elevator_selected, true)
												.withMarkerHeight(111.153f)
												.withX(67.675f)
												.withY(591.42f)
								),
								inFloor(floor2,
										new MapLocation()
												.withPlace(workshops)
												.withMarkerResource(R.raw.harucon2020_marker_workshops, true)
												.withSelectedMarkerResource(R.raw.harucon2020_marker_workshops_selected, true)
												.withMarkerHeight(157.17f)
												.withX(1755.43f)
												.withY(1101.26f),
										new MapLocation()
												.withPlace(new Place().withName("שיפוט קוספליי"))
												.withMarkerResource(R.raw.harucon2020_marker_cosplay_judgement, true)
												.withSelectedMarkerResource(R.raw.harucon2020_marker_cosplay_judgement_selected, true)
												.withMarkerHeight(136.57f)
												.withX(1592.98f)
												.withY(1040.06f),
										new MapLocation()
												.withPlace(new Place().withName(PARENTS_ROOM_NAME))
												.withMarkerResource(R.raw.harucon2020_marker_parents, true)
												.withSelectedMarkerResource(R.raw.harucon2020_marker_parents_selected, true)
												.withMarkerHeight(111.25f)
												.withX(1735.405f)
												.withY(838.33f),
										new MapLocation()
												.withPlace(new Place().withName("שירותים"))
												.withMarkerResource(R.raw.harucon2020_marker_toilet, true)
												.withSelectedMarkerResource(R.raw.harucon2020_marker_toilet_selected, true)
												.withMarkerHeight(116.24f)
												.withX(2005.02f)
												.withY(689.35f),
										new MapLocation()
												.withPlace(mainHall)
												.withMarkerResource(R.raw.harucon2020_marker_main_hall, true)
												.withSelectedMarkerResource(R.raw.harucon2020_marker_main_hall_selected, true)
												.withMarkerHeight(209.174f)
												.withX(1397.95f)
												.withY(606.68f),
										new MapLocation()
												.withPlace(new Place().withName("שירותים"))
												.withMarkerResource(R.raw.harucon2020_marker_toilet, true)
												.withSelectedMarkerResource(R.raw.harucon2020_marker_toilet_selected, true)
												.withMarkerHeight(118.9f)
												.withX(1356.515f)
												.withY(125.51f),
										new MapLocation()
												.withPlace(new Place().withName("מעלית"))
												.withMarkerResource(R.raw.harucon2020_marker_elevator, true)
												.withSelectedMarkerResource(R.raw.harucon2020_marker_elevator_selected, true)
												.withMarkerHeight(111.143f)
												.withX(1205f)
												.withY(147.49f),
										new MapLocation()
												.withPlace(cosplayArea)
												.withMarkerResource(R.raw.harucon2020_marker_cosplay_area, true)
												.withSelectedMarkerResource(R.raw.harucon2020_marker_cosplay_area_selected, true)
												.withMarkerHeight(307.85f)
												.withX(1180.215f)
												.withY(843.82f),
										new MapLocation()
												.withPlace(games)
												.withName("משחקייה")
												.withMarkerResource(R.raw.harucon2020_marker_games, true)
												.withSelectedMarkerResource(R.raw.harucon2020_marker_games_selected, true)
												.withMarkerHeight(355.411f)
												.withX(883.865f)
												.withY(752.84f),
										new MapLocation()
												.withName("שדרת ציירים ומתחם דוכנים")
												.withPlace(agam)
												.withMarkerResource(R.raw.harucon2020_marker_artists, true)
												.withSelectedMarkerResource(R.raw.harucon2020_marker_artists_selected, true)
												.withMarkerHeight(163.97f)
												.withX(883.915f)
												.withY(400.05f),
										new MapLocation()
												.withPlace(new Place().withName("שירותים"))
												.withMarkerResource(R.raw.harucon2020_marker_toilet, true)
												.withSelectedMarkerResource(R.raw.harucon2020_marker_toilet_selected, true)
												.withMarkerHeight(111.25f)
												.withX(402.49f)
												.withY(553.29f),
										new MapLocation()
												.withPlace(new Place().withName("מעלית"))
												.withMarkerResource(R.raw.harucon2020_marker_elevator, true)
												.withSelectedMarkerResource(R.raw.harucon2020_marker_elevator_selected, true)
												.withMarkerHeight(111.203f)
												.withX(293.99f)
												.withY(644.75f)
								)
						)
				);
	}

	private List<Stand> getPinkusStands() {
		return Arrays.asList(
				new Stand().withName("נקסוס").withType(StandType.GENERAL).withLocationName("c1-3").withImageX(297).withImageY(170),
				new Stand().withName("ארגון צדקה של סטארוורס- קוספליי, 501").withType(StandType.DONATION).withLocationName("c4").withImageX(462).withImageY(170),
				new Stand().withName("מחוקים - מחזמר בלשי מקורי").withType(StandType.GENERAL).withLocationName("c5-6").withImageX(584).withImageY(170),
				new Stand().withName("Spiffy Gear").withType(StandType.GENERAL).withLocationName("c7-8").withImageX(754).withImageY(170),
				new Stand().withName("אקיבה").withType(StandType.GENERAL).withLocationName("c9-10").withImageX(1058).withImageY(170),
				new Stand().withName("Fantasy House").withType(StandType.GENERAL).withLocationName("c11-12").withImageX(1222).withImageY(170),
				new Stand().withName("פופסטופ").withType(StandType.GENERAL).withLocationName("c13-16").withImageX(1612).withImageY(170),
				new Stand().withName("Candy Lenses").withType(StandType.GENERAL).withLocationName("c17-22").withImageX(2032).withImageY(170),
				new Stand().withName("nevetx").withType(StandType.GENERAL).withLocationName("c23").withImageX(2410).withImageY(454),
//				new Stand().withName("BOOBA MACHO").withType(StandType.GENERAL).withLocationName("c24").withImageX(2410).withImageY(536),
				new Stand().withName("המרכז ללימודי יפנית").withType(StandType.GENERAL).withLocationName("c25-26").withImageX(2410).withImageY(658),
				new Stand().withName("Svag").withType(StandType.GENERAL).withLocationName("c27-30").withImageX(2410).withImageY(910),
				new Stand().withName("gaming land").withType(StandType.GENERAL).withLocationName("c31-34").withImageX(2166).withImageY(1308),
				new Stand().withName("Bar's Pops").withType(StandType.GENERAL).withLocationName("c35-36").withImageX(1912).withImageY(1308),
				new Stand().withName("אנימה סטור").withType(StandType.GENERAL).withLocationName("c37-42").withImageX(1486).withImageY(1308),
				new Stand().withName("גיקפליז").withType(StandType.GENERAL).withLocationName("c43-44").withImageX(1102).withImageY(1308),
//				new Stand().withName("נינטנדו").withType(StandType.GENERAL).withLocationName("c45-48").withImageX(850).withImageY(1308),
				new Stand().withName("Panda shop").withType(StandType.GENERAL).withLocationName("c49-50").withImageX(636).withImageY(1438),
				new Stand().withName("Lyddar Cosplay").withType(StandType.GENERAL).withLocationName("d1-2").withImageX(1870).withImageY(438),
				new Stand().withName("Velvet Octopus").withType(StandType.GENERAL).withLocationName("d3-4").withImageX(1998).withImageY(568),
				new Stand().withName("RETRO GAME CENTER").withType(StandType.GENERAL).withLocationName("d5-8").withImageX(1998).withImageY(820),
				new Stand().withName("סומקי בע\"מ").withType(StandType.GENERAL).withLocationName("d9-10").withImageX(1868).withImageY(1028),
				new Stand().withName("תכשיטי הסניץ'המוזהב").withType(StandType.GENERAL).withLocationName("d11").withImageX(1734).withImageY(1028),
				new Stand().withName("AkinaPaz").withType(StandType.GENERAL).withLocationName("d12").withImageX(1654).withImageY(1028),
				new Stand().withName("kawaii - land shop").withType(StandType.GENERAL).withLocationName("d13").withImageX(1570).withImageY(1028),
				new Stand().withName("Japaneasy").withType(StandType.GENERAL).withLocationName("d14").withImageX(1484).withImageY(1028),
				new Stand().withName("גברת וודו").withType(StandType.GENERAL).withLocationName("d15-16").withImageX(1362).withImageY(900),
				new Stand().withName("may shiri - design & art").withType(StandType.GENERAL).withLocationName("d17").withImageX(1362).withImageY(774),
				new Stand().withName("אקיורו").withType(StandType.GENERAL).withLocationName("d18-20").withImageX(1362).withImageY(608),
				new Stand().withName("Blueberry_crown").withType(StandType.GENERAL).withLocationName("d21-22").withImageX(1492).withImageY(438),
				new Stand().withName("Patchiko").withType(StandType.GENERAL).withLocationName("d23").withImageX(1616).withImageY(438),
				new Stand().withName("בועת מחשבה - פאנזין אנימה").withType(StandType.DONATION).withLocationName("d24").withImageX(1700).withImageY(438),
				new Stand().withName("Poster Adir!").withType(StandType.GENERAL).withLocationName("e1-2").withImageX(1038).withImageY(438),
				new Stand().withName("Animode").withType(StandType.GENERAL).withLocationName("e3-4").withImageX(1166).withImageY(566),
				new Stand().withName("Pop-Storm").withType(StandType.GENERAL).withLocationName("e5-6").withImageX(1166).withImageY(734),
				new Stand().withName("ofirgdraw").withType(StandType.GENERAL).withLocationName("e7").withImageX(1166).withImageY(858),
				new Stand().withName("קריאת בקלפי טארוט").withType(StandType.GENERAL).withLocationName("e8").withImageX(1166).withImageY(942),
				new Stand().withName("Frozen Flawers").withType(StandType.GENERAL).withLocationName("e9-10").withImageX(1034).withImageY(1028),
				new Stand().withName("בלופ").withType(StandType.GENERAL).withLocationName("e11-14").withImageX(734).withImageY(1028),
				new Stand().withName("LYRA - Magical Stuff").withType(StandType.GENERAL).withLocationName("e15-20").withImageX(520).withImageY(734),
				new Stand().withName("דוכן יד 2").withType(StandType.GENERAL).withLocationName("e21").withImageX(612).withImageY(438),
				new Stand().withName("Hatz Lolita & More").withType(StandType.GENERAL).withLocationName("e22").withImageX(692).withImageY(438),
				new Stand().withName("FlyFox Art").withType(StandType.GENERAL).withLocationName("e23-24").withImageX(826).withImageY(438)
		);
	}

	private List<Stand> getAgamStands() {
		return Arrays.asList(
				new Stand().withName("החתול הסגול").withType(StandType.GENERAL).withLocationName("a1-3").withImageX(392).withImageY(204),
				new Stand().withName("בובות באות מאהבה").withType(StandType.GENERAL).withLocationName("a4-5").withImageX(520).withImageY(204),
				new Stand().withName("KaTsa").withType(StandType.GENERAL).withLocationName("a6").withImageX(598).withImageY(204),
				new Stand().withName("Mini Tokyo").withType(StandType.GENERAL).withLocationName("a7-8").withImageX(676).withImageY(204),
				new Stand().withName("המכללה תל חי").withType(StandType.DONATION).withLocationName("a9").withImageX(807).withImageY(204),
				new Stand().withName("אמאטראסו").withType(StandType.GENERAL).withLocationName("a10").withImageX(854).withImageY(204),
				new Stand().withName("ארבע אצבעות").withType(StandType.DONATION).withLocationName("a11-12").withImageX(934).withImageY(204),
				new Stand().withName("הממלכה").withType(StandType.GENERAL).withLocationName("a13-14").withImageX(1038).withImageY(204),
				new Stand().withName("קומיקאזה").withType(StandType.GENERAL).withLocationName("a15-17").withImageX(1220).withImageY(204),
				new Stand().withName("קומיקס וירקות").withType(StandType.GENERAL).withLocationName("a18-20").withImageX(1372).withImageY(204),
				new Stand().withName("שיפודן ישראל ואנימה ספין").withType(StandType.GENERAL).withLocationName("a21-26").withImageX(1664).withImageY(204),
				new Stand().withName("הפינה המאגניבה של מור הכחולה").withType(StandType.GENERAL).withLocationName("a27").withImageX(1898).withImageY(204),
				new Stand().withName("GeekStar").withType(StandType.GENERAL).withLocationName("a28").withImageX(1946).withImageY(204),
				new Stand().withName("אורלי קסם").withType(StandType.GENERAL).withLocationName("a29").withImageX(2000).withImageY(204),
				new Stand().withName("תומיקס קומיקס").withType(StandType.GENERAL).withLocationName("a30-32").withImageX(2103).withImageY(204),
				new Stand().withName("AnimeWave").withType(StandType.GENERAL).withLocationName("a33-38").withImageX(2392).withImageY(204),
				new Stand().withName("Anime Chudoku").withType(StandType.GENERAL).withLocationName("a39-40").withImageX(74).withImageY(700),
				new Stand().withName("Janken Musical").withType(StandType.DONATION).withLocationName("a41-42").withImageX(74).withImageY(816),
				new Stand().withName("Fetch sketch").withType(StandType.ARTIST).withLocationName("a43").withImageX(2620).withImageY(669),
				new Stand().withName("Ela.Me.Art").withType(StandType.ARTIST).withLocationName("a44").withImageX(2620).withImageY(726),
				new Stand().withName("Martin Draws").withType(StandType.ARTIST).withLocationName("a45").withImageX(2620).withImageY(788),
				new Stand().withName("האומנות של ריי").withType(StandType.ARTIST).withLocationName("b1").withImageX(958).withImageY(477),
				new Stand().withName("הדוכן של PurrFluff").withType(StandType.ARTIST).withLocationName("b2").withImageX(958).withImageY(537),
				new Stand().withName("שרבטים").withType(StandType.ARTIST).withLocationName("b3").withImageX(958).withImageY(596),
				new Stand().withName("Hadar_Art_Tzvi").withType(StandType.ARTIST).withLocationName("b4").withImageX(958).withImageY(654),
				new Stand().withName("ברמנ").withType(StandType.ARTIST).withLocationName("b5-6").withImageX(868).withImageY(860),
				new Stand().withName("adelistic").withType(StandType.ARTIST).withLocationName("b7").withImageX(782).withImageY(860),
				new Stand().withName("Kip, Eve & Co.").withType(StandType.ARTIST).withLocationName("b8").withImageX(722).withImageY(860),
				new Stand().withName("Popatochisp").withType(StandType.ARTIST).withLocationName("b9").withImageX(602).withImageY(753),
				new Stand().withName("grisim").withType(StandType.ARTIST).withLocationName("b10").withImageX(602).withImageY(693),
				new Stand().withName("Mirrorshards").withType(StandType.ARTIST).withLocationName("b11-12").withImageX(602).withImageY(603),
				new Stand().withName("Sweetie's Stand!").withType(StandType.ARTIST).withLocationName("b13-14").withImageX(602).withImageY(488),
				new Stand().withName("אלה וטושה").withType(StandType.ARTIST).withLocationName("b15").withImageX(656).withImageY(400),
				new Stand().withName("דוכן האמן של Catswing").withType(StandType.ARTIST).withLocationName("b16").withImageX(722).withImageY(400),
				new Stand().withName("דוכן האמן של ליאן").withType(StandType.ARTIST).withLocationName("b17").withImageX(778).withImageY(400),
				new Stand().withName("Xylo").withType(StandType.ARTIST).withLocationName("b18").withImageX(834).withImageY(400),
				new Stand().withName("מריונטה").withType(StandType.ARTIST).withLocationName("b19-20").withImageX(1503).withImageY(592),
				new Stand().withName("Q_keshi").withType(StandType.ARTIST).withLocationName("b21").withImageX(1503).withImageY(681),
				new Stand().withName("Kartzi's Doodles").withType(StandType.ARTIST).withLocationName("b22").withImageX(1503).withImageY(741),
				new Stand().withName("הדוכן של Devo").withType(StandType.ARTIST).withLocationName("b23").withImageX(1503).withImageY(796),
				new Stand().withName("Smatan Gold &Cherry Maki").withType(StandType.ARTIST).withLocationName("b24").withImageX(1437).withImageY(860),
				new Stand().withName("קבוצת יצירת קומיקס").withType(StandType.ARTIST).withLocationName("b25").withImageX(1380).withImageY(860),
				new Stand().withName("MaikelKing").withType(StandType.ARTIST).withLocationName("b26").withImageX(1323).withImageY(860),
				new Stand().withName("OdeChan Art").withType(StandType.ARTIST).withLocationName("b27").withImageX(1257).withImageY(860),
				new Stand().withName("ohmilkt").withType(StandType.ARTIST).withLocationName("b28").withImageX(1198).withImageY(694),
				new Stand().withName("Fishiebug").withType(StandType.ARTIST).withLocationName("b29-30").withImageX(1198).withImageY(604),
				new Stand().withName("אלכס ושירה").withType(StandType.ARTIST).withLocationName("b31-32").withImageX(1198).withImageY(486),
				new Stand().withName("מאיירת מציאות").withType(StandType.ARTIST).withLocationName("b33").withImageX(1262).withImageY(400),
				new Stand().withName("Sony’s art").withType(StandType.ARTIST).withLocationName("b34").withImageX(1318).withImageY(400),
				new Stand().withName("DORINDRAWS").withType(StandType.ARTIST).withLocationName("b35").withImageX(1377).withImageY(400),
				new Stand().withName("FOXISDRAWING").withType(StandType.ARTIST).withLocationName("b36").withImageX(1437).withImageY(400),
				new Stand().withName("הדוכן השמור של דוד צור").withType(StandType.ARTIST).withLocationName("b37").withImageX(1862).withImageY(400),
				new Stand().withName("rexpan").withType(StandType.ARTIST).withLocationName("b38").withImageX(1918).withImageY(400),
				new Stand().withName("Gil Eilat").withType(StandType.ARTIST).withLocationName("b39").withImageX(1980).withImageY(400),
				new Stand().withName("Meiiior").withType(StandType.ARTIST).withLocationName("b40").withImageX(2038).withImageY(400),
				new Stand().withName("orchibald art").withType(StandType.ARTIST).withLocationName("b41-42").withImageX(2100).withImageY(488),
				new Stand().withName("shir.k.art").withType(StandType.ARTIST).withLocationName("b43-44").withImageX(2100).withImageY(603),
				new Stand().withName("ozart").withType(StandType.ARTIST).withLocationName("b45-46").withImageX(2100).withImageY(722),
				new Stand().withName("natartsy").withType(StandType.ARTIST).withLocationName("b47").withImageX(1978).withImageY(860),
				new Stand().withName("animlilo").withType(StandType.ARTIST).withLocationName("b48-49").withImageX(1894).withImageY(860),
				new Stand().withName("Golden Cookie").withType(StandType.ARTIST).withLocationName("b50").withImageX(1800).withImageY(860),
				new Stand().withName("דובון").withType(StandType.ARTIST).withLocationName("b51").withImageX(1743).withImageY(652),
				new Stand().withName("Little Artshop Of Horrors").withType(StandType.ARTIST).withLocationName("b52").withImageX(1743).withImageY(592),
				new Stand().withName("Mageeka").withType(StandType.ARTIST).withLocationName("b53").withImageX(1743).withImageY(536),
				new Stand().withName("Yair Sasson Art").withType(StandType.ARTIST).withLocationName("b54").withImageX(1743).withImageY(476)
		);
	}

	@Override
	public SurveySender getEventVoteSender(final ConventionEvent event) {
		if (event.getUserInput().getVoteSurvey() == null) {
			return null;
		}
//        try {
//            if (event.getServerId() == EVENT_ID_AMAIDOL) {
//                SurveyForm form = new SurveyForm()
//                        .withQuestionEntry(QUESTION_ID_AMAIDOL_NAME, "entry.109802680")
//                        .withQuestionEntry(QUESTION_ID_AMAIDOL_VOTE, "entry.1600353678")
//                        .withSendUrl(new URL("https://docs.google.com/forms/d/e/1FAIpQLSf3BlH3jonMmQ-eQo1MeQ76s31ak2824eJVsMcl6IlqueqEDw/formResponse"));
//
//                SurveyDataRetriever.DisabledMessage disabledMessageRetriever = new SurveyDataRetriever.GoogleSpreadSheet(AMAIDOL_SPREADSHEET_ID);
//
//                return new EventVoteSurveyFormSender(form, event.getUserInput().getVoteSurvey(), disabledMessageRetriever);
//
//            }
//        } catch (MalformedURLException e) {
//            throw new RuntimeException(e);
//        }
		return super.getEventVoteSender(event);
	}

	@Override
	@Nullable
	public SurveyDataRetriever.Answers createSurveyAnswersRetriever(FeedbackQuestion question) {
//        switch (question.getQuestionId()) {
//            case QUESTION_ID_AMAIDOL_VOTE: {
//                return new SurveyDataRetriever.GoogleSpreadSheet(AMAIDOL_SPREADSHEET_ID);
//            }
//        }

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

//        if (userInput.getVoteSurvey() == null && event != null) {
//            if (event.getServerId() == EVENT_ID_AMAIDOL) {
//                userInput.setVoteSurvey(new Survey().withQuestions(
//                        new FeedbackQuestion(QUESTION_ID_AMAIDOL_NAME, FeedbackQuestion.AnswerType.SINGLE_LINE_TEXT, true),
//                        new FeedbackQuestion(QUESTION_ID_AMAIDOL_VOTE, FeedbackQuestion.AnswerType.MULTIPLE_ANSWERS_RADIO, true)
//                ));
//            }
//        }
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

package amai.org.conventions.model.conventions;

import android.text.TextUtils;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

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
import amai.org.conventions.utils.CollectionUtils;
import amai.org.conventions.utils.ConventionStorage;
import amai.org.conventions.utils.Dates;
import sff.org.conventions.R;

public class Icon2017Convention extends SffConvention {
	private static final String HALL_NAME_CINEMATHEQUE_1 = "סינמטק 1";
	private static final String HALL_NAME_CINEMATHEQUE_2 = "סינמטק 2";
	private static final String HALL_NAME_CINEMATHEQUE_5 = "סינמטק 5";
	private static final String HALL_NAME_ESHKOL_1 = "אשכול 1";
	private static final String HALL_NAME_ESHKOL_2 = "אשכול 2";
	private static final String HALL_NAME_ESHKOL_3 = "אשכול 3";
	private static final String HALL_NAME_ESHKOL_4 = "אשכול 4";
	private static final String HALL_NAME_ESHKOL_5 = "אשכול 5";
	private static final String HALL_NAME_ESHKOL_6 = "אשכול 6";
	private static final String HALL_NAME_WORKSHOPS_1 = "חדר סדנאות 1";
	private static final String HALL_NAME_WORKSHOPS_2 = "חדר סדנאות 2";
	private static final String HALL_NAME_KIDS = "חדר ילדים";
	private static final String HALL_NAME_MEETINGS = "אוהל מפגשים";
	private static final String HALL_NAME_OUTSIDE = "אירועי חוצות";
	private static final String HALL_NAME_IRONI_1 = "עירוני 1";
	private static final String HALL_NAME_IRONI_2 = "עירוני 2";
	private static final String HALL_NAME_IRONI_3 = "עירוני 3";
	private static final String HALL_NAME_IRONI_4 = "עירוני 4";
	private static final String HALL_NAME_IRONI_5 = "עירוני 5";
	private static final String HALL_NAME_IRONI_6 = "עירוני 6";
	private static final String HALL_NAME_IRONI_7 = "עירוני 7";
	private static final String HALL_NAME_IRONI_8 = "עירוני 8";
	private static final String HALL_NAME_IRONI_9 = "עירוני 9";
	private static final String HALL_NAME_IRONI_10 = "עירוני 10";
	private static final String HALL_NAME_IRONI_11 = "עירוני 11";
	private static final String HALL_NAME_IRONI_12 = "עירוני 12";
	private static final String HALL_NAME_IRONI_13 = "עירוני 13";
	private static final String HALL_NAME_IRONI_14 = "עירוני 14";
	private static final String HALL_NAME_IRONI_15 = "עירוני 15";

	@Override
	protected ConventionStorage initStorage() {
		return new ConventionStorage(this, R.raw.icon2017_convention_events, 1);
	}

	@Override
	protected Calendar initStartDate() {
		return Dates.createDate(2017, Calendar.OCTOBER, 8);
	}

	@Override
	protected Calendar initEndDate() {
		return Dates.createDate(2017, Calendar.OCTOBER, 10);
	}

	@Override
	protected String initID() {
		return "Icon2017";
	}

	@Override
	protected String initDisplayName() {
		return "פסטיבל אייקון 2017";
	}

	@Override
	protected Halls initHalls() {
		return new Halls(Arrays.asList(
				new Hall().withName(HALL_NAME_CINEMATHEQUE_1).withOrder(1),
				new Hall().withName(HALL_NAME_CINEMATHEQUE_2).withOrder(2),
				new Hall().withName(HALL_NAME_CINEMATHEQUE_5).withOrder(3),
				new Hall().withName(HALL_NAME_ESHKOL_1).withOrder(4),
				new Hall().withName(HALL_NAME_ESHKOL_2).withOrder(5),
				new Hall().withName(HALL_NAME_ESHKOL_3).withOrder(6),
				new Hall().withName(HALL_NAME_ESHKOL_4).withOrder(7),
				new Hall().withName(HALL_NAME_ESHKOL_5).withOrder(8),
				new Hall().withName(HALL_NAME_ESHKOL_6).withOrder(9),
				new Hall().withName(HALL_NAME_WORKSHOPS_1).withOrder(10),
				new Hall().withName(HALL_NAME_WORKSHOPS_2).withOrder(11),
				new Hall().withName(HALL_NAME_KIDS).withOrder(12),
				new Hall().withName(HALL_NAME_MEETINGS).withOrder(13),
				new Hall().withName(HALL_NAME_OUTSIDE).withOrder(14),
				new Hall().withName(HALL_NAME_IRONI_1).withOrder(15),
				new Hall().withName(HALL_NAME_IRONI_2).withOrder(16),
				new Hall().withName(HALL_NAME_IRONI_3).withOrder(17),
				new Hall().withName(HALL_NAME_IRONI_4).withOrder(18),
				new Hall().withName(HALL_NAME_IRONI_5).withOrder(19),
				new Hall().withName(HALL_NAME_IRONI_6).withOrder(20),
				new Hall().withName(HALL_NAME_IRONI_7).withOrder(21),
				new Hall().withName(HALL_NAME_IRONI_8).withOrder(22),
				new Hall().withName(HALL_NAME_IRONI_9).withOrder(23),
				new Hall().withName(HALL_NAME_IRONI_10).withOrder(24),
				new Hall().withName(HALL_NAME_IRONI_11).withOrder(25),
				new Hall().withName(HALL_NAME_IRONI_12).withOrder(26),
				new Hall().withName(HALL_NAME_IRONI_13).withOrder(27),
				new Hall().withName(HALL_NAME_IRONI_14).withOrder(28),
				new Hall().withName(HALL_NAME_IRONI_15).withOrder(29)
		));
	}

	@Override
	protected ConventionMap initMap() {
		Hall cinematheque1 = getHalls().findByName(HALL_NAME_CINEMATHEQUE_1);
		Hall cinematheque2 = getHalls().findByName(HALL_NAME_CINEMATHEQUE_2);
		Hall cinematheque5 = getHalls().findByName(HALL_NAME_CINEMATHEQUE_5);
		Hall eshkol1 = getHalls().findByName(HALL_NAME_ESHKOL_1);
		Hall eshkol2 = getHalls().findByName(HALL_NAME_ESHKOL_2);
		Hall eshkol3 = getHalls().findByName(HALL_NAME_ESHKOL_3);
		Hall eshkol4 = getHalls().findByName(HALL_NAME_ESHKOL_4);
		Hall eshkol5 = getHalls().findByName(HALL_NAME_ESHKOL_5);
		Hall eshkol6 = getHalls().findByName(HALL_NAME_ESHKOL_6);
		Hall workshops1 = getHalls().findByName(HALL_NAME_WORKSHOPS_1);
		Hall workshops2 = getHalls().findByName(HALL_NAME_WORKSHOPS_2);
		Hall kids = getHalls().findByName(HALL_NAME_KIDS);
		Hall meetings = getHalls().findByName(HALL_NAME_MEETINGS);
		Hall outside = getHalls().findByName(HALL_NAME_OUTSIDE);
		Hall ironi1 = getHalls().findByName(HALL_NAME_IRONI_1);
		Hall ironi2 = getHalls().findByName(HALL_NAME_IRONI_2);
		Hall ironi3 = getHalls().findByName(HALL_NAME_IRONI_3);
		Hall ironi4 = getHalls().findByName(HALL_NAME_IRONI_4);
		Hall ironi5 = getHalls().findByName(HALL_NAME_IRONI_5);
		Hall ironi6 = getHalls().findByName(HALL_NAME_IRONI_6);
		Hall ironi7 = getHalls().findByName(HALL_NAME_IRONI_7);
		Hall ironi8 = getHalls().findByName(HALL_NAME_IRONI_8);
		Hall ironi9 = getHalls().findByName(HALL_NAME_IRONI_9);
		Hall ironi10 = getHalls().findByName(HALL_NAME_IRONI_10);
		Hall ironi11 = getHalls().findByName(HALL_NAME_IRONI_11);
		Hall ironi12 = getHalls().findByName(HALL_NAME_IRONI_12);
		Hall ironi13 = getHalls().findByName(HALL_NAME_IRONI_13);
		Hall ironi14 = getHalls().findByName(HALL_NAME_IRONI_14);
		Hall ironi15 = getHalls().findByName(HALL_NAME_IRONI_15);

		Floor floor = new Floor(1)
				.withName("מפת התמצאות")
				.withImageResource(R.drawable.icon2017_map, false)
				.withImageHeight(2640)
				.withImageWidth(2040)
				.withDefaultMarkerHeight(100);
		final int BIG_MARKER_HEIGHT = 200;
		final int SMALL_MARKER_HEIGHT = 70;

		return new ConventionMap()
				.withFloors(Collections.singletonList(floor))
				.withLocations(
						CollectionUtils.flattenList(
								inFloor(floor,
										new MapLocation()
												.withPlace(new Place().withName("כניסה מרחוב ליאונרדו דה וינצ'י"))
												.withMarkerResource(R.drawable.ic_action_place, false)
												.withSelectedMarkerResource(R.drawable.ic_action_place_green_dark, false)
												.withX(1510)
												.withY(2423),
										new MapLocation()
												.withPlace(new Place().withName("מודיעין"))
												.withMarkerResource(R.drawable.ic_action_place, false)
												.withSelectedMarkerResource(R.drawable.ic_action_place_green_dark, false)
												.withX(1729)
												.withY(2395),
										new MapLocation()
												.withPlace(new Place().withName("דוכנים"))
												.withMarkerResource(R.drawable.ic_action_place, false)
												.withSelectedMarkerResource(R.drawable.ic_action_place_green_dark, false)
												.withX(507)
												.withY(2393),
										new MapLocation()
												.withPlace(new Place().withName("מיניאטורות"))
												.withMarkerResource(R.drawable.ic_action_place, false)
												.withSelectedMarkerResource(R.drawable.ic_action_place_green_dark, false)
												.withX(1813)
												.withY(2100),
										new MapLocation()
												.withPlace(new Place().withName("מיניאטורות"))
												.withMarkerResource(R.drawable.ic_action_place, false)
												.withSelectedMarkerResource(R.drawable.ic_action_place_green_dark, false)
												.withX(1164)
												.withY(2204),
										new MapLocation()
												.withPlace(new Place().withName("דוכנים"))
												.withMarkerResource(R.drawable.ic_action_place, false)
												.withSelectedMarkerResource(R.drawable.ic_action_place_green_dark, false)
												.withX(736)
												.withY(2242),
										new MapLocation()
												.withPlace(new Place().withName("דוכנים"))
												.withMarkerResource(R.drawable.ic_action_place, false)
												.withSelectedMarkerResource(R.drawable.ic_action_place_green_dark, false)
												.withX(324)
												.withY(2205),
										new MapLocation()
												.withPlace(new Place().withName("דוכנים"))
												.withMarkerResource(R.drawable.ic_action_place, false)
												.withSelectedMarkerResource(R.drawable.ic_action_place_green_dark, false)
												.withX(1550)
												.withY(2134),
										new MapLocation()
												.withName("מדרגות לעלייה לאשכול 3-4")
												.withPlaces(Arrays.asList(eshkol3, eshkol4))
												.withMarkerResource(R.drawable.ic_action_place, false)
												.withSelectedMarkerResource(R.drawable.ic_action_place_green_dark, false)
												.withX(1934)
												.withY(1952),
										new MapLocation()
												.withName("מדרגות לעלייה לאשכול 5-6")
												.withPlaces(Arrays.asList(eshkol5, eshkol6))
												.withMarkerResource(R.drawable.ic_action_place, false)
												.withSelectedMarkerResource(R.drawable.ic_action_place_green_dark, false)
												.withX(1140)
												.withY(1952),
										new MapLocation()
												.withPlace(new Place().withName("שירותי בנים"))
												.withMarkerResource(R.drawable.ic_action_place, false)
												.withSelectedMarkerResource(R.drawable.ic_action_place_green_dark, false)
												.withX(1938)
												.withY(1796)
												.withMarkerHeight(SMALL_MARKER_HEIGHT),
										new MapLocation()
												.withPlace(new Place().withName("דוכנים"))
												.withMarkerResource(R.drawable.ic_action_place, false)
												.withSelectedMarkerResource(R.drawable.ic_action_place_green_dark, false)
												.withX(1550)
												.withY(1799),
										new MapLocation()
												.withPlace(new Place().withName("שירותי בנות"))
												.withMarkerResource(R.drawable.ic_action_place, false)
												.withSelectedMarkerResource(R.drawable.ic_action_place_green_dark, false)
												.withX(1135)
												.withY(1796)
												.withMarkerHeight(SMALL_MARKER_HEIGHT),
										new MapLocation()
												.withPlace(eshkol1)
												.withMarkerResource(R.drawable.ic_action_place, false)
												.withSelectedMarkerResource(R.drawable.ic_action_place_green_dark, false)
												.withX(1546)
												.withY(1538)
												.withMarkerHeight(BIG_MARKER_HEIGHT),
										new MapLocation()
												.withPlace(eshkol2)
												.withMarkerResource(R.drawable.ic_action_place, false)
												.withSelectedMarkerResource(R.drawable.ic_action_place_green_dark, false)
												.withX(1882)
												.withY(1538)
												.withMarkerHeight(BIG_MARKER_HEIGHT),
										new MapLocation()
												.withPlace(new Place().withName("מתחם יד שניה"))
												.withMarkerResource(R.drawable.ic_action_place, false)
												.withSelectedMarkerResource(R.drawable.ic_action_place_green_dark, false)
												.withX(1189)
												.withY(1538),
										new MapLocation()
												.withPlace(new Place().withName("עמדת משחקי שער"))
												.withMarkerResource(R.drawable.ic_action_place, false)
												.withSelectedMarkerResource(R.drawable.ic_action_place_green_dark, false)
												.withX(475)
												.withY(1211),
										new MapLocation()
												.withPlace(new Place().withName("כניסה לעירוני"))
												.withMarkerResource(R.drawable.ic_action_place, false)
												.withSelectedMarkerResource(R.drawable.ic_action_place_green_dark, false)
												.withX(173)
												.withY(2205),
										new MapLocation()
												.withPlace(new Place().withName("סוכה"))
												.withMarkerResource(R.drawable.ic_action_place, false)
												.withSelectedMarkerResource(R.drawable.ic_action_place_green_dark, false)
												.withX(51)
												.withY(1993),
										new MapLocation()
												.withPlace(new Place().withName("שירותים"))
												.withMarkerResource(R.drawable.ic_action_place, false)
												.withSelectedMarkerResource(R.drawable.ic_action_place_green_dark, false)
												.withX(186)
												.withY(1994)
												.withMarkerHeight(SMALL_MARKER_HEIGHT),
										new MapLocation()
												.withPlace(new Place().withName("שירותים"))
												.withMarkerResource(R.drawable.ic_action_place, false)
												.withSelectedMarkerResource(R.drawable.ic_action_place_green_dark, false)
												.withX(186)
												.withY(1921)
												.withMarkerHeight(SMALL_MARKER_HEIGHT),
										new MapLocation()
												.withPlace(workshops1)
												.withMarkerResource(R.drawable.ic_action_place, false)
												.withSelectedMarkerResource(R.drawable.ic_action_place_green_dark, false)
												.withX(186)
												.withY(1838)
												.withMarkerHeight(SMALL_MARKER_HEIGHT),
										new MapLocation()
												.withPlace(kids)
												.withMarkerResource(R.drawable.ic_action_place, false)
												.withSelectedMarkerResource(R.drawable.ic_action_place_green_dark, false)
												.withX(186)
												.withY(1681),
										new MapLocation()
												.withPlace(new Place().withName("הוביטון"))
												.withMarkerResource(R.drawable.ic_action_place, false)
												.withSelectedMarkerResource(R.drawable.ic_action_place_green_dark, false)
												.withX(186)
												.withY(1435),
										new MapLocation()
												.withPlace(new Place().withName("שירותים"))
												.withMarkerResource(R.drawable.ic_action_place, false)
												.withSelectedMarkerResource(R.drawable.ic_action_place_green_dark, false)
												.withX(199)
												.withY(1076)
												.withMarkerHeight(SMALL_MARKER_HEIGHT),
										new MapLocation()
												.withPlace(new Place().withName("שירותים"))
												.withMarkerResource(R.drawable.ic_action_place, false)
												.withSelectedMarkerResource(R.drawable.ic_action_place_green_dark, false)
												.withX(199)
												.withY(990)
												.withMarkerHeight(SMALL_MARKER_HEIGHT),
										new MapLocation()
												.withPlace(new Place().withName("חדר קוספליי 1"))
												.withMarkerResource(R.drawable.ic_action_place, false)
												.withSelectedMarkerResource(R.drawable.ic_action_place_green_dark, false)
												.withX(199)
												.withY(913)
												.withMarkerHeight(SMALL_MARKER_HEIGHT),
										new MapLocation()
												.withPlace(new Place().withName("חדר קוספליי 2"))
												.withMarkerResource(R.drawable.ic_action_place, false)
												.withSelectedMarkerResource(R.drawable.ic_action_place_green_dark, false)
												.withX(199)
												.withY(755),
										new MapLocation()
												.withPlace(new Place().withName("שמירת חפצים"))
												.withMarkerResource(R.drawable.ic_action_place, false)
												.withSelectedMarkerResource(R.drawable.ic_action_place_green_dark, false)
												.withX(199)
												.withY(590),
										new MapLocation()
												.withPlace(new Place().withName("שמירת חפצים"))
												.withMarkerResource(R.drawable.ic_action_place, false)
												.withSelectedMarkerResource(R.drawable.ic_action_place_green_dark, false)
												.withX(199)
												.withY(426),
										new MapLocation()
												.withName("כניסה לעירוני")
												.withPlaces(Arrays.asList(workshops2, ironi1, ironi2, ironi3, ironi4, ironi5, ironi6, ironi7, ironi8, ironi9, ironi10, ironi11, ironi12, ironi13, ironi14, ironi15))
												.withMarkerResource(R.drawable.ic_action_place, false)
												.withSelectedMarkerResource(R.drawable.ic_action_place_green_dark, false)
												.withX(576)
												.withY(970),
										new MapLocation()
												.withPlace(new Place().withName("כניסה נגישה לעירוני"))
												.withMarkerResource(R.drawable.ic_action_place, false)
												.withSelectedMarkerResource(R.drawable.ic_action_place_green_dark, false)
												.withX(46)
												.withY(1149),
										new MapLocation()
												.withPlace(new Place().withName("דוכנים"))
												.withMarkerResource(R.drawable.ic_action_place, false)
												.withSelectedMarkerResource(R.drawable.ic_action_place_green_dark, false)
												.withX(1147)
												.withY(1045),
										new MapLocation()
												.withPlace(new Place().withName("הפונדק החי"))
												.withMarkerResource(R.drawable.ic_action_place, false)
												.withSelectedMarkerResource(R.drawable.ic_action_place_green_dark, false)
												.withX(1890)
												.withY(901),
										new MapLocation()
												.withPlace(new Place().withName("הקולוסיאום"))
												.withMarkerResource(R.drawable.ic_action_place, false)
												.withSelectedMarkerResource(R.drawable.ic_action_place_green_dark, false)
												.withX(1574)
												.withY(901),
										new MapLocation()
												.withPlace(outside)
												.withMarkerResource(R.drawable.ic_action_place, false)
												.withSelectedMarkerResource(R.drawable.ic_action_place_green_dark, false)
												.withX(1316)
												.withY(831),
										new MapLocation()
												.withPlace(new Place().withName("כניסה מרחוב הארבעה"))
												.withMarkerResource(R.drawable.ic_action_place, false)
												.withSelectedMarkerResource(R.drawable.ic_action_place_green_dark, false)
												.withX(1982)
												.withY(649),
										new MapLocation()
												.withPlace(new Place().withName("מודיעין"))
												.withMarkerResource(R.drawable.ic_action_place, false)
												.withSelectedMarkerResource(R.drawable.ic_action_place_green_dark, false)
												.withX(1926)
												.withY(353),
										new MapLocation()
												.withPlace(new Place().withName("אבידות ומציאות"))
												.withMarkerResource(R.drawable.ic_action_place, false)
												.withSelectedMarkerResource(R.drawable.ic_action_place_green_dark, false)
												.withX(1966)
												.withY(211),
										new MapLocation()
												.withPlace(new Place().withName("קופות"))
												.withMarkerResource(R.drawable.ic_action_place, false)
												.withSelectedMarkerResource(R.drawable.ic_action_place_green_dark, false)
												.withX(1614)
												.withY(423),
										new MapLocation()
												.withPlace(new Place().withName("האגודה למד\"ב ולפנטסיה"))
												.withMarkerResource(R.drawable.ic_action_place, false)
												.withSelectedMarkerResource(R.drawable.ic_action_place_green_dark, false)
												.withX(1238)
												.withY(363),
										new MapLocation()
												.withName("מפגשים, הזמנות מראש, השקות וקהילה")
												.withPlace(meetings)
												.withMarkerResource(R.drawable.ic_action_place, false)
												.withSelectedMarkerResource(R.drawable.ic_action_place_green_dark, false)
												.withX(960)
												.withY(423),
										new MapLocation()
												.withPlace(new Place().withName("אולם הספורט"))
												.withMarkerResource(R.drawable.ic_action_place, false)
												.withSelectedMarkerResource(R.drawable.ic_action_place_green_dark, false)
												.withX(658)
												.withY(247),
										new MapLocation()
												.withPlace(new Place().withName("מתחם משחקי לוח"))
												.withMarkerResource(R.drawable.ic_action_place, false)
												.withSelectedMarkerResource(R.drawable.ic_action_place_green_dark, false)
												.withX(285)
												.withY(161),
										new MapLocation()
												.withName("לסינמטק תל אביב")
												.withPlaces(Arrays.asList(cinematheque1, cinematheque2, cinematheque5))
												.withMarkerResource(R.drawable.ic_action_place, false)
												.withSelectedMarkerResource(R.drawable.ic_action_place_green_dark, false)
												.withX(1755)
												.withY(29)
												.withMarkerHeight(BIG_MARKER_HEIGHT)
								)
						)
				);
	}

	@Override
	protected double initLongitude() {
		return 34.7845003;
	}

	@Override
	protected double initLatitude() {
		return 32.0707265;
	}

	@Override
	protected ImageIdToImageResourceMapper initImageMapper() {
		ImageIdToImageResourceMapper imageMapper = new ImageIdToImageResourceMapper();

		imageMapper.addMapping(ImageIdToImageResourceMapper.EVENT_GENERIC, R.drawable.icon2017_event_background);

		return imageMapper;
	}

	@Override
	protected String initFeedbackRecipient() {
		return "sff.conventions.feedback@gmail.com";
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
					.withSendUrl(new URL("https://docs.google.com/forms/d/e/1FAIpQLSdb34x3Nb_E3gQQ_SBZTVMsnro_VzlKwP__uK3YRVQj-s5qyA/formResponse"));
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
					.withSendUrl(new URL("https://docs.google.com/forms/d/e/1FAIpQLSe1lM51MKmn_g943DlKqdRJj_d6PWv8Y2ODf4sm9ALYwmPI8A/formResponse"));
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
		return feedbackForm;
	}

	@Override
	protected URL initModelURL() {
		try {
			return new URL("https://api.sf-f.org.il/program/list_events.php?slug=icon2017");
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	protected URL initTicketsLastUpdateURL() {
		try {
			return new URL("https://api.sf-f.org.il/program/cache_get_last_updated.php?which=available_tickets&slug=icon2017");
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	protected URL initUpdatesURL() {
		try {
			return new URL("https://api.sf-f.org.il/announcements/get.php?slug=icon2017"); // use test_con for tests
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public URL getEventTicketsNumberURL(ConventionEvent event) {
		try {
			return new URL("https://api.sf-f.org.il/program/available_tickets_per_event.php?slug=icon2017&id=" + event.getServerId()); // use test_con for tests
		} catch (MalformedURLException e) {
			return null;
		}
	}

	@Override
	public URL getSecondHandFormURL(String id) {
		try {
			return new URL("https://calm-dawn-51174.herokuapp.com/getAllItemsByForm?itemFormId=" + URLEncoder.encode(id, "UTF-8"));
		} catch (MalformedURLException|UnsupportedEncodingException e) {
			return null;
		}
	}

	@Override
	public URL getSecondHandFormsURL(List<String> ids) {
		try {
			String idsParam = TextUtils.join(",", CollectionUtils.map(ids, new CollectionUtils.Mapper<String, String>() {
				@Override
				public String map(String item) {
					try {
						return URLEncoder.encode(item, "UTF-8");
					} catch (UnsupportedEncodingException e) {
						throw new RuntimeException(e);
					}
				}
			}));
			return new URL("https://calm-dawn-51174.herokuapp.com/getMultipleItemsByForm?itemFormIds=" + idsParam);
		} catch (MalformedURLException|RuntimeException e) {
			return null;
		}
	}

	@Override
	public ConventionEvent findEventByURL(String url) {
		// Event URLs in event descriptions can point to an alternate URL
		if (url.startsWith("http://program.iconfestival.org.il/")) {
			url = "http://iconfestival.com/" + url.substring("http://program.iconfestival.org.il/".length());
		}
		return super.findEventByURL(url);
	}
}

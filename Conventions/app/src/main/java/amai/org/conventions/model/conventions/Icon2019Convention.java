package amai.org.conventions.model.conventions;

import android.text.TextUtils;

import java.io.BufferedWriter;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import amai.org.conventions.feedback.forms.EventFeedbackForm;
import amai.org.conventions.feedback.forms.FeedbackForm;
import amai.org.conventions.model.ConventionEvent;
import amai.org.conventions.model.ConventionMap;
import amai.org.conventions.model.FeedbackQuestion;
import amai.org.conventions.model.Hall;
import amai.org.conventions.model.Halls;
import amai.org.conventions.model.ImageIdToImageResourceMapper;
import amai.org.conventions.model.SecondHandItem;
import amai.org.conventions.utils.CollectionUtils;
import amai.org.conventions.utils.ConventionStorage;
import amai.org.conventions.utils.Dates;
import amai.org.conventions.utils.HttpConnectionCreator;
import sff.org.conventions.R;

public class Icon2019Convention extends SffConvention {
	private static final String HALL_NAME_CINEMATHEQUE_1_3_4 = "סינמטק 1, 3, 4";
	private static final String HALL_NAME_CINEMATHEQUE_2 = "סינמטק 2";
	private static final String HALL_NAME_CINEMATHEQUE_5 = "סינמטק 5";
	private static final String HALL_NAME_ESHKOL_1 = "אשכול 1";
	private static final String HALL_NAME_ESHKOL_2 = "אשכול 2";
	private static final String HALL_NAME_ESHKOL_3 = "אשכול 3";
	private static final String HALL_NAME_ESHKOL_4 = "אשכול 4";
	private static final String HALL_NAME_ESHKOL_5 = "אשכול 5";
	private static final String HALL_NAME_ESHKOL_6 = "אשכול 6";
	private static final String HALL_NAME_WORKSHOPS_1 = "סדנאות 1";
	private static final String HALL_NAME_WORKSHOPS_2 = "סדנאות 2";
	private static final String HALL_NAME_KIDS = "חדר ילדים";
	private static final String HALL_NAME_MEETINGS = "חדר מפגשים";
	private static final String HALL_NAME_OUTSIDE = "אירועי חוצות";
	private static final String HALL_NAME_TENT_1 = "אוהל 1";
	private static final String HALL_NAME_TENT_2 = "אוהל 2";
	private static final String HALL_NAME_TENT_3 = "אוהל 3";
	private static final String HALL_NAME_TENT_4 = "אוהל 4";
	private static final String HALL_NAME_TENT_5 = "אוהל 5";
	private static final String HALL_NAME_TENT_6 = "אוהל 6";
	private static final String HALL_NAME_TENT_7 = "אוהל 7";
	private static final String HALL_NAME_TENT_8 = "אוהל 8";
	private static final String HALL_NAME_IRONI_1 = "עירוני 1";
	private static final String HALL_NAME_IRONI_2 = "עירוני 2";
	private static final String HALL_NAME_IRONI_3 = "עירוני 3";
	private static final String HALL_NAME_IRONI_4 = "עירוני 4";
	private static final String HALL_NAME_IRONI_5 = "עירוני 5";
	private static final String HALL_NAME_IRONI_6 = "עירוני 6";
	private static final String HALL_NAME_IRONI_7 = "עירוני 7";
	private static final String HALL_NAME_ARTEMIS = "ארטמיס";
	private static final String HALL_NAME_MINIATURES_1 = "מיניאטורות 1";
	private static final String HALL_NAME_MINIATURES_2 = "מיניאטורות 2";
	private static final String HALL_NAME_MINIATURES_DEMO = "הדגמות מיניאטורות";
	private static final String HALL_NAME_MINIATURES_WORKSHOP = "סדנאות מיניאטורות";

	private static final String API_SLUG = "icon2019";
	private static final String TEST_API_SLUG = "test_con";
	private static final String YAD2_API = "https://api.sf-f.org.il/yad2/";
	private static final String TEST_YAD2_API = "https://test.api.sf-f.org.il/yad2/";

	@Override
	protected ConventionStorage initStorage() {
		return new ConventionStorage(this, R.raw.icon2019_convention_events, 0);
	}

	@Override
	protected Calendar initStartDate() {
		return Dates.createDate(2019, Calendar.OCTOBER, 15);
	}

	@Override
	protected Calendar initEndDate() {
		return Dates.createDate(2019, Calendar.OCTOBER, 17);
	}

	@Override
	protected String initID() {
		return "Icon2019";
	}

	@Override
	protected String initDisplayName() {
		return "פסטיבל אייקון 2019";
	}

	@Override
	protected Halls initHalls() {
		List<Hall> halls = Arrays.asList(
				new Hall().withName(HALL_NAME_CINEMATHEQUE_1_3_4),
				new Hall().withName(HALL_NAME_CINEMATHEQUE_2),
				new Hall().withName(HALL_NAME_CINEMATHEQUE_5),
				new Hall().withName(HALL_NAME_ESHKOL_1),
				new Hall().withName(HALL_NAME_ESHKOL_2),
				new Hall().withName(HALL_NAME_ESHKOL_3),
				new Hall().withName(HALL_NAME_ESHKOL_4),
				new Hall().withName(HALL_NAME_ESHKOL_5),
				new Hall().withName(HALL_NAME_ESHKOL_6),
				new Hall().withName(HALL_NAME_WORKSHOPS_1),
				new Hall().withName(HALL_NAME_WORKSHOPS_2),
				new Hall().withName(HALL_NAME_KIDS),
				new Hall().withName(HALL_NAME_MEETINGS),
				new Hall().withName(HALL_NAME_OUTSIDE),
				new Hall().withName(HALL_NAME_TENT_1),
				new Hall().withName(HALL_NAME_TENT_2),
				new Hall().withName(HALL_NAME_TENT_3),
				new Hall().withName(HALL_NAME_TENT_4),
				new Hall().withName(HALL_NAME_TENT_5),
				new Hall().withName(HALL_NAME_TENT_6),
				new Hall().withName(HALL_NAME_TENT_7),
				new Hall().withName(HALL_NAME_TENT_8),
				new Hall().withName(HALL_NAME_IRONI_1),
				new Hall().withName(HALL_NAME_IRONI_2),
				new Hall().withName(HALL_NAME_IRONI_3),
				new Hall().withName(HALL_NAME_IRONI_4),
				new Hall().withName(HALL_NAME_IRONI_5),
				new Hall().withName(HALL_NAME_IRONI_6),
				new Hall().withName(HALL_NAME_IRONI_7),
				new Hall().withName(HALL_NAME_ARTEMIS),
				new Hall().withName(HALL_NAME_MINIATURES_1),
				new Hall().withName(HALL_NAME_MINIATURES_2),
				new Hall().withName(HALL_NAME_MINIATURES_DEMO),
				new Hall().withName(HALL_NAME_MINIATURES_WORKSHOP)
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
//		Hall eshkol1 = getHalls().findByName(HALL_NAME_ESHKOL_1);
//		Hall eshkol2 = getHalls().findByName(HALL_NAME_ESHKOL_2);
//		Hall eshkol3 = getHalls().findByName(HALL_NAME_ESHKOL_3);
//		Hall eshkol4 = getHalls().findByName(HALL_NAME_ESHKOL_4);
//		Hall eshkol5 = getHalls().findByName(HALL_NAME_ESHKOL_5);
//		Hall eshkol6 = getHalls().findByName(HALL_NAME_ESHKOL_6);
//		Hall meetings = getHalls().findByName(HALL_NAME_MEETINGS);
//		Hall workshops = getHalls().findByName(HALL_NAME_WORKSHOPS);
//		Hall escapeRoom = getHalls().findByName(HALL_NAME_ESCAPE_ROOM);
//
//		final int MARKER_Y_OFFSET = 0;
//		Floor floor = new Floor(1)
//				.withName("מפת מתחם")
//				.withImageResource(R.drawable.olamot2019_map, false)
//				.withImageHeight(3254)
//				.withImageWidth(2700)
//				.withDefaultMarkerHeight(100);
//		final int BIG_MARKER_HEIGHT = 200;
//		final int SMALL_MARKER_HEIGHT = 70;
//
//		int DEFAULT_MARKER = R.drawable.olamot2019_place_marker_grey;
//		int DEFAULT_SELECTED_MARKER = R.drawable.olamot2019_place_marker_blue;
		return new ConventionMap()
//				.withFloors(Collections.singletonList(floor))
//				.withLocations(
//						CollectionUtils.flattenList(
//								inFloor(floor,
//										new MapLocation()
//												.withPlace(new Place().withName("כניסה מרחוב ליאונרדו דה וינצ'י"))
//												.withMarkerResource(DEFAULT_MARKER, false)
//												.withSelectedMarkerResource(DEFAULT_SELECTED_MARKER, false)
//												.withX(2152)
//												.withY(MARKER_Y_OFFSET + 2902),
//										new MapLocation()
//												.withPlace(new Place().withName("מודיעין"))
//												.withMarkerResource(DEFAULT_MARKER, false)
//												.withSelectedMarkerResource(DEFAULT_SELECTED_MARKER, false)
//												.withX(2272)
//												.withY(MARKER_Y_OFFSET + 2796),
//										new MapLocation()
//												.withPlace(new Place().withName("פינת משחקים"))
//												.withMarkerResource(DEFAULT_MARKER, false)
//												.withSelectedMarkerResource(DEFAULT_SELECTED_MARKER, false)
//												.withX(2400)
//												.withY(MARKER_Y_OFFSET + 2498),
//										new MapLocation()
//												.withPlace(new Place().withName("דוכנים"))
//												.withMarkerResource(DEFAULT_MARKER, false)
//												.withSelectedMarkerResource(DEFAULT_SELECTED_MARKER, false)
//												.withX(2034)
//												.withY(MARKER_Y_OFFSET + 2556),
//										new MapLocation()
//												.withPlace(new Place().withName("פינת משחקים"))
//												.withMarkerResource(DEFAULT_MARKER, false)
//												.withSelectedMarkerResource(DEFAULT_SELECTED_MARKER, false)
//												.withX(1544)
//												.withY(MARKER_Y_OFFSET + 2682),
//										new MapLocation()
//												.withName("מדרגות לעלייה לאשכול 3-4")
//												.withPlaces(Arrays.asList(eshkol3, eshkol4))
//												.withMarkerResource(DEFAULT_MARKER, false)
//												.withSelectedMarkerResource(DEFAULT_SELECTED_MARKER, false)
//												.withX(2354)
//												.withY(MARKER_Y_OFFSET + 2302),
//										new MapLocation()
//												.withName("מדרגות לעלייה לאשכול 5-6")
//												.withPlaces(Arrays.asList(eshkol5, eshkol6))
//												.withMarkerResource(DEFAULT_MARKER, false)
//												.withSelectedMarkerResource(DEFAULT_SELECTED_MARKER, false)
//												.withX(1610)
//												.withY(MARKER_Y_OFFSET + 2302),
//										new MapLocation()
//												.withPlace(new Place().withName("שירותי גברים"))
//												.withMarkerResource(DEFAULT_MARKER, false)
//												.withSelectedMarkerResource(DEFAULT_SELECTED_MARKER, false)
//												.withX(2362)
//												.withY(MARKER_Y_OFFSET + 2158)
//												.withMarkerHeight(SMALL_MARKER_HEIGHT),
//										new MapLocation()
//												.withPlace(new Place().withName("דוכנים"))
//												.withMarkerResource(DEFAULT_MARKER, false)
//												.withSelectedMarkerResource(DEFAULT_SELECTED_MARKER, false)
//												.withX(2036)
//												.withY(MARKER_Y_OFFSET + 2170),
//										new MapLocation()
//												.withPlace(new Place().withName("שירותי נשים"))
//												.withMarkerResource(DEFAULT_MARKER, false)
//												.withSelectedMarkerResource(DEFAULT_SELECTED_MARKER, false)
//												.withX(1598)
//												.withY(MARKER_Y_OFFSET + 2158)
//												.withMarkerHeight(SMALL_MARKER_HEIGHT),
//										new MapLocation()
//												.withPlace(eshkol1)
//												.withMarkerResource(DEFAULT_MARKER, false)
//												.withSelectedMarkerResource(DEFAULT_SELECTED_MARKER, false)
//												.withX(1982)
//												.withY(MARKER_Y_OFFSET + 1914)
//												.withMarkerHeight(BIG_MARKER_HEIGHT),
//										new MapLocation()
//												.withPlace(eshkol2)
//												.withMarkerResource(DEFAULT_MARKER, false)
//												.withSelectedMarkerResource(DEFAULT_SELECTED_MARKER, false)
//												.withX(2390)
//												.withY(MARKER_Y_OFFSET + 1914)
//												.withMarkerHeight(BIG_MARKER_HEIGHT),
//										new MapLocation()
//												.withPlace(new Place().withName("מתחם יד שניה"))
//												.withMarkerResource(DEFAULT_MARKER, false)
//												.withSelectedMarkerResource(DEFAULT_SELECTED_MARKER, false)
//												.withX(1574)
//												.withY(MARKER_Y_OFFSET + 1914),
//										new MapLocation()
//												.withPlace(new Place().withName("דוכנים"))
//												.withMarkerResource(DEFAULT_MARKER, false)
//												.withSelectedMarkerResource(DEFAULT_SELECTED_MARKER, false)
//												.withX(1790)
//												.withY(MARKER_Y_OFFSET + 1328),
//										new MapLocation()
//												.withPlace(new Place().withName("דוכנים"))
//												.withMarkerResource(DEFAULT_MARKER, false)
//												.withSelectedMarkerResource(DEFAULT_SELECTED_MARKER, false)
//												.withX(2094)
//												.withY(MARKER_Y_OFFSET + 1166),
//										new MapLocation()
//												.withPlace(new Place().withName("דוכנים"))
//												.withMarkerResource(DEFAULT_MARKER, false)
//												.withSelectedMarkerResource(DEFAULT_SELECTED_MARKER, false)
//												.withX(1734)
//												.withY(MARKER_Y_OFFSET + 1166),
//										new MapLocation()
//												.withPlace(new Place().withName("דוכנים"))
//												.withMarkerResource(DEFAULT_MARKER, false)
//												.withSelectedMarkerResource(DEFAULT_SELECTED_MARKER, false)
//												.withX(1374)
//												.withY(MARKER_Y_OFFSET + 1166),
//										new MapLocation()
//												.withPlace(new Place().withName("כניסה מרחוב הארבעה"))
//												.withMarkerResource(DEFAULT_MARKER, false)
//												.withSelectedMarkerResource(DEFAULT_SELECTED_MARKER, false)
//												.withX(2604)
//												.withY(MARKER_Y_OFFSET + 812),
//										new MapLocation()
//												.withPlace(new Place().withName("מודיעין"))
//												.withMarkerResource(DEFAULT_MARKER, false)
//												.withSelectedMarkerResource(DEFAULT_SELECTED_MARKER, false)
//												.withX(2432)
//												.withY(MARKER_Y_OFFSET + 522),
//										new MapLocation()
//												.withPlace(new Place().withName("אבידות ומציאות"))
//												.withMarkerResource(DEFAULT_MARKER, false)
//												.withSelectedMarkerResource(DEFAULT_SELECTED_MARKER, false)
//												.withX(2480)
//												.withY(MARKER_Y_OFFSET + 346),
//										new MapLocation()
//												.withPlace(new Place().withName("קופות"))
//												.withMarkerResource(DEFAULT_MARKER, false)
//												.withSelectedMarkerResource(DEFAULT_SELECTED_MARKER, false)
//												.withX(2066)
//												.withY(MARKER_Y_OFFSET + 592),
//										new MapLocation()
//												.withPlace(new Place().withName("דוכן האגודה"))
//												.withMarkerResource(DEFAULT_MARKER, false)
//												.withSelectedMarkerResource(DEFAULT_SELECTED_MARKER, false)
//												.withX(1708)
//												.withY(MARKER_Y_OFFSET + 554),
//										new MapLocation()
//												.withPlace(new Place().withName("דוכן טולקין"))
//												.withMarkerResource(DEFAULT_MARKER, false)
//												.withSelectedMarkerResource(DEFAULT_SELECTED_MARKER, false)
//												.withX(1568)
//												.withY(MARKER_Y_OFFSET + 554),
//										new MapLocation()
//												.withPlace(new Place().withName("דוכנים"))
//												.withMarkerResource(DEFAULT_MARKER, false)
//												.withSelectedMarkerResource(DEFAULT_SELECTED_MARKER, false)
//												.withX(1148)
//												.withY(MARKER_Y_OFFSET + 2778),
//										new MapLocation()
//												.withPlace(new Place().withName("דוכנים"))
//												.withMarkerResource(DEFAULT_MARKER, false)
//												.withSelectedMarkerResource(DEFAULT_SELECTED_MARKER, false)
//												.withX(448)
//												.withY(MARKER_Y_OFFSET + 2792),
//										new MapLocation()
//												.withPlace(new Place().withName("דוכנים"))
//												.withMarkerResource(DEFAULT_MARKER, false)
//												.withSelectedMarkerResource(DEFAULT_SELECTED_MARKER, false)
//												.withX(600)
//												.withY(MARKER_Y_OFFSET + 2510),
//										new MapLocation()
//												.withPlace(new Place().withName("כניסה לעירוני"))
//												.withMarkerResource(DEFAULT_MARKER, false)
//												.withSelectedMarkerResource(DEFAULT_SELECTED_MARKER, false)
//												.withX(368)
//												.withY(MARKER_Y_OFFSET + 2584),
//										new MapLocation()
//												.withPlace(new Place().withName("דוכנים"))
//												.withMarkerResource(DEFAULT_MARKER, false)
//												.withSelectedMarkerResource(DEFAULT_SELECTED_MARKER, false)
//												.withX(142)
//												.withY(MARKER_Y_OFFSET + 2514),
//										new MapLocation()
//												.withPlace(new Place().withName("שירותים"))
//												.withMarkerResource(DEFAULT_MARKER, false)
//												.withSelectedMarkerResource(DEFAULT_SELECTED_MARKER, false)
//												.withX(440)
//												.withY(MARKER_Y_OFFSET + 2358)
//												.withMarkerHeight(SMALL_MARKER_HEIGHT),
//										new MapLocation()
//												.withPlace(new Place().withName("שירותים"))
//												.withMarkerResource(DEFAULT_MARKER, false)
//												.withSelectedMarkerResource(DEFAULT_SELECTED_MARKER, false)
//												.withX(348)
//												.withY(MARKER_Y_OFFSET + 2358)
//												.withMarkerHeight(SMALL_MARKER_HEIGHT),
//										new MapLocation()
//												.withName("מדרגות לעלייה לחדר בריחה")
//												.withPlace(escapeRoom)
//												.withMarkerResource(DEFAULT_MARKER, false)
//												.withSelectedMarkerResource(DEFAULT_SELECTED_MARKER, false)
//												.withX(486)
//												.withY(MARKER_Y_OFFSET + 2064)
//												.withMarkerHeight(SMALL_MARKER_HEIGHT),
//										new MapLocation()
//												.withPlace(workshops)
//												.withMarkerResource(DEFAULT_MARKER, false)
//												.withSelectedMarkerResource(DEFAULT_SELECTED_MARKER, false)
//												.withX(234)
//												.withY(MARKER_Y_OFFSET + 2158)
//												.withMarkerHeight(SMALL_MARKER_HEIGHT),
//										new MapLocation()
//												.withPlace(meetings)
//												.withMarkerResource(DEFAULT_MARKER, false)
//												.withSelectedMarkerResource(DEFAULT_SELECTED_MARKER, false)
//												.withX(234)
//												.withY(MARKER_Y_OFFSET + 2000),
//										new MapLocation()
//												.withPlace(new Place().withName("הוביטון"))
//												.withMarkerResource(DEFAULT_MARKER, false)
//												.withSelectedMarkerResource(DEFAULT_SELECTED_MARKER, false)
//												.withX(234)
//												.withY(MARKER_Y_OFFSET + 1738),
//										new MapLocation()
//												.withPlace(new Place().withName("כניסה נגישה לעירוני"))
//												.withMarkerResource(DEFAULT_MARKER, false)
//												.withSelectedMarkerResource(DEFAULT_SELECTED_MARKER, false)
//												.withX(114)
//												.withY(MARKER_Y_OFFSET + 1536),
//										new MapLocation()
//												.withPlace(new Place().withName("שירותים"))
//												.withMarkerResource(DEFAULT_MARKER, false)
//												.withSelectedMarkerResource(DEFAULT_SELECTED_MARKER, false)
//												.withX(240)
//												.withY(MARKER_Y_OFFSET + 1328)
//												.withMarkerHeight(SMALL_MARKER_HEIGHT),
//										new MapLocation()
//												.withPlace(new Place().withName("שירותים"))
//												.withMarkerResource(DEFAULT_MARKER, false)
//												.withSelectedMarkerResource(DEFAULT_SELECTED_MARKER, false)
//												.withX(240)
//												.withY(MARKER_Y_OFFSET + 1230)
//												.withMarkerHeight(SMALL_MARKER_HEIGHT),
//										new MapLocation()
//												.withPlace(new Place().withName("חדר קוספליי נשים"))
//												.withMarkerResource(DEFAULT_MARKER, false)
//												.withSelectedMarkerResource(DEFAULT_SELECTED_MARKER, false)
//												.withX(240)
//												.withY(MARKER_Y_OFFSET + 1088),
//										new MapLocation()
//												.withPlace(new Place().withName("חדר קוספליי גברים"))
//												.withMarkerResource(DEFAULT_MARKER, false)
//												.withSelectedMarkerResource(DEFAULT_SELECTED_MARKER, false)
//												.withX(240)
//												.withY(MARKER_Y_OFFSET + 912),
//										new MapLocation()
//												.withPlace(new Place().withName("שמירת חפצים"))
//												.withMarkerResource(DEFAULT_MARKER, false)
//												.withSelectedMarkerResource(DEFAULT_SELECTED_MARKER, false)
//												.withX(240)
//												.withY(MARKER_Y_OFFSET + 716),
//										new MapLocation()
//												.withPlace(new Place().withName("שמירת חפצים"))
//												.withMarkerResource(DEFAULT_MARKER, false)
//												.withSelectedMarkerResource(DEFAULT_SELECTED_MARKER, false)
//												.withX(240)
//												.withY(MARKER_Y_OFFSET + 524),
//										new MapLocation()
//												.withPlace(new Place().withName("דוכנים"))
//												.withMarkerResource(DEFAULT_MARKER, false)
//												.withSelectedMarkerResource(DEFAULT_SELECTED_MARKER, false)
//												.withX(646)
//												.withY(MARKER_Y_OFFSET + 1398),
//										new MapLocation()
//												.withName("כניסה לעירוני")
//												.withMarkerResource(DEFAULT_MARKER, false)
//												.withSelectedMarkerResource(DEFAULT_SELECTED_MARKER, false)
//												.withX(856)
//												.withY(MARKER_Y_OFFSET + 1232),
//										new MapLocation()
//												.withPlace(new Place().withName("דוכנים"))
//												.withMarkerResource(DEFAULT_MARKER, false)
//												.withSelectedMarkerResource(DEFAULT_SELECTED_MARKER, false)
//												.withX(646)
//												.withY(MARKER_Y_OFFSET + 842),
//										new MapLocation()
//												.withPlace(new Place().withName("אולם הספורט"))
//												.withMarkerResource(DEFAULT_MARKER, false)
//												.withSelectedMarkerResource(DEFAULT_SELECTED_MARKER, false)
//												.withX(954)
//												.withY(MARKER_Y_OFFSET + 372),
//										new MapLocation()
//												.withPlace(new Place().withName("דוכנים"))
//												.withMarkerResource(DEFAULT_MARKER, false)
//												.withSelectedMarkerResource(DEFAULT_SELECTED_MARKER, false)
//												.withX(642)
//												.withY(MARKER_Y_OFFSET + 116)
//								)
//						)
//				)
		;
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

		imageMapper.addMapping(ImageIdToImageResourceMapper.EVENT_GENERIC, R.drawable.icon2019_background);

		return imageMapper;
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
					.withQuestionEntry(FeedbackQuestion.QUESTION_ID_ENJOYMENT_5P, "entry.415572741")
					.withQuestionEntry(FeedbackQuestion.QUESTION_ID_LECTURER_QUALITY_5P, "entry.1327236956")
					.withQuestionEntry(FeedbackQuestion.QUESTION_ID_SIMILAR_EVENTS_5P, "entry.1416969956")
					.withQuestionEntry(FeedbackQuestion.QUESTION_ID_ADDITIONAL_INFO, "entry.1582215667")
					.withSendUrl(new URL("https://docs.google.com/forms/d/e/1FAIpQLSfraYvfQm83clQrqyQ_F9QGK2Qv5-fXQRilMClSiV0EB5-EKA/formResponse"));
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
					.withQuestionEntry(FeedbackQuestion.QUESTION_ID_LIKED_5P, "entry.1327236956")
					.withQuestionEntry(FeedbackQuestion.QUESTION_ID_MAP_SIGNS, "entry.1416969956")
					.withQuestionEntry(FeedbackQuestion.QUESTION_ID_CONFLICTING_EVENTS, "entry.1582215667")
					.withQuestionEntry(FeedbackQuestion.QUESTION_ID_IMPROVEMENT, "entry.993320932")
					.withSendUrl(new URL("https://docs.google.com/forms/d/e/1FAIpQLScbDYud3x8OfSd-53GY11TrRJVWqdxI6_2wT3DbAEIe1IJ_fg/formResponse"));
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
		return feedbackForm;
	}

	@Override
	protected URL initModelURL() {
		try {
			return new URL("https://api.sf-f.org.il/program/list_events.php?slug=" + API_SLUG);
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	protected URL initTicketsLastUpdateURL() {
		try {
			return new URL("https://api.sf-f.org.il/program/cache_get_last_updated.php?which=available_tickets&slug=" + API_SLUG);
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	protected URL initUpdatesURL() {
		try {
			return new URL("https://api.sf-f.org.il/announcements/get.php?slug=" + API_SLUG);
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public URL getEventTicketsNumberURL(ConventionEvent event) {
		try {
			return new URL("https://api.sf-f.org.il/program/available_tickets_per_event.php?slug=" + API_SLUG + "&id=" + event.getServerId());
		} catch (MalformedURLException e) {
			return null;
		}
	}

	@Override
	public URL getSecondHandFormURL(String id) {
		try {
			return new URL(YAD2_API + "form?formId=" + URLEncoder.encode(id, "UTF-8"));
		} catch (MalformedURLException|UnsupportedEncodingException e) {
			return null;
		}
	}

	@Override
	public URL getSecondHandFormsURL(List<String> ids) {
		try {
			String idsParam = TextUtils.join(",", CollectionUtils.map(ids, item -> {
				try {
					return URLEncoder.encode(item, "UTF-8");
				} catch (UnsupportedEncodingException e) {
					throw new RuntimeException(e);
				}
			}));
			return new URL(YAD2_API + "form?formIds=" + idsParam);
		} catch (MalformedURLException|RuntimeException e) {
			return null;
		}
	}

	@Override
	public URL getSecondHandItemsURL(SecondHandItem.Status status) {
		try {
			return new URL(YAD2_API + "allItems?status=" + status.getServerStatus());
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public HttpURLConnection getUserPurchasedEventsRequest(String user, String password) throws Exception {
		URL url = new URL("https://api.sf-f.org.il/program/events_per_user.php?slug=" + API_SLUG);
		HttpURLConnection request = HttpConnectionCreator.createConnection(url);
		request.setRequestMethod("POST");
		request.setDoInput(true);
		request.setDoOutput(true);
		OutputStream os = request.getOutputStream();
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
			writer.write("email=" +
					URLEncoder.encode(user, "UTF-8") + "&pass=" + URLEncoder.encode(password, "UTF-8"));
			writer.flush();
		} finally {
			if (writer != null) {
				writer.close();
			}
			os.close();
		}
		return request;
	}

	@Override
	public HttpURLConnection getUserIDRequest(String user, String password) throws Exception {
		URL url = new URL("https://api.sf-f.org.il/program/get_user_id.php?slug=" + API_SLUG);
		HttpURLConnection request = HttpConnectionCreator.createConnection(url);
		request.setRequestMethod("POST");
		request.setDoInput(true);
		request.setDoOutput(true);
		OutputStream os = request.getOutputStream();
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
			writer.write("email=" +
					URLEncoder.encode(user, "UTF-8") + "&pass=" + URLEncoder.encode(password, "UTF-8"));
			writer.flush();
		} finally {
			if (writer != null) {
				writer.close();
			}
			os.close();
		}
		return request;
	}

//	@Override
//	public ConventionEvent findEventByURL(String url) {
//		if (url.startsWith("http://program.iconfestival.org.il/")) {
//			url = "https://newprogram.olamot-con.org.il/" + url.substring("http://program.iconfestival.org.il/".length());
//		}
//		return super.findEventByURL(url);
//	}
}

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
import amai.org.conventions.utils.HttpConnectionCreator;
import sff.org.conventions.R;

public class Icon2018Convention extends SffConvention {
	private static final String HALL_NAME_ESHKOL_1 = "אשכול 1";
	private static final String HALL_NAME_ESHKOL_2 = "אשכול 2";
	private static final String HALL_NAME_ESHKOL_3 = "אשכול 3";
	private static final String HALL_NAME_ESHKOL_4 = "אשכול 4";
	private static final String HALL_NAME_ESHKOL_5 = "אשכול 5";
	private static final String HALL_NAME_ESHKOL_6 = "אשכול 6";
	private static final String HALL_NAME_WORKSHOPS = "חדר סדנאות";
	private static final String HALL_NAME_MEETINGS = "חדר מפגשים";
	private static final String HALL_NAME_KIDS = "חדר ילדים";
	private static final String HALL_NAME_OUTSIDE = "אירועי חוצות";
	private static final String HALL_NAME_OUTSIDE_2 = "אירועי חוצות 2";

	@Override
	protected ConventionStorage initStorage() {
		return new ConventionStorage(this, R.raw.olamot2018_convention_events, 1);
	}

	@Override
	protected Calendar initStartDate() {
		return Dates.createDate(2018, Calendar.SEPTEMBER, 25);
	}

	@Override
	protected Calendar initEndDate() {
		return Dates.createDate(2018, Calendar.SEPTEMBER, 27);
	}

	@Override
	protected String initID() {
		return "Icon2018";
	}

	@Override
	protected String initDisplayName() {
		return "פסטיבל אייקון 2018";
	}

	@Override
	protected Halls initHalls() {
		return new Halls(Arrays.asList(
				new Hall().withName(HALL_NAME_ESHKOL_1).withOrder(1),
				new Hall().withName(HALL_NAME_ESHKOL_2).withOrder(2),
				new Hall().withName(HALL_NAME_ESHKOL_3).withOrder(3),
				new Hall().withName(HALL_NAME_ESHKOL_4).withOrder(4),
				new Hall().withName(HALL_NAME_ESHKOL_5).withOrder(5),
				new Hall().withName(HALL_NAME_ESHKOL_6).withOrder(6),
				new Hall().withName(HALL_NAME_WORKSHOPS).withOrder(7),
				new Hall().withName(HALL_NAME_MEETINGS).withOrder(8),
				new Hall().withName(HALL_NAME_KIDS).withOrder(9),
				new Hall().withName(HALL_NAME_OUTSIDE).withOrder(10),
				new Hall().withName(HALL_NAME_OUTSIDE_2).withOrder(11)
		));
	}

	@Override
	protected ConventionMap initMap() {
		Hall eshkol1 = getHalls().findByName(HALL_NAME_ESHKOL_1);
		Hall eshkol2 = getHalls().findByName(HALL_NAME_ESHKOL_2);
		Hall eshkol3 = getHalls().findByName(HALL_NAME_ESHKOL_3);
		Hall eshkol4 = getHalls().findByName(HALL_NAME_ESHKOL_4);
		Hall eshkol5 = getHalls().findByName(HALL_NAME_ESHKOL_5);
		Hall eshkol6 = getHalls().findByName(HALL_NAME_ESHKOL_6);
		Hall workshops = getHalls().findByName(HALL_NAME_WORKSHOPS);
		Hall meetings = getHalls().findByName(HALL_NAME_MEETINGS);
		Hall kids = getHalls().findByName(HALL_NAME_KIDS);
		Hall outside = getHalls().findByName(HALL_NAME_OUTSIDE);
		Hall outside2 = getHalls().findByName(HALL_NAME_OUTSIDE_2);

		final int MAP_HEIGHT_OFFSET = -20;
		final int MAP_HEIGHT = 2448 + MAP_HEIGHT_OFFSET;
		Floor floor = new Floor(1)
				.withName("מפת התמצאות")
				.withImageResource(R.drawable.olamot2018_map, false)
				.withImageHeight(MAP_HEIGHT - MAP_HEIGHT_OFFSET)
				.withImageWidth(1982)
				.withDefaultMarkerHeight(100);
		final int BIG_MARKER_HEIGHT = 200;
		final int SMALL_MARKER_HEIGHT = 70;

		int DEFAULT_MARKER = R.drawable.ic_action_place;
		int DEFAULT_SELECTED_MARKER = R.drawable.ic_action_place_red;
		return new ConventionMap()
				.withFloors(Collections.singletonList(floor))
				.withLocations(
						CollectionUtils.flattenList(
								inFloor(floor,
										new MapLocation()
												.withPlace(new Place().withName("דוכנים"))
												.withMarkerResource(DEFAULT_MARKER, false)
												.withSelectedMarkerResource(DEFAULT_SELECTED_MARKER, false)
												.withX(559)
												.withY(MAP_HEIGHT - 67),
										new MapLocation()
												.withPlace(new Place().withName("דוכנים"))
												.withMarkerResource(DEFAULT_MARKER, false)
												.withSelectedMarkerResource(DEFAULT_SELECTED_MARKER, false)
												.withX(1570)
												.withY(MAP_HEIGHT - 321),
										new MapLocation()
												.withPlace(new Place().withName("מתחם השקות"))
												.withMarkerResource(DEFAULT_MARKER, false)
												.withSelectedMarkerResource(DEFAULT_SELECTED_MARKER, false)
												.withX(1133)
												.withY(MAP_HEIGHT - 231),
										new MapLocation()
												.withPlace(new Place().withName("איזור ישיבה"))
												.withMarkerResource(DEFAULT_MARKER, false)
												.withSelectedMarkerResource(DEFAULT_SELECTED_MARKER, false)
												.withX(708)
												.withY(MAP_HEIGHT - 213),
										new MapLocation()
												.withName("מדרגות לעלייה לאשכול 3-6")
												.withPlaces(Arrays.asList(eshkol3, eshkol4, eshkol5, eshkol6))
												.withMarkerResource(DEFAULT_MARKER, false)
												.withSelectedMarkerResource(DEFAULT_SELECTED_MARKER, false)
												.withX(1794)
												.withY(MAP_HEIGHT - 496),
										new MapLocation()
												.withName("מדרגות לעלייה לאשכול 3-6")
												.withPlaces(Arrays.asList(eshkol3, eshkol4, eshkol5, eshkol6))
												.withMarkerResource(DEFAULT_MARKER, false)
												.withSelectedMarkerResource(DEFAULT_SELECTED_MARKER, false)
												.withX(1228)
												.withY(MAP_HEIGHT - 496),
										new MapLocation()
												.withPlace(new Place().withName("שירותי בנים"))
												.withMarkerResource(DEFAULT_MARKER, false)
												.withSelectedMarkerResource(DEFAULT_SELECTED_MARKER, false)
												.withX(1945)
												.withY(MAP_HEIGHT - 664)
												.withMarkerHeight(SMALL_MARKER_HEIGHT),
										new MapLocation()
												.withPlace(new Place().withName("דוכנים"))
												.withMarkerResource(DEFAULT_MARKER, false)
												.withSelectedMarkerResource(DEFAULT_SELECTED_MARKER, false)
												.withX(1570)
												.withY(MAP_HEIGHT - 663),
										new MapLocation()
												.withPlace(new Place().withName("שירותי בנות"))
												.withMarkerResource(DEFAULT_MARKER, false)
												.withSelectedMarkerResource(DEFAULT_SELECTED_MARKER, false)
												.withX(1149)
												.withY(MAP_HEIGHT - 664)
												.withMarkerHeight(SMALL_MARKER_HEIGHT),
										new MapLocation()
												.withPlace(eshkol1)
												.withMarkerResource(DEFAULT_MARKER, false)
												.withSelectedMarkerResource(DEFAULT_SELECTED_MARKER, false)
												.withX(1512)
												.withY(MAP_HEIGHT - 886)
												.withMarkerHeight(BIG_MARKER_HEIGHT),
										new MapLocation()
												.withPlace(eshkol2)
												.withMarkerResource(DEFAULT_MARKER, false)
												.withSelectedMarkerResource(DEFAULT_SELECTED_MARKER, false)
												.withX(1861)
												.withY(MAP_HEIGHT - 886)
												.withMarkerHeight(BIG_MARKER_HEIGHT),
										new MapLocation()
												.withPlace(new Place().withName("מתחם יד שניה"))
												.withMarkerResource(DEFAULT_MARKER, false)
												.withSelectedMarkerResource(DEFAULT_SELECTED_MARKER, false)
												.withX(1158)
												.withY(MAP_HEIGHT - 886),
										new MapLocation()
												.withName("כניסה לעירוני")
												.withPlace(workshops)
												.withMarkerResource(DEFAULT_MARKER, false)
												.withSelectedMarkerResource(DEFAULT_SELECTED_MARKER, false)
												.withX(510)
												.withY(MAP_HEIGHT - 1456),
										new MapLocation()
												.withPlace(new Place().withName("שירותים"))
												.withMarkerResource(DEFAULT_MARKER, false)
												.withSelectedMarkerResource(DEFAULT_SELECTED_MARKER, false)
												.withX(85)
												.withY(MAP_HEIGHT - 448)
												.withMarkerHeight(SMALL_MARKER_HEIGHT),
										new MapLocation()
												.withPlace(meetings)
												.withMarkerResource(DEFAULT_MARKER, false)
												.withSelectedMarkerResource(DEFAULT_SELECTED_MARKER, false)
												.withX(90)
												.withY(MAP_HEIGHT - 643)
												.withMarkerHeight(SMALL_MARKER_HEIGHT),
										new MapLocation()
												.withPlace(kids)
												.withMarkerResource(DEFAULT_MARKER, false)
												.withSelectedMarkerResource(DEFAULT_SELECTED_MARKER, false)
												.withX(90)
												.withY(MAP_HEIGHT - 768),
										new MapLocation()
												.withPlace(new Place().withName("הוביטון"))
												.withMarkerResource(DEFAULT_MARKER, false)
												.withSelectedMarkerResource(DEFAULT_SELECTED_MARKER, false)
												.withX(90)
												.withY(MAP_HEIGHT - 984),
										new MapLocation()
												.withPlace(new Place().withName("שירותים"))
												.withMarkerResource(DEFAULT_MARKER, false)
												.withSelectedMarkerResource(DEFAULT_SELECTED_MARKER, false)
												.withX(97)
												.withY(MAP_HEIGHT - 1356)
												.withMarkerHeight(SMALL_MARKER_HEIGHT),
										new MapLocation()
												.withPlace(new Place().withName("שירותים"))
												.withMarkerResource(DEFAULT_MARKER, false)
												.withSelectedMarkerResource(DEFAULT_SELECTED_MARKER, false)
												.withX(97)
												.withY(MAP_HEIGHT - 1443)
												.withMarkerHeight(SMALL_MARKER_HEIGHT),
										new MapLocation()
												.withPlace(new Place().withName("חדר קוספליי בנות"))
												.withMarkerResource(DEFAULT_MARKER, false)
												.withSelectedMarkerResource(DEFAULT_SELECTED_MARKER, false)
												.withX(97)
												.withY(MAP_HEIGHT - 1530)
												.withMarkerHeight(SMALL_MARKER_HEIGHT),
										new MapLocation()
												.withPlace(new Place().withName("חדר קוספליי בנים"))
												.withMarkerResource(DEFAULT_MARKER, false)
												.withSelectedMarkerResource(DEFAULT_SELECTED_MARKER, false)
												.withX(97)
												.withY(MAP_HEIGHT - 1686),
										new MapLocation()
												.withPlace(new Place().withName("חדר תיקים 2"))
												.withMarkerResource(DEFAULT_MARKER, false)
												.withSelectedMarkerResource(DEFAULT_SELECTED_MARKER, false)
												.withX(97)
												.withY(MAP_HEIGHT - 1846),
										new MapLocation()
												.withPlace(new Place().withName("חדר תיקים 1"))
												.withMarkerResource(DEFAULT_MARKER, false)
												.withSelectedMarkerResource(DEFAULT_SELECTED_MARKER, false)
												.withX(97)
												.withY(MAP_HEIGHT - 2010),
										new MapLocation()
												.withPlace(new Place().withName("דוכנים"))
												.withMarkerResource(DEFAULT_MARKER, false)
												.withSelectedMarkerResource(DEFAULT_SELECTED_MARKER, false)
												.withX(1263)
												.withY(MAP_HEIGHT - 1389),
										new MapLocation()
												.withPlace(new Place().withName("מתחם לחימה"))
												.withMarkerResource(DEFAULT_MARKER, false)
												.withSelectedMarkerResource(DEFAULT_SELECTED_MARKER, false)
												.withX(1908)
												.withY(MAP_HEIGHT - 1581),
										new MapLocation()
												.withPlace(new Place().withName("כניסה מרחוב הארבעה"))
												.withMarkerResource(DEFAULT_MARKER, false)
												.withSelectedMarkerResource(DEFAULT_SELECTED_MARKER, false)
												.withX(1912)
												.withY(MAP_HEIGHT - 1831),
										new MapLocation()
												.withPlace(new Place().withName("מודיעין"))
												.withMarkerResource(DEFAULT_MARKER, false)
												.withSelectedMarkerResource(DEFAULT_SELECTED_MARKER, false)
												.withX(1818)
												.withY(MAP_HEIGHT - 2037),
										new MapLocation()
												.withPlace(new Place().withName("קופות"))
												.withMarkerResource(DEFAULT_MARKER, false)
												.withSelectedMarkerResource(DEFAULT_SELECTED_MARKER, false)
												.withX(1476)
												.withY(MAP_HEIGHT - 2013),
										new MapLocation()
												.withPlace(new Place().withName("דוכני העמותות"))
												.withMarkerResource(DEFAULT_MARKER, false)
												.withSelectedMarkerResource(DEFAULT_SELECTED_MARKER, false)
												.withX(1081)
												.withY(MAP_HEIGHT - 2032),
										new MapLocation()
												.withPlace(new Place().withName("אולם הספורט"))
												.withMarkerResource(DEFAULT_MARKER, false)
												.withSelectedMarkerResource(DEFAULT_SELECTED_MARKER, false)
												.withX(623)
												.withY(MAP_HEIGHT - 2167)
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

		imageMapper.addMapping(ImageIdToImageResourceMapper.EVENT_GENERIC, R.drawable.olamot2018_background);

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
					.withSendUrl(new URL("https://docs.google.com/forms/d/e/1FAIpQLScHMFgN36WPynPBnoOPBQttY2Kylg2VcnAULKKERsG2UxUQZg/formResponse"));
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
					.withSendUrl(new URL("https://docs.google.com/forms/d/e/1FAIpQLSewy5mWXDUtmdMN_h7PO899Hkxpzd-zJyHypVLbAYnHPi576Q/formResponse"));
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
		return feedbackForm;
	}

	@Override
	protected URL initModelURL() {
		try {
			return new URL("https://api.sf-f.org.il/program/list_events.php?slug=icon2018");
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	protected URL initTicketsLastUpdateURL() {
		try {
			return new URL("https://api.sf-f.org.il/program/cache_get_last_updated.php?which=available_tickets&slug=icon2018");
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	protected URL initUpdatesURL() {
		try {
			return new URL("https://api.sf-f.org.il/announcements/get.php?slug=icon2018"); // use test_con for tests
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public URL getEventTicketsNumberURL(ConventionEvent event) {
		try {
			return new URL("https://api.sf-f.org.il/program/available_tickets_per_event.php?slug=icon2018&id=" + event.getServerId()); // use test_con for tests
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
	public HttpURLConnection getUserPurchasedEventsRequest(String user, String password) throws Exception {
		URL url = new URL("https://api.sf-f.org.il/program/events_per_user.php?slug=icon2018&email=" +
				URLEncoder.encode(user, "UTF-8") + "&pass=" + URLEncoder.encode(password, "UTF-8"));
		HttpURLConnection request = HttpConnectionCreator.createConnection(url);
		request.setRequestMethod("POST");
		request.setDoInput(true);
		request.setDoOutput(true);
		OutputStream os = request.getOutputStream();
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
			writer.write("slug=icon2018&email=" +
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
}

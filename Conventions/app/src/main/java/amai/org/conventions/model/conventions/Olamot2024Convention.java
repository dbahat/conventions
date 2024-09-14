package amai.org.conventions.model.conventions;

import android.content.Context;
import android.text.TextUtils;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import amai.org.conventions.auth.Configuration;
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
import amai.org.conventions.model.SecondHandItem;
import amai.org.conventions.utils.CollectionUtils;
import amai.org.conventions.utils.ConventionStorage;
import amai.org.conventions.utils.Dates;
import amai.org.conventions.utils.HttpConnectionCreator;
import amai.org.conventions.utils.URLUtils;
import sff.org.conventions.R;

public class Olamot2024Convention extends SffConvention {
//	private static final String HALL_NAME_CINEMATHEQUE_1_3_4 = "סינמטק 1, 3, 4";
//	private static final String HALL_NAME_CINEMATHEQUE_2 = "סינמטק 2";
//	private static final String HALL_NAME_CINEMATHEQUE_5 = "סינמטק 5";
	private static final String HALL_NAME_ESHKOL_1 = "אשכול 1";
	private static final String HALL_NAME_ESHKOL_2 = "אשכול 2";
	private static final String HALL_NAME_ESHKOL_3 = "אשכול 3";
	private static final String HALL_NAME_ESHKOL_4 = "אשכול 4";
	private static final String HALL_NAME_ESHKOL_5 = "אשכול 5";
	private static final String HALL_NAME_ESHKOL_6 = "אשכול 6";
	private static final String HALL_NAME_WORKSHOPS = "סדנאות";
//	private static final String HALL_NAME_WORKSHOPS_1 = "סדנאות 1";
//	private static final String HALL_NAME_WORKSHOPS_2 = "סדנאות 2";
//	private static final String HALL_NAME_KIDS = "ילדים";
	private static final String HALL_NAME_MEETINGS = "מפגשים";
	private static final String HALL_NAME_OUTSIDE = "חוצות";
//	private static final String HALL_NAME_ARENA = "הזירה";
//	private static final String HALL_NAME_TENT_1 = "אוהל 1";
//	private static final String HALL_NAME_TENT_2 = "אוהל 2";
//	private static final String HALL_NAME_TENT_3 = "אוהל 3";
//	private static final String HALL_NAME_TENT_4 = "אוהל 4";
//	private static final String HALL_NAME_TENT_5 = "אוהל 5";
//	private static final String HALL_NAME_TENT_6 = "אוהל 6";
//	private static final String HALL_NAME_TENT_7 = "אוהל 7";
//	private static final String HALL_NAME_TENT_8 = "אוהל 8";
//	private static final String HALL_NAME_TENT_20 = "אוהל 20 טבעי";
//	private static final String HALL_NAME_IRONI_1 = "עירוני 1";
//	private static final String HALL_NAME_IRONI_2 = "עירוני 2";
//	private static final String HALL_NAME_IRONI_3 = "עירוני 3";
//	private static final String HALL_NAME_IRONI_4 = "עירוני 4";
//	private static final String HALL_NAME_IRONI_5 = "עירוני 5";
//	private static final String HALL_NAME_IRONI_6 = "עירוני 6";
//	private static final String HALL_NAME_IRONI_7 = "עירוני 7";
//	private static final String HALL_NAME_IRONI_8 = "עירוני 8";
//	private static final String HALL_NAME_ARTEMIS = "ארטמיס";
//	private static final String HALL_NAME_MINIATURES_1 = "מיניאטורות 1";
//	private static final String HALL_NAME_MINIATURES_2 = "מיניאטורות 2";
//	private static final String HALL_NAME_MINIATURES_DEMO = "הדגמות מיניאטורות";
//	private static final String HALL_NAME_MINIATURES_WORKSHOP = "סדנאות מיניאטורות";
//	private static final String HALL_NAME_GAMES_1 = "משחקי קופסה 1";
//	private static final String HALL_NAME_GAMES_2 = "משחקי קופסה 2";
//	private static final String HALL_NAME_GAMES_3 = "משחקים 3";
//	private static final String HALL_NAME_GAMES_4 = "משחקים 4";

	private static final String API_SLUG = "olamot2024";
	private static final String TEST_API_SLUG = "test_con";
	private static final String YAD2_API = "https://api.sf-f.org.il/yad2/";
	private static final String TEST_YAD2_API = "https://test.api.sf-f.org.il/yad2/";

	@Override
	protected ConventionStorage initStorage() {
		return new ConventionStorage(this, R.raw.olamot2024_convention_events, 0);
	}

	@Override
	protected Calendar initStartDate() {
		return Dates.createDate(2024, Calendar.APRIL, 24);
	}

	@Override
	protected Calendar initEndDate() {
		return Dates.createDate(2025, Calendar.APRIL, 25);
	}

	@Override
	protected String initID() {
		return "Olamot2024";
	}

	@Override
	protected String initDisplayName() {
		return "כנס עולמות 2024";
	}

	@Override
	protected Halls initHalls() {
		List<Hall> halls = Arrays.asList(
//				new Hall().withName(HALL_NAME_CINEMATHEQUE_1_3_4),
//				new Hall().withName(HALL_NAME_CINEMATHEQUE_2),
//				new Hall().withName(HALL_NAME_CINEMATHEQUE_5),
				new Hall().withName(HALL_NAME_ESHKOL_1),
				new Hall().withName(HALL_NAME_ESHKOL_2),
				new Hall().withName(HALL_NAME_ESHKOL_3),
				new Hall().withName(HALL_NAME_ESHKOL_4),
				new Hall().withName(HALL_NAME_ESHKOL_5),
				new Hall().withName(HALL_NAME_ESHKOL_6),
				new Hall().withName(HALL_NAME_MEETINGS),
				new Hall().withName(HALL_NAME_WORKSHOPS),
//				new Hall().withName(HALL_NAME_KIDS),
//				new Hall().withName(HALL_NAME_WORKSHOPS_1),
//				new Hall().withName(HALL_NAME_WORKSHOPS_2),
//				new Hall().withName(HALL_NAME_KIDS_VIRTUAL),
//				new Hall().withName(HALL_NAME_MEETINGS_VIRTUAL),
				new Hall().withName(HALL_NAME_OUTSIDE)
//				new Hall().withName(HALL_NAME_ARENA),
//				new Hall().withName(HALL_NAME_TENT_1),
//				new Hall().withName(HALL_NAME_TENT_2),
//				new Hall().withName(HALL_NAME_TENT_3),
//				new Hall().withName(HALL_NAME_TENT_4),
//				new Hall().withName(HALL_NAME_TENT_5),
//				new Hall().withName(HALL_NAME_TENT_6),
//				new Hall().withName(HALL_NAME_TENT_7),
//				new Hall().withName(HALL_NAME_TENT_8),
//				new Hall().withName(HALL_NAME_IRONI_1),
//				new Hall().withName(HALL_NAME_IRONI_2),
//				new Hall().withName(HALL_NAME_IRONI_3),
//				new Hall().withName(HALL_NAME_IRONI_4),
//				new Hall().withName(HALL_NAME_IRONI_5),
//				new Hall().withName(HALL_NAME_IRONI_6),
//				new Hall().withName(HALL_NAME_IRONI_7),
//				new Hall().withName(HALL_NAME_IRONI_8)
//				new Hall().withName(HALL_NAME_ARTEMIS),
//				new Hall().withName(HALL_NAME_MINIATURES_1),
//				new Hall().withName(HALL_NAME_MINIATURES_2),
//				new Hall().withName(HALL_NAME_MINIATURES_DEMO),
//				new Hall().withName(HALL_NAME_MINIATURES_WORKSHOP)
//				new Hall().withName(HALL_NAME_GAMES_1),
//				new Hall().withName(HALL_NAME_GAMES_2),
//				new Hall().withName(HALL_NAME_GAMES_3),
//				new Hall().withName(HALL_NAME_GAMES_4),
//				new Hall().withName(HALL_NAME_TENT_20)
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
//		return null;
		return createMap();
	}

	private ConventionMap createMap() {
		Hall eshkol1 = getHalls().findByName(HALL_NAME_ESHKOL_1);
		Hall eshkol2 = getHalls().findByName(HALL_NAME_ESHKOL_2);
		Hall eshkol3 = getHalls().findByName(HALL_NAME_ESHKOL_3);
		Hall eshkol4 = getHalls().findByName(HALL_NAME_ESHKOL_4);
		Hall eshkol5 = getHalls().findByName(HALL_NAME_ESHKOL_5);
		Hall eshkol6 = getHalls().findByName(HALL_NAME_ESHKOL_6);
		Hall workshops = getHalls().findByName(HALL_NAME_WORKSHOPS);
		Hall meetings = getHalls().findByName(HALL_NAME_MEETINGS);
//		Hall kids = getHalls().findByName(HALL_NAME_KIDS);
//		Hall ironi1 = getHalls().findByName(HALL_NAME_IRONI_1);
//		Hall ironi2 = getHalls().findByName(HALL_NAME_IRONI_2);
//		Hall ironi3 = getHalls().findByName(HALL_NAME_IRONI_3);
//		Hall ironi4 = getHalls().findByName(HALL_NAME_IRONI_4);
//		Hall ironi5 = getHalls().findByName(HALL_NAME_IRONI_5);
//		Hall ironi6 = getHalls().findByName(HALL_NAME_IRONI_6);
//		Hall ironi7 = getHalls().findByName(HALL_NAME_IRONI_7);
//		Hall ironi8 = getHalls().findByName(HALL_NAME_IRONI_8);
		Hall outside = getHalls().findByName(HALL_NAME_OUTSIDE);
//		Hall arena = getHalls().findByName(HALL_NAME_ARENA);
//		Hall tent20 = getHalls().findByName(HALL_NAME_TENT_20);
//		Hall games1 = getHalls().findByName(HALL_NAME_GAMES_1);
//		Hall games2 = getHalls().findByName(HALL_NAME_GAMES_2);

		Floor floor = new Floor(1)
				.withName("מפת מתחם")
				.withImageResource(R.raw.olamot2024_map, true)
				.withImageHeight(813)
				.withImageWidth(836.38f)
				.withDefaultMarkerHeight(35);
		final int BIG_MARKER_HEIGHT = 70;
		final int SMALL_MARKER_HEIGHT = 25;

		return new ConventionMap()
			.withFloors(Collections.singletonList(floor))
			.withLocations(
				CollectionUtils.flattenList(
					inFloor(floor,
						mapLocation("יציאת חירום", 207, 39),
						mapLocation("דוכנים", 357, 42),
						mapLocation("מודיעין", 127, 65),
						mapLocation("מתחם דוכני פופ-אפ", 135, 111),
						mapLocation("מתחם משחקי מחשב", 243, 111),
						mapLocation("אשכולות 3-4", Arrays.asList(eshkol3, eshkol4), 101, 206).withMarkerHeight(SMALL_MARKER_HEIGHT),
						mapLocation("דוכנים", 190, 222),
						mapLocation("אשכולות 5-6", Arrays.asList(eshkol5, eshkol6), 290, 206).withMarkerHeight(SMALL_MARKER_HEIGHT),
						mapLocation("שירותי גברים", 119, 246),
						mapLocation("שירותי נשים", 260, 246),
						mapLocation(eshkol1, 193, 317),
						mapLocation("מרחב מוגן", 238, 292),
						mapLocation(eshkol2, 105, 317),
						mapLocation("דוכנים", 240, 346),
						mapLocation("דוכנים", 169, 415),
						mapLocation("דוכנים", 288, 415),
						mapLocation("דוכנים", 365, 415),
						mapLocation("כניסה ויציאה מרחוב הארבעה", 36, 514),
						mapLocation("מודיעין", 51, 596),
						mapLocation("דוכני עמותות", 129, 596),
						mapLocation("קופות", 293, 587),
						mapLocation("סינמטק תל אביב", 125, 743),
						mapLocation("אולם ספורט", 405, 689),
						mapLocation("קפיטריה", 441, 608),
						mapLocation("כניסה לעירוני", 633, 62),
						mapLocation("שירותי גברים", 600, 122).withMarkerHeight(SMALL_MARKER_HEIGHT),
						mapLocation("מרחב מוגן", 584, 185),
						mapLocation("שירותי נשים", 619, 122).withMarkerHeight(SMALL_MARKER_HEIGHT),
						mapLocation(workshops, 649, 219),
						mapLocation(meetings, 649, 176),
						mapLocation("הוביטון", 649, 267),
						mapLocation("קוספליי גברים", 666, 368),
						mapLocation("עמדת תיקון קוספליי", 573, 403),
						mapLocation("מרחב מוגן", 651, 463),
						mapLocation("קוספליי נשים", 781, 460),
						mapLocation("כניסה ויציאה נגישה לעירוני", 726, 385),
						mapLocation("המתחם הקהילתי", 744, 294),
						mapLocation("אוהל זיכרון", 776, 173),
						mapLocation("יציאת חירום", 730, 73),
						mapLocation("שירותי יוניסקס", 596, 548),
						mapLocation("מעבר לעירוני", 501, 535),
						mapLocation("דוכנים", 515, 613),
						mapLocation("שמירת חפצים", 584, 702),
						mapLocation("השטיח האדום", 719, 625),
						mapLocation("כניסה ויציאה מרחוב שפרינצק", 699, 710)
					)
				)
			);
	}

	private MapLocation mapLocation(String name, float x, float y) {
		return mapLocation(name, null, x, y);
	}

	private MapLocation mapLocation(Place place, float x, float y) {
		return mapLocation(null, Collections.singletonList(place), x, y);
	}

	private MapLocation mapLocation(String name, List<? extends Place> places, float x, float y) {
		final int DEFAULT_MARKER = R.drawable.icon2024_place;
		final int DEFAULT_MARKER_TINT_RES = MapLocation.NO_TINT;
		final int DEFAULT_SELECTED_MARKER = R.drawable.icon2024_place_selected;
		final int DEFAULT_SELECTED_MARKER_TINT_RES = MapLocation.NO_TINT;

		MapLocation result = new MapLocation();
		if (places != null) {
			result.setPlaces(places);
			if (name != null) {
				result.setName(name);
			}
		} else {
			result.setPlace(new Place().withName(name));
		}

		return result
			.withMarkerResource(DEFAULT_MARKER, false, DEFAULT_MARKER_TINT_RES)
			.withSelectedMarkerResource(DEFAULT_SELECTED_MARKER, false, DEFAULT_SELECTED_MARKER_TINT_RES)
			.withX(x)
			.withY(y);
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

//		imageMapper.addMapping(ImageIdToImageResourceMapper.EVENT_GENERIC, R.drawable....);

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
					.withQuestionEntry(FeedbackQuestion.QUESTION_ID_ENJOYMENT_5S, "entry.415572741")
					.withQuestionEntry(FeedbackQuestion.QUESTION_ID_LECTURER_QUALITY_5P, "entry.1327236956")
					.withQuestionEntry(FeedbackQuestion.QUESTION_ID_SIMILAR_EVENTS_5P, "entry.1416969956")
					.withQuestionEntry(FeedbackQuestion.QUESTION_ID_ADDITIONAL_INFO, "entry.1582215667")
					.withSendUrl(new URL("https://docs.google.com/forms/d/e/1FAIpQLSeIsX_1PjmOJrsk5468qphLsYh_1DVgx39bLh4y0v2KFZfn2w/formResponse"));
		} catch (MalformedURLException e) {//
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
					.withQuestionEntry(FeedbackQuestion.QUESTION_ID_LIKED_5S, "entry.1327236956")
					.withQuestionEntry(FeedbackQuestion.QUESTION_ID_MAP_SIGNS, "entry.1416969956")
					.withQuestionEntry(FeedbackQuestion.QUESTION_ID_CONFLICTING_EVENTS, "entry.1582215667")
					.withQuestionEntry(FeedbackQuestion.QUESTION_ID_IMPROVEMENT, "entry.993320932")
					.withSendUrl(new URL("https://docs.google.com/forms/d/e/1FAIpQLSdwefudcwQye8_91qW9wzocvVOYMFsrZyPG6P7_79qBCat57Q/formResponse"));
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
			return new URL(YAD2_API + "form?formId=" + URLUtils.encodeURLParameterValue(id));
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public URL getSecondHandFormsURL(List<String> ids) {
		try {
			String idsParam = TextUtils.join(",", CollectionUtils.map(ids, URLUtils::encodeURLParameterValue));
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
	public HttpURLConnection getUserPurchasedEventsRequest(String token) throws Exception {
		URL url = new URL("https://api.sf-f.org.il/program/cod3/events_per_user_sso/?slug=" + API_SLUG);
		HttpURLConnection request = HttpConnectionCreator.createConnection(url);
		request.setRequestMethod("GET");
		request.addRequestProperty("Authorization", "Bearer " + token);
		request.setDoOutput(true);
		return request;
	}

	@Override
	public HttpURLConnection getUserIDRequest(String token) throws Exception {
		URL url = new URL("https://api.sf-f.org.il/program/cod3/get_user_id_sso/?slug=" + API_SLUG);
		HttpURLConnection request = HttpConnectionCreator.createConnection(url);
		request.setRequestMethod("GET");
		request.addRequestProperty("Authorization", "Bearer " + token);
		request.setDoOutput(true);
		return request;
	}


//	@Override
//	public ConventionEvent findEventByURL(String url) {
//		if (url.startsWith("http://program.iconfestival.org.il/")) {
//			url = "https://newprogram.olamot-con.org.il/" + url.substring("http://program.iconfestival.org.il/".length());
//		}
//		return super.findEventByURL(url);
//	}

	@Override
	public URL getAdditionalConventionFeedbackURL() {
		try {
			return new URL("https://docs.google.com/forms/d/e/1FAIpQLSd7z_RtsWPsON1P7_HUjY_DszR0u8KVYrPn5mO4XJaFTAiuNw/viewform");
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public URL getAdditionalEventFeedbackURL(ConventionEvent event) {
		try {
			return new URL("https://docs.google.com/forms/d/e/1FAIpQLSfOiYCymLEbyr6_PwV4WsKADG7WFFJ0G3ix23cezqOXZoVYZg/viewform" +
					"?entry.1572016508=" + URLUtils.encodeURLParameterValue(event.getTitle()) +
					"&entry.1917108492=" + URLUtils.encodeURLParameterValue(event.getLecturer()) +
					"&entry.10889808=" + URLUtils.encodeURLParameterValue(event.getHall().getName()) +
					"&entry.1131737302=" + URLUtils.encodeURLParameterValue(Dates.formatDateAndTime(Dates.localToConventionTime(event.getStartTime())))
			);
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public boolean canUserLogin() {
		return true;
	}

	@Override
	public List<ConventionEvent.EventLocationType> getEventLocationTypes(ConventionEvent event) {
		//noinspection deprecation - this is on purpose
		return event.getLocationTypes();
	}

	@Override
	public String getEventAdditionalInfo(ConventionEvent event, Context context) {
		List<ConventionEvent.EventLocationType> allLocationTypes = getEventLocationTypes();
		List<ConventionEvent.EventLocationType> eventLocationTypes = getEventLocationTypes(event);
		if (allLocationTypes == null || allLocationTypes.size() < 2 || eventLocationTypes == null || eventLocationTypes.size() == 0) {
			return null;
		}

		ConventionEvent.EventLocationType primaryLocationType = eventLocationTypes.get(0);
		if (eventLocationTypes.size() == 1 && primaryLocationType == ConventionEvent.EventLocationType.PHYSICAL) {
			return context.getString(R.string.physical_only_event_desc);
		} else if (eventLocationTypes.size() == 1 && primaryLocationType == ConventionEvent.EventLocationType.VIRTUAL) {
			return context.getString(R.string.virtual_only_event_desc);
		} else if (primaryLocationType == ConventionEvent.EventLocationType.PHYSICAL) {
			return context.getString(R.string.physical_hybrid_event_desc);
		} else {
			return context.getString(R.string.virtual_hybrid_event_desc);
		}
	}

	@Override
	public boolean areVirtualEventTicketsUnlimited(ConventionEvent event) {
		// For this convention, all hybrid events virtual tickets are unlimited.
		// There are virtual events with limited tickets but they aren't hybrid.
		List<ConventionEvent.EventLocationType> eventLocationTypes = this.getEventLocationTypes(event);
		return eventLocationTypes != null && eventLocationTypes.size() > 1;
	}

	@Override
	public Configuration getAuthConfiguration(Context context) {
		return new Configuration(
				context,
				"con_apps_v2",
				null, // If the client is not "public" this must contain the client secret
				"https://sso.sf-f.org.il/auth/realms/sf-f/protocol/openid-connect/auth",
				"https://sso.sf-f.org.il/auth/realms/sf-f/protocol/openid-connect/token",
				"https://sso.sf-f.org.il/auth/realms/sf-f/protocol/openid-connect/logout",
				"https://sso.sf-f.org.il/auth/realms/sf-f/protocol/openid-connect/userinfo"
		);
	}
}

package amai.org.conventions.model.conventions;

import android.content.Context;
import android.text.TextUtils;

import com.google.gson.JsonObject;

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
import amai.org.conventions.networking.ModelParser;
import amai.org.conventions.networking.SffModelParser;
import amai.org.conventions.utils.CollectionUtils;
import amai.org.conventions.utils.ConventionStorage;
import amai.org.conventions.utils.Dates;
import amai.org.conventions.utils.HttpConnectionCreator;
import amai.org.conventions.utils.URLUtils;
import sff.org.conventions.R;

public class Icon2022Convention extends SffConvention {
//	private static final String HALL_NAME_CINEMATHEQUE_1_3_4 = "סינמטק 1, 3, 4";
//	private static final String HALL_NAME_CINEMATHEQUE_2 = "סינמטק 2";
//	private static final String HALL_NAME_CINEMATHEQUE_5 = "סינמטק 5";
	private static final String HALL_NAME_ESHKOL_1 = "אשכול 1";
	private static final String HALL_NAME_ESHKOL_2 = "אשכול 2";
//	private static final String HALL_NAME_ESHKOL_2_VIRTUAL = "אשכול 2 (וירטואלי)";
	private static final String HALL_NAME_ESHKOL_3 = "אשכול 3";
	private static final String HALL_NAME_ESHKOL_4 = "אשכול 4";
//	private static final String HALL_NAME_ESHKOL_3_VIRTUAL = "אשכול 3 (וירטואלי)";
//	private static final String HALL_NAME_ESHKOL_4_VIRTUAL = "אשכול 4 (וירטואלי)";
	private static final String HALL_NAME_ESHKOL_5 = "אשכול 5";
//	private static final String HALL_NAME_ESHKOL_6 = "אשכול 6";
	private static final String HALL_NAME_WORKSHOPS = "סדנאות";
//	private static final String HALL_NAME_WORKSHOPS_1 = "סדנאות 1";
//	private static final String HALL_NAME_WORKSHOPS_2 = "סדנאות 2";
	private static final String HALL_NAME_KIDS = "ילדים";
//	private static final String HALL_NAME_KIDS_VIRTUAL = "ילדים (וירטואלי)";
	private static final String HALL_NAME_MEETINGS = "מפגשים";
//	private static final String HALL_NAME_MEETINGS_VIRTUAL = "מפגשים (וירטואלי)";
	private static final String HALL_NAME_OUTSIDE = "חוצות";
	private static final String HALL_NAME_ARENA = "הזירה";
//	private static final String HALL_NAME_TENT_1 = "אוהל 1";
//	private static final String HALL_NAME_TENT_2 = "אוהל 2";
//	private static final String HALL_NAME_TENT_3 = "אוהל 3";
//	private static final String HALL_NAME_TENT_4 = "אוהל 4";
//	private static final String HALL_NAME_TENT_5 = "אוהל 5";
//	private static final String HALL_NAME_TENT_6 = "אוהל 6";
//	private static final String HALL_NAME_TENT_7 = "אוהל 7";
//	private static final String HALL_NAME_TENT_8 = "אוהל 8";
	private static final String HALL_NAME_TENT_20 = "אוהל 20 טבעי";
	private static final String HALL_NAME_IRONI_1 = "עירוני 1";
	private static final String HALL_NAME_IRONI_2 = "עירוני 2";
	private static final String HALL_NAME_IRONI_3 = "עירוני 3";
	private static final String HALL_NAME_IRONI_4 = "עירוני 4";
	private static final String HALL_NAME_IRONI_5 = "עירוני 5";
//	private static final String HALL_NAME_IRONI_5_VIRTUAL = "עירוני 5 (וירטואלי)";
	private static final String HALL_NAME_IRONI_6 = "עירוני 6";
//	private static final String HALL_NAME_IRONI_6_VIRTUAL = "עירוני 6 (וירטואלי)";
	private static final String HALL_NAME_IRONI_7 = "עירוני 7";
//	private static final String HALL_NAME_IRONI_7_VIRTUAL = "עירוני 7 (וירטואלי)";
	private static final String HALL_NAME_IRONI_8 = "עירוני 8";
//	private static final String HALL_NAME_IRONI_8_VIRTUAL = "עירוני 8 (וירטואלי)";
//	private static final String HALL_NAME_IRONI_9_VIRTUAL = "עירוני 9 (וירטואלי)";
//	private static final String HALL_NAME_ARTEMIS = "ארטמיס";
//	private static final String HALL_NAME_MINIATURES_1 = "מיניאטורות 1";
//	private static final String HALL_NAME_MINIATURES_2 = "מיניאטורות 2";
//	private static final String HALL_NAME_MINIATURES_DEMO = "הדגמות מיניאטורות";
//	private static final String HALL_NAME_MINIATURES_WORKSHOP = "סדנאות מיניאטורות";
	private static final String HALL_NAME_GAMES_1 = "משחקי קופסה 1";
	private static final String HALL_NAME_GAMES_2 = "משחקי קופסה 2";
//	private static final String HALL_NAME_GAMES_3 = "משחקים 3";
//	private static final String HALL_NAME_GAMES_4 = "משחקים 4";

	private static final String API_SLUG = "icon2022";
	private static final String TEST_API_SLUG = "test_con";
	private static final String YAD2_API = "https://api.sf-f.org.il/yad2/";
	private static final String TEST_YAD2_API = "https://test.api.sf-f.org.il/yad2/";

	@Override
	protected ConventionStorage initStorage() {
		return new ConventionStorage(this, R.raw.icon2022_convention_events, 0);
	}

	@Override
	protected Calendar initStartDate() {
		return Dates.createDate(2022, Calendar.OCTOBER, 11);
	}

	@Override
	protected Calendar initEndDate() {
		return Dates.createDate(2022, Calendar.OCTOBER, 13);
	}

	@Override
	protected String initID() {
		return "Icon2022";
	}

	@Override
	protected String initDisplayName() {
		return "פסטיבל אייקון 2022";
	}

	@Override
	protected Halls initHalls() {
		List<Hall> halls = Arrays.asList(
//				new Hall().withName(HALL_NAME_CINEMATHEQUE_1_3_4),
//				new Hall().withName(HALL_NAME_CINEMATHEQUE_2),
//				new Hall().withName(HALL_NAME_CINEMATHEQUE_5),
				new Hall().withName(HALL_NAME_ESHKOL_1),
				new Hall().withName(HALL_NAME_ESHKOL_2),
//				new Hall().withName(HALL_NAME_ESHKOL_2_VIRTUAL),
				new Hall().withName(HALL_NAME_ESHKOL_3),
//				new Hall().withName(HALL_NAME_ESHKOL_3_VIRTUAL),
				new Hall().withName(HALL_NAME_ESHKOL_4),
				new Hall().withName(HALL_NAME_ESHKOL_5),
//				new Hall().withName(HALL_NAME_ESHKOL_6),
				new Hall().withName(HALL_NAME_WORKSHOPS),
//				new Hall().withName(HALL_NAME_WORKSHOPS_1),
//				new Hall().withName(HALL_NAME_WORKSHOPS_2),
				new Hall().withName(HALL_NAME_KIDS),
//				new Hall().withName(HALL_NAME_KIDS_VIRTUAL),
				new Hall().withName(HALL_NAME_MEETINGS),
//				new Hall().withName(HALL_NAME_MEETINGS_VIRTUAL),
				new Hall().withName(HALL_NAME_OUTSIDE),
				new Hall().withName(HALL_NAME_ARENA),
//				new Hall().withName(HALL_NAME_TENT_1),
//				new Hall().withName(HALL_NAME_TENT_2),
//				new Hall().withName(HALL_NAME_TENT_3),
//				new Hall().withName(HALL_NAME_TENT_4),
//				new Hall().withName(HALL_NAME_TENT_5),
//				new Hall().withName(HALL_NAME_TENT_6),
//				new Hall().withName(HALL_NAME_TENT_7),
//				new Hall().withName(HALL_NAME_TENT_8),
				new Hall().withName(HALL_NAME_IRONI_1),
				new Hall().withName(HALL_NAME_IRONI_2),
				new Hall().withName(HALL_NAME_IRONI_3),
				new Hall().withName(HALL_NAME_IRONI_4),
				new Hall().withName(HALL_NAME_IRONI_5),
//				new Hall().withName(HALL_NAME_IRONI_5_VIRTUAL),
				new Hall().withName(HALL_NAME_IRONI_6),
//				new Hall().withName(HALL_NAME_IRONI_6_VIRTUAL),
				new Hall().withName(HALL_NAME_IRONI_7),
//				new Hall().withName(HALL_NAME_IRONI_7_VIRTUAL),
				new Hall().withName(HALL_NAME_IRONI_8),
//				new Hall().withName(HALL_NAME_IRONI_9_VIRTUAL)
//				new Hall().withName(HALL_NAME_ARTEMIS),
//				new Hall().withName(HALL_NAME_MINIATURES_1),
//				new Hall().withName(HALL_NAME_MINIATURES_2),
//				new Hall().withName(HALL_NAME_MINIATURES_DEMO),
//				new Hall().withName(HALL_NAME_MINIATURES_WORKSHOP)
				new Hall().withName(HALL_NAME_GAMES_1),
				new Hall().withName(HALL_NAME_GAMES_2),
//				new Hall().withName(HALL_NAME_GAMES_3),
//				new Hall().withName(HALL_NAME_GAMES_4),
				new Hall().withName(HALL_NAME_TENT_20)
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
		Hall eshkol1 = getHalls().findByName(HALL_NAME_ESHKOL_1);
		Hall eshkol2 = getHalls().findByName(HALL_NAME_ESHKOL_2);
		Hall eshkol3 = getHalls().findByName(HALL_NAME_ESHKOL_3);
		Hall eshkol4 = getHalls().findByName(HALL_NAME_ESHKOL_4);
		Hall workshops = getHalls().findByName(HALL_NAME_WORKSHOPS);

		Floor floor = new Floor(1)
				.withName("מפת מתחם")
				.withImageResource(R.drawable.olamot2022_map, false)
				.withImageHeight(1402)
				.withImageWidth(1541)
				.withDefaultMarkerHeight(65);
		final int BIG_MARKER_HEIGHT = 140;
		final int SMALL_MARKER_HEIGHT = 45;

		int DEFAULT_MARKER = R.drawable.baseline_place_24_no_padding;
		int DEFAULT_MARKER_TINT_RES = R.color.icon2022_grey3;
		int DEFAULT_SELECTED_MARKER = R.drawable.baseline_place_24_no_padding;
		int DEFAULT_SELECTED_MARKER_TINT_RES = R.color.icon2022_red1;
		return new ConventionMap()
			.withFloors(Collections.singletonList(floor))
			.withLocations(
				CollectionUtils.flattenList(
					inFloor(floor,
						new MapLocation()
							.withPlace(new Place().withName("כניסה מרחוב ליאונרדו דה וינצ'י"))
							.withMarkerResource(DEFAULT_MARKER, false, DEFAULT_MARKER_TINT_RES)
							.withSelectedMarkerResource(DEFAULT_SELECTED_MARKER, false, DEFAULT_SELECTED_MARKER_TINT_RES)
							.withX(1276)
							.withY(1267),
						new MapLocation()
							.withPlace(new Place().withName("מודיעין"))
							.withMarkerResource(DEFAULT_MARKER, false, DEFAULT_MARKER_TINT_RES)
							.withSelectedMarkerResource(DEFAULT_SELECTED_MARKER, false, DEFAULT_SELECTED_MARKER_TINT_RES)
							.withX(1310)
							.withY(1224),
						new MapLocation()
							.withPlace(new Place().withName("דוכן Myst"))
							.withMarkerResource(DEFAULT_MARKER, false, DEFAULT_MARKER_TINT_RES)
							.withSelectedMarkerResource(DEFAULT_SELECTED_MARKER, false, DEFAULT_SELECTED_MARKER_TINT_RES)
							.withX(1361)
							.withY(1166),
						new MapLocation()
							.withPlace(new Place().withName("דוכני הקוביה והממלכה"))
							.withMarkerResource(DEFAULT_MARKER, false, DEFAULT_MARKER_TINT_RES)
							.withSelectedMarkerResource(DEFAULT_SELECTED_MARKER, false, DEFAULT_SELECTED_MARKER_TINT_RES)
							.withX(1233)
							.withY(1134),
						new MapLocation()
							.withPlace(new Place().withName("שוק קח-תן"))
							.withMarkerResource(DEFAULT_MARKER, false, DEFAULT_MARKER_TINT_RES)
							.withSelectedMarkerResource(DEFAULT_SELECTED_MARKER, false, DEFAULT_SELECTED_MARKER_TINT_RES)
							.withX(1017)
							.withY(1184),
						new MapLocation()
							.withName("מדרגות לעלייה לאשכול 3-4")
							.withPlaces(Arrays.asList(eshkol3, eshkol4))
							.withMarkerResource(DEFAULT_MARKER, false, DEFAULT_MARKER_TINT_RES)
							.withSelectedMarkerResource(DEFAULT_SELECTED_MARKER, false, DEFAULT_SELECTED_MARKER_TINT_RES)
							.withX(1367)
							.withY(1003)
							.withMarkerHeight(SMALL_MARKER_HEIGHT),
						new MapLocation()
							.withPlace(new Place().withName("שירותי גברים"))
							.withMarkerResource(DEFAULT_MARKER, false, DEFAULT_MARKER_TINT_RES)
							.withSelectedMarkerResource(DEFAULT_SELECTED_MARKER, false, DEFAULT_SELECTED_MARKER_TINT_RES)
							.withX(1376)
							.withY(944)
							.withMarkerHeight(SMALL_MARKER_HEIGHT),
						new MapLocation()
							.withPlace(new Place().withName("דוכן האגודה"))
							.withMarkerResource(DEFAULT_MARKER, false, DEFAULT_MARKER_TINT_RES)
							.withSelectedMarkerResource(DEFAULT_SELECTED_MARKER, false, DEFAULT_SELECTED_MARKER_TINT_RES)
							.withX(1307)
							.withY(955),
						new MapLocation()
							.withPlace(new Place().withName("דוכן טולקין"))
							.withMarkerResource(DEFAULT_MARKER, false, DEFAULT_MARKER_TINT_RES)
							.withSelectedMarkerResource(DEFAULT_SELECTED_MARKER, false, DEFAULT_SELECTED_MARKER_TINT_RES)
							.withX(1133)
							.withY(955),
						new MapLocation()
							.withPlace(new Place().withName("דוכן אמא\"י"))
							.withMarkerResource(DEFAULT_MARKER, false, DEFAULT_MARKER_TINT_RES)
							.withSelectedMarkerResource(DEFAULT_SELECTED_MARKER, false, DEFAULT_SELECTED_MARKER_TINT_RES)
							.withX(1019)
							.withY(1026)
							.withMarkerHeight(SMALL_MARKER_HEIGHT),
						new MapLocation()
							.withPlace(new Place().withName("שירותי נשים"))
							.withMarkerResource(DEFAULT_MARKER, false, DEFAULT_MARKER_TINT_RES)
							.withSelectedMarkerResource(DEFAULT_SELECTED_MARKER, false, DEFAULT_SELECTED_MARKER_TINT_RES)
							.withX(1037)
							.withY(944)
							.withMarkerHeight(SMALL_MARKER_HEIGHT),
						new MapLocation()
							.withPlace(eshkol1)
							.withMarkerResource(DEFAULT_MARKER, false, DEFAULT_MARKER_TINT_RES)
							.withSelectedMarkerResource(DEFAULT_SELECTED_MARKER, false, DEFAULT_SELECTED_MARKER_TINT_RES)
							.withX(1210)
							.withY(804)
							.withMarkerHeight(BIG_MARKER_HEIGHT),
						new MapLocation()
							.withPlace(eshkol2)
							.withMarkerResource(DEFAULT_MARKER, false, DEFAULT_MARKER_TINT_RES)
							.withSelectedMarkerResource(DEFAULT_SELECTED_MARKER, false, DEFAULT_SELECTED_MARKER_TINT_RES)
							.withX(1386)
							.withY(793)
							.withMarkerHeight(BIG_MARKER_HEIGHT),
						new MapLocation()
							.withPlace(new Place().withName("דוכנים"))
							.withMarkerResource(DEFAULT_MARKER, false, DEFAULT_MARKER_TINT_RES)
							.withSelectedMarkerResource(DEFAULT_SELECTED_MARKER, false, DEFAULT_SELECTED_MARKER_TINT_RES)
							.withX(1125)
							.withY(584),
						new MapLocation()
							.withPlace(new Place().withName("דוכנים"))
							.withMarkerResource(DEFAULT_MARKER, false, DEFAULT_MARKER_TINT_RES)
							.withSelectedMarkerResource(DEFAULT_SELECTED_MARKER, false, DEFAULT_SELECTED_MARKER_TINT_RES)
							.withX(1359)
							.withY(507),
						new MapLocation()
							.withPlace(new Place().withName("דוכנים"))
							.withMarkerResource(DEFAULT_MARKER, false, DEFAULT_MARKER_TINT_RES)
							.withSelectedMarkerResource(DEFAULT_SELECTED_MARKER, false, DEFAULT_SELECTED_MARKER_TINT_RES)
							.withX(1199)
							.withY(507),
						new MapLocation()
							.withPlace(new Place().withName("דוכנים"))
							.withMarkerResource(DEFAULT_MARKER, false, DEFAULT_MARKER_TINT_RES)
							.withSelectedMarkerResource(DEFAULT_SELECTED_MARKER, false, DEFAULT_SELECTED_MARKER_TINT_RES)
							.withX(1038)
							.withY(507),
						new MapLocation()
							.withPlace(new Place().withName("דוכנים"))
							.withMarkerResource(DEFAULT_MARKER, false, DEFAULT_MARKER_TINT_RES)
							.withSelectedMarkerResource(DEFAULT_SELECTED_MARKER, false, DEFAULT_SELECTED_MARKER_TINT_RES)
							.withX(870)
							.withY(507),
						new MapLocation()
							.withPlace(new Place().withName("כניסה מרחוב הארבעה"))
							.withMarkerResource(DEFAULT_MARKER, false, DEFAULT_MARKER_TINT_RES)
							.withSelectedMarkerResource(DEFAULT_SELECTED_MARKER, false, DEFAULT_SELECTED_MARKER_TINT_RES)
							.withX(1482)
							.withY(351),
						new MapLocation()
							.withPlace(new Place().withName("מודיעין"))
							.withMarkerResource(DEFAULT_MARKER, false, DEFAULT_MARKER_TINT_RES)
							.withSelectedMarkerResource(DEFAULT_SELECTED_MARKER, false, DEFAULT_SELECTED_MARKER_TINT_RES)
							.withX(1410)
							.withY(262),
						new MapLocation()
							.withPlace(new Place().withName("אבידות ומציאות"))
							.withMarkerResource(DEFAULT_MARKER, false, DEFAULT_MARKER_TINT_RES)
							.withSelectedMarkerResource(DEFAULT_SELECTED_MARKER, false, DEFAULT_SELECTED_MARKER_TINT_RES)
							.withX(1453)
							.withY(175),
						new MapLocation()
							.withPlace(new Place().withName("קופות"))
							.withMarkerResource(DEFAULT_MARKER, false, DEFAULT_MARKER_TINT_RES)
							.withSelectedMarkerResource(DEFAULT_SELECTED_MARKER, false, DEFAULT_SELECTED_MARKER_TINT_RES)
							.withX(1250)
							.withY(259),
						new MapLocation()
							.withPlace(new Place().withName("כניסה לעירוני"))
							.withMarkerResource(DEFAULT_MARKER, false, DEFAULT_MARKER_TINT_RES)
							.withSelectedMarkerResource(DEFAULT_SELECTED_MARKER, false, DEFAULT_SELECTED_MARKER_TINT_RES)
							.withX(498)
							.withY(1122),
						new MapLocation()
							.withPlace(new Place().withName("שירותי גברים"))
							.withMarkerResource(DEFAULT_MARKER, false, DEFAULT_MARKER_TINT_RES)
							.withSelectedMarkerResource(DEFAULT_SELECTED_MARKER, false, DEFAULT_SELECTED_MARKER_TINT_RES)
							.withX(519)
							.withY(1025)
							.withMarkerHeight(SMALL_MARKER_HEIGHT),
						new MapLocation()
							.withPlace(new Place().withName("שירותי נשים"))
							.withMarkerResource(DEFAULT_MARKER, false, DEFAULT_MARKER_TINT_RES)
							.withSelectedMarkerResource(DEFAULT_SELECTED_MARKER, false, DEFAULT_SELECTED_MARKER_TINT_RES)
							.withX(483)
							.withY(1025)
							.withMarkerHeight(SMALL_MARKER_HEIGHT),
						new MapLocation()
							.withPlace(workshops)
							.withMarkerResource(DEFAULT_MARKER, false, DEFAULT_MARKER_TINT_RES)
							.withSelectedMarkerResource(DEFAULT_SELECTED_MARKER, false, DEFAULT_SELECTED_MARKER_TINT_RES)
							.withX(475)
							.withY(755)
							.withMarkerHeight(SMALL_MARKER_HEIGHT),
						new MapLocation()
							.withPlace(new Place().withName("כניסה נגישה לסדנאות"))
							.withMarkerResource(DEFAULT_MARKER, false, DEFAULT_MARKER_TINT_RES)
							.withSelectedMarkerResource(DEFAULT_SELECTED_MARKER, false, DEFAULT_SELECTED_MARKER_TINT_RES)
							.withX(333)
							.withY(654),
						new MapLocation()
							.withPlace(new Place().withName("שירותים"))
							.withMarkerResource(DEFAULT_MARKER, false, DEFAULT_MARKER_TINT_RES)
							.withSelectedMarkerResource(DEFAULT_SELECTED_MARKER, false, DEFAULT_SELECTED_MARKER_TINT_RES)
							.withX(540)
							.withY(582)
							.withMarkerHeight(SMALL_MARKER_HEIGHT),
						new MapLocation()
							.withPlace(new Place().withName("שירותים"))
							.withMarkerResource(DEFAULT_MARKER, false, DEFAULT_MARKER_TINT_RES)
							.withSelectedMarkerResource(DEFAULT_SELECTED_MARKER, false, DEFAULT_SELECTED_MARKER_TINT_RES)
							.withX(540)
							.withY(532)
							.withMarkerHeight(SMALL_MARKER_HEIGHT),
						new MapLocation()
							.withPlace(new Place().withName("חדר קוספליי נשים"))
							.withMarkerResource(DEFAULT_MARKER, false, DEFAULT_MARKER_TINT_RES)
							.withSelectedMarkerResource(DEFAULT_SELECTED_MARKER, false, DEFAULT_SELECTED_MARKER_TINT_RES)
							.withX(540)
							.withY(462),
						new MapLocation()
							.withPlace(new Place().withName("חדר קוספליי גברים"))
							.withMarkerResource(DEFAULT_MARKER, false, DEFAULT_MARKER_TINT_RES)
							.withSelectedMarkerResource(DEFAULT_SELECTED_MARKER, false, DEFAULT_SELECTED_MARKER_TINT_RES)
							.withX(540)
							.withY(382),
						new MapLocation()
							.withPlace(new Place().withName("שמירת חפצים"))
							.withMarkerResource(DEFAULT_MARKER, false, DEFAULT_MARKER_TINT_RES)
							.withSelectedMarkerResource(DEFAULT_SELECTED_MARKER, false, DEFAULT_SELECTED_MARKER_TINT_RES)
							.withX(515)
							.withY(312),
						new MapLocation()
							.withPlace(new Place().withName("דוכנים"))
							.withMarkerResource(DEFAULT_MARKER, false, DEFAULT_MARKER_TINT_RES)
							.withSelectedMarkerResource(DEFAULT_SELECTED_MARKER, false, DEFAULT_SELECTED_MARKER_TINT_RES)
							.withX(638)
							.withY(611),
						new MapLocation()
							.withName("כניסה לעירוני")
							.withMarkerResource(DEFAULT_MARKER, false, DEFAULT_MARKER_TINT_RES)
							.withSelectedMarkerResource(DEFAULT_SELECTED_MARKER, false, DEFAULT_SELECTED_MARKER_TINT_RES)
							.withX(719)
							.withY(541),
						new MapLocation()
							.withPlace(new Place().withName("דוכנים"))
							.withMarkerResource(DEFAULT_MARKER, false, DEFAULT_MARKER_TINT_RES)
							.withSelectedMarkerResource(DEFAULT_SELECTED_MARKER, false, DEFAULT_SELECTED_MARKER_TINT_RES)
							.withX(638)
							.withY(370),
						new MapLocation()
							.withPlace(new Place().withName("אולם הספורט"))
							.withMarkerResource(DEFAULT_MARKER, false, DEFAULT_MARKER_TINT_RES)
							.withSelectedMarkerResource(DEFAULT_SELECTED_MARKER, false, DEFAULT_SELECTED_MARKER_TINT_RES)
							.withX(758)
							.withY(175),
						new MapLocation()
							.withPlace(new Place().withName("הפונדק"))
							.withMarkerResource(DEFAULT_MARKER, false, DEFAULT_MARKER_TINT_RES)
							.withSelectedMarkerResource(DEFAULT_SELECTED_MARKER, false, DEFAULT_SELECTED_MARKER_TINT_RES)
							.withX(230)
							.withY(981)
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
					.withQuestionEntry(FeedbackQuestion.QUESTION_ID_ENJOYMENT_5P, "entry.415572741")
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
					.withQuestionEntry(FeedbackQuestion.QUESTION_ID_LIKED_5P, "entry.1327236956")
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
			return new URL("https://docs.google.com/forms/d/e/1FAIpQLSe14ezOfJvwkV9yQi8S8LTB8a0gjxjpxEpZ-vtMVEp-jQqc0w/viewform");
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public URL getAdditionalEventFeedbackURL(ConventionEvent event) {
		try {
			return new URL("https://docs.google.com/forms/d/e/1FAIpQLSdHvVv-cztVhWjDe0NzMne40gj-woeg1Eq_9eGv7jxuRBf5Iw/viewform" +
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
	public ModelParser getModelParser() {
		return new SffModelParser() {
			@Override
			protected int getEventPrice(JsonObject eventObj) {
				return -1;
			}
		};
	}

	@Override
	public boolean canUserLogin() {
		return true;
	}

	@Override
	public URL getEventViewURL(ConventionEvent event) {
		try {
			// All virtual-enabled events are in the same room
			// TODO should we also check the hall name?
			if (this.getEventLocationTypes(event).contains(ConventionEvent.EventLocationType.VIRTUAL)) {
				return new URL("https://icon2022.virtualcon.org.il/room-a");
			}
			return null;
		} catch (Exception e) {
			return null;
		}
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

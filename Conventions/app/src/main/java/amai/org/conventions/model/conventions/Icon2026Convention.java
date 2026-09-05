package amai.org.conventions.model.conventions;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;

import java.io.BufferedWriter;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import amai.org.conventions.ActivitiesActivity;
import amai.org.conventions.auth.Configuration;
import amai.org.conventions.feedback.forms.EventFeedbackForm;
import amai.org.conventions.feedback.forms.FeedbackForm;
import amai.org.conventions.model.ConventionEvent;
import amai.org.conventions.model.ConventionMap;
import amai.org.conventions.model.DetailsActivityLocation;
import amai.org.conventions.model.FeedbackQuestion;
import amai.org.conventions.model.Floor;
import amai.org.conventions.model.Hall;
import amai.org.conventions.model.Halls;
import amai.org.conventions.model.ImageIdToImageResourceMapper;
import amai.org.conventions.model.MapLocation;
import amai.org.conventions.model.Place;
import amai.org.conventions.model.Shelter;
import amai.org.conventions.model.StandType;
import amai.org.conventions.model.StandTypes;
import amai.org.conventions.model.StandsArea;
import amai.org.conventions.utils.CollectionUtils;
import amai.org.conventions.utils.ConventionStorage;
import amai.org.conventions.utils.Dates;
import amai.org.conventions.utils.HttpConnectionCreator;
import amai.org.conventions.utils.URLUtils;
import amai.org.conventions.utils.Views;
import sff.org.conventions.R;

public class Icon2026Convention extends SffConvention {
//	private static final String HALL_NAME_CINEMATHEQUE_1_3_4 = "סינמטק 1, 3, 4";
//	private static final String HALL_NAME_CINEMATHEQUE_2 = "סינמטק 2";
//	private static final String HALL_NAME_CINEMATHEQUE_3 = "סינמטק 3";
	private static final String HALL_NAME_CINEMATHEQUE_4 = "סינמטק 4";
//	private static final String HALL_NAME_CINEMATHEQUE_5 = "סינמטק 5";
	private static final String HALL_NAME_ESHKOL_1 = "אשכול 1";
	private static final String HALL_NAME_ESHKOL_2 = "אשכול 2";
	private static final String HALL_NAME_ESHKOL_3 = "אשכול 3";
	private static final String HALL_NAME_ESHKOL_4 = "אשכול 4";
	private static final String HALL_NAME_ESHKOL_5 = "אשכול 5";
//	private static final String HALL_NAME_ESHKOL_6 = "אשכול 6";
//	private static final String HALL_NAME_WORKSHOPS = "עירוני סדנאות";
	private static final String HALL_NAME_WORKSHOPS_1 = "סדנאות 1";
	private static final String HALL_NAME_WORKSHOPS_2 = "סדנאות 2";
	private static final String HALL_NAME_MEETINGS = "מפגשים";
	private static final String HALL_NAME_KIDS = "ילדים";
//	private static final String HALL_NAME_SPECIAL = "אירועים מיוחדים";
//	private static final String HALL_NAME_ENTIRE_CON = "מתחם הכנס";
	private static final String HALL_NAME_OUTSIDE = "זירה וחוצות";
//	private static final String HALL_NAME_ARENA = "זירה";
//	private static final String HALL_NAME_TENT_1 = "אוהל 1";
//	private static final String HALL_NAME_TENT_2 = "אוהל 2";
//	private static final String HALL_NAME_TENT_3 = "אוהל 3";
//	private static final String HALL_NAME_TENT_4 = "אוהל 4";
//	private static final String HALL_NAME_TENT_5 = "אוהל 5";
//	private static final String HALL_NAME_TENT_6 = "אוהל 6";
//	private static final String HALL_NAME_TENT_7 = "אוהל 7";
//	private static final String HALL_NAME_TENT_8 = "אוהל 8";
//	private static final String HALL_NAME_TENT_20 = "אוהל 20 טבעי";
	private static final String HALL_NAME_IRONI_1 = "עירוני 1";
	private static final String HALL_NAME_IRONI_2 = "עירוני 2";
	private static final String HALL_NAME_IRONI_3 = "עירוני 3";
	private static final String HALL_NAME_IRONI_4 = "עירוני 4";
	private static final String HALL_NAME_IRONI_5 = "עירוני 5";
	private static final String HALL_NAME_IRONI_6 = "עירוני 6";
	private static final String HALL_NAME_IRONI_7 = "עירוני 7";
	private static final String HALL_NAME_IRONI_8 = "עירוני 8";
	private static final String HALL_NAME_IRONI_9 = "עירוני 9";
//	private static final String HALL_NAME_IRONI_10 = "עירוני 10";
//	private static final String HALL_NAME_ARTEMIS = "ארטמיס";
//	private static final String HALL_NAME_MINIATURES_1 = "מיניאטורות 1";
//	private static final String HALL_NAME_MINIATURES_2 = "מיניאטורות 2";
//	private static final String HALL_NAME_MINIATURES_DEMO = "הדגמות מיניאטורות";
//	private static final String HALL_NAME_MINIATURES_WORKSHOP = "סדנאות מיניאטורות";
	private static final String HALL_NAME_GAMES_1 = "חדר משחקי קופסה 1";
	private static final String HALL_NAME_GAMES_2 = "חדר משחקי קופסה 2";
//	private static final String HALL_NAME_GAMES_3 = "משחקים 3";
//	private static final String HALL_NAME_GAMES_4 = "משחקים 4";
//	private static final String HALL_NAME_ICODE = "אייקוד";
//	private static final String HALL_NAME_SPACESHIP = "החללית";

	private static final String GENERAL_STAND_TYPE = "כללי";

	private static final String API_SLUG = "icon2026";
	private static final String TEST_API_SLUG = "test_con";
	private static final String YAD2_API = "https://api.yadash.sf-f.org.il/";
	private static final String TEST_YAD2_API = "https://test.api.sf-f.org.il/yad2/";

	@Override
	protected ConventionStorage initStorage() {
		return new ConventionStorage(this)
			.withInitialEventsFile(R.raw.icon2026_convention_events, 0)
			.withInitialStandsFile(R.raw.icon2026_stands, 0);
	}

	@Override
	protected Calendar initStartDate() {
		return Dates.createDate(2026, Calendar.SEPTEMBER, 29);
	}

	@Override
	protected Calendar initEndDate() {
		return Dates.createDate(2026, Calendar.OCTOBER, 1);
	}

	@Override
	protected String initID() {
		return "Icon2026";
	}

	@Override
	protected String initDisplayName() {
		return "פסטיבל אייקון 2026";
	}

	@Override
	protected Halls initHalls() {
		List<Hall> halls = Arrays.asList(
//				new Hall().withName(HALL_NAME_CINEMATHEQUE_1_3_4),
//				new Hall().withName(HALL_NAME_CINEMATHEQUE_2),
//				new Hall().withName(HALL_NAME_CINEMATHEQUE_3),
				new Hall().withName(HALL_NAME_CINEMATHEQUE_4),
//				new Hall().withName(HALL_NAME_CINEMATHEQUE_5),
//				new Hall().withName(HALL_NAME_SPECIAL),
				new Hall().withName(HALL_NAME_ESHKOL_1).withShelter(true),
				new Hall().withName(HALL_NAME_ESHKOL_2),
				new Hall().withName(HALL_NAME_ESHKOL_3),
				new Hall().withName(HALL_NAME_ESHKOL_4),
				new Hall().withName(HALL_NAME_ESHKOL_5),
//				new Hall().withName(HALL_NAME_ESHKOL_6),
				new Hall().withName(HALL_NAME_WORKSHOPS_1),
				new Hall().withName(HALL_NAME_WORKSHOPS_2),
//				new Hall().withName(HALL_NAME_WORKSHOPS),
				new Hall().withName(HALL_NAME_KIDS),
				new Hall().withName(HALL_NAME_MEETINGS),
				new Hall().withName(HALL_NAME_OUTSIDE),
//				new Hall().withName(HALL_NAME_ENTIRE_CON),
//				new Hall().withName(HALL_NAME_KIDS_VIRTUAL),
//				new Hall().withName(HALL_NAME_MEETINGS_VIRTUAL),
//				new Hall().withName(HALL_NAME_ARENA),
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
				new Hall().withName(HALL_NAME_IRONI_6),
				new Hall().withName(HALL_NAME_IRONI_7),
				new Hall().withName(HALL_NAME_IRONI_8),
				new Hall().withName(HALL_NAME_IRONI_9),
//				new Hall().withName(HALL_NAME_IRONI_10),
//				new Hall().withName(HALL_NAME_ARTEMIS),
//				new Hall().withName(HALL_NAME_MINIATURES_1),
//				new Hall().withName(HALL_NAME_MINIATURES_2),
//				new Hall().withName(HALL_NAME_MINIATURES_DEMO),
//				new Hall().withName(HALL_NAME_MINIATURES_WORKSHOP),
				new Hall().withName(HALL_NAME_GAMES_1),
				new Hall().withName(HALL_NAME_GAMES_2)
//				new Hall().withName(HALL_NAME_GAMES_3),
//				new Hall().withName(HALL_NAME_GAMES_4),
//				new Hall().withName(HALL_NAME_TENT_20),
//				new Hall().withName(HALL_NAME_ICODE),
//				new Hall().withName(HALL_NAME_SPECIAL),
		);
		int i = 1;
		for (Hall hall : halls) {
			hall.setOrder(i);
			++i;
		}
		return new Halls(halls);
	}

	@Override
	protected URL initStandsURL() {
		try {
			return new URL("https://api.sf-f.org.il/booths/booths.json");
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	protected String getGeneralStandType() {
		return GENERAL_STAND_TYPE;
	}

	@Override
	protected StandTypes initStandTypes() {
		List<StandType> standTypes = Arrays.asList(
			new StandType().withName("דוכן יוצר.ת עצמאי.ת").withImage(R.drawable.diamond_24px),
			new StandType().withName("דוכן מסחרי").withImage(R.drawable.ic_shopping_basket),
			new StandType().withName("דוכן סופר.ת עצמאי.ת").withImage(R.drawable.book_5_24px)
		);
		int i = 1;
		for (StandType standType : standTypes) {
			standType.setOrder(i);
			++i;
		}
		return new StandTypes(standTypes);
	}

	@Override
	protected ConventionMap initMap() {
//		return null;
		return createMap();
	}

	private ConventionMap createMap() {
		Hall cinematheque4 = getHalls().findByName(HALL_NAME_CINEMATHEQUE_4);
		Hall eshkol1 = getHalls().findByName(HALL_NAME_ESHKOL_1);
		Hall eshkol2 = getHalls().findByName(HALL_NAME_ESHKOL_2);
		Hall eshkol3 = getHalls().findByName(HALL_NAME_ESHKOL_3);
		Hall eshkol4 = getHalls().findByName(HALL_NAME_ESHKOL_4);
		Hall eshkol5 = getHalls().findByName(HALL_NAME_ESHKOL_5);
		Hall workshops1 = getHalls().findByName(HALL_NAME_WORKSHOPS_1);
		Hall workshops2 = getHalls().findByName(HALL_NAME_WORKSHOPS_2);
		Hall kids = getHalls().findByName(HALL_NAME_KIDS);
		Hall meetings = getHalls().findByName(HALL_NAME_MEETINGS);
		Hall ironi1 = getHalls().findByName(HALL_NAME_IRONI_1);
		Hall ironi2 = getHalls().findByName(HALL_NAME_IRONI_2);
		Hall ironi3 = getHalls().findByName(HALL_NAME_IRONI_3);
		Hall ironi4 = getHalls().findByName(HALL_NAME_IRONI_4);
		Hall ironi5 = getHalls().findByName(HALL_NAME_IRONI_5);
		Hall ironi6 = getHalls().findByName(HALL_NAME_IRONI_6);
		Hall ironi7 = getHalls().findByName(HALL_NAME_IRONI_7);
		Hall ironi8 = getHalls().findByName(HALL_NAME_IRONI_8);
		Hall ironi9 = getHalls().findByName(HALL_NAME_IRONI_9);
		Hall games1 = getHalls().findByName(HALL_NAME_GAMES_1);
		Hall games2 = getHalls().findByName(HALL_NAME_GAMES_2);

		Floor floor = new Floor(1)
				.withName("מפת המתחם")
				.withImageResource(R.raw.icon2025_map, true)
				.withImageHeight(3832.88f)
				.withImageWidth(3406.42f)
				.withDefaultMarkerHeight(153.195f);
		final float SMALL_MARKER_HEIGHT = 104.497f;

		StandsArea standsAreaA = new StandsArea().withName("א'-אטלנטיס");
		StandsArea standsAreaB = new StandsArea().withName("ב'-בה סינג סה");
		StandsArea standsAreaC = new StandsArea().withName("ג' - גאליפריי");
		StandsArea standsAreaD = new StandsArea().withName("ד'- דרגונסטון");
		StandsArea standsAreaE = new StandsArea().withName("ה'-היפריון");
		StandsArea standsAreaF = new StandsArea().withName("ו'- וולקן");
		StandsArea standsAreaP = new StandsArea().withName("פ'- פלורין שלישי");
		StandsArea standsAreaEshkol = new StandsArea().withName("אשכול");
		StandsArea standsAreaH = new StandsArea().withName("ז'- זוטרופוליס");
		StandsArea standsAreaG = new StandsArea().withName("ח'- חלם");
		StandsArea standsAreaCinematheque = new StandsArea().withName("סינמטק");

		return new ConventionMap()
			.withFloors(Collections.singletonList(floor))
			.withLocations(
				CollectionUtils.flattenList(
					inFloor(floor,
						mapLocation("כניסה ויציאה", 222.8435f, 2815.011f),
						mapLocation("יציאת חירום", 354.0935f, 2241.987f),
						mapLocation("מודיעין", 617.5815f, 2664.266f),
						mapLocation("זירה", 999.5705f, 2656.62f),
						mapLocation("יריד הדוכנים A", Collections.singletonList(standsAreaA), 916.8625f, 2247.276f),
						mapLocation("יריד הדוכנים B", Collections.singletonList(standsAreaB), 720.7565f, 2185.373f),
						mapLocation("יריד הדוכנים C", Collections.singletonList(standsAreaC), 1120.9395f, 2177.496f),
						mapLocation("יריד הדוכנים D", Collections.singletonList(standsAreaD), 915.6255f, 1995.577f),
						mapLocation("יריד הדוכנים E", Collections.singletonList(standsAreaE), 1586.6745f, 2309.637f),
						mapLocation("יריד הדוכנים F", Collections.singletonList(standsAreaF), 1009.7235f, 1840.217f),
						mapLocation("יריד הדוכנים F", Collections.singletonList(standsAreaF), 1591.3565f, 1900.534f),
						mapLocation("שירותי נשים", 1288.3165f, 1742.721f),
						mapLocation(eshkol1, 1123.6445f, 1730.957f).withMarkerResource(R.drawable.olamot2026_place_red, false).withSelectedMarkerResource(R.drawable.olamot2026_place_red_selected, false),
						mapLocation(eshkol2, 809.8425f, 1739.515f),
						mapLocation("שירותי גברים", 540.6735f, 1748.117f),
						mapLocation("משחקי שער", 1320.0415f, 1436.399f),
//						mapLocation("דוכני עמותות", Arrays.asList(icode, getActivitiesActivityLocationForView(R.id.activities_icode)), 1084.492f, 1504.888f).withMarkerHeight(SMALL_MARKER_HEIGHT),
						mapLocation("עמדת הדפסת כרטיסים עצמאית", 778.309f, 1521.169f).withMarkerHeight(SMALL_MARKER_HEIGHT),
						mapLocation(eshkol5, 1146.046f, 1427.014f).withMarkerHeight(SMALL_MARKER_HEIGHT),
						mapLocation(eshkol4, 912.676f, 1382.617f).withMarkerHeight(SMALL_MARKER_HEIGHT),
						mapLocation(eshkol3, 724.935f, 1431.951f).withMarkerHeight(SMALL_MARKER_HEIGHT),
//						mapLocation(spaceship.getName(), Arrays.asList(spaceship, getActivitiesActivityLocationForView(R.id.activities_spaceship)), 1274.4095f, 1005.949f),
						mapLocation("דוכנים", Collections.singletonList(standsAreaEshkol), 775.3735f, 1069.155f),
//						mapLocation("מתחם משחקי אינדי", Collections.singletonList(getActivitiesActivityLocationForView(R.id.activities_indie)), 569.3475f, 1012.53f),
						mapLocation("מודיעין (אשכול)", 670.1375f, 793.045f),
						mapLocation("יציאה בלבד", 857.5545f, 697.683f),
						mapLocation("יריד הדוכנים H", Collections.singletonList(standsAreaH), 1589.8035f, 757.029f),
						mapLocation("אולם ספורט", 1659.7675f, 3238.266f),
						mapLocation("דוכני POP-UP P", Collections.singletonList(standsAreaP), 2068.1975f, 3400.144f),
						mapLocation("יריד הדוכנים G", Collections.singletonList(standsAreaG), 2032.4585f, 2855.851f),
						mapLocation("כניסה נגישה לעירוני מפלס תחתון", 1992.3625f, 2428.712f),
						mapLocation("יריד הדוכנים G", Collections.singletonList(standsAreaG), 2032.9835f, 2115.124f),
						mapLocation("יריד הדוכנים H", Collections.singletonList(standsAreaH), 2496.2685f, 681.063f),
						mapLocation("כניסה ויציאה", 2876.3195f, 528.572f),
						mapLocation("מדרגה לעירוני מפלס עליון", 2370.3125f, 864.046f),
						mapLocation("שירותי נשים", 2451.592f, 1001.691f).withMarkerHeight(SMALL_MARKER_HEIGHT),
						mapLocation("שירותי גברים", 2201.53f, 998.398f).withMarkerHeight(SMALL_MARKER_HEIGHT),
						mapLocationForShelter("מדרגות למרחב מוגן", 2252.9615f, 1217.837f),
						mapLocation("קוספליי נשים", 2530.478f, 1176.302f).withMarkerHeight(SMALL_MARKER_HEIGHT),
						mapLocation(kids, 2531.122f, 1338.107f).withMarkerHeight(SMALL_MARKER_HEIGHT),
						mapLocation(meetings, 2528.306f, 1528.487f).withMarkerHeight(SMALL_MARKER_HEIGHT),
//						mapLocation(workshops1, 2528.304f, 1738.745f).withMarkerHeight(SMALL_MARKER_HEIGHT),
						mapLocation("יד שנייה", 2526.612f, 1905.99f).withMarkerHeight(SMALL_MARKER_HEIGHT),
						mapLocation("המתחם הקהילתי", 2868.9835f, 1561.953f),
						mapLocation("כניסה נגישה לעירוני מפלס עליון", 2842.7335f, 1942.268f),
						mapLocationForShelter("מדרגות למרחב מוגן", 2678.2485f, 2150.535f),
//						mapLocation("מדרגות לעירוני 1-10, סדנאות 2, משחקי קופסה 1-2", Arrays.asList(workshops2, games1, games2, ironi1, ironi2, ironi3, ironi4, ironi5, ironi6, ironi7, ironi8, ironi9, ironi10), 2525.5655f, 2244.343f),
						mapLocation("תיקון קוספליי", 2216.4955f, 2080.863f),
						mapLocation("שירותי יוניסקס", 2340.3565f, 2397.447f),
						mapLocation("הוביטון", 2338.9185f, 2612.021f),
						mapLocation("קוספליי גברים", 2344.7885f, 2878.327f),
						mapLocation("שמירת חפצים", 2281.1475f, 3076.912f),
						mapLocation("סוכה", 2797.4695f, 2564.641f),
						mapLocation("השטיח האדום", 2579.2545f, 2892.149f),
						mapLocation("כניסה ויציאה", 2844.9555f, 3126.908f),
						mapLocation("כניסה ויציאה", 385.7605f, 3574.662f),
						mapLocation("כניסה ויציאה", 285.5005f, 3318.433f),
//						mapLocation("מדרגות לסינמטק 3+4", Arrays.asList(cinematheque3, cinematheque4), 658.2755f, 3530.08f),
//						mapLocation("מעלית לסינמטק 3+4", Arrays.asList(cinematheque3, cinematheque4), 952.1975f, 3462.456f),
//						mapLocation("עמדת קונסולות VR", Collections.singletonList(getActivitiesActivityLocationForView(R.id.activities_glhf)), 1049.1145f, 3309.571f),
						mapLocation("מודיעין (סינמטק)", 579.7445f, 3309.953f),
						mapLocation("קופות", 1166.8555f, 3196.949f),
						mapLocation("דוכן Out&About", Collections.singletonList(standsAreaCinematheque), 902.1805f, 3076.215f),
//						mapLocation(cinematheque5, 1194.1535f, 2938.762f),
						mapLocation("מתחם משחקי לוח", 714.6065f, 2948.433f),
						mapLocationForShelter("מרחב מוגן", 819.907f, 3438.442f).withMarkerHeight(SMALL_MARKER_HEIGHT)
					)
				)
			);
	}

	private DetailsActivityLocation getActivitiesActivityLocationForView(int viewId) {
		Bundle bundle = new Bundle();
		if (viewId != Views.NO_VIEW) {
			bundle.putInt(ActivitiesActivity.EXTRA_FOCUS_ON_VIEW, viewId);
		}
		return new DetailsActivityLocation().withName("פעילויות").withActivityClass(ActivitiesActivity.class).withBundle(bundle);
	}

	private MapLocation mapLocation(String name, float x, float y) {
		return mapLocation(name, null, x, y);
	}

	private MapLocation mapLocation(Place place, float x, float y) {
		return mapLocation(null, Collections.singletonList(place), x, y);
	}

	private MapLocation mapLocation(String name, List<? extends Place> places, float x, float y) {
		final int DEFAULT_MARKER = R.drawable.olamot2026_place;
		final int DEFAULT_MARKER_TINT_RES = MapLocation.NO_TINT;
		final int DEFAULT_SELECTED_MARKER = R.drawable.olamot2026_place_selected;
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

	private MapLocation mapLocationForShelter(String name, float x, float y) {
		return mapLocation(new Shelter().withName(name), x, y)
			.withMarkerResource(R.drawable.olamot2026_place_red, false, MapLocation.NO_TINT)
			.withSelectedMarkerResource(R.drawable.olamot2026_place_red_selected, false, MapLocation.NO_TINT);
	}

	@Override
	protected double initLongitude() {
		// Ironi
		return 34.7845003;
	}

	@Override
	protected double initLatitude() {
		// Ironi
		return 32.0707265;
	}

	@Override
	protected ImageIdToImageResourceMapper initImageMapper() {
		ImageIdToImageResourceMapper imageMapper = new ImageIdToImageResourceMapper();

//		imageMapper.addLogoMapping(ImageIdToImageResourceMapper.EVENT_GENERIC, R.drawable....);

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
					.withOsEntry("entry.1637672939")
					.withVersionEntry("entry.757753933")
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
					.withOsEntry("entry.2141102636")
					.withVersionEntry("entry.1078400994")
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
	public URL getSecondHandItemsURL(int itemStatus, int formStatus) {
		try {
			return new URL(YAD2_API + "allItems?status=" + itemStatus + "&formStatus=" + formStatus);
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public URL getSecondHandGoToCreateFormsURL() {
		try {
			return new URL("https://yadash.sf-f.org.il/");
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

	@Override
	public HttpURLConnection getUserQRRequest(String token, String user) throws Exception {
		URL url = new URL("https://api.sf-f.org.il/cons/qr/byToken");
		HttpURLConnection request = HttpConnectionCreator.createConnection(url);

		request.setRequestMethod("GET");
		request.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		request.setDoInput(true);
		request.setDoOutput(true);

		OutputStream outputStream = request.getOutputStream();
		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
		writer.write("token=" + URLUtils.encodeURLParameterValue(token) + "&email=" + URLUtils.encodeURLParameterValue(user));
		writer.flush();

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
				"https://sso.sf-f.org.il/realms/sf-f/protocol/openid-connect/auth",
				"https://sso.sf-f.org.il/realms/sf-f/protocol/openid-connect/token",
				"https://sso.sf-f.org.il/realms/sf-f/protocol/openid-connect/logout",
				"https://sso.sf-f.org.il/realms/sf-f/protocol/openid-connect/userinfo"
		);
	}
}

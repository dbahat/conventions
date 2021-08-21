package amai.org.conventions.model.conventions;

import android.text.TextUtils;

import com.google.gson.JsonObject;

import java.io.BufferedWriter;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
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
import amai.org.conventions.networking.ModelParser;
import amai.org.conventions.networking.SffModelParser;
import amai.org.conventions.utils.CollectionUtils;
import amai.org.conventions.utils.ConventionStorage;
import amai.org.conventions.utils.Dates;
import amai.org.conventions.utils.HttpConnectionCreator;
import amai.org.conventions.utils.URLUtils;
import sff.org.conventions.R;

public class Icon2021Convention extends SffConvention {
//	private static final String HALL_NAME_CINEMATHEQUE_1_3_4 = "סינמטק 1, 3, 4";
//	private static final String HALL_NAME_CINEMATHEQUE_2 = "סינמטק 2";
//	private static final String HALL_NAME_CINEMATHEQUE_5 = "סינמטק 5";
	private static final String HALL_NAME_ESHKOL_1 = "אשכול 1";
	private static final String HALL_NAME_ESHKOL_2 = "אשכול 2";
	private static final String HALL_NAME_ESHKOL_3 = "אשכול 3";
//	private static final String HALL_NAME_ESHKOL_4 = "אשכול 4";
//	private static final String HALL_NAME_ESHKOL_5 = "אשכול 5";
//	private static final String HALL_NAME_ESHKOL_6 = "אשכול 6";
//	private static final String HALL_NAME_WORKSHOPS_1 = "סדנאות 1";
//	private static final String HALL_NAME_WORKSHOPS_2 = "סדנאות 2";
//	private static final String HALL_NAME_KIDS = "חדר ילדים";
//	private static final String HALL_NAME_MEETINGS = "חדר מפגשים";
//	private static final String HALL_NAME_OUTSIDE = "אירועי חוצות";
//	private static final String HALL_NAME_TENT_1 = "אוהל 1";
//	private static final String HALL_NAME_TENT_2 = "אוהל 2";
//	private static final String HALL_NAME_TENT_3 = "אוהל 3";
//	private static final String HALL_NAME_TENT_4 = "אוהל 4";
//	private static final String HALL_NAME_TENT_5 = "אוהל 5";
//	private static final String HALL_NAME_TENT_6 = "אוהל 6";
//	private static final String HALL_NAME_TENT_7 = "אוהל 7";
//	private static final String HALL_NAME_TENT_8 = "אוהל 8";
//	private static final String HALL_NAME_IRONI_1 = "עירוני 1";
//	private static final String HALL_NAME_IRONI_2 = "עירוני 2";
//	private static final String HALL_NAME_IRONI_3 = "עירוני 3";
//	private static final String HALL_NAME_IRONI_4 = "עירוני 4";
//	private static final String HALL_NAME_IRONI_5 = "עירוני 5";
//	private static final String HALL_NAME_IRONI_6 = "עירוני 6";
//	private static final String HALL_NAME_IRONI_7 = "עירוני 7";
//	private static final String HALL_NAME_ARTEMIS = "ארטמיס";
//	private static final String HALL_NAME_MINIATURES_1 = "מיניאטורות 1";
//	private static final String HALL_NAME_MINIATURES_2 = "מיניאטורות 2";
//	private static final String HALL_NAME_MINIATURES_DEMO = "הדגמות מיניאטורות";
//	private static final String HALL_NAME_MINIATURES_WORKSHOP = "סדנאות מיניאטורות";
//	private static final String HALL_NAME_GAMES_1 = "משחקים 1";
//	private static final String HALL_NAME_GAMES_2 = "משחקים 2";
//	private static final String HALL_NAME_GAMES_3 = "משחקים 3";
//	private static final String HALL_NAME_GAMES_4 = "משחקים 4";
	private static final String HALL_NAME_MEETINGS = "מפגשים/סדנאות";

	private static final String API_SLUG = "olamot2021";
	private static final String TEST_API_SLUG = "test_con";
	private static final String YAD2_API = "https://api.sf-f.org.il/yad2/";
	private static final String TEST_YAD2_API = "https://test.api.sf-f.org.il/yad2/";

	@Override
	protected ConventionStorage initStorage() {
		return new ConventionStorage(this, R.raw.olamot2021_convention_events, 0);
	}

	@Override
	protected Calendar initStartDate() {
		return Dates.createDate(2021, Calendar.SEPTEMBER, 22);
	}

	@Override
	protected Calendar initEndDate() {
		return Dates.createDate(2021, Calendar.SEPTEMBER, 26);
	}

	@Override
	protected String initID() {
		return "Icon2021";
	}

	@Override
	protected String initDisplayName() {
		return "פסטיבל אייקון 2021";
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
//				new Hall().withName(HALL_NAME_ESHKOL_4),
//				new Hall().withName(HALL_NAME_ESHKOL_5),
//				new Hall().withName(HALL_NAME_ESHKOL_6),
//				new Hall().withName(HALL_NAME_WORKSHOPS_1),
//				new Hall().withName(HALL_NAME_WORKSHOPS_2),
//				new Hall().withName(HALL_NAME_KIDS),
//				new Hall().withName(HALL_NAME_MEETINGS),
//				new Hall().withName(HALL_NAME_OUTSIDE),
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
//				new Hall().withName(HALL_NAME_ARTEMIS),
//				new Hall().withName(HALL_NAME_MINIATURES_1),
//				new Hall().withName(HALL_NAME_MINIATURES_2),
//				new Hall().withName(HALL_NAME_MINIATURES_DEMO),
//				new Hall().withName(HALL_NAME_MINIATURES_WORKSHOP)
//				new Hall().withName(HALL_NAME_GAMES_1),
//				new Hall().withName(HALL_NAME_GAMES_2),
//				new Hall().withName(HALL_NAME_GAMES_3),
//				new Hall().withName(HALL_NAME_GAMES_4),
				new Hall().withName(HALL_NAME_MEETINGS)
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
//		return createMap();
		return null;
	}

	private ConventionMap createMap() {
//		Hall cinematheque1_3_4 = getHalls().findByName(HALL_NAME_CINEMATHEQUE_1_3_4);
//		Hall cinematheque2 = getHalls().findByName(HALL_NAME_CINEMATHEQUE_2);
//		Hall cinematheque5 = getHalls().findByName(HALL_NAME_CINEMATHEQUE_5);
//		Hall eshkol1 = getHalls().findByName(HALL_NAME_ESHKOL_1);
//		Hall eshkol2 = getHalls().findByName(HALL_NAME_ESHKOL_2);
//		Hall eshkol3 = getHalls().findByName(HALL_NAME_ESHKOL_3);
//		Hall eshkol4 = getHalls().findByName(HALL_NAME_ESHKOL_4);
//		Hall eshkol5 = getHalls().findByName(HALL_NAME_ESHKOL_5);
//		Hall eshkol6 = getHalls().findByName(HALL_NAME_ESHKOL_6);
//		Hall workshops1 = getHalls().findByName(HALL_NAME_WORKSHOPS_1);
//		Hall workshops2 = getHalls().findByName(HALL_NAME_WORKSHOPS_2);
//		Hall kids = getHalls().findByName(HALL_NAME_KIDS);
//		Hall meetings = getHalls().findByName(HALL_NAME_MEETINGS);
//		Hall tent1 = getHalls().findByName(HALL_NAME_TENT_1);
//		Hall tent2 = getHalls().findByName(HALL_NAME_TENT_2);
//		Hall tent3 = getHalls().findByName(HALL_NAME_TENT_3);
//		Hall tent4 = getHalls().findByName(HALL_NAME_TENT_4);
//		Hall tent5 = getHalls().findByName(HALL_NAME_TENT_5);
//		Hall tent6 = getHalls().findByName(HALL_NAME_TENT_6);
//		Hall tent7 = getHalls().findByName(HALL_NAME_TENT_7);
//		Hall tent8 = getHalls().findByName(HALL_NAME_TENT_8);
//		Hall ironi1 = getHalls().findByName(HALL_NAME_IRONI_1);
//		Hall ironi2 = getHalls().findByName(HALL_NAME_IRONI_2);
//		Hall ironi3 = getHalls().findByName(HALL_NAME_IRONI_3);
//		Hall ironi4 = getHalls().findByName(HALL_NAME_IRONI_4);
//		Hall ironi5 = getHalls().findByName(HALL_NAME_IRONI_5);
//		Hall ironi6 = getHalls().findByName(HALL_NAME_IRONI_6);
//		Hall ironi7 = getHalls().findByName(HALL_NAME_IRONI_7);
//		Hall artemis = getHalls().findByName(HALL_NAME_ARTEMIS);
//		Hall miniatures1 = getHalls().findByName(HALL_NAME_MINIATURES_1);
//		Hall miniatures2 = getHalls().findByName(HALL_NAME_MINIATURES_2);
//		Hall miniaturesDemo = getHalls().findByName(HALL_NAME_MINIATURES_DEMO);
//		Hall miniaturesWorkshop = getHalls().findByName(HALL_NAME_MINIATURES_WORKSHOP);
//
//		Floor overview = new Floor(1)
//				.withName("מפת המתחם")
//				.withImageResource(R.drawable.icon2019_map_overview, false)
//				.withImageWidth(2894.71069f)
//				.withImageHeight(2378.15063f);
//		Floor eshkolFloor1 = new Floor(2)
//				.withName("אשכול - קומה 1")
//				.withImageResource(R.drawable.icon2019_map_eshkol_floor1, false)
//				.withImageWidth(2287.38306f)
//				.withImageHeight(2060.48608f);
//		Floor eshkolFloor2 = new Floor(3)
//				.withName("אשכול - קומה 2")
//				.withImageResource(R.drawable.icon2019_map_eshkol_floor2, false)
//				.withImageWidth(2182.39014f)
//				.withImageHeight(1615.65161f);
//		Floor ironiFloor1 = new Floor(4)
//				.withName("עירוני - קומה 1")
//				.withImageResource(R.drawable.icon2019_map_ironi_floor1, false)
//				.withImageWidth(1426.11047f)
//				.withImageHeight(2574.49536f);
//		Floor ironiFloor2 = new Floor(5)
//				.withName("עירוני - קומה 2")
//				.withImageResource(R.drawable.icon2019_map_ironi_floor2, false)
//				.withImageWidth(1085.93945f)
//				.withImageHeight(2454.13403f);
//
		return new ConventionMap();
//				.withFloors(Arrays.asList(overview, eshkolFloor1, eshkolFloor2, ironiFloor1, ironiFloor2))
//				.withLocations(
//						CollectionUtils.flattenList(
//								inFloor(overview,
//										new MapLocation()
//												.withPlace(new Place().withName("השטיח האדום"))
//												.withMarkerResource(R.raw.icon2019_marker_red_carpet, true)
//												.withSelectedMarkerResource(R.raw.icon2019_marker_red_carpet_selected, true)
//												.withMarkerHeight(191.645f)
//												.withX(2276.299f)
//												.withY(1846.814f),
//										new MapLocation()
//												.withPlace(new FloorLocation().withName("עירוני").withFloor(ironiFloor1))
//												.withMarkerResource(R.raw.icon2019_marker_ironi, true)
//												.withSelectedMarkerResource(R.raw.icon2019_marker_ironi_selected, true)
//												.withMarkerHeight(280.509f)
//												.withX(2049.089f)
//												.withY(1340.349f),
//										new MapLocation()
//												.withPlace(new Place().withName("הפונדק"))
//												.withMarkerResource(R.raw.icon2019_marker_inn, true)
//												.withSelectedMarkerResource(R.raw.icon2019_marker_inn_selected, true)
//												.withMarkerHeight(180.084f)
//												.withX(2278.656f)
//												.withY(705.887f),
//										new MapLocation()
//												.withPlace(new Place().withName("סוכה"))
//												.withMarkerResource(R.raw.icon2019_marker_sukkah, true)
//												.withSelectedMarkerResource(R.raw.icon2019_marker_sukkah_selected, true)
//												.withMarkerHeight(90.042f)
//												.withX(2163.148f)
//												.withY(642.681f),
//										new MapLocation()
//												.withPlace(new Place().withName("מגרש החנייה"))
//												.withMarkerResource(R.raw.icon2019_marker_parking_lot, true)
//												.withSelectedMarkerResource(R.raw.icon2019_marker_parking_lot_selected, true)
//												.withMarkerHeight(280.508f)
//												.withX(2518.84f)
//												.withY(221.51f),
//										new MapLocation()
//												.withPlace(new Place().withName("דוכנים"))
//												.withMarkerResource(R.raw.icon2019_marker_stands, true)
//												.withSelectedMarkerResource(R.raw.icon2019_marker_stands_selected, true)
//												.withMarkerHeight(152.38f)
//												.withX(2121.682f)
//												.withY(304.551f),
//										new MapLocation()
//												.withPlace(new Place().withName("אוהל משחקי לוח"))
//												.withMarkerResource(R.raw.icon2019_marker_board_games_tent, true)
//												.withSelectedMarkerResource(R.raw.icon2019_marker_board_games_tent_selected, true)
//												.withMarkerHeight(195.132f)
//												.withX(1870.159f)
//												.withY(2173.019f),
//										new MapLocation()
//												.withPlace(new Place().withName("אולם הספורט"))
//												.withMarkerResource(R.raw.icon2019_marker_sports, true)
//												.withSelectedMarkerResource(R.raw.icon2019_marker_sports_selected, true)
//												.withMarkerHeight(293.724f)
//												.withX(1561.978f)
//												.withY(1920.327f),
//										new MapLocation()
//												.withPlace(new Place().withName("דוכנים"))
//												.withMarkerResource(R.raw.icon2019_marker_stands, true)
//												.withSelectedMarkerResource(R.raw.icon2019_marker_stands_selected, true)
//												.withMarkerHeight(152.38f)
//												.withX(1525.478f)
//												.withY(1350.792f),
//										new MapLocation()
//												.withPlace(new Place().withName("פרגולות"))
//												.withMarkerResource(R.raw.icon2019_marker_pergolas, true)
//												.withSelectedMarkerResource(R.raw.icon2019_marker_pergolas_selected, true)
//												.withMarkerHeight(169.393f)
//												.withX(1608.111f)
//												.withY(764.352f),
//										new MapLocation()
//												.withPlace(new Place().withName("מעגלי ישיבה"))
//												.withMarkerResource(R.raw.icon2019_marker_sitting_circles, true)
//												.withSelectedMarkerResource(R.raw.icon2019_marker_sitting_circles_selected, true)
//												.withMarkerHeight(177.343f)
//												.withX(1757.637f)
//												.withY(523.896f),
//										new MapLocation()
//												.withName("אוהל משחקי תפקידים")
//												.withPlaces(Arrays.asList(tent1, tent2, tent3, tent4, tent5, tent6, tent7, tent8))
//												.withMarkerResource(R.raw.icon2019_marker_roleplay_tent, true)
//												.withSelectedMarkerResource(R.raw.icon2019_marker_roleplay_tent_selected, true)
//												.withMarkerHeight(180.084f)
//												.withX(1313.712f)
//												.withY(1621.241f),
//										new MapLocation()
//												.withPlace(new FloorLocation().withName("אשכול").withFloor(eshkolFloor1))
//												.withMarkerResource(R.raw.icon2019_marker_eshkol, true)
//												.withSelectedMarkerResource(R.raw.icon2019_marker_eshkol_selected, true)
//												.withMarkerHeight(280.508f)
//												.withX(1295.398f)
//												.withY(706.997f),
//										new MapLocation()
//												.withName("סינמטק 1,2")
//												.withPlaces(Arrays.asList(cinematheque1_3_4, cinematheque2))
//												.withMarkerResource(R.raw.icon2019_marker_cinematheque1_2, true)
//												.withSelectedMarkerResource(R.raw.icon2019_marker_cinematheque1_2_selected, true)
//												.withMarkerHeight(147.9f)
//												.withX(870.939f)
//												.withY(2122.685f),
//										new MapLocation()
//												.withPlace(new Place().withName("סינמטק תל אביב"))
//												.withMarkerResource(R.raw.icon2019_marker_cinematheque, true)
//												.withSelectedMarkerResource(R.raw.icon2019_marker_cinematheque_selected, true)
//												.withMarkerHeight(293.723f)
//												.withX(1015.874f)
//												.withY(1867.227f),
//										new MapLocation()
//												.withName("מתחם נינטנדו ישראל + סינמטק 3,4,5")
//												.withPlaces(Arrays.asList(cinematheque1_3_4, cinematheque5))
//												.withMarkerResource(R.raw.icon2019_marker_cinematheque3_4_5, true)
//												.withSelectedMarkerResource(R.raw.icon2019_marker_cinematheque3_4_5_selected, true)
//												.withMarkerHeight(147.9f)
//												.withX(870.938f)
//												.withY(1821.259f),
//										new MapLocation()
//												.withPlace(new Place().withName("אוהל קופות"))
//												.withMarkerResource(R.raw.icon2019_marker_cashiers, true)
//												.withSelectedMarkerResource(R.raw.icon2019_marker_cashiers_selected, true)
//												.withMarkerHeight(172.351f)
//												.withX(976.367f)
//												.withY(1576.126f),
//										new MapLocation()
//												.withPlace(new Place().withName("דוכני עמותות"))
//												.withMarkerResource(R.raw.icon2019_marker_organizations, true)
//												.withSelectedMarkerResource(R.raw.icon2019_marker_organizations_selected, true)
//												.withMarkerHeight(114.977f)
//												.withX(1076.401f)
//												.withY(1541.205f),
//										new MapLocation()
//												.withPlace(new Place().withName("הזירה"))
//												.withMarkerResource(R.raw.icon2019_marker_arena, true)
//												.withSelectedMarkerResource(R.raw.icon2019_marker_arena_selected, true)
//												.withMarkerHeight(147.9f)
//												.withX(1178.757f)
//												.withY(1269.692f),
//										new MapLocation()
//												.withPlace(new Place().withName("מודיעין"))
//												.withMarkerResource(R.raw.icon2019_marker_information, true)
//												.withSelectedMarkerResource(R.raw.icon2019_marker_information_selected, true)
//												.withMarkerHeight(130.693f)
//												.withX(806.816f)
//												.withY(1462.614f, true),
//										new MapLocation()
//												.withName("מיניאטורות")
//												.withPlaces(Arrays.asList(miniaturesDemo, miniaturesWorkshop))
//												.withMarkerResource(R.raw.icon2019_marker_miniatures, true)
//												.withSelectedMarkerResource(R.raw.icon2019_marker_miniatures_selected, true)
//												.withMarkerHeight(245.457f)
//												.withX(982.381f)
//												.withY(417.436f),
//										new MapLocation()
//												.withPlace(new Place().withName("מודיעין"))
//												.withMarkerResource(R.raw.icon2019_marker_information2, true)
//												.withSelectedMarkerResource(R.raw.icon2019_marker_information2_selected, true)
//												.withMarkerHeight(130.693f)
//												.withX(1100.559f)
//												.withY(288.687f, true)
//								),
//								inFloor(eshkolFloor1,
//										new MapLocation()
//												.withPlace(new Place().withName("יד שנייה"))
//												.withMarkerResource(R.raw.icon2019_marker_second_hand, true)
//												.withSelectedMarkerResource(R.raw.icon2019_marker_second_hand_selected, true)
//												.withMarkerHeight(245.459f)
//												.withX(1911.432f)
//												.withY(1735.088f),
//										new MapLocation()
//												.withPlace(eshkol1)
//												.withMarkerResource(R.raw.icon2019_marker_eshkol1, true)
//												.withSelectedMarkerResource(R.raw.icon2019_marker_eshkol1_selected, true)
//												.withMarkerHeight(245.459f)
//												.withX(1320.827f)
//												.withY(1692.635f),
//										new MapLocation()
//												.withPlace(eshkol2)
//												.withMarkerResource(R.raw.icon2019_marker_eshkol2, true)
//												.withSelectedMarkerResource(R.raw.icon2019_marker_eshkol2_selected, true)
//												.withMarkerHeight(245.459f)
//												.withX(653.443f)
//												.withY(1694.646f),
//										new MapLocation()
//												.withPlace(new Place().withName("שירותי נשים"))
//												.withMarkerResource(R.raw.icon2019_marker_toilet_women, true)
//												.withSelectedMarkerResource(R.raw.icon2019_marker_toilet_women_selected, true)
//												.withMarkerHeight(280.511f)
//												.withX(1953.621f)
//												.withY(964.208f),
//										new MapLocation()
//												.withPlace(new FloorLocation().withName("עלייה לאשכול 5-6").withFloor(eshkolFloor2))
//												.withMarkerResource(R.drawable.icon2019_marker_stairs_eshkol5_6, false)
//												.withSelectedMarkerResource(R.drawable.icon2019_marker_stairs_eshkol5_6_selected, false)
//												.withMarkerHeight(200.454f)
//												.withX(1753.399f)
//												.withY(911.183f),
//										new MapLocation()
//												.withPlace(new Place().withName("דוכנים"))
//												.withMarkerResource(R.drawable.icon2019_marker_stands2, false)
//												.withSelectedMarkerResource(R.drawable.icon2019_marker_stands2_selected, false)
//												.withMarkerHeight(114.978f)
//												.withX(1224.364f)
//												.withY(1104.463f),
//										new MapLocation()
//												.withPlace(new FloorLocation().withName("עלייה לאשכול 3-4").withFloor(eshkolFloor2))
//												.withMarkerResource(R.raw.icon2019_marker_stairs_eshkol3_4, true)
//												.withSelectedMarkerResource(R.raw.icon2019_marker_stairs_eshkol3_4_selected, true)
//												.withMarkerHeight(200.454f)
//												.withX(677.429f)
//												.withY(924.277f),
//										new MapLocation()
//												.withPlace(new Place().withName("שירותי גברים"))
//												.withMarkerResource(R.raw.icon2019_marker_toilet_men, true)
//												.withSelectedMarkerResource(R.raw.icon2019_marker_toilet_men_selected, true)
//												.withMarkerHeight(280.511f)
//												.withX(487.743f)
//												.withY(964.208f),
//										new MapLocation()
//												.withPlace(new Place().withName("דוכנים"))
//												.withMarkerResource(R.drawable.icon2019_marker_stands3, false)
//												.withSelectedMarkerResource(R.drawable.icon2019_marker_stands3_selected, false)
//												.withMarkerHeight(114.977f)
//												.withX(1207.531f)
//												.withY(714.133f),
//										new MapLocation()
//												.withName("מיניאטורות")
//												.withPlaces(Arrays.asList(miniaturesDemo, miniaturesWorkshop))
//												.withMarkerResource(R.raw.icon2019_marker_miniatures, true)
//												.withSelectedMarkerResource(R.raw.icon2019_marker_miniatures_selected, true)
//												.withMarkerHeight(245.459f)
//												.withX(664.505f)
//												.withY(463.914f)
//								),
//								inFloor(eshkolFloor2,
//										new MapLocation()
//												.withPlace(eshkol6)
//												.withMarkerResource(R.raw.icon2019_marker_eshkol6, true)
//												.withSelectedMarkerResource(R.raw.icon2019_marker_eshkol6_selected, true)
//												.withMarkerHeight(245.459f)
//												.withX(1618.802f)
//												.withY(1318.714f),
//										new MapLocation()
//												.withPlace(eshkol5)
//												.withMarkerResource(R.raw.icon2019_marker_eshkol5, true)
//												.withSelectedMarkerResource(R.raw.icon2019_marker_eshkol5_selected, true)
//												.withMarkerHeight(245.459f)
//												.withX(1626.7f)
//												.withY(882.545f),
//										new MapLocation()
//												.withPlace(eshkol3)
//												.withMarkerResource(R.raw.icon2019_marker_eshkol3, true)
//												.withSelectedMarkerResource(R.raw.icon2019_marker_eshkol3_selected, true)
//												.withMarkerHeight(245.459f)
//												.withX(1082.071f)
//												.withY(946.003f),
//										new MapLocation()
//												.withPlace(eshkol4)
//												.withMarkerResource(R.raw.icon2019_marker_eshkol4, true)
//												.withSelectedMarkerResource(R.raw.icon2019_marker_eshkol4_selected, true)
//												.withMarkerHeight(245.459f)
//												.withX(499.145f)
//												.withY(904.887f),
//										new MapLocation()
//												.withPlace(new Place().withName("דוכנים"))
//												.withMarkerResource(R.drawable.icon2019_marker_stands4, false)
//												.withSelectedMarkerResource(R.drawable.icon2019_marker_stands4_selected, false)
//												.withMarkerHeight(114.978f)
//												.withX(1771.483f)
//												.withY(564.007f),
//										new MapLocation()
//												.withPlace(new Place().withName("דוכנים"))
//												.withMarkerResource(R.drawable.icon2019_marker_stands4, false)
//												.withSelectedMarkerResource(R.drawable.icon2019_marker_stands4_selected, false)
//												.withMarkerHeight(114.978f)
//												.withX(374.573f)
//												.withY(579.738f)
//								),
//								inFloor(ironiFloor1,
//										new MapLocation()
//												.withPlace(new Place().withName("שמירת חפצים"))
//												.withMarkerResource(R.drawable.icon2019_marker_storage, false)
//												.withSelectedMarkerResource(R.drawable.icon2019_marker_storage_selected, false)
//												.withMarkerHeight(230.207f)
//												.withX(680.566f)
//												.withY(2129.796f),
//										new MapLocation()
//												.withPlace(new FloorLocation().withName("מדרגות לקומות 2,3").withFloor(ironiFloor2))
//												.withMarkerResource(R.raw.icon2019_marker_stairs_ironi2_3, true)
//												.withSelectedMarkerResource(R.raw.icon2019_marker_stairs_ironi2_3_selected, true)
//												.withMarkerHeight(233.773f)
//												.withX(865.802f)
//												.withY(1611.711f),
//										new MapLocation()
//												.withPlace(new Place().withName("שירותים"))
//												.withMarkerResource(R.raw.icon2019_marker_toilet, true)
//												.withSelectedMarkerResource(R.raw.icon2019_marker_toilet_selected, true)
//												.withMarkerHeight(132.28f)
//												.withX(764.362f)
//												.withY(1751.418f),
//										new MapLocation()
//												.withPlace(new Place().withName("מודיעין"))
//												.withMarkerResource(R.drawable.icon2019_marker_information3, false)
//												.withSelectedMarkerResource(R.drawable.icon2019_marker_information3_selected, false)
//												.withMarkerHeight(124.64f)
//												.withX(853.831f)
//												.withY(1531.843f, true),
//										new MapLocation()
//												.withPlace(new Place().withName("הוביטון"))
//												.withMarkerResource(R.raw.icon2019_marker_hobitton, true)
//												.withSelectedMarkerResource(R.raw.icon2019_marker_hobitton_selected, true)
//												.withMarkerHeight(121.458f)
//												.withX(913.923f)
//												.withY(972.508f),
//										new MapLocation()
//												.withPlace(kids)
//												.withMarkerResource(R.raw.icon2019_marker_kids, true)
//												.withSelectedMarkerResource(R.raw.icon2019_marker_kids_selected, true)
//												.withMarkerHeight(146.873f)
//												.withX(900.548f)
//												.withY(791.421f),
//										new MapLocation()
//												.withPlace(workshops1)
//												.withMarkerResource(R.raw.icon2019_marker_workshops1, true)
//												.withSelectedMarkerResource(R.raw.icon2019_marker_workshops1_selected, true)
//												.withMarkerHeight(135.098f)
//												.withX(900.548f)
//												.withY(647.164f),
//										new MapLocation()
//												.withPlace(new Place().withName("קוספליי נשים"))
//												.withMarkerResource(R.raw.icon2019_marker_cosplay_women, true)
//												.withSelectedMarkerResource(R.raw.icon2019_marker_cosplay_women_selected, true)
//												.withMarkerHeight(151.361f)
//												.withX(930.673f)
//												.withY(499.106f),
//										new MapLocation()
//												.withPlace(new Place().withName("שירותי נשים"))
//												.withMarkerResource(R.drawable.icon2019_marker_toilet_women2, false)
//												.withSelectedMarkerResource(R.drawable.icon2019_marker_toilet_women2_selected, false)
//												.withMarkerHeight(171.611f)
//												.withX(952.313f)
//												.withY(343.617f, true),
//										new MapLocation()
//												.withPlace(new Place().withName("שירותי גברים"))
//												.withMarkerResource(R.drawable.icon2019_marker_toilet_men2, false)
//												.withSelectedMarkerResource(R.drawable.icon2019_marker_toilet_men2_selected, false)
//												.withMarkerHeight(174.229f)
//												.withX(706.595f)
//												.withY(345.794f, true)
//								),
//								inFloor(ironiFloor2,
//										new MapLocation()
//												.withName("מדרגות לקומה 3 עירוני 1-7 וארטמיס")
//												.withPlaces(Arrays.asList(ironi1, ironi2, ironi3, ironi4, ironi5, ironi6, ironi7, artemis))
//												.withMarkerResource(R.raw.icon2019_marker_stairs_ironi3, true)
//												.withSelectedMarkerResource(R.raw.icon2019_marker_stairs_ironi3_selected, true)
//												.withMarkerHeight(250.11f)
//												.withX(473.844f)
//												.withY(1660.607f),
//										new MapLocation()
//												.withPlace(new Place().withName("קוספליי גברים"))
//												.withMarkerResource(R.raw.icon2019_marker_cosplay_men, true)
//												.withSelectedMarkerResource(R.raw.icon2019_marker_cosplay_men_selected, true)
//												.withMarkerHeight(166.226f)
//												.withX(530.636f)
//												.withY(1214.508f),
//										new MapLocation()
//												.withPlace(miniatures1)
//												.withMarkerResource(R.raw.icon2019_marker_miniatures1, true)
//												.withSelectedMarkerResource(R.raw.icon2019_marker_miniatures1_selected, true)
//												.withMarkerHeight(158.172f)
//												.withX(530.691f)
//												.withY(1006.304f),
//										new MapLocation()
//												.withPlace(miniatures2)
//												.withMarkerResource(R.raw.icon2019_marker_miniatures2, true)
//												.withSelectedMarkerResource(R.raw.icon2019_marker_miniatures2_selected, true)
//												.withMarkerHeight(158.172f)
//												.withX(529.708f)
//												.withY(812.166f),
//										new MapLocation()
//												.withName("מפגשים")
//												.withPlace(meetings)
//												.withMarkerResource(R.raw.icon2019_marker_meetings, true)
//												.withSelectedMarkerResource(R.raw.icon2019_marker_meetings_selected, true)
//												.withMarkerHeight(121.459f)
//												.withX(530.808f)
//												.withY(656.768f),
//										new MapLocation()
//												.withPlace(workshops2)
//												.withMarkerResource(R.raw.icon2019_marker_workshops2, true)
//												.withSelectedMarkerResource(R.raw.icon2019_marker_workshops2_selected, true)
//												.withMarkerHeight(121.459f)
//												.withX(530.808f)
//												.withY(497.071f),
//										new MapLocation()
//												.withPlace(new Place().withName("שירותי נשים"))
//												.withMarkerResource(R.raw.icon2019_marker_toilet_women3, true)
//												.withSelectedMarkerResource(R.raw.icon2019_marker_toilet_women3_selected, true)
//												.withMarkerHeight(210.737f)
//												.withX(548.549f)
//												.withY(390.588f, true),
//										new MapLocation()
//												.withPlace(new Place().withName("שירותי גברים"))
//												.withMarkerResource(R.drawable.icon2019_marker_toilet_men3, false)
//												.withSelectedMarkerResource(R.drawable.icon2019_marker_toilet_men3_selected, false)
//												.withMarkerHeight(210.737f)
//												.withX(341.611f)
//												.withY(390.588f, true)
//								)
//						)
//				)
//		;
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

		imageMapper.addMapping(ImageIdToImageResourceMapper.EVENT_GENERIC, R.drawable.icon2021_background);

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
					.withSendUrl(new URL("https://docs.google.com/forms/d/e/1FAIpQLSfoz56q8nlspshth17qkkFkTxbgjCAXrIgirPemZPk7ZWvwsw/formResponse"));
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
					.withSendUrl(new URL("https://docs.google.com/forms/d/e/1FAIpQLSdUrN2YEaonwI-AvlCKnKxt9D1DovWNM6bZNQpysDK6t4ClWA/formResponse"));
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
					URLUtils.encodeURLParameterValue(user) + "&pass=" + URLUtils.encodeURLParameterValue(password));
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
					URLUtils.encodeURLParameterValue(user) + "&pass=" + URLUtils.encodeURLParameterValue(password));
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

	@Override
	public URL getAdditionalConventionFeedbackURL() {
		try {
			return new URL("https://docs.google.com/forms/d/e/1FAIpQLSd0ppYWffXNunk97MzPXT3qwE2vWhg3D1A2zYDX6VO1GmZdyA/viewform");
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public URL getAdditionalEventFeedbackURL(ConventionEvent event) {
		try {
			return new URL("https://docs.google.com/forms/d/e/1FAIpQLSfsRaPSOVmkeazFuFCmr2Q319nh8kw0eOxc76YBtGoYF1cz3g/viewform" +
					"?entry.1572016508=" + URLUtils.encodeURLParameterValue(event.getTitle()) +
					"&entry.1917108492=" + URLUtils.encodeURLParameterValue(event.getLecturer()) +
					"&entry.10889808=" + URLUtils.encodeURLParameterValue(event.getHall().getName()) +
					"&entry.1131737302=" + URLUtils.encodeURLParameterValue(Dates.formatDateAndTime(event.getStartTime()))
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
			// Only events in eshkol 1-3 are available from the convention website
			if (HALL_NAME_ESHKOL_1.equals(event.getHall().getName())) {
				return new URL("https://olamot2021.virtualcon.org.il/%D7%90%D7%95%D7%9C%D7%9E%D7%95%D7%AA-%D7%94%D7%AA%D7%95%D7%9B%D7%9F/hall1/");
			} else if (HALL_NAME_ESHKOL_2.equals(event.getHall().getName())) {
				return new URL("https://olamot2021.virtualcon.org.il/%D7%90%D7%95%D7%9C%D7%9E%D7%95%D7%AA-%D7%94%D7%AA%D7%95%D7%9B%D7%9F/hall2/");
			} else if (HALL_NAME_ESHKOL_3.equals(event.getHall().getName())) {
				return new URL("https://olamot2021.virtualcon.org.il/%D7%90%D7%95%D7%9C%D7%9E%D7%95%D7%AA-%D7%94%D7%AA%D7%95%D7%9B%D7%9F/hall3/");
			} else {
				return null;
			}
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public ConventionEvent.EventLocationType getEventLocationType(ConventionEvent event) {
//		if (HALL_NAME_ESHKOL_2.equals(event.getHall().getName())) {
//			return ConventionEvent.EventLocationType.VIRTUAL;
//		};
		return ConventionEvent.EventLocationType.PHYSICAL;
	}
}

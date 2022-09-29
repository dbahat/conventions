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
		return new ConventionStorage(this, R.raw.icon2022_convention_events, 1);
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
		Hall eshkol5 = getHalls().findByName(HALL_NAME_ESHKOL_5);
		Hall workshops = getHalls().findByName(HALL_NAME_WORKSHOPS);
		Hall meetings = getHalls().findByName(HALL_NAME_MEETINGS);
		Hall kids = getHalls().findByName(HALL_NAME_KIDS);
		Hall ironi1 = getHalls().findByName(HALL_NAME_IRONI_1);
		Hall ironi2 = getHalls().findByName(HALL_NAME_IRONI_2);
		Hall ironi3 = getHalls().findByName(HALL_NAME_IRONI_3);
		Hall ironi4 = getHalls().findByName(HALL_NAME_IRONI_4);
		Hall ironi5 = getHalls().findByName(HALL_NAME_IRONI_5);
		Hall ironi6 = getHalls().findByName(HALL_NAME_IRONI_6);
		Hall ironi7 = getHalls().findByName(HALL_NAME_IRONI_7);
		Hall ironi8 = getHalls().findByName(HALL_NAME_IRONI_8);
		Hall outside = getHalls().findByName(HALL_NAME_OUTSIDE);
		Hall arena = getHalls().findByName(HALL_NAME_ARENA);
		Hall tent20 = getHalls().findByName(HALL_NAME_TENT_20);
		Hall games1 = getHalls().findByName(HALL_NAME_GAMES_1);
		Hall games2 = getHalls().findByName(HALL_NAME_GAMES_2);

		Floor floor = new Floor(1)
				.withName("מפת מתחם")
				.withImageResource(R.raw.icon2022_map, true)
				.withImageHeight(1335)
				.withImageWidth(1398)
				.withDefaultMarkerHeight(65);
		final int BIG_MARKER_HEIGHT = 140;
		final int SMALL_MARKER_HEIGHT = 45;

		int DEFAULT_MARKER = R.drawable.icon2022_place;
		int DEFAULT_MARKER_TINT_RES = MapLocation.NO_TINT;
		int DEFAULT_SELECTED_MARKER = R.drawable.icon2022_place_selected;
		int DEFAULT_SELECTED_MARKER_TINT_RES = MapLocation.NO_TINT;
		return new ConventionMap()
			.withFloors(Collections.singletonList(floor))
			.withLocations(
				CollectionUtils.flattenList(
					inFloor(floor,
						new MapLocation()
							.withPlace(new Place().withName("כניסה מרחוב דה וינצ'י"))
							.withMarkerResource(DEFAULT_MARKER, false, DEFAULT_MARKER_TINT_RES)
							.withSelectedMarkerResource(DEFAULT_SELECTED_MARKER, false, DEFAULT_SELECTED_MARKER_TINT_RES)
							.withX(299)
							.withY(53),
						new MapLocation()
							.withPlace(new Place().withName("מודיעין"))
							.withMarkerResource(DEFAULT_MARKER, false, DEFAULT_MARKER_TINT_RES)
							.withSelectedMarkerResource(DEFAULT_SELECTED_MARKER, false, DEFAULT_SELECTED_MARKER_TINT_RES)
							.withX(195)
							.withY(107),
						new MapLocation()
							.withPlace(new Place().withName("דוכנים"))
							.withMarkerResource(DEFAULT_MARKER, false, DEFAULT_MARKER_TINT_RES)
							.withSelectedMarkerResource(DEFAULT_SELECTED_MARKER, false, DEFAULT_SELECTED_MARKER_TINT_RES)
							.withX(789)
							.withY(64),
						new MapLocation()
							.withPlace(new Place().withName("כניסה לעירוני"))
							.withMarkerResource(DEFAULT_MARKER, false, DEFAULT_MARKER_TINT_RES)
							.withSelectedMarkerResource(DEFAULT_SELECTED_MARKER, false, DEFAULT_SELECTED_MARKER_TINT_RES)
							.withX(936)
							.withY(77)
							.withMarkerHeight(SMALL_MARKER_HEIGHT),
						new MapLocation()
							.withPlace(new Place().withName("יציאת חירום"))
							.withMarkerResource(DEFAULT_MARKER, false, DEFAULT_MARKER_TINT_RES)
							.withSelectedMarkerResource(DEFAULT_SELECTED_MARKER, false, DEFAULT_SELECTED_MARKER_TINT_RES)
							.withX(1060)
							.withY(147),
						new MapLocation()
							.withName("אשכולות 3-5")
							.withPlaces(Arrays.asList(eshkol3, eshkol4, eshkol5))
							.withMarkerResource(DEFAULT_MARKER, false, DEFAULT_MARKER_TINT_RES)
							.withSelectedMarkerResource(DEFAULT_SELECTED_MARKER, false, DEFAULT_SELECTED_MARKER_TINT_RES)
							.withX(324)
							.withY(350)
							.withMarkerHeight(SMALL_MARKER_HEIGHT),
						new MapLocation()
							.withPlace(new Place().withName("שירותי גברים"))
							.withMarkerResource(DEFAULT_MARKER, false, DEFAULT_MARKER_TINT_RES)
							.withSelectedMarkerResource(DEFAULT_SELECTED_MARKER, false, DEFAULT_SELECTED_MARKER_TINT_RES)
							.withX(107)
							.withY(357)
							.withMarkerHeight(SMALL_MARKER_HEIGHT),
						new MapLocation()
							.withPlace(new Place().withName("שירותי נשים"))
							.withMarkerResource(DEFAULT_MARKER, false, DEFAULT_MARKER_TINT_RES)
							.withSelectedMarkerResource(DEFAULT_SELECTED_MARKER, false, DEFAULT_SELECTED_MARKER_TINT_RES)
							.withX(420)
							.withY(357)
							.withMarkerHeight(SMALL_MARKER_HEIGHT),
						new MapLocation()
							.withPlace(eshkol1)
							.withMarkerResource(DEFAULT_MARKER, false, DEFAULT_MARKER_TINT_RES)
							.withSelectedMarkerResource(DEFAULT_SELECTED_MARKER, false, DEFAULT_SELECTED_MARKER_TINT_RES)
							.withX(270)
							.withY(437),
						new MapLocation()
							.withPlace(eshkol2)
							.withMarkerResource(DEFAULT_MARKER, false, DEFAULT_MARKER_TINT_RES)
							.withSelectedMarkerResource(DEFAULT_SELECTED_MARKER, false, DEFAULT_SELECTED_MARKER_TINT_RES)
							.withX(144)
							.withY(437),
						new MapLocation()
							.withPlace(new Place().withName("דוכנים"))
							.withMarkerResource(DEFAULT_MARKER, false, DEFAULT_MARKER_TINT_RES)
							.withSelectedMarkerResource(DEFAULT_SELECTED_MARKER, false, DEFAULT_SELECTED_MARKER_TINT_RES)
							.withX(263)
							.withY(220)
							.withMarkerHeight(SMALL_MARKER_HEIGHT),
						new MapLocation()
							.withPlace(new Place().withName("שירותי גברים"))
							.withMarkerResource(DEFAULT_MARKER, false, DEFAULT_MARKER_TINT_RES)
							.withSelectedMarkerResource(DEFAULT_SELECTED_MARKER, false, DEFAULT_SELECTED_MARKER_TINT_RES)
							.withX(860)
							.withY(174)
							.withMarkerHeight(SMALL_MARKER_HEIGHT),
						new MapLocation()
							.withPlace(new Place().withName("שירותי נשים"))
							.withMarkerResource(DEFAULT_MARKER, false, DEFAULT_MARKER_TINT_RES)
							.withSelectedMarkerResource(DEFAULT_SELECTED_MARKER, false, DEFAULT_SELECTED_MARKER_TINT_RES)
							.withX(916)
							.withY(174)
							.withMarkerHeight(SMALL_MARKER_HEIGHT),
						new MapLocation()
							.withPlace(workshops)
							.withMarkerResource(DEFAULT_MARKER, false, DEFAULT_MARKER_TINT_RES)
							.withSelectedMarkerResource(DEFAULT_SELECTED_MARKER, false, DEFAULT_SELECTED_MARKER_TINT_RES)
							.withX(984)
							.withY(237)
							.withMarkerHeight(SMALL_MARKER_HEIGHT),
						new MapLocation()
							.withPlace(meetings)
							.withMarkerResource(DEFAULT_MARKER, false, DEFAULT_MARKER_TINT_RES)
							.withSelectedMarkerResource(DEFAULT_SELECTED_MARKER, false, DEFAULT_SELECTED_MARKER_TINT_RES)
							.withX(984)
							.withY(316)
							.withMarkerHeight(SMALL_MARKER_HEIGHT),
						new MapLocation()
							.withPlace(kids)
							.withMarkerResource(DEFAULT_MARKER, false, DEFAULT_MARKER_TINT_RES)
							.withSelectedMarkerResource(DEFAULT_SELECTED_MARKER, false, DEFAULT_SELECTED_MARKER_TINT_RES)
							.withX(984)
							.withY(387)
							.withMarkerHeight(SMALL_MARKER_HEIGHT),
						new MapLocation()
							.withPlace(new Place().withName("הוביטון"))
							.withMarkerResource(DEFAULT_MARKER, false, DEFAULT_MARKER_TINT_RES)
							.withSelectedMarkerResource(DEFAULT_SELECTED_MARKER, false, DEFAULT_SELECTED_MARKER_TINT_RES)
							.withX(984)
							.withY(465)
							.withMarkerHeight(SMALL_MARKER_HEIGHT),
						new MapLocation()
							.withPlace(new Place().withName("יד שניה"))
							.withMarkerResource(DEFAULT_MARKER, false, DEFAULT_MARKER_TINT_RES)
							.withSelectedMarkerResource(DEFAULT_SELECTED_MARKER, false, DEFAULT_SELECTED_MARKER_TINT_RES)
							.withX(984)
							.withY(538)
							.withMarkerHeight(SMALL_MARKER_HEIGHT),
						new MapLocation()
							.withPlace(new Place().withName("הפונדק"))
							.withMarkerResource(DEFAULT_MARKER, false, DEFAULT_MARKER_TINT_RES)
							.withSelectedMarkerResource(DEFAULT_SELECTED_MARKER, false, DEFAULT_SELECTED_MARKER_TINT_RES)
							.withX(1097)
							.withY(343),
						new MapLocation()
							.withPlace(new Place().withName("כניסה נגישה לעירוני"))
							.withMarkerResource(DEFAULT_MARKER, false, DEFAULT_MARKER_TINT_RES)
							.withSelectedMarkerResource(DEFAULT_SELECTED_MARKER, false, DEFAULT_SELECTED_MARKER_TINT_RES)
							.withX(1067)
							.withY(579),
						new MapLocation()
							.withName("מדרגות לעירוני 1-8")
							.withPlaces(Arrays.asList(ironi1, ironi2, ironi3, ironi4, ironi5, ironi6, ironi7, ironi8))
							.withMarkerResource(DEFAULT_MARKER, false, DEFAULT_MARKER_TINT_RES)
							.withSelectedMarkerResource(DEFAULT_SELECTED_MARKER, false, DEFAULT_SELECTED_MARKER_TINT_RES)
							.withX(964)
							.withY(639),
						new MapLocation()
							.withPlace(new Place().withName("שירותים"))
							.withMarkerResource(DEFAULT_MARKER, false, DEFAULT_MARKER_TINT_RES)
							.withSelectedMarkerResource(DEFAULT_SELECTED_MARKER, false, DEFAULT_SELECTED_MARKER_TINT_RES)
							.withX(904)
							.withY(733)
							.withMarkerHeight(SMALL_MARKER_HEIGHT),
						new MapLocation()
							.withPlace(new Place().withName("כניסה לעירוני"))
							.withMarkerResource(DEFAULT_MARKER, false, DEFAULT_MARKER_TINT_RES)
							.withSelectedMarkerResource(DEFAULT_SELECTED_MARKER, false, DEFAULT_SELECTED_MARKER_TINT_RES)
							.withX(727)
							.withY(799),
						new MapLocation()
							.withPlace(new Place().withName("הלבשה נשים"))
							.withMarkerResource(DEFAULT_MARKER, false, DEFAULT_MARKER_TINT_RES)
							.withSelectedMarkerResource(DEFAULT_SELECTED_MARKER, false, DEFAULT_SELECTED_MARKER_TINT_RES)
							.withX(904)
							.withY(833)
							.withMarkerHeight(SMALL_MARKER_HEIGHT),
						new MapLocation()
							.withPlace(new Place().withName("הלבשה גברים"))
							.withMarkerResource(DEFAULT_MARKER, false, DEFAULT_MARKER_TINT_RES)
							.withSelectedMarkerResource(DEFAULT_SELECTED_MARKER, false, DEFAULT_SELECTED_MARKER_TINT_RES)
							.withX(904)
							.withY(903)
							.withMarkerHeight(SMALL_MARKER_HEIGHT),
						new MapLocation()
							.withPlace(new Place().withName("שמירת חפצים"))
							.withMarkerResource(DEFAULT_MARKER, false, DEFAULT_MARKER_TINT_RES)
							.withSelectedMarkerResource(DEFAULT_SELECTED_MARKER, false, DEFAULT_SELECTED_MARKER_TINT_RES)
							.withX(904)
							.withY(1015),
						new MapLocation()
							.withPlace(new Place().withName("יציאת חירום"))
							.withMarkerResource(DEFAULT_MARKER, false, DEFAULT_MARKER_TINT_RES)
							.withSelectedMarkerResource(DEFAULT_SELECTED_MARKER, false, DEFAULT_SELECTED_MARKER_TINT_RES)
							.withX(1077)
							.withY(1093),
						new MapLocation()
							.withPlace(new Place().withName("דוכנים"))
							.withMarkerResource(DEFAULT_MARKER, false, DEFAULT_MARKER_TINT_RES)
							.withSelectedMarkerResource(DEFAULT_SELECTED_MARKER, false, DEFAULT_SELECTED_MARKER_TINT_RES)
							.withX(413)
							.withY(536),
						new MapLocation()
							.withPlace(new Place().withName("דוכנים"))
							.withMarkerResource(DEFAULT_MARKER, false, DEFAULT_MARKER_TINT_RES)
							.withSelectedMarkerResource(DEFAULT_SELECTED_MARKER, false, DEFAULT_SELECTED_MARKER_TINT_RES)
							.withX(745)
							.withY(940),
						new MapLocation()
							.withPlace(new Place().withName("דוכנים"))
							.withMarkerResource(DEFAULT_MARKER, false, DEFAULT_MARKER_TINT_RES)
							.withSelectedMarkerResource(DEFAULT_SELECTED_MARKER, false, DEFAULT_SELECTED_MARKER_TINT_RES)
							.withX(745)
							.withY(657),
						new MapLocation()
							.withPlace(arena)
							.withMarkerResource(DEFAULT_MARKER, false, DEFAULT_MARKER_TINT_RES)
							.withSelectedMarkerResource(DEFAULT_SELECTED_MARKER, false, DEFAULT_SELECTED_MARKER_TINT_RES)
							.withX(185)
							.withY(676),
						new MapLocation()
							.withPlace(new Place().withName("כניסה מרחוב הארבעה"))
							.withMarkerResource(DEFAULT_MARKER, false, DEFAULT_MARKER_TINT_RES)
							.withSelectedMarkerResource(DEFAULT_SELECTED_MARKER, false, DEFAULT_SELECTED_MARKER_TINT_RES)
							.withX(161)
							.withY(745)
							.withMarkerHeight(SMALL_MARKER_HEIGHT),
						new MapLocation()
							.withPlace(new Place().withName("מודיעין"))
							.withMarkerResource(DEFAULT_MARKER, false, DEFAULT_MARKER_TINT_RES)
							.withSelectedMarkerResource(DEFAULT_SELECTED_MARKER, false, DEFAULT_SELECTED_MARKER_TINT_RES)
							.withX(52)
							.withY(843),
						new MapLocation()
							.withPlace(new Place().withName("קופות"))
							.withMarkerResource(DEFAULT_MARKER, false, DEFAULT_MARKER_TINT_RES)
							.withSelectedMarkerResource(DEFAULT_SELECTED_MARKER, false, DEFAULT_SELECTED_MARKER_TINT_RES)
							.withX(542)
							.withY(847),
						new MapLocation()
							.withPlace(new Place().withName("דוכן האגודה"))
							.withMarkerResource(DEFAULT_MARKER, false, DEFAULT_MARKER_TINT_RES)
							.withSelectedMarkerResource(DEFAULT_SELECTED_MARKER, false, DEFAULT_SELECTED_MARKER_TINT_RES)
							.withX(125)
							.withY(843),
						new MapLocation()
							.withPlace(new Place().withName("מתחם משחקי שער"))
							.withMarkerResource(DEFAULT_MARKER, false, DEFAULT_MARKER_TINT_RES)
							.withSelectedMarkerResource(DEFAULT_SELECTED_MARKER, false, DEFAULT_SELECTED_MARKER_TINT_RES)
							.withX(206)
							.withY(889),
						new MapLocation()
							.withPlace(tent20)
							.withMarkerResource(DEFAULT_MARKER, false, DEFAULT_MARKER_TINT_RES)
							.withSelectedMarkerResource(DEFAULT_SELECTED_MARKER, false, DEFAULT_SELECTED_MARKER_TINT_RES)
							.withX(392)
							.withY(895),
						new MapLocation()
							.withPlace(new Place().withName("דוכן העמותה"))
							.withMarkerResource(DEFAULT_MARKER, false, DEFAULT_MARKER_TINT_RES)
							.withSelectedMarkerResource(DEFAULT_SELECTED_MARKER, false, DEFAULT_SELECTED_MARKER_TINT_RES)
							.withX(286)
							.withY(843),
						new MapLocation()
							.withPlace(new Place().withName("קפיטריה"))
							.withMarkerResource(DEFAULT_MARKER, false, DEFAULT_MARKER_TINT_RES)
							.withSelectedMarkerResource(DEFAULT_SELECTED_MARKER, false, DEFAULT_SELECTED_MARKER_TINT_RES)
							.withX(648)
							.withY(920),
						new MapLocation()
							.withPlace(new Place().withName("אולם הספורט"))
							.withMarkerResource(DEFAULT_MARKER, false, DEFAULT_MARKER_TINT_RES)
							.withSelectedMarkerResource(DEFAULT_SELECTED_MARKER, false, DEFAULT_SELECTED_MARKER_TINT_RES)
							.withX(599)
							.withY(1018),
						new MapLocation()
							.withName("אוהל משחקים")
							.withPlaces(Arrays.asList(games1, games2))
							.withMarkerResource(DEFAULT_MARKER, false, DEFAULT_MARKER_TINT_RES)
							.withSelectedMarkerResource(DEFAULT_SELECTED_MARKER, false, DEFAULT_SELECTED_MARKER_TINT_RES)
							.withX(823)
							.withY(1152),
						new MapLocation()
							.withName("השטיח האדום")
							.withPlace(outside)
							.withMarkerResource(DEFAULT_MARKER, false, DEFAULT_MARKER_TINT_RES)
							.withSelectedMarkerResource(DEFAULT_SELECTED_MARKER, false, DEFAULT_SELECTED_MARKER_TINT_RES)
							.withX(980)
							.withY(1052),
						new MapLocation()
							.withPlace(new Place().withName("סינמטק תל אביב"))
							.withMarkerResource(DEFAULT_MARKER, false, DEFAULT_MARKER_TINT_RES)
							.withSelectedMarkerResource(DEFAULT_SELECTED_MARKER, false, DEFAULT_SELECTED_MARKER_TINT_RES)
							.withX(208)
							.withY(1037)
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

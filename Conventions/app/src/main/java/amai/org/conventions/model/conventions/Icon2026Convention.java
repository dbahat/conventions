package amai.org.conventions.model.conventions;

import android.content.Context;
import android.graphics.BlendMode;
import android.os.Build;
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
import amai.org.conventions.model.ImageIdToImageResourceMapper;
import amai.org.conventions.model.MapLocation;
import amai.org.conventions.model.NamedItems;
import amai.org.conventions.model.OrderedNamedItems;
import amai.org.conventions.model.Place;
import amai.org.conventions.model.Shelter;
import amai.org.conventions.model.StandLocations;
import amai.org.conventions.model.StandLocationsBuilder;
import amai.org.conventions.model.StandType;
import amai.org.conventions.model.StandsArea;
import amai.org.conventions.utils.CollectionUtils;
import amai.org.conventions.utils.ConventionStorage;
import amai.org.conventions.utils.Dates;
import amai.org.conventions.utils.HttpConnectionCreator;
import amai.org.conventions.utils.URLUtils;
import amai.org.conventions.utils.Views;
import sff.org.conventions.R;

public class Icon2026Convention extends SffConvention {
	private static final String HALL_NAME_CINEMATHEQUE_4 = "סינמטק 4";
	private static final String HALL_NAME_ESHKOL_1 = "אשכול 1";
	private static final String HALL_NAME_ESHKOL_2 = "אשכול 2";
	private static final String HALL_NAME_ESHKOL_3 = "אשכול 3";
	private static final String HALL_NAME_ESHKOL_4 = "אשכול 4";
	private static final String HALL_NAME_ESHKOL_5 = "אשכול 5";
	private static final String HALL_NAME_WORKSHOPS_1 = "סדנאות 1";
	private static final String HALL_NAME_WORKSHOPS_2 = "סדנאות 2";
	private static final String HALL_NAME_MEETINGS = "מפגשים";
	private static final String HALL_NAME_KIDS = "ילדים";
	private static final String HALL_NAME_OUTSIDE = "זירה וחוצות";
	private static final String HALL_NAME_IRONI_1 = "עירוני 1";
	private static final String HALL_NAME_IRONI_2 = "עירוני 2";
	private static final String HALL_NAME_IRONI_3 = "עירוני 3";
	private static final String HALL_NAME_IRONI_4 = "עירוני 4";
	private static final String HALL_NAME_IRONI_5 = "עירוני 5";
	private static final String HALL_NAME_IRONI_6 = "עירוני 6";
	private static final String HALL_NAME_IRONI_7 = "עירוני 7";
	private static final String HALL_NAME_IRONI_8 = "עירוני 8";
	private static final String HALL_NAME_IRONI_9 = "עירוני 9";
	private static final String HALL_NAME_GAMES_1 = "חדר משחקי קופסה 1";
	private static final String HALL_NAME_GAMES_2 = "חדר משחקי קופסה 2";

	private static final String STANDS_AREA_ESHKOL = "אשכול";
	private static final String STANDS_AREA_DA_VINCI = "דה וינצ'י";
	private static final String STANDS_AREA_COURT = "מגרש";
	private static final String STANDS_AREA_CINEMATHEQUE = "סינמטק";
	private static final String STANDS_AREA_POPUP = "פופ-אפ";

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
	protected OrderedNamedItems<Hall> initHalls() {
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
		return new OrderedNamedItems<>(halls);
	}

	@Override
	protected URL initStandsURL() {
		try {
			return new URL("https://api.sf-f.org.il/booths/" + API_SLUG + ".json");
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	protected String getGeneralStandType() {
		return GENERAL_STAND_TYPE;
	}

	@Override
	protected NamedItems<StandsArea> initStandsAreas() {
		List<StandsArea> standsAreas = Arrays.asList(
			new StandsArea().withName(STANDS_AREA_COURT)
				.withImageResource(R.drawable.icon2026_stands_court)
				.withImageWidth(4704)
				.withImageHeight(3931)
				.withDefaultZoom(3)
				.withMaxZoom(5)
				.withStandLocations(getCourtStandLocations()),
			new StandsArea().withName(STANDS_AREA_DA_VINCI)
				.withImageResource(R.drawable.icon2026_stands_da_vinci)
				.withImageWidth(2500)
				.withImageHeight(1500)
				.withStandLocations(getDaVinciStandLocations()),
			new StandsArea().withName(STANDS_AREA_POPUP)
				.withImageResource(R.drawable.icon2026_stands_popup)
				.withImageWidth(2500)
				.withImageHeight(1500)
				.withStandLocations(getPopupStandLocations()),
			new StandsArea().withName(STANDS_AREA_ESHKOL)
				.withImageResource(R.drawable.icon2026_stands_eshkol)
				.withImageWidth(1709)
				.withImageHeight(1710)
				.withDefaultZoom(2)
				.withMaxZoom(3)
				.withStandLocations(getEshkolStandLocations()),
			new StandsArea().withName(STANDS_AREA_CINEMATHEQUE)
				.withImageResource(R.drawable.icon2026_stands_cinematheque)
				.withImageWidth(1500)
				.withImageHeight(1500)
				.withDefaultZoom(2)
				.withMaxZoom(3)
				.withStandLocations(getCinemathequeStandLocations())
		);
		return new NamedItems<>(standsAreas);
	}

	@Override
	protected NamedItems<StandType> initStandTypes() {
		List<StandType> standTypes = Arrays.asList(
			new StandType().withName("איור").withImage(R.drawable.ic_color_lens),
			new StandType().withName("מלאכת יד").withImage(R.drawable.diamond_24px),
			new StandType().withName("מרץ'").withImage(R.drawable.ic_shopping_basket),
			new StandType().withName("משחקי קופסה").withImage(R.drawable.casino_24px),
			new StandType().withName("ספרים").withImage(R.drawable.book_5_24px),
			new StandType().withName("קומיקס").withImage(R.drawable.book_5_24px),
			new StandType().withName("שונות").withImage(R.drawable.ic_shopping_basket)
		);
		return new OrderedNamedItems<>(standTypes);
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
		Hall games1 = getHalls().findByName(HALL_NAME_GAMES_1);
		Hall games2 = getHalls().findByName(HALL_NAME_GAMES_2);

		Floor floor = new Floor(1)
				.withName("מפת המתחם")
				.withImageResource(R.drawable.icon2026_map, false)
				.withImageHeight(3804f)
				.withImageWidth(3500.315f)
				.withDefaultMarkerHeight(152.779f);
		final float SMALL_MARKER_HEIGHT = 92.788f;

		StandsArea standsAreaCourt = getStandsAreas().findByName(STANDS_AREA_COURT);
		StandsArea standsAreaDaVinci = getStandsAreas().findByName(STANDS_AREA_DA_VINCI);
		StandsArea standsAreaPopup = getStandsAreas().findByName(STANDS_AREA_POPUP);
		StandsArea standsAreaEshkol = getStandsAreas().findByName(STANDS_AREA_ESHKOL);
		StandsArea standsAreaCinematheque = getStandsAreas().findByName(STANDS_AREA_CINEMATHEQUE);

		return new ConventionMap()
			.withFloors(Collections.singletonList(floor))
			.withLocations(
				CollectionUtils.flattenList(
					inFloor(floor,
						mapLocation("מודיעין (מגרש)", 682.9125f, 2641.324f),
						mapLocation("זירה", Collections.singletonList(outside), 1032.0295f, 2637.757f),
						mapLocation("מתחם דוכנים - מגרש", Collections.singletonList(standsAreaCourt), 1592.6965f, 2121.656f),
						mapLocation("אולם ספורט", 1669.8305f, 3222.68f),
						mapLocation("מתחם דוכנים - פופ-אפ", Collections.singletonList(standsAreaPopup), 2099.2775f, 3305.469f),
						mapLocation("השטיח האדום", Collections.singletonList(outside), 2567.6675f, 2869.21f),
						mapLocation("סוכה", 2794.1995f, 2594.611f),
						mapLocation("המתחם הקהילתי", 2881.3625f, 1556.851f),
						mapLocation("מתחם דוכנים - דה וינצ'י", Collections.singletonList(standsAreaDaVinci), 1950.0905f, 760.918f),
						mapLocation("שירותי נשים", 1275.3945f, 1740.74f).withMarkerHeight(SMALL_MARKER_HEIGHT),
						mapLocation("שירותי גברים", 551.5685f, 1737.869f).withMarkerHeight(SMALL_MARKER_HEIGHT),
						mapLocation(eshkol1, 1039.6225f, 1725.021f).withMarkerResource(R.drawable.icon2026_place_red, false, MapLocation.NO_TINT).withSelectedMarkerResource(R.drawable.icon2026_place_red_selected, false, MapLocation.NO_TINT),
						mapLocation(eshkol2, 842.0425f, 1724.87f),
						mapLocation("דוכני עמותות", Collections.singletonList(getActivitiesActivityLocationForView(R.id.activity_city_spirits_cards)), 1122.2995f, 1509.177f).withMarkerHeight(SMALL_MARKER_HEIGHT),
						mapLocation("עמדת כרטיסים", 915.5105f, 1544.813f).withMarkerHeight(SMALL_MARKER_HEIGHT),
						mapLocation("מודיעין (אשכול)", 743.2125f, 1547.679f).withMarkerHeight(SMALL_MARKER_HEIGHT),
						mapLocation("משחקי שער", 1315.6005f, 1428.393f).withMarkerHeight(SMALL_MARKER_HEIGHT),
						mapLocation(eshkol5, 1169.2145f, 1406.317f).withMarkerHeight(SMALL_MARKER_HEIGHT),
						mapLocation(eshkol4, 843.3465f, 1400.858f).withMarkerHeight(SMALL_MARKER_HEIGHT),
						mapLocation(eshkol3, 687.8545f, 1407.53f).withMarkerHeight(SMALL_MARKER_HEIGHT),
						mapLocation("מתחם דוכנים - אשכול", Collections.singletonList(standsAreaEshkol), 920.2285f, 1039.119f).withMarkerHeight(SMALL_MARKER_HEIGHT),
						mapLocation("מתחם משחקי אינדי", Collections.singletonList(getActivitiesActivityLocationForView(R.id.activity_indie_games)), 564.6135f, 1005.506f).withMarkerHeight(SMALL_MARKER_HEIGHT),
						mapLocation("שירותי נשים", 2496.5225f, 974.612f).withMarkerHeight(SMALL_MARKER_HEIGHT),
						mapLocation("שירותי גברים", 2302.2885f, 933.025f).withMarkerHeight(SMALL_MARKER_HEIGHT),
						mapLocationForShelter("מדרגות למרחב מוגן", 2192.3145f, 1185.516f).withMarkerHeight(SMALL_MARKER_HEIGHT),
						mapLocation("קוספליי נשים", 2529.7085f, 1148.506f).withMarkerHeight(SMALL_MARKER_HEIGHT),
						mapLocation(kids, 2529.2955f, 1352.32f).withMarkerHeight(SMALL_MARKER_HEIGHT),
						mapLocation(meetings, 2525.4525f, 1540.871f).withMarkerHeight(SMALL_MARKER_HEIGHT),
						mapLocation(workshops1, 2565.8565f, 1762.507f).withMarkerHeight(SMALL_MARKER_HEIGHT),
						mapLocation(workshops2, 2528.2495f, 1963.194f).withMarkerHeight(SMALL_MARKER_HEIGHT),
						mapLocation("מדרגות לעירוני 1-9 ומשחקי קופסה 1-2", Arrays.asList(ironi1, ironi2, ironi3, ironi4, ironi5, ironi6, ironi7, ironi8, ironi9, games1, games2), 2775.1325f, 2188.282f).withMarkerHeight(SMALL_MARKER_HEIGHT),
						mapLocationForShelter("מדרגות למרחב מוגן", 2619.0285f, 2007.119f).withMarkerHeight(SMALL_MARKER_HEIGHT),
						mapLocation("תיקון קוספליי", 2216.2845f, 2064.274f).withMarkerHeight(SMALL_MARKER_HEIGHT),
						mapLocation("שירותי יוניסקס", 2336.7725f, 2370.395f).withMarkerHeight(SMALL_MARKER_HEIGHT),
						mapLocation("הוביטון", 2334.4425f, 2587.234f).withMarkerHeight(SMALL_MARKER_HEIGHT),
						mapLocation("קוספליי גברים", 2335.6155f, 2838.249f).withMarkerHeight(SMALL_MARKER_HEIGHT),
						mapLocation("שמירת חפצים", 2279.8465f, 3076.914f).withMarkerHeight(SMALL_MARKER_HEIGHT),
						mapLocation("מדרגות לסינמטק 4", Collections.singletonList(cinematheque4), 707.3305f, 3511.446f).withMarkerHeight(SMALL_MARKER_HEIGHT),
						mapLocation("מעלית לסינמטק 4", Collections.singletonList(cinematheque4), 960.4385f, 3397.739f).withMarkerHeight(SMALL_MARKER_HEIGHT),
						mapLocationForShelter("מרחב מוגן", 775.2955f, 3298.655f).withMarkerHeight(SMALL_MARKER_HEIGHT),
						mapLocation("קופות", 1185.7885f, 3156.354f),
						mapLocation("מתחם דוכנים - סינמטק", Collections.singletonList(standsAreaCinematheque), 986.7465f, 3185.284f),
						mapLocation("מודיעין (סינמטק)", 596.6895f, 3274.609f),
						mapLocation("מתחם משחקי לוח", Collections.singletonList(getActivitiesActivityLocationForView(R.id.activity_sirolynia)), 798.4805f, 2942.752f)
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
		final int DEFAULT_MARKER = R.drawable.icon2026_place;
		final int DEFAULT_MARKER_TINT_RES = MapLocation.NO_TINT;
		final int DEFAULT_SELECTED_MARKER = R.drawable.icon2026_place_selected;
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
			.withMarkerResource(R.drawable.icon2026_place_red, false, MapLocation.NO_TINT)
			.withSelectedMarkerResource(R.drawable.icon2026_place_red_selected, false, MapLocation.NO_TINT);
	}

	private StandLocations getPopupStandLocations() {
		float defaultWidth = 104;
		float defaultSpaceHorizontal = 21f;
		float defaultSpaceVertical = 21f;
		float defaultHeight = 104;

		int defaultHighlightColor = R.color.icon2026_red;
		BlendMode highlightBlendMode = null;
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
			highlightBlendMode = BlendMode.OVERLAY;
		}

		return new StandLocationsBuilder()
			.setDefaults(defaultWidth, defaultHeight, 0, defaultSpaceHorizontal, defaultSpaceVertical, defaultHighlightColor, highlightBlendMode)
			.leftToRight(911.000f, 933.000f, "פ", 1, 12, null)
			.build();
	}

	private StandLocations getDaVinciStandLocations() {
		float defaultWidth = 84;
		float defaultSpaceHorizontal = 18;
		float defaultSpaceVertical = 18;
		float defaultHeight = 84;

		int defaultHighlightColor = R.color.icon2026_red;
		BlendMode highlightBlendMode = null;
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
			highlightBlendMode = BlendMode.OVERLAY;
		}

		return new StandLocationsBuilder()
			.setDefaults(defaultWidth, defaultHeight, 0, defaultSpaceHorizontal, defaultSpaceVertical, defaultHighlightColor, highlightBlendMode)
			.leftToRight(402.000f, 1310.000f, "ח", 1, 2, "ח3")
			.leftToRight(1095.000f, 1310.000f, "ח", 3, 6, "ח7")
			.leftToRight(1613.000f, 1310.000f, "ח", 7, 12, null)
			.build();
	}

	private StandLocations getEshkolStandLocations() {
		float defaultWidth = 70.247f;
		float defaultSpaceHorizontal = 14.753f;
		float defaultSpaceVertical = 14.861f;
		float defaultHeight = 70.247f;

		int defaultHighlightColor = R.color.icon2026_red;
		BlendMode highlightBlendMode = null;
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
			highlightBlendMode = BlendMode.OVERLAY;
		}

		return new StandLocationsBuilder()
			.setDefaults(defaultWidth, defaultHeight, 0, defaultSpaceHorizontal, defaultSpaceVertical, defaultHighlightColor, highlightBlendMode)
			.single(535.000f, 976.477f, "ש", 1, "ש2")
			.leftToRight(535.000f, 891.369f, "ש", 2, 9, "ש10")
			.single(1130.750f, 976.477f, "ש", 10, null)
			.build();
	}

	private StandLocations getCinemathequeStandLocations() {
		float defaultWidth = 95.759f;
		float defaultSpaceHorizontal = 10.854f;
		float defaultSpaceVertical = 10.854f;
		float defaultHeight = 95.759f;

		int defaultHighlightColor = R.color.icon2026_red;
		BlendMode highlightBlendMode = null;
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
			highlightBlendMode = BlendMode.OVERLAY;
		}

		return new StandLocationsBuilder()
			.setDefaults(defaultWidth, defaultHeight, 0, defaultSpaceHorizontal, defaultSpaceVertical, defaultHighlightColor, highlightBlendMode)
			.leftToRight(988.000f, 1014.800f, "ס", 1, 2, "ס3")
			.leftToRight(775.000f, 717.000f, "ס", 3, 6, null)
			.build();
	}

	private StandLocations getCourtStandLocations() {
		float defaultWidth = 52f;
		float defaultSpaceHorizontal = 11f;
		float defaultSpaceVertical = 11f;
		float defaultHeight = 52f;

		int defaultHighlightColor = R.color.icon2026_red;
		BlendMode highlightBlendMode = null;
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
			highlightBlendMode = BlendMode.OVERLAY;
		}

		return new StandLocationsBuilder()
			.setDefaults(defaultWidth, defaultHeight, 0, defaultSpaceHorizontal, defaultSpaceVertical, defaultHighlightColor, highlightBlendMode)
			.leftToRight(394.684f, 2465.430f, "א", 1, 8, "א9")
			.leftToRight(967.684f, 2465.430f, "א", 9, 16, "א17")
			.leftToRight(1540.680f, 2465.430f, "א", 17, 24, "א25")
			.leftToRight(2113.680f, 2465.430f, "א", 25, 32, null)

			.leftToRight(408.000f, 2724.430f, "ב", 1, 8, "ב9")
			.leftToRight(981.000f, 2724.430f, "ב", 9, 16, "ב17")
			.topToBottom(1489.000f, 2807.430f, "ב", 17, 20, "ב21")
			.leftToRight(1122.000f, 3078.430f, "ב", 26, 21, "ב27")
			.leftToRight(675.000f, 3078.430f, "ב", 32, 27, "ב33")
			.leftToRight(228.000f, 3078.430f, "ב", 38, 33, null)

			.leftToRight(2065.680f, 2724.430f, "ג", 10, 1, "ג11")
			.topToBottom(1970.680f, 2807.430f, "ג", 11, 14, "ג15")
			.leftToRight(2065.680f, 3078.430f, "ג", 15, 24, null)

			.leftToRight(158.684f, 3337.430f, "ד", 1, 4, "ד5")
			.leftToRight(479.684f, 3337.430f, "ד", 5, 10, "ד11")
			.leftToRight(926.684f, 3337.430f, "ד", 11, 16, "ד17")
			.leftToRight(1373.680f, 3337.430f, "ד", 17, 22, "ד23")
			.leftToRight(1820.680f, 3337.430f, "ד", 23, 28, "ד29")
			.leftToRight(2267.680f, 3337.430f, "ד", 29, 34, null)

			.leftToRight(3184.680f, 2517.430f, "ה", 1, 7, "ה8")
			.topToBottom(3562.680f, 2580.430f, "ה", 8, 14, "ה15")
			.topToBottom(3562.680f, 3116.430f, "ה", 15, 18, "ה19")
			.leftToRight(3167.000f, 3431.000f, "ה", 24, 19, "ה25")
			.leftToRight(2988.000f, 3431.000f, "ה", 26, 25, "ה27")
			.topToBottom(2987.680f, 3179.000f, "ה", 30, 27, "ה31")
			.topToBottom(2987.680f, 2603.430f, "ה", 38, 31, "ה1")

			.leftToRight(795.684f, 3759.430f, "ו", 1, 20, "ו21")
			.leftToRight(2128.680f, 3759.430f, "ו", 21, 28, "ו29")
			.leftToRight(2730.680f, 3759.430f, "ו", 29, 38, null)

			.topToBottom(4060.680f, 3020.430f, "ז", 8, 1, "ז9")
			.topToBottom(4060.680f, 2573.430f, "ז", 14, 9, "ז15")
			.topToBottom(4060.680f, 1625.430f, "ז", 22, 15, "ז23")
			.topToBottom(4060.680f, 926.434f, "ז", 32, 23, "ז33")
			.topToBottom(4060.680f, 227.434f, "ז", 42, 33, null)

			.build();
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

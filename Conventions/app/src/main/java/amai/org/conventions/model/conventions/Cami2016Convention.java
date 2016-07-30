package amai.org.conventions.model.conventions;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import amai.org.conventions.R;
import amai.org.conventions.model.Convention;
import amai.org.conventions.model.ConventionMap;
import amai.org.conventions.model.EventToImageResourceIdMapper;
import amai.org.conventions.model.Floor;
import amai.org.conventions.model.Hall;
import amai.org.conventions.model.MapLocation;
import amai.org.conventions.model.Place;
import amai.org.conventions.model.StandsArea;
import amai.org.conventions.utils.CollectionUtils;
import amai.org.conventions.utils.ConventionStorage;

public class Cami2016Convention extends Convention {
	// Hall names
	private static final String MAIN_HALL_NAME = "אולם ראשי";
	private static final String SCHWARTZ_NAME = "אודיטוריום שוורץ";
	private static final String ESHKOL1_NAME = "אשכול 1";
	private static final String ESHKOL2_NAME = "אשכול 2";
	private static final String ESHKOL3_NAME = "אשכול 3";
	private static final String SPECIAL_EVENTS_NAME = "אירועים מיוחדים";

	@Override
	protected ConventionStorage initStorage() {
		return new ConventionStorage(this, R.raw.cami2016_convention_events, 0);
	}

	@Override
	protected Calendar initDate() {
		Calendar date = Calendar.getInstance();
		date.clear();
		date.set(2016, Calendar.AUGUST, 25);
		return date;
	}

	@Override
	protected String initID() {
		return "Cami2016";
	}

	@Override
	protected String initDisplayName() {
		return "כאמ\"י 2016";
	}

	@Override
	protected String initFacebookFeedPath() {
		return "/cami.org.il/posts";
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
	protected String initFeedbackRecipient() {
		return "content@cami.org.il";
	}

	@Override
	protected URL initModelURL() {
		try {
			// TODO update to Cami URL once events have categories, colors and lecurer names
			// http://2016.cami.org.il/wp-admin/admin-ajax.php?action=get_event_list
			return new URL("http://2016.harucon.org.il/wp-admin/admin-ajax.php?action=get_event_list");
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	protected EventToImageResourceIdMapper initImageMapper() {
		EventToImageResourceIdMapper imageMapper = new EventToImageResourceIdMapper();

		// Non-URL IDs
		imageMapper.addMapping(EventToImageResourceIdMapper.EVENT_GENERIC, R.drawable.cami2016_events_default_cover);

		return imageMapper;
	}

	@Override
	protected List<Hall> initHalls() {
		Hall mainHall = new Hall().withName(MAIN_HALL_NAME).withOrder(1);
		Hall auditorium = new Hall().withName(SCHWARTZ_NAME).withOrder(2);
		Hall eshkol1 = new Hall().withName(ESHKOL1_NAME).withOrder(3);
		Hall eshkol2 = new Hall().withName(ESHKOL2_NAME).withOrder(4);
		Hall eshkol3 = new Hall().withName(ESHKOL3_NAME).withOrder(5);
		Hall specialEvents = new Hall().withName(SPECIAL_EVENTS_NAME).withOrder(6);

		return Arrays.asList(mainHall, auditorium, eshkol1, eshkol2, eshkol3, specialEvents);
	}

	@Override
	protected ConventionMap initMap() {
		Hall mainHall = findHallByName(MAIN_HALL_NAME);
		Hall schwatrz = findHallByName(SCHWARTZ_NAME);
		Hall eshkol1 = findHallByName(ESHKOL1_NAME);
		Hall eshkol2 = findHallByName(ESHKOL2_NAME);
		Hall eshkol3 = findHallByName(ESHKOL3_NAME);
		Hall specialEvents = findHallByName(SPECIAL_EVENTS_NAME);

		Floor floor1 = new Floor(1)
				.withName("מפלס תחתון")
				.withImageResource(R.raw.cami2016_floor1)
				.withImageWidth(552.20001f)
				.withImageHeight(324.60001f);
		Floor floor2 = new Floor(2)
				.withName("מפלס עליון")
				.withImageResource(R.raw.cami2016_floor2)
				.withImageWidth(848.39502f)
				.withImageHeight(502.86401f);

		return new ConventionMap()
				.withFloors(Arrays.asList(floor1, floor2))
				.withLocations(
						CollectionUtils.flattenList(
								inFloor(floor1,
										new MapLocation()
												.withPlace(new Place().withName("שירותים"))
												.withMarkerResource(R.raw.cami2016_marker_toilet)
												.withSelectedMarkerResource(R.raw.cami2016_marker_toilet_floor1_selected)
												.withMarkerHeight(25.066f)
												.withX(507.46f)
												.withY(184.49f),
										new MapLocation()
												.withPlace(specialEvents)
												.withName("החתמת אורח")
												.withMarkerResource(R.raw.cami2016_marker_guest)
												.withSelectedMarkerResource(R.raw.cami2016_marker_guest_selected)
												.withMarkerHeight(38.066f)
												.withX(410.17f)
												.withY(155.19f),
										new MapLocation()
												.withPlace(new StandsArea().withName("מתחם דוכנים"))
												.withMarkerResource(R.raw.cami2016_marker_stalls_pink)
												.withSelectedMarkerResource(R.raw.cami2016_marker_stalls_pink_selected)
												.withMarkerHeight(26.366f)
												.withX(352.06f)
												.withY(194.29f),
										new MapLocation()
												.withPlace(new Place().withName("שמירת חפצים"))
												.withMarkerResource(R.raw.cami2016_marker_storage)
												.withSelectedMarkerResource(R.raw.cami2016_marker_storage_selected)
												.withMarkerHeight(39.667f)
												.withX(314.06f)
												.withY(73.50f),
										new MapLocation()
												.withPlace(new Place().withName("שירותים"))
												.withMarkerResource(R.raw.cami2016_marker_toilet)
												.withSelectedMarkerResource(R.raw.cami2016_marker_toilet_floor1_selected)
												.withMarkerHeight(25.066f)
												.withX(291.36f)
												.withY(26.89f),
										new MapLocation()
												.withPlace(new Place().withName("מודיעין ודוכן אמא\"י"))
												.withMarkerResource(R.raw.cami2016_marker_information)
												.withSelectedMarkerResource(R.raw.cami2016_marker_information_selected)
												.withMarkerHeight(46.766f)
												.withX(249.86f)
												.withY(111.20f),
										new MapLocation()
												.withPlace(eshkol1)
												.withMarkerResource(R.raw.cami2016_marker_eshkol1)
												.withSelectedMarkerResource(R.raw.cami2016_marker_eshkol1_selected)
												.withMarkerHeight(39.466f)
												.withX(148.36f)
												.withY(177.50f),
										new MapLocation()
												.withPlace(schwatrz)
												.withMarkerResource(R.raw.cami2016_marker_schwartz)
												.withSelectedMarkerResource(R.raw.cami2016_marker_schwartz_selected)
												.withMarkerHeight(38.566f)
												.withX(195.86f)
												.withY(162.39f),
										new MapLocation()
												.withPlace(eshkol3)
												.withMarkerResource(R.raw.cami2016_marker_eshkol3)
												.withSelectedMarkerResource(R.raw.cami2016_marker_eshkol3_selected)
												.withMarkerHeight(39.766f)
												.withX(103.36f)
												.withY(239.70f),
										new MapLocation()
												.withPlace(eshkol2)
												.withMarkerResource(R.raw.cami2016_marker_eshkol2)
												.withSelectedMarkerResource(R.raw.cami2016_marker_eshkol2_selected)
												.withMarkerHeight(39.766f)
												.withX(159.16f)
												.withY(239.69f),
										new MapLocation()
												.withPlace(new Place().withName("שירותים"))
												.withMarkerResource(R.raw.cami2016_marker_toilet)
												.withSelectedMarkerResource(R.raw.cami2016_marker_toilet_floor1_selected)
												.withMarkerHeight(25.066f)
												.withX(81.36f)
												.withY(147.39f)),
								inFloor(floor2,
										new MapLocation()
												.withPlace(new Place().withName("שיפוט קוספליי"))
												.withMarkerResource(R.raw.cami2016_marker_cosplay_judgement)
												.withSelectedMarkerResource(R.raw.cami2016_marker_cosplay_judgement_selected)
												.withMarkerHeight(59.226f)
												.withX(729.53f)
												.withY(345.00f),
										new MapLocation()
												.withPlace(new Place().withName("כניסה פלוס"))
												.withMarkerResource(R.raw.cami2016_marker_entrance_plus)
												.withSelectedMarkerResource(R.raw.cami2016_marker_entrance_plus_selected)
												.withMarkerHeight(59.074f)
												.withX(572.36f)
												.withY(185.70f),
										new MapLocation()
												.withPlace(mainHall)
												.withMarkerResource(R.raw.cami2016_marker_main_hall)
												.withSelectedMarkerResource(R.raw.cami2016_marker_main_hall_selected)
												.withMarkerHeight(91.121f)
												.withX(527.28f)
												.withY(268.68f),
										new MapLocation()
												.withPlace(new Place().withName("שירותים"))
												.withMarkerResource(R.raw.cami2016_marker_toilet)
												.withSelectedMarkerResource(R.raw.cami2016_marker_toilet_floor2_selected)
												.withMarkerHeight(38.046f)
												.withX(506.39f)
												.withY(46.52f),
										new MapLocation()
												.withPlace(new Place().withName("מתחם קוספליי"))
												.withMarkerResource(R.raw.cami2016_marker_cosplay_area)
												.withSelectedMarkerResource(R.raw.cami2016_marker_cosplay_area_selected)
												.withMarkerHeight(59.074f)
												.withX(433.93f)
												.withY(371.32f),
										new MapLocation()
												.withPlace(new Place().withName("משחקייה וקונסולות"))
												.withMarkerResource(R.raw.cami2016_marker_games)
												.withSelectedMarkerResource(R.raw.cami2016_marker_games_selected)
												.withMarkerHeight(59.074f)
												.withX(306.85f)
												.withY(332.30f),
										new MapLocation()
												.withPlace(new StandsArea().withName("מתחם דוכנים"))
												.withMarkerResource(R.raw.cami2016_marker_stalls_red)
												.withSelectedMarkerResource(R.raw.cami2016_marker_stalls_red_selected)
												.withMarkerHeight(39.860f)
												.withX(306.85f)
												.withY(181.00f),
										new MapLocation()
												.withPlace(new Place().withName("שירותים"))
												.withMarkerResource(R.raw.cami2016_marker_toilet)
												.withSelectedMarkerResource(R.raw.cami2016_marker_toilet_floor2_selected)
												.withMarkerHeight(38.044f)
												.withX(75.99f)
												.withY(277.38f))
						)
				);
	}
}

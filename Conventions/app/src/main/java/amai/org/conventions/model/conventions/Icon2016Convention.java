package amai.org.conventions.model.conventions;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import amai.org.conventions.model.ConventionMap;
import amai.org.conventions.model.EventToImageResourceIdMapper;
import amai.org.conventions.model.Floor;
import amai.org.conventions.model.Hall;
import amai.org.conventions.model.MapLocation;
import amai.org.conventions.model.Place;
import amai.org.conventions.utils.CollectionUtils;
import amai.org.conventions.utils.ConventionStorage;
import amai.org.conventions.utils.Dates;
import sff.org.conventions.R;

public class Icon2016Convention extends SffConvention {
    @Override
    protected ConventionStorage initStorage() {
        return new ConventionStorage(this, R.raw.icon2016_convention_events, 1);
    }

    @Override
    protected Calendar initStartDate() {
	    return Dates.createDate(2016, Calendar.OCTOBER, 18);
    }

	@Override
	protected Calendar initEndDate() {
		return Dates.createDate(2016, Calendar.OCTOBER, 20);
	}

	@Override
    protected String initID() {
        return "Icon2016";
    }

    @Override
    protected String initDisplayName() {
        return "אייקון 2016";
    }

    @Override
    protected List<Hall> initHalls() {
        return Arrays.asList(
		        new Hall().withName("סינמטק 1").withOrder(1),
		        new Hall().withName("סינמטק 2").withOrder(2),
		        new Hall().withName("אשכול 1").withOrder(3),
		        new Hall().withName("אשכול 2").withOrder(4),
		        new Hall().withName("אשכול 3").withOrder(5),
		        new Hall().withName("אשכול 4").withOrder(6),
		        new Hall().withName("אשכול 5").withOrder(7),
		        new Hall().withName("אשכול 6").withOrder(8),
		        new Hall().withName("חדר סדנאות 1").withOrder(9),
		        new Hall().withName("חדר סדנאות 2").withOrder(10),
		        new Hall().withName("ארועים מיוחדים").withOrder(11),
		        new Hall().withName("עירוני 1").withOrder(12),
		        new Hall().withName("עירוני 2").withOrder(13),
		        new Hall().withName("עירוני 3").withOrder(14),
		        new Hall().withName("עירוני 4").withOrder(15),
		        new Hall().withName("עירוני 5").withOrder(16),
		        new Hall().withName("עירוני 6").withOrder(17),
		        new Hall().withName("עירוני 7").withOrder(18),
		        new Hall().withName("עירוני 8").withOrder(19),
		        new Hall().withName("עירוני 9").withOrder(20),
		        new Hall().withName("עירוני 10").withOrder(21),
		        new Hall().withName("עירוני 11").withOrder(22),
		        new Hall().withName("עירוני 12").withOrder(23)
        );
    }

    @Override
    protected ConventionMap initMap() {
	    Floor floor = new Floor(1)
			    .withName("מפת התמצאות")
			    .withImageResource(R.drawable.icon2016_map, false)
			    .withImageHeight(1303)
			    .withImageWidth(920);

	    return new ConventionMap()
		    .withFloors(Collections.singletonList(floor))
		    .withLocations(
				    CollectionUtils.flattenList(
						    inFloor(floor,
								    new MapLocation()
										    .withPlace(new Place().withName("כניסה"))
										    .withMarkerResource(R.drawable.ic_action_place, false)
										    .withSelectedMarkerResource(R.drawable.ic_action_place_opaque_green, false)
										    .withX(672)
										    .withY(1182)
										    .withMarkerHeight(50),
								    new MapLocation()
										    .withPlace(new Place().withName("מיניאטורות"))
										    .withMarkerResource(R.drawable.ic_action_place, false)
										    .withSelectedMarkerResource(R.drawable.ic_action_place_opaque_green, false)
										    .withX(836)
										    .withY(1073)
										    .withMarkerHeight(50),
								    new MapLocation()
										    .withPlace(new Place().withName("מיניאטורות"))
										    .withMarkerResource(R.drawable.ic_action_place, false)
										    .withSelectedMarkerResource(R.drawable.ic_action_place_opaque_green, false)
										    .withX(545)
										    .withY(1052)
										    .withMarkerHeight(50),

								    new MapLocation()
										    .withPlace(new Place().withName("דוכנים"))
										    .withMarkerResource(R.drawable.ic_action_place, false)
										    .withSelectedMarkerResource(R.drawable.ic_action_place_opaque_green, false)
										    .withX(680)
										    .withY(1042)
										    .withMarkerHeight(50),
								    new MapLocation()
										    .withPlace(new Place().withName("דוכנים"))
										    .withMarkerResource(R.drawable.ic_action_place, false)
										    .withSelectedMarkerResource(R.drawable.ic_action_place_opaque_green, false)
										    .withX(680)
										    .withY(899)
										    .withMarkerHeight(50),
								    new MapLocation()
										    .withPlace(new Place().withName("שירותי בנים"))
										    .withMarkerResource(R.drawable.ic_action_place, false)
										    .withSelectedMarkerResource(R.drawable.ic_action_place_opaque_green, false)
										    .withX(855)
										    .withY(901)
										    .withMarkerHeight(50),
								    new MapLocation()
										    .withPlace(new Place().withName("שירותי בנות"))
										    .withMarkerResource(R.drawable.ic_action_place, false)
										    .withSelectedMarkerResource(R.drawable.ic_action_place_opaque_green, false)
										    .withX(507)
										    .withY(901)
										    .withMarkerHeight(50),
								    new MapLocation()
										    .withPlace(new Place().withName("אשכול 1"))
										    .withMarkerResource(R.drawable.ic_action_place, false)
										    .withSelectedMarkerResource(R.drawable.ic_action_place_opaque_green, false)
										    .withX(678)
										    .withY(789)
										    .withMarkerHeight(100),
								    new MapLocation()
										    .withPlace(new Place().withName("אשכול 2"))
										    .withMarkerResource(R.drawable.ic_action_place, false)
										    .withSelectedMarkerResource(R.drawable.ic_action_place_opaque_green, false)
										    .withX(826)
										    .withY(789)
										    .withMarkerHeight(100),
								    new MapLocation()
										    .withPlace(new Place().withName("מתחם יד שניה וחולצות הפסטיבל"))
										    .withMarkerResource(R.drawable.ic_action_place, false)
										    .withSelectedMarkerResource(R.drawable.ic_action_place_opaque_green, false)
										    .withX(487)
										    .withY(804)
										    .withMarkerHeight(50),
								    new MapLocation()
										    .withPlace(new Place().withName("הפונדק שבין העולמות"))
										    .withMarkerResource(R.drawable.ic_action_place, false)
										    .withSelectedMarkerResource(R.drawable.ic_action_place_opaque_green, false)
										    .withX(846)
										    .withY(575)
										    .withMarkerHeight(50),
								    new MapLocation()
										    .withPlace(new Place().withName("קולוסיאום"))
										    .withMarkerResource(R.drawable.ic_action_place, false)
										    .withSelectedMarkerResource(R.drawable.ic_action_place_opaque_green, false)
										    .withX(711)
										    .withY(498)
										    .withMarkerHeight(50),
								    new MapLocation()
										    .withPlace(new Place().withName("כניסה מרחוב הארבעה"))
										    .withMarkerResource(R.drawable.ic_action_place, false)
										    .withSelectedMarkerResource(R.drawable.ic_action_place_opaque_green, false)
										    .withX(885)
										    .withY(379)
										    .withMarkerHeight(50),
								    new MapLocation()
										    .withPlace(new Place().withName("מודיעין"))
										    .withMarkerResource(R.drawable.ic_action_place, false)
										    .withSelectedMarkerResource(R.drawable.ic_action_place_opaque_green, false)
										    .withX(835)
										    .withY(298)
										    .withMarkerHeight(50),
								    new MapLocation()
										    .withPlace(new Place().withName("קופות ואיסוף הזמנה מראש"))
										    .withMarkerResource(R.drawable.ic_action_place, false)
										    .withSelectedMarkerResource(R.drawable.ic_action_place_opaque_green, false)
										    .withX(712)
										    .withY(311)
										    .withMarkerHeight(50),
								    new MapLocation()
										    .withPlace(new Place().withName("אגודה ישראלית למדע בדיוני ולפנטזיה"))
										    .withMarkerResource(R.drawable.ic_action_place, false)
										    .withSelectedMarkerResource(R.drawable.ic_action_place_opaque_green, false)
										    .withX(596)
										    .withY(293)
										    .withMarkerHeight(50),
								    new MapLocation()
										    .withPlace(new Place().withName("גיבורים הוצאה לאור"))
										    .withMarkerResource(R.drawable.ic_action_place, false)
										    .withSelectedMarkerResource(R.drawable.ic_action_place_opaque_green, false)
										    .withX(516)
										    .withY(285)
										    .withMarkerHeight(50),
								    new MapLocation()
										    .withPlace(new Place().withName("איסוף הזמנות מראש ומתחם השקות"))
										    .withMarkerResource(R.drawable.ic_action_place, false)
										    .withSelectedMarkerResource(R.drawable.ic_action_place_opaque_green, false)
										    .withX(406)
										    .withY(331)
										    .withMarkerHeight(50),
								    new MapLocation()
										    .withPlace(new Place().withName("דוכנים"))
										    .withMarkerResource(R.drawable.ic_action_place, false)
										    .withSelectedMarkerResource(R.drawable.ic_action_place_opaque_green, false)
										    .withX(506)
										    .withY(592)
										    .withMarkerHeight(50),
								    new MapLocation()
										    .withPlace(new Place().withName("דוכנים"))
										    .withMarkerResource(R.drawable.ic_action_place, false)
										    .withSelectedMarkerResource(R.drawable.ic_action_place_opaque_green, false)
										    .withX(273)
										    .withY(1140)
										    .withMarkerHeight(50),
								    new MapLocation()
										    .withPlace(new Place().withName("אולם הספורט"))
										    .withMarkerResource(R.drawable.ic_action_place, false)
										    .withSelectedMarkerResource(R.drawable.ic_action_place_opaque_green, false)
										    .withX(304)
										    .withY(196)
										    .withMarkerHeight(50),
								    new MapLocation()
										    .withPlace(new Place().withName("דוכנים"))
										    .withMarkerResource(R.drawable.ic_action_place, false)
										    .withSelectedMarkerResource(R.drawable.ic_action_place_opaque_green, false)
										    .withX(184)
										    .withY(1052)
										    .withMarkerHeight(50),
								    new MapLocation()
										    .withPlace(new Place().withName("כניסה לעירוני"))
										    .withMarkerResource(R.drawable.ic_action_place, false)
										    .withSelectedMarkerResource(R.drawable.ic_action_place_opaque_green, false)
										    .withX(257)
										    .withY(493)
										    .withMarkerHeight(50),
								    new MapLocation()
										    .withPlace(new Place().withName("מתחם משחקי לוח"))
										    .withMarkerResource(R.drawable.ic_action_place, false)
										    .withSelectedMarkerResource(R.drawable.ic_action_place_opaque_green, false)
										    .withX(180)
										    .withY(148)
										    .withMarkerHeight(50),
								    new MapLocation()
										    .withPlace(new Place().withName("כניסה לעירוני"))
										    .withMarkerResource(R.drawable.ic_action_place, false)
										    .withSelectedMarkerResource(R.drawable.ic_action_place_opaque_green, false)
										    .withX(51)
										    .withY(1054)
										    .withMarkerHeight(50),
								    new MapLocation()
										    .withPlace(new Place().withName("שירותים"))
										    .withMarkerResource(R.drawable.ic_action_place, false)
										    .withSelectedMarkerResource(R.drawable.ic_action_place_opaque_green, false)
										    .withX(51)
										    .withY(980)
										    .withMarkerHeight(50),
								    new MapLocation()
										    .withPlace(new Place().withName("חדר סדנאות 1"))
										    .withMarkerResource(R.drawable.ic_action_place, false)
										    .withSelectedMarkerResource(R.drawable.ic_action_place_opaque_green, false)
										    .withX(51)
										    .withY(920)
										    .withMarkerHeight(50),
								    new MapLocation()
										    .withPlace(new Place().withName("חדר סדנאות 2"))
										    .withMarkerResource(R.drawable.ic_action_place, false)
										    .withSelectedMarkerResource(R.drawable.ic_action_place_opaque_green, false)
										    .withX(51)
										    .withY(862)
										    .withMarkerHeight(50),
								    new MapLocation()
										    .withPlace(new Place().withName("הוביטון"))
										    .withMarkerResource(R.drawable.ic_action_place, false)
										    .withSelectedMarkerResource(R.drawable.ic_action_place_opaque_green, false)
										    .withX(51)
										    .withY(799)
										    .withMarkerHeight(50),
								    new MapLocation()
										    .withPlace(new Place().withName("חדר סגל"))
										    .withMarkerResource(R.drawable.ic_action_place, false)
										    .withSelectedMarkerResource(R.drawable.ic_action_place_opaque_green, false)
										    .withX(51)
										    .withY(737)
										    .withMarkerHeight(50),
								    new MapLocation()
										    .withPlace(new Place().withName("אפסנאות"))
										    .withMarkerResource(R.drawable.ic_action_place, false)
										    .withSelectedMarkerResource(R.drawable.ic_action_place_opaque_green, false)
										    .withX(51)
										    .withY(675)
										    .withMarkerHeight(50),
								    new MapLocation()
										    .withPlace(new Place().withName("שירותים"))
										    .withMarkerResource(R.drawable.ic_action_place, false)
										    .withSelectedMarkerResource(R.drawable.ic_action_place_opaque_green, false)
										    .withX(70)
										    .withY(533)
										    .withMarkerHeight(50),
								    new MapLocation()
										    .withPlace(new Place().withName("חדר קוספליי"))
										    .withMarkerResource(R.drawable.ic_action_place, false)
										    .withSelectedMarkerResource(R.drawable.ic_action_place_opaque_green, false)
										    .withX(70)
										    .withY(468)
										    .withMarkerHeight(50),
								    new MapLocation()
										    .withPlace(new Place().withName("חדר מפגשים"))
										    .withMarkerResource(R.drawable.ic_action_place, false)
										    .withSelectedMarkerResource(R.drawable.ic_action_place_opaque_green, false)
										    .withX(70)
										    .withY(387)
										    .withMarkerHeight(50),
								    new MapLocation()
										    .withPlace(new Place().withName("שמירת חפצים"))
										    .withMarkerResource(R.drawable.ic_action_place, false)
										    .withSelectedMarkerResource(R.drawable.ic_action_place_opaque_green, false)
										    .withX(70)
										    .withY(265)
										    .withMarkerHeight(50),
								    new MapLocation()
										    .withPlace(new Place().withName("מדרגות לאולמות אשכול 3-6"))
										    .withMarkerResource(R.drawable.ic_action_place, false)
										    .withSelectedMarkerResource(R.drawable.ic_action_place_opaque_green, false)
										    .withX(554)
										    .withY(949)
										    .withMarkerHeight(50),
								    new MapLocation()
										    .withPlace(new Place().withName("מדרגות לאולמות אשכול 3-6"))
										    .withMarkerResource(R.drawable.ic_action_place, false)
										    .withSelectedMarkerResource(R.drawable.ic_action_place_opaque_green, false)
										    .withX(808)
										    .withY(949)
										    .withMarkerHeight(50),
								    new MapLocation()
										    .withPlace(new Place().withName("לסינמטק תל אביב"))
										    .withMarkerResource(R.drawable.ic_action_place, false)
										    .withSelectedMarkerResource(R.drawable.ic_action_place_opaque_green, false)
										    .withX(640)
										    .withY(141)
										    .withMarkerHeight(50)
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
    protected EventToImageResourceIdMapper initImageMapper() {
	    EventToImageResourceIdMapper imageMapper = new EventToImageResourceIdMapper();

	    imageMapper.addMapping(EventToImageResourceIdMapper.EVENT_GENERIC, R.drawable.icon2016_event_background);

	    return imageMapper;
    }

    @Override
    protected String initFeedbackRecipient() {
        return "feedback@iconfestival.org.il";
    }

    @Override
    protected URL initModelURL() {
        try {
            return new URL("https://api.sf-f.org.il/program/list_events.php?slug=icon2016");
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

	@Override
	protected URL initUpdatesURL() {
		try {
			return new URL("https://api.sf-f.org.il/announcements/get.php?slug=icon2016"); // use test_con for tests
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
	}
}

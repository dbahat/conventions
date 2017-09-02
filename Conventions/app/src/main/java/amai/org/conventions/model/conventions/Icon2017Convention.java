package amai.org.conventions.model.conventions;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;

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
import sff.org.conventions.R;

public class Icon2017Convention extends SffConvention {
	private static final String HALL_NAME_CINEMATHEQUE_1 = "סינמטק 1";
	private static final String HALL_NAME_CINEMATHEQUE_2 = "סינמטק 2";
	private static final String HALL_NAME_ESHKOL_1 = "אשכול 1";
	private static final String HALL_NAME_ESHKOL_2 = "אשכול 2";
	private static final String HALL_NAME_ESHKOL_3 = "אשכול 3";
	private static final String HALL_NAME_ESHKOL_4 = "אשכול 4";
	private static final String HALL_NAME_ESHKOL_5 = "אשכול 5";
	private static final String HALL_NAME_ESHKOL_6 = "אשכול 6";
	private static final String HALL_NAME_WORKSHOPS_1 = "חדר סדנאות 1";
	private static final String HALL_NAME_WORKSHOPS_2 = "חדר סדנאות 2";
	private static final String HALL_NAME_SPECIAL_EVENTS = "ארועים מיוחדים";
	private static final String HALL_NAME_IRONI_1 = "עירוני 1";
	private static final String HALL_NAME_IRONI_2 = "עירוני 2";
	private static final String HALL_NAME_IRONI_3 = "עירוני 3";
	private static final String HALL_NAME_IRONI_4 = "עירוני 4";
	private static final String HALL_NAME_IRONI_5 = "עירוני 5";
	private static final String HALL_NAME_IRONI_6 = "עירוני 6";
	private static final String HALL_NAME_IRONI_7 = "עירוני 7";
	private static final String HALL_NAME_IRONI_8 = "עירוני 8";
	private static final String HALL_NAME_IRONI_9 = "עירוני 9";
	private static final String HALL_NAME_IRONI_10 = "עירוני 10";
	private static final String HALL_NAME_IRONI_11 = "עירוני 11";
	private static final String HALL_NAME_IRONI_12 = "עירוני 12";

	@Override
    protected ConventionStorage initStorage() {
        return new ConventionStorage(this, R.raw.icon2016_convention_events, 0);
    }

    @Override
    protected Calendar initStartDate() {
	    return Dates.createDate(2017, Calendar.OCTOBER, 8);
    }

	@Override
	protected Calendar initEndDate() {
		return Dates.createDate(2017, Calendar.OCTOBER, 10);
	}

	@Override
    protected String initID() {
        return "Icon2017";
    }

    @Override
    protected String initDisplayName() {
        return "פסטיבל אייקון 2017";
    }

    @Override
    protected Halls initHalls() {
	    return new Halls(Arrays.asList(
		        new Hall().withName(HALL_NAME_CINEMATHEQUE_1).withOrder(1),
		        new Hall().withName(HALL_NAME_CINEMATHEQUE_2).withOrder(2),
		        new Hall().withName(HALL_NAME_ESHKOL_1).withOrder(3),
		        new Hall().withName(HALL_NAME_ESHKOL_2).withOrder(4),
		        new Hall().withName(HALL_NAME_ESHKOL_3).withOrder(5),
		        new Hall().withName(HALL_NAME_ESHKOL_4).withOrder(6),
		        new Hall().withName(HALL_NAME_ESHKOL_5).withOrder(7),
		        new Hall().withName(HALL_NAME_ESHKOL_6).withOrder(8),
		        new Hall().withName(HALL_NAME_WORKSHOPS_1).withOrder(9),
		        new Hall().withName(HALL_NAME_WORKSHOPS_2).withOrder(10),
		        new Hall().withName(HALL_NAME_SPECIAL_EVENTS).withOrder(11),
		        new Hall().withName(HALL_NAME_IRONI_1).withOrder(12),
		        new Hall().withName(HALL_NAME_IRONI_2).withOrder(13),
		        new Hall().withName(HALL_NAME_IRONI_3).withOrder(14),
		        new Hall().withName(HALL_NAME_IRONI_4).withOrder(15),
		        new Hall().withName(HALL_NAME_IRONI_5).withOrder(16),
		        new Hall().withName(HALL_NAME_IRONI_6).withOrder(17),
		        new Hall().withName(HALL_NAME_IRONI_7).withOrder(18),
		        new Hall().withName(HALL_NAME_IRONI_8).withOrder(19),
		        new Hall().withName(HALL_NAME_IRONI_9).withOrder(20),
		        new Hall().withName(HALL_NAME_IRONI_10).withOrder(21),
		        new Hall().withName(HALL_NAME_IRONI_11).withOrder(22),
		        new Hall().withName(HALL_NAME_IRONI_12).withOrder(23)
        ));
    }

    @Override
    protected ConventionMap initMap() {
	    Hall cinematheque1 = getHalls().findByName(HALL_NAME_CINEMATHEQUE_1);
	    Hall cinematheque2 = getHalls().findByName(HALL_NAME_CINEMATHEQUE_2);
	    Hall eshkol1 = getHalls().findByName(HALL_NAME_ESHKOL_1);
	    Hall eshkol2 = getHalls().findByName(HALL_NAME_ESHKOL_2);
	    Hall eshkol3 = getHalls().findByName(HALL_NAME_ESHKOL_3);
	    Hall eshkol4 = getHalls().findByName(HALL_NAME_ESHKOL_4);
	    Hall eshkol5 = getHalls().findByName(HALL_NAME_ESHKOL_5);
	    Hall eshkol6 = getHalls().findByName(HALL_NAME_ESHKOL_6);
	    Hall workshops1 = getHalls().findByName(HALL_NAME_WORKSHOPS_1);
	    Hall workshops2 = getHalls().findByName(HALL_NAME_WORKSHOPS_2);
	    Hall ironi1 = getHalls().findByName(HALL_NAME_IRONI_1);
	    Hall ironi2 = getHalls().findByName(HALL_NAME_IRONI_2);
	    Hall ironi3 = getHalls().findByName(HALL_NAME_IRONI_3);
	    Hall ironi4 = getHalls().findByName(HALL_NAME_IRONI_4);
	    Hall ironi5 = getHalls().findByName(HALL_NAME_IRONI_5);
	    Hall ironi6 = getHalls().findByName(HALL_NAME_IRONI_6);
	    Hall ironi7 = getHalls().findByName(HALL_NAME_IRONI_7);
	    Hall ironi8 = getHalls().findByName(HALL_NAME_IRONI_8);
	    Hall ironi9 = getHalls().findByName(HALL_NAME_IRONI_9);
	    Hall ironi10 = getHalls().findByName(HALL_NAME_IRONI_10);
	    Hall ironi11 = getHalls().findByName(HALL_NAME_IRONI_11);
	    Hall ironi12 = getHalls().findByName(HALL_NAME_IRONI_12);

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
										    .withPlace(eshkol1)
										    .withMarkerResource(R.drawable.ic_action_place, false)
										    .withSelectedMarkerResource(R.drawable.ic_action_place_opaque_green, false)
										    .withX(678)
										    .withY(789)
										    .withMarkerHeight(100),
								    new MapLocation()
										    .withPlace(eshkol2)
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
										    .withPlace(new Place().withName("קופות ואיסוף הזמנות מראש"))
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
										    .withName("כניסה לעירוני")
										    .withPlaces(Arrays.asList(ironi1, ironi2, ironi3, ironi4, ironi5, ironi6, ironi7, ironi8, ironi9, ironi10, ironi11, ironi12))
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
										    .withPlace(workshops1)
										    .withMarkerResource(R.drawable.ic_action_place, false)
										    .withSelectedMarkerResource(R.drawable.ic_action_place_opaque_green, false)
										    .withX(51)
										    .withY(920)
										    .withMarkerHeight(50),
								    new MapLocation()
										    .withPlace(workshops2)
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
										    .withName("מדרגות לאולמות אשכול 3-6")
										    .withPlaces(Arrays.asList(eshkol3, eshkol4, eshkol5, eshkol6))
										    .withMarkerResource(R.drawable.ic_action_place, false)
										    .withSelectedMarkerResource(R.drawable.ic_action_place_opaque_green, false)
										    .withX(554)
										    .withY(949)
										    .withMarkerHeight(50),
								    new MapLocation()
										    .withName("מדרגות לאולמות אשכול 3-6")
										    .withPlaces(Arrays.asList(eshkol3, eshkol4, eshkol5, eshkol6))
										    .withMarkerResource(R.drawable.ic_action_place, false)
										    .withSelectedMarkerResource(R.drawable.ic_action_place_opaque_green, false)
										    .withX(808)
										    .withY(949)
										    .withMarkerHeight(50),
								    new MapLocation()
										    .withName("לסינמטק תל אביב")
										    .withPlaces(Arrays.asList(cinematheque1, cinematheque2))
										    .withMarkerResource(R.drawable.ic_action_place, false)
										    .withSelectedMarkerResource(R.drawable.ic_action_place_opaque_green, false)
										    .withX(898)
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
    protected ImageIdToImageResourceMapper initImageMapper() {
	    ImageIdToImageResourceMapper imageMapper = new ImageIdToImageResourceMapper();

	    imageMapper.addMapping(ImageIdToImageResourceMapper.EVENT_GENERIC, R.drawable.icon2017_page_background);

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
					.withSendUrl(new URL("https://docs.google.com/forms/d/e/1FAIpQLSf0qSN1DreR4h93k1QJWf_flL2LFrLCOvnp6HTZOvK_iLZHGA/formResponse"));
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
					.withSendUrl(new URL("https://docs.google.com/forms/d/e/1FAIpQLSdadoDGXMriFVgW1Lki22OcrOGQoJVIlW8cU29DfkJRvAPWUQ/formResponse"));
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
		return feedbackForm;
	}

	@Override
    protected URL initModelURL() {
        try {
            return new URL("https://api.sf-f.org.il/program/list_events.php?slug=icon2017");
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

	@Override
	protected URL initUpdatesURL() {
		try {
			return new URL("https://api.sf-f.org.il/announcements/get.php?slug=icon2017"); // use test_con for tests
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public ConventionEvent findEventByURL(String url) {
		// Event URLs in event descriptions can point to an alternate URL
		if (url.startsWith("http://program.iconfestival.org.il/")) {
			url = "http://iconfestival.com/" + url.substring("http://program.iconfestival.org.il/".length());
		}
		return super.findEventByURL(url);
	}
}

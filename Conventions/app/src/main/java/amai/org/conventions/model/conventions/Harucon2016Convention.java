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
import amai.org.conventions.utils.CollectionUtils;
import amai.org.conventions.utils.ConventionStorage;

public class Harucon2016Convention extends Convention {
	// Hall names
	private static final String MAIN_HALL_NAME = "אולם ראשי";
	private static final String SCHWARTZ_NAME = "אודיטוריום שוורץ";
	private static final String ESHKOL1_NAME = "אשכול 1";
	private static final String ESHKOL2_NAME = "אשכול 2";
	private static final String ESHKOL3_NAME = "אשכול 3";
	private static final String SPECIAL_EVENTS_NAME = "אירועים מיוחדים";

	@Override
	protected ConventionStorage initStorage() {
		return new ConventionStorage(this, R.raw.harucon2016_convention_events, 1);
	}

	@Override
	protected Calendar initDate() {
		Calendar date = Calendar.getInstance();
		date.clear();
		date.set(2016, Calendar.MARCH, 24);
		return date;
	}

	@Override
	protected String initID() {
		return "Harucon2016";
	}

	@Override
	protected String initDisplayName() {
		return "הארוקון 2016";
	}

	@Override
	protected String initFacebookFeedPath() {
		return "/harucon.org.il/posts";
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
		return "content@harucon.org.il";
	}

	@Override
	protected URL initModelURL() {
		try {
			return new URL("http://2016.harucon.org.il/wp-admin/admin-ajax.php?action=get_event_list");
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	protected EventToImageResourceIdMapper initImageMapper() {
		EventToImageResourceIdMapper imageMapper = new EventToImageResourceIdMapper();
		imageMapper.addMapping("http://2016.harucon.org.il/wp-content/uploads/sites/7/2016/02/Where-do-I-sign-LOGO-300x189.png", R.drawable.event_musical_logo);
		imageMapper.addMapping("http://2016.harucon.org.il/wp-content/uploads/sites/7/2016/02/11692794_1601889390062626_8995957057976976218_n-300x189.png", R.drawable.event_musical_group_name);
		imageMapper.addMapping("http://2016.harucon.org.il/wp-content/uploads/sites/7/2016/01/cosplay2-300x168.jpg", R.drawable.event_cosplay2);
		imageMapper.addMapping("http://2016.harucon.org.il/wp-content/uploads/sites/7/2016/01/cosplay3-300x168.jpg", R.drawable.event_cosplay3);
		imageMapper.addMapping("http://2016.harucon.org.il/wp-content/uploads/sites/7/2016/02/harmony-screenshot-1.jpg", R.drawable.event_harmony1);
		imageMapper.addMapping("http://2016.harucon.org.il/wp-content/uploads/sites/7/2016/02/harmony-screenshot-2.jpg", R.drawable.event_harmony2);
		imageMapper.addMapping("http://2016.harucon.org.il/wp-content/uploads/sites/7/2016/02/harmony-screenshot-3.jpg", R.drawable.event_harmony3);
		imageMapper.addMapping("http://2016.harucon.org.il/wp-content/uploads/sites/7/2016/02/6.-שתי-טיפות-של-דם-218x300.jpg", R.drawable.event_rmusical);
		imageMapper.addMapping("http://2016.harucon.org.il/wp-content/uploads/sites/7/2016/02/1.-זוז-בשם-האהבה-טרילוגיית-מאב-לאב-מהי-1024x768.jpg", R.drawable.event_muv_luv);
		imageMapper.addMapping("http://2016.harucon.org.il/wp-content/uploads/sites/7/2015/12/אלה-בן-יעקב.jpg", R.drawable.event_animation_workshop);
		imageMapper.addMapping("http://2016.harucon.org.il/wp-content/uploads/sites/7/2015/12/הרן-יקיר-1024x716.jpg", R.drawable.event_world_end);
		imageMapper.addMapping("http://2016.harucon.org.il/wp-content/uploads/sites/7/2016/02/6.-אל-ה-Plamo-ומעבר-לו.jpg", R.drawable.event_plamo);
		imageMapper.addMapping("http://2016.harucon.org.il/wp-content/uploads/sites/7/2016/02/3.-כשאפולו-לבש-סייפוקו.jpg", R.drawable.event_apollo);
		imageMapper.addMapping("http://2016.harucon.org.il/wp-content/uploads/sites/7/2015/12/ביאטריס-פיגרים.jpg", R.drawable.event_figures);
		imageMapper.addMapping("http://2016.harucon.org.il/wp-content/uploads/sites/7/2016/02/5.-הנפלאות-הכאב-ובעיקר-הכוויות-מאחורי-הכנת-שריון.jpg", R.drawable.event_armor_pain);
		imageMapper.addMapping("http://2016.harucon.org.il/wp-content/uploads/sites/7/2015/12/דוד-בהא.png", R.drawable.event_games_anime);
		imageMapper.addMapping("http://2016.harucon.org.il/wp-content/uploads/sites/7/2016/02/8.-הנטאי-ותפיסת-המיניות-היפנית-1024x624.png", R.drawable.event_hentai);
		imageMapper.addMapping("http://2016.harucon.org.il/wp-content/uploads/sites/7/2016/02/1.-קוספליי-חומרים-וטכניקות.jpg", R.drawable.event_cosplay_materials);
		imageMapper.addMapping("http://2016.harucon.org.il/wp-content/uploads/sites/7/2016/02/toku.png", R.drawable.event_tokusetsu);
		imageMapper.addMapping("http://2016.harucon.org.il/wp-content/uploads/sites/7/2016/02/ניבה-קרן-אור.png", R.drawable.event_bento);
		imageMapper.addMapping("http://2016.harucon.org.il/wp-content/uploads/sites/7/2015/12/דני-פייגלמן.jpg", R.drawable.event_cosplay_leds);
		imageMapper.addMapping("http://2016.harucon.org.il/wp-content/uploads/sites/7/2015/12/סבי-איזילוב-1024x701.jpg", R.drawable.event_manga_history);
		imageMapper.addMapping("http://2016.harucon.org.il/wp-content/uploads/sites/7/2016/02/6.-שיעור-יפנית-למתחילים.jpg", R.drawable.event_japanese);
		imageMapper.addMapping("http://2016.harucon.org.il/wp-content/uploads/sites/7/2016/02/Crunchyroll-Logo-1-300x120.png", R.drawable.event_crunchyroll_logo);
		imageMapper.addMapping("http://2016.harucon.org.il/wp-content/uploads/sites/7/2015/12/3_Seagull_Gadi_Dagon-1-e1457812487499-400x400.jpg", R.drawable.event_q_a_yoram);
		imageMapper.addMapping("http://2016.harucon.org.il/wp-content/uploads/sites/7/2016/03/e191c4cc-ba21-4b0b-a122-aa0d700a5f6f-1-400x400.jpg", R.drawable.event_q_a_toru);
		imageMapper.addMapping("http://2016.harucon.org.il/wp-content/uploads/sites/7/2016/03/e191c4cc-ba21-4b0b-a122-aa0d700a5f6f-1.jpg", R.drawable.event_q_a_toru);

		// Non-URL IDs
		imageMapper.addMapping(EventToImageResourceIdMapper.EVENT_GENERIC, R.drawable.harucon2016_events_default_cover);

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
				.withImageResource(R.raw.harucon2016_floor1)
				.withImageWidth(552.19623f)
				.withImageHeight(324.59729f)
				.withDefaultMarkerHeight(39.8f);
		Floor floor2 = new Floor(2)
				.withName("מפלס עליון")
				.withImageResource(R.raw.harucon2016_floor2)
				.withImageWidth(556.52002f)
				.withImageHeight(331.22592f)
				.withDefaultMarkerHeight(39.0f);

		return new ConventionMap()
				.withFloors(Arrays.asList(floor1, floor2))
				.withLocations(
						CollectionUtils.flattenList(
								inFloor(floor1,
										new MapLocation()
												.withPlace(new Place().withName("שירותים"))
												.withMarkerResource(R.raw.harucon2016_marker_toilet)
												.withSelectedMarkerResource(R.raw.harucon2016_marker_toilet_selected)
												.withMarkerHeight(25.1f)
												.withX(507.47f)
												.withY(184.49f),
										new MapLocation()
												.withPlace(specialEvents)
												.withName("החתמת אורח")
												.withMarkerResource(R.raw.harucon2016_marker_guest)
												.withSelectedMarkerResource(R.raw.harucon2016_marker_guest_selected)
												.withMarkerHeight(38.099964f)
												.withX(410.17f)
												.withY(155.20f),
										new MapLocation()
												.withPlace(new Place().withName("מתחם דוכנים"))
												.withMarkerResource(R.raw.harucon2016_marker_stalls_purple)
												.withSelectedMarkerResource(R.raw.harucon2016_marker_stalls_purple_selected)
												.withMarkerHeight(26.399996f)
												.withX(352.06f)
												.withY(194.30f),
										new MapLocation()
												.withPlace(new Place().withName("שמירת חפצים"))
												.withMarkerResource(R.raw.harucon2016_marker_storage)
												.withSelectedMarkerResource(R.raw.harucon2016_marker_storage_selected)
												.withMarkerHeight(39.700024f)
												.withX(314.07f)
												.withY(73.50f),
										new MapLocation()
												.withPlace(new Place().withName("שירותים"))
												.withMarkerResource(R.raw.harucon2016_marker_toilet)
												.withSelectedMarkerResource(R.raw.harucon2016_marker_toilet_selected)
												.withMarkerHeight(25.1f)
												.withX(291.36f)
												.withY(26.89f),
										new MapLocation()
												.withPlace(new Place().withName("מודיעין ודוכן אמא\"י"))
												.withMarkerResource(R.raw.harucon2016_marker_information)
												.withSelectedMarkerResource(R.raw.harucon2016_marker_information_selected)
												.withMarkerHeight(46.800014f)
												.withX(249.87f)
												.withY(111.20f),
										new MapLocation()
												.withPlace(eshkol1)
												.withMarkerResource(R.raw.harucon2016_marker_eshkol1)
												.withSelectedMarkerResource(R.raw.harucon2016_marker_eshkol1_selected)
												.withMarkerHeight(39.500004f)
												.withX(148.36f)
												.withY(177.50f),
										new MapLocation()
												.withPlace(schwatrz)
												.withMarkerResource(R.raw.harucon2016_marker_schwartz)
												.withSelectedMarkerResource(R.raw.harucon2016_marker_schwartz_selected)
												.withMarkerHeight(38.600006f)
												.withX(195.86f)
												.withY(162.39f),
										new MapLocation()
												.withPlace(eshkol3)
												.withMarkerResource(R.raw.harucon2016_marker_eshkol3)
												.withSelectedMarkerResource(R.raw.harucon2016_marker_eshkol3_selected)
												.withMarkerHeight(39.800026f)
												.withX(103.36f)
												.withY(239.70f),
										new MapLocation()
												.withPlace(eshkol2)
												.withMarkerResource(R.raw.harucon2016_marker_eshkol2)
												.withSelectedMarkerResource(R.raw.harucon2016_marker_eshkol2_selected)
												.withMarkerHeight(39.800045f)
												.withX(159.16f)
												.withY(239.69f),
										new MapLocation()
												.withPlace(new Place().withName("שירותים"))
												.withMarkerResource(R.raw.harucon2016_marker_toilet)
												.withSelectedMarkerResource(R.raw.harucon2016_marker_toilet_selected)
												.withMarkerHeight(25.1f)
												.withX(81.36f)
												.withY(147.40f)),
								inFloor(floor2,
										// Keep this location before storage because otherwise when
										// storage is selected, it's displayed behind this location
										new MapLocation()
												.withPlace(new Place().withName("שיפוט קוספליי"))
												.withMarkerResource(R.raw.harucon2016_marker_cosplay_judgement)
												.withSelectedMarkerResource(R.raw.harucon2016_marker_cosplay_judgement_selected)
												.withMarkerHeight(39.100006f)
												.withX(477.92f)
												.withY(228.01f),
										new MapLocation()
												.withPlace(new Place().withName("כניסה פלוס"))
												.withMarkerResource(R.raw.harucon2016_marker_entrance_plus)
												.withSelectedMarkerResource(R.raw.harucon2016_marker_entrance_plus_selected)
												.withMarkerHeight(39.000008f)
												.withX(374.02f)
												.withY(122.73f),
										new MapLocation()
												.withPlace(mainHall)
												.withMarkerResource(R.raw.harucon2016_marker_main_hall)
												.withSelectedMarkerResource(R.raw.harucon2016_marker_main_hall_selected)
												.withMarkerHeight(59.977146f)
												.withX(344.22f)
												.withY(177.77f),
										new MapLocation()
												.withPlace(new Place().withName("שירותים"))
												.withMarkerResource(R.raw.harucon2016_marker_toilet)
												.withSelectedMarkerResource(R.raw.harucon2016_marker_toilet_selected)
												.withMarkerHeight(25.1f)
												.withX(330.42f)
												.withY(30.72f),
										new MapLocation()
												.withPlace(new Place().withName("משחקייה וקונסולות"))
												.withMarkerResource(R.raw.harucon2016_marker_games)
												.withSelectedMarkerResource(R.raw.harucon2016_marker_games_selected)
												.withMarkerHeight(39.000011f)
												.withX(291.12f)
												.withY(247.23f),
										new MapLocation()
												.withPlace(new Place().withName("מתחם דוכנים"))
												.withMarkerResource(R.raw.harucon2016_marker_stalls_pink)
												.withSelectedMarkerResource(R.raw.harucon2016_marker_stalls_pink_selected)
												.withMarkerHeight(26f)
												.withX(220.74f)
												.withY(227.91f),
										new MapLocation()
												.withPlace(new Place().withName("מתחם דוכנים"))
												.withMarkerResource(R.raw.harucon2016_marker_stalls_yellow)
												.withSelectedMarkerResource(R.raw.harucon2016_marker_stalls_yellow_selected)
												.withMarkerHeight(26.299999f)
												.withX(198.53f)
												.withY(119.61f),
										new MapLocation()
												.withPlace(new Place().withName("תיקון קוספליי"))
												.withMarkerResource(R.raw.harucon2016_marker_cosplay_fixes)
												.withSelectedMarkerResource(R.raw.harucon2016_marker_cosplay_fixes_selected)
												.withMarkerHeight(39f)
												.withX(171.28f)
												.withY(202.09f),
										new MapLocation()
												.withPlace(new Place().withName("ווידוא ווקאון"))
												.withMarkerResource(R.raw.harucon2016_marker_walkon)
												.withSelectedMarkerResource(R.raw.harucon2016_marker_walkon_selected)
												.withMarkerHeight(39.099998f)
												.withX(121.88f)
												.withY(182.73f),
										new MapLocation()
												.withPlace(new Place().withName("פינת צילום"))
												.withMarkerResource(R.raw.harucon2016_marker_photoshoots)
												.withSelectedMarkerResource(R.raw.harucon2016_marker_photoshoots_selected)
												.withMarkerHeight(39.39999f)
												.withX(104.32f)
												.withY(141.92f),
										new MapLocation()
												.withPlace(new Place().withName("שירותים"))
												.withMarkerResource(R.raw.harucon2016_marker_toilet)
												.withSelectedMarkerResource(R.raw.harucon2016_marker_toilet_selected)
												.withMarkerHeight(25.1f)
												.withX(46.92f)
												.withY(183.32f))
						)
				);
	}
}

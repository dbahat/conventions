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
	private static final String AUDITORIUM_NAME = "אודיטוריום שוורץ";
	private static final String ESHKOL1_NAME = "אשכול 1";
	private static final String ESHKOL2_NAME = "אשכול 2";
	private static final String ESHKOL3_NAME = "אשכול 3";

	@Override
	protected ConventionStorage initStorage() {
		return new ConventionStorage(this, R.raw.harucon2016_convention_events);
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

		// Non-URL IDs
		imageMapper.addMapping(EventToImageResourceIdMapper.EVENT_GENERIC, R.drawable.harucon2016_cover);

		return imageMapper;
	}

	@Override
	protected List<Hall> initHalls() {
		Hall mainHall = new Hall().withName(MAIN_HALL_NAME).withOrder(1);
		Hall auditorium = new Hall().withName(AUDITORIUM_NAME).withOrder(2);
		Hall eshkol1 = new Hall().withName(ESHKOL1_NAME).withOrder(3);
		Hall eshkol2 = new Hall().withName(ESHKOL2_NAME).withOrder(4);
		Hall eshkol3 = new Hall().withName(ESHKOL3_NAME).withOrder(5);

		return Arrays.asList(mainHall, auditorium, eshkol1, eshkol2, eshkol3);
	}

	@Override
	protected ConventionMap initMap() {
		Hall mainHall = findHallByName(MAIN_HALL_NAME);
		Hall schwatrz = findHallByName(AUDITORIUM_NAME);
		Hall eshkol1 = findHallByName(ESHKOL1_NAME);
		Hall eshkol2 = findHallByName(ESHKOL2_NAME);
		Hall eshkol3 = findHallByName(ESHKOL3_NAME);

		Floor floor1 = new Floor(1)
				.withName("מפלס תחתון")
				.withImageResource(R.raw.harucon2016_floor1)
				.withImageHeight(880.61444f)
				.withImageWidth(1691.31787f)
				.withDefaultMarkerHeight(118.09998f);
		Floor floor2 = new Floor(2)
				.withName("מפלס עליון")
				.withImageResource(R.raw.harucon2016_floor2)
				.withImageHeight(965.33521f)
				.withImageWidth(1636.2324f)
				.withDefaultMarkerHeight(114.80001f);

		return new ConventionMap()
				.withFloors(Arrays.asList(floor1, floor2))
				.withLocations(
						CollectionUtils.flattenList(
								inFloor(floor1,
										new MapLocation()
												.withPlace(new Place().withName("שירותים"))
												.withMarkerResource(R.raw.harucon2016_toilet_marker)
												.withSelectedMarkerResource(R.raw.harucon2016_toilet_marker_selected)
												.withMarkerHeight(75.59999f)
												.withX(1531.68f)
												.withY(575.81f),
										new MapLocation()
												.withPlace(new Place().withName("מתחם דוכנים"))
												.withMarkerResource(R.raw.harucon2016_stalls_marker)
												.withSelectedMarkerResource(R.raw.harucon2016_stalls_marker_selected)
												.withMarkerHeight(50.799999f)
												.withX(1083.18f)
												.withY(611.91f),
										new MapLocation()
												.withPlace(new Place().withName("שמירת חפצים"))
												.withMarkerResource(R.raw.harucon2016_storage_marker)
												.withSelectedMarkerResource(R.raw.harucon2016_storage_marker_selected)
												.withMarkerHeight(117.90002f)
												.withX(912.98f)
												.withY(217.61f),
										new MapLocation()
												.withPlace(new Place().withName("שירותים"))
												.withMarkerResource(R.raw.harucon2016_toilet_marker)
												.withSelectedMarkerResource(R.raw.harucon2016_toilet_marker_selected)
												.withMarkerHeight(75.59999f)
												.withX(845.57f)
												.withY(79.21f),
										new MapLocation()
												.withPlace(new Place().withName("מודיעין ודוכן אמא\"י"))
												.withMarkerResource(R.raw.harucon2016_information_marker)
												.withSelectedMarkerResource(R.raw.harucon2016_information_marker_selected)
												.withMarkerHeight(139.00003f)
												.withX(722.27f)
												.withY(329.51f),
										new MapLocation()
												.withPlace(eshkol1)
												.withMarkerResource(R.raw.harucon2016_eshkol1_marker)
												.withSelectedMarkerResource(R.raw.harucon2016_eshkol1_marker_selected)
												.withMarkerHeight(117.50000f)
												.withX(420.58f)
												.withY(526.6f),
										new MapLocation()
												.withPlace(schwatrz)
												.withMarkerResource(R.raw.harucon2016_schwartz_marker)
												.withSelectedMarkerResource(R.raw.harucon2016_schwartz_marker_selected)
												.withMarkerHeight(114.60001f)
												.withX(545.58f)
												.withY(481.71f),
										new MapLocation()
												.withPlace(eshkol3)
												.withMarkerResource(R.raw.harucon2016_eshkol3_marker)
												.withSelectedMarkerResource(R.raw.harucon2016_eshkol3_marker_selected)
												.withMarkerHeight(118.09998f)
												.withX(286.78f)
												.withY(711.61f),
										new MapLocation()
												.withPlace(eshkol2)
												.withMarkerResource(R.raw.harucon2016_eshkol2_marker)
												.withSelectedMarkerResource(R.raw.harucon2016_eshkol2_marker_selected)
												.withMarkerHeight(118.09998f)
												.withX(452.67f)
												.withY(711.61f),
										new MapLocation()
												.withPlace(new Place().withName("שירותים"))
												.withMarkerResource(R.raw.harucon2016_toilet_marker)
												.withSelectedMarkerResource(R.raw.harucon2016_toilet_marker_selected)
												.withMarkerHeight(75.59999f)
												.withX(221.38f)
												.withY(437.41f)),
								inFloor(floor2,
										// Keep this location before storage because otherwise when
										// storage is selected, it's displayed behind this location
										new MapLocation()
												.withPlace(new Place().withName("שיפוט קוספליי"))
												.withMarkerResource(R.raw.harucon2016_cosplay_judgement_marker)
												.withSelectedMarkerResource(R.raw.harucon2016_cosplay_judgement_marker_selected)
												.withMarkerHeight(114.80001f)
												.withX(1417.31f)
												.withY(654.82f),
										new MapLocation()
												.withPlace(new Place().withName("כניסה פלוס"))
												.withMarkerResource(R.raw.harucon2016_entrance_plus_marker)
												.withSelectedMarkerResource(R.raw.harucon2016_entrance_plus_marker_selected)
												.withMarkerHeight(114.80002f)
												.withX(1119.5f)
												.withY(341.03f),
										new MapLocation()
												.withPlace(mainHall)
												.withMarkerResource(R.raw.harucon2016_main_hall_marker)
												.withSelectedMarkerResource(R.raw.harucon2016_main_hall_marker_selected)
												.withMarkerHeight(175.10344f)
												.withX(1031.71f)
												.withY(503.93f),
										new MapLocation()
												.withPlace(new Place().withName("שירותים"))
												.withMarkerResource(R.raw.harucon2016_toilet_marker)
												.withSelectedMarkerResource(R.raw.harucon2016_toilet_marker_selected)
												.withMarkerHeight(75.59999f)
												.withX(991.4f)
												.withY(70.72f),
										new MapLocation()
												.withPlace(new Place().withName("משחקייה"))
												.withMarkerResource(R.raw.harucon2016_games_marker)
												.withSelectedMarkerResource(R.raw.harucon2016_games_marker_selected)
												.withMarkerHeight(114.79999f)
												.withX(848f)
												.withY(699.93f),
										new MapLocation()
												.withPlace(new Place().withName("פינת צילום"))
												.withMarkerResource(R.raw.harucon2016_photoshoots_corner_marker)
												.withSelectedMarkerResource(R.raw.harucon2016_photoshoots_corner_marker_selected)
												.withMarkerHeight(114.80001f)
												.withX(841.41f)
												.withY(314.31f),
										new MapLocation()
												.withPlace(new Place().withName("מתחם דוכנים"))
												.withMarkerResource(R.raw.harucon2016_stalls_marker)
												.withSelectedMarkerResource(R.raw.harucon2016_stalls_marker_selected)
												.withMarkerHeight(50.799999f)
												.withX(587.2f)
												.withY(333.83f),
										new MapLocation()
												.withPlace(new Place().withName("ווידוא ווקאון"))
												.withMarkerResource(R.raw.harucon2016_walkon_marker)
												.withSelectedMarkerResource(R.raw.harucon2016_walkon_marker_selected)
												.withMarkerHeight(114.80001f)
												.withX(380.16f)
												.withY(516.34f),
										new MapLocation()
												.withPlace(new Place().withName("תיקון קוספליי"))
												.withMarkerResource(R.raw.harucon2016_cosplay_fixes_marker)
												.withSelectedMarkerResource(R.raw.harucon2016_cosplay_fixes_marker_selected)
												.withMarkerHeight(114.80002f)
												.withX(339.8f)
												.withY(418.03f),
										new MapLocation()
												.withPlace(new Place().withName("שירותים"))
												.withMarkerResource(R.raw.harucon2016_toilet_marker)
												.withSelectedMarkerResource(R.raw.harucon2016_toilet_marker_selected)
												.withMarkerHeight(75.59999f)
												.withX(154.7f)
												.withY(519.23f))
						)
				);
	}
}

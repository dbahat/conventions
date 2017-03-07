package amai.org.conventions.model.conventions;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import amai.org.conventions.R;
import amai.org.conventions.model.ConventionMap;
import amai.org.conventions.model.ImageIdToImageResourceMapper;
import amai.org.conventions.model.Floor;
import amai.org.conventions.model.Hall;
import amai.org.conventions.model.MapLocation;
import amai.org.conventions.model.Place;
import amai.org.conventions.model.Stand;
import amai.org.conventions.model.StandsArea;
import amai.org.conventions.utils.CollectionUtils;
import amai.org.conventions.utils.ConventionStorage;

public class Harucon2017Convention extends AmaiConvention {
	// Hall names
	private static final String MAIN_HALL_NAME = "אולם ראשי";
	private static final String SCHWARTZ_NAME = "אודיטוריום שוורץ";
	private static final String ESHKOL1_NAME = "אשכול 1";
	private static final String ESHKOL2_NAME = "אשכול 2";
	private static final String ESHKOL3_NAME = "אשכול 3";
	private static final String GAMES_NAME = "משחקיה";
	private static final String SIGNING_NAME = "אזור החתמות";

	@Override
	protected ConventionStorage initStorage() {
		return new ConventionStorage(this, R.raw.harucon2017_convention_events, 1);
	}

	@Override
	protected Calendar initDate() {
		Calendar date = Calendar.getInstance();
		date.clear();
		date.set(2017, Calendar.MARCH, 12);
		return date;
	}

	@Override
	protected String initID() {
		return "Harucon2017";
	}

	@Override
	protected String initDisplayName() {
		return "הארוקון 2017";
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
			return new URL("http://2017.harucon.org.il/wp-admin/admin-ajax.php?action=get_event_list");
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	protected ImageIdToImageResourceMapper initImageMapper() {
		ImageIdToImageResourceMapper imageMapper = new ImageIdToImageResourceMapper();

		imageMapper.addMapping("http://2017.harucon.org.il/wp-content/uploads/sites/11/2017/02/מנגה-קפה-חברתי.jpg", R.drawable.event_manga_cafe);
		imageMapper.addMapping("http://2017.harucon.org.il/wp-content/uploads/sites/11/2016/06/אומנויות-לחימה.jpg", R.drawable.event_martial_arts);
		imageMapper.addMapping("http://2017.harucon.org.il/wp-content/uploads/sites/11/2016/06/קריסטינה.jpg", R.drawable.event_small_details);
		imageMapper.addMapping("http://2017.harucon.org.il/wp-content/uploads/sites/11/2016/06/אובנטו-יפני-טעים-קאוואי-וקל-להכנה-הרצאה-מעשית-לאוטאקו-הרעב.jpg", R.drawable.event_obento);
		imageMapper.addMapping("http://2017.harucon.org.il/wp-content/uploads/sites/11/2016/06/kuroshitsuji.jpg", R.drawable.event_kuroshitsuji);
		imageMapper.addMapping("http://2017.harucon.org.il/wp-content/uploads/sites/11/2016/06/סטודיו-טריגר.jpg", R.drawable.event_studio_trigger);
		imageMapper.addMapping("http://2017.harucon.org.il/wp-content/uploads/sites/11/2016/06/WCS.png", R.drawable.event_wcs);
		imageMapper.addMapping("http://2017.harucon.org.il/wp-content/uploads/sites/11/2016/06/דוגינשי.jpg", R.drawable.event_doujinshi);
		imageMapper.addMapping("http://2017.harucon.org.il/wp-content/uploads/sites/11/2016/06/Revolutionary-Girl-Utnea.png", R.drawable.event_utena);
		imageMapper.addMapping("http://2017.harucon.org.il/wp-content/uploads/sites/11/2016/06/16651726_10206778877070918_1169167867_n.png", R.drawable.event_harucomedy_central);
		imageMapper.addMapping("http://2017.harucon.org.il/wp-content/uploads/sites/11/2016/06/פיינל-פנטזי.jpg", R.drawable.event_final_fantasy);
		imageMapper.addMapping("http://2017.harucon.org.il/wp-content/uploads/sites/11/2016/06/מה-אתם-הייתם-עושים-שחף-עדן.jpg", R.drawable.event_what_would_you_do);
		imageMapper.addMapping("http://2017.harucon.org.il/wp-content/uploads/sites/11/2016/06/Cover_Image_GITS_Manga.png", R.drawable.event_cyberpunk);
		imageMapper.addMapping("http://2017.harucon.org.il/wp-content/uploads/sites/11/2016/06/מאגדה-לאנימה.png", R.drawable.event_legends);
		imageMapper.addMapping("http://2017.harucon.org.il/wp-content/uploads/sites/11/2016/06/בין-הראש-לדף.png", R.drawable.event_head_paper);
		imageMapper.addMapping("http://2017.harucon.org.il/wp-content/uploads/sites/11/2016/06/יוני-ההרפתקה-המבעיתה-של-גוגו.png", R.drawable.event_jojo);
		imageMapper.addMapping("http://2017.harucon.org.il/wp-content/uploads/sites/11/2016/06/טכניקות-קומיקס.jpg", R.drawable.event_comics);
		imageMapper.addMapping("http://2017.harucon.org.il/wp-content/uploads/sites/11/2016/06/מסע-בזמן.png", R.drawable.event_time_travel);
		imageMapper.addMapping("http://2017.harucon.org.il/wp-content/uploads/sites/11/2016/06/2D-הסרט.jpg", R.drawable.event_2d_movie);
		imageMapper.addMapping("http://2017.harucon.org.il/wp-content/uploads/sites/11/2016/06/14195417_524560747755018_332506590212759241_o.jpg", R.drawable.event_taiko_life);

		// Non-URL IDs
		imageMapper.addMapping(ImageIdToImageResourceMapper.EVENT_GENERIC, R.drawable.harucon2017_event_default_image);

		// Excluded IDs - mostly for debug purposes (don't show messages about these when entering an event that has them)
		// Not displayed due to bad quality
		imageMapper.addExcludedId("http://2017.harucon.org.il/wp-content/uploads/sites/11/2016/06/11219643_10207384743566314_4164409570104231506_n.jpg");
		// It's a logo, it doesn't fit in the background
		imageMapper.addExcludedId("http://2017.harucon.org.il/wp-content/uploads/sites/11/2017/02/EOJ_Logo_LONG.png");
		// Cosplay event judges
		imageMapper.addExcludedId("http://2017.harucon.org.il/wp-content/uploads/sites/11/2016/06/DafnaShaulson-150x150.jpg");
		imageMapper.addExcludedId("http://2017.harucon.org.il/wp-content/uploads/sites/11/2016/06/ofirKertes-150x150.jpg");
		imageMapper.addExcludedId("http://2017.harucon.org.il/wp-content/uploads/sites/11/2016/06/oded-150x150.jpg");
		imageMapper.addExcludedId("http://2017.harucon.org.il/wp-content/uploads/sites/11/2016/06/netta-150x150.jpg");
		imageMapper.addExcludedId("http://2017.harucon.org.il/wp-content/uploads/sites/11/2016/06/shachar-1-150x150.jpg");
		imageMapper.addExcludedId("http://2017.harucon.org.il/wp-content/uploads/sites/11/2016/07/yaelgeller-150x150.jpg");
		imageMapper.addExcludedId("http://2017.harucon.org.il/wp-content/uploads/sites/11/2016/07/nimrodgold-150x150.jpg");

		return imageMapper;
	}

	@Override
	protected List<Hall> initHalls() {
		Hall mainHall = new Hall().withName(MAIN_HALL_NAME).withOrder(1);
		Hall auditorium = new Hall().withName(SCHWARTZ_NAME).withOrder(2);
		Hall eshkol1 = new Hall().withName(ESHKOL1_NAME).withOrder(3);
		Hall eshkol2 = new Hall().withName(ESHKOL2_NAME).withOrder(4);
		Hall eshkol3 = new Hall().withName(ESHKOL3_NAME).withOrder(5);
		Hall games = new Hall().withName(GAMES_NAME).withOrder(6);
		Hall signing = new Hall().withName(SIGNING_NAME).withOrder(7);

		return Arrays.asList(mainHall, auditorium, eshkol1, eshkol2, eshkol3, games, signing);
	}

	@Override
	protected ConventionMap initMap() {
		Hall mainHall = findHallByName(MAIN_HALL_NAME);
		Hall schwatrz = findHallByName(SCHWARTZ_NAME);
		Hall eshkol1 = findHallByName(ESHKOL1_NAME);
		Hall eshkol2 = findHallByName(ESHKOL2_NAME);
		Hall eshkol3 = findHallByName(ESHKOL3_NAME);
		Hall games = findHallByName(GAMES_NAME);
		Hall signing = findHallByName(SIGNING_NAME);

		Floor floor1 = new Floor(1)
				.withName("מפלס תחתון")
				.withImageResource(R.raw.harucon2017_floor1, true)
				.withImageWidth(1224.31995f)
				.withImageHeight(626.5155f);
		Floor floor2 = new Floor(2)
				.withName("מפלס עליון")
				.withImageResource(R.raw.harucon2017_floor2, true)
				.withImageWidth(1382.21216f)
				.withImageHeight(780.93359f);

		StandsArea agam = new StandsArea().withName("טרקלין אגם").withStands(getAgamStands()).withImageResource(R.drawable.stands_agam);
		StandsArea pinkus = new StandsArea().withName("אולם פינקוס").withStands(getPinkusStands()).withImageResource(R.drawable.stands_pinkus);
		StandsArea nesher = new StandsArea().withName("רחבת הכניסה").withStands(getNesherStands()).withImageResource(R.drawable.stands_nesher);
		return new ConventionMap()
				.withFloors(Arrays.asList(floor1, floor2))
				.withLocations(
						CollectionUtils.flattenList(
								inFloor(floor1,
										new MapLocation()
												.withPlace(new Place().withName("שירותים"))
												.withMarkerResource(R.raw.harucon2017_marker_toilet, true)
												.withSelectedMarkerResource(R.raw.harucon2017_marker_toilet_selected, true)
												.withMarkerHeight(73.2f)
												.withX(1132.71f)
												.withY(405.1f),
										new MapLocation() // This is before the guest sign post so it will be selected as the stands area
												.withName("מודיעין ודוכן אמא\"י")
												.withPlace(nesher)
												.withMarkerResource(R.raw.harucon2017_marker_information, true)
												.withSelectedMarkerResource(R.raw.harucon2017_marker_information_selected, true)
												.withMarkerHeight(108.6f)
												.withX(552.12f)
												.withY(241.39f),
										new MapLocation()
												.withPlace(signing)
												.withName("אזור החתמות")
												.withMarkerResource(R.raw.harucon2017_marker_signing, true)
												.withSelectedMarkerResource(R.raw.harucon2017_marker_signing_selected, true)
												.withMarkerHeight(84.9f)
												.withX(909.42f)
												.withY(339.51f),
										new MapLocation()
												.withName("מתחם דוכנים")
												.withPlace(pinkus)
												.withMarkerResource(R.raw.harucon2017_marker_stands_pink, true)
												.withSelectedMarkerResource(R.raw.harucon2017_marker_stands_pink_selected, true)
												.withMarkerHeight(71.1f)
												.withX(779.92f)
												.withY(426.69f),
										new MapLocation()
												.withPlace(new Place().withName("שמירת חפצים"))
												.withMarkerResource(R.raw.harucon2017_marker_storage, true)
												.withSelectedMarkerResource(R.raw.harucon2017_marker_storage_selected, true)
												.withMarkerHeight(88.4f)
												.withX(695.22f)
												.withY(157.41f),
										new MapLocation()
												.withPlace(new Place().withName("שירותים"))
												.withMarkerResource(R.raw.harucon2017_marker_toilet, true)
												.withSelectedMarkerResource(R.raw.harucon2017_marker_toilet_selected, true)
												.withMarkerHeight(73.2f)
												.withX(667.23f)
												.withY(79.01f),
										new MapLocation()
												.withPlace(eshkol1)
												.withMarkerResource(R.raw.harucon2017_marker_eshkol1, true)
												.withSelectedMarkerResource(R.raw.harucon2017_marker_eshkol1_selected, true)
												.withMarkerHeight(88f)
												.withX(317.81f)
												.withY(389.2f),
										new MapLocation()
												.withPlace(schwatrz)
												.withMarkerResource(R.raw.harucon2017_marker_schwartz, true)
												.withSelectedMarkerResource(R.raw.harucon2017_marker_schwartz_selected, true)
												.withMarkerHeight(85.9f)
												.withX(431.62f)
												.withY(355.62f),
										new MapLocation()
												.withPlace(eshkol3)
												.withMarkerResource(R.raw.harucon2017_marker_eshkol3, true)
												.withSelectedMarkerResource(R.raw.harucon2017_marker_eshkol3_selected, true)
												.withMarkerHeight(88.6f)
												.withX(225.52f)
												.withY(527.91f),
										new MapLocation()
												.withPlace(eshkol2)
												.withMarkerResource(R.raw.harucon2017_marker_eshkol2, true)
												.withSelectedMarkerResource(R.raw.harucon2017_marker_eshkol2_selected, true)
												.withMarkerHeight(88.6f)
												.withX(349.92f)
												.withY(527.91f),
										new MapLocation()
												.withPlace(new Place().withName("שירותים"))
												.withMarkerResource(R.raw.harucon2017_marker_toilet, true)
												.withSelectedMarkerResource(R.raw.harucon2017_marker_toilet_selected, true)
												.withMarkerHeight(73.1f)
												.withX(176.72f)
												.withY(322.41f)),
								inFloor(floor2,
										new MapLocation()
												.withPlace(new Place().withName("שיפוט קוספליי"))
												.withMarkerResource(R.raw.harucon2017_marker_cosplay_judgement, true)
												.withSelectedMarkerResource(R.raw.harucon2017_marker_cosplay_judgement_selected, true)
												.withMarkerHeight(93.6f)
												.withX(1070.19f)
												.withY(599.92f),
										new MapLocation()
												.withPlace(mainHall)
												.withMarkerResource(R.raw.harucon2017_marker_main_hall, true)
												.withSelectedMarkerResource(R.raw.harucon2017_marker_main_hall_selected, true)
												.withMarkerHeight(142.303f)
												.withX(866.19f)
												.withY(429.06f),
										new MapLocation()
												.withPlace(new Place().withName("שירותים"))
												.withMarkerResource(R.raw.harucon2017_marker_toilet, true)
												.withSelectedMarkerResource(R.raw.harucon2017_marker_toilet_selected, true)
												.withMarkerHeight(73.2f)
												.withX(875.59f)
												.withY(125.93f),
										new MapLocation()
												.withPlace(new Place().withName("מתחם קוספליי"))
												.withMarkerResource(R.raw.harucon2017_marker_cosplay_area, true)
												.withSelectedMarkerResource(R.raw.harucon2017_marker_cosplay_area_selected, true)
												.withMarkerHeight(180.5f)
												.withX(718.07f)
												.withY(590.39f),
										new MapLocation()
												.withPlace(games)
												.withName("משחקייה")
												.withMarkerResource(R.raw.harucon2017_marker_games, true)
												.withSelectedMarkerResource(R.raw.harucon2017_marker_games_selected, true)
												.withMarkerHeight(93.7f)
												.withX(516.5f)
												.withY(528.43f),
										new MapLocation()
												.withName("סמטת האמנים")
												.withPlace(agam)
												.withMarkerResource(R.raw.harucon2017_marker_artist_alley, true)
												.withSelectedMarkerResource(R.raw.harucon2017_marker_artist_alley_selected, true)
												.withMarkerHeight(79.3f)
												.withX(516.6f)
												.withY(288.43f),
										new MapLocation()
												.withPlace(new Place().withName("שירותים"))
												.withMarkerResource(R.raw.harucon2017_marker_toilet, true)
												.withSelectedMarkerResource(R.raw.harucon2017_marker_toilet_selected, true)
												.withMarkerHeight(73.2f)
												.withX(181.77f)
												.withY(472.02f))
						)
				);
	}

	private List<Stand> getPinkusStands() {
		return Arrays.asList(
				new Stand().withName("otaku shop").withType(Stand.StandType.REGULAR_STAND).withLocationName("c01-c04"),
				new Stand().withName("retro game center").withType(Stand.StandType.REGULAR_STAND).withLocationName("c05-c08"),
				new Stand().withName("נקסוס").withType(Stand.StandType.REGULAR_STAND).withLocationName("c09-c11"),
				new Stand().withName("אוטאקו פרוג'קטו").withType(Stand.StandType.REGULAR_STAND).withLocationName("c12"),
				new Stand().withName("שרז עיצובים").withType(Stand.StandType.REGULAR_STAND).withLocationName("c13-c14"),
				new Stand().withName("קנדי לנסס").withType(Stand.StandType.REGULAR_STAND).withLocationName("c15-c20"),
				new Stand().withName("Animestuff").withType(Stand.StandType.REGULAR_STAND).withLocationName("c23"),
				new Stand().withName("קוספליי סנפאיי").withType(Stand.StandType.REGULAR_STAND).withLocationName("c24-c25"),
				new Stand().withName("סוואג").withType(Stand.StandType.REGULAR_STAND).withLocationName("c28-c31"),
				new Stand().withName("BLUP").withType(Stand.StandType.REGULAR_STAND).withLocationName("c32"),
				new Stand().withName("אנימה סטור").withType(Stand.StandType.REGULAR_STAND).withLocationName("c33-c38"),
				new Stand().withName("הקוביה").withType(Stand.StandType.REGULAR_STAND).withLocationName("c39-c40"),
				new Stand().withName("עדשות COLORVUE ועוד").withType(Stand.StandType.REGULAR_STAND).withLocationName("c41-c42"),
				new Stand().withName("וורבלה").withType(Stand.StandType.REGULAR_STAND).withLocationName("c43-c44"),
				new Stand().withName("gaming land").withType(Stand.StandType.REGULAR_STAND).withLocationName("c45-c50"),
				new Stand().withName("BrandMusical").withType(Stand.StandType.REGULAR_STAND).withLocationName("c51-c52"),
				new Stand().withName("Geek n' Otaku").withType(Stand.StandType.REGULAR_STAND).withLocationName("d01-d02"),
				new Stand().withName("מאי שירי design&art").withType(Stand.StandType.REGULAR_STAND).withLocationName("d03"),
				new Stand().withName("דוכן תרומות עבור הפקת Nmusical").withType(Stand.StandType.REGULAR_STAND).withLocationName("d04"),
				new Stand().withName("Lynnja's").withType(Stand.StandType.REGULAR_STAND).withLocationName("d06"),
				new Stand().withName("D&M Armory").withType(Stand.StandType.REGULAR_STAND).withLocationName("d09"),
				new Stand().withName("ODA").withType(Stand.StandType.REGULAR_STAND).withLocationName("d11"),
				new Stand().withName("מריונטה").withType(Stand.StandType.REGULAR_STAND).withLocationName("d15-d16"),
				new Stand().withName("ויולה ויל").withType(Stand.StandType.REGULAR_STAND).withLocationName("d17-d18"),
				new Stand().withName("המרכז ללימוד יפנית").withType(Stand.StandType.REGULAR_STAND).withLocationName("d19-d20"),
				new Stand().withName("Velvet Octopus").withType(Stand.StandType.REGULAR_STAND).withLocationName("d23-d24"),
				new Stand().withName("rivendell").withType(Stand.StandType.REGULAR_STAND).withLocationName("e01"),
				new Stand().withName("roza's art").withType(Stand.StandType.REGULAR_STAND).withLocationName("e02"),
				new Stand().withName("Lee's Stand").withType(Stand.StandType.REGULAR_STAND).withLocationName("e03-e04"),
				new Stand().withName("סרוגי").withType(Stand.StandType.REGULAR_STAND).withLocationName("e05"),
				new Stand().withName("dafna's nail art").withType(Stand.StandType.REGULAR_STAND).withLocationName("e06"),
				new Stand().withName("Aurore22").withType(Stand.StandType.REGULAR_STAND).withLocationName("e08"),
				new Stand().withName("Compoco").withType(Stand.StandType.REGULAR_STAND).withLocationName("e09-e10"),
				new Stand().withName("Crow's Treasure").withType(Stand.StandType.REGULAR_STAND).withLocationName("e11"),
				new Stand().withName("Low.Eno.Shit לא אנושיט").withType(Stand.StandType.REGULAR_STAND).withLocationName("e12"),
				new Stand().withName("בועת מחשבה").withType(Stand.StandType.REGULAR_STAND).withLocationName("e13"),
				new Stand().withName("Hatz lolita & more").withType(Stand.StandType.REGULAR_STAND).withLocationName("e14"),
				new Stand().withName("בתוך הקופסה").withType(Stand.StandType.REGULAR_STAND).withLocationName("e15-e16"),
				new Stand().withName("גיק בסטה").withType(Stand.StandType.REGULAR_STAND).withLocationName("e17"),
				new Stand().withName("Kawaii Stickers").withType(Stand.StandType.REGULAR_STAND).withLocationName("e18"),
				new Stand().withName("Harajuku Jewlery").withType(Stand.StandType.REGULAR_STAND).withLocationName("e19"),
				new Stand().withName("גיקפליז").withType(Stand.StandType.REGULAR_STAND).withLocationName("e20"),
				new Stand().withName("לימור שטרן").withType(Stand.StandType.REGULAR_STAND).withLocationName("e21-e24")
		);
	}

	private List<Stand> getAgamStands() {
		return Arrays.asList(
				new Stand().withName("Besandilove - להתלבש בתשוקה").withType(Stand.StandType.REGULAR_STAND).withLocationName("a01"),
				new Stand().withName("מאי ארט").withType(Stand.StandType.REGULAR_STAND).withLocationName("a05-a08"),
				new Stand().withName("פנדה שופ").withType(Stand.StandType.REGULAR_STAND).withLocationName("a09-a10"),
				new Stand().withName("קומיקס וירקות").withType(Stand.StandType.REGULAR_STAND).withLocationName("a11-a14"),
				new Stand().withName("שיפודן ישראל").withType(Stand.StandType.REGULAR_STAND).withLocationName("a15-a20"),
				new Stand().withName("הגלקסיה").withType(Stand.StandType.REGULAR_STAND).withLocationName("a21-a24"),
				new Stand().withName("go-japan").withType(Stand.StandType.REGULAR_STAND).withLocationName("a25-a26"),
				new Stand().withName("קומיקאזה").withType(Stand.StandType.REGULAR_STAND).withLocationName("a27-a31"),
				new Stand().withName("קבוצת יצירת קומיקס").withType(Stand.StandType.REGULAR_STAND).withLocationName("a32"),
				new Stand().withName("animewave").withType(Stand.StandType.REGULAR_STAND).withLocationName("a33-a38"),
				new Stand().withName("אנימה צ'ודוקו").withType(Stand.StandType.REGULAR_STAND).withLocationName("a39-a42"),
				new Stand().withName("Mirrorshards").withType(Stand.StandType.ARTIST_STAND).withLocationName("b01"),
				new Stand().withName("Grisim").withType(Stand.StandType.ARTIST_STAND).withLocationName("b02"),
				new Stand().withName("BL Palace").withType(Stand.StandType.ARTIST_STAND).withLocationName("b03"),
				new Stand().withName("דוכן ציור").withType(Stand.StandType.ARTIST_STAND).withLocationName("b04-b05"),
				new Stand().withName("Landes").withType(Stand.StandType.ARTIST_STAND).withLocationName("b10"),
				new Stand().withName("אביב ציפין קומיקס").withType(Stand.StandType.ARTIST_STAND).withLocationName("b11"),
				new Stand().withName("מושיק גולסט").withType(Stand.StandType.ARTIST_STAND).withLocationName("b12-b13"),
				new Stand().withName("Dor20 Studio").withType(Stand.StandType.ARTIST_STAND).withLocationName("b15"),
				new Stand().withName("adelistic").withType(Stand.StandType.ARTIST_STAND).withLocationName("b16"),
				new Stand().withName("YUEvander").withType(Stand.StandType.ARTIST_STAND).withLocationName("b18"),
				new Stand().withName("מרטין ציורים").withType(Stand.StandType.ARTIST_STAND).withLocationName("b19"),
				new Stand().withName("Yael's Colors").withType(Stand.StandType.ARTIST_STAND).withLocationName("b20"),
				new Stand().withName("היקום המקביל").withType(Stand.StandType.ARTIST_STAND).withLocationName("b22"),
				new Stand().withName("AniArt 4U").withType(Stand.StandType.ARTIST_STAND).withLocationName("b23"),
				new Stand().withName("Rinska's Booth").withType(Stand.StandType.ARTIST_STAND).withLocationName("b25-b26"),
				new Stand().withName("Fishibug").withType(Stand.StandType.ARTIST_STAND).withLocationName("b27-b28"),
				new Stand().withName("הדוכן של פיצה").withType(Stand.StandType.ARTIST_STAND).withLocationName("b29"),
				new Stand().withName("Tair Art").withType(Stand.StandType.ARTIST_STAND).withLocationName("b30")
		);
	}

	private List<Stand> getNesherStands() {
		return Arrays.asList(
				new Stand().withName("מודיעין ודוכן אמא\"י").withType(Stand.StandType.REGULAR_STAND),
				new Stand().withName("שגרירות יפן").withType(Stand.StandType.REGULAR_STAND),
				new Stand().withName("ידידות יפן").withType(Stand.StandType.REGULAR_STAND)
		);
	}
}

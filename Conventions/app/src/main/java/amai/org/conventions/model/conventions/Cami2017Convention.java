package amai.org.conventions.model.conventions;

import android.support.annotation.Nullable;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import amai.org.conventions.R;
import amai.org.conventions.feedback.SurveySender;
import amai.org.conventions.feedback.forms.EventFeedbackForm;
import amai.org.conventions.feedback.forms.EventVoteSurveyFormSender;
import amai.org.conventions.feedback.forms.FeedbackForm;
import amai.org.conventions.feedback.forms.SurveyForm;
import amai.org.conventions.model.ConventionEvent;
import amai.org.conventions.model.ConventionMap;
import amai.org.conventions.model.FeedbackQuestion;
import amai.org.conventions.model.Floor;
import amai.org.conventions.model.Hall;
import amai.org.conventions.model.Halls;
import amai.org.conventions.model.ImageIdToImageResourceMapper;
import amai.org.conventions.model.MapLocation;
import amai.org.conventions.model.Place;
import amai.org.conventions.model.Stand;
import amai.org.conventions.model.StandsArea;
import amai.org.conventions.model.Survey;
import amai.org.conventions.networking.SurveyDataRetriever;
import amai.org.conventions.utils.CollectionUtils;
import amai.org.conventions.utils.ConventionStorage;

public class Cami2017Convention extends AmaiConvention {
	// Hall names
	private static final String MAIN_HALL_NAME = "אולם ראשי";
	private static final String SCHWARTZ_NAME = "אודיטוריום שוורץ";
	private static final String ESHKOL1_NAME = "אשכול 1";
	private static final String ESHKOL2_NAME = "אשכול 2";
	private static final String ESHKOL3_NAME = "אשכול 3";
	private static final String GAMES_NAME = "משחקייה";

	// Vote questions - these values are serialized, don't change them!
	private static final int QUESTION_ID_SINGING_CONTEST_NAME = 1001;
	private static final int QUESTION_ID_SINGING_CONTEST_VOTE = 1002;
	private static final int QUESTION_ID_SHOWCASE_NAME = 1003;
	private static final int QUESTION_ID_SHOWCASE_VOTE = 1004;

	// Special events server id
	private static final int EVENT_ID_SHOWCASE = 3039;
	private static final int EVENT_ID_SINGING_CONTEST = 2984;

	// Ids of google spreadsheets associated with the special events
	private static final String SHOWCASE_SPREADSHEET_ID = "1zpNagg3Rmf7CGolTV5D8253cWgnbfVHAFE0gvBYHzEw";
	private static final String SINGING_CONTEST_SPREADSHEET_ID = "1Zqd6-hNGw7lqcyk9rONdQbFX6BnSlQ7gPr0_THPYmrc";

	static {
		FeedbackQuestion.addQuestion(QUESTION_ID_SINGING_CONTEST_NAME, R.string.singing_contest_name_question);
		FeedbackQuestion.addQuestion(QUESTION_ID_SINGING_CONTEST_VOTE, R.string.singing_contest_vote_question);
		FeedbackQuestion.addQuestion(QUESTION_ID_SHOWCASE_NAME, R.string.showcase_name_question);
		FeedbackQuestion.addQuestion(QUESTION_ID_SHOWCASE_VOTE, R.string.showcase_vote_question);
	}


	@Override
	protected ConventionStorage initStorage() {
		return new ConventionStorage(this, R.raw.cami2017_convention_events, 1);
	}

	@Override
	protected Calendar initDate() {
		Calendar date = Calendar.getInstance();
		date.clear();
		date.set(2017, Calendar.AUGUST, 24);
		return date;
	}

	@Override
	protected String initID() {
		return "Cami2017";
	}

	@Override
	protected String initDisplayName() {
		return "כאמ\"י 2017";
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
		return null;
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
			return new URL("http://2017.cami.org.il/wp-admin/admin-ajax.php?action=get_event_list");
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	protected ImageIdToImageResourceMapper initImageMapper() {
		ImageIdToImageResourceMapper imageMapper = new ImageIdToImageResourceMapper();

		imageMapper.addMapping("http://2017.cami.org.il/wp-content/uploads/sites/13/2017/07/VsiH1TU.jpg", R.drawable.event_manga_cafe);
		imageMapper.addMapping("http://2017.cami.org.il/wp-content/uploads/sites/13/2017/07/singing-contest.jpg", R.drawable.event_singing_contest);
		imageMapper.addMapping("http://2017.cami.org.il/wp-content/uploads/sites/13/2017/07/כאמידי-סנטרל-600x800.png", R.drawable.event_camidi_central);
		imageMapper.addMapping("http://2017.cami.org.il/wp-content/uploads/sites/13/2017/07/סתיו-וביאטריס-Kuroshitsuji-Mysteries-take-2.png", R.drawable.event_kuroshitsuji); // TODO take better image
		imageMapper.addMapping("http://2017.cami.org.il/wp-content/uploads/sites/13/2017/07/דש-מאמריקה-וחזרה.jpg", R.drawable.event_america);
		imageMapper.addMapping("http://2017.cami.org.il/wp-content/uploads/sites/13/2017/07/שיבוטים.jpg", R.drawable.event_clones);
		imageMapper.addMapping("http://2017.cami.org.il/wp-content/uploads/sites/13/2017/07/קנדו-ונגינאטה-החרב-או-החנית.png", R.drawable.event_kendo);
		imageMapper.addMapping("http://2017.cami.org.il/wp-content/uploads/sites/13/2017/07/אחד-בשביל-כולם-כולם-בשביל-כולם.jpg", R.drawable.event_one_for_all);
		imageMapper.addMapping("http://2017.cami.org.il/wp-content/uploads/sites/13/2017/07/קרוספליי.png", R.drawable.event_crossplay);
		imageMapper.addMapping("http://2017.cami.org.il/wp-content/uploads/sites/13/2017/08/להתגבר-ולהתבגר-אנימות-ספורט.jpg", R.drawable.event_sports_anime);
		imageMapper.addMapping("http://2017.cami.org.il/wp-content/uploads/sites/13/2017/07/cool-japan-600x800-1.jpg", R.drawable.event_cool_japan);
		imageMapper.addMapping("http://2017.cami.org.il/wp-content/uploads/sites/13/2017/07/JRPG-מא-ועד-ת.jpg", R.drawable.event_jrpg);
		imageMapper.addMapping("http://2017.cami.org.il/wp-content/uploads/sites/13/2017/07/תעשיית-המדבבים-ביפן-לא-מה-שחשבתם1.jpg", R.drawable.event_voice_actors);
		imageMapper.addMapping("http://2017.cami.org.il/wp-content/uploads/sites/13/2017/07/pop-in-q-screen1.jpg", R.drawable.event_pop_in_q);
		imageMapper.addMapping("http://2017.cami.org.il/wp-content/uploads/sites/13/2017/07/שיעור-יפנית.jpg", R.drawable.event_japanese_lesson);
		imageMapper.addMapping("http://2017.cami.org.il/wp-content/uploads/sites/13/2017/07/פולקלור-יהודי-בפוקימון1-1.jpeg", R.drawable.event_jewish_pokemon);
		imageMapper.addMapping("http://2017.cami.org.il/wp-content/uploads/sites/13/2017/07/Sound-Horizon-1.jpg", R.drawable.event_sound_horizon);
		imageMapper.addMapping("http://2017.cami.org.il/wp-content/uploads/sites/13/2017/07/WabiSabi-600x800-1.jpeg", R.drawable.event_wabisabi);
		imageMapper.addMapping("http://2017.cami.org.il/wp-content/uploads/sites/13/2017/07/giyaro-600x800.jpg", R.drawable.event_gyaru);
		imageMapper.addMapping("http://2017.cami.org.il/wp-content/uploads/sites/13/2017/07/דוקומונוגטארי-1.png", R.drawable.event_documonogatari);
		imageMapper.addMapping("http://2017.cami.org.il/wp-content/uploads/sites/13/2017/07/12345-sd-nkuj.jpg", R.drawable.event_showcase1);
		imageMapper.addMapping("http://2017.cami.org.il/wp-content/uploads/sites/13/2017/07/צליינות-מעריצים-600x800-1.jpg", R.drawable.event_conventions);
		imageMapper.addMapping("http://2017.cami.org.il/wp-content/uploads/sites/13/2017/07/פיינאל-פאנטאזי-1.jpg", R.drawable.event_final_fantasy);
		imageMapper.addMapping("http://2017.cami.org.il/wp-content/uploads/sites/13/2017/07/גנגסטרים-אלכימאים-ובני-אלמוות-1.jpg", R.drawable.event_baccano);
		imageMapper.addMapping("http://2017.cami.org.il/wp-content/uploads/sites/13/2016/01/cosplay2.jpg", R.drawable.event_cosplay1);
		imageMapper.addMapping("http://2017.cami.org.il/wp-content/uploads/sites/13/2016/01/cosplay3.jpg", R.drawable.event_cosplay2);
		imageMapper.addMapping("http://2017.cami.org.il/wp-content/uploads/sites/13/2017/07/אנימה-מעבר-לסדרות-מצויירות-1.jpg", R.drawable.event_anime_beyond_animation);
		imageMapper.addMapping("http://2017.cami.org.il/wp-content/uploads/sites/13/2017/07/על-דרקונים-וכוונות-טובות-יוקו-טארו-על-אלימות-במשחקי-וידאו-1.png", R.drawable.event_dragons);
		imageMapper.addMapping("http://2017.cami.org.il/wp-content/uploads/sites/13/2017/07/פאנסאב-לא-מה-שחשבתם.png", R.drawable.event_fansub);
		imageMapper.addMapping("http://2017.cami.org.il/wp-content/uploads/sites/13/2017/07/פאנל-יאוי-אקדמי.jpg", R.drawable.event_bl);
		imageMapper.addMapping("http://2017.cami.org.il/wp-content/uploads/sites/13/2017/07/objection-פיניקס-רייט-נגד-קאפקום.jpg", R.drawable.event_ace_attorney);
		imageMapper.addMapping("http://2017.cami.org.il/wp-content/uploads/sites/13/2017/08/pokemon-tournament.png", R.drawable.event_pokemon_tournament);

		// Non-URL IDs
		imageMapper.addMapping(ImageIdToImageResourceMapper.EVENT_GENERIC, R.drawable.cami2017_event_default_image);

		// Logo images
		imageMapper.addLogoMapping("http://2017.cami.org.il/wp-content/uploads/sites/13/2017/07/pop-in-q-screen5.jpg", R.drawable.event_pop_in_q_logo);

		// Excluded IDs - mostly for debug purposes (don't show messages about these when entering an event that has them)
		// Foreground text is not readable
		imageMapper.addExcludedId("http://2017.cami.org.il/wp-content/uploads/sites/13/2017/07/kumodesuga_edited-1.jpg");
		// Unnecessary images in events that have other images
		imageMapper.addExcludedId("http://2017.cami.org.il/wp-content/uploads/sites/13/2016/01/cosplay1.jpg");
		imageMapper.addExcludedId("http://2017.cami.org.il/wp-content/uploads/sites/13/2017/07/photo_2017-07-29_19-49-50.jpg");
		imageMapper.addExcludedId("http://2017.cami.org.il/wp-content/uploads/sites/13/2017/07/pop-in-q-screen3.jpg");
		imageMapper.addExcludedId("http://2017.cami.org.il/wp-content/uploads/sites/13/2017/07/pop-in-q-screen4.jpg");
		// Toei logo
		imageMapper.addExcludedId("http://2017.cami.org.il/wp-content/uploads/sites/13/2017/07/toei-logo.png");
		// Musical logo
		imageMapper.addExcludedId("http://2017.cami.org.il/wp-content/uploads/sites/13/2016/02/photo6023616020196993341.jpg");
		// SFF logo
		imageMapper.addExcludedId("http://2017.cami.org.il/wp-content/uploads/sites/13/2016/02/scififi-logo.png");
		// Singing contest judges
		imageMapper.addExcludedId("http://2017.cami.org.il/wp-content/uploads/sites/13/2017/07/arielle-baum.png");
		imageMapper.addExcludedId("http://2017.cami.org.il/wp-content/uploads/sites/13/2017/07/11800145_1143784582315108_9123802292981015631_n.png");
		imageMapper.addExcludedId("http://2017.cami.org.il/wp-content/uploads/sites/13/2017/07/17192456_1476939775671681_1554553118023902314_o.jpg");
		imageMapper.addExcludedId("http://2017.cami.org.il/wp-content/uploads/sites/13/2017/07/18055649_1521589434540048_4612985064956143749_o.jpg");
		// Showcase judges
		imageMapper.addExcludedId("http://2017.cami.org.il/wp-content/uploads/sites/13/2017/07/WhatsApp-Image-2017-07-29-at-20.48.44.jpeg");
		imageMapper.addExcludedId("http://2017.cami.org.il/wp-content/uploads/sites/13/2017/07/WhatsApp-Image-2017-07-29-at-20.49.18.jpeg");
		imageMapper.addExcludedId("http://2017.cami.org.il/wp-content/uploads/sites/13/2017/07/photo_2017-07-29_19-48-55.jpg");
		// Cosplay judges
		imageMapper.addExcludedId("http://2017.cami.org.il/wp-content/uploads/sites/13/2016/01/עינב-לוי.jpg");
		imageMapper.addExcludedId("http://2017.cami.org.il/wp-content/uploads/sites/13/2016/01/סיוון-מגן.jpg");
		imageMapper.addExcludedId("http://2017.cami.org.il/wp-content/uploads/sites/13/2016/01/ליאור-מוסקוביץ.jpg");
		imageMapper.addExcludedId("http://2017.cami.org.il/wp-content/uploads/sites/13/2016/01/נועה-ירון.jpg");
		imageMapper.addExcludedId("http://2017.cami.org.il/wp-content/uploads/sites/13/2016/01/טל-חזן.jpg");

		return imageMapper;
	}

	@Override
	protected Halls initHalls() {
		Hall mainHall = new Hall().withName(MAIN_HALL_NAME).withOrder(1);
		Hall auditorium = new Hall().withName(SCHWARTZ_NAME).withOrder(2);
		Hall eshkol1 = new Hall().withName(ESHKOL1_NAME).withOrder(3);
		Hall eshkol2 = new Hall().withName(ESHKOL2_NAME).withOrder(4);
		Hall eshkol3 = new Hall().withName(ESHKOL3_NAME).withOrder(5);
		Hall games = new Hall().withName(GAMES_NAME).withOrder(6);

		return new Halls(Arrays.asList(mainHall, auditorium, eshkol1, eshkol2, eshkol3, games));
	}

	@Override
	protected ConventionMap initMap() {
		Hall mainHall = this.getHalls().findByName(MAIN_HALL_NAME);
		Hall schwatrz = this.getHalls().findByName(SCHWARTZ_NAME);
		Hall eshkol1 = this.getHalls().findByName(ESHKOL1_NAME);
		Hall eshkol2 = this.getHalls().findByName(ESHKOL2_NAME);
		Hall eshkol3 = this.getHalls().findByName(ESHKOL3_NAME);
		Hall games = this.getHalls().findByName(GAMES_NAME);

		Floor floor1 = new Floor(1)
				.withName("מפלס תחתון")
				.withImageResource(R.raw.cami2017_floor1, true)
				.withImageWidth(1319.99646f)
				.withImageHeight(678.39734f);
		Floor floor2 = new Floor(2)
				.withName("מפלס עליון")
				.withImageResource(R.raw.cami2017_floor2, true)
				.withImageWidth(1403.06506f)
				.withImageHeight(788.26996f);

		StandsArea agam = new StandsArea().withName("טרקלין אגם").withStands(getAgamStands())/*.withImageResource(R.drawable.stands_agam).withImageWidth(2700).withImageHeight(1504)*/;
		StandsArea pinkus = new StandsArea().withName("אולם פינקוס").withStands(getPinkusStands())/*.withImageResource(R.drawable.stands_pinkus).withImageWidth(2700).withImageHeight(1708)*/;
		StandsArea nesher = new StandsArea().withName("רחבת הכניסה").withStands(getNesherStands())/*.withImageResource(R.drawable.stands_nesher).withImageWidth(2588).withImageHeight(1588)*/;
		return new ConventionMap()
				.withFloors(Arrays.asList(floor1, floor2))
				.withLocations(
						CollectionUtils.flattenList(
								inFloor(floor1,
										new MapLocation()
												.withPlace(new Place().withName("שירותים"))
												.withMarkerResource(R.raw.cami2017_marker_toilet, true)
												.withSelectedMarkerResource(R.raw.cami2017_marker_toilet_selected, true)
												.withMarkerHeight(80.3f)
												.withX(1232.263f)
												.withY(436.397f),
										new MapLocation() // This is before the guest sign post so it will be selected as the stands area
												.withName("מודיעין ודוכן אמא\"י")
												.withPlace(nesher)
												.withMarkerResource(R.raw.cami2017_marker_information, true)
												.withSelectedMarkerResource(R.raw.cami2017_marker_information_selected, true)
												.withMarkerHeight(119f)
												.withX(595.763f)
												.withY(257.197f),
										new MapLocation()
												.withName("מתחם דוכנים")
												.withPlace(pinkus)
												.withMarkerResource(R.raw.cami2017_marker_stands, true)
												.withSelectedMarkerResource(R.raw.cami2017_marker_stands_selected, true)
												.withMarkerHeight(78.1f)
												.withX(845.563f)
												.withY(460.197f),
										new MapLocation()
												.withPlace(new Place().withName("שמירת חפצים"))
												.withMarkerResource(R.raw.cami2017_marker_storage, true)
												.withSelectedMarkerResource(R.raw.cami2017_marker_storage_selected, true)
												.withMarkerHeight(96.9f)
												.withX(752.663f)
												.withY(164.997f),
										new MapLocation()
												.withPlace(new Place().withName("שירותים"))
												.withMarkerResource(R.raw.cami2017_marker_toilet, true)
												.withSelectedMarkerResource(R.raw.cami2017_marker_toilet_selected, true)
												.withMarkerHeight(80.3f)
												.withX(721.963f)
												.withY(78.997f),
										new MapLocation()
												.withPlace(eshkol1)
												.withMarkerResource(R.raw.cami2017_marker_eshkol1, true)
												.withSelectedMarkerResource(R.raw.cami2017_marker_eshkol1_selected, true)
												.withMarkerHeight(96.4f)
												.withX(347.663f)
												.withY(419.197f),
										new MapLocation()
												.withPlace(schwatrz)
												.withMarkerResource(R.raw.cami2017_marker_schwartz, true)
												.withSelectedMarkerResource(R.raw.cami2017_marker_schwartz_selected, true)
												.withMarkerHeight(94.2f)
												.withX(480.135f)
												.withY(388.333f),
										new MapLocation()
												.withPlace(eshkol3)
												.withMarkerResource(R.raw.cami2017_marker_eshkol3, true)
												.withSelectedMarkerResource(R.raw.cami2017_marker_eshkol3_selected, true)
												.withMarkerHeight(97.2f)
												.withX(237.763f)
												.withY(571.197f),
										new MapLocation()
												.withPlace(eshkol2)
												.withMarkerResource(R.raw.cami2017_marker_eshkol2, true)
												.withSelectedMarkerResource(R.raw.cami2017_marker_eshkol2_selected, true)
												.withMarkerHeight(97.2f)
												.withX(374.063f)
												.withY(571.197f),
										new MapLocation()
												.withPlace(new Place().withName("שירותים"))
												.withMarkerResource(R.raw.cami2017_marker_toilet, true)
												.withSelectedMarkerResource(R.raw.cami2017_marker_toilet_selected, true)
												.withMarkerHeight(80.3f)
												.withX(184.263f)
												.withY(345.797f)),
								inFloor(floor2,
										new MapLocation()
												.withPlace(new Place().withName("שיפוט קוספליי"))
												.withMarkerResource(R.raw.cami2017_marker_cosplay_judgement, true)
												.withSelectedMarkerResource(R.raw.cami2017_marker_cosplay_judgement_selected, true)
												.withMarkerHeight(96.5f)
												.withX(1102.9f)
												.withY(601.97f),
										new MapLocation()
												.withPlace(mainHall)
												.withMarkerResource(R.raw.cami2017_marker_main_hall, true)
												.withSelectedMarkerResource(R.raw.cami2017_marker_main_hall_selected, true)
												.withMarkerHeight(146.703f)
												.withX(892.7f)
												.withY(425.87f),
										new MapLocation()
												.withPlace(new Place().withName("שירותים"))
												.withMarkerResource(R.raw.cami2017_marker_toilet, true)
												.withSelectedMarkerResource(R.raw.cami2017_marker_toilet_selected, true)
												.withMarkerHeight(75.5f)
												.withX(902.4f)
												.withY(113.37f),
										new MapLocation()
												.withPlace(new Place().withName("מתחם קוספליי"))
												.withMarkerResource(R.raw.cami2017_marker_cosplay_area, true)
												.withSelectedMarkerResource(R.raw.cami2017_marker_cosplay_area_selected, true)
												.withMarkerHeight(186.1f)
												.withX(740f)
												.withY(592.17f),
										new MapLocation()
												.withPlace(games)
												.withName("משחקייה")
												.withMarkerResource(R.raw.cami2017_marker_games, true)
												.withSelectedMarkerResource(R.raw.cami2017_marker_games_selected, true)
												.withMarkerHeight(96.6f)
												.withX(532.2f)
												.withY(528.37f),
										new MapLocation()
												.withName("סמטת האמנים")
												.withPlace(agam)
												.withMarkerResource(R.raw.cami2017_marker_artist_alley, true)
												.withSelectedMarkerResource(R.raw.cami2017_marker_artist_alley_selected, true)
												.withMarkerHeight(81.7f)
												.withX(532.2f)
												.withY(280.97f),
										new MapLocation()
												.withPlace(new Place().withName("שירותים"))
												.withMarkerResource(R.raw.cami2017_marker_toilet, true)
												.withSelectedMarkerResource(R.raw.cami2017_marker_toilet_selected, true)
												.withMarkerHeight(75.5f)
												.withX(187.2f)
												.withY(470.17f))
						)
				);
	}

	private List<Stand> getPinkusStands() {
		// TODO update stands for cami 2017
		return Arrays.asList(
//				new Stand().withName("otaku shop").withType(Stand.StandType.REGULAR_STAND).withLocationName("c01-c04").withImageX(825).withImageY(535),
//				new Stand().withName("retro game center").withType(Stand.StandType.REGULAR_STAND).withLocationName("c05-c08").withImageX(1016).withImageY(535),
//				new Stand().withName("נקסוס").withType(Stand.StandType.REGULAR_STAND).withLocationName("c09-c11").withImageX(1306).withImageY(535),
//				new Stand().withName("אוטאקו פרוג'קטו").withType(Stand.StandType.REGULAR_STAND).withLocationName("c12").withImageX(1401).withImageY(535),
//				new Stand().withName("שרז עיצובים").withType(Stand.StandType.REGULAR_STAND).withLocationName("c13-c14").withImageX(1563).withImageY(535),
//				new Stand().withName("קנדי לנסס").withType(Stand.StandType.REGULAR_STAND).withLocationName("c15-c20").withImageX(1752).withImageY(535),
//				new Stand().withName("Animestuff").withType(Stand.StandType.REGULAR_STAND).withLocationName("c23").withImageX(2025).withImageY(708),
//				new Stand().withName("קוספליי סנפאיי").withType(Stand.StandType.REGULAR_STAND).withLocationName("c24-c25").withImageX(2025).withImageY(803),
//				new Stand().withName("סוואג").withType(Stand.StandType.REGULAR_STAND).withLocationName("c28-c31").withImageX(2025).withImageY(1107),
//				new Stand().withName("BLUP").withType(Stand.StandType.REGULAR_STAND).withLocationName("c32").withImageX(2068).withImageY(1303),
//				new Stand().withName("אנימה סטור").withType(Stand.StandType.REGULAR_STAND).withLocationName("c33-c38").withImageX(1959).withImageY(1428),
//				new Stand().withName("הקוביה").withType(Stand.StandType.REGULAR_STAND).withLocationName("c39-c40").withImageX(1680).withImageY(1416),
//				new Stand().withName("עדשות COLORVUE ועוד").withType(Stand.StandType.REGULAR_STAND).withLocationName("c41-c42").withImageX(1570).withImageY(1416),
//				new Stand().withName("וורבלה").withType(Stand.StandType.REGULAR_STAND).withLocationName("c43-c44").withImageX(1470).withImageY(1416),
//				new Stand().withName("gaming land").withType(Stand.StandType.REGULAR_STAND).withLocationName("c45-c50").withImageX(1087).withImageY(1428),
//				new Stand().withName("BrandMusical").withType(Stand.StandType.REGULAR_STAND).withLocationName("c51-c52").withImageX(933).withImageY(1500),
//				new Stand().withName("Geek n' Otaku").withType(Stand.StandType.REGULAR_STAND).withLocationName("d01-d02").withImageX(1427).withImageY(810),
//				new Stand().withName("מאי שירי design&art").withType(Stand.StandType.REGULAR_STAND).withLocationName("d03").withImageX(1505).withImageY(810),
//				new Stand().withName("דוכן תרומות עבור הפקת Nmusical").withType(Stand.StandType.REGULAR_STAND).withLocationName("d04").withImageX(1559).withImageY(810),
//				new Stand().withName("Lynnja's").withType(Stand.StandType.REGULAR_STAND).withLocationName("d06").withImageX(1727).withImageY(810),
//				new Stand().withName("D&M Armory").withType(Stand.StandType.REGULAR_STAND).withLocationName("d09").withImageX(1755).withImageY(979),
//				new Stand().withName("ODA").withType(Stand.StandType.REGULAR_STAND).withLocationName("d11").withImageX(1756).withImageY(1099),
//				new Stand().withName("מריונטה").withType(Stand.StandType.REGULAR_STAND).withLocationName("d15-d16").withImageX(1596).withImageY(1211),
//				new Stand().withName("ויולה ויל").withType(Stand.StandType.REGULAR_STAND).withLocationName("d17-d18").withImageX(1489).withImageY(1211),
//				new Stand().withName("המרכז ללימוד יפנית").withType(Stand.StandType.REGULAR_STAND).withLocationName("d19-d20").withImageX(1369).withImageY(1134),
//				new Stand().withName("Velvet Octopus").withType(Stand.StandType.REGULAR_STAND).withLocationName("d23-d24").withImageX(1369).withImageY(885),
//				new Stand().withName("rivendell").withType(Stand.StandType.REGULAR_STAND).withLocationName("e01").withImageX(829).withImageY(803),
//				new Stand().withName("roza's art").withType(Stand.StandType.REGULAR_STAND).withLocationName("e02").withImageX(883).withImageY(803),
//				new Stand().withName("Lee's Stand").withType(Stand.StandType.REGULAR_STAND).withLocationName("e03-e04").withImageX(963).withImageY(803),
//				new Stand().withName("סרוגי").withType(Stand.StandType.REGULAR_STAND).withLocationName("e05").withImageX(1105).withImageY(812),
//				new Stand().withName("dafna's nail art").withType(Stand.StandType.REGULAR_STAND).withLocationName("e06").withImageX(1158).withImageY(812),
//				new Stand().withName("גיל והחציל המעופף").withType(Stand.StandType.REGULAR_STAND).withLocationName("e07").withImageX(1187).withImageY(858),
//				new Stand().withName("Aurore22").withType(Stand.StandType.REGULAR_STAND).withLocationName("e08").withImageX(1187).withImageY(918),
//				new Stand().withName("Compoco").withType(Stand.StandType.REGULAR_STAND).withLocationName("e09-e10").withImageX(1187).withImageY(1012),
//				new Stand().withName("Crow's Treasure").withType(Stand.StandType.REGULAR_STAND).withLocationName("e11").withImageX(1187).withImageY(1104),
//				new Stand().withName("Low.Eno.Shit לא אנושיט").withType(Stand.StandType.REGULAR_STAND).withLocationName("e12").withImageX(1187).withImageY(1169),
//				new Stand().withName("בועת מחשבה").withType(Stand.StandType.REGULAR_STAND).withLocationName("e13").withImageX(1158).withImageY(1213),
//				new Stand().withName("Hatz lolita & more").withType(Stand.StandType.REGULAR_STAND).withLocationName("e14").withImageX(1106).withImageY(1213),
//				new Stand().withName("בתוך הקופסה").withType(Stand.StandType.REGULAR_STAND).withLocationName("e15-e16").withImageX(958).withImageY(1204),
//				new Stand().withName("גיק בסטה").withType(Stand.StandType.REGULAR_STAND).withLocationName("e17").withImageX(884).withImageY(1204),
//				new Stand().withName("Kawaii Stickers").withType(Stand.StandType.REGULAR_STAND).withLocationName("e18").withImageX(829).withImageY(1204),
//				new Stand().withName("Harajuku Jewlery").withType(Stand.StandType.REGULAR_STAND).withLocationName("e19").withImageX(800).withImageY(1158),
//				new Stand().withName("גיקפליז").withType(Stand.StandType.REGULAR_STAND).withLocationName("e20").withImageX(800).withImageY(1096),
//				new Stand().withName("לימור שטרן").withType(Stand.StandType.REGULAR_STAND).withLocationName("e21-e24").withImageX(800).withImageY(941)
		);
	}

	private List<Stand> getAgamStands() {
		// TODO update stands for cami 2017
		return Arrays.asList(
//				new Stand().withName("Besandilove - להתלבש בתשוקה").withType(Stand.StandType.REGULAR_STAND).withLocationName("a01").withImageX(396).withImageY(441),
//				new Stand().withName("גברת וודו").withType(Stand.StandType.REGULAR_STAND).withLocationName("a02").withImageX(445).withImageY(441),
//				new Stand().withName("מאי ארט").withType(Stand.StandType.REGULAR_STAND).withLocationName("a05-a08").withImageX(669).withImageY(441),
//				new Stand().withName("פנדה שופ").withType(Stand.StandType.REGULAR_STAND).withLocationName("a09-a10").withImageX(837).withImageY(441),
//				new Stand().withName("קומיקס וירקות").withType(Stand.StandType.REGULAR_STAND).withLocationName("a11-a14").withImageX(986).withImageY(441),
//				new Stand().withName("שיפודן ישראל").withType(Stand.StandType.REGULAR_STAND).withLocationName("a15-a20").withImageX(1258).withImageY(441),
//				new Stand().withName("הגלקסיה").withType(Stand.StandType.REGULAR_STAND).withLocationName("a21-a24").withImageX(1530).withImageY(441),
//				new Stand().withName("go-japan").withType(Stand.StandType.REGULAR_STAND).withLocationName("a25-a26").withImageX(1678).withImageY(441),
//				new Stand().withName("קומיקאזה").withType(Stand.StandType.REGULAR_STAND).withLocationName("a27-a31").withImageX(1874).withImageY(441),
//				new Stand().withName("קבוצת יצירת קומיקס").withType(Stand.StandType.REGULAR_STAND).withLocationName("a32").withImageX(2024).withImageY(441),
//				new Stand().withName("animewave").withType(Stand.StandType.REGULAR_STAND).withLocationName("a33-a38").withImageX(2224).withImageY(441),
//				new Stand().withName("אנימה צ'ודוקו").withType(Stand.StandType.REGULAR_STAND).withLocationName("a39-a42").withImageX(459).withImageY(837),
//				new Stand().withName("Mirrorshards").withType(Stand.StandType.ARTIST_STAND).withLocationName("b01").withImageX(1013).withImageY(604),
//				new Stand().withName("Grisim").withType(Stand.StandType.ARTIST_STAND).withLocationName("b02").withImageX(1073).withImageY(604),
//				new Stand().withName("BL Palace").withType(Stand.StandType.ARTIST_STAND).withLocationName("b03").withImageX(1123).withImageY(604),
//				new Stand().withName("דוכן ציור").withType(Stand.StandType.ARTIST_STAND).withLocationName("b04-b05").withImageX(1208).withImageY(604),
//				new Stand().withName("Landes").withType(Stand.StandType.ARTIST_STAND).withLocationName("b10").withImageX(1564).withImageY(604),
//				new Stand().withName("אביב ציפין קומיקס").withType(Stand.StandType.ARTIST_STAND).withLocationName("b11").withImageX(1619).withImageY(604),
//				new Stand().withName("מושיק גולסט").withType(Stand.StandType.ARTIST_STAND).withLocationName("b12-b13").withImageX(1650).withImageY(685),
//				new Stand().withName("Dor20 Studio").withType(Stand.StandType.ARTIST_STAND).withLocationName("b15").withImageX(1656).withImageY(877),
//				new Stand().withName("adelistic").withType(Stand.StandType.ARTIST_STAND).withLocationName("b16").withImageX(1621).withImageY(925),
//				new Stand().withName("YUEvander").withType(Stand.StandType.ARTIST_STAND).withLocationName("b18").withImageX(1512).withImageY(925),
//				new Stand().withName("מרטין ציורים").withType(Stand.StandType.ARTIST_STAND).withLocationName("b19").withImageX(1453).withImageY(925),
//				new Stand().withName("Yael's Colors").withType(Stand.StandType.ARTIST_STAND).withLocationName("b20").withImageX(1400).withImageY(925),
//				new Stand().withName("היקום המקביל").withType(Stand.StandType.ARTIST_STAND).withLocationName("b22").withImageX(1238).withImageY(925),
//				new Stand().withName("AniArt 4U").withType(Stand.StandType.ARTIST_STAND).withLocationName("b23").withImageX(1181).withImageY(925),
//				new Stand().withName("Rinska's Booth").withType(Stand.StandType.ARTIST_STAND).withLocationName("b25-b26").withImageX(1043).withImageY(925),
//				new Stand().withName("Fishibug").withType(Stand.StandType.ARTIST_STAND).withLocationName("b27-b28").withImageX(987).withImageY(843),
//				new Stand().withName("הדוכן של פיצה").withType(Stand.StandType.ARTIST_STAND).withLocationName("b29").withImageX(987).withImageY(718),
//				new Stand().withName("Tair Art").withType(Stand.StandType.ARTIST_STAND).withLocationName("b30").withImageX(987).withImageY(655)
		);
	}

	private List<Stand> getNesherStands() {
		// TODO update stands for cami 2017
		return Arrays.asList(
//				new Stand().withName("מודיעין ודוכן אמא\"י").withType(Stand.StandType.REGULAR_STAND).withImageX(1276).withImageY(948),
//				new Stand().withName("שגרירות יפן").withType(Stand.StandType.REGULAR_STAND).withImageX(1112).withImageY(1000),
//				new Stand().withName("ידידות יפן").withType(Stand.StandType.REGULAR_STAND).withImageX(878).withImageY(1000)
		);
	}

	@Override
	public SurveySender getEventVoteSender(final ConventionEvent event) {
		if (event.getUserInput().getVoteSurvey() == null) {
			return null;
		}
		try {
			if (event.getServerId() == EVENT_ID_SHOWCASE) {
				SurveyForm form = new SurveyForm()
						.withQuestionEntry(QUESTION_ID_SHOWCASE_NAME, "entry.1893333202")
						.withQuestionEntry(QUESTION_ID_SHOWCASE_VOTE, "entry.1772924702")
						.withSendUrl(new URL("https://docs.google.com/forms/d/e/1FAIpQLSf4m0Azy1HFovoPF7VXY0IFLM1s0z0o18SDHfjZKw6c6UXvcw/formResponse"));

				SurveyDataRetriever.DisabledMessage disabledMessageRetriever = new SurveyDataRetriever.GoogleSpreadSheet(SHOWCASE_SPREADSHEET_ID);

				return new EventVoteSurveyFormSender(form, event.getUserInput().getVoteSurvey(), disabledMessageRetriever);

			} else if (event.getServerId() == EVENT_ID_SINGING_CONTEST) {
				SurveyForm form = new SurveyForm()
						.withQuestionEntry(QUESTION_ID_SINGING_CONTEST_NAME, "entry.109802680")
						.withQuestionEntry(QUESTION_ID_SINGING_CONTEST_VOTE, "entry.1600353678")
						.withSendUrl(new URL("https://docs.google.com/forms/d/e/1FAIpQLScyynW3kBT4blxsiEBzdEbMV-6pEuKhjux0PesVteOUTqffWA/formResponse"));

				SurveyDataRetriever.DisabledMessage disabledMessageRetriever = new SurveyDataRetriever.GoogleSpreadSheet(SINGING_CONTEST_SPREADSHEET_ID);

				return new EventVoteSurveyFormSender(form, event.getUserInput().getVoteSurvey(), disabledMessageRetriever);
			}
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
		return super.getEventVoteSender(event);
	}

	@Override
	@Nullable
	public SurveyDataRetriever.Answers createSurveyAnswersRetriever(FeedbackQuestion question) {
		switch (question.getQuestionId()) {
			case QUESTION_ID_SHOWCASE_VOTE: {
				return new SurveyDataRetriever.GoogleSpreadSheet(SHOWCASE_SPREADSHEET_ID);
			}
			case QUESTION_ID_SINGING_CONTEST_VOTE: {
				return new SurveyDataRetriever.GoogleSpreadSheet(SINGING_CONTEST_SPREADSHEET_ID);
			}
		}

		return null;
	}

	@Override
	protected ConventionEvent.UserInput createUserInputForEvent(ConventionEvent event) {
		ConventionEvent.UserInput userInput = super.createUserInputForEvent(event);
		convertUserInputForEvent(userInput, event);
		return userInput;
	}

	@Override
	protected void convertUserInputForEvent(ConventionEvent.UserInput userInput, ConventionEvent event) {
		super.convertUserInputForEvent(userInput, event);
		if (userInput.getVoteSurvey() == null) {
			if (event.getServerId() == EVENT_ID_SHOWCASE) {
				userInput.setVoteSurvey(new Survey().withQuestions(
						new FeedbackQuestion(QUESTION_ID_SHOWCASE_NAME, FeedbackQuestion.AnswerType.TEXT, true),
						new FeedbackQuestion(QUESTION_ID_SHOWCASE_VOTE, FeedbackQuestion.AnswerType.MULTIPLE_ANSWERS_RADIO, true)
				));
			} else if (event.getServerId() == EVENT_ID_SINGING_CONTEST) {
				userInput.setVoteSurvey(new Survey().withQuestions(
						new FeedbackQuestion(QUESTION_ID_SINGING_CONTEST_NAME, FeedbackQuestion.AnswerType.TEXT, true),
						new FeedbackQuestion(QUESTION_ID_SINGING_CONTEST_VOTE, FeedbackQuestion.AnswerType.MULTIPLE_ANSWERS_RADIO, true)
				));
			}
		}
	}
}

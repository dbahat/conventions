package amai.org.conventions.model.conventions;

import androidx.annotation.Nullable;

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
import amai.org.conventions.model.EventType;
import amai.org.conventions.model.FeedbackQuestion;
import amai.org.conventions.model.Floor;
import amai.org.conventions.model.Hall;
import amai.org.conventions.model.Halls;
import amai.org.conventions.model.ImageIdToImageResourceMapper;
import amai.org.conventions.model.MapLocation;
import amai.org.conventions.model.Place;
import amai.org.conventions.model.SpecialEventsProcessor;
import amai.org.conventions.model.Stand;
import amai.org.conventions.model.StandsArea;
import amai.org.conventions.model.Survey;
import amai.org.conventions.networking.SurveyDataRetriever;
import amai.org.conventions.utils.CollectionUtils;
import amai.org.conventions.utils.ConventionStorage;

public class Cami2019Convention extends AmaiConvention {
    // Hall names
    private static final String MAIN_HALL_NAME = "אולם ראשי";
    private static final String SCHWARTZ_NAME = "אודיטוריום שוורץ";
    private static final String ESHKOL1_NAME = "אשכול 1";
    private static final String ESHKOL2_NAME = "אשכול 2";
    private static final String ESHKOL3_NAME = "אשכול 3";
    private static final String WORKSHOPS_NAME = "חדר סדנאות";
    private static final String GAMES_NAME = "משחקייה";
    private static final String COSPLAY_AREA_NAME = "מתחם קוספליי";
    // Location names
    public static final String PARENTS_ROOM_NAME = "חדר הורים";

    // Vote questions - these values are serialized, don't change them!
    private static final int QUESTION_ID_AMAIDOL_VOTE = 1000;
    private static final int QUESTION_ID_AMAIDOL_NAME = 1001;

    // Special events server id
    private static final int EVENT_ID_AMAIDOL = 7661;

    // Ids of google spreadsheets associated with the special events
    private static final String AMAIDOL_SPREADSHEET_ID = "1u9xu3FNq2gA25oZoVHVguTzJA5HheXWPf2wnUj-iipE";

    static {
        FeedbackQuestion.addQuestion(QUESTION_ID_AMAIDOL_NAME, R.string.amaidol_name_question);
        FeedbackQuestion.addQuestion(QUESTION_ID_AMAIDOL_VOTE, R.string.amaidol_vote_question);
    }

    @Override
    protected ConventionStorage initStorage() {
        return new ConventionStorage(this, R.raw.cami2019_convention_events, 0);
    }

    @Override
    protected Calendar initDate() {
        Calendar date = Calendar.getInstance();
        date.clear();
        date.set(2019, Calendar.AUGUST, 1);
        return date;
    }

    @Override
    protected String initID() {
        return "Cami2019";
    }

    @Override
    protected String initDisplayName() {
        return "כאמ\"י 2019";
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
                    .withSendUrl(new URL("https://docs.google.com/forms/d/e/1FAIpQLSc8HLV_-SYKWyWfdDEFSEwI6GiKfa900bYlHr_08sgWwYhcDQ/formResponse"));
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
                    .withSendUrl(new URL("https://docs.google.com/forms/d/e/1FAIpQLSdp4Nw5-H86qyVhMFIbiowbPs2edqAv_gNSIbDvTaB9hvP56g/formResponse"));
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
        return feedbackForm;
    }

    @Override
    protected URL initModelURL() {
        try {
            return new URL("http://2019.cami.org.il/wp-admin/admin-ajax.php?action=get_event_list");
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected ImageIdToImageResourceMapper initImageMapper() {
        ImageIdToImageResourceMapper imageMapper = new ImageIdToImageResourceMapper();

        imageMapper
                .addMapping("https://cami.org.il/2019/wp-content/uploads/sites/20/2019/07/Nathan.png", R.drawable.event_natan)
                .addMapping("https://cami.org.il/2019/wp-content/uploads/sites/20/2019/06/noa.png", R.drawable.event_noa)
                .addMapping("https://cami.org.il/2019/wp-content/uploads/sites/20/2019/06/SHAI.png", R.drawable.event_shai)
                .addMapping("https://cami.org.il/2019/wp-content/uploads/sites/20/2019/06/TOMER.jpg", R.drawable.event_tomer)
                .addMapping("https://cami.org.il/2019/wp-content/uploads/sites/20/2019/06/OPHIR.jpg", R.drawable.event_ofir)
                .addMapping("https://cami.org.il/2019/wp-content/uploads/sites/20/2019/06/EFRAT.jpg", R.drawable.event_efrat)
                .addMapping("https://cami.org.il/2019/wp-content/uploads/sites/20/2019/06/GUY.jpg", R.drawable.event_guy)
                .addMapping("https://cami.org.il/2019/wp-content/uploads/sites/20/2019/06/OMER.jpg", R.drawable.event_omer)
                .addMapping("https://cami.org.il/2019/wp-content/uploads/sites/20/2019/06/ODE.jpg", R.drawable.event_ode)
                .addMapping("https://cami.org.il/2019/wp-content/uploads/sites/20/2019/06/SHANI.jpg", R.drawable.event_shani)
                .addMapping("https://cami.org.il/2019/wp-content/uploads/sites/20/2019/06/LEON2.jpg", R.drawable.event_leon_fanfic)
                .addMapping("https://cami.org.il/2019/wp-content/uploads/sites/20/2019/06/LEON1.jpg", R.drawable.event_leon_fighting)
                .addMapping("https://cami.org.il/2019/wp-content/uploads/sites/20/2019/06/NIR.png", R.drawable.event_nir)
                .addMapping("https://cami.org.il/2019/wp-content/uploads/sites/20/2019/06/HANA.jpg", R.drawable.event_hana)
                .addMapping("https://cami.org.il/2019/wp-content/uploads/sites/20/2019/06/Idan.jpg", R.drawable.event_idan)
                .addMapping("https://cami.org.il/2019/wp-content/uploads/sites/20/2019/06/ODED.png", R.drawable.event_oded)
        ;

        imageMapper.addMapping(ImageIdToImageResourceMapper.EVENT_GENERIC, R.drawable.cami2019_event_default_background);

        // Excluded IDs - mostly for debug purposes (don't show messages about these when entering an event that has them)
        imageMapper.addExcludedIds(
                // Events without mobile scaled images
                "https://cami.org.il/2019/wp-content/uploads/sites/20/2019/02/socialmanga.jpg",
                "https://cami.org.il/2019/wp-content/uploads/sites/20/2018/06/Lior.png",
                "https://cami.org.il/2019/wp-content/uploads/sites/20/2019/06/PanelsForSite.png",
                "https://cami.org.il/2019/wp-content/uploads/sites/20/2019/06/yoko.jpg",
                "https://cami.org.il/2019/wp-content/uploads/sites/20/2018/06/singing-contest1.jpg",
                // Amaidol judges
                "https://cami.org.il/2019/wp-content/uploads/sites/20/2019/06/Netta.png",
                "https://cami.org.il/2019/wp-content/uploads/sites/20/2019/06/Sela.jpg",
                "https://cami.org.il/2019/wp-content/uploads/sites/20/2018/06/Lior.png",
                "https://cami.org.il/2019/wp-content/uploads/sites/20/2019/06/Sela.jpg",
                "https://cami.org.il/2019/wp-content/uploads/sites/20/2018/06/Lior.png",
                // Cosplay / showcase judges
                "https://cami.org.il/2019/wp-content/uploads/sites/20/2018/07/IMG_9671-300x200.jpg",
                "https://cami.org.il/2019/wp-content/uploads/sites/20/2018/07/MG_8954-300x200.jpg",
                "https://cami.org.il/2019/wp-content/uploads/sites/20/2019/06/טל-חזן.jpg",
                "https://cami.org.il/2019/wp-content/uploads/sites/20/2019/06/אלכס-רוד.jpg",
                "https://cami.org.il/2019/wp-content/uploads/sites/20/2019/06/סתיו-גיני.jpg",
                "https://cami.org.il/2019/wp-content/uploads/sites/20/2019/06/אופיר-לוטן.jpg",
                "https://cami.org.il/2019/wp-content/uploads/sites/20/2019/06/שחר-אגרנט.jpg",
                "https://cami.org.il/2019/wp-content/uploads/sites/20/2018/07/IMG_9671-300x200.jpg",
                "https://cami.org.il/2019/wp-content/uploads/sites/20/2018/07/MG_8954-300x200.jpg",
                "https://cami.org.il/2019/wp-content/uploads/sites/20/2019/06/טל-חזן.jpg",
                "https://cami.org.il/2019/wp-content/uploads/sites/20/2019/06/אלכס-רוד.jpg",
                "https://cami.org.il/2019/wp-content/uploads/sites/20/2019/06/סתיו-גיני.jpg",
                "https://cami.org.il/2019/wp-content/uploads/sites/20/2019/06/אופיר-לוטן.jpg",
                "https://cami.org.il/2019/wp-content/uploads/sites/20/2019/06/שחר-אגרנט.jpg"
        );

        return imageMapper;
    }

    @Override
    protected Halls initHalls() {
        Hall mainHall = new Hall().withName(MAIN_HALL_NAME).withOrder(1);
        Hall auditorium = new Hall().withName(SCHWARTZ_NAME).withOrder(2);
        Hall eshkol1 = new Hall().withName(ESHKOL1_NAME).withOrder(3);
        Hall eshkol2 = new Hall().withName(ESHKOL2_NAME).withOrder(4);
        Hall eshkol3 = new Hall().withName(ESHKOL3_NAME).withOrder(5);
        Hall workshops = new Hall().withName(WORKSHOPS_NAME).withOrder(6);
        Hall games = new Hall().withName(GAMES_NAME).withOrder(7);
        Hall cosplayArea = new Hall().withName(COSPLAY_AREA_NAME).withOrder(8);
        return new Halls(Arrays.asList(mainHall, auditorium, eshkol1, eshkol2, eshkol3, workshops, games, cosplayArea));
    }

    @Override
    protected ConventionMap initMap() {
        Hall mainHall = this.getHalls().findByName(MAIN_HALL_NAME);
        Hall schwatrz = this.getHalls().findByName(SCHWARTZ_NAME);
        Hall eshkol1 = this.getHalls().findByName(ESHKOL1_NAME);
        Hall eshkol2 = this.getHalls().findByName(ESHKOL2_NAME);
        Hall eshkol3 = this.getHalls().findByName(ESHKOL3_NAME);
        Hall workshops = this.getHalls().findByName(WORKSHOPS_NAME);
        Hall games = this.getHalls().findByName(GAMES_NAME);
        Hall cosplayArea = this.getHalls().findByName(COSPLAY_AREA_NAME);

        Floor floor1 = new Floor(1)
                .withName("קומת כניסה")
                .withImageResource(R.drawable.cami2019_floor1, false)
                .withImageWidth(1337.59998f)
                .withImageHeight(650.61267f);
        Floor floor2 = new Floor(2)
                .withName("קומה עליונה")
                .withImageResource(R.drawable.cami2019_floor2, false)
                .withImageWidth(1488.43005f)
                .withImageHeight(821.03003f);

        StandsArea agam = new StandsArea().withName("טרקלין אגם").withStands(getAgamStands())/*.withImageResource(R.drawable.stands_agam).withImageWidth(2700).withImageHeight(1504)*/;
        StandsArea pinkus = new StandsArea().withName("אולם פינקוס").withStands(getPinkusStands())/*.withImageResource(R.drawable.stands_pinkus).withImageWidth(2700).withImageHeight(1708)*/;
        StandsArea nesher = new StandsArea().withName("רחבת הכניסה").withStands(getNesherStands())/*.withImageResource(R.drawable.stands_nesher).withImageWidth(2588).withImageHeight(1588)*/;
        return new ConventionMap()
                .withFloors(Arrays.asList(floor1, floor2))
                .withDefaultFloor(floor1)
                .withLocations(
                        CollectionUtils.flattenList(
                                inFloor(floor1,
                                        new MapLocation()
                                                .withPlace(new Place().withName("שירותים"))
                                                .withMarkerResource(R.raw.cami2019_marker_toilet1, true)
                                                .withSelectedMarkerResource(R.raw.cami2019_marker_toilet1_selected, true)
                                                .withMarkerHeight(66.83f)
                                                .withX(1240.81f)
                                                .withY(445.573f),
                                        new MapLocation() // This is before the guest sign post so it will be selected as the stands area
                                                .withName("עמדת מודיעין ומרצ'נדייז כנסי")
                                                .withPlace(nesher)
                                                .withMarkerResource(R.raw.cami2019_marker_information, true)
                                                .withSelectedMarkerResource(R.raw.cami2019_marker_information_selected, true)
                                                .withMarkerHeight(61.33f)
                                                .withX(629.3f)
                                                .withY(294.183f),
                                        new MapLocation()
                                                .withPlace(new Place().withName("אזור החתמות"))
                                                .withMarkerResource(R.raw.cami2019_marker_signing, true)
                                                .withSelectedMarkerResource(R.raw.cami2019_marker_signing_selected, true)
                                                .withMarkerHeight(71.12f)
                                                .withX(1064.09f)
                                                .withY(406.843f, true),
                                        new MapLocation()
                                                .withName("מתחם דוכנים")
                                                .withPlace(pinkus)
                                                .withMarkerResource(R.raw.cami2019_marker_stands, true)
                                                .withSelectedMarkerResource(R.raw.cami2019_marker_stands_selected, true)
                                                .withMarkerHeight(64.91f)
                                                .withX(882.73f)
                                                .withY(485.103f),
                                        new MapLocation()
                                                .withPlace(new Place().withName("שמירת חפצים"))
                                                .withMarkerResource(R.raw.cami2019_marker_storage, true)
                                                .withSelectedMarkerResource(R.raw.cami2019_marker_storage_selected, true)
                                                .withMarkerHeight(61.34f)
                                                .withX(770.75f)
                                                .withY(176.613f),
                                        new MapLocation()
                                                .withPlace(new Place().withName("שירותים"))
                                                .withMarkerResource(R.raw.cami2019_marker_toilet2, true)
                                                .withSelectedMarkerResource(R.raw.cami2019_marker_toilet2_selected, true)
                                                .withMarkerHeight(79.27f)
                                                .withX(716.7f)
                                                .withY(92.833f, true),
                                        new MapLocation()
                                                .withPlace(new Place().withName("מתחם כניסה"))
                                                .withMarkerResource(R.raw.cami2019_marker_entrance, true)
                                                .withSelectedMarkerResource(R.raw.cami2019_marker_entrance_selected, true)
                                                .withMarkerHeight(80.52f)
                                                .withX(490.3f)
                                                .withY(131.323f, true),
                                        new MapLocation()
                                                .withPlace(eshkol1)
                                                .withMarkerResource(R.raw.cami2019_marker_eshkol1, true)
                                                .withSelectedMarkerResource(R.raw.cami2019_marker_eshkol1_selected, true)
                                                .withMarkerHeight(53.36f)
                                                .withX(354.73f)
                                                .withY(439.703f),
                                        new MapLocation()
                                                .withPlace(schwatrz)
                                                .withMarkerResource(R.raw.cami2019_marker_schwartz, true)
                                                .withSelectedMarkerResource(R.raw.cami2019_marker_schwartz_selected, true)
                                                .withMarkerHeight(53.36f)
                                                .withX(439.2f)
                                                .withY(397.433f),
                                        new MapLocation()
                                                .withPlace(eshkol3)
                                                .withMarkerResource(R.raw.cami2019_marker_eshkol3, true)
                                                .withSelectedMarkerResource(R.raw.cami2019_marker_eshkol3_selected, true)
                                                .withMarkerHeight(53.36f)
                                                .withX(232.84f)
                                                .withY(579.583f),
                                        new MapLocation()
                                                .withPlace(eshkol2)
                                                .withMarkerResource(R.raw.cami2019_marker_eshkol2, true)
                                                .withSelectedMarkerResource(R.raw.cami2019_marker_eshkol2_selected, true)
                                                .withMarkerHeight(53.36f)
                                                .withX(396.62f)
                                                .withY(572.923f),
                                        new MapLocation()
                                                .withPlace(new Place().withName("שירותים"))
                                                .withMarkerResource(R.raw.cami2019_marker_toilet2, true)
                                                .withSelectedMarkerResource(R.raw.cami2019_marker_toilet2_selected, true)
                                                .withMarkerHeight(79.28f)
                                                .withX(192.24f)
                                                .withY(383.263f, true)),
                                inFloor(floor2,
                                        new MapLocation()
                                                .withPlace(workshops)
                                                .withMarkerResource(R.raw.cami2019_marker_workshops, true)
                                                .withSelectedMarkerResource(R.raw.cami2019_marker_workshops_selected, true)
                                                .withMarkerHeight(83.4f)
                                                .withX(1209.94f)
                                                .withY(727.63f),
                                        new MapLocation()
                                                .withPlace(new Place().withName("שיפוט קוספליי"))
                                                .withMarkerResource(R.raw.cami2019_marker_cosplay_judgement, true)
                                                .withSelectedMarkerResource(R.raw.cami2019_marker_cosplay_judgement_selected, true)
                                                .withMarkerHeight(72.39f)
                                                .withX(1132.72f)
                                                .withY(686.64f),
                                        new MapLocation()
                                                .withPlace(new Place().withName(PARENTS_ROOM_NAME))
                                                .withMarkerResource(R.raw.cami2019_marker_parents_room, true)
                                                .withSelectedMarkerResource(R.raw.cami2019_marker_parents_room_selected, true)
                                                .withMarkerHeight(64.23f)
                                                .withX(1194.57f)
                                                .withY(612.15f),
                                        new MapLocation()
                                                .withPlace(mainHall)
                                                .withMarkerResource(R.raw.cami2019_marker_main_hall, true)
                                                .withSelectedMarkerResource(R.raw.cami2019_marker_main_hall_selected, true)
                                                .withMarkerHeight(98.04f)
                                                .withX(973.05f)
                                                .withY(434.35f),
                                        new MapLocation()
                                                .withPlace(new Place().withName("שירותים"))
                                                .withMarkerResource(R.raw.cami2019_marker_toilet1, true)
                                                .withSelectedMarkerResource(R.raw.cami2019_marker_toilet1_selected, true)
                                                .withMarkerHeight(66.82f)
                                                .withX(983.29f)
                                                .withY(102.06f),
                                        new MapLocation()
                                                .withPlace(cosplayArea)
                                                .withMarkerResource(R.raw.cami2019_marker_cosplay_area, true)
                                                .withSelectedMarkerResource(R.raw.cami2019_marker_cosplay_area_selected, true)
                                                .withMarkerHeight(151.73f)
                                                .withX(830.06f)
                                                .withY(614.89f),
                                        new MapLocation()
                                                .withPlace(games)
                                                .withName("משחקייה")
                                                .withMarkerResource(R.raw.cami2019_marker_games, true)
                                                .withSelectedMarkerResource(R.raw.cami2019_marker_games_selected, true)
                                                .withMarkerHeight(151.73f)
                                                .withX(608.7f)
                                                .withY(532.77f),
                                        new MapLocation()
                                                .withName("שדרת האמנים ומתחם דוכנים")
                                                .withPlace(agam)
                                                .withMarkerResource(R.raw.cami2019_marker_artists_alley, true)
                                                .withSelectedMarkerResource(R.raw.cami2019_marker_artists_alley_selected, true)
                                                .withMarkerHeight(89.71f)
                                                .withX(624.84f)
                                                .withY(286.98f),
                                        new MapLocation()
                                                .withPlace(new Place().withName("שירותים"))
                                                .withMarkerResource(R.raw.cami2019_marker_toilet3, true)
                                                .withSelectedMarkerResource(R.raw.cami2019_marker_toilet3_selected, true)
                                                .withMarkerHeight(77.67f)
                                                .withX(261.87f)
                                                .withY(393.43f, true),
                                        new MapLocation()
                                                .withPlace(new Place().withName("מעבר לאשכולות"))
                                                .withMarkerResource(R.raw.cami2019_marker_eshkols_passage, true)
                                                .withSelectedMarkerResource(R.raw.cami2019_marker_eshkols_passage_selected, true)
                                                .withMarkerHeight(90.96f)
                                                .withX(77.7f)
                                                .withY(393.93f))
                        )
                );
    }

    private List<Stand> getPinkusStands() {
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
            if (event.getServerId() == EVENT_ID_AMAIDOL) {
                SurveyForm form = new SurveyForm()
                        .withQuestionEntry(QUESTION_ID_AMAIDOL_NAME, "entry.109802680")
                        .withQuestionEntry(QUESTION_ID_AMAIDOL_VOTE, "entry.1600353678")
                        .withSendUrl(new URL("https://docs.google.com/forms/d/e/1FAIpQLSf3BlH3jonMmQ-eQo1MeQ76s31ak2824eJVsMcl6IlqueqEDw/formResponse"));

                SurveyDataRetriever.DisabledMessage disabledMessageRetriever = new SurveyDataRetriever.GoogleSpreadSheet(AMAIDOL_SPREADSHEET_ID);

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
            case QUESTION_ID_AMAIDOL_VOTE: {
                return new SurveyDataRetriever.GoogleSpreadSheet(AMAIDOL_SPREADSHEET_ID);
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
    public void convertUserInputForEvent(ConventionEvent.UserInput userInput, ConventionEvent event) {
        super.convertUserInputForEvent(userInput, event);

        if (userInput.getVoteSurvey() == null && event != null) {
            if (event.getServerId() == EVENT_ID_AMAIDOL) {
                userInput.setVoteSurvey(new Survey().withQuestions(
                        new FeedbackQuestion(QUESTION_ID_AMAIDOL_NAME, FeedbackQuestion.AnswerType.SINGLE_LINE_TEXT, true),
                        new FeedbackQuestion(QUESTION_ID_AMAIDOL_VOTE, FeedbackQuestion.AnswerType.MULTIPLE_ANSWERS_RADIO, true)
                ));
            }
        }
    }

    @Override
    public SpecialEventsProcessor getSpecialEventsProcessor() {
        return new SpecialEventsProcessor() {
            @Override
            public boolean processSpecialEvent(ConventionEvent event) {
                return false;
            }
        };
    }

    @Override
    public int getEventIcon(ConventionEvent event) {
        EventType type = event.getType();
        switch (type.getDescription()) {
            case "הרצאות":
                return R.drawable.cami2019_event_icon_lectures;
            case "קוספליי":
                return R.drawable.cami2019_event_icon_cosplay;
            case "מיוחד":
            case "אירועים מיוחדים":
                return R.drawable.cami2019_event_icon_special;
            case "פאנל":
                return R.drawable.cami2019_event_icon_panel;
            case "סדנה":
                return R.drawable.cami2019_event_icon_workshop;
            default:
                return R.drawable.cami2019_event_icon_other;
        }
    }
}

package amai.org.conventions.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import amai.org.conventions.R;
import amai.org.conventions.utils.CollectionUtils;
import amai.org.conventions.utils.ConventionStorage;

public class Convention implements Serializable {

    private static Convention convention = new Convention();

    private List<Hall> halls;
    private List<ConventionEvent> events;
    private List<Update> updates;
	private Map<String, ConventionEvent.UserInput> userInput;
    private String feedbackRecipient;

    private ConventionMap map;
    private Calendar date;

    private ReentrantReadWriteLock eventLockObject = new ReentrantReadWriteLock();
    private ConventionStorage conventionStorage;

    public static Convention getInstance() {
        return convention;
    }

    public ConventionStorage getStorage() {
        return conventionStorage;
    }

    public Convention() {
        this.conventionStorage = new ConventionStorage();
	    this.userInput = new LinkedHashMap<>();

        this.date = Calendar.getInstance();
	    this.date.clear();
        this.date.set(2015, Calendar.AUGUST, 20);
        this.feedbackRecipient = "cami2015androidapp@gmail.com";

	    Hall mainHall = new Hall().withName("אולם ראשי").withOrder(1);
        Hall auditorium = new Hall().withName("אודיטוריום שוורץ").withOrder(2);
        Hall eshkol1 = new Hall().withName("אשכול 1").withOrder(3);
        Hall eshkol3 = new Hall().withName("אשכול 3").withOrder(4);
        Hall games = new Hall().withName("משחקייה").withOrder(5);
        Hall specialEvents = new Hall().withName("אירועים מיוחדים").withOrder(6);

        this.halls = Arrays.asList(mainHall, auditorium, eshkol1, eshkol3, games, specialEvents);

        Floor floor1 = new Floor(1).withName("מפלס תחתון וקומת ביניים").withImageResource(R.raw.cami_floor1).withMarkerHeight(10);
        Floor floor2 = new Floor(2).withName("מפלס עליון").withImageResource(R.raw.cami_floor2).withMarkerHeight(13);

        this.map = new ConventionMap()
                .withFloors(Arrays.asList(floor1, floor2))
                .withLocations(
                        CollectionUtils.flattenList(
                                inFloor(floor1,
                                        new MapLocation()
                                                .withPlace(eshkol3)
                                                .withMarkerResource(R.raw.eshkol3_marker)
                                                .withSelectedMarkerResource(R.raw.eshkol3_marker)
                                                .withX(53)
                                                .withY(87),
		                                new MapLocation()
		                                        .withPlace(new Place().withName("שירותים"))
		                                        .withMarkerResource(R.raw.toilet_marker)
		                                        .withSelectedMarkerResource(R.raw.toilet_marker)
		                                        .withX(89)
		                                        .withY(50),
                                        new MapLocation()
                                                .withPlace(new Place().withName("החתמות"))
                                                .withMarkerResource(R.raw.signatures_marker)
                                                .withSelectedMarkerResource(R.raw.signatures_marker)
                                                .withX(80)
                                                .withY(53),
                                        new MapLocation()
                                                .withPlace(new Place().withName("יד שניה"))
                                                .withMarkerResource(R.raw.second_hand_marker)
                                                .withSelectedMarkerResource(R.raw.second_hand_marker)
                                                .withX(56)
                                                .withY(53),
		                                new MapLocation()
		                                        .withPlace(new Place().withName("מודיעין"))
                                                .withMarkerResource(R.raw.information_marker)
                                                .withSelectedMarkerResource(R.raw.information_marker)
                                                .withX(41)
                                                .withY(31),
		                                new MapLocation()
				                                .withPlace(new Place().withName("שירותים"))
				                                .withMarkerResource(R.raw.toilet_marker)
				                                .withSelectedMarkerResource(R.raw.toilet_marker)
				                                .withX(49)
				                                .withY(6),
		                                new MapLocation()
				                                .withPlace(auditorium)
				                                .withMarkerResource(R.raw.schwartz_marker)
				                                .withSelectedMarkerResource(R.raw.schwartz_marker)
				                                .withX(30)
				                                .withY(43),
		                                new MapLocation()
				                                .withPlace(eshkol1)
				                                .withMarkerResource(R.raw.eshkol1_marker)
				                                .withSelectedMarkerResource(R.raw.eshkol1_marker)
				                                .withX(24)
				                                .withY(47),
		                                new MapLocation()
				                                .withPlace(new Place().withName("שירותים"))
				                                .withMarkerResource(R.raw.toilet_marker)
				                                .withSelectedMarkerResource(R.raw.toilet_marker)
				                                .withX(12)
				                                .withY(38)),
		                        inFloor(floor2,
				                        // Keep this location before storage because otherwise when
				                        // storage is selected, it's displayed behind this location
				                        new MapLocation()
						                        .withPlace(new Place().withName("שיפוט קוספליי"))
						                        .withMarkerResource(R.raw.cosplay_judgement_marker)
						                        .withSelectedMarkerResource(R.raw.cosplay_judgement_marker)
						                        .withX(85)
						                        .withY(75),
				                        new MapLocation()
						                        .withPlace(new Place().withName("שמירת חפצים"))
						                        .withMarkerResource(R.raw.storage_marker)
						                        .withSelectedMarkerResource(R.raw.storage_marker)
						                        .withX(92)
						                        .withY(67),
				                        new MapLocation()
						                        .withPlace(new Place().withName("משחקיה"))
						                        .withMarkerResource(R.raw.games_marker)
						                        .withSelectedMarkerResource(R.raw.games_marker)
						                        .withX(48)
						                        .withY(79),
				                        new MapLocation()
						                        .withPlace(mainHall)
						                        .withMarkerResource(R.raw.main_hall_marker)
						                        .withSelectedMarkerResource(R.raw.main_hall_marker)
						                        .withX(58)
						                        .withY(57),
				                        new MapLocation()
						                        .withPlace(new Place().withName("כניסה פלוס"))
						                        .withMarkerResource(R.raw.entrance_plus_marker)
						                        .withSelectedMarkerResource(R.raw.entrance_plus_marker)
						                        .withX(65)
						                        .withY(37),
				                        new MapLocation()
						                        .withPlace(new Place().withName("תיקון קוספליי"))
						                        .withMarkerResource(R.raw.cosplay_fixes_marker)
						                        .withSelectedMarkerResource(R.raw.cosplay_fixes_marker)
						                        .withX(59)
						                        .withY(24),
				                        new MapLocation()
						                        .withPlace(new Place().withName("פינת צילום"))
						                        .withMarkerResource(R.raw.photoshoot_corner_marker)
						                        .withSelectedMarkerResource(R.raw.photoshoot_corner_marker)
						                        .withX(52)
						                        .withY(18),
				                        new MapLocation()
						                        .withPlace(new Place().withName("שירותים"))
						                        .withMarkerResource(R.raw.toilet_marker)
						                        .withSelectedMarkerResource(R.raw.toilet_marker)
						                        .withX(58)
						                        .withY(11),
				                        new MapLocation()
						                        .withPlace(new Place().withName("ווידוא ווקאון"))
						                        .withMarkerResource(R.raw.walkon_marker)
						                        .withSelectedMarkerResource(R.raw.walkon_marker)
						                        .withX(25)
						                        .withY(63),
				                        new MapLocation()
						                        .withPlace(new Place().withName("שירותים"))
						                        .withMarkerResource(R.raw.toilet_marker)
						                        .withSelectedMarkerResource(R.raw.toilet_marker)
						                        .withX(8)
						                        .withY(61))
                        )
                );

        updates = new ArrayList<>();
    }

    public Calendar getDate() {
        return date;
    }

    public void setEvents(List<ConventionEvent> events) {
        eventLockObject.writeLock().lock();
        try {
            this.events = events;
	        updateUserInputFromEvents();
        } finally {
            eventLockObject.writeLock().unlock();
        }
	}

	public List<ConventionEvent> getEvents() {
        eventLockObject.readLock().lock();
        try {
            return events;
        } finally {
            eventLockObject.readLock().unlock();
        }
    }

    public boolean hasFavorites() {
        List<ConventionEvent> events = getEvents();
        for (ConventionEvent event : events) {
	        if (event.isAttending()) {
		        return true;
	        }
        }

        return false;
    }

	public Map<String, ConventionEvent.UserInput> getUserInput() {
		return userInput;
	}

	public ConventionEvent.UserInput getEventUserInput(String eventId) {
		return userInput.get(eventId);
	}

	public void setUserInput(Map<String, ConventionEvent.UserInput> userInput) {
		this.userInput = userInput;
		updateUserInputFromEvents();
	}

	private void updateUserInputFromEvents() {
		// Add user input for new events
		for (ConventionEvent event : events) {
			if (!userInput.containsKey(event.getId())) {
				userInput.put(event.getId(), new ConventionEvent.UserInput());
			}
		}
		// TODO remove user input for deleted events?
	}

	public List<Hall> getHalls() {
        return halls;
    }

	public Hall findHallByName(String name) {
		for (Hall hall : getHalls()) {
			if (hall.getName().compareToIgnoreCase(name) == 0) {
				return hall;
			}
		}

		return null;
	}

    public ConventionMap getMap() {
        return map;
    }

    public ConventionEvent findEventById(String eventId) {
        for (ConventionEvent event : getEvents()) {
            if (eventId.equals(event.getId())) {
                return event;
            }
        }

        return null;
    }

    public ArrayList<ConventionEvent> findEventsByHall(final String hallName) {
        return CollectionUtils.filter(
                getEvents(),
                new CollectionUtils.Predicate<ConventionEvent>() {
                    @Override
                    public boolean where(ConventionEvent event) {
                        return hallName.equals(event.getHall().getName());
                    }
                },
                new ArrayList<ConventionEvent>()
        );
    }

    public void setUpdates(List<Update> updates) {
        this.updates = updates;
    }

    public List<Update> getUpdates() {
        return updates;
    }

    public String getFeedbackRecipient() {
        return feedbackRecipient;
    }

    private List<MapLocation> inFloor(Floor floor, MapLocation... locations) {
        for (MapLocation location : locations) {
            location.setFloor(floor);
        }
        return Arrays.asList(locations);
    }
}

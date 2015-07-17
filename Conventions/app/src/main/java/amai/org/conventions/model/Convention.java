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

        Hall auditorium = new Hall().withName("אודיטוריום אוסישקין").withOrder(1);
        Hall contentRoom = new Hall().withName("חדר אירועי תוכן").withOrder(2);
        Hall oranim1 = new Hall().withName("אורנים 1").withOrder(3);
        Hall oranim2 = new Hall().withName("אורנים 2").withOrder(4);
        Hall oranim3 = new Hall().withName("אורנים 3").withOrder(5);

        this.halls = Arrays.asList(auditorium, contentRoom, oranim1, oranim2, oranim3);

        Floor floor1 = new Floor(1).withName("מפלס תחתון - כניסה").withImageResource(R.raw.floor1).withMarkerWidth(11);
        Floor floor2 = new Floor(2).withName("מפלס עליון - אולם ראשי").withImageResource(R.raw.floor2).withMarkerWidth(13);

        this.map = new ConventionMap()
                .withFloors(Arrays.asList(floor1, floor2))
                .withLocations(
                        CollectionUtils.flattenList(
                                inFloor(floor1,
                                        new MapLocation()
                                                .withHall(oranim1)
                                                .withMarkerResource(R.raw.oranim1_marker)
                                                .withSelectedMarkerResource(R.raw.oranim1_marker_selected)
                                                .withX(49)
                                                .withY(75),
                                        new MapLocation()
                                                .withHall(oranim2)
                                                .withMarkerResource(R.raw.oranim2_marker)
                                                .withSelectedMarkerResource(R.raw.oranim2_marker_selected)
                                                .withX(59)
                                                .withY(70),
                                        new MapLocation()
                                                .withHall(oranim3)
                                                .withMarkerResource(R.raw.oranim3_marker)
                                                .withSelectedMarkerResource(R.raw.oranim3_marker_selected)
                                                .withX(69)
                                                .withY(64)),
                                inFloor(floor2,
                                        new MapLocation()
                                                .withHall(auditorium)
                                                .withMarkerResource(R.raw.main_hall_marker)
                                                .withSelectedMarkerResource(R.raw.main_hall_marker_selected)
                                                .withX(53)
                                                .withY(51),
                                        new MapLocation()
                                                .withHall(contentRoom)
                                                .withMarkerResource(R.raw.content_room_marker)
                                                .withSelectedMarkerResource(R.raw.content_room_marker_selected)
                                                .withX(86)
                                                .withY(70))
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

    public boolean doesHaveFavorites() {
        List<ConventionEvent> events = getEvents();
        boolean hasFavorites = false;
        for (ConventionEvent event : events) {
            hasFavorites |= event.isAttending();
        }

        return hasFavorites;
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

package amai.org.conventions.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import amai.org.conventions.R;
import amai.org.conventions.utils.CollectionUtils;
import amai.org.conventions.utils.ConventionStorage;
import amai.org.conventions.utils.Dates;

public class Convention implements Serializable {

    private static Convention convention = new Convention();

    private List<Hall> halls;
    private List<ConventionEvent> events;
    private List<Update> updates;
	private Map<String, ConventionEvent.UserInput> userInput;
	private Feedback feedback;
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
	    feedback = new Feedback().withQuestions(
			    new FeedbackQuestion(FeedbackQuestion.QUESTION_ID_AGE, FeedbackQuestion.AnswerType.AGE),
			    new FeedbackQuestion(FeedbackQuestion.QUESTION_ID_LIKED, FeedbackQuestion.AnswerType.TEXT),
			    new FeedbackQuestion(FeedbackQuestion.QUESTION_ID_DISLIKED, FeedbackQuestion.AnswerType.TEXT),
			    new FeedbackQuestion(FeedbackQuestion.QUESTION_ID_ADDITIONAL_INFO, FeedbackQuestion.AnswerType.TEXT)
	    );

        this.date = Calendar.getInstance();
	    this.date.clear();
        this.date.set(2015, Calendar.AUGUST, 20);
        this.feedbackRecipient = "hq@cami.org.il";

	    Hall mainHall = new Hall().withName("אולם ראשי").withOrder(1);
        Hall auditorium = new Hall().withName("אודיטוריום שוורץ").withOrder(2);
        Hall eshkol1 = new Hall().withName("אשכול 1").withOrder(3);
        Hall eshkol2 = new Hall().withName("אשכול 2").withOrder(4);
        Hall games = new Hall().withName("משחקייה").withOrder(5);
        Hall specialEvents = new Hall().withName("אירועים מיוחדים").withOrder(6);

	    // This list can be modified
        this.halls = new ArrayList<>(Arrays.asList(mainHall, auditorium, eshkol1, eshkol2, games, specialEvents));

        Floor floor1 = new Floor(1).withName("מפלס תחתון וקומת ביניים").withImageResource(R.raw.cami_floor1).withMarkerHeight(13);
        Floor floor2 = new Floor(2).withName("מפלס עליון").withImageResource(R.raw.cami_floor2).withMarkerHeight(14);

        this.map = new ConventionMap()
                .withFloors(Arrays.asList(floor1, floor2))
                .withLocations(
                        CollectionUtils.flattenList(
                                inFloor(floor1,
                                        new MapLocation()
                                                .withPlace(eshkol2)
                                                .withMarkerResource(R.raw.eshkol2_marker)
                                                .withSelectedMarkerResource(R.raw.eshkol2_marker_selected)
                                                .withX(26)
                                                .withY(73),
		                                new MapLocation()
		                                        .withPlace(new Place().withName("שירותים"))
		                                        .withMarkerResource(R.raw.toilet_marker)
		                                        .withSelectedMarkerResource(R.raw.toilet_marker_selected)
		                                        .withX(90)
		                                        .withY(60),
                                        new MapLocation()
                                                .withPlace(new Place().withName("החתמות"))
                                                .withMarkerResource(R.raw.signatures_marker)
                                                .withSelectedMarkerResource(R.raw.signatures_marker_selected)
                                                .withX(80)
                                                .withY(63),
//                                        new MapLocation()
//                                                .withPlace(new Place().withName("יד שניה"))
//                                                .withMarkerResource(R.raw.second_hand_marker)
//                                                .withSelectedMarkerResource(R.raw.second_hand_marker_selected)
//                                                .withX(55)
//                                                .withY(64),
		                                new MapLocation()
		                                        .withPlace(new Place().withName("מודיעין"))
                                                .withMarkerResource(R.raw.information_marker)
                                                .withSelectedMarkerResource(R.raw.information_marker_selected)
                                                .withX(41)
                                                .withY(37),
		                                new MapLocation()
				                                .withPlace(new Place().withName("שירותים"))
				                                .withMarkerResource(R.raw.toilet_marker)
				                                .withSelectedMarkerResource(R.raw.toilet_marker_selected)
				                                .withX(49)
				                                .withY(7),
		                                new MapLocation()
				                                .withPlace(auditorium)
				                                .withMarkerResource(R.raw.schwartz_marker)
				                                .withSelectedMarkerResource(R.raw.schwartz_marker_selected)
				                                .withX(30)
				                                .withY(49),
		                                new MapLocation()
				                                .withPlace(eshkol1)
				                                .withMarkerResource(R.raw.eshkol1_marker)
				                                .withSelectedMarkerResource(R.raw.eshkol1_marker_selected)
				                                .withX(26)
				                                .withY(54),
		                                new MapLocation()
				                                .withPlace(new Place().withName("שירותים"))
				                                .withMarkerResource(R.raw.toilet_marker)
				                                .withSelectedMarkerResource(R.raw.toilet_marker_selected)
				                                .withX(12)
				                                .withY(46)),
		                        inFloor(floor2,
				                        // Keep this location before storage because otherwise when
				                        // storage is selected, it's displayed behind this location
				                        new MapLocation()
						                        .withPlace(new Place().withName("שיפוט קוספליי"))
						                        .withMarkerResource(R.raw.cosplay_judgement_marker)
						                        .withSelectedMarkerResource(R.raw.cosplay_judgement_marker_selected)
						                        .withX(83)
						                        .withY(75),
				                        new MapLocation()
						                        .withPlace(new Place().withName("שמירת חפצים"))
						                        .withMarkerResource(R.raw.storage_marker)
						                        .withSelectedMarkerResource(R.raw.storage_marker_selected)
						                        .withX(91)
						                        .withY(67),
				                        new MapLocation()
						                        .withPlace(games)
						                        .withMarkerResource(R.raw.games_marker)
						                        .withSelectedMarkerResource(R.raw.games_marker_selected)
						                        .withX(48)
						                        .withY(79),
				                        new MapLocation()
						                        .withPlace(mainHall)
						                        .withMarkerResource(R.raw.main_hall_marker)
						                        .withSelectedMarkerResource(R.raw.main_hall_marker_selected)
						                        .withX(58)
						                        .withY(57),
				                        new MapLocation()
						                        .withPlace(new Place().withName("כניסה פלוס"))
						                        .withMarkerResource(R.raw.entrance_plus_marker)
						                        .withSelectedMarkerResource(R.raw.entrance_plus_marker_selected)
						                        .withX(65)
						                        .withY(37),
				                        new MapLocation()
						                        .withPlace(new Place().withName("פינת צילום"))
						                        .withMarkerResource(R.raw.photoshoot_corner_marker)
						                        .withSelectedMarkerResource(R.raw.photoshoot_corner_marker_selected)
						                        .withX(55)
						                        .withY(35),
				                        new MapLocation()
						                        .withPlace(new Place().withName("תיקון קוספליי"))
						                        .withMarkerResource(R.raw.cosplay_fixes_marker)
						                        .withSelectedMarkerResource(R.raw.cosplay_fixes_marker_selected)
						                        .withX(58)
						                        .withY(22),
				                        new MapLocation()
						                        .withPlace(new Place().withName("שירותים"))
						                        .withMarkerResource(R.raw.toilet_marker)
						                        .withSelectedMarkerResource(R.raw.toilet_marker_selected)
						                        .withX(58)
						                        .withY(11),
				                        new MapLocation()
						                        .withPlace(new Place().withName("ווידוא ווקאון"))
						                        .withMarkerResource(R.raw.walkon_marker)
						                        .withSelectedMarkerResource(R.raw.walkon_marker_selected)
						                        .withX(24)
						                        .withY(63),
				                        new MapLocation()
						                        .withPlace(new Place().withName("שירותים"))
						                        .withMarkerResource(R.raw.toilet_marker)
						                        .withSelectedMarkerResource(R.raw.toilet_marker_selected)
						                        .withX(7)
						                        .withY(61))
                        )
                );

        updates = new ArrayList<>();
    }

    public Calendar getDate() {
        return date;
    }

	public Feedback getFeedback() {
		return feedback;
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

	public boolean canFillFeedback() {
		// Check if the convention started at least 2 hours ago
		Calendar minimumTimeOfFillingFeedback = Calendar.getInstance();
		minimumTimeOfFillingFeedback.setTime(getFirstEventStartTime());
		minimumTimeOfFillingFeedback.add(Calendar.MINUTE, 120);

		return minimumTimeOfFillingFeedback.getTime().before(Dates.now());
	}

	private Date getFirstEventStartTime() {
		Date minTime = null;
		for (ConventionEvent event : getEvents()) {
			if (minTime == null || minTime.after(event.getStartTime())) {
				minTime = event.getStartTime();
			}
		}
		return minTime;
	}

	public boolean hasEnded() {
		return Dates.now().after(getLastEventEndTime());
	}

	private Date getLastEventEndTime() {
		Date maxTime = null;
		for (ConventionEvent event : getEvents()) {
			if (maxTime == null || maxTime.before(event.getEndTime())) {
				maxTime = event.getEndTime();
			}
		}
		return maxTime;
	}

    private List<MapLocation> inFloor(Floor floor, MapLocation... locations) {
        for (MapLocation location : locations) {
            location.setFloor(floor);
        }
        return Arrays.asList(locations);
    }

	public Hall addHall(String name) {
		Hall hall = new Hall().withName(name).withOrder(getHighestHallOrder() + 1);
		halls.add(hall);
		return hall;
	}

	public List<EventType> getEventTypes() {
		HashSet<EventType> eventTypes = new HashSet<>();
		for (ConventionEvent event : events) {
			eventTypes.add(event.getType());
		}

		List<EventType> eventTypeList = new ArrayList<>(eventTypes);
		Collections.sort(eventTypeList, new Comparator<EventType>() {
			@Override
			public int compare(EventType lhs, EventType rhs) {
				return lhs.getDescription().compareTo(rhs.getDescription());
			}
		});
		return eventTypeList;
	}

	private int getHighestHallOrder() {
		int maxHallOrder = -1;
		for (Hall hall : getHalls()) {
			maxHallOrder = Math.max(maxHallOrder, hall.getOrder());
		}

		return maxHallOrder;
	}
}

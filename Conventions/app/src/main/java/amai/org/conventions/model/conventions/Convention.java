package amai.org.conventions.model.conventions;

import android.content.Context;

import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import amai.org.conventions.map.AggregatedEventTypes;
import amai.org.conventions.model.ConventionEvent;
import amai.org.conventions.model.ConventionMap;
import amai.org.conventions.model.EventToImageResourceIdMapper;
import amai.org.conventions.model.EventType;
import amai.org.conventions.model.Feedback;
import amai.org.conventions.model.FeedbackQuestion;
import amai.org.conventions.model.Floor;
import amai.org.conventions.model.Hall;
import amai.org.conventions.model.MapLocation;
import amai.org.conventions.model.Place;
import amai.org.conventions.model.Stand;
import amai.org.conventions.model.StandsArea;
import amai.org.conventions.model.Update;
import amai.org.conventions.networking.ModelParser;
import amai.org.conventions.utils.CollectionUtils;
import amai.org.conventions.utils.ConventionStorage;
import amai.org.conventions.utils.Dates;
import amai.org.conventions.utils.Objects;

public abstract class Convention implements Serializable {

	private static Convention convention = new Cami2016Convention();

	private List<Hall> halls;
	private List<ConventionEvent> events;
	private List<Update> updates;
	private Map<String, Update> updatesById;
	private Map<String, ConventionEvent.UserInput> userInput;
	private Feedback feedback;
	private String feedbackRecipient;

	private ConventionMap map;
	private Calendar startDate;
	private Calendar endDate;
	private String id;
	private String displayName;
	private URL modelURL;
	private String facebookFeedPath;
	private EventToImageResourceIdMapper imageMapper;

	private double longitude;
	private double latitude;

	private final ReentrantReadWriteLock eventLockObject = new ReentrantReadWriteLock();
	private ConventionStorage conventionStorage;


	public static Convention getInstance() {
		return convention;
	}

	public ConventionStorage getStorage() {
		return conventionStorage;
	}

	public URL getModelURL() {
		return modelURL;
	}

	public String getFacebookFeedPath() {
		return facebookFeedPath;
	}

	public double getLongitude() {
		return longitude;
	}

	public double getLatitude() {
		return latitude;
	}

	protected Convention() {
		this.userInput = new LinkedHashMap<>();
		updates = new ArrayList<>();
		updatesById = new HashMap<>();

		initFeedback();
	}

	private void initFeedback() {
		feedback = new Feedback().withQuestions(
				new FeedbackQuestion(FeedbackQuestion.QUESTION_ID_AGE, FeedbackQuestion.AnswerType.MULTIPLE_ANSWERS),
				new FeedbackQuestion(FeedbackQuestion.QUESTION_ID_LIKED, FeedbackQuestion.AnswerType.SMILEY_3_POINTS),
				new FeedbackQuestion(FeedbackQuestion.QUESTION_ID_MAP_SIGNS, FeedbackQuestion.AnswerType.MULTIPLE_ANSWERS),
				new FeedbackQuestion(FeedbackQuestion.QUESTION_ID_CONFLICTING_EVENTS, FeedbackQuestion.AnswerType.MULTIPLE_ANSWERS_RADIO),
				new FeedbackQuestion(FeedbackQuestion.QUESTION_ID_IMPROVEMENT, FeedbackQuestion.AnswerType.TEXT)
		);
	}

	public void load(Context context) {
		this.conventionStorage = initStorage();
		this.startDate = initStartDate();
		this.endDate = initEndDate();
		this.id = initID();
		this.displayName = initDisplayName();
		this.feedbackRecipient = initFeedbackRecipient();
		this.modelURL = initModelURL();
		this.facebookFeedPath = initFacebookFeedPath();
		// This list can be modified
		this.halls = new ArrayList<>(initHalls());
		this.map = initMap();
		this.longitude = initLongitude();
		this.latitude = initLatitude();
		this.imageMapper = initImageMapper();

		getStorage().initFromFile(context);
	}

	protected abstract ConventionStorage initStorage();

	protected abstract Calendar initStartDate();

	protected abstract Calendar initEndDate();

	protected abstract String initID();

	protected abstract String initDisplayName();

	protected abstract String initFeedbackRecipient();

	protected abstract URL initModelURL();

	protected abstract String initFacebookFeedPath();

	protected abstract List<Hall> initHalls();

	protected abstract ConventionMap initMap();

	protected abstract double initLongitude();

	protected abstract double initLatitude();

	protected abstract EventToImageResourceIdMapper initImageMapper();

	public Calendar getStartDate() {
		return startDate;
	}

	public Calendar getEndDate() {
		return endDate;
	}

	public int getLengthInDays() {
		return (int) ((endDate.getTime().getTime() - startDate.getTime().getTime()) / Dates.MILLISECONDS_IN_DAY) + 1;
	}

	public String getId() {
		return id;
	}

	public String getDisplayName() {
		return displayName;
	}

	public Feedback getFeedback() {
		return feedback;
	}

	public EventToImageResourceIdMapper getImageMapper() {
		return imageMapper;
	}

	/**
	 * Modify properties of the event as needed for special event
	 *
	 * @param event
	 * @return whether the event was modified
	 */
	public boolean handleSpecialEvent(ConventionEvent event) {
		// By default, there are no special events
		return false;
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
						return event.getHall() != null && Objects.equals(hallName, event.getHall().getName());
					}
				},
				new ArrayList<ConventionEvent>()
		);
	}

	public void setUpdates(List<Update> updates) {
		this.updates = updates;
		refreshUpdatesMap();
	}

	public List<Update> getUpdates() {
		return updates;
	}

	private void refreshUpdatesMap() {
		updatesById.clear();
		for (Update update : updates) {
			updatesById.put(update.getId(), update);
		}
	}

	public List<Update> addUpdates(List<Update> newUpdates) {
		for (Update update : newUpdates) {
			if (updatesById.containsKey(update.getId())) {
				// Remove the existing update
				for (Iterator<Update> iter = updates.iterator(); iter.hasNext(); ) {
					Update currUpdate = iter.next();
					if (currUpdate.getId().equals(update.getId())) {
						iter.remove();
					}
				}
			}
			updates.add(update);
			updatesById.put(update.getId(), update);
		}
		return updates;
	}

	public Update getUpdate(String id) {
		return updatesById.get(id);
	}

	public void clearNewFlagFromAllUpdates() {
		for (Update update : updates) {
			update.setIsNew(false);
		}
	}

	public String getFeedbackRecipient() {
		return feedbackRecipient;
	}

	public boolean canFillFeedback() {
		// Check if the convention started at least 2 hours ago
		Calendar minimumTimeOfFillingFeedback = Calendar.getInstance();
		Date firstEventStartTime = getFirstEventStartTime();
		if (firstEventStartTime == null) {
			// Problem reading events
			return false;
		}
		minimumTimeOfFillingFeedback.setTime(firstEventStartTime);
		minimumTimeOfFillingFeedback.add(Calendar.MINUTE, 120);

		return minimumTimeOfFillingFeedback.getTime().before(Dates.now());
	}

	public boolean isFeedbackSendingTimeOver() {
		// Only allow to send feedback for 2 weeks after the convention is over
		Calendar lastFeedbackSendTime = Calendar.getInstance();
		lastFeedbackSendTime.setTime(this.endDate.getTime());
		lastFeedbackSendTime.add(Calendar.DATE, 14);
		return lastFeedbackSendTime.getTime().before(Dates.now());
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
		Date lastEventEndTime = getLastEventEndTime();
		if (lastEventEndTime == null) {
			// Problem reading events
			return false;
		}
		return Dates.now().after(lastEventEndTime);
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

	protected List<MapLocation> inFloor(Floor floor, MapLocation... locations) {
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
		if (events != null) {
			for (ConventionEvent event : events) {
				eventTypes.add(event.getType());
			}
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

	public List<String> getAggregatedSearchCategories() {
		List<SearchCategory> categories = new LinkedList<>();
		AggregatedEventTypes aggregatedEventTypes = new AggregatedEventTypes();
		if (events != null) {
			for (ConventionEvent event : events) {
				// Count the number of occurrences of each event per type
				final String aggregatedEventType = aggregatedEventTypes.get(event.getType());
				SearchCategory category = CollectionUtils.findFirst(categories, new CollectionUtils.Predicate<SearchCategory>() {
					@Override
					public boolean where(SearchCategory item) {
						return item.getSearchCategory().equals(aggregatedEventType);
					}
				});

				if (category != null) {
					category.increase();
				} else {
					categories.add(new SearchCategory(aggregatedEventType));
				}
			}
		}

		// Ensure the result is sorted by occurrences, so the most popular event type is first
		Collections.sort(categories, new Comparator<SearchCategory>() {
			@Override
			public int compare(SearchCategory category1, SearchCategory category2) {
				return category2.getCount() - category1.getCount();
			}
		});

		// Flatten the list again now that it's sorted
		return CollectionUtils.map(categories, new CollectionUtils.Mapper<SearchCategory, String>() {
			@Override
			public String map(SearchCategory item) {
				return item.getSearchCategory();
			}
		});
	}

	private int getHighestHallOrder() {
		int maxHallOrder = -1;
		for (Hall hall : getHalls()) {
			maxHallOrder = Math.max(maxHallOrder, hall.getOrder());
		}

		return maxHallOrder;
	}

	public Date getNewestUpdateTime() {
		Date newest = null;

		for (Update update : getUpdates()) {
			if (newest == null || update.getDate().after(newest)) {
				newest = update.getDate();
			}
		}
		return newest;
	}

	public boolean conflictsWithOtherFavoriteEvent(ConventionEvent event) {
		for (ConventionEvent otherEvent : Convention.getInstance().getEvents()) {
			if (!otherEvent.getId().equals(event.getId()) && otherEvent.isAttending()) {
				boolean first = event.getStartTime().before(otherEvent.getStartTime());
				if ((first && event.getEndTime().after(otherEvent.getStartTime())) ||
						((!first) && otherEvent.getEndTime().after(event.getStartTime()))) {
					return true;
				}
			}
		}
		return false;
	}

	public List<ConventionEvent> getFavoriteConflictingEvents(final ConventionEvent event) {
		return CollectionUtils.filter(getEvents(), new CollectionUtils.Predicate<ConventionEvent>() {
			@Override
			public boolean where(ConventionEvent otherEvent) {
				return !otherEvent.getId().equals(event.getId()) && otherEvent.isAttending() && isConflicting(event, otherEvent);
			}
		});
	}

	private boolean isConflicting(ConventionEvent firstEvent, ConventionEvent secondEvent) {
		// An event conflicts with another event when, in chronological order,
		// the first event ends after the second event starts.
		boolean first = firstEvent.getStartTime().before(secondEvent.getStartTime());
		return (first && firstEvent.getEndTime().after(secondEvent.getStartTime())) ||
				((!first) && secondEvent.getEndTime().after(firstEvent.getStartTime()));
	}

	public StandsArea findStandsArea(int id) {
		for (MapLocation location : map.getLocations()) {
			List<? extends Place> places = location.getPlaces();
			if (places == null) {
				continue;
			}
			for (Place place : places) {
				if (place instanceof StandsArea && ((StandsArea) place).getId() == id) {
					return (StandsArea) place;
				}
			}
		}

		return null;
	}

	public boolean hasStands() {
		for (MapLocation location : map.getLocations()) {
			List<? extends Place> places = location.getPlaces();
			if (places == null) {
				continue;
			}
			for (Place place : places) {
				if (place instanceof StandsArea &&
						((StandsArea) place).getStands().size() > 0) {
					return true;
				}
			}
		}
		return false;
	}

	public List<Stand> getStands() {
		Set<Integer> foundStandAreas = new HashSet<>();
		List<Stand> stands = new LinkedList<>();
		for (MapLocation location : map.getLocations()) {
			List<? extends Place> places = location.getPlaces();
			for (Place place : places) {
				if (place instanceof StandsArea) {
					StandsArea area = (StandsArea) place;
					if (!foundStandAreas.contains(area.getId())) {
						stands.addAll(area.getStands());
						foundStandAreas.add(area.getId());
					}
				}
			}
		}
		return stands;
	}

	public abstract ModelParser getModelParser();

	private static class SearchCategory {
		private String searchCategory;
		private int count;

		public SearchCategory(String searchCategory) {
			this.searchCategory = searchCategory;
			this.count = 1;
		}

		public String getSearchCategory() {
			return searchCategory;
		}

		public void increase() {
			count++;
		}

		public int getCount() {
			return count;
		}
	}
}

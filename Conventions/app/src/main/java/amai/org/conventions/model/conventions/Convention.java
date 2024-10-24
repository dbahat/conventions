package amai.org.conventions.model.conventions;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;

import amai.org.conventions.R;
import amai.org.conventions.ThemeAttributes;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;

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

import amai.org.conventions.BuildConfig;
import amai.org.conventions.events.SearchCategory;
import amai.org.conventions.feedback.SurveySender;
import amai.org.conventions.feedback.forms.ConventionFeedbackFormSender;
import amai.org.conventions.feedback.forms.EventFeedbackForm;
import amai.org.conventions.feedback.forms.EventFeedbackFormSender;
import amai.org.conventions.feedback.forms.FeedbackForm;
import amai.org.conventions.map.AggregatedEventTypes;
import amai.org.conventions.model.ConventionEvent;
import amai.org.conventions.model.ConventionMap;
import amai.org.conventions.model.FeedbackQuestion;
import amai.org.conventions.model.Floor;
import amai.org.conventions.model.Halls;
import amai.org.conventions.model.ImageIdToImageResourceMapper;
import amai.org.conventions.model.MapLocation;
import amai.org.conventions.model.Place;
import amai.org.conventions.model.SearchFilter;
import amai.org.conventions.model.SpecialEventsProcessor;
import amai.org.conventions.model.Stand;
import amai.org.conventions.model.StandsArea;
import amai.org.conventions.model.Survey;
import amai.org.conventions.model.Update;
import amai.org.conventions.networking.ModelParser;
import amai.org.conventions.networking.SurveyDataRetriever;
import amai.org.conventions.utils.CollectionUtils;
import amai.org.conventions.utils.ConventionStorage;
import amai.org.conventions.utils.Dates;
import amai.org.conventions.utils.Objects;

public abstract class Convention implements Serializable {

	private static Convention convention = new Animatsuri2024Convention();
	public static final int NO_COLOR = Color.TRANSPARENT; // Assuming we will never get this from the server...

	// Currently supporting conventions of up to 5 days (UI restriction, since the programme is set
	// to fit up to 5 days in its tab bar).
	private static final int MAX_CONVENTION_LENGTH_IN_DAYS = 5;

	private Halls halls;
	private List<ConventionEvent> events;
	private List<Update> updates;
	private Map<String, Update> updatesById;
	private Map<String, ConventionEvent.UserInput> userInput;
	private Calendar[] eventDates;
	private List<ConventionEvent.EventLocationType> eventLocationTypes;
	private Survey feedback;
	private FeedbackForm conventionFeedbackForm;
	private EventFeedbackForm eventFeedbackForm;

	private ConventionMap map;
	protected Calendar startDate;
	private Calendar endDate;
	private String id;
	private String displayName;
	private URL modelURL;
	private URL updatesURL;
	private ImageIdToImageResourceMapper imageMapper;

	private double longitude;
	private double latitude;

	private final ReentrantReadWriteLock eventLockObject = new ReentrantReadWriteLock();
	private ConventionStorage conventionStorage;


	public static Convention getInstance() {
		return convention;
	}

	@VisibleForTesting
	public static void setConvention(Convention convention) {
		Convention.convention = convention;
	}


	public ConventionStorage getStorage() {
		return conventionStorage;
	}

	public URL getModelURL() {
		return modelURL;
	}

	public URL getUpdatesURL() {
		return updatesURL;
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
		feedback = new Survey().withQuestions(
				new FeedbackQuestion(FeedbackQuestion.QUESTION_ID_AGE, FeedbackQuestion.AnswerType.MULTIPLE_ANSWERS),
				new FeedbackQuestion(FeedbackQuestion.QUESTION_ID_LIKED, FeedbackQuestion.AnswerType.FIVE_STARS),
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
		this.conventionFeedbackForm = initConventionFeedbackForm();
		this.eventFeedbackForm = initEventFeedbackForm();
		this.modelURL = initModelURL();
		this.updatesURL = initUpdatesURL();
		this.halls = initHalls();
		this.map = initMap();
		if (this.map == null) {
			this.map = new ConventionMap();
		}
		this.longitude = initLongitude();
		this.latitude = initLatitude();
		this.imageMapper = initImageMapper();

		getStorage().initFromFile(context);

		if (getLengthInDays() > MAX_CONVENTION_LENGTH_IN_DAYS) {
			throw new RuntimeException("Conventions with over " + MAX_CONVENTION_LENGTH_IN_DAYS + " days are currently un-supported.");
		}

		if (BuildConfig.DEBUG) {
			if (!conventionFeedbackForm.canFillFeedback(feedback)) {
				throw new RuntimeException("Bad convention feedback form");
			}
		}
	}

	protected abstract ConventionStorage initStorage();

	protected abstract Calendar initStartDate();

	protected abstract Calendar initEndDate();

	protected abstract String initID();

	protected abstract String initDisplayName();

	protected abstract URL initUpdatesURL();

	protected abstract URL initModelURL();

	protected abstract Halls initHalls();

	protected abstract ConventionMap initMap();

	protected abstract double initLongitude();

	protected abstract double initLatitude();

	protected abstract ImageIdToImageResourceMapper initImageMapper();

	protected abstract EventFeedbackForm initEventFeedbackForm();

	protected abstract FeedbackForm initConventionFeedbackForm();

	public abstract String getGoogleSpreadsheetsApiKey();

	public Calendar getStartDate() {
		Calendar[] eventDates = getEventDates();
		if (eventDates != null && eventDates.length > 0) {
			return eventDates[0];
		}
		return startDate;
	}

	public Calendar getEndDate() {
		Calendar[] eventDates = getEventDates();
		if (eventDates != null && eventDates.length > 0) {
			return eventDates[eventDates.length - 1];
		}
		return endDate;
	}

	public int getLengthInDays() {
		return getEventDates().length;
	}

	public Calendar[] getEventDates() {
		return eventDates;
	}

	public List<ConventionEvent.EventLocationType> getEventLocationTypes() {
		return eventLocationTypes;
	}

	public String getId() {
		return id;
	}

	public String getDisplayName() {
		return displayName;
	}

	public Survey getFeedback() {
		return feedback;
	}

	public ImageIdToImageResourceMapper getImageMapper() {
		return imageMapper;
	}

	public SpecialEventsProcessor getSpecialEventsProcessor() {
		return new SpecialEventsProcessor();
	}

	/**
	 * @return the survey answers retriever associated with this question, or null of no such retriever was defined.
	 */
	@Nullable
	public SurveyDataRetriever.Answers createSurveyAnswersRetriever(FeedbackQuestion question) {
		return null;
	}

	public void setEvents(List<ConventionEvent> events) {
		eventLockObject.writeLock().lock();
		try {
			this.events = events;
			updateUserInputFromEvents();
			updateEventDates();
			updateEventLocationTypes();
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

	public ConventionEvent getEventById(final String id) {
		return CollectionUtils.findFirst(getEvents(), new CollectionUtils.Predicate<ConventionEvent>() {
			@Override
			public boolean where(ConventionEvent item) {
				return Objects.equals(item.getId(), id);
			}
		});
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
				userInput.put(event.getId(), createUserInputForEvent(event));
			} else {
				convertUserInputForEvent(userInput.get(event.getId()), event);
			}
		}
		// currently not removing outdated user input for deleted events
	}

	private void updateEventDates() {
		Set<Calendar> foundDates = new HashSet<>();
		for (ConventionEvent event : events) {
			Calendar eventStartTime = Dates.toCalendar(event.getStartTime());
			Calendar eventDate = Dates.createDate(eventStartTime.get(Calendar.YEAR), eventStartTime.get(Calendar.MONTH), eventStartTime.get(Calendar.DATE));
			foundDates.add(eventDate);
		}

		// We can't not show any dates. If there are no events, the day will be empty but at least it will be displayed.
		if (foundDates.isEmpty()) {
			foundDates.add(startDate);
		}

		eventDates = foundDates.toArray(new Calendar[]{});

		// Sort the found event dates
		Arrays.sort(eventDates);
	}

	private void updateEventLocationTypes() {
		Set<ConventionEvent.EventLocationType> locationTypes = new HashSet<>();
		for (ConventionEvent event : events) {
			if (this.getEventLocationTypes(event) != null) {
				locationTypes.addAll(this.getEventLocationTypes(event));
			}
		}
		eventLocationTypes = new ArrayList<>(locationTypes);
	}

	protected ConventionEvent.UserInput createUserInputForEvent(ConventionEvent event) {
		return new ConventionEvent.UserInput();
	}

	// "Upgrade" mechanism for user input
	public void convertUserInputForEvent(ConventionEvent.UserInput input, ConventionEvent event) {
	}

	public Halls getHalls() {
		return halls;
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

	public ConventionEvent findEventByServerId(int eventServerId) {
		for (ConventionEvent event : getEvents()) {
			if (event.getServerId() == eventServerId) {
				return event;
			}
		}

		return null;
	}

	public List<ConventionEvent> findEventsByHall(final String hallName) {
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

	public List<Update> addUpdates(List<Update> newUpdates, boolean removeOldUpdates) {
		Set<String> newUpdateIDs = new HashSet<>();
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
			if (removeOldUpdates) {
				newUpdateIDs.add(update.getId());
			}
		}
		if (removeOldUpdates) {
			for (Iterator<Update> iter = updates.iterator(); iter.hasNext(); ) {
				Update update = iter.next();
				if (!newUpdateIDs.contains(update.getId())) {
					iter.remove();
					updatesById.remove(update.getId());
				}
			}
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

	public Calendar getLastFeedbackSendTime() {
		// Only allow to send feedback for a week after the convention is over
		Calendar lastFeedbackSendTime = Calendar.getInstance();
		lastFeedbackSendTime.setTime(this.getEndDate().getTime());
		lastFeedbackSendTime.add(Calendar.DATE, 7);
		return lastFeedbackSendTime;
	}

	public boolean isFeedbackSendingTimeOver() {
		Calendar lastFeedbackSendTime = getLastFeedbackSendTime();
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

	public boolean hasStarted() {
		// We consider the convention 'started' if we are now during or after the date it starts
		return Dates.now().after(getStartDate().getTime());
	}

	public boolean hasEnded() {
		Date lastEventEndTime = getLastEventEndTime();
		if (lastEventEndTime == null) {
			// Problem reading events
			return false;
		}
		return Dates.now().after(lastEventEndTime);
	}

	public boolean haveAllEventsStarted() {
		Date lastEventStartTime = getLastEventStartTime();
		if (lastEventStartTime == null) {
			// Problem reading events
			return false;
		}
		return Dates.now().after(lastEventStartTime);
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

	private Date getLastEventStartTime() {
		Date maxTime = null;
		for (ConventionEvent event : getEvents()) {
			if (maxTime == null || maxTime.before(event.getStartTime())) {
				maxTime = event.getStartTime();
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

	public List<SearchCategory> getEventTypesSearchCategories(Context context) {
		return getEventTypesSearchCategories(context, null);
	}

	private List<SearchCategory> getEventTypesSearchCategories(Context context, AggregatedEventTypes aggregatedEventTypes) {
		List<SearchCategory> categories = new LinkedList<>();
		if (events != null) {
			boolean useColorFromEventType = ThemeAttributes.getBoolean(context, R.attr.useEventTimeColorFromEventType);
			int defaultCategoryColor = Convention.NO_COLOR;
			if (!useColorFromEventType) {
				defaultCategoryColor = ThemeAttributes.getColor(context, R.attr.searchCategoriesColor);
			}

			for (ConventionEvent event : events) {
				// Count the number of occurrences of each event per type
				final String categoryName = aggregatedEventTypes == null ? event.getType().getDescription() : aggregatedEventTypes.getForEventType(event.getType());
				SearchCategory category = CollectionUtils.findFirst(categories, item -> item.getName().equals(categoryName));

				if (category != null) {
					category.increase();
				} else {
					int currCategoryColor = useColorFromEventType ? event.getBackgroundColor() : defaultCategoryColor;
					categories.add(new SearchCategory(categoryName, currCategoryColor));
				}
			}
		}

		// Ensure the result is sorted by occurrences, so the most popular event type is first
		Collections.sort(categories, (category1, category2) -> category2.getCount() - category1.getCount());

		return categories;
	}

	public List<SearchFilter> getEventTypesSearchFilters() {
		List<SearchFilter> filters = CollectionUtils.map(events, new CollectionUtils.Mapper<ConventionEvent, SearchFilter>() {
					@Override
			public SearchFilter map(ConventionEvent event) {
				return new SearchFilter().withName(event.getType().getDescription()).withType(SearchFilter.Type.EventType);
					}
				});

		return normalizeSearchFilters(filters);
	}

	public List<SearchFilter> getCategorySearchFilters() {
		List<SearchFilter> filters = CollectionUtils.map(events, new CollectionUtils.Mapper<ConventionEvent, SearchFilter>() {
			@Override
			public SearchFilter map(ConventionEvent event) {
				return new SearchFilter().withName(event.getCategory()).withType(SearchFilter.Type.Category);
			}
		});

		return normalizeSearchFilters(filters);
	}

	private List<SearchFilter> normalizeSearchFilters(List<SearchFilter> filters) {
		filters = CollectionUtils.filter(filters, new CollectionUtils.Predicate<SearchFilter>() {
			@Override
			public boolean where(SearchFilter item) {
				return item.getName() != null && !"".equals(item.getName().trim());
			}
		});

		Collections.sort(filters, new Comparator<SearchFilter>() {
			@Override
			public int compare(SearchFilter searchFilter, SearchFilter other) {
				return searchFilter.getName().compareTo(other.getName());
			}
		});

		return CollectionUtils.unique(filters, new CollectionUtils.EqualityPredicate<SearchFilter>() {
			@Override
			public boolean equals(SearchFilter lhs, SearchFilter rhs) {
				return lhs.getName().equals(rhs.getName());
			}
		});
	}

	public List<SearchFilter> getKeywordsSearchFilters() {
		Set<String> allTags = new HashSet<>();
		for (ConventionEvent event : events) {
			allTags.addAll(event.getTags());
		}

		List<SearchFilter> filters = CollectionUtils.map(new ArrayList<>(allTags), new CollectionUtils.Mapper<String, SearchFilter>() {
			@Override
			public SearchFilter map(String tag) {
				return new SearchFilter().withName(tag).withType(SearchFilter.Type.Tag);
			}
		});


		return normalizeSearchFilters(filters);
	}

	public List<SearchFilter> getEventLocationTypeFilters(Resources resources) {
		List<SearchFilter> filters = CollectionUtils.map(getEventLocationTypes(), new CollectionUtils.Mapper<ConventionEvent.EventLocationType, SearchFilter>() {
			@Override
			public SearchFilter map(ConventionEvent.EventLocationType item) {
				return new SearchFilter().withName(resources.getString(item.getDescriptionStringId())).withType(SearchFilter.Type.EventLocationType);
			}
		});

		Collections.sort(filters, new Comparator<SearchFilter>() {
			@Override
			public int compare(SearchFilter searchFilter, SearchFilter other) {
				return searchFilter.getName().compareTo(other.getName());
			}
		});

		return filters;
	}

	public List<SearchCategory> getAggregatedEventTypesSearchCategories(Context context) {
		return getEventTypesSearchCategories(context, new AggregatedEventTypes());
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

	public MapLocation findStandsAreaLocation(int id) {
		for (MapLocation location : map.getLocations()) {
			List<? extends Place> places = location.getPlaces();
			if (places == null) {
				continue;
			}
			for (Place place : places) {
				if (place instanceof StandsArea && ((StandsArea) place).getId() == id) {
					return location;
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

	public SurveySender getConventionFeedbackSender() {
		return new ConventionFeedbackFormSender(conventionFeedbackForm, this);
	}

	public SurveySender getEventFeedbackSender(ConventionEvent event) {
		return new EventFeedbackFormSender(eventFeedbackForm, this, event);
	}

	public SurveySender getEventVoteSender(ConventionEvent event) {
		return null;
	}

	public URL getAdditionalConventionFeedbackURL() {
		return null;
	}

	public URL getAdditionalEventFeedbackURL(ConventionEvent event) {
		return null;
	}

	public URL getEventViewURL(ConventionEvent event) {
		return null;
	}

	public List<ConventionEvent.EventLocationType> getEventLocationTypes(ConventionEvent event) {
		return Collections.singletonList(ConventionEvent.EventLocationType.PHYSICAL);
	}

	public String getEventAdditionalInfo(ConventionEvent event, Context context) {
		return null;
	}

	public String convertEventDescriptionURL(String url) {
		return url;
	}
}

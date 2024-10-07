package amai.org.conventions.events.activities;

import java.util.ArrayList;
import java.util.Date;

import amai.org.conventions.model.ConventionEvent;
import amai.org.conventions.model.conventions.Convention;

public class EventsTimeSlot {
	private ArrayList<ConventionEvent> events;
	private Date startTime;
	private Date endTime;

	public EventsTimeSlot() {
		this.events = new ArrayList<>(1);
	}

	public EventsTimeSlot(Date startTime, Date endTime) {
		this.startTime = startTime;
		this.endTime = endTime;
	}

	public ArrayList<ConventionEvent> getEvents() {
		return events;
	}

	public void addEvent(ConventionEvent event) {
		events.add(event);
	}

	public Date getStartTime() {
		if (events != null && events.size() > 0) {
			Date firstStartTime = null;
			Date firstOngoingEventStartTime = null;
			for (ConventionEvent event : events) {
				if (Convention.getInstance().isEventOngoing(event)) {
					if (firstOngoingEventStartTime == null || firstOngoingEventStartTime.after(event.getStartTime())) {
						firstOngoingEventStartTime = event.getStartTime();
					}
				} else {
					if (firstStartTime == null || firstStartTime.after(event.getStartTime())) {
						firstStartTime = event.getStartTime();
					}
				}
			}
			// Only return the time of ongoing events if there are no regular events
			return firstStartTime == null ? firstOngoingEventStartTime : firstStartTime;
		} else {
			return startTime;
		}
	}

	public Date getEndTime() {
		if (events != null && events.size() > 0) {
			Date lastEndTime = null;
			Date lastOngoingEventEndTime = null;
			for (ConventionEvent event : events) {
				if (Convention.getInstance().isEventOngoing(event)) {
					if (lastOngoingEventEndTime == null || lastOngoingEventEndTime.before(event.getEndTime())) {
						lastOngoingEventEndTime = event.getEndTime();
					}
				} else {
					if (lastEndTime == null || lastEndTime.before(event.getEndTime())) {
						lastEndTime = event.getEndTime();
					}
				}
			}
			// Only return the time of ongoing events if there are no regular events
			return lastEndTime == null ? lastOngoingEventEndTime : lastEndTime;
		} else {
			return endTime;
		}
	}

	public boolean areAllEventsOngoing() {
		if (events == null || events.size() == 0) {
			return false;
		}

		for (ConventionEvent event : events) {
			if (!Convention.getInstance().isEventOngoing(event)) {
				return false;
			}
		}

		return true;
	}

	public EventsTimeSlotType getType() {
		if (events == null || events.size() == 0) {
			return EventsTimeSlotType.NO_EVENTS;
		} else if (events.size() == 1) {
			return EventsTimeSlotType.SINGLE_EVENT;
		} else {
			return EventsTimeSlotType.CONFLICTING_EVENTS;
		}
	}

	public enum EventsTimeSlotType {
		NO_EVENTS, SINGLE_EVENT, CONFLICTING_EVENTS
	}
}

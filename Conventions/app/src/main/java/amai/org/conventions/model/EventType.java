package amai.org.conventions.model;

import amai.org.conventions.R;

public enum EventType {

    Games(R.attr.eventTypeCentralColor, "משחקים"),
    Community(R.attr.eventTypeSpecialColor, "אירוע קהילה"),
    Lecture(R.attr.eventTypeLectureColor, "הרצאה"),
    Workshop(R.attr.eventTypeWorkshopColor, "סדנה"),
    Panel(R.attr.eventTypePanelColor, "פאנל"),
    GuestOfHonor(R.attr.eventTypePanelColor, "אורחת כבוד");

    private int backgroundColorAttributeId;
	private String description;

    EventType(int backgroundColorAttributeId, String description) {
        this.backgroundColorAttributeId = backgroundColorAttributeId;
	    this.description = description;
    }

    public int getBackgroundColorAttributeId() {
        return backgroundColorAttributeId;
    }

	public String getDescription() {
		return description;
	}

    public static EventType parse(int eventTypeId) {
        switch (eventTypeId) {
            case 16:
                return EventType.Games;
            case 17:
                return EventType.Community;
            case 18:
                return EventType.Lecture;
            case 19:
                return EventType.Workshop;
            case 20:
                return EventType.Panel;
            case 22:
                return EventType.GuestOfHonor;

            default:
                return EventType.Community;
        }
    }
}

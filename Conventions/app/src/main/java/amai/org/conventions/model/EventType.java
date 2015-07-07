package amai.org.conventions.model;

import android.util.Log;

import amai.org.conventions.R;

public enum EventType {

    Central(R.attr.eventTypeCentralColor, "מרכזי"),
    Special(R.attr.eventTypeSpecialColor, "מיוחד"),
    Screening(R.attr.eventTypeScreeningColor, "הקרנה"),
    Lecture(R.attr.eventTypeLectureColor, "הרצאה"),
    Workshop(R.attr.eventTypeWorkshopColor, "סדנה"),
    Panel(R.attr.eventTypePanelColor, "פאנל");

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
                return EventType.Lecture;
            case 17:
                return EventType.Workshop;
            case 18:
                return EventType.Panel;
            case 19:
                return EventType.Screening;
            case 20:
                return EventType.Central;
            case 21:
                return EventType.Special;

            default:
                return EventType.Central;
        }
    }
}

package amai.org.conventions.model;

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
}

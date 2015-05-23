package amai.org.conventions.model;

import amai.org.conventions.R;

public enum EventType {

    Central(R.color.medium_purple, "מרכזי"),
    Special(R.color.silver, "מיוחד"),
    Screening(R.color.dark_purple, "הקרנה"),
    Lecture(R.color.red, "הרצאה"),
    Workshop(R.color.light_purple, "סדנה"),
    Panel(R.color.yellow, "פאנל");

    private int backgroundColorId;
	private String description;

    EventType(int backgroundColorId, String description) {
        this.backgroundColorId = backgroundColorId;
	    this.description = description;
    }

    public int getBackgroundColorId() {
        return backgroundColorId;
    }

	public String getDescription() {
		return description;
	}
}

package amai.org.conventions.model;

import android.graphics.Color;

public enum EventType {

    Stage(Colors.PURPLE_MEDIUM),
    Special(Colors.GRAY),
    Screening(Colors.PURPLE_DARK),
    Lecture(Colors.RED),
    Workshop(Colors.PURPLE_LIGHT),
    Panel(Colors.YELLOW);

    private int backgroundColor;
    EventType(int backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public int getBackgroundColor() {
        return backgroundColor;
    }
}

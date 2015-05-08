package amai.org.conventions.model;

import amai.org.conventions.R;

public enum EventType {

    Stage(R.color.medium_purple),
    Special(R.color.silver),
    Screening(R.color.dark_purple),
    Lecture(R.color.red),
    Workshop(R.color.light_purple),
    Panel(R.color.yellow);

    private int backgroundColorId;
    EventType(int backgroundColorId) {
        this.backgroundColorId = backgroundColorId;
    }

    public int getBackgroundColorId() {
        return backgroundColorId;
    }
}

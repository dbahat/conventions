package amai.org.conventions.events;

import java.util.Calendar;

import amai.org.conventions.model.ConventionEvent;

public class ProgrammeConventionEvent {
    private ConventionEvent event;
    private Calendar timeSection;

    public ProgrammeConventionEvent(ConventionEvent event, Calendar timeSection) {
        this.event = event;
        this.timeSection = timeSection;
    }

    public ConventionEvent getEvent() {
        return event;
    }

    public Calendar getTimeSection() {
        return timeSection;
    }
}

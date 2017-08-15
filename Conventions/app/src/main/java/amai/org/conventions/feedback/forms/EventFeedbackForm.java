package amai.org.conventions.feedback.forms;

import amai.org.conventions.model.Survey;

public class EventFeedbackForm extends FeedbackForm {
    private String eventTitleEntry;
    private String eventTimeEntry;
    private String hallEntry;

    public String getEventTitleEntry() {
        return eventTitleEntry;
    }

    public void setEventTitleEntry(String eventTitleEntry) {
        this.eventTitleEntry = eventTitleEntry;
    }

    public EventFeedbackForm withEventTitleEntry(String eventTitleEntry) {
        setEventTitleEntry(eventTitleEntry);
        return this;
    }

    public String getEventTimeEntry() {
        return eventTimeEntry;
    }

    public void setEventTimeEntry(String eventTimeEntry) {
        this.eventTimeEntry = eventTimeEntry;
    }

    public EventFeedbackForm withEventTimeEntry(String eventTitleEntry) {
        setEventTimeEntry(eventTitleEntry);
        return this;
    }

    public String getHallEntry() {
        return hallEntry;
    }

    public void setHallEntry(String hallEntry) {
        this.hallEntry = hallEntry;
    }

    public EventFeedbackForm withHallEntry(String hallEntry) {
        setHallEntry(hallEntry);
        return this;
    }

    @Override
    public boolean canFillFeedback(Survey feedback) {
        if (getEventTimeEntry() == null) {
            return false;
        }
        if (getEventTitleEntry() == null) {
            return false;
        }
        if (getHallEntry() == null) {
            return false;
        }
        return super.canFillFeedback(feedback);
    }
}

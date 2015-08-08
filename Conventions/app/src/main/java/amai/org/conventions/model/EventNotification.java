package amai.org.conventions.model;

import java.io.Serializable;
import java.util.Date;

public class EventNotification implements Serializable, Cloneable {
    private Date notificationTime;
    private Type type;

    public EventNotification(Type type) {
        this.type = type;
    }

    public void setNotificationTime(Date notificationTime) {
        this.notificationTime = notificationTime;
    }

    public Date getNotificationTime() {
        return notificationTime;
    }

    public boolean isEnabled() {
        return notificationTime != null;
    }

    public Type getType() {
        return type;
    }

    public enum Type {
        AboutToStart,
        FeedbackReminder
    }
}

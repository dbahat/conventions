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
		return notificationTime != null && notificationTime.after(new Date());
	}

	public Type getType() {
		return type;
	}

	@Override
	public EventNotification clone() throws CloneNotSupportedException {
		return (EventNotification) super.clone();
	}

	public enum Type {
		AboutToStart,
		FeedbackReminder
	}
}

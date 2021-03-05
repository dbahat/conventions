package amai.org.conventions.model;

import java.io.Serializable;

import androidx.annotation.Nullable;

public class EventNotification implements Serializable, Cloneable {
	/**
	 * Time difference in milliseconds between the relevant event time (start/end) and the notification.
	 * Null means the notification is disabled.
	 */
	@Nullable
	private Long timeDiffInMillis;
	private final Type type;

	public EventNotification(Type type) {
		this.type = type;
	}

	public void setTimeDiffInMillis(long timeDiffInMillis) {
		this.timeDiffInMillis = timeDiffInMillis;
	}

	public void disable() {
		this.timeDiffInMillis = null;
	}

	@Nullable
	public Long getTimeDiffInMillis() {
		return timeDiffInMillis;
	}

	public boolean isEnabled() {
		return timeDiffInMillis != null;
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

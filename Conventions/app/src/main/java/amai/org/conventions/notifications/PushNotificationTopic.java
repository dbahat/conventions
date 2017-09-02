package amai.org.conventions.notifications;

import sff.org.conventions.R;
import amai.org.conventions.model.conventions.Convention;

public enum PushNotificationTopic {
	TOPIC_GENERAL("general", R.string.push_notification_title),
	TOPIC_EVENTS("events", R.string.show_event_notifications_title),
	TOPIC_TEST("test", R.string.show_test_notifications_title),
	TOPIC_EMERGENCY("emergency", R.string.push_notification_title);

	private final String topic;
	private final int titleResource;

	PushNotificationTopic(String topic, int titleResource) {
		this.topic = Convention.getInstance().getId().toLowerCase() + "_" + topic;
		this.titleResource = titleResource;
	}

	public String getTopic() {
		return topic;
	}

	public int getTitleResource() {
		return titleResource;
	}

	public static PushNotificationTopic getByTopic(String topic) {
		for (PushNotificationTopic value : PushNotificationTopic.values()) {
			if (value.getTopic().equals(topic)) {
				return value;
			}
		}
		return null;
	}
}

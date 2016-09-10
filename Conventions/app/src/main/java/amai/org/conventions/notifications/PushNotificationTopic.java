package amai.org.conventions.notifications;

import amai.org.conventions.R;

public enum PushNotificationTopic {
	TOPIC_GENERAL("icon2016_general", R.string.push_notification_title),
	TOPIC_EVENTS("icon2016_events", R.string.show_event_notifications_title),
	TOPIC_TEST("icon2016_test", R.string.show_test_notifications_title),
	TOPIC_EMERGENCY("icon2016_emergency", R.string.push_notification_title);

	private final String topic;
	private final int titleResource;

	PushNotificationTopic(String topic, int titleResource) {
		this.topic = topic;
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

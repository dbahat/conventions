package amai.org.conventions.notifications;

import amai.org.conventions.R;

public enum PushNotificationTopic {
	TOPIC_GENERAL("cami2016_general", R.string.push_notification_title),
	TOPIC_EVENTS("cami2016_events", R.string.show_event_notifications_title),
	TOPIC_COSPLAY("cami2016_cosplay", R.string.show_cosplay_notifications_title),
	TOPIC_BUS("cami2016_bus", R.string.show_bus_notifications_title),
	TOPIC_TEST("cami2016_test", R.string.show_test_notifications_title),
	TOPIC_EMERGENCY("cami2016_emergency", R.string.push_notification_title);

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

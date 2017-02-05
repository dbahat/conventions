package amai.org.conventions.notifications;

import amai.org.conventions.R;

public enum PushNotificationTopic {
	TOPIC_GENERAL("harucon2017_general", R.string.push_notification_title),
	TOPIC_EVENTS("harucon2017_events", R.string.show_event_notifications_title),
	TOPIC_COSPLAY("harucon2017_cosplay", R.string.show_cosplay_notifications_title),
	TOPIC_BUS("harucon2017_bus", R.string.show_bus_notifications_title),
	TOPIC_TEST("harucon2017_test", R.string.show_test_notifications_title),
	TOPIC_EMERGENCY("harucon2017_emergency", R.string.push_notification_title);

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

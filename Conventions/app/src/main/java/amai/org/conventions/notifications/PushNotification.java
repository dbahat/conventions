package amai.org.conventions.notifications;


import androidx.annotation.StringRes;

import java.io.Serializable;

import sff.org.conventions.R;


public class PushNotification implements Serializable {
	public int id;
	public String messageId;
	public String message;
	public String category;

	public PushNotification(int id, String messageId, String message, String category) {
		this.id = id;
		this.messageId = messageId;
		this.message = message;
		this.category = category;
	}


	public enum Type {
		EventAboutToStart,
		EventFeedbackReminder,
		ConventionFeedbackReminder,
		ConventionFeedbackLastChanceReminder,
		Push;

		public Channel getChannel() {
			switch (this) {
				case Push:
					return Channel.Notifications;
				case EventAboutToStart:
				case EventFeedbackReminder:
				case ConventionFeedbackReminder:
				case ConventionFeedbackLastChanceReminder:
				default:
					return Channel.Reminders;
			}
		}
	}

	public enum Channel {
		Notifications(R.string.notifications_preferences, R.string.notification_channel_description_notifications),
		Reminders(R.string.reminder_preferences, R.string.notification_channel_description_reminders);

		@StringRes
		private int displayName;
		@StringRes
		private int description;

		Channel(int displayName, int description) {
			this.displayName = displayName;
			this.description = description;
		}

		@StringRes
		public int getDisplayName() {
			return displayName;
		}

		@StringRes
		public int getDescription() {
			return description;
		}
	}
}
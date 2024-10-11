package amai.org.conventions.notifications;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;

import java.util.Calendar;
import java.util.Date;

import amai.org.conventions.ConventionsApplication;
import amai.org.conventions.model.ConventionEvent;
import amai.org.conventions.model.EventNotification;
import amai.org.conventions.model.conventions.Convention;
import amai.org.conventions.utils.Dates;
import amai.org.conventions.utils.Log;

import static android.app.PendingIntent.FLAG_CANCEL_CURRENT;
import static android.app.PendingIntent.FLAG_IMMUTABLE;

public class LocalNotificationScheduler {
	private final static String TAG = LocalNotificationScheduler.class.getCanonicalName();

	public static final int DEFAULT_PRE_EVENT_START_NOTIFICATION_MINUTES = 5;
	public static final int DEFAULT_POST_EVENT_END_NOTIFICATION_MINUTES = 0;

	private Context context;
	private AlarmManager alarmManager;

	public LocalNotificationScheduler(Context context) {
		this.context = context;
		this.alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
	}

	public void scheduleDefaultEventAlarms(ConventionEvent event) {
		SharedPreferences sharedPreferences = ConventionsApplication.settings.getSharedPreferences();
		if (sharedPreferences.getBoolean(Convention.getInstance().getId().toLowerCase() + "_event_starting_reminder", false)) {
			setDefaultEventAboutToStartNotification(event);
			Date eventAboutToStartNotificationTime = event.getEventAboutToStartNotificationTime();
			if (eventAboutToStartNotificationTime != null) {
				scheduleEventAboutToStartNotification(event, eventAboutToStartNotificationTime.getTime());
			}
		}

		if (sharedPreferences.getBoolean(Convention.getInstance().getId().toLowerCase() + "_event_feedback_reminder", false)) {
			setDefaultEventFeedbackReminderNotification(event);
			Date eventFeedbackReminderNotificationTime = event.getEventFeedbackReminderNotificationTime();
			if (eventFeedbackReminderNotificationTime != null) {
				scheduleFillFeedbackOnEventNotification(event, eventFeedbackReminderNotificationTime.getTime());
			}
		}

		Convention.getInstance().getStorage().saveUserInput();
	}

	public void scheduleEventAboutToStartNotification(ConventionEvent event, long time) {
		Log.i(TAG, "Scheduling event start notification of type for event " + event.getTitle() + " to " + time);
		if (time < System.currentTimeMillis()) {
			// Don't allow scheduling notifications in the past
			return;
		}

		PendingIntent pendingIntent = createEventNotificationPendingIntent(event, PushNotification.Type.EventAboutToStart);
		scheduleAlarm(time, pendingIntent, Accuracy.SMALLEST_TIME_WINDOW_BEFORE);
	}

	public void scheduleFillFeedbackOnEventNotification(ConventionEvent event, long time) {
		Log.i(TAG, "Scheduling event feedback notification of type for event " + event.getTitle() + " to " + time);
		if (time < System.currentTimeMillis()) {
			// Don't allow scheduling notifications in the past
			return;
		}

		PendingIntent pendingIntent = createEventNotificationPendingIntent(event, PushNotification.Type.EventFeedbackReminder);
		scheduleAlarm(time, pendingIntent, Accuracy.INACCURATE);
	}

	public void cancelDefaultEventAlarms(ConventionEvent event) {
		cancelEventAlarm(event, PushNotification.Type.EventAboutToStart);
		cancelEventAlarm(event, PushNotification.Type.EventFeedbackReminder);

		event.getUserInput().getEventFeedbackReminderNotification().disable();
		event.getUserInput().getEventAboutToStartNotification().disable();
		Convention.getInstance().getStorage().saveUserInput();
	}

	public void cancelEventAlarm(ConventionEvent event, PushNotification.Type notificationType) {
		Log.i(TAG, "Cancelling event alarm for notification of type " + notificationType + " for event " + event.getTitle());
		// Sending the extras due to apparent bug on Lollipop that this exact intent is used when rescheduling it
		// (canceling then re-setting it), so it needs the extras or it arrives without parameters and is not displayed
		Intent intent = new Intent(context, ShowNotificationReceiver.class)
				.setAction(notificationType.toString() + event.getId())
				.putExtra(ShowNotificationReceiver.EXTRA_EVENT_ID_TO_NOTIFY, event.getId())
				.putExtra(ShowNotificationReceiver.EXTRA_NOTIFICATION_TYPE, notificationType.toString());
		int flags = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S ? PendingIntent.FLAG_UPDATE_CURRENT | FLAG_IMMUTABLE : PendingIntent.FLAG_UPDATE_CURRENT;
		PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, flags);

		alarmManager.cancel(pendingIntent);
	}

	public void scheduleNotificationsToFillConventionFeedback() {
		if (Convention.getInstance().getFeedback().isSent() || Convention.getInstance().isFeedbackSendingTimeOver()) {
			return;
		}

		if (!ConventionsApplication.settings.wasConventionFeedbackNotificationShown()) {
			Calendar conventionFeedbackNotificationTime = Calendar.getInstance();
			conventionFeedbackNotificationTime.setTime(Convention.getInstance().getEndDate().getTime());
			conventionFeedbackNotificationTime.set(Calendar.HOUR_OF_DAY, 22);

			Intent intent = new Intent(context, ShowNotificationReceiver.class)
					.setAction(PushNotification.Type.ConventionFeedbackReminder.toString())
					.putExtra(ShowNotificationReceiver.EXTRA_NOTIFICATION_TYPE, PushNotification.Type.ConventionFeedbackReminder.toString());
			int flags = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S ? PendingIntent.FLAG_UPDATE_CURRENT | FLAG_IMMUTABLE : PendingIntent.FLAG_UPDATE_CURRENT;
			scheduleAlarm(conventionFeedbackNotificationTime.getTimeInMillis(), PendingIntent.getBroadcast(context, 0, intent, flags), Accuracy.INACCURATE);
		}

		if (!ConventionsApplication.settings.wasConventionLastChanceFeedbackNotificationShown()) {
			Calendar lastChanceNotificationTime = Calendar.getInstance();
			lastChanceNotificationTime.setTime(Convention.getInstance().getEndDate().getTime());
			lastChanceNotificationTime.add(Calendar.DATE, 4);
			lastChanceNotificationTime.set(Calendar.HOUR_OF_DAY, 10);

			Intent intent = new Intent(context, ShowNotificationReceiver.class)
					.setAction(PushNotification.Type.ConventionFeedbackLastChanceReminder.toString())
					.putExtra(ShowNotificationReceiver.EXTRA_NOTIFICATION_TYPE, PushNotification.Type.ConventionFeedbackLastChanceReminder.toString());
			int flags = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S ? PendingIntent.FLAG_UPDATE_CURRENT | FLAG_IMMUTABLE : PendingIntent.FLAG_UPDATE_CURRENT;
			scheduleAlarm(lastChanceNotificationTime.getTimeInMillis(), PendingIntent.getBroadcast(context, 0, intent, flags), Accuracy.INACCURATE);
		}
	}

	private PendingIntent createEventNotificationPendingIntent(ConventionEvent event, PushNotification.Type notificationType) {

		Intent intent = new Intent(context, ShowNotificationReceiver.class)
				.setAction(notificationType.toString() + event.getId()) // Setting unique action id so different event intents won't collide
				.putExtra(ShowNotificationReceiver.EXTRA_EVENT_ID_TO_NOTIFY, event.getId())
				.putExtra(ShowNotificationReceiver.EXTRA_NOTIFICATION_TYPE, notificationType.toString());

		int flags = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S ? FLAG_CANCEL_CURRENT | FLAG_IMMUTABLE : FLAG_CANCEL_CURRENT;
		return PendingIntent.getBroadcast(context, 0, intent, flags);
	}

	private void scheduleAlarm(long time, PendingIntent pendingIntent, Accuracy accuracy) {
		// For Kitkat and above, the AlarmService batches notifications to improve battery life at the cost of alarm accuracy.
		// Since event start notification time is important, schedule them using setWindow, which gives an exact window of time,
		// allowing for some optimization while being accurate enough.
		long length;
		switch (accuracy) {
			case INACCURATE:
				scheduleInaccurateAlarm(time, pendingIntent);
				break;
			default: // Accuracy.SMALLEST_TIME_WINDOW_BEFORE
				// The smallest reliable time window is 10 minutes
				length = 10 * Dates.MILLISECONDS_IN_MINUTE;
				time = time - length;
				alarmManager.setWindow(AlarmManager.RTC_WAKEUP, time, length, pendingIntent);
		}
	}

	private void scheduleInaccurateAlarm(long time, PendingIntent pendingIntent) {
		alarmManager.set(AlarmManager.RTC, time, pendingIntent);	}

	private enum Accuracy {SMALLEST_TIME_WINDOW_BEFORE, INACCURATE}

	public static void setDefaultEventAboutToStartNotification(ConventionEvent event) {
		EventNotification eventAboutToStartNotification = event.getUserInput().getEventAboutToStartNotification();
		eventAboutToStartNotification.setTimeDiffInMillis(- DEFAULT_PRE_EVENT_START_NOTIFICATION_MINUTES * Dates.MILLISECONDS_IN_MINUTE);
	}

	public static void setDefaultEventFeedbackReminderNotification(ConventionEvent event) {
		EventNotification feedbackReminder = event.getUserInput().getEventFeedbackReminderNotification();
		feedbackReminder.setTimeDiffInMillis(DEFAULT_POST_EVENT_END_NOTIFICATION_MINUTES * Dates.MILLISECONDS_IN_MINUTE);
	}
}

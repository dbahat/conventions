package amai.org.conventions.notifications;

import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;

import java.util.Calendar;

import amai.org.conventions.ConventionsApplication;
import amai.org.conventions.events.ConfigureNotificationsFragment;
import amai.org.conventions.model.ConventionEvent;
import amai.org.conventions.model.EventNotification;
import amai.org.conventions.model.conventions.Convention;
import amai.org.conventions.utils.Dates;

public class LocalNotificationScheduler {

	private Context context;
	private AlarmManager alarmManager;

	public LocalNotificationScheduler(Context context) {
		this.context = context;
		this.alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
	}

	public void scheduleDefaultEventAlarms(ConventionEvent event) {
		SharedPreferences sharedPreferences = ConventionsApplication.settings.getSharedPreferences();
		if (sharedPreferences.getBoolean(Convention.getInstance().getId().toLowerCase() + "_event_starting_reminder", false)) {
			EventNotification eventAboutToStartNotification = event.getUserInput().getEventAboutToStartNotification();
			eventAboutToStartNotification.setTimeDiffInMillis(
				- ConfigureNotificationsFragment.DEFAULT_PRE_EVENT_START_NOTIFICATION_MINUTES * Dates.MILLISECONDS_IN_MINUTE);
			scheduleEventAboutToStartNotification(event, event.getEventAboutToStartNotificationTime().getTime());
		}

		if (sharedPreferences.getBoolean(Convention.getInstance().getId().toLowerCase() + "_event_feedback_reminder", false)) {
			EventNotification eventFeedbackReminderNotification = event.getUserInput().getEventFeedbackReminderNotification();
			eventFeedbackReminderNotification.setTimeDiffInMillis(
				ConfigureNotificationsFragment.DEFAULT_POST_EVENT_START_NOTIFICATION_MINUTES * Dates.MILLISECONDS_IN_MINUTE
			);
			scheduleFillFeedbackOnEventNotification(event, event.getEventFeedbackReminderNotificationTime().getTime());
		}

		Convention.getInstance().getStorage().saveUserInput();
	}

	public void scheduleEventAboutToStartNotification(ConventionEvent event, long time) {
		if (time < System.currentTimeMillis()) {
			// Don't allow scheduling notifications in the past
			return;
		}

		PendingIntent pendingIntent = createEventNotificationPendingIntent(event, PushNotification.Type.EventAboutToStart);
		scheduleAlarm(time, pendingIntent, Accuracy.UP_TO_1_MINUTE_EARLIER);
	}

	public void scheduleFillFeedbackOnEventNotification(ConventionEvent event, long time) {
		if (time < System.currentTimeMillis()) {
			// Don't allow scheduling notifications in the past
			return;
		}

		PendingIntent pendingIntent = createEventNotificationPendingIntent(event, PushNotification.Type.EventFeedbackReminder);
		scheduleAlarm(time, pendingIntent, Accuracy.UP_TO_5_MINUTES_LATER);
	}

	public void cancelDefaultEventAlarms(ConventionEvent event) {
		cancelEventAlarm(event, PushNotification.Type.EventAboutToStart);
		cancelEventAlarm(event, PushNotification.Type.EventFeedbackReminder);

		event.getUserInput().getEventFeedbackReminderNotification().disable();
		event.getUserInput().getEventAboutToStartNotification().disable();
		Convention.getInstance().getStorage().saveUserInput();
	}

	public void cancelEventAlarm(ConventionEvent event, PushNotification.Type notificationType) {
		// Sending the extras due to apparent bug on Lollipop that this exact intent is used when rescheduling it
		// (canceling then re-setting it), so it needs the extras or it arrives without parameters and is not displayed
		Intent intent = new Intent(context, ShowNotificationReceiver.class)
				.setAction(notificationType.toString() + event.getId())
				.putExtra(ShowNotificationReceiver.EXTRA_EVENT_ID_TO_NOTIFY, event.getId())
				.putExtra(ShowNotificationReceiver.EXTRA_NOTIFICATION_TYPE, notificationType.toString());
		PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);

		alarmManager.cancel(pendingIntent);
	}

	public void scheduleNotificationsToFillConventionFeedback() {
		Calendar twoWeeksPostConventionDate = Calendar.getInstance();
		twoWeeksPostConventionDate.setTime(Convention.getInstance().getEndDate().getTime());
		twoWeeksPostConventionDate.add(Calendar.DATE, 14);

		if (Convention.getInstance().getFeedback().isSent()
				|| Calendar.getInstance().getTimeInMillis() >= twoWeeksPostConventionDate.getTimeInMillis()) {
			return;
		}

		if (!ConventionsApplication.settings.wasConventionFeedbackNotificationShown()) {
			Calendar oneDayPostConventionDate = Calendar.getInstance();
			oneDayPostConventionDate.setTime(Convention.getInstance().getEndDate().getTime());
			oneDayPostConventionDate.set(Calendar.HOUR_OF_DAY, 22);

			Intent intent = new Intent(context, ShowNotificationReceiver.class)
					.setAction(PushNotification.Type.ConventionFeedbackReminder.toString())
					.putExtra(ShowNotificationReceiver.EXTRA_NOTIFICATION_TYPE, PushNotification.Type.ConventionFeedbackReminder.toString());
			scheduleAlarm(oneDayPostConventionDate.getTimeInMillis(), PendingIntent.getBroadcast(context, 0, intent, 0), Accuracy.INACCURATE);
		}

		if (!ConventionsApplication.settings.wasConventionLastChanceFeedbackNotificationShown()) {
			Calendar tenDaysPostConventionDate = Calendar.getInstance();
			tenDaysPostConventionDate.setTime(Convention.getInstance().getEndDate().getTime());
			tenDaysPostConventionDate.add(Calendar.DATE, 4);
			tenDaysPostConventionDate.set(Calendar.HOUR_OF_DAY, 10);

			Intent intent = new Intent(context, ShowNotificationReceiver.class)
					.setAction(PushNotification.Type.ConventionFeedbackLastChanceReminder.toString())
					.putExtra(ShowNotificationReceiver.EXTRA_NOTIFICATION_TYPE, PushNotification.Type.ConventionFeedbackLastChanceReminder.toString());
			scheduleAlarm(tenDaysPostConventionDate.getTimeInMillis(), PendingIntent.getBroadcast(context, 0, intent, 0), Accuracy.INACCURATE);
		}
	}

	private PendingIntent createEventNotificationPendingIntent(ConventionEvent event, PushNotification.Type notificationType) {

		Intent intent = new Intent(context, ShowNotificationReceiver.class)
				.setAction(notificationType.toString() + event.getId()) // Setting unique action id so different event intents won't collide
				.putExtra(ShowNotificationReceiver.EXTRA_EVENT_ID_TO_NOTIFY, event.getId())
				.putExtra(ShowNotificationReceiver.EXTRA_NOTIFICATION_TYPE, notificationType.toString());
		return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
	}

	private void scheduleAlarm(long time, PendingIntent pendingIntent, Accuracy accuracy) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && accuracy != Accuracy.INACCURATE) {
			scheduleAlarmWhileInDoze(time, pendingIntent);
		} else {
			scheduleInaccurateAlarm(time, pendingIntent, accuracy);
		}
	}

	@TargetApi(Build.VERSION_CODES.M)
	private void scheduleAlarmWhileInDoze(long time, PendingIntent pendingIntent) {
		// Starting Android M the device can enter doze mode, meaning it might not get the notifications in time.
		// The only API currently available to get the notification to wake up the device is either
		// the inaccurate setAndAllowWhileIdle() and setExactAndAllowWhileIdle().
		//
		// Since there's no option to define a window, we go with the more battery consuming but accurate setExactAndAllowWhileIdle().
		alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, time, pendingIntent);
	}

	@TargetApi(Build.VERSION_CODES.KITKAT)
	private void scheduleInaccurateAlarm(long time, PendingIntent pendingIntent, Accuracy accuracy) {
		// For Kitkat and above, the AlarmService batches notifications to improve battery life at the cost of alarm accuracy.
		// Since event start notification time is important, schedule them using setWindow, which gives an exact window of time,
		// allowing for some optimization while being accurate enough.
		long length;
		switch (accuracy) {
			case INACCURATE:
				scheduleAlarm(time, pendingIntent);
				break;
			default:
				if (accuracy == Accuracy.UP_TO_1_MINUTE_EARLIER) {
					time = time - Dates.MILLISECONDS_IN_MINUTE;
					length = Dates.MILLISECONDS_IN_MINUTE;
				} else {
					length = 5 * Dates.MILLISECONDS_IN_MINUTE;
				}
				alarmManager.setWindow(AlarmManager.RTC_WAKEUP, time, length, pendingIntent);

		}
	}

	private void scheduleAlarm(long time, PendingIntent pendingIntent) {
		alarmManager.set(AlarmManager.RTC_WAKEUP, time, pendingIntent);
	}

	private enum Accuracy {UP_TO_1_MINUTE_EARLIER, UP_TO_5_MINUTES_LATER, INACCURATE}
}

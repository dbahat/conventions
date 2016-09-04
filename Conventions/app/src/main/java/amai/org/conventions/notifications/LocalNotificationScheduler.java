package amai.org.conventions.notifications;

import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;

import java.util.Calendar;
import java.util.Date;

import amai.org.conventions.ConventionsApplication;
import amai.org.conventions.events.ConfigureNotificationsFragment;
import amai.org.conventions.model.conventions.Convention;
import amai.org.conventions.model.ConventionEvent;
import amai.org.conventions.model.EventNotification;
import amai.org.conventions.utils.Dates;

public class LocalNotificationScheduler {

	private Context context;
    private AlarmManager alarmManager;

    public LocalNotificationScheduler(Context context) {
        this.context = context;
        this.alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
    }

    public void scheduleDefaultEventAlarms(ConventionEvent event) {
	    // TODO move to ConventionApplication.settings for the next convention and use convention id prefix instead of hard-coded
	    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
	    if (sharedPreferences.getBoolean("cami2016_event_starting_reminder", false)) {
	        EventNotification eventAboutToStartNotification = event.getUserInput().getEventAboutToStartNotification();
	        Date defaultEventStartNotificationTime = new Date(event.getStartTime().getTime()
	                - ConfigureNotificationsFragment.DEFAULT_PRE_EVENT_START_NOTIFICATION_MINUTES * Dates.MILLISECONDS_IN_MINUTE);
	        eventAboutToStartNotification.setNotificationTime(defaultEventStartNotificationTime);
	        scheduleEventAboutToStartNotification(event, eventAboutToStartNotification.getNotificationTime().getTime());
	    }

	    // TODO move to ConventionApplication.settings for the next convention and use convention id prefix instead of hard-coded
	    if (sharedPreferences.getBoolean("cami2016_event_feedback_reminder", false)) {
		    EventNotification eventFeedbackReminderNotification = event.getUserInput().getEventFeedbackReminderNotification();
		    Date defaultEventEndNotificationTime = new Date(event.getEndTime().getTime()
				    + ConfigureNotificationsFragment.DEFAULT_POST_EVENT_START_NOTIFICATION_MINUTES * Dates.MILLISECONDS_IN_MINUTE);
		    eventFeedbackReminderNotification.setNotificationTime(defaultEventEndNotificationTime);
		    scheduleFillFeedbackOnEventNotification(event, eventFeedbackReminderNotification.getNotificationTime().getTime());
	    }

        Convention.getInstance().getStorage().saveUserInput();
    }

    public void scheduleEventAboutToStartNotification(ConventionEvent event, long time) {
        if (time < System.currentTimeMillis()) {
            // Don't allow scheduling notifications in the past
            return;
        }

        PendingIntent pendingIntent = createEventNotificationPendingIntent(event, ShowNotificationService.Type.EventAboutToStart);

	    scheduleAlarm(time, pendingIntent, Accuracy.UP_TO_1_MINUTE_EARLIER);
    }

	public void scheduleFillFeedbackOnEventNotification(ConventionEvent event, long time) {
        if (time < System.currentTimeMillis()) {
            // Don't allow scheduling notifications in the past
            return;
        }

        PendingIntent pendingIntent = createEventNotificationPendingIntent(event, ShowNotificationService.Type.EventFeedbackReminder);
        scheduleAlarm(time, pendingIntent, Accuracy.UP_TO_5_MINUTES_LATER);
    }

    public void cancelDefaultEventAlarms(ConventionEvent event) {
        cancelEventAlarm(event, ShowNotificationService.Type.EventAboutToStart);
        cancelEventAlarm(event, ShowNotificationService.Type.EventFeedbackReminder);

        event.getUserInput().getEventFeedbackReminderNotification().setNotificationTime(null);
        event.getUserInput().getEventAboutToStartNotification().setNotificationTime(null);
        Convention.getInstance().getStorage().saveUserInput();
    }

    public void cancelEventAlarm(ConventionEvent event, ShowNotificationService.Type notificationType) {
	    // Sending the extras due to apparent bug on Lollipop that this exact intent is used when rescheduling it
	    // (canceling then re-setting it), so it needs the extras or it arrives without parameters and is not displayed
        Intent intent = new Intent(context, ShowNotificationService.class)
                .setAction(notificationType.toString() + event.getId())
		        .putExtra(ShowNotificationService.EXTRA_EVENT_TO_NOTIFY, event)
		        .putExtra(ShowNotificationService.EXTRA_NOTIFICATION_TYPE, notificationType);
        PendingIntent pendingIntent = PendingIntent.getService(context, 0, intent, 0);

        alarmManager.cancel(pendingIntent);
    }

    public void scheduleNotificationToFillConventionFeedback() {
        Calendar twoWeeksPostConventionDate = Calendar.getInstance();
        twoWeeksPostConventionDate.setTime(Convention.getInstance().getDate().getTime());
        twoWeeksPostConventionDate.add(Calendar.DATE, 14);

        if (Convention.getInstance().getFeedback().isSent()
                || ConventionsApplication.settings.wasConventionFeedbackNotificationShown()
                || Calendar.getInstance().getTimeInMillis() >= twoWeeksPostConventionDate.getTimeInMillis()) {
            return;
        }

        Calendar oneDayPostConventionDate = Calendar.getInstance();
        oneDayPostConventionDate.setTime(Convention.getInstance().getDate().getTime());
        oneDayPostConventionDate.add(Calendar.DATE, 1);
        oneDayPostConventionDate.set(Calendar.HOUR_OF_DAY, 10);

        Intent intent = new Intent(context, ShowNotificationService.class)
                .putExtra(ShowNotificationService.EXTRA_NOTIFICATION_TYPE, ShowNotificationService.Type.ConventionFeedbackReminder);
        scheduleAlarm(oneDayPostConventionDate.getTimeInMillis(), PendingIntent.getService(context, 0, intent, 0), Accuracy.INACCURATE);
    }

    private PendingIntent createEventNotificationPendingIntent(ConventionEvent event, ShowNotificationService.Type notificationType) {

        Intent intent = new Intent(context, ShowNotificationService.class)
                .setAction(notificationType.toString() + event.getId()) // Setting unique action id so different event intents won't collide
                .putExtra(ShowNotificationService.EXTRA_EVENT_TO_NOTIFY, event)
                .putExtra(ShowNotificationService.EXTRA_NOTIFICATION_TYPE, notificationType);
        return PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
    }

	private void scheduleAlarm(long time, PendingIntent pendingIntent, Accuracy accuracy) {
		// For Kitkat and above, the AlarmService batches notifications to improve battery life at the cost of alarm accuracy
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			scheduleInaccurateAlarm(time, pendingIntent, accuracy);
		} else {
			scheduleAlarm(time, pendingIntent);
		}
	}

	@TargetApi(Build.VERSION_CODES.KITKAT)
    private void scheduleInaccurateAlarm(long time, PendingIntent pendingIntent, Accuracy accuracy) {
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

	private enum Accuracy {UP_TO_1_MINUTE_EARLIER, UP_TO_5_MINUTES_LATER, INACCURATE }
}

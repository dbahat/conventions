package amai.org.conventions;

import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import java.util.Calendar;
import java.util.Date;

import amai.org.conventions.events.ConfigureNotificationsFragment;
import amai.org.conventions.model.Convention;
import amai.org.conventions.model.ConventionEvent;
import amai.org.conventions.model.EventNotification;
import amai.org.conventions.utils.Dates;

public class AlarmScheduler {

	private Context context;
    private AlarmManager alarmManager;

    public AlarmScheduler(Context context) {
        this.context = context;
        this.alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
    }

    public void scheduleDefaultEventAlarms(ConventionEvent event) {
        EventNotification eventAboutToStartNotification = event.getUserInput().getEventAboutToStartNotification();
        Date defaultEventStartNotificationTime = new Date(event.getStartTime().getTime()
                - ConfigureNotificationsFragment.DEFAULT_PRE_EVENT_START_NOTIFICATION_MINUTES * Dates.MILLISECONDS_IN_MINUTE);
        eventAboutToStartNotification.setNotificationTime(defaultEventStartNotificationTime);
        scheduleEventAboutToStartNotification(event, eventAboutToStartNotification.getNotificationTime().getTime());

        EventNotification eventFeedbackReminderNotification = event.getUserInput().getEventFeedbackReminderNotification();
        Date defaultEventEndNotificationTime = new Date(event.getEndTime().getTime()
                + ConfigureNotificationsFragment.DEFAULT_POST_EVENT_START_NOTIFICATION_MINUTES * Dates.MILLISECONDS_IN_MINUTE);
        eventFeedbackReminderNotification.setNotificationTime(defaultEventEndNotificationTime);
        scheduleFillFeedbackOnEventNotification(event, eventFeedbackReminderNotification.getNotificationTime().getTime());

        Convention.getInstance().getStorage().saveUserInput();
    }

    public void scheduleEventAboutToStartNotification(ConventionEvent event, long time) {
        if (time < System.currentTimeMillis()) {
            // Don't allow scheduling notifications in the past
            return;
        }

        PendingIntent pendingIntent = createEventNotificationPendingIntent(event, EventNotification.Type.AboutToStart);

	    scheduleAlarm(time, pendingIntent, Accuracy.UP_TO_1_MINUTE_EARLIER);
    }

	public void scheduleFillFeedbackOnEventNotification(ConventionEvent event, long time) {
        if (time < System.currentTimeMillis()) {
            // Don't allow scheduling notifications in the past
            return;
        }

        PendingIntent pendingIntent = createEventNotificationPendingIntent(event, EventNotification.Type.FeedbackReminder);
        scheduleAlarm(time, pendingIntent, Accuracy.UP_TO_5_MINUTES_LATER);
    }

    public void cancelDefaultEventAlarms(ConventionEvent event) {
        cancelEventAlarm(event, EventNotification.Type.AboutToStart);
        cancelEventAlarm(event, EventNotification.Type.FeedbackReminder);

        event.getUserInput().getEventFeedbackReminderNotification().setNotificationTime(null);
        event.getUserInput().getEventAboutToStartNotification().setNotificationTime(null);
        Convention.getInstance().getStorage().saveUserInput();
    }

    public void cancelEventAlarm(ConventionEvent event, EventNotification.Type notificationType) {
	    // Sending the extras due to apparent bug on Lollipop that this exact intent is used when rescheduling it
	    // (canceling then re-setting it), so it needs the extras or it arrives without parameters and is not displayed
        Intent intent = new Intent(context, EventNotificationService.class)
                .setAction(notificationType.toString() + event.getId())
		        .putExtra(EventNotificationService.EXTRA_EVENT_TO_NOTIFY, event)
		        .putExtra(EventNotificationService.EXTRA_EVENT_NOTIFICATION_TYPE, notificationType);
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

        Intent intent = new Intent(context, EventNotificationService.class)
                .putExtra(EventNotificationService.EXTRA_IS_END_OF_CONVENTION_NOTIFICATION, true);
        scheduleAlarm(oneDayPostConventionDate.getTimeInMillis(), PendingIntent.getService(context, 0, intent, 0), Accuracy.INACCURATE);
    }

    private PendingIntent createEventNotificationPendingIntent(ConventionEvent event, EventNotification.Type notificationType) {

        Intent intent = new Intent(context, EventNotificationService.class)
                .setAction(notificationType.toString() + event.getId()) // Setting unique action id so different event intents won't collide
                .putExtra(EventNotificationService.EXTRA_EVENT_TO_NOTIFY, event)
                .putExtra(EventNotificationService.EXTRA_EVENT_NOTIFICATION_TYPE, notificationType);
        return PendingIntent.getService(context, 0, intent, 0);
    }

	private void scheduleAlarm(long time, PendingIntent pendingIntent, Accuracy accuracy) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			scheduleAlarmKitkat(time, pendingIntent, accuracy);
		} else {
			scheduleAlarm(time, pendingIntent);
		}
	}


	@TargetApi(Build.VERSION_CODES.KITKAT)
    private void scheduleAlarmKitkat(long time, PendingIntent pendingIntent, Accuracy accuracy) {
        // For Kitkat and above, the AlarmService batches notifications to improve battery life at the cost of alarm accuracy.
        // Since event start notification time is important, schedule them using setExact, which bypass this optimization.
		long startTime;
		long endTime;
		switch (accuracy) {
			case INACCURATE:
				scheduleAlarm(time, pendingIntent);
				break;
			default:
				if (accuracy == Accuracy.UP_TO_1_MINUTE_EARLIER) {
					startTime = time - Dates.MILLISECONDS_IN_MINUTE;
					endTime = time;
				} else {
					startTime = time;
					endTime = time + 5 * Dates.MILLISECONDS_IN_MINUTE;
				}
                alarmManager.setWindow(AlarmManager.RTC_WAKEUP, startTime, endTime, pendingIntent);

		}
    }

    private void scheduleAlarm(long time, PendingIntent pendingIntent) {
        alarmManager.set(AlarmManager.RTC_WAKEUP, time, pendingIntent);
    }

	private enum Accuracy {UP_TO_1_MINUTE_EARLIER, UP_TO_5_MINUTES_LATER, INACCURATE }
}

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
                - ConfigureNotificationsFragment.DEFAULT_PRE_EVENT_START_NOTIFICATION_MINUTES * 60 * 1000);
        eventAboutToStartNotification.setNotificationTime(defaultEventStartNotificationTime);
        scheduleEventAboutToStartNotification(event, eventAboutToStartNotification.getNotificationTime().getTime());

        EventNotification eventFeedbackReminderNotification = event.getUserInput().getEventFeedbackReminderNotification();
        Date defaultEventEndNotificationTime = new Date(event.getEndTime().getTime()
                + ConfigureNotificationsFragment.DEFAULT_POST_EVENT_START_NOTIFICATION_MINUTES * 60 * 1000);
        eventFeedbackReminderNotification.setNotificationTime(defaultEventEndNotificationTime);
        scheduleFillFeedbackOnEventNotification(event, eventFeedbackReminderNotification.getNotificationTime().getTime());

        Convention.getInstance().getStorage().saveUserInput();
    }

    public void scheduleEventAboutToStartNotification(ConventionEvent event, long time) {
        if (time < System.currentTimeMillis()) {
            // Don't allow scheduling event about to start notifications in the past
            return;
        }

        PendingIntent pendingIntent = createEventNotificationPendingIntent(event, EventNotification.Type.AboutToStart);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            scheduleExactAlarmKitkat(time, pendingIntent);
        } else {
            scheduleAlarm(time, pendingIntent);
        }
    }

    public void scheduleFillFeedbackOnEventNotification(ConventionEvent event, long time) {
        if (time < System.currentTimeMillis()) {
            // Don't allow scheduling event about to ended notifications in the past
            return;
        }

        PendingIntent pendingIntent = createEventNotificationPendingIntent(event, EventNotification.Type.FeedbackReminder);
        scheduleAlarm(time, pendingIntent);
    }

    public void cancelDefaultEventAlarms(ConventionEvent event) {
        cancelEventAlarm(event, EventNotification.Type.AboutToStart);
        cancelEventAlarm(event, EventNotification.Type.FeedbackReminder);

        event.getUserInput().getEventFeedbackReminderNotification().setNotificationTime(null);
        event.getUserInput().getEventAboutToStartNotification().setNotificationTime(null);
        Convention.getInstance().getStorage().saveUserInput();
    }

    public void cancelEventAlarm(ConventionEvent event, EventNotification.Type notificationType) {
        Intent intent = new Intent(context, EventNotificationService.class)
                .setAction(notificationType.toString() + event.getId());
        PendingIntent pendingIntent = PendingIntent.getService(context, 0, intent, 0);

        alarmManager.cancel(pendingIntent);
    }

    public void scheduleNotificationToFillConventionFeedback() {
        Intent intent = new Intent(context, EventNotificationService.class)
                .putExtra(EventNotificationService.EXTRA_IS_END_OF_CONVENTION_NOTIFICATION, true);
        Calendar postConventionDate = Calendar.getInstance();
        postConventionDate.setTime(Convention.getInstance().getDate().getTime());
        postConventionDate.add(Calendar.DAY_OF_MONTH, 1);
        scheduleAlarm(postConventionDate.getTimeInMillis(), PendingIntent.getService(context, 0, intent, 0));
    }

    private PendingIntent createEventNotificationPendingIntent(ConventionEvent event, EventNotification.Type notificationType) {

        Intent intent = new Intent(context, EventNotificationService.class)
                .setAction(notificationType.toString() + event.getId()) // Setting unique action id so different event intents won't collide
                .putExtra(EventNotificationService.EXTRA_EVENT_TO_NOTIFY, event)
                .putExtra(EventNotificationService.EXTRA_EVENT_NOTIFICATION_TYPE, notificationType);
        return PendingIntent.getService(context, 0, intent, 0);
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void scheduleExactAlarmKitkat(long time, PendingIntent pendingIntent) {
        // For Kitkat and above, the AlarmService batches notifications to improve battery life at the cost of alarm accuracy.
        // Since event start notification time is important, schedule them using setExact, which bypass this optimization.
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, time, pendingIntent);
    }

    private void scheduleAlarm(long time, PendingIntent pendingIntent) {
        alarmManager.set(AlarmManager.RTC_WAKEUP, time, pendingIntent);
    }
}

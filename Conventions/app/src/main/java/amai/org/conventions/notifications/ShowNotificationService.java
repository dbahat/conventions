package amai.org.conventions.notifications;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;

import java.util.List;

import amai.org.conventions.ConventionsApplication;
import amai.org.conventions.FeedbackActivity;
import amai.org.conventions.HomeActivity;
import amai.org.conventions.ImageHandler;
import amai.org.conventions.R;
import amai.org.conventions.ThemeAttributes;
import amai.org.conventions.events.activities.EventActivity;
import amai.org.conventions.model.Convention;
import amai.org.conventions.model.ConventionEvent;
import amai.org.conventions.utils.CollectionUtils;

import static amai.org.conventions.model.EventNotification.Type.AboutToStart;
import static amai.org.conventions.model.EventNotification.Type.FeedbackReminder;

/**
 * Allow showing notifications using the Android NotificationManager
 */
public class ShowNotificationService extends Service {

    public static final String EXTRA_EVENT_TO_NOTIFY = "ExtraEventToNotify";
    public static final String EXTRA_NOTIFICATION_TYPE = "EXTRA_NOTIFICATION_TYPE";
    public static final String EXTRA_MESSAGE = "EXTRA_MESSAGE";

    private static final int FILL_CONVENTION_FEEDBACK_NOTIFICATION_ID = 91235;
	private static final int FILL_EVENTS_FEEDBACK_NOTIFICATION_ID = 95837;
    private static final String NEXT_PUSH_NOTIFICATION_ID = "NextPushNotificationId";
    private NotificationManager notificationManager;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

	    // Set the theme programatically because it's not attached by definition to the service's context.
	    // We need the theme to access image resources for the notification.
	    setTheme(getApplicationInfo().theme);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Object typeObj = intent.getSerializableExtra(EXTRA_NOTIFICATION_TYPE);
        if (typeObj != null && typeObj instanceof Type) {
            Type notificationType = (Type) typeObj;
            switch (notificationType) {
                case ConventionFeedbackReminder:
                    showFillConventionFeedbackNotification();
                    break;
                case EventAboutToStart:
                    showEventAboutToStartNotification(intent);
                    break;
                case EventFeedbackReminder:
                    showEventFeedbackReminderNotification(intent);
                    break;
                case Push:
                    showPushNotification(intent);
                    break;
            }
        }

        stopSelf();
        return START_NOT_STICKY;
    }

    private void showFillConventionFeedbackNotification() {

        // In case the user already sent convention feedback, no need to show him the notification
        if (Convention.getInstance().getFeedback().isSent()) {
            return;
        }

        Intent intent = new Intent(this, FeedbackActivity.class);
        Notification.Builder builder = getDefaultNotificationBuilder()
                .setContentTitle(getString(R.string.notification_event_ended_title))
                .setContentText(getString(R.string.notification_feedback_ended_message, Convention.getInstance().getDisplayName()))
                .setContentIntent(PendingIntent.getActivity(this, 0, intent, 0))
                .setDefaults(Notification.DEFAULT_VIBRATE);

        Notification notification = new Notification.BigTextStyle(builder)
                .bigText(getString(R.string.notification_feedback_ended_message, Convention.getInstance().getDisplayName()))
                .build();

        notificationManager.notify(FILL_CONVENTION_FEEDBACK_NOTIFICATION_ID, notification);

        ConventionsApplication.settings.setFeedbackNotificationAsShown();
    }

    private void showEventFeedbackReminderNotification(Intent intent) {
        final ConventionEvent event = (ConventionEvent) intent.getSerializableExtra(EXTRA_EVENT_TO_NOTIFY);
        if (event == null) {
            return;
        }

	    // Check it's still possible to send feedback
	    if (Convention.getInstance().isFeedbackSendingTimeOver()) {
		    return;
	    }

	    // Check the user didn't fill feedback on this event yet
	    if (event.getUserInput().getFeedback().isSent()) {
		    return;
	    }

	    // Check how many other events are waiting for feedback
	    List<ConventionEvent> otherEventsWithoutFeedback = CollectionUtils.filter(Convention.getInstance().getEvents(),
		    new CollectionUtils.Predicate<ConventionEvent>() {
			    @Override
			    public boolean where(ConventionEvent item) {
				    return item.shouldUserSeeFeedback() && !event.getId().equals(item.getId());
			    }
		    });

	    Intent openIntent;
	    String notificationText;
	    int otherEventsNumber = otherEventsWithoutFeedback.size();
	    if (otherEventsNumber == 0) {
	        openIntent = new Intent(this, EventActivity.class)
	            .setAction(FeedbackReminder.toString() + event.getId())
	            .putExtra(EventActivity.EXTRA_EVENT_ID, event.getId())
	            .putExtra(EventActivity.EXTRA_FOCUS_ON_FEEDBACK, true);
		    notificationText = getString(R.string.notification_event_ended_message_format, event.getTitle());
	    } else {
		    openIntent = new Intent(this, FeedbackActivity.class).setAction(FeedbackReminder.toString());
		    if (otherEventsNumber == 1) {
		        notificationText = getString(R.string.notification_2_events_ended_message_format, event.getTitle());
		    } else {
			    notificationText = getString(R.string.notification_several_events_ended_message_format, event.getTitle(), otherEventsNumber);
		    }
	    }

	    // A note about the intent action and update flag:
	    // If we want 2 different notifications to appear at the same time, both pointing to the same activity,
	    // the intent must have a different action (extras don't affect the equality check - see documentation of PendingIntent).
	    // In our case we either have a new intent each time (in case it's a a new event displayed) or the same intent of
	    // opening the FeedbackActivity. If it's the latter we should update it.
        Notification.Builder builder = getDefaultNotificationBuilder()
                .setContentTitle(getResources().getString(R.string.notification_event_ended_title))
                .setContentText(notificationText)
                .setPriority(Notification.PRIORITY_LOW)
                .setContentIntent(PendingIntent.getActivity(this, 0, openIntent, PendingIntent.FLAG_UPDATE_CURRENT));

        Notification notification = new Notification.BigTextStyle(builder)
                .bigText(notificationText)
                .build();

	    notificationManager.notify(FILL_EVENTS_FEEDBACK_NOTIFICATION_ID, notification);
    }

    private void showEventAboutToStartNotification(Intent intent) {

        ConventionEvent event = (ConventionEvent) intent.getSerializableExtra(EXTRA_EVENT_TO_NOTIFY);
        if (event == null) {
            return;
        }

        Intent openEventIntent = new Intent(this, EventActivity.class)
                .setAction(AboutToStart.toString() + event.getId())
                .putExtra(EventActivity.EXTRA_EVENT_ID, event.getId())
                .putExtra(EventActivity.EXTRA_FOCUS_ON_FEEDBACK, false);

        Notification.Builder builder = getDefaultNotificationBuilder()
                .setContentTitle(getResources().getString(R.string.notification_event_about_to_start_title))
                .setContentText(getEventAboutToStartNotificationText(event))
                .setDefaults(Notification.DEFAULT_VIBRATE)
                .setPriority(Notification.PRIORITY_HIGH)
                .setContentIntent(PendingIntent.getActivity(this, 0, openEventIntent, 0));

        Notification notification = new Notification.BigTextStyle(builder)
                .bigText(getEventAboutToStartNotificationText(event))
                .build();

        notificationManager.notify((AboutToStart.toString() + event.getId()).hashCode(), notification);
    }

    private void showPushNotification(Intent intent) {

        String message = intent.getStringExtra(EXTRA_MESSAGE);
        if (message == null) {
            return;
        }

        Intent openAppIntent = new Intent(this, HomeActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        Notification.Builder builder = getDefaultNotificationBuilder()
                .setContentTitle(getString(R.string.app_name)) // Showing the app name as title to be consistent with iOS behavior
                .setContentText(message)
                .setDefaults(Notification.DEFAULT_VIBRATE)
                .setPriority(Notification.PRIORITY_HIGH)
                .setContentIntent(PendingIntent.getActivity(this, 0, openAppIntent, 0));

        // Assign each push notification with a unique ID, so multiple notifications can display at the same time
        notificationManager.notify(getNextPushNotificationId(), builder.build());
    }

    private int getNextPushNotificationId() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        int currentId = sharedPreferences.getInt(NEXT_PUSH_NOTIFICATION_ID, 8000);
        int nextId = currentId + 1;
        sharedPreferences.edit().putInt(NEXT_PUSH_NOTIFICATION_ID, nextId).apply();

        return currentId;
    }

    private String getEventAboutToStartNotificationText(ConventionEvent event) {
        return getResources().getString(R.string.notification_event_about_to_start_message_format,
                event.getTitle(), event.getHall().getName());
    }

    private Notification.Builder getDefaultNotificationBuilder() {

        return new Notification.Builder(this)
                .setSmallIcon(ThemeAttributes.getResourceId(getBaseContext(), R.attr.notificationSmallIcon))
                .setLargeIcon(ImageHandler.getNotificationLargeIcon(this))
                .setContentTitle(getResources().getString(R.string.notification_event_about_to_start_title))
                .setAutoCancel(true);
    }

    public enum Type {
        EventAboutToStart,
        EventFeedbackReminder,
        ConventionFeedbackReminder,
        Push
    }
}

package amai.org.conventions.notifications;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.core.app.NotificationCompat;
import android.view.ContextThemeWrapper;

import java.util.List;

import amai.org.conventions.ConventionsApplication;
import amai.org.conventions.FeedbackActivity;
import amai.org.conventions.ThemeAttributes;
import amai.org.conventions.events.activities.EventActivity;
import amai.org.conventions.model.ConventionEvent;
import amai.org.conventions.model.conventions.Convention;
import amai.org.conventions.networking.UpdatesRefresher;
import amai.org.conventions.updates.UpdatesActivity;
import amai.org.conventions.utils.CollectionUtils;
import amai.org.conventions.utils.Log;
import sff.org.conventions.R;

import static amai.org.conventions.model.EventNotification.Type.AboutToStart;
import static amai.org.conventions.model.EventNotification.Type.FeedbackReminder;
import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * Responsible for showing notifications using the Android NotificationManager.
 * Implemented as a BroadcastReceiver and not a Service since starting Android 8, background services are placed with severe restrictions.
 */
public class ShowNotificationReceiver extends BroadcastReceiver {

    private static final String TAG = ShowNotificationReceiver.class.getSimpleName();

    public static final String EXTRA_EVENT_ID_TO_NOTIFY = "ExtraEventIdToNotify";
    public static final String EXTRA_NOTIFICATION_TYPE = "EXTRA_NOTIFICATION_TYPE";
    public static final String EXTRA_MESSAGE = "EXTRA_MESSAGE";
    public static final String EXTRA_CATEGORY = "EXTRA_CATEGORY";
    public static final String EXTRA_ID = "EXTRA_ID";

    private static final int FILL_CONVENTION_FEEDBACK_NOTIFICATION_ID = 91235;
    private static final int FILL_EVENTS_FEEDBACK_NOTIFICATION_ID = 95837;
    private static final String NEXT_PUSH_NOTIFICATION_ID = "NextPushNotificationId";
    private NotificationManager notificationManager;
    private Context context;

    @Override
    public void onReceive(Context context, Intent intent) {
        // Set the theme programmatically because it's not attached to the sent context.
        // We need the theme to access image resources for the notification.
        this.context = new ContextThemeWrapper(context, context.getApplicationInfo().theme);

        notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);

        // Note - Extras passed to this service must not contain any serialize-able objects.
        // Required since this service also get PendingIntents scheduled by the AlarmManager, which starting Android N won't pass the bundled parametered
        // of serializeables. See https://issuetracker.google.com/issues/37097877
        Bundle extras = intent.getExtras();
        if (!intent.hasExtra(EXTRA_NOTIFICATION_TYPE)) {
            Log.w(TAG, "Got a request to show notification without notification type extra. intent action:" + intent.getAction());
            return;
        }

        //noinspection ConstantConditions
        PushNotification.Type notificationType = Enum.valueOf(PushNotification.Type.class, extras.getString(EXTRA_NOTIFICATION_TYPE));
        Log.v(TAG, "Got a request to show notification of type " + notificationType.toString());
        switch (notificationType) {
            case ConventionFeedbackReminder:
                showFillConventionFeedbackNotification(false);
                break;
            case ConventionFeedbackLastChanceReminder:
                showFillConventionFeedbackNotification(true);
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

    private void showFillConventionFeedbackNotification(boolean lastChance) {

        // In case the user already sent convention feedback, no need to show him the notification
        if (Convention.getInstance().getFeedback().isSent()) {
            return;
        }

        Intent intent = new Intent(context, FeedbackActivity.class);
        String title = lastChance ? context.getString(R.string.notification_feedback_last_chance_title) : context.getString(R.string.notification_event_ended_title);
        String message = lastChance ? context.getString(R.string.notification_feedback_last_chance_message, Convention.getInstance().getDisplayName()) :
                context.getString(R.string.notification_feedback_ended_message, Convention.getInstance().getDisplayName());

        NotificationCompat.Builder builder = getDefaultNotificationBuilder(PushNotification.Type.ConventionFeedbackReminder.getChannel())
                .setContentTitle(title)
                .setContentText(message)
                .setContentIntent(PendingIntent.getActivity(context, 0, intent, 0))
                .setDefaults(Notification.DEFAULT_VIBRATE);

        Notification notification = new NotificationCompat.BigTextStyle(builder)
                .bigText(message)
                .build();

        notificationManager.notify(FILL_CONVENTION_FEEDBACK_NOTIFICATION_ID, notification);

        if (lastChance) {
            ConventionsApplication.settings.setLastChanceFeedbackNotificationAsShown();
        } else {
            ConventionsApplication.settings.setFeedbackNotificationAsShown();
        }
    }

    private void showEventFeedbackReminderNotification(Intent intent) {
        String eventId = intent.getStringExtra(EXTRA_EVENT_ID_TO_NOTIFY);
        final ConventionEvent event = Convention.getInstance().getEventById(eventId);
        if (event == null) {
            // This can happen if the event was deleted
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
            openIntent = new Intent(context, EventActivity.class)
                    .setAction(FeedbackReminder.toString() + event.getId())
                    .putExtra(EventActivity.EXTRA_EVENT_ID, event.getId())
                    .putExtra(EventActivity.EXTRA_FOCUS_ON_FEEDBACK, true);
            notificationText = context.getString(R.string.notification_event_ended_message_format, event.getTitle());
        } else {
            openIntent = new Intent(context, FeedbackActivity.class).setAction(FeedbackReminder.toString());
            if (otherEventsNumber == 1) {
                notificationText = context.getString(R.string.notification_2_events_ended_message_format, event.getTitle());
            } else {
                notificationText = context.getString(R.string.notification_several_events_ended_message_format, event.getTitle(), otherEventsNumber);
            }
        }

        // A note about the intent action and update flag:
        // If we want 2 different notifications to appear at the same time, both pointing to the same activity,
        // the intent must have a different action (extras don't affect the equality check - see documentation of PendingIntent).
        // In our case we either have a new intent each time (in case it's a a new event displayed) or the same intent of
        // opening the FeedbackActivity. If it's the latter we should update it.
        NotificationCompat.Builder builder = getDefaultNotificationBuilder(PushNotification.Type.EventFeedbackReminder.getChannel())
                .setContentTitle(context.getResources().getString(R.string.notification_event_ended_title))
                .setContentText(notificationText)
                .setPriority(Notification.PRIORITY_LOW)
                .setContentIntent(PendingIntent.getActivity(context, 0, openIntent, PendingIntent.FLAG_UPDATE_CURRENT));

        Notification notification = new NotificationCompat.BigTextStyle(builder)
                .bigText(notificationText)
                .build();

        notificationManager.notify(FILL_EVENTS_FEEDBACK_NOTIFICATION_ID, notification);
    }

    private void showEventAboutToStartNotification(Intent intent) {

        String eventId = intent.getStringExtra(EXTRA_EVENT_ID_TO_NOTIFY);
        ConventionEvent event = Convention.getInstance().getEventById(eventId);
        if (event == null) {
            // This can happen if the event was deleted
            Log.v(TAG, "Couldn't find event " + eventId + ". Ignoring.");
            return;
        }

        Log.v(TAG, "Showing event about to start notification for event " + eventId);

        Intent openEventIntent = new Intent(context, EventActivity.class)
                .setAction(AboutToStart.toString() + event.getId())
                .putExtra(EventActivity.EXTRA_EVENT_ID, event.getId())
                .putExtra(EventActivity.EXTRA_FOCUS_ON_FEEDBACK, false);

        NotificationCompat.Builder builder = getDefaultNotificationBuilder(PushNotification.Type.EventAboutToStart.getChannel())
                .setContentTitle(context.getResources().getString(R.string.notification_event_about_to_start_title))
                .setContentText(getEventAboutToStartNotificationText(event))
                .setDefaults(Notification.DEFAULT_VIBRATE)
                .setPriority(Notification.PRIORITY_HIGH)
                .setContentIntent(PendingIntent.getActivity(context, 0, openEventIntent, 0));

        Notification notification = new NotificationCompat.BigTextStyle(builder)
                .bigText(getEventAboutToStartNotificationText(event))
                .build();

        notificationManager.notify((AboutToStart.toString() + event.getId()).hashCode(), notification);
        Log.v(TAG, "Notification " + AboutToStart.toString() + event.getId() + " created successfully");
    }

    public static Intent createIntentForNotification(Context from, String messageId, String message, String category) {
        return new Intent(from, UpdatesActivity.class)
                .setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
                .putExtra(PushNotificationDialogPresenter.EXTRA_PUSH_NOTIFICATION,
                        // The notification id is used to prevent seeing the same notification twice
                        new PushNotification(getNextPushNotificationId(), messageId, message, category));
    }

    private void showPushNotification(Intent intent) {
        String message = intent.getStringExtra(EXTRA_MESSAGE);
        if (message == null) {
            return;
        }
        String category = intent.getStringExtra(EXTRA_CATEGORY); // Could be null
        String messageId = intent.getStringExtra(EXTRA_ID); // Could be null

        Intent openAppIntent = createIntentForNotification(context, messageId, message, category);
        PushNotification notification = (PushNotification) openAppIntent.getSerializableExtra(PushNotificationDialogPresenter.EXTRA_PUSH_NOTIFICATION);
        // The action is necessary to ensure a new pending intent is created instead of re-used
        openAppIntent.setAction(PushNotification.Type.Push.toString() + messageId + "_" + notification.id);

        NotificationCompat.Builder builder = getDefaultNotificationBuilder(PushNotification.Type.Push.getChannel())
                .setContentTitle(context.getString(R.string.app_name)) // Showing the app name as title to be consistent with iOS behavior
                .setContentText(message)
                .setDefaults(Notification.DEFAULT_VIBRATE)
                .setPriority(Notification.PRIORITY_HIGH)
                .setContentIntent(PendingIntent.getActivity(context, 0, openAppIntent, 0));

        // Assign each push notification with a unique ID, so multiple notifications can display at the same time
        notificationManager.notify(notification.id, builder.build());

        // Retrieve new updates so this message will appear in the updates screen
        UpdatesRefresher.getInstance(context).refreshFromServer(false, true, new UpdatesRefresher.OnUpdateFinishedListener() {
            @Override
            public void onSuccess(int newUpdatesNumber) {
            }

            @Override
            public void onError(Exception error) {
            }
        });
    }

    private static int getNextPushNotificationId() {
        SharedPreferences sharedPreferences = ConventionsApplication.settings.getSharedPreferences();
        int currentId = sharedPreferences.getInt(NEXT_PUSH_NOTIFICATION_ID, 8000);
        int nextId = currentId + 1;
        sharedPreferences.edit().putInt(NEXT_PUSH_NOTIFICATION_ID, nextId).apply();

        return currentId;
    }

    private String getEventAboutToStartNotificationText(ConventionEvent event) {
        return context.getResources().getString(R.string.notification_event_about_to_start_message_format,
                event.getTitle(), event.getHall().getName());
    }

    private NotificationCompat.Builder getDefaultNotificationBuilder(PushNotification.Channel channel) {
        return new NotificationCompat.Builder(context, channel.toString())
                .setSmallIcon(ThemeAttributes.getResourceId(context, R.attr.notificationSmallIcon))
                .setAutoCancel(true);
    }

}

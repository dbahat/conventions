package amai.org.conventions;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.TypedValue;

import amai.org.conventions.events.activities.EventActivity;
import amai.org.conventions.model.ConventionEvent;
import amai.org.conventions.model.EventNotification;

import static amai.org.conventions.model.EventNotification.Type.AboutToStart;
import static amai.org.conventions.model.EventNotification.Type.FeedbackReminder;

public class EventNotificationService extends Service {

    public static final String EXTRA_EVENT_TO_NOTIFY = "ExtraEventToNotify";
    public static final String EXTRA_EVENT_NOTIFICATION_TYPE = "ExtraEventNotificationType";

    private NotificationManager notificationManager;
	private static Bitmap largeIcon; // Cache for only creating the icon once

	@Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        ConventionEvent event = (ConventionEvent) intent.getSerializableExtra(EXTRA_EVENT_TO_NOTIFY);
        EventNotification.Type notificationType = (EventNotification.Type) intent.getSerializableExtra(EXTRA_EVENT_NOTIFICATION_TYPE);
        if(event != null && notificationType != null) {
            switch (notificationType) {
                case AboutToStart:
                    showEventAboutToStartNotification(event);
                    break;
                case FeedbackReminder:
                    showEventFeedbackReminderNotification(event);
                    break;
            }
        }

        stopSelf();

        return START_NOT_STICKY;
    }

    private void showEventFeedbackReminderNotification(ConventionEvent event) {
        Intent intent = new Intent(this, EventActivity.class)
                .setAction(FeedbackReminder.toString() + event.getId())
                .putExtra(EventActivity.EXTRA_EVENT_ID, event.getId())
                .putExtra(EventActivity.EXTRA_FOCUS_ON_FEEDBACK, true);

        Notification.Builder builder = new Notification.Builder(this)
                .setSmallIcon(R.drawable.cami_logo_small_white)
		        .setLargeIcon(getNotificationLargeIcon())
		        .setContentTitle(getResources().getString(R.string.notification_event_ended_title))
                .setContentText(getString(R.string.notification_event_ended_message_format, event.getTitle()))
                .setAutoCancel(true)
                .setContentIntent(PendingIntent.getActivity(this, 0, intent, 0));

        Notification notification = new Notification.BigTextStyle(builder)
                .bigText(getString(R.string.notification_event_ended_message_format, event.getTitle()))
                .build();

        notificationManager.notify((FeedbackReminder.toString() + event.getId()).hashCode(), notification);
    }

    private void showEventAboutToStartNotification(ConventionEvent event) {

        Intent intent = new Intent(this, EventActivity.class)
                .setAction(AboutToStart.toString() + event.getId())
                .putExtra(EventActivity.EXTRA_EVENT_ID, event.getId())
                .putExtra(EventActivity.EXTRA_FOCUS_ON_FEEDBACK, false);

        Notification.Builder builder = new Notification.Builder(this)
                .setSmallIcon(R.drawable.cami_logo_small_white)
		        .setLargeIcon(getNotificationLargeIcon())
                .setContentTitle(getResources().getString(R.string.notification_event_about_to_start_title))
                .setContentText(getEventAboutToStartNotificationText(event))
                .setDefaults(Notification.DEFAULT_VIBRATE)
                .setPriority(Notification.PRIORITY_MAX)
                .setAutoCancel(true)
                .setContentIntent(PendingIntent.getActivity(this, 0, intent, 0));

        Notification notification = new Notification.BigTextStyle(builder)
                .bigText(getEventAboutToStartNotificationText(event))
                .build();

        notificationManager.notify((AboutToStart.toString() + event.getId()).hashCode(), notification);
    }

    private String getEventAboutToStartNotificationText(ConventionEvent event) {
        return getResources().getString(R.string.notification_event_about_to_start_message_format,
                event.getTitle(), event.getHall().getName());
    }

	private synchronized Bitmap getNotificationLargeIcon() {
		if (largeIcon != null) {
			return largeIcon;
		}
		largeIcon = resizeBitmap(getResources().getDrawable(R.drawable.cami_logo_app_icon), 64);
		return largeIcon;
	}

	private Bitmap resizeBitmap(Drawable image, int heightInDp) {
		int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, heightInDp, getResources().getDisplayMetrics());
		Bitmap originalBitmap = ((BitmapDrawable) image).getBitmap();
		int width = (int) (originalBitmap.getWidth() * height / (float) originalBitmap.getHeight());
		return Bitmap.createScaledBitmap(originalBitmap, width, height, false);
	}

	public synchronized static void releaseCache() {
		largeIcon = null;
	}
}

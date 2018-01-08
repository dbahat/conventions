package amai.org.conventions.notifications;

import android.content.Intent;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class PushNotificationHandlingService extends FirebaseMessagingService {
    private static final String TAG = PushNotificationTopic.class.getSimpleName();

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        Log.d(TAG, "got push notification message from " + remoteMessage.getFrom());
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
            startService(new Intent(this, ShowNotificationService.class)
                    .putExtra(ShowNotificationService.EXTRA_NOTIFICATION_TYPE, ShowNotificationService.Type.Push)
                    .putExtra(ShowNotificationService.EXTRA_MESSAGE, remoteMessage.getNotification().getBody())
            );
        }
    }
}

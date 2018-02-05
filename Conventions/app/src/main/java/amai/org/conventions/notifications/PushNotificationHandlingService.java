package amai.org.conventions.notifications;

import android.content.Intent;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class PushNotificationHandlingService extends FirebaseMessagingService {
    private static final String TAG = PushNotificationTopic.class.getSimpleName();

    // optional extra parameters in the remote message payload for showing the message / topic. Expected to be added to the firebase message data payload.
    public static final String TOPIC = "topic";
    public static final String MESSAGE = "message";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        Log.d(TAG, "got push notification message from " + remoteMessage.getFrom());
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
            Intent intent = new Intent(this, ShowNotificationService.class)
                    .putExtra(ShowNotificationService.EXTRA_NOTIFICATION_TYPE, ShowNotificationService.Type.Push)
                    .putExtra(ShowNotificationService.EXTRA_MESSAGE, remoteMessage.getNotification().getBody());

            if (remoteMessage.getData() != null && remoteMessage.getData().containsKey(TOPIC)) {
                intent.putExtra(ShowNotificationService.EXTRA_CATEGORY, remoteMessage.getData().get(TOPIC));
            }

            startService(intent);
        }
    }
}

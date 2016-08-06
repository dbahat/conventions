package amai.org.conventions.notifications;

import android.app.IntentService;
import android.content.Intent;

/**
 * Handles registration of the GCM token with the Azure notification hub service.
 * See https://azure.microsoft.com/en-us/documentation/articles/notification-hubs-android-get-started/
 */
public class AzureNotificationRegistrationService extends IntentService {
    private static final String TAG = AzureNotificationRegistrationService.class.getSimpleName();

    public AzureNotificationRegistrationService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        AzurePushNotifications notifications = new AzurePushNotifications(this);
        if (!notifications.isRegistered()) {
	        try {
		        notifications.register();
	        } catch (Exception e) {
		        // Nothing we can do about it from here... we'll try again next time
	        }
        }
    }
}

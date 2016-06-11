package amai.org.conventions.notifications;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import com.microsoft.windowsazure.messaging.NotificationHub;

import amai.org.conventions.utils.Log;

/**
 * Handles registration of the GCM token with the Azure notification hub service.
 * See https://azure.microsoft.com/en-us/documentation/articles/notification-hubs-android-get-started/
 */
public class AzureNotificationRegistrationService extends IntentService {
    public static final String EXTRA_FORCE_REGISTRATION = AzureNotificationRegistrationService.class.getPackage().getName() + ".EXTRA_FORCE_REGISTRATION";

    private static final String TAG = AzureNotificationRegistrationService.class.getSimpleName();
    private static final String REGISTRATION_ID = "registrationID";

    public AzureNotificationRegistrationService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        try {
            InstanceID instanceID = InstanceID.getInstance(this);
            String token = instanceID.getToken(PushNotificationSettings.SENDER_ID,
                    GoogleCloudMessaging.INSTANCE_ID_SCOPE);
            Log.i(TAG, "Got GCM Registration Token: " + token);

            // Storing the registration id that indicates whether the generated token has been
            // sent to your server. If it is not stored, send the token to your server,
            // otherwise your server should have already received the token.
            String regID = sharedPreferences.getString(REGISTRATION_ID, null);
            if (regID == null || intent.getBooleanExtra(EXTRA_FORCE_REGISTRATION, false)) {
                NotificationHub hub = new NotificationHub(PushNotificationSettings.HUB_NAME,
                        PushNotificationSettings.HUB_LISTEN_CONNECTION_STRING, this);
                Log.i(TAG, "Attempting to register with NH using token : " + token);

                regID = hub.register(token).getRegistrationId();

                // If you want to use tags...
                // Refer to : https://azure.microsoft.com/en-us/documentation/articles/notification-hubs-routing-tag-expressions/
                // regID = hub.register(token, "tag1,tag2").getRegistrationId();

                Log.i(TAG, "Registered Successfully - RegId : " + regID);
                sharedPreferences.edit().putString(REGISTRATION_ID, regID).apply();
            }
        } catch (Exception e) {
            Log.e(TAG, "Failed to complete token refresh", e);
            // If an exception happens while fetching the new token or updating our registration data
            // on a third-party server, this ensures that we'll attempt the update at a later time.
        }
    }
}

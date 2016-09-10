package amai.org.conventions.notifications;

import android.content.Intent;

import com.google.firebase.iid.FirebaseInstanceIdService;

import amai.org.conventions.utils.Log;

public class GcmTokenRefreshService extends FirebaseInstanceIdService {

    private static final String TAG = GcmTokenRefreshService.class.getSimpleName();

    @Override
    public void onTokenRefresh() {
        Log.i(TAG, "Refreshing GCM Registration Token");

	    // If the token is expired we will no longer receive notifications
	    new AzurePushNotifications(this).setRegistered(false);

	    // Register in background thread
        Intent intent = new Intent(this, AzureNotificationRegistrationService.class);
        startService(intent);
    }
}

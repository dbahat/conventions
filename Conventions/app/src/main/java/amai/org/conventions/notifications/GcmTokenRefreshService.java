package amai.org.conventions.notifications;

import android.content.Intent;

import com.google.android.gms.iid.InstanceIDListenerService;

import amai.org.conventions.utils.Log;

public class GcmTokenRefreshService extends InstanceIDListenerService {

    private static final String TAG = GcmTokenRefreshService.class.getSimpleName();

    @Override
    public void onTokenRefresh() {
        Log.i(TAG, "Refreshing GCM Registration Token");
        Intent intent = new Intent(this, AzureNotificationRegistrationService.class)
                // Force the registration service to re-register using the new token
                .putExtra(AzureNotificationRegistrationService.EXTRA_FORCE_REGISTRATION, true);
        startService(intent);
    }
}

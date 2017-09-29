package amai.org.conventions.notifications;

import android.content.Intent;

import com.google.firebase.iid.FirebaseInstanceIdService;

import amai.org.conventions.utils.Log;

public class GcmTokenRefreshService extends FirebaseInstanceIdService {

	private static final String TAG = GcmTokenRefreshService.class.getSimpleName();

	@Override
	public void onTokenRefresh() {
		Log.i(TAG, "Refreshing GCM Registration Token");

		AzurePushNotifications notifications = new AzurePushNotifications(this);
		try {
			notifications.register();
		} catch (Exception e) {
			Log.e(TAG, "failed to register. error: " + android.util.Log.getStackTraceString(e));
		}
	}
}

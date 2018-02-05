package amai.org.conventions;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import amai.org.conventions.navigation.NavigationActivity;
import amai.org.conventions.notifications.PushNotificationDialogPresenter;
import amai.org.conventions.notifications.PushNotificationHandlingService;

/**
 * Splash activity used only when opening the application to show a default background and start
 * the next activity
 */
public class SplashActivity extends AppCompatActivity {
	public final static String EXTRA_FINISH = "Finish";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (getIntent().getBooleanExtra(EXTRA_FINISH, false)) {
			finish();
			return;
		}

		Intent intent = new Intent(SplashActivity.this, HomeActivity.class)
				.putExtra(NavigationActivity.EXTRA_INITIALIZE, true)
				.putExtra(NavigationActivity.EXTRA_EXIT_ON_BACK, true);

		if (getIntent().hasExtra("message") && getIntent().hasExtra("topic")) {
			intent
					.putExtra(PushNotificationDialogPresenter.EXTRA_PUSH_NOTIFICATION_MESSAGE, getIntent().getStringExtra(PushNotificationHandlingService.MESSAGE))
					.putExtra(PushNotificationDialogPresenter.EXTRA_PUSH_NOTIFICATION_CATEGORY, getIntent().getStringExtra(PushNotificationHandlingService.TOPIC));
		}

		startActivity(intent);

		// Important - don't call finish() here because for some reason it causes a black screen
		// to appear when navigating between NavigationActivities. This activity must remain in the back
		// stack until we exit the application.
	}
}

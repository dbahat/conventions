package amai.org.conventions;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import amai.org.conventions.navigation.NavigationActivity;
import amai.org.conventions.notifications.PushNotification;
import amai.org.conventions.notifications.PushNotificationDialogPresenter;
import amai.org.conventions.updates.UpdatesActivity;

import static amai.org.conventions.notifications.PushNotificationHandlingService.ID;
import static amai.org.conventions.notifications.PushNotificationHandlingService.MESSAGE;
import static amai.org.conventions.notifications.PushNotificationHandlingService.TOPIC;

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

		if (getIntent().hasExtra(MESSAGE) && getIntent().hasExtra(TOPIC) && getIntent().hasExtra(ID)) {
			Intent intent = new Intent(SplashActivity.this, UpdatesActivity.class)
					.putExtra(PushNotificationDialogPresenter.EXTRA_PUSH_NOTIFICATION, new PushNotification(
							0,
							getIntent().getStringExtra(ID),
							getIntent().getStringExtra(MESSAGE),
							getIntent().getStringExtra(TOPIC)
					));
			startActivity(intent);
			return;
		}


		Intent intent = new Intent(SplashActivity.this, HomeActivity.class)
				.putExtra(NavigationActivity.EXTRA_INITIALIZE, true)
				.putExtra(NavigationActivity.EXTRA_EXIT_ON_BACK, true);
		startActivity(intent);

		// Important - don't call finish() here because for some reason it causes a black screen
		// to appear when navigating between NavigationActivities. This activity must remain in the back
		// stack until we exit the application.
	}
}

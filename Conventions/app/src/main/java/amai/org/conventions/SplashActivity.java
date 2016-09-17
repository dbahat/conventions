package amai.org.conventions;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import amai.org.conventions.events.activities.ProgrammeActivity;
import amai.org.conventions.navigation.NavigationActivity;

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

		new ApplicationInitializer().initialize(this.getApplicationContext());

		Intent intent = new Intent(SplashActivity.this, ProgrammeActivity.class);
		Bundle extras = new Bundle();
		extras.putBoolean(NavigationActivity.EXTRA_EXIT_ON_BACK, true);
		intent.putExtras(extras);
		startActivity(intent);
		// Important - don't call finish() here because for some reason it causes a black screen
		// to appear when navigating between NavigationActivities. This activity must remain in the back
		// stack until we exit the application.
	}
}

package amai.org.conventions;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import amai.org.conventions.events.activities.ProgrammeActivity;

/**
 * Splash activity used only when opening the application to show a default background and start
 * the next activity
 */
public class SplashActivity extends AppCompatActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

        new ApplicationInitializer().initialize(this);

		Intent intent = new Intent(SplashActivity.this, ProgrammeActivity.class);
		startActivity(intent);
		finish();
	}
}

package amai.org.conventions;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import amai.org.conventions.events.activities.ProgrammeActivity;
import amai.org.conventions.navigation.NavigationActivity;


public class FeedbackActivity extends NavigationActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentInContentContainer(R.layout.activity_feedback);
		setToolbarTitle(getString(R.string.feedback));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_feedback, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.feedback_navigate_to_programme:
				navigateToActivity(ProgrammeActivity.class);

				return true;
		}

		return super.onOptionsItemSelected(item);
	}
}

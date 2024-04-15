package amai.org.conventions;

import amai.org.conventions.navigation.NavigationActivity;
import amai.org.conventions.utils.Views;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.ViewGroup;
import android.widget.TextView;

import sff.org.conventions.R;

public class ActivitiesActivity extends NavigationActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentInContentContainer(R.layout.activity_activities);
		setToolbarTitle(getString(R.string.discounts));

		handleLinks();
	}

	private void handleLinks() {
		ViewGroup contentContainer = findViewById(R.id.activities_content_container);
		if (contentContainer == null) {
			return;
		}

		Views.enableLinkClicks(contentContainer);
	}
}
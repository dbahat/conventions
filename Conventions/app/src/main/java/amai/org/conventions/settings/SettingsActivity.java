package amai.org.conventions.settings;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;

import java.util.Date;

import amai.org.conventions.ConventionsApplication;
import amai.org.conventions.R;
import amai.org.conventions.navigation.NavigationActivity;
import amai.org.conventions.notifications.PlayServicesInstallation;
import amai.org.conventions.notifications.PushNotificationTopic;
import amai.org.conventions.notifications.PushNotificationTopicsSubscriber;
import amai.org.conventions.utils.Dates;

public class SettingsActivity extends NavigationActivity {
	private static final int NUMBER_OF_CLICKS_TO_OPEN_ADVANCED_OPTIONS = 7;
	private static final int MAX_MILLISECONDS_TO_OPEN_ADVANCED_OPTIONS = 20000;
	private int numberOfTimesNavigationButtonClicked = 0;
	private Date firstClickTime = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentInContentContainer(R.layout.activity_settings);
		setToolbarTitle(getString(R.string.settings));
	}

	@Override
	protected void onResume() {
		super.onResume();
		numberOfTimesNavigationButtonClicked = 0;
		firstClickTime = null;
	}

	@Override
	protected void onNavigationButtonClicked() {
		if (ConventionsApplication.settings.isAdvancedOptionsEnabled()) {
			return;
		}

		if (firstClickTime == null || (Dates.now().getTime() - firstClickTime.getTime() > MAX_MILLISECONDS_TO_OPEN_ADVANCED_OPTIONS)) {
			// First click (or too much time passed since the last click)
			numberOfTimesNavigationButtonClicked = 1;
			firstClickTime = Dates.now();
		} else {
			++numberOfTimesNavigationButtonClicked;
			if (numberOfTimesNavigationButtonClicked >= NUMBER_OF_CLICKS_TO_OPEN_ADVANCED_OPTIONS) {
				showAdvancedOptions();
			}
		}
	}

	private void showAdvancedOptions() {
		ConventionsApplication.settings.setAdvancedOptionsEnabled(true);
		Toast.makeText(this, R.string.advanced_options_enabled, Toast.LENGTH_SHORT).show();
		recreate();
	}

	public static class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			// Save the preferences in the convention's file
			getPreferenceManager().setSharedPreferencesName(ConventionsApplication.settings.getSharedPreferencesName());
			addPreferencesFromResource(R.xml.settings_preferences);
			setupPreferences(); // This can only be done after loading the preferences
		}

		@Override
		public void onAttach(Activity activity) {
			super.onAttach(activity);
			SharedPreferences sharedPreferences = ConventionsApplication.settings.getSharedPreferences();
			sharedPreferences.registerOnSharedPreferenceChangeListener(this);
		}

		@Override
		public void onDetach() {
			super.onDetach();
			SharedPreferences sharedPreferences = ConventionsApplication.settings.getSharedPreferences();
			sharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
		}

		private void setupPreferences() {
			// Show/hide advanced options
			if (!ConventionsApplication.settings.isAdvancedOptionsEnabled()) {
				Preference advanced = findPreference("advanced");
				getPreferenceScreen().removePreference(advanced);
			}

			// Disable push notification settings if GCM is not available
			if (getActivity() == null) {
				return;
			}
			PlayServicesInstallation.CheckResult checkResult = PlayServicesInstallation.checkPlayServicesExist(getActivity(), true);
			if (!checkResult.isSuccess()) {
				Preference pushSettings = findPreference("push_notifications");
				if (pushSettings != null) {
					pushSettings.setEnabled(false);
				}
				Preference advanced = findPreference("advanced");
				if (advanced != null) {
					advanced.setEnabled(false);
				}
			}
		}

		@Override
		public void onSharedPreferenceChanged(final SharedPreferences sharedPreferences, final String key) {
			PushNotificationTopic topic = PushNotificationTopic.getByTopic(key);
			if (topic != null) {
				final boolean isSelected = sharedPreferences.getBoolean(key, false);
				ConventionsApplication.sendTrackingEvent(new HitBuilders.EventBuilder()
						.setCategory("Notifications")
						.setAction("Category" + (isSelected ? "Added" : "Removed"))
						.setLabel(key)
						.setValue(1) // succeeded
						.build());

				if (isSelected) {
					PushNotificationTopicsSubscriber.subscribe(topic);
				} else {
					PushNotificationTopicsSubscriber.unsubscribe(topic);
				}
			} else {
				if (findPreference(key) != null) {
					final boolean isSelected = sharedPreferences.getBoolean(key, false);
					ConventionsApplication.sendTrackingEvent(new HitBuilders.EventBuilder()
							.setCategory("Notifications")
							.setAction("Preference" + (isSelected ? "Selected" : "Deselected"))
							.setLabel(key)
							.build());
				}
			}
		}
	}
}

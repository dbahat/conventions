package amai.org.conventions.settings;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.widget.Toast;

import java.util.Date;

import amai.org.conventions.R;
import amai.org.conventions.navigation.NavigationActivity;
import amai.org.conventions.notifications.AzurePushNotifications;
import amai.org.conventions.notifications.PlayServicesInstallation;
import amai.org.conventions.utils.Dates;

public class SettingsActivity extends NavigationActivity {
	private int NUMBER_OF_CLICKS_TO_OPEN_ADVANCED_OPTIONS = 7;
	private int MAX_MILLISECONDS_TO_OPEN_ADVANCED_OPTIONS = 20000;
	private boolean advancedOptionsEnabled = false;
	private int numberOfTimesNavigationButtonClicked = 0;
	private Date firstClickTime = null;
	private AzurePushNotifications notifications;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentInContentContainer(R.layout.activity_settings);
		setToolbarTitle(getString(R.string.settings));
		notifications = new AzurePushNotifications(this);
		advancedOptionsEnabled = notifications.isAdvancedOptionsEnabled();
	}

	@Override
	protected void onResume() {
		super.onResume();
		numberOfTimesNavigationButtonClicked = 0;
		firstClickTime = null;
	}

	@Override
	protected void onNavigationButtonClicked() {
		if (advancedOptionsEnabled) {
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
		advancedOptionsEnabled = true;
		notifications.setAdvancedOptionsEnabled(true);
		Toast.makeText(this, R.string.advanced_options_enabled, Toast.LENGTH_SHORT).show();
		recreate();
	}

	public static class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {
		private AzurePushNotifications notifications;

		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			addPreferencesFromResource(R.xml.settings_preferences);
			setupPreferences(); // This can only be done after loading the preferences
		}

		@Override
		public void onAttach(Activity activity) {
			super.onAttach(activity);
			notifications = new AzurePushNotifications(activity);
			SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
			sharedPreferences.registerOnSharedPreferenceChangeListener(this);
		}

		@Override
		public void onDetach() {
			super.onDetach();
			SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
			sharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
		}

		private void setupPreferences() {
			// Show/hide advanced options
			if (!notifications.isAdvancedOptionsEnabled()) {
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
			if (isPushNotificationTopic(key)) {
				final boolean isSelected = sharedPreferences.getBoolean(key, false);
				notifications.registerAsync(new AzurePushNotifications.RegistrationListener() {
					@Override
					public void onSuccess() {
					}

					@Override
					public void onError() {
						// Show error
						Toast.makeText(getActivity(), R.string.push_registration_failed, Toast.LENGTH_LONG).show();

						// Revert (while not listening to changes to prevent infinite loop)
						sharedPreferences.unregisterOnSharedPreferenceChangeListener(SettingsFragment.this);
						sharedPreferences.edit().putBoolean(key, !isSelected).apply();
						sharedPreferences.registerOnSharedPreferenceChangeListener(SettingsFragment.this);

						// Update checkbox
						Preference preference = SettingsFragment.this.findPreference(key);
						if (preference instanceof CheckBoxPreference) {
							((CheckBoxPreference) preference).setChecked(!isSelected);
						}
					}
				});
			}
		}

		private boolean isPushNotificationTopic(String key) {
			for (AzurePushNotifications.PushNotificationTopic value : AzurePushNotifications.PushNotificationTopic.values()) {
				if (value.getTopic().equals(key)) {
					return true;
				}
			}
			return false;
		}
	}
}

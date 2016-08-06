package amai.org.conventions.settings;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.widget.Toast;

import amai.org.conventions.R;
import amai.org.conventions.navigation.NavigationActivity;
import amai.org.conventions.notifications.AzurePushNotifications;
import amai.org.conventions.notifications.PlayServicesInstallation;

public class SettingsActivity extends NavigationActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentInContentContainer(R.layout.activity_settings);
		setToolbarTitle(getString(R.string.settings));
	}

	public static class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {
		private AzurePushNotifications notifications;

		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			addPreferencesFromResource(R.xml.settings_preferences);
			disablePushNotificationPreferencesIfNeeded(); // We have the preferences here but not necessarily the activity
		}

		@Override
		public void onAttach(Activity activity) {
			super.onAttach(activity);
			notifications = new AzurePushNotifications(activity);
			SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
			sharedPreferences.registerOnSharedPreferenceChangeListener(this);
			disablePushNotificationPreferencesIfNeeded(); // We have the activity here but not necessarily the preferences
		}

		@Override
		public void onDetach() {
			super.onDetach();
			SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
			sharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
		}

		private void disablePushNotificationPreferencesIfNeeded() {
			if (getActivity() == null) {
				return;
			}
			// Disable push notification settings if GCM is not available
			PlayServicesInstallation.CheckResult checkResult = PlayServicesInstallation.checkPlayServicesExist(getActivity(), true);
			if (!checkResult.isSuccess()) {
				Preference pushSettings = findPreference("push_notifications");
				if (pushSettings != null) {
					pushSettings.setEnabled(false);
				}
			}
		}

		@Override
		public void onSharedPreferenceChanged(final SharedPreferences sharedPreferences, final String key) {
			if (key.equals(AzurePushNotifications.TOPIC_GENERAL) ||
					key.equals(AzurePushNotifications.TOPIC_EVENTS) ||
					key.equals(AzurePushNotifications.TOPIC_COSPLAY) ||
					key.equals(AzurePushNotifications.TOPIC_BUS)) {
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
	}
}

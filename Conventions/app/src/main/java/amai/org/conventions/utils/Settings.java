package amai.org.conventions.utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import amai.org.conventions.R;
import amai.org.conventions.model.conventions.Convention;
import amai.org.conventions.notifications.PushNotificationTopic;
import androidx.preference.PreferenceManager;

public class Settings {
	public static final int NO_PUSH_NOTIFICATION_SEEN_NOTIFICATION_ID = -1;

	private static final String SETTINGS_SUFFIX = "settings";
	private static final String PREFERENCES_FILE_NAME = Convention.getInstance().getId() + "_" + SETTINGS_SUFFIX;
	private static final String WAS_FEEDBACK_NOTIFICATION_SHOWN = "WasFeedbackNotificationShown";
	private static final String WAS_LAST_CHANCE_FEEDBACK_NOTIFICATION_SHOWN = "WasLastChanceFeedbackNotificationShown";
	private static final String WAS_PLAY_SERVICES_INSTALLATION_CANCELLED = "WasPlayServicesInstallationCancelled";
	private static final String WAS_SETTINGS_POPUP_DISPLAYED = "WasSettingsPopupDisplayed";
	private static final String LAST_SEEN_PUSH_NOTIFICATION_ID = "LastSeenPushNotificationId";
	private static final String LAST_EVENTS_UPDATE_DATE = "LastEventsUpdateDate";
	private static final String LAST_UPDATES_UPDATE_DATE = "LastUpdatesUpdateDate";
	private static final String IS_ADVANCED_OPTIONS_ENABLED = "isAdvancedOptionsEnabled";

	private final SharedPreferences sharedPreferences;

	public Settings(Context context) {
		// readAgain=true so that the default values will be set when the convention id changes
		PreferenceManager.setDefaultValues(context, getSharedPreferencesName(), Context.MODE_PRIVATE, R.xml.settings_preferences, true);
		sharedPreferences = context.getSharedPreferences(getSharedPreferencesName(), Context.MODE_PRIVATE);
	}

	public String getSharedPreferencesName() {
		return PREFERENCES_FILE_NAME;
	}

	public SharedPreferences getSharedPreferences() {
		return sharedPreferences;
	}

	public List<PushNotificationTopic> getNotificationTopics() {
		return CollectionUtils.filter(
				Arrays.asList(PushNotificationTopic.values()),
				new CollectionUtils.Predicate<PushNotificationTopic>() {
					@Override
					public boolean where(PushNotificationTopic item) {
						return item == PushNotificationTopic.TOPIC_EMERGENCY || sharedPreferences.getBoolean(item.getTopic(), false);
					}
				});
	}

	public boolean wasConventionFeedbackNotificationShown() {
		return sharedPreferences.getBoolean(WAS_FEEDBACK_NOTIFICATION_SHOWN, false);
	}

	public void setFeedbackNotificationAsShown() {
		sharedPreferences.edit().putBoolean(WAS_FEEDBACK_NOTIFICATION_SHOWN, true).apply();
	}

	public boolean wasConventionLastChanceFeedbackNotificationShown() {
		return sharedPreferences.getBoolean(WAS_LAST_CHANCE_FEEDBACK_NOTIFICATION_SHOWN, false);
	}

	public void setLastChanceFeedbackNotificationAsShown() {
		sharedPreferences.edit().putBoolean(WAS_LAST_CHANCE_FEEDBACK_NOTIFICATION_SHOWN, true).apply();
	}

	public boolean wasPlayServicesInstallationCancelled() {
		return sharedPreferences.getBoolean(WAS_PLAY_SERVICES_INSTALLATION_CANCELLED, false);
	}

	public void setPlayServicesInstallationCancelled() {
		sharedPreferences.edit().putBoolean(WAS_PLAY_SERVICES_INSTALLATION_CANCELLED, true).apply();
	}

	public boolean wasSettingsPopupDisplayed() {
		return sharedPreferences.getBoolean(WAS_SETTINGS_POPUP_DISPLAYED, false);
	}

	public void setSettingsPopupAsDisplayed() {
		sharedPreferences.edit().putBoolean(WAS_SETTINGS_POPUP_DISPLAYED, true).apply();
	}

	public int getLastSeenPushNotificationId() {
		return sharedPreferences.getInt(LAST_SEEN_PUSH_NOTIFICATION_ID, NO_PUSH_NOTIFICATION_SEEN_NOTIFICATION_ID);
	}

	public void setLastSeenPushNotificationId(int notificationId) {
		sharedPreferences.edit().putInt(LAST_SEEN_PUSH_NOTIFICATION_ID, notificationId).apply();
	}


	public Date getLastEventsUpdateDate() {
		long date = sharedPreferences.getLong(LAST_EVENTS_UPDATE_DATE, -1);
		if (date == -1) {
			return null;
		}
		return Dates.utcToLocalTime(new Date(date));
	}

	public void setLastEventsUpdatedDate() {
		sharedPreferences.edit().putLong(LAST_EVENTS_UPDATE_DATE, Dates.localToUTCTime(Dates.now()).getTime()).apply();
	}

	public Date getLastUpdatesUpdateDate() {
		long date = sharedPreferences.getLong(LAST_UPDATES_UPDATE_DATE, -1);
		if (date == -1) {
			return null;
		}
		return Dates.utcToLocalTime(new Date(date));
	}

	public void setLastUpdatesUpdatedDate() {
		sharedPreferences.edit().putLong(LAST_UPDATES_UPDATE_DATE, Dates.localToUTCTime(Dates.now()).getTime()).apply();
	}

	public void setAdvancedOptionsEnabled(boolean enabled) {
		sharedPreferences.edit().putBoolean(IS_ADVANCED_OPTIONS_ENABLED, enabled).apply();
	}

	public boolean isAdvancedOptionsEnabled() {
		return sharedPreferences.getBoolean(IS_ADVANCED_OPTIONS_ENABLED, false);
	}
}

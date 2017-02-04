package amai.org.conventions.utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Date;

import amai.org.conventions.model.conventions.Convention;

public class Settings {
	public static final int NO_PUSH_NOTIFICATION_SEEN = -1;

	private static final String SETTINGS_SUFFIX = "settings";
	private static final String WAS_FEEDBACK_NOTIFICATION_SHOWN = "WasFeedbackNotificationShown";
	private static final String WAS_LAST_CHANCE_FEEDBACK_NOTIFICATION_SHOWN = "WasLastChanceFeedbackNotificationShown";
	private static final String WAS_NAVIGATION_POPUP_OPENED = "WasNavigationPopupOpened";
	private static final String WAS_PLAY_SERVICES_INSTALLATION_CANCELLED = "WasPlayServicesInstallationCancelled";
	private static final String WAS_SETTINGS_POPUP_DISPLAYED = "WasSettingsPopupDisplayed";
	private static final String LAST_SEEN_PUSH_NOTIFICATION_ID = "LastSeenPushNotificationId";
	private static final String LAST_EVENTS_UPDATE_DATE = "LastEventsUpdateDate";
	private SharedPreferences sharedPreferences;

	public Settings(Context context) {
		String preferencesName = Convention.getInstance().getId() + "_" + SETTINGS_SUFFIX;
		sharedPreferences = context.getSharedPreferences(preferencesName, Context.MODE_PRIVATE);
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

	public boolean wasNavigationPopupOpened() {
		return sharedPreferences.getBoolean(WAS_NAVIGATION_POPUP_OPENED, false);
	}

	public void setNavigationPopupOpened() {
		sharedPreferences.edit().putBoolean(WAS_NAVIGATION_POPUP_OPENED, true).apply();
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
		return sharedPreferences.getInt(LAST_SEEN_PUSH_NOTIFICATION_ID, NO_PUSH_NOTIFICATION_SEEN);
	}

	public void setLastSeenPushNotificationId(int notificationId) {
		sharedPreferences.edit().putInt(LAST_SEEN_PUSH_NOTIFICATION_ID, notificationId).apply();
	}


	public Date getLastEventsUpdateDate() {
		long date = sharedPreferences.getLong(LAST_EVENTS_UPDATE_DATE, -1);
		if (date == -1) {
			return null;
		}
		return new Date(date);
	}

	public void setLastEventsUpdatedDate() {
		sharedPreferences.edit().putLong(LAST_EVENTS_UPDATE_DATE, Dates.now().getTime()).apply();
	}

}

package amai.org.conventions.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class Settings {
    private static final String SETTINGS = "settings";
    private static final String WAS_FEEDBACK_NOTIFICATION_SHOWN = "WasFeedbackNotificationShown";
	private static final String WAS_NAVIGATION_POPUP_OPENED = "WasNavigationPopupOpened";
	private static final String WAS_PLAY_SERVICES_INSTALLATION_CANCELLED = "WasPlayServicesInstallationCancelled";
    private SharedPreferences sharedPreferences;

    public Settings(Context context) {
        sharedPreferences = context.getSharedPreferences(SETTINGS, Context.MODE_PRIVATE);
    }

    public boolean wasConventionFeedbackNotificationShown() {
        return sharedPreferences.getBoolean(WAS_FEEDBACK_NOTIFICATION_SHOWN, false);
    }

    public void setFeedbackNotificationAsShown() {
        sharedPreferences.edit().putBoolean(WAS_FEEDBACK_NOTIFICATION_SHOWN, true).apply();
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
}

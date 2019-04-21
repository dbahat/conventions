package amai.org.conventions.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import amai.org.conventions.events.SearchCategory;
import amai.org.conventions.model.conventions.Convention;
import amai.org.conventions.notifications.PushNotificationTopic;
import sff.org.conventions.R;

public class Settings {
	public static final int NO_PUSH_NOTIFICATION_SEEN_NOTIFICATION_ID = -1;

	private static final String SETTINGS_SUFFIX = "settings";
	private static final String PREFERENCES_FILE_NAME = Convention.getInstance().getId() + "_" + SETTINGS_SUFFIX;
	private static final String WAS_FEEDBACK_NOTIFICATION_SHOWN = "WasFeedbackNotificationShown";
	private static final String WAS_LAST_CHANCE_FEEDBACK_NOTIFICATION_SHOWN = "WasLastChanceFeedbackNotificationShown";
	private static final String WAS_PLAY_SERVICES_INSTALLATION_CANCELLED = "WasPlayServicesInstallationCancelled";
	private static final String WAS_SETTINGS_POPUP_DISPLAYED = "WasSettingsPopupDisplayed";
	private static final String LAST_SEEN_PUSH_NOTIFICATION_ID = "LastSeenPushNotificationId";
	private static final String PROGRAMME_SEARCH_CATEGORIES = "ProgrammeSearchCategories";
	private static final String LAST_EVENTS_UPDATE_DATE = "LastEventsUpdateDate";
	private static final String LAST_UPDATES_UPDATE_DATE = "LastUpdatesUpdateDate";
	private static final String LAST_SECOND_HAND_UPDATE_DATE = "LastSecondHandUpdateDate";
	private static final String LAST_SECOND_HAND_SEARCH_ITEMS_UPDATE_DATE = "LastSecondHandSearchItemsUpdateDate";

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

	public void setProgrammeSearchCategories(List<String> categories) {
		sharedPreferences.edit().putStringSet(PROGRAMME_SEARCH_CATEGORIES, new HashSet<>(categories)).apply();
	}

	public List<String> getProgrammeSearchCategories(Context context) {
		Set<String> categories = sharedPreferences.getStringSet(PROGRAMME_SEARCH_CATEGORIES, null);
		if (categories == null) {
			List<SearchCategory> searchCategories = Convention.getInstance().getAggregatedEventTypesSearchCategories(context);
			// Flatten the list
			return CollectionUtils.map(searchCategories, new CollectionUtils.Mapper<SearchCategory, String>() {
				@Override
				public String map(SearchCategory item) {
					return item.getName();
				}
			});
		}
		return new ArrayList<>(categories);
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

	public Date getLastUpdatesUpdateDate() {
		long date = sharedPreferences.getLong(LAST_UPDATES_UPDATE_DATE, -1);
		if (date == -1) {
			return null;
		}
		return new Date(date);
	}

	public void setAdvancedOptionsEnabled(boolean enabled) {
		sharedPreferences.edit().putBoolean(IS_ADVANCED_OPTIONS_ENABLED, enabled).apply();
	}

	public boolean isAdvancedOptionsEnabled() {
		return sharedPreferences.getBoolean(IS_ADVANCED_OPTIONS_ENABLED, false);
	}

	public void setLastUpdatesUpdatedDate() {
		sharedPreferences.edit().putLong(LAST_UPDATES_UPDATE_DATE, Dates.now().getTime()).apply();
	}

	public Date getLastSecondHandUpdateDate() {
		long date = sharedPreferences.getLong(LAST_SECOND_HAND_UPDATE_DATE, -1);
		if (date == -1) {
			return null;
		}
		return new Date(date);
	}

	public Date getLastSecondHandSearchItemsUpdateDate() {
		long date = sharedPreferences.getLong(LAST_SECOND_HAND_SEARCH_ITEMS_UPDATE_DATE, -1);
		if (date == -1) {
			return null;
		}
		return new Date(date);
	}

	public void setLastSecondHandUpdatedDate() {
		sharedPreferences.edit().putLong(LAST_SECOND_HAND_UPDATE_DATE, Dates.now().getTime()).apply();
	}

	public void setLastSecondHandSearchItemsUpdateDate() {
		sharedPreferences.edit().putLong(LAST_SECOND_HAND_SEARCH_ITEMS_UPDATE_DATE, Dates.now().getTime()).apply();
	}
}
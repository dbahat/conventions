package amai.org.conventions;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;

import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.Date;
import java.util.Locale;

import amai.org.conventions.model.ConventionEvent;
import amai.org.conventions.model.EventNotification;
import amai.org.conventions.model.conventions.Convention;
import amai.org.conventions.navigation.NavigationActivity;
import amai.org.conventions.notifications.LocalNotificationScheduler;
import amai.org.conventions.utils.BundleBuilder;
import amai.org.conventions.utils.Dates;
import amai.org.conventions.utils.Log;
import amai.org.conventions.utils.Objects;
import amai.org.conventions.utils.Settings;

public class ConventionsApplication extends Application {
	private final static String TAG = ConventionsApplication.class.getCanonicalName();

	public static LocalNotificationScheduler alarmScheduler;
	public static Settings settings;
	private static String versionName;
	private static Context currentContext;
	private static ConventionsApplication appContext;

	@Override
	public void onCreate() {
		super.onCreate();
		appContext = this;

		Locale.setDefault(Dates.getLocale());
		Convention.getInstance().load(this);

		settings = new Settings(this);
		alarmScheduler = new LocalNotificationScheduler(this);

		try {
			alarmScheduler.scheduleNotificationsToFillConventionFeedback();
			restoreAlarmConfiguration();
		} catch (Exception e) {
			// Added due to a SecurityException when trying to schedule alarms on some devices.
			// The alarms might not be set in this case, but at least the app won't crash on startup.
			Log.i(TAG, "Could not schedule alarms", e);
			FirebaseAnalytics.getInstance(this).logEvent("alarms_disabled", new BundleBuilder()
					.putString("exception_message", e.getMessage() + "\n" + android.util.Log.getStackTraceString(e))
					.build()
			);
		}

		try {
			versionName = this.getPackageManager().getPackageInfo(this.getPackageName(), 0).versionName;
		} catch (Exception e) {
			Log.i(TAG, "Could not get version name: " + e.getMessage());
		}

		// Keep reference to current activity for showing dialogs and notifications from any activity
		registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
			@Override
			public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
				// Important - only keep our activities here! Update when adding new activities if
				// they aren't NavigationActivity!
				if (activity instanceof NavigationActivity || activity instanceof SplashActivity) {
					currentContext = activity;
				}
			}

			@Override
			public void onActivityStopped(Activity activity) {
				if (currentContext == activity) {
					currentContext = null;
				}
			}

			@Override
			public void onActivityStarted(Activity activity) {
			}

			@Override
			public void onActivityResumed(Activity activity) {
			}

			@Override
			public void onActivityPaused(Activity activity) {
			}

			@Override
			public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
			}

			@Override
			public void onActivityDestroyed(Activity activity) {
			}
		});
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		// Screen rotation apparently returns the locale to the device default
		Locale.setDefault(Dates.getLocale());
	}

	public static ConventionsApplication getAppContext() {
		return appContext;
	}

	/**
	 * Since the Android AlarmManager gets reset whenever the device reboots, we re-schedule all the notifications when the app is launched.
	 */
	private void restoreAlarmConfiguration() {
		for (ConventionEvent event : Convention.getInstance().getEvents()) {
			scheduleEventAlarms(event);
		}
	}

	public boolean rescheduleChangedEventAlarms(ConventionEvent event, ConventionEvent previousEvent) {
		boolean alarmsChanged = false;
		EventNotification aboutToStartNotification = event.getUserInput().getEventAboutToStartNotification();
		if (
			aboutToStartNotification != null &&
			aboutToStartNotification.getNotificationTime() != null &&
			!Objects.equals(event.getStartTime(), previousEvent.getStartTime()) &&
			event.getStartTime() != null && previousEvent.getStartTime() != null
		) {
			// Calculate the new notification time. It should be keep the time difference.
			// For example, if the user set the notification to be 3 minutes before the event start time,
			// the new time should be 3 minutes before the new event start time.
			aboutToStartNotification.setNotificationTime(new Date(
				aboutToStartNotification.getNotificationTime().getTime() -
				previousEvent.getStartTime().getTime() +
				event.getStartTime().getTime()
			));
			alarmsChanged = true;
		}

		EventNotification feedbackNotification = event.getUserInput().getEventFeedbackReminderNotification();
		if (
			feedbackNotification != null &&
			feedbackNotification.getNotificationTime() != null &&
			!Objects.equals(event.getEndTime(), previousEvent.getEndTime()) &&
			event.getEndTime() != null && previousEvent.getEndTime() != null
		) {
			// Calculate the new notification time. It should be keep the time difference.
			// For example, if the user set the notification to be 3 minutes after the event end time,
			// the new time should be 3 minutes after the new event end time.
			feedbackNotification.setNotificationTime(new Date(
				feedbackNotification.getNotificationTime().getTime() -
				previousEvent.getEndTime().getTime() +
				event.getEndTime().getTime()
			));
			alarmsChanged = true;
		}

		if (alarmsChanged) {
			Log.i(TAG, "Rescheduling alarms for event " + event.getTitle() + " (" + event.getId() + ")");
			scheduleEventAlarms(event);
		}
		return alarmsChanged;
	}

	private void scheduleEventAlarms(ConventionEvent event) {
		restoreAlarmConfiguration(event, event.getUserInput().getEventAboutToStartNotification());
		restoreAlarmConfiguration(event, event.getUserInput().getEventFeedbackReminderNotification());
	}

	private void restoreAlarmConfiguration(ConventionEvent event, EventNotification eventNotification) {
		if (eventNotification != null && eventNotification.isEnabled()) {
			switch (eventNotification.getType()) {
				case AboutToStart:
					alarmScheduler.scheduleEventAboutToStartNotification(event, eventNotification.getNotificationTime().getTime());
					break;
				case FeedbackReminder:
					alarmScheduler.scheduleFillFeedbackOnEventNotification(event, eventNotification.getNotificationTime().getTime());
					break;
			}
		}
	}

	@Override
	public void onTrimMemory(int level) {
		// Release memory when low
		if (level >= TRIM_MEMORY_RUNNING_LOW) {
			ImageHandler.releaseCache();
		}
		super.onTrimMemory(level);
	}

	public static String getVersionName() {
		return versionName;
	}

	public static Context getCurrentContext() {
		return currentContext;
	}
}

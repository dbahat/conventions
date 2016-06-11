package amai.org.conventions;

import android.app.Application;
import android.content.Context;

import com.google.android.gms.analytics.ExceptionReporter;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.StandardExceptionParser;
import com.google.android.gms.analytics.Tracker;

import java.util.Collection;
import java.util.Locale;
import java.util.Map;

import amai.org.conventions.model.Convention;
import amai.org.conventions.model.ConventionEvent;
import amai.org.conventions.model.EventNotification;
import amai.org.conventions.notifications.LocalNotificationScheduler;
import amai.org.conventions.utils.Dates;
import amai.org.conventions.utils.Log;
import amai.org.conventions.utils.Settings;

public class ConventionsApplication extends Application {
	private final static String TAG = ConventionsApplication.class.getCanonicalName();

	private static Tracker tracker;
    public static LocalNotificationScheduler alarmScheduler;
    public static Settings settings;
	private static String versionName;

    @Override
    public void onCreate() {
        super.onCreate();

        Locale.setDefault(Dates.getLocale());
        Convention.getInstance().load(this);

	    if (!BuildConfig.DEBUG) {
		    GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
	        analytics.setLocalDispatchPeriod(1800);

	        tracker = analytics.newTracker("UA-65293055-1");
	        tracker.enableExceptionReporting(true);
	        tracker.enableAutoActivityTracking(true);
	    }

        alarmScheduler = new LocalNotificationScheduler(this);
        restoreAlarmConfiguration();

        // Change uncaught exception parser to include more information
        Thread.UncaughtExceptionHandler uncaughtExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
        if (uncaughtExceptionHandler instanceof ExceptionReporter) {
            ExceptionReporter exceptionReporter = (ExceptionReporter) uncaughtExceptionHandler;
            exceptionReporter.setExceptionParser(new ExtendedExceptionParser(this, null));
        }

        settings = new Settings(this);
        alarmScheduler.scheduleNotificationToFillConventionFeedback();

	    try {
		    versionName = this.getPackageManager().getPackageInfo(this.getPackageName(), 0).versionName;
	    } catch (Exception e) {
		    Log.i(TAG, "Could not get version name: " + e.getMessage());
	    }
    }

    /**
     * Since the Android AlarmManager gets reset whenever the device reboots, we re-schedule all the notifications when the app is launched.
     */
    private void restoreAlarmConfiguration() {
        for (ConventionEvent event : Convention.getInstance().getEvents()) {
            restoreAlarmConfiguration(event, event.getUserInput().getEventAboutToStartNotification());
	        restoreAlarmConfiguration(event, event.getUserInput().getEventFeedbackReminderNotification());
        }
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

    private static class ExtendedExceptionParser extends StandardExceptionParser {

        public ExtendedExceptionParser(Context context, Collection<String> additionalPackages) {
            super(context, additionalPackages);
        }

        @Override
        public String getDescription(String threadName, Throwable t) {
            String description = super.getDescription(threadName, t);

            return String.format(Dates.getLocale(), "%s. %s.",
                    description,
                    android.util.Log.getStackTraceString(t));
        }
    }

	public static String getVersionName() {
		return versionName;
	}

	public static void sendTrackingEvent(Map<String, String> trackingEvent) {
		if (tracker != null) {
			tracker.send(trackingEvent);
		}
	}
}

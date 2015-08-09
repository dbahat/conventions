package amai.org.conventions;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.google.android.gms.analytics.ExceptionReporter;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.StandardExceptionParser;
import com.google.android.gms.analytics.Tracker;

import java.util.Collection;
import java.util.Locale;

import amai.org.conventions.model.Convention;
import amai.org.conventions.model.ConventionEvent;
import amai.org.conventions.model.EventNotification;
import amai.org.conventions.utils.ConventionStorage;
import amai.org.conventions.utils.Dates;

public class ConventionsApplication extends Application {

    public static GoogleAnalytics analytics;
    public static Tracker tracker;
    public static AlarmScheduler alarmScheduler;

    @Override
    public void onCreate() {
        super.onCreate();

	    Locale.setDefault(Dates.getLocale());
	    ConventionStorage.initFromFile(this);
        analytics = GoogleAnalytics.getInstance(this);
        analytics.setLocalDispatchPeriod(1800);

        tracker = analytics.newTracker("UA-65293055-1");
        tracker.enableExceptionReporting(true);
        tracker.enableAutoActivityTracking(true);

        alarmScheduler = new AlarmScheduler(this);
        restoreAlarmConfiguration();

        // Change uncaught exception parser to include more information
        Thread.UncaughtExceptionHandler uncaughtExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
        if (uncaughtExceptionHandler instanceof ExceptionReporter) {
            ExceptionReporter exceptionReporter = (ExceptionReporter) uncaughtExceptionHandler;
            exceptionReporter.setExceptionParser(new ExtendedExceptionParser(this, null));
        }
    }

    /**
     * Since the Android AlarmManager gets reset whenever the device reboots, we re-schedule all the notifications when the app is launched.
     */
    private void restoreAlarmConfiguration() {
        for (ConventionEvent event : Convention.getInstance().getEvents()) {
            restoreAlarmConfiguration(event, event.getUserInput().getEventAboutToStartNotification());
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

    private static class ExtendedExceptionParser extends StandardExceptionParser {

        public ExtendedExceptionParser(Context context, Collection<String> additionalPackages) {
            super(context, additionalPackages);
        }

        @Override
        public String getDescription(String threadName, Throwable t) {
            String description = super.getDescription(threadName, t);

            return String.format(Dates.getLocale(), "%s. %s.",
                    description,
                    Log.getStackTraceString(t));
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
}

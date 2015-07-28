package amai.org.conventions;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.google.android.gms.analytics.ExceptionReporter;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.StandardExceptionParser;
import com.google.android.gms.analytics.Tracker;

import java.util.Collection;

import amai.org.conventions.utils.ConventionStorage;
import amai.org.conventions.utils.Dates;

public class ConventionsApplication extends Application {

    public static GoogleAnalytics analytics;
    public static Tracker tracker;

    @Override
    public void onCreate() {

        ConventionStorage.initFromFile(this);
        analytics = GoogleAnalytics.getInstance(this);
        analytics.setLocalDispatchPeriod(1800);

        tracker = analytics.newTracker("UA-65293055-1");
        tracker.enableExceptionReporting(true);
        tracker.enableAutoActivityTracking(true);

        // Change uncaught exception parser to include more information
        Thread.UncaughtExceptionHandler uncaughtExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
        if (uncaughtExceptionHandler instanceof ExceptionReporter) {
            ExceptionReporter exceptionReporter = (ExceptionReporter) uncaughtExceptionHandler;
            exceptionReporter.setExceptionParser(new ExtendedExceptionParser(this, null));
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
}

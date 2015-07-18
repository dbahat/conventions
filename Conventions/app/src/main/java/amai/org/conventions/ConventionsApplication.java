package amai.org.conventions;

import android.app.Application;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;

import amai.org.conventions.utils.ConventionStorage;

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
	}
}

package amai.org.conventions;

import android.app.Application;

import amai.org.conventions.utils.ConventionStorage;

public class ConventionsApplication extends Application {
	@Override
	public void onCreate() {
		ConventionStorage.initFromFile(this);
	}
}

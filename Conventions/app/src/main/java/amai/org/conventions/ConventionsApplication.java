package amai.org.conventions;

import android.app.Application;

import amai.org.conventions.model.Convention;

public class ConventionsApplication extends Application {
	@Override
	public void onCreate() {
		Convention.initFromFile(this);
	}
}

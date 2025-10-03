package amai.org.conventions.model;

import android.app.Activity;
import android.os.Bundle;

import amai.org.conventions.utils.Views;

public class DetailsActivityLocation extends Place {
	private Class<? extends Activity> activityClass;
	private Bundle bundle = null;

	@Override
	public DetailsActivityLocation withName(String name) {
		super.withName(name);
		return this;
	}

	public Class<? extends Activity> getActivityClass() {
		return activityClass;
	}

	public void setActivityClass(Class<? extends Activity> activityClass) {
		this.activityClass = activityClass;
	}

	public DetailsActivityLocation withActivityClass(Class<? extends Activity> activityClass) {
		setActivityClass(activityClass);
		return this;
	}

	public Bundle getBundle() {
		return bundle;
	}

	public void setBundle(Bundle bundle) {
		this.bundle = bundle;
	}

	public DetailsActivityLocation withBundle(Bundle bundle) {
		setBundle(bundle);
		return this;
	}
}

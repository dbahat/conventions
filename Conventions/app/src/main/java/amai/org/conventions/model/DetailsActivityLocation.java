package amai.org.conventions.model;

import android.app.Activity;

public class DetailsActivityLocation extends Place {
	private Class<? extends Activity> activityClass;

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
}

package amai.org.conventions.navigation;

import android.app.Activity;
import android.graphics.drawable.Drawable;

public class NavigationItem {
	private Class<? extends Activity> activity;
	private String text;
	private Drawable icon;

	public NavigationItem(Class<? extends Activity> activity, String text, Drawable icon) {
		this.activity = activity;
		this.text = text;
		this.icon = icon;
	}

	public Class<? extends Activity> getActivity() {
		return activity;
	}

	public String getText() {
		return text;
	}

	public Drawable getIcon() {
		return icon;
	}
}

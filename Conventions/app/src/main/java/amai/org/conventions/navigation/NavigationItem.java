package amai.org.conventions.navigation;

import android.app.Activity;
import android.graphics.drawable.Drawable;

public class NavigationItem {
	private final Class<? extends Activity> activity;
	private final String text;
	private final Drawable icon;
	private final Drawable selectedIcon;

	public NavigationItem(Class<? extends Activity> activity, String text, Drawable icon) {
		this(activity, text, icon, null);
	}

	public NavigationItem(Class<? extends Activity> activity, String text, Drawable icon, Drawable selectedIcon) {
		this.activity = activity;
		this.text = text;
		this.icon = icon;
		this.selectedIcon = selectedIcon;
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

	public Drawable getSelectedIcon() {
		return selectedIcon;
	}
}

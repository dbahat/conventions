package amai.org.conventions.navigation;

import android.app.Activity;
import android.graphics.drawable.Drawable;

public class NavigationItem {
	private final Class<? extends Activity> activity;
	private final String text;
	private final Drawable icon;
	private final Drawable selectedIcon;
	private final boolean showDrawableOnEnd;
	private final boolean showDrawable;

	public NavigationItem(Class<? extends Activity> activity, String text, Drawable icon, boolean showDrawable) {
		this(activity, text, icon, null, showDrawable, false);
	}

	public NavigationItem(Class<? extends Activity> activity, String text, Drawable icon, Drawable selectedIcon, boolean showDrawable) {
		this(activity, text, icon, selectedIcon, showDrawable, false);
	}

	public NavigationItem(Class<? extends Activity> activity, String text, Drawable icon, Drawable selectedIcon, boolean showDrawable, boolean showDrawableOnEnd) {
		this.activity = activity;
		this.text = text;
		this.icon = icon;
		this.selectedIcon = selectedIcon;
		this.showDrawable = showDrawable;
		this.showDrawableOnEnd = showDrawableOnEnd;
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

	public boolean isShowDrawable() {
		return showDrawable;
	}

	public boolean isShowDrawableOnEnd() {
		return showDrawableOnEnd;
	}
}

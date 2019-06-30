package amai.org.conventions.navigation;

import android.app.Activity;
import android.graphics.drawable.Drawable;

import androidx.annotation.Nullable;

public class NavigationItem {
	private final Class<? extends Activity> activity;
	private final String text;
	private final Drawable icon;
	@Nullable
	private final Drawable selectedItemIcon;

	public NavigationItem(Class<? extends Activity> activity, String text, Drawable icon) {
		this(activity, text, icon, null);
	}

	public NavigationItem(Class<? extends Activity> activity, String text, Drawable icon, Drawable selectedItemIcon) {
		this.activity = activity;
		this.text = text;
		this.icon = icon;
		this.selectedItemIcon = selectedItemIcon;
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

	@Nullable
	public Drawable getSelectedItemIcon() {
		return selectedItemIcon;
	}
}

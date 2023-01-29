package amai.org.conventions.navigation;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.TextView;

import amai.org.conventions.ThemeAttributes;
import androidx.recyclerview.widget.RecyclerView;
import sff.org.conventions.R;

public class NavigationItemViewHolder extends RecyclerView.ViewHolder {
	private final TextView textView;

	public NavigationItemViewHolder(View itemView) {
		super(itemView);
		textView = (TextView) itemView.findViewById(R.id.navigation_item_text);
	}

	public void setData(final NavigationItem item, final NavigationActivity currentActivity) {
		textView.setText(item.getText());
		boolean isCurrentItemSelected = currentActivity.getClass() == item.getActivity();
		configureDrawableIfNeeded(currentActivity, isCurrentItemSelected, item, isCurrentItemSelected);

		if (isCurrentItemSelected) {
			int color = ThemeAttributes.getColor(currentActivity, R.attr.navigationPopupSelectedColor);
			textView.setTextColor(color);
			textView.setOnClickListener(null);
		} else {
			int color = ThemeAttributes.getColor(currentActivity, R.attr.navigationPopupNotSelectedColor);
			textView.setTextColor(color);
			textView.setOnClickListener(v -> currentActivity.navigateToActivity(item.getActivity()));
		}
	}

	private void configureDrawableIfNeeded(Context context, boolean isCurrentItemSelected, NavigationItem item, boolean isSelected) {
		if (!item.isShowDrawable()) {
			textView.setCompoundDrawables(null, null, null, null);
			return;
		}

		Drawable icon = item.getIcon();
		if (isSelected && item.getSelectedIcon() != null) {
			icon = item.getSelectedIcon();
		}

		int color;
		if (isCurrentItemSelected) {
			color = ThemeAttributes.getColor(context, R.attr.navigationPopupSelectedColor);
		} else {
			color = ThemeAttributes.getColor(context, R.attr.navigationPopupNotSelectedColor);
		}
			icon.mutate().setColorFilter(color, PorterDuff.Mode.SRC_ATOP);

		// this should be setCompoundDrawablesRelative(icon, null, null, null) but in API 17 and 18 it appears on the wrong side.
		// I tried to fix it by setting the layout direction to RTL directly on the textview but it didn't work.
		if (item.isShowDrawableOnEnd()) {
			textView.setCompoundDrawables(icon, null, null, null);
		} else {
			textView.setCompoundDrawables(null, null, icon, null);
		}
	}
}
